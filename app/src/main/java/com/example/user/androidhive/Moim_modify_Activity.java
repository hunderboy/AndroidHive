package com.example.user.androidhive;

import android.Manifest;
import android.app.Activity;
import android.app.FragmentManager;
import android.app.ProgressDialog;
import android.content.ComponentName;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.pm.PackageManager;
import android.content.pm.ResolveInfo;
import android.graphics.Bitmap;
import android.graphics.Color;
import android.media.MediaScannerConnection;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Build;
import android.os.Bundle;
import android.os.Environment;
import android.provider.MediaStore;
import android.support.design.widget.NavigationView;
import android.support.v4.app.ActivityCompat;
import android.support.v4.content.ContextCompat;
import android.support.v4.content.FileProvider;
import android.support.v4.view.GravityCompat;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.ActionBar;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.MotionEvent;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RadioButton;
import android.widget.RadioGroup;
import android.widget.ScrollView;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import com.bumptech.glide.Glide;
import com.example.user.androidhive.CalenderDecorate.CalenderActivity;
import com.google.android.gms.common.GooglePlayServicesNotAvailableException;
import com.google.android.gms.common.GooglePlayServicesRepairableException;
import com.google.android.gms.location.places.Place;
import com.google.android.gms.location.places.ui.PlacePicker;
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
import java.io.DataOutputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import static com.example.user.androidhive.LoginActivity.Login_State;
import static com.example.user.androidhive.MainActivity.receive0;
import static com.example.user.androidhive.Moim_Make_Activity.Clock_num;
import static com.example.user.androidhive.R.id.Price_charged;

public class Moim_modify_Activity extends AppCompatActivity implements OnMapReadyCallback {

    private DrawerLayout mDrawerLayout;
    private static String TAG = "Moim_Make_Activity";

    // SharedPreference 에서 받아오는 값들을 넣을 변수 = 전역변수 화
//    String receive0;      // = Login_ID = 아이디     // 키 값에 따라 데이터 받기
    String receive1;      // = Login_NAME = 이름
    String receive2;      // = Login_EMAIL = 이메일
    String receive3;      // = Login_PHONE = 휴대폰



    // 모임명
    EditText Moim_Name_Edit;
    // 총인원
    EditText Total_people;


    // 모임 일시 관련
    Button set_date1;    // 모임 날자 설정 버튼1
    Button set_clock1;   // 모임 시간 설정 버튼1
    Button set_date2;    // 모임 날자 설정 버튼2
    Button set_clock2;   // 모임 시간 설정 버튼2
    CheckBox Moim_date_over;    // 모임기간이 하루이상 일 경우 를 위한 체크박스

    // 접수 기간 관련
    Button set_apply_date1;    // 접수 날자 설정 버튼1
    Button set_apply_clock1;   // 접수 시간 설정 버튼1
    Button set_apply_date2;    // 접수 날자 설정 버튼2
    Button set_apply_clock2;   // 접수 시간 설정 버튼2
    CheckBox apply_date_over;    // 접수 기간이 하루이상 일 경우 를 위한 체크박스

    // 신청 방식 관련 = 유,무료
    String Application_Method = "무료";   // 신청방식의 기본 값은 무료
    // 유료일 경우
    String Bank = "";            // 은행명 을 담을 변수
    String Account_number;  // 계좌번호 를 담을 변수
    String Account_holder;  // 예금주 를 담을 변수
    String Participation_fee; // 참가비용 을 담을 변수

    // 선정방법 관련
    String Selection_method = "선착순";    // 기본값은 선착순

    // 신청문의 연락처 설정 부분
    // 앞번호, 중간번호, 끝번호
    String Front_num = "";
    String Middle_num;
    String End_num;

    // 전화번호
    String Call_number;
    // 이메일
    String Front_Email;
    String End_Email="";

    String Email;


    // 위치를 나타내기 위한 3개의 데이터
    // 위치데이터
    String Place_Data = "";

    // 주소 = 주소.getText

    // 상세주소 = EditText




    /** 수정화면에 받아온 데이터를 전역변수로 설정 received_ ~~ */
    String received_no; // 모임 번호
    String received_creator;
    String received_default_imgURI;
    String received_Name;
    String received_Total_People;

    // 모임날짜
    String received_Moim_date_1;
    String received_Moim_date_2;
    String received_Moim_start_time;
    String received_Moim_end_time;

    // 접수날짜
    String received_Moim_apply_date_1;
    String received_Moim_apply_date_2;
    String received_Apply_start_time;
    String received_Apply_end_time;

    // 유 무료
    String received_Application_method;    // 유, 무료

    // 유료의 경우
    String received_Bank;
    String received_Account_number;
    String received_Account_holder;
    String received_Participation_fee;

    String received_Selection_method;     // 선정 방법

    // 전화번호, 이메일
    String received_Call_number;
    String received_Email;

    // 주소 관련
    String received_Place_data;     // 위도 경도 데이터
    String received_Address;
    String received_Detail_address;

    // 모임 간략 설명
    String received_Brief_description;






    // 구글 맵 관련
    Button search_place_button;
    private static final int PLACE_PICKER_REQUEST =5;
    TextView Address_textview;
    EditText DetailAddress_Edit;

    LatLng SEOUL = new LatLng(37.56, 126.97);   // LatLng SEOUL 기본 화면 위치 정보를 저장
    private GoogleMap mMap; // 구글맵 객체 생성
    MarkerOptions markerOptions;    // MarkerOptions 객체를 위치가 선택되면 변경하기 위해 전역변수로 설정한다.



    // 날짜 or 시각 설정에서 구분짓기 위해 필요한 변수들
    static int date_num = 0;  // 날짜 설정 버튼 에서 CalenderActivity 에서 날짜 데이터를 받아오는데, 버튼 별로 구분짓기 위해서
//    static int Clock_num = 0; // 시각 설정 버튼 에서 프래그먼트를 사용 할텐데, 버튼 별로 구분 지을수 있게끔.



    /********** 기본이미지 카메라 갤러리 크롭 변수 *************/
    // 기본이미지 카메라 갤러리 크롭 변수  ------------------------------------------------------------------------------------------------------------------------------------------ 시작
    private ImageView imgMain;  // 모임 기본이미지 선언

    // onActivityResult 에서 받아오는 데이터를 구분짓기 위해 사용되는 변수
    private static final int PICK_FROM_CAMERA = 1;  // 카메라 촬영으로 사진 가져오기
    private static final int PICK_FROM_ALBUM = 2;   // 앨범에서 사진 가져오기
    private static final int CROP_FROM_CAMERA = 3;  // 가져온 사진을 자르기 위한 변수
    private static final int CALANDER_DATE = 4;     // 캘린더 날짜를 받아오는 변수

    private Uri photoUri;   // Uri 를 전역변수로 선언

    // Android MOS(마쉬멜로우 운영체제)부터는 위험한 권한을 사용함에 있어 사용자에게 허락(?)을 받아야 합니다.
    // Camera 와 File System 에 접근하기 위해서는 읽고 / 쓰기 즉, 아래와 같은 권한이 필요합니다.
    private String[] permissions = {Manifest.permission.READ_EXTERNAL_STORAGE,
            Manifest.permission.WRITE_EXTERNAL_STORAGE,
            Manifest.permission.CAMERA};    // 매니페스트 파일에도 권한과 관련된 내용들을 선언해 줘야 한다.

    private static final int MULTIPLE_PERMISSIONS = 101; // 권한 동의 여부 문의 후 CallBack 함수에 쓰일 변수

    private String mCurrentPhotoPath;

    // 기본이미지 카메라 갤러리 크롭 변수  ------------------------------------------------------------------------------------------------------------------------------------------ 끝




    /********** 기본이미지 업로드를 위한 변수 *************/
    int serverResponseCode = 0;
    ProgressDialog progress_dialog;

    String upLoadServerUri = "http://49.247.208.191/AndroidHive/Moim_default_img_Upload.php"; // 업로드할 서버 URI

    // ------- 파일경로 -------
    // 녹스
//    final String uploadFilePath = "/storage/emulated/0/Download/";//경로를 모르겠으면, 갤러리 어플리케이션 가서 메뉴->상세 정보
    // 스마트폰
//    String uploadFilePath = "/mnt/sdcard/NOSTest/"; // 고정되어있는 정보 - 경로를 모르겠으면, 갤러리 어플리케이션 가서 메뉴->상세 정보
    // 스마트폰 임시파일
    String uploadFilePath = "/storage/emulated/0/NOSTest/"; // 고정되어있는 정보 - 경로를 모르겠으면, 갤러리 어플리케이션 가서 메뉴->상세 정보



    String default_image_name;  // 기본 이미지 이름
    // 전송하고자하는 파일 이름
    String uploadFileName;  /** 업로드할 파일의 이름은 계속 변경될 것이다. */










    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_moim_modify_);

        // 리사이클러뷰 에서   데이터 받아오기
        Intent intent = getIntent();    //이전 액티비티에서 수신할 데이터를 받기위해 선언

        // String 변수에게 해당 모임의 데이터들을 받아낸다
         received_no = intent.getStringExtra("NO");  // 모임 번호
         received_creator = intent.getStringExtra("CREATOR");               // 모임 개설자
         received_default_imgURI = intent.getStringExtra("default_imgURI"); // 기본이미지 URI
         received_Name = intent.getStringExtra("NAME");                     // 모임명
         received_Total_People = intent.getStringExtra("TOTAL_PEOPLE");     // 모임 총인원


        // 모임날짜
         received_Moim_date_1 = intent.getStringExtra("Moim_date_1");
         received_Moim_date_2 = intent.getStringExtra("Moim_date_2");
         received_Moim_start_time = intent.getStringExtra("Moim_start_time");
         received_Moim_end_time = intent.getStringExtra("Moim_end_time");

        // 접수날짜
         received_Moim_apply_date_1 = intent.getStringExtra("Moim_apply_date_1");
         received_Moim_apply_date_2 = intent.getStringExtra("Moim_apply_date_2");
         received_Apply_start_time = intent.getStringExtra("Apply_start_time");
         received_Apply_end_time = intent.getStringExtra("Apply_end_time");


        // 유 무료
         received_Application_method = intent.getStringExtra("Application_method");

        // 유료의 경우
         received_Bank  = intent.getStringExtra("Bank");
         received_Account_number  = intent.getStringExtra("Account_number");
         received_Account_holder  = intent.getStringExtra("Account_holder");
         received_Participation_fee = intent.getStringExtra("Participation_fee");


        // 선정 방법
         received_Selection_method = intent.getStringExtra("Selection_method");

        // 전화번호, 이메일
         received_Call_number = intent.getStringExtra("Call_number");
         received_Email = intent.getStringExtra("Email");

        // 주소 관련
         received_Place_data = intent.getStringExtra("Place_data");     // 위도 경도 데이터
         Log.e("받아온 위도 경도 데이터 = ",received_Place_data);
         received_Address = intent.getStringExtra("Address");
         received_Detail_address = intent.getStringExtra("Detail_address");

        // 모임 간략 설명
         received_Brief_description = intent.getStringExtra("Brief_description");






        // Clock_num 초기화
        Clock_num = 0;


        // 모임명 EditText 객체 생성
        Moim_Name_Edit = (EditText)findViewById(R.id.Moim_Name_Edit);
        Moim_Name_Edit.setText(received_Name); /** 모임명 초기화 */

        // 총인원 EditText 객체 생성
        Total_people = (EditText)findViewById(R.id.Total_people);
        Total_people.setText(received_Total_People);     /** 총인원 초기화 */

        // 맵 프래그먼트 객체 생성
        FragmentManager fragmentManager = getFragmentManager();
        MapFragment mapFragment = (MapFragment)fragmentManager.findFragmentById(R.id.map);
        mapFragment.getMapAsync(this);

        // 주소 텍스트뷰 객체 생성
        Address_textview = (TextView)findViewById(R.id.Address_textview);
        // 주소 텍스트뷰 객체 생성
        DetailAddress_Edit = (EditText)findViewById(R.id.DetailAddress_Edit);


        // 장소찾기 버튼 객체 생성
        search_place_button = (Button)findViewById(R.id.search_place_button);
        search_place_button.setOnClickListener(new Button.OnClickListener()     // 장소찾기 버튼 클릭 시
        { @Override
        public void onClick(View view) {
            PlacePicker.IntentBuilder intentBuilder = new PlacePicker.IntentBuilder(); // 구글 플레이스의 PlacePicker 실행
            try {
                Intent intent = intentBuilder.build(Moim_modify_Activity.this);
                startActivityForResult(intent,PLACE_PICKER_REQUEST);
            } catch (GooglePlayServicesRepairableException e) {
                e.printStackTrace();
            } catch (GooglePlayServicesNotAvailableException e) {
                e.printStackTrace();
            }
        }});




        /** ------------------------------------------------------------------ 이미지 삽입 부분. ------------------------------------------------------------------*/

        final String img_NAME = received_default_imgURI;   // 포지션 별 String 형태로 된 이미지 Uri를 가져온다.
        ImageView basicIMG = (ImageView)findViewById(R.id.BasicImage_View);


        Glide.with(getApplicationContext()).load("http://49.247.208.191/AndroidHive/Moim_default_IMG/"+img_NAME).placeholder(R.drawable.noimage).into(basicIMG);

        /** ------------------------------------------------------------------ 이미지 삽입 끝 ------------------------------------------------------------------*/





        /* 기본이미지 추가 버튼 객체 생성 */
        ImageButton Add_BasicImage_Button = (ImageButton)findViewById(R.id.Add_BasicImage_Button);
        // 기본이미지 추가 버튼 클릭 리스너
        Add_BasicImage_Button.setOnClickListener(new Button.OnClickListener() {
            @Override
            public void onClick(View view) {
                checkPermissions(); // 권한 묻기
                initView();
                show();
            }
        });


