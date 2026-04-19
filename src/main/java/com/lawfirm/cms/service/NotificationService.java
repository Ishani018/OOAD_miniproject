package com.lawfirm.cms.service;

import com.lawfirm.cms.model.Notification;
import com.lawfirm.cms.observer.ISubject;
import com.lawfirm.cms.repository.NotificationRepository;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

@Service
public class NotificationService implements ISubject {

    private final NotificationRepository notificationRepository;
    private final List<Long> subscribers = new ArrayList<>();

    public NotificationService(NotificationRepository notificationRepository) {
        this.notificationRepository = notificationRepository;
    }

    @Override
    public void subscribe(Long userId) {
        if (!subscribers.contains(userId)) {
            subscribers.add(userId);
        }
    }

    @Override
    public void unsubscribe(Long userId) {
        subscribers.remove(userId);
    }

    @Override
    public void notifyObservers(String message, Long caseId) {
        for (Long userId : subscribers) {
            Notification notification = new Notification();
            notification.setMessage(message);
            notification.setTimestamp(LocalDateTime.now());
            notification.setRead(false);
            notification.setRecipientId(userId);
            notification.setCaseId(caseId);
            notificationRepository.save(notification);
        }
    }
}
