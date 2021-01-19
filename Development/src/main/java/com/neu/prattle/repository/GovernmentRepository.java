package com.neu.prattle.repository;

import com.neu.prattle.model.Government;

import org.springframework.data.repository.CrudRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

/**
 * Repository interface for the government database table.
 */
@Repository
public interface GovernmentRepository extends CrudRepository<Government, Integer> {

  /**
   * Find the government entity(user) by username.
   * @param username the username of the government entity.
   * @return the associated government entity if it exists.
   */
  Optional<Government> findByGovUsername(String username);
}