/**
 *  모임 날짜 설정 부분
 */
// 모임 날짜 설정 관련부분 -----------------------------------------------------------------------------------------------------------------------------------------------------  시작
        // 모임 날짜1 버튼 아이디 설정
        set_date1 = (Button)findViewById(R.id.set_date1);   // 모임일 날짜 설정
        set_date1.setText(received_Moim_date_1); /** 모임 날짜1 초기화 */




        // 모임 날짜1 버튼 클릭 리스너
        set_date1.setOnClickListener(new Button.OnClickListener() {
            @Override
            public void onClick(View view) {
                date_num = 1; // 날짜 설정 을 구분짓기 위한 static 변수

                Intent intent = new Intent(
                        getApplicationContext(), // 현재 화면의 제어권자
                        CalenderActivity.class); // 다음 넘어갈 클래스 지정
                startActivityForResult(intent,CALANDER_DATE); // 다음 화면으로 넘어간다
            }
        }); // 날짜 선택은 CalenderActivity 에서 선택되어 데이터가 되돌아와서 설정된다.

        // 모임 시각1 버튼 아이디 설정
        set_clock1 = (Button)findViewById(R.id.set_clock1);
        set_clock1.setText(received_Moim_start_time);/** 모임 시각1 초기화 */

        // 모임 시각1 버튼 클릭 리스너
        set_clock1.setOnClickListener(new Button.OnClickListener() {
            @Override
            public void onClick(View view) {
                Clock_num = 1;  // 시각 설정 프래그먼트를 구분짓기 위한 static 변수
                // Clock_num = 1 이면 ~부터의 시각을 설정하기 위한 시각 설정 프래그먼트가 열린다.

                TimePickerFragment newFragment = new TimePickerFragment();
                //show : fragmentManager에 추가된 대화상자 출력
                newFragment.show(getFragmentManager(), "TimePicker");   // 프래그먼트 부른다.
            }
        });
//        --------------------------------------------------- 모임 날짜 설정 첫줄 (Line ~부터) 끝

//        ---------------------------------------------------  (Line ~까지) 시작

        // 모임이 하루 이상일 경우, 사용하는 CheckBox
        Moim_date_over = (CheckBox) findViewById(R.id.Moim_date_over) ;
        // 모임 날짜2 버튼 아이디 설정 = 위의 체크박스가 클릭 되기 전까지는 보이지 않는다.
        set_date2 = (Button)findViewById(R.id.set_date2);   // 모임일 날짜 설정

        if(received_Moim_date_2.equals("")){    // 모임 날짜설정2 가 공백 일 경우 데이터가 없다는 의미므로

            // 공백은 데이터가 없다. 그러므로 체크박스를 누를필요가 없다.
            Moim_date_over.setChecked(false);   // 체크박스 해제
            // 데이터가 잇으면 체크박스를 누르고 데이터를 저장했다는 의미이다.
            set_date2.setText("모임 날짜설정");   /** 모임 날짜2 초기화 */
        }
        else {  // 모임 날짜설정2 가 공백이 아니므로

            // 체크박스를 눌렀었고
            Moim_date_over.setChecked(true);   // 체크박스 설정
            set_date2.setVisibility(View.VISIBLE);  // 원래 보이지 않던 모임시각설정버튼2 가 보인다.
            // 데이터를 입력했었다는 의미
            set_date2.setText(received_Moim_date_2);    /** 모임 날짜2 초기화 */
        }

        // 체크박스가 클릭되면
        Moim_date_over.setOnClickListener(new CheckBox.OnClickListener() {
            @Override
            public void onClick(View v) {   // 박스 클릭하면
                if (((CheckBox)v).isChecked()) {    // 체크박스 선택 0
                    set_date2.setVisibility(View.VISIBLE);    // 원래 보이지 않던 모임시각설정버튼2 가 보인다.
                } else {                            // 체크방스 선택 X
                    set_date2.setVisibility(View.INVISIBLE);  // 모임 시각 설정버튼2 보이지 않는다.
                }
            }
        });




        // 모임 날짜2 버튼 클릭 리스너
        set_date2.setOnClickListener(new Button.OnClickListener() {
            @Override
            public void onClick(View view) {
                date_num = 2; // 날짜 설정 을 구분짓기 위한 static 변수

                Intent intent = new Intent(
                        getApplicationContext(), // 현재 화면의 제어권자
                        CalenderActivity.class); // 다음 넘어갈 클래스 지정
                startActivityForResult(intent,CALANDER_DATE); // 다음 화면으로 넘어간다
            }
        });


        // 모임 시각2 버튼 아이디 설정
        set_clock2 = (Button)findViewById(R.id.set_clock2);
        set_clock2.setText(received_Moim_end_time);/** 모임 시각2 초기화 */

        // 모임 시각2 버튼  클릭 리스너 2
        set_clock2.setOnClickListener(new Button.OnClickListener() {
            @Override
            public void onClick(View view) {
                Clock_num = 2;  // 1번 시각 설정
                // Clock_num = 2 이면 ~까지의 시각을 설정하기 위한 시각 설정 프래그먼트가 열린다.

                TimePickerFragment newFragment = new TimePickerFragment();
                //show : fragmentManager에 추가된 대화상자 출력
                newFragment.show(getFragmentManager(), "TimePicker");
            }
        });
// 모임 날짜 설정 관련부분 -----------------------------------------------------------------------------------------------------------------------------------------------------  끝


/**
 * 접수 기간 설정 부분
 */
// 접수 기간 설정 관련부분 -----------------------------------------------------------------------------------------------------------------------------------------------------  시작
// 접수 날짜1 버튼 아이디 설정
        set_apply_date1 = (Button)findViewById(R.id.set_apply_date1);   // 모임일 날짜 설정
        set_apply_date1.setText(received_Moim_apply_date_1);/** 접수 날짜1 초기화 */


        // 접수 날짜1 버튼 클릭 리스너
        set_apply_date1.setOnClickListener(new Button.OnClickListener() {
            @Override
            public void onClick(View view) {
                date_num = 3; // 날짜 설정 을 구분짓기 위한 static 변수

                Intent intent = new Intent(
                        getApplicationContext(), // 현재 화면의 제어권자
                        CalenderActivity.class); // 다음 넘어갈 클래스 지정
                startActivityForResult(intent,CALANDER_DATE); // 다음 화면으로 넘어간다
            }
        }); // 날짜 선택은 CalenderActivity 에서 선택되어 데이터가 되돌아와서 설정된다.


        // 접수 시각1 버튼 아이디 설정
        set_apply_clock1 = (Button)findViewById(R.id.set_apply_clock1);
        set_apply_clock1.setText(received_Apply_start_time);/** 접수 시각1 초기화 */

        // 접수 시각1 버튼 클릭 리스너
        set_apply_clock1.setOnClickListener(new Button.OnClickListener() {
            @Override
            public void onClick(View view) {
                Clock_num = 3;  // 시각 설정 프래그먼트를 구분짓기 위한 static 변수
                // Clock_num = 1 이면 ~부터의 시각을 설정하기 위한 시각 설정 프래그먼트가 열린다.

                TimePickerFragment newFragment = new TimePickerFragment();
                //show : fragmentManager에 추가된 대화상자 출력
                newFragment.show(getFragmentManager(), "TimePicker");   // 프래그먼트 부른다.
            }
        });
//        --------------------------------------------------- 접수 기간 설정 첫줄 (~부터) 끝

//        ---------------------------------------------------  (~까지) 시작
        // 접수기간 이 하루 이상일 경우, 사용하는 CheckBox
        apply_date_over = (CheckBox) findViewById(R.id.apply_date_over) ;
        // 접수 날짜2 버튼 아이디 설정 = 위의 체크박스가 클릭 되기 전까지는 보이지 않는다.
        set_apply_date2 = (Button)findViewById(R.id.set_apply_date2);   // 접수일 날짜 설정

        if(received_Moim_apply_date_2.equals("")){  // 접수 날짜 2가 공백이라면
            // 공백은 데이터가 없다. 그러므로 체크박스를 누를필요가 없다.
            apply_date_over.setChecked(false);   // 체크박스 해제
            // 데이터가 잇으면 체크박스를 누르고 데이터를 저장했다는 의미이다.
            set_apply_date2.setText("접수 날짜설정");   /** 모임 날짜2 초기화 */
        }
        else {  // 모임 날짜설정2 가 공백이 아니므로

            // 체크박스를 눌렀었고
            apply_date_over.setChecked(true);   // 체크박스 설정
            set_apply_date2.setVisibility(View.VISIBLE);  // 원래 보이지 않던 접수날짜 설정 버튼 2 가 보인다.
            // 데이터를 입력했었다는 의미
            set_apply_date2.setText(received_Moim_apply_date_2);    /** 모임 날짜2 초기화 */
        }


        // 체크박스가 클릭되면
        apply_date_over.setOnClickListener(new CheckBox.OnClickListener() {
            @Override
            public void onClick(View v) {   // 박스 클릭하면
                if (((CheckBox)v).isChecked()) {    // 체크박스 선택 0
                    set_apply_date2.setVisibility(View.VISIBLE);    // 원래 보이지 않던 접수날짜 설정 버튼 2 가 보인다.
                } else {                            // 체크박스 선택 X
                    set_apply_date2.setVisibility(View.INVISIBLE);  // 접수날짜 설정 버튼 2 가 보이지 않는다.
                }
            }
        });

        set_apply_date2.setText(received_Moim_apply_date_2);/** 접수 날짜2 초기화 */

        // 접수 날짜2 버튼 클릭 리스너
        set_apply_date2.setOnClickListener(new Button.OnClickListener() {
            @Override
            public void onClick(View view) {
                date_num = 4; // 날짜 설정 을 구분짓기 위한 static 변수

                Intent intent = new Intent(
                        getApplicationContext(), // 현재 화면의 제어권자
                        CalenderActivity.class); // 다음 넘어갈 클래스 지정
                startActivityForResult(intent,CALANDER_DATE); // 다음 화면으로 넘어간다
            }
        });


        // 접수 시각2 버튼 아이디 설정
        set_apply_clock2 = (Button)findViewById(R.id.set_apply_clock2);
        set_apply_clock2.setText(received_Apply_end_time);/** 접수 시각2 초기화 */

        // 접수 시각2 버튼  클릭 리스너 2
        set_apply_clock2.setOnClickListener(new Button.OnClickListener() {
            @Override
            public void onClick(View view) {
                Clock_num = 4;  // 1번 시각 설정
                // Clock_num = 2 이면 ~까지의 시각을 설정하기 위한 시각 설정 프래그먼트가 열린다.

                TimePickerFragment newFragment = new TimePickerFragment();
                //show : fragmentManager에 추가된 대화상자 출력
                newFragment.show(getFragmentManager(), "TimePicker");
            }
        });

// 접수 기간 설정 관련부분 -----------------------------------------------------------------------------------------------------------------------------------------------------  끝





/**
 * Drawer 부분
 */
