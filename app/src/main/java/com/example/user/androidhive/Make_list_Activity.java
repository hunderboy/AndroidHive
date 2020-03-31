package com.example.user.androidhive;


import android.app.ProgressDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Color;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.design.widget.NavigationView;
import android.support.v4.view.GravityCompat;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.ActionBar;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.LinearLayoutManager;
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
import com.google.zxing.integration.android.IntentIntegrator;
import com.google.zxing.integration.android.IntentResult;

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
import static com.example.user.androidhive.MainActivity.receive0;
import static com.example.user.androidhive.Opened_Moim_List_Adapter.QR_Moim_NUMBER;

public class Make_list_Activity extends AppCompatActivity {

    final static int LIST_Refresh = 0; // 리스트 새롭게 하는 요청 코드

    // 복사에 필요한 부분
    // String receive0; /** 로그인 아이디 */
    private DrawerLayout mDrawerLayout;
    private static String TAG = "Make_list_Activity";

    //  리사이클 러뷰 관련 변수
    RecyclerView mRecyclerView;                 // 리사이클러뷰 객체
    RecyclerView.Adapter mAdapter;              // 어댑터 객체
    RecyclerView.LayoutManager mLayoutManager;  // 레이아웃매니저 객체
    ArrayList<Opened_Moim_Data> myDataset;      // 데이터 세트를 담고 있는 어레이리스트 객체

    // 받아온 데이터 변수
    String mJsonString;
    private static final String TAG_JSON="webnautes";

    private static final String TAG_NO = "no";
    private static final String TAG_CREATOR = "creator";
    private static final String TAG_DEFAULT_imgURI = "default_imgURI";
    private static final String TAG_NAME = "Name";
    private static final String TAG_TOTAL_PEOPLE ="Total_People";
    private static final String TAG_Place_Data ="Place_Data";

    // 삭제를 위한 모임 번호
    String Delete_Num;




    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_make_list_);

        // 개설하기 버튼
        Button MoimMake_button = (Button) findViewById(R.id.MoimMake_button);
        // 개설하기 버튼 클릭 리스너
        MoimMake_button.setOnClickListener(new Button.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(
                        getApplicationContext(), // 현재 화면의 제어권자
                        Moim_Make_Activity.class); // 다음 넘어갈 클래스 지정
                startActivityForResult(intent,LIST_Refresh); // 다음 화면으로 넘어간다

            }
        });



