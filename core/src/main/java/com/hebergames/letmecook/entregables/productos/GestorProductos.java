package com.hebergames.letmecook.entregables.productos;

import com.hebergames.letmecook.entregables.productos.bebidas.Cafe;
import com.hebergames.letmecook.entregables.productos.bebidas.Gaseosa;
import com.hebergames.letmecook.entregables.productos.bebidas.TamanoBebida;
import com.hebergames.letmecook.entregables.recetas.TipoReceta;
import com.hebergames.letmecook.utiles.Aleatorio;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Random;

public class GestorProductos {

    private final ArrayList<TipoProducto> PRODUCTOS_DISPONIBLES;
    private final ArrayList<TipoReceta> RECETAS_DISPONIBLES;

    public GestorProductos() {
        PRODUCTOS_DISPONIBLES = new ArrayList<>();
        RECETAS_DISPONIBLES = new ArrayList<>();
        cargarProductos();
    }


    private void cargarProductos() {
        RECETAS_DISPONIBLES.addAll(Arrays.asList(TipoReceta.values()));

        PRODUCTOS_DISPONIBLES.add(TipoProducto.CAFE);
        PRODUCTOS_DISPONIBLES.add(TipoProducto.GASEOSA);
        RECETAS_DISPONIBLES.removeIf(receta ->
            receta.getTipoProducto().getCategoria() == CategoriaProducto.INVALIDO
        );
    }

    public Producto obtenerProductoAleatorioPorCategorias(CategoriaProducto... categoriasPermitidas) {
        if (categoriasPermitidas == null || categoriasPermitidas.length == 0) {
            return obtenerProductoAleatorio();
        }

        ArrayList<TipoReceta> recetasFiltradas = new ArrayList<>();
        ArrayList<TipoProducto> productosFiltrados = new ArrayList<>();

        for (TipoReceta receta : RECETAS_DISPONIBLES) {
            for (CategoriaProducto cat : categoriasPermitidas) {
                if (receta.getTipoProducto().getCategoria() == cat) {
                    recetasFiltradas.add(receta);
                    break;
                }
            }
        }

        for (TipoProducto producto : PRODUCTOS_DISPONIBLES) {
            for (CategoriaProducto cat : categoriasPermitidas) {
                if (producto.getCategoria() == cat) {
                    productosFiltrados.add(producto);
                    break;
                }
            }
        }

        int totalOpciones = recetasFiltradas.size() + productosFiltrados.size();

        if (totalOpciones == 0) {
            return obtenerProductoAleatorio();
        }

        int seleccion = Aleatorio.entero(totalOpciones);

        if (seleccion < recetasFiltradas.size()) {

            return recetasFiltradas.get(seleccion).crear().preparar();

        } else {

            int indexProducto = seleccion - recetasFiltradas.size();
            return generarBebidaAleatoria(productosFiltrados.get(indexProducto));

        }
    }

    public Producto obtenerProductoAleatorio() {
        boolean generarReceta = Aleatorio.booleano();

        if (generarReceta && !RECETAS_DISPONIBLES.isEmpty()) {
            int index = Aleatorio.entero(RECETAS_DISPONIBLES.size());
            return RECETAS_DISPONIBLES.get(index).crear().preparar();
        } else if (!PRODUCTOS_DISPONIBLES.isEmpty()) {
            int index = Aleatorio.entero(PRODUCTOS_DISPONIBLES.size());
            TipoProducto tipo = PRODUCTOS_DISPONIBLES.get(index);

            return generarBebidaAleatoria(tipo);
        }

        return null;
    }

    private Producto generarBebidaAleatoria(TipoProducto tipo) {
        TamanoBebida[] tamanos = TamanoBebida.values();
        TamanoBebida tamano = tamanos[Aleatorio.entero(tamanos.length)];

        if (tipo == TipoProducto.CAFE) {

            String[] tiposCafe = Cafe.getTiposCafe().keySet().toArray(new String[0]);
            String tipoCafe = tiposCafe[Aleatorio.entero(tiposCafe.length)];
            return new Cafe(tipoCafe, tamano);

        } else if (tipo == TipoProducto.GASEOSA) {

            String[] tiposGaseosa = Gaseosa.getTiposGaseosa().keySet().toArray(new String[0]);
            String tipoGaseosa = tiposGaseosa[Aleatorio.entero(tiposGaseosa.length)];
            return new Gaseosa(tipoGaseosa, tamano);

        }

        return tipo.crear();
    }
}
