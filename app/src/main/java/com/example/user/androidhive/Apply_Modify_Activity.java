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
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import com.bumptech.glide.Glide;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.net.HttpURLConnection;
import java.net.URL;

import static com.example.user.androidhive.LoginActivity.Login_State;
import static com.example.user.androidhive.MainActivity.receive0;

public class Apply_Modify_Activity extends AppCompatActivity {
    // 복사 시작
    private DrawerLayout mDrawerLayout;
    private static String TAG = "Apply_Modify_Activity";
    // 복사 끝


    // 서버에서 받아온 데이터(JSON 형태)를 담을 변수들 - 전역변수
    String mJsonString; // JSON 전체 데이터를 담는 변수
    private static final String TAG_JSON="webnautes";   // JSON 키

    /**
     * Json 으로 오는 데이터들중
     * 모임 데이터 로 오는 KEY 값 선언
     * 모임 데이터 관련
     */
    private static final String TAG_NO = "no";                                      // 1. 모임번호

    private static final String TAG_DEFAULT_imgURI = "default_imgURI";              // 2. 모임기본이미지 URI
    private static final String TAG_NAME = "Name";                                  // 3. 모임명
    private static final String TAG_Selection_method = "Selection_method";          // 4. 선정 방법 = 선착순, 개설자 선정
    private static final String TAG_Application_method = "Application_method";      // 5. 신청 방식 = 유,무료

    /**
     * Json 으로 오는 데이터들중
     * 신청자 데이터 로 오는 KEY 값 선언
     * 신청자 데이터 관련
     */
    private static final String TAG_Applicant_ID = "Applicant_ID";          // 1. 신청자 아이디
    private static final String TAG_Applicant_Name = "Applicant_Name";      // 2. 신청자 이름
    private static final String TAG_Applicant_Email = "Applicant_Email";    // 3. 신청자 이메일
    private static final String TAG_Applicant_Phone = "Applicant_Phone";    // 4. 신청자 휴대폰 번호






    /**
     * 데이터들을 DB 에서 가져 와야지
     * 모임 번호를 전달 받아서 getNO - 모임데이터를 가져오고
     * 모임 번호를 통해
     * 1. 모임기본이미지
     * 2. 모임명.
     * 3. 모임 선정방법
     * 4. 지원 방법
     * // 유료의 경우
     * 5. 은행명
     * 6. 예금주
     * 7. 계좌번호
     * 8. 결제금액
     *
     * 신청자 데이터 가져오기
     * 1. 아이디
     * 2. 이름
     * 3. 이메일
     * 4. 전화번호
     *
     */

//------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------

    // 로그인 할때와 유지할때 사용되는 변수(onStart 에서)
    // + 신청자 데이터 설정할때에도 사용
    String Login_NAME;       // 신청자 이름
    String Login_EMAIL;      // 신청자 이메일
    String Login_PHONE;      // 신청자 휴대폰


    // 신청리스트 화면에서 신청내용 수정버튼 클릭 시  인텐트로 넘어온 데이터들을 받을 변수를 전역변수로 지정
    String received_no;     // 모임 번호 - 모임 번호는 실제로 모임을 신청할때 사용된다.
    String received_ID;     // ID

//------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------

    /** 상단 모임 관련 데이터 위젯 */
    ImageView default_image;    // 기본이미지
    TextView Moim_name;         // 모임명
    TextView Moim_Selection_method;  // 선정방법 = 선착순 or 개설자 선정
    TextView Charged_Text;      // 유료 무료


    /** 중단 신청자 관련 데이터 위젯 */
    TextView applicant_ID;              // 신청자 ID
    TextView applicant_NAME;            // 신청자 이름
    TextView applicant_EMAIL;           // 신청자 이메일

    EditText applicant_Middle_phoneNUM; // 신청자 중간폰번호

    // 중단 신청자 연락처 데이터 설정 부분
    String[] arr;   // 휴대폰 번호를 나눠서 가질 배열 - 전역변수 선언
    // 앞번호
    String Front_num = "";


