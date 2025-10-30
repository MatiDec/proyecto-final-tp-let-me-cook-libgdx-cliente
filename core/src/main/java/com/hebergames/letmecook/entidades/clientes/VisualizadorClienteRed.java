package com.hebergames.letmecook.entidades.clientes;

import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.graphics.g2d.TextureRegion;
import com.hebergames.letmecook.estaciones.EstacionTrabajo;
import com.hebergames.letmecook.red.paquetes.DatosCliente;
import com.hebergames.letmecook.utiles.GestorTexturas;
import com.hebergames.letmecook.utiles.Recursos;

import java.util.ArrayList;

public class VisualizadorClienteRed {
    private DatosCliente datos;
    private TextureRegion texturaCliente;

    public VisualizadorClienteRed(DatosCliente datos) {
        this.datos = datos;
        this.texturaCliente = GestorTexturas.getInstance().getTexturaCliente();
    }

    public void actualizar(DatosCliente nuevosDatos) {
        this.datos = nuevosDatos;
    }

    public void dibujar(SpriteBatch batch, ArrayList<EstacionTrabajo> estaciones) {
        if (datos.indexEstacion < 0 || datos.indexEstacion >= estaciones.size()) {
            return;
        }

        EstacionTrabajo estacion = estaciones.get(datos.indexEstacion);
        float x = estacion.area.x + estacion.area.width / 2f - 32;
        float y = estacion.area.y + estacion.area.height + 20;

        // Dibujar textura del cliente
        if (texturaCliente != null) {
            batch.draw(texturaCliente, x, y, 64, 64);
        }

        // Dibujar barra de tolerancia
        float anchoBarraMax = 60f;
        float altoBarra = 8f;
        float xBarra = x + 2;
        float yBarra = y - 15;

        // Fondo de la barra (rojo)
        Color colorFondo = Color.RED;
        batch.setColor(colorFondo);
        batch.draw(Recursos.PIXEL,
                   xBarra, yBarra, anchoBarraMax, altoBarra);

        // Barra de progreso
        float anchoBarraActual = anchoBarraMax * datos.porcentajeTolerancia;
        Color colorBarra = datos.porcentajeTolerancia > 0.5f ? Color.GREEN :
                          datos.porcentajeTolerancia > 0.25f ? Color.YELLOW : Color.RED;
        batch.setColor(colorBarra);
        batch.draw(Recursos.PIXEL,
                   xBarra, yBarra, anchoBarraActual, altoBarra);

        batch.setColor(Color.WHITE);
    }
}