// Drawer 부분  -----------------------------------------------------------------------------------------------------------------------------------------------------  시작
        // 여기서 부터 모두 복사해야 함.
        // 복사 시작
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
                            Toast.makeText(Moim_modify_Activity.this, "로그인 후에 확인 가능 합니다", Toast.LENGTH_SHORT).show();
                        }
                        else{
                            Intent intent = new Intent(
                                    getApplicationContext(), // 현재 화면의 제어권자
                                    Profle_Activity.class); // 다음 넘어갈 클래스 지정 - Profle_Activity
                            startActivity(intent); // 다음 화면으로 넘어간다
                        }break;
                    case R.id.nav_sub_menu_item02:  // 즐겨찾기 버튼
                        if(Login_State == 0 ){// 아직 로그인 안했을 때
                            Toast.makeText(Moim_modify_Activity.this, "로그인 후에 확인 가능 합니다", Toast.LENGTH_SHORT).show();
                        }
                        else{
                            Intent intent = new Intent(
                                    getApplicationContext(), // 현재 화면의 제어권자
                                    BookMarkActivity.class); // 다음 넘어갈 클래스 지정 - BookMarkActivity
                            startActivity(intent); // 다음 화면으로 넘어간다
                        }break;

                    case R.id.nav_sub_menu_item03:  // 내가 만든 모임 버튼
                        if(Login_State == 0 ){// 아직 로그인 안했을 때
                            Toast.makeText(Moim_modify_Activity.this, "로그인 후에 확인 가능 합니다", Toast.LENGTH_SHORT).show();
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
                            Toast.makeText(Moim_modify_Activity.this, "로그인 후에 확인 가능 합니다", Toast.LENGTH_SHORT).show();
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
                            Toast.makeText(Moim_modify_Activity.this, "로그인 후에 확인 가능 합니다", Toast.LENGTH_SHORT).show();
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
        // 복사 끝
// Drawer 부분  -------------------------------------------------------------------------------------------------------------------------------------------------------  끝


/**
 * 유,무료 신청 부분 = 신청방식 설정
 */
// 유,무료 신청 부분  -------------------------------------------------------------------------------------------------------------------------------------------------------  끝
        // 라디오 그룹 객체 선언
        RadioGroup rg = (RadioGroup)findViewById(R.id.radioGroup1);
        RadioButton Price_free = (RadioButton)findViewById(R.id.Price_free);                    // 무료 버튼 객체
        final RadioButton Price_charged_Object = (RadioButton)findViewById(R.id.Price_charged); // 유료 버튼 객체


        // 유료신청 계좌입금 부분 레이아웃 객체 선언
        final LinearLayout Charged_info = (LinearLayout)findViewById(R.id.Charged_info);        // 레이아웃
        final TextView Charged_TextView = (TextView)findViewById(R.id.Charged_textView);        // 텍스트 뷰


        if(received_Application_method.equals("무료")){   // 무료 일 경우
            Price_free.setChecked(true);
        }
        else{   // 유료 일 경우
            Price_charged_Object.setChecked(true);
            Charged_info.setVisibility(View.VISIBLE);   // 원래 유료 신청 정보는 안보이는 상태인데 -> 보이게 변경한다.
            Charged_TextView.setVisibility(View.VISIBLE);   // 원래 유료 신청 정보는 안보이는 상태인데 -> 보이게 변경한다.
        }



        // 라디오 버튼 유,무료 선택 했을때 다르게 나타내게끔.
        rg.setOnCheckedChangeListener(new RadioGroup.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(RadioGroup group, int checkedId) {
                if(checkedId == Price_charged){    // 유료 신청 버튼 클릭시
                    Charged_info.setVisibility(View.VISIBLE);   // 원래 유료 신청 정보는 안보이는 상태인데 -> 보이게 변경한다.
                    Charged_TextView.setVisibility(View.VISIBLE);   // 원래 유료 신청 정보는 안보이는 상태인데 -> 보이게 변경한다.
                    Application_Method = "유료";
                }
                else{   // 무료 신청 버튼 클릭 시
                    Charged_info.setVisibility(View.GONE);  // 다시 안보이게 변경
                    Charged_TextView.setVisibility(View.GONE);   // 원래 유료 신청 정보는 안보이는 상태인데 -> 보이게 변경한다.
                    Application_Method = "무료";
                }
            }
        });
// 유,무료 신청 부분  -------------------------------------------------------------------------------------------------------------------------------------------------------  끝


/**
 * 은행 스피너 부분 - 은행을 리스트화 한 뒤에 선택하게 끔
 */
        // 스피너 객체 선언
        final Spinner Bank_spinner = (Spinner)findViewById(R.id.Bank_spinner);

        String[] list2 = new String[9]; // 스피너 리스트 담기 - 리스트 크기는 9(0~8)
        list2[0] = "은행 선택";
        list2[1] = "국민은행";
        list2[2] = "산업은행";
        list2[3] = "기업은행";
        list2[4] = "수협중앙회";
        list2[5] = "우리은행";
        list2[6] = "SC은행";
        list2[7] = "대구은행";
        list2[8] = "부산은행";

        //어레이 어댑터(ArrayAdapter) 사용
        ArrayAdapter spinnerAdapter;
        spinnerAdapter = new ArrayAdapter(this, R.layout.support_simple_spinner_dropdown_item, list2);
        Bank_spinner.setAdapter(spinnerAdapter);

        // 확인화면에서 가져온 은행데이터를 가지고
        // 만약에 '국민은행'이라면
        // 반복문을 통해 국민은행과 일치하는 데이터를 list2 에서 찾으면, 스피너를 '국민은행'으로 설정하고
        // Bank 데이터에 received_Bank 를 삽입한다.
        for(int i=0 ; i < 9 ; i++){
            if(list2[i].equals(received_Bank)){
                // 비교하면서 은행이 일치하면
                Bank_spinner.setSelection(i);
                Bank = received_Bank;
                break;
            }
        }


        //스피너 아이템 클릭시에 발생되는 클릭 리스너
        Bank_spinner.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {   // 아이템이 선택되면 나오는 리스너
                if(position == 0){  // 포지션 0 이면 제낀다
                    Bank = "";  // String Bank 를 비워 버린다.
                }else{  // 포지션이 1~8 사이의 아이템 클릭시 이벤트
                    Toast.makeText(Moim_modify_Activity.this,Bank_spinner.getItemAtPosition(position)+" 선택",Toast.LENGTH_SHORT).show();    // 해당 클릭 아이이템의 텍스트를 토스트로 출력
                    Bank = Bank_spinner.getItemAtPosition(position).toString(); // 바뀔 때 마다 은행명을 Bank 변수에 담는다.
                    Log.e("선택된 은행명 = ",Bank);   // 선택된 은행명 확인
                }
            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {  // 아이템이 선택되지 않으면 나오는 리스너
            }
        });
// 은행 스피너 부문  -------------------------------------------------------------------------------------------------------------------------------------------------------  끝


        /** 계좌번호, 예금주, 참가비용 설정 */
        EditText 계좌번호 = (EditText)findViewById(R.id.Account_Number_Edit);   // 계좌번호
        계좌번호.setText(received_Account_number);
        EditText 예금주 = (EditText)findViewById(R.id.Account_Owner_Edit);      // 예금주
        예금주.setText(received_Account_holder);
        EditText 참가비용 = (EditText)findViewById(R.id.Moim_Price_Edit);       // 참가비용
        참가비용.setText(received_Participation_fee);





/**
 * 신청자 선정방법 부분 - 라디오 버튼으로 선택하게끔 설정
 */
// 선정방법 부문  -------------------------------------------------------------------------------------------------------------------------------------------------------  끝

        // 라디오 그룹 객체 선언
        RadioGroup Choice_how = (RadioGroup)findViewById(R.id.radioGroup2);

        // 선착순 버튼
        RadioButton By_order_of_arrival = (RadioButton)findViewById(R.id.By_order_of_arrival);
        // 개설자 선정 버튼
        final RadioButton My_pickup = (RadioButton)findViewById(R.id.My_pickup);


        if(received_Selection_method.equals("선착순")){   // 선착순 일 경우
            By_order_of_arrival.setChecked(true);
        }
        else{   // 유료 일 경우
            My_pickup.setChecked(true);
        }


        // 라디오 버튼 선정방법 선택 했을때 다르게 나타내게끔.
        Choice_how.setOnCheckedChangeListener(new RadioGroup.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(RadioGroup group, int checkedId) {
                if(checkedId == R.id.My_pickup){    // 개설자 선정 버튼 클릭시
                    Toast.makeText(Moim_modify_Activity.this,"개설자 선정 선택", Toast.LENGTH_SHORT).show();
                    Selection_method = "개설자 선정";
                }
                else{   // 무료 신청 버튼 클릭 시
                    Toast.makeText(Moim_modify_Activity.this,"선착순 선택", Toast.LENGTH_SHORT).show();
                    Selection_method = "선착순";
                }
            }
        });
// 선정방법 부문  -------------------------------------------------------------------------------------------------------------------------------------------------------  끝


/**
 * 맵과 스크롤뷰 터치 겹칩 해결 부분
 */
// 맵과 스크롤뷰 터치 겹칩 해결 부분 -----------------------------------------------------------------------------------------------------------------------------------------------------  끝

        // 스크롤뷰 객체 선언
        final ScrollView mainScrollView = (ScrollView) findViewById(R.id.main_scrollview);
        // 이미지뷰 객세선언 = 투명한 이미지뷰
        ImageView transparentImageView = (ImageView) findViewById(R.id.transparent_image);

        // 투명한 이미지뷰 터치리스너
        transparentImageView.setOnTouchListener(new View.OnTouchListener() {
            @Override
            public boolean onTouch(View v, MotionEvent event) {
                int action = event.getAction();
                switch (action) {
                    case MotionEvent.ACTION_DOWN:
                        // Disallow ScrollView to intercept touch events.
                        mainScrollView.requestDisallowInterceptTouchEvent(true);    // 스크롤뷰 터치 XXX
                        // Disable touch on transparent view
                        return false;

                    case MotionEvent.ACTION_UP:
                        // Allow ScrollView to intercept touch events.
                        mainScrollView.requestDisallowInterceptTouchEvent(false);   // 스크롤뷰 터치 OOO
                        return true;

                    case MotionEvent.ACTION_MOVE:
                        mainScrollView.requestDisallowInterceptTouchEvent(true);    // 스크롤뷰 터치 XXX
                        return false;

                    default:
                        return true;
                }
            }
        });
// 맵과 스크롤뷰 터치 겹칩 해결 부분  -----------------------------------------------------------------------------------------------------------------------------------------------------  끝




/**
 *  신청 문의 연락처 작성 1. 전화번호    2. 이메일
 */
 // 전화번호 스피너------------------------------------------------------------------------------------------------------------------------------------------------------------------------------  시작

        // 휴대폰 번호를  분리자(-) 로 나누어 String 배열 arr 에 따로 담는다.
        String[] arr = received_Call_number.split("-");

        Log.e("앞 번호 = ", arr[0] );
        Log.e("중간 번호 = ", arr[1] );
        Log.e("끝 번호 = ", arr[2] );


        // 중간번호 설정
        EditText 중간번호 = (EditText)findViewById(R.id.Middle_num);   // 중간번호
        중간번호.setText(arr[1]);
        // 끝 번호 설정
        EditText 끝번호 = (EditText)findViewById(R.id.end_num);      // 끝번호
        끝번호.setText(arr[2]);


        // 앞 번호 스피너 객체 선언
        final Spinner Phone_spinner = (Spinner)findViewById(R.id.Phone_spinner);

        String[] Phone_list2 = new String[21]; // 스피너 리스트 담기 - 리스트 크기는 21(0~20)
        Phone_list2[0] = "번호 선택";
        Phone_list2[1] = "010";
        Phone_list2[2] = "011";
        Phone_list2[3] = "015";
        Phone_list2[4] = "016";
        Phone_list2[5] = "017";
        Phone_list2[6] = "018";
        Phone_list2[7] = "019";
        Phone_list2[8] = "02";
        Phone_list2[9] = "031";
        Phone_list2[10] = "032";
        Phone_list2[11] = "033";
        Phone_list2[12] = "041";
        Phone_list2[13] = "042";
        Phone_list2[14] = "043";
        Phone_list2[15] = "044";
        Phone_list2[16] = "051";
        Phone_list2[17] = "052";
        Phone_list2[18] = "053";
        Phone_list2[19] = "054";
        Phone_list2[20] = "055";

        //어레이 어댑터(ArrayAdapter) 사용
        ArrayAdapter spinnerAdapter2;
        spinnerAdapter2 = new ArrayAdapter(this, R.layout.support_simple_spinner_dropdown_item, Phone_list2);
        Phone_spinner.setAdapter(spinnerAdapter2);


        // arr[0] 에는 휴대폰 번호 앞자리가 담겨 있으며
        // 만약에 '010'이라면
        // 반복문을 통해 '010'과 일치하는 데이터를 Phone_list2 에서 찾으면, 스피너를 '010'으로 설정하고
        // Front_num 데이터에 arr[0] 를 삽입한다.
        for(int i=0 ; i < 20 ; i++){
            if(Phone_list2[i].equals(arr[0])) {
                // 비교하면서 앞 번호가 일치하면
                Phone_spinner.setSelection(i);      // 스피너는 i 따라 기본으로 선택된다.
                Front_num = arr[0]; // 휴대폰 번호 앞자리은 arr[0] 을 Front_num 에 담는다.
                break;
            }
        }


        //스피너 아이템 클릭시에 발생되는 클릭 리스너
        Phone_spinner.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {   // 아이템이 선택되면 나오는 리스너
                if(position == 0){  // 포지션 0 이면 제낀다
                    Front_num = "";
                }else{  // 포지션이 1~8 사이의 아이템 클릭시 이벤트
                    Toast.makeText(Moim_modify_Activity.this,Phone_spinner.getItemAtPosition(position)+" 선택",Toast.LENGTH_SHORT).show();    // 해당 클릭 아이이템의 텍스트를 토스트로 출력
                    Front_num = Phone_spinner.getItemAtPosition(position).toString(); // 앞번호 데이터를 담는다.
                }
            }
            @Override
            public void onNothingSelected(AdapterView<?> parent) {  // 아무것도 선택이 안되었을때
            }
        });
