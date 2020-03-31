package com.example.user.androidhive;

import android.app.FragmentManager;
import android.app.ProgressDialog;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Color;
import android.graphics.Paint;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.design.widget.NavigationView;
import android.support.v4.view.GravityCompat;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.ActionBar;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.MotionEvent;
import android.view.View;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ScrollView;
import android.widget.TextView;
import android.widget.Toast;

import com.bumptech.glide.Glide;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.MapFragment;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.MarkerOptions;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.net.HttpURLConnection;
import java.net.URL;

import static android.util.Log.e;
import static com.example.user.androidhive.LoginActivity.Login_State;
import static com.example.user.androidhive.MainActivity.receive0;
import static com.example.user.androidhive.R.id.map;

public class Searched_Moim_See_Activity extends AppCompatActivity implements OnMapReadyCallback {

    private DrawerLayout mDrawerLayout;
    private static String TAG = "Searched_Moim_See";

    private static final String TAG_JSON_NUMBERS = "MY_Apply_list";   // 제이슨 태그 이름


    // DB 에서 데이터를 전달 받을 때, 필요한 키 생성
    String mJsonString; // 넘어온 제이슨 데이터를 받을 변수
    private static final String TAG_JSON = "webnautes";   // 제이슨 태그 이름
    // 받아올 데이터들의  키값을 미리 설정
    private static final String TAG_NO = "no";                              // 모임번호 key = 두가지의 AcynkTask와 동일하게 사용.
    private static final String TAG_CREATOR = "creator";
    private static final String TAG_DEFAULT_imgURI = "default_imgURI";
    private static final String TAG_NAME = "Name";

    private static final String TAG_TOTAL_PEOPLE ="Total_People";
    private static final String TAG_Available_Seats ="Available_Seats";


    private static final String TAG_Moim_date_1 = "Moim_date_1";
    private static final String TAG_Moim_date_2 = "Moim_date_2";
    private static final String TAG_Moim_start_time = "Moim_start_time";
    private static final String TAG_Moim_end_time = "Moim_end_time";

    private static final String TAG_Moim_apply_date_1 = "Moim_apply_date_1";
    private static final String TAG_Moim_apply_date_2 = "Moim_apply_date_2";
    private static final String TAG_Apply_start_time = "Apply_start_time";
    private static final String TAG_Apply_end_time = "Apply_end_time";

    private static final String TAG_Application_methodE = "Application_method";

    private static final String TAG_Bank = "Bank";
    private static final String TAG_Account_number = "Account_number";
    private static final String TAG_Account_holder = "Account_holder";
    private static final String TAG_Participation_fee = "Participation_fee";

    private static final String TAG_Selection_method = "Selection_method";

    private static final String TAG_Call_number = "Call_number";
    private static final String TAG_Email ="Email";

    private static final String TAG_Place_data ="Place_data";
    private static final String TAG_Address ="Address";
    private static final String TAG_Detail_address ="Detail_address";

    private static final String TAG_Brief_description ="Brief_description";


    // 해당아이디의 신청 모임 번호들을 담을 String 변수
    String ALL_apply_numbers = "";



    /** 화면에 표시할 데이터들을 전역변수로 설정
     * showResult() 메소드에서 사용된다.
     * */
    String no;                  // 모임 번호
    String creator;             // 개설자
    String default_imgURI;      // 기본이미지 이름
    String Name;                // 모임명

    String Total_People;        // 총인원
    String Available_Seats;     // 신청가능 인원


    // 모임날짜
    String Moim_date_1;
    String Moim_date_2;
    String Moim_start_time;
    String Moim_end_time;

    // 접수날짜
    String Moim_apply_date_1;
    String Moim_apply_date_2;
    String Apply_start_time;
    String Apply_end_time;

    // 유 무료
    String Application_method;    // 유료 or 무료

    // 유료의 경우
    String Bank;
    String Account_number;
    String Account_holder;
    String Participation_fee;

    String Selection_method;     // 선정 방법

    // 전화번호, 이메일
    String Call_number;
    String Email;

    // 주소 관련
    String Place_data;     // 위도 경도 데이터
    String Address;
    String Detail_address;

    // 모임 간략 설명
    String Brief_description;





    /** 위젯 객체 전역변수로 설정 */
    ImageView BasicImage_View;

    TextView NameText;          // 모임명
    TextView ChargedText;       // 유,무료
    TextView Moim_DayText;      // 모임날짜
    TextView Resister_DayText;  // 모임접수 날짜

    TextView PeopleText;        // 인원수 , 숫자로 되야됨 예)20
    TextView Available_people_text;        // 현재 신청 가능 인원 = EX)20 - 숫자로 설정
    TextView Deadline_Text;        // 마감 텍스트뷰 - 마감 or 명 신청가능

    TextView EmailText;         // 이메일
    TextView CreatorText;       // 개설자
    TextView PhoneText;         // 휴대폰번호
    TextView Content_Text;      // 글내용

    LinearLayout participate_layout;    // 유료일때는 나타나고 무료일때는 숨겨지는 레이아웃
    TextView Participation_FEE;      // 참가비

