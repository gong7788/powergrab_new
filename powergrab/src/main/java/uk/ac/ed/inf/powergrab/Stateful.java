package uk.ac.ed.inf.powergrab;

import java.util.ArrayList;
import java.util.Random;

public class Stateful {
    private int seed;
    private int step;
    private double coins;
    private double power;
    private double cost = 1.25;

    private Position current_position;
    private State initial;
    private ArrayList<State> states = new ArrayList<State>();
    private ArrayList<State> safe_states = new ArrayList<State>();
    private ArrayList<State> search_list = new ArrayList<State>();
    private ArrayList<State> danger_states = new ArrayList<State>();
    private static Direction[] Values = Direction.values();
    private Random rand = new Random(seed);

    private ArrayList<Position> path = new ArrayList<Position>();
    private ArrayList<Double> coins_list = new ArrayList<Double>();
    private ArrayList<Double> power_list = new ArrayList<Double>();

    public Stateful(double longitude, double latitude, int seed) {
        this.current_position = new Position(latitude, longitude);
        this.seed = seed;
        this.step = 250;
        this.coins = 0;
        this.initial = new State(latitude, longitude);
        //TODO check initial power
        this.power = 250;
    }

    public ArrayList<State> getSafe_states() {
        return safe_states;
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

    /**
     *
     * @return          a random direction
     */
    public Direction getRandomDirection() {
        return Values[rand.nextInt(Values.length)];
    }

    public void update(Position p){
        path.add(p);
        coins_list.add(coins);
        power_list.add(power);
    }

    /**
     * Calculates distance between two positions
     *
     * @param longitude1    longitude of first position
     * @param latitude1     latitude of first position
     * @param longitude2    longitude of second position
     * @param latitude2     latitude of second position
     * @return              the distance between two positions
     */
    public double distance(double longitude1, double latitude1, double longitude2, double latitude2) {
        double dist_sq = (longitude1 - longitude2)*(longitude1 - longitude2) +
                (latitude1 - latitude2)*(latitude1 - latitude2);

        return Math.sqrt(dist_sq);
    }

    /**
     *  Divide stations into two groups (safe stations and danger stations)
     */
    public void divide_safe_danger(){
        for (State s: states) {
            if (s.getLabel().equals("lighthouse")){
                safe_states.add(s);
                search_list.add(s);
            }
            else {
                danger_states.add(s);
            }
        }
    }

    /**
     * Find the next station that drone will fly
     *
     * @param current_state  the current state
     * @return               the target station
     */
    public State findNext(State current_state){
        double min = Double.MAX_VALUE;
        int index = 0;
        for (int i = 0; i < search_list.size(); i++){
            State s = search_list.get(i);
            double lat1 = s.getLatitude();
            double lon1 = s.getLongitude();
            double lat_c = current_state.getLatitude();
            double lon_c = current_state.getLongitude();
            double dist_c = distance(lon1, lat1, lon_c, lat_c);
            if (dist_c < min) {
                min = dist_c;
                index = i;
            }
        }
        State s = search_list.get(index);
        search_list.remove(index);
        return s;
    }

    /**
     * Moves to the next station, use {@link #move_one_step(State)} to move step by step,
     * changed drone when it arrives the target station
     *
     * @param target   the target station that will be reached.
     */
    public void move_to_next_state(State target){
        double lat_t = target.getLatitude();
        double lon_t = target.getLongitude();
        double dist;
        do {
            Position nextP = move_one_step(target);
            power = power - cost;
            update(nextP);
            current_position = nextP;
            step -= 1;
            double lat_p = nextP.latitude;
            double lon_p = nextP.longitude;
            dist = distance(lon_t, lat_t, lon_p, lat_p);
        }while (dist > 0.00025 && !gameover());

        charged(target);
    }

    /**
     *
     * @param target
     * @return
     */
    public Position move_one_step(State target){
        double lat_t = target.getLatitude();
        double lon_t = target.getLongitude();
        double min = Double.MAX_VALUE;
        Position next_step = null;
        for (Direction d : Direction.values()){
            Position nextP = current_position.nextPosition(d);
            double lat = nextP.latitude;
            double lon = nextP.longitude;
            double dist = distance(lon, lat, lon_t, lat_t);
            if (dist < min && noDangerAround(nextP) && nextP.inPlayArea() && !contain(nextP)) {
                min = dist;
                next_step = nextP;
            }
        }
        return next_step;
    }

    public boolean noDangerAround(Position p){
        // if there is any danger state in this position area return false, else true
        boolean no_danger = true;
        double lat1 = p.latitude;
        double lon1 = p.longitude;
        for (State s : danger_states){
            double lat2 = s.getLatitude();
            double lon2 = s.getLongitude();
            double dist = distance(lon1, lat1, lon2, lat2);
            if (dist < 0.00025) no_danger = false;
        }
        return no_danger;
    }

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

    public boolean gameover() {
        if (step <= 0 || power <= 0) {
            return true;
        }
        else return false;
    }

    public void randomMove(){
        while(!gameover()){
            Position nextP = current_position.nextPosition(getRandomDirection());
            while (!nextP.inPlayArea() || !noDangerAround(nextP)) {
                nextP = current_position.nextPosition(getRandomDirection());
            }
            power = power - cost;
            update(nextP);
            current_position = nextP;
            step -= 1;
        }
    }

    public boolean contain(Position nextP){
        for (Position p : path){
            if (nextP.equals(p)) {
                return true;
            }
        }
        return false;
    }

    // Baseline Algorithm: Greedy
    public void greedy(){
        update(current_position);
        // First step
        double min = Double.MAX_VALUE;
        int init_index = 0;
        for (int i = 0; i < search_list.size(); i++){
            State first = search_list.get(i);
            double lat1 = first.getLatitude();
            double lon1 = first.getLongitude();
            double lat_init = current_position.latitude;
            double lon_init = current_position.longitude;
            double dist_init = distance(lon1, lat1, lon_init, lat_init);
            if (dist_init < min) {
                min = dist_init;
                init_index = i;
            }
        }

        State first = search_list.get(init_index);
        search_list.remove(init_index);
        move_to_next_state(first);
        State current_state = first;
        while (!search_list.isEmpty() && !gameover()){
            State next_state = findNext(current_state);
            move_to_next_state(next_state);
            current_state = next_state;
        }
        randomMove();

    }

    public void ACS(){
        ArrayList<State> search_list_ = new ArrayList<State>(search_list);
        search_list_.add(0, initial);

        for (int i = 0; i < search_list_.size(); i++) {
            search_list_.get(i).code = i;
        }

        int stationNum = search_list_.size();

        ACS acs = new ACS(stationNum, 40, 100, 5.0, 5.0,
                0.8, 10, 0, search_list_);
        acs.init();
        acs.solve();

        ArrayList<State> ACS_station_order = acs.findPath();
        int i = 1;

        //State current_state = ACS_station_order.get(0);
        while (i < ACS_station_order.size() && !gameover()){
            State next_state = findNext(ACS_station_order.get(i));
            i++;
            move_to_next_state(next_state);
            //current_state = next_state;
        }
        // Starting random moving
        randomMove();

    }


}
