package com.hebergames.letmecook.red;

import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.hebergames.letmecook.elementos.Texto;
import com.hebergames.letmecook.entregables.ingredientes.TipoEnvase;
import com.hebergames.letmecook.entregables.ingredientes.TipoIngrediente;
import com.hebergames.letmecook.entregables.productos.bebidas.Cafe;
import com.hebergames.letmecook.entregables.productos.bebidas.Gaseosa;
import com.hebergames.letmecook.entregables.productos.bebidas.TamanoBebida;
import com.hebergames.letmecook.utiles.Recursos;

import java.util.ArrayList;

public class VisualizadorMenuEstacion {
    private ArrayList<Texto> textosMenu;
    private Texto textoTitulo;
    private boolean visible;
    private boolean esJugador1;
    private String tipoEstacion;

    public VisualizadorMenuEstacion() {
        textosMenu = new ArrayList<>();
        visible = false;
    }

    public void mostrarMenuHeladera(boolean esJugador1) {
        this.esJugador1 = esJugador1;
        this.visible = true;
        this.tipoEstacion = "Heladera";

        textosMenu.clear();

        int numero = 1;
        for (TipoIngrediente tipo : TipoIngrediente.values()) {
            Texto texto = new Texto(Recursos.FUENTE_MENU, 24, Color.WHITE, true);
            texto.setTexto(numero + ". " + tipo.getNombre());
            textosMenu.add(texto);
            numero++;
            if (numero > 9) break;
        }
    }

    public void mostrarMenuMesa(boolean esJugador1, ArrayList<String> objetosEnMesa) {
        this.esJugador1 = esJugador1;
        this.visible = true;
        this.tipoEstacion = "Mesa";

        textosMenu.clear();

        // Opción 1 y 2: Slots
        for (int i = 0; i < 2; i++) {
            Texto texto = new Texto(Recursos.FUENTE_MENU, 24, Color.WHITE, true);
            String contenido = (i < objetosEnMesa.size()) ? objetosEnMesa.get(i) : "Vacío";
            texto.setTexto((i + 1) + ". Slot " + (i + 1) + " [" + contenido + "]");
            textosMenu.add(texto);
        }

        // Opción 3: Crear producto
        Texto textoCrear = new Texto(Recursos.FUENTE_MENU, 24, Color.WHITE, true);
        textoCrear.setTexto("3. Crear Producto");
        textosMenu.add(textoCrear);

        // Opción 4: Retirar producto
        Texto textoRetirar = new Texto(Recursos.FUENTE_MENU, 24, Color.WHITE, true);
        textoRetirar.setTexto("4. Retirar Producto");
        textosMenu.add(textoRetirar);
    }

    public void mostrarMenuCafetera(boolean esJugador1, String estadoActual, float progreso) {
        this.esJugador1 = esJugador1;
        this.visible = true;
        this.tipoEstacion = "Cafetera";

        textosMenu.clear();

        switch (estadoActual) {
            case "SELECCION_TAMANO":
                textoTitulo = new Texto(Recursos.FUENTE_MENU, 20, Color.WHITE, true);
                textoTitulo.setTexto("Selecciona tamaño:");

                String[] tamanos = {"Pequeño", "Mediano", "Grande"};
                for (int i = 0; i < tamanos.length; i++) {
                    Texto texto = new Texto(Recursos.FUENTE_MENU, 16, Color.YELLOW, true);
                    texto.setTexto((i + 1) + ". " + tamanos[i]);
                    textosMenu.add(texto);
                }
                break;

            case "SELECCION_TIPO":
                textoTitulo = new Texto(Recursos.FUENTE_MENU, 20, Color.WHITE, true);
                textoTitulo.setTexto("Selecciona tipo de café:");

                String[] tipos = Cafe.getTiposCafe().keySet().toArray(new String[0]);
                for (int i = 0; i < tipos.length; i++) {
                    Texto texto = new Texto(Recursos.FUENTE_MENU, 16, Color.YELLOW, true);
                    texto.setTexto((i + 1) + ". " + tipos[i]);
                    textosMenu.add(texto);
                }
                break;

            case "PREPARANDO":
                textoTitulo = new Texto(Recursos.FUENTE_MENU, 20, Color.WHITE, true);
                textoTitulo.setTexto(String.format("Preparando... %.0f%%", progreso * 100f));
                break;

            case "LISTO":
                textoTitulo = new Texto(Recursos.FUENTE_MENU, 20, Color.WHITE, true);
                textoTitulo.setTexto("¡Café listo! Presiona E");
                break;
        }
    }

