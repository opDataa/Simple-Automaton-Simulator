/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package Modelo.saveLoad;

import static Modelo.saveLoad.FileLoader.DELIMITER__END_OF_FILE;
import static Modelo.saveLoad.FileLoader.DELIMITER__FINAL_STATES;
import static Modelo.saveLoad.FileLoader.DELIMITER__INITIAL_STATES;
import static Modelo.saveLoad.FileLoader.DELIMITER__NUM_STATES;
import static Modelo.saveLoad.FileLoader.DELIMITER__START_TRANSACTIONS;
import static Modelo.saveLoad.FileLoader.DELIMITER__TIPO_AUTOMATA;
import static Modelo.saveLoad.FileLoader.SPACE_REGEX;
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileWriter;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Scanner;



/**
 * Soluciona el problema de haber cambiado el formato del archivo que representa a un autómata (con respecto del que se pedía el año pasado).
 * Para ello, traduce un fichero en el formato actual al antiguo. Esto crea un archivo temporal.
 * 
 * @see TRANSLATED_FILE_NAME
 * @author Jtorr
 * @version 2.0
 */
public class FileFormatTranslator {
    
    /**
     * An optional flag that delimiters the lambda transctions
     */
    private static final String DELIMITER__START_LAMBDA_TRANSACTIONS = "TRANSICIONES LAMBDA:";
    
    /**
     * The name of the temporal file that results from the translation
     */
    private static final String TRANSLATED_FILE_NAME = "tempX";
    
    
    /**
     * Creates a new (temporal) file in the older-automaton-file-format 
     * 
     * @param fileToTranslate The file in the new format 
     * @return A new file in the old format
     * @throws FileNotFoundException
     * @throws IOException 
     */
    public static File translate(File fileToTranslate) throws FileNotFoundException, IOException{
        Scanner scannerX = new Scanner(fileToTranslate);

        boolean errFileFormat = false;
        String lineX = scannerX.nextLine();

        ArrayList<String> lineasFicheroNuevo = new ArrayList<String>();
                
        // 'TIPO: {AFD|AFND}'
        if (lineX.contains(DELIMITER__TIPO_AUTOMATA)){
            //automataType = lineX.trim().split(DELIMITER__TIPO_AUTOMATA)[1].trim();                      
            lineasFicheroNuevo.add(lineX);
            
            // [CAMPO OBLIGATORIO] 'ESTADOS: ... '
            lineX = scannerX.nextLine();   
            if (lineX.contains(DELIMITER__NUM_STATES)==false){errFileFormat=true;}
            lineasFicheroNuevo.add(lineX);
                    
            // [CAMPO OBLIGATORIO] 'INICIAL: ... '
            lineX = scannerX.nextLine();   
            if (lineX.contains(DELIMITER__INITIAL_STATES)==false){errFileFormat=true;}
            lineasFicheroNuevo.add(lineX);

            
            // [CAMPO OBLIGATORIO] 'FINALES: ... '
            lineX = scannerX.nextLine();   
            if (lineX.contains(DELIMITER__FINAL_STATES)==false){errFileFormat=true;}
            lineasFicheroNuevo.add(lineX);

            
                       
            // [CAMPO OPCIONAL] 'TRANSICIONES: \n...'
            lineX = scannerX.nextLine();   
            if (lineX.contains(DELIMITER__START_TRANSACTIONS)){
                lineasFicheroNuevo.add(lineX);

                lineX = scannerX.nextLine();   
                while(errFileFormat==false && scannerX.hasNext() && lineX!=DELIMITER__END_OF_FILE && lineX.contains(DELIMITER__START_LAMBDA_TRANSACTIONS)==false){
                    String[] rawData = lineX.trim().split(SPACE_REGEX);
                    
                    if(rawData.length<3){errFileFormat= true;}
                    
                    // q0 '1' q2 <== Se guarda tal cual 
                    else if(rawData.length==3){
                        lineasFicheroNuevo.add(lineX);
                    }
                    
                    // q0 '1' q2 q3 <== hay que hacer operaciones y guardarlo en el formato correcto: una linea por transaccion
                    /*
                        -> Formato Valido:
                        q0 '1' q2
                        q0 '1' q3
                    */
                    else if(rawData.length>3){
                        // TODO: ?? 
                        
                        final String A = rawData[0];
                        final String command = rawData[1];
                        
                        for(int i=2;i<rawData.length;i++){
                            String B = rawData[i];
                            String lineaValida = A+command+B;
                            
                            lineasFicheroNuevo.add(lineaValida);
                        }
                        
                    }
                    
                    
                    
                    lineX = scannerX.nextLine();                     
                }
                
            }
            
            
            // [CAMPO OPCIONAL] 'TRANSICIONES LAMBDA: \n...'
            if (lineX.contains(DELIMITER__START_LAMBDA_TRANSACTIONS)){
                // lineasFicheroNuevo.add(lineX); <== Esto no existe en el formato valido que lee el 'FileLoader' (!)
                
                lineX = scannerX.nextLine();   
                while(errFileFormat==false && scannerX.hasNext() && lineX!=DELIMITER__END_OF_FILE){
                    String[] rawData = lineX.trim().split(SPACE_REGEX);
                   
                    if(rawData.length<2){errFileFormat= true;}
  
             
                    // q0 q2 <== Se guarda tal cual 
                    else if(rawData.length==2){
                        lineasFicheroNuevo.add(lineX);   
                    }


                    // q0 q2 q3 <== hay que hacer operaciones y guardarlo en el formato correcto: una linea por transaccion
                    /* 
                        -> Formato Valido:
                            q0 q2
                            q0 q3                                         
                    */
                    else if(rawData.length>2){
                        final String A = rawData[0];
                        
                        for(int i=1;i<rawData.length;i++){
                            String B = rawData[i];
                            String lineaValida = A+B;
                            
                            lineasFicheroNuevo.add(lineaValida);
                        }
                    }

                    lineX = scannerX.nextLine();                     
                }
            }
            
        }
        else{errFileFormat=true;}
        
        
        
        
        File validFormatFile = null;
        
        if(errFileFormat){System.out.println("ERR -> Incorrect File Format"); }
        else{            
            // Descabezamos el nombre del archivo
            final String absolutePath = fileToTranslate.getAbsolutePath().split(fileToTranslate.getName())[0];
            
            validFormatFile = new File(absolutePath+"\\"+TRANSLATED_FILE_NAME);


            BufferedWriter out = new BufferedWriter(new FileWriter(validFormatFile));   


            for(String lineaValidaX: lineasFicheroNuevo){
                out.write(lineaValidaX);        
                out.newLine();
            }

            out.write(DELIMITER__END_OF_FILE);
            out.close();
        }
        
        
        return validFormatFile;
    }
    
    
}
