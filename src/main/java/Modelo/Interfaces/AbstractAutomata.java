/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package Modelo.Interfaces;

import java.util.HashSet;
import java.util.logging.Level;
import java.util.logging.Logger;
import java.util.stream.Stream;
import org.graphstream.graph.Edge;
import org.graphstream.graph.Graph;
import org.graphstream.graph.Node;
import org.graphstream.graph.implementations.SingleGraph;
import org.graphstream.ui.graphicGraph.stylesheet.StyleConstants;
import org.graphstream.ui.spriteManager.Sprite;
import org.graphstream.ui.spriteManager.SpriteManager;


//TODO: crear de forma autmatica el estado "Muerto"
/**
 *
 * @author Jtorr
 */
public abstract class AbstractAutomata {
    public static final String DEAD_NODE__NAME = "DeadNode";
    private Graph graphStream;

    private HashSet<String> initialStates, finalStates;
    
    private HashSet<Transaction> transactionsList; //indica la lista de transiciones del AFD
    
    
     
    public AbstractAutomata(){
        System.setProperty("org.graphstream.ui", "swing");

        this.initialStates = new HashSet<String>();
        this.finalStates = new HashSet<String>();
        this.transactionsList = new HashSet<Transaction>();        
    }
    public void addInitialState(String initialStateX){ this.initialStates.add(initialStateX); }
    public void addFinalState(String finalStateX){ this.finalStates.add(finalStateX); }
    protected HashSet<String> getInitialStates(){
        try {if(this.initialStates.isEmpty()){ throw new Exception("Error: No 'initialStated' found!"); }} 
        catch (Exception ex) { Logger.getLogger(AbstractAutomata.class.getName()).log(Level.SEVERE, null, ex); }
        
        return (HashSet<String>) this.initialStates.clone(); 
    
    }
    protected HashSet<Transaction> getTransactionsList(){
         try {if(this.transactionsList.isEmpty()){ throw new Exception("Error: No 'transaction' found!"); }} 
        catch (Exception ex) { Logger.getLogger(AbstractAutomata.class.getName()).log(Level.SEVERE, null, ex); }
        
        
        return this.transactionsList;
    }
    // TODO: agregarTransicion
    /*protected void addTransaction(String initialState, char command, HashSet<String> nextStates){
        this.transactionsList.add(new Transaction(initialState,command,nextStates));
    }*/
    
    
    private Transaction getTransaction(String initialState, Character command){
        Transaction foundTransaction = null;
         for (Transaction transactionX : this.transactionsList){
            if (transactionX.getInitialState() == initialState && transactionX.getCommand()==command){
                foundTransaction = transactionX; break;
            }
        }
         
         return foundTransaction;
       
    }
    
    /**
     * Adds a new {@link Transaction}. In case already exists a {@link Transaction} with the given param 'command': merges the 'nextStates' to the {@link Transaction#finalStates}.  
     * IMPORTANT: This method has set as 'protected' since the 'AutomataDeterminista' shoudnt use it!.
     * @param initialState
     * @param command
     * @param nextStates 
     * 
     * @return 'false' if the transaction already existed; 'true' otherwise
     */
    protected boolean addTransaction(String initialState, Character command, HashSet<String> nextStates){
        Transaction alreadyExistedTransaction = getTransaction(initialState, command);
        if (alreadyExistedTransaction==null){
            Transaction newTransaction = new Transaction(initialState,command,nextStates);
            this.transactionsList.add(newTransaction);            
        }
        else{ alreadyExistedTransaction.addFinalStates(nextStates); }        
    
        return (alreadyExistedTransaction!=null);
    }
    /**
     * Adds a new {@link Transaction}. In case already exists a {@link Transaction} with the given param 'command': merges the 'nextState' to the {@link Transaction#finalStates}.  
     * (Specific implementation of {@link #addTransaction(java.lang.String, java.lang.Character, java.util.HashSet)} ).
     * 
     * @see #addTransaction(java.lang.String, java.lang.Character, java.util.HashSet) 
     * @param initialState
     * @param command
     * @param nextState 
     * 
     * @return 'false' if the transaction already existed; 'true' otherwise
     */
    public boolean addTransaction(String initialState, Character command, String nextState){
        HashSet<String> nextStates = new HashSet<String>();
        nextStates.add(nextState);
        
        return this.addTransaction(initialState, command, nextStates);  
    }
   
    /**
     * 
     * @param initialState
     * @param command
     * @return null if there is no nextState attached to the given 'initialState' and 'command'
     */
    protected HashSet<String> getNextStates(String initialState, Character command){
       /* HashSet<String> nextStates = new HashSet<String>();
        for(Transaction transactionX : this.transactionsList){
            if (transactionX.getInitialState()==stateX){
                HashSet<String> finalStates = transactionX.getFinalStates();
                nextStates.addAll(finalStates);
            }
        }
     
        return nextStates;
        */
       Transaction foundTransaction = this.getTransaction(initialState, command);
       HashSet<String> nextSates = null;
       if (foundTransaction!=null){ nextSates = foundTransaction.getFinalStates(); }
       
       return nextSates;
    }
    
    
    // esFinal
    public boolean isFinalState(String stateX){
        try {
            if(this.finalStates.isEmpty()){ throw new Exception("Error: No 'finalStates' found!"); }
        } 
        catch (Exception ex) { Logger.getLogger(AbstractAutomata.class.getName()).log(Level.SEVERE, null, ex); }
        
      
        
        boolean isFinalState = false;
        for(String finalStateX : this.finalStates){
             if(finalStateX == stateX){ isFinalState = true;  break;}    
        }
        
        return isFinalState;
    }
    
    
    // Acepta/Rechaza un string pasado como 
    // sup.: Solo existe un unico 'initialState'; TODO: que puedan existir mas
   
