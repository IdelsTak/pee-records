/*
 * Copyright 2021
 */
package com.github.idelstak.pee.records.dao.api;

import java.io.IOException;
import java.net.URI;
import java.net.URISyntaxException;
import java.net.URL;
import java.nio.file.FileSystemNotFoundException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.nio.file.spi.FileSystemProvider;
import java.sql.Connection;
import java.sql.SQLException;
import java.util.Collections;
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
            URI uri = url.toURI();

            //Important: tell the executable that the file is in a jar
            //Remember, when running the compiled source code
            //the file is in a normal filesystem folder
            //but, when the code is packed into a jar
            //the path should check inside a jar and not inside
            //a filesystem folder
            if ("jar".equals(uri.getScheme())) {
                for (FileSystemProvider fsp : FileSystemProvider.installedProviders()) {
                    if (fsp.getScheme().equalsIgnoreCase("jar")) {
                        try {
                            fsp.getFileSystem(uri);
                        } catch (FileSystemNotFoundException exception) {
                            //Filesystem was not found
                            //so, initialize it first
                            fsp.newFileSystem(uri, Collections.emptyMap());
                        }
                    }
                }
            }
            
            path = Paths.get(uri);
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
