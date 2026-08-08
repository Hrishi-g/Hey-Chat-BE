package com.app.chatApp.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;

public class SignupDto {

    @NotBlank(message = "Name is required")
    private String name;
    @NotBlank(message = "Mobile number is required")
    private String mblNo;
    @Size(min = 8, message = "Password must be at least 8 characters")
    private String pass;
    @NotBlank(message = "Confirm password is required")
    private String confirmPass;
    private String imgUrl;
    private String gender;
    private String dob;
    private String email;

    public SignupDto() {
    }

    public SignupDto(String name, String mblNo, String pass, String confirmPass, String imgUrl, String gender,
            String dob, String email) {
        this.name = name;
        this.mblNo = mblNo;
        this.pass = pass;
        this.confirmPass = confirmPass;
        this.imgUrl = imgUrl;
        this.gender = gender;
        this.dob = dob;
        this.email = email;
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

    public String getConfirmPass() {
        return confirmPass;
    }

    public void setConfirmPass(String confirmPass) {
        this.confirmPass = confirmPass;
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
        return "SignupDto [name=" + name + ", mblNo=" + mblNo + ", pass=" + pass + ", confirmPass=" + confirmPass
                + ", imgUrl=" + imgUrl + ", gender=" + gender + ", dob=" + dob + "]";
    }

}
