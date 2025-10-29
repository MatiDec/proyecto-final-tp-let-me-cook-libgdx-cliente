package com.hebergames.letmecook.pantallas.juego;

import com.hebergames.letmecook.utiles.Recursos;

import java.io.*;
import java.util.Properties;

public class GestorConfiguracion {

    private static final Properties PROPIEDADES = new Properties();

    public static void cargar() {
        File archivo = new File(Recursos.ARCHIVO_CONFIG);
        if (archivo.exists()) {
            try (FileInputStream fis = new FileInputStream(archivo)) {
                PROPIEDADES.load(fis);
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
    }

    public static void guardar() {
        try (FileOutputStream fos = new FileOutputStream(Recursos.ARCHIVO_CONFIG)) {
            PROPIEDADES.store(fos, "Configuraciones del juego");
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public static void set(String clave, String valor) {
        PROPIEDADES.setProperty(clave, valor);
        guardar();
    }

    public static String get(String clave, String defecto) {
        return PROPIEDADES.getProperty(clave, defecto);
    }

    public static boolean getBoolean(String clave, boolean defecto) {
        return Boolean.parseBoolean(get(clave, String.valueOf(defecto)));
    }

    public static int getInt(String clave, int defecto) {
        try {
            return Integer.parseInt(get(clave, String.valueOf(defecto)));
        } catch (NumberFormatException e) {
            return defecto;
        }
    }
}
