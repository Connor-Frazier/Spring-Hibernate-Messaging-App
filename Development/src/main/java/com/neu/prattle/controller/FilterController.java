package com.neu.prattle.controller;


import com.neu.prattle.exceptions.UserDoesNotExistException;
import com.neu.prattle.model.Filter;
import com.neu.prattle.service.FilterService;
import com.neu.prattle.service.UserService;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

import javax.ws.rs.core.Response;

/**
 * A REST controller for handling CRUD operations on Filter objects.
 */
@RestController
@RequestMapping(path = "/rest/filter")
@CrossOrigin(origins = {"http://com.northeastern.cs5500.team1.s3-website.us-east-2.amazonaws.com",
        "http://localhost:3000"})
public class FilterController {

  private Logger logger = LoggerFactory.getLogger(FilterController.class);
  private FilterService filterService;
  private UserService userService;

  @Autowired
  public void setFilterService(FilterService filterService) {
    this.filterService = filterService;
  }

  @Autowired
  public void setUserService(UserService userService) {
    this.userService = userService;
  }

  /**
   * Handles a HTTP PUT request add a filter for a username.
   *
   * @param filterText the content to filter to add.
   * @param username the username of the user to add the filer for.
   * @return A Response indicating the outcome of the requested operation.
   */
  @PutMapping(value = "/add/{filterText}/{username}")
  public Response addFilter(@PathVariable String filterText, @PathVariable String username) {
    try {
      filterService.addFilter(filterText, username);
    } catch (UserDoesNotExistException e) {
      logger.error("Filter cannot be added as the user does not exist");
      return Response.status(400, e.getMessage()).build();
    }
    logger.info("Filter added");
    return Response.ok().build();
  }

  /**
   * Handles a HTTP DELETE request for removing a filter for a user.
   *
   * @param filterText the content of the filter to remove.
   * @param username the username of the usr to remove the filter for.
   * @return A Response indicating the outcome of the requested operation.
   */
  @DeleteMapping(value = "/remove/{filterText}/{username}")
  public Response removeFilter(@PathVariable String filterText, @PathVariable String username) {
    try {
      filterService.removeFilter(filterText, username);
    } catch (UserDoesNotExistException e) {
      logger.error("Filter cannot be removed as the user does not exist");
      return Response.status(400, e.getMessage()).build();
    }
    logger.info("Filter removed");
    return Response.ok().build();
  }

  /**
   * Handles a HTTP GET request for getting all the filters for a username.
   *
   * @param username the username of the user to get the filters for.
   * @return A Response indicating the outcome of the requested operation.
   */
  @GetMapping(value = "/get/{username}")
  public List<Filter> getFiltersByUsername(@PathVariable String username) {
    return filterService.getFiltersForUser(userService.findUserByName(username).orElse(null));
  }
}
