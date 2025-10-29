package com.hebergames.letmecook.estaciones.interaccionclientes;

import com.hebergames.letmecook.entidades.clientes.Cliente;

public interface EstacionEntrega {
    boolean tieneCliente();
    Cliente getCliente();
    void liberarCliente();
    void asignarCliente(Cliente cliente);
}
