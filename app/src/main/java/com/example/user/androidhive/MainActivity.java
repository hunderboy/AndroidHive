package com.example.user.androidhive;

import android.app.Activity;
import android.app.ActivityManager;
import android.app.ProgressDialog;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Color;
import android.os.AsyncTask;
import android.os.Build;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.support.annotation.RequiresApi;
import android.support.design.widget.NavigationView;
import android.support.v4.app.ActivityCompat;
import android.support.v4.app.FragmentStatePagerAdapter;
import android.support.v4.view.GravityCompat;
import android.support.v4.view.ViewPager;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.ActionBar;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.GridLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.bumptech.glide.Glide;
import com.example.user.androidhive.Commercial_Fragment.FirstFragment;
import com.example.user.androidhive.Commercial_Fragment.FiveFragment;
import com.example.user.androidhive.Commercial_Fragment.FourFragment;
import com.example.user.androidhive.Commercial_Fragment.SecondFragment;
import com.example.user.androidhive.Commercial_Fragment.ThirdFragment;
import com.example.user.androidhive.MainActivity_New_Moim.New_Moim_Data;
import com.example.user.androidhive.MainActivity_New_Moim.New_moim_GridLayout_Adapter;
import com.example.user.androidhive.MainActivity_Populer_Moim.Populer_Moim_Data;
import com.example.user.androidhive.MainActivity_Populer_Moim.Populer_moim_GridLayout_Adapter;
import com.facebook.stetho.Stetho;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.ArrayList;

import jp.wasabeef.glide.transformations.CropCircleTransformation;

import static com.example.user.androidhive.LoginActivity.Login_State;

/**
 * 2018-1-8
 * 수정
 * 상단바에 돋보기 버튼 클릭 시 검색화면으로 이동 - 검색기능 구현을 위해
 */

public class MainActivity extends AppCompatActivity {

    // 복사 시작
    private DrawerLayout mDrawerLayout;
    private static String TAG = "MainActivity";
    // 복사 끝


    // 아이디를 담고있는 receive0  = static 변수 = 모든 액티비티에 적용
    static String receive0 = "";            // 아이디
    static String Login_user_NAME = "";     // 이름
    static String receive2 = "";            // 이메일
    static String receive3 = "";            // 휴대폰
    static String Profile_image_NAME = "";  // 프로필 이미지 이름 = 초기에 설정되어 있지 않으면 기본값 = '없음'


    // 광고 관련 변수들
    public static final int SEND_INFORMATION = 0; // 호출 변수 설정.
    public static final int SEND_STOP = 1;        // 광고 스레드 호출 변수

    ViewPager Commercial_ViewPaser; // 광고 뷰페이저
    Thread thread;                  // 광고 이동 스레드


    /**
     *  신규 모임 리사이클러뷰 객체 생성
     */
    RecyclerView New_moim_RecyclerView;                         // 신규 모임 리사이클러뷰 객체
    ArrayList<New_Moim_Data> New_moim_DataList;                 // 신규 모임 데이터 리스트
    GridLayoutManager New_moim_GridLayoutManager;               // 신규 모임 신규모임 grid 레이아웃 매니저
    New_moim_GridLayout_Adapter new_moim_gridLayout_adapter;    // 신규 모임 grid 레이아웃 어댑터

    // 신규 모임 데이터 가져오기
    // Json 데이터 파싱 변수들
    String mJsonString;     // json 전체 string 값1
    private static final String TAG_JSON_get_new_moim_data="get_new_moim_data"; // json 제목명

    private static final String TAG_Moim_no = "no";                             // 모임 번호
    private static final String TAG_DEFAULT_imgURI = "default_imgURI";          // 모임이미지 uri
    private static final String TAG_Moim_date_1 = "Moim_date_1";                // 모임 날짜1
    private static final String TAG_Moim_date_2 = "Moim_date_2";                // 모임 날짜2 - 비어있을수도 있음
    private static final String TAG_Application_method = "Application_method";  // 유료 or 무료
    private static final String TAG_simple_address = "simple_address";          // 유료 or 무료



    /**
     *  인기 모임 관련 데이터 생성
     */
    RecyclerView Populer_moim_RecyclerView;
    ArrayList<Populer_Moim_Data> Populer_moim_DataList;
    GridLayoutManager Populer_moim_GridLayoutManager;
    Populer_moim_GridLayout_Adapter Populer_moim_gridLayout_adapter;

//    Populer_Moim_Data.php
    // 인기 모임 데이터 가져오기
    // Json 데이터 파싱 변수들
    String mJsonString2;        // json 전체 string 값2
    private static final String TAG_JSON_get_populer_moim_data="get_populer_moim_data"; // json 제목명





