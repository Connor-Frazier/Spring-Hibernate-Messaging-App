package com.neu.prattle.controller;

import com.neu.prattle.dto.UserDTO;
import com.neu.prattle.exceptions.UserAlreadyPresentException;
import com.neu.prattle.exceptions.UserDoesNotExistException;
import com.neu.prattle.model.User;
import com.neu.prattle.model.UserFeedMapper;
import com.neu.prattle.model.UserIPMapper;
import com.neu.prattle.repository.UserIPMapperRepository;
import com.neu.prattle.service.UserService;
import com.neu.prattle.utils.JwtUtil;

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
import org.springframework.web.bind.annotation.RestController;

import java.util.List;
import java.util.Optional;

import javax.servlet.http.HttpServletRequest;
import javax.ws.rs.core.Response;

/**
 * A REST controller for handling CRUD operations on User objects.
 */
@RestController
@RequestMapping(path = "/rest/user")
@CrossOrigin(origins = {"http://com.northeastern.cs5500.team1.s3-website.us-east-2.amazonaws.com", "http://localhost:3000"})
public class UserController {

    private UserService userService;
    private UserIPMapperRepository userIPMapperRepository;
    private Logger logger = LoggerFactory.getLogger(UserController.class);

    @Autowired
    public void setUserService(UserService userService) {
        this.userService = userService;
        logger.info("User service bean created");
    }

    @Autowired
    public void setUserIPMapperRepository(UserIPMapperRepository userIPMapperRepository) {
        this.userIPMapperRepository = userIPMapperRepository;
        logger.info("User IPMapper bean created");
    }

    /**
     * Handles a HTTP POST request for user creation
     *
     * @param user The User object decoded from the payload of POST request.
     * @return A Response indicating the outcome of the requested operation.
     */
    @PostMapping(value = "/create")
    public Response createUserAccount(@RequestBody UserDTO user, HttpServletRequest request) {
        logger.info("Attempting to create user account for {}", user.getUsername());
        try {
            User userToBeAdded = User.getUserBuilder()
                    .username(user.getUsername())
                    .password(user.getPassword())
                    .firstName(user.getFirstName())
                    .lastName(user.getLastName())
                    .contactNumber(user.getContactNumber())
                    .timezone(user.getTimezone())
                    .profilePicture(user.getProfilePicturePath())
                    .build();
            UserIPMapper mapper = new UserIPMapper(userToBeAdded, getRemoteAddress(request));
            userToBeAdded.setUserIPMapper(mapper);
            this.userService.addUser(userToBeAdded);
        } catch (UserAlreadyPresentException e) {
            logger.error("User {} already exists", user.getUsername());
            return Response.status(409).build();
        }
        logger.info("Account created for {}", user.getUsername());
        return Response.ok().build();
    }

    /**
     * Handles a HTTP GET request for getting a User.
     *
     * @param name The name of the user to get.
     * @return A Response indicating the outcome of the requested operation.
     */
    @GetMapping(value = "/{name}")
    public User getUser(@PathVariable String name) {
        logger.info("Reading user account for {}", name);
        return userService.findUserByName(name).orElse(null);
    }

    /**
     * Handles a HTTP POST request for logging in.
     *
     * @param user The User object decoded from the payload of POST request to use for login.
     * @return A Response indicating the outcome of the requested operation.
     */
    @PostMapping(value = "/login")
    public Response userLogin(@RequestBody UserDTO user, HttpServletRequest request) {
        User loggedUser;
        try {
            logger.info("Attempting to login  {}", user.getUsername());
            loggedUser = this.userService.validateUser(user.getUsername(), user.getPassword());
        } catch (IllegalStateException e) {
            logger.error("User {} being logged in does not exist", user.getUsername());
            return Response.status(400, "User attempted to "
                    + "be logged in does not exist").build();
        }
        updateUserIpAddress(loggedUser.getUsername(), getRemoteAddress(request));
        logger.info("User {} logged in", user.getUsername());
        return Response.ok().build();
    }

