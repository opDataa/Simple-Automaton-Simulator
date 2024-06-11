/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package Modelo;

import Modelo.Interfaces.AbstractAutomata;
import Modelo.Interfaces.Transaction;
import java.util.Collections;
import java.util.Comparator;
import java.util.HashSet;
import java.util.LinkedList;

/**
 * Represents an 'Automata Final No Determinista' (AFND)
 * 
 * NOTE: In order to implement the lambdaTransactions, we'll difference them from the rest using 'null' as {@link Transaction#command}.
 * 
 * @author Jtorr
 */
public class AutomataNoDeterminista extends AbstractAutomata {
    
    /**
     * Add support to have lambdaTransacions.
     */
    private HashSet<Transaction> lambdaTransactions;

    /**
     * Constructor
     */
    public AutomataNoDeterminista(){
        super();
        this.lambdaTransactions = new HashSet<Transaction>();
    }

    

        
    // UTIL --------------------------------------------------------------------
    /**
     * Stores the 'lambdaTransactions' ((@link Transaction#command)==null) at the start of {@link #validate(java.lang.String) }
     * (Removes the previously storedData)
     */
    private void saveLambdaTransactions(){
        this.lambdaTransactions.clear(); // Clears the previously storedData
        
        for(Transaction transactionX : super.getTransactionsList()){
            if(transactionX.getCommand()==null){ this.lambdaTransactions.add(transactionX); }
        }         
    }
   

    // SETTERs ----------------------------------------------------------------- 
    /**
     * Adds a transaction with the {@link Transaction#command} as {@code null} (and multiple final states)
     * 
     * @param initialState One {@link Transaction#initialState}
     * @param finalStates A list of {@link Transaction#finalStates}
     */
    public void addLambdaTransaction(String initialState, HashSet<String> finalStates) { super.addTransaction(initialState, null, finalStates); }
    /**
     * Adds a transaction with the {@link Transaction#command} as {@code null}.
     * 
     * @param initialState One {@link Transaction#initialState}
     * @param finalState One {@link Transaction#finalStates}
     */
    public void addLambdaTransaction(String initialState, String finalState) { super.addTransaction(initialState, null, finalState); }
    