    /** 하단 유료일 경우 나타날 데이터의 위젯 */
    TextView BANK_Textview;              // 은행명
    TextView ACCOUNT_Textview;           // 예금주
    TextView PIRCE_Textview;             // 계좌번호
    TextView HOLDER_Textview;            // 결제금액

//------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------








    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_apply__modify_);

        // 신청 내역 수정버튼 클릭 시 넘어오는 모임번호와 아이디로
        // 해당 모임의 아이디 사용자의 신청 데이터를 가져온다.
        Intent intent = getIntent();
        received_no = intent.getStringExtra("NUMBER"); // 모임 번호
        received_ID = intent.getStringExtra("ID");     // 아이디

        // 해당 AsyncTask 통해서 신청데이터를 가져온다.
        Get_Moim_Data task2 = new Get_Moim_Data();
        task2.execute(received_no,received_ID);


//------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------
        /**
         *  상단 모임 데이터 설정 부분
         */
        // 상단 모임 관련 데이터 위젯 설정
        default_image = (ImageView)findViewById(R.id.default_image);       // 기본이미지
        Moim_name = (TextView)findViewById(R.id.Moim_name);                // 모임명
        Moim_Selection_method = (TextView)findViewById(R.id.Selection_method);  // 선정방법 = 선착순 or 개설자 선정
        Charged_Text = (TextView)findViewById(R.id.Charged_Text);          // 신청방식 = 유,무료


        /**
         * 중단 신청자 관련 데이터 위젯 설정 부분
         */
        applicant_ID = (TextView)findViewById(R.id.ID_Textview);               // 신청자 ID
        applicant_NAME = (TextView)findViewById(R.id.NAME_Textview);             // 신청자 이름
        applicant_EMAIL = (TextView)findViewById(R.id.Email_Textview);            // 신청자 이메일
        applicant_Middle_phoneNUM = (EditText)findViewById(R.id.applicant_Middle_phoneNUM); // 신청자 중간 폰번호



