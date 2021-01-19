package com.neu.prattle.exceptions;

/**
 * A custom exception that represents an error when trying to save a message that already exists.
 */
public class MessageAlreadyExistsException extends RuntimeException {

  public MessageAlreadyExistsException(String message) {
    super(message);
  }
}
