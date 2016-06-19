package com.example;

import java.util.Map;

public class MessageListenerImpl implements MessageListener {

    @Override
    public void handleMessage(Map msg) {
        String name = (String) msg.get("name");
        Long id = (Long) msg.get("id");
        Map headers = (Map) msg.get("requestHeader");
        Map body = (Map ) msg.get("requestBody");
    }
}
