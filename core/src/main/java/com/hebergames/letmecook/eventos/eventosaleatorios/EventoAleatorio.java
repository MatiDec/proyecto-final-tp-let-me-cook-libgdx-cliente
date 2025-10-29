package com.hebergames.letmecook.eventos.eventosaleatorios;

public interface EventoAleatorio {
    void activar();
    void desactivar();
    String getNombre();
    float getProbabilidad();
}
