package com.example.user.androidhive;

import android.content.ComponentName;
import android.content.Intent;
import android.content.ServiceConnection;
import android.os.AsyncTask;
import android.os.Bundle;
import android.os.IBinder;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.View;
import android.widget.Button;

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

import static com.example.user.androidhive.MainActivity.receive0;
import static com.example.user.androidhive.ServiceChat_Activity.isService;

public class ChatList_Activity extends AppCompatActivity {

    // 화면 태그
    private static String TAG = "ChatList_Activity";

    // 받아온 데이터 변수 - 채팅방 리스트 데이터 가져 오기. = 채팅방 리스트는 본인 클라이언트 저장소 에서 가져온다.
    String mJsonString;     // JSON 전체 String 값
    private static final String TAG_JSON = "User_RoomList_Data";   // JSON 데이터 key =  User_RoomList_Data
    // JSON 상세 데이터 키
    private static final String TAG_Room_Num = "Room_KEY";                  // 1. 서버 방 키
    private static final String TAG_Room_Moim_Name = "Room_Moim_Name";      // 2. 채팅방 모임명
    private static final String TAG_Users = "Users";                        // 3. 채팅방 참여자
    private static final String TAG_Number_of_people = "Number_of_people";  // 4. 참여자 수
    private static final String TAG_Moim_img_Name = "Moim_img_Name";        // 5. 모임 이미지 파일명


    Button Members_Button;    // 멤버 버튼
    Button Room_Edit_Button;  // 편집 버튼 - 방 나가기 (아직 사용중 아님)
    Button Service_exit_Button;  // 내 채팅 = 서비스 종료 버튼

    boolean isService2 = isService; // 서비스 변수 (사용중/비사용중 서비스 작동중인지 확인하기 위해 사용)

    // 채팅방 리스트 리사이클러뷰 관련
    RecyclerView RoomList_RecyclerView;                 // 방리스트 RoomList 리사이클러뷰 객체
    RecyclerView.LayoutManager RoomList_LayoutManager;  // 레이아웃매니저 객체
    ArrayList<Room_info_Data> RoomList_Dataset;         // 데이터 세트를 담고 있는 어레이리스트 객체
    Room_List_Adapter roomList_adapter;                 // 어댑터 객체 생성








// onCreate 시작 -------------------------------------------------------------------------------------------------------------------------------------------
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_chat_list_);

// 이해가 필요한 코드 -------------------------------------------------------------------------------------------------------------------------------------------

        final ServiceConnection conn = new ServiceConnection() {  // 서비스 연결 객체 생성
            public void onServiceConnected(ComponentName name, IBinder service) {   // 입력값 1. ComponentName, 2. IBinder
                /** 서비스와 연결되었을 때 호출되는 메소드 */
            }
            public void onServiceDisconnected(ComponentName name) {
                // 서비스와 연결이 끊겼을 때 호출되는 메서드
                isService2 = false; // 서비스 연결 변경 = false = 중지상태
            }
        };

        // 내 채팅 = 서비스 종료 버튼 클릭 이벤트
        Service_exit_Button = (Button) findViewById(R.id.Service_exit_Button);  // 서비스 종료 버튼 - 현재 '내 채팅'을 서비스 종료 버튼으로 함.
        Service_exit_Button.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View arg0) {

                Intent intent = new Intent(getApplicationContext(), Chat_test_Activity.class);    // 넘어갈 Members_Activity 화면
                startActivity(intent);    // 현재화면 제어권자에서 startActivity 시켜라 라는 말이다.


//                unbindService(conn); // 서비스 종료
            }
        });// 내 채팅 = 서비스 종료 버튼 클릭 리스너 끝

// ---------------------------------------------------------------------------------------------------------------------------------------------------------



        // 멤버 버튼 클릭 이벤트
        Members_Button = (Button) findViewById(R.id.members_button);  // 멤버 버튼
        Members_Button.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View arg0) {
                // 화면이동
                Intent intent = new Intent(getApplicationContext(), Members_Activity.class);    // 넘어갈 Members_Activity 화면
                startActivity(intent);    // 현재화면 제어권자에서 startActivity 시켜라 라는 말이다.
            }
        });// 멤버 버튼 클릭 리스너 끝


        /**
         * 리사이클 러뷰 관련
         */
        // 리사이클 러뷰 객체 선언
        RoomList_RecyclerView = (RecyclerView) findViewById(R.id.roomlist_recycler_view);
        RoomList_RecyclerView.setHasFixedSize(true);
        // 리니어레이아웃 매니저 객체 선언
        RoomList_LayoutManager = new LinearLayoutManager(this);
        RoomList_RecyclerView.setLayoutManager(RoomList_LayoutManager); // 리사이클러뷰 레이아웃매니저 설정
        // 데이터 세드 설정
        RoomList_Dataset = new ArrayList<Room_info_Data>();                     // 어레이리스트 객체인 데이터세트 객체 선언
        roomList_adapter = new Room_List_Adapter(RoomList_Dataset);  // 어댑터 객체 선언하며 데이터세트를 어댑터에 연결
        RoomList_RecyclerView.setAdapter(roomList_adapter);           // 리사이클러뷰에 어댑터 설정


    }
