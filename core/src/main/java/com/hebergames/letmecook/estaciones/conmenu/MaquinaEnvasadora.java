package com.hebergames.letmecook.estaciones.conmenu;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.math.Rectangle;
import com.hebergames.letmecook.elementos.Texto;
import com.hebergames.letmecook.entidades.Jugador;
import com.hebergames.letmecook.entregables.ObjetoAlmacenable;
import com.hebergames.letmecook.entregables.ingredientes.Ingrediente;
import com.hebergames.letmecook.entregables.ingredientes.TipoEnvase;
import com.hebergames.letmecook.entregables.productos.Producto;
import com.hebergames.letmecook.entregables.recetas.GestorRecetas;
import com.hebergames.letmecook.entregables.recetas.Receta;
import com.hebergames.letmecook.utiles.GestorJugadores;
import com.hebergames.letmecook.utiles.Recursos;

import java.util.ArrayList;
import java.util.List;

public class MaquinaEnvasadora extends EstacionConMenu {

    private List<OpcionMenu> opcionesMenu;
    private List<Texto> textosMenu;

    public MaquinaEnvasadora(Rectangle area) {
        super(area);
        inicializarOpciones();
    }

    @Override
    public void alLiberar() {}

    private void inicializarOpciones() {
        opcionesMenu = new ArrayList<>();
        opcionesMenu.add(new OpcionMenu(1, "Envasar Ingrediente", this::envasarIngrediente));
    }

    private void envasarIngrediente() {
        Jugador jugador = getJugadorOcupante();
        if (jugador == null) {
            return;
        }

        ObjetoAlmacenable objetoInventario = jugador.getInventario();

        if (!(objetoInventario instanceof Ingrediente)) {
            return;
        }

        Ingrediente ingrediente = (Ingrediente) objetoInventario;
        String nombreIngrediente = ingrediente.getNombre();

        TipoEnvase tipoEnvase = TipoEnvase.obtenerPorIngrediente(nombreIngrediente);

        if (tipoEnvase == null) {
            return;
        }

        Ingrediente envase = tipoEnvase.crearEnvase();

        ArrayList<Ingrediente> ingredientes = new ArrayList<>();
        ingredientes.add(envase);
        ingredientes.add(ingrediente);

        Receta receta = GestorRecetas.getInstance().buscarReceta(ingredientes);

        if (receta == null) {
            return;
        }

        Producto productoEnvasado = receta.preparar();

        jugador.sacarDeInventario();
        jugador.guardarEnInventario(productoEnvasado);
    }

    @Override
    public void iniciarMenu(Jugador jugador) {
        textosMenu = new ArrayList<>();

        Texto texto = new Texto(Recursos.FUENTE_MENU, 24, Color.WHITE, true);

        ObjetoAlmacenable objeto = jugador.getInventario();
        String textoOpcion = "1. Envasar Ingrediente";

        if (objeto instanceof Ingrediente) {
            Ingrediente ingrediente = (Ingrediente) objeto;
            TipoEnvase tipoEnvase = TipoEnvase.obtenerPorIngrediente(ingrediente.getNombre());

            if (tipoEnvase != null) {
                textoOpcion += " [" + ingrediente.getNombre() + " → " + tipoEnvase.getNombre() + "]";
            } else {
                textoOpcion += " [No válido]";
            }
        } else {
            textoOpcion += " [Sin ingrediente]";
        }

        texto.setTexto(textoOpcion);
        textosMenu.add(texto);
    }

    @Override
    public void manejarSeleccionMenu(Jugador jugador, int numeroSeleccion) {
        for (OpcionMenu opcion : opcionesMenu) {
            if (opcion.getNumero() == numeroSeleccion) {
                if (opcion.esAccionSimple()) {
                    opcion.ejecutarAccion();
                    iniciarMenu(jugador);

                    if (getJugadorOcupante() != null) {
                        jugador.salirDeMenu();
                        alLiberar();
                    }
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

        float anchoMenu = 500f;
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
