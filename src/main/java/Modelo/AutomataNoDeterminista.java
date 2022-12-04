/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package Modelo;

import Modelo.Interfaces.AbstractAutomata;
import Modelo.Interfaces.Transaction;
import java.util.HashSet;

/**
 * To implement lambdaTransactions, we'll difference them from the rest by using 'null' ass {@link Transaction#command}.
 * 
 * @author Jtorr
 */
public class AutomataNoDeterminista extends AbstractAutomata {
    
    private HashSet<Transaction> lambdaTransactions = null;

    
     public AutomataNoDeterminista(){
        super();
        this.lambdaTransactions = new HashSet<Transaction>();
    }

    
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
    
  
    @Override
    public boolean addTransaction(String initialState, Character command, HashSet<String> nextStates){ return super.addTransaction(initialState, command, nextStates); }
 
    /*
    @Override
    protected boolean validate(String inputString) {
        char[ ] commands = inputString.toCharArray();     
        saveLambdaTransactions();
        HashSet<String> initialStates = this.getInitialStates();
        
        boolean anyValid = false;
        for(String initialStateX : initialStates){
            if(validate(initialStateX,inputString)){ anyValid = true; break; }
        }
        
        return anyValid;
    }
    // TODO: funcion recursiva
    // TODO_2: seguimiento paso a paso con cada iteracion
    private boolean validate(String initialState, String inputString) {

        boolean isValid = false;
        char coomand = inputString.charAt(0); // Extract the first character  
        super.addOperation(initialState);    // A
        super.addOperation("["+coomand+"]");            // [C]

        inputString = inputString.substring(1); // Removes the first character
        HashSet<String> nextStates = this.getNextStates(initialState, coomand);
        
        if(nextStates!=null){
            boolean endReached = (inputString.length()==0);
           
            for(String nextStateX: nextStates){
                if(endReached){
                    if(super.isFinalState(nextStateX)){
                        isValid = true;
                        break;
                    }
                }
            }
        }
        
        
        
        return isValid;
    }
  
    */
    
    //TODO: agregar otro icono para cuando sea: <<OPERATION_CODE__COMMAND_INVALID>>
    private boolean validateX(String initialState, String partialTextImput){
        boolean isValid = false;
       
        HashSet<String> initialStates = new HashSet<String>();
        initialStates.add(initialState);
        this.addLamdaStates(initialStates);
        
        if (partialTextImput.length()>0){
            Character command = partialTextImput.charAt(0);

            for(String initialStateX : initialStates){
                
                super.addOperation(initialStateX); // A

                HashSet<String> finalStates = this.getNextStates(initialStateX, partialTextImput.charAt(0));

                if (finalStates==null || finalStates.size()==0){
                    super.addOperation(OPERATION_CODE__COMMAND_INVALID); // {CX}
                    
                    // Siempre que vuelve de un LambdaNode a su initialNode
                    if(initialState.equals(initialStateX)==false){
                        System.out.println(initialState+"!="+initialStateX);
                        super.addOperation(initialState);//super.addOperation("["+command+"]"); // A         (see: <showCurrentOperation()>)
                    }
                }
                else{
                    super.addOperation("["+command+"]"); // [C]

                    for(String finalStateX : finalStates){
                        isValid = this.validateX(finalStateX, partialTextImput.substring(1));
                        if (isValid==true){ break; }
                        else{ 
                            super.addOperation(initialStateX);//super.addOperation("["+command+"]"); // B        (see: <showCurrentOperation()>)
                            //super.addOperation(initialState);//super.addOperation("["+command+"]"); // A         (see: <showCurrentOperation()>)

                        }
                        
                    }
                }
                if(isValid){ break; }
                else{
                    super.addOperation(initialState);//super.addOperation("["+command+"]"); // B        (see: <showCurrentOperation()>)
                }
            }
        }
        else{
            for(String initialStateX : initialStates){
                super.addOperation(initialStateX); // B
          
                isValid = this.isFinalState(initialStateX);
                if(isValid==true){
                    super.addOperation(OPERATION_CODE__FINAL_NODE_VALID);       // [VALID]
                    break; 
                }
                else{super.addOperation(OPERATION_CODE__FINAL_NODE_INVALID); }  // {INVALID}

            }
        }
        
        
        
        return isValid;
    }

    
    @Override
    protected boolean validate(String inputString) {
        this.saveLambdaTransactions();
        boolean isValid= false;
        char[ ] commands = inputString.toCharArray();
        HashSet<String> initialStates =this.getInitialStates();
                
        for(String initialStateX : initialStates){
            isValid = this.validateX(initialStateX, inputString);      
            if(isValid==true){break;}
        }
        
        
        // May redundant; but sometimes the 'INVALID' icon not shows
       if(isValid==true){
           super.addOperation(OPERATION_CODE__FINAL_NODE_VALID);       // [VALID]
        }
        else{super.addOperation(OPERATION_CODE__FINAL_NODE_INVALID); }  // {INVALID}
      
        return isValid;
    }
    
    
    // LAMDA TRANSACTIONS
    public void addLambdaTransaction(String initialState, HashSet<String> finalStates) {
        super.addTransaction(initialState, null, finalStates);
    }
    public void addLambdaTransaction(String initialState, String finalState) {
        super.addTransaction(initialState, null, finalState);
    }


    /**
     * [DEPRECATED]Adds 'lambdaTransaction' support 
     *  ===> No need to add that support because the 'addLamdaStates' is enought!.
     * @see #addLamdaStates(java.util.HashSet)  
     * @return 
     */
    @Override
    public HashSet<String> getInitialStates(){
        /*
        HashSet<String> initialStates = new HashSet<String>();         
        
        boolean foundNewInitialStates;
        do{
            foundNewInitialStates = false;
        
            for(Transaction lambdaTransaction : this.lambdaTransaction){
                if (initialStates.contains(lambdaTransaction.getInitialState())){
                   
                    for (String lambdaState : lambdaTransaction.getFinalStates()){
                        if (initialStates.contains(lambdaState)==false){
                            initialStates.add(lambdaState);
                            foundNewInitialStates = true;
                        }                        
                    }                  
                }            
            }         
        }
        while(foundNewInitialStates==true);
        */
        HashSet<String> initialStates = super.getInitialStates();
        //this.addLamdaStates(initialStates);
       
        return initialStates;
    }
    
    
   /**
     * Adds the lambdaStates 
     * 
     * @see #lambdaTransactions
     * @param statesList 
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
    
    
    /**
     * Adds 'lambdaTransaction' support 
     * 
     * @see #addLamdaStates(java.util.HashSet) 
     * @param initialState
     * @param command
     * @return 
     */
    @Override
    protected HashSet<String> getNextStates(String initialState, Character command){
        HashSet<String> nextStates = super.getNextStates(initialState, command);
        if (nextStates!=null){ this.addLamdaStates(nextStates); }
        
       /*
       Transaction foundTransaction = this.getTransaction(stateX, command);
       HashSet<String> nextSates = null;
       if (foundTransaction!=null){ nextSates = foundTransaction.getFinalStates(); }
       */
       return nextStates;
    }

    
    
   
   
}
