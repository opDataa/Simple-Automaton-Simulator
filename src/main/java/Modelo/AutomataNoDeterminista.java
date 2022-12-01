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

    /**
     * Stores the 'lambdaTransactions' ((@link Transaction#command)==null) at the start of {@link #validate(java.lang.String) }
     * (Removed the previously storedData)
     */
    private void saveLambdaTransactions(){
        this.lambdaTransactions.clear(); // Clears the previously storedData
        
        HashSet<Transaction> lambdaTransactions = new HashSet<Transaction>();
        for(Transaction transactionX : super.getTransactionsList()){
            if(transactionX.getCommand()==null){ lambdaTransactions.add(transactionX); }
        }         
    }
    
  
    @Override
    public boolean addTransaction(String initialState, Character command, HashSet<String> nextStates){ return super.addTransaction(initialState, command, nextStates); }

 
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
        

    // LAMDA TRANSACTIONS
    public void addLambdaTransaction(String initialState, HashSet<String> finalStates) {
        super.addTransaction(initialState, null, finalStates);
    }
    public void addLambdaTransaction(String initialState, String finalState) {
        super.addTransaction(initialState, null, finalState);
    }


    /**
     * Adds 'lambdaTransaction' support 
     * 
     * @see #addLamdaStates(java.util.HashSet) 
     * @return 
     */
    @Override
    protected HashSet<String> getInitialStates(){
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
        this.addLamdaStates(initialStates);
       
        return initialStates;
    }
    
    
   /**
     * Adds the lambdaStates 
     * 
     * @see #lambdaTransactions
     * @param initialStates 
     */
    protected void addLamdaStates(HashSet<String> statesList){
                
        boolean foundNewInitialStates;
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

    
    
    public AutomataNoDeterminista(){
        super();
        this.lambdaTransactions = new HashSet<Transaction>();
    }

   
}
