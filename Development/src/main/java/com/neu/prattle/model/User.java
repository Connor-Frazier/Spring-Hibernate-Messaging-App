package com.neu.prattle.model;

import com.fasterxml.jackson.annotation.JsonBackReference;
import com.fasterxml.jackson.annotation.JsonIdentityInfo;
import com.fasterxml.jackson.annotation.JsonManagedReference;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.annotation.ObjectIdGenerators;
import com.neu.prattle.model.group.GroupUserMapper;

import org.hibernate.annotations.Fetch;
import org.hibernate.annotations.FetchMode;

import java.sql.Timestamp;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Objects;
import java.util.Set;
import java.util.TimeZone;

import javax.persistence.CascadeType;
import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.FetchType;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.JoinTable;
import javax.persistence.ManyToMany;
import javax.persistence.OneToMany;
import javax.persistence.OneToOne;
import javax.persistence.Table;

/***
 * A User POJO object represents a basic account information for a user.
 */
@Entity
@Table(name = "user")
@JsonIdentityInfo(
        generator = ObjectIdGenerators.PropertyGenerator.class,
        property = "userID")
public class User {

  @Id
  @GeneratedValue(strategy = GenerationType.AUTO)
  @Column(name = "user_id")
  private int userID;

  @Column(name = "username")
  private String username;

  @Column(name = "hashPassword")
  @JsonProperty(access = JsonProperty.Access.WRITE_ONLY)
  private String password;

  @Column(name = "first_name")
  private String firstName;

  @Column(name = "last_name")
  private String lastName;

  @Column(name = "contact_num")
  private String contactNumber;

  @Column(name = "timezone")
  private TimeZone timezone;

  @OneToOne(mappedBy = "user", cascade = CascadeType.ALL)
  private UserIPMapper userIPMapper;

  @ManyToMany
  @JoinTable(
          name = "user_follows",
          joinColumns = @JoinColumn(name = "follower_id"),
          inverseJoinColumns = @JoinColumn(name = "followee_id")
  )
  @JsonManagedReference
  @Fetch(FetchMode.JOIN)
  private Set<User> followers;

  @ManyToMany(mappedBy = "followers")
  @JsonBackReference
  private Set<User> followees;

  @OneToMany(mappedBy = "user")
  @JsonBackReference
  private List<GroupUserMapper> mappings;

  @OneToMany(mappedBy = "user")
  @JsonBackReference
  @Fetch(FetchMode.JOIN)
  private List<UserFeedMapper> userFeeds;

  @Column(name = "last_log_out_time")
  private Timestamp logOutTimestamp;

  @OneToMany(mappedBy = "user")
  @JsonBackReference
  private Set<Subpoena> subpoenas;

  @ManyToMany(cascade = CascadeType.ALL, fetch = FetchType.EAGER)
  @JoinTable(
          name = "user_filter_map",
          joinColumns = @JoinColumn(name = "user_id"),
          inverseJoinColumns = @JoinColumn(name = "filter_id"))
  private Set<Filter> filters;

  @Column(name = "profile_picture_url")
  private String profilePicturePath;

  /**
   * Default constructor.
   */
  public User() {
    super();
  }

  /**
   * Constructs the user using the builder.
   *
   * @param builder builder that builds the user object
   */
  public User(UserBuilder builder) {
    setUsername(builder.userName);
    setPassword(builder.password);
    setFirstName(builder.firstName);
    setLastName(builder.lastName);
    setContactNumber(builder.contactNumber);
    setTimezone(builder.timezone);
    setFollowers(builder.followers);
    setFollowees(builder.followees);
    setLogOutTimestamp(null);
    setUserFeeds(builder.userFeeds);
    setProfilePicturePath(builder.profilePicture);
    setFilters(new HashSet<>());
  }

  public String getUsername() {
    return username;
  }


  public void setUsername(String username) {
    if (username == null) {
      throw new NullPointerException("The user's name cannot be null");
    } else if (username.trim().equals("")) {
      throw new IllegalArgumentException("The user's name cannot be empty");
    }
    this.username = username;
  }

  public String getPassword() {
    return password;
  }

  public void setPassword(String password) {
    if (password == null) {
      throw new NullPointerException("The user's password cannot be null");
    } else if (password.contains(" ")) {
      throw new IllegalArgumentException("The user's password cannot contain empty characters");
    } else if (password.length() < 8) {
      throw new IllegalArgumentException("Specified password is weak");
    }
    this.password = password;
  }


