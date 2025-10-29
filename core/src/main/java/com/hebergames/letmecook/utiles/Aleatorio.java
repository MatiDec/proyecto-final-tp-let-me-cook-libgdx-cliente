package com.hebergames.letmecook.utiles;

import java.util.ArrayList;
import java.util.Random;

public class Aleatorio {
    private static final Random RANDOM = new Random();

    private Aleatorio() {}

    public static int entero(int limite) {
        return RANDOM.nextInt(limite);
    }

    public static int enteroRango(int min, int max) {
        return min + RANDOM.nextInt(max - min + 1);
    }

    public static float decimal() {
        return RANDOM.nextFloat();
    }

    public static float decimalRango(float min, float max) {
        return min + RANDOM.nextFloat() * (max - min);
    }

    public static boolean booleano() {
        return RANDOM.nextBoolean();
    }

    public static <T> T elementoAleatorio(ArrayList<T> lista) {
        if (lista == null || lista.isEmpty()) {
            return null;
        }
        return lista.get(RANDOM.nextInt(lista.size()));
    }

    public static <T> T elementoAleatorio(T[] array) {
        if (array == null || array.length == 0) {
            return null;
        }
        return array[RANDOM.nextInt(array.length)];
    }
}
