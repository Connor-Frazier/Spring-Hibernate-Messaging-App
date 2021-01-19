package com.neu.prattle.repository;

import com.neu.prattle.model.UserIPMapper;

import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.CrudRepository;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import javax.transaction.Transactional;

/**
 * Repository interface for the user ip address mapping database table.
 */
@Repository
public interface UserIPMapperRepository extends CrudRepository<UserIPMapper, Integer> {

  /**
   * Set the ip address for a user.
   * @param userId the user id to set the ip address for.
   * @param ipAddress the ip address of the user that is being set.
   */
  @Transactional
  @Modifying
  @Query("update UserIPMapper uip"
          + " set uip.ipAddress = :ipAddress WHERE uip.userID = :user_id")
  void setIpAddress(@Param("user_id") int userId, @Param("ipAddress") String ipAddress);
}