    // 주소 부분 텍스트뷰 객체 생성
    TextView Address_textview;
    TextView Detail_address_textview;

    // 신청하기 버튼 전역 객체 생성
    Button Apply_button;


    /** 구글 맵 관련 */
    LatLng PLACE = new LatLng(37.56, 126.97);   // LatLng SEOUL 기본 화면 위치 정보를 저장
    private GoogleMap mMap; // 구글맵 객체 생성
    MarkerOptions markerOptions;    // MarkerOptions 객체를 위치가 선택되면 변경하기 위해 전역변수로 설정한다.


    // 인텐트로 받아오는 데이터 변수를 전역변수로 = onStart 에서 다시 이변수를 사용해서 화면을 표현하기 위해서
    String NUMBER1 ;






    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_searched__moim__see_);



        // 이미지 뷰 객체
        BasicImage_View = (ImageView)findViewById(R.id.BasicImage_View);

        // 텍스트뷰 객체 선언
        NameText = (TextView)findViewById(R.id.NameText);                       // 모임명 - 텍스트뷰
        ChargedText = (TextView)findViewById(R.id.ChargedText);                 // 유,무료
        Moim_DayText = (TextView)findViewById(R.id.Moim_DayText);               // 모임날짜
        Resister_DayText = (TextView)findViewById(R.id.Resister_DayText);       // 접수날짜

        PeopleText = (TextView)findViewById(R.id.PeopleText);                         // 총인원
        Available_people_text = (TextView)findViewById(R.id.Available_people_text);   // 현재 신청 가능 인원 = EX)20 - 숫자로 설정
        Deadline_Text = (TextView)findViewById(R.id.Deadline_Text);                   // 마감 텍스트뷰 - 마감 or 명 신청가능

        EmailText = (TextView)findViewById(R.id.EmailText);                     // 이메일
        CreatorText = (TextView)findViewById(R.id.CreatorText);                 // 개설자
        PhoneText = (TextView)findViewById(R.id.PhoneText);                     // 휴대폰 번호
        Content_Text = (TextView)findViewById(R.id.Content_Text);               // 모임 간략 내용

        participate_layout = (LinearLayout)findViewById(R.id.Price_layout);    // 참가비를 보여주기 위한 레이아웃
        Participation_FEE = (TextView)findViewById(R.id.Participation_FEE);     // 참가비

        // 주소, 상세 주소
        Address_textview = (TextView)findViewById(R.id.Address_textview);
        Detail_address_textview = (TextView)findViewById(R.id.Detail_address_textview);




        // 신청하기 버튼 객체 생성
        Apply_button = (Button) findViewById(R.id.Apply_button);
        // 신청하기 버튼 클릭 리스너
        Apply_button.setOnClickListener(new Button.OnClickListener() {
            @Override
            public void onClick(View view) {
                /**
                 * 예외 처리로 신청한 모임의 경우 이미 신청 되어있다고 버튼이 변경되어 있어야한다.
                 * setText = 참여 신청 하였습니다.
                 * Color = 얇은 회색
                 */

                Intent intent = new Intent(
                        getApplicationContext(), // 현재 화면의 제어권자
                        ApplyActivity.class); // 다음 넘어갈 클래스 지정 = ApplyActivity 신청하기 화면으로 이동


                /** 인텐트 이용하여 신청화면으로 데이터 넘기기
                 *  모임 데이터 전달
                 *  1. 모임 이미지 이름
                 *  2. 모임명
                 *  3. 신청자 선정방법
                 * */
                intent.putExtra("NO",no);                               // 모임 번호 넘기기
                intent.putExtra("default_imgURI",default_imgURI );      // 기본이미지 URI
                intent.putExtra("NAME",Name );                          // 모임명
                intent.putExtra("Selection_method",Selection_method );  // 선정 방법
                intent.putExtra("Application_method",Application_method );  // 유료,무료 넘기기

                // 유료 라면 결제 정보도 넘겨야 한다.
                intent.putExtra("Bank",Bank );                          // 은행명
                intent.putExtra("Account_number",Account_number );      // 예금주
                intent.putExtra("Account_holder",Account_holder );      // 계좌번호
                intent.putExtra("Participation_fee",Participation_fee );// 결제금액

                startActivity(intent); // 신청화면 으로 넘어간다
            }
        });




        // 맵 객체
        FragmentManager fragmentManager = getFragmentManager();
        MapFragment mapFragment = (MapFragment)fragmentManager.findFragmentById(map);
        mapFragment.getMapAsync(this);

/**
 * 맵과 스크롤뷰 터치 겹칩 해결 부분
 */