    /**
     *  서비스 작동 상태 확인
     */
    public boolean isServiceRunningCheck() {
        // 액티비티 매니저 = 현재 서비스들의 상태를 가져옴.
        ActivityManager manager = (ActivityManager) this.getSystemService(Activity.ACTIVITY_SERVICE);

        // 조건문 = 현재 돌아가고 있는 모든 서비스들의 수 만큼 반복
        for (ActivityManager.RunningServiceInfo service : manager.getRunningServices(Integer.MAX_VALUE)) {
            Log.e("서비스 이름 = ",service.service.getClassName().toString());   // 해당 서비스의 이름을 표현

            if ("com.example.user.androidhive.MyService".equals(service.service.getClassName())) { // 서비스 클래스 이름 = com.example.user.androidhive.MyService 이면
                return false; // 실행중 의미 = 실행중이니 건들면 안된다는 부정적 의미로 false
            }
        }
        return true; // 실행중인 서비스가 없으니 실행시켜도 된다는 긍정적 의미로 true
    }
    boolean Service_State = false; // 서비스 실행 상태 변수. 기본값 = false = 항상 서비스는 실행되고 있다고 가정한다.




// onCreate 시작 ---------------------------------------------------------------------------------------------------------------------------------------------------------
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        // Stetho 연결
        Stetho.initializeWithDefaults(this);


        /**
         *  서비스 상태값 받아 오기
         *  서비스 중지 상태 = ture => 앱시작할때 실행해도 괜찮다.
         *  서비스 실행 상태 = false => 앱시작 할때 서비스 또 실행하지 마라.
         */
        Service_State = isServiceRunningCheck();
        Log.e("서비스 상태 = ", String.valueOf(Service_State));

//        // 서비스 실행 조건문
//        if(Service_State == true){ // ture => 앱시작할때 서비스를 실행해도 괜찮다.
//            // 서비스 실행 시켜라
//// ---------------------------------------------------------------------------------------------------------------------------------------------------------
//            /** 서비스 시작 인텐트 */
//            Intent AndroidHive_MyService_intent = new Intent(MainActivity.this,MyService.class);  // MyService 인텐트 이동
//            startService(AndroidHive_MyService_intent);   // 서비스 시작 인텐트 실행
//// ---------------------------------------------------------------------------------------------------------------------------------------------------------
//
//            Toast.makeText(MainActivity.this, "서비스 실행가능", Toast.LENGTH_SHORT).show();
//
//        }
//        else if(Service_State == false){ // false => 앱시작 할때 서비스를 또 다시 실행하지 마라.
//            // 아무일도 일어 나지 않음
//            Toast.makeText(MainActivity.this, "서비스 실행 중", Toast.LENGTH_SHORT).show();
//        }





        // 광고 뷰페이저 객체 선언
        Commercial_ViewPaser = (ViewPager)findViewById(R.id.Commercial_ViewPaser);
        Commercial_ViewPaser.setAdapter(new pagerAdapter(getSupportFragmentManager()));   // 뷰 페이저 어댑터 세팅
        Commercial_ViewPaser.setCurrentItem(0);   // 앱 실행시 첫번째 페이지로 초기화 시켜줌.

        // 시작과 동시에 광고 스레드 동작
        thread = new Thread();  // 스레드 객체 생성
        thread.start(); // 스레드 시작



        /**
         * 신규 모임
         * GridLayoutManager 그리드 레이아웃 관련 1줄에 2개 씩 배열
         */
        // 리사이클 러뷰 객체 선언
        New_moim_RecyclerView = (RecyclerView) findViewById(R.id.new_moim_gridView);
        New_moim_RecyclerView.setHasFixedSize(true);
        New_moim_GridLayoutManager = new GridLayoutManager(MainActivity.this, 2);
        New_moim_RecyclerView.setLayoutManager(New_moim_GridLayoutManager);

        New_moim_DataList = new ArrayList<>();    // 어레이리스트 객체 생성 = 그리드 뷰에 들어갈
        new_moim_gridLayout_adapter = new New_moim_GridLayout_Adapter(this, New_moim_DataList);
        New_moim_RecyclerView.setAdapter(new_moim_gridLayout_adapter);


        // 신규 모임 데이터를 가져오는 AsyncTask
        Get_New_Moim_Data get_new_moim_data = new Get_New_Moim_Data();   // 그 아이디를 기준으로 스레드 실행해서 나머지 데이터 가져오기
        get_new_moim_data.execute("아이디보냄~^^"); // 아이디 전송



        /**
         * 인기 모임
         * GridLayoutManager 그리드 레이아웃 관련 1줄에 2개 씩 배열
         */
        // 리사이클 러뷰 객체 선언
        Populer_moim_RecyclerView = (RecyclerView) findViewById(R.id.populer_moim_gridView);
        Populer_moim_RecyclerView.setHasFixedSize(true);
        Populer_moim_GridLayoutManager = new GridLayoutManager(MainActivity.this, 2); //
        Populer_moim_RecyclerView.setLayoutManager(Populer_moim_GridLayoutManager);

        Populer_moim_DataList = new ArrayList<>();    // 어레이리스트 객체 생성 = 그리드 뷰에 들어갈
        Populer_moim_gridLayout_adapter = new Populer_moim_GridLayout_Adapter(this, Populer_moim_DataList);
        Populer_moim_RecyclerView.setAdapter(Populer_moim_gridLayout_adapter);


