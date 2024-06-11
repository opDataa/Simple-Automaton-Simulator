/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package Modelo.saveLoad;

import Modelo.AutomataDeterminista;
import Modelo.AutomataNoDeterminista;
import Modelo.Interfaces.AbstractAutomata;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.util.NoSuchElementException;
import java.util.Scanner;
import javax.swing.JFileChooser;

/**
 * Loads an {@link Modelo.AutomataDeterminista} or an {@link Modelo.AutomataNoDeterminista}
 * 
 * @author Jtorr
 */
public class FileLoader {       
    /**
     * The name folder of the dataset (where the automata's are stored).
     */
    private final String DATASET_FOLDER_NAME = "dataSetFinal_V2"; //"dataSetFinal"; // "dataset_FINAL";
    /**
     * Absolute path of the folder that contains the {@link #DATASET_FOLDER_NAME}
     */
    private final String DATASET_ABSOLUTE_PATH = this.getClass().getProtectionDomain().getCodeSource().getLocation().toString().split("/target/classes/")[0].split("file:/")[1].replace("/", "\\").replace("%20"," ")+"\\src\\main\\java\\"+DATASET_FOLDER_NAME+"\\";
    //private final String DATASET_ABSOLUTE_PATH = this.getClass().getProtectionDomain().getCodeSource().getLocation().toString().split("/target/classes/")[0].split("file:/")[1].replace("/", "\\").replace("%20"," ")+"\\src\\main\\java\\dataset\\";

    
    /**
     * Space regexp 
     */
    public static final String SPACE_REGEX = "(?= )";

    // File flags (the file should have that flag order)
    /**
     * Flag that determines the type of the automata: 
     *  - 'AFND' {@link Modelo.AutomataNoDeterminista}
     *  - 'AFD' {@link Modelo.AutomataDeterminista}
     */
    public static final String DELIMITER__TIPO_AUTOMATA = "TIPO:"; 
    /**
     * An optiona flag to write some extra information about the automata
     */
    public static final String DELIMITER__INFO_AUTOMATA = "INFO:";
    @Deprecated
    public static final String DELIMITER__NUM_STATES = "ESTADOS:";
    /**
     * Flag that contains the {@link Modelo.Interfaces.Transaction#initialState}
     */
    public static final String DELIMITER__INITIAL_STATES = "INICIAL:";
    /**
     * Flag that contains the {@link Modelo.Interfaces.Transaction#finalStates}
     * (ONLY THE AFND CAN HAVE MORE THAN ONE FINAL_STATE)
     */
    public static final String DELIMITER__FINAL_STATES = "FINALES:";
    /**
     * Flag that contains the {@link Modelo.Interfaces.Transaction}'s of the automata.
     * 
     * [FORMAT]:
     * - Normal Transaction ==> initialNode [commandOrSymbol] finalNode
     * - Lambda Transaction ==> initialNode lambdaNode
     */
    public static final String DELIMITER__START_TRANSACTIONS = "TRANSICIONES:";
    /**
     * Flag that mark the end of the file.
     */
    public static final String DELIMITER__END_OF_FILE = "FIN";
        

    /**
     * Stores the last loaded file in order to execute the {@link #reloadAutomata() } method.
     */
    private File previouslyLoadedFile = null;
    
    
    /**
     * Open a popUp Window that let you to choose a file (witch must represent an {})
     * @return the choosed file or {@code null} if no file has choosed
     * 
     * // TODO
     * @version 2: Traduce el fichero usando el 'FileInterpreter' al formato que el 'genAutomataFromFile()' es capas de leer.
     */
    public File chooseFile() throws IOException{
        File choosedFile = null;
        
        JFileChooser fileChooser = new JFileChooser();
        fileChooser.setCurrentDirectory(new File(DATASET_ABSOLUTE_PATH));
        int response = fileChooser.showOpenDialog(null);
        if (response == JFileChooser.APPROVE_OPTION){
            choosedFile = new File(fileChooser.getSelectedFile().getAbsolutePath());                                                        
        }         
        
        // Crea un fichero temporal en el formato valido del 'FileLoader' 
        choosedFile = FileFormatTranslator.translate(choosedFile);
        
        
        return choosedFile;
    }    
    
    
    
