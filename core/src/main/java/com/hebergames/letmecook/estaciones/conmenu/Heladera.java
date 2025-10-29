package com.hebergames.letmecook.estaciones.conmenu;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.math.Rectangle;
import com.hebergames.letmecook.entidades.Jugador;
import com.hebergames.letmecook.entregables.ObjetoAlmacenable;
import com.hebergames.letmecook.entregables.ingredientes.*;
import com.hebergames.letmecook.elementos.Texto;
import com.hebergames.letmecook.sonido.GestorAudio;
import com.hebergames.letmecook.sonido.SonidoJuego;
import com.hebergames.letmecook.utiles.GestorJugadores;
import com.hebergames.letmecook.utiles.Recursos;

import java.util.ArrayList;
import java.util.List;

public class Heladera extends EstacionConMenu {

    private List<OpcionMenu> opcionesMenu;
    private List<Texto> textosMenu;

    private final GestorIngredientes GESTOR_INGREDIENTES;

    public Heladera(Rectangle area) {
        super(area);
        GESTOR_INGREDIENTES = GestorIngredientes.getInstance();
        inicializarOpciones();
    }

    private void inicializarOpciones() {
        opcionesMenu = new ArrayList<>();

        int numero = 1;
        for (TipoIngrediente tipo : TipoIngrediente.values()) {
            int numeroFinal = numero;

            opcionesMenu.add(new OpcionMenu(
                numeroFinal,
                tipo.getNombre(),
                () -> GESTOR_INGREDIENTES.crearIngrediente(tipo)
            ));

            numero++;
            if (numero > 9) break;
        }
    }

    @Override
    public void alLiberar() {
        textosMenu = null;
    }

    @Override
    public void iniciarMenu(Jugador jugador) {
        textosMenu = new ArrayList<>();

        for (OpcionMenu opcion : opcionesMenu) {
            Texto texto = new Texto(Recursos.FUENTE_MENU, 24, Color.WHITE, true);
            texto.setTexto(opcion.getTextoMenu());
            textosMenu.add(texto);
        }
    }

    @Override
    public void manejarSeleccionMenu(Jugador jugador, int numeroSeleccion) {
        int i = 0;
        boolean encontrado = false;

        while (i < opcionesMenu.size() && !encontrado) {
            OpcionMenu opcion = opcionesMenu.get(i);

            if (opcion.getNumero() == numeroSeleccion) {
                encontrado = true;

                if (!jugador.tieneInventarioLleno()) {
                    ObjetoAlmacenable objeto = opcion.crearObjeto();
                    if (objeto != null) {
                        jugador.guardarEnInventario(objeto);
                        GestorAudio.getInstance().reproducirSonido(SonidoJuego.ITEM_RECOGIDO);
                    }
                }
            }

            i++;
        }
    }

    @Override
    protected void dibujarMenu(SpriteBatch batch, Jugador jugador) {
        if (textosMenu == null || textosMenu.isEmpty()) {
            return;
        }

        List<Jugador> jugadores = GestorJugadores.getInstancia().getJugadores();
        boolean esJugador1 = (!jugadores.isEmpty() && jugador == jugadores.get(0));

        float anchoMenu = 200f;
        float MARGEN = 50f;
        float x = esJugador1 ? MARGEN : Gdx.graphics.getWidth() - anchoMenu - MARGEN;

        float ESPACIADO = 40f;
        float alturaTotal = textosMenu.size() * ESPACIADO;
        float y = (Gdx.graphics.getHeight() / 2f) + (alturaTotal / 2f);

        for (Texto texto : textosMenu) {
            texto.setPosition(x, y);
            texto.dibujarEnUi(batch);
            y -= ESPACIADO;
        }
    }

    @Override
    public void alInteractuar() {
        Jugador jugador = getJugadorOcupante();
        if (jugador == null) {
            return;
        }

        if (textosMenu == null) {
            iniciarMenu(jugador);
        }
    }
}
