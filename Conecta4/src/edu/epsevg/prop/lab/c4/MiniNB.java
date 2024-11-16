/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package edu.epsevg.prop.lab.c4;

import static java.lang.Integer.min;

/**
 *
 * @author wangm
 */
public class MiniNB implements Jugador, IAuto {

    private String nombre;
    private int jugadasExploradas = 0;
    private int profundidad = 8;
    private boolean poda;
    private int colorNB = 1;     

    private int[][] tablaPuntuacion = {{3, 4, 5, 7, 7, 5, 4, 3},
                                      {4, 6, 8, 11, 11, 8, 6, 4},
                                      {5, 8, 11, 13, 13, 11, 8, 5},
                                      {5, 8, 11, 13, 13, 11, 8, 5},
                                      {5, 8, 11, 13, 13, 11, 8, 5},
                                      {5, 8, 11, 13, 13, 11, 8, 5},
                                      {4, 6, 8, 7, 7, 8, 6, 4},
                                      {3, 4, 5, 7, 7, 5, 4, 3}};

    public MiniNB(int depth, boolean pruning) {
        nombre = "MiniNB";
        jugadasExploradas = 0;
        poda = pruning;
        profundidad = depth;
    }

    @Override
    public int moviment(Tauler t, int color) {                
        ++jugadasExploradas;
        colorNB = color;
        int columna_elegida = miniMax(t);
        System.out.println("Numero de nodos explorados: " + jugadasExploradas);
        
        return columna_elegida;
    }

    @Override
    public String nom() {
        return nombre;
    }

    // Funcion que implementa el algortimo MiniMax
    private int miniMax(Tauler t) {
        int actual = Integer.MIN_VALUE, columnaJugar = 0;
        int alpha = Integer.MIN_VALUE, beta = Integer.MAX_VALUE;
        
        for (int col = 0; col < t.getMida(); ++col){
            if (t.movpossible(col)) {
                Tauler tablaNueva = new Tauler(t);
                tablaNueva.afegeix(col, colorNB);
                int min = valorMin(tablaNueva, col, profundidad -1, alpha, beta);
                if (min >= actual) { // poner >= o cambiar el valor de menos infinito a un poco mas para que se cumpla condicion
                    actual = min;
                    columnaJugar = col;
                }
            }
        }
        return columnaJugar;
    }

    // Funcion que calcula la heuristica maxima de todos los estados siguientes posibles al estado indicado en la tabla t y se tiene en cuenta si es con o sin poda
    private int valorMax(Tauler t, int columna, int prof, int alpha, int beta) {        
       int actual = Integer.MIN_VALUE;
       if (t.solucio(columna, colorNB) || t.solucio(columna, -colorNB)) actual = valorHeuristico(t, columna);
       else if (prof == 0 || !t.espotmoure()) actual = 0; // funcion heuristica poner aqui
       else {
           for (int col = 0; col < t.getMida(); ++col) {
                if (t.movpossible(col)){
                    Tauler tablaNueva = new Tauler(t);
                    tablaNueva.afegeix(col, colorNB);
                    int min = valorMin(tablaNueva, col, prof-1, alpha, beta); // funcion heuristica poner aqui
                    actual = Math.max(actual, min);
                    if (poda) {
                        alpha = Math.max(actual, alpha);
                        if (alpha >= beta) break;
                    }
                }
           }
       }
       return actual;
    }
    
    // Funcion que calcula la heuristica minima de todos los estados siguientes posibles al estado indicado en la tabla t y se tiene en cuenta si es con o sin poda
    private int valorMin(Tauler t, int columna, int prof, int alpha, int beta) {
       int actual = Integer.MAX_VALUE;
        if (t.solucio(columna, colorNB) || t.solucio(columna, -colorNB)) actual = valorHeuristico(t, columna);       
       else if (prof == 0 || !t.espotmoure()) actual = 0; // funcion heuristica poner aqui
       else {
           for (int col = 0; col < t.getMida(); col++) {
                if (t.movpossible(col)){
                    Tauler tablaNueva = new Tauler(t);
                    tablaNueva.afegeix(col, -colorNB);
                    int max = valorMax(tablaNueva, col, prof-1, alpha, beta);
                    actual = Math.min(actual, max);
                    if (poda) {
                        beta = Math.min(actual, beta);
                        if (alpha >= beta) break;
                    }
                }
           }
       }
       return actual;
    }    
    
    // Funcion que devuelve el valor heuristico para los eatdos de victoria o derrota
    private int valorHeuristico(Tauler t, int columna) {
        if (t.solucio(columna, colorNB)) return Integer.MAX_VALUE;
        else return Integer.MIN_VALUE; //cambiar valor, es decir, un poco menos que infinito para que se pueda ejecutar arriba minimax o en la condicion del minimax poner >=
    }
    
    private int HeuristicaPocha(Tauler t){
        int res = 0;              
        
        return res;
    }
}
    
   


// hacer minimax y heuristica separados
// primero hacer minimax, si no tienes heuristica todavia pues hacer return 0 todo el rato
// heuristica probar con clase prova, es decir, hacer print de la tabla

