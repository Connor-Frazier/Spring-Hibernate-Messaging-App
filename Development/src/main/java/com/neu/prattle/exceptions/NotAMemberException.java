package com.neu.prattle.exceptions;

/**
 * A custom exception that represents an error when a user is not a member of a group.
 */
public class NotAMemberException extends RuntimeException {

  public NotAMemberException(String message) {
    super(message);
  }
}
