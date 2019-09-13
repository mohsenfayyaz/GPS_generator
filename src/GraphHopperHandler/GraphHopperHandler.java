package GraphHopperHandler;

import com.graphhopper.GHRequest;
import com.graphhopper.GHResponse;
import com.graphhopper.GraphHopper;
import com.graphhopper.PathWrapper;
import com.graphhopper.reader.osm.GraphHopperOSM;
import com.graphhopper.routing.util.EncodingManager;
import org.apache.log4j.BasicConfigurator;
import java.util.List;
import java.util.Locale;

public class GraphHopperHandler {
    private GraphHopper hopper;
    private String graphFolder = "./GHTempFiles";

    public GraphHopperHandler(String osmFile){
        BasicConfigurator.configure();
        // create one GraphHopper instance
        hopper = new GraphHopperOSM().forServer();
        hopper.setDataReaderFile(osmFile);
        // where to store graphhopper files?
        hopper.setGraphHopperLocation(graphFolder);
        hopper.setEncodingManager(EncodingManager.create("car"));

        // now this can take minutes if it imports or a few seconds for loading
        // of course this is dependent on the area you import
        hopper.importOrLoad();


    }

    public List<PathWrapper> findRoutes(double latFrom, double lonFrom, double latTo, double lonTo){
        // simple configuration of the request object, see the GraphHopperServlet classs for more possibilities.
        GHRequest req = new GHRequest(latFrom, lonFrom, latTo, lonTo).
                setWeighting("fastest").
                setVehicle("car").
                setLocale(Locale.US);
        GHResponse rsp = hopper.route(req);

        // first check for errors
        if (rsp.hasErrors()) {
            // handle them!
            // rsp.getErrors()
            return null;
        }

        return rsp.getAll();

//        // use the best path, see the GHResponse class for more possibilities.
//        PathWrapper path = rsp.getBest();

        // points, distance in meters and time in millis of the full path
//        PointList pointList = path.getPoints();
//        double distance = path.getDistance();
//        long timeInMs = path.getTime();
//
//        System.out.println("Route Points: \n" + pointList);
//
//        InstructionList il = path.getInstructions();
//        // iterate over every turn instruction
//        for (Instruction instruction : il) {
//            instruction.getDistance();
//
//            //...
//        }
//
//        // or get the json
//        List<Map<String, Object>> iList = il.createJson();
//
//        // or get the result as gpx entries:
//        List<GPXEntry> list = il.createGPXList();
    }



}
