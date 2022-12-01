/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package Controlador;

import Modelo.Interfaces.AbstractAutomata;
import Modelo.saveLoad.FileLoader;
import java.io.File;
import java.io.FileNotFoundException;
import java.util.NoSuchElementException;
import java.util.logging.Level;
import java.util.logging.Logger;



/**
 *
 * @author Jtorr
 */
public class ControladorAutomata {
    private AbstractAutomata automataX;
    private FileLoader fileLoader;
    
    public ControladorAutomata(){
        try {
            this.fileLoader = new FileLoader(); 
            File fileX = this.fileLoader.chooseFile();
            this.fileLoader.genAutomataFromFile(fileX);
            
        } 
        catch (FileNotFoundException ex)    { Logger.getLogger(ControladorAutomata.class.getName()).log(Level.SEVERE, null, ex); } 
        catch (NoSuchElementException ex)   { Logger.getLogger(ControladorAutomata.class.getName()).log(Level.SEVERE, null, ex); }
    }
    
    
    
    
    
}
