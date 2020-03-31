package com.example.user.androidhive;

import android.content.BroadcastReceiver;
import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.ServiceConnection;
import android.os.AsyncTask;
import android.os.Bundle;
import android.os.Handler;
import android.os.IBinder;
import android.os.Message;
import android.os.Messenger;
import android.os.RemoteException;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.text.TextUtils;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.IOException;
import java.net.InetSocketAddress;
import java.nio.ByteBuffer;
import java.nio.channels.SocketChannel;
import java.nio.charset.Charset;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.TimeZone;

import static com.example.user.androidhive.MainActivity.receive0;

public class ServiceChat_Activity extends AppCompatActivity {



    // 객체 생성
    Button btnSend;        // 전송 버튼
    EditText editMessage;  // 메세지 작성 EditText

    // 현재 화면 제어권자 확인 Context
    Context context;
    // 해당 사용자의 아이디
    String User_NAME_person;

    // 신청된 모임리스트 의 리사이클러뷰 관련
    RecyclerView ChatHistory_RecyclerView;                 // 리사이클러뷰 객체
    RecyclerView.LayoutManager ChatHistory_LayoutManager;  // 레이아웃매니저 객체
    ArrayList<Object> ChatHistory_Dataset;                 // 데이터 세트를 담고 있는 어레이리스트 객체
    CHAT_Adapter chat_adapter;                             // 어댑터 객체 생성

    // 채팅 내역을 UI 화면에 나타내는 핸들러
    Handler msghandler;

    // 방 키
    String Room_Key;


    // SQLite
    SQLite_Create_Room SQLite_save_chat;

    // ------------------------------------------------------------------------------------------------------------------------------------------------------------------
    // 시간 가져오기
    TimeZone time;  // Asia/Seoul = 지역 설정 을 통해 한국 시간의 가져오기 위한 TimeZone 객체
    Date date;      // Date 객체 = 시간 포맷을 나타내기위해서 필요함.
    DateFormat df = new SimpleDateFormat("yyyy-MM-dd,HH:mm:ss"); // 년-월-일 시:분:초
    DateFormat df2 = new SimpleDateFormat("HH:mm");              // 시:분

    String timeTEXT;    // EX) 오후 or 오전 04:15

    // 현재 시간 구하는 메소드
    private String Korea_getTime() {
        date = new Date();
        time = TimeZone.getTimeZone("Asia/Seoul");  // 한국 지역 설정
        df.setTimeZone(time);                       // 한국 시간 설정
        return df.format(date); // 전체시간 데이터 가져옴
    }

    // 현재 시간의 시간과 분을 구하는 메소드
    private String Korea_getTime_HM() {
        date = new Date();
        time = TimeZone.getTimeZone("Asia/Seoul"); // 한국 지역 설정
        df2.setTimeZone(time);                     // 한국 시간 설정
        return df2.format(date); // 시-분 데이터 가져옴
    }
// ------------------------------------------------------------------------------------------------------------------------------------------------------------------






// 서비스 관련 코드 ----------------------------------------------------------------------------------------------------------------------------------------------------
    // 서비스 중인지 확인을 위한 변수
    static boolean isService = false;
    // 메신저 객체
    private Messenger mServiceMessenger = null; // 액티비티로 부터 서비스로 메세지를 보내는데 사용됨. = 서비스에서 그 메세지를 받고 서버에 전송한다.


    ServiceConnection conn = new ServiceConnection() {  // 서비스 연결 객체 생성
        public void onServiceConnected(ComponentName name, IBinder service) {   // 입력값 1. ComponentName, 2. IBinder
            /** 서비스와 연결되었을 때 호출되는 메소드 */
            // 메신저 객체 생성 - 이 메신저를 통해 서비스로 메세지를 전송할 수 있다. =>
            mServiceMessenger = new Messenger(service); // 메신저 객체 생성할때 IBinder service 입력하여 준다.

            isService = true;   // 서비스 연결 변경 = true = 동작중 상태
//            Toast.makeText(getApplicationContext(), "Service 시작", Toast.LENGTH_LONG).show();
        }
        public void onServiceDisconnected(ComponentName name) {
            // 서비스와 연결이 끊겼을 때 호출되는 메서드
            isService = false; // 서비스 연결 변경 = false = 중지상태
        }
    };

// ------------------------------------------------------------------------------------------------------------------------------------------------------------------





