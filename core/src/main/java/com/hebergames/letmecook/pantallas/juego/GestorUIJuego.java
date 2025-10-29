package com.hebergames.letmecook.pantallas.juego;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.hebergames.letmecook.elementos.Texto;
import com.hebergames.letmecook.entidades.clientes.Cliente;
import com.hebergames.letmecook.pedidos.TarjetaPedido;
import com.hebergames.letmecook.utiles.GestorTexturas;
import com.hebergames.letmecook.utiles.Recursos;

import java.util.ArrayList;

public class GestorUIJuego {

    private final ArrayList<ObjetoVisualizable> objetosUI;
    private final Texto textoContador;
    private final Texto textoInventario1;
    private final Texto textoInventario2;

    private static final float MARGEN = 50f;
    private final ArrayList<Texto> TEXTOS_PEDIDOS;
    private final ArrayList<TarjetaPedido> TARJETAS_PEDIDOS;

    private final Texto TEXTO_PUNTAJE;
    private final Texto TEXTO_INDICADOR_CALENDARIO;
    private final int MAX_PEDIDOS_VISIBLES = 5;

    public GestorUIJuego() {
        objetosUI = new ArrayList<>();

        textoContador = new Texto(Recursos.FUENTE_MENU, 32, Color.WHITE, true);
        textoContador.setTexto("00:00");

        textoInventario1 = new Texto(Recursos.FUENTE_MENU, 32, Color.GREEN, true);
        textoInventario1.setTexto("J1 Inventario: Vacío");

        textoInventario2 = new Texto(Recursos.FUENTE_MENU, 32, Color.BLUE, true);
        textoInventario2.setTexto("J2 Inventario: Vacío");

        TEXTO_PUNTAJE = new Texto(Recursos.FUENTE_MENU, 32, Color.YELLOW, true);
        TEXTO_PUNTAJE.setTexto("Puntos: 0");
        objetosUI.add(TEXTO_PUNTAJE);

        TEXTO_INDICADOR_CALENDARIO = new Texto(Recursos.FUENTE_MENU, 24, Color.CYAN, true);
        TEXTO_INDICADOR_CALENDARIO.setTexto("[TAB] Calendario");
        objetosUI.add(TEXTO_INDICADOR_CALENDARIO);

        TEXTOS_PEDIDOS = new ArrayList<>();

        TARJETAS_PEDIDOS = new ArrayList<>();

        objetosUI.add(textoContador);
        objetosUI.add(textoInventario1);
        objetosUI.add(textoInventario2);

        actualizarPosiciones(Gdx.graphics.getWidth(), Gdx.graphics.getHeight());
    }

    public void actualizarPedidosActivos(ArrayList<Cliente> clientes) {
        for (TarjetaPedido t : TARJETAS_PEDIDOS) {
            t.dispose();
        }
        TARJETAS_PEDIDOS.clear();
        TEXTOS_PEDIDOS.clear();

        for (int i = 0; i < Math.min(clientes.size(), MAX_PEDIDOS_VISIBLES); i++) {
            TarjetaPedido tarjeta = new TarjetaPedido();
            TARJETAS_PEDIDOS.add(tarjeta);
        }
    }


    public void actualizarTiempo(int segundos) {
        int minutos = segundos / 60;
        int segundosRestantes = segundos % 60;
        String tiempoFormateado = String.format("%02d:%02d", minutos, segundosRestantes);
        textoContador.setTexto(tiempoFormateado);
    }

    public void actualizarPuntaje(int puntos) {
        TEXTO_PUNTAJE.setTexto("Puntos: " + puntos);
    }

    public void actualizarInventario(String nombreItem1, String nombreItem2) {
        if (nombreItem1 != null) {
            textoInventario1.setTexto("J1 Inventario: " + nombreItem1);
        }
        if (nombreItem2 != null) {
            textoInventario2.setTexto("J2 Inventario: " + nombreItem2);
        }

    }

    public void dibujar(SpriteBatch batch) {
        for (ObjetoVisualizable obj : objetosUI) {
            obj.dibujarEnUi(batch);
        }
        for (Texto texto : TEXTOS_PEDIDOS) {
            texto.dibujarEnUi(batch);
        }
    }

    public void dibujarPedidos(SpriteBatch batch, ArrayList<Cliente> clientes, float anchoUI, float altoUI) {
        float yInicial = altoUI / 2f;
        float x = anchoUI - 220f;

        for (int i = 0; i < Math.min(clientes.size(), MAX_PEDIDOS_VISIBLES); i++) {
            Cliente cliente = clientes.get(i);
            float y = yInicial - (i * 120f);

            if (i < TARJETAS_PEDIDOS.size()) {
                TARJETAS_PEDIDOS.get(i).dibujar(batch, cliente, x, y,
                    GestorTexturas.getInstance().getTexturaCliente());
            }
        }
    }

    public void actualizarPosiciones(float anchoUI, float altoUI) {
        textoContador.setPosition(anchoUI / 2f - textoContador.getAncho() / 2f, altoUI - MARGEN);
        textoInventario1.setPosition(MARGEN, altoUI - MARGEN);
        textoInventario2.setPosition(anchoUI - textoInventario2.getAncho() - MARGEN, altoUI - MARGEN);
        TEXTO_PUNTAJE.setPosition(MARGEN, altoUI - MARGEN * 2);

        float margenPedidos = 100f;
        float yInicialPedidos = altoUI - margenPedidos;
        for (int i = 0; i < TEXTOS_PEDIDOS.size(); i++) {
            Texto texto = TEXTOS_PEDIDOS.get(i);
            float yPos = yInicialPedidos - (i * 30);
            texto.setPosition(anchoUI - texto.getAncho() - MARGEN, yPos);
        }

        TEXTO_INDICADOR_CALENDARIO.setPosition(
            anchoUI / 2f - TEXTO_INDICADOR_CALENDARIO.getAncho() / 2f,
            MARGEN / 2f
        );
    }

    public void dispose() {
        for (TarjetaPedido tarjeta : TARJETAS_PEDIDOS) {
            tarjeta.dispose();
        }
        TARJETAS_PEDIDOS.clear();
    }

}
