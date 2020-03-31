package com.example.user.androidhive;

import android.app.ProgressDialog;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.TextView;

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

import jp.wasabeef.glide.transformations.CropCircleTransformation;

import static com.example.user.androidhive.MainActivity.receive0;

public class Profle_Activity extends AppCompatActivity {

    private static String TAG = "Profle_Activity";


    // 서버에서 받아온 데이터(JSON 형태)를 담을 변수들 - 전역변수
    String mJsonString; // JSON 전체 데이터를 담는 변수
    private static final String TAG_JSON="Profile_Data";   // JSON 키

    private static final String TAG_member_name = "member_name";            // 1. 유저 이름
    private static final String TAG_member_id = "member_nickname";          // 2. 유저 ID
    private static final String TAG_member_email = "member_email";          // 3. 유저 이메일
    private static final String TAG_member_callnumber = "member_callnumber";// 4. 유저 전화번호
    private static final String TAG_member_profile_img = "Profile_image";    // 5. 프로필 이미지 .jpg


    // 프로필 이미지뷰
    ImageView Profile_image;     // 프로필 이미지

    // 텍스트뷰 객체 생성
    TextView USER_ID_text;      // 유저 아이디
    TextView USER_Name_text;    // 유저 이름
    TextView USER_Email_text;   // 유저 이메일
    TextView USER_PhoneNum_text;// 유저 전화번호


