package uk.ac.ed.inf.powergrab;

import java.io.IOException;
import java.util.ArrayList;

public class ACSTest {
    public static void main(String[] args) {
        double longitude = -3.1870;
        double latitude = 55.9426;

        MyMap Geomap = new MyMap();
        try {
            Geomap.downloadMap();
        } catch (IOException e) {
            e.printStackTrace();
        }

        Geomap.transfer2States();
        ArrayList<State> states = Geomap.getStates();
        Stateful drone_stateful = new Stateful(longitude, latitude, 5678);
        drone_stateful.setStates(states);
        drone_stateful.divide_safe_danger();
        ArrayList<State> search_list = drone_stateful.getSafe_states();
        State init_station = new State(55.9426, -3.1870);
        search_list.add(0, init_station);
        for (int i = 0; i < search_list.size(); i++) {
            search_list.get(i).code = i;
        }

        int stationNum = search_list.size();

        long startTime = System.currentTimeMillis();
        ACS acs = new ACS(stationNum, 40, 100, 5.0, 5.0,
                0.8, 10, 0, search_list);
        acs.init();
        acs.solve();
        long endTime = System.currentTimeMillis();

        System.out.println(acs.getBestLength() + "\t" + (endTime - startTime) / 1000F + "s");

        int[] path_ = acs.getBestTour();


        String head = Geomap.getHead();




    }
}
