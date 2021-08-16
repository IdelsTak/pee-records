/*
 * Copyright 2021
 */

module pee.dao {
    requires java.sql;
    requires org.mybatis;
    requires pee.model;
    
    exports com.github.idelstak.pee.records.dao.api;
    exports com.github.idelstak.pee.records.dao.spi;
    exports com.github.idelstak.pee.records.dao.spi.impl;
}
