package edu.epsevg.prop.lab.c4;

import static java.lang.Integer.min;
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
    {3, 4,  5,  9,  9,  5, 4, 3}, 
    {4, 6,  8, 12, 12,  8, 6, 4}, 
    {5, 8, 11, 15, 15, 11, 8, 5}, 
    {9, 12, 15, 20, 20, 15, 12, 9}, 
    {9, 12, 15, 20, 20, 15, 12, 9}, 
    {5, 8, 11, 15, 15, 11, 8, 5}, 
    {4, 6,  8, 12, 12,  8, 6, 4}, 
    {3, 4,  5,  9,  9,  5, 4, 3}
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

        // Generar y ordenar jugadas posibles
        List<int[]> movimientos = obtenerJugadas(t, colorNB);

        for (int[] jugada : movimientos) {
            int column = jugada[0];
            Tauler tablaNueva = new Tauler(t);
            tablaNueva.afegeix(column, colorNB);
            int actual = valorMin(tablaNueva, column, profundidad - 1, alpha, beta);
            //System.out.println(actual + ":" + column);
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
    
    private int heuristicaGlobal (Tauler t, int color) {
        int valorHeuristico = 0;
        ++jugadasExploradas;
        //valorHeuristico = heuristicaTabla(t, color) + heuristicaAlineaciones(t, color);
        valorHeuristico = heuristicaProva(t, color) + heuristicaTabla(t, color) * 2;
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
    private int heuristicaProva(Tauler t, int color) {    
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
        
        //if (valorH == 0) break; // Si la fila está vacía, terminamos el bucle
        //score += valorH;
    }
    return score;
}

    private int evaluarVerticales(Tauler t, int color) {
    int score = 0;
    int tamano = t.getMida();
    for (int col = 0; col < tamano; col++) {
        if (t.getColor(0, col) == 0) continue;
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
    
private int evaluarLinea(Tauler t, int color, int filaIn, int columnaIn, int filaDelta, int columnaDelta){
    int h = 0;
    int tamano = t.getMida();
    int jugCons = 0, opoCons = 0, jugZero = 0, opoZero = 0, vacios = 0;        
    //boolean hayFichas = false;
    // 2,1 4,2 4,3 5,3-> perd a PvM:4, false
    // 4,5 3,4 3,3 -> perd a PvM:8, true
    int fila = filaIn, col = columnaIn;
    while (fila >= 0 && fila < tamano && col >= 0 && col < tamano) {
        int celda = t.getColor(fila, col);
        if (celda == color) {            
            //hayFichas = true;
            if (opoCons+opoZero >= 4){
                h -= returnH(opoCons);//opoCons*multOpo;
            }
            jugCons++;
            opoZero = 0;
            opoCons = 0;
        }
        else if (celda == -color){            
            //hayFichas = true;
            if (jugCons+jugZero >= 4){
                h += returnH(jugCons);//jugCons*multJug;
            }
            opoCons++;  
            jugZero = 0;
            jugCons = 0;
        }
        else if (celda == 0){
            opoZero++;
            jugZero++;
        }                
        fila += filaDelta;
        col += columnaDelta;        
    }
    if (jugCons + jugZero >= 4) {
        h += returnH(jugCons);//jugCons * multJug;
    }
    if (opoCons + opoZero >= 4) {
        h -= returnH(opoCons);//opoCons * multOpo;
    }    
    
    // Si no hay fichas en la fila, devolvemos 0 como indicador
    return h;
}    
    
    private static int returnH(int jugCons){
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
        return 0;
    }
}

// hacer minimax y heuristica separados
// primero hacer minimax, si no tienes heuristica todavia pues hacer return 0 todo el rato
// heuristica probar con clase prova, es decir, hacer print de la tabla

