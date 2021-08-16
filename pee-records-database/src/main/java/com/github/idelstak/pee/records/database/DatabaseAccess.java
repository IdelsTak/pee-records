/*
 * Copyright 2021
 */
package com.github.idelstak.pee.records.database;

import com.zaxxer.hikari.HikariConfig;
import com.zaxxer.hikari.HikariDataSource;
import javax.sql.DataSource;

/**
 *
 * @author Hiram K. <https://github.com/IdelsTak>
 */
public class DatabaseAccess {

    private final String username;
    private final char[] password;
    private final String url;
    private HikariDataSource dataSource;

    public DatabaseAccess(String username, char[] password, String url) {
        this.username = username;
        this.password = password;
        this.url = url;
    }

    public DataSource getDataSource() {
        if (dataSource == null) {
            HikariConfig config = new HikariConfig();

            config.setDriverClassName("com.mysql.cj.jdbc.Driver");
            config.setJdbcUrl(url);
            config.setUsername(username);
            config.setPassword(new String(password));
            dataSource = new HikariDataSource(config);
        }

        return dataSource;
    }

    public void closeConnection() {
        dataSource.close();
    }

}