// 여기서 부터 모두 복사해야 함.------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------
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
        toolbar.setTitleTextColor(Color.TRANSPARENT);           // 툴바 타이틀 투명하게 변경
        toolbar.setSubtitleTextColor(Color.TRANSPARENT);        // 툴바 서브 타이틀 투명하게 변경


        // DrawerLayout 아이디 설정
        mDrawerLayout = (DrawerLayout) findViewById(R.id.drawer_layout);    // 메인 xml 의 최상단 drawer_layout 레이아웃
        // navigationView id 설정
        NavigationView navigationView = (NavigationView) findViewById(R.id.navigation_view);
        // 헤더뷰 객체 생성
        View headerview = navigationView.getHeaderView(0);

        // 네비게이션뷰 클릭 리스너
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
                            Toast.makeText(Make_list_Activity.this, "로그인 후에 확인 가능 합니다", Toast.LENGTH_SHORT).show();
                        }
                        else{
                            Intent intent = new Intent(
                                    getApplicationContext(), // 현재 화면의 제어권자
                                    Profle_Activity.class); // 다음 넘어갈 클래스 지정 - Profle_Activity
                            startActivity(intent); // 다음 화면으로 넘어간다
                        }break;
                    case R.id.nav_sub_menu_item02:  // 즐겨찾기 버튼
                        if(Login_State == 0 ){// 아직 로그인 안했을 때
                            Toast.makeText(Make_list_Activity.this, "로그인 후에 확인 가능 합니다", Toast.LENGTH_SHORT).show();
                        }
                        else{
                            Intent intent = new Intent(
                                    getApplicationContext(), // 현재 화면의 제어권자
                                    BookMarkActivity.class); // 다음 넘어갈 클래스 지정 - BookMarkActivity
                            startActivity(intent); // 다음 화면으로 넘어간다
                        }break;

                    case R.id.nav_sub_menu_item03:  // 내가 만든 모임 버튼
                        if(Login_State == 0 ){// 아직 로그인 안했을 때
                            Toast.makeText(Make_list_Activity.this, "로그인 후에 확인 가능 합니다", Toast.LENGTH_SHORT).show();
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
                            Toast.makeText(Make_list_Activity.this, "로그인 후에 확인 가능 합니다", Toast.LENGTH_SHORT).show();
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
                            Toast.makeText(Make_list_Activity.this, "로그인 후에 확인 가능 합니다", Toast.LENGTH_SHORT).show();
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

        ImageButton header_userimage = (ImageButton) headerview.findViewById(R.id.user_image);  // 헤더뷰 이미지 버튼


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

                    receive0 = dataJsonObject.getString("Login_ID");                            // 아이디 - 키 값에 따라 데이터 받기
                    String receive1 = dataJsonObject.getString("Login_NAME");                   // 이름
                    String receive2 = dataJsonObject.getString("Login_EMAIL");                  // 이메일
                    String receive3 = dataJsonObject.getString("Login_PHONE");                  // 휴대폰
                    String Profile_image_NAME = dataJsonObject.getString("Login_Profile_image");// 프로필 이미지 이름


                    // 데이터 로그 찍어보기
                    Log.e("화면 = ",TAG);
                    Log.e("Login_ID = ",receive0);
                    Log.e("Login_NAME = ",receive1);
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



                }// try 문 끝
            } catch (JSONException e) {
                e.printStackTrace();
            }// 불러오기 끝

            // 로그 아웃 버튼 클릭 리스너
            Main_logout_button.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    Toast.makeText(Make_list_Activity.this, "로그아웃 되었습니다.", Toast.LENGTH_SHORT).show();
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
// 복사 끝 --------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------






        /**
         * 리사이클 러뷰 관련
         * 리사이클 러뷰는
         * myDataset.add(new Opened_Moim_Data(Name,Total_People));
         * 같은 add 함수와 같은 메소드에 있어야 한다 =  중요!!!!!!!!!!!
         * 다른 메소드에 있을 경우 생성이 되지 않는다.
         */
        // 리사이클 러뷰 객체 선언
        mRecyclerView = (RecyclerView) findViewById(R.id.my_recycler_view);

        // use this setting to improve performance if you know that changes
        // in content do not change the layout size of the RecyclerView
        // 각 Item 들이 RecyclerView 의 전체 크기를 변경하지 않는 다면
        // setHasFixedSize() 함수를 사용해서 성능을 개선할 수 있습니다.
        // 변경될 가능성이 있다면 false 로 , 없다면 true를 설정해주세요.
        mRecyclerView.setHasFixedSize(true);

        // use a linear layout manager
        // 리니어레이아웃 매니저 객체 선언
        mLayoutManager = new LinearLayoutManager(this);
        mRecyclerView.setLayoutManager(mLayoutManager);

        // specify an adapter (see also next example)
        myDataset = new ArrayList<>();                          // 어레이리스트 객체인 데이터세트 객체 선언
        mAdapter = new Opened_Moim_List_Adapter(myDataset);     // 어댑터 객체 선언하며 데이터세트를 어댑터에 연결
        mRecyclerView.setAdapter(mAdapter);     // 리사이클러뷰에 어댑터 설정



