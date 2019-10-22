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
	private String mapString = "http://homepages.inf.ed.ac.uk/stg/powergrab/2019/03/03/powergrabmap.geojson";
	private String mapSource = "";
	private double total_coins = 0;

	public ArrayList<State> states = new ArrayList<State>();

	public MyMap() {}
	
	public ArrayList<State> getStates() {
		return states;
	}
	
	public String getmapSource() {
		return mapSource;
	}

	public double getTotal_coins() {
		return total_coins;
	}

	public String getHead(){
		String head = mapSource.substring(0, mapSource.length()-2) + ",";
		return head;
	}

	public void downloadMap() throws IOException {
		
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
		inputStream.close();
	}
	
	public void transfer2States() {
		FeatureCollection fc = FeatureCollection.fromJson(mapSource);
        List<Feature> feature_list = fc.features();
		for(int i = 0; i < 50; i++) {
			Feature f = feature_list.get(i);
			Point p =(Point) f.geometry();
			double longitude = p.coordinates().get(0);
			double latitude = p.coordinates().get(1);
			double coins = f.getProperty("coins").getAsDouble();
			double power = f.getProperty("power").getAsDouble();
			String label = f.getProperty("marker-symbol").getAsString();
			if (label.equals("lighthouse")){
				total_coins = total_coins + coins;
			}
			State state = new State(latitude, longitude, coins, power, label);
			states.add(state);
		}
	}


}