    // 액티비티 종료되면서 서비스도 종료된다.
    @Override
    public void onDestroy() {
        super.onDestroy();
//        unbindService(conn); // 서비스 종료

        try {
            socketChannel.close(); // 소켓 채널 닫기
        } catch (Exception e) {
            e.printStackTrace();
        }


    }


/** 브로드 캐스트 리시버 관련 코드 ----------------------------------------------------------------------------------------------------------------------------- */
    @Override
    public void onResume() {
        super.onResume();
        registerReceiver(mReceiver, mIntentFilter); // 화면 시작 될때마다 리시버 시작 - 시작 + 재시작
    }


    @Override
    protected void onPause() {
        super.onPause();
        unregisterReceiver(mReceiver);  // 리시버 해제
    }


    /**
     * BroadcastReceiver 에서 처리방식 행동을 정의하는 String 변수
     * 만약에 BroadcastReceiver 에서 데이터를 받았는데
     *
     - 받은 데이터를 A 형태로 처리하고 싶으면
     String A_Processing = "A 형태처리";
     if(intent.getAction().equals(A_Processing)) { A 방식 처리 }
     *
     - 받은 데이터를 B 형태로 처리하고 싶으면
     String B_Processing = "B 형태처리";
     if(intent.getAction().equals(B_Processing)) { B 방식 처리 }
     *
     * 이처럼 Stirng 변수 하나를 만들고 고유의 값을 삽입하여
     * 데이터 처리방식의 변화를 준다.
     */
    public static final String mBroadcastStringAction = "Message_Processing"; // 메세지 처리방식 String 변수
//    public static final String mBroadcastImageAction = "Image_Processing";  // 이미지 처리방식 String 변수

    // 인텐트 필터
    private IntentFilter mIntentFilter; // mIntentFilter.addAction 통해 액션방식을 삽입함으로써 처리방식을 다르게 함

    /**
     * BroadcastReceiver 객체 생성하고
     * onReceive 메소드 내부에
     * Service 로 부터 메세지를 받고
     * 받은 메세지를 핸들러로 전송하여 화면에 메세지를 표시한다.
     */
    private BroadcastReceiver mReceiver = new BroadcastReceiver() {
        @Override
        public void onReceive(Context context, Intent intent) { // 입력데이터 = 화면제어권자, 인텐트

            if (intent.getAction().equals(mBroadcastStringAction)) {    // 인텐트 액션이 = mBroadcastStringAction = String 데이터가져오기 경우
                String massage_Text = intent.getStringExtra("Chat_TEXT"); // 채팅 데이터를 받음
                Log.e("나에게 온 채팅 메세지 = ",massage_Text);

//                String[] get_room_num_arr = massage_Text.split(" =>방번호ps:");    //  =>방번호ps: 로 구분하기.. 하드코딩..
//
////                get_room_num_arr[1] = 여기에 방번호 있음
//                Log.e("방번호ps = ",get_room_num_arr[1]);
                // 내가 가진 방번호
//                String my_room_list =  dbHelper.RoomList_info_getResult();


                // 핸들러에게 전달할 메시지 객체
                Message hdmsg = msghandler.obtainMessage();
                hdmsg.what = 1111;        // 식별자 - 핸들러 호출 장소 번호 = 1111 -> 핸들러의 호출 장소는 코드의 여러군데가 있을수 있으므로 코드장소마다 번호 부여하는것.
                hdmsg.obj = massage_Text; // 메시지의 본문
                // 핸들러에게 메시지 전달 (화면변경 요청)
                msghandler.sendMessage(hdmsg);

                // 브로드 캐스트 리시버로 들어와서 데이터를 표현 하는데에는 성공
            }
        }
    };// BroadcastReceiver 끝
/**  브로드 캐스트 리시버 관련 코드  끝 ----------------------------------------------------------------------------------------------------------------------------- */









// 새로작성한 코드
    Handler handler;                // UI를 적용할 핸들러
    String data;                    // 메세지 데이터
    SocketChannel socketChannel;    // 소켓 채널 객체
//    private static final String HOST = "52.78.93.199";  // 49.247.208.191
    private static final String HOST = "49.247.208.191";

