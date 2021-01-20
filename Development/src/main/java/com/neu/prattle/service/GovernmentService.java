package com.neu.prattle.service;

import com.neu.prattle.model.Government;
import com.neu.prattle.model.Subpoena;
import com.neu.prattle.model.User;

import org.springframework.stereotype.Service;

import java.util.List;

/**
 * Acts as an interface between the data layer and the servlet controller level.
 *
 * The controller is responsible for interfacing with this instance
 * to perform all the CRUD operations on government objects.
 */
@Service
public interface GovernmentService {

  /**
   * Handle a login attempt from a government entity.
   * @param govUsername the government username.
   * @param govPassword the government password.
   * @return the associated government entity.
   */
  Government validateAccount(String govUsername, String govPassword);

  /**
   * Find a government entity by username.
   * @param govUsername the government entity username.
   * @return the associated government entity.
   */
  Government findByGovName(String govUsername);

  /**
   * Create a subpoena on a user for a government entity.
   * @param government the government entity.
   * @param user the user that is being subpoenaed.
   * @return the created subpoena object.
   */
  Subpoena createSubpoena(Government government, User user);

  /**
   * Delete a subpoena between a government entity and a user.
   * @param government the government entity.
   * @param user the user that has been subpoenaed.
   * @return true if deleted, otherwise false.
   */
  boolean deleteSubpoena(Government government, User user);

  /**
   * Fond all the subpoenas for a government entity.
   * @param government the government entity.
   * @return the associated subpoenas.
   */
  List<Subpoena> findAllSubpoenas(Government government);

  /**
   * Find out if a user is subpoenaed by a government entity.
   * @param government the government entity.
   * @param user the user.
   * @return true if the user has been subpoenaed by this government entity, otherwise false.
   */
  boolean isSubpoenaedUser(Government government, User user);
}
