package com.maxwell.youchat.model;

import java.sql.Timestamp;

public class Message {
    private Integer id;
    private Integer sendUserId;
    private Integer receiveUserId;
    private Timestamp createTime;
    private String content;

    public Message(Integer sendUserId, Integer receiveUserId, Timestamp createTime, String content) {
        this.sendUserId = sendUserId;
        this.receiveUserId = receiveUserId;
        this.createTime = createTime;
        this.content = content;
    }

    public Integer getId() {
        return id;
    }

    public Integer getSendUserId() {
        return sendUserId;
    }

    public Integer getReceiveUserId() {
        return receiveUserId;
    }

    public Timestamp getCreateTime() {
        return createTime;
    }

    public String getContent() {
        return content;
    }
}
