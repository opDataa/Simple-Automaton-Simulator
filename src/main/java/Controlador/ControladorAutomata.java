
/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package Controlador;

import Modelo.Interfaces.AbstractAutomata;
import static Modelo.Interfaces.AbstractAutomata.DEAD_NODE__NAME;
import Modelo.Interfaces.Transaction;
import Modelo.saveLoad.FileLoader;
import Vista.ControlPane;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.KeyEvent;
import java.awt.event.KeyListener;
import java.awt.event.MouseEvent;
import java.awt.event.MouseListener;
import java.awt.geom.Point2D;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.HashSet;
import java.util.NoSuchElementException;
import java.util.logging.Level;
import java.util.logging.Logger;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import javax.swing.JOptionPane;
import org.graphstream.graph.Edge;
import org.graphstream.graph.Graph;
import org.graphstream.graph.Node;
import org.graphstream.graph.implementations.MultiGraph;
import org.graphstream.ui.graphicGraph.stylesheet.StyleConstants;
import org.graphstream.ui.spriteManager.Sprite;
import org.graphstream.ui.spriteManager.SpriteManager;
import org.graphstream.ui.view.Viewer;
import org.graphstream.ui.view.camera.Camera;


/**
 * Controller class that constrols the automata generation and visualization (using the {@link org.graphstream} library)
 * 
 * @author Jtorr
 */
public class ControladorAutomata implements ActionListener {
   
    /**
     * The {@code char} that represents a 'LambdaTransaction'.
     */
    private final Character LAMBDA = 'λ';
    /**
     * Turns {@code True} if the the 'RELOAD' button has pressed.
     * 
     * Solves a bug that ocurrs when, just before a 'reload' the step btns are pressed; with that, now starts by the very beginning and not in the step number one.
     * @see #prevStep() 
     * @see #nextStep() 
     */
    private boolean reloadedAutomata = false;
    
    // VIEW
    /**
     * Stores current operation index witch is used by the 'step by step' mode (in the {@link #nextStep()} and the {@link #prevStep()} methods).
     * It is the index of the {@link #automataOperations} list.
     */
    private int stepIndex = 0;
    /**
     * Used by {@link ControladorTextInput} to marks the current {@link Transaction#command} operation.
     * @see ControladorTextInput#setStepIndex(int, boolean) 
     */
    private int pathIndex = 0; // see: colorizeCurrentCommand(); 
    /**
     * Represents every iteration that the generated {@link #automataX} makes in order to reach a final node.
     * It is controlled in the {@link #showCurrentOperation_v3(int) } method.
     * 
     * @see AbstractAutomata#popOperationsList() 
     */
    private ArrayList<String> automataOperations = null;
    /**
     * The view class that contains the control panel that allows to export and run the automata.
     */
    private ControlPane controlPane;
   
    // MODEL
    /**
     * If the given textInput is {@code VALID} or {@code INVALID}.
     * NOTE: The {@link AbstractAutomata} does not save that flag in its class. 
     * Is {@code true} if the {@link AbstractAutomata#runAutomata(java.lang.String) } result is {@code VALID}; {@code false} otherwise.
     */
    private boolean isValid = false; 
    /**
     * The automata
     */
    private AbstractAutomata automataX;
    /**
     * Imports the {@link AbstractAutomata} from a {@code .txt} file. 
     */
    private FileLoader fileLoader;
    
    // CONTROLLER (textInput/StepViewer)
    /**
     * Controls the textInput. 
     */
    private ControladorTextInput textInputControlller = null;
    
    // GraphStream
    /**
     * The name of the file that contains the style used by the {@link org.graphstream.graph.Graph}.
     */
    private final String DEFAULT__STYLE_SHEET = "styleX";
    /**
     * (GraphStream-Library) Let us to modify the view.
     */
    private Viewer graphViewer = null;
    /**
     * (GraphStream-Library) Represents the {@link AbstractAutomata} states and {@link Transaction}'s.
     */
    private Graph graphStream = null;  
    /**
     * (GraphStream-Library) Is the sprite (icons) factory.
     */
    private SpriteManager spriteManager;   

    
    // Sprites
    /**
     * Stores the last used sprite, witch is (mainly) used during the "step by step" Mode "drawings" operations.
     * NOTE: Except for the {@link #INITIAL_STATE_ARROW} there cant be more that one visible icon ({@link Sprite}) at the same time.
     * @see #attachSprite(java.lang.String, java.lang.String) 
     * @see #detachCurrentSprite() 
     */
    private Sprite spriteToDetach = null;
   
    /**
     * Dict. that contains the inited {@link Sprite}'s.
     * @see #initSprites() 
     */
    private HashMap<String,Sprite> spritesDict;
    /**
     * The {@code spriteID} for the {@link AbstractAutomata#initialStates}
     */
    private final String INITIAL_STATE_ARROW = "InitialStateArrow";
    /**
     * The list of {@link Sprite}'s to init.
     * @see #initSprites() 
     */
    private final String[] spritesIDs = {INITIAL_STATE_ARROW,AbstractAutomata.OPERATION_CODE__FINAL_NODE_VALID,AbstractAutomata.OPERATION_CODE__FINAL_NODE_INVALID,AbstractAutomata.OPERATION_CODE__COMMAND_INVALID};

    
    // ERROR-MESSAGES ----------------------------------------------------------
    private final String ERR_MSG__NO_AUTOMATA_LOADED = "No se ha importado ningún autómata.";
    private final String ERR_MSG__STEP_MODE = "Para habilitar el modo \"paso a paso\", primero debes importar un autómata.";
    private final String ERR_MSG__VIEW_SYSTEM = "Para alterar la vista, primero debes importar un autómata.";
    private final String POPUP_TITLE__STEP_MODE = "Step by Step Mode";
    private final String POPUP_TITLE__LOAD_SYSTEM = "File Loader";
    private final String POPUP_TITLE__DIRECT_MODE = "Direct Mode";
    private final String POPUP_TITLE__VIEW_SYSTEM = "View System";  
    private final String POPUP_TITLE__UNKNOWN_ERR= "Unsupported Err";

