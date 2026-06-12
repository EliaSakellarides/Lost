package com.lost.records;

import com.google.gson.Gson;
import com.sun.net.httpserver.HttpExchange;
import com.sun.net.httpserver.HttpServer;

import java.io.IOException;
import java.io.OutputStream;
import java.net.InetSocketAddress;
import java.nio.charset.StandardCharsets;
import java.util.List;
import java.util.concurrent.Executors;

/**
 * Piccola API REST locale che espone la classifica su localhost:8000.
 * Endpoint: GET/POST /records e GET /records/best.
 */
public final class RecordApiServer {
    private static final int PORT = 8000;
    private static final Gson GSON = new Gson();
    private static HttpServer server;

    private RecordApiServer() {
    }

    /**
     * Avvia il server HTTP su un thread daemon.
     * Se la porta e' occupata il gioco prosegue senza API.
     */
    public static synchronized void start() {
        if (server != null) {
            return;
        }

        try {
            RecordService service = new RecordService();
            server = HttpServer.create(new InetSocketAddress(PORT), 0);
            server.createContext("/records/best", exchange ->
                handleList(exchange, service.getBestRecords(5)));
            server.createContext("/records", exchange ->
                handleRecords(exchange, service));
            server.setExecutor(Executors.newSingleThreadExecutor(r -> {
                Thread thread = new Thread(r, "lost-record-api");
                thread.setDaemon(true);
                return thread;
            }));
            server.start();
            System.out.println(" Record API attiva su http://localhost:" + PORT + "/records");
        } catch (IOException | RuntimeException e) {
            server = null;
            System.out.println("Record API non avviata: " + e.getMessage());
        }
    }

    private static void handleRecords(HttpExchange exchange, RecordService service) throws IOException {
        if ("GET".equalsIgnoreCase(exchange.getRequestMethod())) {
            handleList(exchange, service.getAllRecords());
            return;
        }

        if ("POST".equalsIgnoreCase(exchange.getRequestMethod())) {
            String body = new String(exchange.getRequestBody().readAllBytes(), StandardCharsets.UTF_8);
            RecordRequest request;
            try {
                request = GSON.fromJson(body, RecordRequest.class);
            } catch (RuntimeException e) {
                send(exchange, 400, "{\"error\":\"JSON non valido\"}");
                return;
            }
            if (request == null || request.playerName == null || request.completionMillis < 0) {
                send(exchange, 400, "{\"error\":\"Record non valido\"}");
                return;
            }

            GameRecord saved = service.saveCompletion(request.playerName, request.completionMillis);
            send(exchange, 201, GSON.toJson(saved));
            return;
        }

        send(exchange, 405, "{\"error\":\"Metodo non supportato\"}");
    }

    private static void handleList(HttpExchange exchange, List<GameRecord> records) throws IOException {
        if (!"GET".equalsIgnoreCase(exchange.getRequestMethod())) {
            send(exchange, 405, "{\"error\":\"Metodo non supportato\"}");
            return;
        }
        send(exchange, 200, GSON.toJson(records));
    }

    private static void send(HttpExchange exchange, int status, String body) throws IOException {
        byte[] bytes = body.getBytes(StandardCharsets.UTF_8);
        exchange.getResponseHeaders().set("Content-Type", "application/json; charset=utf-8");
        exchange.sendResponseHeaders(status, bytes.length);
        try (OutputStream os = exchange.getResponseBody()) {
            os.write(bytes);
        }
    }

    private static class RecordRequest {
        private String playerName;
        private long completionMillis;
    }
}
