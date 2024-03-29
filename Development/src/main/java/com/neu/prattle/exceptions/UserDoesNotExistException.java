package com.neu.prattle.exceptions;

/***
 * An representation of an error which is thrown where a request has been made
 * for a user object that doesn't exists in the system.
 */
public class UserDoesNotExistException extends Exception {

  public UserDoesNotExistException(String message) {
    super(message);
  }
}
