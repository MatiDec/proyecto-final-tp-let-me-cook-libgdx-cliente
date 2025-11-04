package com.hebergames.letmecook.pantallas.conexion;

import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.graphics.g2d.TextureRegion;
import com.badlogic.gdx.math.Rectangle;
import com.hebergames.letmecook.estaciones.EstacionTrabajo;
import com.hebergames.letmecook.estaciones.procesadoras.EstadoMaquina;
import com.hebergames.letmecook.estaciones.procesadoras.Procesadora;
import com.hebergames.letmecook.mapa.GestorMapa;
import com.hebergames.letmecook.mapa.indicadores.EstadoIndicador;
import com.hebergames.letmecook.mapa.indicadores.GestorIndicadores;
import com.hebergames.letmecook.red.VisualizadorMenuEstacion;
import com.hebergames.letmecook.red.paquetes.DatosEstacion;
import com.hebergames.letmecook.red.paquetes.DatosJugador;
import com.hebergames.letmecook.red.paquetes.PaqueteEstado;
import com.hebergames.letmecook.sonido.GestorAudio;
import com.hebergames.letmecook.sonido.SonidoJuego;
import com.hebergames.letmecook.utiles.GestorTexturas;

import java.util.ArrayList;

public class GestorEstacionesOnline {
    private ArrayList<EstacionTrabajo> estaciones;
    private GestorIndicadores gestorIndicadores;
    private final VisualizadorMenuEstacion VISUALIZADOR_MENU;
    private final int ID_JUGADOR;

    public GestorEstacionesOnline(int ID_JUGADOR) {
        this.ID_JUGADOR = ID_JUGADOR;
        this.gestorIndicadores = new GestorIndicadores();
        this.VISUALIZADOR_MENU = new VisualizadorMenuEstacion();
    }

    public void inicializarEstaciones(GestorMapa gestorMapa) {
        this.estaciones = gestorMapa.getEstaciones();

        for (EstacionTrabajo estacion : estaciones) {
            if (estacion.getProcesadora() != null && estacion.getProcesadora() instanceof Procesadora) {
                Procesadora proc = (Procesadora) estacion.getProcesadora();
                if (proc.getIndicador() != null) {
                    gestorIndicadores.registrarIndicador(proc.getIndicador());
                }
            }
        }
    }

    public void actualizarDesdeServidor(PaqueteEstado estado, int estacionCercanaIndex) {
        if (estado == null) return;

        actualizarEstadoEstaciones(estado);
        actualizarMenuVisualizacion(estado, estacionCercanaIndex);
    }

    private void actualizarEstadoEstaciones(PaqueteEstado estado) {
        for (DatosEstacion datosEst : estado.getEstaciones()) {
            if (datosEst.index < 0 || datosEst.index >= estaciones.size()) continue;

            EstacionTrabajo estacion = estaciones.get(datosEst.index);

            if (estacion.getProcesadora() instanceof Procesadora) {
                Procesadora proc = (Procesadora) estacion.getProcesadora();

                if (proc.getIndicador() != null) {
                    EstadoIndicador estadoIndicadorPrevio = proc.getIndicador().getEstado();
                    EstadoIndicador estadoIndicador;

                    if (datosEst.estadoIndicador != null && !datosEst.estadoIndicador.isEmpty()) {
                        try {
                            estadoIndicador = EstadoIndicador.valueOf(datosEst.estadoIndicador);
                        } catch (IllegalArgumentException e) {
                            estadoIndicador = EstadoIndicador.INACTIVO;
                        }
                    } else {
                        estadoIndicador = EstadoIndicador.INACTIVO;
                    }

                    if (estadoIndicador != estadoIndicadorPrevio) {
                        proc.getIndicador().setEstado(estadoIndicador);

                        if (estadoIndicador == EstadoIndicador.LISTO) {
                            GestorAudio.getInstance().reproducirSonido(SonidoJuego.COCCION_PERFECTA);
                        } else if (estadoIndicador == EstadoIndicador.QUEMANDOSE) {
                            GestorAudio.getInstance().reproducirSonido(SonidoJuego.ALERTA_QUEMADO);
                        }
                    }
                }
            }

            estacion.setFueraDeServicio(datosEst.fueraDeServicio);
        }
    }

