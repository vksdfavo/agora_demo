package com.example.newdemo;

import java.util.UUID;

public class AgoraChannelIdGenerator {
    public static String generateChannelId() {
        UUID uuid = UUID.randomUUID();
        return uuid.toString();
    }
}