    private static final int PORT = 5001;   // 네티 서버 포트














// onCreate 시작 ------------------------------------------------------------------------------------------------------------------------------------------------------------
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_service_chat_);

        // SQLite 객체 생성 = 이게 있어야지 저장 가능
        SQLite_save_chat = new SQLite_Create_Room(getApplicationContext(), "CHAT_ROOM.db", null, 1); // "/mnt/sdcard/" +

        // 현재 들어간 채팅방의 키 받기
        Intent get_Room_number_intent = getIntent();
        Room_Key = get_Room_number_intent.getStringExtra("Room_Key");
        Log.e("현재 방의 키 = ",Room_Key);


        // 현재 화면 context - 와이파이 상태 정보 체크 사용을 위해서
        context = this;
        // 채팅 입력하는 EditText
        editMessage = (EditText) findViewById(R.id.editMessage);
        // 현재 사용자 아이디 연결
        User_NAME_person = receive0;    // static User_NAME 가져오기 = 아이디 식별자
        // 전송 버튼
        btnSend = (Button) findViewById(R.id.btnSend);  // 메세지 전송버튼
//        btnSend.setOnClickListener(new View.OnClickListener() { // 메세지 전송 클릭 리스너
//            @Override
//            public void onClick(View arg0) {
//                //SendThread 시작
//                if (editMessage.getText().toString() != null) { // 입력 editMessage 가 null 아니면
//                    /**
//                     * 전송 버튼이 클릭 되면
//                     * 일단 Massage 클래스 내부의 핸들러로 Service 에 메세지를 전송해 줘야 한다.
//                     * Service 에서 메세지를 받아오면 서버로 보내는 Send_Massage 클래스 에서 메세지를 처리하여
//                     * 서버로 보낸다.
//                     */
//
//                    // 원래는 객체 형태로 담아서 보내야 정상. 지금은 일단 빠르게 만들려고 생각해서 그냥 하드코딩함.
//                    // 방키:::::아이디:::::채팅메세지
//                    String Total_MSG = Room_Key+":::::"+User_NAME_person+":::::"+editMessage.getText().toString() ;
//                    // EX) 이름 : 메세지 =>방번호ps:Room_number  핵심 =>방번호ps:
//                    Log.e("보낸 메세지 = ",Total_MSG);
//
//
////                    Message hdmsg = msghandler.obtainMessage();
////                    hdmsg.what = 1111;        // 식별자 - 핸들러 호출 장소 번호 = 1111 -> 핸들러의 호출 장소는 코드의 여러군데가 있을수 있으므로 코드장소마다 번호 부여하는것.
////                    hdmsg.obj = Total_MSG; // 메시지의 본문
////                    // 핸들러에게 메시지 전달 (화면변경 요청)
////                    msghandler.sendMessage(hdmsg);
//
//                    // 채팅 메세지를 data set 에 추가하고, 어댑터에 변경 알리면서 화면에 표현
//                    ChatHistory_Dataset.add(new My_Chat_Data(editMessage.getText().toString(), timeTEXT));// 메시지 , 채팅 시간
//                    chat_adapter.notifyDataSetChanged(); // 리스트뷰 변경 알림
//
//                    /**
//                     * 서비스를 이용하여 서버로 채팅데이터를 전송하는 메소드
//                     */
//                    // sendMessageToService 메소드 실행 = 메시지를 서비스로 전달
////                    sendMessageToService(Total_MSG);
//                    editMessage.setText(""); // edittext 초기화
//
//                }
//            }
//        });// //전송 버튼 클릭 이벤트 끝




        // 브로드 캐스트 리시버 데이터 받기 위한 인텐트필터 객체
        mIntentFilter = new IntentFilter();                 // 인텐트 필터 객체 선언
        mIntentFilter.addAction(mBroadcastStringAction);    // 인텐트 필터에 액션 삽입 = mBroadcastStringAction




        /**
         * 리사이클 러뷰 관련
         * 리사이클 러뷰는
         * myDataset.add(new Opened_Moim_Data(Name,Total_People));
         * 같은 add 함수와 같은 메소드에 있어야 한다 =  중요!!!!!!!!!!!
         * 다른 메소드에 있을 경우 생성이 되지 않는다.
         */
        // 리사이클 러뷰 객체 선언
        ChatHistory_RecyclerView = (RecyclerView) findViewById(R.id.ChatHistory_recycler_view);
        ChatHistory_RecyclerView.setHasFixedSize(true);
        // 리니어레이아웃 매니저 객체 선언
        ChatHistory_LayoutManager = new LinearLayoutManager(this);
        ChatHistory_RecyclerView.setLayoutManager(ChatHistory_LayoutManager); // 리사이클러뷰 레이아웃매니저 설정
        ChatHistory_Dataset = new ArrayList<>();                     // 어레이리스트 객체인 데이터세트 객체 선언
        chat_adapter = new CHAT_Adapter(ServiceChat_Activity.this);  // 어댑터 객체 선언하며 데이터세트를 어댑터에 연결
        ChatHistory_RecyclerView.setAdapter(chat_adapter);           // 리사이클러뷰에 어댑터 설정

        chat_adapter.setChatFeed(ChatHistory_Dataset);  // 현재화면의 데이터 세트를 어댑터의 데이터 세트로 복사


        /**
         * 일단 bindService 사용 안함으로 주석 처리한다.
          */
