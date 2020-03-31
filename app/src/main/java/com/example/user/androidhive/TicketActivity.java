package com.example.user.androidhive;

import android.app.ProgressDialog;
import android.content.Intent;
import android.graphics.Color;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.ImageView;
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

public class TicketActivity extends AppCompatActivity {

    private static String TAG = "TicketActivity";

    // 텍스트뷰 객체 생성
    TextView Moim_Name_text;   // 모임명

    TextView USER_NAME_text;   // 신청자 이름

    TextView USER_ID_text;   // 신청자 아이디
    TextView Apply_date_Text;   // 신청일

    TextView Moim_Time_text;   // 모임기간
    TextView Moim_address_text;   // 모임장소

    TextView Status_text;   // 출석상태 = 미출석 or 2018-02-21 꼴의 형태

    // QR코드 이미지뷰
    ImageView QR_image;     // QR코드 이미지




    // 서버에서 받아온 데이터(JSON 형태)를 담을 변수들 - 전역변수
    String mJsonString; // JSON 전체 데이터를 담는 변수
    private static final String TAG_JSON="Ticket_Data";   // JSON 키

    private static final String TAG_NAME = "Applicant_Name";                      // 1. 신청자 이름
    private static final String TAG_Attendance_status = "Attendance_status";      // 2. 신청자 출석상태


    // 데이터 전역 변수 설정
    String M_NAME;          // 모임명
    String M_APPLY_DATE;    // 신청일
    String M_OPEN_DATE;     // 모임기간
    String M_PLACE;         // 모임장소
    String M_NUMBER;        // 모임 번호
    String USER_ID;         // 아이디


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_ticket);

        // 텍스트뷰 객체 생성
        Moim_Name_text = (TextView)findViewById(R.id.Moim_Name_text);       // 모임명

        USER_NAME_text = (TextView)findViewById(R.id.USER_NAME_text);       // 신청자 이름

        USER_ID_text = (TextView)findViewById(R.id.USER_ID_text);           // 신청자 아이디
        Apply_date_Text = (TextView)findViewById(R.id.Apply_date_Text);     // 신청일

        Moim_Time_text = (TextView)findViewById(R.id.Moim_Time_text);       // 모임기간
        Moim_address_text = (TextView)findViewById(R.id.Moim_address_text); // 모임장소

        Status_text = (TextView)findViewById(R.id.Status_text);             // 출석상태 = 미출석 or 2018-02-21 꼴의 형태

        // 이미지뷰 객체 생성
        QR_image = (ImageView)findViewById(R.id.QR_image);                  // QR 코드 이미지뷰


        // 티켓버튼 클릭 후 인텐트에서 데이터 받아옴
        Intent intent = getIntent();    //이전 액티비티에서 수신할 데이터를 받기위해 선언
        // String 변수에게 해당 모임의 데이터들을 받아낸다
         M_NAME = intent.getStringExtra("M_NAME");            // 모임명
         M_APPLY_DATE = intent.getStringExtra("M_APPLY_DATE");// 신청일

         M_OPEN_DATE = intent.getStringExtra("M_OPEN_DATE");  // 모임기간
         M_PLACE = intent.getStringExtra("M_PLACE");          // 모임장소


        // 모임 번호와 아이디로 QR코드 구성
         M_NUMBER = intent.getStringExtra("M_NUMBER");        // 모임 번호
         USER_ID = intent.getStringExtra("USER_ID");          // 아이디

        Log.e("모임 번호",""+M_NUMBER);
        Log.e("아이디",""+USER_ID);


        /**
         * QR코드 이미지 설정을 위한 코드
         */
            // {"M_NUMBER":"DATA","USER_ID":"ADDRESS"} 형태의 꼴로 만들어야 한다.
        String QR코드 = "{\"M_NUMBER\":\""+M_NUMBER+"\",\"USER_ID\":\""+USER_ID+"\"}";

        Log.e("QR코드 데이터",""+QR코드);


        Glide.with(QR_image.getContext())
                .load("http://chart.apis.google.com/chart?cht=qr&chs=350x350&chl="+ QR코드)
                .placeholder(R.drawable.noimage)
                .into(QR_image);

        /**
         * 모임번호 하고 아이디 넘겨서
         * 신청자 이름, 출석상태 가져오자
         */
        Get_data task2 = new Get_data();
        task2.execute(M_NUMBER,USER_ID); // 모임번호와 아이디를 전달한다.





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
     *  티켓 데이터를 가져오기 위한 AsyncTask
     */
    private class Get_data extends AsyncTask<String, Void, String> {
        ProgressDialog progressDialog = new ProgressDialog(TicketActivity.this);    // 다이얼로그 객체 생성
        String errorString = null;      // 에러스트링 객체 생성
        @Override
        protected void onPreExecute() {     // 시작전
            progressDialog.setProgressStyle(ProgressDialog.STYLE_SPINNER);
            progressDialog.setMessage("티켓 검색중 입니다...");
            // show dialog
            progressDialog.show();
            super.onPreExecute();
        }
        @Override
        protected void onPostExecute(String result) {   // 완료 후
            super.onPostExecute(result);
            Log.d(TAG, "response - " + result);


            if (result == null){    // 값을 받아 오지 못했다면
                Toast.makeText(TicketActivity.this,"값을 받아오지 못했습니다.",Toast.LENGTH_SHORT).show();
            }
            else if(result.equals("검색된 모임 없음")){    // 검색된 모임이 없으면
                Toast.makeText(TicketActivity.this,"즐겨찾기에 추가된 모임이 없습니다.",Toast.LENGTH_SHORT).show();
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

            String Apply_Moim_no = params[0];   // 모임번호
            String received_ID = params[1];   // 아이디


            String serverURL = "http://49.247.208.191/AndroidHive/Apply/Participant/Ticket_data.php";   // AndroidHive/Apply/Participant/Ticket_data.php 의 파일로 이동
            String postParameters = "Apply_Moim_no=" + Apply_Moim_no + "&received_ID=" + received_ID;

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
                String Applicant_Name = item.getString(TAG_NAME);                     // 신청자 이름
                String Attendance_status = item.getString(TAG_Attendance_status);     // 신청자 출석상태

                /**
                 * 데이터와 위젯간의 연결 세팅 작업 부분
                 */
                Moim_Name_text.setText(M_NAME);     // 모임명
                USER_NAME_text.setText(Applicant_Name);     // 신청자 이름
                USER_ID_text.setText(USER_ID);       // 신청자 아이디
                Apply_date_Text.setText(M_APPLY_DATE);    // 신청일
                Moim_Time_text.setText(M_OPEN_DATE);     // 모임기간
                Moim_address_text.setText(M_PLACE);  // 모임장소


                if(!Attendance_status.equals("미출석")){   // 미출석이 아닌 경우
                    Status_text.setTextColor(Color.BLUE);   // 파랑색으로 변경
                    String confirm_TEXT = Attendance_status +" 출석확인";
                    Status_text.setText(confirm_TEXT);      // 출석상태 =  '2018-02-21 출석확인' 꼴의 형태
                }
                else {
                    Status_text.setText(Attendance_status); // 출석상태 = 미출석
                }


            }// 반복문 종료

        } catch (JSONException e) {
            Log.d(TAG, "showResult : ", e);
        }

    }// showResult 끝






}// 코드 끝
