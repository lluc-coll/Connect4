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

    private int[][] tablaPuntuacion = {
        {3, 4, 5, 9, 9, 5, 4, 3},
        {4, 6, 8, 12, 12, 8, 6, 4},
        {5, 8, 11, 15, 15, 11, 8, 5},
        {9, 12, 15, 20, 20, 15, 12, 9},
        {9, 12, 15, 20, 20, 15, 12, 9},
        {5, 8, 11, 15, 15, 11, 8, 5},
        {4, 6, 8, 12, 12, 8, 6, 4},
        {3, 4, 5, 9, 9, 5, 4, 3}
    };

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
        //System.out.println("Pone ficha en columna : " + columna_elegida);
        //System.out.println("Numero de nodos explorados: " + jugadasExploradas);       
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

        if (poda) {
            // Con poda: se genera y ordenan movimientos
            List<int[]> movimientos = obtenerJugadas(t, colorNB);
            for (int[] jugada : movimientos) {
                int col = jugada[0];
                Tauler tablaNueva = new Tauler(t);
                tablaNueva.afegeix(col, colorNB);
                int actual = valorMin(tablaNueva, col, profundidad - 1, alpha, beta);
                if (actual > max) {
                    max = actual;
                    columnaJugar = col;
                }
            }
        } else {
            // Sin poda: se usa diseño antiguo
            for (int col = 0; col < t.getMida(); ++col) {
                if (t.movpossible(col)) {
                    Tauler tablaNueva = new Tauler(t);
                    tablaNueva.afegeix(col, colorNB);
                    int actual = valorMin(tablaNueva, col, profundidad - 1, alpha, beta);
                    if (actual > max) { // Aquí se puede considerar cambiar -30000 por un poco menos infinito
                        max = actual;
                        columnaJugar = col;
                    }
                }
            }
        }

        long tiempoFinal = System.currentTimeMillis();
        double tiempo = (tiempoFinal - tiempoInicial) / 1000.0;
        //System.out.println("Tiempo: " + tiempo + " s");
        return columnaJugar;
    }

    private int valorMax(Tauler t, int columna, int prof, int alpha, int beta) {
        ++jugadasExploradas;
        int max = -100000;

        if (t.solucio(columna, -colorNB)) {
            return max;
        } else if (prof == 0 || !t.espotmoure()) {
            return heuristicaGlobal(t, colorNB);
        }

        else if (poda) {
            // Con poda: genera y ordena jugadas
            List<int[]> movimientos = obtenerJugadas(t, colorNB);
            for (int[] jugada : movimientos) {
                int col = jugada[0];
                Tauler tablaNueva = new Tauler(t);
                tablaNueva.afegeix(col, colorNB);
                int min = valorMin(tablaNueva, col, prof - 1, alpha, beta);
                max = Math.max(max, min);
                alpha = Math.max(alpha, max);
                if (alpha >= beta) {
                    break;
                }
            }
        } else {
            // Sin poda: iteración simple
            for (int col = 0; col < t.getMida(); ++col) {
                if (t.movpossible(col)) {
                    Tauler tablaNueva = new Tauler(t);
                    tablaNueva.afegeix(col, colorNB);
                    int min = valorMin(tablaNueva, col, prof - 1, alpha, beta);
                    max = Math.max(max, min);
                }
            }
        }
        return max;
    }

    private int valorMin(Tauler t, int columna, int prof, int alpha, int beta) {
        ++jugadasExploradas;
        int min = 100000;

        if (t.solucio(columna, colorNB)) {
            return min;
        } else if (prof == 0 || !t.espotmoure()) {
            return heuristicaGlobal(t, colorNB);
        }

        else if (poda) {
            // Con poda: genera y ordena jugadas
            List<int[]> movimientos = obtenerJugadas(t, -colorNB);
            for (int[] jugada : movimientos) {
                int col = jugada[0];
                Tauler tablaNueva = new Tauler(t);
                tablaNueva.afegeix(col, -colorNB);
                int max = valorMax(tablaNueva, col, prof - 1, alpha, beta);
                min = Math.min(min, max);
                beta = Math.min(beta, min);
                if (alpha >= beta) {
                    break;
                }
            }
        } else {
            // Sin poda: iteración simple
            for (int col = 0; col < t.getMida(); ++col) {
                if (t.movpossible(col)) {
                    Tauler tablaNueva = new Tauler(t);
                    tablaNueva.afegeix(col, -colorNB);
                    int max = valorMax(tablaNueva, col, prof - 1, alpha, beta);
                    min = Math.min(min, max);
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

    private int heuristicaGlobal(Tauler t, int color) {
        ++jugadasExploradas;
        int valorHeuristico = heuristicaTauler(t, color) + heuristicaTabla(t, color);
        return valorHeuristico;
    }

    public int heuristicaTabla(Tauler t, int color) {
        int h = 0;
        int tamano = t.getMida();
        for (int f = 0; f < tamano; ++f) {
            for (int c = 0; c < tamano; ++c) {
                int col = t.getColor(f, c);
                if (col != 0) {
                    int signe = (col == color) ? 1 : -1;
                    h += tablaPuntuacion[f][c] * signe;
                }
            }
        }
        return h;

    }

    private int heuristicaTauler(Tauler t, int color) {
        int score = 0;

        score += evaluarHorizontales(t, color);
        score += evaluarVerticales(t, color);
        score += evaluarDiagonalesPrincipales(t, color);
        score += evaluarDiagonalesSecundarias(t, color);

        return score;
    }

    private int evaluarHorizontales(Tauler t, int color) {
        int score = 0;
        int tamano = t.getMida();
        for (int fila = 0; fila < tamano; fila++) {
            score += evaluarLinea(t, color, fila, 0, 0, 1);
        }
        return score;
    }

    private int evaluarVerticales(Tauler t, int color) {
        int score = 0;
        int tamano = t.getMida();
        for (int col = 0; col < tamano; col++) {
            if (t.getColor(0, col) == 0) {
                continue;
            }
            score += evaluarLinea(t, color, 0, col, 1, 0);
        }
        return score;
    }

    private int evaluarDiagonalesPrincipales(Tauler t, int color) {
        int score = 0;
        int tamano = t.getMida();

        // Diagonales desde la columna 0 hacia abajo
        for (int fila = 0; fila <= tamano - 4; fila++) {
            score += evaluarLinea(t, color, fila, 0, 1, 1); // Dirección hacia abajo y derecha
        }

        // Diagonales desde la fila 0 hacia la derecha
        for (int col = 1; col <= tamano - 4; col++) {
            score += evaluarLinea(t, color, 0, col, 1, 1); // Dirección hacia abajo y derecha
        }

        return score;
    }

    private int evaluarDiagonalesSecundarias(Tauler t, int color) {
        int score = 0;
        int tamano = t.getMida();

        // Diagonales desde la columna 0 hacia arriba
        for (int fila = 3; fila < tamano; fila++) { // Empieza en fila 3 para asegurar longitud mínima de 4
            score += evaluarLinea(t, color, fila, 0, -1, 1); // Dirección hacia arriba y derecha
        }

        // Diagonales desde la última fila hacia la derecha
        for (int col = 1; col <= tamano - 4; col++) {
            score += evaluarLinea(t, color, tamano - 1, col, -1, 1); // Dirección hacia arriba y derecha
        }

        return score;
    }

    private int evaluarLinea(Tauler t, int color, int filaIn, int columnaIn, int filaDelta, int columnaDelta) {
        int h = 0;
        int tamano = t.getMida();
        int jugCons = 0, opoCons = 0, jugZero = 0, opoZero = 0;
        int fila = filaIn, col = columnaIn;
        while (fila >= 0 && fila < tamano && col >= 0 && col < tamano) {
            int celda = t.getColor(fila, col);
            if (celda == color) {
                if (opoCons + opoZero >= 4) {
                    h -= returnH(opoCons);
                }
                jugCons++;
                opoZero = 0;
                opoCons = 0;
            } else if (celda == -color) {
                if (jugCons + jugZero >= 4) {
                    h += returnH(jugCons);
                }
                opoCons++;
                jugZero = 0;
                jugCons = 0;
            } else if (celda == 0) {
                opoZero++;
                jugZero++;
            }
            fila += filaDelta;
            col += columnaDelta;
        }
        if (jugCons + jugZero >= 4) {
            h += returnH(jugCons);
        }
        if (opoCons + opoZero >= 4) {
            h -= returnH(opoCons);
        }

        return h;
    }

    private static int returnH(int jugCons) {
        switch (jugCons) {
            case 3:
                return 30;
            case 2:
                return 5;
            case 1:
                return 1;
            default:
                break;
        }
        if (jugCons > 3){
            return 50;
        }
        return 0;
    }
}

// hacer minimax y heuristica separados
// primero hacer minimax, si no tienes heuristica todavia pues hacer return 0 todo el rato
// heuristica probar con clase prova, es decir, hacer print de la tabla

