package uk.ac.ed.inf.powergrab;

import java.io.IOException;
import java.io.InputStream;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.ArrayList;
import java.util.List;
import java.util.Scanner;

import com.mapbox.geojson.Feature;
import com.mapbox.geojson.FeatureCollection;
import com.mapbox.geojson.Point;

public class MyMap {
	private String mapString = "http://homepages.inf.ed.ac.uk/stg/powergrab/2019/01/01/powergrabmap.geojson";
	private String mapSource = "";
	private double[] states = new double[100];
	public ArrayList<State> statess = new ArrayList<State>();

	public MyMap() {}
	
	public ArrayList<State> getStatess() {
		return statess;
	}
	
	public String getmapSource() {
		return mapSource;
	}

	public double[] getstates() {
		return states;
		}
	
	public void downloadMap() throws MalformedURLException, IOException {
		
//	    String mapString = getMapString("2019/01/01");
		
		URL mapURL = new URL(mapString);
		
		HttpURLConnection conn = (HttpURLConnection) mapURL.openConnection();
		conn.setReadTimeout(10000); // milliseconds
		conn.setConnectTimeout(15000); // milliseconds
		conn.setRequestMethod("GET");
		conn.setDoInput(true);
		conn.connect();
		InputStream inputStream = conn.getInputStream();
		Scanner reader = new Scanner(inputStream);
		while (reader.hasNext()) {
			mapSource = mapSource + reader.nextLine();
		}
		
	}
	
	public void getStates() throws MalformedURLException, IOException {
		FeatureCollection fc = FeatureCollection.fromJson(mapSource);
        List<Feature> feature_list = fc.features();
		for(int i = 0; i < 50; i++) {
			Feature f = feature_list.get(i);
			Point p =(Point) f.geometry();
			double longitude = p.coordinates().get(0);
			double latitude = p.coordinates().get(1);
			double coins = f.getProperty("coins").getAsDouble();
			double power = f.getProperty("power").getAsDouble();
			//states[2*i] = longitude;
			//states[2*i+1] = latitude;
			State state = new State(latitude, longitude, coins, power);
			statess.add(state);
		}
	}


}
