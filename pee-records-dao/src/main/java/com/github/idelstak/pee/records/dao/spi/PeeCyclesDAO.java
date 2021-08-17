/*
 * Copyright 2021
 */
package com.github.idelstak.pee.records.dao.spi;

import com.github.idelstak.pee.records.model.spi.core.Entity;
import java.io.IOException;
import java.time.LocalDate;
import java.util.Optional;
import com.github.idelstak.pee.records.model.spi.PeeCycle;

/**
 *
 * @author Hiram K. <https://github.com/IdelsTak>
 */
public interface PeeCyclesDAO {

    Iterable<PeeCycle> getAllPeeRecords() throws IOException;

    Iterable<PeeCycle> getPatientPeeRecords(Entity patientId) throws IOException;

    Optional<PeeCycle> getPeeRecord(Entity peeRecordId) throws IOException;

    Entity addPeeRecord(Entity patientId, LocalDate startDate) throws IOException;

}
