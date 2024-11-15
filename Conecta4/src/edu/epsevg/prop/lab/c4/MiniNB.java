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
    
    private int[][] tablaPuntuacion= {{3, 4,  5,  7,  7,  5, 4, 3}, 
                                      {4, 6,  8, 11, 11,  8, 6, 4}, 
                                      {5, 8, 11, 13, 13, 11, 8, 5}, 
                                      {5, 8, 11, 13, 13, 11, 8, 5}, 
                                      {5, 8, 11, 13, 13, 11, 8, 5}, 
                                      {5, 8, 11, 13, 13, 11, 8, 5}, 
                                      {4, 6,  8,  7,  7,  8, 6, 4},
                                      {3, 4,  5,  7,  7,  5, 4, 3}};
    
    public MiniNB(int depth, boolean Poda) {
        nombre = "MiniNB";
        jugadasExploradas = 0;
        poda = Poda;
        profundidad = depth;
    }

    @Override
    public int moviment(Tauler t, int color) {                
        colorNB = color;
        int columna_elegida = miniMax(t);
        System.out.println("Numero de nodos explorados: " + jugadasExploradas);
        
        return columna_elegida;
    }

    @Override
    public String nom() {
        return nombre;
    }

    private int miniMax(Tauler t, int prof, boolean esMax) {
        int actual = Integer.MIN_VALUE, columnaJugar = 0;
        int alpha = Integer.MIN_VALUE, beta = Integer.MAX_VALUE;
        
        for (int col = 0; col < t.getMida(); ++col){
            if (t.movpossible(col)) {
                Tauler tablaNueva = new Tauler(t);
                tablaNueva.afegeix(col, colorNB);
                int min = valorMin(tablaNueva, col, profundidad -1, alpha, beta);
                if (min > actual) {
                    actual = min;
                    columnaJugar = col;
                }
        }
        return columnaJugar;
    }

    private int valorMin(Tauler T, int col, int prof, int alpha, int beta) {
       int actual = Integer.MIN_VALUE;
       if (solucio(col, colorNB)) 
    }

    private int valorMax(Tauler T, int col, int prof, int alpha, int beta) {
            
    }
    
}


// hacer minimax y heuristica separados
// primero hacer minimax, si no tienes heuristica todavia pues hacer return 0 todo el rato
// heuristica probar con clase prova, es decir, hacer print de la tabla


private int minimax1(Tauler t, int prof, boolean esMax){
    if (prof = 0){
        int heuristica = 0; //funcion heuristica
        return heuristica;
    }
    if (esMax){
        int max = Integer.MIN_VALUE; //funcion heuristica???
        for (int i = 0; i<8; i++){
            Tauler nou = new Tauler(t);
            nou.afegeix(i, 1);
            int aux = minimax(nou, prof-1, false);
            max = Math.max(max, aux);
        }
        return max;
    }
    else{
        int min = Integer.MAX_VALUE; //funcion heuristica???
        for (int i = 0; i<8; i++){
            Tauler nou = new Tauler(t);
            nou.afegeix(i, -1);
            int aux = minimax(nou, prof-1, true);
            min = Math.min(min, aux);
        }
        return min;
    }
}