package com.gardey.tennis_sheet.exceptions;


public class CourtNameAlreadyExistsException extends Exception {
    
    public CourtNameAlreadyExistsException(String courtName) {
        super("Court with name '" + courtName + "' already exists");
    }
    
}