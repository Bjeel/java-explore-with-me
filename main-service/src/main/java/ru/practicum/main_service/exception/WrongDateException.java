package ru.practicum.main_service.exception;

public class WrongDateException extends RuntimeException {
  public WrongDateException(String message) {
    super(message);
  }
}
