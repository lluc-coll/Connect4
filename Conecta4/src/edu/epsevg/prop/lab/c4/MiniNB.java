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

    private int[][] tablaPuntuacion = {
        {3, 4, 5, 7, 7, 5, 4, 3},
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
        colorNB = color;
        int columna_elegida = miniMax(t);
        System.out.println("Numero de nodos explorados: " + jugadasExploradas);
        jugadasExploradas = 0;
        return columna_elegida;
    }

    @Override
    public String nom() {
        return nombre;
    }

    // Funcion que implementa el algortimo MiniMax
    private int miniMax(Tauler t) {
        int max = Integer.MIN_VALUE, columnaJugar = 0;
        int alpha = Integer.MIN_VALUE, beta = Integer.MAX_VALUE;
        t.pintaTaulerALaConsola();
        for (int col = 0; col < t.getMida(); ++col) {
            if (t.movpossible(col)) {
                Tauler tablaNueva = new Tauler(t);
                tablaNueva.afegeix(col, colorNB);
                int actual = valorMin(tablaNueva, col, profundidad - 1, alpha, beta);
                System.out.println(actual + ":" + col);
                if (actual >= max) { // poner >= o cambiar el valor de menos infinito a un poco mas para que se cumpla condicion
                    max = actual;
                    columnaJugar = col;
                }
            }
        }
        return columnaJugar;
    }

    // Funcion que calcula la heuristica maxima de todos los estados siguientes posibles al estado indicado en la tabla t y se tiene en cuenta si es con o sin poda
    private int valorMax(Tauler t, int columna, int prof, int alpha, int beta) {
        ++jugadasExploradas;
        int max = Integer.MIN_VALUE;
        if (t.solucio(columna, -colorNB)) {
            max = Integer.MIN_VALUE;
        } else if (prof == 0 || !t.espotmoure()) {
            max = valorColumna(t, columna);//valorHeuristico(t, columna); // funcion heuristica poner aqui
        } else {
            for (int col = 0; col < t.getMida(); ++col) {
                if (t.movpossible(col)) {
                    Tauler tablaNueva = new Tauler(t);
                    tablaNueva.afegeix(col, colorNB);
                    int value = valorColumna(tablaNueva, col);
                    int min = valorMin(tablaNueva, col, prof - 1, alpha, beta) - value; // funcion heuristica poner aqui
                    max = Math.max(max, min);
                    if (poda) {
                        alpha = Math.max(max, alpha);
                        if (alpha >= beta) {
                            break;
                        }
                    }
                }
            }
        }
        return max;
    }

    // Funcion que calcula la heuristica minima de todos los estados siguientes posibles al estado indicado en la tabla t y se tiene en cuenta si es con o sin poda
    private int valorMin(Tauler t, int columna, int prof, int alpha, int beta) {
        ++jugadasExploradas;
        int min = Integer.MAX_VALUE;
        if (t.solucio(columna, colorNB)) {
            min = Integer.MAX_VALUE;
        } else if (prof == 0 || !t.espotmoure()) {
            min = valorColumna(t, columna);//valorHeuristico(t, columna); // funcion heuristica poner aqui
        } else {
            for (int col = 0; col < t.getMida(); col++) {
                if (t.movpossible(col)) {
                    Tauler tablaNueva = new Tauler(t);
                    tablaNueva.afegeix(col, -colorNB);
                    int value = valorColumna(tablaNueva, col);
                    int actual = valorMax(tablaNueva, col, prof - 1, alpha, beta) + value;
                    min = Math.min(min, actual);
                    if (poda) {
                        beta = Math.min(min, beta);
                        if (alpha >= beta) {
                            break;
                        }
                    }
                }
            }
        }
        return min;
    }

    // Funcion que devuelve el valor heuristico para los eatdos de victoria o derrota
    private int valorHeuristico(Tauler t, int columna) {
        if (t.solucio(columna, colorNB)) {
            return Integer.MAX_VALUE;
        } else if (t.solucio(columna, -colorNB)) {
            return Integer.MIN_VALUE; //cambiar valor, es decir, un poco menos que infinito para que se pueda ejecutar arriba minimax o en la condicion del minimax poner >=
        }

        return 0;//tablaPuntuacion[columna][i];
    }

    private int valorColumna(Tauler t, int columna) {
        int i = 0;
        while (t.getColor(i, columna) != 0 && i < 7) {
            i++;
        }
        return tablaPuntuacion[columna][i - 1];
    }
}

// hacer minimax y heuristica separados
// primero hacer minimax, si no tienes heuristica todavia pues hacer return 0 todo el rato
// heuristica probar con clase prova, es decir, hacer print de la tabla

