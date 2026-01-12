package com.gardey.tennis_sheet.exceptions;

public class ReservationConflictException extends Exception {
    public ReservationConflictException(Long courtId, String start) {
        super("Reservation conflict for court " + courtId + " at " + start);
    }
}
