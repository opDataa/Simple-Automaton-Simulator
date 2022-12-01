/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Project/Maven2/JavaApp/src/main/java/${packagePath}/${mainClassName}.java to edit this template
 */

package com.mycompany.automatas;

import Modelo.AutomataDeterminista;
import Modelo.Interfaces.AbstractAutomata;
import java.util.ArrayList;
import org.graphstream.graph.*;
import org.graphstream.graph.implementations.SingleGraph;

/**
 *
 * @author Jtorr
 */
public class Automatas {

    public static void main(String[] args) {
        test_AutomataDeterminista();
        //dibujaGrafos_TESTING();

    }


    public static void dibujaGrafos_TESTING(){
        System.setProperty("org.graphstream.ui", "swing");

        Graph graph = new SingleGraph("Tutorial 1");
/*
        graph.addNode("A");
        graph.addNode("B");
        graph.addNode("C");
        graph.addNode("D");
        */
        graph.setStrict(false);
        graph.setAutoCreate( true );
        graph.addEdge("AB", "A", "B", true);
        graph.addEdge("BC", "B", "C", true);
        graph.addEdge("CA", "C", "A", true);
        graph.addEdge("DA", "D", "A", true);    
        graph.addEdge("DD", "D", "D", true);

        Node A = graph.getNode("A");
        A.setAttribute("ui.style", "shape:circle;fill-color: red;size: 90px;text-size: 30px; text-alignment: center;");

        A.setAttribute("ui.label", "A");
        graph.display();
        
        
    }
    public static void test_AutomataDeterminista(){
        AutomataDeterminista a1 = new AutomataDeterminista();
      
        a1.addInitialState("q0");
        a1.addFinalState("q1");
        
        a1.addTransaction("q0", '1', "q2");
        //a1.addTransaction("q0", '1', "q7"); // debería lanzar un error porque es un 'AutomataDeterminista'.          
        a1.addTransaction("q0", '0', "q1");
        a1.addTransaction("q2", '0', "q1");
       
        
        // Estado "muerto" (M)
        a1.addTransaction("q2", '1', AbstractAutomata.DEAD_NODE__NAME);     
        a1.addTransaction("q1", '1', AbstractAutomata.DEAD_NODE__NAME);
        
        // TODO[VISUAL-BUG]: los 'Edge's reflexivos (aquellos que apuntan sobre uno mismo) no muestran su nombre ('command') !!.
        a1.addTransaction(AbstractAutomata.DEAD_NODE__NAME, '1', AbstractAutomata.DEAD_NODE__NAME);
        // TODO[SYSTEM-BUG]: por alguna razon no me deja hacer que de un mismo nodo partan mas de un nexo ('Edge') hacia un mismo Nodo!
        //a1.addTransaction("q1", '0', AbstractAutomata.DEAD_NODE__NAME);
        /*
        a1.addTransaction("q2", '1', "M");     
        a1.addTransaction("q1", '1', "M");
        a1.addTransaction("q1", '0', "M");
        a1.addTransaction("M", '0', "M");
        a1.addTransaction("M", '1', "M");
        */
        
        // validSrings: "0" , "10"
        ArrayList<String> listX=  new ArrayList<String>();
        listX.add("0");
        //listX.add("10");
        //listX.add("1");
        //listX.add("101");
        //listX.add("100");
        //listX.add("01");

        

        
        for(String strX: listX){
            boolean isValid = a1.runAutomata(strX);
            System.out.println(strX+((isValid)? ": VALID":": INVALID"));
        }
    }
}
