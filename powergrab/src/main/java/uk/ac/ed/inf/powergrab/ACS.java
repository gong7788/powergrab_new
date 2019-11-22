package uk.ac.ed.inf.powergrab;

import java.util.ArrayList;

class ACS {
    private Ant[] ants;
    private int stationNum;

    private double[][] distance;
    private double[][] pheromone;

    private double bestLength;
    private int[] bestTour;

    private int antNum;
    private int generation;
    private double alpha;
    private double beta;
    private double rho;
    private int Q;
    private int deltaType;

    private ArrayList<State> search_list;

    ACS(int stationNum, int antNum, int generation, double alpha, double beta, double rho, int Q, int deltaType, ArrayList<State> search_list){
        this.stationNum = stationNum;
        this.antNum = antNum;
        this.generation = generation;
        this.alpha = alpha;
        this.beta = beta;
        this.rho = rho;
        this.Q = Q;
        this.deltaType = deltaType;
        this.search_list = search_list;

        ants = new Ant[antNum];
    }

    void init(){
        getDistance(search_list);

        pheromone = new double[stationNum][stationNum];
        double start = 1.0 /((stationNum - 1) * antNum);
        for (int i = 0; i < stationNum; i++) {
            for (int j = 0; j < stationNum; j++) {
                pheromone[i][j] = start;
            }
        }

        bestLength = Double.MAX_VALUE;
        bestTour = new int[stationNum];

        for (int i = 0; i < antNum; i++) {
            ants[i] = new Ant(stationNum);
            ants[i].init(distance, alpha, beta, search_list);
        }
    }

    private void getDistance(ArrayList<State> search_list){
        distance = new double[stationNum][stationNum];
        for (int i = 0; i < stationNum-1; i++) {
            distance[i][i] = 0;
            double lat1 = search_list.get(i).getLatitude();
            double lon1 = search_list.get(i).getLongitude();
            for (int j = i+1; j < stationNum; j++) {
                double lat2 = search_list.get(j).getLatitude();
                double lon2 = search_list.get(j).getLongitude();
                distance[i][j] = Math.sqrt(((lat1 - lat2) * (lat1 - lat2) + (lon1 - lon2) * (lon1 - lon2)));
                distance[j][i] = distance[i][j];
            }
        }
    }

    public void solve() {
        for (int g = 0; g < generation; g++) {
            for (int ant = 0; ant < antNum; ant++) {
                for (int i = 0; i < stationNum; i++) {
                    ants[ant].selectNextStation(pheromone);
                }

                if (ants[ant].getTourLength() < bestLength){
                    bestLength = ants[ant].getTourLength();
                    for (int k = 0; k < stationNum; k++) {
                        bestTour[k] = ants[ant].getPassedStation().get(k).code;
                    }
                }

                double[][] delta = ants[ant].getDelta();
                for (int i = 0; i < stationNum; i++) {
                    for (State s : ants[ant].getPassedStation()){
                        int index = s.code;
                        if (deltaType == 0){
                            delta[i][index] = Q;
                        }
                        if (deltaType == 1){
                            delta[i][index] = Q / distance[i][index];
                        }
                        if (deltaType == 2){
                            delta[i][index] = Q / ants[ant].getTourLength();
                        }
                    }
                }
                ants[ant].setDelta(delta);
            }
            updatePheromone();

            for (int i = 0; i < antNum; i++){
                ants[i].init(distance, alpha, beta, search_list);
            }
        }
        //print();
    }

    private void updatePheromone(){
        for (int i = 0; i < stationNum; i++) {
            for (int j = 0; j < stationNum; j++) {
                pheromone[i][j] = pheromone[i][j] * rho;
            }
        }

        for (int i = 0; i < stationNum; i++) {
            for (int j = 0; j < stationNum; j++) {
                for (int ant = 0; ant < antNum; ant++) {
                    pheromone[i][j] += ants[ant].getDelta()[i][j];
                }
            }
        }
    }


    ArrayList<State> findPath(){
        ArrayList<State> station_order = new ArrayList<State>();
        for (int i = 0; i < bestTour.length; i++) {
            int index = bestTour[i];
            State s = search_list.get(index);
            station_order.add(s);
        }
        return station_order;
    }

    double getBestLength() {
        return bestLength;
    }

    int[] getBestTour() {
        return bestTour;
    }
}
