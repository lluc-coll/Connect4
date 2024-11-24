package edu.epsevg.prop.lab.c4;

import java.util.ArrayList;
import java.util.List;

public class MiniNB implements Jugador, IAuto {

    private String nombre;
    private int jugadasExploradas = 0;
    private int profundidad;
    private boolean poda;
    private int colorNB = 1;
    long tiempoInicial = 0;

    private int[][] tablaPuntuacion = {{3, 4,  5,  7,  7,  5, 4, 3}, 
                                       {4, 6,  8, 7, 7,  8, 6, 4}, 
                                       {5, 8, 11, 11, 11, 11, 8, 5}, 
                                       {5, 8, 11, 13, 13, 11, 8, 5}, 
                                       {5, 8, 11, 13, 13, 11, 8, 5}, 
                                       {5, 8, 11, 11, 11, 11, 8, 5}, 
                                       {4, 6,  8,  7,  7,  8, 6, 4},
                                       {3, 4,  5,  7,  7,  5, 4, 3}}; 

    public MiniNB(int depth, boolean pruning) {
        nombre = "MiniNB";
        jugadasExploradas = 0;
        poda = pruning;
        profundidad = depth;
    }

    @Override
    public int moviment(Tauler t, int color) {
        colorNB = color;
        jugadasExploradas = 0;        
        tiempoInicial = System.currentTimeMillis();
        int columna_elegida = miniMax(t);
        System.out.println("Pone ficha en columna: " + columna_elegida);
        System.out.println("Numero de nodos explorados: " + jugadasExploradas);       
        return columna_elegida;
    }

    @Override
    public String nom() {
        return nombre;
    }

    // Funcion que implementa el algortimo MiniMax
    private int miniMax(Tauler t) {
        int max = -30000, columnaJugar = 0;
        int alpha = Integer.MIN_VALUE, beta = Integer.MAX_VALUE;

        // Generar y ordenar jugadas posibles
        List<int[]> movimientos = obtenerJugadas(t, colorNB);

        for (int[] jugada : movimientos) {
            int column = jugada[0];
            Tauler tablaNueva = new Tauler(t);
            tablaNueva.afegeix(column, colorNB);
            int actual = valorMin(tablaNueva, column, profundidad - 1, alpha, beta);
            System.out.println(actual + ":" + column);
            if (actual > max) {
                max = actual;
                columnaJugar = column;
            }
        }
        long tiempoFinal = System.currentTimeMillis();
        double tiempo = (tiempoFinal - tiempoInicial) / 1000.0;
        System.out.println("Tiempo: " + tiempo + " s");
        return columnaJugar;
    }

    private int valorMax(Tauler t, int columna, int prof, int alpha, int beta) {
        ++jugadasExploradas;
        int max = -10000;

        if (t.solucio(columna, colorNB) || t.solucio(columna, -colorNB)) {
            max = valorHeuristico(t, columna);
        } else if (prof == 0 || !t.espotmoure()) {
            max = heuristicaGlobal(t, colorNB);
        } else {
            List<int[]> movimientos = obtenerJugadas(t, colorNB);

            for (int[] jugada : movimientos) {
                int column = jugada[0];
                Tauler tablaNueva = new Tauler(t);
                tablaNueva.afegeix(column, colorNB);
                int min = valorMin(tablaNueva, column, prof - 1, alpha, beta);
                max = Math.max(max, min);
                if (poda) {
                    alpha = Math.max(max, alpha);
                    if (alpha >= beta) break;
                }
            }
        }
        return max;
    }

    private int valorMin(Tauler t, int columna, int prof, int alpha, int beta) {
        ++jugadasExploradas;
        int min = 10000;

        if (t.solucio(columna, colorNB) || t.solucio(columna, -colorNB)) {
            min = valorHeuristico(t, columna);
        } else if (prof == 0 || !t.espotmoure()) {
            min = heuristicaGlobal(t, colorNB);
        } else {
            List<int[]> movimientos = obtenerJugadas(t, -colorNB);

            for (int[] jugada : movimientos) {
                int column = jugada[0];
                Tauler tablaNueva = new Tauler(t);
                tablaNueva.afegeix(column, -colorNB);
                int max = valorMax(tablaNueva, column, prof - 1, alpha, beta);
                min = Math.min(min, max);
                if (poda) {
                    beta = Math.min(min, beta);
                    if (alpha >= beta) break;
                }
            }
        }
        return min;
    }

