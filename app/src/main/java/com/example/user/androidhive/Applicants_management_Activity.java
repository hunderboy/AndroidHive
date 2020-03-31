package com.example.user.androidhive;

import android.app.ProgressDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.LinearLayout;
import android.widget.Spinner;
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
import java.util.List;

import static android.util.Log.e;

public class Applicants_management_Activity extends AppCompatActivity implements CheckBox_Click_to_Activity  {
    private static final String TAG = "Applicants_management_Activity";

    // 받아온 데이터 변수 - 신청모임 리스트
    String mJsonString;     // JSON 전체 String 값
    private static final String TAG_JSON = "get_applicants_data";   // JSON 데이터 key =  Users_Applied_List_data
    private static final String TAG_JSON_Search_Applicants_result = "Search_Applicants_result";   // JSON 데이터 key =  Users_Applied_List_data

    // JSON 상세 데이터 키
    private static final String TAG_Applicant_ID = "Applicant_ID";             // 1. 아이디
    private static final String TAG_Applicant_Phone = "Applicant_Phone";       // 2. 전화번호
    private static final String TAG_Application_status = "Application_status"; // 3. 신청상태
    private static final String TAG_Attendance_status = "Attendance_status";   // 4. 출석상태
    private static final String TAG_Applicant_NAME = "Applicant_Name";         // 5. 신청자 이름




    // 화면 위젯 설정
    ImageButton BackButton; // 뒤로가기 버튼
    ImageButton Applicant_Search_Button;    // 신청자 검색 버튼

    // 검색 위젯
    LinearLayout Search_layout; // 검색 위젯이 담겨있는 LinearLayout  - visiblity 조정을 위해 선언 기본 = gone
    EditText Search_EditText; // 검색을 위한 EditText
    Button Search_start_Button; // 검색 시작 버튼

    // 상태창 위젯
    LinearLayout State_window;       // 상태창 레이아웃
    ImageButton All_Select_button;   // 신청자 전체 선택 버튼 - 누르면 상태창 위젯 변경됨
    TextView textView1;              // "신청"
    TextView apply_num_textview;     // 신청자 수
    TextView textView2;              // "건"
    Spinner Filter_Spinner;          // 스피너 (상태창에 포함)
    // ------------------------------------------------------------------------
    TextView Select_Num_tv;          // 선택된 사용자 수 텍스트 뷰
    Button delete_button;            // 삭제 버튼

    TextView No_Applicant_textview; // "신청자가 없습니다."




    // 리사이클러뷰 관련 - 해당 모임의 신청자 리스트들을 보여준다.
    RecyclerView Candidate_List_RecyclerView;                 // 리사이클러뷰 객체  candidate_list_recycler_view
    RecyclerView.Adapter Candidate_List_Adapter;              // 어댑터 객체
    RecyclerView.LayoutManager Candidate_List_LayoutManager;  // 레이아웃매니저 객체
    ArrayList<Candidate_Data> Candidate_List_Dataset; // 데이터 세트를 담고 있는 어레이리스트 객체


    String Manage_Moim_NUMBER;      // 모임 번호 - 해당 모임 번호에 신청한 신청자 리스트들이 뜬다.
    int Applicants_Number = 0;      // 해당 모임의 신청자 수
    int Search_state = 0;           // 검색 상태 변수
    int Spinner_Value = 0;          // 스피너 변수 0 = 전체, 1 = 참여확정, 2 = 대기자
    int ALL_Select_button_Value = 0;// 전체 아이템 선택 변수
    int Selected_item_Number = 0;   // 선택된 아이템 숫자 - 체크박스 선택 수

