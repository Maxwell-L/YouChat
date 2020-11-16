package com.maxwell.youchat.entity;

import androidx.annotation.NonNull;

import org.greenrobot.greendao.annotation.Entity;
import org.greenrobot.greendao.annotation.Id;

import org.greenrobot.greendao.annotation.Generated;

@Entity(nameInDb = "t_message")
public class Message {
    @Id
    private Long id;

    @NonNull
    private Long sendUserId;

    @NonNull
    private Long receiveUserId;

    @NonNull
    private Long createTime;

    @NonNull
    private String content;

    @Generated(hash = 1234143995)
    public Message(Long id, @NonNull Long sendUserId, @NonNull Long receiveUserId,
            @NonNull Long createTime, @NonNull String content) {
        this.id = id;
        this.sendUserId = sendUserId;
        this.receiveUserId = receiveUserId;
        this.createTime = createTime;
        this.content = content;
    }

    @Generated(hash = 637306882)
    public Message() {
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

    public Long getCreateTime() {
        return createTime;
    }

    public String getContent() {
        return content;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public void setSendUserId(Long sendUserId) {
        this.sendUserId = sendUserId;
    }

    public void setReceiveUserId(Long receiveUserId) {
        this.receiveUserId = receiveUserId;
    }

    public void setCreateTime(Long createTime) {
        this.createTime = createTime;
    }

    public void setContent(String content) {
        this.content = content;
    }
}
