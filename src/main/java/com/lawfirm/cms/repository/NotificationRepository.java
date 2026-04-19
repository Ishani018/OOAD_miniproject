package com.lawfirm.cms.repository;

import com.lawfirm.cms.model.Notification;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface NotificationRepository extends JpaRepository<Notification, Long> {
    List<Notification> findByRecipientIdOrderByTimestampDesc(Long recipientId);
    List<Notification> findByRecipientIdAndReadFalse(Long recipientId);
}
