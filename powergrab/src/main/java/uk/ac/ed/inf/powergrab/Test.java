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
		// TODO Auto-generated method stub
		double longitude = -3.1870;
		double latitude = 55.9426;

		Random rnd = new Random(5678);
		for (int i = 0; i < 10; i++) {
			//System.out.print(Direction.getRandomDirection()+ " ");
			System.out.print(rnd.nextInt(16)+ " ");

			System.out.print(rnd.nextInt(10)+ " ");
			System.out.println();
		}



		
		MyMap Geomap = new MyMap();
        Geomap.downloadMap();
        String mapSource = Geomap.getmapSource();
		
		
		FeatureCollection fc = FeatureCollection.fromJson(mapSource);
        List<Feature> feature_list = fc.features();
        Feature feature1 = feature_list.get(49);
        Geometry g = feature1.geometry();
        Point p = (Point) g;

        Geomap.getStates();
		ArrayList<State> states = Geomap.getStatess();
		
		Stateless stateless_drone = new Stateless(longitude, latitude, 5867);
		stateless_drone.serch_state(Geomap.getstates());
		stateless_drone.setFeature_list(feature_list);
		
		
		//Direction d = stateless_drone.findDirec(p);
//		Position drone = new Position(latitude, longitude);
//		Position next_step = drone.nextPosition(Direction.SSE);
//		Direction d = stateless_drone.findDirec_test(next_step);
//		System.out.println(d);
		// stateless_drone.start();
		
	}

}