        // 신규 모임 데이터를 가져오는 AsyncTask
        Get_Populer_Moim_Data get_populer_moim_data = new Get_Populer_Moim_Data();   // 그 아이디를 기준으로 스레드 실행해서 나머지 데이터 가져오기
        get_populer_moim_data.execute("아이디보냄~^^"); // 아이디 전송







// 복사 시작 ------------------------------------------------------------------------------------------------------------------------------------------------
        // 여기서 부터 모두 복사해야 함.
        // 버튼 생성 과 아이디 연결
        Button home_button = (Button) findViewById(R.id.home_button);
        // 홈버튼 클릭 리스너
        home_button.setOnClickListener(new Button.OnClickListener() {
            @Override
            public void onClick(View view) {

                // 임시적으로 서비스 종료 버튼 ㄱㄱ
                Intent intent = new Intent(MainActivity.this,MyService.class);  // MyService 인텐트 이동
                stopService(intent);    // 중지 서비스 인텐트 실행


//                Intent intent = new Intent(
//                        getApplicationContext(), // 현재 화면의 제어권자
//                        MainActivity.class); // 다음 넘어갈 클래스 지정
//                startActivity(intent); // 다음 화면으로 넘어간다
//
//                finish();// 현재 액티비티 종료
            }
        });


        // 툴바 객체 생성
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        ActionBar actionBar = getSupportActionBar();
        actionBar.setHomeAsUpIndicator(R.drawable.ic_menu); // 툴바 왼쪽 세줄 그림 설정
        actionBar.setDisplayHomeAsUpEnabled(true);

        // 텍스트 컬러 설정 = 투명하게 만듬 그래서 타이틀 없애버림 = 편법
        toolbar.setTitleTextColor(Color.TRANSPARENT);
        toolbar.setSubtitleTextColor(Color.TRANSPARENT);


        // DrawerLayout 아이디 설정
        mDrawerLayout = (DrawerLayout) findViewById(R.id.drawer_layout);    // 메인 xml 의 최상단 drawer_layout 레이아웃
        // navigationView id 설정
        NavigationView navigationView = (NavigationView) findViewById(R.id.navigation_view);

        navigationView.setNavigationItemSelectedListener(new NavigationView.OnNavigationItemSelectedListener() {    // 네비게이션 아이템 클릭 리스너
            @Override
            public boolean onNavigationItemSelected(MenuItem menuItem) {        // 네비게이션 아이템 선택시에
                menuItem.setChecked(true);
                mDrawerLayout.closeDrawers();

                int id = menuItem.getItemId();
                switch (id) {

//                    /**
//                     * 카테고리 부분
//                     */
//                    case R.id.navigation_item_attachment:   // 첫번째 카테고리 버튼
//                        Toast.makeText(MainActivity.this, menuItem.getTitle(), Toast.LENGTH_SHORT).show();
//                        break;
//
//                    case R.id.navigation_item_images:   // 교육 - 지워야 함
//                        Toast.makeText(MainActivity.this, menuItem.getTitle(), Toast.LENGTH_SHORT).show();
//                        break;
//
//                    case R.id.navigation_item_location: // 장소 버튼 - 지워야 함
//                        break;




                    /**
                     * MY 카테고리 부분
                     */
                    case R.id.nav_sub_menu_item01:  // 내 정보 버튼
                        if(Login_State == 0 ){// 아직 로그인 안했을 때
                            Toast.makeText(MainActivity.this, "로그인 후에 확인 가능 합니다", Toast.LENGTH_SHORT).show();
                        }
                        else{
                            Intent intent = new Intent(
                                    getApplicationContext(), // 현재 화면의 제어권자
                                    Profle_Activity.class); // 다음 넘어갈 클래스 지정 - Profle_Activity
                            startActivity(intent); // 다음 화면으로 넘어간다
                        }break;
                    case R.id.nav_sub_menu_item02:  // 즐겨찾기 버튼
                        if(Login_State == 0 ){// 아직 로그인 안했을 때
                            Toast.makeText(MainActivity.this, "로그인 후에 확인 가능 합니다", Toast.LENGTH_SHORT).show();
                        }
                        else{
                            Intent intent = new Intent(
                                    getApplicationContext(), // 현재 화면의 제어권자
                                    BookMarkActivity.class); // 다음 넘어갈 클래스 지정 - BookMarkActivity
                            startActivity(intent); // 다음 화면으로 넘어간다
                        }break;

                    case R.id.nav_sub_menu_item03:  // 내가 만든 모임 버튼
                        if(Login_State == 0 ){// 아직 로그인 안했을 때
                            Toast.makeText(MainActivity.this, "로그인 후에 확인 가능 합니다", Toast.LENGTH_SHORT).show();
                        }
                        else{
                            Intent intent = new Intent(
                                    getApplicationContext(), // 현재 화면의 제어권자
                                    Make_list_Activity.class); // 다음 넘어갈 클래스 지정 - Make_list_Activity
                            startActivity(intent); // 다음 화면으로 넘어간다
                        }
                        break;

                    case R.id.nav_sub_menu_item04:  // 신청 내역 버튼
                        if(Login_State == 0 ){// 아직 로그인 안했을 때
                            Toast.makeText(MainActivity.this, "로그인 후에 확인 가능 합니다", Toast.LENGTH_SHORT).show();
                        }
                        else{
                            Intent intent = new Intent(
                                    getApplicationContext(), // 현재 화면의 제어권자
                                    Apply_List_Activity.class); // 다음 넘어갈 클래스 지정 - Apply_List_Activity 화면
                            startActivity(intent); // 다음 화면으로 넘어간다
                        }
                        break;

                    case R.id.nav_sub_menu_item05:  // 채팅 버튼
                        if(Login_State == 0 ){// 아직 로그인 안했을 때
                            Toast.makeText(MainActivity.this, "로그인 후에 확인 가능 합니다", Toast.LENGTH_SHORT).show();
                        }
                        else{
                            Intent intent = new Intent(
                                    getApplicationContext(), // 현재 화면의 제어권자
                                    ChatList_Activity.class); // 다음 넘어갈 클래스 지정 - Chat_LIST_Activity 채팅 리스트 화면  // ChatList_Activity
                            startActivity(intent); // 다음 화면으로 넘어간다
                        }
                        break;
               }
                return true;
            }
        });
// 복사 끝 ------------------------------------------------------------------------------------------------------------------------------------------------


