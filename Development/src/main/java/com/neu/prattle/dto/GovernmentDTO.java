package com.neu.prattle.dto;

/**
 * A public class to work as a data transfer object for the Government POJO object.
 */
public class GovernmentDTO {

  private String govUsername;
  private String govPassword;

  public String getGovUsername() {
    return govUsername;
  }

  public void setGovUsername(String govUsername) {
    this.govUsername = govUsername;
  }

  public String getGovPassword() {
    return govPassword;
  }

  public void setGovPassword(String govPassword) {
    this.govPassword = govPassword;
  }
}