    public void mostrarMenuFuente(boolean esJugador1, String estadoActual, float progreso) {
        this.esJugador1 = esJugador1;
        this.visible = true;
        this.tipoEstacion = "Fuente";

        textosMenu.clear();

        switch (estadoActual) {
            case "SELECCION_TAMANO":
                textoTitulo = new Texto(Recursos.FUENTE_MENU, 20, Color.WHITE, true);
                textoTitulo.setTexto("Selecciona tamaño:");

                String[] tamanos = {"Pequeño", "Mediano", "Grande"};
                for (int i = 0; i < tamanos.length; i++) {
                    Texto texto = new Texto(Recursos.FUENTE_MENU, 16, Color.YELLOW, true);
                    texto.setTexto((i + 1) + ". " + tamanos[i]);
                    textosMenu.add(texto);
                }
                break;

            case "SELECCION_TIPO":
                textoTitulo = new Texto(Recursos.FUENTE_MENU, 20, Color.WHITE, true);
                textoTitulo.setTexto("Selecciona bebida:");

                String[] tipos = Gaseosa.getTiposGaseosa().keySet().toArray(new String[0]);
                for (int i = 0; i < tipos.length; i++) {
                    Texto texto = new Texto(Recursos.FUENTE_MENU, 16, Color.YELLOW, true);
                    texto.setTexto((i + 1) + ". " + tipos[i]);
                    textosMenu.add(texto);
                }
                break;

            case "PREPARANDO":
                textoTitulo = new Texto(Recursos.FUENTE_MENU, 20, Color.WHITE, true);
                textoTitulo.setTexto(String.format("Sirviendo... %.0f%%", progreso * 100f));
                break;

            case "LISTO":
                textoTitulo = new Texto(Recursos.FUENTE_MENU, 20, Color.WHITE, true);
                textoTitulo.setTexto("¡Bebida lista! Presiona E");
                break;
        }
    }

    public void mostrarMenuEnvasadora(boolean esJugador1, String nombreIngrediente) {
        this.esJugador1 = esJugador1;
        this.visible = true;
        this.tipoEstacion = "MaquinaEnvasadora";

        textosMenu.clear();

        Texto texto = new Texto(Recursos.FUENTE_MENU, 24, Color.WHITE, true);
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

    public void ocultar() {
        visible = false;
        textosMenu.clear();
        textoTitulo = null;
    }

    public void dibujar(SpriteBatch batch, float anchoUI, float altoUI) {
        if (!visible) return;

        float anchoMenu = 400f;
        float MARGEN = 50f;
        float x = esJugador1 ? MARGEN : anchoUI - anchoMenu - MARGEN;

        // Dibujar título si existe
        if (textoTitulo != null) {
            textoTitulo.setPosition(x, altoUI / 2f + 100);
            textoTitulo.dibujarEnUi(batch);
        }

        // Dibujar opciones
        float ESPACIADO = 40f;
        float alturaTotal = textosMenu.size() * ESPACIADO;
        float y = (altoUI / 2f) + (alturaTotal / 2f);

        for (Texto texto : textosMenu) {
            texto.setPosition(x, y);
            texto.dibujarEnUi(batch);
            y -= ESPACIADO;
        }
    }

    public boolean isVisible() {
        return visible;
    }
}