    /**
     * Class constr
     */
    public ControladorAutomata(){
        // Init fileLoader
        this.fileLoader = new FileLoader(); 
        
        // Init View
        this.controlPane = new ControlPane();
        ControlPane.setLocationToCenterLeft(this.controlPane);
        this.controlPane.setVisible(true);               // Display the pane
        
        // Init TextInput Controller
        this.textInputControlller = new ControladorTextInput(this.controlPane.jTextFieldCommandInput);
        // Add Listeners
        addListeners();
    }
    /**
     * ADDs the listeners.
     */
    private void addListeners(){
        this.controlPane.addKeyListener(new KeyListener(){

            @Override
            public void keyTyped(KeyEvent e) {
                //throw new UnsupportedOperationException("Not supported yet."); // Generated from nbfs://nbhost/SystemFileSystem/Templates/Classes/Code/GeneratedMethodBody
            }

            @Override
            public void keyPressed(KeyEvent e) {
   
                // (fromInternet) [SOURCE] https://stackoverflow.com/questions/616924/how-to-check-if-the-key-pressed-was-an-arrow-key-in-java-keylistener 
                int keyCode = e.getKeyCode();
                switch( keyCode ) { 
                    case KeyEvent.VK_LEFT:
                        // handle left
                        prevStep();
                        
                        break;
                    case KeyEvent.VK_RIGHT :
                        // handle right
                        nextStep();
                        break;
                        
                    case KeyEvent.VK_UP:
                        rotateLeft(DEFAULT_ROTATION_INCREMENT);
                        break;
                    case KeyEvent.VK_DOWN:
                        rotateRight(DEFAULT_ROTATION_INCREMENT);
                        break;
                 }
    

                

            
            }

            @Override
            public void keyReleased(KeyEvent e) {
                //throw new UnsupportedOperationException("Not supported yet."); // Generated from nbfs://nbhost/SystemFileSystem/Templates/Classes/Code/GeneratedMethodBody
            }
            
            
        });
        this.controlPane.addMouseListener(new MouseListener(){
            @Override
            public void mouseClicked(MouseEvent e) {
                //throw new UnsupportedOperationException("Not supported yet."); // Generated from nbfs://nbhost/SystemFileSystem/Templates/Classes/Code/GeneratedMethodBody
            }

            @Override
            public void mousePressed(MouseEvent e) {
                //throw new UnsupportedOperationException("Not supported yet."); // Generated from nbfs://nbhost/SystemFileSystem/Templates/Classes/Code/GeneratedMethodBody
                forceFocusOnArrowKeysListener();
            }

            @Override
            public void mouseReleased(MouseEvent e) {
                //throw new UnsupportedOperationException("Not supported yet."); // Generated from nbfs://nbhost/SystemFileSystem/Templates/Classes/Code/GeneratedMethodBody
            }

            @Override
            public void mouseEntered(MouseEvent e) {
                //throw new UnsupportedOperationException("Not supported yet."); // Generated from nbfs://nbhost/SystemFileSystem/Templates/Classes/Code/GeneratedMethodBody
            }

            @Override
            public void mouseExited(MouseEvent e) {
                //throw new UnsupportedOperationException("Not supported yet."); // Generated from nbfs://nbhost/SystemFileSystem/Templates/Classes/Code/GeneratedMethodBody
            }
        });
           
        this.controlPane.jButtonLoadAutomataFromFile.addActionListener(this);
        this.controlPane.jButtonNextStep.addActionListener(this);
        this.controlPane.jButtonPrevStep.addActionListener(this);
        this.controlPane.jButtonRunAutomata.addActionListener(this);
        this.controlPane.jButtonRefresh.addActionListener(this);
        this.controlPane.jButtonZoomOut.addActionListener(this);
        this.controlPane.jButtonStopAutoFit.addActionListener(this);
        this.controlPane.jButtonShowFinalSolution.addActionListener(this);
        this.controlPane.jButtonHelp.addActionListener(this);
        
        // EnterKey-Pressed ==> Runs the automata 
        this.controlPane.jTextFieldCommandInput.addKeyListener(new KeyListener(){
            
            @Override
            public void keyTyped(KeyEvent e) {
                //throw new UnsupportedOperationException("Not supported yet."); // Generated from nbfs://nbhost/SystemFileSystem/Templates/Classes/Code/GeneratedMethodBody
            }

            @Override
            public void keyPressed(KeyEvent e) {
                // (fromInternet) [SOURCE] https://stackoverflow.com/questions/13731710/allowing-the-enter-key-to-press-the-submit-button-as-opposed-to-only-using-mo
                if (e.getKeyCode()==KeyEvent.VK_ENTER){
                    
                    System.out.println("ENTER-PRESSED!");
                    
                    System.out.println("textInput: "+ controlPane.jTextFieldCommandInput.getText()+".");
                    String auxText = controlPane.jTextFieldCommandInput.getText().trim();
                    
                    controlPane.jTextFieldCommandInput.setText(auxText);
                    controlPane.jTextFieldCommandInput.setCaretPosition(0);  
                    
                    try{ showFinalSolution(); }
                    catch (Exception ex) { ControlPane.showPopUp(controlPane, POPUP_TITLE__DIRECT_MODE, ex.getMessage(), JOptionPane.WARNING_MESSAGE); }        

                }

            
            }

            @Override
            public void keyReleased(KeyEvent e) {
                //throw new UnsupportedOperationException("Not supported yet."); // Generated from nbfs://nbhost/SystemFileSystem/Templates/Classes/Code/GeneratedMethodBody
            }
            
            
        });
    }
    
        
   
  
    /**
     * Force to focus on the {@link #controlPane} in order to skipSteps using the arrowsKeys.
     * It is used after any pressing the {@link #controlPane}
     * and after enabling the HTML mode in the {@link ControladorTextInput} (causing the 'textInput' to lost its focus).
     */
    private void forceFocusOnArrowKeysListener(){
        this.controlPane.requestFocus();
        //System.out.println("FOCUSS");
                
    }   
    @Override
    public void actionPerformed(ActionEvent e) {
           
        switch(e.getActionCommand()){
            case "help":
                this.controlPane.jDialogHelp.setVisible(true);
                
                break;
            case "ShowFinalSolution":            
                try{ showFinalSolution(); }
                catch (Exception ex) { ControlPane.showPopUp(controlPane, POPUP_TITLE__DIRECT_MODE, ex.getMessage(), JOptionPane.WARNING_MESSAGE); }        
                break;
            case "reload":
            case "importAutomata":        
                this.rotationCounter = 0;               

                this.reloadedAutomata = (e.getActionCommand().equals("reload"))? true:false;
                
                try{                    
                    if (reloadedAutomata==true){
                        // Reload the previously loaded automata (for refresh purpouses)
                        this.automataX = this.fileLoader.reloadAutomata();
                    }
                    else{
                        // Choose file from disk
                        File fileX = this.fileLoader.chooseFile();

                        // Generates the 'AbstractAutomata' from selected file
                        this.automataX = this.fileLoader.genAutomataFromFile(fileX);  
                    }
                    
                    
                    // Init and display the Graphs-Representation of the 'Automata' 
                    this.initGraphStream("Test1");
                    
                    // Sets the textInput as an input
                    this.textInputControlller.disableHTML();
                    // Resets the index used to colorize the textInput
                    this.pathIndex = 0;
                    
                  }  
                catch (FileNotFoundException | NoSuchElementException ex){ ControlPane.showPopUp(this.controlPane, POPUP_TITLE__UNKNOWN_ERR, ex.getMessage(), JOptionPane.ERROR_MESSAGE); } 
                catch (NullPointerException ex){ ControlPane.showPopUp(this.controlPane, (this.reloadedAutomata)? POPUP_TITLE__VIEW_SYSTEM:POPUP_TITLE__LOAD_SYSTEM, (this.reloadedAutomata)? ERR_MSG__VIEW_SYSTEM:ERR_MSG__NO_AUTOMATA_LOADED, JOptionPane.WARNING_MESSAGE); } catch (IOException ex) {
                Logger.getLogger(ControladorAutomata.class.getName()).log(Level.SEVERE, null, ex);
            }

            break;     
     
     
            case "runAutomata":
                try { this.runAutomata(); }
                catch (Exception ex) { ControlPane.showPopUp(this.controlPane, POPUP_TITLE__STEP_MODE, ex.getMessage(), JOptionPane.WARNING_MESSAGE); }
                break;                
            case "forceZoomOut":               
                try{  this.toggleZoom(); }
                catch(NullPointerException ex){ ControlPane.showPopUp(controlPane, POPUP_TITLE__VIEW_SYSTEM, ERR_MSG__VIEW_SYSTEM, JOptionPane.WARNING_MESSAGE); }         
                break;
            case "forceStopAutoFit":
                try{  this.forceStopFit(); }
                catch(NullPointerException ex){ ControlPane.showPopUp(controlPane, POPUP_TITLE__VIEW_SYSTEM, ERR_MSG__VIEW_SYSTEM, JOptionPane.WARNING_MESSAGE); }         
                break;
            case "prevStep":
                this.prevStep();
                break;                     
            case "nextStep":
                this.nextStep();
                break;
        }
        
    }
    
    
    
