package org.techtown.rc119.Login_Register;

import com.google.gson.annotations.SerializedName;

public class JoinData {
    @SerializedName("username")
    private String username;

    @SerializedName("userid")
    private String userid;

    @SerializedName("userpassword")
    private String userpassword;

    @SerializedName("userphone")
    private String userphone;

    public JoinData(String username, String userid, String userpassword, String userphone) {
        this.username = username;
        this.userid=userid;
        this.userpassword = userpassword;
        this.userphone=userphone;
    }
}
