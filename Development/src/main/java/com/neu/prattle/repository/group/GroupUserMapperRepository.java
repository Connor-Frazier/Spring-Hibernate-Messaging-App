package com.neu.prattle.repository.group;


import com.neu.prattle.model.group.GroupUserCompositeKey;
import com.neu.prattle.model.group.GroupUserMapper;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;

/**
 * Repository interface for the group user mapping database table.
 */
@Repository
public interface GroupUserMapperRepository extends JpaRepository<GroupUserMapper, GroupUserCompositeKey> {

  /**
   * Get the group user mappings for a group.
   * @param groupId the id of the group to get the mappings for.
   * @return the associated list of mappings.
   */
  @Query("select gum from GroupUserMapper gum where gum.group.groupID= :groupId")
  List<GroupUserMapper> getMapsByGroupId(@Param("groupId") int groupId);
}
