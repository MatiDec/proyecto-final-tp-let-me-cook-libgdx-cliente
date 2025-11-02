package com.hebergames.letmecook.red;

import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.hebergames.letmecook.elementos.Texto;
import com.hebergames.letmecook.entregables.ingredientes.TipoEnvase;
import com.hebergames.letmecook.entregables.ingredientes.TipoIngrediente;
import com.hebergames.letmecook.entregables.productos.bebidas.Cafe;
import com.hebergames.letmecook.entregables.productos.bebidas.Gaseosa;
import com.hebergames.letmecook.utiles.Recursos;

import java.util.ArrayList;

public class VisualizadorMenuEstacion {
    private final ArrayList<Texto> textosMenu = new ArrayList<>();
    private final ArrayList<Texto> poolTextos = new ArrayList<>(); // pool reutilizable
    private Texto textoTitulo;
    private boolean visible;
    private boolean esJugador1;
    private String tipoEstacion;

    private static final int TAMANIO_POOL_INICIAL = 15;

    public VisualizadorMenuEstacion() {
        // Crear un pequeño pool inicial de textos
        for (int i = 0; i < TAMANIO_POOL_INICIAL; i++) {
            poolTextos.add(new Texto(Recursos.FUENTE_MENU, 20, Color.WHITE, true));
        }
        visible = false;
    }

    /** Obtiene un Texto disponible del pool (reutiliza o crea si no hay) */
    private Texto obtenerTextoLibre() {
        if (!poolTextos.isEmpty()) {
            return poolTextos.remove(poolTextos.size() - 1);
        }
        // Si se necesitan más, se crean solo cuando sea necesario
        return new Texto(Recursos.FUENTE_MENU, 20, Color.WHITE, true);
    }

    /** Devuelve un texto al pool */
    private void liberarTexto(Texto texto) {
        texto.setTexto("");
        poolTextos.add(texto);
    }

    private void limpiarTextos() {
        for (Texto t : textosMenu) liberarTexto(t);
        textosMenu.clear();
    }

    public void mostrarMenuHeladera(boolean esJugador1) {
        this.esJugador1 = esJugador1;
        this.visible = true;
        this.tipoEstacion = "Heladera";

        limpiarTextos();
        textoTitulo = obtenerTextoLibre();
        textoTitulo.setTexto("Heladera");

        int numero = 1;
        for (TipoIngrediente tipo : TipoIngrediente.values()) {
            if (numero > 9) break;
            Texto texto = obtenerTextoLibre();
            texto.setTexto(numero + ". " + tipo.getNombre());
            textosMenu.add(texto);
            numero++;
        }
    }

    public void mostrarMenuMesa(boolean esJugador1, ArrayList<String> objetosEnMesa) {
        this.esJugador1 = esJugador1;
        this.visible = true;
        this.tipoEstacion = "Mesa";

        limpiarTextos();
        textoTitulo = obtenerTextoLibre();
        textoTitulo.setTexto("Mesa");

        for (int i = 0; i < 2; i++) {
            Texto t = obtenerTextoLibre();
            String contenido = i < objetosEnMesa.size() ? objetosEnMesa.get(i) : "Vacío";
            t.setTexto((i + 1) + ". Slot " + (i + 1) + " [" + contenido + "]");
            textosMenu.add(t);
        }

        Texto crear = obtenerTextoLibre();
        crear.setTexto("3. Crear Producto");
        textosMenu.add(crear);

        Texto retirar = obtenerTextoLibre();
        retirar.setTexto("4. Retirar Producto");
        textosMenu.add(retirar);
    }

