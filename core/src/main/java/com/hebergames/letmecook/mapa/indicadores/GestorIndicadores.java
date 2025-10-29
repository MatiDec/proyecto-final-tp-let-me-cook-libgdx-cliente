package com.hebergames.letmecook.mapa.indicadores;

import com.badlogic.gdx.graphics.OrthographicCamera;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.math.Rectangle;
import com.hebergames.letmecook.sonido.GestorAudio;

import java.util.ArrayList;

public class GestorIndicadores {
    private final ArrayList<IndicadorVisual> INDICADORES;
    private final Rectangle areaVisibleCache;
    private float tiempoUltimaAlerta;

    public GestorIndicadores() {
        INDICADORES = new ArrayList<>();
        areaVisibleCache = new Rectangle();
        tiempoUltimaAlerta = 0f;
    }

    public void registrarIndicador(IndicadorVisual indicador) {
        if (indicador != null && !INDICADORES.contains(indicador)) {
            INDICADORES.add(indicador);
        }
    }

    public void actualizar(float delta, OrthographicCamera camara) {
        calcularAreaVisible(camara);

        boolean hayAlertaActiva = false;
        tiempoUltimaAlerta += delta;

        for (IndicadorVisual indicador : INDICADORES) {
            indicador.actualizar(delta, camara, areaVisibleCache);

            if (indicador.isVisible() &&
                indicador.getEstado() == EstadoIndicador.QUEMANDOSE &&
                indicador.isEnBorde()) {
                hayAlertaActiva = true;
            }
        }

        float INTERVALO_SONIDO_ALERTA = 2f;
        if (hayAlertaActiva && tiempoUltimaAlerta >= INTERVALO_SONIDO_ALERTA) {
            GestorAudio.getInstance().reproducirSonido("alerta_quemado");
            tiempoUltimaAlerta = 0f;
        }
    }

    private void calcularAreaVisible(OrthographicCamera camara) {
        float anchoVista = camara.viewportWidth * camara.zoom;
        float altoVista = camara.viewportHeight * camara.zoom;

        areaVisibleCache.set(
            camara.position.x - anchoVista / 2f,
            camara.position.y - altoVista / 2f,
            anchoVista,
            altoVista
        );
    }

    public void dibujar(SpriteBatch batch) {
        for (IndicadorVisual indicador : INDICADORES) {
            indicador.dibujar(batch);
        }
    }

}