    // GRAPHICS-GENERATION------------------------------------------------------
    private void initGraphStream(String graphStreamName){
        System.setProperty("org.graphstream.ui", "swing"); // For Swing 

        
        if (this.graphStream!=null){
            this.graphStream.clear();
            this.graphViewer.close();    
        }
        
        this.graphStream = new MultiGraph(graphStreamName);
        this.spriteManager = new SpriteManager(this.graphStream);

        // With these 2 lines: there is no need to add each node, but the edges that links them
        this.graphStream.setStrict(false);
        this.graphStream.setAutoCreate( true );
                           
        // Draw Graphs
        this.drawGraphsStructure();
        graphStream.setAttribute("ui.antialias");

        
        // Init & Save Sprites
        this.initSprites();
        
        // Set StyleSheet
        initStyleSheet(DEFAULT__STYLE_SHEET);
        // set 'Nodes' Style
        setStyle_Nodes();
        // set 'Edeges' Style
        setStyle_Edges();        

        // Displays the 'GraphStream-Pane'
        this.graphViewer = this.graphStream.display();          
    }
    /**
     * DRAWs the {@link AbstractAutomata} graph's representation.
     */
    private void drawGraphsStructure(){
        for(Transaction transactionX:this.automataX.getTransactions()){
            String initialState = transactionX.getInitialState();
            Character command = transactionX.getCommand();
            for(String finalState : transactionX.getFinalStates()){

                String edgeX_name = genEdgeId(initialState,(command==null)? LAMBDA:command,finalState);  
                Edge edgeX = this.graphStream.addEdge(edgeX_name, initialState, finalState, true);
                
               
                edgeX.setAttribute("command", ((command==null)? "λ":command)) ;
                if (command==null){
                    edgeX.setAttribute("ui.class", "lambdaEdge");
                }
                else if(initialState.equals(finalState)){
                    edgeX.setAttribute("ui.class", "selfloop");
                }
            }  
        }
        
        

    }  
    /**
     * INITs the sytleSheet file that will be used by the 'GraphStream' library.
     * NOTE: It must be located in the 'StyleSheets' folder.
     * @param styleSheetFileName The fileName of the 
     */
    private void initStyleSheet(final String styleSheetFileName){
        //final String STYLE_SHEET__ABSOLUTE_PATH = this.getClass().getProtectionDomain().getCodeSource().getLocation().toString().split("/target/classes/")[0].split("file:/")[1].replace("/", "\\")+"\\src\\main\\java\\StyleSheets";
        final String STYLE_SHEET__ABSOLUTE_PATH = this.getClass().getProtectionDomain().getCodeSource().getLocation().toString().split("/target/classes/")[0].split("file:/")[1].replace("/", "\\").replace("%20"," ")+"\\src\\main\\java\\StyleSheets";
        this.graphStream.setAttribute("ui.stylesheet", "url('"+STYLE_SHEET__ABSOLUTE_PATH+"\\"+styleSheetFileName+"')");  
    }
    /**
     * INITs the {@link Node}'s style
     */
    private void setStyle_Nodes(){
        
        // Adds an ArrowSprite (that points to a 'Node') to difference the 'initialState' from the rest.
        //Sprite InitialStateArrow  = this.spritesDict.get(this.INITIAL_STATE_ARROW);       
        
        final int NUM_NODES = this.graphStream.getNodeCount();
        for (int i=0;i<NUM_NODES;i++){            
            Node nodeX = this.graphStream.getNode(i);
            String nodeNameX = nodeX.toString().trim();
         
            // Displays the 'Graph' name
            nodeX.setAttribute("ui.label", nodeNameX);

            // Add Style
            // (FINAL-State: Style)
            if(this.automataX.getFinalStates().contains(nodeNameX)){ nodeX.setAttribute("ui.class", "finalNode"); }
            // (INITIAL-State: Style)
            if (this.automataX.getInitialStates().contains(nodeNameX)){       
                //Sprite initialStateArrow = this.spritesDict.get(this.INITIAL_STATE_ARROW);
                //initialStateArrow.attachToNode(nodeNameX);
                this.attachSprite(nodeNameX, this.INITIAL_STATE_ARROW);
                
            }
            // (DEAD-State: Style)
            if (nodeNameX.equals(AbstractAutomata.DEAD_NODE__NAME)) {
                nodeX.setAttribute("ui.class", "deadNode");
                
            }       
        }    
    }   
    /**
     * INITs the {@link Edge}'s style
     */
    private void setStyle_Edges(){
        final int NUM_EDGES = this.graphStream.getEdgeCount();
        for (int i=0;i<NUM_EDGES;i++){
            Edge edgeX = this.graphStream.getEdge(i);
            
            // Displays the 'Transaction-command' in the middle of the 'Edge'
            edgeX.setAttribute("ui.label", edgeX.getAttribute("command"));

            // Add Style if is 'selfLoop' in a 'DeadNode':    
             if(edgeX.getId().contains(DEAD_NODE__NAME)){
               
                // Verify if trully is a 'selfLoop' inside a 'DeadNode': 
                // (fromInternet) [SOURCE] https://www.w3schools.com/java/java_regex.asp
                String regex = "^("+AbstractAutomata.DEAD_NODE__NAME+").*("+AbstractAutomata.DEAD_NODE__NAME+")$";
                final String TO_COMPARE = edgeX.getId();
                Pattern pattern = Pattern.compile(regex, Pattern.LITERAL);
                Matcher matcher = pattern.matcher(TO_COMPARE);
                boolean matchFound = matcher.find();
                
                // Adds style:
                if (matchFound){ edgeX.setAttribute("ui.class","dottedEdge"); }
             }               
        }    
    }
    /**
     * INITS the {@link Sprite}'s  that will be used during the simulation and witch {@code id}'s are stored in {@link #spritesIDs}
     * Stores them in a dict. with its {@code spriteID} as the {@code key}.
     * NOTE: Have more that one {@code initialState} may cause trouble since this method is not defined in case you want to init more than one {@link #INITIAL_STATE_ARROW} {@link Sprite}.
     * @see #spritesDict
     * @see #spritesIDs
     */
    private void initSprites(){           
        final int SPRITE_OFFSET_X__TO_RIGHT = +43;  // +43 := right-side
        final int SPRITE_OFFSET_X__TO_LEFT = -43;   // -43 := left-side
        final int SPRITE_OFFSET_Y__CENTERED = -2;   // -2  := Y-Centered
        this.spritesDict = new HashMap<String,Sprite>();
            
        for(String spriteID : this.spritesIDs){
            int offsetX = SPRITE_OFFSET_X__TO_RIGHT;
            if (spriteID.equals(this.INITIAL_STATE_ARROW)){
                offsetX = SPRITE_OFFSET_X__TO_LEFT;
            }
            
            Sprite spriteX = this.spriteManager.addSprite(spriteID); 
            spriteX.setAttribute("ui.class", spriteID);
            spriteX.setPosition(StyleConstants.Units.PX, offsetX, SPRITE_OFFSET_Y__CENTERED, 0);
            if(!spriteID.equals(this.INITIAL_STATE_ARROW)){
                spriteX.setAttribute( "ui.hide" );
            }

            this.spritesDict.put(spriteID, spriteX);
        }
    }
    
    
    /**
     * GENERATEs the {@code id} of an {@link Edge}.
     * 
     * @param initialState A {@link Transaction#initialState} 
     * @param commmand A {@link Transaction#command}
     * @param finalState Any state from any {@link Transaction#finalStates} 
     * @return An {@link Edge} {@code id}.
     */
    private String genEdgeId(String initialState, Character commmand, String finalState){
        return initialState+commmand+finalState;
    }

    
    

    
    // ROTATION-SYSTEM ---------------------------------------------------------  
    /**
     * The {@code theta} increment on every camera rotation.
     * NOTE: It must to be 36. It means that with 10 rotations in the same direction makes the 360º.
     * And is '36' because for each rotation the {@link Sprite}'s image will change by a rotated version of the original,
     * in order to mantain the ilusion that the image do not rotate but keep static on each rotation.
     * 
     * @see #rotateRight(double) 
     * @see #rotateLeft(double) 
     */
    private final double DEFAULT_ROTATION_INCREMENT = 36; //Must be '36' 
    /**
     * Used to determine the {@code rotationFactor}.
     * It will be incremented or decremented depending on the direction of rotation. 
     * 
     * @see #rotateRight(double) 
     * @see #rotateLeft(double) 
     * @see #getRotationFactor(int) 
     */
    private int rotationCounter =0; // 
    /**
     * Rotates the camera to the right.
     * 
     * @param theta The rotation degrees
     * @see #rotateLeft(double) 
     */
    private void rotateRight(double theta){
        try{
            Camera camera =  this.graphViewer.getDefaultView().getCamera();   
            this.graphViewer.getDefaultView().getCamera().setViewRotation(camera.getViewRotation() +theta);
            
            final int LIMIT = (360/(int)DEFAULT_ROTATION_INCREMENT)-1; // := 10-1 = 9
            if(theta>0){ 
                if(this.rotationCounter==0){this.rotationCounter=LIMIT;}
                else{ this.rotationCounter--; }
            }
            else{
                if(this.rotationCounter==LIMIT){this.rotationCounter=0;}
                else{ this.rotationCounter++; }   
            }
            this.updateSpriteIcon((this.isValid)? AbstractAutomata.OPERATION_CODE__FINAL_NODE_VALID:AbstractAutomata.OPERATION_CODE__FINAL_NODE_INVALID);
            this.updateSpriteIcon(this.INITIAL_STATE_ARROW);
        }
        catch(NullPointerException ex){ ControlPane.showPopUp(controlPane, POPUP_TITLE__VIEW_SYSTEM, ERR_MSG__VIEW_SYSTEM, JOptionPane.WARNING_MESSAGE); }        

        
    }
    /**
     * Rotates the camera to the left.
     * 
     * @param theta The rotation degrees 
     * @see #rotateRight(double) 
     */
    private void rotateLeft(double theta){
        this.rotateRight(-theta);
    }
    /**
     * GETs the {@link Sprite}'s "cssClassName" acording to the current {@code rotationFactor}.
     * 
     * @param spriteID The {@code id} of a {@link Sprite} (contained in {@link #spritesIDs})
     * @return The cssClassName of the {@link Sprite} (declared in the file 'styleX' from the 'StyleSheets' folder)
     * @see #getRotationFactor(int) 
     */
    private String getRotatedSpriteStyle(String spriteID){
      
        String iconStyleName = "";     

        if(spriteID.equals(AbstractAutomata.OPERATION_CODE__COMMAND_INVALID)){ iconStyleName  = "invalidCommand"; }
        else{
            String imagePrefixName="";
            switch(spriteID){
                case INITIAL_STATE_ARROW:
                    imagePrefixName = "arrow";
                    break;
                case AbstractAutomata.OPERATION_CODE__FINAL_NODE_VALID:
                    imagePrefixName = "valid";
                    break;
                case AbstractAutomata.OPERATION_CODE__FINAL_NODE_INVALID:
                    imagePrefixName = "invalid";
                    break;
            }

            if (imagePrefixName.isEmpty()==false){
                int ROTATION_FACTOR = getRotationFactor(0);
                //System.out.println("ROTATION_FACTOR: "+ROTATION_FACTOR);

                iconStyleName = imagePrefixName+"_"+ROTATION_FACTOR; 
            }
        }
       // System.out.println("imagePrefixName: "+imagePrefixName);
        return iconStyleName;
    }
    /**
     * GETs the position of any sprite depending on the {@code rotationFactor}.
     * (NOTE: This is handmade)
     * 
     * @param rotationFactor Determine the position of the sprite. 0 := 180º, with increments of 36º.  range: [0-9]
     * @return A {@link Point2D.Double} as the sprite coordinates. 
     * @see #getRotationFactor(int) 
    */
    private Point2D.Double getRotatedSpritePosition(int rotationFactor){
        double x = 0;
        double y = 0;
        //System.out.println("rotationFactor: "+rotationFactor);
        switch(rotationFactor){
            case 0:
                x = -33;
                y = 23;
                break;
            case 1:   
                x = -16;
                y = 38;
                break;
            case 2:
                x = 7;
                y = 40;   
                break;
            case 3:              
                x = 28;
                y = 29;
                break;
            case 4:
                x = 40;
                y = 6;
                break;
            case 5:
                x = +33;
                y = -23;
                break;
            case 6: 
                x = +16;
                y = -38;
                break;
            case 7:
                x = -7;
                y = -40; 
                break;
            case 8:
                x = -28;
                y = -29;                   
                break;
            case 9:
                x = -43;
                y = -2;
                break;                       
        }
        
        return new Point2D.Double(x,y);
    }
    /**
     * GETs the rotation factor witch is used to mantain the {@link Sprite}'s orientation and for modify its position around a {@link Node}.
     * 
     * @param offset Increment by that ammount the return result
     * @return The rotation factor
     * @see #getRotatedSpritePosition(int) 
     * @see #getRotatedSpriteStyle(java.lang.String) 
     */
    private int getRotationFactor(int offset){
        int ROTATION_FACTOR = this.rotationCounter + offset;
        int auxLimit = 0;
        while((ROTATION_FACTOR<0 || ROTATION_FACTOR>9) && auxLimit<9999){
            if(ROTATION_FACTOR<0){ROTATION_FACTOR+=10;}
            else if(ROTATION_FACTOR>9){ROTATION_FACTOR-=10;}
            auxLimit++;
        }
        return ROTATION_FACTOR;        
    }
    /**
     * UPDATEs the {@link Sprite} image according to the current {@code rotationFactor} and its position arround the {@link Node} 
     * (using the {@code offset} of the function: {@link #getRotationFactor(int)})
     * 
     * @param spriteID The {@code id} of a {@link Sprite} (contained in {@link #spritesIDs})
     * @see #rotateRight(double) 
     * @see #rotateLeft(double) 
     * @see #getRotationFactor(int) 
     */
    private void updateSpriteIcon(String spriteID){
        Sprite spriteToUpdate = this.spritesDict.get(spriteID);
        
        if(spriteID.equals(AbstractAutomata.OPERATION_CODE__COMMAND_INVALID)){
            spriteToUpdate.setAttribute("ui.class","invalidCommand");
        }
        else{ // For rotated sprites
            
            int offSet = 0;
            switch(spriteID){
                case INITIAL_STATE_ARROW:
                    offSet = -1;
                    break;
                case AbstractAutomata.OPERATION_CODE__FINAL_NODE_VALID:
                    offSet = -2;
                    break;
                case AbstractAutomata.OPERATION_CODE__FINAL_NODE_INVALID:
                    offSet = 0;
                    break;
            }
            
            final int ROTATION_FACTOR = this.getRotationFactor(offSet);


            Point2D.Double spritePosition = getRotatedSpritePosition(ROTATION_FACTOR);
            spriteToUpdate.setPosition(spritePosition.x, spritePosition.y, 0);              
            
            spriteToUpdate.setAttribute("ui.class",this.getRotatedSpriteStyle(spriteID) );

        }

    }

    
    
