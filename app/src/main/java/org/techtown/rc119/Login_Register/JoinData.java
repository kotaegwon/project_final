package org.techtown.rc119.Login_Register;

import com.google.gson.annotations.SerializedName;

public class JoinData {
    @SerializedName("name")
    private String name;

    @SerializedName("id")
    private String id;

    @SerializedName("pw")
    private String pwd;

    @SerializedName("phonenumber")
    private String phonenumber;

    public JoinData(String name, String id, String pw, String phonenumber) {
        this.name = name;
        this.id=id;
        this.pwd = pw;
        this.phonenumber=phonenumber;
    }
}
