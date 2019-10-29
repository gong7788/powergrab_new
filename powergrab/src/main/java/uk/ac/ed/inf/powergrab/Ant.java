package uk.ac.ed.inf.powergrab;

import java.util.ArrayList;
import java.util.Random;

public class Ant {
    private ArrayList<State> passedStation;
    private ArrayList<State> allowedStation;
    private ArrayList<State> search_list;
    private double[][] delta; //
    private double[][] distance;
    private double alpha;
    private double beta; // coefficient of distance between stations

    private double tourLength;
    private int stationNum;
    //private State init_station = new State(55.9426, -3.1870);
    private State current_station;

    public Ant(int stationNum){
        this.stationNum = stationNum;
        tourLength = 0;
    }

    public void init(double[][] distance, double alpha, double beta, ArrayList<State> search_list){
        this.distance = distance;
        this.alpha = alpha;
        this.beta = beta;
        this.search_list = search_list;
        this.allowedStation = new ArrayList<State>(search_list);

        passedStation = new ArrayList<State>();

        delta = new double[stationNum][stationNum];
        for (int i = 0; i < stationNum; i++) {
            for (int j = 0; j < stationNum; j++) {
                delta[i][j] = 0;
            }
        }

        State init_station = search_list.get(0);
        passedStation.add(init_station);
        current_station = init_station;
        allowedStation.remove(init_station);
    }

    //choose next station
    public void selectNextStation(double[][] pheromone){
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

        Random rnd = new Random();
        double rand = rnd.nextDouble();

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
            State nextState = search_list.get(nextStation_index);
            passedStation.add(nextState);
            allowedStation.remove(nextState);
            current_station = nextState;
        }

    }

    // calculate distance
    private double calculateTourLength(){
        double length = 0;
        for (int i = 0; i < passedStation.size()-1; i++) {
            int first = passedStation.get(i).code;
            int second = passedStation.get(i+1).code;
            length += distance[first][second];
        }
        return length;
    }

    public ArrayList<State> getAllowedStation() {
        return allowedStation;
    }

    public void setAllowedStation(ArrayList<State> allowedStation) {
        this.allowedStation = allowedStation;
    }

    public double getTourLength() {
        tourLength = calculateTourLength();
        return tourLength;
    }

    public int getStationNum() {
        return stationNum;
    }

    public void setStationNum(int stationNum) {
        this.stationNum = stationNum;
    }

    public ArrayList<State> getPassedStation() {
        return passedStation;
    }

    public void setPassedStation(ArrayList<State> passedStation) {
        this.passedStation = passedStation;
    }

    public double[][] getDelta() {
        return delta;
    }

    public void setDelta(double[][] delta) {
        this.delta = delta;
    }

//    public ArrayList<State> getSearch_list() {
//        return search_list;
//    }

//    public void setSearch_list(ArrayList<State> search_list) {
//        this.search_list = search_list;
//    }
}
