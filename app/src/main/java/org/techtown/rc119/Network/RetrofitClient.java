package org.techtown.rc119.Network;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;

import java.util.concurrent.TimeUnit;

import okhttp3.OkHttpClient;
import okhttp3.logging.HttpLoggingInterceptor;
import retrofit2.Retrofit;
import retrofit2.converter.gson.GsonConverterFactory;
import retrofit2.converter.scalars.ScalarsConverterFactory;

public class RetrofitClient {

    private final static String BASE_URL = "http://192.168.0.254:8500";//http://104.198.3.107:8888";//"http://192.168.0.254:8600";
    private static Retrofit retrofit = null;

    private RetrofitClient() {
    }
    public static Retrofit getClient() {
        Gson gson=new GsonBuilder()
                .setLenient().create();
        if (retrofit == null) {
            retrofit=new Retrofit.Builder()
                    .baseUrl(BASE_URL)
                    .addConverterFactory(ScalarsConverterFactory.create())
                    .addConverterFactory(GsonConverterFactory.create(gson))
                    //.addConverterFactory(GsonConverterFactory.create())
                    .build();
        }
        return retrofit;
    }
}