    List<String> Will_deleted_ID_arr; // 삭제할 아이디가 담겨 있을 List<String> 배열

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_applicants_management_);

        Will_deleted_ID_arr = new ArrayList<String>(); // 삭제할 아이디가 담겨 있을 List<String> 배열

        // 신청자 없음
        No_Applicant_textview  = (TextView)findViewById(R.id.No_Applicant_textview); // "신청자가 없습니다."

        // 이미지 버튼
        BackButton = (ImageButton)findViewById(R.id.BackButton);        // 뒤로가기 버튼
        Applicant_Search_Button = (ImageButton)findViewById(R.id.Applicant_Search_Button);  // 신청자 검색 버튼

        // 검색 위젯
        Search_layout = (LinearLayout)findViewById(R.id.Search_layout); // 검색 위젯이 담겨있는 LinearLayout  - visiblity 조정을 위해 선언 기본 = gone
        Search_EditText = (EditText)findViewById(R.id.Search_EditText); // 검색을 위한 EditText
        Search_start_Button = (Button)findViewById(R.id.Search_start_Button); // 검색 시작 버튼

        // 상태창 위젯
        State_window = (LinearLayout)findViewById(R.id.State_window);
        All_Select_button = (ImageButton)findViewById(R.id.All_Select_button); // 신청자 전체 선택 버튼 - 누르면 상태창 위젯 변경됨
        textView1 = (TextView)findViewById(R.id.textView1); // "신청"
        apply_num_textview = (TextView)findViewById(R.id.apply_num_textview); // 신청자 수
        textView2 = (TextView)findViewById(R.id.textView2); // "건"
        // 스피너
        Filter_Spinner = (Spinner)findViewById(R.id.Filter_Spinner);

        /**
         * All_Select_button 신청자 전체 선택 버튼을 누르거나
         * 아이템의 선택 버튼을 누르면 나타나는 위젯들
         */
        Select_Num_tv = (TextView)findViewById(R.id.Select_Num_tv); // 선택된 사용자 수 텍스트 뷰
        delete_button = (Button)findViewById(R.id.delete_button);   // 삭제 버튼 아이템 삭제



        // 검색 시작 버튼 클릭 리스너
        Search_start_Button.setOnClickListener(new Button.OnClickListener() {
            @Override
            public void onClick(View view) {
                Candidate_List_Dataset.clear(); // 클리어
                Candidate_List_Adapter.notifyDataSetChanged(); // 어댑터에 알림

                String Search_Word = Search_EditText.getText().toString();  // 이름 또는 전화번호 입력
                String m_no = Manage_Moim_NUMBER; // 모임번호

                // 신청자 검색
                Search_Applicants_Task search_applicants_task = new Search_Applicants_Task();
                search_applicants_task.execute(Search_Word,m_no);
                Search_EditText.setText(""); // editTEXT 초기화
            }
        });// 검색 시작 버튼 클릭 리스너 끝


        // 삭제 버튼 클릭 리스너
        delete_button.setOnClickListener(new Button.OnClickListener() {
            @Override
            public void onClick(View view) {

                AlertDialog.Builder builder = new AlertDialog.Builder(view.getContext());
                builder.setTitle("신청자 삭제");
                builder.setMessage("삭제 를 하시겠습니까?");
                builder.setPositiveButton("확인",
                        new DialogInterface.OnClickListener() {
                            public void onClick(DialogInterface dialog, int which) {

                                for(int a = 0; a < Candidate_List_Dataset.size(); a++){ // 사이즈 만큼 반복
                                    if(Candidate_List_Dataset.get(a).Checking.equals("1")){ // 만약 아이템 체크 상태가 = 1 이면 리스트에서도 삭제하고, 서버에서도 데이터를 삭제해야 한다.

                                        String m_no = Candidate_List_Dataset.get(a).Manage_Moim_NUMBER; // 모임번호
                                        String a_id = Candidate_List_Dataset.get(a).User_ID;            // 아이디
                                        Log.e("삭제할 ID = ",a_id );

                                        // 삭제 테스크 실행
                                        Delete_applicants_Task delete_applicants_task = new Delete_applicants_Task();
                                        delete_applicants_task.execute(m_no,a_id);

                                        Applicants_Number = Applicants_Number - 1;
                                        Candidate_List_Dataset.remove(a);   // 리스트에서 삭제
                                    }
                                }// 반복문 종료
                                Candidate_List_Adapter.notifyDataSetChanged(); // 어댑터에 알림

                                apply_num_textview.setText(String.valueOf(Applicants_Number));

                                // 상태창 원상복귀 해야 한다.
                                All_Select_button.setImageResource(R.drawable.ic_not_check_box);    // 선택해제 이미지 설정
                                // 1. 숨기기 GONE
                                Select_Num_tv.setVisibility(View.GONE); // 선택된 숫자
                                delete_button.setVisibility(View.GONE); // 삭제 버튼
                                Select_Num_tv.setText(String.valueOf(Selected_item_Number)); // 숫자 설정 = 0
                                // 2. 보이게
                                textView1.setVisibility(View.VISIBLE);          // "신청"
                                apply_num_textview.setVisibility(View.VISIBLE); // 신청자 수
                                textView2.setVisibility(View.VISIBLE);          // "건"
                                Filter_Spinner.setVisibility(View.VISIBLE);     // 스피너
                            }
                        });
                builder.setNeutralButton("취소",
                        new DialogInterface.OnClickListener() {
                            public void onClick(DialogInterface dialog, int which) {
                                Toast.makeText(getApplicationContext(),"삭제 취소되었습니다.",Toast.LENGTH_LONG).show();
                            }
                        });
                builder.show();
            }
        });// 삭제 버튼 클릭 리스너 끝







        // 신청자 전체 선택 버튼 클릭 리스너
        All_Select_button.setOnClickListener(new Button.OnClickListener() {
            @Override
            public void onClick(View view) {
                if(ALL_Select_button_Value == 0){ // 전체 선택이 아닌 상태라면
                    Selected_item_Number = 0; // 초기화

                    // 아이템이 전체 선택이 되게끔 만들어야 한다.
                    for(int a = 0; a < Candidate_List_Dataset.size(); a++){ // 사이즈 만큼 반복
                        Candidate_List_Dataset.get(a).Checking = "1";
                        Candidate_List_Adapter.notifyDataSetChanged();
                        Selected_item_Number = Selected_item_Number + 1;    // 선택된 아이템 수 = 사이즈 크기
                    }

                    // 상태창 변경해야 한다.
                    All_Select_button.setImageResource(R.drawable.check_1_icon);    // 전체선택 이미지 설정
                    // 1. 숨기기 GONE
                    textView1.setVisibility(View.GONE);          // "신청"
                    apply_num_textview.setVisibility(View.GONE); // 신청자 수
                    textView2.setVisibility(View.GONE);          // "건"
                    Filter_Spinner.setVisibility(View.GONE);     // 스피너

                    // 2. 보이게
                    Select_Num_tv.setVisibility(View.VISIBLE); // 선택된 숫자
                    delete_button.setVisibility(View.VISIBLE); // 삭제 버튼
                    Select_Num_tv.setText(String.valueOf(Selected_item_Number)); // 숫자 설정


                    ALL_Select_button_Value = 1;    // 전체 선택 변수 => 1 전체 선택상태로 변경
                }
                else if(ALL_Select_button_Value == 1){   // 전체 선택이 되어 있는 상태라면

                    // 아이템이 전체 선택이 해제하게 만들어야 한다.
                    for(int a = 0; a < Candidate_List_Dataset.size(); a++){ // 사이즈 만큼 반복
                        Candidate_List_Dataset.get(a).Checking = "0";
                        Candidate_List_Adapter.notifyDataSetChanged();
                    }
                    Selected_item_Number = 0; // 선택된 아이템 수 => 0 으로 초기화

                    // 상태창 원상복귀 해야 한다.
                    All_Select_button.setImageResource(R.drawable.ic_not_check_box);    // 선택해제 이미지 설정
                    // 1. 숨기기 GONE
                    Select_Num_tv.setVisibility(View.GONE); // 선택된 숫자
                    delete_button.setVisibility(View.GONE); // 삭제 버튼
                    Select_Num_tv.setText(String.valueOf(Selected_item_Number)); // 숫자 설정 = 0

                    // 2. 보이게
                    textView1.setVisibility(View.VISIBLE);          // "신청"
                    apply_num_textview.setVisibility(View.VISIBLE); // 신청자 수
                    textView2.setVisibility(View.VISIBLE);          // "건"
                    Filter_Spinner.setVisibility(View.VISIBLE);     // 스피너


                    ALL_Select_button_Value = 0;    // 전체 선택 변수 => 0  전체 선택 해제로 변경
                }
            }
        });// 신청자 전체 선택 버튼 클릭 리스너 끝





        // 신청자 검색 버튼 클릭 리스너
        Applicant_Search_Button.setOnClickListener(new Button.OnClickListener() {
            @Override
            public void onClick(View view) {
                if(Search_state == 0){  // 검색 상태 변수가 0 이면
                    State_window.setVisibility(View.GONE); // 상태창 안보이게
                    Search_layout.setVisibility(View.VISIBLE);  // 검색창 보이게
                    Search_state = 1;
                }
                else {  // 검색 상태 변수가 1 이면
                    State_window.setVisibility(View.VISIBLE);   // 상태창 보이게
                    Search_layout.setVisibility(View.GONE);// 검색창 안보이게
                    Search_state = 0;
                }
            }
        });// 신청자 검색 버튼 클릭 리스너 끝



        /**
         * 스피너에 선택되어 있는 모임에서 신청자의 리스트를 가져와야 한다.
         * 스피너 설정이 우선
         * 그다음 신청자 리스트
         * 스피너에 선택된 해당 모임의 데이터 정보를 가지고 신청자 리스트를 표시해야 한다.
         */
        // 리사이클 러뷰 객체 선언
        Candidate_List_RecyclerView = (RecyclerView) findViewById(R.id.candidate_list_recycler_view);
        // 각 Item 들이 RecyclerView 의 전체 크기를 변경하지 않는 다면
        // setHasFixedSize() 함수를 사용해서 성능을 개선할 수 있습니다.
        // 변경될 가능성이 있다면 false 로 , 없다면 true를 설정해주세요.
        Candidate_List_RecyclerView.setHasFixedSize(true);
        // 리니어레이아웃 매니저 객체 선언
        Candidate_List_LayoutManager = new LinearLayoutManager(this);
        Candidate_List_RecyclerView.setLayoutManager(Candidate_List_LayoutManager);
        // 데이터세트, 어댑터 세팅
        Candidate_List_Dataset = new ArrayList<>();                          // 어레이리스트 객체인 데이터세트 객체 선언
        Candidate_List_Adapter = new Candidate_List_Adapter(Candidate_List_Dataset,this);     // 어댑터 객체 선언하며 데이터세트를 어댑터에 연결
        Candidate_List_RecyclerView.setAdapter(Candidate_List_Adapter);     // 리사이클러뷰에 어댑터 설정




        // 인텐트로 데이터 가져오기
        Intent intent = getIntent();
        // String 변수에 담기 - 이것을 기반으로 모임을 찾는다.
        Manage_Moim_NUMBER = intent.getStringExtra("management_Moim_NUMBER");  // 키 = management_Moim_NUMBER
        e("모임 번호 = ",Manage_Moim_NUMBER);   // 모임번호 확인
        // 모임 번호를 통해 해당 모임의 신청자 데이터를 받아와서 위젯에 표시해야함


