package com.example.user.androidhive;

import android.app.Notification;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.app.Service;
import android.content.Context;
import android.content.Intent;
import android.os.Build;
import android.os.Handler;
import android.os.IBinder;
import android.os.Message;
import android.os.Messenger;
import android.support.annotation.RequiresApi;
import android.util.Log;
import android.widget.Toast;

import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.IOException;
import java.net.Socket;

import static com.example.user.androidhive.MainActivity.receive0;

public class MyService extends Service {

    public static final int MSG_SEND_TO_SERVICE = 3;

    /** 다른 클래스에서도 사용가능한 socket_1  */
    public static Socket socket_1;       // 클라이언트 소켓

    Server_Connect ServerConnect_thread; // 서버 연결 쓰레드
    Receive_Thread receive;              // 메세지 수신 스레드
    Send_Thread send;                    // 메세지 보내는 스레드

    String msg; // 받은 메세지


// --------------------------------------------------------------------------------------------------------------------------------------------------------- 시작
    /**
     * onCreate() 와 onDestroy() 는
     * startService , bindService 두개 모두 할때 실행된다.
     */
    @Override
    public void onCreate() {
        super.onCreate();

    }
    @Override
    public void onDestroy() {
        super.onDestroy();

        thread.stopForever();   // 스레드 종료를 위한 stopForever 메소드 불러옴
        thread = null;          // 쓰레기 값을 만들어서 빠르게 회수하라고 null을 넣어줌.

//        ServerConnect_thread.stopForever(); // 스레드 종료를 위한 stopForever 메소드 불러옴
//        ServerConnect_thread = null;        //쓰레기 값을 만들어서 빠르게 회수하라고 null을 넣어줌.
//        Toast.makeText(getApplicationContext(), "서비스 종료 됨.", Toast.LENGTH_LONG).show();
        Log.e("서비스 종료","");
        Toast.makeText(getApplicationContext(),"서비스 종료됨.",Toast.LENGTH_SHORT).show();
    }
// ---------------------------------------------------------------------------------------------------------------------------------------------------------

    NotificationManager Notifi_M;   // 알림매니저 전역 객체 생성
    ServiceThread thread;           // 서비스 스레드 객체 생성
    Notification Notifi ;           // 알림 전역 객체 생성


// startService() ------------------------------------------------------------------------------------------------------------------------------------------ 시작
    /**
     * 백그라운드에서 실행되는 동작들이 들어가는 곳.
     서비스가 호출될때마다 매번 실행(onResume()과 비슷)
     *
     * startService() 메소드가 실행되면 거치는곳
     */
    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {  // 인텐트 , 플래그, 스타트아이디
        Toast.makeText(getApplicationContext(),"서비스 시작 = onStartCommand",Toast.LENGTH_SHORT).show();


        Notifi_M = (NotificationManager) getSystemService(Context.NOTIFICATION_SERVICE);    // 알림 매니저 객체 생성
        myServiceHandler handler = new myServiceHandler();  // 서비스 핸들러 객체 생성
        thread = new ServiceThread(handler);    // 서비스 스레드 객체 생성
        thread.start(); // 스레드 시작 - 서비스 시작과 동시에 시작됨. - 서비스 종료 할때 까지 계속 작동함.


        /**
         * - START_STICKY
         * 서비스가 런타임에 의해 종료 되면 항상 재시작되며,
         * 재시작 될때 마다 onStartCommand 가 호출 된다.
         * 이때 전달되는 intent 는 null이다.
         * 지속적인 백그라운드 작업이 필요한 경우나 음악 재생 서비스 등에 적합한 방식.
         */
        return START_STICKY;
    }
// startService() -------------------------------------------------------------------------------------------------------------------------------------------


    // 핸들러를 상속 받은 myServiceHandler
    class myServiceHandler extends Handler {
        @RequiresApi(api = Build.VERSION_CODES.JELLY_BEAN)
        @Override
        public void handleMessage(android.os.Message msg) {  // sendEmptyMessage 작동 함
            Intent intent = new Intent(MyService.this, MainActivity.class);
            PendingIntent pendingIntent = PendingIntent.getActivity(MyService.this, 0, intent,PendingIntent.FLAG_UPDATE_CURRENT);

            Notifi = new Notification.Builder(getApplicationContext())
                    .setContentTitle("Content Title")                   // 타이틀
                    .setContentText("Content Text")                     // 텍스트
                    .setSmallIcon(R.drawable.default_image)                      // 알림 아이콘 이미지 설정
                    .setTicker("알림!!!")                                // 알림이 출력될때 상단에 나오는 문구??
                    .setContentIntent(pendingIntent)
                    .build();

            //소리추가
            Notifi.defaults = Notification.DEFAULT_SOUND;
            //알림 소리를 한번만 내도록
            Notifi.flags = Notification.FLAG_ONLY_ALERT_ONCE;
            //확인하면 자동으로 알림이 제거 되도록
            Notifi.flags = Notification.FLAG_AUTO_CANCEL;

            Notifi_M.notify( 777 , Notifi); // 알림 Id = 777

            //토스트 띄우기
            Toast.makeText(MyService.this, "뜸?", Toast.LENGTH_SHORT).show(); // 서비스 동작 확인
        }
    }// 핸들러 끝


