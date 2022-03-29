package org.techtown.rc119.activity;

import androidx.annotation.NonNull;
import androidx.appcompat.app.ActionBarDrawerToggle;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.core.text.HtmlCompat;
import androidx.drawerlayout.widget.DrawerLayout;
import androidx.fragment.app.Fragment;

import android.content.Intent;
import android.graphics.Bitmap;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Bundle;
import android.os.Environment;
import android.provider.MediaStore;
import android.text.Spanned;
import android.text.method.ScrollingMovementMethod;
import android.util.Log;
import android.view.KeyEvent;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.MotionEvent;
import android.view.View;
import android.webkit.WebChromeClient;
import android.webkit.WebView;
import android.widget.Button;
import android.widget.FrameLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.material.navigation.NavigationView;

import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.select.Elements;
import org.techtown.rc119.ImageList.ImageListActivity;
import org.techtown.rc119.Network.ApiService;
import org.techtown.rc119.Network.RetrofitClient;
import org.techtown.rc119.R;
import org.techtown.rc119.fragment.GoogleMapFragment;
import org.techtown.rc119.fragment.onBackPressedListener;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.List;

import okhttp3.ResponseBody;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;
import retrofit2.Retrofit;


public class ControlActivity extends AppCompatActivity {

    private DrawerLayout drawerLayout;
    private WebView webview;
    private FrameLayout frame_top;
    private NavigationView navigationView;
    private ActionBarDrawerToggle drawerToggle;
    private String date = dateName(System.currentTimeMillis());
    private Button btn_go, btn_right, btn_left, btn_back, btn_capture;
    private long lastTimeBackPressed;

    private String temp; //온도 저장 변수
    private String temp_url="http://192.168.0.254:8500/login";
    private TextView tv_temperature; //온도 출력 텍스트

    //private final String NodeUrl = "http://192.168.0.254:8500";//"http://104.198.3.107:3000";//"http://192.168.0.254:8500";

    private Retrofit retrofit;
    private ApiService service;

