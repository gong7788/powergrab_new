package uk.ac.ed.inf.powergrab;

import java.util.ArrayList;
import java.util.Random;

class Drone {
    private int seed;
    private int step;
    private double coins;
    private double power;

    Position current_position;
    ArrayList<Station> stations = new ArrayList<Station>();
    ArrayList<Position> path = new ArrayList<Position>();
    ArrayList<Double> coins_list = new ArrayList<Double>();
    ArrayList<Double> power_list = new ArrayList<Double>();
    ArrayList<Direction> direction_list = new ArrayList<Direction>();
    private static Direction[] Values = Direction.values();
    private Random rand = new Random(seed);

    Drone(double longitude, double latitude, int seed) {
        this.current_position = new Position(latitude, longitude);
        this.seed = seed;
        this.step = 250;
        this.coins = 0;
        this.power = 250;
    }

    //-------------------------Setters and getters--------------------
    void setStations(ArrayList<Station> stations) {
        this.stations = stations;
    }

    ArrayList<Position> getPath() {
        return path;
    }

    double getCoins() {
        return coins;
    }

    //-------------------------Help function--------------------------
    /**
     * Gives a random direction in 16 directions
     *
     * @return          a random direction
     */
    Direction getRandomDirection() {
        return Values[rand.nextInt(Values.length)];
    }

    /**
     * Calculates distance between two positions
     *
     * @param longitude1    longitude of first position
     * @param latitude1     latitude of first position
     * @param longitude2   longitude of second position
     * @param latitude2     latitude of second position
     * @return              the distance between two positions
     */
    double distance(double longitude1, double latitude1, double longitude2, double latitude2) {
        double dist_sq;
        dist_sq = (longitude1 - longitude2)*(longitude1 - longitude2) +
                (latitude1 - latitude2)*(latitude1 - latitude2);

        return Math.sqrt(dist_sq);
    }

    /**
     * Checks game status
     * @return   true if step <= 0 or power <=0, else false
     */
    boolean is_gameover() {
        return (step <= 0 || power <= 0);
    }

    /**
     * Charges drone when arrive a station, and clear station power/coins
     * @param power_station   the station arrived
     */
    void charged(Station power_station) {
        double coins_s = power_station.getCoins();
        double power_s = power_station.getPower();
        // add coins/power into Drone
        coins = coins + coins_s;
        power = power + power_s;
        // subtract coins/power from state
        power_station.setCoins(0);
        power_station.setPower(0);
        power_station.setEmpty(true);
    }

    /**
     * updates data for each movement, for later output
     * @param p    the next position
     * @param d    the direction of this movement
     */
    void update(Position p, Direction d){
        path.add(p);
        direction_list.add(d);
        step -= 1;
        power -= 1.25;
        coins_list.add(coins);
        power_list.add(power);
    }




}