// 전화번호 스피너------------------------------------------------------------------------------------------------------------------------------------------------------------------------------  끝

// 이메일 스피너------------------------------------------------------------------------------------------------------------------------------------------------------------------------------  시작

        // 이메일  분리자(@) 로 나누어 String 배열 email_arr 에 따로 담는다.
        String[] email_arr = received_Email.split("@"); // arr 은 가변적 자동으로 담긴다.

        Log.e("앞 이메일 아이디 = ", email_arr[0] );
        Log.e("뒤 이메일 사이트 = ", email_arr[1] );
        Log.e("이메일 배열 길이 = ", "" + email_arr.length );  // 배열 길이


        // 이메일 아이디 EditText 객체 설정
        EditText 이메일 = (EditText)findViewById(R.id.Front_Email);   // 이메일
        // 이메일 아이디 초기 값 설정
        이메일.setText(email_arr[0]);

        // 이메일 스피너 객체 선언
        final Spinner Email_spinner = (Spinner)findViewById(R.id.Email_spinner);

        String[] Email_list = new String[14]; // 스피너 리스트 담기 - 리스트 크기는 14(0~13)
        Email_list[0] = "이메일 선택";
        Email_list[1] = "naver.com";
        Email_list[2] = "gmail.com";
        Email_list[3] = "hanmail.net";
        Email_list[4] = "nate.com";
        Email_list[5] = "daum.net";
        Email_list[6] = "hotmail.com";
        Email_list[7] = "empal.com";
        Email_list[8] = "korea.com";
        Email_list[9] = "dreamwiz,com";
        Email_list[10] = "paran.com";
        Email_list[11] = "yahoo.com";
        Email_list[12] = "hanmir.com";
        Email_list[13] = "msn.com";


        //어레이 어댑터(ArrayAdapter) 사용
        ArrayAdapter spinnerAdapter3;
        spinnerAdapter3 = new ArrayAdapter(this, R.layout.support_simple_spinner_dropdown_item, Email_list);
        Email_spinner.setAdapter(spinnerAdapter3);


        // email_arr[1] 에는 이메일 아이디가 담겨 있으며
        // 만약에 'naver.com'이라면
        // 반복문을 통해 'naver.com'과 일치하는 데이터를 Email_list 에서 찾으면, 스피너를 'naver.com'으로 설정하고
        // End_Email 데이터에 email_arr[1] 를 삽입한다.
        for(int i=0 ; i < 13 ; i++){
            if(Email_list[i].equals(email_arr[1])){
                // 비교하면서 앞 번호가 일치하면
                Email_spinner.setSelection(i);      // 스피너는 i 따라 기본으로 선택된다.
                End_Email = email_arr[1];   // 이메일 사이트 데이터인 email_arr[1] 을 End_Email 에 담는다.
                break;
            }
        }


        //스피너 아이템 클릭시에 발생되는 클릭 리스너
        Email_spinner.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {   // 아이템이 선택되면 나오는 리스너
                if(position == 0){  // 포지션 0 이면 제낀다
                    End_Email = "";
                }else{  // 포지션이 1~8 사이의 아이템 클릭시 이벤트
                    Toast.makeText(Moim_modify_Activity.this,Email_spinner.getItemAtPosition(position)+" 선택",Toast.LENGTH_SHORT).show();    // 해당 클릭 아이이템의 텍스트를 토스트로 출력
                    End_Email = Email_spinner.getItemAtPosition(position).toString();
                }
            }
            @Override
            public void onNothingSelected(AdapterView<?> parent) {  // 아무것도 선택이 안되었을때
            }
        });
// 이메일 스피너--------------------------------------------------------------------------------------------------------------------------------------------------------------------  끝


        // 확인화면에서 입력되어 있는 모임소개 글을 미리 뿌려준다.
        EditText 간단모임내용 = (EditText)findViewById(R.id.explainContent);   // 간단모임내용 객체 선언
        간단모임내용.setText(received_Brief_description);     // 모임 내용 설정.



/** ------------------------------------------------------------------ 지도 삽입 부분. ------------------------------------------------------------------*/

        Address_textview.setText(received_Address);  // 주소
        DetailAddress_Edit.setText(received_Detail_address);    // 상세주소