    private Boolean isBtnDown;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_control);

        //(Rtrofit 클래스)
        service = RetrofitClient.getClient().create(ApiService.class);

        //온도 텍스트뷰
        tv_temperature=(TextView)findViewById(R.id.tv_temperature);

        //액션바를 툴바로 대체하기기
        Toolbar toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        //프레임 레이아웃 웹뷰 프레그먼트 온도 값 보여줌
        frame_top = (FrameLayout) findViewById(R.id.frame_top);

        //캡쳐 버튼
        btn_capture = (Button) findViewById(R.id.btn_capture);

        //방향키
        btn_go = (Button) findViewById(R.id.btn_go);
        btn_right = (Button) findViewById(R.id.btn_right);
        btn_left = (Button) findViewById(R.id.btn_left);
        btn_back = (Button) findViewById(R.id.btn_back);

        //웹뷰 라즈베리카메라 서버연결
        webview = (WebView) findViewById(R.id.webView);

        webview.getSettings().setJavaScriptEnabled(true);
        webview.setWebChromeClient(new WebChromeClient());
        webview.getSettings().setUseWideViewPort(true);
        webview.getSettings().setLoadWithOverviewMode(true);
        webview.loadUrl(" http://192.168.0.207:8091/?action=stream");

        drawerLayout = findViewById(R.id.drawer_layout);
        navigationView = findViewById(R.id.sidenav);
        drawerToggle = new ActionBarDrawerToggle(this, drawerLayout, toolbar, R.string.app_name, R.string.app_name);
        drawerToggle.syncState(); //삼선메뉴만들기
        drawerLayout.addDrawerListener(drawerToggle);
        //네비게이션뷰의 메뉴아이콘의 색조제거
        navigationView.setItemIconTintList(null);
        navigationView.setNavigationItemSelectedListener(new NavigationView.OnNavigationItemSelectedListener() {
            @Override
            public boolean onNavigationItemSelected(@NonNull MenuItem item) {
                switch (item.getItemId()) {
                    case R.id.menu_image:
                        Toast.makeText(getApplicationContext(), "갤러리", Toast.LENGTH_SHORT).show();
                        Intent image = new Intent(Intent.ACTION_PICK);
                        image.setDataAndType(MediaStore.Images.Media.EXTERNAL_CONTENT_URI, "image/*");
                        startActivity(image);
                        break;
                    case R.id.menu_image_list:
                        startActivity(new Intent(getApplicationContext(), ImageListActivity.class));
                        break;
                    case R.id.menu_logout:
                        Toast.makeText(getApplicationContext(), "로그아웃", Toast.LENGTH_SHORT).show();
                        Intent logout=new Intent(ControlActivity.this, LogInActivity.class);
                        logout.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
                        startActivity(logout);
                        break;
                    case R.id.menu_map:
                        Toast.makeText(getApplicationContext(), "현재 위치",Toast.LENGTH_SHORT).show();
                        startActivity(new Intent(ControlActivity.this, GoogleMapActivity.class));
                        break;
                }
                return false;
            }
        });

        //메인 UI 이벤트
        View.OnClickListener onClickListener = new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                switch (view.getId()) {

                    //캡쳐 버튼
                    case R.id.btn_capture:
                        frame_top.buildDrawingCache();
                        Bitmap saveView = frame_top.getDrawingCache();
                        File dir = new File(Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_DCIM).toString() + "/Screenshots/" + date + ".png");
                        FileOutputStream fos;
                        try {
                            fos = new FileOutputStream(dir);
                            saveView.compress(Bitmap.CompressFormat.JPEG, 100, fos);
                            //이미지 저장후 스캐닝 -> 이미지 저장후 갤러리에 보이지 않던 문제 해결
                            sendBroadcast(new Intent(Intent.ACTION_MEDIA_SCANNER_SCAN_FILE, Uri.parse("file://" + dir)));
                            Toast.makeText(getApplicationContext(), "저장완료", Toast.LENGTH_SHORT).show();
                        } catch (Exception e) {
                            e.printStackTrace();
                            Toast.makeText(getApplicationContext(), "저장실패", Toast.LENGTH_SHORT).show();
                        }
                        break;
                }
            }
        };

        btn_capture.setOnClickListener(onClickListener);

        //방향키 터치 이벤트
        View.OnTouchListener touchListener = new View.OnTouchListener() {
            @Override
            public boolean onTouch(View v, MotionEvent event) {
                switch (v.getId()) {
                    case R.id.btn_go:
                        if (event.getAction() == MotionEvent.ACTION_DOWN) {
                            isBtnDown = true;
                            new Thread(new Runnable() {
                                @Override
                                public void run() {
                                    TouchDirection("go");
                                    Log.i("ko", "go");
                                }
                            }).start();
                        } else if (event.getAction() == MotionEvent.ACTION_UP) {
                            isBtnDown = false;
                            TouchDirection("stop");
                            Thread.currentThread().interrupted();
                            Log.i("ko", "stop");
                            Toast.makeText(ControlActivity.this, "정지", Toast.LENGTH_SHORT).show();
                        }
                        break;

                    case R.id.btn_right:
                        if (event.getAction() == MotionEvent.ACTION_DOWN) {
                            isBtnDown = true;
                            new Thread(new Runnable() {
                                @Override
                                public void run() {

                                    TouchDirection("right");
                                    Log.i("ko", "right");

                                }
                            }).start();
                        } else if (event.getAction() == MotionEvent.ACTION_UP) {
                            isBtnDown = false;
                            TouchDirection("stop");
                            Log.i("ko", "stop");
                            Toast.makeText(ControlActivity.this, "정지", Toast.LENGTH_SHORT).show();
                        }
                        break;

                    case R.id.btn_left:
                        if (event.getAction() == MotionEvent.ACTION_DOWN) {
                            isBtnDown = true;
                            new Thread(new Runnable() {
                                @Override
                                public void run() {

                                    TouchDirection("left");
                                    Log.i("ko","left");
                                }
                            }).start();
                        } else if (event.getAction() == MotionEvent.ACTION_UP) {
                            isBtnDown = false;
                            TouchDirection("stop");
                            Log.i("ko", "stop");
                            Toast.makeText(ControlActivity.this, "정지", Toast.LENGTH_SHORT).show();
                        }
                        break;
                    case R.id.btn_back:
                        if (event.getAction() == MotionEvent.ACTION_DOWN) {
                            isBtnDown = true;
                            new Thread(new Runnable() {
                                @Override
                                public void run() {
                                    TouchDirection("back");
                                    Log.i("ko","back");
                                }
                            }).start();
                        } else if (event.getAction() == MotionEvent.ACTION_UP) {
                            isBtnDown = false;
                            TouchDirection("stop");
                            Log.i("ko", "stop");
                            Toast.makeText(ControlActivity.this, "정지", Toast.LENGTH_SHORT).show();
                        }
                        break;
                }
                return false;
            }
        };
        btn_go.setOnTouchListener(touchListener);
        btn_right.setOnTouchListener(touchListener);
        btn_left.setOnTouchListener(touchListener);
        btn_back.setOnTouchListener(touchListener);
    }
    //툴바에 toolbar 인플레이트
    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        MenuInflater menuInflater = getMenuInflater();
        menuInflater.inflate(R.menu.toolbar_menu, menu);
        return true;
    }

