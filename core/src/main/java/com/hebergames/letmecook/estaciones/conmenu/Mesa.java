package com.hebergames.letmecook.estaciones.conmenu;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.math.Rectangle;
import com.hebergames.letmecook.entidades.Jugador;
import com.hebergames.letmecook.elementos.Texto;
import com.hebergames.letmecook.entregables.ObjetoAlmacenable;
import com.hebergames.letmecook.entregables.ingredientes.Ingrediente;
import com.hebergames.letmecook.entregables.productos.Producto;
import com.hebergames.letmecook.entregables.recetas.GestorRecetas;
import com.hebergames.letmecook.entregables.recetas.Receta;
import com.hebergames.letmecook.sonido.GestorAudio;
import com.hebergames.letmecook.sonido.SonidoJuego;
import com.hebergames.letmecook.utiles.GestorJugadores;
import com.hebergames.letmecook.utiles.Recursos;

import java.util.ArrayList;
import java.util.List;

public class Mesa extends EstacionConMenu {

    private final int MAX_SLOTS = 2;
    private List<OpcionMenu> opcionesMenu;
    private List<Texto> textosMenu;

    private final ObjetoAlmacenable[] SLOTS;

    private Producto productoPreparado;

    public Mesa(Rectangle area) {
        super(area);
        SLOTS = new ObjetoAlmacenable[MAX_SLOTS];
        productoPreparado = null;
        inicializarOpciones();
    }

    @Override
    public void alLiberar() {

    }

    private void inicializarOpciones() {
        opcionesMenu = new ArrayList<>();

        opcionesMenu.add(new OpcionMenu(1, "Slot 1", () -> depositarEnSlot(0)));
        opcionesMenu.add(new OpcionMenu(2, "Slot 2", () -> depositarEnSlot(1)));
        opcionesMenu.add(new OpcionMenu(3, "Crear Producto", () -> crearProducto()));
        opcionesMenu.add(new OpcionMenu(4, "Retirar Producto", () -> retirarProducto()));
    }

    private void depositarEnSlot(int slotIndex) {
        if (slotIndex < 0 || slotIndex >= MAX_SLOTS) {
            return;
        }

        Jugador jugador = getJugadorOcupante();
        if (jugador == null) {
            return;
        }

        if (SLOTS[slotIndex] != null) {
            if (!jugador.tieneInventarioLleno()) {
                ObjetoAlmacenable objetoRetirado = SLOTS[slotIndex];
                SLOTS[slotIndex] = null;
                jugador.guardarEnInventario(objetoRetirado);
            }
            return;
        }

        ObjetoAlmacenable objeto = jugador.getInventario();

        if (objeto == null) {
            return;
        }

        SLOTS[slotIndex] = objeto;
        jugador.sacarDeInventario();
    }

    private void crearProducto() {
        if (productoPreparado != null) {
            return;
        }

        ArrayList<Ingrediente> ingredientesDisponibles = new ArrayList<>();
        for (ObjetoAlmacenable objeto : SLOTS) {
            if (objeto instanceof Ingrediente) {
                ingredientesDisponibles.add((Ingrediente) objeto);
            }
        }

        if (ingredientesDisponibles.isEmpty()) {
            return;
        }

        Receta receta = GestorRecetas.getInstance().buscarReceta(ingredientesDisponibles);

        if (receta == null) {
            return;
        }

        productoPreparado = receta.preparar();

        for (int i = 0; i < SLOTS.length; i++) {
            SLOTS[i] = null;
        }
    }

    private void retirarProducto() {
        Jugador jugador = getJugadorOcupante();
        if (jugador == null) {
            return;
        }

        if (productoPreparado == null) {
            return;
        }

        if (jugador.tieneInventarioLleno()) {
            return;
        }

        jugador.guardarEnInventario(productoPreparado);
        GestorAudio.getInstance().reproducirSonido(SonidoJuego.ITEM_RECOGIDO);
        productoPreparado = null;
    }

    @Override
    public void iniciarMenu(Jugador jugador) {
        textosMenu = new ArrayList<>();

        for (OpcionMenu opcion : opcionesMenu) {
            Texto texto = new Texto(Recursos.FUENTE_MENU, 24, Color.WHITE, true);

            String textoOpcion = opcion.getTextoMenu();
            if (opcion.getNumero() <= MAX_SLOTS) {
                int slotIndex = opcion.getNumero() - 1;
                if (SLOTS[slotIndex] != null) {
                    textoOpcion += " [" + SLOTS[slotIndex].getNombre() + "]";
                } else {
                    textoOpcion += " [Vacío]";
                }
            } else if (opcion.getNumero() == 4) {
                if (productoPreparado != null) {
                    textoOpcion += " [" + productoPreparado.getNombre() + "]";
                } else {
                    textoOpcion += " [No disponible]";
                }
            }

            texto.setTexto(textoOpcion);
            textosMenu.add(texto);
        }
    }

    @Override
    public void manejarSeleccionMenu(Jugador jugador, int numeroSeleccion) {
        for (OpcionMenu opcion : opcionesMenu) {
            if (opcion.getNumero() == numeroSeleccion) {
                if (opcion.esAccionSimple()) {
                    opcion.ejecutarAccion();
                    iniciarMenu(jugador);
                }
                break;
            }
        }
    }

    @Override
    protected void dibujarMenu(SpriteBatch batch, Jugador jugador) {
        if (textosMenu == null || textosMenu.isEmpty()) return;

        List<Jugador> jugadores = GestorJugadores.getInstancia().getJugadores();
        boolean esJugador1 = (!jugadores.isEmpty() && jugador == jugadores.get(0));

        float anchoMenu = 400f;
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
        iniciarMenu(getJugadorOcupante());
    }

}
