package com.restaurant.creditmanagement.service;

import org.springframework.context.ApplicationEvent;

public class RedisMessageEvent extends ApplicationEvent {

    private final String channel;
    private final String message;

    public RedisMessageEvent(Object source, String channel, String message) {
        super(source);
        this.channel = channel;
        this.message = message;
    }

    public String getChannel() {
        return channel;
    }

    public String getMessage() {
        return message;
    }
}