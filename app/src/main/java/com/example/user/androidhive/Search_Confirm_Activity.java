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
import android.widget.EditText;
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

/**
 * 2018-1-8
 * 검색된 모임들을 리스트에 표시 하기 위해 만든 화면
 */
public class Search_Confirm_Activity extends AppCompatActivity {
    private static String TAG = "Search_Confirm_Activity";

    // 전역변수 화
    String Searched_Word;   // 검색어 - 인텐트로 받아온 검색어
    String ID; /** 로그인 아이디 */


    // 객체 생성
    EditText Search_EditText;
    TextView No_Moim;


    // 검색된 모임리스트 의 리사이클러뷰 관련
    RecyclerView searched_RecyclerView;                 // 리사이클러뷰 객체
    RecyclerView.Adapter searched_Adapter;              // 어댑터 객체
    RecyclerView.LayoutManager searched_LayoutManager;  // 레이아웃매니저 객체
    ArrayList<Searched_Moim_Data> searched_Dataset;                // 데이터 세트를 담고 있는 어레이리스트 객체


    // 받아온 북마크 값
    String BookMark_JSON;
    String[] Bookmark_arr;  // 검색한 모임과 비교할 북마크 배열
    int Bookmark_arr_length;    // 북마크 배열 길이




    // 받아온 데이터 변수
    String mJsonString;
    private static final String TAG_JSON="webnautes";

    private static final String TAG_NO = "no";                                      // 모임번호
    private static final String TAG_DEFAULT_imgURI = "default_imgURI";              // 모임기본이미지 URI
    private static final String TAG_NAME = "Name";                                  // 모임명

    // 주소 관련부분 - 서로 합쳐야 함.
    private static final String TAG_Moim_date_1 = "Moim_date_1";                    // 모임날짜1
    private static final String TAG_Moim_date_2 = "Moim_date_2";                    // 모임날짜2
    private static final String TAG_Moim_start_time = "Moim_start_time";            // 모임 시작 시간
    private static final String TAG_Moim_end_time = "Moim_end_time";                // 모임 종료 시간


