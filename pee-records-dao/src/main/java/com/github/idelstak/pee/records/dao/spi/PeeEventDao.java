/*
 * Copyright 2021
 */
package com.github.idelstak.pee.records.dao.spi;

import com.github.idelstak.pee.records.model.spi.PeeEvent;
import com.github.idelstak.pee.records.model.spi.core.Entity;
import java.io.IOException;
import java.time.LocalDateTime;
import java.util.Optional;

/**
 *
 * @author Hiram K. <https://github.com/IdelsTak>
 */
public interface PeeEventDao {

    Iterable<PeeEvent> getAllEvents() throws IOException;

    Optional<PeeEvent> getEvent(Entity eventId) throws IOException;

    Optional<Entity> addEvent(Entity cycleId, LocalDateTime whenPeed, PeeEvent.Type type) throws IOException;

    void updateTime(Entity eventId, LocalDateTime newPeeTime) throws IOException;

    void updateType(Entity eventId, PeeEvent.Type newType) throws IOException;

    void removeEvent(Entity eventId) throws IOException;
}
