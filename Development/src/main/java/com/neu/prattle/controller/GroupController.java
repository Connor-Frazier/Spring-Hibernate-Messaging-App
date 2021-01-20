package com.neu.prattle.controller;

import com.neu.prattle.dto.GroupDTO;
import com.neu.prattle.dto.GroupMemberDTO;
import com.neu.prattle.dto.SubGroupDTO;
import com.neu.prattle.exceptions.GroupNotFoundException;
import com.neu.prattle.exceptions.UserDoesNotExistException;
import com.neu.prattle.model.User;
import com.neu.prattle.model.group.Group;
import com.neu.prattle.model.group.GroupUserMapper;
import com.neu.prattle.repository.group.GroupRepository;
import com.neu.prattle.repository.group.GroupUserMapperRepository;
import com.neu.prattle.service.UserService;
import com.neu.prattle.service.group.GroupService;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.bind.annotation.RestController;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

import javax.ws.rs.core.Response;

/**
 * A REST controller for handling CRUD operations on Group objects.
 */
@RestController
@RequestMapping(path = "/rest/group")
@CrossOrigin(origins = {"http://com.northeastern.cs5500.team1.s3-website.us-east-2.amazonaws.com", "http://localhost:3000"})
public class GroupController {

    private GroupService groupService;

    private UserService userService;

    private Logger logger = LoggerFactory.getLogger(GroupController.class);

    private GroupRepository groupRepository;

    private GroupUserMapperRepository groupUserMapperRepository;

    @Autowired
    public void setGroupUserMapperRepository(GroupUserMapperRepository groupUserMapperRepository) {
        this.groupUserMapperRepository = groupUserMapperRepository;
    }

    @Autowired
    public void setGroupRepository(GroupRepository groupRepository) {
        this.groupRepository = groupRepository;
    }

    @Autowired
    public void setGroupService(GroupService groupService) {
        this.groupService = groupService;
    }

    @Autowired
    public void setUserService(UserService userService) {
        this.userService = userService;
    }

    /**
     * Handles a HTTP POST request to create a group.
     *
     * @param groupDTO dto representing the group.
     * @return A Response indicating the outcome of the requested operation.
     */
    @PostMapping(value = "/create")
    public Response createGroup(@RequestBody GroupDTO groupDTO) {
        logger.info("Attempting to create group {} ...", groupDTO.getGroupName());
        Group groupToBeAdded = new Group(groupDTO);
        if (groupService.findGroupByName(groupToBeAdded.getGroupName()).isPresent()) {
            logger.error("Group {} already exists", groupDTO.getGroupName());
            return Response.status(500, "Group already exists").build();
        }
        groupService.addGroup(groupToBeAdded);
        Optional<User> optionalModerator = userService.findUserByName(groupDTO.getModeratorName());
        if (optionalModerator.isPresent()) {
            User moderator = optionalModerator.get();
            groupService.addMemberToGroup(groupToBeAdded, moderator, true, true, true);
            logger.info("Group {} created", groupDTO.getGroupName());
            return Response.ok().build();
        }
        logger.error("User {} created", groupDTO.getModeratorName());
        return Response.status(500, "Moderator not found").build();
    }

    /**
     * Handles a HTTP PUT request to add a member to group.
     *
     * @param groupMemberDTO dto representing user.
     * @return A Response indicating the outcome of the requested operation.
     */
    @PutMapping(value = "/addmember/{requesterName}")
    public Response addMember(@RequestBody GroupMemberDTO groupMemberDTO, @PathVariable String requesterName) {
        logger.info("Attempting to add user {} to group {}", groupMemberDTO.getMemberName(),
                groupMemberDTO.getGroupName());
        Optional<User> optionalUser = this.userService.findUserByName(groupMemberDTO.getMemberName());
        if (!optionalUser.isPresent()) {
            logger.error("User {} not found", groupMemberDTO.getMemberName());
            return Response.status(500, "User being invited does not exist").build();
        }
        User potentialMember = optionalUser.get();
        Optional<User> optionalRequester = userService.findUserByName(requesterName);
        if (!optionalRequester.isPresent()) {
            logger.error("Requester {} not found", groupMemberDTO.getMemberName());
            return Response.status(500, "User initiating the invite does not exist").build();
        }
        User requester = optionalRequester.get();
        Optional<Group> optionalGroup = this.groupService.findGroupByName(groupMemberDTO.getGroupName());
        if (!optionalGroup.isPresent()) {
            logger.error("Group {} not found", groupMemberDTO.getGroupName());
            return Response.status(500, "Group not found").build();
        }
        Group group = optionalGroup.get();
        if (groupService.isModerator(group, requester)) {
            this.groupService.addMemberToGroup(group, potentialMember,
                    groupMemberDTO.isModerator(), groupMemberDTO.isFollower(), groupMemberDTO.isMember());
        } else {
            groupService.addMemberToGroup(group, potentialMember, false, false, false);
        }
        logger.info("User {} added to group {}", groupMemberDTO.getMemberName(),
                groupMemberDTO.getGroupName());
        return Response.ok().build();
    }

