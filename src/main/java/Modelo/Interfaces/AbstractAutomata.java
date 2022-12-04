/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package Modelo.Interfaces;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.logging.Level;
import java.util.logging.Logger;



//TODO: crear de forma autmatica el estado "Muerto"
/**
 * Consist in a list of {@code Initial} ({@link #initialStates}) and {@code Final} ({@link #finalStates}) States (every "State" is represented by a Node).
 * The states are connected by a {@link Transaction} and, in order to travel between states, you'll need to "consume" an specific {@code command}; a key.
 * The {@code Automata} consist in closed-loop ({@link #validate(java.lang.String)}) that iterates througth states depending on the {@code command} of each iteration (witch are taken from an inputString; as it characters).
 * If it manage to end (every command is consummed) in a {@code Final} state, the input is considered {@code VALID}; {@code INVALID} otherwise. 
 * Whats more, the first state is always an {@code Initial}' state and it keeps triying every combination/path till any of them result in a {@code VALID} end.
 * (At the end if none path is {@code VALID} the result is {@code INVALID}).
 * 
 * NOTE: In order to extend this class you'll need to {@code Override} the abstract method: {@link #validate(java.lang.String)} where the validation takes part.
 * 
 * @author Jtorr
 * @see Transaction
 * @see #validate(java.lang.String) 
 */

public abstract class AbstractAutomata {
    public static final String DEAD_NODE__NAME = "DeadNode";
    private HashSet<String> initialStates;
    private HashSet<String> finalStates;           //Is never returned; you can only check if an state is "Final" or not. 
    private HashSet<Transaction> transactionsList; // Also works as a wrapper of every state.
    
    
    /**
    * Stores the {@code step by step} operations that take part in the {@code validation} process ({@link #validate(java.lang.String) }) of the {@code Automata}.
    * Is used for graphical purpouses.
    * @see #addOperation(java.lang.String) 
    * @see org.graphstream
    */
    private String operationsList_str = "";
    public static final String OPERATION_CODE__COMMAND_INVALID = "<CX>";
    public static final String OPERATION_CODE__FINAL_NODE_INVALID = "<BX>";
    public static final String OPERATION_CODE__FINAL_NODE_VALID = "<VALID>";

  

     
    /**
     * Base constructor that inits it data structures ({@code HashSet<String>}).
     */
    public AbstractAutomata(){
        this.initialStates = new HashSet<String>();
        this.finalStates = new HashSet<String>();
        this.transactionsList = new HashSet<Transaction>();            
    }
    
    /**
     * ADDs a new "Initial" state.
     * 
     * @param initialStateX: The starting Nodes of the "validation".
     * @see #validate(java.lang.String) 
     */
    public void addInitialState(String initialStateX){ this.initialStates.add(initialStateX); }
    /**
     * ADDs a new "Final" state.
     * 
     * @param finalStateX : If the "Validation" ends in this Node, the input is "VALID"; "INVALID" otherwise.
     * @see #validate(java.lang.String) 
     * @see #isFinalState(java.lang.String) 
     */
    public void addFinalState(String finalStateX){ this.finalStates.add(finalStateX); }
  
    
    
    /**
     * GETs the list of "Initial" states.
     * 
     * @return A list with the previously added "Initial" states.
     * @see #addInitialState(java.lang.String)
     */
    public HashSet<String> getInitialStates(){
        try {if(this.initialStates.isEmpty()){ throw new Exception("Error: No 'initialStated' found!"); }} 
        catch (Exception ex) { Logger.getLogger(AbstractAutomata.class.getName()).log(Level.SEVERE, null, ex); }
        
        return (HashSet<String>) this.initialStates.clone(); 
    
    }

    
    /**
     * Create and adds a {@code new} {@link Transaction}. In case already exists a {@link Transaction} with the given param 'command': merges the 'nextStates' to the {@link Transaction#finalStates}.  
     * (IMPORTANT: This method has set as 'protected' since the 'AutomataDeterminista' shoudnt use it (becouse it can have multiple 'nextStates'; but only one)).
     *
     * @param initialState {@link Transaction#initialState}
     * @param command {@link Transaction#command}
     * @param nextStates {@link Transaction#finalStates}
     * @return {@code false} if the transaction already exists; {@code true} otherwise.
     * @see Transaction#Transaction(java.lang.String, java.lang.Character, java.util.HashSet) 
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
     * Create and adds a {@code new} {@link Transaction}. In case already exists a {@link Transaction} with the given {@link Transaction#command}: merges the {@code nextState} param to its {@link Transaction#finalStates}.  
     * (Specific implementation of {@link #addTransaction(java.lang.String, java.lang.Character, java.util.HashSet)}).
     * 
     * @param initialState {@link Transaction#initialState}
     * @param command {@link Transaction#command}
     * @param nextState {@link Transaction#finalStates}
     * 
     * @return {@code false} if the transaction already exists; {@code true} otherwise.
     * @see #addTransaction(java.lang.String, java.lang.Character, java.util.HashSet) 
     */
    public boolean addTransaction(String initialState, Character command, String nextState){
        HashSet<String> nextStates = new HashSet<String>();
        nextStates.add(nextState);
        
        return this.addTransaction(initialState, command, nextStates);  
    }
   
    
     
