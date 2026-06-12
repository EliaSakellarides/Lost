package com.lost.records;

import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.time.Instant;
import java.util.ArrayList;
import java.util.List;

/**
 * Accesso JDBC al database H2 dei record (migliori tempi di completamento).
 * Crea la tabella al primo avvio e usa PreparedStatement per le query.
 */
public class RecordRepository {
    private static final String DEFAULT_DB_URL = "jdbc:h2:file:" +
        Paths.get(System.getProperty("user.home"), ".lost", "records").toString();

    private final String dbUrl;

    /** Apre il repository sul database predefinito in ~/.lost/records. */
    public RecordRepository() {
        this(System.getProperty("lost.records.db.url", DEFAULT_DB_URL));
    }

    /**
     * Apre il repository su un URL JDBC specifico (usato nei test).
     * @param dbUrl URL JDBC del database H2
     */
    public RecordRepository(String dbUrl) {
        this.dbUrl = dbUrl;
        initialize();
    }

    /**
     * Inserisce un nuovo record di completamento.
     * @param playerName nome del giocatore
     * @param completionMillis tempo di completamento in millisecondi
     * @return il record salvato con id e data
     */
    public GameRecord save(String playerName, long completionMillis) {
        String sql = "INSERT INTO records (player_name, completion_millis, completed_at) VALUES (?, ?, ?)";
        try (Connection connection = openConnection();
             PreparedStatement statement = connection.prepareStatement(sql, Statement.RETURN_GENERATED_KEYS)) {
            statement.setString(1, playerName);
            statement.setLong(2, completionMillis);
            statement.setString(3, Instant.now().toString());
            statement.executeUpdate();

            try (ResultSet keys = statement.getGeneratedKeys()) {
                if (keys.next()) {
                    return findById(keys.getInt(1));
                }
            }
        } catch (SQLException e) {
            throw new IllegalStateException("Impossibile salvare il record", e);
        }
        return null;
    }

    /**
     * Restituisce i migliori tempi in ordine crescente.
     * @param limit numero massimo di record da restituire
     * @return lista dei migliori record
     */
    public List<GameRecord> findBest(int limit) {
        String sql = "SELECT id, player_name, completion_millis, completed_at " +
            "FROM records ORDER BY completion_millis ASC, completed_at ASC LIMIT ?";
        List<GameRecord> records = new ArrayList<>();
        try (Connection connection = openConnection();
             PreparedStatement statement = connection.prepareStatement(sql)) {
            statement.setInt(1, limit);
            try (ResultSet rs = statement.executeQuery()) {
                while (rs.next()) {
                    records.add(fromResultSet(rs));
                }
            }
        } catch (SQLException e) {
            throw new IllegalStateException("Impossibile leggere i record", e);
        }
        return records;
    }

    /**
     * Restituisce tutti i record salvati, dal piu' recente.
     * @return lista completa dei record
     */
    public List<GameRecord> findAll() {
        String sql = "SELECT id, player_name, completion_millis, completed_at " +
            "FROM records ORDER BY completed_at DESC";
        List<GameRecord> records = new ArrayList<>();
        try (Connection connection = openConnection();
             PreparedStatement statement = connection.prepareStatement(sql);
             ResultSet rs = statement.executeQuery()) {
            while (rs.next()) {
                records.add(fromResultSet(rs));
            }
        } catch (SQLException e) {
            throw new IllegalStateException("Impossibile leggere i record", e);
        }
        return records;
    }

    private GameRecord findById(int id) {
        String sql = "SELECT id, player_name, completion_millis, completed_at FROM records WHERE id = ?";
        try (Connection connection = openConnection();
             PreparedStatement statement = connection.prepareStatement(sql)) {
            statement.setInt(1, id);
            try (ResultSet rs = statement.executeQuery()) {
                if (rs.next()) {
                    return fromResultSet(rs);
                }
            }
        } catch (SQLException e) {
            throw new IllegalStateException("Impossibile leggere il record salvato", e);
        }
        return null;
    }

    private void initialize() {
        try {
            Files.createDirectories(Paths.get(System.getProperty("user.home"), ".lost"));
        } catch (Exception ignored) {
            // H2 gestira' eventuali errori di percorso all'apertura della connessione.
        }

        String sql = "CREATE TABLE IF NOT EXISTS records (" +
            "id INT AUTO_INCREMENT PRIMARY KEY, " +
            "player_name VARCHAR(255) NOT NULL, " +
            "completion_millis BIGINT NOT NULL, " +
            "completed_at VARCHAR(64) NOT NULL" +
            ")";
        try (Connection connection = openConnection();
             Statement statement = connection.createStatement()) {
            statement.execute(sql);
        } catch (SQLException e) {
            throw new IllegalStateException("Impossibile inizializzare il database record", e);
        }
    }

    private Connection openConnection() throws SQLException {
        return DriverManager.getConnection(dbUrl, "sa", "");
    }

    private GameRecord fromResultSet(ResultSet rs) throws SQLException {
        return new GameRecord(
            rs.getInt("id"),
            rs.getString("player_name"),
            rs.getLong("completion_millis"),
            rs.getString("completed_at")
        );
    }
}
