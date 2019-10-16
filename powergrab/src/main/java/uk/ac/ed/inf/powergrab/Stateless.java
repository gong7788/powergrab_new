package uk.ac.ed.inf.powergrab;
/**
 * @author s1756255
 */
import java.util.*;

import com.google.gson.JsonElement;
import com.mapbox.geojson.Feature;
import com.mapbox.geojson.Point;

public class Stateless {
	private int seed;
	private int step;
	private double coins;
	private double power;
	private double cost = 1.25;
	
	private List<Feature> feature_list = null;
	private Position current_position;
	//public ArrayList<State> valid_states = new ArrayList<State>();
	public ArrayList<Position> path = new ArrayList<Position>();

//	MyMap Geomap = new MyMap();
//	double [] states = Geomap.getstates();

//	public void setValid_states(ArrayList<State> valid_states) {
//		this.valid_states = valid_states;
//	}

	public Stateless(double longitude, double latitude, int seed) {
		this.current_position = new Position(latitude, longitude);
		this.seed = seed;
		this.step = 250;
		this.coins = 0;
		//TODO check initial power
		this.power = 250;
	}
	
	public void setFeature_list(List<Feature> feature_list) {
		this.feature_list = feature_list;
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
		 * @param longgitude2   longitude of second position
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
		 * finds all states in the search area(0.0003)
		 *
		 * @param states   an list of
		 * @return         index of states that in the search area (states)
		 */
		ArrayList<State> valid_states = new ArrayList<State>();
		
		double lat1 = current_position.latitude;
		double lon1 = current_position.longitude;
		for(int i = 0; i < 50; i++) {
			State s = statess.get(i);
			double longitude_state = s.getLongitude();
			double latitude_state = s.getLatitude();
			double dist = distance(longitude_state, latitude_state, lon1, lat1);

			if(dist <= 0.0003) {
				valid_states.add(s);
			}
		}
		return valid_states;
	}
	
	public boolean is_gameover() {
		if (step <= 0 || power <= 0) {
			return true; 
		}
		else return false; 
	}
	
	public void start() {
		path.add(current_position);
		while (!is_gameover()) {
			//fly();
		}
	}
	
	public Direction findDirec(Point state) {
		/**
		 *
		 */
		Direction dir = null;
		double min = Double.MAX_VALUE;
		double lat_s = state.coordinates().get(1);
		double long_s = state.coordinates().get(0);
		
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
	
	public Direction findDirec_test(Position state) {	
		Direction dir = null;
		double min = 88888;
		double lat_s = state.latitude;
		double long_s = state.longitude;
		
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
	
	//TODO keep this
	public void fly() {
		ArrayList<State> around_states = serch_state(MyMap.statess);
		ArrayList<Feature> safe_state = new ArrayList<Feature>();
		ArrayList<Feature> danger_state = new ArrayList<Feature>();
		ArrayList<Direction> danger_direction = new ArrayList<Direction>();
		// divide safe and danger state
		for(int i = 0; i < around_states.size(); i++){
			int state_index = around_states.get(i);
			Feature f = feature_list.get(state_index);
			String marker = f.getProperty("marker-symbol").getAsString();
			double state_power = f.
			double state_coins = f.
			if (marker.equals("danger")) {
				danger_state.add(f);
			}
			else if (state_power == 0 || state_coins == 0) {
				around_states.remove(i);
			}
			else {
				safe_state.add(f);
			}
		}
		
		
		if (around_states.isEmpty()) {
			// Can't find states in checking area
			Position nextP = current_position.nextPosition(getRandomDirection());
			while (!nextP.inPlayArea()) {
				nextP = current_position.nextPosition(getRandomDirection());
			}
			power = power - cost;
			path.add(nextP);
		} 
		else if(safe_state.isEmpty()) {
			// all states are danger state
			// find all direction
			for (int i = 0; i < danger_state.size(); i++) {
				Point p = (Point) danger_state.get(i).geometry();
				Direction d = findDirec(p);
				danger_direction.add(d);
			}
			
			Direction next_direction = getRandomDirection();
			while (danger_direction.contains(next_direction)) {
				// keep random until find a safe direction.
				next_direction = getRandomDirection();
			}
		}
		//JsonElement coins_je  = f.getProperty("coins");
		//JsonElement power_je = f.getProperty("power");
		else {
			// choose the state have max power
			double max = 0;
			Feature powerest_state = safe_state.get(0);
			for(int i = 0; i < safe_state.size(); i++) {
				Feature f = feature_list.get(i);
				double state_power = f.getProperty("power").getAsDouble();
				if (state_power > max) {
					max = state_power;
					powerest_state = f;
				}
			}
			// move to the nearest safe state
			Point p = (Point) powerest_state.geometry();
			Direction dirc = findDirec(p);
			Position nextP = current_position.nextPosition(dirc);
			charged(powerest_state);
			power = power - cost;
			path.add(nextP);
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
	public void charged(Feature powerest_state) {
		Point p = (Point) powerest_state.geometry();
		double lat1 = current_position.latitude;
		double lon1 = current_position.longitude;
		double lat2 = p.coordinates().get(1);
		double lon2 = p.coordinates().get(0);
		double dist = distance(lon1, lat1, lon2, lat2);
		if (dist <= 0.00025) {
			double state_power = powerest_state.getProperty("power").getAsDouble();
			double state_coins = powerest_state.getProperty("coins").getAsDouble();
			power = power + state_power;
			coins = coins + state_coins;
		}
	}
	

}
