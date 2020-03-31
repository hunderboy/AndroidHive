package com.example.user.androidhive;

import android.app.ProgressDialog;
import android.content.Intent;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.ImageButton;
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
import java.util.ArrayList;

import static com.example.user.androidhive.MainActivity.Login_user_NAME;
import static com.example.user.androidhive.MainActivity.receive0;

/**
 * 2018-1-24
 * 신청내역을 확인하기 위한 화면
 * 신청모임에 대한 데이터는 모임 Table 에서 찾고
 * 신청모임에 대한 나의 내역은 신청 Table 에서 찾는다.
 */
public class Apply_List_Activity extends AppCompatActivity {

    private static String TAG = "Apply_List_Activity";

    // 전역변수 화
    String ID = receive0; /** 로그인 유저 아이디 */
    String User_NAME = Login_user_NAME; /** 로그인 유저 이름 */

    // 텍스트뷰 객체 생성
    TextView No_Moim;   // "검색된 모임이 없습니다" 를 나타내는 텍스트뷰


    // 신청된 모임리스트 의 리사이클러뷰 관련
    RecyclerView ApplyList_RecyclerView;                 // 리사이클러뷰 객체
    RecyclerView.Adapter ApplyList_Adapter;              // 어댑터 객체
    RecyclerView.LayoutManager ApplyList_LayoutManager;  // 레이아웃매니저 객체
    ArrayList<Applied_list_Data> ApplyList_Dataset;     // 데이터 세트를 담고 있는 어레이리스트 객체


    // 서버에서 받아온 데이터(JSON 형태)를 담을 변수들 - 전역변수
    String mJsonString; // JSON 전체 데이터를 담는 변수
    private static final String TAG_JSON="MY_Apply_list";   // JSON 키

    private static final String TAG_NO = "no";                                      // 1. 모임번호
    private static final String TAG_DEFAULT_imgURI = "default_imgURI";              // 2. 모임기본이미지 URI
    private static final String TAG_NAME = "Name";                                  // 3. 모임명

    // 4. 날짜 관련부분 - 서로 합쳐야 함.
    private static final String TAG_Moim_date_1 = "Moim_date_1";                    // 모임날짜1
    private static final String TAG_Moim_date_2 = "Moim_date_2";                    // 모임날짜2
    private static final String TAG_Moim_start_time = "Moim_start_time";            // 모임 시작 시간
    private static final String TAG_Moim_end_time = "Moim_end_time";                // 모임 종료 시간

    private static final String TAG_Address ="Address";                             // 5. 모임 주소 = 일반주소 (상세주소를 제외한 주소)

    private static final String TAG_Application_status = "Application_status";      // 6. 신청 상태 = 참여확정 or 대기자

    private static final String TAG_Application_method = "Application_method";      // 7. 신청 방법 = 유료 or 무료
    private static final String TAG_Available_Seats = "Available_Seats";            // 8. 신청가능한 인원

