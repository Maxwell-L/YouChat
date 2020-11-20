package com.maxwell.youchat.entity;

import androidx.annotation.NonNull;

import org.greenrobot.greendao.annotation.Entity;
import org.greenrobot.greendao.annotation.Id;
import org.greenrobot.greendao.annotation.Generated;

@Entity(nameInDb = "t_friend")
public class Friend {
    @Id
    private Long id;

    @NonNull
    private String username;

    @NonNull
    private Integer sex; // 0-male, 1-female

    private String icon;
    
    private Long lastMessageId;

    @Generated(hash = 1095567106)
    public Friend(Long id, @NonNull String username, @NonNull Integer sex,
            String icon, Long lastMessageId) {
        this.id = id;
        this.username = username;
        this.sex = sex;
        this.icon = icon;
        this.lastMessageId = lastMessageId;
    }

    @Generated(hash = 287143722)
    public Friend() {
    }

    public Long getId() {
        return this.id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getUsername() {
        return this.username;
    }

    public void setUsername(String username) {
        this.username = username;
    }

    public Integer getSex() {
        return this.sex;
    }

    public void setSex(Integer sex) {
        this.sex = sex;
    }

    public String getIcon() {
        return this.icon;
    }

    public void setIcon(String icon) {
        this.icon = icon;
    }

    public Long getLastMessageId() {
        return this.lastMessageId;
    }

    public void setLastMessageId(Long lastMessageId) {
        this.lastMessageId = lastMessageId;
    }
}
