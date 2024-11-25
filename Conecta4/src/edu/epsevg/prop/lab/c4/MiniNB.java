package edu.epsevg.prop.lab.c4;

import java.util.ArrayList;
import java.util.List;

public class MiniNB implements Jugador, IAuto {

    /**
     * Nom del millor jugador de Connecta-4!
     */
    public String nombre;
    /**
     * Nombre de jugades explorades per tirada
     */
    public int jugadasExploradas = 0;
    /**
     * Nombre de jugades explorades per partida
     */
    public int jugadasTotalesPartida = 0;
    /**
     * Profunditat a la que s'arribara durant el minimax
     */
    public int profundidad;
    /**
     * Boolean per saber si aplicar poda alpha-beta o no.
     */
    public boolean poda;
    /**
     * Color que li toca al MiniNB
     */
    public int colorNB;
    private long tiempoInicial = 0;

    /**
     * Taula de puntuacions del tauler. Cada casella representa la puntuacio
     * d'aquella casella al tauler.
     */
    public int[][] tablaPuntuacion = {
        {3, 4, 5, 9, 9, 5, 4, 3},
        {4, 6, 8, 12, 12, 8, 6, 4},
        {5, 8, 11, 15, 15, 11, 8, 5},
        {9, 12, 15, 20, 20, 15, 12, 9},
        {9, 12, 15, 20, 20, 15, 12, 9},
        {5, 8, 11, 15, 15, 11, 8, 5},
        {4, 6, 8, 12, 12, 8, 6, 4},
        {3, 4, 5, 9, 9, 5, 4, 3}
    };