    /**
     * Handles a HTTP GET request to get all members part of specified group.
     *
     * @param groupName the name of the group.
     * @return A Response indicating the outcome of the requested operation.
     */
    @GetMapping(value = "/getmembersbygroup/{groupName}")
    public @ResponseBody
    List<User> getMembersByGroupName(@PathVariable("groupName") String groupName) {
        logger.info("Attempting to return members of group {}", groupName);
        return groupService.getMemberTypeByGroupName(groupName, true, false, false);
    }

    /**
     * Handles a HTTP GET request to get a group by the group's name.
     *
     * @param group the name of the group.
     * @return A Response indicating the outcome of the requested operation.
     */
    @GetMapping(value = "/{group}")
    public Group getGroup(@PathVariable String group) {
        logger.info("Attempting to return group {}", group);
        return groupService.findGroupByName(group).orElse(null);
    }

    /**
     * Handles a HTTP GET request to get a list of all groups.
     *
     * @return A Response indicating the outcome of the requested operation.
     */
    @GetMapping(value = "/allgroups")
    public List<Group> getAllGroups() {
        logger.info("Attempting to return all groups");
        return groupService.findGroups();
    }

    /**
     * Handles a HTTP GET request to get the groups that a user belongs to.
     *
     * @param username the username of the user to get the groups for.
     * @return A Response indicating the outcome of the requested operation.
     */
    @GetMapping(value = "/mygroup/{username}")
    public List<Group> getMyGroups(@PathVariable String username) {
        logger.info("Attempting to return all groups that {} is part of", username);
        Optional<User> optionalUser = userService.findUserByName(username);
        if (!optionalUser.isPresent()) {
            logger.info("{} is not part of any groups", username);
            return new ArrayList<>();
        }
        logger.info("Returned groups for user {}", username);
        return groupService.getGroupsForUser(optionalUser.get());
    }

    /**
     * Handles a HTTP PUT request to update a group.
     *
     * @param groupDTO a dto representing the changes to the group.
     * @param groupName the name of the group to make the updates to.
     * @return A Response indicating the outcome of the requested operation.
     */
    @PutMapping(value = "/update/{groupName}")
    public Response updateGroup(@RequestBody GroupDTO groupDTO, @PathVariable String groupName) {
        try {
            logger.info("Attempting to update group {}", groupName);
            groupService.updateGroup(groupDTO, groupName);
        } catch (GroupNotFoundException e) {
            logger.error("Group {} which is being attempted to be updated not found", groupName);
            return Response.status(500, e.getMessage()).build();
        }
        logger.info("Group {} updated", groupName);
        return Response.ok().build();
    }

    /**
     * Handles a HTTP GET request to get the list of moderators for a group.
     *
     * @param groupName the name of the group.
     * @return A Response indicating the outcome of the requested operation.
     */
    @GetMapping(value = "/moderators/{groupName}")
    public List<User> getModerators(@PathVariable String groupName) {
        logger.info("Attempting to get moderators of group {}", groupName);
        Optional<Group> optionalGroup = groupService.findGroupByName(groupName);
        List<User> resultUsers = new ArrayList<>();
        if (optionalGroup.isPresent()) {
            List<GroupUserMapper> mapper = optionalGroup.get().getMappings();
            for (GroupUserMapper mapping : mapper) {
                if (mapping.isModerator()) {
                    logger.info("Retrieved moderators of group {}", groupName);
                    resultUsers.add(mapping.getUser());
                }
            }
        }
        logger.error("Group {} for which moderators are being requested not found", groupName);
        return resultUsers;
    }

