package uk.ac.ed.inf.powergrab;

import java.util.ArrayList;

class DrawLines {
    private ArrayList<Position> path;
    private String head;

    private String tail =
            "]\n" +
            "}\n" +
            "}\n" +
            "]\n" +
            "}";
    private String path_line = "";

    DrawLines(ArrayList<Position> path, String head) {
        this.path = path;
        this.head = head + " {\n" +
                "      \"type\": \"Feature\",\n" +
                "      \"properties\": {},\n" +
                "      \"geometry\": {\n" +
                "      \"type\": \"LineString\",\n" +
                "      \"coordinates\": [";
    }

    //Draw lines on the map
    String output(){
        path_line = path_line + head;
        int x = path.size();
        for (int i= 0; i<path.size()-1; i++){
            Position p = path.get(i);
            double longitude = p.longitude;
            double latitude = p.latitude;
            String coor = "[" + longitude+ ","+ latitude + "],\n";
            path_line = path_line + coor;
        }
        Position last = path.get(x-1);
        path_line = path_line +"[" + last.longitude+ ","+ last.latitude + "]\n" ;
        path_line = path_line + tail;
        return path_line;
    }
}
