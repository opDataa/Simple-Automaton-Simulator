/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Project/Maven2/JavaApp/src/main/java/${packagePath}/${mainClassName}.java to edit this template
 */

package com.mycompany.automatas;

import Controlador.ControladorAutomata;
import Modelo.AutomataDeterminista;
import Modelo.Interfaces.AbstractAutomata;
import java.util.ArrayList;
import org.graphstream.graph.*;
import org.graphstream.graph.implementations.MultiGraph;
import org.graphstream.graph.implementations.SingleGraph;

/**
 *
 * @author Jtorr
 */
public class Automatas {

    public static void main(String[] args) {
       //test_AutomataDeterminista();
        //dibujaGrafos_TESTING();
        ControladorAutomata controladorAutomata = new ControladorAutomata(); 
        //test2();
    
        //test3();
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
        //graph.setStrict(false);
        //graph.setAutoCreate( true );
        
        /*
        graph.addEdge("AB", "A", "B", true);
        graph.addEdge("BC", "B", "C", true);
        //graph.addEdge("1", "B", "A", true);
        graph.addEdge("2", "C", "A", true);
        graph.addEdge("3", "C", "A", true);
        graph.addEdge("34", "C", "A", true);
        */
        
       // graph.addEdge("1", "A", "B", true);
        graph.addEdge("2", "A", "B", true);



  /*      graph.addEdge("CA", "C", "A", true);
        graph.addEdge("DA", "D", "A", true);    
        graph.addEdge("DD", "D", "D", true);
*/
        Node A = graph.getNode("A");
        A.setAttribute("ui.style", "shape:circle;fill-color: red;size: 40px;text-size: 30px; text-alignment: center;");

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

    public static void test2(){
        System.setProperty("org.graphstream.ui", "swing");
	final String NODE_STYLE = "shape:circle;fill-color: cyan;size: 30px;text-size: 20px; text-alignment: center;";

        Graph graph = new MultiGraph("Tutorial 1");

        Node A = graph.addNode("A");
        Node B = graph.addNode("B");
        Node C = graph.addNode("C");
        
        A.setAttribute("ui.label", "A");
        B.setAttribute("ui.label", "B");
        C.setAttribute("ui.label", "C");
        A.setAttribute("ui.style", NODE_STYLE);
        B.setAttribute("ui.style", NODE_STYLE);
        C.setAttribute("ui.style", NODE_STYLE);


        
        //graph.addEdge("AB", "A", "B");
        //graph.addEdge("BC", "B", "C");
        //graph.addEdge("CA", "C", "A");

        graph.addEdge("ab", "A", "B",true);
        graph.addEdge("ac", "A", "C",true);

        graph.addEdge("ba", "B", "A",true);
        graph.addEdge("ab2", "A", "B",true);

        graph.display();
    
    }

    public static void test3(){
        System.setProperty("org.graphstream.ui", "swing");
	final String NODE_STYLE = "shape:circle;fill-color: cyan;size: 30px;text-size: 20px; text-alignment: center;";

        
        Graph graph = new MultiGraph("Tutorial 1");
        // With these 2 lines: there is no need to add each node, but the edges that links them

                           
        /*
        Node A = graph.addNode("A");
        Node B = graph.addNode("B");
        Node C = graph.addNode("C");
        Node D = graph.addNode("D");

        graph.addEdge("AB", "A", "B");
        graph.addEdge("BC", "B", "C");
        graph.addEdge("CD", "C", "D");

        A.setAttribute("layout.frozen");
        D.setAttribute("layout.frozen");
        A.setAttribute("xy", 0, 0);
        D.setAttribute("xy", 0.1f, 0);
*/       
        Node A = graph.addNode("A");
        //Node B = graph.addNode("B");
        //Node C = graph.addNode("C");

        A.setAttribute("ui.label", "A");
        //B.setAttribute("ui.label", "B");
        A.setAttribute("ui.style", NODE_STYLE);
        //B.setAttribute("ui.style", NODE_STYLE);

        //Edge edge3 =  graph.addEdge("cc", "C", "A",false);

        //Edge edge2 =  graph.addEdge("a7b", "A", "B",false);
        Edge edge =  graph.addEdge("ab", "A", "A",true);

        final String EDGE_STYLE = "text-size:40px;text-color:blue;text-alignment: under;";
        edge.setAttribute("ui.label", "1");
        edge.setAttribute("ui.style", EDGE_STYLE);
        //edge2.setAttribute("ui.label", '1');
        //edge2.setAttribute("ui.style", "text-size:40px;");
        
        
        //TODO:  D.setAttribute("xy", 0.1f, 0);

        
        
        graph.display();
    
    }

}
