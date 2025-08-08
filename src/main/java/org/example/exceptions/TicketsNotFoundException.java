package org.example.exceptions;

public class TicketsNotFoundException extends RuntimeException {
    public TicketsNotFoundException(String message) {
        super(message);
    }
}
