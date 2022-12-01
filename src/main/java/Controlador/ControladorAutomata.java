/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package Controlador;

import Modelo.Interfaces.AbstractAutomata;
import static Modelo.Interfaces.AbstractAutomata.DEAD_NODE__NAME;
import Modelo.Interfaces.Transaction;
import Modelo.saveLoad.FileLoader;
import java.io.File;
import java.io.FileNotFoundException;
import java.util.NoSuchElementException;
import java.util.logging.Level;
import java.util.logging.Logger;
import org.graphstream.graph.Edge;
import org.graphstream.graph.Graph;
import org.graphstream.graph.Node;
import org.graphstream.graph.implementations.SingleGraph;
import org.graphstream.ui.graphicGraph.stylesheet.StyleConstants;
import org.graphstream.ui.spriteManager.Sprite;
import org.graphstream.ui.spriteManager.SpriteManager;



/**
 *
 * @author Jtorr
 */
public class ControladorAutomata {
    
    private final String DEFAULT__STYLE_SHEET = "styleX";

    private Graph graphStream;
    private SpriteManager spriteManager;        

    private AbstractAutomata automataX;
    private FileLoader fileLoader = new FileLoader(); 
    
    public ControladorAutomata(){
        try {
            // Choose file from disk
            File fileX = this.fileLoader.chooseFile();
            
            // Generates the 'AbstractAutomata' from selected file
            this.automataX = this.fileLoader.genAutomataFromFile(fileX);  
           
            // Validates the 'Automata' input
            this.automataX.runAutomata("01");
            
            // Init and display the Graphs-Representation of the 'Automata' 
            this.initGraphStream(fileX.getName());
            this.graphStream.display();                  
        } 
        catch (FileNotFoundException ex)    { Logger.getLogger(ControladorAutomata.class.getName()).log(Level.SEVERE, null, ex); } 
        catch (NoSuchElementException ex)   { Logger.getLogger(ControladorAutomata.class.getName()).log(Level.SEVERE, null, ex); }
   
    
    
    }
    
    /**
     * .
     * @param graphStreamName 
     */
    private void initGraphStream(String graphStreamName){
        this.graphStream = new SingleGraph(graphStreamName);
        this.spriteManager = new SpriteManager(this.graphStream);
        
        // With these 2 lines: there is no need to add each node, but the edges that links them
        this.graphStream.setStrict(false);
        this.graphStream.setAutoCreate( true );
                           
        // Draw Graphs
        this.drawGraphsStructure();
           
        // Set StyleSheet
        initStyleSheet(DEFAULT__STYLE_SHEET);
        // set 'Nodes' Style
        setStyle_Nodes();
        // set 'Edeges' Style
        setStyle_Edges();        
    }
    private void drawGraphsStructure(){
        for(Transaction transactionX:this.automataX.getTransactions()){
            String initialState = transactionX.getInitialState();
            Character command = transactionX.getCommand();
            for(String finalState : transactionX.getFinalStates()){

                String edgeX_name = genEdgeId(initialState,command,finalState);  
                this.graphStream.addEdge(edgeX_name, initialState, finalState, true);
                
                // Stores the {@link Transaction#command} to display in the middle of the {@link org.graphstream.graph.Edge}.
                System.out.println("==> COMMAND: "+ command);
                this.graphStream.getEdge(edgeX_name).setAttribute("command", command);
            }  
        }
    }
    private void initStyleSheet(final String styleSheetFileName){
        final String STYLE_SHEET__ABSOLUTE_PATH = this.getClass().getProtectionDomain().getCodeSource().getLocation().toString().split("/target/classes/")[0].split("file:/")[1].replace("/", "\\")+"\\src\\main\\java\\StyleSheets";
        this.graphStream.setAttribute("ui.stylesheet", "url('"+STYLE_SHEET__ABSOLUTE_PATH+"\\"+styleSheetFileName+"')");  
    }

    private String genEdgeId(String initialState, Character commmand, String finalState){
        return initialState+commmand+finalState;
    }
    private void setStyle_Nodes(){
        
        //Initial Arrow to difference the 'initialState' from the rest.
        Sprite InitialStateArrow = spriteManager.addSprite("InitialStateArrow"); 
        InitialStateArrow.setPosition(StyleConstants.Units.PX, -43, -2, 0);
        
        final int NUM_NODES = this.graphStream.getNodeCount();
        for (int i=0;i<NUM_NODES;i++){            
            Node nodeX = this.graphStream.getNode(i);
            String nodeNameX = nodeX.toString().trim();
         
            // Displays the 'Graph' name
            nodeX.setAttribute("ui.label", nodeNameX);

            // Add Style
            nodeX.setAttribute("ui.class", "defaultNode");
            // (FINAL-State: Style)
            if(this.automataX.getFinalStates().contains(nodeNameX)){ nodeX.setAttribute("ui.class", "defaultNode, finalNode"); }
            // (INITIAL-State: Style)
            else if (this.automataX.getInitialStates().contains(nodeNameX)){ InitialStateArrow.attachToNode(nodeNameX); }
            // (DEAD-State: Style)
            else if (nodeNameX==AbstractAutomata.DEAD_NODE__NAME) {nodeX.setAttribute("ui.class", "deadNode");}       
        }    
    }
    
    private void setStyle_Edges(){
        final int NUM_EDGES = this.graphStream.getEdgeCount();
        for (int i=0;i<NUM_EDGES;i++){
            Edge edgeX = this.graphStream.getEdge(i);
            
            // Displays the 'Transaction-command' in the middle of the 'Edge'
            edgeX.setAttribute("ui.label", edgeX.getAttribute("command"));

            // Add Style
            edgeX.setAttribute("ui.class", "defaultEdge");
            if(edgeX.toString().contains(DEAD_NODE__NAME)){edgeX.setAttribute("ui.class","defaultEdge, dottedEdge");}               
        }    
    }
  
}