// 맵과 스크롤뷰 터치 겹칩 해결 부분 -----------------------------------------------------------------------------------------------------------------------------------------------------  끝
        final ScrollView mainScrollView = (ScrollView) findViewById(R.id.main_scrollview);
        ImageView transparentImageView = (ImageView) findViewById(R.id.transparent_image);

        transparentImageView.setOnTouchListener(new View.OnTouchListener() {

            @Override
            public boolean onTouch(View v, MotionEvent event) {
                int action = event.getAction();
                switch (action) {
                    case MotionEvent.ACTION_DOWN:
                        // Disallow ScrollView to intercept touch events.
                        mainScrollView.requestDisallowInterceptTouchEvent(true);
                        // Disable touch on transparent view
                        return false;

                    case MotionEvent.ACTION_UP:
                        // Allow ScrollView to intercept touch events.
                        mainScrollView.requestDisallowInterceptTouchEvent(false);
                        return true;

                    case MotionEvent.ACTION_MOVE:
                        mainScrollView.requestDisallowInterceptTouchEvent(true);
                        return false;

                    default:
                        return true;
                }
            }
        });
// 맵과 스크롤뷰 터치 겹칩 해결 부분  -----------------------------------------------------------------------------------------------------------------------------------------------------  끝





// 여기서 부터 모두 복사해야 함.    ------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------
        // 버튼 생성 과 아이디 연결
        Button home_button = (Button) findViewById(R.id.home_button);

        // 홈버튼 클릭 리스너
        home_button.setOnClickListener(new Button.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(
                        getApplicationContext(), // 현재 화면의 제어권자
                        MainActivity.class); // 다음 넘어갈 클래스 지정
                startActivity(intent); // 다음 화면으로 넘어간다

                finish();// 현재 액티비티 종료
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

        // 헤더뷰 객체 생성
        View headerview = navigationView.getHeaderView(0);
        // 헤더의 텍스트뷰 아이디 설정





        navigationView.setNavigationItemSelectedListener(new NavigationView.OnNavigationItemSelectedListener() {    // 네비게이션 아이템 클릭 리스너
            @Override
            public boolean onNavigationItemSelected(MenuItem menuItem) {        // 네비게이션 아이템 선택시에
                menuItem.setChecked(true);
                mDrawerLayout.closeDrawers();

                int id = menuItem.getItemId();
                switch (id) {


                    /**
                     * MY 구분부분
                     *
                     */
                    case R.id.nav_sub_menu_item01:  // 내 정보 버튼
                        if(Login_State == 0 ){// 아직 로그인 안했을 때
                            Toast.makeText(Searched_Moim_See_Activity.this, "로그인 후에 확인 가능 합니다", Toast.LENGTH_SHORT).show();
                        }
                        else{
                            Intent intent = new Intent(
                                    getApplicationContext(), // 현재 화면의 제어권자
                                    Profle_Activity.class); // 다음 넘어갈 클래스 지정 - Profle_Activity
                            startActivity(intent); // 다음 화면으로 넘어간다
                        }break;
                    case R.id.nav_sub_menu_item02:  // 즐겨찾기 버튼
                        if(Login_State == 0 ){// 아직 로그인 안했을 때
                            Toast.makeText(Searched_Moim_See_Activity.this, "로그인 후에 확인 가능 합니다", Toast.LENGTH_SHORT).show();
                        }
                        else{
                            Intent intent = new Intent(
                                    getApplicationContext(), // 현재 화면의 제어권자
                                    BookMarkActivity.class); // 다음 넘어갈 클래스 지정 - BookMarkActivity
                            startActivity(intent); // 다음 화면으로 넘어간다
                        }break;

                    case R.id.nav_sub_menu_item03:  // 내가 만든 모임 버튼
                        if(Login_State == 0 ){// 아직 로그인 안했을 때
                            Toast.makeText(Searched_Moim_See_Activity.this, "로그인 후에 확인 가능 합니다", Toast.LENGTH_SHORT).show();
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
                            Toast.makeText(Searched_Moim_See_Activity.this, "로그인 후에 확인 가능 합니다", Toast.LENGTH_SHORT).show();
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
                            Toast.makeText(Searched_Moim_See_Activity.this, "로그인 후에 확인 가능 합니다", Toast.LENGTH_SHORT).show();
                        }
                        else{
                            Intent intent = new Intent(
                                    getApplicationContext(), // 현재 화면의 제어권자
                                    ChatList_Activity.class); // 다음 넘어갈 클래스 지정 - Chat_LIST_Activity 채팅 리스트 화면
                            startActivity(intent); // 다음 화면으로 넘어간다
                        }
                        break;
                }
                return true;
            }
        });


        // 헤더뷰 에 있는 위젯
        TextView Main_Login_id = (TextView) headerview.findViewById(R.id.text_id);
        TextView Main_Login_email = (TextView) headerview.findViewById(R.id.text_email);
        final Button Main_logout_button = (Button) headerview.findViewById(R.id.logout_button);   // 로그아웃 버튼


        // 로그인 상태 변수가 1이면 = 로그인 상태
        if(Login_State == 1){

            SharedPreferences SF = getSharedPreferences("SF", MODE_PRIVATE);     // Shared 객체 생성. getSharedPreferences(String name, int mode)

            String JSONArray저장스트링 = SF.getString("제이슨어레이 KEY","");  // SharedPreferences 객체명.getString(" key "," 기본값 ");
            // String kkk = 에 제이슨 어레이에 저장된 String 형태의 Key 들과 Data 들을 옮긴다.
            e("저장된 제이슨 어레이 확인 ",""+JSONArray저장스트링);

            try {                                                               // Log.e("저장된 제이슨 어레이 확인 ",""+JSONArray저장스트링);
                JSONArray jsonArray = new JSONArray(JSONArray저장스트링);
                e("저장된 데이터 불러오는중 ","abc");      // abc 안찍어 줘서 모르는거다.
                e("제이슨 어레이 길이 = ",""+jsonArray.length());

                for(int i = 0; i < jsonArray.length(); i++) {   // JSONArray 길이 만큼 반복하여 JSONObject 를  꺼냄
                    // Array 에서 하나의 JSONObject 를 추출
                    JSONObject dataJsonObject = jsonArray.getJSONObject(i);
                    e("제이슨 어레이 객체에 저장된 값","abc"+jsonArray.getJSONObject(i));    // 별 희한 하네 여기서 로그 찍으니까 되네
                    // 추출한 Object 에서 필요한 데이터를 표시할 방법을 정해서 화면에 표시
                    // 필자는 RecyclerView 로 데이터를 표시 함
                    e("for문을 도는중","abc");

                    receive0 = dataJsonObject.getString("Login_ID");         // 아이디  // 키 값에 따라 데이터 받기
                    String receive1 = dataJsonObject.getString("Login_NAME");       // 이름
                    String receive2 = dataJsonObject.getString("Login_EMAIL");      // 이메일
                    String receive3 = dataJsonObject.getString("Login_PHONE");      // 휴대폰
//                String 이미지Uri스트링 = dataJsonObject.getString("제품URI");   // 제품 Uri String

                    // 데이터 로그 찍어보기
                    e("화면 = ",TAG);
                    e("Login_ID = ",receive0);
                    e("Login_NAME = ",receive1);
                    e("Login_EMAIL = ",receive2);
                    e("Login_PHONE = ",receive3);


                    Main_Login_id.setText(receive0 + " 님");
                    Main_Login_email.setText(receive2);
                    Main_logout_button.setVisibility(View.VISIBLE); // 로그아웃 버튼이 보이게끔.

                }// try 문 끝
            } catch (JSONException e) {
                e.printStackTrace();
            }// 불러오기 끝

            // 로그 아웃 버튼 클릭 리스너
            Main_logout_button.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    Toast.makeText(Searched_Moim_See_Activity.this, "로그아웃 되었습니다.", Toast.LENGTH_SHORT).show();
                    Login_State = 0; // 상태변경

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
            });

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
                            LoginActivity.class); // 다음 넘어갈 클래스 지정
                    startActivity(intent); // 다음 화면으로 넘어간다

                    mDrawerLayout.closeDrawers();   // 서랍 닫기
                }
                else{   // 로그인 상태 면

                }
            }
        });

        // 헤더뷰 이미지 버튼 아아디 설정
        ImageButton header_userimage = (ImageButton) headerview.findViewById(R.id.user_image);
        // 헤더뷰 이미지 버튼 클릭 리스너

        header_userimage.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                if(Login_State == 0) {  // 로그인 상태가 아니면
                    Intent intent = new Intent(
                            getApplicationContext(), // 현재 화면의 제어권자
                            LoginActivity.class); // 다음 넘어갈 클래스 지정
                    startActivity(intent); // 다음 화면으로 넘어간다

                    mDrawerLayout.closeDrawers();   // 서랍 닫기
                }
                else{   // 로그인 상태 면

                }
            }
        });
