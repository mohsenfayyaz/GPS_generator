package main;

import GraphHopperHandler.GraphHopperHandler;

public class Main {

    public static void main(String[] args) {
        String osmFile = "./assets/Tehran.osm.pbf";
        double latFrom = 35.727389, lonFrom = 51.407537, latTo = 35.733850, lonTo = 51.399460;

        GraphHopperHandler myGHHandler = new GraphHopperHandler(osmFile);
        myGHHandler.findRoute(latFrom, lonFrom, latTo, lonTo);
    }

}