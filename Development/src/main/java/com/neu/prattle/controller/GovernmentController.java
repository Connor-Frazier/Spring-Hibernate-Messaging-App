package com.neu.prattle.controller;


import com.neu.prattle.dto.GovernmentDTO;
import com.neu.prattle.exceptions.UserDoesNotExistException;
import com.neu.prattle.model.Government;
import com.neu.prattle.model.Message;
import com.neu.prattle.model.Subpoena;
import com.neu.prattle.model.User;
import com.neu.prattle.service.GovernmentService;
import com.neu.prattle.service.MessageService;
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

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

import javax.ws.rs.core.Response;

/**
 * A REST controller for handling CRUD operations on Government objects.
 */
@RestController
@RequestMapping(path = "/rest/government")
@CrossOrigin(origins = {"http://com.northeastern.cs5500.team1.s3-website.us-east-2.amazonaws.com", "http://localhost:3000"})
public class GovernmentController {

    private Logger logger = LoggerFactory.getLogger(GovernmentController.class);

    private GovernmentService governmentService;
    private UserService userService;
    private MessageService messageService;

    @Autowired
    public void setMessageService(MessageService messageService) {
        this.messageService = messageService;
    }

    @Autowired
    public void setUserService(UserService userService) {
        this.userService = userService;
    }

    @Autowired
    public void setGovernmentService(GovernmentService governmentService) {
        this.governmentService = governmentService;
    }

    /**
     * Handles a HTTP POST request for logging in.
     *
     * @param governmentDTO the dto object decoded from the payload of POST request to use for login
     * @return - A Response indicating the outcome of the requested operation.
     */
    @PostMapping(value = "/login")
    public String secureUserLogin(@RequestBody GovernmentDTO governmentDTO) {
        Government government;
        try {
            logger.info("Attempting to login {}", governmentDTO.getGovUsername());
            government = governmentService.validateAccount(governmentDTO.getGovUsername(),
                    governmentDTO.getGovPassword());
        } catch (IllegalStateException e) {
            logger.error("User {} does not exist", governmentDTO.getGovUsername());
            return Response.status(400, "Account does not exist").build().toString();
        }
        logger.info("Government account {} logged in", governmentDTO.getGovUsername());
        return JwtUtil.generateGovToken(government);
    }

    /**
     * Handles a HTTP PUT request for creating a subpoena on a user for a government entity.
     * @param govName the name of the government entity.
     * @param username the username of the user that is getting subpoenaed.
     * @return A Response indicating the outcome of the requested operation.
     */
    @PutMapping(value = "/{govName}/subpoena/{username}")
    public Subpoena createSubpoena(@PathVariable String govName, @PathVariable String username) {
        Government government = getGovernment(govName);
        if (government == null) {
            return null;
        }
        User user = getUser(username);
        if (user == null) {
            logger.error("No user account associated with name {} found", username);
            return null;
        }
        return governmentService.createSubpoena(government, user);
    }


    /**
     * Handles a HTTP DELETE request to remove a subpoena on a user for a government entity.
     * @param govName the name of the government entity.
     * @param username the username of the user of the subpoena.
     * @return A Response indicating the outcome of the requested operation.
     */
    @DeleteMapping(value = "/{govName}/unsubpoena/{username}")
    public Response deleteSubpoena(@PathVariable String govName, @PathVariable String username) {
        Government government = getGovernment(govName);
        if (government == null) {
            return null;
        }
        User user = getUser(username);
        if (user == null) {
            return null;
        }
        if (governmentService.deleteSubpoena(government, user)) {
            return Response.ok().build();
        }
        return Response.status(400, "Could not delete subpoena as it does not exist")
                .build();
    }

    /**
     * Handles a HTTP GET request to get all the subpoenas for a government entity.
     * @param govName the name of the government entity.
     * @return A Response indicating the outcome of the requested operation.
     */
    @GetMapping(value = "/{govName}/subpoenas")
    public List<Subpoena> getAllSubpoenasForGovernment(@PathVariable String govName) {
        Government government = getGovernment(govName);
        if (government == null) {
            return new ArrayList<>();
        }
        return governmentService.findAllSubpoenas(government);
    }

    /**
     * Handles a HTTP GET request to get the messages for a user that has been a subpoenaed if the subpoena exists.
     *
     * @param govname the name of the governemnt entity.
     * @param username the username of the user to get the messages for.
     * @return A Response indicating the outcome of the requested operation.
     */
    @GetMapping(value = "/conversation/{govname}/{username}")
    public List<Message> getMessagesForUser(@PathVariable String govname,
                                            @PathVariable String username) {
        if (governmentService.isSubpoenaedUser(governmentService.findByGovName(govname),
                userService.findUserByName(username).orElse(null))) {
            logger.info("Attempting to get messages for user {}", username);
            try {
                return messageService.findMessagesForReceivingUser(username, true);
            } catch (UserDoesNotExistException e) {
                logger.info("The recipient user could not be found.");
                return new ArrayList<>();
            }
        } else {
            logger.info("User {} is not subpoenaed.", username);
            return new ArrayList<>();
        }
    }

    /**
     * Helper method to get the user object given a username.
     * @param username the username of the user to get user object for.
     * @return an optional containing the user object if it exists.
     */
    private User getUser(String username) {
        Optional<User> optionalUser = userService.findUserByName(username);
        return optionalUser.orElse(null);
    }

    /**
     * Helper method to get the government entity given a government entity name.
     *
     * @param govName the name of the government entity.
     * @return the goverment object if it exists.
     */
    private Government getGovernment(String govName) {
        try {
            return governmentService.findByGovName(govName);
        } catch (Exception e) {
            logger.error("No government account associated with name {} found", govName);
            return null;
        }
    }
}
