package com.neu.prattle.dto;

/**
 * A public class to work as a data transfer object for the User POJO object.
 */
public class UserDTO {

  private String username;
  private String password;
  private String firstName;
  private String lastName;
  private String contactNumber;
  private String timezone;
  private String profilePicturePath;

  public String getUsername() {
    return username;
  }

  public String getPassword() {
    return password;
  }

  public String getFirstName() {
    return firstName;
  }

  public String getLastName() {
    return lastName;
  }

  public String getContactNumber() {
    return contactNumber;
  }

  public String getTimezone() {
    return timezone;
  }

  public String getProfilePicturePath() {
    return profilePicturePath;
  }

  public void setUsername(String username) {
    this.username = username;
  }

  public void setPassword(String password) {
    this.password = password;
  }

  public void setFirstName(String firstName) {
    this.firstName = firstName;
  }

  public void setLastName(String lastName) {
    this.lastName = lastName;
  }

  public void setContactNumber(String contactNumber) {
    this.contactNumber = contactNumber;
  }

  public void setTimezone(String timezone) {
    this.timezone = timezone;
  }

  public void setProfilePicturePath(String profilePicturePath) {
    this.profilePicturePath = profilePicturePath;
  }
}
