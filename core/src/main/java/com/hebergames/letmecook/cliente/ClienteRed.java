package com.hebergames.letmecook.cliente;

import com.hebergames.letmecook.red.PaqueteRed;
import com.hebergames.letmecook.red.paquetes.*;
import java.net.*;
import java.util.concurrent.*;

public class ClienteRed {
    private static final int PUERTO_SERVIDOR = 25565;

    private DatagramSocket socket;
    private InetAddress direccionServidor;
    private boolean conectado;
    private int idJugador;
    private ExecutorService executor;
    private PaqueteEstado ultimoEstado;
    private boolean esperandoJugadores;
    private boolean servidorCerrado;
    private String razonDesconexion;
    private boolean jugadorDesconectado;

    public ClienteRed() {
        executor = Executors.newSingleThreadExecutor();
        esperandoJugadores = true;
        servidorCerrado = false;
        razonDesconexion = "";
        jugadorDesconectado = false;
    }

    public boolean conectar(String ipServidor) {
        try {
            socket = new DatagramSocket();
            direccionServidor = InetAddress.getByName(ipServidor);

            // Enviar solicitud de conexión
            enviarPaquete(new PaqueteConexion(0, false));

            // Esperar respuesta
            byte[] buffer = new byte[4096];
            DatagramPacket paquete = new DatagramPacket(buffer, buffer.length);
            socket.setSoTimeout(5000); // 5 segundos timeout

            socket.receive(paquete);
            PaqueteRed respuesta = PaqueteRed.deserializar(paquete.getData());

            if (respuesta instanceof PaqueteConexion) {
                PaqueteConexion conexion = (PaqueteConexion) respuesta;
                if (conexion.esAprobado()) {
                    idJugador = conexion.getIdJugador();
                    conectado = true;

                    System.out.println("Conectado como Jugador " + idJugador);

                    // Iniciar hilos de recepción y ping
                    executor.execute(this::recibirEstados);
                    executor.execute(this::enviarPingsAutomaticos);

                    return true;
                }
            }

            return false;

        } catch (Exception e) {
            System.err.println("Error al conectar: " + e.getMessage());
            return false;
        }
    }

    private void recibirEstados() {
        byte[] buffer = new byte[8192];

        while (conectado) {
            try {
                DatagramPacket paquete = new DatagramPacket(buffer, buffer.length);
                socket.receive(paquete);

                PaqueteRed recibido = PaqueteRed.deserializar(paquete.getData());

                System.out.println("Paquete recibido: " + recibido.getTipo());

                if (recibido instanceof PaqueteEstado) {
                    ultimoEstado = (PaqueteEstado) recibido;
                    esperandoJugadores = false;
                    System.out.println("Estado actualizado! Saliendo de espera...");
                } else if (recibido instanceof PaqueteDesconexion) {
                    PaqueteDesconexion desc = (PaqueteDesconexion) recibido;
                    manejarDesconexionRecibida(desc);
                    break; // Salir del loop
                }

            } catch (SocketTimeoutException e) {
                // Timeout normal
            } catch (Exception e) {
                if (conectado) {
                    System.err.println("Error recibiendo estado: " + e.getMessage());
                    e.printStackTrace();
                }
            }
        }
    }

    private void manejarDesconexionRecibida(PaqueteDesconexion desc) {
        System.out.println("⚠️ Desconexión recibida: " + desc.getRazon());

        switch (desc.getRazon()) {
            case "CIERRE_SERVIDOR":
                servidorCerrado = true;
                razonDesconexion = "El servidor se ha cerrado";
                break;
            case "JUGADOR_ABANDONO":
                jugadorDesconectado = true;
                razonDesconexion = "Un jugador abandonó la partida";
                break;
            case "TIMEOUT":
                jugadorDesconectado = true;
                razonDesconexion = "Se perdió la conexión con un jugador";
                break;
        }

        conectado = false;
    }

    public boolean isServidorCerrado() { return servidorCerrado; }
    public boolean isJugadorDesconectado() { return jugadorDesconectado; }
    public String getRazonDesconexion() { return razonDesconexion; }

    private void enviarPingsAutomaticos() {
        while (conectado) {
            try {
                enviarPaquete(new PaqueteRed() {
                    @Override
                    public TipoPaquete getTipo() { return TipoPaquete.PING; }
                });
                Thread.sleep(1000);
            } catch (InterruptedException e) {
                break;
            }
        }
    }

    public void enviarInput(boolean arriba, boolean abajo, boolean izquierda,
                           boolean derecha, boolean correr) {
        if (!conectado) return;

        PaqueteInput input = new PaqueteInput(idJugador, arriba, abajo,
                                              izquierda, derecha, correr);
        enviarPaquete(input);
    }

    public void enviarInteraccion(int indexEstacion, PaqueteInteraccion.TipoInteraccion tipo) {
        if (!conectado) return;

        PaqueteInteraccion interaccion = new PaqueteInteraccion(idJugador, indexEstacion, tipo);
        enviarPaquete(interaccion);
    }

    public void enviarInteraccion(int indexEstacion, PaqueteInteraccion.TipoInteraccion tipo, int parametro) {
        if (!conectado) return;

        PaqueteInteraccion interaccion = new PaqueteInteraccion(idJugador, indexEstacion, tipo, parametro);
        enviarPaquete(interaccion);
    }

    private void enviarPaquete(PaqueteRed paquete) {
        try {
            byte[] datos = paquete.serializar();
            DatagramPacket datagramPacket = new DatagramPacket(
                datos, datos.length, direccionServidor, PUERTO_SERVIDOR);
            socket.send(datagramPacket);
        } catch (Exception e) {
            System.err.println("Error enviando paquete: " + e.getMessage());
        }
    }

    public PaqueteEstado getUltimoEstado() {
        return ultimoEstado;
    }

    public boolean isEsperandoJugadores() {
        return esperandoJugadores;
    }

    public int getIdJugador() {
        return idJugador;
    }

    public void desconectar() {
        if (conectado) {
            enviarPaquete(new PaqueteRed() {
                @Override
                public TipoPaquete getTipo() { return TipoPaquete.DESCONEXION; }
            });
            conectado = false;
            executor.shutdown();
            socket.close();
        }
    }
}
