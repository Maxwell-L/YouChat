package com.maxwell.youchat.model;

import java.sql.Timestamp;

public class Message {
    private Long id;
    private Long sendUserId;
    private Long receiveUserId;
    private Timestamp createTime;
    private String content;

    public Message(Long sendUserId, Long receiveUserId, Timestamp createTime, String content) {
        this.sendUserId = sendUserId;
        this.receiveUserId = receiveUserId;
        this.createTime = createTime;
        this.content = content;
    }

    public Long getId() {
        return id;
    }

    public Long getSendUserId() {
        return sendUserId;
    }

    public Long getReceiveUserId() {
        return receiveUserId;
    }

    public Timestamp getCreateTime() {
        return createTime;
    }

    public String getContent() {
        return content;
    }
}
