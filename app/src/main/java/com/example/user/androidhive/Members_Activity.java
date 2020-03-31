package com.example.user.androidhive;

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
import android.widget.Button;
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
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.TimeZone;

import static com.example.user.androidhive.MainActivity.receive0;

public class Members_Activity extends AppCompatActivity implements OnitemClick {
    private static String TAG = "Members_Activity";

    // 받아온 데이터 변수 - 신청모임 리스트
    String mJsonString;     // JSON 전체 String 값
    private static final String TAG_JSON = "Users_Applied_List_data";   // JSON 데이터 key =  Users_Applied_List_data
    // JSON 상세 데이터 키
    private static final String TAG_Moim_NO = "A_Moim_no";                  // 1. 모임번호
    private static final String TAG_Moim_Name = "A_Moim_Name";              // 2. 신청 모임명
    private static final String TAG_DEFAULT_imgURI = "Moim_default_imgURI"; // 3. 대표이미지 이름


    // 받아온 데이터 변수 - 신청자 리스트
    String mJsonString2;     // JSON 전체 String
    private static final String TAG_JSON_User_List = "Moim_Users_List_data";   // JSON 데이터 key =  Moim_Users_List_data
    // JSON 상세 데이터 키
    private static final String TAG_Moim_user_name = "member_name";  // 1. 신청자 이름
    private static final String TAG_Moim_user_ID = "Applicant_ID";   // 2. 신청자 아이디


    // 받아온 데이터 변수 - 신청자 리스트
    String mJsonString3;     // JSON 전체 String
    private static final String TAG_JSON_room_num = "C_Room_num";   // JSON 데이터 key =  Moim_Users_List_data
    // JSON 상세 데이터 키
    private static final String TAG_Moim_Room_Num = "Room_Num";  // 1. 신청자 이름



    // 스피너 객체
    Spinner spinner1;
    // 스피너 어댑터
    AdapterSpinner1 adapterSpinner1;
    // Applied_Moim_List_Spinner_Data 클래스의 데이터를 가지는 데이터 세트 = ArrayList
    ArrayList<Applied_Moim_List_Spinner_Data> Applied_Moim_List_Spinner_Data_SET;


    String user_name = receive0;    // 유저 아이디


    // 신청된 모임리스트 의 리사이클러뷰 관련
    RecyclerView Member_List_RecyclerView;                 // 리사이클러뷰 객체
    RecyclerView.Adapter Member_List_Adapter;              // 어댑터 객체
    RecyclerView.LayoutManager Member_List_LayoutManager;  // 레이아웃매니저 객체
    ArrayList<Applied_User_list_Data> Member_List_Dataset; // 데이터 세트를 담고 있는 어레이리스트 객체

    // 채팅방 만들기 버튼
    Button Create_Room_button;

    // 초대 할 사람 수
    int people_number; // 사람수 int 자료형
    TextView num_count_text; // 선택 사람 수 확인 TextView


    // 방생성 데이터 = 총 4 개
    String Room_Moim_Name;                // 방 모임명
    String Participants = "선택된 사용자 없음"; // 방 참여자들
    int Number_of_participants;           // 참여인원 수 (나 포함 되어야 하기 때문에 +1)
    String Moim_img_File_Name;            // 모임대표이미지 파일명


    int spinner_Click_Num = 0;  // 스피너 클릭 시 미리 선택되어있던 사용자 초기화 하기 위한 변수
    int spinner_position = 0;   // 사용자가 현재 보고 있는 모임의 스피너 포지션,  스피너 기본 포지션은 0

    String Moim_NUMBER;  // 모임 번호
    int people_initiate = 0; // 참여자 초기화 변수, 스피너에서 선택한 모임이 변경되면 참여자들 리스트를 초기화한다. MemberList 어댑터에서 변경.



// --------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------
    // 시간 가져오기
    TimeZone time;  // Asia/Seoul = 지역 설정 을 통해 한국 시간의 가져오기 위한 TimeZone 객체
    Date date;      // Date 객체 = 시간 포맷을 나타내기위해서 필요함.
    DateFormat room_create_time = new SimpleDateFormat("yyyy-MM-dd__HH:mm:ss"); // 년-월-일 시:분:초

    String Room_Key;

