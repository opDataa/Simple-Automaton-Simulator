/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package Modelo.Interfaces;

import java.util.HashSet;

/**
 * Transaction implementation that represents the move between states of an {@link AbstractAutomata}.
 * 
 * @author Jtorr
 */
public class Transaction {
        
    /**
     * The initial state
     */
    private String initialState;
    /**
     * The list of final statates
     */
    private HashSet<String> finalStates;
    /**
     * The command or symbol that an automata needs to move between states.
     */
    private Character command;

    /**
     * Empty constructor.
     */
    protected Transaction(){}
    /**
     * Class Constructor that inits a Transaction with more than one final state.
     * 
     * @param initialState the initial state
     * @param command the command or symbol to travel through states
     * @param finalStates the list of final states
     */
    protected Transaction(String initialState,Character command, HashSet<String> finalStates){
        this.init(initialState, command, finalStates);
    }
    /**
     * Class Constructor that inits a Transaction with only one final state.
     * 
     * @param initialState the initial state
     * @param command the command or symbol to travel through states
     * @param finalState the final states
     */
    protected Transaction(String initialState,Character command, String finalState){
        this.init(initialState, command, finalState);
    }

    /**
     * Inits a Transaction with more than one final state.
     * @param initialState the initial state
     * @param command the command or symbol to travel through states
     * @param finalStates the list of final states
     */
    protected void init(String initialState,Character command, HashSet<String> finalStates){
        this.initialState = initialState;        
        this.finalStates = finalStates;
        this.command = command;   
    }
    /**
     * Inits a Transaction with only one final state.
     * @param initialState the initial state
     * @param command the command or symbol to travel through states
     * @param finalState the final states
     */
    protected void init(String initialState,Character command, String finalState){
        this.initialState = initialState;            
        this.finalStates = new HashSet<String>();            
        this.command = command; 

        this.addFinalState(finalState);
    }


    /**
     * GETs the initial state
     * @return the initial state
     */
    public String getInitialState() { return this.initialState; }
    /**
     * GETs the list of final states
     * @return the list of final states
     */
    public HashSet<String> getFinalStates() {return (HashSet<String>) this.finalStates.clone(); }
    /**
     * GETs the command of the transaction that lets an automata to travel through states
     * @return the command of the transaction that lets an automata to travel through states
     */
    public Character getCommand(){ return this.command; }     


    /**
     * ADDs more final states (but not replaces the previous list)
     * @param finalStates a list of final states 
     */
    public void addFinalStates(HashSet<String> finalStates){this.finalStates.addAll(finalStates);}
    /**
     * ADDs one more final state.
     * NOTE: this can be considered the unitary implementation of {@link #addFinalStates(java.util.HashSet) }
     * 
     * @param finalState a final state
     */
    public void addFinalState(String finalState){this.finalStates.add(finalState);}


    /**
     * ToString method in the following format:
     *  {@link #initialState} =={{@link #command}]=> {@link #finalStates}.
     * 
     * @return the {@link Transaction} parsed to string
     */
    @Override
    public String toString(){
       String finalStates_str = "";
       for(String finalStateX : this.finalStates){finalStates_str += finalStateX+" ";}

       return this.initialState+" ==[ "+ this.command+" ]=> "+finalStates_str;
    }



}
