package com.gardey.tennis_sheet.exceptions;

public class PersonEmailAlreadyExistsException extends RuntimeException {
    public PersonEmailAlreadyExistsException(String email) {
        super("Email already exists: " + email);
    }
    
}
