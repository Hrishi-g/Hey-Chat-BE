package com.app.chatApp.dto;

public class OtpDto {
    private String mblNo;
    private String otp;

    public OtpDto() {
    }

    public OtpDto(String mblNo, String otp) {
        this.mblNo = mblNo;
        this.otp = otp;
    }

    public String getMblNo() {
        return mblNo;
    }

    public void setMblNo(String mblNo) {
        this.mblNo = mblNo;
    }

    public String getOtp() {
        return otp;
    }

    public void setOtp(String otp) {
        this.otp = otp;
    }

    @Override
    public String toString() {
        return "OtpDto [mblNo=" + mblNo + ", otp=" + otp + "]";
    }
}
