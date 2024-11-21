/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package edu.epsevg.prop.lab.c4;

/**
 *
 * @author bernat
 */
public class Prova {
    public static void main(String[] args) {
        Tauler t = new Tauler(8);
        t.afegeix(1, -1);
        t.afegeix(7, +1);
        t.afegeix(1, -1);
        t.pintaTaulerALaConsola();
 
        int[] result = contarConsecutivos(t, -1);
        System.out.println("2 consecutivos: " + result[0]);
        System.out.println("3 consecutivos: " + result[1]);
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
/*
Fila
7  ->  0 0 0 0 0 0 0 0
6  ->  0 0 0 0 0 0 0 0
5  ->  0 0 0 0 0 0 0 0
4  ->  0 0 0 0 0 0 0 0
3  ->  0 0 0 0 0 0 0 0
2  ->  0 0 0 0 0 0 0 0
1  ->  0-1 0 0 0 0 0 0
0  ->  0-1 0 0 0 0 0 1

       0 1 2 3 4 5 6 7   Columna

*/
