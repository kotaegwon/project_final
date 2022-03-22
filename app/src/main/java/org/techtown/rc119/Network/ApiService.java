package org.techtown.rc119.Network;

import org.techtown.rc119.Login_Register.JoinData;
import org.techtown.rc119.Login_Register.JoinResponse;
import org.techtown.rc119.Login_Register.LoginData;
import org.techtown.rc119.Login_Register.LoginResponse;

import okhttp3.ResponseBody;
import retrofit2.Call;
import retrofit2.http.Body;
import retrofit2.http.DELETE;
import retrofit2.http.Field;
import retrofit2.http.FormUrlEncoded;
import retrofit2.http.GET;
import retrofit2.http.POST;
import retrofit2.http.PUT;
import retrofit2.http.Path;
import retrofit2.http.Query;

public interface ApiService {

    @GET("/retrofit/get")
    Call<ResponseBody> getFunc(@Query("data")String data);

    @FormUrlEncoded
    @POST("/retrofit/post")
    Call<ResponseBody> postFunc(@Field("data") String data);

    @FormUrlEncoded
    @PUT("routes/retrofit/put/{id}")
    Call<ResponseBody> putFunc(@Path("id") String id, @Field("data") String data);

    @DELETE("/retrofit/delete/{id}")
    Call<ResponseBody> deleteFunc(@Path("id") String id);

    //로그인
    @POST("/login")
    Call<LoginResponse> userLogin(@Body LoginData data);

    //회원가입
    @POST("/register")
    Call<JoinResponse> userJoin(@Body JoinData data);
/*
    @GET("/go")
    Call<ResponseBody> directGo();

    @GET("/left")
    Call<ResponseBody> directLeft();

    @GET("/right")
    Call<ResponseBody> directRight();

    @GET("/back")
    Call<ResponseBody> directBack();
*/
    @GET("/{direction}")
    Call<ResponseBody> direction(@Path("direction") String direction);
}