    // OTHER-VIEW-METHODS ------------------------------------------------------
    /**
     * Zooms out in order to see the Nodes correctly.
     * NOTE: Is usually needed to fit the view manually using the arrow-keys, but with this method the whole {@link AbstractAutomata}'s Graph representation will be visible.
     */
    private void toggleZoom(){
        this.graphViewer.getDefaultView().getCamera().setAutoFitView(true);
        //this.graphViewer.computeGraphMetrics();
        this.graphViewer.disableAutoLayout();               


        Camera cameraX = this.graphViewer.getDefaultView().getCamera();
        cameraX.setViewPercent(2f);        
    }
    /**
     * Freeze the {@link Node}'s fitting movement that happens during the {@link #graphStream} init. 
     */
    private void forceStopFit(){
        this.graphViewer.disableAutoLayout();
    }
    
    
   /**
    * Enable the direct mode
    */
    private boolean directMode = false;

    
    // STEP-BY-STEP-MODE--------------------------------------------------------
    /**
     * STARTs the "Step by Step" MODE.
     * 
     * @see #nextStep() 
     * @see #prevStep() 
     * @throws Exception {@link #ERR_MSG__NO_AUTOMATA_LOADED} 
     */
    private void runAutomata() throws Exception{
        
        if (this.automataX == null){ 
            //System.out.println("Error: To run an <<Automata>>, first import it!");
            throw new Exception(ERR_MSG__NO_AUTOMATA_LOADED);

        }
        else {
            String stringInput =this.textInputControlller.getText();                  
            
            this.forceFocusOnArrowKeysListener();
            
            if(false && stringInput.length()==0){ System.out.println("Error: You need to write a command to validate it!"); } 
            else{

                // Check if the 'stringInput' is valid or not.
                this.isValid = this.automataX.runAutomata(stringInput);
                // Saves every operation to show it step by step
                this.automataOperations = this.automataX.popOperationsList();

                // Reset the stepIndex
                this.stepIndex = 0;
                // Reset any 'animation' (the dynamic Edges/Nodes style during 'step by step' execution)
                this.disselectEverything();
                // Select the initial node 
                this.showCurrentOperation_v3(0);

                 
                this.textInputControlller.setStepIndex(0, false);            
            }
        }

    }
    /**
     * Skip to the NEXT step.
     * (NOTE: if not during the "Step by Step mode" starts at the beginning)
     */
    private void nextStep(){
        if (this.automataOperations==null) {
            //System.out.println("ERROR: To show 'step by step' the 'validation' process, first 'RUN' the Automata.");}
            ControlPane.showPopUp(this.controlPane, "Step by Step Mode", ERR_MSG__STEP_MODE, JOptionPane.WARNING_MESSAGE); 
        }            
        else{
            if(this.reloadedAutomata ||this.directMode){
                this.disselectEverything();
                this.showCurrentOperation_v3(0);
                this.directMode = false;
                this.reloadedAutomata = false;
            }
            else if(this.stepIndex+1>=this.automataOperations.size()){ System.out.println("ERROR: Your cant go more further because you are already at the very end!"); }
            else{ showCurrentOperation_v3(+1); }
        }
    }
    /**
     * Skip to the PREVious step.
     * (NOTE: if not during the "Step by Step mode" starts at the beginning)
     */
    private void prevStep(){
        if (this.automataOperations==null) {
            //System.out.println("ERROR: To show 'step by step' the 'validation' process, first 'RUN' the Automata.");
            ControlPane.showPopUp(this.controlPane, "Step by Step Mode", ERR_MSG__STEP_MODE, JOptionPane.WARNING_MESSAGE); 
        }
        else{
            if(this.reloadedAutomata ||this.directMode){
                this.disselectEverything();
                this.showCurrentOperation_v3(0);
                this.directMode = false;
                reloadedAutomata = false;
            }
            else if(this.stepIndex<=0){ System.out.println("ERROR: Your cant go back more because you are already at the very begining!"); }
            else{ showCurrentOperation_v3(-1); }
        }
                
    }
   
    
 
    
    // DYNAMIC-SELECTION--------------------------------------------------------
    private final String SELECTED__EDGE_TEXT_COLORC = "green";
    private final int SELECTED__EDGE_TEXT_SIZE = 40;
    private final int EDGE_TEXT_SIZE= 30;
    private final String EDGE_TEXT_COLOR = "black";
    
