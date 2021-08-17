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
public interface PeeCyclesDao {

    Iterable<PeeCycle> getAllPeeCycles() throws IOException;

    Optional<PeeCycle> getPeeCycle(Entity cycleId) throws IOException;

    Optional<Entity> addPeeCycle(Entity patientId, LocalDate startDate) throws IOException;

    void updateStartDate(Entity cycleId, LocalDate newStartDate) throws IOException;

    void updateEndDate(Entity cycleId, LocalDate newEndDate) throws IOException;

    void removeCycle(Entity cycleId) throws IOException;

}