//    @Override
//    public boolean onOptionsItemSelected(@NonNull MenuItem item) {
//        switch (item.getItemId()) {
//            case R.id.action_map:
//                GoogleMapFragment gmap = new GoogleMapFragment();
//                getSupportFragmentManager().beginTransaction()
//                        .replace(R.id.frame_top, gmap, "main")
//                        .commit();
//                return true;
//            default:
//                return super.onOptionsItemSelected(item);
//        }
//    }

    @Override
    public boolean onKeyDown(int keyCode, KeyEvent event) {
        if ((keyCode == KeyEvent.KEYCODE_BACK) && webview.canGoBack()) {
            webview.goBack();

            return true;
        }
        return super.onKeyDown(keyCode, event);
    }

    //이미지 파일 저장할때 날짜로 저장
    private String dateName(long dateTaken) {
        Date date = new Date(dateTaken);
        SimpleDateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd HH.mm.ss");
        return dateFormat.format(date);
    }


    private void TouchDirection(String data) {

        Call<ResponseBody> call_post = service.direction(data);
        //enqueue로 비동기 통신 실행
        call_post.enqueue(new Callback<ResponseBody>() {
            @Override
            //Callback 리스너 등록
            public void onResponse(Call<ResponseBody> call, Response<ResponseBody> response) {
                if (response.isSuccessful()) { //onResponse통신 성공시 Callback
                    try {
                        String result = response.body().string();
                        Log.i("ko", "result = " + result);
                        Toast.makeText(getApplicationContext(), result, Toast.LENGTH_SHORT).show();
                    } catch (IOException e) {
                        e.printStackTrace();
                    }
                } else {
                    //에러 코드 확인
                    Log.i("ko", "error = " + String.valueOf(response.code()));
                    Toast.makeText(getApplicationContext(), "error = " + String.valueOf(response.code()), Toast.LENGTH_SHORT).show();
                }
            }

            //통신 실패시 callback
            @Override
            public void onFailure(Call<ResponseBody> call, Throwable t) {
                Log.i("ko", "Fail");
                Toast.makeText(getApplicationContext(), "Response Fail", Toast.LENGTH_SHORT).show();
            }
        });
    }

    @Override
    public void onBackPressed() {
        List<Fragment> fragmentList = getSupportFragmentManager().getFragments();
        for (Fragment fragment : fragmentList) {
            if (fragment instanceof onBackPressedListener) {
                ((onBackPressedListener) fragment).onBackPressed();
                return;
            }
        }
        //두 번 클릭시 어플 종료
        if (System.currentTimeMillis() - lastTimeBackPressed < 1500) {
            finish();
            return;
        }
        lastTimeBackPressed = System.currentTimeMillis();
        Toast.makeText(this, "'뒤로' 버튼을 한 번 더 누르면 종료됩니다.", Toast.LENGTH_SHORT).show();
    }
}