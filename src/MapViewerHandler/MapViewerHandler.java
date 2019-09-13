package MapViewerHandler;

import javax.swing.JFrame;
import com.graphhopper.PathWrapper;
import com.graphhopper.util.shapes.GHPoint3D;
import org.jxmapviewer.JXMapKit;
import org.jxmapviewer.JXMapViewer;
import org.jxmapviewer.OSMTileFactoryInfo;
import org.jxmapviewer.viewer.DefaultTileFactory;
import org.jxmapviewer.viewer.GeoPosition;
import org.jxmapviewer.viewer.TileFactoryInfo;

import java.awt.*;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashSet;
import java.util.List;
import java.util.Set;
import java.util.Iterator;

import org.jxmapviewer.painter.CompoundPainter;
import org.jxmapviewer.painter.Painter;
import org.jxmapviewer.viewer.DefaultWaypoint;
import org.jxmapviewer.viewer.Waypoint;
import org.jxmapviewer.viewer.WaypointPainter;

public class MapViewerHandler {

    final private JXMapKit  mapViewer = new JXMapKit();

    public MapViewerHandler(){
        final JXMapKit  mapViewer = new JXMapKit();
    }

    private void renderFrame(String title){
        // Display the viewer in a JFrame
        JFrame frame = new JFrame(title);
        frame.getContentPane().add(mapViewer);
        frame.setSize(800, 600);
        frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        frame.setVisible(true);

        // Create a TileFactoryInfo for OpenStreetMap
        TileFactoryInfo info = new OSMTileFactoryInfo();
        DefaultTileFactory tileFactory = new DefaultTileFactory(info);
        mapViewer.setTileFactory(tileFactory);
    }

    public void drawRouteOnMap(PathWrapper path, Color color) {
        drawRouteOnMap(path, color, "MohsenFayyaz.ir");
    }

    public void drawRouteOnMap(PathWrapper path, Color color, String title){
        List<GeoPosition> track = makeTrackFromGraphHopperPathWrapper(path);

        renderFrame(title);

        RoutePainter routePainter = new RoutePainter(track, color);

        // Set the focus
        mapViewer.getMainMap().zoomToBestFit(new HashSet<GeoPosition>(track), 0.7);

        // Create waypoints from the geo-positions
        Set<Waypoint> waypoints = new HashSet<Waypoint>(makeDefaultWayPointFromGraphHopperPathWrapper(path));

        // Create a waypoint painter that takes all the waypoints
        WaypointPainter<Waypoint> waypointPainter = new WaypointPainter<Waypoint>();
        waypointPainter.setWaypoints(waypoints);

        // Create a compound painter that uses both the route-painter and the waypoint-painter
        List<Painter<JXMapViewer>> painters = new ArrayList<Painter<JXMapViewer>>();
        painters.add(routePainter);
        painters.add(waypointPainter);

        CompoundPainter<JXMapViewer> painter = new CompoundPainter<JXMapViewer>(painters);
        mapViewer.getMainMap().setOverlayPainter(painter);


    }

    private static List<GeoPosition> makeTrackFromGraphHopperPathWrapper(PathWrapper path) {
        List<GeoPosition> track = new ArrayList<GeoPosition>();
        Iterator<GHPoint3D> pointsIterator = path.getPoints().iterator();
        while (pointsIterator.hasNext()){
            GHPoint3D currentPoint = pointsIterator.next();
            track.add(new GeoPosition(currentPoint.lat, currentPoint.lon));
        }
        return track;
    }

    private static List<DefaultWaypoint> makeDefaultWayPointFromGraphHopperPathWrapper(PathWrapper path) {
        List<DefaultWaypoint> track = new ArrayList<DefaultWaypoint>();
        Iterator<GHPoint3D> pointsIterator = path.getPoints().iterator();
        while (pointsIterator.hasNext()){
            GHPoint3D currentPoint = pointsIterator.next();
            track.add(new DefaultWaypoint(new GeoPosition(currentPoint.lat, currentPoint.lon)));
        }
        return track;
    }


}

