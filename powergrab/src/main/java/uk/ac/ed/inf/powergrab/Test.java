package uk.ac.ed.inf.powergrab;

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
		
		Stateless stateless_drone = new Stateless(longitude, latitude, 5678);
		stateless_drone.setStates(states);
		stateless_drone.start();
		ArrayList<Position> path = stateless_drone.getPath();
//		for (Position point : path) {
//			System.out.printf("Position: [%f, %f]\n", point.longitude, point.latitude);
//		}
		System.out.println("Collected: " + stateless_drone.getCoins());
		System.out.println("Total: " + Geomap.getTotal_coins());
		String head = Geomap.getHead();

		DrawLines drawer = new DrawLines(path, head);

		System.out.println(drawer.output());

//		for (int i = 0; i < 50; i++) {
//			Direction d = stateless_drone.getRandomDirection();
//			System.out.println(d);
//		}
		
		//Direction d = stateless_drone.findDirec(p);
//		Position drone = new Position(latitude, longitude);
//		Position next_step = drone.nextPosition(Direction.SSE);
//		Direction d = stateless_drone.findDirec_test(next_step);
//		System.out.println(d);
		// stateless_drone.start();
		
	}

}
