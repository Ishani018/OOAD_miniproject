package com.lawfirm.cms.observer;

public interface IObserver {
    void update(String message, Long recipientId, Long caseId);
}
