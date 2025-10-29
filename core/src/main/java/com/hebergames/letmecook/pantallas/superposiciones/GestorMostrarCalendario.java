package com.hebergames.letmecook.pantallas.superposiciones;

public class GestorMostrarCalendario {
    private float tiempoMostrado;
    private boolean mostrandoCalendario;
    private static final float TIEMPO_MOSTRAR = 3f;

    public GestorMostrarCalendario() {
        this.tiempoMostrado = 0f;
        this.mostrandoCalendario = false;
    }

    public void iniciarMostrar() {
        this.mostrandoCalendario = true;
        this.tiempoMostrado = 0f;
    }

    public void actualizar(float delta) {
        if (mostrandoCalendario) {
            tiempoMostrado += delta;
            if (tiempoMostrado >= TIEMPO_MOSTRAR) {
                mostrandoCalendario = false;
            }
        }
    }

    public boolean estaMostrando() {
        return !mostrandoCalendario;
    }

}