    private void actualizarMenuVisualizacion(PaqueteEstado estado, int estacionCercanaIndex) {
        DatosJugador datosLocal = (ID_JUGADOR == 1) ? estado.getJugador1() : estado.getJugador2();

        if (!datosLocal.estaEnMenu) {
            if (VISUALIZADOR_MENU.isVisible()) {
                VISUALIZADOR_MENU.ocultar();
            }
            return;
        }

        if (estacionCercanaIndex < 0) return;

        DatosEstacion datosEst = null;
        for (DatosEstacion de : estado.getEstaciones()) {
            if (de.index == estacionCercanaIndex) {
                datosEst = de;
                break;
            }
        }

        if (datosEst == null) return;

        boolean esJ1 = (ID_JUGADOR == 1);

        switch (datosEst.tipoEstacion) {
            case "Heladera":
                VISUALIZADOR_MENU.mostrarMenuHeladera(esJ1);
                break;

            case "Mesa":
                VISUALIZADOR_MENU.mostrarMenuMesa(esJ1, datosEst.objetosEnEstacion);
                break;

            case "Cafetera":
                VISUALIZADOR_MENU.mostrarMenuCafetera(esJ1, datosEst.estadoMenuBebida, datosEst.progresoPreparacion);
                break;

            case "Fuente":
                VISUALIZADOR_MENU.mostrarMenuFuente(esJ1, datosEst.estadoMenuBebida, datosEst.progresoPreparacion);
                break;

            case "MaquinaEnvasadora":
                String nombreIngrediente = datosLocal.objetoEnMano;
                VISUALIZADOR_MENU.mostrarMenuEnvasadora(esJ1, nombreIngrediente);
                break;

            default:
                VISUALIZADOR_MENU.ocultar();
                break;
        }
    }

    public void dibujarOverlaysEstaciones(SpriteBatch batch, PaqueteEstado estado) {
        if (estado == null) return;

        for (DatosEstacion datosEst : estado.getEstaciones()) {
            if (datosEst.index < 0 || datosEst.index >= estaciones.size()) continue;

            EstacionTrabajo estacion = estaciones.get(datosEst.index);

            if (estacion.getProcesadora() instanceof Procesadora) {
                TextureRegion overlay = null;

                if (datosEst.estadoMaquina.equals("ACTIVA")) {
                    overlay = GestorTexturas.getInstance()
                        .getTexturaMaquina(datosEst.tipoEstacion.toLowerCase(), EstadoMaquina.ACTIVA);
                } else if (datosEst.estadoMaquina.equals("LISTA")) {
                    overlay = GestorTexturas.getInstance()
                        .getTexturaMaquina(datosEst.tipoEstacion.toLowerCase(), EstadoMaquina.LISTA);
                }

                if (overlay != null) {
                    Rectangle area = estacion.area;
                    batch.draw(overlay, area.x, area.y, area.width, area.height);
                }
            }
        }
    }

    public void dibujarMenu(SpriteBatch batch, PaqueteEstado estado, int estacionCercanaIndex, float worldWidth, float worldHeight) {
        if (estado == null) return;

        DatosJugador datosLocal = (ID_JUGADOR == 1) ? estado.getJugador1() : estado.getJugador2();

        if (datosLocal != null && datosLocal.estaEnMenu && estacionCercanaIndex >= 0) {
            VISUALIZADOR_MENU.dibujar(batch, worldWidth, worldHeight);
        }
    }

    public GestorIndicadores getGestorIndicadores() {
        return this.gestorIndicadores;
    }

    public ArrayList<EstacionTrabajo> getEstaciones() {
        return this.estaciones;
    }

    public void reinicializar() {
        gestorIndicadores = new GestorIndicadores();
    }
}
