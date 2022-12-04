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
import java.awt.Component;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.io.File;
import java.io.FileNotFoundException;
import java.math.BigDecimal;
import java.math.RoundingMode;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.NoSuchElementException;
import java.util.logging.Level;
import java.util.logging.Logger;
import java.util.stream.Collectors;
import org.graphstream.graph.Edge;
import org.graphstream.graph.Graph;
import org.graphstream.graph.Node;
import org.graphstream.graph.implementations.MultiGraph;
import org.graphstream.ui.graphicGraph.stylesheet.StyleConstants;
import org.graphstream.ui.spriteManager.Sprite;
import org.graphstream.ui.spriteManager.SpriteManager;



//TO-DO [EXTRA-MECHANIC]: Determinar si un "AutomataImportado", es  determinista o no (tip: Si de un Nodo parten más de un 'Transaction' es 'AutomataNoDeterminista')
// ===> Se indica en el ficheroImportado (PERO NO HAY FORMA DE DETERMINAR QUE FUCNIONA BIEN?)

//TODO [BASE]: Guardado de pasos para un 'AutomataNoDeterminista' (y verificar que funciona correctamente).

//TODO [JAVADOC] (FUCIONAMIENTO_BASE): Explicar el cómo funcionan los automatas ('AbstractAutomata') y las "transiciones" ('Transaction')
//TODO-2 [JAVADOC-2]: Documentar cada método.
//TODO-END [VISUAL-BUG]: Si 2 o más 'Edges' parten de un Nodo A a otro Nodo B, los nombres de los 'Edges' se superponen. 
// [PARTIAL-FIX]: Al mostrar paso a paso, el texto del 'Edge' se remarca, sulucionando momentáneamente el solapamiento. 
/**
 *
 * @author Jtorr
 */
public class ControladorAutomata implements ActionListener {
    /**
     * λ
     */
    private final Character LAMDA = 'λ';

    private final String DEFAULT__STYLE_SHEET = "styleX";
    // View
    private String stringInput = null;
    private int stepIndex = 0;
    private int pathIndex = 0; // see: colorizeCurrentCommand()
    private ArrayList<String> automataOperations = null;
    private ControlPane controlPane;
    // GraphStream
    private Graph graphStream = null;
    private SpriteManager spriteManager;        
    private HashMap<String,Sprite> spritesDict;

    // Models
    private boolean isValid = false;
    private AbstractAutomata automataX;
    private FileLoader fileLoader;
    
    public ControladorAutomata(){
        // Init fileLoader
        this.fileLoader = new FileLoader(); 
        
        // Init View
        this.controlPane = new ControlPane();
        ControlPane.setLocationToCenterLeft(this.controlPane);
        this.controlPane.setVisible(true);               // Display the pane
        
        // Add Listeners
        addListeners();
    }

    // LISTENERS
    private void addListeners(){
        this.controlPane.jButtonLoadAutomataFromFile.addActionListener(this);
        this.controlPane.jButtonNextStep.addActionListener(this);
        this.controlPane.jButtonPrevStep.addActionListener(this);
        this.controlPane.jButtonRunAutomata.addActionListener(this);
    }
    @Override
    public void actionPerformed(ActionEvent e) {
        switch(e.getActionCommand()){
            case "importAutomata":            
                try{
                    
                    // Choose file from disk
                    File fileX = this.fileLoader.chooseFile();

                    // Generates the 'AbstractAutomata' from selected file
                    this.automataX = this.fileLoader.genAutomataFromFile(fileX);  

                    // Init and display the Graphs-Representation of the 'Automata' 
                    //if (this.graphStream==null){
                        this.initGraphStream("Test1");
                        this.graphStream.display();  

                    //}
                    
                  } 
                catch (FileNotFoundException ex)    { Logger.getLogger(ControladorAutomata.class.getName()).log(Level.SEVERE, null, ex); } 
                catch (NoSuchElementException ex)   { Logger.getLogger(ControladorAutomata.class.getName()).log(Level.SEVERE, null, ex); }
            break;     
            case "runAutomata":
                if (this.automataX == null){ System.out.println("Error: To run an <<Automata>>, first import it!"); }
                else {
                    this.stringInput = this.getStringInput();
                    this.controlPane.enableHTML();
                    if(false && stringInput.length()==0){ System.out.println("Error: You need to write a command to validate it!"); } 
                    else{
                        
                        // Check if the 'stringInput' is valid or not.
                        this.isValid = this.automataX.runAutomata(this.stringInput);
                        // Saves every operation to show it step by step
                        this.automataOperations = this.automataX.popOperationsList();
                                                                               
                        // Reset the stepIndex
                        this.stepIndex = 0;
                        // Select the initial node
                        //this.showCurrentOperation(0);
                         this.showCurrentOperation_v2(0);
                    }
                }
                break;                
            case "prevStep":
                
                
                if (this.automataOperations==null) {System.out.println("ERROR: To show 'step by step' the 'validation' process, first 'RUN' the Automata.");}
                else{
                    if(this.stepIndex<=0){ System.out.println("ERROR: Your cant go back more because you are already at the very begining!"); }
                    else{ showCurrentOperation_v2(-1); }
                }
                
                break;                     
            case "nextStep":
                if (this.automataOperations==null) {System.out.println("ERROR: To show 'step by step' the 'validation' process, first 'RUN' the Automata.");}
                else{
                    if(this.stepIndex+1>=this.automataOperations.size()){ System.out.println("ERROR: Your cant go more further because you are already at the very end!"); }
                    else{ showCurrentOperation_v2(+1); }
                }
                    
                break;
        }
    }
    private String getStringInput(){
        return this.controlPane.jTextFieldCommandInput.getText();
    }
    
   
    
    