// 복사 끝 -------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------





// 검색 화면의 모임 리스트에서 모임 하나 선택 시  ---------------------------------------------------------------------------------------------------------------------------------------------------------------------
        // 검색된 모임의 아이템 클릭 시 해당 모임의 데이터 받아오기
        Intent intent = getIntent();    //이전 액티비티에서 수신할 데이터를 받기위해 선언

        // String 변수에 담기 - 이것을 기반으로 모임을 찾는다.
        NUMBER1 = intent.getStringExtra("NUMBER");      // 키 = NUMBER

        e("번호 = ",NUMBER1);

        // 이것들을 PHP 문으로 넘기고
        // PHP 문에서 모임을 찾아서
        // 관련 데이터 들을 JSON 형태로 여기로 넘기고
        // 여기에서 그것들을 풀어서
        // 연결 시켜서 나타낸다.

        /** 신청한 모임이라면 2번 신청되끔 하면 안되기에
         *  내가 신청한 모임들의 번호를 알아내는 AcynkTast
         * */
        Get_Apply_List task1 = new Get_Apply_List();     // 태스크 생성
        task1.execute(receive0);  // 태스크 진행하면서 [ receive0 = 아이디] 를 전달한다.


        /** 해당 모임의 데이터 일부를 전달하고 그것을 기반으로 해당 데이터의 전체 데이터를 찾기위해 MoimDataGET 를 사용 */
        MoimDataGET task = new MoimDataGET();     // 태스크 생성
        task.execute(NUMBER1 );  // 태스크 진행하면서 [모임번호] 를 전달한다.
