package com.neu.prattle.model.group;

import com.fasterxml.jackson.annotation.JsonBackReference;
import com.fasterxml.jackson.annotation.JsonIdentityInfo;
import com.fasterxml.jackson.annotation.JsonManagedReference;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.annotation.JsonProperty.Access;
import com.fasterxml.jackson.annotation.ObjectIdGenerators;
import com.neu.prattle.dto.GroupDTO;

import org.hibernate.annotations.Fetch;
import org.hibernate.annotations.FetchMode;

import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.JoinTable;
import javax.persistence.ManyToMany;
import javax.persistence.OneToMany;
import javax.persistence.Table;

/**
 * A POJO for a group of users.
 * @author Neel Deshpande
 */
@Entity
@Table(name = "`groups`")
@JsonIdentityInfo(
        generator = ObjectIdGenerators.PropertyGenerator.class,
        property = "groupID")
public class Group {

  @Id
  @GeneratedValue(strategy = GenerationType.AUTO)
  @Column(name = "group_id")
  private int groupID;

  @Column(name = "hashPassword")
  @JsonProperty(access = Access.WRITE_ONLY)
  private String password;

  @Column(name = "`name`")
  private String groupName;

  @Column(name = "group_email")
  private String groupEmail;

  @Column(name = "description")
  private String description;

  @OneToMany(mappedBy = "group")
  @JsonManagedReference
  @Fetch(FetchMode.JOIN)
  private List<GroupUserMapper> mappings;

  @ManyToMany
  @JoinTable(
          name = "group_group_mapping",
          joinColumns = @JoinColumn(name = "sub_group_id"),
          inverseJoinColumns = @JoinColumn(name = "parent_group_id")
  )
  @JsonManagedReference
  private List<Group> parentGroups;

  @ManyToMany(mappedBy = "parentGroups")
  @JsonBackReference
  private List<Group> subGroups;

  /**
   * Default constructor.
   */
  public Group() {
    super();
  }

  /**
   * Constructor that build Group object from a GroupBuilder
   *
   * @param groupBuilder the builder object
   */
  public Group(GroupBuilder groupBuilder) {
    setGroupName(groupBuilder.name);
    setPassword(groupBuilder.password);
    setMappings(groupBuilder.mappings);
    setGroupEmail(groupBuilder.email);
    setDescription(groupBuilder.description);
    this.subGroups = new ArrayList<>();
    this.parentGroups = new ArrayList<>();
  }

  public Group(GroupDTO dto) {
    setGroupName(dto.getGroupName());
    setDescription(dto.getDescription());
    setGroupEmail(dto.getGroupEmail());
    setPassword(dto.getPassword());
    this.subGroups = new ArrayList<>();
    this.parentGroups = new ArrayList<>();
  }

  public int getGroupID() {
    return groupID;
  }

  public String getPassword() {
    return password;
  }

  public void setPassword(String password) {
    validateString(password);
    if (password.length() < 8) {
      throw new IllegalArgumentException("Password must be at least 8 characters long!");
    }
    this.password = password;
  }

  public String getGroupName() {
    return groupName;
  }

  public void setGroupName(String name) {
    validateString(name);
    this.groupName = name;
  }

  public String getGroupEmail() {
    return groupEmail;
  }

  public void setGroupEmail(String groupEmail) {
    validateString(groupEmail);
    this.groupEmail = groupEmail;
  }

  public String getDescription() {
    return description;
  }

  public void setDescription(String description) {
    validateString(description);
    this.description = description;
  }

  public List<GroupUserMapper> getMappings() {
    return mappings;
  }

  public void setMappings(List<GroupUserMapper> mappings) {
    this.mappings = mappings;
  }

  public List<Group> getParentGroups() {
    return parentGroups;
  }

  public List<Group> getSubGroups() {
    return subGroups;
  }

  /**
   * Get the builder for Group
   *
   * @return an instance of GroupBuilder
   */
  public static GroupBuilder getBuilder() {
    return new GroupBuilder();
  }

  @Override
  public boolean equals(Object o) {
    if (this == o) {
      return true;
    }
    if (o == null || getClass() != o.getClass()) {
      return false;
    }
    Group group = (Group) o;
    return Objects.equals(groupName, group.groupName) &&
            Objects.equals(groupEmail, group.groupEmail) &&
            Objects.equals(description, group.description) &&
            Objects.equals(mappings, group.mappings);
  }

  @Override
  public int hashCode() {
    return Objects.hash(groupName, groupEmail, description, mappings);
  }

  private void validateString(String candidate) {
    if (candidate == null) {
      throw new NullPointerException("Candidate cannot be null!");
    }
    if (candidate.equals("")) {
      throw new IllegalArgumentException(candidate + " cannot be empty!");
    }
  }

  /**
   * A builder helper class to create instances of {@link Group}
   */
  public static class GroupBuilder {

    private String name;
    private String password;
    private String email;
    private String description;
    private List<GroupUserMapper> mappings;

    GroupBuilder() {
      name = "";
      password = "";
      email = "";
      description = "";
      mappings = new ArrayList<>();
    }

    public GroupBuilder name(String groupName) {
      this.name = groupName;
      return this;
    }

    public GroupBuilder password(String password) {
      this.password = password;
      return this;
    }

    public GroupBuilder users(List<GroupUserMapper> members) {
      this.mappings = members;
      return this;
    }

    public GroupBuilder email(String email) {
      this.email = email;
      return this;
    }

    public GroupBuilder description(String description) {
      this.description = description;
      return this;
    }

    public Group build() {
      return new Group(this);
    }
  }
}
