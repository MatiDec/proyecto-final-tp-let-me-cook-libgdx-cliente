package com.hebergames.letmecook.pantallas.conexion;

import com.hebergames.letmecook.cliente.ClienteRed;
import com.hebergames.letmecook.mapa.niveles.GestorPartida;
import com.hebergames.letmecook.mapa.niveles.NivelPartida;
import com.hebergames.letmecook.red.paquetes.PaqueteCambioNivel;
import com.hebergames.letmecook.sonido.GestorAudio;
import com.hebergames.letmecook.sonido.SonidoJuego;

public class GestorCambioNivelOnline {
    private final ClienteRed CLIENTE_RED;
    private final GestorAudio GESTOR_AUDIO;

    public GestorCambioNivelOnline(ClienteRed CLIENTE_RED, GestorAudio GESTOR_AUDIO) {
        this.CLIENTE_RED = CLIENTE_RED;
        this.GESTOR_AUDIO = GESTOR_AUDIO;
    }

    public boolean verificarYProcesarCambioNivel() {
        PaqueteCambioNivel paqueteCambio = CLIENTE_RED.getPaqueteCambioNivel();

        if (paqueteCambio != null) {
            CLIENTE_RED.limpiarPaqueteCambioNivel();
            procesarCambioNivel(paqueteCambio);
            return true;
        }
        return false;
    }

    private void procesarCambioNivel(PaqueteCambioNivel paquete) {
        GestorPartida gestorPartida = GestorPartida.getInstancia();
        int nivelCompletadoIndex = paquete.getNumeroNivel() - 1;

        if (nivelCompletadoIndex >= 0 && nivelCompletadoIndex < gestorPartida.getTodosLosNiveles().size()) {
            NivelPartida nivelCompletado = gestorPartida.getTodosLosNiveles().get(nivelCompletadoIndex);
            nivelCompletado.marcarCompletado(paquete.getPuntajeNivelCompletado());
        }

        gestorPartida.sumarPuntajeSinModificarNivel(paquete.getPuntajeNivelCompletado());
        gestorPartida.establecerNivelActual(paquete.getNumeroNivel());

        GESTOR_AUDIO.reproducirSonido(SonidoJuego.NIVEL_COMPLETADO);
    }
}