/** ------------------------------------------------------------------ 지도 삽입 끝. ------------------------------------------------------------------*/


    /**
     * 모임 취소 버튼 부분
     */
        // 모임 취소 버튼 객체 생성
        Button Cancel_Button = (Button)findViewById(R.id.Cancel_Button);
        Cancel_Button.setOnClickListener(new Button.OnClickListener() {
            @Override
            public void onClick(View view) {

                back_button_alertshow();

            }
        });









        /**
         * 모임 등록 버튼 부분
         */
        // 모임 등록 버튼 객체 생성
        Button Moim_Register_button = (Button)findViewById(R.id.Moim_Register_button);
        Moim_Register_button.setOnClickListener(new Button.OnClickListener() {
            @Override
            public void onClick(View view) {


                // 이미지 전달 하기 위한 코드 필요
                uploadFileName = default_image_name; // 기본이미지 이름을 업로드파일이름에 적용시킨다.

                if(uploadFileName== null){    // 이미지 파일 이름 = null 이면 실행을 이미지 업로드 하는 코드를 실행을 시키지 말아야 한다.

                }
                else{
                    // 이미지 전달을 위한 작업 스레드 생성
                    new Thread(new Runnable() {
                        public void run() {
                            uploadFile(uploadFilePath + "" + uploadFileName);   // 업로드 경로 + 파일 이름. - 이걸 전달함으로써 업로드 된다.
                        }
                    }).start();
                }


                // 저장할 이미지의 이름 설정
                String Moim_default_imgName = uploadFileName;

                if(Moim_default_imgName == null){   // 이미지가 선택된 것이 없으면
                    // null 이니까 기본이미지 uri 에 있던 이미지 이름을 집어 넣어야 한다.
                     Moim_default_imgName = received_default_imgURI;
                }
                else{
                     Moim_default_imgName = uploadFileName;
                }





                // 데이터 전달 하기 위한 코드 필요
                // 1. 개설자 = 현재 접속자 아이디 , 2. 모임명 = Moim_Name_Edit 의 텍스트 , 3. 기본이미지이름 = uploadFileName , 4. 모임총인원 = Total_people 의 텍스트
                String Moim_Creator = receive0;                                 // 모임 개설자 = 현재 접속자 아이디, receive0 에는 현재 접속자 아이디가 들어있다. (자동으로 들어감)
                String Moim_name = Moim_Name_Edit.getText().toString();         // 모임명 = 모임 에디트 텍스트에 적힌 내용을 전달 받는다.

                /** 1번째 예외처리 = 모임명 */
                if(Moim_name.equals("")){  // 모임명 (필수) 비어있으면
                    Toast.makeText(Moim_modify_Activity.this,"모임명을 입력해 주세요.",Toast.LENGTH_SHORT).show();    // 해당 클릭 아이이템의 텍스트를 토스트로 출력
                }


                else{

                    // 모임 날짜 데이터 가져오기 5 6 7 8
                    String Moim_date_1 = set_date1.getText().toString();                      // 모임날짜1 = 모임 에디트 텍스트에 적힌 내용을 전달 받는다.
                    String Moim_date_2 = set_date2.getText().toString();         // 모임날짜2 = 모임 에디트 텍스트에 적힌 내용을 전달 받는다.
                    String Moim_start_time = set_clock1.getText().toString();         // 모임명 = 모임 에디트 텍스트에 적힌 내용을 전달 받는다.
                    String Moim_end_time = set_clock2.getText().toString();         // 모임명 = 모임 에디트 텍스트에 적힌 내용을 전달 받는다.

                    /** 2번째 예외처리 = 모임날짜 설정 */
                    // 4개의 버튼이 설정이 안되어 있을때의 예외처리
                    if(Moim_date_1.equals("모임 날짜설정")){  // 모임 날짜 1 (필수) 이 선택이 안되어 있으면
                        Toast.makeText(Moim_modify_Activity.this,"모임 날짜를 선택하여 주세요.",Toast.LENGTH_SHORT).show();
                    }
                    else if(Moim_start_time.equals("시작 시간 설정")){    // 시작 시간이 설정 (필수) 이 안되어 있으면
                        Toast.makeText(Moim_modify_Activity.this,"모임 시작 시간을 설정해 주세요.",Toast.LENGTH_SHORT).show();
                    }
                    else if(Moim_end_time.equals("종료 시간 설정")){      // 종료 시간이 설정 (필수) 이 안되어 있으면
                        Toast.makeText(Moim_modify_Activity.this,"모임 종료 시간을 설정해 주세요.",Toast.LENGTH_SHORT).show();
                    }
                    else{// 2번째 예외처리 통과

                        if(Moim_date_2.equals("모임 날짜설정")){       // 모임 날짜 2 가 설정이 안되어 있으면
                            Moim_date_2 = "";   // 빈칸으로 둔다.
                            Log.e("모임날짜2 = ","비어있음."+Moim_date_2);  // 모임날짜가 비어있으면 비어있음 만 나온다
                        }

                        // 접수 날짜 데이터 가져오기 9 10 11 12
                        String Moim_apply_date_1 = set_apply_date1.getText().toString();                      // 모임날짜1 = 모임 에디트 텍스트에 적힌 내용을 전달 받는다.
                        String Moim_apply_date_2 = set_apply_date2.getText().toString();         // 모임날짜2 = 모임 에디트 텍스트에 적힌 내용을 전달 받는다.
                        String Apply_start_time = set_apply_clock1.getText().toString();         // 모임명 = 모임 에디트 텍스트에 적힌 내용을 전달 받는다.
                        String Apply_end_time = set_apply_clock2.getText().toString();         // 모임명 = 모임 에디트 텍스트에 적힌 내용을 전달 받는다.

                        /** 3번째 예외처리 = 접수날짜 설정 */
                        if(Moim_apply_date_1.equals("접수 날짜설정")){  // 접수 날짜 1 (필수) 이 선택이 안되어 있으면
                            Toast.makeText(Moim_modify_Activity.this,"접수 날짜를 선택하여 주세요.",Toast.LENGTH_SHORT).show();
                        }
                        else if(Apply_start_time.equals("시작 시간 설정")){    // 시작 시간이 설정 (필수) 이 안되어 있으면
                            Toast.makeText(Moim_modify_Activity.this,"접수 시작 시간을 설정해 주세요.",Toast.LENGTH_SHORT).show();
                        }
                        else if(Apply_end_time.equals("종료 시간 설정")){      // 종료 시간이 설정 (필수) 이 안되어 있으면
                            Toast.makeText(Moim_modify_Activity.this,"접수 종료 시간을 설정해 주세요.",Toast.LENGTH_SHORT).show();
                        }
                        else {

                            if(Moim_apply_date_2.equals("접수 날짜설정")){       // 접수 날짜 2 가 설정이 안되어 있으면
                                Moim_apply_date_2 = "";   // 빈칸으로 둔다.
                                Log.e("접수날짜2 = ","비어있음."+Moim_apply_date_2);  // 접수 날짜가 비어있으면 비어있음 만 나온다
                            }

                            /**
                             * 신청 방식은 변수 Application_Method (유료,무료)  를 그대로 집어 넣는다. 13
                             */

                            /**
                             * 유료인 경우 넣을 변수 - 은행명, 계좌번호, 예금주, 참가비용 = 유료가 아닌 경우에는 Null 값이 들어 가야 정상이다.  14 15 16 17
                             */
                            Log.e("신청방식 = ",Application_Method);// 신청방식 확인
                            if(Application_Method.equals("유료")){  // 은행명이 유료 인 경우
                                /** 은행명은 Bank 변수를 그대로 사용한다.  14  */
                                EditText 계좌번호 = (EditText)findViewById(R.id.Account_Number_Edit);   // 계좌번호 15
                                Account_number = 계좌번호.getText().toString();
                                EditText 예금주 = (EditText)findViewById(R.id.Account_Owner_Edit);      // 예금주 16
                                Account_holder = 예금주.getText().toString();
                                EditText 참가비용 = (EditText)findViewById(R.id.Moim_Price_Edit);       // 참가비용 17
                                Participation_fee = 참가비용.getText().toString();

                                /** 4 번째 예외처리 = 신청방식 유료일 경우 */
                                if(Bank.equals("")){
                                    Toast.makeText(Moim_modify_Activity.this,"은행을 설정해 주세요.",Toast.LENGTH_SHORT).show();    // 해당 클릭 아이이템의 텍스트를 토스트로 출력
                                }
                                else if(Account_number.equals("")){    // 시작 시간이 설정 (필수) 이 안되어 있으면
                                    Toast.makeText(Moim_modify_Activity.this,"계좌번호를 입력하세요.",Toast.LENGTH_SHORT).show();    // 해당 클릭 아이이템의 텍스트를 토스트로 출력
                                }
                                else if(Account_holder.equals("")){    // 시작 시간이 설정 (필수) 이 안되어 있으면
                                    Toast.makeText(Moim_modify_Activity.this,"예금주를 입력하세요.",Toast.LENGTH_SHORT).show();    // 해당 클릭 아이이템의 텍스트를 토스트로 출력
                                }
                                else if(Participation_fee.equals("")){    // 시작 시간이 설정 (필수) 이 안되어 있으면
                                    Toast.makeText(Moim_modify_Activity.this,"참가비용을 입력하세요.",Toast.LENGTH_SHORT).show();    // 해당 클릭 아이이템의 텍스트를 토스트로 출력
                                }
                                else{   // 예외 무사 통과의 경우 진행

                                    Log.e("은행명 = ",Bank+"");
                                    Log.e("계좌번호 = ",Account_number+"");
                                    Log.e("예금주 = ",Account_holder+"");
                                    Log.e("참가비용 = ",Participation_fee+"");

                                    // 총인원
                                    String Moim_Total_people = Total_people.getText().toString();   // 총인원 = 총인원 에디트 텍스트에 적힌 내용을 전달 받는다.

                                    /** 5번째 예외처리 = 총인원 설정 */
                                    if(Moim_Total_people.equals("")){ // 총인원이 비어있을 경우
                                        Toast.makeText(Moim_modify_Activity.this,"총인원을 입력하세요.",Toast.LENGTH_SHORT).show();    // 해당 클릭 아이이템의 텍스트를 토스트로 출력
                                    }
                                    else{

                                        /** 선정방법은 변수 Selection_method (선착순, 개설자 선정)  를 그대로 집어 넣는다. 18 */
                                        Log.e("선정방법 = ",Selection_method);


                                        /**
                                         * 신청문의 연락처 설정 (전화번호 + 이메일) 19 20
                                         */
                                        // 신청문의 연락처 설정 - 앞번호 + 중간번호 + 끝번호
                                        /** 앞번호는 다른코드에서 준비되어 있다. = Front_num */
                                        EditText 중간번호 = (EditText)findViewById(R.id.Middle_num);   // 중간번호
                                        Middle_num = 중간번호.getText().toString();
                                        EditText 끝번호 = (EditText)findViewById(R.id.end_num);      // 끝번호
                                        End_num = 끝번호.getText().toString();

                                        /** 6번째 예외처리 = 전화번호 설정 */
                                        if(Front_num.equals("")){ // 앞번호가 비어있을 경우
                                            Toast.makeText(Moim_modify_Activity.this,"전화번호를 입력하세요.",Toast.LENGTH_SHORT).show();
                                        }
                                        else if(Middle_num.equals("")){ // 중간번호가 비어있을 경우
                                            Toast.makeText(Moim_modify_Activity.this,"전화번호를 입력하세요.",Toast.LENGTH_SHORT).show();
                                        }
                                        else if(End_num.equals("")){ // 끝번호가 비어있을 경우
                                            Toast.makeText(Moim_modify_Activity.this,"전화번호를 입력하세요.",Toast.LENGTH_SHORT).show();
                                        }
                                        else{

                                            // 1. 전화번호
                                            Call_number = Front_num+"-"+Middle_num+"-"+End_num; // 다 합친 형태 EX) 010-1234-5678
                                            Log.e("전화번호 = ",Call_number);



                                            // 이메일 앞부분
                                            EditText 이메일 = (EditText)findViewById(R.id.Front_Email);   // 이메일
                                            Front_Email = 이메일.getText().toString();

                                            /** 7 번째 예외처리 = 이메일 설정 */
                                            if(Front_Email.equals("")){ // 이메일 아이디가 비어있을 경우
                                                Toast.makeText(Moim_modify_Activity.this,"이메일을 입력하세요.",Toast.LENGTH_SHORT).show();
                                            }
                                            else if(End_Email.equals("")){ // 이메일 이 비어있을 경우
                                                Toast.makeText(Moim_modify_Activity.this,"이메일을 입력하세요.",Toast.LENGTH_SHORT).show();
                                            }
                                            else {

                                                // 2. 이메일
                                                Email = Front_Email+"@"+End_Email;
                                                Log.e("이메일 = ",Email);


                                                /**
                                                 *  모임 장소 데이터 - 1.위도/경도 데이터  2.주소  3.상세주소 21 22 23
                                                 */
                                                // 위치를 나타내기 위한 3개의 데이터

                                                // 1. 위치데이터 = Place_Data 변수
                                                // 2. 주소 = 주소.getText = TextView
                                                String Address = Address_textview.getText().toString();
                                                // 3. 상세주소 = EditText
                                                EditText 상세주소 = (EditText)findViewById(R.id.DetailAddress_Edit);
                                                String Detail_Address = 상세주소.getText().toString();

                                                /** 8 번째 예외처리 = 모임장소 설정 */
                                                if(Place_Data.equals("")){  // 위치데이터가 비어있을 경우
                                                    Toast.makeText(Moim_modify_Activity.this,"장소를 설정하여 주세요.",Toast.LENGTH_SHORT).show();
                                                }
                                                else if(Address.equals("")){  // 위치데이터가 비어있을 경우
                                                    Toast.makeText(Moim_modify_Activity.this,"장소를 설정하여 주세요.",Toast.LENGTH_SHORT).show();
                                                }
                                                else if(Detail_Address.equals("")){  // 위치데이터가 비어있을 경우
                                                    Toast.makeText(Moim_modify_Activity.this,"장소를 설정하여 주세요.",Toast.LENGTH_SHORT).show();
                                                }
                                                else{
                                                    /**
                                                     * 간단한 모임소개 입력 24
                                                     */
                                                    EditText 간단모임내용 = (EditText)findViewById(R.id.explainContent);   // 간단모임내용
                                                    String Brief_description = 간단모임내용.getText().toString();
                                                    Log.e("간단모임내용 = ",Brief_description);




/**
 * 이부분 작업 할 차례 수정을 해야 하니까 모임번호로 모임 찾아서 다시 집어 넣어야 한다. --------------------------------------------------------------------------------------------------------------------------------------------
 */


                                                    InsertData Data_transfer_task = new InsertData();
                                                    Data_transfer_task.execute(Moim_Creator,Moim_name,Moim_default_imgName,Moim_Total_people                    // 개설자, 모임명, 모임기본이미지 이름, 총인원
                                                            ,Moim_date_1, Moim_date_2, Moim_start_time, Moim_end_time                       // 모임날짜1, 모임날짜2, 모임시작시간, 모임종료시간
                                                            ,Moim_apply_date_1, Moim_apply_date_2, Apply_start_time, Apply_end_time         // 모임접수날짜1, 모임접수날짜2, 접수시작시간, 접수종료시간
                                                            ,Application_Method, Bank, Account_number, Account_holder, Participation_fee    // 신청방식, 은행명, 계좌번호, 예금주, 참가비용
                                                            ,Selection_method                       // 선정방식
                                                            ,Call_number, Email                     // 신청문의 연락처 설정 = 전화번호, 이메일
                                                            ,Place_Data, Address, Detail_Address    // 모임 장소 데이터 1.위도/경도 데이터  2.주소  3.상세주소
                                                            ,Brief_description                      // 간단 모임설명
                                                            ,received_no
                                                    );  // 태스크 진행하면서 아이디, 패스워드를 전달한다.

                                                    Intent intent = new Intent();
                                                    setResult(RESULT_OK, intent);
                                                    finish();

                                                }// 8 번째 예외 처리 끝
                                            }// 7 번째 예외 처리 끝
                                        }// 6 번째 예외 처리 끝
                                    }// 5 번째 예외 처리 끝
                                }// 4 번째 예외처리 = 신청방식 유료일 경우 - 끝 = 여기서 유료와 무료의 코드가 나뉜다.
                            }// 신청방식 유료 끝

                            /** 신청방식이 무료일 경우 예외처리 없이 그대로 진행 */
                            else{   // 은행명이 무료 인경우
                                Bank = "";
                                Account_number = "";
                                Account_holder = "";
                                Participation_fee = "";

                                Log.e("은행명 = ",Bank+"");
                                Log.e("계좌번호 = ",Account_number+"");
                                Log.e("예금주 = ",Account_holder+"");
                                Log.e("참가비용 = ",Participation_fee+"");


                                // 총인원
                                String Moim_Total_people = Total_people.getText().toString();   // 총인원 = 총인원 에디트 텍스트에 적힌 내용을 전달 받는다.

                                /** 5번째 예외처리 = 총인원 설정 */
                                if(Moim_Total_people.equals("")){ // 총인원이 비어있을 경우
                                    Toast.makeText(Moim_modify_Activity.this,"총인원을 입력하세요.",Toast.LENGTH_SHORT).show();    // 해당 클릭 아이이템의 텍스트를 토스트로 출력
                                }
                                else{

                                    /** 선정방법은 변수 Selection_method (선착순, 개설자 선정)  를 그대로 집어 넣는다. 18 */
                                    Log.e("선정방법 = ",Selection_method);


                                    /**
                                     * 신청문의 연락처 설정 (전화번호 + 이메일) 19 20
                                     */
                                    // 신청문의 연락처 설정 - 앞번호 + 중간번호 + 끝번호
                                    /** 앞번호는 다른코드에서 준비되어 있다. = Front_num */
                                    EditText 중간번호 = (EditText)findViewById(R.id.Middle_num);   // 중간번호
                                    Middle_num = 중간번호.getText().toString();
                                    EditText 끝번호 = (EditText)findViewById(R.id.end_num);      // 끝번호
                                    End_num = 끝번호.getText().toString();

                                    /** 6번째 예외처리 = 전화번호 설정 */
                                    if(Front_num.equals("")){ // 앞번호가 비어있을 경우
                                        Toast.makeText(Moim_modify_Activity.this,"전화번호를 입력하세요.",Toast.LENGTH_SHORT).show();
                                    }
                                    else if(Middle_num.equals("")){ // 중간번호가 비어있을 경우
                                        Toast.makeText(Moim_modify_Activity.this,"전화번호를 입력하세요.",Toast.LENGTH_SHORT).show();
                                    }
                                    else if(End_num.equals("")){ // 끝번호가 비어있을 경우
                                        Toast.makeText(Moim_modify_Activity.this,"전화번호를 입력하세요.",Toast.LENGTH_SHORT).show();
                                    }
                                    else{

                                        // 1. 전화번호
                                        Call_number = Front_num+"-"+Middle_num+"-"+End_num; // 다 합친 형태 EX) 010-1234-5678
                                        Log.e("전화번호 = ",Call_number);



                                        // 이메일 앞부분
                                        EditText 이메일 = (EditText)findViewById(R.id.Middle_num);   // 이메일
                                        Front_Email = 이메일.getText().toString();

                                        /** 7 번째 예외처리 = 이메일 설정 */
                                        if(Front_Email.equals("")){ // 이메일 아이디가 비어있을 경우
                                            Toast.makeText(Moim_modify_Activity.this,"이메일을 입력하세요.",Toast.LENGTH_SHORT).show();
                                        }
                                        else if(End_Email.equals("")){ // 이메일 이 비어있을 경우
                                            Toast.makeText(Moim_modify_Activity.this,"이메일을 입력하세요.",Toast.LENGTH_SHORT).show();
                                        }
                                        else {

                                            // 2. 이메일
                                            Email = Front_Email+"@"+End_Email;
                                            Log.e("이메일 = ",Email);


                                            /**
                                             *  모임 장소 데이터 - 1.위도/경도 데이터  2.주소  3.상세주소 21 22 23
                                             */
                                            // 위치를 나타내기 위한 3개의 데이터

                                            // 1. 위치데이터 = Place_Data 변수
                                            // 2. 주소 = 주소.getText = TextView
                                            String Address = Address_textview.getText().toString();
                                            // 3. 상세주소 = EditText
                                            EditText 상세주소 = (EditText)findViewById(R.id.DetailAddress_Edit);
                                            String Detail_Address = 상세주소.getText().toString();

                                            /** 8 번째 예외처리 = 모임장소 설정 */
                                            if(Place_Data.equals("")){  // 위치데이터가 비어있을 경우
                                                Toast.makeText(Moim_modify_Activity.this,"장소를 설정하여 주세요.",Toast.LENGTH_SHORT).show();
                                            }
                                            else if(Address.equals("")){  // 위치데이터가 비어있을 경우
                                                Toast.makeText(Moim_modify_Activity.this,"장소를 설정하여 주세요.",Toast.LENGTH_SHORT).show();
                                            }
                                            else if(Detail_Address.equals("")){  // 위치데이터가 비어있을 경우
                                                Toast.makeText(Moim_modify_Activity.this,"장소를 설정하여 주세요.",Toast.LENGTH_SHORT).show();
                                            }
                                            else{
                                                /**
                                                 * 간단한 모임소개 입력 24
                                                 */
                                                EditText 간단모임내용 = (EditText)findViewById(R.id.explainContent);   // 간단모임내용
                                                String Brief_description = 간단모임내용.getText().toString();
                                                Log.e("간단모임내용 = ",Brief_description);


                                                InsertData Data_transfer_task = new InsertData();
                                                Data_transfer_task.execute(Moim_Creator,Moim_name,Moim_default_imgName,Moim_Total_people                    // 개설자, 모임명, 모임기본이미지 이름, 총인원
                                                        ,Moim_date_1, Moim_date_2, Moim_start_time, Moim_end_time                       // 모임날짜1, 모임날짜2, 모임시작시간, 모임종료시간
                                                        ,Moim_apply_date_1, Moim_apply_date_2, Apply_start_time, Apply_end_time         // 모임접수날짜1, 모임접수날짜2, 접수시작시간, 접수종료시간
                                                        ,Application_Method, Bank, Account_number, Account_holder, Participation_fee    // 신청방식, 은행명, 계좌번호, 예금주, 참가비용
                                                        ,Selection_method                       // 선정방식
                                                        ,Call_number, Email                     // 신청문의 연락처 설정 = 전화번호, 이메일
                                                        ,Place_Data, Address, Detail_Address    // 모임 장소 데이터 1.위도/경도 데이터  2.주소  3.상세주소
                                                        ,Brief_description                      // 간단 모임설명
                                                        ,received_no
                                                );  // 태스크 진행하면서 아이디, 패스워드를 전달한다.

                                                Intent intent = new Intent();
                                                setResult(RESULT_OK, intent);
                                                finish();

                                            }// 8 번째 예외 처리 끝
                                        }// 7 번째 예외 처리 끝
                                    }// 6 번째 예외 처리 끝
                                }// 5 번째 예외 처리 끝
                            }// 무료 끝
                        }// 3. 접수날짜 설정 예외처리 끝
                    }// 2. 모임날짜 설정 예외처리 끝
                }// 1. 모임명 예외처리 끝



            }// 등록버튼 끝
        });