    // 현재 시간 구하는 메소드
    private String Room_Create_getTime() {
        date = new Date();
        time = TimeZone.getTimeZone("Asia/Seoul");  // 한국 지역 설정
        room_create_time.setTimeZone(time);                       // 한국 시간 설정
        return room_create_time.format(date); // 전체시간 데이터 가져옴
    }

// --------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------






    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_members_);


        // 멤버 액티비티를 시작하면 SQLite 에 'CHAT_ROOM.db' DB 가 생성된다.
        // DB 내용을 표현할 액티비티 에서는 표시해야한다.
        final SQLite_Create_Room SQLite_DB_Helper = new SQLite_Create_Room(getApplicationContext(), "CHAT_ROOM.db", null, 1); // "/mnt/sdcard/" +
        Log.e("SQL 확인 = ",SQLite_DB_Helper.RoomList_info_getResult());  // SQL 확인

//        SQLite_DB_Helper.RoomList_info_delete("masury2011___2018-03-30__17:00:48");


        // 채팅방 만들기 버튼 객체 생성
        Create_Room_button = (Button)findViewById(R.id.Create_Room_button);
        Create_Room_button.setOnClickListener(new Button.OnClickListener() {
            @Override
            public void onClick(View view) { // 채팅방 만들기 버튼을 클릭하면
                /**
                 * 서버에서 방을 만들어 주고
                 * 방생성한 해당 사용자의 룸 리스트에도 방 데이터가 생성이 되어야 한다..!!
                 * 채팅방 입성 화면도 보여 줘야 한다.
                 */
                /**
                 *  1. 일단 방 데이터를 생성 하자
                    방생성에 필요한 데이터
                    - 1. 방 모임명 = 해당 모임명 Room_Moim_Name   - 스피너에서
                    - 2. 참여자들 = 아이디들 Participants   - 아이템 클릭 리스너 에서
                    - 3. 참여인원 수 = people_number + 1 = Number_of_participants  - 콜백 함수로
                    - 4. 모임 이미지 name = Moim_img_File_Name   - 스피너에서
                 */
                // 일단 로그를 찍어서 해당 데이터를 가져오는지 확인한다.
                Number_of_participants = people_number + 1; // 참여 인원 수의 경우, 나를 포함 하여야 하므로 +1 한다.
                Log.e("해당 모임명 = ",Room_Moim_Name);                           // 생성할 방의 모임명
                Log.e("참여자들 = ",Participants);                                // 나 포함 EX) masury2011,ki1234,ukdif8978, 형태의 꼴 = 선택된 사용자 없으면 애초에 방개설 불가
                Log.e("참여 인원 수 = ", String.valueOf(Number_of_participants)); // 나 포함 + 1
                Log.e("모임 이미지 name = ",Moim_img_File_Name);                  // 해당 모임의 이미지 Uri

                // 예외 처리 필요함.
                // 그런데 모임명, 모임이미지는 바뀔 때 마다 변해야 하는데... 모임 번호 가지고 있으면서 서버측에서 변화 할때 마다 들고 있어야 하지 않나
                // 모임 테이블에서 모임명, 모임이미지 변화 할때, 동시에 변하는 ... 일단은 제끼자

                // 해당 4개의 데이터들을 DB의 Room_list 테이블에 삽입한다.
                // 방을 만듬과 동시에 채팅방 입성 - 그 채팅방에서는 서버 룸 번호를 들고 있어야 한다.
                if(Participants.equals("선택된 사용자 없음")){  // 선택된 사용자 없을 경우 예외처리
                    Toast.makeText(getApplicationContext(),"초대 멤버를 선택해야 합니다.",Toast.LENGTH_SHORT).show();
                }
                else{   // 선택된 사용자가 있을 경우
                    // 채팅방 키 생성 - Room_Key 를 통해서 해당 방에 누가 있는지 알아내고 그 사용자에게만 채팅 전송하기 위함
                    String Room_Create_getTime = Room_Create_getTime(); // 시간 가져옴
                    Room_Key = receive0+"___"+Room_Create_getTime;      // 시간 결합  EX) masury2011___2018-03-15__15:30:22   = 방 생성자 + 시간

                    Log.e("Room_Key = ",Room_Key);  // 방 키 확인

                    // RoomList_no 는 자동으로 숫자가 증가한다.
//                    SQLite_DB_Helper.RoomList_info_insert(Room_Key, Room_Moim_Name, Participants, Number_of_participants, Moim_img_File_Name ); // 삽입 후
//                    Log.e("SQL 확인 = ",SQLite_DB_Helper.RoomList_info_getResult());  // SQL 확인


                    // 방생성을 위한 AsyncTask
                    Create_Room_Task create_room_task = new Create_Room_Task();
                    create_room_task.execute(Room_Key,Room_Moim_Name , Participants , String.valueOf(Number_of_participants) ,  Moim_img_File_Name);  // 4개의 데이터 전송
                    // 4개의 데이터로 서버에 방 추가


                    // 채팅 화면 이동
                    // 방 키를 가지면서 이동
                    Intent intent = new Intent(getApplicationContext(), // 현재 화면의 제어권자
                            ServiceChat_Activity.class); // 다음 넘어갈 클래스 지정
                    intent.putExtra("Room_Key", Room_Key); // 서버 방 번호

                    startActivity(intent); // 다음 화면으로 넘어간다

                    finish(); // 멤버화면 종료

                }

            }
        });// 채팅방 만들기 버튼  끝



        people_number = 0;  // 사람 인원 수 초기화 = 0
        num_count_text = (TextView)findViewById(R.id.num_count_text);   // 명수 체크 번호
        num_count_text.setText(String.valueOf(people_number));  // 일단 0 으로 설정

        /**
         *  멤버 화면이 시작 되면서
         *  해당 아이디가 신청한 모임을 불러와야 한다.
         *  (화면 시작하면해서 해당 아이디 전달 -> 서버)
         *  서버에서는 해당아이디와 일치하는 데이터의
         *  모임번호(고유값), 모임명, 모임이미지- 텍스트 = 3가지 데이터
         *  가져오고
         *  그 해당 데이터들을 데이터 클래스의 입력값으로 뿌린다.
         *  이후 어댑터에서 처리 하여 스피너에 모임 표시
         */
        // 서버에서 해당아이디의 신청된 모임 데이터를 받아오기 위한 AsyncTask
        Users_Applied_List get_data_task = new Users_Applied_List();
        get_data_task.execute(user_name);  // 아이디 전송


        // 스피너 객체 생성
        spinner1 = (Spinner)findViewById(R.id.spinner1);
        // 데이터 세트 객체 선언
        Applied_Moim_List_Spinner_Data_SET = new ArrayList<>();
        // 스피너 어댑터 객체 생성
        adapterSpinner1 = new AdapterSpinner1(this, Applied_Moim_List_Spinner_Data_SET);  // 입력 데이터 (화면제어권자, 데이터 어레이리스트)
        // Adapter 적용
        spinner1.setAdapter(adapterSpinner1);


        //스피너 아이템 클릭시에 발생되는 클릭 리스너
        spinner1.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {   // 아이템이 선택되면 나오는 리스너
                Toast.makeText(getApplicationContext(),adapterSpinner1.get_Selected_moim_Name(position)+" 선택함." ,Toast.LENGTH_SHORT).show();

                if(spinner_position != position){   // 내가 보는 모임의 포지션이 이동할 포지션이랑 다른 경우
                    // 초대 사람 수 초기화
                    people_number = 0;  // 0 으로 초기화
                    num_count_text.setText(String.valueOf(people_number));  // 멤버 화면의 숫자 변경

                    // 참여자들 초기화
                    Participants = "선택된 사용자 없음";
                }
                spinner_position = position;    // 포지션 저장 = 스피너가 선택 될때 마다
                Log.e("스피너 포지션 = ", String.valueOf(spinner_position));


//                people_number = 0;  // 0 으로 초기화
//                num_count_text.setText(String.valueOf(people_number));  // 멤버 화면의 숫자 변경


                if(position == 0){
                    Member_List_Dataset.clear();
                    Member_List_Adapter.notifyDataSetChanged();

                    // 데이터 화면에서 가져왔음. => 모임 번호로 해당 모임의 신청자 리스트를 가져온다
                    // 스레드로 모임번호 넘기고 데이터 받아오고
                    // 리사이클러뷰에 뿌린다.
                    Moim_NUMBER = adapterSpinner1.get_Item_num(position);    // 모임번호    - 가장 처음 시작 스피너를 시작 할때 이부분을 거친다. = 모임 번호가 저장될것이다.

                    // 각 모임 신청자 리스트 가져오기
                    Moim_Users_List get_data_task2 = new Moim_Users_List();
                    get_data_task2.execute(Moim_NUMBER);  // 모임번호 전송

                    // 방만들기에 필요한 데이터
                    Room_Moim_Name = adapterSpinner1.get_Selected_moim_Name(position); // 1. 모임명
                    Log.e("선택한 리스트의 모임명 = ",Room_Moim_Name);
                    Moim_img_File_Name = adapterSpinner1.get_Selected_moim_IMG_Name(position); // 4. 모임 이미지 이름
                    Log.e("선택한 리스트의 모임이미지이름 = ",Moim_img_File_Name);
                }
                else{
                    Member_List_Dataset.clear();                // 초기화
                    Member_List_Adapter.notifyDataSetChanged(); // 알림

                    Moim_NUMBER = adapterSpinner1.get_Item_num(position);    // 모임번호

                    // 각 모임 신청자 리스트 가져오기
                    Moim_Users_List get_data_task2 = new Moim_Users_List();
                    get_data_task2.execute(Moim_NUMBER);  // 모임번호 전송


                    // 방만들기에 필요한 데이터
                    Room_Moim_Name = adapterSpinner1.get_Selected_moim_Name(position); // 1. 모임명
                    Log.e("선택한 리스트의 모임명 = ",Room_Moim_Name);
                    Moim_img_File_Name = adapterSpinner1.get_Selected_moim_IMG_Name(position); // 4. 모임 이미지 이름
                    Log.e("선택한 리스트의 모임이미지이름 = ",Moim_img_File_Name);
                }
            }
            @Override
            public void onNothingSelected(AdapterView<?> parent) {  // 선택 전 클릭 - 스피너 리스트만 나올 때
                Toast.makeText(getApplicationContext(),"onNothingSelected 들어옴" ,Toast.LENGTH_SHORT).show();
                Log.e("스피너의 ","onNothingSelected 들어옴 ");
                Log.e("spinner_Click_Num = ", String.valueOf(spinner_Click_Num));

//                if(spinner_Click_Num == 1){ // spinner_Click_Num 가 1 이라면 = 리스트 중 선택이 되어있다는 의미
//                    /**
//                     *  선택 된거 초기화 해야함 = 예외 처리
//                     다이얼로그 띄워서 해제 하도록 하자.
//                     */
//                    spinner_click_alertshow();  // 스피너 클릭시 경고창 띄우기
//                }
            }// onNothingSelected 끝
        });// 스피너 아이템 클릭 리스너 끝




    /**
     * 스피너에 선택되어 있는 모임에서 신청자의 리스트를 가져와야 한다.
     * 스피너 설정이 우선
     * 그다음 신청자 리스트
     * 스피너에 선택된 해당 모임의 데이터 정보를 가지고 신청자 리스트를 표시해야 한다.
     */
        // 리사이클 러뷰 객체 선언
        Member_List_RecyclerView = (RecyclerView) findViewById(R.id.member_list_recycler_view);
        // 각 Item 들이 RecyclerView 의 전체 크기를 변경하지 않는 다면
        // setHasFixedSize() 함수를 사용해서 성능을 개선할 수 있습니다.
        // 변경될 가능성이 있다면 false 로 , 없다면 true를 설정해주세요.
        Member_List_RecyclerView.setHasFixedSize(true);
        // 리니어레이아웃 매니저 객체 선언
        Member_List_LayoutManager = new LinearLayoutManager(this);
        Member_List_RecyclerView.setLayoutManager(Member_List_LayoutManager);
        // 데이터세트, 어댑터 세팅
        Member_List_Dataset = new ArrayList<>();                          // 어레이리스트 객체인 데이터세트 객체 선언
        Member_List_Adapter = new MemberList_Adapter(Member_List_Dataset,this);     // 어댑터 객체 선언하며 데이터세트를 어댑터에 연결
        Member_List_RecyclerView.setAdapter(Member_List_Adapter);     // 리사이클러뷰에 어댑터 설정


    }// onCreate 끝



    // 스피너 경고창 띄우기
    void spinner_click_alertshow() {
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setTitle("모임을 변경 하시겠습니까?");
        builder.setMessage("초대한 멤버 목록을 초기화 합니다.");

        builder.setPositiveButton("취소",
                new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int which) {
                        // 취소 버튼의 경우 아무일도 일어나지 않는다
                    }
                });
        builder.setNeutralButton("확인",
                new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int which) {
                        // 사람수 초기화
                        people_number = 0;  // 0 으로 초기화
                        num_count_text.setText(String.valueOf(people_number));  // 멤버 화면의 숫자 변경

                        // 리스트 초기화 하여 다시 가져온다
                        Member_List_Dataset.clear();                // 초기화
                        Member_List_Adapter.notifyDataSetChanged(); // 리스트 변경 알림

                        // Moim_NUMBER = 모임번호
                        Log.e("확인 버튼 눌렀을때 모임번호 = ",Moim_NUMBER);

                        // 각 모임 신청자 리스트 가져오기
                        Moim_Users_List get_data_task2 = new Moim_Users_List();
                        get_data_task2.execute(Moim_NUMBER);  // 모임번호 전송


                        finish();
                    }
                });
        builder.show();
    }



    // 아이템 클릭 시 어댑터에서 화면으로 넘어오는 콜백 함수 = OnitemClick 인터페이스 사용
    // 인터페이스 onClick으로 실행됨.
    @Override
    public void onClick(int value, String users){   // value 에서는 +1,-1 숫자 값이 넘어오고, users 에서는 참여할 사용자 데이터가 넘어옴
        // value this data you receive when increment() / decrement() called
        people_number = people_number + value;
        num_count_text.setText(String.valueOf(people_number));  // 멤버 화면의 숫자 변경

        if(people_number > 0){ // people_number 값이 0 보다 크면
            // 1 명 이상 초대 의미
            spinner_Click_Num = 1;  // 1명 이상의 경우, 스피너 클릭 시 초기화 할 필요 있음 !!
        }else {
            // 0 명 초대 의미
            spinner_Click_Num = 0;  // 0 명 초대의 경우, 스피너 클릭 시 초기화 할 필요가 없음
        }

        // 방 참여자들
        Participants = users;
        if(Participants.equals(receive0+",")){
            Participants = "선택된 사용자 없음"; // 초기화 시킴
        }
    }






    /**
     * - 사용자의 신청한 모임 리스트 가져오기 - 스피너에 할당
     * 사용자 아이디를 전송하여 신청한 모임의 데이터들을 가져옴
     * 1. 모임번호  2. 모임명  3. 모임이미지-텍스트
     * 이 데이터들을 가지고 스피너 적용
     */
    private class Users_Applied_List extends AsyncTask<String, Void, String> {
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
                Log.e("", "아이디 가 비어있습니다.");
            }
            else if(error_sql.equals("SQL문")){  // result에서 잘라낸 데이터가 'SQL문' 이라면 에러가 발생햇다는 의미
                Log.e("SQL문 에러", ""+result);
            }
            else if(error_sql.equals("신청된 모임 없음")){  // result에서 잘라낸 데이터가 '신청된 모임 없음' 이라면 에러가 발생햇다는 의미
                Log.e("신청된 모임 없음", ""+result);
            }
            else {  // 데이터 제대로 받아왔다는 의미
                mJsonString = result;   // json 데이터 전체를 mJsonString 에 삽입
                showResult();   // showResult() 메소드 실행
            }
        }

        @Override
        protected String doInBackground(String... params) {
            String ID  = params[0]; // 아이디 전달
            Log.e("Users_Appli 아이디 =",ID);

            String serverURL = "http://49.247.208.191/AndroidHive/Chat_Members_List/Users_Applied_List_data.php";   // AndroidHive/Chat_Members_List/Users_Applied_List_data.php 의 파일로 이동
            String postParameters = "ID=" + ID ;   // 아이디 전달

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
                Log.d("word_List_Adapter", "InsertData: Error ", e);
                errorString = e.toString();
                return null;
            }
        }
    }// Users_Applied_List 끝


    private void showResult(){
        try {
            JSONObject jsonObject = new JSONObject(mJsonString);
            JSONArray jsonArray = jsonObject.getJSONArray(TAG_JSON);

            for(int i=0;i<jsonArray.length();i++){  // 불러오는 객체가 1개 이상이면 더 늘어남. 기본 = 1
                String 제이슨길이 = String.valueOf(jsonArray.length());  // 제이슨 길이 int 를 스트링 으로
                Log.e("제이슨어레이 길이 = ",제이슨길이);
                JSONObject item = jsonArray.getJSONObject(i);
                // 데이터 파싱
                String M_no = item.getString(TAG_Moim_NO);                  // 1. 모임번호
                String M_name = item.getString(TAG_Moim_Name);              // 2. 신청 모임명
                String M_default_imgURI = item.getString(TAG_DEFAULT_imgURI); // 3. 대표이미지 이름
                Log.e("--------------------- ","");
                Log.e("모임번호 = ",M_no);
                Log.e("신청 모임명 = ",M_name);
                Log.e("대표이미지 이름 = ",M_default_imgURI);

                Applied_Moim_List_Spinner_Data_SET.add(new Applied_Moim_List_Spinner_Data(M_no,M_name,M_default_imgURI));  // 모임번호, 모임명, 모임대표이미지 이름

            }// 반복문 종료
            adapterSpinner1.notifyDataSetChanged();    // 데이터 추가가 완료되었으면 notifyDataSetChanged() 메서드를 호출해 데이터 변경 체크를 실행합니다.
        } catch (JSONException e) {
            Log.d(TAG, "showResult : ", e);
        }
    }// showResult 끝



    /**
     * - 각 모임의 신청자 리스트 가져오기 - 리사이클러뷰에 할당
     * 모임번호 를 전송하여 신청한 모임의 신청자 리스트 들을 가져옴
     * 1. 신청자 이름  2. 신청자 아이디  3. 신청자 프로필 이미지 이름
     * 이 데이터들을 가지고 리사이클러뷰에 적용
     */
    private class Moim_Users_List extends AsyncTask<String, Void, String> {
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
                Log.e("", "아이디 가 비어있습니다.");
            }
            else if(error_sql.equals("SQL문")){  // result에서 잘라낸 데이터가 'SQL문' 이라면 에러가 발생햇다는 의미
                Log.e("SQL문 에러", ""+result);
            }
            else if(error_sql.equals("신청된 모임 없음")){  // result에서 잘라낸 데이터가 '신청된 모임 없음' 이라면 에러가 발생햇다는 의미
                Log.e("신청된 모임 없음", ""+result);
            }
            else {  // 데이터 제대로 받아왔다는 의미
                mJsonString2 = result;   // json 데이터 전체를 mJsonString 에 삽입
                showResult2();   // showResult() 메소드 실행
            }
        }

        @Override
        protected String doInBackground(String... params) {
            String Moim_Number  = params[0]; // 모임번호 전달
            Log.e("전달할 모임번호 =",Moim_Number);

            String serverURL = "http://49.247.208.191/AndroidHive/Chat_Members_List/Moim_Applied_Users_List.php";   // AndroidHive/Chat_Members_List/Moim_Applied_Users_List.php 의 파일로 이동
            String postParameters = "Moim_Number=" + Moim_Number ;   // 모임번호 전달

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
                Log.d("word_List_Adapter", "InsertData: Error ", e);
                errorString = e.toString();
                return null;
            }
        }
    }// Moim_Users_List 끝


    private void showResult2(){
        try {
            JSONObject jsonObject = new JSONObject(mJsonString2);
            JSONArray jsonArray = jsonObject.getJSONArray(TAG_JSON_User_List);

            for(int i=0;i<jsonArray.length();i++){  // 불러오는 객체가 1개 이상이면 더 늘어남. 기본 = 1
                String 제이슨길이 = String.valueOf(jsonArray.length());  // 제이슨 길이 int 를 스트링 으로
                Log.e("제이슨어레이 길이 = ",제이슨길이);
                JSONObject item = jsonArray.getJSONObject(i);
                // 데이터 파싱
                String User_name = item.getString(TAG_Moim_user_name);  // 1. 신청자 이름
                String User_id = item.getString(TAG_Moim_user_ID);      // 2. 신청자 아이디
//                String M_default_imgURI = item.getString(TAG_DEFAULT_imgURI); // 3. 대표이미지 이름
                Log.e("--------------------- ","");
                Log.e("1. 신청자 이름 = ",User_name);
                Log.e("2. 신청자 아이디 = ",User_id);

                if(User_id.equals(receive0)){   // 만약 받아온 신청자 아이디가 현재 접속자 아이디라면
                    // add 문을 실행하지 않는다.
                } else{
                    Member_List_Dataset.add(new Applied_User_list_Data(User_name , User_id, "0"));  // 이름, 아이디, 체크박스 체크상태,  프로필사진이름
                }
            }// 반복문 종료
            Member_List_Adapter.notifyDataSetChanged();    // 데이터 추가가 완료되었으면 notifyDataSetChanged() 메서드를 호출해 데이터 변경 체크를 실행합니다.
        } catch (JSONException e) {
            Log.d(TAG, "showResult : ", e);
        }
    }// showResult 끝



    /**
     * - 방생성 하는 AsyncTask
     * 방생성을 위한 데이터를 전달한다.
     */
    private class Create_Room_Task extends AsyncTask<String, Void, String> {
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
                Log.e("", "데이터 제대로 전달이 안됨.");
            }
            else if(error_sql.equals("SQL문")){  // result에서 잘라낸 데이터가 'SQL문' 이라면 에러가 발생햇다는 의미
                Log.e("SQL문 에러 = ",result);
            }
            else {  // 데이터 제대로 입력되었다는 의미
                Log.e("방생성 결과 = ",result);

//                mJsonString3 = result;   // json 데이터 전체를 mJsonString 에 삽입


            }
        }

        @Override
        protected String doInBackground(String... params) {

            String Room_Key  = params[0];         // 채팅방 키 전달
            String Task_Room_Moim_Name  = params[1];         // 해당 모임명 전달
            String Task_Participants  = params[2];           // 참여자들 전달
            String Task_Number_of_participants  = params[3]; // 참여 인원 수 전달
            String Task_Moim_img_File_Name  = params[4];     // 해당 모임의 이미지 Uri

            Log.e("해당 모임명 =",Task_Room_Moim_Name);
            Log.e("참여자들 =",Task_Participants);
            Log.e("참여 인원 수 =",Task_Number_of_participants);
            Log.e("모임의 이미지 Uri =",Task_Moim_img_File_Name);

            String serverURL = "http://49.247.208.191/AndroidHive/Room/Create_Room.php";   // AndroidHive/Chat_Members_List/Users_Applied_List_data.php 의 파일로 이동
            String postParameters = "Room_Key=" + Room_Key + "&Task_Room_Moim_Name=" + Task_Room_Moim_Name + "&Task_Participants=" + Task_Participants +
                    "&Task_Number_of_participants=" + Task_Number_of_participants+"&Task_Moim_img_File_Name=" + Task_Moim_img_File_Name;   // 데이터 전달

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
    }// Create_Room_Task 끝


