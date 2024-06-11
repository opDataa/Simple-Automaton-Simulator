/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package Modelo.Interfaces;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.logging.Level;
import java.util.logging.Logger;





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
public abstract class AbstractAutomata{
    
    /**
     * The list of initial states (ReadOnly).
     * @see #getInitialStates() 
     */
    private HashSet<String> initialStates;
    /**
     * The list of final states (ReadOnly).
     * 
     * @see #getFinalStates() 
     * @see #isFinalState(java.lang.String)
     */
    private HashSet<String> finalStates;           //Is never returned; you can only check if an state is "Final" or not.  
    /**
     * The list of {@link Transaction}'s (readOnly)
     * 
     * @see #getTransactionsList() 
     */
    private HashSet<Transaction> transactionsList; // Also works as a wrapper of every state.
    
     
    /**
     * Base constructor that inits it data structures ({@code HashSet<String>}).
     */
    public AbstractAutomata(){
        // Basic data structures
        this.initialStates = new HashSet<String>();
        this.finalStates = new HashSet<String>();
        this.transactionsList = new HashSet<Transaction>();    

        // Adds the 'step by step' support by saving the operations (travel throught states) of the 'VALID' iteration.
        this.finalSolutionDict = new HashMap<Integer, String>();        
    }
    
      
    // KORE --------------------------------------------------------------------
    /**
     * CHECKs if the given {@code input} (String) is {@code VALID} or {@code INVALID}.
     * Is the public method that runs the abstract method: {@link #validate(java.lang.String)} .
     * 
     * @param inputString Every character is used as a {@link Transaction#command}.
     * @return {@code true} if the {@code validation} proccess ends in one of the {@link #finalStates}; {@code false} otherwise.
     * @see #isFinalState(java.lang.String) 
     */
    public boolean runAutomata(String inputString){
        
        //if(this.finalSolutionDict==null){  this.finalSolutionDict = new HashMap<Integer, String>(); }
        this.finalSolutionDict.clear();
        
        System.out.println("-----------------------------------");
        System.out.println("-----------------------------------");
        System.out.println("            VERIFICATION\n");
        System.out.println(" --> Input: "+ inputString);
        System.out.println("-----------------------------------");
        System.out.println("-----------------------------------\n");
        
        
        // Check is valid
        boolean isValid = validate(inputString);
        
        // Clears the solutionDict if the 'inputString' is 'INVALID' (because its data will be considered rubish/waste)
        if(isValid==false){ this.finalSolutionDict.clear(); }
        
        System.out.println("[SOLUTION] "+ this.operationsList_str);
        return isValid;
    }  
    /**
     * [{@code ABSTRACT METHOD}]
     * 
     * @param inputString Every character is used as a {@link Transaction#command}.
     * @return {@code true} if the {@code validation} proccess ends in one of the {@link #finalStates}; {@code false} otherwise.
     */
    protected abstract boolean validate(String inputString);    
    
    // SETTERs -----------------------------------------------------------------    
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
    
    // GETTERs -----------------------------------------------------------------    
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
            // System.out.println("initialState: "+initialState);
            // System.out.println("command: "+command);

