package com.hebergames.letmecook.mapa;


import com.badlogic.gdx.graphics.OrthographicCamera;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.maps.MapObject;
import com.badlogic.gdx.maps.MapObjects;
import com.badlogic.gdx.maps.objects.RectangleMapObject;
import com.badlogic.gdx.math.Rectangle;
import com.hebergames.letmecook.entidades.Jugador;
import com.hebergames.letmecook.eventos.eventosaleatorios.EventoPisoMojado;
import com.hebergames.letmecook.eventos.eventosaleatorios.GestorEventosAleatorios;
import com.hebergames.letmecook.estaciones.EstacionTrabajo;

import java.util.ArrayList;

public class GestorMapa {

    private Mapa mapaActual;
    private ArrayList<EstacionTrabajo> estaciones;

    public GestorMapa() {
        this.estaciones = new ArrayList<>();
    }

    public void setMapaActual(Mapa mapa) {
        if (this.mapaActual != null && this.mapaActual != mapa) {
            this.mapaActual.dispose();
        }
        this.mapaActual = mapa;
        this.estaciones = mapa.getEstacionesTrabajo();
    }

    public void renderizar(OrthographicCamera camara) {
        if (mapaActual != null) {
            mapaActual.render(camara);
        }
    }

    public void actualizarEstaciones(float delta) {
        for (EstacionTrabajo estacion : estaciones) {
            estacion.actualizar(delta);
            estacion.verificarDistanciaYLiberar();
        }
    }

    public void dibujarIndicadores(SpriteBatch batch) {
        GestorEventosAleatorios gestorEventos = GestorEventosAleatorios.getInstancia();
        EventoPisoMojado eventoPiso = gestorEventos.getEventoPisoMojado();
        if (eventoPiso != null) {
            eventoPiso.dibujar(batch);
        }

        for (EstacionTrabajo estacion : estaciones) {
            estacion.dibujarIndicador(batch);
            estacion.dibujarIndicadorError(batch);
            estacion.dibujarEstado(batch);
        }
    }

    public void asignarColisionesYInteracciones(Jugador jugador) {
        if (mapaActual != null) {
            jugador.setColisionables(mapaActual.getRectangulosColision());
            jugador.setInteractuables(mapaActual.getRectangulosInteractuables());
        }
    }

    public Rectangle getPuntoSpawn(String nombreObjeto) {
        if (mapaActual == null || mapaActual.getMapa() == null) {
            return null;
        }

        if (mapaActual.getMapa().getLayers().get("Jugadores") != null) {
            MapObjects objetos = mapaActual.getMapa().getLayers().get("Jugadores").getObjects();

            for (MapObject objeto : objetos) {
                if (objeto.getName() != null && objeto.getName().equals(nombreObjeto)) {
                    if (objeto instanceof RectangleMapObject) {
                        Rectangle rect = ((RectangleMapObject) objeto).getRectangle();
                        return new Rectangle(rect.x, rect.y, rect.width, rect.height);
                    }
                }
            }
        }

        return null;
    }

    public ArrayList<EstacionTrabajo> getEstaciones() {
        return this.estaciones;
    }

    public ArrayList<Rectangle> getTilesCaminables() {
        if (mapaActual != null) {
            return mapaActual.getTilesCaminables();
        }
        return new ArrayList<>();
    }

    public void dispose() {
        if (mapaActual != null) {
            mapaActual.dispose();
        }
    }
}