        // 검색 버튼 객체 생성
        ImageButton SearchButton = (ImageButton)findViewById(R.id.search_button);
        SearchButton.setOnClickListener(new Button.OnClickListener() {  // 검색 버튼 클릭 시
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(
                        getApplicationContext(), // 현재 화면의 제어권자
                        SearchActivity.class); // 다음 넘어갈 클래스 지정 = 검색화면으로 이동

                // id 를 넘기는 이유는 id 를 기반으로 최근 검색어를 찾기 위함이다.
                intent.putExtra("ID", receive0 );      // ID 넘기기

                startActivity(intent); // 다음 화면으로 넘어간다
            }
        });


    }
// onCreate 끝 ---------------------------------------------------------------------------------------------------------------------------------------------------------




    /**
     * 뷰페이저 이동 UI 핸들러
     */
    final Handler handler = new Handler(){
        @Override
        public void handleMessage(Message msg) { // 핸들러 내부 메소드   화면에 표시하는 역할은 핸들러가 한다.
            switch (msg.what) {
                case SEND_INFORMATION:

                    if(msg.arg1 == 1 ){    // a가 1이면 첫번째 화면을 보여줘라.
                        Commercial_ViewPaser.setCurrentItem(0);
                    }
                    else if(msg.arg1 == 2){    // a가 2이면 두번째 화면을 보여줘라.
                        Commercial_ViewPaser.setCurrentItem(1);
                    }
                    else if(msg.arg1 == 3){    // a가 3이면 세번째 화면을 보여줘라.
                        Commercial_ViewPaser.setCurrentItem(2);
                    }
                    else if(msg.arg1 == 4){    // a가 3이면 세번째 화면을 보여줘라.
                        Commercial_ViewPaser.setCurrentItem(3);
                    }
                    else if(msg.arg1 == 5){    // a가 3이면 세번째 화면을 보여줘라.
                        Commercial_ViewPaser.setCurrentItem(4);
                    }

                    break;

                case SEND_STOP:
                    thread.stopThread();    // 스레드 중지
                    break;

                default:
                    break;
            }
        }
    };

    /**
     * 뷰페이저를 이용 광고 스레드 시작 내부 클래스
     */
    class Thread extends java.lang.Thread {
        boolean stopped = false;    // 스레드 클래스의 전역변수
        int a = 0;

        public Thread(){    // Thread 메소드
            stopped = false;
        }
        public void stopThread() {  // stopThread 메소드
            stopped = true;
        }

        @Override public void run() {   // 실제 동작을 가리키는 run 메소드
            super.run();

            while (stopped == false){   // 정지가 아닌 상태에서는 계속 실행.
                a++;

                // 메시지 얻어오기
                Message message = handler.obtainMessage();
                // 메시지 ID 설정
                message.what = SEND_INFORMATION;    // 전달 정보 id 설정
                // 메시지 내용 설정 (int)
                message.arg1 = a;           // 1,2,3 반복해서 숫자가 변할 예정.
                // 메시지 전달
                handler.sendMessage(message);

                if(a == 5){    // a가 2이면 두번째 화면을 보여줘라.
                    a = 0;  // a를 0으로 다시 초기화.
                }

                try {
                    // 3초 씩 딜레이 부여
                    sleep(3000);
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }
            }
        }
    }// 스레드 끝




    /**
     * pagerAdapter
     * 광고 뷰페이저 어댑터
     * 페이지 이동 관련
     */
    private class pagerAdapter extends FragmentStatePagerAdapter { // Stage 가 붙은 어댑터를 사용하는 이유가 있다.

        public pagerAdapter(android.support.v4.app.FragmentManager fm)
        {
            super(fm);
        }

        // 포지션에 따라 프래그먼트가 이동하는 부분
        @Override
        public android.support.v4.app.Fragment getItem(int position)  {   // setCurrentItem(tag) 에서 tag = position
            switch(position)    // position 값에 따라서 변화 되는 Fragment
            {
                case 0:
//                    Log.e("케이스 0 포지션값","= "+position);
                    return new FirstFragment();
                case 1:
//                    Log.e("케이스 1 포지션값","= "+position);
                    return new SecondFragment();
                case 2:
//                    Log.e("케이스 2 포지션값","= "+position);
                    return new ThirdFragment();
                case 3:
//                    Log.e("케이스 3 포지션값","= "+position);
                    return new FourFragment();
                case 4:
//                    Log.e("케이스 4 포지션값","= "+position);
                    return new FiveFragment();


                default:
                    return null;
            }
        }
        @Override
        public int getCount() {  // 뷰 페이저 내에 들어가는 Page 의 개수
            return 5;
        }
    }






