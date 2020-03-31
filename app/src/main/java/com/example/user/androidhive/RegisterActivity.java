package com.example.user.androidhive;

import android.app.ProgressDialog;
import android.content.Intent;
import android.graphics.Color;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.design.widget.NavigationView;
import android.support.v4.view.GravityCompat;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.ActionBar;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.text.Editable;
import android.text.TextUtils;
import android.text.TextWatcher;
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

import java.io.BufferedReader;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.regex.Pattern;

import static android.util.Log.e;
import static com.example.user.androidhive.LoginActivity.Login_State;


public class RegisterActivity extends AppCompatActivity {
    private DrawerLayout mDrawerLayout;
    private static String TAG = "RegisterActivity.회원가입화면";

    // 에디트택스트 아이디, 패스워드, 패스워드 확인
    private EditText mEditText_Name;
    private EditText mEditText_ID;
    private EditText mEditText_Email;
    private EditText mEditText_et_pw;
    private EditText mEditTextet_pw_chk;
    private EditText mEditText_PhoneNum;

    // 텍스트뷰 설정
    private TextView mTextView_ID_massage;
    private TextView mTextView_PW_massage1;
    private TextView mTextView_PW_massage;



    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_register);


        // 여기서 부터 모두 복사해야 함.
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
                Toast.makeText(RegisterActivity.this, "header_user_info clicked", Toast.LENGTH_SHORT).show();
                Intent intent = new Intent(
                        getApplicationContext(), // 현재 화면의 제어권자
                        LoginActivity.class); // 다음 넘어갈 클래스 지정
                startActivity(intent); // 다음 화면으로 넘어간다

                mDrawerLayout.closeDrawers();   // 서랍 닫기
                finish();// 현재 액티비티 종료
            }
        });


        // 헤더뷰 이미지 버튼 아아디 설정
        ImageButton header_userimage = (ImageButton) headerview.findViewById(R.id.user_image);
        // 헤더뷰 이미지 버튼 클릭 리스너
        header_userimage.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Toast.makeText(RegisterActivity.this, "header_userimage clicked", Toast.LENGTH_SHORT).show();
                Intent intent = new Intent(
                        getApplicationContext(), // 현재 화면의 제어권자
                        LoginActivity.class); // 다음 넘어갈 클래스 지정
                startActivity(intent); // 다음 화면으로 넘어간다
                mDrawerLayout.closeDrawers();   // 서랍 닫기
                finish();// 현재 액티비티 종료
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
                     *
                     */
                    case R.id.nav_sub_menu_item01:  // 내 정보 버튼
                        if(Login_State == 0 ){// 아직 로그인 안했을 때
                            Toast.makeText(RegisterActivity.this, "로그인 후에 확인 가능 합니다", Toast.LENGTH_SHORT).show();
                        }
                        else{
                            Intent intent = new Intent(
                                    getApplicationContext(), // 현재 화면의 제어권자
                                    Profle_Activity.class); // 다음 넘어갈 클래스 지정 - Profle_Activity
                            startActivity(intent); // 다음 화면으로 넘어간다
                        }break;
                    case R.id.nav_sub_menu_item02:  // 즐겨찾기 버튼
                        if(Login_State == 0 ){// 아직 로그인 안했을 때
                            Toast.makeText(RegisterActivity.this, "로그인 후에 확인 가능 합니다", Toast.LENGTH_SHORT).show();
                        }
                        else{
                            Intent intent = new Intent(
                                    getApplicationContext(), // 현재 화면의 제어권자
                                    BookMarkActivity.class); // 다음 넘어갈 클래스 지정 - BookMarkActivity
                            startActivity(intent); // 다음 화면으로 넘어간다
                        }break;

                    case R.id.nav_sub_menu_item03:  // 내가 만든 모임 버튼
                        if(Login_State == 0 ){// 아직 로그인 안했을 때
                            Toast.makeText(RegisterActivity.this, "로그인 후에 확인 가능 합니다", Toast.LENGTH_SHORT).show();
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
                            Toast.makeText(RegisterActivity.this, "로그인 후에 확인 가능 합니다", Toast.LENGTH_SHORT).show();
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
                            Toast.makeText(RegisterActivity.this, "로그인 후에 확인 가능 합니다", Toast.LENGTH_SHORT).show();
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



        // 에디트 텍스터 연결
        mEditText_Name = (EditText) findViewById(R.id.name);                 // 이름
        mEditText_ID = (EditText) findViewById(R.id.nickname);               // 아이디
        mEditText_Email = (EditText) findViewById(R.id.email);               // 이메일
        mEditText_et_pw = (EditText) findViewById(R.id.password);            // 패스워드
        mEditTextet_pw_chk = (EditText) findViewById(R.id.password_check);   // 패스워드 확인
        mEditText_PhoneNum = (EditText) findViewById(R.id.phonenum);         // 휴대폰번호

        // 텍스트뷰 연결
        mTextView_ID_massage = (TextView) findViewById(R.id.id_massage);        // 이메일 메세지1
        mTextView_PW_massage1 = (TextView) findViewById(R.id.password_1);       // 패스워드 메세지1
        mTextView_PW_massage = (TextView) findViewById(R.id.password_massage);  // 패스워드 메세지2



        // 입력 변화 텍스트 처리 = 아이디 에서
        mEditText_ID.addTextChangedListener(new TextWatcher() {
            @Override // 입력되는 텍스트에 변화가 있을 때
            public void onTextChanged(CharSequence s, int start, int before, int count) {
            }

            @Override // 입력이 끝났을 때
            public void afterTextChanged(Editable arg0) {

                String user_id = mEditText_ID.getText().toString();     // 아이디 받아오기

                if(!Pattern.matches("^[a-z]{1}[a-z0-9]{4,11}$", user_id)) {  // 시작은 영문(소문자)으로만, 영문(소문자),숫자로 만 이루어진 4~11자, 대신 영문으로만 있어도 되고 숫자로만 있어도 됨

//                    [a-zA-Z]{1}[a-zA-Z0-9_]{4,11}  //   예) 시작은 영문으로만, '_'를 제외한 특수문자 안되며 영문, 숫자, '_'으로만 이루어진 5 ~ 12자 이하
                    // 유효성에 맞지 않으면
                    mTextView_ID_massage.setText("첫문자는 영문(소문자)으로만, 영문(소문자),숫자 만 포함된 4~11자 이어야 합니다.");
                    mTextView_ID_massage.setVisibility(View.VISIBLE);   // 글자 보이기
                }
                else {  // 유효성에 맞으면
                    mTextView_ID_massage.setVisibility(View.GONE);  // 글자 숨기기
                }
            }

            @Override // 입력하기 전에
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {
            }
        });


        // 입력 변화 텍스트 처리 = 비밀번호 에서
        mEditText_et_pw.addTextChangedListener(new TextWatcher() {
            @Override // 입력되는 텍스트에 변화가 있을 때
            public void onTextChanged(CharSequence s, int start, int before, int count) {
            }

            @Override // 입력이 끝났을 때
            public void afterTextChanged(Editable arg0) {
                String password = mEditText_et_pw.getText().toString();   // 패스워드 받아오기
                String confirm = mEditTextet_pw_chk.getText().toString(); // 패스워드 확인 받아오기

                if(!Pattern.matches("^(?=.*[a-z])(?=.*[0-9])(?=.*[!@#$%^&+=]).{4,11}$", password)) {  // 영소문자, 숫자, 특수문자 포함. 8 ~ 15 문자

                    // 포함해야 한다!
//                    [a-zA-Z]{1}[a-zA-Z0-9]{4,11}
//                    [a-z][A-Z][0-9][!@#$%^&+=]{8,15}  // 영문소문자, 대문자, 숫자, 특수문자 만 포함된 8-15 자리 숫자.

//                    [a-zA-Z0-9!@#$%^&+=]{4,11}    // 영문소문자,숫자,특수문자 만 포함된 4 11 자
//                    .*(?=^.{8,15}$)(?=.*\d)(?=.*[a-z])(?=.*[!@#$%^&+=]).*$

                    // 유효성에 맞지 않으면
                    mTextView_PW_massage1.setText("영소문자,숫자,특수문자 만 포함 8~15자 이어야 합니다.");
                    mTextView_PW_massage1.setVisibility(View.VISIBLE);   // 글자 보이기
                }
                else {  // 유효성에 맞으면
                    mTextView_PW_massage1.setVisibility(View.GONE);  // 글자 숨기기
                }
            }

            @Override // 입력하기 전에
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {
            }
        });




        // 입력 변화 텍스트 처리 = 비밀번호 Check 에서
        mEditTextet_pw_chk.addTextChangedListener(new TextWatcher() {
            @Override // 입력되는 텍스트에 변화가 있을 때
            public void onTextChanged(CharSequence s, int start, int before, int count) {
            }

            @Override // 입력이 끝났을 때
            public void afterTextChanged(Editable arg0) {

                String password = mEditText_et_pw.getText().toString();
                String confirm = mEditTextet_pw_chk.getText().toString();

                if ( password.equals(confirm) ) {   // 비밀번호가 서로 일치 할 때

                    if(!Pattern.matches("^(?=.*[a-z])(?=.*[0-9])(?=.*[!@#$%^&+=]).{4,11}$", password)) {  // 영소문자 or 영대문자(필수아님), 숫자, 특수문자 포함. 8 ~ 15 문자
                        // 유효성에 맞지 않으면
                        mTextView_PW_massage.setText("영소문자,숫자,특수문자 포함 8~15자 이어야 합니다.");
                        mTextView_PW_massage.setTextColor(Color.RED);     // 글자 빨강색으로 변경
                        mTextView_PW_massage.setVisibility(View.VISIBLE);   // 글자 보이기
                    }
                    else {  // 유효성에 맞으면
                        mTextView_PW_massage.setVisibility(View.GONE);  // 글자 숨기기

                    }
                }

                else {  // 일치하지 않을 때
                    mTextView_PW_massage.setText("비밀번호가 일치하지 않습니다.");
                    // 유효성에 맞지 않으면
                    mTextView_PW_massage.setVisibility(View.VISIBLE);   // 글자 보이기
                    mTextView_PW_massage.setTextColor(Color.RED);     // 글자 빨강색으로 변경
                }
            }

            @Override // 입력하기 전에
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {
            }
        });




        // 버튼 연결
        Button register_button = (Button)findViewById(R.id.btnRegister);

        // 등록 버튼 클릭 리스너
        register_button.setOnClickListener(new View.OnClickListener() {     // 등록버튼 눌렀을 시에
            @Override
            public void onClick(View v) {
                // 적혀진 데이터를 String 에 전달하기 - 이 전달된 데이터가 DB 로 보낼 데이터 들이다.
                String name = mEditText_Name.getText().toString();              // 이름
                String nickname = mEditText_ID.getText().toString();            // 닉네임
                String e_mail = mEditText_Email.getText().toString();           // 이메일
                String pw = mEditText_et_pw.getText().toString();               // 패스워드
                String pw_check = mEditTextet_pw_chk.getText().toString();      // 패스워드 확인
                String phone_number = mEditText_PhoneNum.getText().toString();  // 휴대폰번호

                // 하나로도 빈칸이 있으면
                if( TextUtils.isEmpty(name) || TextUtils.isEmpty(nickname) || TextUtils.isEmpty(e_mail) || TextUtils.isEmpty(pw) || TextUtils.isEmpty(pw_check) || TextUtils.isEmpty(phone_number) ) {
                    Toast.makeText(RegisterActivity.this,"모든 칸을 채워주세요.",Toast.LENGTH_SHORT).show();
                    return;
                }
                // 이메일 형식 체크
                else if(!android.util.Patterns.EMAIL_ADDRESS.matcher(e_mail).matches()) {
                    Toast.makeText(RegisterActivity.this,"올바른 이메일 형식이 아닙니다",Toast.LENGTH_SHORT).show();
                    return;
                }
                //비밀번호 유효성
                else if(!Pattern.matches("^(?=.*[a-z])(?=.*[0-9])(?=.*[!@#$%^&+=]).{4,11}$", pw)) {      // 영소문자 , 숫자, 특수문자 포함. 8 ~ 15 문자
                    Toast.makeText(RegisterActivity.this,"비밀번호 형식을 지켜주세요.",Toast.LENGTH_SHORT).show();  // 영 대문자 막아 놨다 내가
                    return;
                }
                // 비밀번호가 일치하지 않으면
                else if(!pw.equals(pw_check)){
                    Toast.makeText(RegisterActivity.this,"비밀번호가 일치하지 않습니다.",Toast.LENGTH_SHORT).show();
                    return;
                }
                 //핸드폰번호 유효성
                else if(!Pattern.matches("^((01[1|6|7|8|9])[1-9]+[0-9]{6,7})|(010[1-9][0-9]{7})$", phone_number)) {      // 휴대폰번호 - 제외 삽입
                    Toast.makeText(RegisterActivity.this,"올바른 핸드폰 번호가 아닙니다.",Toast.LENGTH_SHORT).show();
                    return;
                }

                // 모든 예외 통과시에
                else{

                    InsertData task = new InsertData();     // AsyncTask 의 객체 생성
                    task.execute(name ,nickname ,e_mail ,pw ,phone_number);  // 태스크 진행하면서 이름, 이메일, 패스워드, 휴대폰번호 넣는다.
                }

            }
        });
    }



    // 어싱크테스크 에서 데이터 전달
    class InsertData extends AsyncTask<String, Void, String> {
        ProgressDialog progressDialog;

        @Override
        protected void onPreExecute() {     // 작업 시작전
            super.onPreExecute();
            progressDialog = ProgressDialog.show(RegisterActivity.this, "Please Wait", null, true, true);   // 완료 될때까지 다이얼로그 생성
        }

        @Override
        protected void onPostExecute(String result) {   // 작업이 끝난후
            super.onPostExecute(result);

            progressDialog.dismiss();   // 다이얼로그 해제
//            mTextViewResult.setText(result);
            e(TAG, "POST response  - " + result);   // result 값을 echo 를 받아들여서 출력한다.

            if(result.equals("닉네임존재")){
                Toast.makeText(RegisterActivity.this,"존재하는 아이디 입니다.",Toast.LENGTH_SHORT).show();
            }
            else if(result.equals("이메일존재")){
                Toast.makeText(RegisterActivity.this,"존재하는 이메일 입니다.",Toast.LENGTH_SHORT).show();
            }
            else{
                Toast.makeText(RegisterActivity.this,"회원가입 되었습니다.",Toast.LENGTH_SHORT).show();

                finish();   // 현재 액티비티 종료함.
            }
        }


        @Override
        protected String doInBackground(String... params) {     // 실제 이뤄지는 작업

            String name = (String)params[0];    // Params - 파라미터 타입은 작업 실행 시에 송신.
            String nickname = (String)params[1];
            String e_mail = (String)params[2];
            String pw = (String)params[3];
            String phone_number = (String)params[4]; // 폰번호

            Log.e("이름 확인 ="," "+ name);
            Log.e("아이디 확인 ="," "+ nickname);
            Log.e("이메일 확인 ="," "+ e_mail);
            Log.e("패스워드 확인 ="," "+ pw);
            Log.e("폰번호 확인 ="," "+ phone_number);

            // 010 2101 8817 => 010-2101-8817 문자열 자르기 작업
            String front_Num = phone_number.substring(0,3); // = 010 추출 , (시작점,?-1=끝점)
            String Mid_Num = phone_number.substring(3,7);   // = 중간번호 추출
            String End_Num = phone_number.substring(7,11);   // = 끝번호 추출

            Log.e("폰 앞번호 ="," "+ front_Num);
            Log.e("폰 중간번호 ="," "+ Mid_Num);
            Log.e("폰 끝번호 ="," "+ End_Num);

            String cutting_phone_number = front_Num + "-" + Mid_Num + "-" + End_Num; // 휴대폰 문자열 자르기
            Log.e("최종 폰번호 ="," "+ cutting_phone_number);


            String serverURL = "http://49.247.208.191/insert2.php";    // 웹 기본 디렉토리 폴더의 insert.php 파일에 접속하라.
            String postParameters = "name=" + name + "&nickname=" + nickname + "&e_mail=" + e_mail + "&password=" + pw + "&phone_number=" + cutting_phone_number;


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
                if(responseStatusCode == HttpURLConnection.HTTP_OK) {
                    inputStream = httpURLConnection.getInputStream();
                }
                else{
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
                e(TAG, "InsertData: Error ", e);
                return new String("Error: " + e.getMessage());
            }

        }
    }






    // 복사 시작
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
    // 복사 끝




}
