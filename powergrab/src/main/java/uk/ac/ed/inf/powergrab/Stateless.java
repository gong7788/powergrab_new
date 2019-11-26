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
	 * @param stations   an ArrayList stores 50 stations
	 * @return         stations that in the search area
	 */
	 private ArrayList<Station> search_state(ArrayList<Station> stations) {
		ArrayList<Station> in_big_circle = new ArrayList<Station>();
		ArrayList<Station> station_list = new ArrayList<Station>();

		double lat1 = current_position.latitude;
		double lon1 = current_position.longitude;
		for(int i = 0; i < 50; i++) {
			Station s = stations.get(i);
			double longitude_state = s.getLongitude();
			double latitude_state = s.getLatitude();
			double dist = distance(longitude_state, latitude_state, lon1, lat1);
			if(dist <= 0.00055) {
				in_big_circle.add(s);
			}
		}

		for (Station s : in_big_circle){
			double lat_s = s.getLatitude();
			double lon_s = s.getLongitude();
			for (Direction d : Direction.values()){
				Position nextP = current_position.nextPosition(d);
				double lat_p = nextP.latitude;
				double lon_p = nextP.longitude;
				double dist = distance(lon_s, lat_s, lon_p, lat_p);
				if (dist <= 0.00025 && !station_list.contains(s)){
					station_list.add(s);
				}
			}
		}
		return station_list;
	}

	/**
	 * Finds all direction that can reach the target station in one step
	 * @param station    the target station
	 * @return         list of directions
	 */
	private ArrayList<Direction> findAllDirection(Station station) {
		ArrayList<Direction> allDirection = new ArrayList<Direction>();
		double lat_s = station.getLatitude();
		double long_s = station.getLongitude();

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
		ArrayList<Station> station_list = search_state(stations); // stations in search area
		ArrayList<Station> safe_station = new ArrayList<Station>();
		ArrayList<Station> danger_station = new ArrayList<Station>();
		ArrayList<Direction> danger_direction = new ArrayList<Direction>();
		// divide station_list into safe and danger state
		for(int i = 0; i < station_list.size(); i++){
			Station s = station_list.get(i);
			String label = s.getLabel();
			if (label.equals("danger")) {
				danger_station.add(s);
			}
			else if (s.isEmpty()) {
				station_list.remove(s);
			}
			else {
				safe_station.add(s);
			}
		}
		
		// Case 1: Doesn't find any station in checking area, moves in a random direction
		if (station_list.isEmpty()) {
			Direction nextd = getRandomDirection();
			Position nextP = current_position.nextPosition(nextd);
			while (!nextP.inPlayArea()) {
				nextd = getRandomDirection();
				nextP = current_position.nextPosition(nextd);
			}

			current_position = nextP;
			update(nextP, nextd);
		}
		//Case 2: all nearby stations are danger station
		else if(safe_station.isEmpty()) {
			// find all direction that will goes into a danger station
			for (Station s: danger_station) {
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
			Station power_station = safe_station.get(0);// default: first one
			for(Station s : safe_station) {
				double state_power = s.getPower();
				if (state_power > max) {
					max = state_power;
					power_station = s;
				}
			}
			// adds all danger direction into list
			for (Station s: danger_station) {
				for (Direction d : findAllDirection(s)){
					if (!danger_direction.contains(d)){
						danger_direction.add(d);
					}
				}
			}
			// move to the state has most power
			Direction dirc = null;
			label:
			for (Direction d : findAllDirection(power_station)){
				Position nextP = current_position.nextPosition(d);
				double lat = nextP.latitude;
				double lon = nextP.longitude;
				double lat_power = power_station.getLatitude();
				double lon_power = power_station.getLongitude();
				double dist2safe = distance(lon, lat, lon_power, lat_power);
				if (!danger_station.isEmpty()) {
					for (Station danger_s : danger_station) {
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
				// case wo only have safe stations
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
			charged(power_station);

			current_position = nextP;
			update(nextP, dirc);
		}
	}

	//Running stateless
	void start() {
		path.add(current_position);
		while (!is_gameover()) {
			move_one_step();
		}
	}
	

}
