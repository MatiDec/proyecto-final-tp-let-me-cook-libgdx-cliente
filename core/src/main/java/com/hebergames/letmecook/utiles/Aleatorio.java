package com.hebergames.letmecook.utiles;

import java.util.Random;

public class Aleatorio {
    private static final Random RANDOM = new Random();

    private Aleatorio() {}

    public static int entero(int limite) {
        return RANDOM.nextInt(limite);
    }

    public static float decimalRango(float min, float max) {
        return min + RANDOM.nextFloat() * (max - min);
    }

    public static boolean booleano() {
        return RANDOM.nextBoolean();
    }

}
