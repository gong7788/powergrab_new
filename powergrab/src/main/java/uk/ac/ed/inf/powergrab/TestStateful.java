package uk.ac.ed.inf.powergrab;

import java.io.IOException;
import java.util.ArrayList;

public class TestStateful {
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
        // Greedy
//        drone_stateful.greedy();
//
//        ArrayList<Position> path = drone_stateful.getPath();
//        String head = Geomap.getHead();
//		DrawLines drawer = new DrawLines(path, head);
//		System.out.println(drawer.output());
//
//        System.out.printf("Collected: %.4f\n",drone_stateful.getCoins());
//        System.out.printf("Total: %.4f\n", Geomap.getTotal_coins());


        // ACS
        drone_stateful.ACS();
        ArrayList<Position> path = drone_stateful.getPath();
        String head = Geomap.getHead();
		DrawLines drawer = new DrawLines(path, head);
		System.out.println(drawer.output());

        System.out.printf("Collected: %.4f\n",drone_stateful.getCoins());
        System.out.printf("Total: %.4f\n", Geomap.getTotal_coins());
    }
}
