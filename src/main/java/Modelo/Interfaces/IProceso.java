/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Interface.java to edit this template
 */
package Modelo.Interfaces;

/**
 *
 * @author Jtorr
 */
public interface IProceso {
    public boolean esFinal(int estado); //true si estado es un estado final
    public boolean reconocer(String cadena) ; //true si la cadena es reconocida
    public String toString( ) ; //muestra las transiciones y estados finales
}
