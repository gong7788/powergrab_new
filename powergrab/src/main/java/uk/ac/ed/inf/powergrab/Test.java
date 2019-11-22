package uk.ac.ed.inf.powergrab;

import java.io.FileWriter;
import java.io.IOException;
import java.net.MalformedURLException;
import java.util.ArrayList;
import java.util.List;
import java.util.Random;

import com.mapbox.geojson.Feature;
import com.mapbox.geojson.FeatureCollection;
import com.mapbox.geojson.Geometry;
import com.mapbox.geojson.Point;

public class Test {

	public static void main(String[] args) throws MalformedURLException, IOException {
		int day = Integer.parseInt(args[0]);
		int month = Integer.parseInt(args[1]);
		int year = Integer.parseInt(args[2]);
		double latitude = Double.parseDouble(args[3]);
		double longitude = Double.parseDouble(args[4]);
		int seed = Integer.parseInt(args[5]);

		MyMap Geomap = new MyMap(day, month, year);
        Geomap.downloadMap();
        String mapSource = Geomap.getmapSource();
//		System.out.println(mapSource);
		
//		FeatureCollection fc = FeatureCollection.fromJson(mapSource);
//        List<Feature> feature_list = fc.features();
//        Feature feature1 = feature_list.get(49);
//        Geometry g = feature1.geometry();
//        Point p = (Point) g;


        Geomap.transfer2States();
		ArrayList<State> states = Geomap.getStates();
		
		Stateless stateless_drone = new Stateless(longitude, latitude, seed);
		stateless_drone.setStates(states);
		stateless_drone.start();
		ArrayList<Position> path = stateless_drone.getPath();


		String head = Geomap.getHead();

		DrawLines drawer = new DrawLines(path, head);

		System.out.println(drawer.output());
		System.out.println("Collected: " + stateless_drone.getCoins());
		System.out.println("Total: " + Geomap.getTotal_coins());

		int coin_list = stateless_drone.coins_list.size();
        int power_list = stateless_drone.power_list.size();
        int dir_list = stateless_drone.direction_list.size();
        int path_len = path.size();
        System.out.printf("Coin size: %d, power: %d, dir: %d, path_len: %d \n",
                coin_list, power_list, dir_list, path_len);

        for (int i = 0; i < 250; i++){
        	Double pre_lat = path.get(i).latitude;
        	Double pre_long = path.get(i).longitude;
        	Double lat = path.get(i+1).latitude;
        	Double lon = path.get(i+1).longitude;
        	Direction d = stateless_drone.direction_list.get(i);
        	Double power = stateless_drone.power_list.get(i);
        	Double coin = stateless_drone.coins_list.get(i);
        	String msg = pre_lat +","+ pre_long +","+ d +","+ lat +","+ lon +","+ coin +","+ power +"\r\n";

        	String name = String.format("D:\\output\\stateless-%02d-%02d-%d.txt", day, month, year);
			try {
				FileWriter fw = new FileWriter(name, true);
				fw.write(msg);
				fw.close();
			}
			catch (Exception e){
				System.out.print(e);
				System.err.print("Success...");
			}
		}
	}

}
