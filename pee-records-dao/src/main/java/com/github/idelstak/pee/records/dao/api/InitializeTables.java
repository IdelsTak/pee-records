/*
 * Copyright 2021
 */
package com.github.idelstak.pee.records.dao.api;

import java.io.IOException;
import java.net.URISyntaxException;
import java.net.URL;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.sql.Connection;
import java.sql.SQLException;
import javax.sql.DataSource;
import org.apache.ibatis.jdbc.ScriptRunner;

/**
 *
 * @author Hiram K. <https://github.com/IdelsTak>
 */
public class InitializeTables {

    private final DataSource source;

    public InitializeTables(DataSource source) {
        this.source = source;
    }

    /**
     * Loads an {@code sql} file containing the script to create all the
     * required tables.
     *
     * @throws IOException if an error occurs while executing the {@code sql}
     * statements
     */
    public void start() throws IOException {
        Path path = null;

        try {
            URL url = getClass().getResource("/scripts/peecalendar-tables.sql");
            path = Paths.get(url.toURI());
        } catch (URISyntaxException ex) {
            throw new IOException(ex);
        }

        try (Connection conn = source.getConnection()) {
            ScriptRunner runner = new ScriptRunner(conn);

            runner.runScript(Files.newBufferedReader(path));
        } catch (SQLException ex) {
            throw new IOException(ex);
        }
    }

}