  public int getUserID() {
    return userID;
  }

  public void setUserID(int userID) {
    this.userID = userID;
  }

  public String getFirstName() {
    return firstName;
  }

  public void setFirstName(String firstName) {
    this.firstName = firstName;
  }

  public String getLastName() {
    return lastName;
  }

  public void setLastName(String lastName) {
    this.lastName = lastName;
  }

  public String getContactNumber() {
    return contactNumber;
  }

  public void setContactNumber(String contactNumber) {
    this.contactNumber = contactNumber;
  }

  public TimeZone getTimezone() {
    return timezone;
  }

  public void setTimezone(TimeZone timezone) {
    this.timezone = timezone;
  }

  public UserIPMapper getUserIPMapper() {
    return userIPMapper;
  }

  public void setUserIPMapper(UserIPMapper userIPMapper) {
    this.userIPMapper = userIPMapper;
  }

  public Set<User> getFollowers() {
    return followers;
  }

  public void setFollowers(Set<User> followers) {
    this.followers = followers;
  }

  public Set<User> getFollowees() {
    return followees;
  }

  public void setFollowees(Set<User> followees) {
    this.followees = followees;
  }

  public Timestamp getLogOutTimestamp() {
    return logOutTimestamp;
  }

  public void setLogOutTimestamp(Timestamp logOutTimestamp) {
    this.logOutTimestamp = logOutTimestamp;
  }

  public List<GroupUserMapper> getMappings() {
    return mappings;
  }

  public void setMappings(List<GroupUserMapper> mappings) {
    this.mappings = mappings;
  }

  public List<UserFeedMapper> getUserFeeds() {
    return userFeeds;
  }

  public void setUserFeeds(List<UserFeedMapper> userFeeds) {
    this.userFeeds = userFeeds;
  }

  public Set<Subpoena> getSubpoenas() {
    return subpoenas;
  }

  public void setSubpoenas(Set<Subpoena> subpoenas) {
    this.subpoenas = subpoenas;
  }

  public Set<Filter> getFilters() {
    return filters;
  }

  public void setFilters(Set<Filter> filters) {
    this.filters = filters;
  }

  public String getProfilePicturePath() {
    return profilePicturePath;
  }

  public void setProfilePicturePath(String profilePicturePath) {
    this.profilePicturePath = profilePicturePath;
  }

  @Override
  public boolean equals(Object obj) {
    if (!(obj instanceof User))
      return false;

    User user = (User) obj;
    return user.username.equals(this.username);
  }

  @Override
  public int hashCode() {
    return Objects.hash(username);
  }

  /**
   * Returns the builder to construct the User object.
   *
   * @return builder object
   */
  public static UserBuilder getUserBuilder() {
    return new UserBuilder();
  }

  /**
   * Represents the builder for constructing the {@link com.neu.prattle.model.User} object.
   */
  public static class UserBuilder {

    private String userName;
    private String password;
    private String firstName;
    private String lastName;
    private String contactNumber;
    private TimeZone timezone;
    private Set<User> followers;
    private Set<User> followees;
    private List<UserFeedMapper> userFeeds;
    private String profilePicture;

    UserBuilder() {
      userName = "";
      password = "";
      firstName = "";
      lastName = "";
      contactNumber = "";
      timezone = TimeZone.getTimeZone("UTC");
      followers = new HashSet<>();
      followees = new HashSet<>();
      userFeeds = new ArrayList<>();
      profilePicture = null;
    }

    public UserBuilder username(String userName) {
      this.userName = userName;
      return this;
    }

    public UserBuilder password(String password) {
      this.password = password;
      return this;
    }

    public UserBuilder firstName(String firstName) {
      this.firstName = firstName;
      return this;
    }

    public UserBuilder lastName(String lastName) {
      this.lastName = lastName;
      return this;
    }

    public UserBuilder contactNumber(String contactNumber) {
      this.contactNumber = contactNumber;
      return this;
    }

    public UserBuilder timezone(String timezone) {
      if (timezone == null) {
        timezone = "UTC";
      }
      this.timezone = TimeZone.getTimeZone(timezone);
      return this;
    }

    public UserBuilder profilePicture(String profilePicture) {
      this.profilePicture = profilePicture;
      return this;
    }

    public User build() {
      return new User(this);
    }
  }
}
