/*
 * Copyright 2021
 */
package com.github.idelstak.pee.records.model.spi;

import com.github.idelstak.pee.records.model.spi.core.Entity;
import java.time.LocalDateTime;
import java.util.Arrays;
import java.util.Optional;

/**
 *
 * @author Hiram K. <https://github.com/IdelsTak>
 */
public interface PeeEvent extends Entity {

    PeeRecord getPeeRecord();

    LocalDateTime getWhen();

    Status getStatus();

    public enum Status {
        WET_NIGHT("Wet Night"),
        FEW_DROPS("Few Drops"),
        DRY_NIGHT("Dry Night");
        private final String description;

        private Status(String description) {
            this.description = description;
        }

        @Override
        public String toString() {
            return description;
        }

        public class Description {

            private final String description;

            public Description(String description) {
                if (description == null || description.isBlank()) {
                    throw new IllegalArgumentException("Pee status description must not be null");
                }
                this.description = description;
            }

            public Status toPeeState() {
                Optional<Status> optionalState = Arrays.stream(Status.values())
                        .filter(state -> state.toString().equalsIgnoreCase(description))
                        .findFirst();
                return optionalState.orElseThrow(() -> new TypeNotPresentException(description, null));
            }
        }
    }

}