/**
 * onCreate 끝   ------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------- 끝
 */
    }// onCreate 끝  ----------------------------------------------------------------------------------------------------------------------------------------------------- 끝





    /**
     * 위도 경도, 이름 주소 받아서 마커 찍는 pickMark 메소드
     */
    private void pickMark(final LatLng LL,String name, String address){ // 위도 경도, 이름 주소 받아서 마커 찍는 함수
        mMap.clear();
        markerOptions = new MarkerOptions(); // 옵션 설정 해놓을 변수
        markerOptions.position(LL); // 위치 적용
//        markerOptions.title(String.format(Locale.KOREA,"%.3f",LL.latitude)+","+String.format(Locale.KOREA,"%.3f",LL.longitude));
        markerOptions.title(name); // 이름
//        markerOptions.snippet(address.substring(0,20)); // 주소 넣음
        markerOptions.snippet(address); // 주소 넣음
        markerOptions.draggable(true); // 마커가 드래그 가능하도록

        //색 다르게
//        if(MarkerPoints.size()==0)
//        {
//            markerOptions.icon(BitmapDescriptorFactory.defaultMarker(BitmapDescriptorFactory.HUE_GREEN));
//        }
//        else if (MarkerPoints.size() == 1) {
//            markerOptions.icon(BitmapDescriptorFactory.defaultMarker(BitmapDescriptorFactory.HUE_RED));
//        }
//        else if(MarkerPoints.size()>1)
//        {
//            markerOptions.icon(BitmapDescriptorFactory.defaultMarker(BitmapDescriptorFactory.HUE_AZURE));
//        }

        mMap.addMarker(markerOptions).setDraggable(true);
        mMap.addMarker(markerOptions).showInfoWindow(); // 맵에 추가
//        MarkerPoints.add(LL); // 위치정보 마커 리스트에 추가
    } // pickMark





    /**
     * 초기 맵 마커 설정 onMapReady 메소드
     */
    @Override
    public void onMapReady(final GoogleMap map) {

        mMap = map; // 전역 map 객체인 mMap 과 onMapReady 메소드 인수 GoogleMap map 과 동일한 객체로 인식하게 한다.

        markerOptions = new MarkerOptions();    // MarkerOptions 객체 생성
        markerOptions.position(SEOUL);  // 위도 경도 받아오기
        markerOptions.title("서울");  // 타이틀 설정
        markerOptions.snippet("위치정보를 받아오지 못했습니다.");    // 세부 내용 설정
        mMap.addMarker(markerOptions);  // 마커 추가

        mMap.moveCamera(CameraUpdateFactory.newLatLng(SEOUL));
        mMap.animateCamera(CameraUpdateFactory.zoomTo(10));





// 처음 시작 할때 만 설정 된다.

        //  숫자로만 된 위치데이터를 가져와서 둘로 쪼갠다,
        String[] latlong =  received_Place_data.split(",");
        double latitude = Double.parseDouble(latlong[0]);   // 위도
        double longitude = Double.parseDouble(latlong[1]);  // 경도
        Log.e("latitude",""+latitude);
        Log.e("longitude",""+longitude);


        SEOUL = new LatLng(latitude, longitude);   // LatLng 위도 경도 대입
        Log.e("위치데이터",""+SEOUL.toString());

        Place_Data = SEOUL.toString();  // 위치데이터에 위도경도데이터인 SEOUL 을 담는다.

        // 위치데이터와 주소를 입력하고 맵 프래그먼트에 표시
        pickMark(SEOUL, received_Detail_address, received_Address);   // pickMark(final LatLng LL, String received_Detail_address, String received_Address)

//                markerOptions.title(Detail_address);        // 마커 타이틀에 이름 넣고
//                markerOptions.snippet(Address);   // 마커 세부 내용에 주소 넣는다.

        mMap.moveCamera(CameraUpdateFactory.newLatLng(SEOUL));  // 맵마커의 위치에 따라 카메라 이동. 위치 = PLACE
        mMap.animateCamera(CameraUpdateFactory.zoomTo(13)); // 카메라 줌 상태 설정



    }










    /**
     * onStart()
     */
    @Override
    protected void onStart() {
        super.onStart();
        // 네비게이션 뷰 객체 생성 - 헤더 뷰의 객체를 생성하기 위해서 는 네비게시션 뷰의 객체가 필요하다.
        NavigationView navigationView = (NavigationView) findViewById(R.id.navigation_view);

        // 헤더뷰 객체 생성
        View headerview = navigationView.getHeaderView(0);
        // 헤더뷰 에 있는 위젯
        TextView Main_Login_id = (TextView) headerview.findViewById(R.id.text_id);          // 헤더뷰의 아이디 텍스트 객체
        TextView Main_Login_email = (TextView) headerview.findViewById(R.id.text_email);    // 헤더뷰의 이메일 텍스트 객체
        // 헤더뷰의 로그아웃 버튼 객체 생성
        final Button Main_logout_button = (Button) headerview.findViewById(R.id.logout_button);   // 로그아웃 버튼



        /**
         * 로그인 상태 변수에 따라서 헤더뷰의 상태가 변하는 상황
         */
        // 로그인 상태 변수가 1이면 = 로그인 상태
        if(Login_State == 1){

            SharedPreferences SF = getSharedPreferences("SF", MODE_PRIVATE);     // Shared 객체 생성. getSharedPreferences(String name, int mode)

            String JSONArray저장스트링 = SF.getString("제이슨어레이 KEY","");  // SharedPreferences 객체명.getString(" key "," 기본값 ");
            // String kkk = 에 제이슨 어레이에 저장된 String 형태의 Key 들과 Data 들을 옮긴다.
            Log.e("저장된 제이슨 어레이 확인 ",""+JSONArray저장스트링);

            try {                                                               // Log.e("저장된 제이슨 어레이 확인 ",""+JSONArray저장스트링);
                JSONArray jsonArray = new JSONArray(JSONArray저장스트링);
                Log.e("저장된 데이터 불러오는중 ","abc");      // abc 안찍어 줘서 모르는거다.
                Log.e("제이슨 어레이 길이 = ",""+jsonArray.length());

                for(int i = 0; i < jsonArray.length(); i++) {   // JSONArray 길이 만큼 반복하여 JSONObject 를  꺼냄
                    // Array 에서 하나의 JSONObject 를 추출
                    JSONObject dataJsonObject = jsonArray.getJSONObject(i);
                    Log.e("제이슨 어레이 객체에 저장된 값","abc"+jsonArray.getJSONObject(i));    // 별 희한 하네 여기서 로그 찍으니까 되네
                    // 추출한 Object 에서 필요한 데이터를 표시할 방법을 정해서 화면에 표시
                    // 필자는 RecyclerView 로 데이터를 표시 함
                    Log.e("for문을 도는중","abc");

                    receive0 = dataJsonObject.getString("Login_ID");         // 아이디  // 키 값에 따라 데이터 받기
                    receive1 = dataJsonObject.getString("Login_NAME");       // 이름
                    receive2 = dataJsonObject.getString("Login_EMAIL");      // 이메일
                    receive3 = dataJsonObject.getString("Login_PHONE");      // 휴대폰
//                String 이미지Uri스트링 = dataJsonObject.getString("제품URI");   // 제품 Uri String

                    // 데이터 로그 찍어보기
                    Log.e("화면 = ",TAG);
                    Log.e("Login_ID = ",receive0);
                    Log.e("Login_NAME = ",receive1);
                    Log.e("Login_EMAIL = ",receive2);
                    Log.e("Login_PHONE = ",receive3);


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
                    Toast.makeText(Moim_modify_Activity.this, "로그아웃 되었습니다.", Toast.LENGTH_SHORT).show();
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
                    // 프로필 볼수 있게끔 코드 짜줘야 한다.
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
                    // 프로필 볼수 있게끔 코드 짜줘야 한다.
                }
            }
        });

    }// onStart 끝





// 기본 이미지 설정 부분 - 카메라 갤러리 크롭 포함 ------------------------------------------------------------------------------------------


    void show()
    {
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setTitle("방식 선택");
        builder.setMessage("어떤 방법을 선택하시겠습니까?");

        builder.setPositiveButton("카메라",
                new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int which) {
                        Toast.makeText(getApplicationContext(),"카메라를 선택했습니다.",Toast.LENGTH_LONG).show();

                        takePhoto(); // 카메라 메소드 실행

                    }
                });
        builder.setNeutralButton("갤러리",
                new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int which) {
                        Toast.makeText(getApplicationContext(),"갤러리를 선택했습니다.",Toast.LENGTH_LONG).show();

                        goToAlbum(); // 갤러리 메소드 실행
                    }
                });

        builder.setNegativeButton("취소",
                new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int which) {
                    }
                });

        builder.show();
    }







    private boolean checkPermissions() {
        int result;
        List<String> permissionList = new ArrayList<>();
        for (String pm : permissions) {
            result = ContextCompat.checkSelfPermission(this, pm);
            if (result != PackageManager.PERMISSION_GRANTED) { // 사용자가 해당 권한을 가지고 있지 않을 경우 리스트에 해당 권한명 추가
                permissionList.add(pm);
            }
        }
        if (!permissionList.isEmpty()) { // 권한이 추가되었으면 해당 리스트가 empty가 아니므로 request 즉 권한을 요청합니다.
            ActivityCompat.requestPermissions(this, permissionList.toArray(new String[permissionList.size()]), MULTIPLE_PERMISSIONS);
            return false;
        }
        return true;
    }

    /**
     *  초기 이미지 설정
     */
    private void initView() {
        imgMain = (ImageView) findViewById(R.id.BasicImage_View);





// 수정중----------------------
//        btnCamera = (Button) findViewById(R.id.btn_camera);
//        btnAlbum = (Button) findViewById(R.id.btn_album);
//
//        btnCamera.setOnClickListener(this);
//        btnAlbum.setOnClickListener(this);
    }

    // 카메라 선택
    private void takePhoto() {
        Intent intent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE); // 사진을 찍기 위하여 설정합니다.
        File photoFile = null;
        try {
            photoFile = createImageFile();
        } catch (IOException e) {
            Toast.makeText(Moim_modify_Activity.this, "이미지 처리 오류! 다시 시도해주세요.", Toast.LENGTH_SHORT).show();
            finish();
            e.printStackTrace();
        }
        if (photoFile != null) {
            photoUri = FileProvider.getUriForFile(Moim_modify_Activity.this, "com.example.user.androidhive.provider", photoFile);  // FileProvider의 경우 이전 포스트를 참고하세요.
            intent.putExtra(MediaStore.EXTRA_OUTPUT, photoUri);  // 사진을 찍어 해당 Content uri를 photoUri에 적용시키기 위함
            startActivityForResult(intent, PICK_FROM_CAMERA);
        }
    }

    // Android M에서는 Uri.fromFile 함수를 사용하였으나 7.0부터는 이 함수를 사용할 시 FileUriExposedException이
    // 발생하므로 아래와 같이 함수를 작성합니다. 이전 포스트에 참고한 영문 사이트를 들어가시면 자세한 설명을 볼 수 있습니다.
    private File createImageFile() throws IOException {
        String timeStamp = new SimpleDateFormat("HHmmss").format(new Date());
        String imageFileName = "nostest_" + timeStamp + "_";
        File storageDir = new File(Environment.getExternalStorageDirectory() + "/NOSTest/"); // NOSTest 라는 경로에 이미지를 저장하기 위함
        if (!storageDir.exists()) {
            storageDir.mkdirs();
        }
        File image = File.createTempFile(imageFileName, ".jpg", storageDir);

        mCurrentPhotoPath = "file:" + image.getAbsolutePath();
        return image;
    }

    // 앨범선택
    private void goToAlbum() {
        Intent intent = new Intent(Intent.ACTION_PICK); // ACTION_PICK 즉 사진을 고르겠다!
        intent.setType(MediaStore.Images.Media.CONTENT_TYPE);
        startActivityForResult(intent, PICK_FROM_ALBUM);
    }

