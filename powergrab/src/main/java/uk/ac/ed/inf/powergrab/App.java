package uk.ac.ed.inf.powergrab;

import java.io.IOException;
import java.net.MalformedURLException;
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
	
//	public static String getMap() throws MalformedURLException, IOException {
//		String mapString0101 = "http://homepages.inf.ed.ac.uk/stg/powergrab/2019/01/01/powergrabmap.geojson";
//		String mapSource = null;
//		URL mapURL = new URL(mapString0101);
//		
//		HttpURLConnection conn = (HttpURLConnection) mapURL.openConnection();
//		conn.setReadTimeout(10000); // milliseconds
//		conn.setConnectTimeout(15000); // milliseconds
//		conn.setRequestMethod("GET");
//		conn.setDoInput(true);
//		conn.connect();
//		InputStream inputStream = conn.getInputStream();
//		Scanner reader = new Scanner(inputStream);
//		while (reader.hasNext()) {
//			mapSource = mapSource + reader.nextLine();
//		}
//		mapSource = mapSource.substring(4);
//		
//		return mapSource;
//		
//	}
	
    public static void main( String[] args )
    {
        System.out.println( "Hello World!" );
        double latitude = Double.parseDouble(args[3]);
        double longitude = Double.parseDouble(args[4]);
        
        MyMap Geomap = new MyMap();
        try {
			Geomap.downloadMap();
		} catch (MalformedURLException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
        String mapSource = Geomap.getmapSource();
		
        // System.out.println(mapSource);
        
    }
    
    
    
}