            if (transactionX.getInitialState().equals(initialState) && ((transactionX.getCommand()==null && command ==null) || (transactionX.getCommand()!=null && transactionX.getCommand().equals(command)))){
                foundTransaction = transactionX; break;
            }
        }
         
         return foundTransaction;   
    }
 
       
    // EXTRA -------------------------------------------------------------------
    /**
     * If a state have that name, it will be coloured in a different way.
     * 
     * @see Controlador.ControladorAutomata#setStyle_Nodes() 
     */
    public static final String DEAD_NODE__NAME = "MX"; 
  
    
    
    // =========================================================================
    // =============================[ STEP_BY_STEP ]============================
    // =============================[     SAVE     ]============================
    // =========================================================================
    
    // Step by Step 'Iteration-Language':
    /**
     * The {@code code} that represents the situation when: there is no {@link Transaction} from the current state and with the current {@link Transaction#command}.
     */
    public static final String OPERATION_CODE__COMMAND_INVALID = "<CX>"; 
    /**
     * The {@code code} that represents the situation when: the current state is not final.
     */
    public static final String OPERATION_CODE__FINAL_NODE_INVALID = "<BX>";
    /**
     * The {@code code} that represents the situation when: the given {@code textInput} from {@link #runAutomata(java.lang.String)} is considered {@code VALID}.
     */
    public static final String OPERATION_CODE__FINAL_NODE_VALID = "<VALID>";

    
    /**
    * Stores the {@code step by step} operations that take part in the {@code validation} process ({@link #validate(java.lang.String) }) of the {@code Automata}.
    * Is used for graphical purpouses.
    * @see #addOperation(java.lang.String) 
    * @see org.graphstream
    */
    private String operationsList_str = "";
    /**
    * Stores the final solution of the {<@code step by step} iterations of the {@link AbstractAutomata} throught states (and {@link Transaction}).
    * 
    * [FORMAT] <numCharsOnIteration, sequenceOfCommands>
    * numCharsOnIteration := 'seudoStepIndex' pero inverso. ; Si el stringInput.length==2 = ("137") => finalSolution =   <3,strA="1"> , <2,strB="37">, <1,strC="">.
    *
    * In order to have a clean sololution, for each step (where the 'step' is repesented by the number of characters that lasts for each time the {@link #validate(java.lang.String) is executed),
    * the last travel throught states replaces the before one (because the last travel is supposed to be the valid one, if it exist).
    * NOTE: Later, the 'stepIndex' is reversed.
    * 
    */
    private HashMap<Integer, String> finalSolutionDict = null;        

    
    // SETTERs -----------------------------------------------------------------
    /**
     * UPDATEs the {@link #finalSolutionDict} {@code <key,value>}} (diccionary).
     * @see #finalSolutionDict
     * @param stepIndex The {@code <key>} of the {@link #finalSolutionDict} dictionary
     * @param commandsSequence The {@code <value>} of the {@link #finalSolutionDict} dictionary
     */
    protected void updateFinalSolution(int stepIndex,String commandsSequence){ this.finalSolutionDict.put(stepIndex, commandsSequence); } 
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
    protected void addOperation(String operationCode){ this.operationsList_str+=operationCode.trim()+" "; System.out.println("+ "+operationCode);}
    /**
     * SAVEs a {@link Transaction#command} in a specific format (between square brackets)
     * 
     * @param command A {@link Transaction#command}
     * @see #addOperation(java.lang.String) 
     */
    protected void addOperation_transactionCommand(char command){ this.addOperation("["+command+"]"); }

    // GETTERs -----------------------------------------------------------------
    /**
     * Retuns the final and valid automata's iteration throught states. 
     * 
     * @return An {@code String} that represent the travel throught states or {@code null} if the given input on {@link #runAutomata(java.lang.String)} is {@code INVALID}.
     */
    public String getFinalSolution(){  // Funciona correctamente ??
        String finalSolutionSequence = null;
        
        if(this.finalSolutionDict != null){
            finalSolutionSequence = "";
            for(int i=this.finalSolutionDict.size();i>0;i--){ // i>0 porque va a existir un elemento con 'numCharsOnIteration' = 0.
                String sequenceX = this.finalSolutionDict.get(i);
                finalSolutionSequence+= sequenceX+" ";
            }
            finalSolutionSequence = finalSolutionSequence.trim();
        }
        
        return finalSolutionSequence;
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
        //System.out.println("OPERATIONS: " + this.operationsList_str);
        final String SPACE_REGREX = "(?= )";

        String[] operationsArray = this.operationsList_str.trim().split(SPACE_REGREX);
        ArrayList<String> operationsList = new ArrayList<String>();
        for(String operationX : operationsArray){
            operationsList.add(operationX.trim());
            if(operationX.contains(AbstractAutomata.OPERATION_CODE__FINAL_NODE_VALID)){break;}
        }
        
        this.operationsList_str = "";
        
        return operationsList;
    }  
   
    
    // UTILs -------------------------------------------------------------------
    /**
     * Checks if a {@code state} is contained in the {@link #finalStates} list.
     * 
     * @param state string that represents an state from the {@link #finalStates} list.
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
    /**
     * [READ ONLY] For graphics purpouses only.
     * NOTE: The {@link #getTransactionsList()} does the same, but it is {@code protected} nor {@code public}.
     * 
     * @return {@link #transactionsList}
     * @see Controlador.ControladorAutomata
     */
    public final HashSet<Transaction> getTransactions(){ return (HashSet<Transaction>) this.transactionsList.clone(); }
    /**
     * [READ ONLY] For graphics purpouses only
     * 
     * @return {@link #finalStates}
     * @see Controlador.ControladorAutomata
     */
    public final HashSet<String> getFinalStates(){ return (HashSet<String>) this.finalStates.clone(); }   
 
    

}


