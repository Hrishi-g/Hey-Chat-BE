package com.app.chatApp.dto;

import java.util.Collection;
import java.util.List;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;

public class SecurityContextDto implements UserDetails {
    private Long userId;
    private String mblNo;

    public SecurityContextDto() {
    }

    public SecurityContextDto(Long userId, String mblNo) {
        this.userId = userId;
        this.mblNo = mblNo;
    }

    public Long getUserId() {
        return userId;
    }

    public void setUserId(Long userId) {
        this.userId = userId;
    }

    public String getMblNo() {
        return mblNo;
    }

    public void setMblNo(String mblNo) {
        this.mblNo = mblNo;
    }

    @Override
    public Collection<? extends GrantedAuthority> getAuthorities() {
        return List.of();
    }

    @Override
    public String getPassword() {
        return null;
    }

    @Override
    public String getUsername() {
        return mblNo;
    }

    @Override
    public String toString() {
        return "SecurityContextDto [userId=" + userId + ", mblNo=" + mblNo + "]";
    }
}
