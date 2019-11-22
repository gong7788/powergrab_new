package uk.ac.ed.inf.powergrab;

import java.io.FileWriter;
import java.io.IOException;
import java.util.ArrayList;

public class TestStateful {
    public static void main(String[] args) throws IOException {
        int day = Integer.parseInt(args[0]);
        int month = Integer.parseInt(args[1]);
        int year = Integer.parseInt(args[2]);
        double latitude = Double.parseDouble(args[3]);
        double longitude = Double.parseDouble(args[4]);
        int seed = Integer.parseInt(args[5]);
        int count = 0;

        for (int i = 28; i < 29; i++) {
            day = i;
            month = 10;

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
		    String output = drawer.output();
		    System.out.println(output);

            System.out.printf("Collected: %.4f\n",drone_stateful.getCoins());
            System.out.printf("Total: %.4f\n", Geomap.getTotal_coins());



//        int coin_list = drone_stateful.coins_list.size();
//        int power_list = drone_stateful.power_list.size();
//        int dir_list = drone_stateful.direction_list.size();
//        int path_len = path.size();
//        System.out.printf("Coin size: %d, power: %d, dir: %d, path_len: %d",
//                coin_list, power_list, dir_list, path_len);

            // File Writer
//        String name = String.format("D:\\output\\stateful-%02d-%02d-2019.geojson", day, month);
//        try {
//            FileWriter fw = new FileWriter(name);
//            fw.write(output);
//            fw.close();
//        }
//        catch (Exception e){
//            System.out.print(e);
//            System.err.print("Success...");
//        }
//
//
//            int diff = (int) Math.abs(drone_stateful.getCoins()-Geomap.getTotal_coins());
//            if (diff != 0){
//                count++;
//                System.out.printf("Day: %d, Month: %d, diff: %d \r\n", day, month, diff);
//
//                String name = "D:\\output\\test.txt";
//                String msg = String.format("Day: %d, Month: %d, Diff: %d \r\n", day, month, diff);
//                try {
//                    FileWriter fw = new FileWriter(name, true);
//                    fw.write(msg);
//                    fw.close();
//                }
//                catch (Exception e){
//                    System.out.print(e);
//                    System.err.print("Success...");
//                }
//            }
//            else System.out.printf("Day: %d, Month: %d  Pass \r\n", day, month);

            }

//            if (count == 0){
//                System.out.println("All month passed");
//            }
    }
}
