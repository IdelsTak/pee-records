/*
 * Copyright 2021
 */

module pee.database.access {
    requires java.logging;
    requires java.sql;
    requires mysql.connector.java;
    requires com.zaxxer.hikari;
    
    exports com.github.idelstak.pee.records.database;
}
