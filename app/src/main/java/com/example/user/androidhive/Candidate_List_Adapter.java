package com.example.user.androidhive;

import android.content.DialogInterface;
import android.content.res.ColorStateList;
import android.graphics.Color;
import android.os.AsyncTask;
import android.os.Build;
import android.support.annotation.RequiresApi;
import android.support.v7.app.AlertDialog;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.TextView;
import android.widget.Toast;

import java.io.BufferedReader;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.net.HttpURLConnection;
import java.net.URL;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.TimeZone;

/**
 * Created by user on 2018-04-03.
 * 신청자 관리 화면에서
 * 신청자 리스트를 보여주는 리사이클러뷰 어댑터
 */

public class Candidate_List_Adapter extends RecyclerView.Adapter<Candidate_List_Adapter.ViewHolder>{
    private static final String TAG = "Candidate_List_Adapter";


    private ArrayList<Candidate_Data> Candidate_List_Dataset;   // 데이터를 가질 어레이리스트를 선언

    // 시간 가져오기
    TimeZone time;  // Asia/Seoul = 지역 설정 을 통해 한국 시간의 가져오기 위한 TimeZone 객체
    Date date;      // Date 객체 = 시간 포맷을 나타내기위해서 필요함.
    DateFormat df = new SimpleDateFormat("yyyy-MM-dd"); // 년-월-일

    String timeTEXT;    // EX) 오후 or 오전 04:15

    // 현재 시간 구하는 메소드
    private String Korea_getTime() {
        date = new Date();
        time = TimeZone.getTimeZone("Asia/Seoul");  // 한국 지역 설정
        df.setTimeZone(time);                       // 한국 시간 설정
        return df.format(date); // 전체시간 데이터 가져옴
    }



    // applicants_list_item  레이아웃 에 존재하는 위젯들을 바인딩 한다.
    public class ViewHolder extends RecyclerView.ViewHolder{
        public View view;   // 뷰 바인딩 메소드에서 아이템 클릭 리스너를 실행 시키기 위해서 필요한 뷰 객체
        // 이미지 버튼
        ImageButton user_select_Button; // 아이템 체크박스 버튼

        // 표현할 이미지 뷰와 텍스트 객체 선언
        TextView user_id_textview;          // 아이디
        TextView user_phoneNum;             // 전화번호
        TextView user_statement;            // 신청 상태
        TextView attendence_statement;  // 출석 상태

        // 출석 버튼
        Button attendence_button;


        // 뷰홀더 메소드 생성
        public ViewHolder(View view) {
            super(view);
            this.view = view;
            // 이미지 버튼
             user_select_Button = (ImageButton)view.findViewById(R.id.user_select_Button);  // 아이템 체크박스 버튼

            // 표현할 이미지 뷰와 텍스트 객체 선언
             user_id_textview = (TextView)view.findViewById(R.id.user_id_textview);                 // 아이디
             user_phoneNum = (TextView)view.findViewById(R.id.user_phoneNum);                       // 전화번호
             user_statement = (TextView)view.findViewById(R.id.user_statement);                     // 신청 상태
            attendence_statement = (TextView)view.findViewById(R.id.attendence_statement_textview); // 출석 상태

            // 출석 버튼
             attendence_button = (Button)view.findViewById(R.id.attendence_button); // 출석 버튼

        }// ViewHolder 끝

    }// 뷰홀더 끝

    private CheckBox_Click_to_Activity mCallback;  // onClick 콜백 객체 - 체크 박스 때문에

    // 어댑터 생성자
    // Provide a suitable constructor (depends on the kind of dataset)
    public Candidate_List_Adapter(ArrayList<Candidate_Data> myDataset, CheckBox_Click_to_Activity listener) {
        Candidate_List_Dataset = myDataset;
        this.mCallback = listener;
    }

