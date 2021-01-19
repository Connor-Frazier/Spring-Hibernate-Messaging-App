package com.neu.prattle.repository;

import com.neu.prattle.model.Filter;

import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.CrudRepository;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

/**
 * Repository interface for the filter database table.
 */
@Repository
public interface FilterRepository extends CrudRepository<Filter, Integer> {

  /**
   * Find the filter by the content the filter is looking for.
   * @param filterString the content to filter for this filter.
   * @return the associated filter object.
   */
  Optional<Filter> findByFilterString(String filterString);

  /**
   * Find all filters for a user by the user's id.
   * @param userId the id of the user.
   * @return the list of the user's filter.
   */
  @Query(value = "select f.* from filters f join user_filter_map ufm on f.filter_id = ufm"
          + ".filter_id where user_id = ?1", nativeQuery = true)
  List<Filter> findFiltersByUserId(@Param("userId") int userId);

}
