package com.hebergames.letmecook.estaciones;

import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.math.Rectangle;
import com.hebergames.letmecook.entidades.Jugador;
import com.hebergames.letmecook.entregables.ObjetoAlmacenable;
import com.hebergames.letmecook.entregables.ingredientes.Ingrediente;
import com.hebergames.letmecook.entregables.productos.Producto;
import com.hebergames.letmecook.estaciones.conmenu.EstacionConMenu;
import com.hebergames.letmecook.estaciones.interaccionclientes.CajaRegistradora;
import com.hebergames.letmecook.estaciones.interaccionclientes.MesaRetiro;
import com.hebergames.letmecook.estaciones.procesadoras.MaquinaProcesadora;
import com.hebergames.letmecook.sonido.GestorAudio;
import com.hebergames.letmecook.sonido.SonidoJuego;
import com.hebergames.letmecook.utiles.GestorTexturas;
import com.hebergames.letmecook.utiles.Recursos;

public abstract class EstacionTrabajo {
    public Rectangle area;
    protected MaquinaProcesadora procesadora;

    private final float DIFERENCIA = 150f;

    private Jugador jugadorOcupante = null;

    private boolean fueraDeServicio = false;


    public EstacionTrabajo(Rectangle area) {
        this.area = area;
    }

    public boolean estaCerca(float jugadorX, float jugadorY) {
        float centroMaquinaX = area.x + area.width / 2f;
        float centroMaquinaY = area.y + area.height / 2f;

        float centroJugadorX = jugadorX + Recursos.MEDIDA_TILE / 2f;
        float centroJugadorY = jugadorY + Recursos.MEDIDA_TILE / 2f;

        float dx = centroJugadorX - centroMaquinaX;
        float dy = centroJugadorY - centroMaquinaY;

        double distancia = Math.sqrt(dx * dx + dy * dy);

        return distancia <= DIFERENCIA;
    }


    public final void interactuarConJugador(Jugador jugador) {
        if (jugador == null) {
            return;
        }

        if (fueraDeServicio) {
            GestorAudio.getInstance().reproducirSonido(SonidoJuego.ERROR_INTERACCION);
            return;
        }

        if (!puedeInteractuar(jugador)) {
            return;
        }

        if (jugadorOcupante != null && jugadorOcupante != jugador) {
            return;
        }

        if (jugadorOcupante != jugador) {
            ocupar(jugador);
        }

        if (this instanceof EstacionConMenu) {
            if (!jugador.estaEnMenu()) {
                jugador.entrarEnMenu(this);
            }
        }

        alInteractuar();

        if (this instanceof EstacionConMenu) {
            ((EstacionConMenu) this).iniciarMenu(jugador);
        }

        if (this instanceof CajaRegistradora) {
            CajaRegistradora caja = (CajaRegistradora) this;
            if (caja.tomarPedido()) {
                jugador.salirDeMenu();
                if (this instanceof EstacionConMenu) {
                    ((EstacionConMenu) this).alLiberar();
                }
                jugadorOcupante = null;
            }
        }

        if (this instanceof MesaRetiro) {
            MesaRetiro mesa = (MesaRetiro) this;
            if (mesa.tieneCliente() && jugador.getInventario() instanceof Producto) {
                Producto productoJugador = (Producto) jugador.getInventario();
                mesa.entregarProducto(productoJugador);
                jugador.sacarDeInventario();

                jugador.salirDeMenu();
                if (this instanceof EstacionConMenu) {
                    ((EstacionConMenu) this).alLiberar();
                }
                jugadorOcupante = null;
            }
        }
    }

    private boolean puedeInteractuar(Jugador jugador) {
        return estaCerca(jugador.getPosicion().x, jugador.getPosicion().y);
    }

    public float calcularDistanciaA(float x, float y) {
        float centroMaquinaX = area.x + area.width / 2f;
        float centroMaquinaY = area.y + area.height / 2f;

        float dx = x - centroMaquinaX;
        float dy = y - centroMaquinaY;

        return (float) Math.sqrt(dx * dx + dy * dy);
    }

    public void manejarProcesamiento(Jugador jugador) {
        if (procesadora == null) {
            return;
        }

        if (procesadora.tieneProcesandose()) {
            if (!jugador.tieneInventarioLleno()) {
                Ingrediente resultado = procesadora.obtenerResultado();
                if (resultado != null) {
                    jugador.guardarEnInventario(resultado);
                }
            }
            return;
        }

        ObjetoAlmacenable objetoInventario = jugador.getInventario();

        if (objetoInventario instanceof Ingrediente) {
            Ingrediente ingrediente = (Ingrediente) objetoInventario;

            if (procesadora.iniciarProceso(ingrediente)) {
                jugador.sacarDeInventario();
            }
        }
    }

    public void actualizar(float delta) {
        if (procesadora != null) {
            procesadora.actualizarProceso(delta);
        }
    }

    public void dibujarIndicador(SpriteBatch batch) {}

    public void dibujarIndicadorError(SpriteBatch batch) {
        if (fueraDeServicio) {
            float x = area.x + area.width / 2 - 16;
            float y = area.y + area.height + 10;
            batch.draw(GestorTexturas.getInstance().getTexturaError(), x, y, 32, 32);
        }
    }

    public void dibujarEstado(SpriteBatch batch) {}

    public void dibujar(SpriteBatch batch, Jugador jugador) {}

    public Jugador getJugadorOcupante() {
        return this.jugadorOcupante;
    }

    public void ocupar(Jugador jugador) {
        if (jugador != null) {
            this.jugadorOcupante = jugador;
        }
    }

    public void verificarDistanciaYLiberar() {
        if (jugadorOcupante != null) {

            if (!estaCerca(jugadorOcupante.getPosicion().x,
                jugadorOcupante.getPosicion().y)) {
                jugadorOcupante.salirDeMenu();
                if (this instanceof EstacionConMenu) {
                    ((EstacionConMenu) this).alLiberar();
                }
                jugadorOcupante = null;
            }
        }
    }

    public void setFueraDeServicio(boolean fuera) {
        this.fueraDeServicio = fuera;
    }

    public abstract void alInteractuar();

    public MaquinaProcesadora getProcesadora() { return this.procesadora; }
}
