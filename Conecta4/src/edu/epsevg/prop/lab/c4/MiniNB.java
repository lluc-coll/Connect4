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
                int value = valorColumna(tablaNueva, col);

                int actual = valorMin(tablaNueva, col, profundidad - 1, alpha, beta);//+value;
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
            max = valorHeuristico(t, columna); // funcion heuristica poner aqui
        } else {
            for (int col = 0; col < t.getMida(); ++col) {
                if (t.movpossible(col)) {
                    Tauler tablaNueva = new Tauler(t);
                    tablaNueva.afegeix(col, colorNB);
                    int min = valorMin(tablaNueva, col, prof - 1, alpha, beta);
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
            min = valorHeuristico(t, columna); // funcion heuristica poner aqui
        } else {
            for (int col = 0; col < t.getMida(); col++) {
                if (t.movpossible(col)) {
                    Tauler tablaNueva = new Tauler(t);
                    tablaNueva.afegeix(col, -colorNB);
                    int actual = valorMax(tablaNueva, col, prof - 1, alpha, beta);
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
        /*if (t.solucio(columna, colorNB)) {
            return Integer.MAX_VALUE;
        } else if (t.solucio(columna, -colorNB)) {
            return Integer.MIN_VALUE; //cambiar valor, es decir, un poco menos que infinito para que se pueda ejecutar arriba minimax o en la condicion del minimax poner >=
        }*/
        int[] result = contarConsecutivos(t, colorNB);
        
        return result[0]*2+result[1]*5;//tablaPuntuacion[columna][i];
    }

    // devuelve el valor de la casilla a la que se ha puesto la ultima pieza
    private int valorColumna(Tauler t, int columna) {
        int i = 0;
        while (t.getColor(i, columna) != 0 && i < 7) {
            i++;
        }
        return tablaPuntuacion[columna][i - 1];
    }
    
   public static  int[] contarConsecutivos(Tauler t, int jugador) {
        int dosEnLinea = 0;
        int tresEnLinea = 0;

        // Contar en filas y columnas
        for (int i = 0; i < 8; i++) {
            int[] fila = new int[8];
            for (int j = 0; j < 8; j++) {
                fila[j] = t.getColor(i, j);
            }
            // Contar en filas
            dosEnLinea += contarConsecutivosEnLinea(fila, jugador, 2);
            tresEnLinea += contarConsecutivosEnLinea(fila, jugador, 3);

            // Contar en columnas
            int[] columna = new int[8];
            for (int j = 0; j < 8; j++) {
                columna[j] = t.getColor(j, i);
            }
            dosEnLinea += contarConsecutivosEnLinea(columna, jugador, 2);
            tresEnLinea += contarConsecutivosEnLinea(columna, jugador, 3);
        }

        // Contar en diagonales
        for (int k = -7; k <= 7; k++) {
            int[] diagonalPrincipal = obtenerDiagonal(t, k, true);
            int[] diagonalSecundaria = obtenerDiagonal(t, k, false);

            if (diagonalPrincipal.length >= 2) {
                dosEnLinea += contarConsecutivosEnLinea(diagonalPrincipal, jugador, 2);
                tresEnLinea += contarConsecutivosEnLinea(diagonalPrincipal, jugador, 3);
            }
            if (diagonalSecundaria.length >= 2) {
                dosEnLinea += contarConsecutivosEnLinea(diagonalSecundaria, jugador, 2);
                tresEnLinea += contarConsecutivosEnLinea(diagonalSecundaria, jugador, 3);
            }
        }
        
        int[] result = {dosEnLinea, tresEnLinea};
        
        return result;
    }

    private static int contarConsecutivosEnLinea(int[] linea, int jugador, int longitud) {
        int contador = 0;
        int consecutivos = 0;

        for (int casilla : linea) {
            if (casilla == jugador) {
                consecutivos++;
                if (consecutivos == longitud) {
                    contador++;
                }
            } else {
                consecutivos = 0;
            }
        }
        return contador;
    }

    private static int[] obtenerDiagonal(Tauler t, int k, boolean principal) {
        // k es el desplazamiento de la diagonal: 0 para la principal, -1 para abajo, +1 para arriba
        // principal indica si es la diagonal principal o secundaria
        int n = 7;
        int[] diagonal;
        int startRow = Math.max(0, -k);
        int startCol = Math.max(0, k);

        if (principal) {
            diagonal = new int[Math.min(n - startRow, n - startCol)];
            for (int i = 0; i < diagonal.length; i++) {
                diagonal[i] = t.getColor(startRow+i, startCol+i);//tablero[startRow + i][startCol + i];
            }
        } else {
            startRow = Math.max(0, k);
            startCol = Math.max(0, -k);
            diagonal = new int[Math.min(n - startRow, n - startCol)];
            
            for (int i = 0; i < diagonal.length; i++) {
                //System.out.println(diagonal.length+":"+(startRow+i)+","+(7-(startCol+i)));
                diagonal[i] = t.getColor(startRow+i, 7-(startCol+i));//tablero[startRow + i][7 - (startCol + i)];
            }
        }
        return diagonal;
    }
}

// hacer minimax y heuristica separados
// primero hacer minimax, si no tienes heuristica todavia pues hacer return 0 todo el rato
// heuristica probar con clase prova, es decir, hacer print de la tabla

