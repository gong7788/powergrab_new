package uk.ac.ed.inf.powergrab;

import java.io.FileWriter;
import java.io.IOException;
import java.net.MalformedURLException;
import java.util.ArrayList;

/**
 * Hello world!
 *
 */
public class App {
    public static void main( String[] args ) {
        int day = Integer.parseInt(args[0]);
        int month = Integer.parseInt(args[1]);
        int year = Integer.parseInt(args[2]);
        double latitude = Double.parseDouble(args[3]);
        double longitude = Double.parseDouble(args[4]);
        int seed = Integer.parseInt(args[5]);
        String type = args[6];
        
        MyMap Geomap = new MyMap(day, month, year);
        try {
			Geomap.downloadMap();
		} catch (MalformedURLException e) {
			e.printStackTrace();
		} catch (IOException e) {
			e.printStackTrace();
		}

        Geomap.transfer2States();
        ArrayList<State> states = Geomap.getStates();
        String head = Geomap.getHead();
        String output;

        if (type.equals("stateless")){
            Stateless stateless_drone = new Stateless(longitude, latitude, seed);
            stateless_drone.setStates(states);
            stateless_drone.start();
            ArrayList<Position> path = stateless_drone.getPath();

            DrawLines drawer = new DrawLines(path, head);
            output = drawer.output();
//            System.out.println(output);
//
//            System.out.println("Collected: " + stateless_drone.getCoins());
//            System.out.println("Total: " + Geomap.getTotal_coins());
            writefile(path, stateless_drone.coins_list, stateless_drone.power_list, stateless_drone.direction_list,
                    day, month, year, output, type);
        }
        else if (type.equals("stateful")){
            Stateful drone_stateful = new Stateful(longitude, latitude, seed);
            drone_stateful.setStates(states);
            drone_stateful.divide_safe_danger();

            drone_stateful.ACS();
            ArrayList<Position> path = drone_stateful.getPath();

            DrawLines drawer = new DrawLines(path, head);
            output = drawer.output();
//            System.out.println(output);

//            System.out.printf("Collected: %.4f\n",drone_stateful.getCoins());
//            System.out.printf("Total: %.4f\n", Geomap.getTotal_coins());
//
//            int coin_list = drone_stateful.coins_list.size();
//            int power_list = drone_stateful.power_list.size();
//            int dir_list = drone_stateful.direction_list.size();
//            int path_len = path.size();
//            System.out.printf("Coin size: %d, power: %d, dir: %d, path_len: %d",
//                    coin_list, power_list, dir_list, path_len);

            writefile(path, drone_stateful.coins_list, drone_stateful.power_list, drone_stateful.direction_list,
                    day, month, year, output, type);
        }

        // File Writer
//        String name = String.format("D:\\output\\stateful-%02d-%02d-%d.geojson", day, month, year);
//        try {
//            FileWriter fw = new FileWriter(name);
//            fw.write(output);
//            fw.close();
//        }
//        catch (Exception e) {
//            e.printStackTrace();
//        }
        
    }

    private static void writefile(ArrayList<Position> path,
                   ArrayList<Double> coins_list,
                   ArrayList<Double> power_list,
                   ArrayList<Direction> direction_list,
                   int day, int month, int year, String output, String type) {

        String name = String.format("D:\\output\\%s-%02d-%02d-%d.geojson", type, day, month, year);
        try {
            FileWriter fw = new FileWriter(name);
            fw.write(output);
            fw.close();
        }
        catch (Exception e) {
            e.printStackTrace();
        }

        for (int i = 0; i < path.size()-1; i++) {
            double pre_lat = path.get(i).latitude;
            double pre_long = path.get(i).longitude;
            double lat = path.get(i + 1).latitude;
            double lon = path.get(i + 1).longitude;
            Direction d = direction_list.get(i);
            double power = power_list.get(i);
            double coin = coins_list.get(i);
            String msg = pre_lat + "," + pre_long + "," + d + "," + lat + "," + lon + "," + coin + "," + power + "\r\n";

            name = String.format("D:\\output\\%s-%02d-%02d-%d.txt", type, day, month, year);
            try {
                FileWriter fw = new FileWriter(name, true);
                fw.write(msg);
                fw.close();
            } catch (Exception e) {
                e.printStackTrace();
            }

        }
    }
    
}
