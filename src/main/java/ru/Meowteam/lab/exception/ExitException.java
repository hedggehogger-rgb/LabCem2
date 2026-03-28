package ru.Meowteam.lab.exception;

public class ExitException extends RuntimeException {
    public ExitException() {
        super("Выход из программы");
    }
}