    /**
     * Selects a {@link Node} changing its style.
     * @param nodeX The {@link Node} to select
     */
    private void selectNode(Node nodeX){
        //System.out.println("SELECTED: "+nodeX.toString());
        nodeX.setAttribute("ui.color", 0.5f);
        //nodeX.setAttribute("ui.size", 35);
        
        // Avoid style overriding
        if(this.automataX.isFinalState(nodeX.getId())){ nodeX.setAttribute("ui.class", "finalNode, selectedNode"); }
        else{ nodeX.setAttribute("ui.class", "selectedNode"); }
            
        // If that node has a selfLoop, the redimension will affect the Edge but not its text, 
        // so we need to modify its offset to mantain some consistancy on that change of size.
        //Edge selfLoopEdge = nodeX.getEdgeToward(nodeX);
        //if(selfLoopEdge!=null){ selfLoopEdge.setAttribute("ui.class", "selfloopSelected"); }
        
        
    }
    /**
     * Selects a {@link Node} changing its style (by its {@code name})
     * @param nodeName The {@code name} of the {@link Node} to select.
     */
    private void selectNode(String nodeName){
       this.selectNode(this.graphStream.getNode(nodeName));
    }
    /**
     * Selects an {@link Edge} changing its style.
     * @param edgeX The {@link Edge} to select
     */
    private void selectEdge(Edge edgeX){
        
        edgeX.setAttribute("ui.color", 0.9f);
        edgeX.setAttribute("ui.size", 2);
        
        String textOffsets = "";
        if(isSelfLoop(edgeX)){
            edgeX.setAttribute("ui.class", "selfloopSelected");
        }
        edgeX.setAttribute("ui.style", "text-color: "+SELECTED__EDGE_TEXT_COLORC+"; text-size:"+SELECTED__EDGE_TEXT_SIZE+"px;");   
    }  
    /**
     * Select every stored {@link Edge} on the {@link #lambdaRoute}.
     */
    private void selectLambdaEdge(){
        for (Edge lambdaEdgeX : this.lambdaRoute){
            this.selectEdge(lambdaEdgeX);
            lambdaEdgeX.setAttribute("ui.class", "lambdaEdgeSelected");  
        }     
    }
    /**
     * Disselect a {@link Node} restoring its default style.
     * @param nodeX The {@link Node} to disselect
     */
    private void disselectNode(Node nodeX){
        //System.out.println("DIS-SELECTED: "+nodeX.toString());

        nodeX.setAttribute("ui.color", 0.0f);
        // nodeX.setAttribute("ui.size", 30);
        
        
        nodeX.removeAttribute("ui.class");
        
        // Avoid style overriding
        if(this.automataX.isFinalState(nodeX.getId())){ nodeX.setAttribute("ui.class", "finalNode"); }

        
        // If that node has a selfLoop, the redimension will affect the Edge but not its text, 
        // so we need to modify its offset to mantain some consistancy on that change of size.
        //Edge selfLoopEdge = nodeX.getEdgeToward(nodeX);
        //if(selfLoopEdge!=null){ selfLoopEdge.setAttribute("ui.class", "selfloop"); }
    }
    /**
     * Disselect an {@link Edge} restoring its default style.
     * @param edgeX The {@link Edge} to disselect
     */
    private void disselectEdge(Edge edgeX){
        if(isSelfLoop(edgeX)){
            edgeX.setAttribute("ui.class", "selfloop");
        }
        edgeX.setAttribute("ui.style", "text-color: "+EDGE_TEXT_COLOR+"; text-size:"+EDGE_TEXT_SIZE+"px;");   
        edgeX.setAttribute("ui.color", 0.0f);
        edgeX.setAttribute("ui.size", 1);
        
    }  
    /**
     * Disselect a lambda-{@link Edge} restoring its default style.
     * @param lambdaEdgeX The {@link Edge} to disselect
     */
    private void disselectLambdaEdge(Edge lambdaEdgeX){
        lambdaEdgeX.setAttribute("ui.class", "lambdaEdge");  
    }
    /**
     * Disselect every stored {@link Edge} on the {@link #lambdaRoute}.
     * @see #disselectLambdaEdge(org.graphstream.graph.Edge) 
     */
    private void disselectLambdaEdge(){
        for (Edge lambdaEdgeX : this.lambdaRoute){
            //this.disselectEdge(lambdaEdgeX);
            disselectLambdaEdge(lambdaEdgeX);
            //lambdaEdgeX.setAttribute("ui.class", "lambdaEdge");  
            //System.out.println("disselectLambdaEdge");
        }     
        
        this.lambdaRoute.clear();
    }
    /**
    * Disselect every {@link Edge}, {@link Node} and {@link Sprite} restoring their default style.
    */
    private void disselectEverything(){
        
        int NUM_EDGES = this.graphStream.getEdgeCount();
        for (int i=0;i<NUM_EDGES;i++){
            Edge edgeX = this.graphStream.getEdge(i);
            if (edgeX.getId().contains(this.LAMBDA+"")){
                this.disselectLambdaEdge(edgeX);
            }
            this.disselectEdge(edgeX);
        }
        int NUM_NODES = this.graphStream.getNodeCount();
        for (int i=0;i<NUM_NODES;i++){
            Node nodeX = this.graphStream.getNode(i);
            this.disselectNode(nodeX);
        } 
        
        
        // [ESTO DEBERIA COMENTARSE, PORQUE 'detachCurrentSprite()' ES MAS QUE SUFICIENTE ] ASFD !!!
        for(Sprite spriteX : this.spritesDict.values()){
            if(spriteX.getId().equals(this.INITIAL_STATE_ARROW)==false){
                spriteX.detach();
                spriteX.setAttribute("ui.hide");
                
            }
        }  
        
        if(this.spriteToDetach!=null){
            this.detachCurrentSprite();
        }
    }