    class ServiceThread extends Thread{

        Handler handler;
        boolean isRun = true;

        // 생성자
        public ServiceThread(Handler handler){ // 입력 조건 = handler
            this.handler = handler;// 이 ServiceThread 에서 사용하는 handler 와 입력값으로 가져온 handler 를 동일화 시킨다.
        }

        // 서비스 종료를 위한 메소드 stopForever
        public void stopForever(){
            synchronized (this) {
                this.isRun = false;
            }
        }

        public void run(){
            //반복적으로 수행할 작업을 한다.
            while(isRun){   // isRun = true = 무한반복한다는 이야기
                handler.sendEmptyMessage(0); //쓰레드에 있는 핸들러에게 메세지를 보냄
                try{
                    Thread.sleep(10000); //10초씩 쉰다.
                }catch (Exception e) {}
            }
        }

    }






// bindService() -------------------------------------------------------------------------------------------------------------------------------------------- 시작
    /**
     *  서비스 바인딩 onBind
     *  - 서비스 바인딩은 연결된 액티비티가 사라지면 서비스도 소멸됩니다. (즉 백그라운드에서 무한히 실행되진 않습니다)
     */
    @Override
    public IBinder onBind(Intent intent) {
        Toast.makeText(getApplicationContext(),"서비스 시작 = onBind",Toast.LENGTH_SHORT).show();

        ServerConnect_thread = new Server_Connect("49.247.208.191", "5001");    // 서버 연결 스레드 객체 생성
        ServerConnect_thread.start(); // 스레드 시작 - 서비스 시작과 동시에 시작됨. - 서비스 종료 할때 까지 계속 작동함. => 현재 지금 서비스가 작동해도 계속 동작 하게끔 설정함.
        // 서비스가 종료 되면 일단 스레드가 종료 되게 끔. 할 예정. 그런데 채팅 화면을 벗어나면 서비스가 종료되는 문제가 있음. 원래는 종료가 되면 안됨!!

        return mMessenger.getBinder();
    }
    /**
     * 바인딩 해제 onUnbind
     */
    @Override
    public boolean onUnbind(Intent intent) {
        Log.e("onUnbind","onUnbind");
        Toast.makeText(MyService.this, "onUnbind", Toast.LENGTH_SHORT).show();
        return super.onUnbind(intent);
    }
// bindService() --------------------------------------------------------------------------------------------------------------------------------------------







// Server_Connect 서버 연결 ----------------------------------------------------------------------------------------------------------------------------------- 시작
    /**
     * 서버아이피와 포트번호를
     * 서버에 접속하는데 사용함
     */
    class Server_Connect extends Thread {
        String ip;   // 접속 ip
        String port; // 접속 포트번호
        boolean isRun = true;                   // 서비스 중지를 위한 변수
        private DataOutputStream output = null; // 아이피와 포트번호 전달을 위한 '데이터 아웃풋 스트림'

        // 생성자
        public Server_Connect(String ip, String port){
            this.ip = ip;
            this.port = port;
        }

        // 서비스 종료할때 스레드도 종료하기 위한 메소드 stopForever = 스레드 종료
        public void stopForever(){
            synchronized (this) {
                this.isRun = false;
            }
        }

        // start 요청시 발생
        public void run(){
            //반복적으로 수행할 작업을 한다.
            try {
                // 연결후 바로 ReceiveThread 시작
                // 채팅 서버에 접속
                socket_1 = new Socket(ip, Integer.parseInt(port)); // 아이피와 포트번호 을 가진 소켓 생성 => socket_1(ip, port)

                // 서버에 메시지를 전달하기위한 스트림 생성
                output = new DataOutputStream(socket_1.getOutputStream()); // socket_1 을 가진 DataOutputStream 객체 output 을 생성
                // 메시지 수신용 스레드 생성
                receive = new Receive_Thread(socket_1); // socket_1 을 가진 수신 스레드 객체 생성 => 생성자 Receive_Thread(Socket socket)
                receive.start(); // 수신 스레드 시작

//            Bundle bundle = new Bundle();
//            bundle.putString("text", "메시지 내용입니다");
//            bundle.putString("text1", String.valueOf(jsonObject));



                output.writeUTF(receive0);  // output 에는 socket_1(ip, port) 가 담겨있고, 보내면서, 아이디를 같이 전송한다.
            }
            catch (IOException e) {
                e.printStackTrace();
            }
        }
    }// Server_Connect 끝
// Server_Connect 서버 연결 -------------------------------------------------------------------------------------------------------------------------------------








// Receive_Thread 수신 쓰레드 --------------------------------------------------------------------------------------------------------------------------------- 시작
    /**
     * 수신 스레드
     */
    class Receive_Thread extends Thread {
        private Socket socket = null;   // 소켓
        DataInputStream input;          // 들어오는 텍스트 데이터를 담는 스트림