    /**
     * GETs the list of {@link Transaction}s.
     * 
     * @return A list with the previously added {@link Transaction}s.
     * @see #addTransaction(java.lang.String, java.lang.Character, java.lang.String) 
     * @see #addTransaction(java.lang.String, java.lang.Character, java.util.HashSet) 
     */
    protected HashSet<Transaction> getTransactionsList(){
         try {if(this.transactionsList.isEmpty()){ throw new Exception("Error: No 'transaction' found!"); }} 
        catch (Exception ex) { Logger.getLogger(AbstractAutomata.class.getName()).log(Level.SEVERE, null, ex); }
        
        
        return this.transactionsList;
    }
    /**
     * GETs a {@link Transaction} that has given {@code initialState} and {@code command}. 
     * NOTE: Can only exist zero or one.
     * 
     * @param initialState A {@link Transaction#initialState}
     * @param command A {@link Transaction#command}
     * @return A previously added {@link Transaction} that match the given parameters ({@link Transaction#initialState} and  {@link Transaction#command}) or null if it does not exist.
     * @see #addTransaction(java.lang.String, java.lang.Character, java.util.HashSet) 
     * @see #addTransaction(java.lang.String, java.lang.Character, java.lang.String) 
     */
    private Transaction getTransaction(String initialState, Character command){   
        Transaction foundTransaction = null;
         for (Transaction transactionX : this.transactionsList){
            System.out.println("initialState: "+initialState);
            System.out.println("command: "+command);

            if (transactionX.getInitialState().equals(initialState) && ((transactionX.getCommand()==null && command ==null) || (transactionX.getCommand()!=null && transactionX.getCommand().equals(command)))){
                foundTransaction = transactionX; break;
            }
        }
         
         return foundTransaction;   
    }
    /**
     * GETs the {@link Transaction#finalStates} of the {@link Transaction} that has given {@code initialState} and {@code command}. 
     * (Formed by the two methods: {@link #getTransaction(java.lang.String, java.lang.Character) } and {@link Transaction#getFinalStates()})
     * 
     * @param initialState {@link Transaction#initialState}
     * @param command {@link Transaction#command}
     * @return A {@link Transaction#finalStates} or null if the {@link Transaction} does not exist.
     * @see Transaction#getFinalStates() 
     * @see #getTransaction(java.lang.String, java.lang.Character) 
     */
    protected HashSet<String> getNextStates(String initialState, Character command){
        HashSet<String> nextSates = null;    
        Transaction foundTransaction = this.getTransaction(initialState, command);
        if (foundTransaction!=null){ nextSates = foundTransaction.getFinalStates(); }
       
        return nextSates;
    }
    


    /**
     * Checks if an {@code state} is contained in the {@link #finalStates} list.
     * 
     * @param state 
     * @return {@code true} if the {@code state} is contained in the previously saved {@link #finalStates} list; {@code fakse} otherwise.
     * @see #addFinalState(java.lang.String) 
     */
    public boolean isFinalState(String state){
        try {
            if(this.finalStates.isEmpty()){ throw new Exception("Error: No 'finalStates' found!"); }
        } 
        catch (Exception ex) { Logger.getLogger(AbstractAutomata.class.getName()).log(Level.SEVERE, null, ex); }
        
        boolean isFinalState = this.finalStates.contains(state);
        return isFinalState;
    }
    
    
    // Acepta/Rechaza un string pasado como 
    // sup.: Solo existe un unico 'initialState'; TODO: que puedan existir mas
    