    /**
     * Handles a HTTP POST request for logging in.
     *
     * @param user The User object decoded from the payload of POST request to use for login.
     * @return A Response indicating the outcome of the requested operation.
     */
    @PostMapping(value = "/securelogin")
    public String secureUserLogin(@RequestBody UserDTO user, HttpServletRequest request) {
        User loggedUser;
        try {
            logger.info("Attempting to login {}", user.getUsername());
            loggedUser = this.userService.validateUser(user.getUsername(), user.getPassword());
            updateUserIpAddress(loggedUser.getUsername(), getRemoteAddress(request));
            this.userService.updateFollowersFeed(loggedUser.getUsername(), loggedUser.getUsername() + " has logged in");
        } catch (IllegalStateException | UserDoesNotExistException e) {
            logger.error("User {} does not exist", user.getUsername());
            return Response.status(400, "User does not exist").build().toString();
        }
        logger.info("User {} logged in", user.getUsername());
        return JwtUtil.generateToken(loggedUser);
    }

    /**
     * Handles a HTTP GET request for getting a User.
     *
     * @return A Response indicating the outcome of the requested operation.
     */
    @GetMapping(value = "/allusers")
    public List<User> getAllUsers() {
        logger.info("Attempting to read all users");
        List<User> result = userService.getAllUsers();
        for (User user : result) {
            user.setFollowees(null);
            user.setFollowers(null);
        }
        logger.info("All users retrieved");
        return result;
    }

    /**
     * Handles a HTTP PUT request to update a user's details.
     *
     * @param user dto object representing the changes
     * @param username user name of the user to be updated
     * @return A Response indicating the outcome of the requested operation.
     */
    @PutMapping(value = "/update/{username}")
    public User updateUser(@RequestBody UserDTO user, @PathVariable String username)
            throws UserDoesNotExistException {
        logger.info("Attempting to updated user details");
        return this.userService.updateUser(user, username);
    }

    /**
     * Handles a HTTP DELETE request to delete a user.
     *
     * @param username the username of the user to delete.
     * @return A Response indicating the outcome of the requested operation.
     */
    @DeleteMapping(value = "/{username}")
    public Response deleteUser(@PathVariable String username) {
        try {
            logger.info("Attempting to delete user {}", username);
            userService.removeUser(username);
        } catch (UserDoesNotExistException e) {
            logger.error("User {} being deleted does not exist", username);
            return Response.status(400, "User does not exist").build();
        }
        logger.info("User {} deleted", username);
        return Response.ok().build();
    }

    /**
     * Handles a HTTP PUT request to add a follower to a user.
     *
     * @param username the username of the user who requested the follow.
     * @param followUsername the username of the user to be followed.
     * @return A Response indicating the outcome of the requested operation.
     */
    @PutMapping(value = "/{username}/follow/{followUsername}")
    public Response followUser(@PathVariable String username, @PathVariable String followUsername) {
        try {
            logger.info("{} is attempting to follow {}", username, followUsername);
            userService.addFollower(followUsername, username);
        } catch (UserDoesNotExistException e) {
            logger.error("Either of {} or {} does not exist", username, followUsername);
            return Response.status(400, "User attempted to be followed does not exist")
                    .build();
        }
        logger.info("{} now follows {}", username, followUsername);
        return Response.ok().build();
    }

    /**
     * Handles a HTTP DELETE request to remove a follow for a user.
     *
     * @param username the username of the user to be un-followed.
     * @param followerName the username of the current user.
     * @return A Response indicating the outcome of the requested operation.
     */
    @DeleteMapping(value = "/{username}/unfollow/{followerName}")
    public Response unfollowUser(@PathVariable String username, @PathVariable String followerName) {
        try {
            logger.info("{} is attempting to unfollow {}", username, followerName);
            userService.removeFollower(followerName, username);
        } catch (UserDoesNotExistException e) {
            logger.error("Either of {} or {} does not exist", username, followerName);
            return Response.status(400, "Problem occurred while unfollowing")
                    .build();
        }
        logger.info("{} no longer follows {}", followerName, username);
        return Response.ok().build();
    }

