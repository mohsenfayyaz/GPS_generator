package GPSGenerator;

import GraphHopperHandler.GraphHopperHandler;
import com.graphhopper.PathWrapper;

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

        return path;
    }
}