// onStart() 시작 ------------------------------------------------------------------------------------------------------------------------------------------------------------
    @Override
    protected void onStart() {
        super.onStart();
        Log.e("onStart ","입장");


        NavigationView navigationView = (NavigationView) findViewById(R.id.navigation_view);

        // 헤더뷰 객체 생성
        View headerview = navigationView.getHeaderView(0);
        // 헤더뷰 에 있는 위젯
        TextView Main_Login_id = (TextView) headerview.findViewById(R.id.text_id);              // 헤더뷰 로그인 아이디 텍스트뷰
        TextView Main_Login_email = (TextView) headerview.findViewById(R.id.text_email);        // 헤더뷰 이미엘 텍스트뷰
        final Button Main_logout_button = (Button) headerview.findViewById(R.id.logout_button); // 헤더뷰 로그아웃 버튼
        ImageButton header_userimage = (ImageButton) headerview.findViewById(R.id.user_image);  // 헤더뷰 이미지 버튼



        // 로그인 상태 변수가 1이면 = 로그인 상태
        if(Login_State == 1){
            Log.e("로그인 상태 "," 입장");


            SharedPreferences SF = getSharedPreferences("SF", MODE_PRIVATE);     // Shared 객체 생성. getSharedPreferences(String name, int mode)

            String JSONArray저장스트링 = SF.getString("제이슨어레이 KEY","");  // SharedPreferences 객체명.getString(" key "," 기본값 ");
            // String kkk = 에 제이슨 어레이에 저장된 String 형태의 Key 들과 Data 들을 옮긴다.
            Log.e("저장된 제이슨 어레이 ","확인 : "+JSONArray저장스트링);

            try {                                                               // Log.e("저장된 제이슨 어레이 확인 ",""+JSONArray저장스트링);
                JSONArray jsonArray = new JSONArray(JSONArray저장스트링);
//                Log.e("저장된 데이터 불러오는중 ","abc");      // abc 안찍어 줘서 모르는거다.
                Log.e("제이슨 어레이 길이 = ",""+jsonArray.length());

                for(int i = 0; i < jsonArray.length(); i++) {   // JSONArray 길이 만큼 반복하여 JSONObject 를  꺼냄
                    // Array 에서 하나의 JSONObject 를 추출
                    JSONObject dataJsonObject = jsonArray.getJSONObject(i);
                    Log.e("제이슨 어레이 객체에 저장된 값","abc"+jsonArray.getJSONObject(i));    // 별 희한 하네 여기서 로그 찍으니까 되네
                    // 추출한 Object 에서 필요한 데이터를 표시할 방법을 정해서 화면에 표시
                    // 필자는 RecyclerView 로 데이터를 표시 함
                    Log.e("for문을 도는중","abc");

                    receive0 = dataJsonObject.getString("Login_ID");         // 아이디  // 키 값에 따라 데이터 받기
                    Login_user_NAME = dataJsonObject.getString("Login_NAME");       // 이름
                    receive2 = dataJsonObject.getString("Login_EMAIL");      // 이메일
                    receive3 = dataJsonObject.getString("Login_PHONE");      // 휴대폰
                    Profile_image_NAME = dataJsonObject.getString("Login_Profile_image"); // 프로필 이미지 이름


                    // 데이터 로그 찍어보기
                    Log.e("화면 = ",TAG);
                    Log.e("Login_ID = ",receive0);
                    Log.e("Login_NAME = ",Login_user_NAME);
                    Log.e("Login_EMAIL = ",receive2);
                    Log.e("Login_PHONE = ",receive3);
                    Log.e("Login_Profile_NAME = ",Profile_image_NAME);


                    Main_Login_id.setText(receive0 + " 님");
                    Main_Login_email.setText(receive2);
                    Main_logout_button.setVisibility(View.VISIBLE); // 로그아웃 버튼이 보이게끔.


                    Glide.with(header_userimage.getContext())
                            .load("http://49.247.208.191/AndroidHive/Moim_default_IMG/"+Profile_image_NAME+".jpg")
                            .placeholder(R.drawable.thumnail)
                            .centerCrop()
                            .crossFade()
                            .bitmapTransform(new CropCircleTransformation(header_userimage.getContext()))
                            .override(100,100)  // 리소스의 이미지를 강제적으로 작게 줄여서 보여주려고 할대 사용
                            .into(header_userimage);


                }// 반복문 끝
            } // try 문 끝
            catch (JSONException e) {
                e.printStackTrace();
            }// 불러오기 끝

            // 로그 아웃 버튼 클릭 리스너
            Main_logout_button.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    Toast.makeText(MainActivity.this, "로그아웃 되었습니다.", Toast.LENGTH_SHORT).show();
                    Login_State = 0; // 상태변경

                    receive0 = "";  // static 아이디 초기화.

                    Main_logout_button.setVisibility(View.GONE); // 로그아웃 버튼이 안보임

                    try {
                        // 로그 아웃 되면서 쉐어드에 저장 되어 있는 것들을 초기화 시켜야 한다.
                        // SharedPreferences 저장
                        SharedPreferences SF = getSharedPreferences("SF", MODE_PRIVATE);     // SharedPreferences 객체 생성
                        SharedPreferences.Editor editor = SF.edit();    //저장하려면 editor가 필요

                        JSONArray SF_JsonArray = new JSONArray();      // 쉐어드 제이슨어레이 객체 준비
                        JSONObject SF_JsonObject = new JSONObject();   // 쉐어드 제이슨 객체 생성

                        SF_JsonObject.put("Login_ID", "");        // 비워 버림
                        SF_JsonObject.put("Login_NAME", "");
                        SF_JsonObject.put("Login_EMAIL", "");
                        SF_JsonObject.put("Login_PHONE", "");

                        SF_JsonArray.put(SF_JsonObject);    // 쉐얻 제이슨 어레이 에 제이슨 오브젝트를 집어 넣는다.

                        editor.putString("제이슨어레이 KEY", String.valueOf(SF_JsonArray)); // SF에 키값이 "제이슨어레이 KEY" 인 제이슨어레이를 저장.
                        editor.apply();    // 파일에 최종 반영함
                    }
                    catch (JSONException e) {
                        e.printStackTrace();
                    }


                    Intent intent = new Intent(
                            getApplicationContext(), // 현재 화면의 제어권자
                            MainActivity.class); // 다음 넘어갈 클래스 지정
                    startActivity(intent); // 다음 화면으로 넘어간다

                    finish();// 현재 화면 종료
                }
            });// // 로그 아웃 버튼 클릭 리스너 끝

        }// 로그인 상태 끝


        // 헤더뷰 레이아웃 아이디 설정
        LinearLayout header_user_info = (LinearLayout) headerview.findViewById(R.id.user_info);
        // 헤더뷰 레이아웃 클릭 리스너
        header_user_info.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                if(Login_State == 0) {  // 로그인 상태가 아니면
                    Intent intent = new Intent(
                            getApplicationContext(), // 현재 화면의 제어권자
                            LoginActivity.class); // 다음 넘어갈 클래스 지정 - LoginActivity 로그인 화면
                    startActivity(intent); // 다음 화면으로 넘어간다

                    mDrawerLayout.closeDrawers();   // 서랍 닫기
                }
                else{   // 로그인 상태 면
                    // 응답없음
                }
            }
        });


        // 헤더뷰 이미지 버튼 클릭 리스너
        header_userimage.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                if(Login_State == 0) {  // 로그인 상태가 아니면
                    Intent intent = new Intent(
                            getApplicationContext(), // 현재 화면의 제어권자
                            LoginActivity.class); // 다음 넘어갈 클래스 지정 - LoginActivity 로그인 화면
                    startActivity(intent); // 다음 화면으로 넘어간다

                    mDrawerLayout.closeDrawers();   // 서랍 닫기
                }
                else{   // 로그인 상태 면
                    // 응답없음
                }
            }
        });
    }
