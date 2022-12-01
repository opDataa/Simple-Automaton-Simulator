/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package Modelo.saveLoad;

import Modelo.AutomataDeterminista;
import Modelo.Interfaces.AbstractAutomata;
import java.io.File;
import java.io.FileNotFoundException;
import java.util.HashSet;
import java.util.NoSuchElementException;
import java.util.Scanner;
import javax.swing.JFileChooser;

/**
 *
 * @author Jtorr
 */
public class FileLoader {
    private final String DATASET_ABSOLUTE_PATH = this.getClass().getProtectionDomain().getCodeSource().getLocation().toString().split("/target/classes/")[0].split("file:/")[1].replace("/", "\\")+"\\src\\main\\java\\dataset\\";

    private static final String SPACE_REGREX = "(?= )";
    private static final String DELIMITER__NUM_STATES = "ESTADOS:";
    private static final String DELIMITER__INITIAL_STATES = "INICIAL:";
    private static final String DELIMITER__FINAL_STATES = "FINALES:";
    private static final String DELIMITER__START_TRANSACTIONS = "TRANSICIONES:";
    public static final String DELIMITER__END_OF_FILE = "FIN";
    private static final String CHAR_REGREX = "(?=('\\w'))"; // Funciona??
    
    
     // chooseFile (pop-up sindow) [getter]
    public File chooseFile(){
        File choosedFile = null;
        
        JFileChooser fileChooser = new JFileChooser();
        fileChooser.setCurrentDirectory(new File(DATASET_ABSOLUTE_PATH));
        int response = fileChooser.showOpenDialog(null);
        if (response == JFileChooser.APPROVE_OPTION){
            choosedFile = new File(fileChooser.getSelectedFile().getAbsolutePath());                                                        
        }         
        
        return choosedFile;
    }    
    
    
    
    /* 
    public Punto[] extractPointsFromFile(File fileX) throws FileNotFoundException,NoSuchElementException{
        Scanner scannerX = new Scanner(fileX);
        String lineX = scannerX.nextLine();

        Punto pointList[] = null;
        boolean startSavingPoints = false;    
        int pointIndex = 0;
        int numPoints = 0; //DIMENSION
        while(scannerX.hasNext() && lineX!=DELIMITER__END_OF_FILE){
            if (lineX.contains(DELIMITER__NUM_POINTS)){
                String lineData[] = lineX.trim().split(SPACE_REGREX);
                numPoints = Integer.parseInt(lineData[lineData.length-1].trim());
            }
            
            if(startSavingPoints == true){
                if (pointList==null) {pointList = new Punto[numPoints];  }
                                                    

                String[] rawData = lineX.trim().split(SPACE_REGREX);
                String pointName = rawData[0].trim();
                double coordX = Double.parseDouble(rawData[rawData.length-2].trim());
                double coordY = Double.parseDouble(rawData[rawData.length-1].trim());

                Punto pointX = new Punto(pointName,coordX, coordY);
                pointList[pointIndex]= pointX;                                   
                pointIndex++;
            }
            
            if (lineX.compareTo(DELIMITER__START_POINTS) == 0) { startSavingPoints = true; }
            
            lineX = scannerX.nextLine();                     
        }
        
    
       return pointList;
    }
*/
    
    /**
     * TODO [ASK-TEACHER]: Como diferenciar un automataDeterminista de uno no determinista en el fichero a importar??? 
     * IMPORTANTE: Por ahora, solo genera 'AutomataDeterminista's .
     * IMPORTANTE [DECISION-DE-DISEÑO]: Los automatas no pueden intanciar nodos por separado; solo {@link Transaction}'s
     * 
     * @param fileX
     * @return {@link AbstractAutomata}
     * @throws FileNotFoundException
     * @throws NoSuchElementException 
     */
    public AbstractAutomata genAutomataFromFile(File fileX) throws FileNotFoundException,NoSuchElementException{
        Scanner scannerX = new Scanner(fileX);
        String lineX = scannerX.nextLine();

        AbstractAutomata automataX = new AutomataDeterminista();
        boolean startSavingTransactions = false;    
        int pointIndex = 0;
        int numPoints = 0; //DIMENSION
        String statesList[];
        
        while(scannerX.hasNext() && lineX!=DELIMITER__END_OF_FILE){
                      
            if(startSavingTransactions == true){
                
                // [TRANSACTION-FORMAT]:= [EJ] q0 '0' q1 <===> <initialState, command, finalState>
                String[] rawData = lineX.trim().split(SPACE_REGREX);
                String initialState = rawData[0];
                Character command = rawData[1].trim().split(CHAR_REGREX)[0].charAt(0);;
                String finalState = rawData[2];
                
                // Add Transactions
                automataX.addTransaction(initialState, command, finalState);
            }
            else{
                if (lineX.contains(DELIMITER__NUM_STATES)){
                    statesList = lineX.trim().split(DELIMITER__NUM_STATES)[1].split(SPACE_REGREX);                      
                }
                else if (lineX.contains(DELIMITER__INITIAL_STATES)){
                    String initialStates[] = lineX.trim().split(DELIMITER__INITIAL_STATES)[1].split(SPACE_REGREX);                      
                    for(String initialStateX:initialStates){
                        automataX.addInitialState(initialStateX);
                    }
                }        
                else if (lineX.contains(DELIMITER__FINAL_STATES)){
                    String finalStates[] = lineX.trim().split(DELIMITER__FINAL_STATES)[1].split(SPACE_REGREX);                      
                    for(String initialStateX:finalStates){
                        automataX.addFinalState(initialStateX);
                    }
                }
                else if (lineX.compareTo(DELIMITER__START_TRANSACTIONS)==0){
                    startSavingTransactions = true;
                }        
            }
            
            lineX = scannerX.nextLine();                     
        }
        
            
       return automataX;
    }
    
    

}
