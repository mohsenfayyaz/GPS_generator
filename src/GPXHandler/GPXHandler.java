package GPXHandler;

import com.graphhopper.util.PointList;
import com.graphhopper.util.shapes.GHPoint3D;

import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Iterator;

public class GPXHandler {

    public static void generateGpx(String filePath, String name, PointList points) {
        File file = new File(filePath);
        try {
            file.createNewFile();
        }catch (Exception e){
            System.out.println(e);
        }

        String header = "<?xml version=\"1.0\" encoding=\"UTF-8\" standalone=\"no\" ?><gpx xmlns=\"http://www.topografix.com/GPX/1/1\" creator=\"MapSource 6.15.5\" version=\"1.1\" xmlns:xsi=\"http://www.w3.org/2001/XMLSchema-instance\"  xsi:schemaLocation=\"http://www.topografix.com/GPX/1/1 http://www.topografix.com/GPX/1/1/gpx.xsd\"><trk>\n";
        name = "<name>" + name + "</name><trkseg>\n";

        String segments = "";
        DateFormat df = new SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ssZ");

        Date date = new java.util.Date();

        Iterator<GHPoint3D> pointsIterator = points.iterator();
        while (pointsIterator.hasNext()) {
            GHPoint3D currentPoint = pointsIterator.next();
            segments += "<trkpt lat=\"" + currentPoint.lat + "\" lon=\"" + currentPoint.lon + "\"><time>" + df.format(new Date()) + "</time></trkpt>\n";
        }

        String footer = "</trkseg></trk></gpx>";

        try {
            FileWriter writer = new FileWriter(file, false);
            writer.append(header);
            writer.append(name);
            writer.append(segments);
            writer.append(footer);
            writer.flush();
            writer.close();
            System.out.println("GPX file created successfully");

        } catch (IOException e) {
            System.out.println("Error Writting Path");
            System.out.println(e);
        }
    }

}
