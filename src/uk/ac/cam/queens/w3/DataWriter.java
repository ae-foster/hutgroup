package uk.ac.cam.queens.w3;

import java.io.BufferedWriter;
import java.io.File;
import java.io.FileWriter;
import java.io.IOException;

/**
 * Created with IntelliJ IDEA.
 * User: jh
 * Date: 12/6/13
 * Time: 10:17 AM
 * To change this template use File | Settings | File Templates.
 */
public class DataWriter {
    private final static String outputFile = "output.csv";
    private final static String dataPath = "data/";
    private BufferedWriter bw;

    public DataWriter () throws IOException {
        File file = new File(dataPath+outputFile);

        // if file doesnt exists, then create it
        if (!file.exists()) {
            file.createNewFile();
        }

        FileWriter fw = new FileWriter(file.getAbsoluteFile());
        bw = new BufferedWriter(fw);
    }

    public void write (String data) {
        try {
            bw.write(data);
        } catch (IOException e){
            System.out.println("Failed to write to output file");
        }
    }

    public void close (){
        try {
            bw.flush();
            bw.close();
        } catch (IOException e){
            System.out.println("Failed to close output file");
        }
    }
}
