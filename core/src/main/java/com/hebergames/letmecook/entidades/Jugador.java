package com.hebergames.letmecook.entidades;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.graphics.g2d.Animation;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.graphics.g2d.TextureRegion;
import com.badlogic.gdx.math.Rectangle;
import com.badlogic.gdx.math.Vector2;
import com.hebergames.letmecook.entregables.ObjetoAlmacenable;
import com.hebergames.letmecook.eventos.entrada.DatosEntrada;
import com.hebergames.letmecook.eventos.eventosaleatorios.EventoPisoMojado;
import com.hebergames.letmecook.eventos.eventosaleatorios.GestorEventosAleatorios;
import com.hebergames.letmecook.estaciones.EstacionTrabajo;
import com.hebergames.letmecook.sonido.GestorAudio;
import com.hebergames.letmecook.sonido.SonidoJuego;
import com.hebergames.letmecook.utiles.GestorAnimacion;

import java.util.ArrayList;
import java.util.List;

public class Jugador {
    protected Vector2 posicion;
    protected Vector2 velocidad;
    protected TextureRegion frameActual;
    protected Animation<TextureRegion> animacion;
    protected float estadoTiempo;
    protected float anguloRotacion = 0f;
    private final Rectangle HITBOX;
    private final float ANCHO_HITBOX = 120;
    private final float OFFSET_HITBOX_X = 0;
    private final float OFFSET_HITBOX_Y = 0;

    private boolean colisionReciente = false;
    private float tiempoColisionReset = 0f;
    private final float TIEMPO_RESET_COLISION = 0.5f; // tiempo mínimo entre sonidos

    private boolean estaEnMenu = false;
    private EstacionTrabajo estacionActual = null;

    private List<Rectangle> colisionables = new ArrayList<>();
    private List<Rectangle> interactuables = new ArrayList<>();
    private List<Jugador> otrosJugadores = new ArrayList<>();

    private ObjetoAlmacenable inventario;

    public final int DISTANCIA_MOVIMIENTO = 400;
    public final int DISTANCIA_CORRIENDO = 800;

    private boolean estaDeslizando = false;
    private final Vector2 VELOCIDAD_DESLIZAMIENTO = new Vector2(0, 0);
    private float tiempoDeslizamiento = 0f;

    protected GestorAnimacion gestorAnimacion;
    private String objetoEnMano = "vacio";

    public Jugador(float x, float y, GestorAnimacion gestorAnimacion) {
        this.posicion = new Vector2(x, y);
        this.velocidad = new Vector2(0, 0);
        this.gestorAnimacion = gestorAnimacion;
        this.animacion = gestorAnimacion.getAnimacionPorObjeto(objetoEnMano);
        this.estadoTiempo = 0;
        float ALTO_HITBOX = 120;
        this.HITBOX = new Rectangle(x + OFFSET_HITBOX_X, y + OFFSET_HITBOX_Y, ANCHO_HITBOX, ALTO_HITBOX);
    }

    public void actualizar(float delta) {
        if (velocidad.isZero(0.01f) && !estaDeslizando) {
            frameActual = animacion.getKeyFrame(0, true);
            return;
        }

        if (velocidad.x != 0 || velocidad.y != 0 || estaDeslizando) {
            estadoTiempo += delta;
        } else {
            estadoTiempo = 0;
        }
        frameActual = animacion.getKeyFrame(estadoTiempo, true);

        if (estaDeslizando) {
            tiempoDeslizamiento += delta;

            float DURACION_DESLIZAMIENTO = 0.3f;
            float progreso = tiempoDeslizamiento / DURACION_DESLIZAMIENTO;
            float factorReduccion = Math.max(0f, 1f - progreso);

            velocidad.set(VELOCIDAD_DESLIZAMIENTO.x * factorReduccion,
                VELOCIDAD_DESLIZAMIENTO.y * factorReduccion);

            float desplazamientoX = velocidad.x * delta;
            float desplazamientoY = velocidad.y * delta;

            GestorEventosAleatorios gestorEventos = GestorEventosAleatorios.getInstancia();
            EventoPisoMojado eventoPiso = gestorEventos.getEventoPisoMojado();

            if (eventoPiso != null && eventoPiso.estaSobrePisoMojado(posicion.x, posicion.y)) {
                desplazamientoX *= 2f;
                desplazamientoY *= 2f;
            }

            if (colisionMovimiento(desplazamientoX, desplazamientoY)) {
                estaDeslizando = false;
                tiempoDeslizamiento = 0f;
                velocidad.set(0, 0);
                VELOCIDAD_DESLIZAMIENTO.set(0, 0);
            } else {
                posicion.add(desplazamientoX, desplazamientoY);
                HITBOX.setPosition(posicion.x + OFFSET_HITBOX_X, posicion.y + OFFSET_HITBOX_Y);

                if (tiempoDeslizamiento >= DURACION_DESLIZAMIENTO) {
                    estaDeslizando = false;
                    tiempoDeslizamiento = 0f;
                    velocidad.set(0, 0);
                    VELOCIDAD_DESLIZAMIENTO.set(0, 0);
                }
            }
            return;
        }

        float desplazamientoX = velocidad.x * delta;
        float desplazamientoY = velocidad.y * delta;

        if (colisionMovimiento(desplazamientoX, desplazamientoY)) {
            velocidad.set(0, 0);
        } else {
            posicion.add(desplazamientoX, desplazamientoY);
            HITBOX.setPosition(posicion.x + OFFSET_HITBOX_X, posicion.y + OFFSET_HITBOX_Y);
        }

        if (colisionReciente) {
            tiempoColisionReset += delta;
            if (tiempoColisionReset >= TIEMPO_RESET_COLISION) {
                colisionReciente = false;
                tiempoColisionReset = 0f;
            }
        }

    }

