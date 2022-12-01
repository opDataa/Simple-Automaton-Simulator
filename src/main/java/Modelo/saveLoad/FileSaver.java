/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package Modelo.saveLoad;


import java.io.BufferedWriter;
import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;

/**
 *
 * @author Jtorr
 */
public class FileSaver {
    
  //  private final String DATASET_ABSOLUTE_PATH = this.getClass().getProtectionDomain().getCodeSource().getLocation().toString().split("/target/classes/")[0].split("file:/")[1].replace("/", "\\")+"\\src\\main\\java\\dataset\\";

/*
    
    // sourceFileName:= file where the points have been extracted; if randomGenerated, it will be null;
    public void saveDijkstraSolution(final String sourceFileName, final String STARTING_POINT_NAME, ArrayList<Ruta> shortestPaths)throws IOException{
        final String FILE_TYPE = "tour";
        final String nameOnly = sourceFileName.split("\\.")[0];
        final String FILE_NAME = nameOnly+".opt."+FILE_TYPE;
        final int NUM_PTOS = shortestPaths.size(); 
        
        // Calculates the total distance for each point path combination (starting from the same point: 'STARTING_POINT_NAME')
        double distTotal = 0; //(DIST_TOTAL:= suma de todos los pesos de cada 'Ruta' en 'shortestPaths') 
        for (int i=0;i<shortestPaths.size();i++){  distTotal += shortestPaths.get(i).getWeight(); }
    
        File fileX = new File(DATASET_ABSOLUTE_PATH+FILE_NAME);

        
        // This will output the full path where the file will be written to...
        BufferedWriter out = new BufferedWriter(new FileWriter(fileX));   
        out.write("NAME : "+ FILE_NAME);
        out.newLine();
        out.write("TYPE : "+ FILE_TYPE.toUpperCase());
        out.newLine();       
        out.write("DIMESION : "+ NUM_PTOS+1);
        out.newLine();      
        out.write("SOLUTION : "+ Punto.getDoubleRounded(distTotal));
        out.newLine();
        out.write(FILE_TYPE.toUpperCase()+"_SELECTION");
        out.newLine();
        
        ProgressBar progressBarX = new ProgressBar("SavedPoints", NUM_PTOS); 

        // [FORMAT] :=  pathWeigth - p1,p2,...,pn
        for (int i=0; i<NUM_PTOS; i++){
            Ruta rutaX = shortestPaths.get(i);
            out.write(rutaX.toString());
            out.newLine();
            progressBarX.step();
        }
        // out.write("0"+" - "+STARTING_POINT_NAME);
        // out.newLine();

        out.write(FileLoader.DELIMITER__END_OF_FILE);
        out.close();
    }
    */
}
