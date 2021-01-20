package com.neu.prattle.service;

import com.neu.prattle.model.HashTag;
import com.neu.prattle.model.Message;

import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;

/**
 * Acts as an interface between the data layer and servlet controller level.
 *
 * The controller is responsible for interfacing with this instance
 * to perform all the CRUD operations on hashtag objects.
 */
@Service
public interface HashTagService {

  /**
   * Returns a hashtag if it exists.
   * @param hashTag the hashtag text.
   * @return the associated hashtag if it exists.
   */
  Optional<HashTag> getHashTag(String hashTag);

  /**
   * Creates and saves a new hashtag if it does not already exist.
   * @param hashTag the hashtag content.
   * @param message the message that the hashtag was in.
   * @return the resulting hashtag.
   */
  HashTag createHashTag(String hashTag, Message message);

  /**
   * Returns a list of the top hashtags.
   * @return the associated hashtags.
   */
  List<HashTag> getTopHashTags();
}
