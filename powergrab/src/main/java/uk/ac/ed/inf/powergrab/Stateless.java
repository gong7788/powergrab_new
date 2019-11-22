package uk.ac.ed.inf.powergrab;

import java.util.*;

class Stateless extends Drone {

	Stateless(double longitude, double latitude, int seed) {
		super(longitude, latitude, seed);
	}

	/**
	 * Searches all stations around the drone (0.00025)
	 * This methods searches all stations that can be charged in one step(0.00055), then finds
	 * stations around the next steps (0.00025)
	 * @param states   an ArrayList stores 50 states
	 * @return         stations that in the search area
	 */
	 private ArrayList<State> search_state(ArrayList<State> states) {
		ArrayList<State> in_big_circle = new ArrayList<State>();
		ArrayList<State> state_list = new ArrayList<State>();

		double lat1 = current_position.latitude;
		double lon1 = current_position.longitude;
		for(int i = 0; i < 50; i++) {
			State s = states.get(i);
			double longitude_state = s.getLongitude();
			double latitude_state = s.getLatitude();
			double dist = distance(longitude_state, latitude_state, lon1, lat1);
			if(dist <= 0.00055) {
				in_big_circle.add(s);
			}
		}

		for (State s : in_big_circle){
			double lat_s = s.getLatitude();
			double lon_s = s.getLongitude();
			for (Direction d : Direction.values()){
				Position nextP = current_position.nextPosition(d);
				double lat_p = nextP.latitude;
				double lon_p = nextP.longitude;
				double dist = distance(lon_s, lat_s, lon_p, lat_p);
				if (dist <= 0.00025 && !state_list.contains(s)){
					state_list.add(s);
				}
			}
		}
		return state_list;
	}

	/**
	 * Finds all direction that can reach the target state in one step
	 * @param state    the target state
	 * @return         list of directions
	 */
	private ArrayList<Direction> findAllDirection(State state) {
		ArrayList<Direction> allDirection = new ArrayList<Direction>();
		double lat_s = state.getLatitude();
		double long_s = state.getLongitude();

		for (Direction direction : Direction.values()) {
			Position nextP = current_position.nextPosition(direction);
			double lat = nextP.latitude;
			double lon = nextP.longitude;
			double dist = distance(lon, lat, long_s, lat_s);
			if (dist < 0.00025) {
				allDirection.add(direction);
			}
		}
		return allDirection;
	}

	/**
	 * Moves one step
	 */
	private void move_one_step() {
		ArrayList<State> state_list = search_state(states); // states in search area
		ArrayList<State> safe_state = new ArrayList<State>();
		ArrayList<State> danger_state = new ArrayList<State>();
		ArrayList<Direction> danger_direction = new ArrayList<Direction>();
		// divide state_list into safe and danger state
		for(int i = 0; i < state_list.size(); i++){
			State s = state_list.get(i);
			String label = s.getLabel();
			if (label.equals("danger")) {
				danger_state.add(s);
			}
			else if (s.isEmpty()) {
				state_list.remove(s);
			}
			else {
				safe_state.add(s);
			}
		}
		
		// Case 1: Doesn't find any station in checking area, moves in a random direction
		if (state_list.isEmpty()) {
			Direction nextd = getRandomDirection();
			Position nextP = current_position.nextPosition(nextd);
			while (!nextP.inPlayArea()) {
				nextd = getRandomDirection();
				nextP = current_position.nextPosition(nextd);
			}

			current_position = nextP;
			update(nextP, nextd);
		}
		//Case 2: all nearby states are danger station
		else if(safe_state.isEmpty()) {
			// find all direction that will goes into a danger station
			for (State s: danger_state) {
				for (Direction d : findAllDirection(s)){
					if (!danger_direction.contains(d)){
						danger_direction.add(d);
					}
				}
			}
			
			Direction next_direction = getRandomDirection();
			while (danger_direction.contains(next_direction) ||
					!current_position.nextPosition(next_direction).inPlayArea()) {
				// if this direction has danger station, generate a new one
				next_direction = getRandomDirection();
			}
			// move
			Position nextP = current_position.nextPosition(next_direction);

			current_position = nextP;
			update(nextP, next_direction);
		}
		// Case 3: there is a safe station in next step
		else {
			// choose the state have max power
			double max = 0;
			State power_state = safe_state.get(0);// default: first one
			for(State s : safe_state) {
				double state_power = s.getPower();
				if (state_power > max) {
					max = state_power;
					power_state = s;
				}
			}
			// adds all danger direction into list
			for (State s: danger_state) {
				for (Direction d : findAllDirection(s)){
					if (!danger_direction.contains(d)){
						danger_direction.add(d);
					}
				}
			}
			// move to the state has most power
			Direction dirc = null;
			label:
			for (Direction d : findAllDirection(power_state)){
				Position nextP = current_position.nextPosition(d);
				double lat = nextP.latitude;
				double lon = nextP.longitude;
				double lat_power = power_state.getLatitude();
				double lon_power = power_state.getLongitude();
				double dist2safe = distance(lon, lat, lon_power, lat_power);
				if (!danger_state.isEmpty()) {
					for (State danger_s : danger_state) {
						double lat_danger = danger_s.getLatitude();
						double lon_danger = danger_s.getLongitude();
						double dist2danger = distance(lon, lat, lon_danger, lat_danger);
						if (dist2safe > dist2danger) { //if there is one danger state is closer then skip this direction
							continue label;
						}
					}
					// there is one direction, safe state is closer
					dirc = d;
					break;
				}
				// case wo only have safe states
				dirc = d;
				break;
			}

			if (dirc==null) {
				do {
					dirc = getRandomDirection();

				} while (danger_direction.contains(dirc) ||
						!current_position.nextPosition(dirc).inPlayArea());
			}
			Position nextP = current_position.nextPosition(dirc);
			charged(power_state);

			current_position = nextP;
			update(nextP, dirc);
		}
	}

	void start() {
		path.add(current_position);
		while (!is_gameover()) {
			move_one_step();
		}
	}
	

}
