package uk.ac.ed.inf.powergrab;

import java.util.ArrayList;

class Stateful extends Drone{

    private Station initial;
    private ArrayList<Station> safe_stations = new ArrayList<Station>();
    private ArrayList<Station> search_list = new ArrayList<Station>();
    private ArrayList<Station> danger_stations = new ArrayList<Station>();

    Stateful(double longitude, double latitude, int seed) {
        super(longitude, latitude, seed);
        this.initial = new Station(latitude, longitude);
    }
    //-------------------Setter and Getters--------------------------------
    ArrayList<Station> getSafe_stations() {
        return safe_stations;
    }

    //-------------------Help Functions-------------------------------------

    /**
     *  Divide stations into two groups (safe stations and danger stations)
     */
    void divide_safe_danger(){
        for (Station s: stations) {
            if (s.getLabel().equals("lighthouse")){
                safe_stations.add(s);
                search_list.add(s);
            }
            else {
                danger_stations.add(s);
            }
        }
    }

    /**
     * Find the next station that Drone will fly
     *
     * @param current_station  the current state
     * @return               the target station
     */
    private Station findNext(Station current_station){
        double min = Double.MAX_VALUE;
        int index = 0;
        for (int i = 0; i < search_list.size(); i++){
            Station s = search_list.get(i);
            double lat1 = s.getLatitude();
            double lon1 = s.getLongitude();
            double lat_c = current_station.getLatitude();
            double lon_c = current_station.getLongitude();
            double dist_c = distance(lon1, lat1, lon_c, lat_c);
            if (dist_c < min) {
                min = dist_c;
                index = i;
            }
        }
        Station s = search_list.get(index);
        search_list.remove(index);
        return s;
    }

    /**
     * Moves to the next station, use {@link #move_one_step(Station)} to move step by step,
     * changed when it arrives the target station
     *
     * @param target   the target station that will be reached.
     */
    private void move_to_next_state(Station target){
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
    private Position move_one_step(Station target){
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
            double small_dist2Danger = dangerDist(nextP);
            if (dist < min && dist < small_dist2Danger && nextP.inPlayArea() && !contain(nextP)) {
                min = dist;
                next_step = nextP;
                nextd = d;
            }

        }
        //if all 16 direction are not valid, go back
        if (nextd == null){
            for (Direction d : Direction.values()){
                Position nextP = current_position.nextPosition(d);
                double lat = nextP.latitude;
                double lon = nextP.longitude;
                double dist = distance(lon, lat, lon_t, lat_t);
                if (dist < min && noDangerAround(nextP) && nextP.inPlayArea()&& !contain(nextP)) {
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
     * Checks whether any danger station nearby
     * @param p      the position will be checked
     * @return       true if no danger station nearby, o/w false
     */
    private boolean noDangerAround(Position p){
        // if there is any danger state in this position area return false, else true
        boolean no_danger = true;
        double lat1 = p.latitude;
        double lon1 = p.longitude;
        for (Station s : danger_stations){
            double lat2 = s.getLatitude();
            double lon2 = s.getLongitude();
            double dist = distance(lon1, lat1, lon2, lat2);
            if (dist < 0.00025) no_danger = false;
        }
        return no_danger;
    }

    private double dangerDist(Position p){
        double lat1 = p.latitude;
        double lon1 = p.longitude;
        double min = Double.MAX_VALUE;
        double dist;
        for (Station s : danger_stations){
            double lat2 = s.getLatitude();
            double lon2 = s.getLongitude();
            dist = distance(lon1, lat1, lon2, lat2);
            if (dist < min){
                min = dist;
            }
        }
        return min;
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
     * @return             true if next position is in the path, o/w false
     */
    private boolean contain(Position nextP){
        for (Position p : path){
            if (nextP.equals(p)) {
                return true;
            }
        }
        return false;
    }

    //---------------------Running Stateful----------------------------------
    // Baseline Algorithm: Greedy
    void greedy(){
        path.add(current_position);
        // Choose the first station
        double min = Double.MAX_VALUE;
        int init_index = 0;
        for (int i = 0; i < search_list.size(); i++){
            Station first = search_list.get(i);
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

        Station first = search_list.get(init_index);
        search_list.remove(init_index);
        move_to_next_state(first);
        Station current_station = first;
        // Keep searching next station
        while (!search_list.isEmpty() && !is_gameover()){
            Station next_station = findNext(current_station);
            move_to_next_state(next_station);
            current_station = next_station;
        }

        randomMove();

    }

    // Ant Colony System algorithm
    void ACS(){
        path.add(current_position);
        ArrayList<Station> search_list_ = new ArrayList<Station>(search_list);
        search_list_.add(0, initial);

        for (int i = 0; i < search_list_.size(); i++) {
            search_list_.get(i).code = i;
        }

        int stationNum = search_list_.size();

        //Adjusts parameters will effect the ACS algorithm
        ACS acs = new ACS(stationNum, 40, 100, 5.0, 5.0,
                0.8, 10, 0, search_list_);
        acs.init();
        acs.solve();

        ArrayList<Station> ACS_station_order = acs.findPath();
        int i = 1;

        //Station current_state = ACS_station_order.get(0);
        while (i < ACS_station_order.size() && !is_gameover()){
            Station next_station = findNext(ACS_station_order.get(i));
            i++;
            move_to_next_state(next_station);
            //current_state = next_station;
        }
        // Starting random moving
        randomMove();

    }


}
