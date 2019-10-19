package uk.ac.ed.inf.powergrab;
/**
 * @author s1756255
 */
import java.util.*;


public class Stateless {
	private int seed;
	private int step;
	private double coins;
	private double power;
	private double cost = 1.25;

	private Position current_position;
	private ArrayList<State> states = new ArrayList<State>();
	private ArrayList<Position> path = new ArrayList<Position>();

	public Stateless(double longitude, double latitude, int seed) {
		this.current_position = new Position(latitude, longitude);
		this.seed = seed;
		this.step = 250;
		this.coins = 0;
		//TODO check initial power
		this.power = 250;
	}

	public void setStates(ArrayList<State> states) {
		this.states = states;
	}

	public ArrayList<Position> getPath() {
		return path;
	}

	public double getCoins() {
		return coins;
	}

	Direction[] Values = Direction.values();
    int Size = Values.length;
    Random rand = new Random(seed);

    public Direction getRandomDirection() {
    	return Values[rand.nextInt(Size)];
    }
    
	public double distance(double longitude1, double latitude1, double longitude2, double latitude2) {
		/**
		 * Calculates distance between two positions
		 *
		 * @param longitude1    longitude of first position
		 * @param latitude1     latitude of first position
		 * @param longitude2   longitude of second position
		 * @param latitude2     latitude of second position
		 * @return              the distance between two positions
		 */
		double dist_sq = 0;
		dist_sq = (longitude1 - longitude2)*(longitude1 - longitude2) + 
				(latitude1 - latitude2)*(latitude1 - latitude2);
		
		return Math.sqrt(dist_sq);
	}

	public ArrayList<State> serch_state(ArrayList<State> statess) {
		/**
		 * finds all states that in the search area
		 *
		 * @param states   an ArrayList stores 50 states
		 * @return         states that in the search area
		 */
		ArrayList<State> in_big_circle = new ArrayList<State>();
		ArrayList<State> state_list = new ArrayList<State>();

		double lat1 = current_position.latitude;
		double lon1 = current_position.longitude;
		for(int i = 0; i < 50; i++) {
			State s = statess.get(i);
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
	
	public boolean is_gameover() {
		if (step <= 0 || power <= 0) {
			return true; 
		}
		else return false; 
	}
	//TODO Delete
	public Direction findDirec(State state) {
		/**
		 * finds direction of given state relative to the current position
		 *
		 * @param state    the given state
		 * @return         direction of the state relative to the current position
		 */
		Direction dir = null;
		double min = Double.MAX_VALUE;
		double lat_s = state.getLatitude();
		double long_s = state.getLongitude();
		
		for(Direction direction : Direction.values()) {
			Position nextP = current_position.nextPosition(direction);
			double lat = nextP.latitude;
			double lon = nextP.longitude;
			double dist = distance(lon, lat, long_s, lat_s);
			if (dist < min) {
				min = dist;
				dir = direction;
			}
		}
		return dir;
	}

	public ArrayList<Direction> findAllDirection(State state) {
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

	public void move_one_step() {
		ArrayList<State> state_list = serch_state(states); // states in search area
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
		
		// move function
		if (state_list.isEmpty()) {
			// No find states in checking area, move to a random direction
			Position nextP = current_position.nextPosition(getRandomDirection());
			while (!nextP.inPlayArea()) {
				nextP = current_position.nextPosition(getRandomDirection());
			}
			power = power - cost;
			path.add(nextP);
			current_position = nextP;
			step -= 1;
		} 
		else if(safe_state.isEmpty()) {
			// all states are danger state
			// find all direction
			for (int i = 0; i < danger_state.size(); i++) {
				State s = danger_state.get(i);
				for (Direction d : findAllDirection(s)){
					if (!danger_direction.contains(d)){
						danger_direction.add(d);
					}
				}
			}
			
			Direction next_direction = getRandomDirection();
			while (danger_direction.contains(next_direction) ||
					!current_position.nextPosition(next_direction).inPlayArea()) {
				// if this direction has danger state, generate a new one
				next_direction = getRandomDirection();
			}
			// move
			Position nextP = current_position.nextPosition(next_direction);
			power = power - cost;
			path.add(nextP);
			current_position = nextP;
			step -= 1;
		}
		else {
			// choose the state have max power
			double max = 0;
			State power_state = safe_state.get(0);// default: first one
			for(int i = 0; i < safe_state.size(); i++) {
				State s = safe_state.get(i);
				double state_power = s.getPower();
				if (state_power > max) {
					max = state_power;
					power_state = s;
				}
			}

			for (int i = 0; i < danger_state.size(); i++) {
				State s = danger_state.get(i);
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
			power = power - cost;
			path.add(nextP);
			current_position = nextP;
			step -= 1;
		}
	}
	//TODO delete
//	public void fly_to_next(){
//		//get 16 next position with weight
//		get_weighted_state();
//	}
	//TODO delete
//	public void get_weighted_state(){
//		ArrayList<Integer> around_states = serch_state(states);
//		HashMap<Direction, Double> DW = new HashMap<Direction, Double>();
//		for(Direction direction : Direction.values()) {
//			Position nextP = current_position.nextPosition(direction);
//			double weight = calWeight(nextP, around_states);
//			DW.put(direction, weight);
//		}
//		// sort DW by weight by decreasing order
//		List<Map.Entry<Direction, Double>> list = new ArrayList<Map.Entry<Direction, Double>>(DW.entrySet());
//		Collections.sort(list, new Comparator<Map.Entry<Direction, Double>>() {
//			public int compare(Map.Entry<Direction, Double> o1, Map.Entry<Direction, Double> o2) {
//				return o2.getValue().compareTo(o1.getValue());
//			}
//		});
//
//	}
//  TODO delete
//	public double calWeight(Position p, ArrayList<Integer> around_states){
//		// the weight of next position (safe state power + danger state power)
//		double weight = 0;
//		double lat1 = p.latitude;
//		double lon1 = p.longitude;
//		for(Integer i : around_states){
//			double longitude_state = states[2*i];
//			double latitude_state = states[2*i+1];
//			double dist = distance(lon1, lat1, longitude_state, latitude_state);
//			if (dist <= 0.0025){
//				double power = feature_list.get(i).getProperty("power").getAsDouble();
//				weight = weight + power;
//			}
//		}
//		return weight;
//	}
//	
	public void charged(State power_state) {
		double coins_s = power_state.getCoins();
		double power_s = power_state.getPower();
		// add coins/power into drone
		coins = coins + coins_s;
		power = power + power_s;
		// subtract coins/power from state
		power_state.setCoins(0);
		power_state.setPower(0);
		power_state.setEmpty(true);
	}

	public void start() {
		path.add(current_position);
		while (!is_gameover()) {
			move_one_step();
		}
	}
	

}
