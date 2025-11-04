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
    private final ArrayList<Texto> TEXTOS_MENU = new ArrayList<>();
    private final ArrayList<Texto> POOL_TEXTOS = new ArrayList<>();
    private Texto textoTitulo;
    private boolean visible;
    private boolean esJugador1;
    private String tipoEstacion;

    private final int TAMANIO_POOL_INICIAL = 15;

    public VisualizadorMenuEstacion() {
        for (int i = 0; i < TAMANIO_POOL_INICIAL; i++) {
            POOL_TEXTOS.add(new Texto(Recursos.FUENTE_MENU, 20, Color.WHITE, true));
        }
        visible = false;
    }

    private Texto obtenerTextoLibre() {
        if (!POOL_TEXTOS.isEmpty()) {
            return POOL_TEXTOS.remove(POOL_TEXTOS.size() - 1);
        }
        return new Texto(Recursos.FUENTE_MENU, 20, Color.WHITE, true);
    }

    private void liberarTexto(Texto texto) {
        texto.setTexto("");
        POOL_TEXTOS.add(texto);
    }

    private void limpiarTextos() {
        for (Texto t : TEXTOS_MENU) liberarTexto(t);
        TEXTOS_MENU.clear();
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
            TEXTOS_MENU.add(texto);
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
            TEXTOS_MENU.add(t);
        }

        Texto crear = obtenerTextoLibre();
        crear.setTexto("3. Crear Producto");
        TEXTOS_MENU.add(crear);

        Texto retirar = obtenerTextoLibre();
        retirar.setTexto("4. Retirar Producto");
        TEXTOS_MENU.add(retirar);
    }

    public void mostrarMenuCafetera(boolean esJugador1, String estadoActual, float progreso) {
        this.esJugador1 = esJugador1;
        this.tipoEstacion = "Cafetera";
        this.visible = true;

        if ("PREPARANDO".equals(estadoActual) && textoTitulo != null && TEXTOS_MENU.isEmpty()) {
            textoTitulo.setTexto(String.format("Preparando... %.0f%%", progreso * 100f));
            return;
        }

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
            default:
                textoTitulo.setTexto("Cafetera");
                break;
        }
    }

    public void mostrarMenuFuente(boolean esJugador1, String estadoActual, float progreso) {
        this.esJugador1 = esJugador1;
        this.tipoEstacion = "Fuente";
        this.visible = true;

        if ("PREPARANDO".equals(estadoActual) && textoTitulo != null && TEXTOS_MENU.isEmpty()) {
            textoTitulo.setTexto(String.format("Sirviendo... %.0f%%", progreso * 100f));
            return;
        }

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
            default:
                textoTitulo.setTexto("Fuente");
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
        TEXTOS_MENU.add(texto);
    }

    private void agregarOpciones(String[] opciones) {
        for (int i = 0; i < opciones.length; i++) {
            Texto t = obtenerTextoLibre();
            t.setTexto((i + 1) + ". " + opciones[i]);
            TEXTOS_MENU.add(t);
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

        float espaciado = 40f;
        float alturaTotal = (TEXTOS_MENU.size() + 1) * espaciado;
        float yInicial = (altoUI / 2f) + (alturaTotal / 2f);

        if (textoTitulo != null) {
            textoTitulo.setPosition(x, yInicial);
            textoTitulo.dibujarEnUi(batch);
            yInicial -= espaciado * 1.5f;
        }

        float y = yInicial;
        for (Texto t : TEXTOS_MENU) {
            t.setPosition(x, y);
            t.dibujarEnUi(batch);
            y -= espaciado;
        }
    }

    public boolean isVisible() {
        return visible;
    }
}
