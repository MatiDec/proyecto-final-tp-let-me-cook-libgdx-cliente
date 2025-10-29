package com.hebergames.letmecook.estaciones.procesadoras;

import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.graphics.g2d.TextureRegion;
import com.badlogic.gdx.math.Rectangle;
import com.hebergames.letmecook.elementos.Texto;
import com.hebergames.letmecook.entregables.ingredientes.CoccionListener;
import com.hebergames.letmecook.entregables.ingredientes.EstadoCoccion;
import com.hebergames.letmecook.entregables.ingredientes.Ingrediente;
import com.hebergames.letmecook.mapa.indicadores.EstadoIndicador;
import com.hebergames.letmecook.mapa.indicadores.IndicadorVisual;
import com.hebergames.letmecook.sonido.GestorAudio;
import com.hebergames.letmecook.sonido.SonidoJuego;
import com.hebergames.letmecook.utiles.GestorTexturas;
import com.hebergames.letmecook.utiles.Recursos;

import java.util.Objects;

public class Procesadora implements MaquinaProcesadora, CoccionListener {

    private Ingrediente ingredienteCocinando;
    private boolean procesando = false;
    private final String TIPO_MAQUINA;
    private TextureRegion[] texturasMaquina;
    private boolean sonidoTemporizadorActivo = false;

    private final Rectangle AREA;

    private final IndicadorVisual INDICADOR;

    public Procesadora(Rectangle area, String tipoMaquina) {
        this.AREA = area;
        this.TIPO_MAQUINA = tipoMaquina;
        inicializarIndicador();
        this.INDICADOR = new IndicadorVisual(
            area.x + area.width / 2f,
            area.y + area.height
        );
        cargarTexturas();
    }

    private void cargarTexturas() {
        this.texturasMaquina = GestorTexturas.getInstance().getTexturasMaquina(TIPO_MAQUINA);
    }

    private void inicializarIndicador() {
        Texto indicadorEstado = new Texto(Recursos.FUENTE_MENU, 16, Color.WHITE, true);
        float centroX = AREA.x + AREA.width / 2f;
        float arribaY = AREA.y + AREA.height + 10f;
        indicadorEstado.setPosition(centroX, arribaY);
    }

    @Override
    public boolean puedeIniciarProceso() {
        return !procesando;
    }

    @Override
    public boolean iniciarProceso(Ingrediente ingrediente) {

        if (!puedeIniciarProceso() || !ingrediente.esCocinableInterna()
            || ingrediente.estaQuemado()) {
            return false;
        }

        ingredienteCocinando = ingrediente;
        ingredienteCocinando.setCoccionListener(this);
        procesando = true;

        return true;
    }

    @Override
    public void actualizarProceso(float delta) {
        if (!procesando || ingredienteCocinando == null) {
            if (INDICADOR != null) {
                INDICADOR.setVisible(false);
            }
            return;
        }

        ingredienteCocinando.actualizarCoccion(delta);

        if (INDICADOR != null) {
            INDICADOR.setVisible(true);

            if (ingredienteCocinando.estaQuemado()) {
                INDICADOR.setEstado(EstadoIndicador.QUEMANDOSE);
            } else if (ingredienteCocinando.getEstadoCoccion() == EstadoCoccion.BIEN_HECHO) {
                INDICADOR.setEstado(EstadoIndicador.LISTO);
            } else {
                INDICADOR.setEstado(EstadoIndicador.PROCESANDO);
            }
        }

        if (!ingredienteCocinando.estaQuemado() && ingredienteCocinando.getEstadoCoccion() != EstadoCoccion.BIEN_HECHO) {
            if (!sonidoTemporizadorActivo) {
                GestorAudio.getInstance().reproducirSonido(SonidoJuego.TEMPORIZADOR.getIdentificador());
                sonidoTemporizadorActivo = true;
            }
        }

        if (ingredienteCocinando.getEstadoCoccion() == EstadoCoccion.BIEN_HECHO) {
            GestorAudio.getInstance().detenerSonido(SonidoJuego.TEMPORIZADOR.getIdentificador());
            sonidoTemporizadorActivo = false;
        }

    }

    public void dibujarEstado(SpriteBatch batch) {
        if (!procesando && (ingredienteCocinando == null)) return;

        TextureRegion overlay = null;

        if (procesando) {
            overlay = GestorTexturas.getInstance()
                .getTexturaMaquina(TIPO_MAQUINA, EstadoMaquina.ACTIVA);
        } else if (ingredienteCocinando.getEstadoCoccion() == EstadoCoccion.BIEN_HECHO) {
            overlay = GestorTexturas.getInstance()
                .getTexturaMaquina(TIPO_MAQUINA, EstadoMaquina.LISTA);
        }

        if (overlay != null) {
            batch.draw(overlay, AREA.x, AREA.y, AREA.width, AREA.height);
        }
    }

    @Override
    public boolean tieneProcesandose() {
        return procesando && ingredienteCocinando != null;
    }

    @Override
    public Ingrediente obtenerResultado() {
        if (ingredienteCocinando == null) return null;

        Ingrediente resultado = ingredienteCocinando;
        ingredienteCocinando.setCoccionListener(null);
        ingredienteCocinando = null;
        procesando = false;
        GestorAudio.getInstance().detenerSonido(SonidoJuego.TEMPORIZADOR.getIdentificador());
        sonidoTemporizadorActivo = false;

        return resultado;
    }

    @Override
    public void onCambioEstado(EstadoCoccion nuevoEstado) {
        if (Objects.requireNonNull(nuevoEstado) == EstadoCoccion.BIEN_HECHO) {
            GestorAudio.getInstance().reproducirSonido("coccion_perfecta");
        }
    }

    @Override
    public void onIngredienteQuemado() {
        GestorAudio.getInstance().detenerSonido(SonidoJuego.TEMPORIZADOR.getIdentificador());
        GestorAudio.getInstance().reproducirSonido(SonidoJuego.ALERTA_QUEMADO.getIdentificador());
        sonidoTemporizadorActivo = false;
    }

    public IndicadorVisual getIndicador() {
        return this.INDICADOR;
    }

    public TextureRegion getTexturaActual() {
        if (texturasMaquina == null) {
            return null;
        }
        if (ingredienteCocinando != null &&
            ingredienteCocinando.getEstadoCoccion() == EstadoCoccion.BIEN_HECHO) {
            return texturasMaquina[EstadoMaquina.LISTA.getIndice()];
        }
        return texturasMaquina[EstadoMaquina.ACTIVA.getIndice()];
    }
}