// 필터 스피너 객체 선언 -------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------

        String[] Filter_list = new String[3]; // 스피너 리스트 담기 - 리스트 크기는 3(0~2)
        Filter_list[0] = "전체";
        Filter_list[1] = "참여확정";
        Filter_list[2] = "대기자";

        //어레이 어댑터(ArrayAdapter) 사용
        ArrayAdapter spinnerAdapter;
        spinnerAdapter = new ArrayAdapter(this, R.layout.support_simple_spinner_dropdown_item, Filter_list);
        Filter_Spinner.setAdapter(spinnerAdapter);

        //스피너 아이템 클릭시에 발생되는 클릭 리스너
        Filter_Spinner.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {   // 아이템이 선택되면 나오는 리스너

                if(position == 0){  // position == 0 => 전체
                    Spinner_Value = 0;  // 스피너 변수
                    Applicants_Number = 0; // 신청수 초기화
                    Toast.makeText(Applicants_management_Activity.this,Filter_Spinner.getItemAtPosition(position)+" 선택",Toast.LENGTH_SHORT).show();
                    Candidate_List_Dataset.clear(); // 초기화
                    Candidate_List_Adapter.notifyDataSetChanged();

                    // 각 모임 신청자 리스트 가져오기
                    applicant_list_Task get_data_task = new applicant_list_Task();
                    get_data_task.execute(Manage_Moim_NUMBER);  // 모임번호 전송

                }
                else if(position == 1){  // position == 1 => 참여확정
                    Spinner_Value = 1;// 스피너 변수
                    Applicants_Number = 0; // 신청수 초기화
                    Toast.makeText(Applicants_management_Activity.this,Filter_Spinner.getItemAtPosition(position)+" 선택",Toast.LENGTH_SHORT).show();
                    Candidate_List_Dataset.clear(); // 초기화
                    Candidate_List_Adapter.notifyDataSetChanged();

                    // 각 모임 신청자 리스트 가져오기
                    applicant_list_Task get_data_task = new applicant_list_Task();
                    get_data_task.execute(Manage_Moim_NUMBER);  // 모임번호 전송

                }
                else if(position == 2){  // position == 2 => 대기자
                    Spinner_Value = 2;// 스피너 변수
                    Applicants_Number = 0; // 신청수 초기화
                    Toast.makeText(Applicants_management_Activity.this,Filter_Spinner.getItemAtPosition(position)+" 선택",Toast.LENGTH_SHORT).show();
                    Candidate_List_Dataset.clear(); // 초기화
                    Candidate_List_Adapter.notifyDataSetChanged();

                    // 각 모임 신청자 리스트 가져오기
                    applicant_list_Task get_data_task = new applicant_list_Task();
                    get_data_task.execute(Manage_Moim_NUMBER);  // 모임번호 전송

                }
            }// onItemSelected 끝
            @Override
            public void onNothingSelected(AdapterView<?> parent) {  // 아무것도 선택이 안되었을때
            }
        });
