package com.hebergames.letmecook.estaciones.conmenu;

import com.hebergames.letmecook.entregables.ObjetoAlmacenable;
import java.util.function.Supplier;

public class OpcionMenu {
    private final int NUMERO;
    private final String NOMBRE;
    private Supplier<ObjetoAlmacenable> creadorObjeto;
    private Runnable accion;
    private final TipoOpcion TIPO;

    public OpcionMenu(final int NUMERO, final String NOMBRE, Supplier<ObjetoAlmacenable> creadorObjeto) {
        this.NUMERO = NUMERO;
        this.NOMBRE = NOMBRE;
        this.creadorObjeto = creadorObjeto;
        this.TIPO = TipoOpcion.CREAR_OBJETO;
    }

    public OpcionMenu(int numero, String NOMBRE, Runnable accion) {
        this.NUMERO = numero;
        this.NOMBRE = NOMBRE;
        this.accion = accion;
        this.TIPO = TipoOpcion.ACCION_SIMPLE;
    }

    public int getNumero() {
        return this.NUMERO;
    }

    public String getNombre() {
        return this.NOMBRE;
    }

    public String getTextoMenu() {
        return NUMERO + ". " + NOMBRE;
    }

    public ObjetoAlmacenable crearObjeto() {
        if (TIPO == TipoOpcion.CREAR_OBJETO && creadorObjeto != null) {
            return creadorObjeto.get();
        }
        return null;
    }

    public void ejecutarAccion() {
        if (TIPO == TipoOpcion.ACCION_SIMPLE && accion != null) {
            accion.run();
        }
    }

    public TipoOpcion getTipo() {
        return TIPO;
    }

    public boolean esAccionSimple() {
        return TIPO == TipoOpcion.ACCION_SIMPLE;
    }
}
