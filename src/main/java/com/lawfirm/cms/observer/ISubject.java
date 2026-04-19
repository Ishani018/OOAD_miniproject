package com.lawfirm.cms.observer;

public interface ISubject {
    void subscribe(Long userId);
    void unsubscribe(Long userId);
    void notifyObservers(String message, Long caseId);
}