// onStart 끝 ---------------------------------------------------------------------------------------------------------------------------------------------------------------



// 복사 시작 ---------------------------------------------------------------------------------------------------------------------------------------------------------------
    // 툴바의 오른쪽 옵션 메뉴 생성
    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.menu, menu);
        return true;
    }

    // 옵션메뉴 들중 선택했을때 하는 행동들 설정
    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();

        switch (id) {
            case android.R.id.home:
                mDrawerLayout.openDrawer(GravityCompat.START);
                return true;
            case R.id.action_settings:
                return true;
        }

        return super.onOptionsItemSelected(item);
    }
// 복사 끝 ---------------------------------------------------------------------------------------------------------------------------------------------------------------


    long pressTime;
    // 백버튼이 눌릴때 하는 행동 메소드
    @RequiresApi(api = Build.VERSION_CODES.JELLY_BEAN)
    @Override
    public void onBackPressed() {
        // super.onBackPressed();
        if(System.currentTimeMillis() - pressTime <2000){
            ActivityCompat.finishAffinity(this);    // 모든 액티비티 종료시키고
            System.exit(0); // 프로세스까지 종료

            return;
        }
        Toast.makeText(this,"한 번더 누르시면 앱이 종료됩니다",Toast.LENGTH_LONG).show();
        pressTime = System.currentTimeMillis();
//
//        backPressCloseHandler.onBackPressed();
    }




    /**
     *  신규 모임 데이터를 가져오는 AsyncTask
     */
    private class Get_New_Moim_Data extends AsyncTask<String, Void, String> {

        ProgressDialog progressDialog =new ProgressDialog(MainActivity.this);
        String errorString = null;

        @Override
        protected void onPreExecute() {     // 시작전
            progressDialog.setProgressStyle(ProgressDialog.STYLE_SPINNER);
            progressDialog.setMessage("데이터 검색 중...");
            // show dialog
            progressDialog.show();
            super.onPreExecute();
        }
        @Override
        protected void onPostExecute(String result) {   // 완료 후
            super.onPostExecute(result);
            Log.e(TAG, "response - " + result);


            if (result == null){    // 값을 받아 오지 못했다면
                Toast.makeText(MainActivity.this,"값을 받아오지 못했습니다.",Toast.LENGTH_SHORT).show();
            }
            else {
                mJsonString = result;
                showResult();
            }
            Log.e("다이얼로그 해제됨","");
            progressDialog.dismiss();   // 다이얼로그 해제
        }


        @Override
        protected String doInBackground(String... params) {

            String Id = params[0];   // 현재 접속자 아이디 =  개설자 이다.

            Log.e("로그인ID =",Id);

            String serverURL = "http://49.247.208.191/AndroidHive/Main/Get_New_Moim_Data.php";   // AndroidHive/Main/Get_New_Moim_Data.php 의 파일로 이동
            String postParameters = "Id=" + Id;

            try {
                URL url = new URL(serverURL);
                HttpURLConnection httpURLConnection = (HttpURLConnection) url.openConnection();

                httpURLConnection.setReadTimeout(5000);
                httpURLConnection.setConnectTimeout(5000);
                httpURLConnection.setRequestMethod("POST");
                httpURLConnection.setDoInput(true);
                httpURLConnection.connect();

                OutputStream outputStream = httpURLConnection.getOutputStream();
                outputStream.write(postParameters.getBytes("UTF-8"));
                outputStream.flush();
                outputStream.close();
                int responseStatusCode = httpURLConnection.getResponseCode();
                Log.d(TAG, "response code - " + responseStatusCode);
                InputStream inputStream;
                if(responseStatusCode == HttpURLConnection.HTTP_OK) {
                    inputStream = httpURLConnection.getInputStream();
                } else{
                    inputStream = httpURLConnection.getErrorStream();
                }
                InputStreamReader inputStreamReader = new InputStreamReader(inputStream, "UTF-8");
                BufferedReader bufferedReader = new BufferedReader(inputStreamReader);

                StringBuilder sb = new StringBuilder();
                String line;
                while((line = bufferedReader.readLine()) != null){
                    sb.append(line);
                }
                bufferedReader.close();
                return sb.toString().trim();
            } catch (Exception e) {
                Log.d(TAG, "InsertData: Error ", e);
                errorString = e.toString();
                return null;
            }
        }
    }// GetData 끝


    /**
     * PHP 에서 가져온 JSON 형태의 데이터를
     * 파싱하여 리사이클러뷰에 넣어준다.
     */
    private void showResult(){

        String total_date;// 날짜 데이터 변수

        try {
            JSONObject jsonObject = new JSONObject(mJsonString);
            JSONArray jsonArray = jsonObject.getJSONArray(TAG_JSON_get_new_moim_data);

            for(int i=0;i<jsonArray.length();i++){  // 불러오는 객체가 1개 이상이면 더 늘어남. 기본 = 1
                String 제이슨길이 = String.valueOf(jsonArray.length());  // 제이슨 길이 int 를 스트링 으로
                Log.e("제이슨어레이 길이 = ",제이슨길이);
                JSONObject item = jsonArray.getJSONObject(i);


                // 데이터 꺼내기
                String Moim_no = item.getString(TAG_Moim_no);                      // 모임 번호
                String DEFAULT_imgURI = item.getString(TAG_DEFAULT_imgURI);        // 모임 이미지 uri
                String Moim_date_1 = item.getString(TAG_Moim_date_1);              // 모임 일자1
                String Moim_date_2 = item.getString(TAG_Moim_date_2);              // 모임 일자2
                String Application_method = item.getString(TAG_Application_method);// 유료 or 무료
                String simple_address = item.getString(TAG_simple_address);        // 간단 주소

                Log.e(TAG+"--------------------- ","");
                Log.e("// 모임 번호 = ",Moim_no);
                Log.e("// 모임 이미지 uri = ",DEFAULT_imgURI);
                Log.e("// 모임 일자1 = ",Moim_date_1);
                Log.e("// 모임 일자2 = ",Moim_date_2);
                Log.e("// 유료 or 무료 = ",Application_method);
                Log.e("// 간단 주소 = ",simple_address);

                if(Moim_date_2.equals("")){ // Moim_date_2 빈칸이면
                     total_date = Moim_date_1;

                }
                else{ // 빈칸이 아니라면
                    // 날짜 데이터 합산하기
                     total_date = Moim_date_1 +"~"+Moim_date_2;
                }

                // 모임 번호, 유료 or 무료, 날짜, 이미지파일 이름
                New_moim_DataList.add(new New_Moim_Data(Moim_no,Application_method,total_date,DEFAULT_imgURI,simple_address ));  //

            }// 반복문 종료
            new_moim_gridLayout_adapter.notifyDataSetChanged();    // 데이터 추가가 완료되었으면 notifyDataSetChanged() 메서드를 호출해 데이터 변경 체크를 실행합니다. = 없어도 되긴 하던데
        } catch (JSONException e) {
            Log.d(TAG, "showResult : ", e);
        }
    }// showResult 끝









    /**
     *  인기 모임 데이터를 가져오는 AsyncTask
     */
    private class Get_Populer_Moim_Data extends AsyncTask<String, Void, String> {

        ProgressDialog progressDialog =new ProgressDialog(MainActivity.this);
        String errorString = null;

        @Override
        protected void onPreExecute() {     // 시작전
            progressDialog.setProgressStyle(ProgressDialog.STYLE_SPINNER);
            progressDialog.setMessage("데이터 검색 중...");
            // show dialog
            progressDialog.show();
            super.onPreExecute();
        }
        @Override
        protected void onPostExecute(String result) {   // 완료 후
            super.onPostExecute(result);
            Log.e(TAG, "response - " + result);


            if (result == null){    // 값을 받아 오지 못했다면
                Toast.makeText(MainActivity.this,"값을 받아오지 못했습니다.",Toast.LENGTH_SHORT).show();
            }
            else {
                mJsonString2 = result;
                showResult2();
            }
            Log.e("다이얼로그 해제됨","");
            progressDialog.dismiss();   // 다이얼로그 해제
        }


        @Override
        protected String doInBackground(String... params) {

            String Id = params[0];   // 현재 접속자 아이디 =  개설자 이다.

            Log.e("로그인ID =",Id);

            String serverURL = "http://49.247.208.191/AndroidHive/Main/Populer_Moim_Data.php";   // AndroidHive/Main/Get_New_Moim_Data.php 의 파일로 이동
            String postParameters = "Id=" + Id;

            try {
                URL url = new URL(serverURL);
                HttpURLConnection httpURLConnection = (HttpURLConnection) url.openConnection();

                httpURLConnection.setReadTimeout(5000);
                httpURLConnection.setConnectTimeout(5000);
                httpURLConnection.setRequestMethod("POST");
                httpURLConnection.setDoInput(true);
                httpURLConnection.connect();

                OutputStream outputStream = httpURLConnection.getOutputStream();
                outputStream.write(postParameters.getBytes("UTF-8"));
                outputStream.flush();
                outputStream.close();
                int responseStatusCode = httpURLConnection.getResponseCode();
                Log.d(TAG, "response code - " + responseStatusCode);
                InputStream inputStream;
                if(responseStatusCode == HttpURLConnection.HTTP_OK) {
                    inputStream = httpURLConnection.getInputStream();
                } else{
                    inputStream = httpURLConnection.getErrorStream();
                }
                InputStreamReader inputStreamReader = new InputStreamReader(inputStream, "UTF-8");
                BufferedReader bufferedReader = new BufferedReader(inputStreamReader);

                StringBuilder sb = new StringBuilder();
                String line;
                while((line = bufferedReader.readLine()) != null){
                    sb.append(line);
                }
                bufferedReader.close();
                return sb.toString().trim();
            } catch (Exception e) {
                Log.d(TAG, "InsertData: Error ", e);
                errorString = e.toString();
                return null;
            }
        }
    }// GetData 끝


    /**
     * PHP 에서 가져온 JSON 형태의 데이터를
     * 파싱하여 리사이클러뷰에 넣어준다.
     */
    private void showResult2(){

        String total_date;// 날짜 데이터 변수

        try {
            JSONObject jsonObject = new JSONObject(mJsonString2);
            JSONArray jsonArray = jsonObject.getJSONArray(TAG_JSON_get_populer_moim_data);

            for(int i=0;i<jsonArray.length();i++){  // 불러오는 객체가 1개 이상이면 더 늘어남. 기본 = 1
                String 제이슨길이 = String.valueOf(jsonArray.length());  // 제이슨 길이 int 를 스트링 으로
                Log.e("제이슨어레이 길이 = ",제이슨길이);
                JSONObject item = jsonArray.getJSONObject(i);


                // 데이터 꺼내기
                String Moim_no = item.getString(TAG_Moim_no);                      // 모임 번호
                String DEFAULT_imgURI = item.getString(TAG_DEFAULT_imgURI);        // 모임 이미지 uri
                String Moim_date_1 = item.getString(TAG_Moim_date_1);              // 모임 일자1
                String Moim_date_2 = item.getString(TAG_Moim_date_2);              // 모임 일자2
                String Application_method = item.getString(TAG_Application_method);// 유료 or 무료
                String simple_address = item.getString(TAG_simple_address);        // 간단 주소

                Log.e(TAG+"--------------------- ","");
                Log.e("// 모임 번호 = ",Moim_no);
                Log.e("// 모임 이미지 uri = ",DEFAULT_imgURI);
                Log.e("// 모임 일자1 = ",Moim_date_1);
                Log.e("// 모임 일자2 = ",Moim_date_2);
                Log.e("// 유료 or 무료 = ",Application_method);
                Log.e("// 간단 주소 = ",simple_address);

                if(Moim_date_2.equals("")){ // Moim_date_2 빈칸이면
                    total_date = Moim_date_1;

                }
                else{ // 빈칸이 아니라면
                    // 날짜 데이터 합산하기
                    total_date = Moim_date_1 +"~"+Moim_date_2;
                }

                // 모임 번호, 유료 or 무료, 날짜, 이미지파일 이름
                Populer_moim_DataList.add(new Populer_Moim_Data(Moim_no,Application_method,total_date,DEFAULT_imgURI,simple_address ));  //

            }// 반복문 종료
            Populer_moim_gridLayout_adapter.notifyDataSetChanged();    // 데이터 추가가 완료되었으면 notifyDataSetChanged() 메서드를 호출해 데이터 변경 체크를 실행합니다. = 없어도 되긴 하던데
        } catch (JSONException e) {
            Log.d(TAG, "showResult : ", e);
        }
    }// showResult 끝





}// 코드 끝



//        Button OpenCV_camera_button = (Button)findViewById(R.id.OpenCV_camera_button);
//        OpenCV_camera_button.setOnClickListener(new View.OnClickListener() {
//            @Override
//            public void onClick(View v) {
//                // 화면이동
//                Intent intent = new Intent(
//                        getApplicationContext(), // 현재 화면의 제어권자
//                        Test_Activity.class); // 다음 넘어갈 클래스 지정
//
//                startActivity(intent); // 다음 화면으로 넘어간다
//
//            }
//        });

