//        mRecyclerView.addOnItemTouchListener(
//                new Creator_list_confirm_ClickListener(getApplicationContext(), mRecyclerView, new Creator_list_confirm_ClickListener.OnItemClickListener() {
//                    @Override
//                    public void onItemClick(View view, int position) {  // 클릭
//
//                        // 화면도 넘기면서 모임을 찾을 기반 데이터를 같이 넘긴다. = 모임명 + 개설자 + 모임번호
//                        Intent intent = new Intent(
//                                getApplicationContext(), // 현재화면의 제어권자
//                                Moim_Confirm_Activity.class);    // 다음넘어갈 화면
//
//
//                        // intent 객체에 데이터를 실어서 보내기
//                        // 리스트뷰 클릭시 인텐트 (Intent) 생성하고 position 값을 이용하여 인텐트로 넘길값들을 넘긴다.
//                        intent.putExtra("NAME", myDataset.get(position).text);      // 모임명 넘기기
//                        intent.putExtra("CREATOR", myDataset.get(position).Creator);   // 개설자
//                        intent.putExtra("NUMBER", myDataset.get(position).Number);    // 모임 번호
//
////                        intent.putExtra("POS", position);   // 포지션값 전달. 필요하면 하고
//
//
//                        Log.e("넘길 모임명 = ",""+ myDataset.get(position).text);
//                        Log.e("넘길 개설자 = ",""+ myDataset.get(position).Creator);
//                        Log.e("넘길 번호 = "," "+ myDataset.get(position).Number);
//
//                        startActivity(intent);
////                        startActivityForResult(intent,ACT_EDIT2);  // = 수정 삭제 할때, 이걸로 해야 한다.!!!!!!!!!!!!!!!!!!!!!!!
//
//                    }
//
//                    @Override
//                    public void onLongItemClick(View view, int position) {  // 롱 클릭
//                        Delete_Num = myDataset.get(position).Number;
//                        show(); // 정말로 삭제 할것인지 경고창을 띄운다.
//                    }
//                }
//        ));




        // 화면이 처음 만들어 지면서 리스트를 표현할때 불러오는 AsyncTask
        GetData task = new GetData();
        task.execute(receive0); // 아이디를 전달한다. = receive0

    }// onCreate 끝







    protected void onActivityResult (int requestCode, int resultCode, Intent data){  // 리스트를 추가했을 때, 리스트 새로고침

        IntentResult result = IntentIntegrator.parseActivityResult(requestCode, resultCode, data);  // QR 코드 스캔 후 생성되는 생성자

        if (result != null) {   // 생성자가 없으면
            //qrcode 가 없으면
            if (result.getContents() == null) {
                Toast.makeText(Make_list_Activity.this, "취소!", Toast.LENGTH_SHORT).show();
            } else {            // 생성자 있으면
                //qrcode 결과가 있으면
                Toast.makeText(Make_list_Activity.this, "스캔완료!", Toast.LENGTH_SHORT).show();
                try {
                    //data를 json으로 변환
                    JSONObject obj = new JSONObject(result.getContents()); // 제이슨 객체를 가져오고

                    // 카값에 따라 데이터 설정
                    String QR_cord_M_NO = obj.getString("M_NUMBER"); // QR 코드 모임번호 설정
                    String QR_cord_M_ID = obj.getString("USER_ID");  // QR 코드 아이디 설정

                    String Item_M_NO = QR_Moim_NUMBER;  // 클릭한 아이템의 모임 번호 설정
                    Log.e("클릭한 아이템의 모임 번호","" +Item_M_NO);

                    if(QR_cord_M_NO.equals(Item_M_NO)){ // QR 코드에서 받아온 모임번호와 클릭한 아이템의 모임번호가 일치하면

                        // 출석 확인을 위한 Attendence_Confirm AsyncTask
                        Attendence_Confirm task = new Attendence_Confirm();   // 그 아이디를 기준으로 스레드 실행해서 나머지 데이터 가져오기
                        task.execute(QR_cord_M_NO,QR_cord_M_ID);    // 받아온 모임번호와 아이디를 넘겨준다.
                    }
                    else{   // 일치하지 않으면
                        Toast.makeText(Make_list_Activity.this, "해당 모임에 관련된 출석코드가 아닙니다.", Toast.LENGTH_SHORT).show();
                    }


                } catch (JSONException e) { // 오류시에
                    e.printStackTrace();
                    //Toast.makeText(MainActivity.this, result.getContents(), Toast.LENGTH_LONG).show();
                    Toast.makeText(Make_list_Activity.this, "오류 발생!", Toast.LENGTH_SHORT).show();
                    Log.e("JSONException","" +result.getContents());
                }
            }
        } else {
            super.onActivityResult(requestCode, resultCode, data);
        }


        switch (requestCode){
            case LIST_Refresh:  // 모임 추가 완료 후
                if (resultCode == RESULT_OK){   // 수정 버튼은 추가 액티비티에서 SaveButton을 클릭하면 RESULT_OK 라는 값을 넘겨줌.

                    Toast.makeText(Make_list_Activity.this,"모임 생성 완료.",Toast.LENGTH_SHORT).show();
//
//                    myDataset.clear(); // 데이터 세트를 모두 클리어 한다.
//                    mAdapter.notifyDataSetChanged();
//
//                    // 모임을 개설하고 새롭게 리스트를 표현할때 불러오는 AsyncTask
//                    GetData task = new GetData();   // 그 아이디를 기준으로 스레드 실행해서 나머지 데이터 가져오기
//                    task.execute(receive0);    // 받아온 아이디를 넘겨준다.
                }
                break;
        }
    }// onActivityResult 끝





    private class Attendence_Confirm extends AsyncTask<String, Void, String> {
        ProgressDialog progressDialog =new ProgressDialog(Make_list_Activity.this);
        String errorString = null;
        @Override
        protected void onPreExecute() {     // 시작전
            progressDialog.setProgressStyle(ProgressDialog.STYLE_SPINNER);
            progressDialog.setMessage("출석 확인 중 입니다...");
            // show dialog
            progressDialog.show();
            super.onPreExecute();
        }
        @Override
        protected void onPostExecute(String result) {   // 완료 후
            super.onPostExecute(result);
            Log.d(TAG, "response - " + result);


            if (result == null){    // 값을 받아 오지 못했다면
                Toast.makeText(Make_list_Activity.this,"값을 받아오지 못했습니다.",Toast.LENGTH_SHORT).show();
            }
            else if(result.equals("신청자존재안함")){
                Toast.makeText(Make_list_Activity.this,"존재하지 않는 신청자 입니다..",Toast.LENGTH_SHORT).show();
            }
            else if(result.equals("신청자확인완료")){
                Toast.makeText(Make_list_Activity.this,"출석 확인 완료",Toast.LENGTH_SHORT).show();
            }
            else if(result.equals("신청자중복오류")){
                Toast.makeText(Make_list_Activity.this,"중복되는 신청자 입니다. 시스템 오류가 있습니다",Toast.LENGTH_SHORT).show();
            }
            else if(result.equals("값을 받아오지 못했습니다.")){
                Toast.makeText(Make_list_Activity.this,"데이터 전달이 제대로 안됨",Toast.LENGTH_SHORT).show();
            }
            else {
                Toast.makeText(Make_list_Activity.this,"SQL문 처리중 에러 발생",Toast.LENGTH_SHORT).show();
                Log.e(TAG, "SQL문 처리중 에러 발생 - " + result);
            }
            progressDialog.dismiss();   // 다이얼로그 해제
        }

        @Override
        protected String doInBackground(String... params) {

            String QR_cord_M_NO = params[0];   // 받아온 QR 코드 모임번호
            String QR_cord_M_ID = params[1];   // 받아온 QR 코드 아이디

            Log.e("받아온 QR 코드 모임번호 =",QR_cord_M_NO);
            Log.e("받아온 QR 코드 아이디 =",QR_cord_M_ID);

            String serverURL = "http://49.247.208.191/AndroidHive/Apply/Founder/Attendence_Confirm.php";   // AndroidHive/Apply/Founder/Attendence_Confirm.php 의 파일로 이동
            String postParameters = "QR_cord_M_NO=" + QR_cord_M_NO + "&QR_cord_M_ID=" + QR_cord_M_ID;   // 모임번호, 아이디 전달

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
    }// Attendence_Confirm 끝












    @Override
    protected void onRestart() {
        super.onRestart();

        myDataset.clear(); // 데이터 세트를 모두 클리어 한다.
        mAdapter.notifyDataSetChanged();

        // 모임을 개설하고 새롭게 리스트를 표현할때 불러오는 AsyncTask
        GetData task = new GetData();   // 그 아이디를 기준으로 스레드 실행해서 나머지 데이터 가져오기
        task.execute(receive0);    // 받아온 아이디를 넘겨준다.

        Log.e("onStart 끝 ", "");
    }// onStart 끝





    private class GetData extends AsyncTask<String, Void, String> {

        ProgressDialog progressDialog =new ProgressDialog(Make_list_Activity.this);
        String errorString = null;

        @Override
        protected void onPreExecute() {     // 시작전
            progressDialog.setProgressStyle(ProgressDialog.STYLE_SPINNER);
            progressDialog.setMessage("리스트 확인 중 입니다...");
            // show dialog
            progressDialog.show();
            super.onPreExecute();
//            progressDialog = ProgressDialog.show(Make_list_Activity.this, "Please Wait", null, true, true); // 다이얼로그 실행
        }


        @Override
        protected void onPostExecute(String result) {   // 완료 후
            super.onPostExecute(result);
            Log.d(TAG, "response - " + result);


            if (result == null){    // 값을 받아 오지 못했다면
                Toast.makeText(Make_list_Activity.this,"값을 받아오지 못했습니다.",Toast.LENGTH_SHORT).show();
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

            String 로그인ID = params[0];   // 현재 접속자 아이디 =  개설자 이다.

            Log.e("로그인ID =",로그인ID);

            String serverURL = "http://49.247.208.191/AndroidHive/Opened_Moim_Confirm.php";   // AndroidHive/Opened_Moim_Confirm.php 의 파일로 이동
            String postParameters = "로그인ID=" + 로그인ID;

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
        try {
            JSONObject jsonObject = new JSONObject(mJsonString);
            JSONArray jsonArray = jsonObject.getJSONArray(TAG_JSON);

            for(int i=0;i<jsonArray.length();i++){  // 불러오는 객체가 1개 이상이면 더 늘어남. 기본 = 1

                String 제이슨길이 = String.valueOf(jsonArray.length());  // 제이슨 길이 int 를 스트링 으로
                Log.e("제이슨어레이 길이 = ",제이슨길이);

                JSONObject item = jsonArray.getJSONObject(i);

                String no = item.getString(TAG_NO);                             // 모임 번호
                String creator = item.getString(TAG_CREATOR);                   // 모임 개설자
                String default_imgURI = item.getString(TAG_DEFAULT_imgURI);     // 이미지
                String Name = item.getString(TAG_NAME);                         // 모임명

                String Total_People = item.getString(TAG_TOTAL_PEOPLE);         // 총인원수
                String 모집인원 = "모집인원 "+Total_People+"명"; // 모집인원 ?? 명 = 형태로 변환시키기 위해

                String Place_Data = item.getString(TAG_Place_Data);
                Log.e("위치데이터 = ",Place_Data);


                Log.e("--------------------- ","");
                Log.e("모임번호 = ",no);
                Log.e("개설자 = ",creator);
                Log.e("이미지 URI = ",default_imgURI);
                Log.e("모임 이름= ",Name);
                Log.e("모집인원 = ",모집인원);

//                myDataset.add(new Opened_Moim_Data(Name,default_imgURI,Total_People));  // 텍스트, 이미지

                myDataset.add(new Opened_Moim_Data(Name,모집인원,default_imgURI,no ,creator,Place_Data));  // 텍스트, 이미지

            }// 반복문 종료
            mAdapter.notifyDataSetChanged();    // 데이터 추가가 완료되었으면 notifyDataSetChanged() 메서드를 호출해 데이터 변경 체크를 실행합니다. = 없어도 되긴 하던데
        } catch (JSONException e) {
            Log.d(TAG, "showResult : ", e);
        }
    }// showResult 끝





    /**
     *  모임을 삭제 할것인지 묻는 경고창 - 모임 롱클릭 시에 실행된다.
     */
    void show()
    {
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setTitle("경고");
        builder.setMessage("모임을 정말로 삭제 하시겠습니까?");

        builder.setPositiveButton("취소",
                new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int which) {
                        Toast.makeText(getApplicationContext(),"취소를 선택했습니다.",Toast.LENGTH_LONG).show();
                    }
                });
        builder.setNeutralButton("확인",
                new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int which) {
                        DeleteData delete_task = new DeleteData();
                        delete_task.execute(Delete_Num);    // 삭제할 모임 번호를 DeleteData 에 전송한다.

                        Toast.makeText(getApplicationContext(),"모임을 삭제하였습니다. ",Toast.LENGTH_LONG).show();
                    }
                });
        builder.show();
    }


    /**
     * 모임 삭제 를 위한 DeleteData AsyncTask
     * 아이템 롱클릭 시 실행된다.
     *
     */
    private class DeleteData extends AsyncTask<String, Void, String> {
        ProgressDialog progressDialog = new ProgressDialog(Make_list_Activity.this);
        String errorString = null;
        @Override
        protected void onPreExecute() {     // 시작전
            progressDialog.setProgressStyle(ProgressDialog.STYLE_SPINNER);
            progressDialog.setMessage("모임 삭제 중 입니다...");
            // show dialog
            progressDialog.show();
            super.onPreExecute();
//            progressDialog = ProgressDialog.show(Make_list_Activity.this, "Please Wait", null, true, true); // 다이얼로그 실행
        }
        @Override
        protected void onPostExecute(String result) {   // 완료 후
            super.onPostExecute(result);
            Log.d(TAG, "response - " + result);


            if (result == null){    // 값을 받아 오지 못했다면
                Toast.makeText(Make_list_Activity.this,"삭제에 실패하였습니다.",Toast.LENGTH_SHORT).show();
            }
            else if(result.equals("삭제완료")){     // 삭제완료가 넘어오면
                Toast.makeText(Make_list_Activity.this,"모임이 삭제되었습니다.",Toast.LENGTH_SHORT).show();
                Log.e("결과 = ",""+result);

                // 리스트를 다시 뿌려주기 위해서
                myDataset.clear(); // 데이터 세트를 모두 클리어 한다.
                mAdapter.notifyDataSetChanged();

                // 모임을 개설하고 새롭게 리스트를 표현할때 불러오는 AsyncTask
                GetData task = new GetData();   // 그 아이디를 기준으로 스레드 실행해서 나머지 데이터 가져오기
                task.execute(receive0);    // 받아온 아이디를 넘겨준다.

            }
            Log.e("다이얼로그 해제됨","");
            progressDialog.dismiss();   // 다이얼로그 해제
        }


        @Override
        protected String doInBackground(String... params) {

            String DELETE_NUM = params[0];   // 삭제될 모임 번호

            Log.e("삭제될 모임번호 =",DELETE_NUM);

            String serverURL = "http://49.247.208.191/AndroidHive/Moim_delete.php";   // AndroidHive/Moim_delete.php 의 파일로 이동
            String postParameters = "DELETE_NUM=" + DELETE_NUM;

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








// 복사 시작 --------------------------------------------------------------------------------------------------------------------------------------------------------------
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

}// 코드 끝....





