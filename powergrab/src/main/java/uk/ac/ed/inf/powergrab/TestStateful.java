package uk.ac.ed.inf.powergrab;

import java.io.IOException;
import java.util.ArrayList;

public class TestStateful {
    public static void main(String[] args) {
        int day = Integer.parseInt(args[0]);
        int month = Integer.parseInt(args[1]);
        int year = Integer.parseInt(args[2]);
        double latitude = Double.parseDouble(args[3]);
        double longitude = Double.parseDouble(args[4]);
        int seed = Integer.parseInt(args[5]);

        MyMap Geomap = new MyMap(day, month, year);
        try {
            Geomap.downloadMap();
        } catch (IOException e) {
            e.printStackTrace();
        }

        Geomap.transfer2States();
        ArrayList<State> states = Geomap.getStates();
        Stateful drone_stateful = new Stateful(longitude, latitude, seed);
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