    /**
     * Handles a HTTP PUT request to add a user as a moderator to a group.
     *
     * @param groupName the name of the group.
     * @param username the username of the user being added as a moderator.
     * @param requesterName the username of the user that is adding the new moderator.
     * @return A Response indicating the outcome of the requested operation.
     */
    @PutMapping(value = "/moderators/{groupName}/{username}/{requesterName}")
    public Response addModerator(@PathVariable String groupName,
                                 @PathVariable String username, @PathVariable String requesterName) {
        return toggleModeratorStatus(groupName, username, requesterName, true);
    }

    /**
     * Handles a HTTP DELETE request to remove a user as a moderator to a group.
     *
     * @param groupName the name of the group.
     * @param username the username of the user that is being removed as a moderator.
     * @param requesterName the username of the user that is removing a moderator.
     * @return A Response indicating the outcome of the requested operation.
     */
    @DeleteMapping(value = "/moderators/{groupName}/{username}/{requesterName}")
    public Response removeModerator(@PathVariable String groupName,
                                    @PathVariable String username, @PathVariable String requesterName) {
        return toggleModeratorStatus(groupName, username, requesterName, false);
    }

    /**
     * Handles a HTTP PUT request that adds a user as a follower to a group.
     *
     * @param username the username of the user that is following the group.
     * @param groupName the name of the group.
     * @return A Response indicating the outcome of the requested operation.
     */
    @PutMapping(value = "/{username}/follow/{groupName}")
    public Response followGroup(@PathVariable String username, @PathVariable String groupName) {
        try {
            logger.info("User {} requesting to follow group {}", username, groupName);
            Group group = returnGroup(groupName);
            User user = returnUser(username);
            doFollowOrUnfollow(group, user, true);
        } catch (Exception e) {
            logger.error(e.getMessage());
            return Response.status(500, e.getMessage()).build();
        }
        logger.info("User {} now follows group {}", username, groupName);
        return Response.ok().build();
    }

    /**
     * Handles a HTTP DELETE request to remove a user as a follower of a group.
     *
     * @param username the username of the user to remove as a follower.
     * @param groupName the name of the group.
     * @return A Response indicating the outcome of the requested operation.
     */
    @DeleteMapping(value = "/{username}/unfollow/{groupName}")
    public Response unfollowGroup(@PathVariable String username, @PathVariable String groupName) {
        try {
            logger.info("User {} requesting to unfollow group {}", username, groupName);
            Group group = returnGroup(groupName);
            User user = returnUser(username);
            doFollowOrUnfollow(group, user, false);
        } catch (Exception e) {
            logger.error(e.getMessage());
            return Response.status(500, e.getMessage()).build();
        }
        logger.info("User {} no longer follows group {}", username, groupName);
        return Response.ok().build();
    }

    /**
     * Handles a HTTP GET request to get a list of followers for a group.
     *
     * @param groupName the name of the group.
     * @return A Response indicating the outcome of the requested operation.
     */
    @GetMapping(value = "/{groupName}/followers")
    public List<User> getFollowers(@PathVariable String groupName) {
        logger.info("Attempting to retrieve followers of group {}", groupName);

        Optional<Group> optionalGroup = groupService.findGroupByName(groupName);
        if (!optionalGroup.isPresent()) {
            logger.error("Group {} for which followers are requested not found", groupName);
            return new ArrayList<>();
        }
        Group group = optionalGroup.get();
        List<GroupUserMapper> mapperList = group.getMappings();
        List<User> result = new ArrayList<>();
        for (GroupUserMapper mapper : mapperList) {
            if (mapper.isFollower()) {
                result.add(mapper.getUser());
            }
        }
        logger.info("Followers of group {} retrieved successfully", groupName);
        return result;
    }

    /**
     * Handles a HTTP POST request to a add a group as a subgroup to a group.
     *
     * @param subGroupDTO a dto containing the parent and subgroup data.
     * @return A Response indicating the outcome of the requested operation.
     */
    @PostMapping(value = "/addSubGroup")
    public Response addSubGroup(@RequestBody SubGroupDTO subGroupDTO) {
        Group parent = groupService.findGroupById(subGroupDTO.getParentId()).orElse(null);
        Group child = groupService.findGroupById(subGroupDTO.getChildId()).orElse(null);
        if (parent == null) {
            return Response.status(500, "Parent group does not exist!").build();
        } else if (child == null) {
            return Response.status(500, "Child group does not exist!").build();
        }
        groupService.addSubGroup(parent, child);
        return Response.ok(parent).build();
    }

