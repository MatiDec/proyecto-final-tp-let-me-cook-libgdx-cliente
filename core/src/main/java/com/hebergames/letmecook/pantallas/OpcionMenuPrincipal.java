package com.hebergames.letmecook.pantallas;

public enum OpcionMenuPrincipal {

    MULTI_LOCAL("Multijugador Local"), MULTI_ONLINE("Multijugador Online"), TUTORIAL("Tutorial de juego"), OPCIONES("Opciones"), SALIR("Salir");

    private final String NOMBRE;

    OpcionMenuPrincipal(String NOMBRE) {
        this.NOMBRE = NOMBRE;
    }

    public String getNombre() {
        return this.NOMBRE;
    }
}
