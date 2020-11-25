package com.maxwell.youchat.entity;

import androidx.annotation.NonNull;

import org.greenrobot.greendao.annotation.Entity;
import org.greenrobot.greendao.annotation.Id;

import org.greenrobot.greendao.annotation.Generated;
import org.greenrobot.greendao.annotation.Index;

@Entity(nameInDb = "t_message")
public class ChatMessage {
    @Id
    private Long id;

    @NonNull
    private Long sendUserId;

    @NonNull
    @Index
    private Long receiveUserId;

    private Long groupId;

    @NonNull
    private Long createTime;

    @NonNull
    private String content;


    @Generated(hash = 1576009258)
    public ChatMessage(Long id, @NonNull Long sendUserId, @NonNull Long receiveUserId,
            Long groupId, @NonNull Long createTime, @NonNull String content) {
        this.id = id;
        this.sendUserId = sendUserId;
        this.receiveUserId = receiveUserId;
        this.groupId = groupId;
        this.createTime = createTime;
        this.content = content;
    }

    @Generated(hash = 2271208)
    public ChatMessage() {
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

    public Long getGroupId() {
        return groupId;
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

    public void setGroupId(Long groupId) {
        this.groupId = groupId;
    }

    public void setCreateTime(Long createTime) {
        this.createTime = createTime;
    }

    public void setContent(String content) {
        this.content = content;
    }
}