    public void mostrarMenuCafetera(boolean esJugador1, String estadoActual, float progreso) {
        this.esJugador1 = esJugador1;
        this.visible = true;
        this.tipoEstacion = "Cafetera";
        limpiarTextos();

        textoTitulo = obtenerTextoLibre();
        switch (estadoActual) {
            case "SELECCION_TAMANO":
                textoTitulo.setTexto("Selecciona tamaño:");
                agregarOpciones(Cafe.getTiposCafe().keySet().toArray(new String[0]));
                break;
            case "SELECCION_TIPO":
                textoTitulo.setTexto("Selecciona tipo de café:");
                agregarOpciones(Cafe.getTiposCafe().keySet().toArray(new String[0]));
                break;
            case "PREPARANDO":
                textoTitulo.setTexto(String.format("Preparando... %.0f%%", progreso * 100f));
                break;
            case "LISTO":
                textoTitulo.setTexto("¡Café listo! Presiona E");
                break;
        }
    }

    public void mostrarMenuFuente(boolean esJugador1, String estadoActual, float progreso) {
        this.esJugador1 = esJugador1;
        this.visible = true;
        this.tipoEstacion = "Fuente";
        limpiarTextos();

        textoTitulo = obtenerTextoLibre();
        switch (estadoActual) {
            case "SELECCION_TAMANO":
                textoTitulo.setTexto("Selecciona tamaño:");
                agregarOpciones(new String[]{"Pequeño", "Mediano", "Grande"});
                break;
            case "SELECCION_TIPO":
                textoTitulo.setTexto("Selecciona bebida:");
                agregarOpciones(Gaseosa.getTiposGaseosa().keySet().toArray(new String[0]));
                break;
            case "PREPARANDO":
                textoTitulo.setTexto(String.format("Sirviendo... %.0f%%", progreso * 100f));
                break;
            case "LISTO":
                textoTitulo.setTexto("¡Bebida lista! Presiona E");
                break;
        }
    }

    public void mostrarMenuEnvasadora(boolean esJugador1, String nombreIngrediente) {
        this.esJugador1 = esJugador1;
        this.visible = true;
        this.tipoEstacion = "MaquinaEnvasadora";
        limpiarTextos();

        textoTitulo = obtenerTextoLibre();
        textoTitulo.setTexto("Envasadora");

        Texto texto = obtenerTextoLibre();
        String textoOpcion = "1. Envasar Ingrediente";

        if (nombreIngrediente != null && !nombreIngrediente.equals("vacio")) {
            TipoEnvase tipoEnvase = TipoEnvase.obtenerPorIngrediente(nombreIngrediente);
            if (tipoEnvase != null) {
                textoOpcion += " [" + nombreIngrediente + " → " + tipoEnvase.getNombre() + "]";
            } else {
                textoOpcion += " [No válido]";
            }
        } else {
            textoOpcion += " [Sin ingrediente]";
        }

        texto.setTexto(textoOpcion);
        textosMenu.add(texto);
    }

    private void agregarOpciones(String[] opciones) {
        for (int i = 0; i < opciones.length; i++) {
            Texto t = obtenerTextoLibre();
            t.setTexto((i + 1) + ". " + opciones[i]);
            textosMenu.add(t);
        }
    }

    public void ocultar() {
        visible = false;
        if (textoTitulo != null) {
            liberarTexto(textoTitulo);
            textoTitulo = null;
        }
        limpiarTextos();
    }

    public void dibujar(SpriteBatch batch, float anchoUI, float altoUI) {
        if (!visible) return;

        float margen = 50f;
        float anchoMenu = 400f;
        float x = esJugador1 ? margen : anchoUI - anchoMenu - margen;

        if (textoTitulo != null) {
            textoTitulo.setPosition(x, altoUI / 2f + 100);
            textoTitulo.dibujarEnUi(batch);
        }

        float espaciado = 40f;
        float alturaTotal = textosMenu.size() * espaciado;
        float y = (altoUI / 2f) + (alturaTotal / 2f);

        for (Texto t : textosMenu) {
            t.setPosition(x, y);
            t.dibujarEnUi(batch);
            y -= espaciado;
        }
    }

    public boolean isVisible() {
        return visible;
    }
}
