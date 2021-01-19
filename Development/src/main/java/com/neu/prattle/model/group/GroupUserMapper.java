package com.neu.prattle.model.group;

import com.fasterxml.jackson.annotation.JsonBackReference;
import com.fasterxml.jackson.annotation.JsonManagedReference;
import com.neu.prattle.model.User;

import java.util.Objects;

import javax.persistence.Column;
import javax.persistence.EmbeddedId;
import javax.persistence.Entity;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;
import javax.persistence.MapsId;
import javax.persistence.Table;

/**
 * A POJO for the group user mapping.
 */
@Entity
@Table(name = "group_user_mapping")
public class GroupUserMapper {

  @EmbeddedId
  private GroupUserCompositeKey id;

  @ManyToOne
  @MapsId("group_id")
  @JoinColumn(name = "group_id", nullable = false)
  @JsonBackReference
  private Group group;

  @ManyToOne
  @MapsId("user_id")
  @JoinColumn(name = "user_id", nullable = false)
  @JsonManagedReference
  private User user;

  @Column(name = "isModerator")
  private boolean isModerator;

  @Column(name = "isFollower")
  private boolean isFollower;

  @Column(name = "isMember")
  private boolean isMember;

  /**
   * Default constructor.
   */
  public GroupUserMapper() {
  }

  /**
   * Constructor for creating a mapping with a group and user entity.
   * @param group the group in the mapping.
   * @param user the user in the mapping.
   * @param key the key that relates the two in the database.
   * @param isModerator is the user a moderator.
   * @param isFollower is the user a follower of the group.
   * @param isMember is the user a member of the group.
   */
  public GroupUserMapper(Group group, User user, GroupUserCompositeKey key, boolean isModerator,
                         boolean isFollower,
                         boolean isMember) {
    this.group = group;
    this.user = user;
    this.id = key;
    this.isModerator = isModerator;
    this.isFollower = isFollower;
    this.isMember = isMember;
  }

  /**
   * Constructor for creating a mapping with only the relation key.
   * @param key the key that relates the two in the database.
   * @param isModerator is the user a moderator.
   * @param isFollower is the user a follower of the group.
   * @param isMember is the user a member of the group.
   */
  public GroupUserMapper(GroupUserCompositeKey key, boolean isModerator, boolean isFollower,
                         boolean isMember) {
    this.id = key;
    this.isModerator = isModerator;
    this.isFollower = isFollower;
    this.isMember = isMember;
  }

  public GroupUserCompositeKey getId() {
    return id;
  }

  public void setId(GroupUserCompositeKey id) {
    this.id = id;
  }

  public Group getGroup() {
    return group;
  }

  public void setGroup(Group group) {
    this.group = group;
  }

  public boolean isModerator() {
    return isModerator;
  }

  public void setModerator(boolean moderator) {
    isModerator = moderator;
  }

  public boolean isFollower() {
    return isFollower;
  }

  public void setFollower(boolean follower) {
    isFollower = follower;
  }

  public boolean isMember() {
    return isMember;
  }

  public void setMember(boolean member) {
    isMember = member;
  }

  public User getUser() {
    return user;
  }

  public void setUser(User user) {
    this.user = user;
  }

  @Override
  public boolean equals(Object o) {
    if (this == o) {
      return true;
    }
    if (o == null || getClass() != o.getClass()) {
      return false;
    }
    GroupUserMapper that = (GroupUserMapper) o;
    return isModerator == that.isModerator &&
            isFollower == that.isFollower &&
            isMember == that.isMember &&
            Objects.equals(id, that.id) &&
            Objects.equals(group, that.group) &&
            Objects.equals(user, that.user);
  }

  @Override
  public int hashCode() {
    return Objects.hash(id, group.getGroupID(), user.getUserID(), isModerator, isFollower, isMember);
  }
}