    public boolean runAutomata(String inputString){
        // Init the graph visualization
        initGraphStream();
        this.graphStream.display();       

        
        // Check is valid
        boolean isValid = validate(inputString);
        
        return isValid;
    }
    protected abstract boolean validate(String inputString);

    
    private String genEdgeId(String initialState, Character commmand, String finalState){
        return initialState+commmand+finalState;
    }
    
    private void initGraphStream(){
        final String GRAPH_STREAM__NAME = "test1";
        this.graphStream = new SingleGraph(GRAPH_STREAM__NAME);
        
        //Initial Arrow to difference the 'initialState' from the rest.
        SpriteManager spriteManager = new SpriteManager(this.graphStream);
        final Sprite InitialStateArrow = spriteManager.addSprite("InitialStateArrow");
        //InitialStateArrow.setPosition(0, +0., 0);
        InitialStateArrow.setPosition(StyleConstants.Units.PX, -43, -2, 0);
        // With these 2 lines: there is no need to add each node, but the edges that links them
        this.graphStream.setStrict(false);
        this.graphStream.setAutoCreate( true );
        
        // [DEPRECATED] NOTA_DESARROLLO: Aunque 'AbstractAutomata' no debería tener ninguna mencion las lambdaTransactions (see: 'AutomataNoDeterminista'), 
        // por facilidad voy a hacerlo aquí (para resaltar las 'lambdaTransactions' en otro color).
        // =======> [SOLUTION]: Basta hacer un metodo que permita colorear el 'Edge' pasandole la transaccion que tiene asociada.
        for(Transaction transactionX:this.transactionsList){
          String initialState = transactionX.getInitialState();
          Character command = transactionX.getCommand();
          for(String finalState : transactionX.getFinalStates()){
              
              String edgeX_name = genEdgeId(initialState,command,finalState);  
              this.graphStream.addEdge(edgeX_name, initialState, finalState, true);
              this.graphStream.getEdge(edgeX_name).setAttribute("command", command);
                                  
          }  
        }
        
        final String STYLE_SHEET__ABSOLUTE_PATH = this.getClass().getProtectionDomain().getCodeSource().getLocation().toString().split("/target/classes/")[0].split("file:/")[1].replace("/", "\\")+"\\src\\main\\java\\StyleSheets";
        final String DEFAULT__STYLE_SHEET = "styleX";
        this.graphStream.setAttribute("ui.stylesheet", "url('"+STYLE_SHEET__ABSOLUTE_PATH+"\\"+DEFAULT__STYLE_SHEET+"')");

        final int numNodes = this.graphStream.getNodeCount();
        for (int i=0;i<numNodes;i++){            
            Node nodeX = this.graphStream.getNode(i);
            String nodeNameX = nodeX.toString();
            
            nodeX.setAttribute("ui.class", "defaultNode");
            if(this.finalStates.contains(nodeNameX)){ nodeX.setAttribute("ui.class", "defaultNode, finalNode"); }
            else if (this.initialStates.contains(nodeNameX)){ InitialStateArrow.attachToNode(nodeNameX); }
            else if (nodeNameX==AbstractAutomata.DEAD_NODE__NAME) {nodeX.setAttribute("ui.class", "deadNode");}
            nodeX.setAttribute("ui.label", nodeNameX);
        }    
        
        
        final int numEdges = this.graphStream.getEdgeCount();
        for (int i=0;i<numEdges;i++){
            Edge edgeX = this.graphStream.getEdge(i);
            //edgeX.setAttribute("ui.style", EDGE__DEFAULT_STYLE);
            
            edgeX.setAttribute("ui.class", "defaultEdge");
            edgeX.setAttribute("ui.label", edgeX.getAttribute("command"));
            if(edgeX.toString().contains(DEAD_NODE__NAME)){edgeX.setAttribute("ui.class","defaultEdge, dottedEdge");}
                   
        }    
    }
    
    
    /* public boolean checkInput(String inputString) {
        
        char[ ] commands = inputString.toCharArray();      
        final String initialState = this.initialStates.iterator().next();    
        String stateX = initialState; //int estado = 0 ; //El estado inicial es el 0
        
        for(int i=0; i<commands.length; i++) {     
            stateX = getNextState(stateX,commands[i]);
        }
        return isFinalState(stateX);
    }
    */

    /*
    public class TransicionAFD {
        private String initialState,finalState;
        private char command;
        public TransicionAFD(String initialState, char command, String nextState){
            this.initialState = initialState;
            this.finalState = nextState;
            this.command = command;           
        }
     
        public String getInitialState() { return this.initialState; }
        public String getFinalState() {return this.finalState; }
        public char getCommand(){ return this.command; }     
    }
    */
    /*
    private class TransicionAFD extends Transaction{
        
        public TransicionAFD(String initialState, Character command,String finalState) {
            HashSet<String> finalStates = new HashSet<String>(1);
            finalStates.add(finalState);
            super.init(initialState, command, finalStates);
        }
        
        
        // UTIL
        public String getFinalState(){
            return super.getFinalStates().iterator().next();
        }
  
        
        
    }
    */
            
    
    public class DataWrapper{
        private String[] statesList, initialStates, finalStates;
        public DataWrapper(String[] statesList, String[] initialStates, String[] finalStates){
           this.statesList = statesList; 
           this.initialStates = initialStates;
           this.finalStates = finalStates;
        }
        
        
    }
    
    
}
