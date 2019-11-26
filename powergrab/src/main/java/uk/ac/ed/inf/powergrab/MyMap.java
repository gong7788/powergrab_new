package uk.ac.ed.inf.powergrab;

import java.io.IOException;
import java.io.InputStream;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.ArrayList;
import java.util.List;
import java.util.Scanner;

import com.mapbox.geojson.Feature;
import com.mapbox.geojson.FeatureCollection;
import com.mapbox.geojson.Point;

class MyMap {
	private String mapString;
	private String mapSource = "";
	private double total_coins = 0;

	private ArrayList<Station> stations = new ArrayList<Station>();

	MyMap(int day, int month, int year) {
		mapString = String.format("http://homepages.inf.ed.ac.uk/stg/powergrab/%02d/%02d/%02d/powergrabmap.geojson",
				year, month, day);
	}

	//-----------------------Setters and Getters-------------------------------------
	ArrayList<Station> getStations() {
		return stations;
	}
	
	String getmapSource() {
		return mapSource;
	}

	double getTotal_coins() {
		return total_coins;
	}

	String getHead(){
		return mapSource.substring(0, mapSource.length()-2) + ",";
	}

	//------------------------------Methods------------------------------------------

	/**
	 * Downloads map and get String form
	 */
	void downloadMap() throws IOException {
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

	/**
	 * Transfer all features into Station, then add them into a list
	 */
	void transfer2States(){
		FeatureCollection fc = FeatureCollection.fromJson(mapSource);
        List<Feature> feature_list = fc.features();
		for(Feature f : feature_list) {
			Point p =(Point) f.geometry();
			double longitude = p.coordinates().get(0);
			double latitude = p.coordinates().get(1);
			double coins = f.getProperty("coins").getAsDouble();
			double power = f.getProperty("power").getAsDouble();
			String label = f.getProperty("marker-symbol").getAsString();
			if (label.equals("lighthouse")){
				total_coins = total_coins + coins;
			}
			Station station = new Station(latitude, longitude, coins, power, label);
			stations.add(station);
		}
	}


}
