package com.example.user.androidhive;

import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.widget.ImageView;

import com.bumptech.glide.Glide;

import jp.wasabeef.glide.transformations.CropCircleTransformation;

public class Test_Activity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_test_);


        ImageView imageView2 = (ImageView)findViewById(R.id.imageView2);

//        Intent intent = getIntent();
//        String name = intent.getStringExtra("BMP"); // 파일 이름을 가져온다.
//        Log.e("전달받은 파일이름 = ", name);
//
//        String ex_storage = Environment.getExternalStorageDirectory().getAbsolutePath();
//        String foler_name = "/"+"Pictures"+"/";
//        String string_path = ex_storage+foler_name;
//
//        File imgFile = new  File(string_path + name + ".jpg");  // 전제주소 + 파일이름.jpg => 파일화
//        Log.e("이미지파일 = ", String.valueOf(imgFile));
//
//        if(imgFile.exists()){   // 파일이 존재하면
//            Log.e("이미지파일 존재함", "");
//            Bitmap myBitmap = BitmapFactory.decodeFile(imgFile.getAbsolutePath()); // 파일을 비트맵 이미지화 하여
//            imageView2.setImageBitmap(myBitmap);    // 이미지 뷰에 표시한다.
//        }


        String img = "ex_save_820714";

        Glide.with(imageView2.getContext())
                .load("http://49.247.208.191/AndroidHive/Moim_default_IMG/"+img+".jpg")
                .placeholder(R.drawable.ic_account_circle)
                .centerCrop()
                .crossFade()
                .bitmapTransform(new CropCircleTransformation(imageView2.getContext()))   // 원형크롭 라이브러리 사용함 = compile 'jp.wasabeef:glide-transformations:2.0.1'
                .override(100,100)  // 리소스의 이미지를 강제적으로 작게 줄여서 보여주려고 할대 사용
                .into(imageView2);


    }
}




//  private DrawerLayout mDrawerLayout;


