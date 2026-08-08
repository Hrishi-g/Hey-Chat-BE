package com.app.chatApp.dto;

public class EmailOtpPayload {
    private String email;
    private String otp;
    private String name;
    private String type; // "login" or "signup"

    public EmailOtpPayload() {
    }

    public EmailOtpPayload(String email, String otp, String name, String type) {
        this.email = email;
        this.otp = otp;
        this.name = name;
        this.type = type;
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public String getOtp() {
        return otp;
    }

    public void setOtp(String otp) {
        this.otp = otp;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getType() {
        return type;
    }

    public void setType(String type) {
        this.type = type;
    }

    @Override
    public String toString() {
        return "EmailOtpPayload [email=" + email + ", otp=" + otp + ", name=" + name + ", type=" + type + "]";
    }
}
