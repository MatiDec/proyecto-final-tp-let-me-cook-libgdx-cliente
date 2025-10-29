package com.hebergames.letmecook.mapa.indicadores;

import com.badlogic.gdx.graphics.OrthographicCamera;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.graphics.g2d.TextureRegion;
import com.badlogic.gdx.math.Rectangle;
import com.badlogic.gdx.math.Vector2;
import com.hebergames.letmecook.utiles.GestorTexturas;

public class IndicadorVisual {
    private TextureRegion texturaActual;
    private final Vector2 POSICION_MUNDO;
    private final Vector2 POSICION_PANTALLA;
    private boolean visible;
    private boolean enBorde;
    private float tiempoAnimacion;
    private float anguloFlecha;
    private EstadoIndicador estado;

    public IndicadorVisual(float x, float y) {
        float OFFSET_Y = 40f;
        this.POSICION_MUNDO = new Vector2(x, y + OFFSET_Y);
        this.POSICION_PANTALLA = new Vector2();
        this.visible = false;
        this.enBorde = false;
        this.tiempoAnimacion = 0f;
        this.estado = EstadoIndicador.INACTIVO;
    }

    public void actualizar(float delta, OrthographicCamera camara, Rectangle areaVisible) {
        tiempoAnimacion += delta;

        if (estado == EstadoIndicador.PROCESANDO) {
            actualizarTextura();
        }

        if (!estaEnVista(areaVisible)) {
            enBorde = true;
            calcularPosicionBorde(camara);
        } else {
            enBorde = false;
            POSICION_PANTALLA.set(POSICION_MUNDO);
        }
    }

    private boolean estaEnVista(Rectangle areaVisible) {
        return areaVisible.contains(POSICION_MUNDO.x, POSICION_MUNDO.y);
    }


    private void calcularPosicionBorde(OrthographicCamera camara) {
        float camaraX = camara.position.x;
        float camaraY = camara.position.y;
        float anchoVista = camara.viewportWidth * camara.zoom;
        float altoVista = camara.viewportHeight * camara.zoom;

        float margen = 50f;

        float minX = camaraX - anchoVista / 2f + margen;
        float maxX = camaraX + anchoVista / 2f - margen;
        float minY = camaraY - altoVista / 2f + margen;
        float maxY = camaraY + altoVista / 2f - margen;

        Vector2 direccion = new Vector2(POSICION_MUNDO).sub(camaraX, camaraY).nor();

        float t1 = (maxX - camaraX) / direccion.x;
        float t2 = (minX - camaraX) / direccion.x;
        float t3 = (maxY - camaraY) / direccion.y;
        float t4 = (minY - camaraY) / direccion.y;

        float t = Float.MAX_VALUE;

        if (direccion.x > 0 && t1 > 0) t = Math.min(t, t1);
        if (direccion.x < 0 && t2 > 0) t = Math.min(t, t2);
        if (direccion.y > 0 && t3 > 0) t = Math.min(t, t3);
        if (direccion.y < 0 && t4 > 0) t = Math.min(t, t4);

        POSICION_PANTALLA.set(camaraX + direccion.x * t, camaraY + direccion.y * t);

        anguloFlecha = (float) Math.toDegrees(Math.atan2(direccion.y, direccion.x));
    }

    public void dibujar(SpriteBatch batch) {
        if (!visible || texturaActual == null) return;

        float TAMANO = 32f;
        float x = POSICION_PANTALLA.x - TAMANO / 2f;
        float y = POSICION_PANTALLA.y - TAMANO / 2f;

        if (enBorde) {
            GestorTexturas gestor = GestorTexturas.getInstance();
            TextureRegion flecha = gestor.getTexturaFlecha();

            if (flecha != null) {
                float rad = (float) Math.toRadians(anguloFlecha);
                float distancia = TAMANO * 1.2f;

                float flechaX = POSICION_PANTALLA.x + (float) Math.cos(rad) * distancia;
                float flechaY = POSICION_PANTALLA.y + (float) Math.sin(rad) * distancia;

                batch.draw(flecha,
                    flechaX - TAMANO / 2f,
                    flechaY - TAMANO / 2f,
                    TAMANO / 2f,
                    TAMANO / 2f,
                    TAMANO,
                    TAMANO,
                    1f,
                    1f,
                    anguloFlecha);
            }
        }

        batch.draw(texturaActual, x, y, TAMANO, TAMANO);
        batch.setColor(1f, 1f, 1f, 1f);
    }

    public void setTextura(TextureRegion textura) {
        this.texturaActual = textura;
    }

    public void setEstado(EstadoIndicador estado) {
        this.estado = estado;
        actualizarTextura();
    }

    private void actualizarTextura() {
        GestorTexturas gestor = GestorTexturas.getInstance();

        switch (estado) {
            case PROCESANDO:
                int frame = (int)(tiempoAnimacion / 2f) % 3;
                texturaActual = gestor.getTexturaTemporizador(frame);
                break;
            case LISTO:
                texturaActual = gestor.getTexturaCheck();
                break;
            case QUEMANDOSE:
                texturaActual = gestor.getTexturaAlerta();
                break;
            case INACTIVO:
                texturaActual = null;
                visible = false;
                break;
        }
    }

    public void setVisible(boolean visible) {
        this.visible = visible;
    }

    public boolean isVisible() {
        return visible;
    }

    public EstadoIndicador getEstado() {
        return estado;
    }

    public boolean isEnBorde() {
        return enBorde;
    }
}
