package com.lost.socket;

import java.io.IOException;
import java.io.OutputStream;
import java.net.InetSocketAddress;
import java.net.ServerSocket;
import java.net.Socket;
import java.nio.charset.StandardCharsets;
import java.util.List;
import java.util.concurrent.CopyOnWriteArrayList;

/**
 * "Radio DHARMA": piccolo server TCP basato su socket grezzi
 * (java.net.ServerSocket) che trasmette in tempo reale gli eventi
 * della partita ai client collegati.
 *
 * <p>A differenza dell'API REST (che risponde solo quando interrogata),
 * il socket mantiene la connessione aperta e invia gli eventi in push:
 * basta collegarsi con {@code nc localhost 4815} per ascoltare la radio.</p>
 *
 * <p>La porta 4815 richiama i Numeri di LOST (4 8 15 16 23 42).
 * Il server e' fail-safe: se la porta e' occupata o si verifica un
 * errore, il gioco prosegue senza la radio.</p>
 */
public final class DharmaRadioServer {

    private static final int PORT = 4815;
    private static ServerSocket serverSocket;
    private static final List<Socket> clients = new CopyOnWriteArrayList<>();

    private DharmaRadioServer() {
    }

    /**
     * Avvia il server su un thread daemon in ascolto sulla porta 4815.
     * Chiamato una sola volta all'avvio del gioco; se la porta non e'
     * disponibile l'errore viene segnalato e il gioco continua.
     */
    public static synchronized void start() {
        if (serverSocket != null) {
            return;
        }
        try {
            serverSocket = new ServerSocket();
            serverSocket.bind(new InetSocketAddress("localhost", PORT));
            Thread acceptThread = new Thread(DharmaRadioServer::acceptLoop, "dharma-radio");
            acceptThread.setDaemon(true);
            acceptThread.start();
            System.out.println(" Radio DHARMA in onda su localhost:" + PORT +
                " (collegati con: nc localhost " + PORT + ")");
        } catch (IOException e) {
            serverSocket = null;
            System.out.println("Radio DHARMA non avviata: " + e.getMessage());
        }
    }

    private static void acceptLoop() {
        while (serverSocket != null && !serverSocket.isClosed()) {
            try {
                Socket client = serverSocket.accept();
                clients.add(client);
                sendTo(client, "=== RADIO DHARMA - Frequenza dell'isola ===");
                sendTo(client, "Sei in ascolto. Gli eventi della partita appariranno qui.");
            } catch (IOException e) {
                // Server chiuso o errore di accept: esce dal ciclo
                return;
            }
        }
    }

    /**
     * Trasmette una riga di testo a tutti i client collegati.
     * I client non piu' raggiungibili vengono rimossi.
     * Se nessuno e' in ascolto (o il server non e' attivo) non fa nulla.
     * @param message evento di gioco da trasmettere
     */
    public static void broadcast(String message) {
        if (message == null || message.isBlank() || clients.isEmpty()) {
            return;
        }
        String line = message.replace("\n", " ").trim();
        for (Socket client : clients) {
            if (!sendTo(client, line)) {
                clients.remove(client);
            }
        }
    }

    private static boolean sendTo(Socket client, String line) {
        try {
            OutputStream out = client.getOutputStream();
            out.write((line + "\n").getBytes(StandardCharsets.UTF_8));
            out.flush();
            return true;
        } catch (IOException e) {
            closeQuietly(client);
            return false;
        }
    }

    private static void closeQuietly(Socket client) {
        try {
            client.close();
        } catch (IOException ignored) {
            // chiusura best-effort
        }
    }
}
