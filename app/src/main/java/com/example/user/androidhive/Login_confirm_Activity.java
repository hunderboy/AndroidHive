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

import static com.example.user.androidhive.R.id.email;
import static com.example.user.androidhive.R.id.phone;

public class Login_confirm_Activity extends AppCompatActivity {

    private static String TAG = "Login_confirm_Activity";

    String mJsonString;

    private static final String TAG_JSON="webnautes";
    private static final String TAG_ID = "id";
    private static final String TAG_NAME = "name";
    private static final String TAG_EMAIL = "email";
    private static final String TAG_PHONE ="phone";

    // 텍스트뷰 선언
    TextView 아이디;
    TextView 이름;
    TextView 이메일;
    TextView 휴대폰번호;


    //  버튼 선언
    Button 로그아웃;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login_confirm_);

        // 버튼 연결
        로그아웃 = (Button) findViewById(R.id.btnLogout);

        // 로그아웃 버튼 리스너
        로그아웃.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                finish();   // 현재 액티비티 종료
            }
        });

        // 텍스트뷰 연결
        아이디 = (TextView) findViewById(R.id.id);
        이름 = (TextView) findViewById(R.id.name);
        이메일 = (TextView) findViewById(email);
        휴대폰번호 = (TextView) findViewById(phone);

        // 인텐트로 넘어온 데이터 받고
        Intent intent = getIntent();
        String 넘어온아이디 = intent.getStringExtra("아이디");

        String 로그인ID = 넘어온아이디;
        Log.e("화면 = ",TAG);
        Log.e("넘어온 로그인ID =",로그인ID);


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

                String receive0 = dataJsonObject.getString("Login_ID");     // 키 값에 따라 데이터 받기
                String receive1 = dataJsonObject.getString("Login_NAME");
                String receive2 = dataJsonObject.getString("Login_EMAIL");
                String receive3 = dataJsonObject.getString("Login_PHONE");
//                String 이미지Uri스트링 = dataJsonObject.getString("제품URI");   // 제품 Uri String

                // 데이터 로그 찍어보기
                Log.e("화면 = ",TAG);
                Log.e("Login_ID = ",receive0);
                Log.e("Login_NAME = ",receive1);
                Log.e("Login_EMAIL = ",receive2);
                Log.e("Login_PHONE = ",receive3);

                아이디.setText(receive0);
                이름.setText(receive1);
                이메일.setText(receive2);
                휴대폰번호.setText(receive3);


            }// try 문 끝
        } catch (JSONException e) {
            e.printStackTrace();
        }// 불러오기 끝




//        GetData task = new GetData();   // 그 아이디를 기준으로 스레드 실행해서 나머지 데이터 가져오기
//        task.execute(로그인ID);    // 받아온 아이디를 넘겨준다.



    }// onCreate 끝


    private class GetData extends AsyncTask<String, Void, String> {

        ProgressDialog progressDialog;
        String errorString = null;

        @Override
        protected void onPreExecute() {     // 시작전
            super.onPreExecute();

            progressDialog = ProgressDialog.show(Login_confirm_Activity.this,
                    "Please Wait", null, true, true);
        }


        @Override
        protected void onPostExecute(String result) {   // 완료 후
            super.onPostExecute(result);

            progressDialog.dismiss();
//            mTextViewResult.setText(result);
            Log.d(TAG, "response - " + result);

            if (result == null){    // 값을 받아 오지 못했다면
//                mTextViewResult.setText(errorString);
                Toast.makeText(Login_confirm_Activity.this,"값을 받아오지 못했습니다.",Toast.LENGTH_SHORT).show();
            }
            else {
                mJsonString = result;
                showResult();
            }
        }


        @Override
        protected String doInBackground(String... params) {

            String 로그인ID = params[0];

            Log.e("로그인ID =",로그인ID);

            String serverURL = "http://49.247.208.191/Login_data.php";
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
    }

    private void showResult(){
        try {
            JSONObject jsonObject = new JSONObject(mJsonString);
            JSONArray jsonArray = jsonObject.getJSONArray(TAG_JSON);

            for(int i=0;i<jsonArray.length();i++){

                String 제이슨길이 = String.valueOf(jsonArray.length());  // 제이슨 길이 int 를 스트링 으로

                Log.e("제이슨어레이 길이 = ",제이슨길이);

                JSONObject item = jsonArray.getJSONObject(i);

//                private static final String TAG_ID = "id";
//                private static final String TAG_NAME = "name";
//                private static final String TAG_EMAIL = "email";
//                private static final String TAG_PHONE ="phone";
                String id = item.getString(TAG_ID);
                String name = item.getString(TAG_NAME);
                String email = item.getString(TAG_EMAIL);
                String phone = item.getString(TAG_PHONE);

                아이디.setText(id);
                이름.setText(name);
                이메일.setText(email);
                휴대폰번호.setText(phone);
            }

        } catch (JSONException e) {

            Log.d(TAG, "showResult : ", e);
        }

    }


}
