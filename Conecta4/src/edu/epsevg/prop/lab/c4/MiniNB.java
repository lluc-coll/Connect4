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

    private int MAS_INFINITO = Integer.MAX_VALUE;
    private int MENOS_INFINITO = Integer.MIN_VALUE;

    private int[][] tablaPuntuacion = {{3, 4, 5, 7, 7, 5, 4, 3},
    {4, 6, 8, 11, 11, 8, 6, 4},
    {5, 8, 11, 13, 13, 11, 8, 5},
    {5, 8, 11, 13, 13, 11, 8, 5},
    {5, 8, 11, 13, 13, 11, 8, 5},
    {5, 8, 11, 13, 13, 11, 8, 5},
    {4, 6, 8, 7, 7, 8, 6, 4},
    {3, 4, 5, 7, 7, 5, 4, 3}};

    public MiniNB(int depth, boolean Poda) {
        nombre = "MiniNB";
        jugadasExploradas = 0;
        poda = Poda;
        profundidad = depth;
    }

    @Override
    public int moviment(Tauler t, int color) {
        colorNB = color;
        //int columna_elegida = miniMax(t);
        System.out.println("Numero de nodos explorados: " + jugadasExploradas);
        jugadasExploradas = 0;
        t.pintaTaulerALaConsola();
        //return columna_elegida;
        if (poda) {
            return minimaxPoda(t, profundidad, true, Integer.MIN_VALUE, Integer.MAX_VALUE);
        }
        return minimax(t, profundidad, true);
    }

    @Override
    public String nom() {
        return nombre;
    }

    private int minimax(Tauler t, int prof, boolean esMax) {
        if (prof == 0) {
            int heuristica = HeuristicaPocha(t); //funcion heuristica
            return heuristica;
        }
        if (esMax) {
            int colMax = 0;
            int max = Integer.MIN_VALUE; //funcion heuristica???
            for (int i = 0; i < t.getMida(); i++) {
                if (t.movpossible(i)) {
                    Tauler nou = new Tauler(t);
                    nou.afegeix(i, 1);
                    int aux = minimax(nou, prof - 1, false);
                    if (max < aux) {
                        max = aux;
                        colMax = i;
                    }
                    
                    jugadasExploradas++;
                }

            }
            if (prof == profundidad) {
                return colMax;
            }
            return max;
            
        } else {
            int min = Integer.MAX_VALUE; //funcion heuristica???
            for (int i = 0; i < t.getMida(); i++) {
                if (t.movpossible(i)) {
                    Tauler nou = new Tauler(t);
                    nou.afegeix(i, -1);
                    int aux = minimax(nou, prof - 1, true);
                    min = Math.min(min, aux);
                    
                    jugadasExploradas++;
                }
            }
            return min;
        }
    }

    private int minimaxPoda(Tauler t, int prof, boolean esMax, int alpha, int beta) {
        if (prof == 0) {
            int heuristica = HeuristicaPocha(t); //funcion heuristica
            return heuristica;
        }
        if (esMax) {
            int colMax = 0;
            int max = Integer.MIN_VALUE; //HeuristicaPocha(t);
            for (int i = 0; i < t.getMida(); i++) {
                if (t.movpossible(i)) {
                    Tauler nou = new Tauler(t);
                    nou.afegeix(i, 1);
                    int aux = minimaxPoda(nou, prof - 1, false, alpha, beta);
                    if (max < aux) {
                        max = aux;
                        colMax = i;
                    }
                    alpha = Math.max(alpha, aux);
                    if (beta<=alpha){
                        i = t.getMida();
                    }
                    
                    jugadasExploradas++;
                }
            }
            if (prof == profundidad) {
                return colMax;
            }
            return max;
        } else {
            int min = Integer.MAX_VALUE; //HeuristicaPocha(t);
            for (int i = 0; i < t.getMida(); i++) {
                if (t.movpossible(i)) {
                    Tauler nou = new Tauler(t);
                    nou.afegeix(i, -1);
                    int aux = minimaxPoda(nou, prof - 1, true, alpha, beta);
                    min = Math.min(min, aux);
                    beta = Math.min(beta, aux);
                    if (beta<=alpha){
                        i = t.getMida();
                    }
                    
                    jugadasExploradas++;
                }
            }
            return min;
        }
    }
    
    
    private int HeuristicaPocha(Tauler t){
        int res = 0;
        
        t.
        
        return res;
    }
}

// hacer minimax y heuristica separados
// primero hacer minimax, si no tienes heuristica todavia pues hacer return 0 todo el rato
// heuristica probar con clase prova, es decir, hacer print de la tabla

