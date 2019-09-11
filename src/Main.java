import com.graphhopper.GHRequest;
import com.graphhopper.GHResponse;
import com.graphhopper.GraphHopper;
import com.graphhopper.PathWrapper;
import com.graphhopper.reader.osm.GraphHopperOSM;
import com.graphhopper.routing.util.EncodingManager;
import com.graphhopper.util.GPXEntry;
import com.graphhopper.util.Instruction;
import com.graphhopper.util.InstructionList;
import com.graphhopper.util.PointList;

import java.util.List;
import java.util.Locale;
import java.util.Map;

public class Main {

    public static void main(String[] args) {
        f();
    }

    public static void f(){
        String osmFile = "./assets/Tehran.osm.pbf";
        String graphFolder = "./output";
        double latFrom = 35.727389, lonFrom = 51.407537, latTo = 35.733850, lonTo = 51.399460;

        // create one GraphHopper instance
        GraphHopper hopper = new GraphHopperOSM().forServer();
        hopper.setDataReaderFile(osmFile);
        // where to store graphhopper files?
        hopper.setGraphHopperLocation(graphFolder);
        hopper.setEncodingManager(EncodingManager.create("car"));

        // now this can take minutes if it imports or a few seconds for loading
        // of course this is dependent on the area you import
        hopper.importOrLoad();

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
            return;
        }

        // use the best path, see the GHResponse class for more possibilities.
        PathWrapper path = rsp.getBest();

        // points, distance in meters and time in millis of the full path
        PointList pointList = path.getPoints();
        double distance = path.getDistance();
        long timeInMs = path.getTime();

        System.out.println(pointList);

        InstructionList il = path.getInstructions();

        // iterate over every turn instruction
        for (Instruction instruction : il) {
            instruction.getDistance();

            //...
        }

        // or get the json
        List<Map<String, Object>> iList = il.createJson();

        // or get the result as gpx entries:
        List<GPXEntry> list = il.createGPXList();
        System.out.println("Hello World!");
    }
}