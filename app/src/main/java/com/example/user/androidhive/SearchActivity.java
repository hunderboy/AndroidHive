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
 * 2018-1-6
 * 검색기능 구현하기 위해 생성 - 검색화면
 */
public class SearchActivity extends AppCompatActivity {

    private static String TAG = "SearchActivity";

    // 받아온 데이터 변수
    String mJsonString;
    private static final String TAG_JSON="search_word_Data";

    private static final String TAG_RECENT_WORD = "Recent_search_word";


    //  리사이클 러뷰 관련 변수 - Recent_Word_RecyclerView = 최근 검색어 리사이클 러뷰

    RecyclerView Recent_Word_RecyclerView;                 // 리사이클러뷰 객체
    RecyclerView.Adapter Recent_Word_Adapter;              // 어댑터 객체
    LinearLayoutManager Recent_Word_LayoutManager;         // 레이아웃매니저 객체 - 역순으로 추가 하기 위해 = RecyclerView.LayoutManager 대신  LinearLayoutManager 사용
    ArrayList<Recent_word_Data> Recent_Word_Dataset;            // 최근 검색어 데이터클래스 를 담고 있는 어레이리스트 객체



    // 전역변수 위젯
    EditText Search_EditText;
    TextView Explain_Text;

    // 전역변수 아이디
    String ID;



    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_search);

        // 최근 검색어 텍스트뷰 위젯 - 검색어가 없으면 '최근 검색어가 없습니다' 라고 변경
        Explain_Text = (TextView)findViewById(R.id.Explain_Text);


        ImageButton BackButton = (ImageButton)findViewById(R.id.BackButton);
        // BackButton 클릭 리스너
        BackButton.setOnClickListener(new Button.OnClickListener() {
            @Override
            public void onClick(View view) {
                finish();// 현재 액티비티 종료
            }
        });


        // 검색어를 입력 받을 검색 EditText
        Search_EditText = (EditText)findViewById(R.id.Search_EditText);



        Button Search_Button = (Button)findViewById(R.id.Search_Button);
        // 검색 버튼 클릭 리스너
        Search_Button.setOnClickListener(new Button.OnClickListener() {
            @Override
            public void onClick(View view) {

                String Searching_Text = Search_EditText.getText().toString();   // 검색어를 Searching_Text 에 담는다.

                if(Searching_Text.equals("")){  // 비어있을 경우
                    Toast.makeText(SearchActivity.this,"검색어를 입력하세요.",Toast.LENGTH_SHORT).show();
                }
                else{
                    Intent intent = new Intent(
                            getApplicationContext(), // 현재 화면의 제어권자
                            Search_Confirm_Activity.class); // 다음 넘어갈 클래스 지정 = 검색 확인 화면으로 이동

                    // id 를 넘기는 이유는 id 를 기반으로 최근 검색어를 찾기 위함이다.
                    intent.putExtra("Word",Searching_Text  );      // 검색어 넘기기
                    intent.putExtra("ID",ID  );      // ID 넘기기

                    Log.e("넘기는 단어 = ",Searching_Text );

                    startActivity(intent); // 다음 화면으로 넘어간다
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
        Recent_Word_RecyclerView = (RecyclerView) findViewById(R.id.Recent_word_recycler_view);

        // use this setting to improve performance if you know that changes
        // in content do not change the layout size of the RecyclerView
        // 각 Item 들이 RecyclerView 의 전체 크기를 변경하지 않는 다면
        // setHasFixedSize() 함수를 사용해서 성능을 개선할 수 있습니다.
        // 변경될 가능성이 있다면 false 로 , 없다면 true를 설정해주세요.
        Recent_Word_RecyclerView.setHasFixedSize(true);

        // use a linear layout manager
        // 리니어레이아웃 매니저 객체 선언 - 역순으로 추가 하기 위해 = RecyclerView.LayoutManager 대신  LinearLayoutManager 사용
        Recent_Word_LayoutManager = new LinearLayoutManager(this);
        /** 역순으로 추가 위한 레이아웃 매니저 추가 코드 setReverseLayout  setStackFromEnd  */
        Recent_Word_LayoutManager.setReverseLayout(true);   //  역순
        Recent_Word_LayoutManager.setStackFromEnd(true);    // 끝에서 부터 쌓는다.

        Recent_Word_RecyclerView.setLayoutManager(Recent_Word_LayoutManager);

        // specify an adapter (see also next example)
        Recent_Word_Dataset = new ArrayList<>();                          // 어레이리스트 객체인 데이터세트 객체 선언
        Recent_Word_Adapter = new Recent_word_List_Adapter(Recent_Word_Dataset);     // 어댑터 객체 선언하며 데이터세트를 어댑터에 연결
        Recent_Word_RecyclerView.setAdapter(Recent_Word_Adapter);     // 리사이클러뷰에 어댑터 설정



        // 인텐트로 넘어온 아이디 받기
        Intent intent = getIntent();    //이전 액티비티에서 수신할 데이터를 받기위해 선언

        // String 변수에 담기 - 이것을 기반으로 모임을 찾는다.
        ID = intent.getStringExtra("ID");        // 키 = NAME


         // 화면이 처음 만들어 지면서 검색어 리스트를 표현할때 불러오는 AsyncTask
         GetRecentWord task = new GetRecentWord();
         task.execute(ID);  // 아이디를 기반으로 검색어데이터를 찾는다.


    }// onCreate 끝


    /**
     * 최근 검색어를 불러오는 어싱크테스크
     * onCreate 와 동시에 바로 실행됨.
     */
    private class GetRecentWord extends AsyncTask<String, Void, String> {

        ProgressDialog progressDialog =new ProgressDialog(SearchActivity.this);
        String errorString = null;

        @Override
        protected void onPreExecute() {     // 시작전
            progressDialog.setProgressStyle(ProgressDialog.STYLE_SPINNER);
            progressDialog.setMessage("최근검색어를 확인 중 입니다...");
            // show dialog
            progressDialog.show();
            super.onPreExecute();
//            progressDialog = ProgressDialog.show(Make_list_Activity.this, "Please Wait", null, true, true); // 다이얼로그 실행
        }


        @Override
        protected void onPostExecute(String result) {   // 완료 후
            super.onPostExecute(result);
            Log.d(TAG, "response - " + result);


            String error_sql = result.substring(0,3);

            if (result.equals("아이디가 비어있습니다.")){    // 값을 받아 오지 못했다면
                Toast.makeText(SearchActivity.this,"아이디를 전달하지 못했습니다.",Toast.LENGTH_SHORT).show();
            }
            else if(error_sql.equals("SQL문")){  // result에서 잘라낸 데이터가 'SQL문' 이라면 에러가 발생햇다는 의미
                Toast.makeText(SearchActivity.this,""+result,Toast.LENGTH_SHORT).show();    // 에러 표시
            }
            else {  // 데이터 제대로 받아왔다는 의미
                mJsonString = result;
                showResult();
            }
            Log.e("다이얼로그 해제됨","");
            progressDialog.dismiss();   // 다이얼로그 해제
        }


        @Override
        protected String doInBackground(String... params) {

            String ID  = params[0];   // 아이디 전달

            Log.e("아이디 =",ID);

            String serverURL = "http://49.247.208.191/AndroidHive/Recent_Word/Recent_Word_List.php";   // AndroidHive/Recent_Word_List.php 의 파일로 이동
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
    }// GetRecentWord 끝


    /**
     * PHP 에서 가져온 JSON 형태의 데이터를
     * 파싱하여 리사이클러뷰에 넣어준다.
     * 최근검색어 표시
     */
    private void showResult(){
        try {
            JSONObject jsonObject = new JSONObject(mJsonString);
            JSONArray jsonArray = jsonObject.getJSONArray(TAG_JSON);

            for(int i=0;i<jsonArray.length();i++){  // 계속 jsonArray.length() 는 1일 것이다.
                String 제이슨길이 = String.valueOf(jsonArray.length());  // 제이슨 길이 int 를 스트링 으로
                Log.e("제이슨어레이 길이 = ",제이슨길이);

                JSONObject item = jsonArray.getJSONObject(i);


                String Current_Login_ID = ID;   // 현재 아이디
                String Word = item.getString(TAG_RECENT_WORD);    // 해당 아이디의 최근 검색어

                Log.e("--------------------- ","");
                Log.e("최근 검색어 전체 = ",Word);
                Log.e("현재 아이디 = ",Current_Login_ID);

                // 최근 검색어를 , 기준으로 배열에 삽입한다.
                String[] arr = Word.split(",");  // 0 , 1 , 2 , 3 , ? = 가변적 길이 현재는 4 개
                Log.e("첫번째 배열 = ", arr[0] );    // 경제


                if(arr[0].equals("")){  // 첫번째 배열이 비어 있으면 = 검색어가 없다는 의미와 마찬가지 임
                    Explain_Text.setText("최근 검색어가 없습니다");
                }
                else {
                    int arr_length = arr.length;
                    Log.e("배열 길이 = ", "" + arr_length);      // 배열 길이 확인

                    // Front_num 데이터에 arr[0] 를 삽입한다.
                    for (int a = 0; a < arr_length; a++) {    // 배열 길이는 달라지고. a 는 1씩 증가
                        Recent_Word_Dataset.add(new Recent_word_Data(Current_Login_ID, arr[a]));  // 아이디(공통), 현재 배열의 검색어
                    }
                }

            }// 반복문 종료
            Recent_Word_Adapter.notifyDataSetChanged();    // 데이터 추가가 완료되었으면 notifyDataSetChanged() 메서드를 호출해 데이터 변경 체크를 실행합니다. = 없어도 되긴 하던데

        } catch (JSONException e) {
            Log.d(TAG, "showResult : ", e);
        }
    }// showResult 끝








    // 액티비티 재 시작 시에 리스트뷰 갱신한다.
    @Override
    protected void onRestart() {
        super.onRestart();

        Recent_Word_Dataset.clear(); // 데이터 세트를 모두 클리어 한다.
        Recent_Word_Adapter.notifyDataSetChanged();

        // 리스트를 새로고침한다.
        GetRecentWord task = new GetRecentWord();
        task.execute(ID);  // 아이디를 기반으로 검색어데이터를 찾는다.
    }





}