// 스피너 끝 -------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------




        ImageButton BackButton = (ImageButton)findViewById(R.id.BackButton);
        // BackButton 클릭 리스너
        BackButton.setOnClickListener(new Button.OnClickListener() {
            @Override
            public void onClick(View view) {
                finish();// 현재 액티비티 종료
            }
        });



    }// onCreate 끝





    // 아이템 클릭 시 어댑터에서 화면으로 넘어오는 콜백 함수 = OnitemClick 인터페이스 사용
    // 인터페이스 onClick으로 실행됨.
    @Override
    public void onClick(int value, String users){   // value 에서는 +1,-1 숫자 값이 넘어오고, users 에서는 ID 가 넘어옴

        Selected_item_Number = Selected_item_Number + value;    // value 에따른 선택된 아이템 수 변화
        Select_Num_tv.setText(String.valueOf(Selected_item_Number));  //  숫자 변경

        if(Selected_item_Number > 0){ // Selected_item_Number 값이 0 보다 크면

            // 1. 숨기기 GONE
            textView1.setVisibility(View.GONE);          // "신청"
            apply_num_textview.setVisibility(View.GONE); // 신청자 수
            textView2.setVisibility(View.GONE);          // "건"
            Filter_Spinner.setVisibility(View.GONE);     // 스피너

            // 2. 보이게
            Select_Num_tv.setVisibility(View.VISIBLE); // 선택된 숫자
            delete_button.setVisibility(View.VISIBLE); // 삭제 버튼

        }
        else { // Selected_item_Number 값이  = 0 이면

            // 1. 숨기기 GONE
            Select_Num_tv.setVisibility(View.GONE); // 선택된 숫자
            delete_button.setVisibility(View.GONE); // 삭제 버튼

            // 2. 보이게
            textView1.setVisibility(View.VISIBLE);          // "신청"
            apply_num_textview.setVisibility(View.VISIBLE); // 신청자 수
            textView2.setVisibility(View.VISIBLE);          // "건"
            Filter_Spinner.setVisibility(View.VISIBLE);     // 스피너

        }



    }// onClick 끝







    /**
     * - 신청자 데이터 가져오는 AsyncTask
     */
    private class applicant_list_Task extends AsyncTask<String, Void, String> {
        ProgressDialog progressDialog = new ProgressDialog(Applicants_management_Activity.this);
        String errorString = null;
        @Override
        protected void onPreExecute() {     // 시작전
            super.onPreExecute();
            progressDialog.setProgressStyle(ProgressDialog.STYLE_SPINNER);
            progressDialog.setMessage("신청자 리스트를 가져오는중 입니다...");
            // show dialog
            progressDialog.show();
        }
        @Override
        protected void onPostExecute(String result) {   // 완료 후
            super.onPostExecute(result);
            Log.e("", "response - " + result);

            String error_sql = result.substring(0,3);

            if (result.equals("값을 받아오지 못했습니다.")){    // 값을 받아 오지 못했다면
                Log.e("", "데이터가 제대로 전달이 안됨.");
            }
            else if(result.equals("검색된 신청자 없음")){
                Log.e("검색된 신청자 없음",result);

            }
            else if(error_sql.equals("SQL문")){  // result에서 잘라낸 데이터가 'SQL문' 이라면 에러가 발생햇다는 의미
                Log.e("SQL문 에러 = ",result);
            }
            else {  // 데이터 제대로 입력되었다는 의미
                Log.e("결과 = ",result);
                mJsonString = result;
                showResult3();
            }
            progressDialog.dismiss();   // 다이얼로그 해제
        }

        @Override
        protected String doInBackground(String... params) {

            String m_moim_num = params[0]; // 모임 번호 전달

            Log.e("전달할 모임 번호 = ",m_moim_num);

            String serverURL = "http://49.247.208.191/AndroidHive/Moim_management/Get_Applicants_List.php";   // AndroidHive/Moim_management/Get_Applicants_List.php 의 파일로 이동
            String postParameters = "m_moim_num=" + m_moim_num ;

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
                Log.d("word_List_Adapter", "response code - " + responseStatusCode);
                InputStream inputStream;
                if(responseStatusCode == HttpURLConnection.HTTP_OK) {
                    inputStream = httpURLConnection.getInputStream(); } else{
                    inputStream = httpURLConnection.getErrorStream(); }
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
                Log.d("word_List_Adapter", "InsertData: Error ", e);
                errorString = e.toString();
                return null;
            }
        }


    }// applicant_list_Task 끝


    private void showResult3(){
        try {
            JSONObject jsonObject = new JSONObject(mJsonString);  // json 데이터 전체
            JSONArray jsonArray = jsonObject.getJSONArray(TAG_JSON); // 키에 따라서 json object 분할

            for(int i=0;i<jsonArray.length();i++){  // 불러오는 객체가 1개 이상이면 더 늘어남. 기본 = 1
                String 제이슨길이 = String.valueOf(jsonArray.length());  // 제이슨 길이 int 를 스트링 으로
                Log.e("제이슨어레이 길이 = ",제이슨길이);
                JSONObject item = jsonArray.getJSONObject(i);


                // 데이터 파싱
                String Applicant_ID = item.getString(TAG_Applicant_ID);             // 1. 아이디
                String Applicant_Phone = item.getString(TAG_Applicant_Phone);       // 2. 전화번호
                String Application_status = item.getString(TAG_Application_status); // 3. 신청상태
                String Attendance_status = item.getString(TAG_Attendance_status);   // 4. 출석상태
                                                                                    // 5. 모임번호
                String Applicant_NAME = item.getString(TAG_Applicant_NAME);         // 6. 신청자 이름
                String Checking = "0";                                              // 7. 체킹 유무


                Log.e("--------------------- ","");
                Log.e("// 1. 신청자 아이디 = ",Applicant_ID);
                Log.e("// 2. 신청자 전화번호 = ",Applicant_Phone);
                Log.e("// 3. 신청자 신청상태 = ",Application_status);
                Log.e("// 4. 신청자 출석상태 = ",Attendance_status);
                Log.e("// 5. 신청자 이름 = ",Applicant_NAME);

                if(Spinner_Value == 0){ // 스피너 전체 선택
                    Applicants_Number =  Applicants_Number + 1; // 신청 수 증가
                    // 아이디, 전화번호, 신청 상태, 출석 상태, 모임 번호
                    Candidate_List_Dataset.add(new Candidate_Data(Applicant_ID , Applicant_Phone, Application_status, Attendance_status, Manage_Moim_NUMBER, Applicant_NAME, Checking  ));  // 아이템 추가
                }
                else if(Spinner_Value == 1){ // 스피너 참여확정 선택
                    if(Application_status.equals("참여확정") ){ // 신청 상태가 참여확정일 경우
                        Applicants_Number =  Applicants_Number + 1;// 신청 수 증가
                        // 아이디, 전화번호, 신청 상태, 출석 상태, 모임 번호
                        Candidate_List_Dataset.add(new Candidate_Data(Applicant_ID , Applicant_Phone, Application_status, Attendance_status, Manage_Moim_NUMBER, Applicant_NAME , Checking ));  // 아이템 추가
                    }
                }
                else if(Spinner_Value == 2){ // 스피너 대기자 선택
                    if(Application_status.equals("대기자") ){ // 신청 상태가 참여확정일 경우
                        Applicants_Number =  Applicants_Number + 1;// 신청 수 증가
                        // 아이디, 전화번호, 신청 상태, 출석 상태, 모임 번호
                        Candidate_List_Dataset.add(new Candidate_Data(Applicant_ID , Applicant_Phone, Application_status, Attendance_status, Manage_Moim_NUMBER, Applicant_NAME  , Checking));  // 아이템 추가
                    }
                }

            }// 반복문 종료

            apply_num_textview.setText(String.valueOf(Applicants_Number)); // 신청자 수 숫자 설정
            Candidate_List_Adapter.notifyDataSetChanged();    // 데이터 추가가 완료되었으면 notifyDataSetChanged() 메서드를 호출해 데이터 변경 체크를 실행합니다.
        } catch (JSONException e) {
            Log.d("showResult : ", String.valueOf(e));
        }

    }// showResult 끝




    /**
     * - 신청자 삭제하는 AsyncTask
     */
    private class Delete_applicants_Task extends AsyncTask<String, Void, String> {
        ProgressDialog progressDialog = new ProgressDialog(Applicants_management_Activity.this);
        String errorString = null;
        @Override
        protected void onPreExecute() {     // 시작전
            super.onPreExecute();
            progressDialog.setProgressStyle(ProgressDialog.STYLE_SPINNER);
            progressDialog.setMessage("삭제 중 입니다...");
            // show dialog
            progressDialog.show();
        }
        @Override
        protected void onPostExecute(String result) {   // 완료 후
            super.onPostExecute(result);
            Log.e("", "response - " + result);

            String error_sql = result.substring(0,3);

            if (result.equals("아이디 또는 모임번호 가 비어있습니다")){    // 값을 받아 오지 못했다면
                Log.e("", "아이디 또는 모임번호 가 비어있습니다");
            }
            else if(error_sql.equals("SQL문")){  // result에서 잘라낸 데이터가 'SQL문' 이라면 에러가 발생햇다는 의미
                Log.e("SQL문 에러 = ",result);
            }
            else {  // 데이터 제대로 입력되었다는 의미
                Log.e("결과 = ",result);
            }
            progressDialog.dismiss();   // 다이얼로그 해제
        }

        @Override
        protected String doInBackground(String... params) {

            String m_moim_num = params[0]; // 모임 번호 전달
            String applicant_id = params[1]; // 삭제할 아이디

            Log.e("전달할 모임 번호 = ",m_moim_num);
            Log.e("// 삭제할 아이디 = ",applicant_id);

            String serverURL = "http://49.247.208.191/AndroidHive/Moim_management/Delete_Applicants.php";   // AndroidHive/Moim_management/Delete_Applicants.php 의 파일로 이동
            String postParameters = "m_moim_num=" + m_moim_num + "&applicant_id=" + applicant_id ;

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
                Log.d("word_List_Adapter", "response code - " + responseStatusCode);
                InputStream inputStream;
                if(responseStatusCode == HttpURLConnection.HTTP_OK) {
                    inputStream = httpURLConnection.getInputStream(); } else{
                    inputStream = httpURLConnection.getErrorStream(); }
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
                Log.d("word_List_Adapter", "InsertData: Error ", e);
                errorString = e.toString();
                return null;
            }
        }


    }// applicant_list_Task 끝




    /**
     *  신청자 검색을 위한 AsyncTask
     */
    private class Search_Applicants_Task extends AsyncTask<String, Void, String> {
        ProgressDialog progressDialog =new ProgressDialog(Applicants_management_Activity.this);
        String errorString = null;
        @Override
        protected void onPreExecute() {     // 시작전
            progressDialog.setProgressStyle(ProgressDialog.STYLE_SPINNER);
            progressDialog.setMessage("신청자 검색중 입니다...");
            // show dialog
            progressDialog.show();
            super.onPreExecute();
        }
        @Override
        protected void onPostExecute(String result) {   // 완료 후
            super.onPostExecute(result);
            Log.d("Applicants_management", "response - " + result);

            String error_sql = result.substring(0,3);

            if (result.equals("검색어 비어있음")){
                Log.e("검색어 비어있음","");
            }
            else if(result.equals("검색된 신청자 없음")){
                Log.e("검색된 신청자 없음","");
            }
            else if(error_sql.equals("SQL문")){
                Log.e("SQL문 에러 = ",result);
            }
            else { // 제대로 넘어옴
                mJsonString = result;
                showResult();
            }
            progressDialog.dismiss();   // 다이얼로그 해제
        }


        @Override
        protected String doInBackground(String... params) {

            String search_word = params[0];   // 검색어 - 이름 또는 전화번호 입력
            String m_no = params[1];   // 모임번호

            Log.e("이름 또는 전화번호 입력 검색어 =",search_word);
            Log.e("모임번호 =",m_no);

            String serverURL = "http://49.247.208.191/AndroidHive/Moim_management/Search_Applicants.php";   // AndroidHive/Moim_management/Search_Applicants.php 의 파일로 이동
            String postParameters = "search_word=" + search_word + "&m_no=" + m_no ;

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
                Log.d("Applicants_management", "response code - " + responseStatusCode);

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
                Log.d("Applicants_management", "InsertData: Error ", e);
                errorString = e.toString();
                return null;
            }
        }
    }// Get_Searched_MoimData 끝


    // 검색결과 보여 주는 메소드
    private void showResult(){
        Applicants_Number = 0;
        try {
            JSONObject jsonObject = new JSONObject(mJsonString);
            JSONArray jsonArray = jsonObject.getJSONArray(TAG_JSON_Search_Applicants_result);

            for(int i=0;i<jsonArray.length();i++){  // 불러오는 객체가 1개 이상이면 더 늘어남. 기본 = 1
                String 제이슨길이 = String.valueOf(jsonArray.length());  // 제이슨 길이 int 를 스트링 으로
                Log.e("제이슨어레이 길이 = ",제이슨길이);
                JSONObject item = jsonArray.getJSONObject(i);

                // 데이터 파싱
                String Applicant_ID = item.getString(TAG_Applicant_ID);             // 1. 아이디
                String Applicant_Phone = item.getString(TAG_Applicant_Phone);       // 2. 전화번호
                String Application_status = item.getString(TAG_Application_status); // 3. 신청상태
                String Attendance_status = item.getString(TAG_Attendance_status);   // 4. 출석상태
                // 5. 모임번호
                String Applicant_NAME = item.getString(TAG_Applicant_NAME);         // 6. 신청자 이름
                String Checking = "0";                                              // 7. 체킹 유무

                Log.e("--------------------- ","");
                Log.e("// 1. 신청자 아이디 = ",Applicant_ID);
                Log.e("// 2. 신청자 전화번호 = ",Applicant_Phone);
                Log.e("// 3. 신청자 신청상태 = ",Application_status);
                Log.e("// 4. 신청자 출석상태 = ",Attendance_status);
                Log.e("// 5. 신청자 이름 = ",Applicant_NAME);


                // 데이터 검색 된다 하더라도 검색창은 유지 해야한다.



                if(Spinner_Value == 0){ // 스피너 전체 선택
                    Applicants_Number =  Applicants_Number + 1; // 신청 수 증가
                    // 아이디, 전화번호, 신청 상태, 출석 상태, 모임 번호
                    Candidate_List_Dataset.add(new Candidate_Data(Applicant_ID , Applicant_Phone, Application_status, Attendance_status, Manage_Moim_NUMBER, Applicant_NAME, Checking  ));  // 아이템 추가
                }
                else if(Spinner_Value == 1){ // 스피너 참여확정 선택
                    if(Application_status.equals("참여확정") ){ // 신청 상태가 참여확정일 경우
                        Applicants_Number =  Applicants_Number + 1;// 신청 수 증가
                        // 아이디, 전화번호, 신청 상태, 출석 상태, 모임 번호
                        Candidate_List_Dataset.add(new Candidate_Data(Applicant_ID , Applicant_Phone, Application_status, Attendance_status, Manage_Moim_NUMBER, Applicant_NAME , Checking ));  // 아이템 추가
                    }
                }
                else if(Spinner_Value == 2){ // 스피너 대기자 선택
                    if(Application_status.equals("대기자") ){ // 신청 상태가 참여확정일 경우
                        Applicants_Number =  Applicants_Number + 1;// 신청 수 증가
                        // 아이디, 전화번호, 신청 상태, 출석 상태, 모임 번호
                        Candidate_List_Dataset.add(new Candidate_Data(Applicant_ID , Applicant_Phone, Application_status, Attendance_status, Manage_Moim_NUMBER, Applicant_NAME  , Checking));  // 아이템 추가
                    }
                }

            }// 반복문 종료
            apply_num_textview.setText(String.valueOf(Applicants_Number)); // 신청자 수 숫자 설정
            Candidate_List_Adapter.notifyDataSetChanged();    // 데이터 추가가 완료되었으면 notifyDataSetChanged() 메서드를 호출해 데이터 변경 체크를 실행합니다.
        } catch (JSONException e) {

            Log.d("Applicants_management", "showResult : ", e);
        }

    }// showResult 끝




}// 코드 끝
