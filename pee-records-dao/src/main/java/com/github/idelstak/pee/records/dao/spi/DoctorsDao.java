/*
 * Copyright 2021
 */
package com.github.idelstak.pee.records.dao.spi;

import com.github.idelstak.pee.records.model.api.Credentials;
import com.github.idelstak.pee.records.model.api.Name;
import com.github.idelstak.pee.records.model.spi.Doctor;
import com.github.idelstak.pee.records.model.spi.core.Entity;
import java.io.IOException;
import java.util.Optional;

/**
 *
 * @author Hiram K. <https://github.com/IdelsTak>
 */
public interface DoctorsDao {

    Iterable<Doctor> getAllDoctors() throws IOException;

    Optional<Doctor> getDoctor(Entity doctorId) throws IOException;

    Optional<Entity> addDoctor(Name name, Credentials credentials) throws IOException;

    void rename(Entity doctorId, Name newName) throws IOException;

    void updateLoginCredentials(Entity doctorId, Credentials newCredentials) throws IOException;

    void deactivate(Entity doctorId) throws IOException;

    void activate(Entity doctorId) throws IOException;

}
