package org.techtown.rc119.Login_Register;

import com.google.gson.annotations.SerializedName;

public class LoginData {
    @SerializedName("userid")
    String userid;

    @SerializedName("userpassword")
    String userpassword;

    public LoginData(String userid, String userpassword) {
        this.userid = userid;
        this.userpassword = userpassword;
    }
}