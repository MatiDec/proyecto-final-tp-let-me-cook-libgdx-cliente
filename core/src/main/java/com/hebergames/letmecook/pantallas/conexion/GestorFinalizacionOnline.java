package com.hebergames.letmecook.pantallas.conexion;

import com.badlogic.gdx.Gdx;
import com.hebergames.letmecook.cliente.ClienteRed;
import com.hebergames.letmecook.mapa.niveles.GestorPartida;
import com.hebergames.letmecook.mapa.niveles.NivelPartida;
import com.hebergames.letmecook.pantallas.Pantalla;
import com.hebergames.letmecook.pantallas.PantallaFinal;
import com.hebergames.letmecook.pantallas.juego.GestorTiempoJuego;
import com.hebergames.letmecook.red.paquetes.PaqueteEstado;
import com.hebergames.letmecook.sonido.GestorAudio;
import com.hebergames.letmecook.sonido.SonidoJuego;

public class GestorFinalizacionOnline {
    private final ClienteRed CLIENTE;
    private final GestorAudio GESTOR_AUDIO;
    private final GestorTiempoJuego GESTOR_TIEMPO;
    private boolean juegoFinalizado = false;
    private boolean despedido = false;
    private String razonDespido = "";

    public GestorFinalizacionOnline(ClienteRed cliente, GestorAudio GESTOR_AUDIO, GestorTiempoJuego gestorTiempo) {
        this.CLIENTE = cliente;
        this.GESTOR_AUDIO = GESTOR_AUDIO;
        this.GESTOR_TIEMPO = gestorTiempo;
    }

    public void verificarFinJuego() {
        if (CLIENTE.isJugadorDesconectado() && !juegoFinalizado) {
            finalizarPorDesconexion();
            return;
        }

        PaqueteEstado estado = CLIENTE.getUltimoEstado();
        if (estado != null && estado.isJuegoTerminado() && !juegoFinalizado) {
            procesarFinJuego(estado);
        }
    }

    private void procesarFinJuego(PaqueteEstado estado) {
        int puntaje = estado.getPuntaje();
        String razon = estado.getRazonFin();

        GestorPartida gestorPartida = GestorPartida.getInstancia();
        int nivelActualIndex = gestorPartida.getNivelActualIndex();

        if (nivelActualIndex >= 0 && nivelActualIndex < gestorPartida.getTodosLosNiveles().size()) {
            NivelPartida nivelActual = gestorPartida.getTodosLosNiveles().get(nivelActualIndex);
            if (!nivelActual.isCompletado()) {
                nivelActual.marcarCompletado(puntaje - gestorPartida.getPuntajeTotalPartida());
            }
        }

        boolean esDespido = !razon.isEmpty();

        if (esDespido) {
            despedido = true;
            razonDespido = razon;
        }

        terminarJuego(puntaje);
    }

    private void terminarJuego(int puntaje) {
        juegoFinalizado = true;
        GESTOR_AUDIO.detenerMusica();

        if (CLIENTE != null && CLIENTE.isConectado()) {
            CLIENTE.desconectar();
        }

        if (despedido) {
            GESTOR_AUDIO.reproducirSonido(SonidoJuego.DESPIDO);

            GestorPartida gestorPartida = GestorPartida.getInstancia();
            int puntajeTotal = gestorPartida.getPuntajeTotalPartida() + puntaje;

            Pantalla.cambiarPantalla(new PantallaFinal(
                GESTOR_TIEMPO.getTiempoFormateado(),
                puntajeTotal,
                true,
                razonDespido
            ));
        } else {
            GestorPartida gestorPartida = GestorPartida.getInstancia();
            int puntajeTotal = gestorPartida.getPuntajeTotalPartida();

            GESTOR_AUDIO.reproducirSonido(SonidoJuego.NIVEL_COMPLETADO);

            Pantalla.cambiarPantalla(new PantallaFinal(
                GESTOR_TIEMPO.getTiempoFormateado(),
                puntajeTotal,
                false,
                ""
            ));
        }
    }

    public void finalizarPorDesconexion() {
        if (juegoFinalizado) return;

        juegoFinalizado = true;

        String razon = "Conexión perdida con el servidor";
        if (CLIENTE.isJugadorDesconectado()) {
            razon = CLIENTE.getRazonDesconexion();
        }

        if (GESTOR_AUDIO != null) {
            GESTOR_AUDIO.detenerMusica();
        }

        Gdx.app.postRunnable(() -> {
            Pantalla.cambiarPantalla(new PantallaConexion());
        });
    }

    public boolean isJuegoFinalizado() {
        return this.juegoFinalizado;
    }

    public void marcarComoFinalizado() {
        this.juegoFinalizado = true;
    }
}
