/*
 * Copyright 2021
 */

module pee.model {
    requires java.sql;
    exports com.github.idelstak.pee.records.model.api;
    exports com.github.idelstak.pee.records.model.spi;
    exports com.github.idelstak.pee.records.model.spi.core;
    exports com.github.idelstak.pee.records.model.impl;
}