// ---------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------



    }// onCreate 끝







    /**
     *  해당아이디의 신청정보의 모임번호 검색을 위한 AsyncTask
     */
    private class Get_Apply_List extends AsyncTask<String, Void, String> {
        String errorString = null;
        @Override
        protected void onPreExecute() {     // 시작전
            super.onPreExecute();
        }
        @Override
        protected void onPostExecute(String result) {   // 완료 후
            super.onPostExecute(result);
            Log.d(TAG, "response - " + result);

            String errorSQL = result.substring(0,3);   // 0 번째에서 14번째 까지 단어 추출

            if (result == null){    // 값을 받아 오지 못했다면
                Toast.makeText(Searched_Moim_See_Activity.this,"해당아이디의 모임번호를 받아오는데 문제가 있습니다.",Toast.LENGTH_SHORT).show();
            }
            else if(result.equals("신청된 모임 없음")){    // 검색된 모임이 없으면
            }
            else if(result.equals("값을 받아오지 못했습니다.")){    // 아이디가 제대로 넘어 가지 않은 상황
                Toast.makeText(Searched_Moim_See_Activity.this,"아이디가 넘어가지 않았음.",Toast.LENGTH_SHORT).show(); //
            }
            else if(errorSQL.equals("SQL문")){    // 아이디가 제대로 넘어 가지 않은 상황
                Toast.makeText(Searched_Moim_See_Activity.this,"SQL문 처리중 에러 발생.",Toast.LENGTH_SHORT).show();
                Log.e(TAG, "response - " + result);
            }
            else {  // 제대로 값을 받아왔다면
                mJsonString = result;   // 받아온 JSON 형태의 데이터들을 담는다.
                get_apply_list();
            }
            Log.e("다이얼로그 해제됨","");
        }


        @Override
        protected String doInBackground(String... params) {

            String ID = params[0];   // 검색어

            Log.e(TAG,"");
            Log.e("search_word =",ID);
//            49.247.208.191
            String serverURL = "http://49.247.208.191/AndroidHive/Apply/Participant/MY_Apply_list.php";   // AndroidHive/MY_Apply_list.php 의 파일로 이동
            String postParameters = "ID=" + ID;

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
    }// Get_Searched_MoimData 끝


    /**
     * PHP 에서 가져온 JSON 형태의 모임 데이터를
     * 파싱하여 해당아이디의 신청한 모임들의 번호들을 추출한다.
     */
    private void get_apply_list(){
        try {
            JSONObject jsonObject = new JSONObject(mJsonString);
            JSONArray jsonArray = jsonObject.getJSONArray(TAG_JSON_NUMBERS);

            for(int i=0;i<jsonArray.length();i++){  // 불러오는 객체가 1개 이상이면 더 늘어남. 기본 = 1
                String 제이슨길이 = String.valueOf(jsonArray.length());  // 제이슨 길이 int 를 스트링 으로
                Log.e("제이슨어레이 길이 = ",제이슨길이);
                JSONObject item = jsonArray.getJSONObject(i);

                /**
                 * 모임 데이터 표현
                 */
                String numbers = item.getString(TAG_NO);  // 모임 번호

                ALL_apply_numbers = ALL_apply_numbers + numbers + ",";   // 현재아이디의 모임번호들을 차례로 담는다.
                Log.e("--------------------- ","");
                Log.e("해당아이디의 신청모임 번호들 = ",ALL_apply_numbers);  // 최종적으로 다 담기면  EX)12,14,55,44,58, 형태로 된다.

            }// 반복문 종료

        } catch (JSONException e) {
            Log.d(TAG, "showResult : ", e);
        }
    }// get_apply_list 끝












    /** 모든 데이터를 찾아서 모임확인화면에 데이터를 표현한다. */
    // 어싱크테스크 에서 데이터 전달
    class MoimDataGET extends AsyncTask<String, Void, String> {
        ProgressDialog progressDialog;

        @Override
        protected void onPreExecute() {     // 작업 시작전
            super.onPreExecute();
            progressDialog = ProgressDialog.show(Searched_Moim_See_Activity.this, "Please Wait", null, true, true);   // 완료 될때까지 다이얼로그 생성
        }

        @Override
        protected void onPostExecute(String result) {   // 작업이 끝난후
            super.onPostExecute(result);
            Log.e("MoimDataGET", "POST response  = " + result);   // result 값을 echo 를 받아들여서 출력한다.

            if (result.equals("모임찾기실패")){    // 값을 받아 오지 못했다면
                Toast.makeText(Searched_Moim_See_Activity.this,"모임찾기에 실패하였습니다..",Toast.LENGTH_SHORT).show();
                finish();
            }
            else {  //로그인 성공 json 값을 받아 왔으니 데이터를 넣자
                mJsonString = result;   // 받아온 JSON 데이터를 mJsonString 에 저장한다.
                showResult();   // JSON 데이터를 해체하는 메소드인 showResult 로 이동한다.
            }

            progressDialog.dismiss();   // 다이얼로그 해제
        }// onPostExecute 끝


        @Override
        protected String doInBackground(String... params) {     // 실제 이뤄지는 작업

            String NUMBER = (String)params[0];  // 실제 적으로 모임 넘버를 통해 데이터를 찾음.
            e("모임번호 확인 ="," "+ NUMBER);

            String serverURL = "http://49.247.208.191/AndroidHive/Moim_find.php"; // Moim_find.php 으로 모임 번호 전달
            String postParameters = "NUMBER=" + NUMBER;

            try {
                URL url = new URL(serverURL);
                HttpURLConnection httpURLConnection = (HttpURLConnection) url.openConnection();

                httpURLConnection.setReadTimeout(5000);
                httpURLConnection.setConnectTimeout(5000);
                httpURLConnection.setRequestMethod("POST");     // 메소드 타입 POST
//                httpURLConnection.setRequestProperty("content-type", "application/json");
                httpURLConnection.setDoInput(true);
                httpURLConnection.connect();

                OutputStream outputStream = httpURLConnection.getOutputStream();
                outputStream.write(postParameters.getBytes("UTF-8"));
                outputStream.flush();
                outputStream.close();

                int responseStatusCode = httpURLConnection.getResponseCode();
                e("MoimDataGET", "POST response code - " + responseStatusCode);

                InputStream inputStream;
                if(responseStatusCode == HttpURLConnection.HTTP_OK) {
                    inputStream = httpURLConnection.getInputStream();
                } else{
                    inputStream = httpURLConnection.getErrorStream();
                }
                InputStreamReader inputStreamReader = new InputStreamReader(inputStream, "UTF-8");
                BufferedReader bufferedReader = new BufferedReader(inputStreamReader);

                StringBuilder sb = new StringBuilder();
                String line = null;

                while((line = bufferedReader.readLine()) != null){
                    sb.append(line);
                }
                bufferedReader.close();
                return sb.toString();
            }

            catch (Exception e) {
                e("MoimDataGET", "InsertData: Error ", e);
                return new String("Error: " + e.getMessage());
            }
        }
    }// MoimDataGET 끝






    /** JSON 데이터를 파싱하는 메소드
     *  반복문을 통해 각각의 JSON 객체(모임 1개의 데이터 모음집) 안에 포함된 데이터들을 가져와서
     *  데이터 들을 위젯에 뿌려서 표시한다.
     */
    private void showResult(){
        try {
            JSONObject jsonObject = new JSONObject(mJsonString);
            JSONArray jsonArray = jsonObject.getJSONArray(TAG_JSON);

            for(int i=0;i<jsonArray.length();i++){

                JSONObject item = jsonArray.getJSONObject(i);

                /** 데이터 가져오기 ----------------------------------------------------------------------------------- 시작*/
                // JSON으로 넘어온 데이터들을 Key태그에 맞춰서 php 파일에서 받아온 데이터들을 각 스트링에 넣는다.
                no = item.getString(TAG_NO);                            // 모임 번호
                creator = item.getString(TAG_CREATOR);                  // 개설자
                default_imgURI = item.getString(TAG_DEFAULT_imgURI);    // 기본이미지 이름
                Name = item.getString(TAG_NAME);                        // 모임명

                Total_People = item.getString(TAG_TOTAL_PEOPLE);        // 총인원
                Available_Seats = item.getString(TAG_Available_Seats);  // 신청가능인원


                // 모임날짜
                Moim_date_1 = item.getString(TAG_Moim_date_1);
                Moim_date_2 = item.getString(TAG_Moim_date_2);
                Moim_start_time = item.getString(TAG_Moim_start_time);
                Moim_end_time = item.getString(TAG_Moim_end_time);

                // 접수날짜
                Moim_apply_date_1 = item.getString(TAG_Moim_apply_date_1);
                Moim_apply_date_2 = item.getString(TAG_Moim_apply_date_2);
                Apply_start_time = item.getString(TAG_Apply_start_time);
                Apply_end_time = item.getString(TAG_Apply_end_time);

                // 유 무료
                Application_method = item.getString(TAG_Application_methodE);    // 유, 무료

                // 유료의 경우
                Bank = item.getString(TAG_Bank);
                Account_number = item.getString(TAG_Account_number);
                Account_holder = item.getString(TAG_Account_holder);
                Participation_fee = item.getString(TAG_Participation_fee);

                /** 아직 안넣음 */
                Selection_method = item.getString(TAG_Selection_method);     // 선정 방법

                // 전화번호, 이메일
                Call_number = item.getString(TAG_Call_number);
                Email = item.getString(TAG_Email);

                /** 아직 안넣음 */
                // 주소 관련
                Place_data = item.getString(TAG_Place_data);     // 위도 경도 데이터
                Address = item.getString(TAG_Address);
                Detail_address = item.getString(TAG_Detail_address);

                e("TAG = ",TAG);
                e("수정 전 위도경도 데이터 = ",Place_data);

                // 위도 경도 데이터 숫자만 남기기
                Place_data = Place_data.replace("lat/lng","");
                Place_data = Place_data.replace(":","");
                Place_data = Place_data.replace("(","");
                Place_data = Place_data.replace(")","");
                Place_data = Place_data.replaceAll(" ","");
                e("수정 후 위도경도 데이터(숫자만) = ",Place_data);


                // 모임 간략 설명
                Brief_description = item.getString(TAG_Brief_description);


                e("화면 = ",TAG);
                e("은행 = ",Bank);
                e("계좌번호 = ",Account_number);
                e("예금주 = ",Account_holder);
                e("참가비용 = ",Participation_fee);
                e("이미지 Name = ",default_imgURI);
                /** 데이터 가져오기 ----------------------------------------------------------------------------------- 끝   */




                /** 위젯에 데이터 삽입하기 ----------------------------------------------------------------------------------- 시작   */
                NameText.setText(Name);         // 모임명 설정
                ChargedText.setText(Application_method);    // 유,무료 설정

                /** ------------------------------------------------------------------ ------------------------------------------------------------------*/
                /**
                 * 유료(빨강)
                 * 무료(초록)
                 * 글자색 설정
                 */
                if(Application_method.equals("유료")){    // 유료일 경우
                    ChargedText.setTextColor(Color.RED);    // 글자색 빨강색 변경
                }
                else{                                         // 무료일 경우
                    ChargedText.setTextColor(Color.GREEN);    // 글자색 초록색 변경
                }
                ChargedText.setText(Application_method);    // 유,무료 설정

                /**
                 * 모임날짜 설정
                 */
                if(Moim_date_2.equals("")){ // 모임날짜 2가 비어잇으면
                    Moim_DayText.setText(Moim_date_1 + " " + Moim_start_time + " ~ " + Moim_end_time);
                }
                else{
                    Moim_DayText.setText(Moim_date_1 + " " + Moim_start_time + " ~ " + Moim_date_2 + " " + Moim_end_time);
                }

                /**
                 * 접수날짜 설정
                 */
                if(Moim_apply_date_2.equals("")){ // 접수날짜 2가 비어잇으면
                    Resister_DayText.setText(Moim_apply_date_1 + " " + Apply_start_time + " ~ " + Apply_end_time);
                }
                else{
                    Resister_DayText.setText(Moim_apply_date_1 + " " + Apply_start_time + " ~ " + Moim_apply_date_2 + " " + Apply_end_time);
                }

                /**
                 * 유료일 경우 참가비용 보이기 설정
                 */
                if(Application_method.equals("유료")){   // 만약 유료이면
                    participate_layout.setVisibility(View.VISIBLE);
                    Participation_FEE.setText(Participation_fee + " 원");
                }
                else{ // 무료 이면
                    participate_layout.setVisibility(View.GONE);
                }
                /** ------------------------------------------------------------------ ------------------------------------------------------------------*/


                PeopleText.setText(Total_People);               // 총인원 설정

                if(Available_Seats.equals("0")){    // 넘어온 신청가능 인원이 0 명이면
                    // 신청가능 인원이 0 명이면
                    // 마감 되었다는 텍스트를 띄워야 한다.
                    // 마감 텍스트를 확인한 사용자가 신청하기 버튼을 눌렀을때
                    // 대기자 상태가 되어도 괜찮겠냐는 다이얼로그를 띄우게 해서 확인하게 끔 한다.

                    Deadline_Text.setTextColor(Color.RED);      // 텍스트 색상 빨강색
                    Deadline_Text.setPaintFlags(Deadline_Text.getPaintFlags() | Paint.FAKE_BOLD_TEXT_FLAG);   // 텍스트 굵게 설정
                    Deadline_Text.setText("신청좌석수 마감");    // '신청좌석수 마감' 텍스트 설정
                    Available_people_text.setVisibility(View.GONE); // 신청가능인원 텍스트는 보이지 않게끔 설정
                }
                else{   // 넘어온 신청가능 인원이 0 명이 아니라면, 최소 1명 이상이라면
                    Available_people_text.setText(Available_Seats); // 신청가능 인원 설정
                }

                CreatorText.setText(creator);  // 개설자
                PhoneText.setText(Call_number);    // 휴대폰
                EmailText.setText(Email);    // 이메일


                Content_Text.setText(Brief_description);    // 모임 내용.

                /** 위젯에 데이터 삽입하기 ----------------------------------------------------------------------------------- 끝   */



                /** ------------------------------------------------------------------ 이미지 삽입 부분. ------------------------------------------------------------------*/

                final String img_NAME = default_imgURI;   // 포지션 별 String 형태로 된 이미지 Uri를 가져온다.

                Glide.with(getApplicationContext()).load("http://49.247.208.191/AndroidHive/Moim_default_IMG/"+img_NAME).placeholder(R.drawable.noimage).into(BasicImage_View);

                /** ------------------------------------------------------------------ 이미지 삽입 끝 ------------------------------------------------------------------*/




                /** ------------------------------------------------------------------ 지도 삽입 부분. ------------------------------------------------------------------*/

                //  숫자로만 된 위치데이터를 가져와서 둘로 쪼갠다,
                String[] latlong =  Place_data.split(",");
                double latitude = Double.parseDouble(latlong[0]);   // 위도
                double longitude = Double.parseDouble(latlong[1]);  // 경도
                e("latitude",""+latitude);
                e("longitude",""+longitude);


                PLACE = new LatLng(latitude, longitude);   // LatLng 위도 경도 대입
                e("위치데이터",""+PLACE.toString());

                // 위치데이터와 주소를 입력하고 맵 프래그먼트에 표시
                pickMark(PLACE, Detail_address, Address);   // pickMark(final LatLng LL, String address)
//                markerOptions.title(Detail_address);        // 마커 타이틀에 이름 넣고
//                markerOptions.snippet(Address);   // 마커 세부 내용에 주소 넣는다.

                mMap.moveCamera(CameraUpdateFactory.newLatLng(PLACE));  // 맵마커의 위치에 따라 카메라 이동. 위치 = PLACE
                mMap.animateCamera(CameraUpdateFactory.zoomTo(13)); // 카메라 줌 상태 설정

                Address_textview.setText(Address);  // 주소
                Detail_address_textview.setText(Detail_address);    // 상세주소

                /** ------------------------------------------------------------------ 지도 삽입 끝. ------------------------------------------------------------------*/

                /**
                 * 만약 보여줄려고 하는 모임이 현재 아이디가 '이미 신청한 모임'이라면 신청버튼을 보여주지 않아야 한다,
                 * ------------------------------------------------------------------ 신청하기 버튼 보이기 유무 ------------------------------------------------------------------*/

                // 화면에 보여주고자 하는 모임번호 = no
                // 이미 신청하였고 비교하고자 하는 모임번호들 = ALL_apply_numbers EX)12,54,14,58,55

                // 모임 번호들을 분리자(,) 로 나누어 String[] 배열 arr 에 따로 담는다.
                String[] numbers_Array = ALL_apply_numbers.split(",");   // String[] arr 전역 변수임
                // 모임번호 배열 길이
                int numbers_Array_length = numbers_Array.length;    // 만약 배열의 길이가 3 이라면

//                Log.e("앞 번호 = ", numbers_Array[0] );        // 10
//                Log.e("중간 번호 = ", numbers_Array[1] );      // 54
//                Log.e("끝 번호 = ", numbers_Array[2] );        // 7

                for(int a = 0; a < numbers_Array_length; a++){     // 3번 비교해야 한다.
                    if(no.equals(numbers_Array[a])){    // 모임번호가 신청된 모임번호가 같다면
                        Apply_button.setVisibility(View.GONE);  // 신청하기 버튼이 보이지 않게끔 설정
                    }
                }
                /** ------------------------------------------------------------------ 신청하기 버튼 보이기 유무 끝. ------------------------------------------------------------------*/

            }// 반복문 종료


        } catch (JSONException e) {
            Log.d("MoimDataGET", "showResult : ", e);
        }
    }// showresult 끝




    /**
     * 위도 경도, 상세주소, 주소 받아서 마커 찍는 pickMark 메소드
     */
    private void pickMark(final LatLng LL, String Detail_address, String address){ // 위도 경도, 이름 주소 받아서 마커 찍는 함수
        mMap.clear();
        markerOptions = new MarkerOptions();
        markerOptions.position(LL); // 위치 적용
        markerOptions.title(Detail_address);    // 타이틀에는 상세주소를
        markerOptions.snippet(address);         // snippet에는 일반주소

        mMap.addMarker(markerOptions).showInfoWindow(); // 맵에 추가
    } // pickMark 끝









    @Override
    protected void onStart() {
        super.onStart();
//
//        MoimDataGET task = new MoimDataGET();     // 태스크 생성
//        task.execute( NUMBER1 );  // 태스크 진행하면서 [모임명,개설자,모임번호] 를 전달한다.

        e("onStart 끝 ", "");
    }// onStart 끝


    /**
     * 초기 맵 마커 설정 onMapReady 메소드
     */
    @Override
    public void onMapReady(final GoogleMap map) {

        mMap = map; // 전역 map 객체인 mMap 과 onMapReady 메소드 인수 GoogleMap map 과 동일한 객체로 인식하게 한다.

        markerOptions = new MarkerOptions();    // MarkerOptions 객체 생성
        markerOptions.position(PLACE);  // 위도 경도 받아오기
        markerOptions.title("서울");  // 타이틀 설정
        markerOptions.snippet("위치정보를 받아오지 못했습니다.");    // 세부 내용 설정 = 위치 정보를 제대로 표현하지 못할경우 이렇게 뜬다.
        mMap.addMarker(markerOptions);  // 마커 추가

        mMap.moveCamera(CameraUpdateFactory.newLatLng(PLACE));
        mMap.animateCamera(CameraUpdateFactory.zoomTo(10));
    }






    // 복사 시작 -------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------
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
    // 복사 끝------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------





}// 코드 끝
