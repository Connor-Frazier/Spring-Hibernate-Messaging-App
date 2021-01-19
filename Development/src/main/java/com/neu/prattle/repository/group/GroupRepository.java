package com.neu.prattle.repository.group;

import com.neu.prattle.model.group.Group;

import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.CrudRepository;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

/**
 * Repository interface for the group database table.
 */
@Repository
public interface GroupRepository extends CrudRepository<Group, Integer> {

  /**
   * Find a group by the group name.
   * @param groupName the name of the group to find.
   * @return the associated group.
   */
  Optional<Group> findByGroupName(String groupName);

  /**
   * Find a group by its id.
   * @param id the id of the group to find.
   * @return the associated group.
   */
  Optional<Group> findByGroupID(int id);

  /**
   * Find the sub groups for a group.
   * @param groupId The parent group id to find the subgroups for.
   * @return the associated sub groups of the parent group.
   */
  @Query(value = "select * from `groups` where group_id in (select sub_group_id from `groups` g "
          + "join group_group_mapping ggm on g.group_id=ggm.parent_group_id where parent_group_id=?1);"
          , nativeQuery = true)
  List<Group> fetchSubGroups(@Param("group_id") int groupId);

  /**
   * Find the parent groups for a child group.
   * @param groupId the child group to find the parent groups for.
   * @return the associated parent groups of the child group.
   */
  @Query(value = "select * from `groups` where group_id in (select parent_group_id from `groups` g "
          + "join group_group_mapping ggm on g.group_id=ggm.parent_group_id where sub_group_id=?1);"
          , nativeQuery = true)
  List<Group> fetchParentGroups(@Param("group_id") int groupId);
}