    /**
     * GETs if the given {@code input} (String) is {@code VALID} or {@code INVALID}.
     * Is the public method that runs the abstract method: {@link #validate(java.lang.String)} .
     * 
     * @param inputString Every character is used as a {@link Transaction#command}.
     * @return {@code true} if the {@code validation} proccess ends in one of the {@link #finalStates}; {@code false} otherwise.
     * @see #isFinalState(java.lang.String) 
     */
    public boolean runAutomata(String inputString){
        
        System.out.println("-----------------------------------");
        System.out.println("-----------------------------------");
        System.out.println("            VERIFICATION\n");
        System.out.println(" --> Input: "+ inputString);
        System.out.println("-----------------------------------");
        System.out.println("-----------------------------------\n");
        
        
        // Check is valid
        boolean isValid = validate(inputString);
        
        return isValid;
    }  
    /**
     * [{@code ABSTRACT METHOD}]
     * 
     * @param inputString Every character is used as a {@link Transaction#command}.
     * @return {@code true} if the {@code validation} proccess ends in one of the {@link #finalStates}; {@code false} otherwise.
     */
    protected abstract boolean validate(String inputString);

    
    // TODO: Operation-Format (string)
    /*
        Having 2 nodes: nodeA (startingNode) and nodeB (endingNode):
            ---> CheckAnimation (spin-carging symbol) over the currentNode (where the verification is aplying) + cross/tick if invalid/valid

    From NextStateFound (currentNode:=startingState):
        YES:
            startingState + command + endingState
        NO:
            startingState + [INVALID]
    EndReached, Is finalNode (currentNode:=endingNode):
        YES:
            finalNode + [VALID]
        NO:
            finalNode + [INVALID]
    EJ: (given in the Practica1): 
   
    */
    // TODO2: Implement that format in a way the 'GraphStream' can dynamically represent those operations.
    
    
    
    // For Graphical Purpouses (GraphStream)
    /**
     * ADDs a {@code new} operation to the {@link #operationsList_str}.
     * Must be implemented in the {@code Abstact Method} ({@link #validate(java.lang.String)}) in order to know it {@code step by step} workflow.
     * 
     * @param operationCode Every {@code step by step} operation in a string format
     * @see #operationsList_str
     * @see #OPERATION_CODE__COMMAND_INVALID
     * @see #OPERATION_CODE__FINAL_NODE_INVALID
     * @see #OPERATION_CODE__FINAL_NODE_VALID
     */
    protected void addOperation(String operationCode){
        //System.out.print(operationCode);
        operationsList_str+=operationCode.trim()+" ";
    }
    /**
     * GETs the {@link #operationsList_str} in a {@code ArrayList<String>} format, finally {@code pop}'s (clear/reset) the {@link #operationsList_str} variable for future {@code validations} ({@link #runAutomata(java.lang.String) }).
     * 
     * @return A list of every {@code operation} made during the {@link #validate(java.lang.String) } method.
     * @see #addOperation(java.lang.String) 
     * @see #operationsList_str
     * @see #validate(java.lang.String) 
     * @see #runAutomata(java.lang.String) 
     */
    public ArrayList<String> popOperationsList(){
        final String SPACE_REGREX = "(?= )";
        //ArrayList<String> clonedList = (ArrayList<String>) this.operationsList.clone();
        String[] operationsArray = this.operationsList_str.trim().split(SPACE_REGREX);
        ArrayList<String> operationsList = new ArrayList<String>();
        for(String operationX : operationsArray){
            operationsList.add(operationX.trim());
        }
        
        //this.operationsList.clear();
        this.operationsList_str = "";
        
        return operationsList;
    }  
    /**
     * [READ ONLY] Only for graphics purpouses
     * 
     * @see Controlador.ControladorAutomata
     * @return {@link #transactionsList}
     */
    public final HashSet<Transaction> getTransactions(){ return this.transactionsList; }
    /**
     * [READ ONLY] Only for graphics purpouses
     * 
     * @see Controlador.ControladorAutomata
     * @return {@link #finalStates}
     */
    public final HashSet<String> getFinalStates(){ return this.finalStates; }
}
