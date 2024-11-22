package edu.epsevg.prop.lab.c4;

import static java.lang.Integer.min;

public class MiniNB implements Jugador, IAuto {

    private String nombre;
    private int jugadasExploradas = 0;
    private int profundidad = 8;
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
                                       {3, 4,  5,  7,  7,  5, 4, 3}}; // recorrer toda la tabla, suma puntos donde esta la ficha del jugador, suma tus puntos totales y luego los del oponente y se resta ese seria el valor devuelto

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
        System.out.println(" Pone ficha en columna :" + columna_elegida);
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
        for (int col = 0; col < t.getMida(); ++col) {
            if (t.movpossible(col)) {
                Tauler tablaNueva = new Tauler(t);
                tablaNueva.afegeix(col, colorNB);
                int actual = valorMin(tablaNueva, col, profundidad - 1, alpha, beta);
                System.out.println(actual + ":" + col);
                if (actual > max) { // poner >= o cambiar el valor de menos infinito a un poco mas para que se cumpla condicion
                    max = actual;
                    columnaJugar = col;
                } 
            }
        }
        long tiempoFinal = System.currentTimeMillis();
        double tiempo = (tiempoFinal - tiempoInicial) / 1000.0;
        System.out.println("Tiempo: " + tiempo + " s");
        return columnaJugar;
    }

    // Funcion que calcula la heuristica maxima de todos los estados siguientes posibles al estado indicado en la tabla t y se tiene en cuenta si es con o sin poda
    private int valorMax(Tauler t, int columna, int prof, int alpha, int beta) {
        ++jugadasExploradas;
        int max = -10000;
        if (t.solucio(columna, colorNB) || t.solucio(columna, -colorNB)) max = valorHeuristico(t, columna);
        else if (prof == 0 || !t.espotmoure()) {
            max = heuristicaGlobal(t, colorNB); // funcion heuristica poner aqui
        } else {
            for (int col = 0; col < t.getMida(); ++col) {
                if (t.movpossible(col)) {
                    Tauler tablaNueva = new Tauler(t);
                    tablaNueva.afegeix(col, colorNB);                    
                    int min = valorMin(tablaNueva, col, prof - 1, alpha, beta) ; 
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
        int min = 10000;
        if (t.solucio(columna, colorNB) || t.solucio(columna, -colorNB)) min = valorHeuristico(t, columna);
        else if (prof == 0 || !t.espotmoure()) {
            min = heuristicaGlobal(t, colorNB); // funcion heuristica poner aqui
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
        if (t.solucio(columna, colorNB)) return 10000;
        else return -10000; //cambiar valor, es decir, un poco menos que infinito para que se pueda ejecutar arriba minimax o en la condicion del minimax poner >=
    }    
    
    private int heuristicaGlobal (Tauler t, int color) {
        int valorHeuristico = 0;
        valorHeuristico = heuristicaTabla(t, color) + heuristicaAlineaciones(t, color);
        return valorHeuristico;
    }
    
    
    public int heuristicaTabla(Tauler t, int color) {
    int h = 0;
    for (int f = 0; f < t.getMida(); ++f) { 
        for (int c = 0; c < t.getMida(); ++c) { 
            int col = t.getColor(f, c); 
            if (col != 0) { 
                int signe = (col == color) ? 1 : -1; 
                h += tablaPuntuacion[f][c] * signe; 
            }
        }
    }
    return h;
}
    
    private int heuristicaAlineaciones(Tauler t, int color) {
    int h = 0;

    int[][] direciones = {
        {0, 1},   
        {1, 0},   
        {1, 1},   
        {1, -1}};

    for (int fil = 0; fil < t.getMida(); ++fil) {
        for (int col = 0; col < t.getMida(); ++col) {
            if (t.getColor(fil, col) == 0) continue;

            for (int[] dir : direciones) {
                h += evaluarDireccion(t, fil, col, dir[0], dir[1], color);
            }
        }
    }

    return h;
}

private int evaluarDireccion(Tauler t, int fil, int col, int dRow, int dCol, int color) {
    int jugador = 0, oponente = 0, vacia = 0;
    int h = 0;

    for (int i = 0; i < 4; ++i) { 
        int newRow = fil + i * dRow;
        int newCol = col + i * dCol;

        if (newRow < 0 || newRow >= t.getMida() || newCol < 0 || newCol >= t.getMida()) break; // Fuera del tablero

        int currentColor = t.getColor(newRow, newCol);
        if (currentColor == color) {
            jugador++;
        } else if (currentColor == -color) {
            oponente++;
        } else {
            vacia++;
        }
    }

    if (oponente == 0) { 
        if (jugador == 3 && vacia == 1) h += 10;
        else if (jugador == 2 && vacia == 2) h += 5;
        else if (jugador == 1 && vacia == 3) h += 1;
    }

    if (jugador == 0) { 
        if (oponente == 3 && vacia == 1) h -= 10;
        else if (oponente == 2 && vacia == 2) h -= 5;
        else if (oponente == 1 && vacia == 3) h -= 1;
    }
    
    // con estos valores solo pierde en depth 4, true, p1,p2
    return h;
}

    
    
}


// hacer minimax y heuristica separados
// primero hacer minimax, si no tienes heuristica todavia pues hacer return 0 todo el rato
// heuristica probar con clase prova, es decir, hacer print de la tabla

