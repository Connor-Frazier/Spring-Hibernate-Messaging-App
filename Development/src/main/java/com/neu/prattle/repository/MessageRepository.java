package com.neu.prattle.repository;

import com.neu.prattle.model.Message;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;

/**
 * Repository interface for the message database table.
 */
@Repository
public interface MessageRepository extends JpaRepository<Message, Integer> {

  /**
   * Find the messages sent to a user by the user's id.
   * @param id the id of the user to find the messages for.
   * @return the associated messages.
   */
  List<Message> findByToUserId(int id);

  /**
   * Find a list of messages sent from one user to another.
   * @param fromUserId the sending user id.
   * @param toUserId the receiving user id.
   * @return the associated list of messages.
   */
  @Query("select m from Message m where m.fromUserId = :from_user_id "
          + "and m.toUserId = :to_user_id ")
  List<Message> findByFromUserIdAndToUserId(@Param("from_user_id") int fromUserId,
                                            @Param("to_user_id") int toUserId);

  /**
   * Find new unread messages for a receiving user from a sending user.
   * @param fromUserId the sending user id.
   * @param toUserId the recieving user id.
   * @return the associated list of messages.
   */
  @Query("select m from Message m where m.fromUserId = :from_user_id "
          + "and m.toUserId = :to_user_id "
          + "and m.messageStatus = com.neu.prattle.model.MessageStatus.DELIVERED")
  List<Message> findNewMessages(@Param("from_user_id") int fromUserId,
                                @Param("to_user_id") int toUserId);

  /**
   * Find unread messages for a receiving user.
   * @param toUserId the receiving user id.
   * @return the associated list of messages.
   */
  @Query(value = "select * from message join user on message.to_user_id = `user`.user_id " +
          "join message_type_details mtd on message.msg_id = mtd.msg_id " +
          "left join message_encryption me on message.msg_id = me.msg_id" +
          " where message.generation_time > `user`.last_log_out_time" +
          " and `user`.user_id = ?1 ", nativeQuery = true)
  List<Message> fetchUnreadMessages(@Param("to_user_id") int toUserId);

  /**
   * Find the subsequent messages from a message.
   * @param sourceMessageID the source message id.
   * @return the messages following the source message
   */
  List<Message> findAllBySourceMessageIdOrderByGeneratedTime(int sourceMessageID);
}
