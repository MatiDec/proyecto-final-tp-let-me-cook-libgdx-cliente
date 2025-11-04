package com.hebergames.letmecook.pantallas;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.Input;
import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.GL20;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.hebergames.letmecook.elementos.Texto;
import com.hebergames.letmecook.mapa.niveles.GestorPartida;
import com.hebergames.letmecook.mapa.niveles.NivelPartida;
import com.hebergames.letmecook.pantallas.superposiciones.InfoDiaNivel;
import com.hebergames.letmecook.utiles.Recursos;
import com.hebergames.letmecook.utiles.Render;

import java.util.ArrayList;

public class PantallaFinal extends Pantalla {

    private final String TIEMPO;
    private final int PUNTAJE;

    private Texto titulo;
    private Texto resumenTiempo;
    private Texto resumenPuntaje;
    private Texto opcionMenu;
    private ArrayList<InfoDiaNivel> diasNiveles;
    private final boolean DESPEDIDO;
    private final String RAZON_DESPIDO;
    private Texto textoDespido;
    private Texto textoRazon;
    private final int PUNTAJE_TOTAL;
    private final ArrayList<Integer> PUNTAJES_NIVELES;

    private SpriteBatch batch;

    private float tiempoTranscurrido = 0f;
    private final float TIEMPO_MAXIMO = 10f;

    public PantallaFinal(String TIEMPO, int PUNTAJE, boolean DESPEDIDO, String RAZON_DESPIDO) {
        this.TIEMPO = TIEMPO;
        this.PUNTAJE = PUNTAJE;
        this.DESPEDIDO = DESPEDIDO;
        this.RAZON_DESPIDO = RAZON_DESPIDO;

        GestorPartida gestorPartida = GestorPartida.getInstancia();
        this.PUNTAJE_TOTAL = gestorPartida.getPuntajeTotalPartida();

        this.PUNTAJES_NIVELES = new ArrayList<>();
        for (NivelPartida nivel : gestorPartida.getTodosLosNiveles()) {
            PUNTAJES_NIVELES.add(nivel.getPuntajeObtenido());
        }
    }

    @Override
    public void show() {
        batch = Render.batch;

        if (DESPEDIDO) {
            titulo = new Texto(Recursos.FUENTE_MENU, 64, Color.RED, true);
            titulo.setTexto("¡HAS SIDO DESPEDIDO!");
        } else {
            titulo = new Texto(Recursos.FUENTE_MENU, 64, Color.WHITE, true);
            titulo.setTexto("¡Partida Finalizada!");
        }

        resumenPuntaje = new Texto(Recursos.FUENTE_MENU, 40, Color.YELLOW, true);
        resumenPuntaje.setTexto("Puntaje Total: " + PUNTAJE_TOTAL);

        opcionMenu = new Texto(Recursos.FUENTE_MENU, 28, Color.YELLOW, true);
        opcionMenu.setTexto("Presiona ENTER para volver al menú");

        resumenTiempo = new Texto(Recursos.FUENTE_MENU, 40, Color.CYAN, true);
        resumenTiempo.setTexto("Tiempo total: " + TIEMPO);

        if (DESPEDIDO) {
            textoDespido = new Texto(Recursos.FUENTE_MENU, 36, Color.RED, true);
            textoDespido.setTexto("Razón del despido:");

            textoRazon = new Texto(Recursos.FUENTE_MENU, 30, Color.ORANGE, true);
            textoRazon.setTexto(RAZON_DESPIDO);
        }

        diasNiveles = new ArrayList<>();
        GestorPartida gestorPartida = GestorPartida.getInstancia();
        ArrayList<NivelPartida> niveles = gestorPartida.getTodosLosNiveles();
        for (int i = 0; i < niveles.size(); i++) {
            NivelPartida nivel = niveles.get(i);
            int puntajeNivel = (i < PUNTAJES_NIVELES.size()) ? PUNTAJES_NIVELES.get(i) : 0;
            InfoDiaNivel info = new InfoDiaNivel(i + 1, nivel, puntajeNivel);
            diasNiveles.add(info);
        }

        posicionarTarjetas();
    }

