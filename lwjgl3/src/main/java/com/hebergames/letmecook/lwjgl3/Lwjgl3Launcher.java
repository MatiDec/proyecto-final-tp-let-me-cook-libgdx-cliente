package com.hebergames.letmecook.lwjgl3;

import com.badlogic.gdx.backends.lwjgl3.Lwjgl3Application;
import com.badlogic.gdx.backends.lwjgl3.Lwjgl3ApplicationConfiguration;
import com.hebergames.letmecook.LetMeCookPrincipal;
import com.hebergames.letmecook.pantallas.juego.GestorConfiguracion;

/** Launches the desktop (LWJGL3) application. */
public class Lwjgl3Launcher {
    private static final int ANCHO_POR_DEFECTO = 840;
    private static final int ALTO_POR_DEFECTO = 680;

    public static void main(String[] args) {
        if (StartupHelper.startNewJvmIfRequired()) return; // This handles macOS support and helps on Windows.
        createApplication();
    }

    private static Lwjgl3Application createApplication() {
        return new Lwjgl3Application(new LetMeCookPrincipal(), getDefaultConfiguration());
    }

    private static Lwjgl3ApplicationConfiguration getDefaultConfiguration() {
        Lwjgl3ApplicationConfiguration configuration = new Lwjgl3ApplicationConfiguration();
        configuration.setTitle("LetMeCook");
        GestorConfiguracion.cargar();

        // Obtener los valores del archivo de configuración
        boolean esPantallaCompleta = GestorConfiguracion.getBoolean("pantallaCompleta", false);
        String resolucion = GestorConfiguracion.get("resolucion", ANCHO_POR_DEFECTO + "x" + ALTO_POR_DEFECTO);

        int ancho = ANCHO_POR_DEFECTO;
        int alto = ALTO_POR_DEFECTO;

        // Intentar procesar la resolución del archivo
        try {
            String[] partes = resolucion.split("x");
            if (partes.length == 2) {
                ancho = Integer.parseInt(partes[0]);
                alto = Integer.parseInt(partes[1]);
            }
        } catch (NumberFormatException e) {
            System.err.println("Error al parsear la resolución, usando valores por defecto.");
            // Si hay un error, ancho y alto ya tienen los valores por defecto
        }

        // Aplicar el modo de pantalla según la configuración
        if (esPantallaCompleta) {
            // Pone el juego en pantalla completa usando la resolución nativa del monitor
            configuration.setFullscreenMode(Lwjgl3ApplicationConfiguration.getDisplayMode());
        } else {
            // Pone el juego en modo ventana con la resolución del archivo
            configuration.setWindowedMode(ancho, alto);
        }
        //// Vsync limits the frames per second to what your hardware can display, and helps eliminate
        //// screen tearing. This setting doesn't always work on Linux, so the line after is a safeguard.
        configuration.useVsync(true);
        //// Limits FPS to the refresh rate of the currently active monitor, plus 1 to try to match fractional
        //// refresh rates. The Vsync setting above should limit the actual FPS to match the monitor.
        configuration.setForegroundFPS(Lwjgl3ApplicationConfiguration.getDisplayMode().refreshRate + 1);
        //// If you remove the above line and set Vsync to false, you can get unlimited FPS, which can be
        //// useful for testing performance, but can also be very stressful to some hardware.
        //// You may also need to configure GPU drivers to fully disable Vsync; this can cause screen tearing.
        configuration.setResizable(false);
        //// You can change these files; they are in lwjgl3/src/main/resources/ .
        //// They can also be loaded from the root of assets/ .
        configuration.setWindowIcon("libgdx128.png", "libgdx64.png", "libgdx32.png", "libgdx16.png");
        return configuration;
    }
}
