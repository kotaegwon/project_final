package org.techtown.rc119.Login_Register;

import com.google.gson.annotations.SerializedName;

public class LoginData {
    @SerializedName("id")
    String id;

    @SerializedName("pw")
    String pw;

    public LoginData(String id, String pw) {
        this.id = id;
        this.pw = pw;
    }
}