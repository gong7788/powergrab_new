package uk.ac.ed.inf.powergrab;

import java.util.ArrayList;

class Stateful extends Drone{

    private State initial;
    private ArrayList<State> safe_states = new ArrayList<State>();
    private ArrayList<State> search_list = new ArrayList<State>();
    private ArrayList<State> danger_states = new ArrayList<State>();

    Stateful(double longitude, double latitude, int seed) {
        super(longitude, latitude, seed);
        this.initial = new State(latitude, longitude);
    }

    ArrayList<State> getSafe_states() {
        return safe_states;
    }



    /**
     *  Divide stations into two groups (safe stations and danger stations)
     */
    void divide_safe_danger(){
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
     * Find the next station that Drone will fly
     *
     * @param current_state  the current state
     * @return               the target station
     */
    private State findNext(State current_state){
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
     * changed when it arrives the target station
     *
     * @param target   the target station that will be reached.
     */
    private void move_to_next_state(State target){
        double lat_t = target.getLatitude();
        double lon_t = target.getLongitude();
        double dist;
        do {
            Position nextP = move_one_step(target);

            current_position = nextP;
            double lat_p = nextP.latitude;
            double lon_p = nextP.longitude;
            dist = distance(lon_t, lat_t, lon_p, lat_p);
        }while (dist > 0.00025 && !is_gameover());

        charged(target);
    }

    /**
     * Move one step
     *
     * @param target   the station drone will go
     * @return         the next position will move
     */
    private Position move_one_step(State target){
        double lat_t = target.getLatitude();
        double lon_t = target.getLongitude();
        double min = Double.MAX_VALUE;
        Position next_step = null;
        Direction nextd = null;
        for (Direction d : Direction.values()){
            Position nextP = current_position.nextPosition(d);
            double lat = nextP.latitude;
            double lon = nextP.longitude;
            double dist = distance(lon, lat, lon_t, lat_t);
            if (dist < min && noDangerAround(nextP) && nextP.inPlayArea() && !contain(nextP)) {
                min = dist;
                next_step = nextP;
                nextd = d;
            }
        }
        //if all 16 direction are not valid, choose
        if (nextd == null){
            for (Direction d : Direction.values()){
                Position nextP = current_position.nextPosition(d);
                double lat = nextP.latitude;
                double lon = nextP.longitude;
                double dist = distance(lon, lat, lon_t, lat_t);
                if (dist < min && noDangerAround(nextP) && nextP.inPlayArea()) {
                    min = dist;
                    next_step = nextP;
                    nextd = d;
                }
            }
        }

        update(next_step, nextd);
        return next_step;
    }

    /**
     * Checks any danger station nearby
     * @param p      the position will be checked
     * @return       true if no danger station nearby, o/w false
     */
    private boolean noDangerAround(Position p){
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


    /**
     * Moving randomly
     * Starts moving randomly like stateless after collecting all coins
     */
    private void randomMove(){
        while(!is_gameover()){
            Direction nextd = getRandomDirection();
            Position nextP = current_position.nextPosition(nextd);
            while (!nextP.inPlayArea() || !noDangerAround(nextP)) {
                nextP = current_position.nextPosition(getRandomDirection());
            }
            update(nextP, nextd);
            current_position = nextP;
        }
    }

    /**
     * Checks a position whether in the path
     * @param nextP        the position to be checked
     * @return             true if it's in the path
     */
    private boolean contain(Position nextP){
        for (Position p : path){
            if (nextP.equals(p)) {
                return true;
            }
        }
        return false;
    }

    // Baseline Algorithm: Greedy
    void greedy(){
        path.add(current_position);
        // Choose the first station
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
        // Keep searching next station
        while (!search_list.isEmpty() && !is_gameover()){
            State next_state = findNext(current_state);
            move_to_next_state(next_state);
            current_state = next_state;
        }

        randomMove();

    }

    // Ant Colony System algorithm
    void ACS(){
        path.add(current_position);
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
        while (i < ACS_station_order.size() && !is_gameover()){
            State next_state = findNext(ACS_station_order.get(i));
            i++;
            move_to_next_state(next_state);
            //current_state = next_state;
        }
        // Starting random moving
        randomMove();

    }


}
