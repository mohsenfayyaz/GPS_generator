package GPSGenerator;

import GPXHandler.GPXHandler;
import GraphHopperHandler.GraphHopperHandler;
import MapViewerHandler.MapViewerHandler;
import com.graphhopper.PathWrapper;
import com.graphhopper.util.PointList;
import com.graphhopper.util.shapes.GHPoint3D;
import com.grum.geocalc.Coordinate;
import com.grum.geocalc.EarthCalc;
import com.grum.geocalc.Point;

import java.awt.*;
import java.io.File;
import java.util.Iterator;
import java.util.List;
import java.util.Random;

public class GPSGenerator {
    private GraphHopperHandler myGHHandler;

    public GPSGenerator(String osmFile) {
        myGHHandler = new GraphHopperHandler(osmFile);
    }

    public void generate(double latFrom, double lonFrom, double latTo, double lonTo, double sampleRateMeters, double noiseStandardDeviation) {
        List<PathWrapper> myPathList = myGHHandler.findRoutes(latFrom, lonFrom, latTo, lonTo);
        for (PathWrapper path : myPathList) {
            System.out.println("-----------");
            System.out.println(path.getPoints());

            new MapViewerHandler().drawRouteOnMap(path, Color.BLUE, "GRAPH HOPPER ROUTE");

            path = samplePath(path, sampleRateMeters);
            new MapViewerHandler().drawRouteOnMap(path, Color.GREEN, "SAMPLED ROUTE");

            path = addNoise(path, noiseStandardDeviation, 0);
            new MapViewerHandler().drawRouteOnMap(path, Color.GREEN, "NOISY SAMPLED ROUTE");


            GPXHandler.generateGpx("gpx.gpx", "gpx", path.getPoints());
        }


    }

    private PathWrapper addNoise(PathWrapper path, double noiseStandardDeviation, double mean) {
        PointList noisePoints = new PointList();
        PointList points = path.getPoints();
        Iterator<GHPoint3D> pointsIterator = points.iterator();

        Random rand = new Random();

        while (pointsIterator.hasNext()) {
            GHPoint3D currentPoint = pointsIterator.next();
            double randDistance = rand.nextGaussian() * noiseStandardDeviation + mean;
            double randBearing = rand.nextDouble() * 360;
            GHPoint3D randPoint = calcNextPointByBearing(currentPoint.lat, currentPoint.lon, randBearing, randDistance);
            noisePoints.add(randPoint);
        }

        PathWrapper noisePath = new PathWrapper();
        noisePath.setPoints(noisePoints);
        return noisePath;
    }


    private PathWrapper samplePath(PathWrapper path, double sampleRateMeters) {
        PointList samplingPoints = new PointList();
        PointList points = path.getPoints();
        Iterator<GHPoint3D> pointsIterator = points.iterator();


        GHPoint3D currentPoint = pointsIterator.next(), previousPoint;
        samplingPoints.add(currentPoint); // add start point

        double passedDistance = 0;
        while (pointsIterator.hasNext()) {
            previousPoint = currentPoint;
            currentPoint = pointsIterator.next();
            double pointsDistance = calcDistance(previousPoint.getLat(), previousPoint.getLon(), currentPoint.getLat(), currentPoint.getLon());

            while (passedDistance + sampleRateMeters < pointsDistance) {
                passedDistance += sampleRateMeters;
                GHPoint3D betweenPoint = calcNextPointBetweenPoints(previousPoint.getLat(), previousPoint.getLon(), currentPoint.getLat(), currentPoint.getLon(), passedDistance);
                samplingPoints.add(betweenPoint);
            }
            passedDistance = -(pointsDistance - passedDistance);

        }
        samplingPoints.add(currentPoint); // add last point

        PathWrapper samplingPath = new PathWrapper();
        samplingPath.setPoints(samplingPoints);
        return samplingPath;
    }

    private double calcDistance(double latFrom, double lonFrom, double latTo, double lonTo) {
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

    private double calcBearing(double latFrom, double lonFrom, double latTo, double lonTo) {
        //Prev Point
        Coordinate lat = Coordinate.fromDegrees(latFrom);
        Coordinate lng = Coordinate.fromDegrees(lonFrom);
        Point prev = Point.at(lat, lng);

        //Cur Point
        lat = Coordinate.fromDegrees(latTo);
        lng = Coordinate.fromDegrees(lonTo);
        Point curr = Point.at(lat, lng);

        double bearing = EarthCalc.vincentyFinalBearing(prev, curr); //in decimal degrees
        return bearing;
    }

    private GHPoint3D calcNextPointBetweenPoints(double latFrom, double lonFrom, double latTo, double lonTo, double distance) {
        Coordinate lat = Coordinate.fromDegrees(latFrom);
        Coordinate lng = Coordinate.fromDegrees(lonFrom);
        Point startPoint = Point.at(lat, lng);

        //Distance away point, bearing is from start to destination
        Point otherPoint = EarthCalc.pointAt(startPoint, calcBearing(latFrom, lonFrom, latTo, lonTo), distance);
        return new GHPoint3D(otherPoint.latitude, otherPoint.longitude, 0);
    }

    private GHPoint3D calcNextPointByBearing(double latFrom, double lonFrom, double bearing, double distance) {
        Coordinate lat = Coordinate.fromDegrees(latFrom);
        Coordinate lng = Coordinate.fromDegrees(lonFrom);
        Point startPoint = Point.at(lat, lng);

        //Distance away point, bearing is from start to destination
        Point otherPoint = EarthCalc.pointAt(startPoint, bearing, distance);
        return new GHPoint3D(otherPoint.latitude, otherPoint.longitude, 0);
    }
}
