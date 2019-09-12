package GPSGenerator;

import GraphHopperHandler.GraphHopperHandler;
import com.graphhopper.PathWrapper;
import com.graphhopper.util.PointList;
import com.graphhopper.util.shapes.GHPoint3D;
import com.grum.geocalc.Coordinate;
import com.grum.geocalc.EarthCalc;
import com.grum.geocalc.Point;

import java.util.Iterator;
import java.util.List;

public class GPSGenerator {
    private GraphHopperHandler myGHHandler;
    public GPSGenerator(String osmFile){
        myGHHandler = new GraphHopperHandler(osmFile);
    }

    public void generate(double latFrom, double lonFrom, double latTo, double lonTo, double sampleRateMeters, double noiseVariance){
        List<PathWrapper> myPathList = myGHHandler.findRoutes(latFrom, lonFrom, latTo, lonTo);
        for (PathWrapper path : myPathList) {
            path = samplePath(path, sampleRateMeters);
        }
    }

    private PathWrapper samplePath(PathWrapper path, double sampleRateMeters){
        PathWrapper samplinPath = new PathWrapper();
        PointList samplingPoints = new PointList();
        PointList points = path.getPoints();
        Iterator<GHPoint3D> pointsIterator = points.iterator();


        GHPoint3D currentPoint = pointsIterator.next(), previousPoint;
        while(pointsIterator.hasNext()){
            previousPoint = currentPoint;
            currentPoint = pointsIterator.next();
            double pointsDistance = calcDistance(previousPoint.getLat(), previousPoint.getLon(), currentPoint.getLat(), currentPoint.getLon());


            double passedDistance = 0;
            while(passedDistance + sampleRateMeters < pointsDistance){
                passedDistance += sampleRateMeters;

            }


        }
        System.out.println(points.iterator());
        return samplinPath;
    }

    private double calcDistance(double latFrom, double lonFrom, double latTo, double lonTo){
        //Prev Point
        Coordinate lat = Coordinate.fromDegrees(latFrom);
        Coordinate lng = Coordinate.fromDegrees(lonFrom);
        Point prev = Point.at(lat, lng);

        //Cur Point
        lat = Coordinate.fromDegrees(latTo);
        lng = Coordinate.fromDegrees(lonTo);
        Point curr = Point.at(lat, lng);

        // Spherical law of cosines - Harvesine formula - Vincenty formula(most accurate)
        double distance = EarthCalc.vincentyDistance(prev, curr); //in meters
        return distance;
    }
}
