package uk.ac.ed.inf.powergrab;

import java.io.FileWriter;
import java.io.IOException;
import java.net.MalformedURLException;
import java.util.ArrayList;
import java.util.List;

import com.mapbox.geojson.Feature;
import com.mapbox.geojson.FeatureCollection;
import com.mapbox.geojson.Geometry;
import com.mapbox.geojson.Point;

/**
 * Hello world!
 *
 */
public class App 
{
    public static void main( String[] args )
    {
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
        String output = "";

        if (type.equals("stateless")){
            Stateless stateless_drone = new Stateless(longitude, latitude, seed);
            stateless_drone.setStates(states);
            stateless_drone.start();
            ArrayList<Position> path = stateless_drone.getPath();

            DrawLines drawer = new DrawLines(path, head);
            output = drawer.output();
            System.out.println(output);

            System.out.println("Collected: " + stateless_drone.getCoins());
            System.out.println("Total: " + Geomap.getTotal_coins());
        }
        else if (type.equals("stateful")){
            Stateful drone_stateful = new Stateful(longitude, latitude, seed);
            drone_stateful.setStates(states);
            drone_stateful.divide_safe_danger();

            drone_stateful.ACS();
            ArrayList<Position> path = drone_stateful.getPath();

            DrawLines drawer = new DrawLines(path, head);
            output = drawer.output();
            System.out.println(output);

            System.out.printf("Collected: %.4f\n",drone_stateful.getCoins());
            System.out.printf("Total: %.4f\n", Geomap.getTotal_coins());
        }

        // File Writer
//        String name = String.format("D:\\output\\stateful-%02d-%02d-2019.geojson", day, month);
//        try {
//            FileWriter fw = new FileWriter(name);
//            fw.write(output);
//            fw.close();
//        }
//        catch (Exception e) {
//            e.printStackTrace();
//        }
        
    }
    
}
