package com.neu.prattle.repository;

import com.neu.prattle.model.Government;
import com.neu.prattle.model.Subpoena;
import com.neu.prattle.model.User;

import org.springframework.data.repository.CrudRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

/**
 * Repository interface for the subpoena database table.
 */
@Repository
public interface SubpoenaRepository extends CrudRepository<Subpoena, Integer> {

  /**
   * Delete a subpoena between a government entity and the subpoenaed user.
   * @param government the government entity that issued the subpoena.
   * @param user the user that has been subpoenaed.
   * @return the amount of deleted records.
   */
  long deleteByGovernmentAndUser(Government government, User user);

  /**
   * Find all the subpoena issued by a government entity.
   * @param government the government entity to get the subpoenas for.
   * @return the asscoiated list of subpoenas.
   */
  List<Subpoena> findAllByGovernment(Government government);
}