    // UTILs
    /**
     * CHEKs if the given {@code operationX} represents a {@link Node}.
     * @param operationX String that may represents part of the route (a jump) that the {@code AbstractAutomata} has follow.
     * @return {@code true} if is an existing {@code Node}; {@code false} otherwise.
     */
    private boolean isNodeOperation(String operationX){
        return (!isEndingOperation(operationX) && !isEdgeOperation(operationX));
    }
    /**
     * CHEKs if the given {@code operationX} represents an {@link endingOperation}.
     * The possible {@code endingOperation}'s are: {@link AbstractAutomata#OPERATION_CODE__COMMAND_INVALID}, {@link AbstractAutomata#OPERATION_CODE__COMMAND_VALID} and {@link AbstractAutomata#OPERATION_CODE__COMMAND_INVALID}.
     * 
     * @param operationX String that may represents part of the route (a jump) that the {@code AbstractAutomata} has follow.
     * @return {@code true} if is a valid {@code endingOperation}; {@code false} otherwise.

    */
    private boolean isEndingOperation(String operationX){
        return operationX.contains("<");
    }
    /**
     * CHEKs if the given {@code operationX} represents an {@link Edge}.
     * @param operationX String that may represents part of the route (a jump) that the {@code AbstractAutomata} has follow.
     * @return {@code true} if represents an existing {@code Edge}; {@code false} otherwise.
     */
    private boolean isEdgeOperation(String operationX){
        return operationX.contains("]");
    }
   
    
    /**
     * GETs every {@link Edge} that starts by  {@code startingState} (a {@link Node}'s name) and contains the given {@code command} ( the {@link Transaction#command} ).
     * 
     * @param startingState A {@link Node}'s name
     * @param command A {@link Transaction#command}
     * @return A list of {@link Edge}'s that meet the specifications.
     */
    private HashSet<Edge> getEdges(String startingState, Character command){
        HashSet<Edge> edgesList = new HashSet<Edge>();
        final int NUM_EDGES = this.graphStream.getEdgeCount();
        for (int i=0;i<NUM_EDGES;i++){
            Edge edgeX = this.graphStream.getEdge(i);
            String initialStateX = edgeX.getSourceNode().toString();
            Character commandX = edgeX.getAttribute("command").toString().charAt(0);
            
            if (startingState.equals(initialStateX) && command.equals(commandX)){
                edgesList.add(edgeX);
            }
        }
        
        return edgesList;
    }
    /**
     * CHECKs if an {@link Edge} is a {@code selfLoop} or not.
     * @param edgeX An {@link Edge} (that represents a {@link Transaction})
     * @return {@code true} if is a {@code selfLoop}; {@code false} otherwise.
     */
    private boolean isSelfLoop(Edge edgeX){ return edgeX.getNode0().toString().equals(edgeX.getNode1().toString()); } 
    

    
    
    
    /**
     * Temporaly stores the next lambdaEdges to select or to disselect.
     * @see #selectLambdaEdge() 
     * @see #disselectLambdaEdge() 
     */
    private HashSet<Edge> lambdaRoute = new HashSet<Edge>();
    /**
     * Auxiliar Variable, used to increment the {@link #stepIndex} that controls the {@link ControladorTextInput#textPaneX}.
     * NOTE: To increment the {@code stepIndex} there must be at least a "two in a row" skipStep of the same type: {@link #nextStep()} or {@link #prevStep() }
     *
     * @see #checkToIncrementPathIndex(int, java.lang.String, java.lang.String) 
     * @see ControladorTextInput#setStepIndex(int, boolean) 
     */
    private int auxStepIndex = 0;
    /**
     * Auxiliar backup used while going backward ({@link #prevStep() }).
     * NOTE: Specifically, it is used when the {@link Modelo.AutomataNoDeterminista} make simultaneous jumps from the same node.
     * Used only for the {@link ControladorTextInput}'s highlighter system.
     * 
     * [FORMAT] {@code <NodeName, pathIndexInThatMoment>}
     */
    private HashMap<String, Integer> pathIndexBackUp = new HashMap<String, Integer>();

    
    /**
     * Clears (disselect) the previous "step by step" selection.
     * @param increment Determinines the {@code stepDirection}. If {@code >0}: fordwards; backward otherwise (It will be {@code =0} at the start of the "step by step" mode.
     * @param prevOperationX The previous operation. Represents part of the route (a jump) that the {@code AbstractAutomata} has follow.
     * @param currentOperationX The curent operation. Represents part of the route (a jump) that the {@code AbstractAutomata} has follow.
     * @see #showCurrentOperation_v3(int) 
     */
    private void cleanPreviousOperations(int increment, String prevOperationX, String currentOperationX){
        if(this.lambdaRoute.size()>0){
                  this.disselectLambdaEdge();
        }




        // FORDWARDS (+1)
        if(increment!=0){//(increment>0){

            // Node
            if (this.isNodeOperation(prevOperationX)){
                // A <CX> A B*  (A --> lamnda --> B)
                // A [1]* B
                // A <CX>* B
                if (this.isEndingOperation(currentOperationX)==false){
                    Node prevNodeX = this.graphStream.getNode(prevOperationX);
                    this.disselectNode(prevNodeX); 


                }
            }
            // Edge
            else if (this.isEdgeOperation(prevOperationX)){
                 // A [1] B*
                String prevNode_name = this.automataOperations.get(this.stepIndex-1);
                String nextNode_name = this.automataOperations.get(this.stepIndex+1);
                String edgeX_id = this.genEdgeId(prevNode_name, prevOperationX.charAt(1), nextNode_name);

                Edge edgeX = this.graphStream.getEdge(edgeX_id);

                this.disselectEdge(edgeX);
            }
            // Special-Operations (IF Valid: ends; IF Invalid: rollback)
            else if (this.isEndingOperation(prevOperationX) && this.spriteToDetach!=null){    

                
                
    
                // A <CX> B*
                //  A [0] B <CX> <BX>*
                this.detachCurrentSprite();
                
           
                String prevNode_name = this.automataOperations.get(this.stepIndex-1); 
                if(this.isEndingOperation(prevNode_name)){}     // A [0] B <BX> <BX> A*  ; [EJ] ejemplo3_2 (input: '00')

                else{            
                    Node prevNodeX = this.graphStream.getNode(prevNode_name);
                    this.disselectNode(prevNodeX);
                }


                // A <CX> <BX>  (???)

            }

        }

        
    }
    /**
     * Draws (select) the current "step by step" operations.
     * FORDWARDS (+1) /  BACKWARDS (-1)
     * @param increment Determinines the {@code stepDirection}. If {@code >0}: fordwards; backward otherwise (It will be {@code =0} at the start of the "step by step" mode.
     * @param prevOperationX The previous operation. Represents part of the route (a jump) that the {@code AbstractAutomata} has follow.
     * @param currentOperationX The curent operation. Represents part of the route (a jump) that the {@code AbstractAutomata} has follow.
     * @return 
     */
    private boolean drawCurrentOperations(int increment, String prevOperationX, String currentOperationX){
        boolean isBadEnd = false;
        
        this.stepIndex+= increment;


        // Node
        if (this.isNodeOperation(currentOperationX)){

            // A* [1] B
            Node nodeX = this.graphStream.getNode(currentOperationX);
            this.selectNode(nodeX);


            // A <CX> A B* [1] (lamda)
            if (increment>0 && this.isNodeOperation(prevOperationX) && !prevOperationX.equals(currentOperationX)){
                this.lambdaRoute = this.getLambdaRoute(prevOperationX, currentOperationX);
                this.selectLambdaEdge();
            }
            
            else if (increment<0 && (this.stepIndex-1>=0) ){
                String prevNode_name = this.automataOperations.get(this.stepIndex-1);
                if (this.isNodeOperation(prevNode_name)){
                    this.lambdaRoute = this.getLambdaRoute(prevNode_name, currentOperationX);
                    this.selectLambdaEdge();         
                }
            }
        }
        // Edge
        else if (this.isEdgeOperation(currentOperationX)){
            // A [1]* B
            String prevNode_name = this.automataOperations.get(this.stepIndex-1);
            String nextNode_name = this.automataOperations.get(this.stepIndex+1);
            String edgeX_id = this.genEdgeId(prevNode_name, currentOperationX.charAt(1), nextNode_name);

            Edge edgeX = this.graphStream.getEdge(edgeX_id);

            this.selectEdge(edgeX);                   
        }
        // Special-Operations (Valid: ends; Invalid: rollback)
        else if (this.isEndingOperation(currentOperationX)){
            String prevNode_name = this.automataOperations.get(this.stepIndex-1);
            
            if (currentOperationX.equals(AbstractAutomata.OPERATION_CODE__COMMAND_INVALID)){
                isBadEnd = true;  

                if(increment<0 && this.isNodeOperation(prevOperationX)){  
                    String realPrevOperation = this.automataOperations.get(this.stepIndex-1); // If you go backwards the previously declared var 'prevOperation' is not really the previous but the future one. 
                    this.selectNode(this.graphStream.getNode(realPrevOperation));
                }
                
            }
            
            if (prevNode_name.equals(AbstractAutomata.OPERATION_CODE__COMMAND_INVALID)){ 
                if(increment>0 && currentOperationX.equals(AbstractAutomata.OPERATION_CODE__FINAL_NODE_INVALID)) {isBadEnd =true;}

                prevNode_name = this.automataOperations.get(this.stepIndex-2);
            }
           
            this.attachSprite(prevNode_name, currentOperationX);
        }
        
            
                    
        return isBadEnd;
    }
    