//        // startService() 메소드 대신 bindService() 메소드를 통해 시작되는 서비스를 서비스 바인딩 (Service Bind 혹은 Bound Service) 라 합니다.
//        Intent intent = new Intent(ServiceChat_Activity.this, MyService.class);  // MyService 인텐트 이동
//        bindService(intent, // intent 객체
//                conn, // 서비스와 연결에 대한 정의
//                Context.BIND_AUTO_CREATE);  // 서비스 시작

//
//        /**
//         * UI 변경을 위한 핸들러
//         */
//        // 핸들러 작성
//        msghandler = new Handler() {
//            @Override   // alt + insert 클릭후 Override Method -> handleMessage
//            public void handleMessage(Message msg) {
//                if (msg.what == 1111) { // 핸들러 식별자 = 1111 , 핸들러 호출 1111 번에 대한 처리를 진행한다.
//
//                    String msgTEST = msg.obj.toString();
//                    Log.e("온 메세지 = ", "" + msgTEST);
//                    String[] divide_msg = msgTEST.split(":::::"); // ::::: 나누기
//
//                    // 구분 짓는거 완료!!
//                    Log.e("받아온 방 키 = ", "" + divide_msg[0]); // 방 키
//                    Log.e("받아온 이름 = ", "" + divide_msg[1]);  // 이름
//                    Log.e("메세지 = ", "" + divide_msg[2]);      // 메세지
//                    Log.e("현재 사용자 = ", "" + User_NAME_person);
//
//                    String ALLtime = Korea_getTime().toString(); // 시-분
//                    Log.e("보낸 전체시간 = ", "" + ALLtime);
//                    String MStime = Korea_getTime_HM().toString(); // 전체시간
//                    Log.e("보낸 시-분 = ", "" + MStime);
//
//                    String[] HandM = MStime.split(":");
//                    int Hour = Integer.parseInt(HandM[0]);   // String 형태의 데이터 인 시간을 int 값으로
//                    int minute = Integer.parseInt(HandM[1]); // 분을 int 값으로
//
//                    if (Hour >= 12) { // 시간이 12 보다 같거나 크면 = 오후
//                        Hour = Hour - 12;
//                        timeTEXT = "오후 " + Hour + ":" + minute;
//                        Log.e("표시할 시간 = ", "" + timeTEXT);
//                    } else {   // 오전
//                        timeTEXT = "오전 " + Hour + ":" + minute;
//                        Log.e("표시할 시간 = ", "" + timeTEXT);
//                    }
//
//
//                    /**
//                     * 채팅 메세지 보내는 부분
//                     * 1. LineText_Data
//                     * 2. My_Chat_Data     == 내채팅
//                     * 3. ChatHistory_Data == 상태방 채팅
//                     */
//                    if (divide_msg[2].equals("접속")) { // 누군가 접속 했다 - 번호 2
//                        Log.e("접속 메세지 들어옴 = ", "");
////                        ChatHistory_Dataset.add(new LineText_Data(divide_msg[0] + " 님이 접속하셨습니다."));// LineText_Data - 입력값 1개 - 아이디
////                        chat_adapter.notifyDataSetChanged();   // 리스트뷰 변경 알림
//                    }
//                    else {   // 채팅 친다 .
//                        if(divide_msg[0].equals(Room_Key)){ // 받아온 키가 해당 방키와 일치하면 데이터 표시
//
//                            if (User_NAME_person.equals(divide_msg[1])) { // 채팅 친사람이 나라면
//                                Log.e("나의 채팅 들어옴 = ", "");
////                                // 오른쪽에 채팅이 보이게 하자
////                                ChatHistory_Dataset.add(new My_Chat_Data(divide_msg[2], timeTEXT));// 메시지 , 채팅 시간
////                                chat_adapter.notifyDataSetChanged(); // 리스트뷰 변경 알림
//                            }
//                            else {  // 채팅 친사람이 상대방이라면
//                                Log.e("상대방 채팅 들어옴", "");
//                                ChatHistory_Dataset.add(new ChatHistory_Data(divide_msg[1], divide_msg[2],timeTEXT));// Chat  History_Data - 입력값 2개 Id,메세지
//                                chat_adapter.notifyDataSetChanged(); // 리스트뷰 변경 알림
//                            }
//                        }
//
//                    }
//                }
//            }
//        };
//
//
//




        // 핸들러를 통해 서버와 연결
        handler = new Handler();
        new Thread(new Runnable() {
            @Override
            public void run() {
                try {
                    // 소켓채널 객체를 통해서 채팅 서버와 연결
                    socketChannel = SocketChannel.open();
                    socketChannel.configureBlocking(true);
                    socketChannel.connect(new InetSocketAddress(HOST, PORT)); // 호스트와 포트를 통해 서버 연결

                } catch (Exception ioe) {
                    Log.e("asd", ioe.getMessage() + "a");
                    ioe.printStackTrace();

                }
                checkUpdate.start();
            }
        }).start();

        // 전송 버튼 눌렸을때

        btnSend.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                try {

                    if ( editMessage.getText().toString() != null || editMessage.getText().toString() != ""  ) { // 입력 editMessage 가 null 아니면

                        final String return_msg = editMessage.getText().toString(); // 메세지 가져와서 String 화

                        // 메세지 타입 설정
                        String msg_type = "massage";


                        // 채팅 시간 표현하기
                        String ALLtime = Korea_getTime().toString(); // 전체시간
                        Log.e("보낸 전체시간 = ", "" + ALLtime);


                        String MStime = Korea_getTime_HM().toString(); // 전체시간
                        Log.e("보낸 시-분 = ", "" + MStime);

                        String[] HandM = MStime.split(":");
                        int Hour = Integer.parseInt(HandM[0]);   // String 형태의 데이터 인 시간을 int 값으로
                        int minute = Integer.parseInt(HandM[1]); // 분을 int 값으로

                        if (Hour >= 12) { // 시간이 12 보다 같거나 크면 = 오후
                            Hour = Hour - 12;
                            timeTEXT = "오후 " + Hour + ":" + minute;
                            Log.e("표시할 시간 = ", "" + timeTEXT);
                        } else {   // 오전
                            timeTEXT = "오전 " + Hour + ":" + minute;
                            Log.e("표시할 시간 = ", "" + timeTEXT);
                        }



                        /**
                         * 채팅 메세지를 data set 에 추가하고, 어댑터에 변경 알리면서 화면에 표현
                         */
                        ChatHistory_Dataset.add(new My_Chat_Data(editMessage.getText().toString(), timeTEXT));// 메시지 , 채팅 시간
                        chat_adapter.notifyDataSetChanged(); // 리스트뷰 변경 알림


                        // 채팅데이터를 저장하는테 필요한 데이터
                        // 0. 메세지 타입
                        // 1. room_key
                        // 2. sender
                        // 3. msg
                        // 4. send_time = 보낸 시간 2019-07-13,오전/오후,시(00~11),분(00~59)

                        // 데이터 확인
                        Log.e("Msg_type = ",msg_type);       // 메세지 타입
                        Log.e("Room_Key = ",Room_Key);       // 방 키
                        Log.e("sender = ",User_NAME_person); // 보내는 사람
                        Log.e("msg = ",return_msg);          // 채팅 메세지
                        Log.e("send_time = ",ALLtime);       // 전체시간



                        /**
                         * Sqlite 에 채팅 데이터 저장
                         */
                        SQLite_save_chat.ChatData_insert(Room_Key, User_NAME_person, return_msg, ALLtime);





                        if ( msg_type.equals("massage") ){ // 메세지 타입이 - massage 일 경우

                            // massage json 객체 생성
                            JSONObject massage_obj = new JSONObject();

                            // massage json 객체에 데이터 넣기 = 메세지 전달 할때 방키, 보내는 사람, 채팅 메세지, 채팅시간(전체시간)
                            massage_obj.put("Msg_type", "massage");    // 타입 = 연결 응답 타입
                            massage_obj.put("Room_key", Room_Key);          // 방 키
                            massage_obj.put("Massage", return_msg);         // 메세지
                            massage_obj.put("Sender", User_NAME_person);    // 전송자 아이디
                            massage_obj.put("Send_time", ALLtime);          // 전송 시간


                            // 응답 Json 스트링화 하기
                            String massage_jsonString = massage_obj.toString();


                            if (!TextUtils.isEmpty(return_msg)) {
                                Log.e("서버 메세지 전송 실행 = ","");
                                new SendmsgTask().execute(massage_jsonString); // 서버 메세지 전송 실행 - 넘길때 객체를 넘겨야 한다.

                                // 서버에 넘길 데이터
                                // 1. 방키
                                // 2. 전송자
                                // 3. 채팅 메세지
                            }
                            else { }

                        }
                        else{ }


                        // 리사이클러뷰 최하단으로 이동
                        ChatHistory_RecyclerView.post(new Runnable() {
                            @Override
                            public void run() {
                                ChatHistory_RecyclerView.scrollToPosition(ChatHistory_RecyclerView.getAdapter().getItemCount() - 1);
                            }
                        });




                    }
                    else {
                        Toast.makeText(getApplicationContext(), "메세지를 입력해 주세요.", Toast.LENGTH_SHORT).show();
                    }

                } catch (Exception e) {
                    e.printStackTrace();
                }
            }
        });





        /**
         * 리사이클러뷰에 기존에 있던 채팅 데이터 불러오기
         * SQLite 에서 불러 온다.
         */
        try {
            Log.e("셀렉트 결과 : ",SQLite_save_chat.Chat_msg_getResult(Room_Key));

            String result  = SQLite_save_chat.Chat_msg_getResult(Room_Key);

            JSONArray chatlist_JsonArray = new JSONArray(result);


            for(int i = 0; i < chatlist_JsonArray.length(); i++) {   // JSONArray 길이 만큼 반복하여 JSONObject 를  꺼냄

                // Array 에서 하나의 JSONObject 를 추출
                JSONObject dataJsonObject = chatlist_JsonArray.getJSONObject(i);

                // 기존에 SQLite에 저장된 채팅 데이터 가져오기
                String Num = dataJsonObject.getString("번호");
                String Room_key = dataJsonObject.getString("방_키");
                String Sender = dataJsonObject.getString("전송자");
                String Massage = dataJsonObject.getString("메세지");
                String Send_time = dataJsonObject.getString("보낸시간");

                // 데이터 로그 찍어보기
                Log.e("번호 = ",Num);
                Log.e("방_키 = ",Room_key);
                Log.e("전송자 = ",Sender);
                Log.e("메세지 = ",Massage);
                Log.e("보낸시간 = ",Send_time);




                // 상대방 채팅과 내 채팅 나누기
                if(Sender.equals(User_NAME_person)){ // 내 채팅일 경우

                    String timeTEXT;
                    String[] Send_time_array = Send_time.split(",");

                    String[] HandM = Send_time_array[1].split(":");
                    int Hour = Integer.parseInt(HandM[0]);   // String 형태의 데이터 인 시간을 int 값으로
                    int minute = Integer.parseInt(HandM[1]); // 분을 int 값으로

                    if (Hour >= 12) { // 시간이 12 보다 같거나 크면 = 오후
                        Hour = Hour - 12;
                        timeTEXT = "오후 " + Hour + ":" + minute;
                        Log.e("표시할 시간 = ", "" + timeTEXT);
                    } else {   // 오전
                        timeTEXT = "오전 " + Hour + ":" + minute;
                        Log.e("표시할 시간 = ", "" + timeTEXT);
                    }

                    // 내 채팅
                    ChatHistory_Dataset.add(new My_Chat_Data(Massage, timeTEXT));// 메시지 , 채팅 시간
                    chat_adapter.notifyDataSetChanged(); // 리스트뷰 변경 알림

                }
                else {

                    String timeTEXT;
                    String[] Send_time_array = Send_time.split(",");


                    String[] HandM = Send_time_array[1].split(":");
                    int Hour = Integer.parseInt(HandM[0]);   // String 형태의 데이터 인 시간을 int 값으로
                    int minute = Integer.parseInt(HandM[1]); // 분을 int 값으로

                    if (Hour >= 12) { // 시간이 12 보다 같거나 크면 = 오후
                        Hour = Hour - 12;
                        timeTEXT = "오후 " + Hour + ":" + minute;
                        Log.e("표시할 시간 = ", "" + timeTEXT);
                    } else {   // 오전
                        timeTEXT = "오전 " + Hour + ":" + minute;
                        Log.e("표시할 시간 = ", "" + timeTEXT);
                    }

                    // 상대방 채팅
                    ChatHistory_Dataset.add(new ChatHistory_Data(Sender, Massage,timeTEXT));// 이름 , 메세지 , 시간
                    chat_adapter.notifyDataSetChanged(); // 리스트뷰 변경 알림

                }

            }// for 문 끝

        } catch (JSONException e) {
            e.printStackTrace();
        }


        // JsonAarry로 받고 object로 나눠서 표현




        // 리사이클러뷰 최하단으로 이동
        ChatHistory_RecyclerView.post(new Runnable() {
            @Override
            public void run() {
                ChatHistory_RecyclerView.scrollToPosition(ChatHistory_RecyclerView.getAdapter().getItemCount() - 1);
            }
        });


    }