    private void posicionarTarjetas() {
        float anchoVentana = Gdx.graphics.getWidth();
        float altoVentana = Gdx.graphics.getHeight();

        int cantidadDias = diasNiveles.size();
        float escalaX = anchoVentana / Gdx.graphics.getWidth();
        float escalaY = altoVentana / Gdx.graphics.getHeight();

        float anchoTotal = (cantidadDias * Recursos.ANCHO_DIA * escalaX) + ((cantidadDias - 1) * Recursos.ESPACIADO * escalaX);
        float inicioX = (anchoVentana - anchoTotal) / 2f;
        float posY = altoVentana / 2f + 50f * escalaY;

        for (int i = 0; i < diasNiveles.size(); i++) {
            InfoDiaNivel info = diasNiveles.get(i);
            float posX = inicioX + (i * (Recursos.ANCHO_DIA * escalaX + Recursos.ESPACIADO * escalaX));
            info.setPosicion(posX, posY);
        }

        posicionarTextosSegunPantalla();

        if (DESPEDIDO && textoDespido != null && textoRazon != null) {
            textoDespido.setPosition(Gdx.graphics.getWidth()/2f - textoDespido.getAncho()/2f,
                Gdx.graphics.getHeight() - 280 * escalaY);
            textoRazon.setPosition(Gdx.graphics.getWidth()/2f - textoRazon.getAncho()/2f,
                Gdx.graphics.getHeight() - 330 * escalaY);
            posicionarTextosSegunPantalla();
        }
    }

    private void posicionarTextosSegunPantalla() {
        float anchoVentana = Gdx.graphics.getWidth();
        float altoVentana = Gdx.graphics.getHeight();
        float escalaY = altoVentana / Gdx.graphics.getHeight();

        if (diasNiveles.isEmpty()) return;

        InfoDiaNivel primera = diasNiveles.get(0);

        float yTarjetas = primera.getY();
        float alturaTarjeta = Recursos.ALTO_DIA * escalaY;
        float margenSuperiorTarjetas = yTarjetas + alturaTarjeta;

        float separacionVertical = 40f * escalaY;
        float inicioY = Math.min(altoVentana - 100f * escalaY, margenSuperiorTarjetas + 100f * escalaY);

        titulo.setPosition(anchoVentana / 2f - titulo.getAncho() / 2f, inicioY);

        float yActual = inicioY - separacionVertical;
        resumenTiempo.setPosition(anchoVentana / 2f - resumenTiempo.getAncho() / 2f, yActual);

        yActual -= separacionVertical;
        resumenPuntaje.setPosition(anchoVentana / 2f - resumenPuntaje.getAncho() / 2f, yActual);

        if (DESPEDIDO && textoDespido != null && textoRazon != null) {
            yActual -= separacionVertical * 1.5f;
            textoDespido.setPosition(anchoVentana / 2f - textoDespido.getAncho() / 2f, yActual);

            yActual -= separacionVertical;
            textoRazon.setPosition(anchoVentana / 2f - textoRazon.getAncho() / 2f, yActual);
        }

        opcionMenu.setPosition(anchoVentana / 2f - opcionMenu.getAncho() / 2f, 80f * escalaY);
    }


    @Override
    public void render(float delta) {
        tiempoTranscurrido += delta;

        if (tiempoTranscurrido >= TIEMPO_MAXIMO) {
            Pantalla.cambiarPantalla(new PantallaMenu());
            return;
        }

        Gdx.gl.glClearColor(0, 0, 0, 1);
        Gdx.gl.glClear(GL20.GL_COLOR_BUFFER_BIT);

        batch.begin();
        titulo.dibujarEnUi(batch);
        resumenTiempo.dibujarEnUi(batch);
        resumenPuntaje.dibujarEnUi(batch);
        opcionMenu.dibujarEnUi(batch);

        if (DESPEDIDO && textoDespido != null && textoRazon != null) {
            textoDespido.dibujarEnUi(batch);
            textoRazon.dibujarEnUi(batch);
        }

        for (InfoDiaNivel info : diasNiveles) {
            info.dibujar(batch, false);
        }
        batch.end();

        if (Gdx.input.isKeyJustPressed(Input.Keys.ENTER)) {
            Pantalla.cambiarPantalla(new PantallaMenu());
        }
    }

    @Override
    public void resize(int width, int height) {
        if (titulo != null) {
            posicionarTarjetas();
        }
    }

    @Override public void pause() {}
    @Override public void resume() {}
    @Override public void hide() {}
    @Override public void dispose() {}
}
