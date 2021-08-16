/*
 * Copyright 2021
 */
package com.github.idelstak.pee.records.dao.spi;

import com.github.idelstak.pee.records.model.spi.PeeRecord;
import com.github.idelstak.pee.records.model.spi.core.Entity;
import java.io.IOException;
import java.time.LocalDate;
import java.util.Optional;

/**
 *
 * @author Hiram K. <https://github.com/IdelsTak>
 */
public interface PeeRecordsDAO {

    Iterable<PeeRecord> getAllPeeRecords() throws IOException;

    Iterable<PeeRecord> getPatientPeeRecords(Entity patientId) throws IOException;

    Optional<PeeRecord> getPeeRecord(Entity peeRecordId) throws IOException;

    Entity addPeeRecord(Entity patientId, LocalDate startDate) throws IOException;

}