// 수정중----------------------
//    @Override
//    public void onClick(View v) {
//        switch (v.getId()) {
//            case R.id.btn_camera:
//                takePhoto();
//                break;
//            case R.id.btn_album:
//                goToAlbum();
//                break;
//        }
//    }


    // 아래는 권한 요청 Callback 함수입니다. PERMISSION_GRANTED로 권한을 획득했는지 확인할 수 있습니다. 아래에서는 !=를 사용했기에
    // 권한 사용에 동의를 안했을 경우를 if문으로 코딩되었습니다.
    @Override
    public void onRequestPermissionsResult(int requestCode, String permissions[], int[] grantResults) {
        switch (requestCode) {
            case MULTIPLE_PERMISSIONS: {
                if (grantResults.length > 0) {
                    for (int i = 0; i < permissions.length; i++) {
                        if (permissions[i].equals(this.permissions[0])) {
                            if (grantResults[i] != PackageManager.PERMISSION_GRANTED) {
                                showNoPermissionToastAndFinish();
                            }
                        } else if (permissions[i].equals(this.permissions[1])) {
                            if (grantResults[i] != PackageManager.PERMISSION_GRANTED) {
                                showNoPermissionToastAndFinish();

                            }
                        } else if (permissions[i].equals(this.permissions[2])) {
                            if (grantResults[i] != PackageManager.PERMISSION_GRANTED) {
                                showNoPermissionToastAndFinish();

                            }
                        }
                    }
                } else {
                    showNoPermissionToastAndFinish();
                }
                return;
            }
        }
    }

    // 권한 획득에 동의를 하지 않았을 경우 아래 Toast 메세지를 띄우며
    private void showNoPermissionToastAndFinish() {
        Toast.makeText(this, "권한 요청에 동의 해주셔야 이용 가능합니다. 설정에서 권한 허용 하시기 바랍니다.", Toast.LENGTH_SHORT).show();
    }





// 기본 이미지 설정 부분 - 카메라 갤러리 크롭 ------------------------------------------------------------------------------------------ 시작

    // onActivityResult 콜백 함수 및 가장 중요한 Crop 자르기 함수 부분
    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        if (resultCode != RESULT_OK) {
            Toast.makeText(this, "취소 되었습니다.", Toast.LENGTH_SHORT).show();
            return;
        }
        if (requestCode == PICK_FROM_ALBUM) {
            if (data == null) {
                return;
            }
            photoUri = data.getData();
            cropImage();
        } else if (requestCode == PICK_FROM_CAMERA) {
            cropImage();
            // 갤러리에 나타나게
            MediaScannerConnection.scanFile(Moim_modify_Activity.this, // 앨범에 사진을 보여주기 위해 Scan을 합니다.
                    new String[]{photoUri.getPath()}, null,
                    new MediaScannerConnection.OnScanCompletedListener() {
                        public void onScanCompleted(String path, Uri uri) {
                        }
                    });
        } else if (requestCode == CROP_FROM_CAMERA) {   // 카메라에서 크롭한 이미지를 받아올때
            imgMain.setImageURI(null);
            imgMain.setImageURI(photoUri);

            String 이미지Uri = photoUri.toString();   // photoUri 를 String 화 한다.

            Log.e("화면 = ",TAG);
            Log.e("이미지 Uri = ",이미지Uri);

//            try { //저는 bitmap 형태의 이미지로 가져오기 위해 아래와 같이 작업하였으며 Thumbnail을 추출하였습니다.
//                Bitmap bitmap = MediaStore.Images.Media.getBitmap(this.getContentResolver(), photoUri);
//                Bitmap thumbImage = ThumbnailUtils.extractThumbnail(bitmap, 128, 128);
//                ByteArrayOutputStream bs = new ByteArrayOutputStream();
//                thumbImage.compress(Bitmap.CompressFormat.JPEG, 100, bs); //이미지가 클 경우 OutOfMemoryException 발생이 예상되어 압축
//
//                //여기서는 ImageView에 setImageBitmap을 활용하여 해당 이미지에 그림을 띄우시면 됩니다.
//                mImageView.setImageBitmap(thumbImage);
//            } catch (Exception e) {
//                Log.e("ERROR", e.getMessage().toString());
//            }
        }
        // 달력 관련 부분.
        else if (requestCode == CALANDER_DATE) { // requestCode == CALANDER_DATE 이면 년,월,일 에 대한 데이터를 처리한다.
            String year = data.getStringExtra("YEAR");
            String month = data.getStringExtra("MONTH");
            String day = data.getStringExtra("DAY");
            String day_of_the_week = data.getStringExtra("Day_Of_The_Week");

            Log.e("년도 ="," "+year);
            Log.e("월 ="," "+month);
            Log.e("일 ="," "+day);
            Log.e("요일 ="," "+day_of_the_week);



            if(date_num == 1){
                set_date1 = (Button)findViewById(R.id.set_date1);
                set_date1.setText(year+"."+month+"."+day+" "+day_of_the_week);
            }
            else if(date_num == 2){
                set_date2 = (Button)findViewById(R.id.set_date2);
                set_date2.setText(year+"."+month+"."+day+" "+day_of_the_week);
            }
            else if(date_num == 3){
                set_apply_date1 = (Button)findViewById(R.id.set_apply_date1);
                set_apply_date1.setText(year+"."+month+"."+day+" "+day_of_the_week);
            }
            else if(date_num == 4){
                set_apply_date2 = (Button)findViewById(R.id.set_apply_date2);
                set_apply_date2.setText(year+"."+month+"."+day+" "+day_of_the_week);
            }
        }

        else if(requestCode == PLACE_PICKER_REQUEST && resultCode == Activity.RESULT_OK)
        {
            final Place place = PlacePicker.getPlace(this, data);
            final CharSequence name = place.getName();
            final CharSequence address = place.getAddress();
            String attributions = (String) place.getAttributions();
            if (attributions == null) {
                attributions = "";
            }

//            Location location = followMeLocationSource.getLastKnownLocation();

            /** 받아온 정보에서 위치, 이름 , 주소 받아와서 마크 찍기 */
            pickMark(place.getLatLng(),name.toString(),address.toString()); // 받아온 정보에서 위치, 이름 , 주소 받아와서 마크 찍기


            // 텍스트 뷰에 이름, 주소 넣는다.
            Address_textview.setText("");
            Address_textview.append(address); // 주소만 들어간다.  넣기

// 텍스트뷰의 라인 길이 받아와서 텍스트뷰의 layout_gravity 조정 하려고 했으나 실패함 ------------------------------------------------------------------------------------------
//            Log.e("주소 텍스트 뷰 라인 수 ",""+Address_textview.getLineCount()); // 텍스트뷰 라인수 값 받아서 로그 확인
//            Log.e("주소 텍스트 뷰 getEllipsize ",""+Address_textview.getEllipsize()); // 텍스트뷰 라인수 값 받아서 로그 확인
//            if(Address_textview.getLineCount() == 2){   // 라인 수가 2 인 경우
////                Address_textview.setLayoutParams();
////                FrameLayout.LayoutParams layoutParams = new FrameLayout.LayoutParams(FrameLayout.LayoutParams.WRAP_CONTENT, FrameLayout.LayoutParams.WRAP_CONTENT , Gravity.CENTER_HORIZONTAL);
////                layout.setLayoutParams(layoutParams); // layout_gravity 조정 하려고 했으나 실패함
//
//                TextView Address_textview2 = (TextView)findViewById(R.id.Address_textview2);    // 2번 주소 텍스트 선언
//
//                Address_textview.setVisibility(View.GONE);  // 1번 주소텍스트 는 안보이게 하고
//
//                Address_textview2.append(address+" "+name); // 주소 + 이름  넣기
//                Address_textview2.setVisibility(View.VISIBLE);  // 2번 주소텍스트 를 보이게 한다.
//            }

//            Address_textview.append("address : " + address+"\n"); // 주소 넣기
//            Address_textview.append("name : " + name);    // 이름 넣기
//            Address_textview.append(Html.fromHtml(attributions));
//  -------------------------------------------------------------------------------------------------------------------------------------------------------------------------------- 끝

            markerOptions = new MarkerOptions();
            markerOptions.title("name");        // 마커 타이틀에 이름 넣고
            markerOptions.snippet("address");   // 마커 세부 내용에 주소 넣는다.

            Log.d("Place_Pick","2");
            Log.d("Place_Pick",attributions);
            Log.e("위치데이터",place.getLatLng().toString());
            Log.e("getRating",""+place.getRating());

            Place_Data = place.getLatLng().toString();  // 위치데이터에 위도경도데이터인 LL 을 담는다.

//            String lat = Place_Data.getString("lat");
//            String lng = Place_Data.getString("lng");

            mMap.moveCamera(CameraUpdateFactory.newLatLng(place.getLatLng()));  // 맵마커의 위치에 따라 카메라 이동. 위치 = place.getLatLng()
            mMap.animateCamera(CameraUpdateFactory.zoomTo(13)); // 카메라 줌 상태 설정
        }
        else {
            super.onActivityResult(requestCode, resultCode, data);
        }

    }// onActivityResult 끝






    //Android N crop image
    //    (이 부분에서 몇일동안 정신 못차렸습니다 ㅜ)
    // 모든 작업에 있어 사전에 FALG_GRANT_WRITE_URI_PERMISSION 과 READ 퍼미션을 줘야 uri를 활용한 작업에 지장을 받지 않는다는 것이 핵심입니다.
    public void cropImage() {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.N) {
            this.grantUriPermission("com.android.camera", photoUri,
                    Intent.FLAG_GRANT_WRITE_URI_PERMISSION | Intent.FLAG_GRANT_READ_URI_PERMISSION);
        }
        Intent intent = new Intent("com.android.camera.action.CROP");
        intent.setDataAndType(photoUri, "image/*");

        List<ResolveInfo> list = getPackageManager().queryIntentActivities(intent, 0);
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.N) {
            grantUriPermission(list.get(0).activityInfo.packageName, photoUri,
                    Intent.FLAG_GRANT_WRITE_URI_PERMISSION | Intent.FLAG_GRANT_READ_URI_PERMISSION);
        }
        int size = list.size();
        if (size == 0) {
            Toast.makeText(this, "취소 되었습니다.", Toast.LENGTH_SHORT).show();
            return;
        } else {
            Toast.makeText(this, "용량이 큰 사진의 경우 시간이 오래 걸릴 수 있습니다.", Toast.LENGTH_SHORT).show();

            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.N) {
                intent.addFlags(Intent.FLAG_GRANT_READ_URI_PERMISSION);
                intent.addFlags(Intent.FLAG_GRANT_WRITE_URI_PERMISSION);
            }
            intent.putExtra("crop", "true");
            intent.putExtra("aspectX", 1);
            intent.putExtra("aspectY", 1);
            intent.putExtra("scale", true);
            File croppedFileName = null;
            try {
                croppedFileName = createImageFile();
            } catch (IOException e) {
                e.printStackTrace();
            }

            File folder = new File(Environment.getExternalStorageDirectory() + "/NOSTest/");
            File tempFile = new File(folder.toString(), croppedFileName.getName());
            Log.e("임시파일 = ",""+tempFile);
            Log.e("크롭된파일 이름 = ",""+croppedFileName.getName());
            default_image_name = croppedFileName.getName();

            photoUri = FileProvider.getUriForFile(Moim_modify_Activity.this, "com.example.user.androidhive.provider", tempFile);

            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.N) {
                intent.addFlags(Intent.FLAG_GRANT_READ_URI_PERMISSION);
                intent.addFlags(Intent.FLAG_GRANT_WRITE_URI_PERMISSION);
            }

            intent.putExtra("return-data", false);
            intent.putExtra(MediaStore.EXTRA_OUTPUT, photoUri);
            intent.putExtra("outputFormat", Bitmap.CompressFormat.JPEG.toString());

            Intent i = new Intent(intent);
            ResolveInfo res = list.get(0);
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.N) {
                i.addFlags(Intent.FLAG_GRANT_READ_URI_PERMISSION);
                i.addFlags(Intent.FLAG_GRANT_WRITE_URI_PERMISSION);

                grantUriPermission(res.activityInfo.packageName, photoUri,
                        Intent.FLAG_GRANT_WRITE_URI_PERMISSION | Intent.FLAG_GRANT_READ_URI_PERMISSION);
            }
            i.setComponent(new ComponentName(res.activityInfo.packageName, res.activityInfo.name));
            startActivityForResult(i, CROP_FROM_CAMERA);
        }
    }

