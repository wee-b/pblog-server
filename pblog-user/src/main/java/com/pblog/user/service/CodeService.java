package com.pblog.user.service;

public interface CodeService {

    boolean verifyEmailUser(String email);

    String verifyEmailCode(String receiveEmail);

    void sendEmailCode(String email);
}
