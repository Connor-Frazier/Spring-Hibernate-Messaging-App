package com.neu.prattle.exceptions;

/**
 * A custom exception that represents an error when trying to find a message that could not be found
 * or does not exist.
 */
public class MessageDoesNotExistException extends RuntimeException {
  
  public MessageDoesNotExistException(String message) {
    super(message);
  }
}
