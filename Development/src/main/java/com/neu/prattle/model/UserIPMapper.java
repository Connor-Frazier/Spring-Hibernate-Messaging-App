package com.neu.prattle.model;

import org.apache.commons.validator.routines.InetAddressValidator;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.MapsId;
import javax.persistence.OneToOne;
import javax.persistence.Table;

/**
 * Represents the mapping between the users and their ip addresses.
 */
@Entity
@Table(name = "user_ip_mapping")
public class UserIPMapper {

  @Id
  @Column(name = "user_id")
  private int userID;

  @JoinColumn(name = "user_id")
  @OneToOne
  @MapsId
  private User user;

  @Column(name = "ip_address")
  private String ipAddress;

  /**
   * Default constructor.
   */
  public UserIPMapper() {
  }

  /**
   * Constructs the mapping based on the specified parameters.
   *
   * @param user      user
   * @param ipAddress ip address of the user
   */
  public UserIPMapper(User user, String ipAddress) {
    setUser(user);
    setIpAddress(ipAddress);
  }

  public int getUserID() {
    return userID;
  }

  public void setUserID(int userID) {
    this.userID = userID;
  }

  public User getUser() {
    return user;
  }

  public void setUser(User user) {
    this.user = user;
  }

  public String getIpAddress() {
    return ipAddress;
  }

  public void setIpAddress(String ipAddress) {
    if (!validateIPAddress(ipAddress)) {
      throw new IllegalArgumentException("Malformed IP address");
    }
    this.ipAddress = ipAddress;
  }

  /**
   * Validates the correctness of the ip address before setting it.
   *
   * @param ipAddress ip address to be validated
   * @return true if valid, else false
   */
  private boolean validateIPAddress(String ipAddress) {
    InetAddressValidator validator = InetAddressValidator.getInstance();
    return (validator.isValidInet4Address(ipAddress) || validator.isValidInet6Address(ipAddress));
  }
}