// 기본 이미지 설정 부분 - 카메라 갤러리 크롭 ------------------------------------------------------------------------------------------ 끝






// 전달할 데이터 = 이미지 Uri , 모임이름, 모임 개설자,





    /**
     *  기본 이미지 파일 업로드 위한  uploadFile 클래스
     */
    public int uploadFile(String sourceFileUri) {   // 이미지 파일을 업로드 하기 위해서는 String sourceFileUri 필요  = uploadFilePath + "" + uploadFileName

        String fileName = sourceFileUri;    // 받아온 sourceFileUri 을 fileName 에 저장 한다.
        Log.e("파일이름 = ",fileName);

        HttpURLConnection conn = null;  // 커넥션 null
        DataOutputStream dos = null;

        String lineEnd = "\r\n";
        String twoHyphens = "--";
        String boundary = "*****";

        int bytesRead, bytesAvailable, bufferSize;

        byte[] buffer;

        int maxBufferSize = 1 * 1024 * 1024;    // 최대 버퍼 사이즈

        File sourceFile = new File(sourceFileUri);  // sourceFileUri에 해당 되는 File 객체 sourceFile를 생성한다

        if (!sourceFile.isFile()) { // 만약 sourceFile 이 파일이 아니면
//            progress_dialog.dismiss(); // 다이얼 로그 해제
            Log.e("uploadFile", "Source File not exist :" +uploadFilePath + "" + uploadFileName);

            runOnUiThread(new Runnable() {
                public void run() {
                    Toast.makeText(Moim_modify_Activity.this, "소스 파일이 존재하지 않습니다 :" +uploadFilePath + "" + uploadFileName, Toast.LENGTH_SHORT).show();
//                    messageText.setText("소스 파일이 존재하지 않습니다 :" +uploadFilePath + "" + uploadFileName);   // 파일이 존재하지 않는다고 토스트 띄움
                }
            });
            return 0;
        }
        else {  // 파일이 맞다면
            try {
                // open a URL connection to the Servlet
                FileInputStream fileInputStream = new FileInputStream(sourceFile);
                URL url = new URL(upLoadServerUri);

                // Open a HTTP  connection to  the URL
                conn = (HttpURLConnection) url.openConnection();
                conn.setDoInput(true); // Allow Inputs
                conn.setDoOutput(true); // Allow Outputs
                conn.setUseCaches(false); // Don't use a Cached Copy

                conn.setRequestMethod("POST");
                conn.setRequestProperty("Connection", "Keep-Alive");
                conn.setRequestProperty("ENCTYPE", "multipart/form-data");
                conn.setRequestProperty("Content-Type", "multipart/form-data;boundary=" + boundary);
                conn.setRequestProperty("fileToUpload", fileName);  // PHP 파일과 동일해야 하는 fileToUpload

                Log.e("업로드할 파일명 = ",""+fileName );

                dos = new DataOutputStream(conn.getOutputStream());
                dos.writeBytes(twoHyphens + boundary + lineEnd);
                dos.writeBytes("Content-Disposition: form-data; name=\"fileToUpload\";filename=\"" + fileName + "\"" + lineEnd); // PHP 파일과 동일해야 하는 fileToUpload
                dos.writeBytes(lineEnd);


                // create a buffer of  maximum size
                bytesAvailable = fileInputStream.available();
                bufferSize = Math.min(bytesAvailable, maxBufferSize);
                buffer = new byte[bufferSize];


                // read file and write it into form...
                bytesRead = fileInputStream.read(buffer, 0, bufferSize);

                while (bytesRead > 0) {
                    dos.write(buffer, 0, bufferSize);
                    bytesAvailable = fileInputStream.available();
                    bufferSize = Math.min(bytesAvailable, maxBufferSize);
                    bytesRead = fileInputStream.read(buffer, 0, bufferSize);
                }

                // send multipart form data necesssary after file data...
                dos.writeBytes(lineEnd);
                dos.writeBytes(twoHyphens + boundary + twoHyphens + lineEnd);

                // Responses from the server (code and message)
                serverResponseCode = conn.getResponseCode();
                String serverResponseMessage = conn.getResponseMessage();

                Log.i("uploadFile", "HTTP Response is : " + serverResponseMessage + ": " + serverResponseCode);

                if(serverResponseCode == 200){  // 서버 반응 코드가 200 이면 성공적인것
                    runOnUiThread(new Runnable() {  // UI 스레드
                        public void run() {
                            String msg = "File Upload Completed.\n\n See uploaded file here : \n\n" +uploadFileName;    // 파일 전달 완료

//                            messageText.setText(msg);
//                            Toast.makeText(Moim_Make_Activity.this, "File Upload Complete.", Toast.LENGTH_SHORT).show();
                            Log.e("이미지 업로드 확인 = ", "이미지 업로드 성공");
                        }
                    });
                }


                //close the streams //
                fileInputStream.close();
                dos.flush();
                dos.close();

                // 에코 데이터 받아 오는 곳 = 결과
                InputStream inputStream =conn.getInputStream();
                InputStreamReader inputStreamReader = new InputStreamReader(inputStream);
                BufferedReader bufferedReader =  new BufferedReader(inputStreamReader);


                final StringBuilder sb = new StringBuilder();
                String line;

                while((line = bufferedReader.readLine()) !=null)
                    sb.append(line);

                bufferedReader.close();
                sb.toString().trim();

                Log.e("Upload file to server", "error: " + sb.toString().trim());   // 데이터 결과를 받아와서 로그로 찍어본다.


            } catch (MalformedURLException ex) {
//                progress_dialog.dismiss();
                ex.printStackTrace();

                runOnUiThread(new Runnable() {
                    public void run() {
//                        messageText.setText("MalformedURLException Exception : check script url.");
                        Toast.makeText(Moim_modify_Activity.this, "MalformedURLException", Toast.LENGTH_SHORT).show();
                    }
                });
                Log.e("Upload file to server", "error: " + ex.getMessage(), ex);

            } catch (Exception e) {
//                progress_dialog.dismiss();
                e.printStackTrace();

                runOnUiThread(new Runnable() {
                    public void run() {
//                        messageText.setText("Got Exception : see logcat ");
                        Toast.makeText(Moim_modify_Activity.this, "Got Exception : see logcat ", Toast.LENGTH_SHORT).show();
                    }
                });
                Log.e("Upload Exception", "Exception : " + e.getMessage(), e);
            }

//            progress_dialog.dismiss();
            return serverResponseCode;
        } // End else block
    }// uploadFile 끝









    /**
     * 이미지 데이터 제외하고 다른 데이터를 넣기위해 사용 되는 InsertData 클래스 extends AsyncTask
     */
    private class InsertData extends AsyncTask<String, Void, String> {

        ProgressDialog progressDialog;
        String errorString = null;

        @Override
        protected void onPreExecute() {     // 시작전
            super.onPreExecute();
            progressDialog = ProgressDialog.show(Moim_modify_Activity.this, "Please Wait", null, true, true);
        }


        @Override
        protected void onPostExecute(String result) {   // 완료 후
            super.onPostExecute(result);

            progressDialog.dismiss();
//            mTextViewResult.setText(result);
            Log.d(TAG, "response - " + result);

            if (result == null){    // 값을 받아 오지 못했다면
                Toast.makeText(Moim_modify_Activity.this,"값을 받아오지 못했습니다.",Toast.LENGTH_SHORT).show();
            }
            else if(result.equals("SQL문 처리 성공")){  // 성공
                Toast.makeText(Moim_modify_Activity.this,"모임이 수정되었습니다.",Toast.LENGTH_SHORT).show();
            }
            else if(result.equals("데이터를 입력하세요.")){  // 데이터 전달이 제대로 안됫을 경우
                Toast.makeText(Moim_modify_Activity.this,"POST로 데이터가 넘어가질 않음",Toast.LENGTH_SHORT).show();
                Log.e("response = ", result);
            }
            else {  // Sql 문 에러 발생시
                Toast.makeText(Moim_modify_Activity.this,"데이터 전달에 실패함.",Toast.LENGTH_SHORT).show();
                Log.e("response = ", result);
            }
            finish();// 현재화면 종료
        }





        @Override
        protected String doInBackground(String... params) {
            // 전달할 데이터
            String moim_creator = params[0];                // 개설자
            String moim_name = params[1];                   // 모임명
            String Moim_default_imgName = params[2];        // 모임 기본 이미지 이름
            String Moim_Total_people = params[3];           // 총 인원
            // 모임 날짜
            String Moim_date_1 = params[4];
            String Moim_date_2 = params[5];
            String Moim_start_time = params[6];
            String Moim_end_time = params[7];
            // 접수 날짜
            String Moim_apply_date_1 = params[8];
            String Moim_apply_date_2 = params[9];
            String Apply_start_time = params[10];
            String Apply_end_time = params[11];
            // 신청방식에 따라 - 유,무료
            String Application_Method = params[12];
            String Bank = params[13];
            String Account_number = params[14];
            String Account_holder = params[15];
            String Participation_fee = params[16];
            // 선정방식
            String Selection_method = params[17];
            // 연락처
            String Call_number = params[18];
            String Email = params[19];
            // 장소데이터
            String Place_Data = params[20];
            String Address = params[21];
            String Detail_Address = params[22];
            // 간단모임소개
            String Brief_description = params[23];
            String Moim_No = params[24];


            Log.e("개설자 = ",moim_creator);
            Log.e("모임명 = ",moim_name);
            Log.e("기본이미지 이름 = ",Moim_default_imgName+"");
            Log.e("총인원 = ",Moim_Total_people);
            Log.e("모임번호 = ",Moim_No);



            String serverURL = "http://49.247.208.191/AndroidHive/Moim_Data_Modify.php";  //
            // 파라미터를 키 밸류 구조로 전달 = 총 25 개
            String postParameters = "moim_creator=" + moim_creator + "&moim_name=" + moim_name + "&moim_default_imgName=" + Moim_default_imgName + "&Moim_Total_people=" + Moim_Total_people
                    + "&Moim_date_1=" + Moim_date_1 + "&Moim_date_2=" + Moim_date_2 + "&Moim_start_time=" + Moim_start_time + "&Moim_end_time=" + Moim_end_time
                    + "&Moim_apply_date_1=" + Moim_apply_date_1 + "&Moim_apply_date_2=" + Moim_apply_date_2 + "&Apply_start_time=" + Apply_start_time + "&Apply_end_time=" + Apply_end_time
                    + "&Application_Method=" + Application_Method + "&Bank=" + Bank + "&Account_number=" + Account_number + "&Account_holder=" + Account_holder + "&Participation_fee=" + Participation_fee
                    + "&Selection_method=" + Selection_method
                    + "&Call_number=" + Call_number+ "&Email=" + Email
                    + "&Place_Data=" + Place_Data+ "&Address=" + Address+ "&Detail_Address=" + Detail_Address
                    + "&Brief_description=" + Brief_description
                    + "&Moim_No=" + Moim_No;

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
                }
                else{
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
    }// InsertData 끝







    @Override
    public void onBackPressed() {

        back_button_alertshow();
    }

    // 백 버튼 누르를 경우 경고창 띄우기
    void back_button_alertshow()
    {
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setTitle("경고");
        builder.setMessage("현재 입력한 데이터들을 읽게 됩니다. 그래도 뒤로 가시겠습니까?");

        builder.setPositiveButton("취소",
                new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int which) {
                        Toast.makeText(getApplicationContext(),"취소를 선택했습니다.",Toast.LENGTH_LONG).show();
                    }
                });
        builder.setNeutralButton("확인",
                new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int which) {
                        Toast.makeText(getApplicationContext(),"확인를 선택했습니다.",Toast.LENGTH_LONG).show();
                        finish();
                    }
                });

        builder.show();
    }





    // 복사 시작
    /**
     *  툴바의 오른쪽 옵션 메뉴 생성
     */
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
    // 복사 끝



}