    private List<int[]> obtenerJugadas(Tauler t, int color) {
        List<int[]> jugadas = new ArrayList<>();

        for (int column = 0; column < t.getMida(); ++column) {
            if (t.movpossible(column)) {
                Tauler tablaNueva = new Tauler(t);
                tablaNueva.afegeix(column, color);
                int valor = heuristicaGlobal(tablaNueva, color);
                jugadas.add(new int[]{column, valor});
            }
        }

        // Ordenar las jugadas por valor heurístico descendente
        jugadas.sort((columna, valorH) -> Integer.compare(valorH[1], columna[1]));
        return jugadas;
    }

    // Funcion que devuelve el valor heuristico para los eatdos de victoria o derrota
    private int valorHeuristico(Tauler t, int columna) {
        if (t.solucio(columna, colorNB)) return 10000;
        else return -10000; //cambiar valor, es decir, un poco menos que infinito para que se pueda ejecutar arriba minimax o en la condicion del minimax poner >=
    }    
    
    private int heuristicaGlobal(Tauler t, int color) {
        int valorHeuristico = 0; 
        valorHeuristico = heuristicaTabla(t, color) + heuristicaFichasConsecutivas(t, color)*2;
        return valorHeuristico;
    }

    
    
    public int heuristicaTabla(Tauler t, int color) {
        int valorHeu = 0;
        for (int fil = 0; fil < t.getMida(); ++fil) { 
            for (int col = 0; col < t.getMida(); ++col) { 
                int colour = t.getColor(fil, col); 
                if (colour != 0) { 
                    int signo = (colour == color) ? 1 : -1; 
                    valorHeu += tablaPuntuacion[fil][col] * signo; 
                }
            }
        }
        return valorHeu;
    }        
    
    private int heuristicaFichasConsecutivas(Tauler t, int color) {
        int valorHeu = 0;
        int mida = t.getMida();

        // Evaluar la dirección vertical (de abajo hacia arriba)
        for (int col = 0; col < mida; ++col) {
            for (int fil = 0; fil <= mida - 4; ++fil) { // Solo posiciones válidas
                if (t.getColor(fil, col) == 0) break; // Fila 0 no hay ficha
                valorHeu += evaluarFichasConsecutivas(t, fil, col, 1, 0, color);
            }
        }

        // Evaluar la dirección horizontal (de izquierda a derecha)
        for (int fil = 0; fil < mida; ++fil) {
            for (int col = 0; col <= mida - 4; ++col) { // Solo posiciones válidas            
                valorHeu += evaluarFichasConsecutivas(t, fil, col, 0, 1, color);
            }
        }

        // Evaluar la diagonal principal (de abajo izquierda hacia arriba derecha)
        for (int fil = 0; fil <= mida - 4; ++fil) {
            for (int col = 0; col <= mida - 4; ++col) { // Solo posiciones válidas
                valorHeu += evaluarFichasConsecutivas(t, fil, col, 1, 1, color);
            }
        }

        // Evaluar la diagonal secundaria (de abajo derecha hacia arriba izquierda)
        for (int fil = 0; fil <= mida - 4; ++fil) {
            for (int col = 3; col < mida; ++col) { // Solo posiciones válidas
                valorHeu += evaluarFichasConsecutivas(t, fil, col, 1, -1, color);
            }
        }

        return valorHeu;
    }



    private int evaluarFichasConsecutivas(Tauler t, int fil, int col, int dRow, int dCol, int color) {
        int jugador = 0, oponente = 0, vacia = 0;
        int valorHeu = 0;

        for (int i = 0; i < 4; ++i) { 
            int nuevaFila = fil + i * dRow;
            int nuevaColumna = col + i * dCol;

            if (nuevaFila < 0 || nuevaFila >= t.getMida() || nuevaColumna < 0 || nuevaColumna >= t.getMida()) break; // Fuera del tablero

            int colorActual = t.getColor(nuevaFila, nuevaColumna);
            if (colorActual == color) {
                jugador++;
            } else if (colorActual == -color) {
                oponente++;
            } else {
                vacia++;
            }
        }

        if (oponente == 0) { 
            if (jugador == 3 && vacia == 1) valorHeu += 30;
            else if (jugador == 2 && vacia == 2) valorHeu += 5;
            else if (jugador == 1 && vacia == 3) valorHeu += 1;
        }

        if (jugador == 0) { 
            if (oponente == 3 && vacia == 1) valorHeu -= 30;
            else if (oponente == 2 && vacia == 2) valorHeu -= 5;
            else if (oponente == 1 && vacia == 3) valorHeu -= 1;
        }

        return valorHeu;
    }  
}
