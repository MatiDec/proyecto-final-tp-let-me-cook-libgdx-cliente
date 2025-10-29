package com.hebergames.letmecook.sonido;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.audio.Music;
import com.badlogic.gdx.audio.Sound;
import com.badlogic.gdx.utils.Disposable;
import com.hebergames.letmecook.pantallas.juego.GestorConfiguracion;

import java.util.HashMap;
import java.util.Map;

public class GestorAudio implements Disposable {

    private static GestorAudio instancia;

    private final Map<String, Music> CANCIONES;
    private final Map<String, Sound> SONIDOS;

    private final Map<String, Long> SONIDOS_ACTIVOS;

    private float volumenMusica = 0.6f;
    private float volumenSonidos = 0.6f;

    private Music musicaActual;
    private String nombreMusicaActual;

    private GestorAudio() {
        CANCIONES = new HashMap<>();
        SONIDOS = new HashMap<>();
        SONIDOS_ACTIVOS = new HashMap<>();
        cargarVolumenDesdeConfiguracion();
    }

    private void cargarVolumenDesdeConfiguracion() {
        GestorConfiguracion.cargar();
        int volumenConfig = GestorConfiguracion.getInt("volumenMusica", 60);
        this.volumenMusica = volumenConfig / 100f;
        this.volumenSonidos = volumenConfig / 100f;
    }

    public static GestorAudio getInstance() {
        if (instancia == null) {
            instancia = new GestorAudio();
        }
        return instancia;
    }

    public void cargarTodasLasMusicasNiveles() {
        for (CancionNivel cancion : CancionNivel.values()) {
            cargarMusica(cancion.getIdentificador(), cancion.getRuta());
        }
        System.out.println("Todas las músicas de niveles cargadas");
    }

    public void cargarTodosLosSonidos() {
        for (SonidoJuego sonido : SonidoJuego.values()) {
            cargarSonido(sonido.getIdentificador(), sonido.getRuta());
        }
        System.out.println("Todos los sonidos del juego cargados");
    }

    public void cargarMusica(String nombre, String rutaArchivo) {
        try {
            Music musica = Gdx.audio.newMusic(Gdx.files.internal(rutaArchivo));
            CANCIONES.put(nombre, musica);
            System.out.println("Música cargada: " + nombre);
        } catch (Exception e) {
            System.err.println("Error al cargar música: " + nombre + " - " + e.getMessage());
        }
    }

    public void cargarSonido(String nombre, String rutaArchivo) {
        try {
            Sound sonido = Gdx.audio.newSound(Gdx.files.internal(rutaArchivo));
            SONIDOS.put(nombre, sonido);
            System.out.println("Sonido cargado: " + nombre);
        } catch (Exception e) {
            System.err.println("Error al cargar sonido: " + nombre + " - " + e.getMessage());
        }
    }

    public void reproducirMusicaNivel(CancionNivel musica) {
        reproducirCancion(musica.getIdentificador(), true);
    }

    public void reproducirCancion(String nombre, boolean enBucle) {
        if (musicaActual != null && !nombre.equals(nombreMusicaActual)) {
            detenerMusica();
        }

        Music musica = CANCIONES.get(nombre);
        if (musica != null) {
            musica.setVolume(volumenMusica);
            musica.setLooping(enBucle);
            musica.play();
            musicaActual = musica;
            nombreMusicaActual = nombre;
        } else {
            System.err.println("Música no encontrada: " + nombre);
        }
    }

    public void pausarMusica() {
        if (musicaActual != null && musicaActual.isPlaying()) {
            musicaActual.pause();
        }
    }

    public void reanudarMusica() {
        if (musicaActual != null && !musicaActual.isPlaying()) {
            musicaActual.play();
        }
    }

    public void detenerMusica() {
        if (musicaActual != null) {
            musicaActual.stop();
        }
        musicaActual = null;
        nombreMusicaActual = null;
    }

    public void reproducirSonido(SonidoJuego sonido) {
        reproducirSonido(sonido.getIdentificador());
    }

    public void reproducirSonido(String nombre) {
        Sound sonido = SONIDOS.get(nombre);
        if (sonido != null) {
            long id = sonido.play(volumenSonidos);
            SONIDOS_ACTIVOS.put(nombre, id);
        } else {
            System.err.println("Sonido no encontrado: " + nombre);
        }
    }

    public void detenerSonido(String nombre) {
        Sound sonido = SONIDOS.get(nombre);
        Long id = SONIDOS_ACTIVOS.get(nombre);

        if (sonido != null && id != null) {
            sonido.stop(id);
            SONIDOS_ACTIVOS.remove(nombre);
            System.out.println("Sonido detenido: " + nombre);
        }
    }

    public void setVolumenMusica(float volumen) {
        this.volumenMusica = Math.max(0.0f, Math.min(1.0f, volumen));
        if (musicaActual != null) {
            musicaActual.setVolume(this.volumenMusica);
        }
    }

    @Override
    public void dispose() {
        for (Music musica : CANCIONES.values()) {
            musica.dispose();
        }
        CANCIONES.clear();

        for (Sound sonido : SONIDOS.values()) {
            sonido.dispose();
        }
        SONIDOS.clear();

        SONIDOS_ACTIVOS.clear();

        musicaActual = null;
        nombreMusicaActual = null;
    }
}