// onCreate 끝 ------------------------------------------------------------------------------------------------------------------------------------------------------------








    // 메세지 전송 메소드
    private class SendmsgTask extends AsyncTask<String, Void, Void> {
        @Override
        protected Void doInBackground(String... strings) {
            try {
                socketChannel
                        .socket()
                        .getOutputStream()
                        .write(strings[0].getBytes("UTF-8")); // 서버로 코드 인코딩?? utf-8
            } catch (IOException e) {
                e.printStackTrace();
            }
            return null;
        }
        @Override
        protected void onPreExecute() {
            super.onPreExecute();
            runOnUiThread(new Runnable() {
                @Override
                public void run() {
                    editMessage.setText(""); //
                }
            });
        }
    }



    void receive() {
        while (true) {
            try {
                ByteBuffer byteBuffer = ByteBuffer.allocate(256);
                //서버가 비정상적으로 종료했을 경우 IOException 발생
                int readByteCount = socketChannel.read(byteBuffer); //데이터받기
                Log.d("readByteCount", readByteCount + "");
                //서버가 정상적으로 Socket의 close()를 호출했을 경우
                if (readByteCount == -1) {
                    throw new IOException();
                }

                byteBuffer.flip(); // 문자열로 변환

//                Charset charset = Charset.forName("EUC-KR");
                Charset charset = Charset.forName("UTF-8");

                data = charset.decode(byteBuffer).toString();
                Log.d("receive", "msg :" + data);
                handler.post(showUpdate);
            } catch (IOException e) {
                Log.d("getMsg", e.getMessage() + "");
                try {
                    socketChannel.close();
                    break;
                } catch (IOException ee) {
                    ee.printStackTrace();
                }
            }
        }
    }

    private Thread checkUpdate = new Thread() {
        public void run() {
            try {
                String line;
                receive();
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
    };

    private Runnable showUpdate = new Runnable() {

        public void run() {
            // 받아야 할 데이터
            // 1. 방키
            // 2. 보낸 사람
            // 3. 채팅 내용 메세지



            String receive = "Coming word : " + data;


            try {
                JSONObject json = new JSONObject(data);

                // 메세지 타입
                String msg_type = json.getString("Msg_type");
                Log.e(" 메세지타입 = ",msg_type);


                /**
                 * 메세지 타입 에 따른 다른 처리 방식
                 */
                if(msg_type.equals("connection Success")){ // 메세지 타입이 '연결 성공' 이면

                    String address = json.get("Massage").toString();    // 주소 데이터
                    Log.e(" 주소 = ",address); // 주소가 제대로 걸러서 뜨면은~~

                    // 응답 객체
                    JSONObject response_obj = new JSONObject();

                    // 응답 객체에 데이터 넣기 = 재응답에서 전달 할때 아이디,주소 전송해야됨.
                    response_obj.put("Msg_type", "connection response");    // 타입 = 연결 응답 타입
                    response_obj.put("Room_key", null);
                    response_obj.put("Massage", address);           // 주소
                    response_obj.put("Sender", User_NAME_person);   // 전송자 아이디

                    // 응답 Json 스트링화 하기
                    String response_jsonString = response_obj.toString();

                    // 연결 응답 메세지 전송 실행 - json을 String 으로 넘김
                    new SendmsgTask().execute(response_jsonString);

                }
                else if(msg_type.equals("massage")){ // 메세지 타입이 '메세지' 이면,

                    String Sender = json.get("Sender").toString();      // 주소 데이터
                    String Room_key = json.get("Room_key").toString();  // 방 키
                    String Send_time = json.get("Send_time").toString();// 보낸 시간
                    String Massage = json.get("Massage").toString();    // 메세지


                    if(Sender.equals(receive0)){ // 보낸 사람이 본인 일 경우
                        Log.e(" 내가 보낸 메세지 전송 완료.","");  // 해당 앱 사용자가 보낸 메세지 사용 완료
                    }
                    else{ // 타인이 보낸 메세지 일 경우

                        // 채팅 시간 표현하기
                        String ALLtime = Korea_getTime().toString(); // 전체시간
                        Log.e("보낸 전체시간 = ", "" + ALLtime);


                        String MStime = Korea_getTime_HM().toString(); // 전체시간
                        Log.e("보낸 시-분 = ", "" + MStime);

                        String[] HandM = MStime.split(":");
                        int Hour = Integer.parseInt(HandM[0]);   // String 형태의 데이터 인 시간을 int 값으로
                        int minute = Integer.parseInt(HandM[1]); // 분을 int 값으로

                        if (Hour >= 12) { // 시간이 12 보다 같거나 크면 = 오후
                            Hour = Hour - 12;
                            timeTEXT = "오후 " + Hour + ":" + minute;
                            Log.e("표시할 시간 = ", "" + timeTEXT);
                        } else {   // 오전
                            timeTEXT = "오전 " + Hour + ":" + minute;
                            Log.e("표시할 시간 = ", "" + timeTEXT);
                        }






                        /**
                         * 일단 해당 메세지를 화면에 띄우고
                         * 그다음 SQL 라이트에 저장한다.
                         * 화면에 띄우는데 필요한것
                         * 1. 메세지
                         * 2. 보낸 사람 아이디
                         * 3. 전송 시간(이거는 가져 온거 말고 내 코드로 직접짠거 넣어준다.)
                         * 4.
                         */
                        Log.e("상대방 채팅 들어옴 = ", Sender);
                        ChatHistory_Dataset.add(new ChatHistory_Data(Sender, Massage,timeTEXT));// 이름 , 메세지 , 시간
                        chat_adapter.notifyDataSetChanged(); // 리스트뷰 변경 알림



                        // 리사이클러뷰 최하단으로 이동
                        ChatHistory_RecyclerView.post(new Runnable() {
                            @Override
                            public void run() {
                                ChatHistory_RecyclerView.scrollToPosition(ChatHistory_RecyclerView.getAdapter().getItemCount() - 1);
                            }
                        });



                        /**
                         * Sqlite 에 채팅 데이터 저장
                         */
                        SQLite_save_chat.ChatData_insert(Room_key, Sender, Massage, Send_time); // 방키, 보낸사람, 보낸 메세지, 전송시간

                    }

                }




            } catch (JSONException e) {
                e.printStackTrace();
            }


            // 받은 채팅 데이터 표시
            Log.e("받은 채팅 데이터 = ",receive);  // 받은 채팅 데이터 =: Coming word : [/112.221.220.204:14459]메세지msg
//            E/받은 채팅 데이터 =: Coming word : [SERVER] - /112.221.220.204:14459has left!       = 떠낫다
//                                              [SERVER] - /112.221.220.204:19517has joined!    = 들어왓다
//                                              [/112.221.220.204:19517]ctsgb                   = 채팅 메세지


            // 주소값이 계속 변한다. = [/112.221.220.204:14459]


//            Log.e("상대방 채팅 들어옴", "");
//            ChatHistory_Dataset.add(new ChatHistory_Data(divide_msg[1], divide_msg[2],timeTEXT));// Chat  History_Data - 입력값 2개 Id,메세지
//            chat_adapter.notifyDataSetChanged(); // 리스트뷰 변경 알림
//
//            receiveMsgTv.setText(receive);
        }

    };










    /** Service 로 메시지를 보내는 메소드 */
    private void sendMessageToService(String str_MSG) {
        if (isService) {    // Service 가 현재 동작 중이라면 작동함.
            if (mServiceMessenger != null) {    // 메신저 클래스 객체 mServiceMessenger = null 이 아니라면... 서비스 작동 상태와 연관이 있다.
                try {
                    Message msg = Message.obtain(null, MyService.MSG_SEND_TO_SERVICE, str_MSG ); // 입력번호, 메세지
                    mServiceMessenger.send(msg);    // 서비스로 메세지 전송
                } catch (RemoteException e) {
                }
            }
        }
        else {
            Toast.makeText(getApplicationContext(), "서비스가 작동 중이지 않습니다.", Toast.LENGTH_SHORT).show();
        }
    }





}