    /**
     * Handles a HTTP GET request for getting a list of followers.
     *
     * @return A Response indicating the outcome of the requested operation.
     */
    @GetMapping(value = "/{username}/followers")
    public List<User> getAllFollowers(@PathVariable String username) throws UserDoesNotExistException {
        return userService.getAllFollowers(username);
    }

    /**
     * Handles a HTTP GET request for getting a list of following.
     *
     * @return A Response indicating the outcome of the requested operation.
     */
    @GetMapping(value = "/{username}/following")
    public List<User> getAllFollowing(@PathVariable String username) throws UserDoesNotExistException {
        return userService.getAllFollowing(username);
    }

    /**
     * Handles a HTTP POST request for logging a user out.
     *
     * @param username the username of the user to logout.
     * @return A response indicating the outcome of the requested operation.
     */
    @PostMapping(value = "/logout/{username}")
    public Response logoutUser(@PathVariable String username) {
        logger.info("Attempting to logout {}", username);
        try {
            userService.logout(username);
        } catch (Exception e) {
            logger.info("No user associated with username {}", username);
            return Response.status(500, "User attempted to be logged off does not exist").build();
        }
        logger.info("{} logged out successfully", username);
        return Response.ok().build();
    }

    /**
     * Handles a HTTP GET request to get the user id of a user.
     *
     * @param username the username of the user to get the id for.
     * @return A Response indicating the outcome of the requested operation.
     */
    @GetMapping(value = "/{username}/userID")
    public int getUserID(@PathVariable String username) {
        Optional<User> optionalUser = userService.findUserByName(username);
        return optionalUser.map(User::getUserID).orElse(-1);
    }

    /**
     * Handles a HTTP GET request to get the username for a user by the user id.
     *
     * @param userID the user id of the user to get the username for.
     * @return A Response indicating the outcome of the requested operation.
     */
    @GetMapping(value = "/{userID}/username")
    public String getUserName(@PathVariable int userID) {
        Optional<User> optionalUser = userService.findUserById(userID);
        return optionalUser.map(User::getUsername).orElse(null);
    }

    /**
     * Handles a HTTP GET request to a user's user feeds.
     *
     * @param username the username of the user to get the user feeds for.
     * @return A Response indicating the outcome of the requested operation.
     * @throws UserDoesNotExistException when the username does not belong to an existing user.
     */
    @GetMapping(value = "/{username}/feeds")
    public List<UserFeedMapper> getUserFeeds(@PathVariable String username) throws UserDoesNotExistException {
        logger.info("Getting user feeds for {}", username);
        return userService.getUserFeeds(username);
    }

    /**
     * Handles a HTTP GET request to get the avatar file address for a user.
     *
     * @param username the username of the user to get the avatar for.
     * @return A Response indicating the outcome of the requested operation.
     */
    @GetMapping(value = "/{username}/avatar")
    public String getAvatar(@PathVariable String username) {
        Optional<User> optionalUser = userService.findUserByName(username);
        return optionalUser.map(user -> userService.getAvatar(user)).orElse(null);
    }

    /**
     * Private helper to set user's IP address.
     *
     * @param remoteAddr remote IP address
     */
    private void updateUserIpAddress(String username, String remoteAddr) {
        Optional<User> optUser = userService.findUserByName(username);
        optUser.ifPresent(user -> userIPMapperRepository.setIpAddress(user.getUserID(), remoteAddr));
    }

    /**
     * Private helper to return ip address.
     *
     * @param request http request object
     * @return ip address
     */
    private String getRemoteAddress(HttpServletRequest request) {
        String remoteAddr = "";
        if (request != null) {
            remoteAddr = request.getHeader("X-FORWARDED-FOR");
            if (remoteAddr == null || remoteAddr.equals("")) {
                remoteAddr = request.getRemoteAddr();
            }
        }
        return remoteAddr;
    }

}
