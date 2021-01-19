package com.neu.prattle.exceptions;

/**
 * A custom exception that represents an error when a group already exists.
 */
public class GroupAlreadyPresentException extends RuntimeException {

  private static final long serialVersionUID = -4845176561270017896L;

  public GroupAlreadyPresentException(String message) {
    super(message);
  }
}