        // 생성자
        public Receive_Thread(Socket socket) {
            this.socket = socket;   // 소켓 동일화
            try{
                // 채팅서버로 부터 메시지를 받기위한 스트림 생성
                input = new DataInputStream(socket.getInputStream());   // 인풋스트림 객체 생성
            }catch(Exception e){
                e.printStackTrace();
            }
        }
        // 메세지 수신후 Handler로 전달
        public void run() {
            try {

                /**
                 * input = new DataInputStream(socket.getInputStream());
                 * 스트림 객체가 존재하면  input != null
                 */
                while (input != null) {

                    // 채팅 서버로 부터 받은 메시지
                    msg = input.readUTF();   // 인풋스트림 객체 로 부터 메시지 읽어들임
                    Log.e("받은 메세지 = ",msg);


                    /**
                     * 여기서 채팅 데이터 읽어 왔다.
                     * 액티비티로 이 채팅 데이터를 보내야한다.
                     */
                    Intent broadcastIntent = new Intent();  // 브로드 캐스트 인텐트 생성
                    broadcastIntent.setAction(ServiceChat_Activity.mBroadcastStringAction); // 메인 화면에 mBroadcastStringAction 액션 작동 전달
                    broadcastIntent.putExtra("Chat_TEXT", msg); // 키값 = Data , 밸류 = msg 전달
                    sendBroadcast(broadcastIntent); // 브로드 캐스트 인텐트 전달

                }// while 반복문 끝

            } catch (IOException e) {
                e.printStackTrace();
            }
        }
    }// Receive_Thread 끝
// Receive_Thread 수신 쓰레드 ----------------------------------------------------------------------------------------------------------------------------------




// SendThread 송신 쓰레드 -------------------------------------------------------------------------------------------------------------------------------------- 시작
    /**
     * 메시지를 보내는 쓰레드
     */
    class Send_Thread extends Thread {
        private Socket socket;
        String sendmsg ;            // 보낼 메세지
        DataOutputStream output;

        // 생성자
        public Send_Thread(Socket socket,String MSG) {
            this.sendmsg = MSG;
            this.socket = socket;
            try {
                // 채팅서버로 메시지를 보내기 위한 스트림 생성
                output = new DataOutputStream(socket.getOutputStream());
            } catch (Exception e) {
            }
        }

        public void run() {
            try {
                // 메세지 전송부 (누군지 식별하기위한 방법으로 mac를 사용)
                Log.d(ACTIVITY_SERVICE, "11111");

                if (output != null) {
                    if (sendmsg != null) {
                        // 채팅서버에 메세지 전달
                        Log.e("서버에 보내는 메세지 =",sendmsg);

                        output.writeUTF(sendmsg);
                    }
                }
            } catch (IOException e) {
                e.printStackTrace();
            } catch (NullPointerException npe) {
                npe.printStackTrace();
            }
        }
    }// SendThread 끝
// SendThread 송신 쓰레드 --------------------------------------------------------------------------------------------------------------------------------------






// 액티비티에서 서비스로 전달한 데이터를 받는 Messenger 객체. handleMessage 로 데이터 처리 ----------------------------------------------------------------------------- 시작
    /** activity로부터 binding 된 Messenger */
    private final Messenger mMessenger = new Messenger(new Handler(new Handler.Callback() {
        @Override
        public boolean handleMessage(Message msg) {
            // 액티비티에서 서비스로 온 메세지 확인
            Log.w("test","ControlService - message what : "+msg.what +" , msg.obj "+ msg.obj); // msg.obj = 실제 메세지

            switch (msg.what) {
                case MSG_SEND_TO_SERVICE:   // 3
                    /** 여기서 받은 메세지를 쓰레드로 넘겨 주어야 함 */
                    send = new Send_Thread(socket_1, msg.obj.toString());  // 메시지 전송 스레드 시작
                    send.start();

                    break;
            }
            return false;
        }
    }));
// -----------------------------------------------------------------------------------------------------------------------------------------------------------





}// 코드 끝
