package com.fhsh.daitda.deliverymanager.application.client;

import com.fhsh.daitda.deliverymanager.application.command.UserInfoCommand;

import java.util.UUID;

public interface UserClient {
    UserInfoCommand getUser(UUID userId);
}
