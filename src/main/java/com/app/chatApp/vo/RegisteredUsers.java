package com.app.chatApp.vo;

import java.time.LocalDateTime;
import java.util.Collection;

import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.Table;

@Entity
@Table(name = "registered_users")
public class RegisteredUsers implements UserDetails {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    Long userId;
    String name;
    @Column(name = "mbl_no", unique = true)
    String mblNo;
    @Column(unique = true)
    String email;
    String pass;
    @Column(name = "img_url")
    String imgUrl;
    String gender;
    String dob;
    @Column(name = "created_date")
    LocalDateTime createdDate = LocalDateTime.now();

    public RegisteredUsers() {
    }

    public RegisteredUsers(String name, String mblNo, String email, String pass, String imgUrl, String gender,
            String dob) {
        this.name = name;
        this.mblNo = mblNo;
        this.email = email;
        this.pass = pass;
        this.imgUrl = imgUrl;
        this.gender = gender;
        this.dob = dob;
    }

    public Long getUserId() {
        return userId;
    }

    public void setUserId(Long userId) {
        this.userId = userId;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getMblNo() {
        return mblNo;
    }

    public void setMblNo(String mblNo) {
        this.mblNo = mblNo;
    }

    public String getPass() {
        return pass;
    }

    public void setPass(String pass) {
        this.pass = pass;
    }

    public String getImgUrl() {
        return imgUrl;
    }

    public void setImgUrl(String imgUrl) {
        this.imgUrl = imgUrl;
    }

    public String getGender() {
        return gender;
    }

    public void setGender(String gender) {
        this.gender = gender;
    }

    public String getDob() {
        return dob;
    }

    public void setDob(String dob) {
        this.dob = dob;
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    @Override
    public String toString() {
        return "registeredUsers [userId=" + userId + ", name=" + name + ", mblNo=" + mblNo + ", email=" + email
                + ", pass=" + pass
                + ", imgUrl=" + imgUrl
                + ", gender=" + gender + ", dob=" + dob + ", createdDate=" + createdDate + "]";
    }

    @Override
    public Collection<? extends GrantedAuthority> getAuthorities() {
        return java.util.List.of();
    }

    @Override
    public String getPassword() {
        return this.pass;
    }

    @Override
    public String getUsername() {
        return this.mblNo;
    }
}
