/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package Modelo.Interfaces;

import java.util.HashSet;

/**
 *
 * 
 * @author Jtorr
 */
public class Transaction {
        private String initialState;
        private HashSet<String> finalStates;
        private Character command;
        /*public Transaction(String initialState, char command, String nextState){
            this.initialState = initialState;
            this.finalState = nextState;
            this.command = command;           
        }
        */
        protected Transaction(){}
        protected void init(String initialState,Character command, HashSet<String> finalStates){
            this.initialState = initialState;        
            this.finalStates = finalStates;
            this.command = command;   
        }
        protected void init(String initialState,Character command, String finalState){
            this.initialState = initialState;            
            this.finalStates = new HashSet<String>();            
            this.command = command; 
            
            this.addFinalState(finalState);
        }
        
        
        protected Transaction(String initialState,Character command, HashSet<String> finalStates){
            this.init(initialState, command, finalStates);
        }
        protected Transaction(String initialState,Character command, String finalState){
            this.init(initialState, command, finalState);
        }
        
        public String getInitialState() { return this.initialState; }
        public HashSet<String> getFinalStates() {return this.finalStates; }
        public Character getCommand(){ return this.command; }     
        
        
        public void addFinalStates(HashSet<String> finalStates){this.finalStates.addAll(finalStates);}
        public void addFinalState(String finalState){this.finalStates.add(finalState);}

}