    /**
     * Constructor del millor jugador de Connecta-4!
     *
     * @param depth Profunditat a la que es vol arribar.
     * @param pruning Boolean per saber si aplicar poda alpha-beta o no.
     */
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
        jugadasTotalesPartida += jugadasExploradas;
        System.out.println("Numero de nodos explorados: " + jugadasExploradas);
        System.out.println("Numero de nodos totales explorados: " + jugadasTotalesPartida);
        return columna_elegida;
    }

    @Override
    public String nom() {
        return nombre;
    }

    /**
     * Funcion que implementa el algortimo MiniMax. Si "poda" = true, obte les
     * jugades i les ordena de major a menor probabilitat de guanyar.
     *
     * @param t Tauler actual de la partida.
     * @return La columna amb mes probabilitats de guanyar.
     */
    public int miniMax(Tauler t) {
        int max = -300000, columnaJugar = 0;
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
        System.out.println("Tiempo: " + tiempo + " s");
        return columnaJugar;
    }

    /**
     * Funcio maximitzadora del minimax. Si "poda" = true, obte les jugades i
     * les ordena de major a menor probabilitat de guanyar.
     *
     * @param t Tauler en la profunditat "prof"
     * @param columna Columna a la qual s'ha posat la ultima fitxa
     * @param prof Profunditat actual
     * @param alpha Variable alpha per fer la poda en cas qeu "poda" = true.
     * @param beta Variable beta per fer la poda en cas qeu "poda" = true.
     * @return La millor jugada pel nostre jugador en el Tauler t.
     */
    public int valorMax(Tauler t, int columna, int prof, int alpha, int beta) {
        int max = -100000;

        if (t.solucio(columna, -colorNB)) {
            return max;
        } else if (prof == 0 || !t.espotmoure()) {
            return heuristicaGlobal(t, colorNB);
        } else if (poda) {
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

    /**
     * Funcio minimitzadora del minimax. Si "poda" = true, obte les jugades i
     * les ordena de major a menor probabilitat de guanyar.
     *
     * @param t Tauler en la profunditat "prof"
     * @param columna Columna a la qual s'ha posat la ultima fitxa
     * @param prof Profunditat actual
     * @param alpha Variable alpha per fer la poda en cas qeu "poda" = true.
     * @param beta Variable beta per fer la poda en cas qeu "poda" = true.
     * @return La millor jugada per l'enemic en el Tauler t.
     */
    public int valorMin(Tauler t, int columna, int prof, int alpha, int beta) {
        int min = 100000;

        if (t.solucio(columna, colorNB)) {
            return min;
        } else if (prof == 0 || !t.espotmoure()) {
            return heuristicaGlobal(t, colorNB);
        } else if (poda) {
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

    /**
     * Funcio per obtenir jugades donat un Tauler t
     *
     * @param t Tauler actual
     * @param color Color al qual li toca tirar
     * @return Els moviments possibles ordenats heuristicament (Per aplicar poda
     * mes eficientment).
     */
    public List<int[]> obtenerJugadas(Tauler t, int color) {
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

    /**
     * Funcio per ajuntar les dos heuristiques i calcular les jugades
     * explorades.
     *
     * @param t Tauler on calcular heuristica
     * @param color Color jugador
     * @return El valor heuristic final
     */
    public int heuristicaGlobal(Tauler t, int color) {
        ++jugadasExploradas;
        int valorHeuristico = heuristicaTauler(t, color) + heuristicaTabla(t, color);
        return valorHeuristico;
    }

    /**
     * Funcio heuristica per la taula de puntuacions
     *
     * @param t Tauler on calcular heuristica
     * @param color Color jugador
     * @return El valor heuristic de la suma i resta dels valors de la taula.
     */
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

    /**
     * Funcio auxiliar per evaluar cada linea
     *
     * @param t Tauler on calcular heuristica
     * @param color Color jugador
     * @return La suma de valors heuristics de cada linea
     */
    public int heuristicaTauler(Tauler t, int color) {
        int score = 0;

        score += evaluarHorizontales(t, color);
        score += evaluarVerticales(t, color);
        score += evaluarDiagonalesPrincipales(t, color);
        score += evaluarDiagonalesSecundarias(t, color);

        return score;
    }

    /**
     * Funcio que calcula el valor de les lines horitzontals
     *
     * @param t Tauler on calcular heuristica
     * @param color Color jugador
     * @return El valor heuristic de les linies horitzontals
     */
    public int evaluarHorizontales(Tauler t, int color) {
        int score = 0;
        int tamano = t.getMida();
        for (int fila = 0; fila < tamano; fila++) {
            int[] aux = evaluarLinea(t, color, fila, 0, 0, 1, false);
            if (aux[1] == 1) {
                break;
            }
            score += aux[0];
        }
        return score;
    }

    /**
     * Funcio que calcula el valor de les lines verticals
     *
     * @param t Tauler on calcular heuristica
     * @param color Color jugador
     * @return El valor heuristic de les linies verticals
     */
    public int evaluarVerticales(Tauler t, int color) {
        int score = 0;
        int tamano = t.getMida();
        for (int col = 0; col < tamano; col++) {
            if (t.getColor(0, col) == 0) {
                continue;
            }
            score += evaluarLinea(t, color, 0, col, 1, 0, true)[0];
        }
        return score;
    }

    /**
     * Funcio que calcula el valor de les lines diagonal amunt-esquerra
     *
     * @param t Tauler on calcular heuristica
     * @param color Color jugador
     * @return El valor heuristic de les linies diagonal amunt-esquerra
     */
    public int evaluarDiagonalesPrincipales(Tauler t, int color) {
        int score = 0;
        int tamano = t.getMida();

        // Diagonales desde la columna 0 hacia abajo
        for (int fila = 0; fila <= tamano - 4; fila++) {
            score += evaluarLinea(t, color, fila, 0, 1, 1, false)[0]; // Dirección hacia abajo y derecha
        }

        // Diagonales desde la fila 0 hacia la derecha
        for (int col = 1; col <= tamano - 4; col++) {
            score += evaluarLinea(t, color, 0, col, 1, 1, false)[0]; // Dirección hacia abajo y derecha
        }

        return score;
    }

    /**
     * Funcio que calcula el valor de les lines diagonal amunt-dreta
     *
     * @param t Tauler on calcular heuristica
     * @param color Color jugador
     * @return El valor heuristic de les linies diagonal amunt-dreta
     */
    public int evaluarDiagonalesSecundarias(Tauler t, int color) {
        int score = 0;
        int tamano = t.getMida();

        // Diagonales desde la columna 0 hacia arriba
        for (int fila = 3; fila < tamano; fila++) { // Empieza en fila 3 para asegurar longitud mínima de 4
            score += evaluarLinea(t, color, fila, 0, -1, 1, false)[0]; // Dirección hacia arriba y derecha
        }

        // Diagonales desde la última fila hacia la derecha
        for (int col = 1; col <= tamano - 4; col++) {
            score += evaluarLinea(t, color, tamano - 1, col, -1, 1, false)[0]; // Dirección hacia arriba y derecha
        }

        return score;
    }

    /**
     * Funcio que evalua cada linea mirant el numero de fitxes seguides que te
     * cada jugador.
     *
     * @param t Tauler on calcular heuristica
     * @param color Color jugador
     * @param filaIn Fila desde donde se inicia
     * @param columnaIn Columna desde donde se inicia
     * @param filaDelta Direccion horizontal
     * @param columnaDelta Direccion vertical
     * @param vertical Boolean per aplicar la optimitzacio vertical
     * @return Array de Int amb dos valors: La suma de valors heuristics de la fila i un 1 si la fila han estat tot zeros
     */
    public int[] evaluarLinea(Tauler t, int color, int filaIn, int columnaIn, int filaDelta, int columnaDelta, boolean vertical) {
        int h = 0;
        int tamano = t.getMida();
        int jugCons = 0, opoCons = 0, jugZero = 0, opoZero = 0;
        int fila = filaIn, col = columnaIn;
        boolean allZeros = true;
        while (fila >= 0 && fila < tamano && col >= 0 && col < tamano) {
            int celda = t.getColor(fila, col);
            if (celda == color) {
                allZeros = false;
                if (opoCons + opoZero >= 4) {
                    h -= returnH(opoCons);
                }
                jugCons++;
                opoZero = 0;
                opoCons = 0;
            } else if (celda == -color) {
                allZeros = false;
                if (jugCons + jugZero >= 4) {
                    h += returnH(jugCons);
                }
                opoCons++;
                jugZero = 0;
                jugCons = 0;
            } else if (celda == 0) {
                opoZero++;
                jugZero++;
                if (vertical) {
                    jugZero += tamano-col;
                    opoZero += tamano-col;
                    break;
                }
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

        int totZeros = 0;
        if (allZeros) {
            totZeros = 1;
        }
        int[] ret = {h, totZeros};
        return ret;
    }

    /**
     * Funcio heuristica final
     *
     * @param jugCons Numero de fitxes seguides (poden tenir espais entremig)
     * @return Un valor heuristic depenent de les fitxes seguides
     */
    public static int returnH(int jugCons) {
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
        if (jugCons > 3) {
            return 50;
        }
        return 0;
    }
}
