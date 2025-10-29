package com.hebergames.letmecook.sonido;

import com.hebergames.letmecook.utiles.Recursos;

public enum SonidoJuego {
    TEMPORIZADOR("temporizador", Recursos.RUTA_AUDIO + "sonidos/tictac.ogg"),
    COCCION_PERFECTA("coccion_perfecta", Recursos.RUTA_AUDIO + "sonidos/coccion_completa.ogg"),
    CLIENTE_LLEGA("cliente_llega", Recursos.RUTA_AUDIO + "sonidos/cliente_llega.ogg"),
    CLIENTE_LLEGA_VIRTUAL("cliente_llega_virtual", Recursos.RUTA_AUDIO + "sonidos/cliente_llega_virtual.ogg"),
    PEDIDO_ENTREGADO("pedido_entregado", Recursos.RUTA_AUDIO + "sonidos/pedido_correcto.ogg"),
    PEDIDO_INCORRECTO("pedido_incorrecto",Recursos.RUTA_AUDIO + "sonidos/pedido_mal.ogg"),
    ITEM_RECOGIDO("item_recogido", Recursos.RUTA_AUDIO + "sonidos/pickup.ogg"),
    DESPIDO("despido", Recursos.RUTA_AUDIO + "sonidos/despido.ogg"),
    COLISION_JUGADORES("colision_jugadores", Recursos.RUTA_AUDIO + "sonidos/colision_jugadores.ogg"),
    CLIENTE_SE_VA("cliente_se_va", Recursos.RUTA_AUDIO + "sonidos/cliente_se_va.ogg"),
    ERROR_INTERACCION("error_interaccion", Recursos.RUTA_AUDIO + "sonidos/error_interaccion.ogg"),
    ALERTA_QUEMADO("alerta_quemado", Recursos.RUTA_AUDIO + "sonidos/alerta_quemado.ogg"),
    NIVEL_COMPLETADO("nivel_completado", Recursos.RUTA_AUDIO + "sonidos/nivel_completo.ogg");

    private final String IDENTIFICADOR;
    private final String RUTA;

    SonidoJuego(String IDENTIFICADOR, String RUTA) {
        this.IDENTIFICADOR = IDENTIFICADOR;
        this.RUTA = RUTA;
    }

    public String getIdentificador() { return this.IDENTIFICADOR; }

    public String getRuta() { return this.RUTA; }
}
