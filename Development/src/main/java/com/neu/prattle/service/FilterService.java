package com.neu.prattle.service;

import com.neu.prattle.exceptions.UserDoesNotExistException;
import com.neu.prattle.model.Filter;
import com.neu.prattle.model.User;

import org.springframework.stereotype.Service;

import java.util.List;

/**
 * Acts as an interface between the data layer and servlet controller level.
 *
 * The controller is responsible for interfacing with this instance
 * to perform all the CRUD operations on filter objects.
 */
@Service
public interface FilterService {

  /**
   * Save a filter for a given user.
   * @param filterText the content for the filter.
   * @param username the username of the user to add the filter for.
   * @throws UserDoesNotExistException when there is no user for the username given.
   */
  void addFilter(String filterText, String username) throws UserDoesNotExistException;

  /**
   * Remove a filter for a given user.
   * @param filterText the content of the filter to remove.
   * @param username the username of the user that the filter belongs to.
   * @throws UserDoesNotExistException when there is no user for the username given.
   */
  void removeFilter(String filterText, String username) throws UserDoesNotExistException;

  /**
   * Returns all of the filters for a given user.
   * @param user the user to get the filters for.
   * @return the associated filters.
   */
  List<Filter> getFiltersForUser(User user);
}
