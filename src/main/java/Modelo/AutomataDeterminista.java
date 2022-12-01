/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package Modelo;

import Modelo.Interfaces.AbstractAutomata;
import Modelo.Interfaces.Transaction;
import java.util.HashSet;
import java.util.logging.Level;
import java.util.logging.Logger;

/**
 *
 * @author Jtorr
 */
// One initialState; One o more finalStates; UNIQUE RESTRAINT: <initialState, command>.
public class AutomataDeterminista extends AbstractAutomata{
    
    
    @Override
    protected boolean validate(String inputString) {
        char[ ] commands = inputString.toCharArray();      
        final String initialState = this.getInitialState();  
        String stateX = initialState; //int estado = 0 ; //El estado inicial es el 0
        System.out.println("inputString: "+inputString);
        System.out.print("-->"+stateX);

        
        for(int i=0; i<commands.length; i++) {     
            
            stateX = this.getNextState(stateX,commands[i]);
            
            if(stateX==null){ break; }
            else{ System.out.print(" => "+stateX); }
        }
        
        boolean isValid = isFinalState(stateX);
        if (isValid){ System.out.print(" : VALID"); }
        else{ System.out.print(" : FAIL"); }
        
        return isValid;
    }
    
    
    
    // UTIL --------------------------------------------------------------------   
    /**
     * GETs the 'initialState'. It is the first state during {@link validate}.
     * Since the {@link AutomataDeterminista} can only have one 'initialState', we return the 'first-saved-elem' from the {@link Transaction#initialState}.
     * @see AbstractAutomata#initialStates
     * @return String (never null)
     */
    private String getInitialState(){
       return super.getInitialStates().iterator().next();
    }
    /**
     * GETs the transaction 'nextState' that match with the given params.
     * Since the {@link AutomataDeterminista} can only have one 'nextState' per {@link Transaction#command}, we return the 'first-saved-elem' from the {@link Transaction#finalStates}.
     * @param initialState 
     * @param command
     * @return The first 'nextState'; null if no exist any one that match the 'command' param.
     */
    private String getNextState(String initialState,char command){
        String nextState= null;
     
        HashSet<String> nextStates = super.getNextStates(initialState, command);
        if (nextStates!=null){ nextState = nextStates.iterator().next(); }
        
        return nextState;
    }   
    
    /**
     * Throws an error in case you try to add more than one {@link Transaction} per {@link Transaction#command}.
     * @param initialState
     * @param command
     * @param nextState
     * @return 
     */
    @Override
    public boolean addTransaction(String initialState, Character command, String nextState){
        boolean alreadyExistedTransaction = super.addTransaction(initialState, command, nextState);

        if(alreadyExistedTransaction){
            try { throw new Exception("WARING: An <AutomataDeterminista> can only have one <Transaction> per <Command>! (The 'AutomataDeterminista' will only use the first-added finalState)"); } 
            catch (Exception ex) { Logger.getLogger(AutomataDeterminista.class.getName()).log(Level.SEVERE, null, ex); }
        }

        return alreadyExistedTransaction;
    }
}


    