//    private void showResult3(){
//        try {
//            JSONObject jsonObject = new JSONObject(mJsonString3);  // json 데이터 전체
//            JSONArray jsonArray = jsonObject.getJSONArray(TAG_JSON_room_num); // 키에 따라서 json object 분할
//
//            for(int i=0;i<jsonArray.length();i++){  // 불러오는 객체가 1개 이상이면 더 늘어남. 기본 = 1
//                String 제이슨길이 = String.valueOf(jsonArray.length());  // 제이슨 길이 int 를 스트링 으로
//                Log.e("제이슨어레이 길이 = ",제이슨길이);
//                JSONObject item = jsonArray.getJSONObject(i);
//                // 데이터 파싱
//                String Room_Num = item.getString(TAG_Moim_Room_Num);  // 1. 방 번호
//                Log.e("--------------------- ","");
//                Log.e("// 1. 방 번호  = ",Room_Num);
//
//                /**
//                 *  // sqlite 방번호 저장
//                 */
////                dbHelper.RoomList_info_insert(Room_Num);   // sqlite 방번호 저장
//
//                // 채팅 화면으로 이동하면서 서버 방번호도 같이 넘김
//                Intent intent = new Intent(this, ServiceChat_Activity.class);    // 넘어갈 ServiceChat_Activity 화면 = 모임확인화면
//
//                // intent 객체에 데이터를 실어서 보내기
//                // 리스트뷰 클릭시 인텐트 (Intent) 생성하고 position 값을 이용하여 인텐트로 넘길값들을 넘긴다.
//                intent.putExtra("Room_NUMBER", Room_Num); // 서버 방 번호
//
//                this.startActivity(intent);    // 현재화면 제어권자에서 startActivity 시켜라 라는 말이다.
//
//            }// 반복문 종료
//        } catch (JSONException e) {
//            Log.d(TAG, "showResult : ", e);
//        }
//    }// showResult 끝



}// 코드 끝