    // Create new views (invoked by the layout manager)
    @Override
    public Candidate_List_Adapter.ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        // create a new view
        View v = LayoutInflater.from(parent.getContext()).inflate(R.layout.applicants_list_item, parent, false);
        // 뷰 홀더에 넣어줄 뷰를 찾는 과정. = recent_word_list.xml 이 아이템의 뷰 이다.
        ViewHolder vh = new ViewHolder(v);  // 이 view를 넣기위한 ViewHolder를 다음과 같이 선언한 후 넣는다.
        return vh;
    }


    // 뷰 바인딩
    @RequiresApi(api = Build.VERSION_CODES.LOLLIPOP)
    @Override
    public void onBindViewHolder(final ViewHolder holder, final int position) {


        /**
         *  각 데이터에 대한 모든 예외처리 다해야한다.
         *  1. 대기자
         *  2. 미출석
         */
        // 데이터 불러오기
        String User_ID = Candidate_List_Dataset.get(position).User_ID;                                 // 1. 신청자 아이디
        String User_Phone_Num = Candidate_List_Dataset.get(position).User_Callnum;                     // 2. 신청자 전화번호
        String User_Statement  = Candidate_List_Dataset.get(position).User_Apply_State;                // 3. 신청자 신청 상태
        String User_Attendence_Statement = Candidate_List_Dataset.get(position).User_Attendance_State; // 4. 신청자 출석 상태
        String User_Name = Candidate_List_Dataset.get(position).User_NAME;                             // 5. 신청자 이름
        Log.e("TAG = ",TAG );
        Log.e("// 1. 신청자 아이디 = ",User_ID );
        Log.e("// 2. 신청자 전화번호 = ",User_Phone_Num );
        Log.e("// 3. 신청자 신청 상태 = ",User_Statement );
        Log.e("// 4. 신청자 출석 상태 = ",User_Attendence_Statement );
        Log.e("// 5. 신청자 이름 = ",User_Name );



// 텍스트 세팅 ------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------
        // user_id_textview 설정
        String Name_and_ID = User_Name+"("+User_ID+")";

        holder.user_id_textview.setText(Name_and_ID);  // 1. 신청자 이름(아이디)
        holder.user_phoneNum.setText(User_Phone_Num);  // 2. 신청자 전화번호

        /**
         * 신청 상태의 기본은 '대기자' 이다.
         * 배경은 회색
         * => 하단의 조건문을 거치게 되면
         * 1. 텍스트 = '참여확정'
         * 2. 배경 = 파랑색
         * 3. 글자 색 = 파랑색
         * 으로 변경 된다.
         */
        // 신청자 신청 상태 예외처리 => 대기자 or 참여확정
        if(!User_Statement.equals("대기자") ){ // 참여확정 인 경우        신청 상태가 "대기자" 이 아닐 경우 => '참여 확정' 했다는 의미
            Log.e("참여확정 예외처리 들어옴 = ",User_Statement );
            holder.user_statement.setTextColor(Color.BLUE); // 글자색 파랑색으로
            ColorStateList button_tintColor_Blue = new ColorStateList(new int[][]{new int[0]}, new int[]{0xff00ddff});  // 파란색
            holder.user_statement.setBackgroundTintList(button_tintColor_Blue); // 텍스트뷰 배경 파랑색 설정
            holder.user_statement.setText(User_Statement); // 3. 신청자 신청 상태 => 참여확정
        }
        else{   // 대기자 인 경우
            Log.e("대기자 예외처리 들어옴 = ",User_Statement );
            holder.user_statement.setTextColor(Color.BLACK); // 글자색 검은색으로
            ColorStateList Background_tintColor_Gray = new ColorStateList(new int[][]{new int[0]}, new int[]{0xff808080});  // 회색 ff4444
            holder.user_statement.setBackgroundTintList(Background_tintColor_Gray); // 텍스트뷰 배경 회색 설정
            holder.user_statement.setText(User_Statement); // 3. 신청자 신청 상태 => 대기자
        }

        /**
         * 출석 상태의 기본은 '미출석' 이다.
         * => 하단의 조건문을 거치게 되면
         * 1. 텍스트 = EX) 2018-02-24
         * 2. 글자색 = 파랑색
         * 3. 버튼 텍스트 = '출석취소'
         * 4. 버튼 틴트컬러 = 노란색
         * 으로 변경된다.
         */
        // 신청자 출석 상태 예외처리 => 미출석 or EX) 2018-01-04
        if(!User_Attendence_Statement.equals("미출석") ){ // 2018-01-04 출석함 경우        출석 상태가 "미출석" 이 아닐 경우 => 출석 했다는 의미
            Log.e("2018 출석처리 들어옴 = ",User_Statement );
            holder.attendence_statement.setTextColor(Color.BLUE); // 글자색 파랑색으로
            holder.attendence_statement.setText(User_Attendence_Statement); // 4. 신청자 출석 상태 - 2018-02-24

            // 출석 버튼 예외처리
            holder.attendence_button.setText("출석취소");   // 버튼 텍스트를 "출석취소"로 변경
            ColorStateList button_tintColor_Yellow = new ColorStateList(new int[][]{new int[0]}, new int[]{0xffffdd33});    // 노랑색
            holder.attendence_button.setBackgroundTintList(button_tintColor_Yellow); // 버튼 틴트컬러 노랑색 변경
        }
        else{   // 미출석 경우
            Log.e("미출석 예외처리 들어옴 = ",User_Statement );
            holder.attendence_statement.setTextColor(Color.BLACK); // 글자색 검정색으로
            holder.attendence_statement.setText(User_Attendence_Statement); // 4. 신청자 출석 상태 - 미출석

            // 출석 버튼 예외처리
            holder.attendence_button.setText("출석확인");   // 버튼 텍스트를 "출석취소"로 변경
            ColorStateList button_tintColor_Blue = new ColorStateList(new int[][]{new int[0]}, new int[]{0xff00ddff});    // 파랑색
            holder.attendence_button.setBackgroundTintList(button_tintColor_Blue); // 버튼 틴트컬러 파랑색 변경
        }

// 끝  ----------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------


// 체크박스 예외처리 ------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------
        String Check_Value = Candidate_List_Dataset.get(position).Checking; // 체크박스 값

        if(Check_Value.equals("0") ){ // 만약 해당 아이템의 체크박스 선택 유무에서 0 이면
            Log.e("체크박스 0 처리들어옴 = ","" );
            // 체크박스 선택 안됨 처리
            holder.user_select_Button.setImageResource(R.drawable.ic_not_check_box);
        }
        else{   // 미출석 경우
            Log.e("체크박스 1 처리들어옴 = ","" );
            // 체크박스 선택 됨 처리
            holder.user_select_Button.setImageResource(R.drawable.check_1_icon);
        }
        Log.e("체크박스 상태 = ",Candidate_List_Dataset.get(position).Checking );

// 끝  ----------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------


        // 출석 버튼 리스너 처리
        holder.attendence_button.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(final View view) {
                String button_text = holder.attendence_button.getText().toString(); // 버튼 텍스트 가져옴

                if(button_text.equals("출석확인")){ // 출석확인 인 경우 ------------------------------------------------------------------------------------------------------------------------------

                    AlertDialog.Builder builder = new AlertDialog.Builder(view.getContext());
                    builder.setTitle("출석 확인");
                    builder.setMessage("출석 확인을 하시겠습니까?");
                    builder.setPositiveButton("확인",
                            new DialogInterface.OnClickListener() {
                                public void onClick(DialogInterface dialog, int which) {

                                    /**
                                     * 확인 버튼을 누르면 오늘 날짜로 출석일 데이터를 만들어서
                                     * 해당 모임 을 찾고
                                     * 아이디를 찾고
                                     * 해당 아이디의 출석 상태를 '미출석'으로 변경한다.
                                     * '미출석'을 바로 해당 아이템에 변경하여 적용 시키고
                                     * 출석취소 버튼은 -> 출석확인 버튼으로 변경 시킨다.출석취소
                                     */
                                    // 모임번호
                                    String M_no = Candidate_List_Dataset.get(position).Manage_Moim_NUMBER;
                                    // 아이디
                                    String user_id = Candidate_List_Dataset.get(position).User_ID;
                                    // 오늘날짜 는 PHP 에서 삽입한다.

                                    timeTEXT = Korea_getTime();
                                    Log.e("오늘 날짜 = ",timeTEXT);

                                    // 출석 상태 텍스트 파랑색, 텍스트 설정
                                    holder.attendence_statement.setText(timeTEXT); // 신청자 출석 상태 - EX) 미출석 or 2018-02-24
                                    holder.attendence_statement.setTextColor(Color.BLUE); // 글자색 파랑색으로
                                    // 출석 버튼 예외처리
                                    holder.attendence_button.setText("출석취소");   // 버튼 텍스트를 "출석취소"로 변경
                                    ColorStateList button_tintColor_Yellow = new ColorStateList(new int[][]{new int[0]}, new int[]{0xffffdd33});    // 노랑색
                                    holder.attendence_button.setBackgroundTintList(button_tintColor_Yellow); // 버튼 틴트컬러 노랑색 변경

                                    // 출석 상태 변경 위한 attendence_change_task
                                    Attendence_Change_Task attendence_change_task = new Attendence_Change_Task();
                                    attendence_change_task.execute(M_no,user_id,timeTEXT);  // 모임번호, 아이디, 현재날짜
                                }
                            });
                    builder.setNeutralButton("취소",
                            new DialogInterface.OnClickListener() {
                                public void onClick(DialogInterface dialog, int which) {
                                    Toast.makeText(view.getContext(),"취소되었습니다.",Toast.LENGTH_LONG).show();
                                }
                            });
                    builder.show();
                }
                else if(button_text.equals("출석취소")){ // 출석취소 인 경우 --------------------------------------------------------------------------------------------------------------------------------------------

                    AlertDialog.Builder builder = new AlertDialog.Builder(view.getContext());
                    builder.setTitle("출석 취소");
                    builder.setMessage("출석 취소를 하시겠습니까?");
                    builder.setPositiveButton("확인",
                            new DialogInterface.OnClickListener() {
                                public void onClick(DialogInterface dialog, int which) {

                                    /**
                                     * 확인 버튼을 누르면 오늘 날짜로 출석일 데이터를 만들어서
                                     * 해당 모임 을 찾고
                                     * 아이디를 찾고
                                     * 해당 아이디의 출석 상태를 오늘날짜로 변경한다.
                                     * 오늘 날짜를 바로 해당 아이템에 변경하여 적용 시키고
                                     * 출석확인 버튼은 -> 출석취소 버튼으로 변경 시킨다.
                                     */
                                    // 모임번호
                                    String M_no = Candidate_List_Dataset.get(position).Manage_Moim_NUMBER;
                                    // 아이디
                                    String user_id = Candidate_List_Dataset.get(position).User_ID;
                                    // 오늘날짜 는 PHP 에서 삽입한다.

                                    timeTEXT = "미출석";
                                    Log.e("미출석 = ",timeTEXT);

                                    // 출석 상태 텍스트 파랑색, 텍스트 설정
                                    holder.attendence_statement.setText(timeTEXT); // 신청자 출석 상태 - EX) 미출석 or 2018-02-24
                                    holder.attendence_statement.setTextColor(Color.BLACK); // 글자색 검정색으로
                                    // 출석 버튼 예외처리
                                    holder.attendence_button.setText("출석확인");   // 버튼 텍스트를 "출석취소"로 변경
                                    ColorStateList button_tintColor_Blue = new ColorStateList(new int[][]{new int[0]}, new int[]{0xff00ddff});  // 파란색
                                    holder.attendence_button.setBackgroundTintList(button_tintColor_Blue); // 버튼 틴트컬러 파란색 변경

                                    // 출석 상태 변경 위한 attendence_change_task
                                    Attendence_Change_Task attendence_change_task = new Attendence_Change_Task();
                                    attendence_change_task.execute(M_no,user_id,timeTEXT);  // 모임번호, 아이디, 미출석
                                }
                            });
                    builder.setNeutralButton("취소",
                            new DialogInterface.OnClickListener() {
                                public void onClick(DialogInterface dialog, int which) {
                                    Toast.makeText(view.getContext(),"취소되었습니다.",Toast.LENGTH_LONG).show();
                                }
                            });
                    builder.show();
                }// 조건문 종료
            }// 출석 버튼 클릭 리스너 끝
        });


        /**
         * 체크박스 버튼 리스너 달기
         */
        holder.user_select_Button.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(final View view) {
                // 체크박스 눌렀다.
                //  0 일 경우 Uncheck => check
                if(Candidate_List_Dataset.get(position).Checking.equals("0")){
                    // 해당 아이템이 체크 상태로 변경해야 함.
                    holder.user_select_Button.setImageResource(R.drawable.check_1_icon);
                    Candidate_List_Dataset.get(position).Checking = "1";    // 해당 아이템 체크 상태 = 1 로 변경
                    Log.e("해당 포지션의 체크상태 = ",Candidate_List_Dataset.get(position).Checking);

                    int one = 1;
                    String user_id = Candidate_List_Dataset.get(position).User_ID;
                    // 아이템이 처음으로 눌렸다면
                    // 리스트 화면 상태창이 변경되어야 한다,
                    mCallback.onClick(one,user_id); // (+1,유저아이디) 전송


                }
                // 1 일 경우 Check => uncheck
                else if(Candidate_List_Dataset.get(position).Checking.equals("1")){
                    // 해당 아이템이 체크안됨 상태로 변경해야 함.
                    holder.user_select_Button.setImageResource(R.drawable.ic_not_check_box);
                    Candidate_List_Dataset.get(position).Checking = "0";    // 해당 아이템 체크 상태 = 0 로 변경
                    Log.e("해당 포지션의 체크상태 = ",Candidate_List_Dataset.get(position).Checking);

                    int one = -1;
                    String user_id = Candidate_List_Dataset.get(position).User_ID;
                    // 아이템이 처음으로 눌렸다면
                    // 리스트 화면 상태창이 변경되어야 한다,
                    mCallback.onClick(one,user_id); // (-1,유저아이디) 전송




                }
            }// 출석 버튼 클릭 리스너 끝
        });








    }// onBindViewHolder 끝


    // Return the size of your dataset (invoked by the layout manager)
    @Override
    public int getItemCount() {
        return Candidate_List_Dataset.size();
    }






    /**
     * - 신청자 출석상태 변경 AsyncTask
     */
    private class Attendence_Change_Task extends AsyncTask<String, Void, String> {
        String errorString = null;
        @Override
        protected void onPreExecute() {     // 시작전
            super.onPreExecute();
        }
        @Override
        protected void onPostExecute(String result) {   // 완료 후
            super.onPostExecute(result);
            Log.e("", "response - " + result);

            String error_sql = result.substring(0,3);

            if (result.equals("값을 받아오지 못했습니다.")){    // 값을 받아 오지 못했다면
                Log.e("", "데이터가 제대로 전달이 안됨.");
            }
            else if(result.equals("신청자존재안함")){
                Log.e("신청자존재안함",result);
            }
            else if(result.equals("신청자중복오류")){
                Log.e("신청자중복오류",result);
            }
            else if(error_sql.equals("SQL문")){  // result에서 잘라낸 데이터가 'SQL문' 이라면 에러가 발생햇다는 의미
                Log.e("SQL문 에러 = ",result);
            }
            else {  // 데이터 제대로 입력되었다는 의미
                Log.e("결과 = ",result);

                String[] result_arr = result.split(":::");
                Log.e("오늘날짜 or 미출석 = ",result_arr[1]);
                // 여기서 추가한 시간을 가져온다.
                // 가져온 시간을 데이터를 표시해 줘야한다.

            }
        }

        @Override
        protected String doInBackground(String... params) {

            String M_no = params[0];    // 모임 번호 전달
            String user_id = params[1]; // 아이디 전달
            String today = params[2];   // 오늘날짜 전달

            Log.e("전달할 모임 번호 = ",M_no);
            Log.e("전달할 아이디 = ",user_id);

            String serverURL = "http://49.247.208.191/AndroidHive/Moim_management/Attendence_Change.php";   // AndroidHive/Moim_management/Attendence_Change.php 의 파일로 이동
            String postParameters = "M_no=" + M_no + "&user_id=" + user_id + "&today=" + today  ;

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


    }// Attendence_Change_Task 끝




}// 코드 끝