//    // 여기서 부터 모두 복사해야 함.
//    // 버튼 생성 과 아이디 연결
//    Button home_button = (Button) findViewById(R.id.home_button);
//
//// 홈버튼 클릭 리스너
//        home_button.setOnClickListener(new Button.OnClickListener() {
//@Override
//public void onClick(View view) {
//        Intent intent = new Intent(
//        getApplicationContext(), // 현재 화면의 제어권자
//        MainActivity.class); // 다음 넘어갈 클래스 지정
//        startActivity(intent); // 다음 화면으로 넘어간다
//
//        finish();// 현재 액티비티 종료
//        }
//        });
//
//
//
//
//        // 툴바 객체 생성
//        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
//        setSupportActionBar(toolbar);
//        ActionBar actionBar = getSupportActionBar();
//        actionBar.setHomeAsUpIndicator(R.drawable.ic_menu); // 툴바 왼쪽 세줄 그림 설정
//        actionBar.setDisplayHomeAsUpEnabled(true);
//
//        // 텍스트 컬러 설정 = 투명하게 만듬 그래서 타이틀 없애버림 = 편법
//        toolbar.setTitleTextColor(Color.TRANSPARENT);
//        toolbar.setSubtitleTextColor(Color.TRANSPARENT);
//
//
//        // DrawerLayout 아이디 설정
//        mDrawerLayout = (DrawerLayout) findViewById(R.id.drawer_layout);    // 메인 xml 의 최상단 drawer_layout 레이아웃
//        // navigationView id 설정
//        NavigationView navigationView = (NavigationView) findViewById(R.id.navigation_view);
//
//        // 헤더뷰 객체 생성
//        View headerview = navigationView.getHeaderView(0);
//        // 헤더의 텍스트뷰 아이디 설정
//        TextView profilename = (TextView) headerview.findViewById(R.id.text_email);
////        profilename.setText("헤더파일 수정중...");
//
//        // 헤더뷰 레이아웃 아이디 설정
//        LinearLayout header_user_info = (LinearLayout) headerview.findViewById(R.id.user_info);
//        // 헤더뷰 레이아웃 클릭 리스너
//        header_user_info.setOnClickListener(new View.OnClickListener() {
//@Override
//public void onClick(View v) {
//        Toast.makeText(MainActivity.this, "header_user_info clicked", Toast.LENGTH_SHORT).show();
//        Intent intent = new Intent(
//        getApplicationContext(), // 현재 화면의 제어권자
//        LoginActivity.class); // 다음 넘어갈 클래스 지정
//        startActivity(intent); // 다음 화면으로 넘어간다
//
//        mDrawerLayout.closeDrawers();   // 서랍 닫기
//        }
//        });
//        // 헤더뷰 이미지 버튼 아아디 설정
//        ImageButton header_userimage = (ImageButton) headerview.findViewById(R.id.user_image);
//        // 헤더뷰 이미지 버튼 클릭 리스너
//        header_userimage.setOnClickListener(new View.OnClickListener() {
//@Override
//public void onClick(View v) {
//        Toast.makeText(MainActivity.this, "header_userimage clicked", Toast.LENGTH_SHORT).show();
//        Intent intent = new Intent(
//        getApplicationContext(), // 현재 화면의 제어권자
//        LoginActivity.class); // 다음 넘어갈 클래스 지정
//        startActivity(intent); // 다음 화면으로 넘어간다
//        mDrawerLayout.closeDrawers();   // 서랍 닫기
//        }
//        });
//
//
//
//
//        navigationView.setNavigationItemSelectedListener(new NavigationView.OnNavigationItemSelectedListener() {    // 네비게이션 아이템 클릭 리스너
//@Override
//public boolean onNavigationItemSelected(MenuItem menuItem) {        // 네비게이션 아이템 선택시에
//        menuItem.setChecked(true);
//        mDrawerLayout.closeDrawers();
//
//        int id = menuItem.getItemId();
//        switch (id) {
//        case R.id.navigation_item_attachment:
//        Toast.makeText(MainActivity.this, menuItem.getTitle(), Toast.LENGTH_LONG).show();
//        break;
//
//        case R.id.navigation_item_images:
//        Toast.makeText(MainActivity.this, menuItem.getTitle(), Toast.LENGTH_LONG).show();
//        break;
//
//        case R.id.navigation_item_location:
//        Toast.makeText(MainActivity.this, menuItem.getTitle(), Toast.LENGTH_LONG).show();
//        break;
//
//        case R.id.nav_sub_menu_item01:
//        Toast.makeText(MainActivity.this, menuItem.getTitle(), Toast.LENGTH_LONG).show();
//        break;
//
//        case R.id.nav_sub_menu_item02:
//        Toast.makeText(MainActivity.this, menuItem.getTitle(), Toast.LENGTH_LONG).show();
//        Intent intent = new Intent(
//        getApplicationContext(), // 현재 화면의 제어권자
//        Main_moim_Activity.class); // 다음 넘어갈 클래스 지정
//        startActivity(intent); // 다음 화면으로 넘어간다
//        break;
//        }
//        return true;
//        }
//        });
//// 복사 끝



//    // 복사 시작
//    // 툴바의 오른쪽 옵션 메뉴 생성
//    @Override
//    public boolean onCreateOptionsMenu(Menu menu) {
//        getMenuInflater().inflate(R.menu.menu, menu);
//        return true;
//    }
//
//    // 옵션메뉴 들중 선택했을때 하는 행동들 설정
//    @Override
//    public boolean onOptionsItemSelected(MenuItem item) {
//        // Handle action bar item clicks here. The action bar will
//        // automatically handle clicks on the Home/Up button, so long
//        // as you specify a parent activity in AndroidManifest.xml.
//        int id = item.getItemId();
//
//        switch (id) {
//            case android.R.id.home:
//                mDrawerLayout.openDrawer(GravityCompat.START);
//                return true;
//            case R.id.action_settings:
//                return true;
//        }
//
//        return super.onOptionsItemSelected(item);
//    }
//// 복사 끝