    /**
     * Read an automata (.txt) and generactes an {@link AutomataNoDeterminista}  o an {@link AutomataNoDeterminista}
     * @param fileX The file that contains the autoamta
     * 
     * @return An {@link AutomataNoDeterminista} or an {@link AutomataNoDeterminista}
     * @throws FileNotFoundException err
     * @throws NoSuchElementException err
     * @see Controlador.ControladorAutomata#actionPerformed(java.awt.event.ActionEvent) 
     */
    public AbstractAutomata genAutomataFromFile(File fileX) throws FileNotFoundException,NoSuchElementException{
        this.previouslyLoadedFile = fileX;

        
        Scanner scannerX = new Scanner(fileX);
        String lineX = scannerX.nextLine();

        AbstractAutomata automataX = null; // = new AutomataDeterminista();
        
        boolean startSavingTransactions = false;    
        boolean startSavingTransactionsLAMBDA = false;
        int pointIndex = 0;
        int numPoints = 0; //DIMENSION
        String statesList[];
        
        
        // TIPO: (AFD/AFND)
        if (lineX.contains(DELIMITER__TIPO_AUTOMATA)){
            String automataType = lineX.trim().split(DELIMITER__TIPO_AUTOMATA)[1].trim();                      
            switch(automataType.toUpperCase()){
                case "AFD":
                    automataX = new AutomataDeterminista();  
                    break;                            
                case "AFND":
                    automataX = new AutomataNoDeterminista();
                break;
            }
 
            lineX = scannerX.nextLine();   
            
            while(scannerX.hasNext() && lineX!=DELIMITER__END_OF_FILE){

                if(startSavingTransactions == true){                     
                    // [TRANSACTION-FORMAT]:= [EJ] q0 '0' q1 <===> <initialState, command, finalState>
                    String[] rawData = lineX.trim().split(SPACE_REGEX);

                    switch(rawData.length){

                        case 3: // Normal Transactions
                            String initialState = rawData[0].trim();
                            Character command = rawData[1].trim().charAt(1);//rawData[1].trim().split(CHAR_REGREX)[0].charAt(0);;
                            String finalState = rawData[2].trim();
                            // Add Transactions
                            automataX.addTransaction(initialState, command, finalState);  
                        break;

                        case 2: // Lambda Transactions
                            String initialStateLAMDA = rawData[0].trim();
                            String finalStateLAMBDA = rawData[1].trim();
                            // Add Transactions
                            AutomataNoDeterminista a = (AutomataNoDeterminista) automataX;
                            a.addLambdaTransaction(initialStateLAMDA, finalStateLAMBDA); // TODO: este casteo es peligroso y puede dar errores.
                            break;

                        default:
                            System.out.println("ERR: invalid Transaction format: "+ lineX);
                            break;
                    }
                }
                else{
                    if (lineX.contains(DELIMITER__INFO_AUTOMATA)){
                        System.out.println("INFO: "+ lineX.trim().split(DELIMITER__INFO_AUTOMATA)[1]);
                    }
                    else if (lineX.contains(DELIMITER__INITIAL_STATES)){
                        String initialStates[] = lineX.trim().split(DELIMITER__INITIAL_STATES)[1].split(SPACE_REGEX);                      
                        for(String initialStateX:initialStates){
                            automataX.addInitialState(initialStateX.trim());
                        }
                    }        
                    else if (lineX.contains(DELIMITER__FINAL_STATES)){
                        String finalStates[] = lineX.trim().split(DELIMITER__FINAL_STATES)[1].split(SPACE_REGEX);                      
                        for(String initialStateX:finalStates){
                            automataX.addFinalState(initialStateX.trim());
                        }
                    }
                    else if (lineX.contains(DELIMITER__START_TRANSACTIONS)){
                        startSavingTransactions = true;
                    }        
                }

                lineX = scannerX.nextLine();                     
            }
        }
        else { System.out.println("ERR -> Incorrect File Format: No autoamtaType found!"); } 
            
       return automataX;
    }
    
    /**
     * Reload previously loaded automata 
     * 
     * @return An {@link AutomataNoDeterminista} or an {@link AutomataNoDeterminista}
     * @throws FileNotFoundException err
     * @see #genAutomataFromFile(java.io.File) 
     * @see #previouslyLoadedFile
     */
    public AbstractAutomata reloadAutomata() throws FileNotFoundException{
        return genAutomataFromFile(this.previouslyLoadedFile);
    }

}
