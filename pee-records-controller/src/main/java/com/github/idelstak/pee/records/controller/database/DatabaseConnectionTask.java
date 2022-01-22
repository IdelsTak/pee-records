/*
 * Copyright 2021
 */
package com.github.idelstak.pee.records.controller.database;

import com.github.idelstak.pee.records.database.DatabaseAccess;
import java.sql.Connection;
import java.sql.SQLException;
import java.util.Optional;
import javafx.concurrent.Task;
import javax.sql.DataSource;

/**
 *
 * @author Hiram K. <https://github.com/IdelsTak>
 */
class DatabaseConnectionTask extends Task<Optional<Connection>> {

    private final DatabaseAccess access;

    DatabaseConnectionTask(DatabaseAccess access) {
        this.access = access;
    }

    @Override
    protected Optional<Connection> call() throws Exception {
        Optional<Connection> connection = Optional.empty();
        updateTitle("Connecting...");
        try {
            updateProgress(-1L, 1L);
            DataSource source = access.getDataSource();
            try (final Connection conn = source.getConnection()) {
                try {
                    Thread.sleep(2000L);
                } catch (InterruptedException ie) {
                    cancel();
                }
                connection = Optional.of(conn);
                updateTitle("Connection successful. Save these settings before exiting");
                updateMessage("");
            }
        } catch (SQLException e) {
            updateTitle("Connection failed. See reason(s) below");
            updateMessage(e.getMessage());
        } finally {
            updateProgress(1L, 1L);
        }
        return connection;
    }

}