    public void dibujar(SpriteBatch batch) {
        TextureRegion frame = frameActual;

        float x = posicion.x;
        float y = posicion.y;
        float width = 128;
        float height = 128;
        float originX = width / 2f;
        float originY = height / 2f;

        batch.draw(frame, x, y, originX, originY, width, height, 1f, 1f, anguloRotacion);
    }

    public Vector2 getPosicion() {
        return this.posicion;
    }

    public void manejarEntrada(DatosEntrada datosEntrada) {
        if (estaDeslizando) {
            return;
        }

        float dx = 0, dy = 0;

        if (datosEntrada.arriba) dy += DISTANCIA_MOVIMIENTO;
        if (datosEntrada.abajo) dy -= DISTANCIA_MOVIMIENTO;
        if (datosEntrada.izquierda) dx -= DISTANCIA_MOVIMIENTO;
        if (datosEntrada.derecha) dx += DISTANCIA_MOVIMIENTO;

        if (datosEntrada.correr && (dx != 0 || dy != 0)) {
            float multiplicador = (float) DISTANCIA_CORRIENDO / DISTANCIA_MOVIMIENTO;
            dx *= multiplicador;
            dy *= multiplicador;
        }

        if (dx != 0 || dy != 0) {
            float angulo = (float) Math.toDegrees(Math.atan2(dy, dx)) - 90f;
            setAnguloRotacion(angulo);
            moverSiNoColisiona(dx, dy, datosEntrada.correr);
        } else {
            velocidad.set(0, 0);
        }
    }

    private void reproducirSonidoColision() {
        // Distorsión: pitch aleatorio entre 0.8 y 1.2
        float pitch = 0.8f + (float)Math.random() * 0.4f;
        GestorAudio.getInstance().reproducirSonido(SonidoJuego.COLISION_JUGADORES);
    }

    private boolean colisiona(Rectangle rect) {

        for (Rectangle obstaculo : colisionables) {
            if (obstaculo.overlaps(rect)) {
                return true;
            }
        }

        for (Jugador otro : otrosJugadores) {
            if (otro != this && otro.getHITBOX().overlaps(rect)) {
                // Solo reproducir sonido si no se ha reproducido recientemente
                if (!colisionReciente) {
                    reproducirSonidoColision();
                    colisionReciente = true;
                    tiempoColisionReset = 0f;
                }
                return true;
            }
        }
        return false;
    }