    private static final String TAG_Address ="Address";                             // 모임 주소 = 일반주소 (상세주소를 제외한 주소)
    private static final String TAG_Application_methodE = "Application_method";     // 유, 무료






    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_search__confirm_);

        // 검색모임 없음 = 텍스트뷰 객체 생성
        No_Moim = (TextView)findViewById(R.id.No_Moim);


        // 검색어 입력 화면에서 데이터 받아오기
        Intent intent = getIntent();    //이전 액티비티에서 수신할 데이터를 받기위해 선언

        // String 변수에 담기 - 이것을 기반으로 모임을 찾는다.
        Searched_Word = intent.getStringExtra("Word");        // 키 = Word   - 검색어 = 이것으로 모임을 검색한다.
        ID = intent.getStringExtra("ID");                     // 키 = ID     - 아이디

        Log.e("화면 = ",TAG);
        Log.e("받아온 단어 = ",Searched_Word);

        // 이것들을 PHP 문으로 넘기고
        // PHP 문에서 모임을 찾아서
        // 관련 데이터 들을 JSON 형태로 여기로 넘기고
        // 여기에서 그것들을 풀어서
        // 연결 시켜서 나타낸다.

        /**
         *  액티비티 시작하자마자 검색어를 저장한다.!!
         */
        Word_Add_Task2 task = new Word_Add_Task2();
        task.execute(ID,Searched_Word); // 아이디와 검색어를 전달한다.




        // 검색어를 입력 받을 검색 EditText
        Search_EditText = (EditText)findViewById(R.id.Search_EditText);
        Search_EditText.setText(Searched_Word); // 화면 시작하자마자 검색한 단어로 검색창에 검색단어를 설정한다.


        Button Search_Button = (Button)findViewById(R.id.Search_Button);
        // 검색 버튼 클릭 리스너
        Search_Button.setOnClickListener(new Button.OnClickListener() {
            @Override
            public void onClick(View view) {

                // 검색어를 Searching_Text 에 담는다.
                Searched_Word  = Search_EditText.getText().toString();

                if(Searched_Word.equals("")){  // 비어있을 경우
                    Toast.makeText(Search_Confirm_Activity.this,"검색어를 입력하세요.",Toast.LENGTH_SHORT).show();
                }
                else{
                    // 검색버튼을 눌렀을때 해당 검색어를 곧바로 DB에 저장하기위해 사용하는 Word_Add_Task2 (AsyncTask)
                    Word_Add_Task2 task = new Word_Add_Task2();
                    task.execute(ID,Searched_Word); // 아이디와 검색어를 전달한다.

                    // 전달 받은 검색어를 DB에 전달하는 메소드
                    Search_moim2();
                    // 하나의 메소드 안에 두개 이상의 AsyncTask 사용 할수가 없어서
                    // 외부메소드 생성함.
                }
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
        searched_RecyclerView = (RecyclerView) findViewById(R.id.Searched_recycler_view);

        // use this setting to improve performance if you know that changes
        // in content do not change the layout size of the RecyclerView
        // 각 Item 들이 RecyclerView 의 전체 크기를 변경하지 않는 다면
        // setHasFixedSize() 함수를 사용해서 성능을 개선할 수 있습니다.
        // 변경될 가능성이 있다면 false 로 , 없다면 true를 설정해주세요.
        searched_RecyclerView.setHasFixedSize(true);

        // use a linear layout manager
        // 리니어레이아웃 매니저 객체 선언
        searched_LayoutManager = new LinearLayoutManager(this);
        searched_RecyclerView.setLayoutManager(searched_LayoutManager);

        // specify an adapter (see also next example)
        searched_Dataset = new ArrayList<>();                          // 어레이리스트 객체인 데이터세트 객체 선언
        searched_Adapter = new Searched_Moim_List_Adapter(searched_Dataset);     // 어댑터 객체 선언하며 데이터세트를 어댑터에 연결
        searched_RecyclerView.setAdapter(searched_Adapter);     // 리사이클러뷰에 어댑터 설정


        // 즐겨찾기 번호를 가져오는 메소드
        Search_Bookmark_NUM();  // 모임을 검색하는 메소드보다 먼저 실행 되어야함.



        // 전달 받은 검색어를 DB에 전달하는 메소드
        Search_moim();
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




    // 처음 시작하자마자
    void Search_Bookmark_NUM(){
        /** 사용자의 북마크 번호를 검색
         * 모임 검색보다 먼저 이루어져야 함
         * */
        // 화면이 처음 만들어 지면서 검색된 리스트를 표현할때 불러오는 AsyncTask
        Search_Bookmark task3 = new Search_Bookmark();
        task3.execute(ID); // 아이디를 전달한다.
    }



    // 처음 시작하자마자
    void Search_moim(){
        /** 검색어 전달하고 모임 검색 */
        // 화면이 처음 만들어 지면서 검색된 리스트를 표현할때 불러오는 AsyncTask
        Get_Searched_MoimData task2 = new Get_Searched_MoimData();
        task2.execute(Searched_Word); // 검색어를 전달한다. = Searched_Word
    }

    // 다시 모임을 재검색 했을때 실행되는 메소드
    void Search_moim2(){
        searched_Dataset.clear(); // 데이터 세트를 모두 클리어 한다.
        searched_Adapter.notifyDataSetChanged();

        /** 검색어 전달하고 모임 검색 */
        // 다시 검색할때 사용되는 메소드
        Get_Searched_MoimData task2 = new Get_Searched_MoimData();
        task2.execute(Searched_Word); // 검색어를 전달한다. = Searched_Word
    }






    /**
     * 사용자의 북마크 번호를 검색. 아이디 전송
     * onCreate 와 동시에 바로 실행됨.
     */
    private class Search_Bookmark extends AsyncTask<String, Void, String> {
        String errorString = null;

        @Override
        protected void onPreExecute() {     // 시작전
            super.onPreExecute();
        }
        @Override
        protected void onPostExecute(String result) {   // 완료 후
            super.onPostExecute(result);
            Log.d(TAG, "response - " + result);

            String error_sql = result.substring(0,3);

            if (result.equals("아이디가 비어있습니다.")){    // 값을 받아 오지 못했다면
                Toast.makeText(Search_Confirm_Activity.this,"아이디를 전달하지 못했습니다.",Toast.LENGTH_SHORT).show();
            }
            else if(error_sql.equals("SQL문")){  // result에서 잘라낸 데이터가 'SQL문' 이라면 에러가 발생햇다는 의미
                Toast.makeText(Search_Confirm_Activity.this,""+result,Toast.LENGTH_SHORT).show();    // 에러 표시
            }
            else {  // 데이터 제대로 받아왔다는 의미
                BookMark_JSON = result;
                getBookmark();
            }
        }

        @Override
        protected String doInBackground(String... params) {

            String ID  = params[0];   // 아이디 전달

            Log.e("아이디 =",ID);

            String serverURL = "http://49.247.208.191/AndroidHive/BookMark/UserBookmark.php";   // AndroidHive/BookMark/UserBookmark.php 의 파일로 이동
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
    }// Search_Bookmark 끝


    /**
     * PHP 에서 가져온 해당아이디의 모임 번호를 추출한다.
     */
    private void getBookmark(){
        try {
            JSONObject jsonObject = new JSONObject(BookMark_JSON);
            JSONArray jsonArray = jsonObject.getJSONArray("Bookmark_Data");

            for(int i=0;i<jsonArray.length();i++){  // 계속 jsonArray.length() 는 1일 것이다.
                String 제이슨길이 = String.valueOf(jsonArray.length());  // 제이슨 길이 int 를 스트링 으로
                Log.e("제이슨어레이 길이 = ",제이슨길이);

                JSONObject item = jsonArray.getJSONObject(i);

                String Current_Login_ID = ID;   // 현재 아이디
                String Bookmark_num = item.getString("Bookmark");    // 해당 아이디의 북마크 key = Bookmark

                Log.e("--------------------- ","");
                Log.e("북마크 번호 = ",Bookmark_num);
                Log.e("현재 아이디 = ",Current_Login_ID);


                // 북마크를 , 기준으로 배열에 삽입한다.
                Bookmark_arr = Bookmark_num.split(",");  // EX : 0 , 1 , 2 , 3 , ? = 가변적 길이 현재는 4 개
                Log.e("첫번째 배열 = ", Bookmark_arr[0] );    // 경제
                // 북마크 배열의 길이
                Bookmark_arr_length = Bookmark_arr.length;
                Log.e("북마크 배열 길이 = ",""+ Bookmark_arr_length );      // 배열 길이 확인

            }// 반복문 종료

        } catch (JSONException e) {
            Log.d(TAG, "showResult : ", e);
        }
    }// showResult 끝




    /**
     *  모임 검색을 위한 AsyncTask
     */
    private class Get_Searched_MoimData extends AsyncTask<String, Void, String> {

        ProgressDialog progressDialog =new ProgressDialog(Search_Confirm_Activity.this);
        String errorString = null;

        @Override
        protected void onPreExecute() {     // 시작전
            progressDialog.setProgressStyle(ProgressDialog.STYLE_SPINNER);
            progressDialog.setMessage("모임 검색중 입니다...");
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
                Toast.makeText(Search_Confirm_Activity.this,"값을 받아오지 못했습니다.",Toast.LENGTH_SHORT).show();
            }
            else if(result.equals("검색된 모임 없음")){    // 검색된 모임이 없으면
                No_Moim.setVisibility(View.VISIBLE);    // 검색된 모임이 없다는 텍스트뷰 보이기
                Toast.makeText(Search_Confirm_Activity.this,"검색된 모임이 없습니다..",Toast.LENGTH_SHORT).show();
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

            String search_word = params[0];   // 검색어

            Log.e("Get_Searched_MoimData","");
            Log.e("search_word =",search_word);

            String serverURL = "http://49.247.208.191/AndroidHive/Search_Moim.php";   // AndroidHive/Search_Moim.php 의 파일로 이동
            String postParameters = "search_word=" + search_word;

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

                String Bookmark_number = "0";   // 북마크 기본 값 0

                String no = item.getString(TAG_NO);                             // 모임 번호

                String default_imgURI = item.getString(TAG_DEFAULT_imgURI);     // 이미지
                String Name = item.getString(TAG_NAME);                         // 모임명

                String Moim_date_1 = item.getString(TAG_Moim_date_1);            // 모임날짜1
                String Moim_date_2 = item.getString(TAG_Moim_date_2);            // 모임날짜2
                Log.e("Moim_date_2 = ",Moim_date_2);    // 모임날짜2 가 비어있으면 데이터가 어떻게 전달되는지 확인하자
                String Moim_start_time = item.getString(TAG_Moim_start_time);    // 모임 시작 시간
                String Moim_end_time = item.getString(TAG_Moim_end_time);        // 모임 종료 시간

                String Address = item.getString(TAG_Address);                         // 모임 주소 = 일반주소 (상세주소를 제외한 주소)
                String Application_method = item.getString(TAG_Application_methodE);  // 유, 무료


                // 북마크 중복체크
                for(int a = 0; a < Bookmark_arr_length ; a++){  // 북마크 길이만큼 반복해서 체크 함

                    if(Bookmark_arr[a].equals(no)){ // 북마크 번호가 검색된 모임 번호와 같으면 = String 비교
                        Bookmark_number = "1";
                    }
                }


                if(Moim_date_2.equals("")){ // 모임날짜 2가 비어잇으면
                     Combine_date = Moim_date_1 + " " + Moim_start_time + " ~ " + Moim_end_time;
                    Log.e("조합된_모임날짜 = ",Combine_date);
                }
                else{
                     Combine_date = Moim_date_1 + " " + Moim_start_time + " ~ " + Moim_date_2 + " " + Moim_end_time;
                    Log.e("조합된_모임날짜 = ",Combine_date);
                }

                Log.e("--------------------- ","");
                Log.e("모임번호 = ",no);
                Log.e("이미지 URI = ",default_imgURI);
                Log.e("모임 이름= ",Name);
                Log.e("모임 주소= ",Address);
                Log.e("가격 = ",Application_method);

//               String ID, String Number, String IMG, String Name, String Moim_Time, String address, String Charged , String Bookmark_number
                searched_Dataset.add(new Searched_Moim_Data(ID,no,default_imgURI,Name,Combine_date ,Address,Application_method,Bookmark_number));  // 텍스트, 이미지


            }// 반복문 종료
            searched_Adapter.notifyDataSetChanged();    // 데이터 추가가 완료되었으면 notifyDataSetChanged() 메서드를 호출해 데이터 변경 체크를 실행합니다. = 없어도 되긴 하던데

        } catch (JSONException e) {

            Log.d(TAG, "showResult : ", e);
        }

    }// showResult 끝










    /**
     * 검색된 최신 검색어를 저장.
     */
    private class Word_Add_Task2 extends AsyncTask<String, Void, String> {
        String errorString = null;

        @Override
        protected void onPreExecute() {     // 시작전
            super.onPreExecute();
        }

        @Override
        protected void onPostExecute(String result) {   // 완료 후
            super.onPostExecute(result);
            Log.d(TAG, "response - " + result);

            String error_sql = result.substring(0,3);

            if (result.equals("아이디 또는 전체검색어가 비어있습니다.")){    // 값을 받아 오지 못했다면
                Toast.makeText(Search_Confirm_Activity.this,"아이디와 전체검색어 전달 실패",Toast.LENGTH_SHORT).show();
            }
            else if(error_sql.equals("SQL문")){  // result에서 잘라낸 데이터가 'SQL문' 이라면 에러가 발생햇다는 의미
                Toast.makeText(Search_Confirm_Activity.this,""+result,Toast.LENGTH_SHORT).show();    // 에러 표시
            }
            else {  // 데이터 제대로 받아왔다는 의미
                // 아무일도 안함.
//                Toast.makeText(Search_Confirm_Activity.this, TAG + " 에서 검색어 수정 성공",Toast.LENGTH_SHORT).show();
            }
        }


        @Override
        protected String doInBackground(String... params) {

            String ID  = params[0];             // 아이디 전달
            String save_Word  = params[1];     // 검색어를 전체 전달

            Log.e("SaveWords_Task 에서 아이디 =",ID);
            Log.e("save_Word =",save_Word);

            String serverURL = "http://49.247.208.191/AndroidHive/Recent_Word/Recent_Word_add.php";   // AndroidHive/Recent_Word/Recent_Word_add.php 의 파일로 이동
            String postParameters = "ID=" + ID + "&save_Word=" + save_Word;   // 아이디와 검색어를 전달

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
    }// GetRecentWord 끝



}