    /**
     * To increment the {@link #pathIndex} is needed two increments (in a row) of the same sign, except during the very first time (that starts in the very initial node).
     * 
     * @param increment The {@link #showCurrentOperation_v3(int)} parameter
     * @param prevOperationX a previous operation
     * @param currentOperationX a next operation
     * @return {@code true} if the increment is zero (0) or if it completes the "two increments in a row of the same sign" restriction; {@code false} otherwise
     * 
     * @see #showCurrentOperation_v3(int) 
     */
    private boolean checkToIncrementPathIndex(int increment, String prevOperationX, String currentOperationX){
        boolean incrementPathIndex = false;
        boolean forceRollBack = false; //When the currentNode is '<BX>'
        if(increment<0){
            if(this.stepIndex == 0) { this.pathIndex = 0; }
            else if(this.isNodeOperation(prevOperationX)){
                incrementPathIndex = true; 
            }
            
        }
        else if (increment>0 && this.automataOperations.size()>this.stepIndex){
            if(this.isEdgeOperation(currentOperationX) || currentOperationX.equals(AbstractAutomata.OPERATION_CODE__COMMAND_INVALID)){ // Ambas suponen el paso por un 'Edge' solo que la segunda nos indica que no hay 'Edge' para dicho 'command'/'simbolo'.
                incrementPathIndex = true;
            }
            else if (currentOperationX.equals(AbstractAutomata.OPERATION_CODE__FINAL_NODE_INVALID)){
               // if(this.textInputControlller.getText().length()-1>this.pathIndex){ // Si es la ultima, se trata de un 'INVALID' y no debería hacer el rollback
                if(this.automataOperations.size()-1>this.stepIndex){ //[BUG-FIX]: [Ejemplo_2] (0001) => [SOLUTION] A [0] A [0] A [0] A [1] B <BX> ; so by the end on the command 'B' it not turns back
                   // this.pathIndex--;
                   increment= -1;
                   incrementPathIndex = true;
                   forceRollBack = true; 
                   
                }
            }
            
        }
        
        
        //A expceción de la primera vez (que se parte del nodo inicial), para incrmeentar pathIndex, se requieren dos incrementos del mismo signo seguidos.
        if(this.stepIndex==1){
            this.pathIndex=1; this.auxStepIndex = this.stepIndex; 
        }
        else if(incrementPathIndex==true){
            final int incremnetGap = Math.abs(Math.abs(this.auxStepIndex)-Math.abs(this.stepIndex));
            if ((increment>0 && this.backUpUsedWhileGoingBack==true)||(incremnetGap>=2) ||forceRollBack ==true){                
                boolean isSameNode = (prevOperationX.equals(currentOperationX) && this.isNodeOperation(currentOperationX));
                if(isSameNode==false || this.backUpUsed == true){
                    this.auxStepIndex = this.stepIndex;
                }

                this.pathIndex+=increment; 
                this.backUpUsedWhileGoingBack = false;
                this.backUpUsed = false;
            }         
        }
                
        
        return incrementPathIndex;
    }
    
    
    

    /**
     * Solves the situation where: [0] A {@literal <}CX{@literal >}* {@literal <}BX{@literal >}  := going back to 'A' use the backup but if you go 'nextStep' instead of increment the wrong path it still pointing the same 'symbol' because is needed two steps to change the 'pahtIndex'.
     */
    private boolean backUpUsedWhileGoingBack = false; 
    /**
     * Its used to reset the {@link #auxStepIndex}
     */
    private boolean backUpUsed = false;
    
        
    /**
     * Shows the current operation:
     * Basic Flow:
     *   1) Clears any previous selection
     *   2) Selects the current components
     * 
     * NOTE: 
     *   "A [1] B" represents a transaction where:
     *      A := a node (initial)
     *      B := a node (final)
     *      1 := a transaction command
     *      And the sense of the transaction is A ---> B
     *   The '*' (asterisk) indiecates the element that represents the current iteraction
     *   ---> "A* [1] B" indicates that we are in the 'A' node (initial).
     * 
     * @param increment 0 (first time); -1 (backwards); +1 (fordwards) 
     */
    private void showCurrentOperation_v3(int increment){
        
        if(increment==0){ 
            this.stepIndex = 0;
            this.pathIndex = 0;
            this.pathIndexBackUp.clear();
        }
        
        
        if(this.reloadedAutomata==true){
            this.disselectEverything();
            this.reloadedAutomata = false;
        }
        
        this.textInputControlller.enableHTML();

        //Clears any sytleModification happened during the "direct" mode, witch shows the final solution in one step.
        if(this.directMode ==true && this.spriteToDetach!=null){
            detachCurrentSprite();
        }
        
        
        boolean isBadEnd = false;
        String prevOperationX =  this.automataOperations.get(this.stepIndex);   
        String currentOperationX = this.automataOperations.get(this.stepIndex+increment);

  
        // FOR-DEBUG-ONLY
        if(increment>0){ System.out.println(prevOperationX+" => "+currentOperationX); }
        else{ System.out.println(currentOperationX+" <= "+prevOperationX); }

        
        // For better 'look and feel'
        if (increment<0 && prevOperationX.contains(">") && currentOperationX.contains(">")){
            this.stepIndex--;
            this.showCurrentOperation_v3(-1);
        }
        else{
            // Clear previous operations
            this.cleanPreviousOperations(increment, prevOperationX, currentOperationX);
            // Draw current operations
            isBadEnd = this.drawCurrentOperations(increment, prevOperationX, currentOperationX);
        }
        
        boolean incrementPathIndex = false;
        
        // To control the 'ControladorTextInput'
        boolean isLambaStep = !(this.lambdaRoute==null || this.lambdaRoute.isEmpty()) || (prevOperationX.equals(AbstractAutomata.OPERATION_CODE__COMMAND_INVALID)&&this.isNodeOperation(currentOperationX)&&this.pathIndexBackUp.containsKey(currentOperationX));
        if(isLambaStep==false){
            incrementPathIndex = this.checkToIncrementPathIndex(increment, prevOperationX, currentOperationX);
            //this.checkToIncrementPathIndex(increment, prevOperationX, currentOperationX);

            // Stores a Backup of the currentNode's current 'pathIndex' 
            if(this.isNodeOperation(currentOperationX)){
                this.pathIndexBackUp.put(currentOperationX, pathIndex);          
            }
        }
        // If exsists: returns the Backup
        else if(this.pathIndexBackUp.containsKey(prevOperationX)){
            this.pathIndex = this.pathIndexBackUp.get(prevOperationX);
            if(increment<0){this.backUpUsedWhileGoingBack = true;}
            this.backUpUsed = true;
            
        }
        

        // FOR DEBUG-ONLY
        System.out.println("pathIndex: "+this.pathIndex);
        
        // Colours the current 'command' of the textInput
        if(incrementPathIndex){
            this.textInputControlller.setStepIndex(this.pathIndex, isBadEnd); 
        }
    }
    /**
     * Remarks the final solution: the visited nodes and the path traveled to reach a valid final node or nothing in case no final node found.
     * 
     * @throws Exception that the {@link #runAutomata() } may throw
     * @see #runAutomata() 
     */
    private void showFinalSolution() throws Exception{  
            this.runAutomata();

            if(this.isValid){
                String finalSolution = this.automataX.getFinalSolution();


                //Special situation where no textInput is needed to be considered as 'VALID'.
                if(finalSolution.isEmpty()){

                   // this.showCurrentOperation_v3(0);
                   // this.showCurrentOperation_v3(+1);

                    // Selects the Starting Node
                    String initialNode_name = this.automataOperations.get(0).trim();

                    // Ending Node (due lambdaTransaction)
                    String finalNode_name = "";
                    for(int i=this.automataOperations.size()-1;i>=0;i--){
                        String operationX = this.automataOperations.get(i).trim();
                         if(this.isNodeOperation(operationX)){
                             finalNode_name = operationX;
                             break;
                         }      
                    }


                    // Special case: lambdaTransactions points directly to the final node.                 
                    // Confirm that the solution is made with at least one "lambdaTransaction".
                    if(initialNode_name.equals(finalNode_name) == false){
                        this.selectNode(initialNode_name);
                        this.selectNode(finalNode_name);

                        HashSet<Edge> auxLambdaRoute = this.getLambdaRoute(initialNode_name, finalNode_name);
                        this.lambdaRoute.addAll(auxLambdaRoute);   

                        this.attachSprite(initialNode_name, AbstractAutomata.OPERATION_CODE__FINAL_NODE_VALID);
                    }
                    else{ 

                        this.showCurrentOperation_v3(1);
                    }

                }
                else{
                    //System.out.println("[SOLUTION] "+ finalSolution);

                    final String SPACE_REGREX = "(?= )";
                    String[] operationsArray = finalSolution.trim().split(SPACE_REGREX);

                    //TODO: ver si las lambdatransactions tambien ban de 3 en 3. 
                    String prevCommand = null;
                    for(int i=0;i<operationsArray.length;i++){
                        String commandX = operationsArray[i].trim();
                        if(this.isNodeOperation(commandX)){        
                            if(commandX!=prevCommand && !commandX.equals(prevCommand)){ // commandX!=prevCommand := porque con cada transaccion el nodo final coincide con el inicial de la siguiente transaccion.  

                                System.out.println("nodeName: "+ commandX);
                                // 'Node' selection
                                this.selectNode(commandX);

                                // 'LambdaRoute' selection 
                                if(prevCommand!=null && this.isNodeOperation(prevCommand)){
                                    this.lambdaRoute.addAll(this.getLambdaRoute(prevCommand, commandX));
                                }            
                            }
                        }
                        else if(this.isEdgeOperation(commandX)){

                            // 'Edge' selection
                            //NOTA: como es la solucion final, es seguro que sean nodos.
                            String prevNode_name = operationsArray[i-1].trim();
                            String nextNode_name = operationsArray[i+1].trim();
                            Character symbolX = commandX.charAt(1);

                            String edgeX_id = this.genEdgeId(prevNode_name,symbolX , nextNode_name);
                            Edge edgeX = this.graphStream.getEdge(edgeX_id);

                            // Special case: lambdaTransactions points directly to the final node. 
                            if(edgeX == null){
                                this.lambdaRoute.addAll(this.getLambdaRoute(prevNode_name, nextNode_name));

                            }
                            else { this.selectEdge(edgeX); }
                        }

                        prevCommand = commandX;
                    }

                    //this.selectLambdaEdge();
                    //this.lambdaRoute.clear();

                    String initialNode_name = operationsArray[0];
                    System.out.println("**finalNode_VALID: "+initialNode_name );
                    this.attachSprite(initialNode_name, AbstractAutomata.OPERATION_CODE__FINAL_NODE_VALID);

                    // Special case: lambdaTransactions points directly to the final node. 
                    String finalNode_name = operationsArray[operationsArray.length-1].trim();
                    if(this.automataX.isFinalState(finalNode_name)==false){
                        for (String finalStateX : this.automataX.getFinalStates()){
                            finalStateX = finalStateX.trim();
                            HashSet<Edge> auxLambdaRoute = this.getLambdaRoute(finalNode_name, finalStateX);
                            if (auxLambdaRoute.isEmpty()==false){
                                this.lambdaRoute.addAll(auxLambdaRoute);
                                this.selectNode(finalStateX);
                                break;
                            }
                        }

                    }
                }
            }
            else{
                String firstNode_name = this.automataX.getInitialStates().iterator().next();
                this.attachSprite(firstNode_name, AbstractAutomata.OPERATION_CODE__FINAL_NODE_INVALID);
            }

            
            this.selectLambdaEdge();


            this.directMode = true;          
    }
    
    
    
    
    