    private void moverSiNoColisiona(float dx, float dy, boolean estaCorriendo) {
        GestorEventosAleatorios gestorEventos = GestorEventosAleatorios.getInstancia();
        EventoPisoMojado eventoPiso = gestorEventos.getEventoPisoMojado();

        if (eventoPiso != null && eventoPiso.estaSobrePisoMojado(posicion.x, posicion.y)) {
            dx *= 2f;
            dy *= 2f;
        }
        float deltaTime = Gdx.graphics.getDeltaTime();
        float desplazamientoX = dx * deltaTime;
        float desplazamientoY = dy * deltaTime;

        float nuevaX = HITBOX.x + desplazamientoX;
        float nuevaY = HITBOX.y + desplazamientoY;

        Rectangle areaFutura = new Rectangle(nuevaX, nuevaY, HITBOX.width, HITBOX.height);

        if (!colisiona(areaFutura)) {
            velocidad.set(dx, dy);
            return;
        }

        boolean puedeMoverX = false;
        boolean puedeMoverY = false;

        if (dx != 0) {
            Rectangle areaFuturaX = new Rectangle(HITBOX.x + desplazamientoX, HITBOX.y, HITBOX.width, HITBOX.height);
            puedeMoverX = !colisiona(areaFuturaX);
        }

        if (dy != 0) {
            Rectangle areaFuturaY = new Rectangle(HITBOX.x, HITBOX.y + desplazamientoY, HITBOX.width, HITBOX.height);
            puedeMoverY = !colisiona(areaFuturaY);
        }

        if (puedeMoverX) {
            velocidad.x = dx;
        } else {
            velocidad.x = 0;
        }

        if (puedeMoverY) {
            velocidad.y = dy;
        } else {
            velocidad.y = 0;
        }
    }

    private boolean colisionMovimiento(float dx, float dy) {
        float distancia = Math.max(Math.abs(dx), Math.abs(dy));
        int pasos = Math.max(2, (int)((distancia + (ANCHO_HITBOX / 4f) - 1) / (ANCHO_HITBOX / 4f)));

        float pasoX = dx / pasos;
        float pasoY = dy / pasos;

        float px = HITBOX.x;
        float py = HITBOX.y;

        for (int i = 0; i < pasos; i++) {
            px += pasoX;
            py += pasoY;
            Rectangle area = new Rectangle(px, py, HITBOX.width, HITBOX.height);
            if (colisiona(area)) {
                return true;
            }
        }
        return false;
    }

    public void iniciarDeslizamiento() {
        if (velocidad.len() > 0 && !estaDeslizando) {
            estaDeslizando = true;
            tiempoDeslizamiento = 0f;

            float FACTOR_DESLIZAMIENTO = 1.5f;
            VELOCIDAD_DESLIZAMIENTO.set(velocidad).scl(FACTOR_DESLIZAMIENTO);
        }
    }

    public boolean tieneInventarioLleno() {
        return this.inventario != null;
    }

    public void guardarEnInventario(ObjetoAlmacenable objeto) {
        if (this.inventario == null) {
            this.inventario = objeto;
            setObjetoEnMano(objeto.getNombre());
        }
    }

    public void sacarDeInventario() {
        ObjetoAlmacenable item = this.inventario;
        this.inventario = null;
        setObjetoEnMano("vacio");
    }

    public void descartarInventario(){
        this.inventario = null;
        setObjetoEnMano("vacio");
    }

    public boolean estaEnMenu() {
        return estaEnMenu;
    }

    public void entrarEnMenu(EstacionTrabajo estacion) {
        this.estaEnMenu = true;
        this.estacionActual = estacion;
    }

    public void salirDeMenu() {
        this.estaEnMenu = false;
        this.estacionActual = null;
    }

    public EstacionTrabajo getEstacionActual() {
        return estacionActual;
    }

    public boolean esJugador1() {
        return true;
    }

    public ObjetoAlmacenable getInventario() {
        return this.inventario;
    }

    public String getNombreItemInventario() {
        if (inventario != null) {
            return this.inventario.getNombre();
        }
        return "Vacío";
    }

    public void setOtrosJugadores(List<Jugador> otrosJugadores) { this.otrosJugadores = otrosJugadores; }

    public void setColisionables(List<Rectangle> colisionables) {
        this.colisionables = colisionables;
    }

    public void setAnguloRotacion(float angulo) {
        this.anguloRotacion = angulo;
    }

    public void setObjetoEnMano(String nombreObjeto) {
        if (!nombreObjeto.equalsIgnoreCase(this.objetoEnMano)) {
            this.objetoEnMano = nombreObjeto;
            this.animacion = gestorAnimacion.getAnimacionPorObjeto(nombreObjeto);
            this.estadoTiempo = 0;
        }
    }

    public Rectangle getHITBOX() { return this.HITBOX; }

    public void setInteractuables(ArrayList<Rectangle> rectangulosInteractuables) {
        this.interactuables = rectangulosInteractuables;
    }
}
