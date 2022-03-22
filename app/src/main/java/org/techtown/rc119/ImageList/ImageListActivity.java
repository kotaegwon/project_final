package org.techtown.rc119.ImageList;

import android.content.Intent;
import android.database.Cursor;
import android.net.Uri;
import android.os.Bundle;
import android.os.Environment;
import android.provider.MediaStore;
import android.text.TextUtils;
import android.util.Log;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.ActionBarDrawerToggle;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.drawerlayout.widget.DrawerLayout;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.google.android.material.navigation.NavigationView;
import com.yanzhenjie.permission.Action;
import com.yanzhenjie.permission.AndPermission;
import com.yanzhenjie.permission.runtime.Permission;

import org.techtown.rc119.R;
import org.techtown.rc119.activity.LogInActivity;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

public class ImageListActivity extends AppCompatActivity {
    private TextView textView;
    private RecyclerView recyclerView;
    private NavigationView navigationView;
    private ActionBarDrawerToggle drawerToggle;
    private PictureAdapter adapter;
    private String date = dateName(System.currentTimeMillis());
    private DrawerLayout drawerLayout;

    SimpleDateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd HH:mm.ss");
    int pictureCount = 0; //

    ArrayList<PictureInfo> pictures;

    private String dateName(long dateTaken) {
        Date date = new Date(dateTaken);
        SimpleDateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd HH.mm.ss");
        return dateFormat.format(date);
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.list_activity);

        drawerLayout = findViewById(R.id.drawer_layout);
        navigationView = findViewById(R.id.sidenav);
        Toolbar toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        drawerToggle = new ActionBarDrawerToggle(this, drawerLayout, toolbar, R.string.app_name, R.string.app_name);
        drawerToggle.syncState(); //삼선메뉴만들기
        drawerLayout.addDrawerListener(drawerToggle);
        //네비게이션뷰의 메뉴아이콘의 색조제거
        navigationView.setItemIconTintList(null);


        navigationView.setNavigationItemSelectedListener(new NavigationView.OnNavigationItemSelectedListener() {
            @Override
            public boolean onNavigationItemSelected(@NonNull MenuItem item) {
                switch (item.getItemId()){
                    case R.id.menu_image:
                        Toast.makeText(getApplicationContext(), "갤러리", Toast.LENGTH_SHORT).show();
                        Intent image=new Intent(Intent.ACTION_PICK);
                        image.setDataAndType(MediaStore.Images.Media.EXTERNAL_CONTENT_URI,"image/*");
                        startActivity(image);
                        break;
                    case R.id.menu_image_list:
                        startActivity(new Intent(getApplicationContext(), ImageListActivity.class));
                    case R.id.menu_logout:
                        Toast.makeText(getApplicationContext(), "로그아웃", Toast.LENGTH_SHORT).show();
                        startActivity(new Intent(ImageListActivity.this, LogInActivity.class));
                        break;
                }
                return false;
            }
        });

        textView = findViewById(R.id.textView);

        recyclerView = findViewById(R.id.recyclerView);

        LinearLayoutManager layoutManager = new LinearLayoutManager(this);
        recyclerView.setLayoutManager(layoutManager);

        adapter = new PictureAdapter();

        recyclerView.setAdapter(adapter);

        adapter.setOnItemClickListener(new OnPictureItemClickListener() {
            @Override
            public void onItemClick(PictureAdapter.ViewHolder holder, View view, int position) {
                PictureInfo item = adapter.getItem(position);
                        Intent shareIntent=new Intent(Intent.ACTION_SEND); //share팝업 나오게 하기
                        shareIntent.setType("image/jpg");
                        Uri uri=Uri.parse("file://"+ Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_DCIM).toString()+"/Screenshots/"+date+".jpg");
                        shareIntent.putExtra(Intent.EXTRA_STREAM,uri); //이미지 전송
                        startActivity(Intent.createChooser(shareIntent,"Share 팝업"));
                Toast.makeText(getApplicationContext(), "아이템 선택됨 : " + item.getDisplayName(), Toast.LENGTH_LONG).show();
            }
        });

        Button button = findViewById(R.id.button);
        button.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                ArrayList<PictureInfo> result = queryAllPictures();
                adapter.setItems(result);
                adapter.notifyDataSetChanged();
            }
        });

        ArrayList<PictureInfo> result = queryAllPictures();
        adapter.setItems(result);
        adapter.notifyDataSetChanged();

        AndPermission.with(this)
                .runtime()
                .permission(Permission.READ_EXTERNAL_STORAGE,Permission.WRITE_EXTERNAL_STORAGE)
                .onGranted(new Action<List<String>>() {
                    @Override
                    public void onAction(List<String> permissions) {
                        //showToast("허용된 권한 갯수 : " + permissions.size());
                    }
                })
                .onDenied(new Action<List<String>>() {
                    @Override
                    public void onAction(List<String> permissions) {
                        //showToast("거부된 권한 갯수 : " + permissions.size());
                    }
                })
                .start();

    }

    public void showToast(String message) {
        Toast.makeText(this, message, Toast.LENGTH_LONG).show();
    }



    private ArrayList<PictureInfo> queryAllPictures() {
        ArrayList<PictureInfo> result = new ArrayList<>();
        Uri uri = MediaStore.Images.Media.EXTERNAL_CONTENT_URI;
        String[] projection = { MediaStore.MediaColumns.DATA, MediaStore.MediaColumns.DISPLAY_NAME, MediaStore.MediaColumns.DATE_ADDED };

        Cursor cursor = getContentResolver().query(uri, projection, null, null, MediaStore.MediaColumns.DATE_ADDED + " desc");
        int columnDataIndex = cursor.getColumnIndexOrThrow(MediaStore.MediaColumns.DATA);
        int columnNameIndex = cursor.getColumnIndexOrThrow(MediaStore.MediaColumns.DISPLAY_NAME);
        int columnDateIndex = cursor.getColumnIndexOrThrow(MediaStore.MediaColumns.DATE_ADDED);

        pictureCount = 0;
        while (cursor.moveToNext()) {
            String path = cursor.getString(columnDataIndex);
            String displayName = cursor.getString(columnNameIndex);
            String outDate = cursor.getString(columnDateIndex);
            String addedDate = dateFormat.format(new Date(new Long(outDate).longValue() * 1000L));

            if (!TextUtils.isEmpty(path)) {
                PictureInfo info = new PictureInfo(path, displayName, addedDate);
                result.add(info);
            }

            pictureCount++;
        }

        textView.setText(pictureCount + " 개");
        Log.i("ko", "Picture count : " + pictureCount);

        for (PictureInfo info : result) {
            Log.i("ko", info.toString());
        }

        return result;
    }

}