    // 데이터 전역 변수 설정
    String U_NAME;      // 유저 이름
    String U_ID;        // 유저 아이디
    String U_Email;     // 유저 이메일
    String U_PhoneNum;  // 유저 전화번호



    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_profle_);


        // 이미지뷰 객체 생성
        Profile_image = (ImageView)findViewById(R.id.Profile_image); // 프로필 이미지뷰

        // 텍스트뷰 객체 생성
        USER_ID_text = (TextView)findViewById(R.id.USER_ID_text);             // 모임명
        USER_Name_text = (TextView)findViewById(R.id.USER_Name_text);         // 신청자 이름
        USER_Email_text = (TextView)findViewById(R.id.USER_Email_text);       // 신청자 아이디
        USER_PhoneNum_text = (TextView)findViewById(R.id.USER_PhoneNum_text); // 신청일

        // 프로필 이미지 원형 설정
        Glide.with(Profile_image.getContext())
                .load(R.drawable.ic_account_circle)
                .placeholder(R.drawable.ic_account_circle)
                .centerCrop()
                .crossFade()
                .bitmapTransform(new CropCircleTransformation(Profile_image.getContext()))   // 원형크롭 라이브러리 사용함 = compile 'jp.wasabeef:glide-transformations:2.0.1'
                .override(100,100)  // 리소스의 이미지를 강제적으로 작게 줄여서 보여주려고 할대 사용
                .into(Profile_image);



        // 이미지 수정 버튼 객체 생성
        Button Profile_image_modify_button = (Button)findViewById(R.id.Profile_image_modify_button);
        // 이미지 수정 클릭 리스너
        Profile_image_modify_button.setOnClickListener(new Button.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(
                        getApplicationContext(), // 현재 화면의 제어권자
                        CameraView_Activity.class); // 다음 넘어갈 클래스 지정 - CameraView_Activity - 얼굴 인식 카메라 뷰 이동
                startActivity(intent); // 다음 화면으로 넘어간다

            }
        });



        // 뒤로가기 버튼 객체 생성
        ImageButton BackButton = (ImageButton)findViewById(R.id.BackButton);
        // BackButton 클릭 리스너
        BackButton.setOnClickListener(new Button.OnClickListener() {
            @Override
            public void onClick(View view) {
                finish();// 현재 액티비티 종료
            }
        });

    }// onCreate 끝





    /**
     *  프로필 데이터를 가져오기 위한 AsyncTask
     */
    private class Get_Profile_data extends AsyncTask<String, Void, String> {
        ProgressDialog progressDialog = new ProgressDialog(Profle_Activity.this);    // 다이얼로그 객체 생성
        String errorString = null;      // 에러스트링 객체 생성
        @Override
        protected void onPreExecute() {     // 시작전
            progressDialog.setProgressStyle(ProgressDialog.STYLE_SPINNER);
            progressDialog.setMessage("프로필 불러오는 중...");
            // show dialog
            progressDialog.show();
            super.onPreExecute();
        }
        @Override
        protected void onPostExecute(String result) {   // 완료 후
            super.onPostExecute(result);
            Log.d(TAG, "response - " + result);


            if (result == null) {    // 값을 받아 오지 못했다면

                Log.e("값을 받아오지 못했습니다.", "");
            }
            else if(result.equals("해당 아이디 없음")){    // 검색된 모임이 없으면
                Log.e("해당 아이디 없음","");
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

            String received_ID = params[0];   // 아이디


            String serverURL = "http://49.247.208.191/AndroidHive/Profile/Get_Profile_Data.php";   // AndroidHive/Apply/Participant/Ticket_data.php 의 파일로 이동
            String postParameters = "received_ID=" + received_ID;

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
    }// Get_data 끝


    /**
     * PHP 에서 가져온 JSON 형태의 모임 데이터를
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

                /**
                 * 모임 데이터 표현
                 */
                U_NAME = item.getString(TAG_member_name);                     // 신청자 이름
                U_ID = item.getString(TAG_member_id);     // 신청자 출석상태
                U_Email = item.getString(TAG_member_email);     // 신청자 출석상태
                U_PhoneNum = item.getString(TAG_member_callnumber);     // 신청자 출석상태


                String profile_img_NAME = item.getString(TAG_member_profile_img);

                Log.e("이미지 표시전 프로필 이미지 name = ",profile_img_NAME);

                if(profile_img_NAME.equals("없음")){

                    Glide.with(Profile_image.getContext())
                            .load(R.drawable.ic_account_circle)
                            .placeholder(R.drawable.ic_account_circle)
                            .centerCrop()
                            .crossFade()
                            .bitmapTransform(new CropCircleTransformation(Profile_image.getContext()))   // 원형크롭 라이브러리 사용함 = compile 'jp.wasabeef:glide-transformations:2.0.1'
                            .override(100,100)  // 리소스의 이미지를 강제적으로 작게 줄여서 보여주려고 할대 사용
                            .into(Profile_image);
                    Log.e("프로필 이미지 name = ","빈칸");
                }
                // 이곳에는 안들어온다
                else { // 비어있지 않은 경우

                    Glide.with(Profile_image.getContext())
                            .load("http://49.247.208.191/AndroidHive/Moim_default_IMG/"+profile_img_NAME+".jpg")
                            .placeholder(R.drawable.ic_account_circle)
                            .centerCrop()
                            .crossFade()
                            .bitmapTransform(new CropCircleTransformation(Profile_image.getContext()))   // 원형크롭 라이브러리 사용함 = compile 'jp.wasabeef:glide-transformations:2.0.1'
                            .override(100,100)  // 리소스의 이미지를 강제적으로 작게 줄여서 보여주려고 할대 사용
                            .into(Profile_image);
                    Log.e("프로필 이미지 name = ","profile_img_NAME");
                }


                /**
                 * 데이터와 위젯간의 연결 세팅 작업 부분
                 */
                USER_ID_text.setText(U_ID);             // 모임명
                USER_Name_text.setText(U_NAME);         // 신청자 이름
                USER_Email_text.setText(U_Email);       // 신청자 아이디
                USER_PhoneNum_text.setText(U_PhoneNum); // 신청일


                /**
                 *  프로필 액티비티에서 새롭게 접속자 데이터를 가져오면서 SharedPreferences 의 객체 SF 에 접속자 데이터를 저장한다.
                 */
                // echo로 가져온 데이터를 쉐어드에 저장해야 한다.
                // SharedPreferences 저장
                SharedPreferences SF = getSharedPreferences("SF", MODE_PRIVATE);     // SharedPreferences 객체 생성
                SharedPreferences.Editor editor = SF.edit();    //저장하려면 editor가 필요

                JSONArray SF_JsonArray = new JSONArray();      // 쉐어드 제이슨어레이 객체 준비
                JSONObject SF_JsonObject = new JSONObject();   // 쉐어드 제이슨 객체 생성

                SF_JsonObject.put("Login_ID", U_ID);                     // 아이디 저장
                SF_JsonObject.put("Login_NAME", U_NAME);                 // 이름 저장
                SF_JsonObject.put("Login_EMAIL", U_Email);               // 이메일 저장
                SF_JsonObject.put("Login_PHONE", U_PhoneNum);            // 폰번호 저장
                SF_JsonObject.put("Login_Profile_image", profile_img_NAME); // 프로필 이미지 파일명 저장

                // 데이터 로그 찍어보기
                Log.e("화면 = ",TAG);
                Log.e("Login_ID = ",U_ID);
                Log.e("Login_NAME = ",U_NAME);
                Log.e("Login_EMAIL = ",U_Email);
                Log.e("Login_PHONE = ",U_PhoneNum);
                Log.e("Login_Profile_image = ",profile_img_NAME);

                SF_JsonArray.put(SF_JsonObject);    // 쉐얻 제이슨 어레이 에 제이슨 오브젝트를 집어 넣는다.

                editor.putString("제이슨어레이 KEY", String.valueOf(SF_JsonArray)); // SF에 키값이 "제이슨어레이 KEY" 인 제이슨어레이를 저장.
                editor.apply();    // 파일에 최종 반영함

            }// 반복문 종료

        } catch (JSONException e) {
            Log.d(TAG, "showResult : ", e);
        }

    }// showResult 끝


    @Override
    protected void onStart() {
        super.onStart();
        Log.e("onStart 들어옴","");

        /**
         * 모임번호 하고 아이디 넘겨서
         * 신청자 이름, 출석상태 가져오자
         */
        Get_Profile_data Get_Profile_data_task = new Get_Profile_data();
        Get_Profile_data_task.execute(receive0); // 모임번호와 아이디를 전달한다.

        // 이 부분에서 접속자 프로필 데이터 SF 에 저장한다.





    }

}// 코드 끝