    /**
     * Handles a HTTP GET request to check if a user is a moderator for a group.
     * @param username the username of the user to check.
     * @param groupName the name of the group to check.
     * @return A Response indicating the outcome of the requested operation.
     */
    @GetMapping(value = "/{groupName}/{username}/isModerator")
    public boolean checkIfModerator(@PathVariable String username, @PathVariable String groupName) {
        Group group;
        User user;
        try {
            group = returnGroup(groupName);
            user = returnUser(username);
        } catch (Exception e) {
            return false;
        }
        return this.groupService.isModerator(group, user);
    }

    /**
     * Handles a HTTP GET request to check if a user is a member of a group.
     * @param username the username of the user to check.
     * @param groupName the name of the group to check.
     * @return A Response indicating the outcome of the requested operation.
     */
    @GetMapping(value = "/{groupName}/{username}/isMember")
    public boolean checkIfMember(@PathVariable String username, @PathVariable String groupName) {
        Group group;
        User user;
        try {
            group = returnGroup(groupName);
            user = returnUser(username);
        } catch (Exception e) {
            return false;
        }
        return this.groupService.isMember(group, user);
    }

    /**
     * Handles a HTTP GET request check if a user is a follower of a group.
     * @param username the username of the user to check.
     * @param groupName the name of the group to check.
     * @return A Response indicating the outcome of the requested operation.
     */
    @GetMapping(value = "/{groupName}/{username}/isFollower")
    public boolean checkIfFollower(@PathVariable String username, @PathVariable String groupName) {
        Group group;
        User user;
        try {
            group = returnGroup(groupName);
            user = returnUser(username);
        } catch (Exception e) {
            return false;
        }
        return this.groupService.isFollower(group, user);
    }

    /**
     * Handles a HTTP PUT request to allow a user to accept an invite to a group.
     * @param groupName the name of the group.
     * @param username the username of the user.
     * @return A Response indicating the outcome of the requested operation.
     */
    @PutMapping(value = "/invite/{groupName}/accept/{username}")
    public Response acceptInvite(@PathVariable String groupName, @PathVariable String username) {
        Group group;
        User user;
        try {
            group = returnGroup(groupName);
            user = returnUser(username);
            groupService.acceptInvite(group, user);
        } catch (Exception e) {
            return Response.status(500, "User or group does not exist").build();
        }
        return Response.ok().build();
    }

    /**
     * Handles a HTTP DELETE request to allow a user to decline an invite to a group.
     * @param groupName the name of the group.
     * @param username the username of the user.
     * @return A Response indicating the outcome of the requested operation.
     */
    @DeleteMapping(value = "/invite/{groupName}/reject/{username}")
    public Response rejectInvite(@PathVariable String groupName, @PathVariable String username) {
        Group group;
        User user;
        try {
            group = returnGroup(groupName);
            user = returnUser(username);
            groupService.rejectInvite(group, user);
        } catch (Exception e) {
            return Response.status(500, "User or group does not exist").build();
        }
        return Response.ok().build();
    }

    /**
     * Handles a HTTP GET request to the users that have been invited to join the group.
     *
     * @param groupName the name of the group.
     * @return A Response indicating the outcome of the requested operation.
     */
    @GetMapping(value = "/{groupName}/invites")
    public List<User> getInvites(@PathVariable String groupName) {
        Group group;
        try {
            group = returnGroup(groupName);
        } catch (Exception e) {
            return new ArrayList<>();
        }
        return groupService.getInvites(group);
    }

    /**
     * Handles a HTTP GET request to get the id of group given the group name.
     *
     * @param groupName the name of the group.
     * @return A Response indicating the outcome of the requested operation.
     */
    @GetMapping(value = "/{groupName}/groupID")
    public int getGroupID(@PathVariable String groupName) {
        Optional<Group> optionalGroup = groupService.findGroupByName(groupName);
        return optionalGroup.map(Group::getGroupID).orElse(-1);
    }


    /**
     * Handles a HTTP GET request to get the name of a group givent the group id.
     * @param groupID the id of the group.
     * @return A Response indicating the outcome of the requested operation.
     */
    @GetMapping(value = "/{groupID}/groupName")
    public String getGroupName(@PathVariable int groupID) {
        Optional<Group> optionalGroup = groupService.findGroupById(groupID);
        return optionalGroup.map(Group::getGroupName).orElse(null);
    }