    /**
     * Overrided method of {@link AbstractAutomata#addTransaction(java.lang.String, java.lang.Character, java.util.HashSet)}
     * NOTE: the difference here is that now is a public method.
     * 
     * @param initialState {@link Transaction#initialState}
     * @param command {@link Transaction#command}
     * @param nextStates {@link Transaction#finalStates}
     * @return {@code false} if the transaction already exists; {@code true} otherwise.
     * @see Transaction#Transaction(java.lang.String, java.lang.Character, java.util.HashSet) 
     */
    @Override
    public boolean addTransaction(String initialState, Character command, HashSet<String> nextStates){ return super.addTransaction(initialState, command, nextStates); }
   
 
    /**
     * Adds every lambda state that is related with any of the states given as parameters (in a list).
     * It does no return anything since the given list parameter keeps the added states
     * 
     * @see #lambdaTransactions
     * @param statesList a list of states to extract its related lambdaStates
     */
    protected void addLamdaStates(HashSet<String> statesList){
                
        boolean foundNewInitialStates = true;
        do{
            foundNewInitialStates = false;
        
            for(Transaction lambdaTransaction : this.lambdaTransactions){
                if (statesList.contains(lambdaTransaction.getInitialState())){
                   
                    for (String lambdaState : lambdaTransaction.getFinalStates()){
                        if (statesList.contains(lambdaState)==false){
                            statesList.add(lambdaState);
                            foundNewInitialStates = true;
                        }                        
                    }                  
                }            
            }         
        }
        while(foundNewInitialStates==true);        
    }
    @Override
    protected boolean validate(String inputString) {
        this.saveLambdaTransactions();
        boolean isValid= false;
        char[ ] commands = inputString.toCharArray();
        HashSet<String> initialStates =this.getInitialStates();
                
        for(String initialStateX : initialStates){
            isValid = this.validateRecursive(initialStateX, inputString);      
            if(isValid==true){break;}
        }
        
        
        // May redundant; but sometimes the 'INVALID' icon not shows
       if(isValid==true){
           super.addOperation(OPERATION_CODE__FINAL_NODE_VALID);       // [VALID]
        }
        else{
           super.addOperation(OPERATION_CODE__FINAL_NODE_INVALID);
       }  // {INVALID}
      
        return isValid;
    }
    /**
     * 
     * @param initialState
     * @param partialTextImput
     * @return 
     */
    private boolean validateRecursive(String initialState, String partialTextImput){
        final int INPUT_LENGTH = partialTextImput.length(); // <<see: 'updateFinalSequence'>>
        String commandsSequence = "";                       // <<see: 'updateFinalSequence'>>
              
        boolean isValid = false;
        
        HashSet<String> initialStates = new HashSet<String>();
        initialStates.add(initialState);
        this.addLamdaStates(initialStates);

        
        LinkedList<String> initialStatesSorted = new LinkedList<String>(initialStates);
        initialStatesSorted.remove(initialState);
        initialStatesSorted.addFirst(initialState);
        System.out.println("Initial_States: "+ initialStatesSorted.toString());

        if (partialTextImput.length()>0){
            Character command = partialTextImput.charAt(0);
            
           
            int j=0;
            for(String initialStateX : initialStatesSorted){
                j++;
              // A [C] B
                
                // Si initialStatesSorted.size()>1 := [initialState, lambdaStatesList]
                // Si la iteracion actual es un 'lambdaState', se pone el 'padre'.
                if(initialState.equals(initialStateX)==false){
                    super.addOperation(initialState); // {A}                    
                }
                    
                super.addOperation(initialStateX); // A

                
                
                // List of States to travel from the 'initialStateX' using the current 'command' (the first char of the given string: 'partialTextImput')
                HashSet<String> finalStates = this.getNextStates(initialStateX, partialTextImput.charAt(0));

                
                // If with that 'command' there is no "next" state  
                if (finalStates==null || finalStates.size()==0){
                    super.addOperation(OPERATION_CODE__COMMAND_INVALID); // {CX}
                            
                    // Siempre que vuelve de un LambdaNode a su initialNode
                    /*if(initialState.equals(initialStateX)==false){
                        System.out.println(initialState+"!="+initialStateX);
                        super.addOperation(initialState);//super.addOperation("["+command+"]"); // A         (see: <showCurrentOperation()>)
                    }
                    */
                }
                else{
                    
                    
                    // Special case: lambdaTransactions points directly to the final node.
                    LinkedList<String> finalStatesSorted = new LinkedList<String>(finalStates);
                    Collections.sort(finalStatesSorted, new SortByFinalState()); // Sort: first the nodes that are not 'finalState'
                    System.out.println(initialStateX+" => [FinalStates]"+ finalStatesSorted.toString());

                    
                    //super.addOperation("["+command+"]"); // [C]
                    int i = 0;

                    
                    for(String finalStateX : finalStatesSorted){
                                                i++;
                        super.addOperation_transactionCommand(command);//super.addOperation("["+command+"]"); // [C]

                        isValid = this.validateRecursive(finalStateX, partialTextImput.substring(1));
                        System.out.println("[FinalState] "+ finalStateX+((isValid)? " VALID":" NOPE"));

                        
                        if (isValid==true){ 
                            //[EJ] EjemploPropio_2_1 (input: '0') ==> finalState debería de ser 'C'
                            
                            
                            
                            super.updateFinalSolution(INPUT_LENGTH,initialStateX+" "+"["+command+"]"+" "+finalStateX); // TODO: VER SI FUNCIONA BIEN ?? ASDF
                            break; 
                        }
                        else{ 
                            // Marca el inicio de un nuevo ciclo tras un 'comeback'.
                            if(i<finalStates.size()){ // Al ser la ultima iteracion no existe dicho 'comeback'.
                                super.addOperation(initialStateX);//super.addOperation("["+command+"]"); // B        (see: <showCurrentOperation()>)
                                //super.addOperation(initialState);//super.addOperation("["+command+"]"); // A         (see: <showCurrentOperation()>)
                            }
                        }
                        
                    }
                }
               
                
                if(isValid){ break; }
                else{
                   // super.addOperation(OPERATION_CODE__FINAL_NODE_INVALID);   // {INVALID}
                    

//                                            super.addOperation(OPERATION_CODE__FINAL_NODE_INVALID);   // {INVALID}

                    if(j<initialStatesSorted.size()){ // Al ser la ultima iteracion no existe dicho 'comeback'.
                       super.addOperation(OPERATION_CODE__FINAL_NODE_INVALID);   // {INVALID}
                       //super.addOperation(initialState);//super.addOperation("["+command+"]"); // B        (see: <showCurrentOperation()>)
                    }
                }
                
            }
            
            
            if(isValid==false){
                super.addOperation(OPERATION_CODE__FINAL_NODE_INVALID);   // {INVALID}

            }
        }
        else{
            for(String initialStateX : initialStatesSorted){
                super.addOperation(initialStateX); // B
          
                isValid = this.isFinalState(initialStateX);
                
                
                // Special case: lambdaTransactions points directly to the final node.  [EJ] 'ejemploPropio_1' , input: '0' .
                if(isValid==false){
                    HashSet<String> lambdaStates = new HashSet<String>() {{add(initialStateX);}}; // (fromInternet) [SOURCE] https://stackoverflow.com/questions/1005073/initialization-of-an-arraylist-in-one-line
                    this.addLamdaStates(lambdaStates);
                    lambdaStates.remove(initialStateX);
                 
                    String veryFinalState = "";
                    for(String lambdaStateX : lambdaStates){
                        isValid = super.isFinalState(lambdaStateX);
                        
                        if(isValid){ veryFinalState = lambdaStateX; break;}                      
                    }
                    
                    if(isValid){
                        super.addOperation(veryFinalState); // B'
                    }
                    
                }
                
                
                if(isValid==true){
                    
                    super.addOperation(OPERATION_CODE__FINAL_NODE_VALID);       // [VALID]
                    
                    break; 
                }
                else{
                    super.addOperation(OPERATION_CODE__FINAL_NODE_INVALID); 
                }  // {INVALID}

            }
        }
        
        
        return isValid;
    }


    
    /**
     * Comparator class.
     * @see #validateRecursive(java.lang.String, java.lang.String) 
     */
    class SortByFinalState implements Comparator<String> {

        // Method
        // Sorting in ascending order of name
        public int compare(String nodeA, String nodeB)
        {
            String nodeA_isFinalState = (isFinalState(nodeA))? "1":"0";
            String nodeB_isFinalState = (isFinalState(nodeB))? "1":"0";

            return nodeA_isFinalState.compareTo(nodeB_isFinalState);
        }
    }
    


}
