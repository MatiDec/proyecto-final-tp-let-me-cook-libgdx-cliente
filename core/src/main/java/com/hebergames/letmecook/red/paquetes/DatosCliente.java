package com.hebergames.letmecook.red.paquetes;

import java.io.Serializable;
import java.util.ArrayList;

public class DatosCliente implements Serializable {
    public int id;
    public float tiempoRestante;
    public float porcentajeTolerancia;
    public String estadoPedido;
    public ArrayList<String> productosPedido;
    public boolean esVirtual;
    public int indexEstacion;

    public DatosCliente(int id, float tiempoRestante, float porcentajeTolerancia,
                        String estadoPedido, ArrayList<String> productosPedido,
                        boolean esVirtual, int indexEstacion) {
        this.id = id;
        this.tiempoRestante = tiempoRestante;
        this.porcentajeTolerancia = porcentajeTolerancia;
        this.estadoPedido = estadoPedido;
        this.productosPedido = productosPedido;
        this.esVirtual = esVirtual;
        this.indexEstacion = indexEstacion;
    }
}