    // GRAPH-STREAM
    /**
    * 
    * @param graphStreamName 
    */
    private void initGraphStream(String graphStreamName){
        
        System.setProperty("org.graphstream.ui", "swing"); // For Swing

        this.graphStream = new MultiGraph(graphStreamName);
        this.spriteManager = new SpriteManager(this.graphStream);

        
        // With these 2 lines: there is no need to add each node, but the edges that links them
        this.graphStream.setStrict(false);
        this.graphStream.setAutoCreate( true );
                           
        // Draw Graphs
        this.drawGraphsStructure();
        graphStream.setAttribute("ui.antialias");

        //this.fixOverlappingEdgeLabel(this.graphStream, 10);
        
        // Init & Save Sprites
        this.initSprites();
        
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
                Edge edgeX = this.graphStream.addEdge(edgeX_name, initialState, finalState, true);
                
                // Stores the {@link Transaction#command} to display in the middle of the {@link org.graphstream.graph.Edge}.
                //System.out.println("==> COMMAND: "+ command);
                
                //System.out.println("edgeX_name: "+edgeX_name);
                //Edge edgeX = this.graphStream.getEdge(edgeX_name);
                edgeX.setAttribute("command", ((command==null)? "λ":command)) ;
                if (command==null){
                    edgeX.setAttribute("ui.class", "lamdaEdge");
                }
                else if(initialState.equals(finalState)){
                    edgeX.setAttribute("ui.class", "selfloop");
                }
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
        
        // Adds an ArrowSprite (that points to a 'Node') to difference the 'initialState' from the rest.
        Sprite InitialStateArrow  = this.spritesDict.get("InitialStateArrow");
        //Sprite InitialStateArrow = spriteManager.addSprite("InitialStateArrow"); 
        //InitialStateArrow.setPosition(StyleConstants.Units.PX, -43, -2, 0);
        

        
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
            else if (this.automataX.getInitialStates().contains(nodeNameX)){ InitialStateArrow.attachToNode(nodeNameX); }
            // (DEAD-State: Style)
            else if (nodeNameX.equals(AbstractAutomata.DEAD_NODE__NAME)) {
                nodeX.setAttribute("ui.class", "deadNode");
            }       
        }    
    }   
    private void setStyle_Edges(){
        final int NUM_EDGES = this.graphStream.getEdgeCount();
        for (int i=0;i<NUM_EDGES;i++){
            Edge edgeX = this.graphStream.getEdge(i);
            
            // Displays the 'Transaction-command' in the middle of the 'Edge'
            edgeX.setAttribute("ui.label", edgeX.getAttribute("command"));

            // Add Style
            if(edgeX.toString().contains(DEAD_NODE__NAME)){edgeX.setAttribute("ui.class","dottedEdge");}               
        }    
    }
    private final String[] spritesIDs ={"InitialStateArrow","finalStateValid","finalStateInvalid","invalidCommand"};
    
    
    
    //[DEPRECATED] NOTA [VISUAL-BUG]: El posicionamiento 'TO_RIGHT' hace que los sprites se muestren en la esquina superior izquierda; como si se instanciara un Nodo en dicha esquina y los 'Sprites' se guardan temporal mente en este al hacer un 'sprite.detach()'...
    // [SOLUTION]: .setAttribute("ui.hide" ).
    private final int SPRITE_OFFSET_X__TO_RIGHT = +43;  // +43 := right-side
    private final int SPRITE_OFFSET_X__TO_LEFT = -43;   // -43 := left-side
    private final int SPRITE_OFFSET_Y__CENTERED = -2;   // -2  := Y-Centered

    private void initSprites(){      
        this.spritesDict = new HashMap<String,Sprite>();
            
        for(String spriteID : this.spritesIDs){
            int offsetX = SPRITE_OFFSET_X__TO_RIGHT;
            if (spriteID.equals("InitialStateArrow")){
                offsetX = SPRITE_OFFSET_X__TO_LEFT;
            }
            
            Sprite spriteX = this.spriteManager.addSprite(spriteID); 
            spriteX.setAttribute("ui.class", spriteID);
            spriteX.setPosition(StyleConstants.Units.PX, offsetX, SPRITE_OFFSET_Y__CENTERED, 0);
            spriteX.setAttribute( "ui.hide" );

            this.spritesDict.put(spriteID, spriteX);
        }
    }
    
    private Node prevNode = null;
    private Sprite spriteToDetach = null;
    private Edge prevEdges[] = null;
    private Edge prevLamdaEdge = null;
    private Edge prevUsedEdge = null; // [SOLO FUNCIONA EN UN SENTIDO: HACIA ADELANTE!] Aquel 'Edge' de los 'prevEdges' que conectan al 'Nodo' actual; Si se va hacia adelante: este se dejará de seleccionar cuando lo haga el 'Nodo' al que apunta. Si se va hacia atras: Se dejará de seleccionar con el resto de 'prevEdges'.
    // TO-DO: Dynamically modificates the 'GraphStream'
    // TODO: Para los 'Edges' de las transaccionesLAMBDA: Si se esta en un nodo y el prevEdges == null; el nodo actual se trata de una transaccionLAMBDA; buscarla (:= 'prevLamdaEdge') y colorearla a la par que el nodo 
    private void showCurrentOperation(int increment){
        this.stepIndex+= increment;
        String currentOperationX = this.automataOperations.get(this.stepIndex);
       
       
        //TODO: ESTO NO ESTA BIEN, HAY QUE HACER UN REMODELADO DE TODA ESTA FUNCION Y PENSAR LOS CASOS BIEN PARA ESTE ESTRUCTURADO DE FORMA LOGICA Y NO LA AMALGAMA QUE TENGO MONTADA. 
        // EJEMPLO PARA VERIFICAR QUE TODO FUNCIONA BIEN ===> 'instantane.txt' + stringInput:= '012'.
        if (increment>0){
            if( this.prevLamdaEdge!=null){
                this.colorizeCurrentCommand(true);
            }
            else{
                if (this.isEdgeOperation(currentOperationX)){
                    pathIndex++;
                    this.colorizeCurrentCommand(true);

                }
                else if(this.isEndingOperation(currentOperationX) && currentOperationX.equals(AbstractAutomata.OPERATION_CODE__COMMAND_INVALID)){
                    pathIndex++;
                    this.colorizeCurrentCommand(false);
                }
            }
        }
        else if (increment<0){
            if(this.isEdgeOperation(currentOperationX)==false && this.prevEdges!=null){
                this.pathIndex--;
                this.colorizeCurrentCommand(true);
            }
            
            
        }

        
        
        //System.out.println("stepIndex: "+ this.stepIndex);
        
        /*
        String prevOperationX = this.automataOperations.get(this.stepIndex);
        if (!prevOperationX.contains("]") && !prevOperationX.contains(">")){
            System.out.println("prevNode:"+prevOperationX );
            Node nodeX = this.graphStream.getNode(prevOperationX);
            nodeX.removeAttribute("ui.style");
        }
        */
        
        //Resets the edges:
        final int NUM_EDGES = this.graphStream.getEdgeCount();
        for (int i=0;i<NUM_EDGES;i++){
            Edge edgeX = this.graphStream.getEdge(i);
            this.disselectEdge(edgeX);

            
        }
        

        
        System.out.println("["+this.stepIndex+"] => "+currentOperationX);
        
        
        // Resets the sprite link to the node
        if(spriteToDetach!=null){ 
            this.spriteToDetach.detach(); 
            this.spriteToDetach.setAttribute("ui.hide" );
            this.spriteToDetach = null;
        }
        
        if(increment>0 && this.isEndingOperation(currentOperationX)){
            switch(currentOperationX){
                case AbstractAutomata.OPERATION_CODE__FINAL_NODE_VALID:
                    System.out.println("VALID");
                    Sprite validIcon = this.spritesDict.get("finalStateValid");
                    validIcon.attachToNode(this.prevNode.toString());    
                    this.spriteToDetach = validIcon;
                    break;
                case AbstractAutomata.OPERATION_CODE__FINAL_NODE_INVALID:
                    System.out.println("INVALID");
                    Sprite invalidIcon = this.spritesDict.get("finalStateInvalid");
                    invalidIcon.attachToNode(this.prevNode.toString());  
                    this.spriteToDetach = invalidIcon;
                    break;
                case AbstractAutomata.OPERATION_CODE__COMMAND_INVALID:
                   Sprite invalidCommand = this.spritesDict.get("invalidCommand");
                   invalidCommand.attachToNode(this.prevNode.toString());
                   this.spriteToDetach = invalidCommand;
                   break;
            }
            
            this.spriteToDetach.removeAttribute( "ui.hide" );

            // [DEPRECATED]Since an 'endingOperation' does not feels like an step but like a consecuence of that, step.
            // [CAUSE]: you'll never go futher (in 'AutomataDeterminista' if doesnt matters, but it does in 'AutomataNoDeterminista').
            // showCurrentOperation(-1);
        }
        else{
            if(this.prevNode!=null){ 
                this.disselectNode(prevNode);
                    

                // Resets the sprite link to the node
                if(spriteToDetach!=null){ spriteToDetach.detach(); this.spriteToDetach = null;}
            }
            
        }
        
        String currentNodeName = null;
        
        // LamdaEdge: dis-select
        if (this.prevLamdaEdge!=null){ 
            this.disselectEdge(prevLamdaEdge); 
            this.prevLamdaEdge = null;
        }
        // LamdaEdge: select
        if (this.isNodeOperation(currentOperationX)){
            //System.out.println("nextNode:"+currentOperationX );

            // LAMDA-Edges
            if(increment>0 && this.prevNode.toString().equals(currentOperationX)==false){ //<<this.prevNode.toString().equals(currentOperationX)==false>>:= ocurre siempre que tiene que retroceder porque la rama anterior no  era 'VALIDA'.
                if(this.stepIndex!=0 && this.prevEdges == null){
                    //String prevOperation =  this.automataOperations.get(this.stepIndex-1);
                    String prevNodeName= this.prevNode.toString(); // =this.graphStream.getNode(prevOperation).toString();
                    this.prevLamdaEdge = this.graphStream.getEdge(this.genEdgeId(prevNodeName, null, currentOperationX));
                    if(prevLamdaEdge!=null){
                        this.selectEdge(this.prevLamdaEdge);
                    }
                    
                }
            }
            
            Node nodeX = this.graphStream.getNode(currentOperationX);
            currentNodeName = nodeX.toString();
            this.selectNode(nodeX);

            this.prevNode = nodeX;
        }
        // Edges: dis-select ; TODO: keep selected the 'Edge' that point the next 'Node'
        if(this.prevEdges!=null){
            for (Edge edgeX : this.prevEdges){
                
                
                //edgeX.setAttribute("ui.style", "text-color: black; text-size:30px;"); 
                if(currentNodeName!=null && increment>0){ // [SOLO FUNCIONA HACIA ADELANTE!]
                    if(prevUsedEdge!=null){
                        if (edgeX.getNode1().toString().equals(currentNodeName)==true){ this.prevUsedEdge = edgeX; }
                        else{ 
                            this.disselectEdge(edgeX);
                        }
       
                    }
                }
                else{ 
                    this.disselectEdge(edgeX); 
                }
                    
                
            }
            this.prevEdges = null;
        }
        
        // TODO [FUTURE-VERY_IMPORTANT]: SOLO aplicable a los AUTOMATAS-NO-DETERMINISTAS cuando hacen backtraking; el backtraking es una operacion a guardar!
        // Edges: select
        else if (this.isEdgeOperation(currentOperationX)){
            if(this.prevUsedEdge!=null){ 
                this.disselectEdge(this.prevLamdaEdge);
                this.prevUsedEdge = null; }
            
            Character command = currentOperationX.charAt(1);
            String prevOperation =  this.automataOperations.get(this.stepIndex-1);
            
            HashSet<Edge> nextEdgesList = this.getEdges(prevOperation, command);
            this.prevEdges = new Edge[nextEdgesList.size()];
            int i=0;
            for(Edge edgeX :nextEdgesList){
                selectEdge(edgeX);
                
                this.prevEdges[i++] = edgeX;
                
            }
            //String nextOperation = null;
            
            
           // String edgeID = this.genEdgeId(DEAD_NODE__NAME, Character.MIN_VALUE, DEAD_NODE__NAME)
            
        }
        
            
            
            
        
        
        
      //     this.graphStream
       // 3 tipos : 
       /*
        Nodo: A
            Commando: [C]
        Return/End:
            InvalidCommand: <CX> 
            InvalidFinalNode: <BX>
            ValidFinalNode: <VALID>
       */
    }
    
    private void selectNode(Node nodeX){
        System.out.println("SELECTED: "+nodeX.toString());
        nodeX.setAttribute("ui.color", 0.5f);
        nodeX.setAttribute("ui.size", 35);
        
        // If that node has a selfLoop, the redimension will affect the Edge but not its text, 
        // so we need to modify its offset to mantain some consistancy on that change of size.
        //Edge selfLoopEdge = nodeX.getEdgeToward(nodeX);
        //if(selfLoopEdge!=null){ selfLoopEdge.setAttribute("ui.class", "selfloopSelected"); }
        
        
    }
    
    private void disselectNode(Node nodeX){
        System.out.println("DIS-SELECTED: "+nodeX.toString());

        nodeX.setAttribute("ui.color", 0.0f);
        nodeX.setAttribute("ui.size", 30);
        
        
        // If that node has a selfLoop, the redimension will affect the Edge but not its text, 
        // so we need to modify its offset to mantain some consistancy on that change of size.
        //Edge selfLoopEdge = nodeX.getEdgeToward(nodeX);
        //if(selfLoopEdge!=null){ selfLoopEdge.setAttribute("ui.class", "selfloop"); }
    }
    // Colorize an Edge and its text in red; used during crossing an Edge.
    private void selectEdge(Edge edgeX){
        edgeX.setAttribute("ui.color", 0.9f);
        edgeX.setAttribute("ui.size", 2);
        
        String textOffsets = "";
        if(isSelfLoop(edgeX)){
            edgeX.setAttribute("ui.class", "selfloopSelected");
        }
        edgeX.setAttribute("ui.style", "text-color: red; text-size:40px;");   
    }
    // Restores the default style of an Edge.
    private void disselectEdge(Edge edgeX){
        if(isSelfLoop(edgeX)){
            edgeX.setAttribute("ui.class", "selfloop");
        }
        edgeX.setAttribute("ui.style", "text-color: black; text-size:30px;");   
        edgeX.setAttribute("ui.color", 0.0f);
        edgeX.setAttribute("ui.size", 1);
    }
    

    private void selectLambdaEdge(){
        this.selectEdge(this.lambdaEdge);
        this.lambdaEdge.setAttribute("ui.class", "lambdaEdgeSelected");
    }
    private void disselectLambdaEdge(){
        this.disselectEdge(this.lambdaEdge);
        this.lambdaEdge.setAttribute("ui.class", "lambdaEdge");
        this.lambdaEdge = null;
    }
    
    // UTILs
    private boolean isNodeOperation(String operationX){
        return (!isEndingOperation(operationX) && !isEdgeOperation(operationX));
    }
    private boolean isEndingOperation(String operationX){
        return operationX.contains("<");
    }
    private boolean isEdgeOperation(String operationX){
        return operationX.contains("]");
    }
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
    private boolean isSelfLoop(Edge edgeX){ return edgeX.getNode0().toString().equals(edgeX.getNode1().toString()); } 
    

    
    private Edge lambdaEdge = null;
    
    /**
     *  2 Partes:
     *    1) Borra la 'animacion' anterior
     *    2) Dibuja la 'animacion' actual.
     * --> Cada parte difiere segun el sentido del avanze (backwards/fordwards).
     * NOTA-SIMBOLOGIA:
     *  A [1] B := nodoA y nodoB estan conectados por una transaccion de comando :=  '1' (sentido: A-->B).
     *  El asterisco << * >>, indica en que elemento se encuentra en la iteracion actual ==> A* [1] B := actualmente nos encontramos en el NodoA.
     * 
     * @param increment 0 (first time); -1 (backwards); +1 (fordwards) 
     * 
     */
    private void showCurrentOperation_v2(int increment){
        
        // CLEAR: previous Animation
        String prevOperationX =  this.automataOperations.get(this.stepIndex);   
        String currentOperationX = this.automataOperations.get(this.stepIndex+increment);

        
        if(lambdaEdge!=null){
           this.disselectLambdaEdge();
        }
        
        
        
        
        // FORDWARDS (+1)
        if(increment>0){
            
            // Node
            if (this.isNodeOperation(prevOperationX)){
                // A <CX> A B*  (A --> lamnda --> B)
                // A [1]* B
                // A <CX>* B
                if (this.isEndingOperation(currentOperationX)==false){
                    Node prevNodeX = this.graphStream.getNode(prevOperationX);
                    this.disselectNode(prevNodeX); 
                    
                    
                    
                    // A <CX> A B* (lamda)
                    if (this.isNodeOperation(currentOperationX)){
                        this.lambdaEdge = prevNodeX.getEdgeBetween(currentOperationX);
                        this.selectLambdaEdge();
                    }
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
            // Special-Operations (Valid: ends; Invalid: rollback)
            else if (this.isEndingOperation(prevOperationX)){    
                // A <CX> B*
                this.spriteToDetach.detach(); 
                this.spriteToDetach.setAttribute("ui.hide" );
                this.spriteToDetach = null;
                
                String prevNode_name = this.automataOperations.get(this.stepIndex-1); 
                Node prevNodeX = this.graphStream.getNode(prevNode_name);
                this.disselectNode(prevNodeX);
                
                
                
                // A <CX> <BX>  (???)

            }
            
        }
        // BACKWARDS (-1)
        else if (increment<0){
            
            
        }
        
        
        
        
        // DRRAW: current Animation
        this.stepIndex+= increment;
        
        
        // FORDWARDS (+1) /  BACKWARDS (-1)
        if (true){
                  
            // Node
            if (this.isNodeOperation(currentOperationX)){
                // A* [1] B
                Node nodeX = this.graphStream.getNode(currentOperationX);
                this.selectNode(nodeX);
                
              
                
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

                switch(currentOperationX){
                    case AbstractAutomata.OPERATION_CODE__FINAL_NODE_VALID:
                        this.spriteToDetach   = this.spritesDict.get("finalStateValid");
                        break;
                    case AbstractAutomata.OPERATION_CODE__FINAL_NODE_INVALID:
                        this.spriteToDetach  = this.spritesDict.get("finalStateInvalid");
                        break;
                    case AbstractAutomata.OPERATION_CODE__COMMAND_INVALID:
                        this.spriteToDetach  = this.spritesDict.get("invalidCommand");
                        break;
                }
                
                this.spriteToDetach.attachToNode(prevNode_name);  
                this.spriteToDetach.removeAttribute("ui.hide");
            }
            
            
        }

        
        
    }
    
    
    
    
    // 'Look and Feel'
    
    
    
    // The step to step System also remarks the current command in the textInput.
    //     
    private void colorizeCurrentCommand(boolean foundPath){
        final int COMMAND_INDEX = this.pathIndex;
        final String FOUND_COLOR = (foundPath)? "green":"red";
        String prevPart = "", nextPart =this.stringInput;
        Character commandX = null;
        
        // [EJ] "<strong>T<span style='color:red;'>E</span>st</strong>"
        if(COMMAND_INDEX>0){
            if(COMMAND_INDEX==0){
                commandX = this.stringInput.charAt(0);
                nextPart = this.stringInput.substring(0);            
            }
            else{    
                prevPart = this.stringInput.substring(0, COMMAND_INDEX-1);
                commandX = this.stringInput.charAt(COMMAND_INDEX-1);
                nextPart = this.stringInput.substring(COMMAND_INDEX);
            }
        }
        String formattedString ="<strong>"+ prevPart + "<span style='color:"+FOUND_COLOR+";'>"+((commandX!=null)? commandX:"")+"</span>"+nextPart+"</strong>";
        this.controlPane.jTextFieldCommandInput.setText(formattedString);

    }


}
