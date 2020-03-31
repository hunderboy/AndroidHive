package com.example.user.androidhive;

import android.app.ProgressDialog;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Color;
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
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

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

public class LoginActivity extends AppCompatActivity {

    private DrawerLayout mDrawerLayout;

    public static int Login_State = 0; // 로그인 상태 변수. 기본은 0 = 로그아웃 상태

    // SF 기능 관련
    private static String TAG = "phptest_LoginActivity";

    private static final String TAG_JSON="webnautes";   // 제이슨 테그 이름

    private static final String TAG_ID = "id";          // 쉐어드 키값을 미리 설정
    private static final String TAG_NAME = "name";
    private static final String TAG_EMAIL = "email";
    private static final String TAG_PHONE ="phone";
    private static final String TAG_Profile_image ="Profile_image";


    String mJsonString;

    // 버튼 생성
    Button Login;
    Button Register;

    // 에디트 텍스트 생성
    EditText 아이디;
    EditText 패스워드;

    String Login_Success;   // 로그인 성공 스트링



    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);


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
        TextView profilename = (TextView) headerview.findViewById(R.id.text_email);
//        profilename.setText("헤더파일 수정중...");

        // 헤더뷰 레이아웃 아이디 설정
        LinearLayout header_user_info = (LinearLayout) headerview.findViewById(R.id.user_info);
        // 헤더뷰 레이아웃 클릭 리스너
        header_user_info.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Toast.makeText(LoginActivity.this, "header_user_info clicked", Toast.LENGTH_SHORT).show();
                Intent intent = new Intent(
                        getApplicationContext(), // 현재 화면의 제어권자
                        LoginActivity.class); // 다음 넘어갈 클래스 지정
                startActivity(intent); // 다음 화면으로 넘어간다

                mDrawerLayout.closeDrawers();   // 서랍 닫기
                finish();// 현재화면 종료
            }
        });
        // 헤더뷰 이미지 버튼 아아디 설정
        ImageButton header_userimage = (ImageButton) headerview.findViewById(R.id.user_image);
        // 헤더뷰 이미지 버튼 클릭 리스너
        header_userimage.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Toast.makeText(LoginActivity.this, "header_userimage clicked", Toast.LENGTH_SHORT).show();
                Intent intent = new Intent(
                        getApplicationContext(), // 현재 화면의 제어권자
                        LoginActivity.class); // 다음 넘어갈 클래스 지정
                startActivity(intent); // 다음 화면으로 넘어간다

                mDrawerLayout.closeDrawers();   // 서랍 닫기
                finish();// 현재화면 종료
            }
        });




        navigationView.setNavigationItemSelectedListener(new NavigationView.OnNavigationItemSelectedListener() {    // 네비게이션 아이템 클릭 리스너
            @Override
            public boolean onNavigationItemSelected(MenuItem menuItem) {        // 네비게이션 아이템 선택시에
                menuItem.setChecked(true);
                mDrawerLayout.closeDrawers();

                int id = menuItem.getItemId();
                switch (id) {

                    /**
                     * MY 구분부분
                     */
                    case R.id.nav_sub_menu_item01:  // 내 정보 버튼
                        if(Login_State == 0 ){// 아직 로그인 안했을 때
                            Toast.makeText(LoginActivity.this, "로그인 후에 확인 가능 합니다", Toast.LENGTH_SHORT).show();
                        }
                        else{
                            Intent intent = new Intent(
                                    getApplicationContext(), // 현재 화면의 제어권자
                                    Profle_Activity.class); // 다음 넘어갈 클래스 지정 - Profle_Activity
                            startActivity(intent); // 다음 화면으로 넘어간다
                        }break;
                    case R.id.nav_sub_menu_item02:  // 즐겨찾기 버튼
                        if(Login_State == 0 ){// 아직 로그인 안했을 때
                            Toast.makeText(LoginActivity.this, "로그인 후에 확인 가능 합니다", Toast.LENGTH_SHORT).show();
                        }
                        else{
                            Intent intent = new Intent(
                                    getApplicationContext(), // 현재 화면의 제어권자
                                    BookMarkActivity.class); // 다음 넘어갈 클래스 지정 - BookMarkActivity
                            startActivity(intent); // 다음 화면으로 넘어간다
                        }break;

                    case R.id.nav_sub_menu_item03:  // 내가 만든 모임 버튼
                        if(Login_State == 0 ){// 아직 로그인 안했을 때
                            Toast.makeText(LoginActivity.this, "로그인 후에 확인 가능 합니다", Toast.LENGTH_SHORT).show();
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
                            Toast.makeText(LoginActivity.this, "로그인 후에 확인 가능 합니다", Toast.LENGTH_SHORT).show();
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
                            Toast.makeText(LoginActivity.this, "로그인 후에 확인 가능 합니다", Toast.LENGTH_SHORT).show();
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
        // 툴바 관련 부분 끝





        // 버튼 연결
        Login = (Button) findViewById(R.id.btnLogin);                       // 로그인 버튼
        Register = (Button) findViewById(R.id.btnLinkToRegisterScreen);     // 회원가입 버튼

        // 에디트 텍스트 연결
        아이디 = (EditText) findViewById(R.id.login_id);   // 아이디
        패스워드 = (EditText) findViewById(R.id.password);  // 패스워드

        // 회원가입 버튼 클릭 리스너
        Register.setOnClickListener(new Button.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(
                        getApplicationContext(), // 현재 화면의 제어권자
                        RegisterActivity.class); // 다음 넘어갈 클래스 지정
                startActivity(intent); // 다음 화면으로 넘어간다
            }
        });

        // 로그인 버튼 클릭 리스너
        Login.setOnClickListener(new Button.OnClickListener() {
            @Override
            public void onClick(View view) {

                // 각 이메일과 패스워드를 스트링 으로 받아오고
                String 로그인ID = 아이디.getText().toString();
                String 로그인PASSWORD = 패스워드.getText().toString();



                if(로그인ID.isEmpty()){
                    Toast.makeText(LoginActivity.this,"아이디를 입력하세요.",Toast.LENGTH_SHORT).show();
                }
                else if(로그인PASSWORD.isEmpty()){
                    Toast.makeText(LoginActivity.this,"비밀번호를 입력하세요.",Toast.LENGTH_SHORT).show();
                }
                else{
                    InsertData task = new InsertData();     // 태스크 생성
                    task.execute(로그인ID,로그인PASSWORD);  // 태스크 진행하면서 아이디, 패스워드를 전달한다.
                }

                // 아이디 일치와 비밀번호 일치는 무조건 PHP 파일가서 확인을 해야 한다.
                // PHP 문으로 가서 일치 확인을 해야하고 쿼리를 날려야 한다.

            }
        });
    }// onCreate 끝



    // 로그인 데이터 처리하는 AsyncTask
    class InsertData extends AsyncTask<String, Void, String> {
        ProgressDialog progressDialog;

        @Override
        protected void onPreExecute() {     // 작업 시작전
            super.onPreExecute();

            progressDialog = ProgressDialog.show(LoginActivity.this, "Please Wait", null, true, true);   // 완료 될때까지 다이얼로그 생성
        }

        @Override
        protected void onPostExecute(String result) {   // 작업이 끝난후
            super.onPostExecute(result);
            progressDialog.dismiss();   // 다이얼로그 해제
//            mTextViewResult.setText(result);
            e(TAG, "POST response  = " + result);   // result 값을 echo 를 받아들여서 출력한다.

            if (result.equals("로그인실패")){    // 값을 받아 오지 못했다면
//                mTextViewResult.setText(errorString);
                Toast.makeText(LoginActivity.this,"존재하지 않는 아이디 또는 비밀번호가 틀립니다.",Toast.LENGTH_SHORT).show();
            }
            else {  //로그인 성공 json 값을 받아 왔으니 데이터를 넣자
                mJsonString = result;
                showResult();

                Toast.makeText(LoginActivity.this,"로그인에 성공하였습니다.",Toast.LENGTH_SHORT).show();

                Login_State = 1; // 로그인 상태

                Intent intent = new Intent(
                        getApplicationContext(), // 현재 화면의 제어권자
                        MainActivity.class); // 다음 넘어갈 클래스 지정
                intent.putExtra("아이디",아이디.getText().toString());    // 아이디 String 을 넘긴다.

                startActivity(intent); // 다음 화면으로 넘어간다

                finish();   // 현재 액티비티 종료
            }

            // 로그인 성공시
//            if(Login_Success.equals("로그인성공")){
//
//            }
//            else {  // 로그인 실패시
//                Toast.makeText(LoginActivity.this,"존재하지 않는 아이디 또는 비밀번호가 틀립니다.",Toast.LENGTH_SHORT).show();
//            }
        }

        @Override
        protected String doInBackground(String... params) {     // 실제 이뤄지는 작업

            String 로그인ID = (String)params[0];    // Params - 파라미터 타입은 작업 실행 시에 송신.
            String 로그인PASSWORD = (String)params[1];


            Log.e("아이디 확인 ="," "+ 로그인ID);
            Log.e("비밀번호 확인 ="," "+ 로그인PASSWORD);


            String serverURL = "http://49.247.208.191/login.php";    // 웹 기본 디렉토리 폴더의 insert.php 파일에 접속하라.
            String postParameters = "로그인ID=" + 로그인ID + "&로그인PASSWORD=" + 로그인PASSWORD;


            try {
                URL url = new URL(serverURL);
                HttpURLConnection httpURLConnection = (HttpURLConnection) url.openConnection();

                httpURLConnection.setReadTimeout(5000);
                httpURLConnection.setConnectTimeout(5000);
                httpURLConnection.setRequestMethod("POST");     // 메소드 타입 POST
                //httpURLConnection.setRequestProperty("content-type", "application/json");
                httpURLConnection.setDoInput(true);
                httpURLConnection.connect();

                OutputStream outputStream = httpURLConnection.getOutputStream();
                outputStream.write(postParameters.getBytes("UTF-8"));
                outputStream.flush();
                outputStream.close();

                int responseStatusCode = httpURLConnection.getResponseCode();
                e(TAG, "POST response code - " + responseStatusCode);
                InputStream inputStream;
                if (responseStatusCode == HttpURLConnection.HTTP_OK) {
                    inputStream = httpURLConnection.getInputStream();
                } else {
                    inputStream = httpURLConnection.getErrorStream();
                }
                InputStreamReader inputStreamReader = new InputStreamReader(inputStream, "UTF-8");
                BufferedReader bufferedReader = new BufferedReader(inputStreamReader);
                StringBuilder sb = new StringBuilder();
                String line = null;

                while ((line = bufferedReader.readLine()) != null) {
                    sb.append(line);
                }
                bufferedReader.close();
                return sb.toString();
            }
            catch (Exception e) {
                e(TAG, "InsertData: Error ", e);
                return new String("Error: " + e.getMessage());
            }
        }
    }


    /**
     * 로그인 후 해당 유저의 데이터들을 쉐어드 SF 에 저장하는 과정
     * 1. 아이디
     * 2. 이름
     * 3. 이메일
     * 4. 휴대폰 번호
     * 저장됨
     */
    private void showResult(){
        try {
            JSONObject jsonObject = new JSONObject(mJsonString);
            JSONArray jsonArray = jsonObject.getJSONArray(TAG_JSON);

            for(int i=0;i<jsonArray.length();i++){
                JSONObject item = jsonArray.getJSONObject(i);

                // 태그에 맞춰서 php 파일에서 받아온 데이터들을 각 스트링에 넣는다.
                String id = item.getString(TAG_ID);
                String name = item.getString(TAG_NAME);
                String email = item.getString(TAG_EMAIL);
                String phone = item.getString(TAG_PHONE);
                String Profile_image = item.getString(TAG_Profile_image);    // 프로필 이미지


                // echo로 가져온 데이터를 쉐어드에 저장해야 한다.
                // SharedPreferences 저장
                SharedPreferences SF = getSharedPreferences("SF", MODE_PRIVATE);     // SharedPreferences 객체 생성
                SharedPreferences.Editor editor = SF.edit();    //저장하려면 editor가 필요

//                editor.putString("SFKey_id", id);  // 입력   // Editor 객체.putString(키,데이터)  // 결국 키값에 객체를 저장하는거 아닌가? - 그 객체에 담긴 데이터를 끄집에 내는것.
//                editor.putString("SFKey_name", name);   // 입력
//                editor.putString("SFKey_email", email);   // 입력
//                editor.putString("SFKey_phone", phone);   // 입력

                JSONArray SF_JsonArray = new JSONArray();      // 쉐어드 제이슨어레이 객체 준비

                JSONObject SF_JsonObject = new JSONObject();   // 쉐어드 제이슨 객체 생성

                SF_JsonObject.put("Login_ID", id);          // 제이슨 오브젝트에 키값과 데이터 설정
                SF_JsonObject.put("Login_NAME", name);
                SF_JsonObject.put("Login_EMAIL", email);
                SF_JsonObject.put("Login_PHONE", phone);
                SF_JsonObject.put("Login_Profile_image", Profile_image);

                // 데이터 로그 찍어보기
                Log.e("화면 = ",TAG);
                Log.e("Login_ID = ",id);
                Log.e("Login_NAME = ",name);
                Log.e("Login_EMAIL = ",email);
                Log.e("Login_PHONE = ",phone);
                Log.e("Login_Profile_image = ",Profile_image);


                SF_JsonArray.put(SF_JsonObject);    // 쉐얻 제이슨 어레이 에 제이슨 오브젝트를 집어 넣는다.

                editor.putString("제이슨어레이 KEY", String.valueOf(SF_JsonArray)); // SF에 키값이 "제이슨어레이 KEY" 인 제이슨어레이를 저장.
                editor.apply();    // 파일에 최종 반영함

            }

        } catch (JSONException e) {

            Log.d(TAG, "showResult : ", e);
        }

    }





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

}
