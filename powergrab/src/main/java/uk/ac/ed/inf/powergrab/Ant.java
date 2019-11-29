package uk.ac.ed.inf.powergrab;

import java.util.ArrayList;
import java.util.Random;

class Ant {
    private ArrayList<Station> passedStation;  // store station the ant already passed
    private ArrayList<Station> allowedStation; // stations that not passed
    private ArrayList<Station> search_list;  // all safe stations
    private double[][] delta;
    private double[][] distance; // distance matrix
    private double alpha; // heuristic factor
    private double beta; // coefficient of distance between stations

    private double tourLength;
    private int stationNum;
    private Station current_station;

    Ant(int stationNum){
        this.stationNum = stationNum;
        tourLength = 0;
    }

    /**
     * Initialize Ant
     * @param distance         distance matrix
     * @param alpha            heuristic factor
     * @param beta             coefficient of distance between stations
     * @param search_list      all safe stations
     */
    void init(double[][] distance, double alpha, double beta, ArrayList<Station> search_list){
        this.distance = distance;
        this.alpha = alpha;
        this.beta = beta;
        this.search_list = search_list;
        this.allowedStation = new ArrayList<Station>(search_list);

        passedStation = new ArrayList<Station>();

        delta = new double[stationNum][stationNum];
        for (int i = 0; i < stationNum; i++) {
            for (int j = 0; j < stationNum; j++) {
                delta[i][j] = 0;
            }
        }

        Station init_station = search_list.get(0);
        passedStation.add(init_station);
        current_station = init_station;
        allowedStation.remove(init_station);
    }
    //--------------------------Methods----------------------------------------------
    //choose next station
    void selectNextStation(double[][] pheromone){
        double[] probability = new double[stationNum];
        double sum = 0;
        int current = current_station.code;

        for (int i = 0; i < allowedStation.size(); i++){
            int index = allowedStation.get(i).code;
            sum += Math.pow(pheromone[current][index], alpha) * Math.pow(1/distance[current][index], beta);
        }

        // calculate probability of each city
        for (int i = 0; i < stationNum; i++) {
            int index = search_list.get(i).code;
            if (allowedStation.contains(search_list.get(i))){
                probability[i] = (Math.pow(pheromone[current][index], alpha) * Math.pow(1/distance[current][index], beta))/sum;
            }
            else probability[i] = 0;
        }

        Random rnd = new Random(5678); //pseudo random
        double rand = rnd.nextDouble();

        //Random pick the next station
        double sumselect = 0;
        int nextStation_index = 0;
        for (int i = 0; i < stationNum; i++) {
            sumselect += probability[i];
            if (sumselect >= rand){
                nextStation_index = i;
                break;
            }
        }

        if (nextStation_index != 0){
            Station nextStation = search_list.get(nextStation_index);
            passedStation.add(nextStation);
            allowedStation.remove(nextStation);
            current_station = nextStation;
        }

    }

    // calculate path distance
    private double calculateTourLength(){
        double length = 0;
        for (int i = 0; i < passedStation.size()-1; i++) {
            int first = passedStation.get(i).code;
            int second = passedStation.get(i+1).code;
            length += distance[first][second];
        }
        return length;
    }

    //-----------------------Setters and Getters-------------------------
    double getTourLength() {
        tourLength = calculateTourLength();
        return tourLength;
    }

    ArrayList<Station> getPassedStation() {
        return passedStation;
    }

    double[][] getDelta() {
        return delta;
    }

    void setDelta(double[][] delta) {
        this.delta = delta;
    }

}
