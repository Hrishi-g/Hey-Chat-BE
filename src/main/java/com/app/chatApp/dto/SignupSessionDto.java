package com.app.chatApp.dto;

import java.io.Serializable;

public class SignupSessionDto implements Serializable {
    private static final long serialVersionUID = 1L;

    private String otp;
    private SignupDto signupData;

    public SignupSessionDto() {
    }

    public SignupSessionDto(String otp, SignupDto signupData) {
        this.otp = otp;
        this.signupData = signupData;
    }

    public String getOtp() {
        return otp;
    }

    public void setOtp(String otp) {
        this.otp = otp;
    }

    public SignupDto getSignupData() {
        return signupData;
    }

    public void setSignupData(SignupDto signupData) {
        this.signupData = signupData;
    }
}