    /**
     * Helper method to get the user object for a username.
     *
     * @param username the username of the user.
     * @throws UserDoesNotExistException when the username does not belong to a current user in the system.
     */
    private User returnUser(String username) throws UserDoesNotExistException {
        Optional<User> optionalUser = userService.findUserByName(username);
        if (!optionalUser.isPresent()) {
            throw new UserDoesNotExistException("User does not exist");
        }
        return optionalUser.get();
    }

    /**
     * Helper method to toggle a user's status as a follower of a group.
     *
     * @param group the name of the group.
     * @param user the user to toggle the follow status for.
     * @param follow true if setting the status to following, false if setting to un-following.
     * @throws UserDoesNotExistException thrown when the user does not exist.
     */
    private void doFollowOrUnfollow(Group group, User user, boolean follow) throws UserDoesNotExistException {
        if (follow) {
            followSteps(group, user);
        } else {
            unfollowSteps(group, user);
        }
    }

    /**
     * Helper method to remove a user as a follower of a group.
     *
     * @param group the group object.
     * @param user  the user object.
     * @throws UserDoesNotExistException when user does not exist.
     */
    private void unfollowSteps(Group group, User user) throws UserDoesNotExistException {
        List<GroupUserMapper> mappers = group.getMappings();
        for (GroupUserMapper mapping : mappers) {
            if (mapping.getUser() == user) {
                mapping.setFollower(false);
                groupService.updateFollowersFeed(group.getGroupName(), user.getUsername() + " unfollowed " + group.getGroupName());
                groupUserMapperRepository.save(mapping);
                groupRepository.save(group);
                return;
            }
        }
        throw new UserDoesNotExistException("User does not follow group");
    }

    /**
     * Helper method to add a user as a follower of a group.
     *
     * @param group the group object.
     * @param user the user object.
     */
    private void followSteps(Group group, User user) {
        List<GroupUserMapper> mappers = group.getMappings();
        if (groupService.isModerator(group, user) || groupService.isMember(group, user)) {
            for (GroupUserMapper mapping : mappers) {
                if (mapping.getUser() == user) {
                    mapping.setFollower(true);
                    groupUserMapperRepository.save(mapping);
                }
            }
            groupRepository.save(group);
        } else {
            groupService.addMemberToGroup(group, user, false, true, false);
        }
        groupService.updateFollowersFeed(group.getGroupName(), user.getUsername() + " started following " + group.getGroupName());
    }

    /**
     * Helper method to get the group object given a group name.
     *
     * @param groupName the name of the group.
     * @return the group object of the group with the given name.
     */
    private Group returnGroup(String groupName) {
        Optional<Group> optionalGroup = groupService.findGroupByName(groupName);
        if (!optionalGroup.isPresent()) {
            throw new GroupNotFoundException("Group does not exist");
        }
        return optionalGroup.get();
    }

    /**
     * Helper method to toggle moderator status of a user for a group.
     *
     * @param groupName the name of the group.
     * @param username the username of the user to toggle the moderator status for.
     * @param requesterName the username of the user make the request.
     * @param isModerator   true if set to moderator, else false
     * @return A Response indicating the outcome of the requested operation.
     */
    private Response toggleModeratorStatus(String groupName, String username, String requesterName,
                                           boolean isModerator) {

        Group group = getGroup(groupName);
        if (group == null) {
            return Response.status(400, "Group does not exist").build();
        }
        if (!checkIfModerator(requesterName, groupName)) {
            return Response.status(400, "Requester is not a moderator").build();
        }
        User user = getUser(username);
        if (user == null) {
            return Response.status(400, "User being modified does not exist").build();
        }
        List<GroupUserMapper> mappings = group.getMappings();
        boolean matchFound = false;
        for (GroupUserMapper mapping : mappings) {
            if (mapping.getUser() == user) {
                mapping.setModerator(isModerator);
                groupUserMapperRepository.save(mapping);
                groupRepository.save(group);
                matchFound = true;
                break;
            }
        }
        if (matchFound) {
            return Response.ok().build();
        }
        return Response.status(400, "User does not exist in the group").build();
    }

    /**
     * Helper method to get a user object of a user given a username.
     * @param username the username of the user to get the user object for.
     * @return the user object of the user.
     */
    private User getUser(String username) {
        Optional<User> optionalUser = userService.findUserByName(username);
        return optionalUser.orElse(null);
    }

}
