package com.neu.prattle.exceptions;

/**
 * A custom exception that represents an error when a group can not be found.
 */
public class GroupNotFoundException extends RuntimeException {

  public GroupNotFoundException(String message) {
    super(message);
  }
}
