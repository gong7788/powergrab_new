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
        String mapSource = Geomap.getmapSource();
		
        // System.out.println(mapSource);
        
    }
    
    
    
}
