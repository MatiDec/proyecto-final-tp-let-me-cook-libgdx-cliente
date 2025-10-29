package com.hebergames.letmecook.entregables.ingredientes;

import com.badlogic.gdx.graphics.g2d.TextureRegion;
import com.hebergames.letmecook.entregables.ObjetoAlmacenable;
import com.hebergames.letmecook.estaciones.procesadoras.MetodoCoccion;

public abstract class Ingrediente implements ObjetoAlmacenable {
    protected String nombre;
    protected TextureRegion textura;
    protected boolean esCocinableInterna;

    protected EstadoCoccion estadoCoccion;
    protected float tiempoCoccionActual;
    protected float tiempoCoccionMinimo;
    protected float tiempoCoccionMaximo;
    protected CoccionListener coccionListener;
    protected MetodoCoccion metodoCoccion;

    public Ingrediente(String nombre, TextureRegion textura) {
        this.nombre = nombre;
        this.textura = textura;
        this.metodoCoccion = null;
        this.estadoCoccion = EstadoCoccion.MAL_HECHO;
        this.tiempoCoccionActual = 0f;
    }

    public Ingrediente(String nombre, TextureRegion textura, MetodoCoccion metodoCoccion,
                       float tiempoCoccionMinimo, float tiempoCoccionMaximo) {
        this.nombre = nombre;
        this.textura = textura;
        this.metodoCoccion = metodoCoccion;
        this.estadoCoccion = EstadoCoccion.MAL_HECHO;
        this.tiempoCoccionActual = 0f;
        this.tiempoCoccionMinimo = tiempoCoccionMinimo;
        this.tiempoCoccionMaximo = tiempoCoccionMaximo;
        this.esCocinableInterna = (metodoCoccion != null);
    }

    public void actualizarCoccion(float delta) {
        if (!esCocinableInterna) return;

        tiempoCoccionActual += delta;
        EstadoCoccion estadoAnterior = estadoCoccion;

        if (tiempoCoccionActual >= tiempoCoccionMaximo) {
            estadoCoccion = EstadoCoccion.PASADO;
            if (coccionListener != null && estadoAnterior != EstadoCoccion.PASADO) {
                coccionListener.onIngredienteQuemado();
            }
        } else if (tiempoCoccionActual >= tiempoCoccionMinimo) {
            estadoCoccion = EstadoCoccion.BIEN_HECHO;
        }

        if (estadoAnterior != estadoCoccion && coccionListener != null) {
            coccionListener.onCambioEstado(estadoCoccion);
        }
    }

    public void setCoccionListener(CoccionListener listener) {
        this.coccionListener = listener;
    }

    public boolean esCocinableInterna() {
        return this.metodoCoccion != null;
    }

    public EstadoCoccion getEstadoCoccion() {
        return this.estadoCoccion;
    }

    public boolean estaQuemado() {
        return estadoCoccion == EstadoCoccion.PASADO;
    }

    @Override
    public String getNombre() {
        if (esCocinableInterna && estadoCoccion != EstadoCoccion.MAL_HECHO) {
            return nombre + " (" + estadoCoccion.getESTADO() + ")";
        }
        return this.nombre;
    }

    public TextureRegion getTextura() { return this.textura; }
}