    /**
     * Detach currently displayed sprite (witch is saved on {@link #spriteToDetach}) front a Node; then hide it.
     * @see #spriteToDetach
     */
    private void detachCurrentSprite(){
        this.spriteToDetach.setAttribute("ui.hide" );
        this.spriteToDetach.detach(); 
        this.spriteToDetach = null;
    }
    /**
     * Attach one of the inited sprites on {@link #spritesDict} during {@link #initSprites() }.
     * NOTE: Since there cant be more that one sprite showing at the same time, every time one is displayed it is saved 
     * in the local var. {@link #spriteToDetach}.
     * @see #detachSprite() 
     * @param nodeName The name of the node.
     * @param spriteID The id given to the sprite, witch are declared on {@link #spritesIDs}
     */
    private void attachSprite(String nodeName, String spriteID){
        this.updateSpriteIcon(spriteID);

        this.spriteToDetach = this.spritesDict.get(spriteID);
        this.spriteToDetach.attachToNode(nodeName);   //
        
        // Update depending on the current rotation
        this.spriteToDetach.setAttribute("ui.class", this.getRotatedSpriteStyle(spriteID));       
        this.spriteToDetach.removeAttribute("ui.hide");
        
        if(spriteID.equals(this.INITIAL_STATE_ARROW)){this.spriteToDetach = null;}
    }
   
    
   
    
    /**
     * GETs the "lambdaEdges"; the path that form part of the lambdaRoute between the {@code initialNode_name} and the {@code finalNode_name}.
     * [EJ] A [lamda] B [lamda] C :
     * If {@link AbstractAutomata#operationsList_str}, is: A [CX] A C , 
     * It should return the 2 lambdas that are between A and C. 
     * 
     * @param initialNode_name A state from any {@link Transaction#initialState}
     * @param finalNode_name A state from any {@link Transaction#finalStates}
     * @return The inter-connected lambda {@llnk Edge}s between the initial and final node names (given as parameters)
     */
    private HashSet<Edge> getLambdaRoute( String initialNode_name, String finalNode_name){
        Node initialNode = this.graphStream.getNode(initialNode_name);
        ArrayList<Node> lambdaRoute_nodes = null;
        lambdaRoute_nodes = this.getLambdaRoute(new ArrayList<Node>(),initialNode, finalNode_name);
        
        HashSet<Edge> lambdaRoute = new HashSet<Edge>();
        Collections.reverse(lambdaRoute_nodes);
        Node prevNode = initialNode;
        
        //System.out.println("LAMBDA-ROUTE");
        //for (Node nodeX : lambdaRoute_nodes){ System.out.println("*Node: "+ nodeX.toString()); }
        
        
        for (int i=1; i<lambdaRoute_nodes.size() ;i++){
            Node nextNode = lambdaRoute_nodes.get(i);

            
            Edge lambdaEdge = this.graphStream.getEdge(this.genEdgeId(prevNode.getId(), LAMBDA, nextNode.getId()));

            lambdaRoute.add(lambdaEdge);
            
            prevNode = nextNode;
            
        }
        
        return lambdaRoute;
    }  
    /**
    * [Recursive Function]
    * 
    * @param lambdaRoute An auxiliar data structure that stores the returns of each iteration. Starts in blank.
    * @param initialNode A starting {@link Node}
    * @param finalNode_name  A final {@link Node} name
    * @return The {@code lambdaRoute} with one more element (a Node)
    * @see #getLambdaRoute(java.lang.String, java.lang.String) 
    */
    private ArrayList<Node> getLambdaRoute(ArrayList<Node> lambdaRoute, Node initialNode, String finalNode_name){
        
        boolean foundEdge = false;
        HashSet<Edge> lambdaEdges = this.getEdges(initialNode.toString(), LAMBDA);
        for(Edge edgeX: lambdaEdges){
            Node nextNode = edgeX.getTargetNode(); //edgeX.getOpposite(initialNode); 
            lambdaRoute = getLambdaRoute(lambdaRoute, nextNode, finalNode_name);
           
            if (lambdaRoute.size()>0){break;}
        }
        
        if (lambdaRoute.size()>0 || initialNode.toString().equals(finalNode_name)){
            lambdaRoute.add(initialNode);
        }
        
        return lambdaRoute;
    }
    
}
