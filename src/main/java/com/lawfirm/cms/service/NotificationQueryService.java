package com.lawfirm.cms.service;

import com.lawfirm.cms.model.Notification;
import com.lawfirm.cms.repository.NotificationRepository;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;

@Service
public class NotificationQueryService {

    private final NotificationRepository notificationRepository;

    public NotificationQueryService(NotificationRepository notificationRepository) {
        this.notificationRepository = notificationRepository;
    }

    public List<Notification> getNotifications(Long userId) {
        return notificationRepository.findByRecipientIdOrderByTimestampDesc(userId);
    }

    public int getUnreadCount(Long userId) {
        return notificationRepository.findByRecipientIdAndReadFalse(userId).size();
    }

    public void markAsRead(Long notificationId) {
        Optional<Notification> opt = notificationRepository.findById(notificationId);
        if (opt.isPresent()) {
            Notification notification = opt.get();
            notification.setRead(true);
            notificationRepository.save(notification);
        }
    }
}
