/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package com.mycompany.mavenproject3;

import java.util.Comparator;

public class Heap<E> {

    private Comparator<E> cmp;
    private E[] arreglo;
    private int max;
    private int efectivo;
    private boolean isMax;

    public Heap(int max, boolean isMax, Comparator<E> cmp) {
        this.max = max;
        this.isMax = isMax;
        this.cmp = cmp;
        this.arreglo = (E[]) new Object[max];
        this.efectivo = 0;
    }

    public boolean estaVacio() {
        return efectivo == 0;
    }

    public int getEfectivo() {
        return efectivo;
    }

    private boolean esValido(int i) {
        return i >= 0 && i < efectivo;
    }

    private int posIzq(int i) {
        int izq = 2 * i + 1;
        return esValido(izq) ? izq : -1;
    }

    private int posDer(int i) {
        int der = 2 * i + 2;
        return esValido(der) ? der : -1;
    }

    private int posPadre(int i) {
        if (i == 0) return -1;
        int padre = (i - 1) / 2;
        return esValido(padre) ? padre : -1;
    }

    private void swap(int i, int j) {
        E tmp = arreglo[i];
        arreglo[i] = arreglo[j];
        arreglo[j] = tmp;
    }

    private boolean esHoja(int i) {
        return posIzq(i) == -1 && posDer(i) == -1;
    }

    private int mejor(int i) {
        int izq = posIzq(i);
        int der = posDer(i);
        int mejor = i;

        if (izq != -1) {
            int cmpIzq = cmp.compare(arreglo[izq], arreglo[mejor]);
            if ((isMax && cmpIzq > 0) || (!isMax && cmpIzq < 0)) {
                mejor = izq;
            }
        }

        if (der != -1) {
            int cmpDer = cmp.compare(arreglo[der], arreglo[mejor]);
            if ((isMax && cmpDer > 0) || (!isMax && cmpDer < 0)) {
                mejor = der;
            }
        }

        return mejor;
    }

    private void ajustar(int i) {
        if (!esValido(i) || esHoja(i)) return;

        int mejor = mejor(i);
        if (mejor != i) {
            swap(i, mejor);
            ajustar(mejor);
        }
    }

    public void encolar(E nuevo) {
        if (efectivo == max) return;

        arreglo[efectivo] = nuevo;
        int actual = efectivo;
        efectivo++;

        int padre = posPadre(actual);
        while (padre != -1) {
            int cmpRes = cmp.compare(arreglo[actual], arreglo[padre]);
            boolean subir = (isMax && cmpRes > 0) || (!isMax && cmpRes < 0);

            if (!subir) break;

            swap(actual, padre);
            actual = padre;
            padre = posPadre(actual);
        }
    }

    public E desencolar() {
        if (estaVacio()) return null;

        E raiz = arreglo[0];
        efectivo--;

        if (efectivo > 0) {
            arreglo[0] = arreglo[efectivo];
            arreglo[efectivo] = null;
            ajustar(0);
        } else {
            arreglo[0] = null;
        }

        return raiz;
    }
}