    private static final String TAG_Apply_DATE = "Apply_DATE";                      // 9. 신청일







    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_apply__list_);

        // 검색모임 없음 = 텍스트뷰 객체 생성
        No_Moim = (TextView)findViewById(R.id.No_Moim);


        ImageButton Search_Button = (ImageButton)findViewById(R.id.Search_Button);
        // 검색 버튼 클릭 리스너
        Search_Button.setOnClickListener(new Button.OnClickListener() {
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



        /**
         * 리사이클 러뷰 관련
         * 리사이클 러뷰는
         * myDataset.add(new Opened_Moim_Data(Name,Total_People));
         * 같은 add 함수와 같은 메소드에 있어야 한다 =  중요!!!!!!!!!!!
         * 다른 메소드에 있을 경우 생성이 되지 않는다.
         */
        // 리사이클 러뷰 객체 선언
        ApplyList_RecyclerView = (RecyclerView) findViewById(R.id.Apply_List_recycler_view);

        // use this setting to improve performance if you know that changes
        // in content do not change the layout size of the RecyclerView
        // 각 Item 들이 RecyclerView 의 전체 크기를 변경하지 않는 다면
        // setHasFixedSize() 함수를 사용해서 성능을 개선할 수 있습니다.
        // 변경될 가능성이 있다면 false 로 , 없다면 true를 설정해주세요.
        ApplyList_RecyclerView.setHasFixedSize(true);

        // use a linear layout manager
        // 리니어레이아웃 매니저 객체 선언
        ApplyList_LayoutManager = new LinearLayoutManager(this);
        ApplyList_RecyclerView.setLayoutManager(ApplyList_LayoutManager);

        // specify an adapter (see also next example)
        ApplyList_Dataset = new ArrayList<>();                          // 어레이리스트 객체인 데이터세트 객체 선언
        ApplyList_Adapter = new Apply_List_Adapter(ApplyList_Dataset);     // 어댑터 객체 선언하며 데이터세트를 어댑터에 연결
        ApplyList_RecyclerView.setAdapter(ApplyList_Adapter);     // 리사이클러뷰에 어댑터 설정


        // 전달 받은 검색어를 DB에 전달하는 메소드
        Apply_List();
        // 하나의 메소드 안에 두개 이상의 AsyncTask 사용 할수가 없어서
        // 외부메소드 생성함. = 검색어를 저장하는 AsyncTask 와 검색어를 통해 모임을 찾는 AsyncTask 가 한 메소드에 존재 해서는 안됨. - 동시에 실행되어야 하지만



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





    // 처음 시작하자마자 신청 내역 검색
    void Apply_List(){
        /**  아이디 전달하고 신청내역 검색 */
        // 신청자가 신청 리스트를 표현할때 불러오는 AsyncTask
        Get_Apply_List task2 = new Get_Apply_List();
        // receive0 은 = Static 변수 이고, 현재 접속자 아이디를 가지고 있다.
        task2.execute(receive0); // 유저 아이디를 전달한다. = Bookmark_num = 1,2,3, 형태의 모임번호들

    }







    /**
     *  모임 검색을 위한 AsyncTask
     */
    private class Get_Apply_List extends AsyncTask<String, Void, String> {

        ProgressDialog progressDialog = new ProgressDialog(Apply_List_Activity.this);
        String errorString = null;

        @Override
        protected void onPreExecute() {     // 시작전
            progressDialog.setProgressStyle(ProgressDialog.STYLE_SPINNER);
            progressDialog.setMessage("신청내역 검색중 입니다...");
            // show dialog
            progressDialog.show();
            super.onPreExecute();
        }


        @Override
        protected void onPostExecute(String result) {   // 완료 후
            super.onPostExecute(result);
            Log.d(TAG, "response - " + result);


            if (result == null){    // 값을 받아 오지 못했다면
                Toast.makeText(Apply_List_Activity.this,"값을 받아오지 못했습니다.",Toast.LENGTH_SHORT).show();
            }
            else if(result.equals("검색된 모임 없음")){    // 검색된 모임이 없으면
                No_Moim.setVisibility(View.VISIBLE);    // 검색된 모임이 없다는 텍스트뷰 보이기
                Toast.makeText(Apply_List_Activity.this,"즐겨찾기에 추가된 모임이 없습니다.",Toast.LENGTH_SHORT).show();
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

            String ID = params[0];   // 아이디

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
     * 파싱하여 리사이클러뷰에 넣어준다.
     */
    private void showResult(){
        String Combine_date;    // 이 메소드에서는 Combine_date 을 공통으로 사용하라.

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
                String no = item.getString(TAG_NO);                             // 모임 번호

                String default_imgURI = item.getString(TAG_DEFAULT_imgURI);     // 이미지
                String Name = item.getString(TAG_NAME);                         // 모임명

                String Moim_date_1 = item.getString(TAG_Moim_date_1);            // 모임날짜1
                String Moim_date_2 = item.getString(TAG_Moim_date_2);            // 모임날짜2
                Log.e("Moim_date_2 = ",Moim_date_2);    // 모임날짜2 가 비어있으면 데이터가 어떻게 전달되는지 확인하자
                String Moim_start_time = item.getString(TAG_Moim_start_time);    // 모임 시작 시간
                String Moim_end_time = item.getString(TAG_Moim_end_time);        // 모임 종료 시간

                String Application_method = item.getString(TAG_Application_method);     // 유료 or 무료
                String Available_Seats = item.getString(TAG_Available_Seats);       // 신청가능한 인원

                String Apply_DATE = item.getString(TAG_Apply_DATE);       // 신청일


                if(Moim_date_2.equals("")){ // 모임날짜 2가 비어잇으면
                    Combine_date = Moim_date_1 + " " + Moim_start_time + " ~ " + Moim_end_time; // 모임날짜 합치기
                    Log.e("조합된_모임날짜 = ",Combine_date);
                }
                else{
                    Combine_date = Moim_date_1 + " " + Moim_start_time + " ~ " + Moim_date_2 + " " + Moim_end_time; // 모임날짜 합치기
                    Log.e("조합된_모임날짜 = ",Combine_date);
                }
                // 모임 주소 = 일반주소 (상세주소를 제외한 주소)
                String Address = item.getString(TAG_Address);

                /**
                 * 신청 데이터 표현
                 */
                String Application_status = item.getString(TAG_Application_status);     // 참여확정 or 대기자


                Log.e("--------------------- ","");
                Log.e("모임번호 = ",no);
                Log.e("이미지 URI = ",default_imgURI);
                Log.e("모임 이름= ",Name);
                Log.e("모임 주소= ",Address);
                Log.e("참여확정 or 대기자 = ",Application_status);

//               String ID, String Number, String IMG, String Name, String Moim_Time, String address, String Application_status, Application_method, Available_Seats, Apply_DATE
                // 아이디, 모임번호, 기본이미지, 모임명, 모임시간, 주소, 접수상태, 유료 or 무료, 신청가능한 인원, 신청일
                ApplyList_Dataset.add(new Applied_list_Data(ID,no,default_imgURI,Name,Combine_date ,Address,Application_status,Application_method,Available_Seats,Apply_DATE));


            }// 반복문 종료
            ApplyList_Adapter.notifyDataSetChanged();    // 데이터 추가가 완료되었으면 notifyDataSetChanged() 메서드를 호출해 데이터 변경 체크를 실행합니다. = 없어도 되긴 하던데

        } catch (JSONException e) {

            Log.d(TAG, "showResult : ", e);
        }

    }// showResult 끝






}// 코드 끝