//        /**
//         *  하단. 유료일 경우 결제관련 데이터 설정 부분
//         */
//        if(received_Application_method.equals("유료")){
//            // 결제 내용이 포함된 릴레이티브 레이아웃 객체 선언
//            RelativeLayout Charged_layout = (RelativeLayout)findViewById(R.id.Charged_layout);
//            Charged_layout.setVisibility(View.VISIBLE); // 레이아웃 보이게
//
//            TextView textView35 = (TextView)findViewById(R.id.textView35);
//            textView35.setVisibility(View.VISIBLE); // 결제정보 글자가 보이게
//
//            View view30 = (View)findViewById(R.id.view30);  // 구분선
//            view30.setVisibility(View.VISIBLE); // 구분선 보이게
//
//            // 하단 결제관련 데이터 위젯 설정
//            BANK_Textview = (TextView)findViewById(R.id.BANK_Textview);       // 은행명
//            ACCOUNT_Textview = (TextView)findViewById(R.id.ACCOUNT_Textview); // 예금주
//            HOLDER_Textview = (TextView)findViewById(R.id.HOLDER_Textview);   // 계좌번호
//            PIRCE_Textview = (TextView)findViewById(R.id.PIRCE_Textview);     // 결제금액
//
//            // 결제관련 데이터 세팅
//            BANK_Textview.setText(received_Bank);
//            ACCOUNT_Textview.setText(received_Account_number);
//            HOLDER_Textview.setText(received_Account_holder);
//            PIRCE_Textview.setText(received_Participation_fee);
//
//            /**
//             * 결제기능은
//             * 현재 개설자 통장입금 결제 방식 뿐이므로
//             * 통장입금 후 개설자가 확인하여 승인하면 참가자격이 주어진다. 라는 글귀 필요.
//             *
//             */
//
//        }
//------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------





        // 여기서 부터 모두 복사해야 함.
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
                            Toast.makeText(Apply_Modify_Activity.this, "로그인 후에 확인 가능 합니다", Toast.LENGTH_SHORT).show();
                        }
                        else{
                            Intent intent = new Intent(
                                    getApplicationContext(), // 현재 화면의 제어권자
                                    Profle_Activity.class); // 다음 넘어갈 클래스 지정 - Profle_Activity
                            startActivity(intent); // 다음 화면으로 넘어간다
                        }break;
                    case R.id.nav_sub_menu_item02:  // 즐겨찾기 버튼
                        if(Login_State == 0 ){// 아직 로그인 안했을 때
                            Toast.makeText(Apply_Modify_Activity.this, "로그인 후에 확인 가능 합니다", Toast.LENGTH_SHORT).show();
                        }
                        else{
                            Intent intent = new Intent(
                                    getApplicationContext(), // 현재 화면의 제어권자
                                    BookMarkActivity.class); // 다음 넘어갈 클래스 지정 - BookMarkActivity
                            startActivity(intent); // 다음 화면으로 넘어간다
                        }break;

                    case R.id.nav_sub_menu_item03:  // 내가 만든 모임 버튼
                        if(Login_State == 0 ){// 아직 로그인 안했을 때
                            Toast.makeText(Apply_Modify_Activity.this, "로그인 후에 확인 가능 합니다", Toast.LENGTH_SHORT).show();
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
                            Toast.makeText(Apply_Modify_Activity.this, "로그인 후에 확인 가능 합니다", Toast.LENGTH_SHORT).show();
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
                            Toast.makeText(Apply_Modify_Activity.this, "로그인 후에 확인 가능 합니다", Toast.LENGTH_SHORT).show();
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


        /** 수정하기 = modify_button 버튼 클릭 리스너 = 신청데이터가 수정되어야 함. */
        Button modify_button = (Button)findViewById(R.id.modify_button);
        modify_button.setOnClickListener(new Button.OnClickListener() {
            @Override
            public void onClick(View view) {
                /**
                 * 수정하기 버튼 클릭시
                 * 1. 모임번호 = received_no
                 * 2. 신청자 아이디 = received_ID
                 * 3. 신청자 이메일 = Login_EMAIL
                 * 4. 신청자 이름 = Login_NAME
                 * 5. 신청자 전화번호 =
                 * 를 전달하여 Apply_info 에 저장하고
                 * 무료 모임 일경우
                 * 1. 신청상태 = 참여확정
                 * 2. 출석상태 = 미출석
                 * 3. 신청일 = 현재 날짜
                 *
                 * 만약 무료모임에서 참석인원수가 초과 했을 떄 (0명 신청가능)
                 * 신청상태 = 대기자 처리
                 */

                // 신청자 전화번호 취합하기
                String Middle_NUM = applicant_Middle_phoneNUM.getText().toString(); // 전화번호 뒷부분 가져오기  EX) 21018817
                String sub1 = Middle_NUM.substring(0,4);    // 중간 번호
                String sub2 = Middle_NUM.substring(4);      // 끝 번호

                String PHONE = Front_num+"-"+sub1+"-"+sub2; //ex) 010-2251-8847 형태로

                // 무료 모임일 경우 기본적으로 참여확정 상태이다.
                // 모임번호 = received_no, 신청자 아이디 = received_ID, 이메일 = Login_EMAIL, 이름 = Login_NAME, 전화번호 = PHONE

                // 어짜피 수정하는건 휴대폰 전화 번호 뿐인데..
                // 전화번호 = PHONE , 모임번호 = received_no, 신청자 아이디 = received_ID
                modifiedTask task = new modifiedTask();
                task.execute(PHONE,received_no,received_ID);

            }
        });// 수정하기 버튼 클릭리스너 끝




    }// onCreate 끝








    private class Get_Moim_Data extends AsyncTask<String, Void, String> {
        String errorString = null;
        @Override
        protected void onPreExecute() {     // 시작전
            super.onPreExecute();
        }
        @Override
        protected void onPostExecute(String result) {   // 완료 후
            super.onPostExecute(result);
            Log.d(TAG, "response - " + result);

            String SQL_Error = result.substring(0,10);
            Log.e("SQL_Error = ", SQL_Error);


            if (result == null){    // 값을 받아 오지 못했다면
                Toast.makeText(Apply_Modify_Activity.this,"모임데이터를 가져오는데 실패하였습니다.",Toast.LENGTH_SHORT).show();
            }
            else if(result.equals("데이터를 입력하세요.")){  // 데이터 전달이 제대로 안됫을 경우
                Toast.makeText(Apply_Modify_Activity.this,"POST로 데이터가 넘어가질 않음",Toast.LENGTH_SHORT).show();
                Log.e("response = ", result);
            }
            else if(result.equals("신청된 모임 없음")){    // 검색된 모임이 없으면
                Toast.makeText(Apply_Modify_Activity.this,"신청된 모임이 없습니다.",Toast.LENGTH_SHORT).show();
            }
            else if(SQL_Error.equals("SQL문 처리중 에러")){    // Sql 문 에러 발생시
                Toast.makeText(Apply_Modify_Activity.this,"데이터 전달에 실패함.",Toast.LENGTH_SHORT).show();
                Log.e("response = ", result);
            }
            else {  // 데이터 전달이 제대로 이루어 졌을 때

                mJsonString = result;
                showResult();

            }
            Log.e("다이얼로그 해제됨","");
        }


        @Override
        protected String doInBackground(String... params) {

            String Apply_Moim_no = params[0];   // 모임번호
            String received_ID = params[1];   // 모임번호

            Log.e("TAG",TAG);
            Log.e("신청한 모임번호 =",Apply_Moim_no);
            Log.e("신청자 ID =",received_ID);


            String serverURL = "http://49.247.208.191/AndroidHive/Apply/Participant/Get_Apply_Data.php";   // AndroidHive/Get_Apply_Data.php 의 파일로 이동
            String postParameters = "Apply_Moim_no=" + Apply_Moim_no + "&received_ID=" + received_ID ;
            // 1. 모임번호로 모임 데이터를 찾고
            // 1. 모임 번호와 2. 아이디로 신청 데이터를 찾고
            // 두 데이터를 선별하고 합쳐서 가지고 온다. 데이터를 섞는다.

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
    }//



    /**
     * 서버에서 가져온 JSON 형태의 신청 데이터를
     * 파싱하여 위젯에 삽입한다..
     */
    private void showResult(){

        try {
            JSONObject jsonObject = new JSONObject(mJsonString);
            JSONArray jsonArray = jsonObject.getJSONArray(TAG_JSON);

            for(int i=0;i<jsonArray.length();i++){  // 불러오는 객체가 1개 이상이면 더 늘어남. 기본 = 1
                String 제이슨길이 = String.valueOf(jsonArray.length());  // 제이슨 길이 int 를 스트링 으로
                Log.e("제이슨어레이 길이 = ",제이슨길이);
                JSONObject item = jsonArray.getJSONObject(i);

                // -----------------------------------------------------------------------------------------------------------------------------------------------------------------------------
                /**
                 * 모임 데이터 표현
                 */
                String no = item.getString(TAG_NO);                                 // 1. 모임 번호

                String default_imgURI = item.getString(TAG_DEFAULT_imgURI);         // 2. 모임 기본이미지 URI
                String Name = item.getString(TAG_NAME);                             // 3. 모임명
                String Selection_method = item.getString(TAG_Selection_method);     // 4. 선정 방법 = 선착순, 개설자 선정
                String Application_method = item.getString(TAG_Application_method); // 5. 신청 방식 = 유,무료

                /**
                 * 받아온 모임 데이터로 상단의 모임 위젯들의 데이터 세팅 하기
                 */
                /** 1. 초기 이미지 설정, 받아온 default_imgURI 로 이미지를 설정한다. */
                Glide.with(getApplicationContext()).load("http://49.247.208.191/AndroidHive/Moim_default_IMG/"+default_imgURI).placeholder(R.drawable.default_image).into(default_image);
                /** 2. 모임명 설정 */
                Moim_name.setText(Name);
                /** 3. 선정방법 설정 */
                Moim_Selection_method.setText(Selection_method);
                /** 4. 유료,무료 설정 */
                Charged_Text.setText(Application_method);
                // 만약 Application_method = 유료 라면
                if(Application_method.equals("유료")){
                    Charged_Text.setTextColor(Color.RED);   // 글자가 유료인 경우에는 글자가 빨간색으로
                }

                // -----------------------------------------------------------------------------------------------------------------------------------------------------------------------------
// -----------------------------------------------------------------------------------------------------------------------------------------------------------------------------
                /**
                 * 신청 데이터 표현
                 */
                String Applicant_ID = item.getString(TAG_Applicant_ID);         // 1. 신청자 아이디
                String Applicant_Name = item.getString(TAG_Applicant_Name);     // 2. 신청자 이름 참여확정 or 대기자
                String Applicant_Email = item.getString(TAG_Applicant_Email);   // 3. 신청자 이메일
                String Applicant_Phone = item.getString(TAG_Applicant_Phone);   // 4. 신청자 휴대폰 번호


                /** 1. 신청자 아이디, 이름, 이메일 설정 */
                applicant_ID.setText(Applicant_ID);             // 신청자 아이디 설정
                applicant_NAME.setText(Applicant_Name);         // 신청자 이름 설정
                applicant_EMAIL.setText(Applicant_Email);       // 신청자 이메일 설정


/**
 *  신청 연락처 수정 1. 전화번호
 *  Applicant_Phone 변수에 신청자의 휴대폰 번호가 담겨 있다. EX) 010-2101-8841
 */
                // 전화번호 스피너------------------------------------------------------------------------------------------------------------------------------------------------------------------------------  시작

                // 휴대폰 번호를  분리자(-) 로 나누어 String 배열 arr 에 따로 담는다.
                arr = Applicant_Phone.split("-");   // String[] arr 전역 변수임

                Log.e("앞 번호 = ", arr[0] );
                Log.e("중간 번호 = ", arr[1] );
                Log.e("끝 번호 = ", arr[2] );

                // 중간번호 설정
                applicant_Middle_phoneNUM.setText(arr[1]+arr[2]);



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
                for(int a =0 ; a < 20 ; a++){
                    if(Phone_list2[a].equals(arr[0])){
                        // 비교하면서 앞 번호가 일치하면
                        Phone_spinner.setSelection(a);      // 스피너는 i 따라 기본으로 선택된다.
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
//                    Toast.makeText(ApplyActivity.this,Phone_spinner.getItemAtPosition(position)+" 선택",Toast.LENGTH_SHORT).show();    // 해당 클릭 아이이템의 텍스트를 토스트로 출력
                            Front_num = Phone_spinner.getItemAtPosition(position).toString(); // 앞번호 데이터를 담는다.
                        }
                    }
                    @Override
                    public void onNothingSelected(AdapterView<?> parent) {  // 아무것도 선택이 안되었을때
                    }
                });
// 전화번호 스피너------------------------------------------------------------------------------------------------------------------------------------------------------------------------------  끝







// -----------------------------------------------------------------------------------------------------------------------------------------------------------------------------
                Log.e("--------------------- ","");
                Log.e("모임번호 = ",no);
                Log.e("이미지 URI = ",default_imgURI);
                Log.e("모임 이름= ",Name);









            }// 반복문 종료

        } catch (JSONException e) {
            Log.d(TAG, "showResult : ", e);
        }

    }// showResult 끝







    private class modifiedTask extends AsyncTask<String, Void, String> {
        ProgressDialog progressDialog = new ProgressDialog(Apply_Modify_Activity.this);
        String errorString = null;
        @Override
        protected void onPreExecute() {     // 시작전
            progressDialog.setProgressStyle(ProgressDialog.STYLE_SPINNER);
            progressDialog.setMessage("모임 수정 중 입니다...");
            // show dialog
            progressDialog.show();
            super.onPreExecute();
        }


        @Override
        protected void onPostExecute(String result) {   // 완료 후
            super.onPostExecute(result);
            Log.d(TAG, "response - " + result);


            if (result == null){    // 값을 받아 오지 못했다면
                Toast.makeText(Apply_Modify_Activity.this,"수정에 실패하였습니다.",Toast.LENGTH_SHORT).show();
            }
            else if(result.equals("데이터를 입력하세요.")){  // 데이터 전달이 제대로 안됫을 경우
                Toast.makeText(Apply_Modify_Activity.this,"POST로 데이터가 넘어가질 않음",Toast.LENGTH_SHORT).show();
                Log.e("response = ", result);
            }
            else if(result.equals("SQL문 처리 성공")){     // 신청완료가 넘어오면
                Toast.makeText(Apply_Modify_Activity.this,"수정이 완료되었습니다.",Toast.LENGTH_SHORT).show();
                Log.e("결과 = ",""+result);

                finish();// 현재 액티비티 종료
            }
            else {  // Sql 문 에러 발생시
                Toast.makeText(Apply_Modify_Activity.this,"데이터 전달에 실패함.",Toast.LENGTH_SHORT).show();
                Log.e("response = ", result);
            }
            Log.e("다이얼로그 해제됨","");
            progressDialog.dismiss();   // 다이얼로그 해제
        }


        @Override
        protected String doInBackground(String... params) {

            String applicant_PHONE = params[0]; // 신청자 전화번호
            // 모임 번호와 신청자 아이디로 데이터를 찾고 신청자 전화번호를 삽입한다.
            String Apply_Moim_no = params[1];   // 모임번호
            String applicant_ID = params[2];    // 신청자 아이디


//            String Apply_Moim_no = params[0];   // 모임번호
//
//            String applicant_ID = params[1];    // 신청자 아이디
//            String applicant_EMAIL = params[2]; // 신청자 이메일
//            String applicant_NAME = params[3];  // 신청자 이름
//            String applicant_PHONE = params[4]; // 신청자 전화번호
//
//            String Current_Time = params[5];        // 신청일
//            String Application_status = params[6];  // 신청 상태
//            String Attendance = params[7];          // 출석 상태


            String serverURL = "http://49.247.208.191/AndroidHive/Apply/Participant/Apply_Modify.php";   // AndroidHive/Apply/Participant/Apply_Modify.php 의 파일로 이동
            String postParameters = "applicant_PHONE=" + applicant_PHONE + "&Apply_Moim_no=" + Apply_Moim_no + "&applicant_ID=" + applicant_ID;

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
    }// modifiedTask 끝












    @Override
    protected void onStart() {
        super.onStart();
//        Toast.makeText(getApplicationContext(),"onStart.", Toast.LENGTH_SHORT).show();

        NavigationView navigationView = (NavigationView) findViewById(R.id.navigation_view);

        // 헤더뷰 객체 생성
        View headerview = navigationView.getHeaderView(0);
        // 헤더뷰 에 있는 위젯
        TextView Main_Login_id = (TextView) headerview.findViewById(R.id.text_id);
        TextView Main_Login_email = (TextView) headerview.findViewById(R.id.text_email);
        final Button Main_logout_button = (Button) headerview.findViewById(R.id.logout_button);   // 로그아웃 버튼


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
                    Login_NAME = dataJsonObject.getString("Login_NAME");       // 이름
                    Login_EMAIL = dataJsonObject.getString("Login_EMAIL");      // 이메일
                    Login_PHONE = dataJsonObject.getString("Login_PHONE");      // 휴대폰
//                String 이미지Uri스트링 = dataJsonObject.getString("제품URI");   // 제품 Uri String

                    // 데이터 로그 찍어보기
                    Log.e("화면 = ",TAG);
                    Log.e("Login_ID = ",receive0);
                    Log.e("Login_NAME = ",Login_NAME);
                    Log.e("Login_EMAIL = ",Login_EMAIL);
                    Log.e("Login_PHONE = ",Login_PHONE);



                    Main_Login_id.setText(receive0 + " 님");
                    Main_Login_email.setText(Login_EMAIL);
                    Main_logout_button.setVisibility(View.VISIBLE); // 로그아웃 버튼이 보이게끔.

                }// try 문 끝
            } catch (JSONException e) {
                e.printStackTrace();
            }// 불러오기 끝

            // 로그 아웃 버튼 클릭 리스너
            Main_logout_button.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    Toast.makeText(Apply_Modify_Activity.this, "로그아웃 되었습니다.", Toast.LENGTH_SHORT).show();
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
            });// 로그 아웃 버튼 클릭 리스너 끝
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




    }// onStart 끝







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




}// 코드 끝