// onCreate 끝 -------------------------------------------------------------------------------------------------------------------------------------------




    /**
     * onStart 에서 채팅방 리스트 데이터를 받아온다.
     */
    @Override
    protected void onStart() {
        super.onStart();
        RoomList_Dataset.clear();
        roomList_adapter.notifyDataSetChanged();

        // 각 모임 신청자 리스트 가져오기
        get_Room_List get_room_list_task = new get_Room_List(); // 클래스 객체 = new 클래스 => 객체 생성
        get_room_list_task.execute(receive0);  // 접속자 아이디 전송

    }




    /**
     * - 사용자의 채팅방 리스트 가져오기
     * 사용자 아이디를 전송하여 채팅방 리스트  가져옴
     * 1. 방 번호  2. 모임명  3. 채팅방 참여자들  4. 채팅참여자 수  5. 모임 기본이미지 파일 이름
     * 이 데이터들로 채팅방 리스트 구성
     */
    private class get_Room_List extends AsyncTask<String, Void, String> {
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

            if (result.equals("아이디가 비어있습니다.")){  // 값을 받아 오지 못했다면
                Log.e("", "아이디 가 비어있습니다.");
            }
            else if(error_sql.equals("SQL문")){  // result에서 잘라낸 데이터가 'SQL문' 이라면 에러가 발생햇다는 의미
                Log.e("SQL문 에러", ""+result);
            }
            else if(error_sql.equals("검색된 채팅방 없음")){  // result에서 잘라낸 데이터가 '검색된 채팅방 없음' 이라면 참여 하고 있는 채팅방 없음을 의미
                Log.e("검색된 채팅방 없음", ""+result);
            }
            else {  // 데이터 제대로 받아왔다는 의미
                mJsonString = result;   // json 데이터 전체를 mJsonString 에 삽입
                showResult();   // showResult() 메소드 실행
            }
        }

        @Override
        protected String doInBackground(String... params) {
            String ID  = params[0]; // 아이디 전달
            Log.e("채팅방 리스트 검색을 위한 아이디 =",ID);

            String serverURL = "http://49.247.208.191/AndroidHive/Room/ChatList.php";   // AndroidHive/Room/ChatList.php 의 파일로 이동
            String postParameters = "ID=" + ID ;   // 아이디 전달

            try {
                /** HTTP 통신을 위한 코드 */
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

    /**
     * PHP 에서 가져온 JSON 형태의 데이터를
     * 데이터클래스에 넣을 수 있어야 한다.
     * - 채팅방 리스트 표현
     */
    private void showResult(){
        try {
            JSONObject jsonObject = new JSONObject(mJsonString);
            JSONArray jsonArray = jsonObject.getJSONArray(TAG_JSON);

            for(int i=0;i<jsonArray.length();i++){  // 불러오는 객체가 1개 이상이면 더 늘어남. 기본 = 1
                String 제이슨길이 = String.valueOf(jsonArray.length());  // 제이슨 길이 int 를 스트링 으로
                Log.e("제이슨어레이 길이 = ",제이슨길이);
                JSONObject item = jsonArray.getJSONObject(i);

                // 데이터 파싱
                String Room_KEY = item.getString(TAG_Room_Num);                 // 1. 서버 방 번호
                String Room_Moim_Name = item.getString(TAG_Room_Moim_Name);     // 2. 채팅방 모임명
                String Users = item.getString(TAG_Users);                       // 3. 채팅방 참여자
                String Number_of_people = item.getString(TAG_Number_of_people); // 4. 참여자 수
                String Moim_img_Name = item.getString(TAG_Moim_img_Name);       // 5. 모임 이미지 파일명
                Log.e("---------------------","------------------------------------------");
                Log.e("// 1. 서버 방 번호 = ",Room_KEY);
                Log.e("// 2. 채팅방 모임명 = ",Room_Moim_Name);
                Log.e("// 3. 채팅방 참여자 = ",Users);
                Log.e("// 4. 참여자 수 = ",Number_of_people);
                Log.e("// 5. 모임 이미지 파일명 = ",Moim_img_Name);


                // 방번호, 모임명, 참여자들 , 참여자수 , 이미지 파일
                RoomList_Dataset.add(new Room_info_Data(Room_KEY, Room_Moim_Name, Users , Number_of_people , Moim_img_Name ) );

            }// 반복문 종료
            roomList_adapter.notifyDataSetChanged();    // 데이터 추가가 완료되었으면 notifyDataSetChanged() 메서드를 호출해 데이터 변경 체크를 실행합니다.
        } catch (JSONException e) {
            Log.d(TAG, "showResult : ", e);
        }

    }// showResult 끝




}// 코드 끝
