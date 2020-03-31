package com.example.user.androidhive;

import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.IOException;
import java.io.OutputStream;
import java.net.InetAddress;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;

/**
 * Created by user on 2018-03-24.
 * 채팅 메세지를 서버에서 받아 해당 방 참여자에게만 전송하기위해 생성된 코드
 */

public class ChatServer {

    /**
     * HashMap 은 key,value 로 구성되어 있습니다.
     */
    // 접속자의 정보를 저장할 맵
    HashMap<String, DataOutputStream> clients;
    // 서버 소켓(서비스용 소켓)
    ServerSocket serverSocket = null;


    // 방 데이터들 담을 ArrayList
    static ArrayList<Room_DATA> Room_DATA_SET;   // 객체 생성
    // 채팅으로 전달 받은 방키
    String received_room_key;
    // 채팅을 전송할 아이디 들이  담겨 있는 arr
    static List<String> ID_arr;


//	private static Connection con;
//	private static Statement stmt;
//	private static ResultSet rs;
//	private static PreparedStatement pstmt = null;


    public ChatServer(){
        // 연결부 hashmap 생성자(Key, value) 선언
        clients = new HashMap<String, DataOutputStream>();	// key = String , value = DataOutputStream
        /**
         * 해시맵의 키는 아이디가 되어야 할것이고,
         * 밸류 값은 각각의 아이디의 연결 소켓이 되어야 할것이다.
         */
    }


    public static void main(String[] args) throws IOException {

        ID_arr = new ArrayList<String>();
        Room_DATA_SET = new ArrayList();


//        Connection connection = null;
//        Statement st = null;
//        try {
//            Class.forName("com.mysql.jdbc.Driver");
//            connection = DriverManager.getConnection("jdbc:mysql://49.247.208.191:3306/AndroidHive" , "root", "132dktmskf@");
//            st = connection.createStatement();
//            System.out.println("mysql 접속 성공");
//
//            String sql;
//            sql = "select * FROM Room_List;";	//Room_List 테이블의 데이터 전체 가져옴
//            System.out.println("쿼리문 전송 성공");
//
//            ResultSet rs = st.executeQuery(sql);
//
//            int index = 0;  // 초기값 0
//
//            while (rs.next()) {	// 반복
//
//                System.out.println("반복문 들어옴. " +"\n" );
//                System.out.println("현재 index 번호 = "+index +"\n" );
//
//                String Room_KEY = rs.getString("Room_KEY"); // 방 키
//                String Users = rs.getString("Users");       // 방에 있는 유저들
//
//                Room_DATA_SET.add(new Room_DATA(Room_KEY, Users));
//
//                System.out.println("Room_DATA_SET 방 키 = "+Room_DATA_SET.get(index).Room_key +"\n" );
//                System.out.println("Room_DATA_SET 참여자 = "+Room_DATA_SET.get(index).Users +"\n" );
//
//                index = index +1;
//
//            }
//
//            rs.close();
//            st.close();
//            connection.close();
//        } catch (SQLException se1) {
//            se1.printStackTrace();
//        } catch (Exception ex) {
//            ex.printStackTrace();
//        } finally {
//            try {
//                if (st != null)
//                    st.close();
//            } catch (SQLException se2) {
//            }
//            try {
//                if (connection != null)
//                    connection.close();
//            } catch (SQLException se) {
//                se.printStackTrace();
//            }
//        }

        new ChatServer().start(); // start() 메소드 시작

    }// main(String[] args)  종료

    void start(){

        int port = 5001; // 서비스를 위한 포트번호
        // ServerSocket(서비스를 위한 소켓) , Socket
        Socket socket = null;   // 클라이언트 - 서버 연결된 소켓 객체

        try {
            // 서버 소켓 생성
            serverSocket = new ServerSocket(port); // 포트 번호에 따른 서버 소켓 생성
            System.out.println("접속 대기중 ..");  // 접속 대기 상태


            while(true){    // 무한 반복을 통해 사용자 접속을 항상 대기
                // 사용자 접속 되면 = 소켓 연결
                socket = serverSocket.accept();	// 소켓 받기 - 사용자 정보를 받음!!, 이 과정을 통해 클라이언트 - 서버 소켓 연결 완료

                // 클라이언트의 ip 주소
                InetAddress ip = socket.getInetAddress(); // 받은 소켓을 통해 클라이언트의 ip 주소 획득
                System.out.println(ip + " connected");    // ip 주고 연결 확인
                /**
                 * - 스레드.start() => run() 메소드 실행
                 * - 연결된 socket을 MultiThread 클래스에 전달.
                 * MultiThread 클래스의 생성자 =  MultiThread(Socket socket)
                 */
                new MultiThread(socket).start();    // 소켓 삽입, MultiThread 시작
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
    }// start() 메소드 끝


    /**
     * 클라이언트가 접속 했을때 실행됨.
     */
    class MultiThread extends Thread {
        Socket socket=null; // 소켓
        String Connect_ID =null;    // 접속자 ID
        String msg=null;    // 메세지

        // 스트림
        DataInputStream input;
        DataOutputStream output;

        // 생성자
        public MultiThread(Socket socket){
            this.socket = socket;   // 소켓 객체 동일화

            try {
                input = new DataInputStream(socket.getInputStream());    // input  스트림 객체 선언
                output = new DataOutputStream(socket.getOutputStream()); // output 스트림 객체 선언
            } catch (IOException e) {
                e.printStackTrace();
            }
        }

        public void run() {  // 계속 듣기만!!
            try {
                Connect_ID = input.readUTF();  // 전송된 아이디 가져오기
                System.out.println("연결된 사용자 : "+Connect_ID+"\n");    // 아이디 출력
                // 클라이언트의 정보를 저장(Connect_ID 를 key로)
                clients.put(Connect_ID,output);	// 해시 맵에 데이터 저장 => key = Connect_ID , value = output (DataOutputStream 객체)
                sendMsg(Connect_ID+" : 접속");	// 메시지 전송  EX) 아이디 : 접속  -  이 부분도 JSON 형태의 접속 관련 JSON key를 만들어서 전송해야함. 접속과 메세지를 구분짓기 위해서

                while(true){
                    String receive_massage = input.readUTF();	// 메세지 수신
                    // 방키:::::아이디:::::채팅메세지
                    System.out.println("받은 메세지 = "+receive_massage+"\n");    // 메세지 표시

                    String[] RM_arr =  receive_massage.split(":::::");  // ::::: 으로 데이터 나눔
                    System.out.println("방 키 = "+RM_arr[0]+"\n");
                    received_room_key = RM_arr[0];  // 방키

                    Find_Chat_users(received_room_key); // Find_Chat_users 메소드로 이동


                    sendMsg(receive_massage);	// 메시지 전송. sendMsg 메소드 이용

                }
            } catch (IOException e) {
                sendMsg("No massage");
                e.printStackTrace();
            }
        }// run() 끝




        /**
         * 해당 방의 참여 하고 있는 유저들을 찾는 메소드
         */
        void Find_Chat_users (String received_room_key){
            System.out.println("Room_DATA_SET 길이 = "+Room_DATA_SET.size()+"\n");

            for (int i = 0; i < Room_DATA_SET.size(); i++ ) { // 방 개수 만큼 반복함.
                // 채팅으로 전송한 방키를 가져와서
                // Room_DATA_SET 에 있는 방키와 비교한다.
                if(received_room_key.equals(Room_DATA_SET.get(i).Room_key) ){   // 전송 받은 방 키 = 서버에서 가지고 있는 방키 가 같으면
                    String users =  Room_DATA_SET.get(i).Users; // 해당 방의 유저들 전체 String 데이터들을 가져온다,

                    String[] users_arr = users.split(","); // , 를 기준으로 나눈다.

                    for (int a = 0; a < users_arr.length; a++ ) {   // users_arr 길이 만큼 반복
                        // ID_arr 에 ID 삽입
                        ID_arr.add(users_arr[a]);
                        /**
                         * 이 작업이 끝나게 되면
                         * ID_arr[0] = masury2011
                         * ID_arr[1] = ki1234
                         * ID_arr[2] = sudo888
                         * 이렇게 데이터가 담기기 되는것이다.
                         * 이것을 가지고 최종적으로 데이터를 보낼때 ID_arr[] 배열에 포함 되어있는 아이디과 HashMap 의 키값과 비교 하여 일치하는 경우에만 데이터를 전송하게 되는것이다.
                         */
                    }
                }
            }// 반복문 종료
        }// Find_Chat_users




        /**
         - 메세지 전송하는 메소드
         - MultiThread 클래스 내에 포함 되어 있음.
         */
        void sendMsg(String msg){
            // 클라이언트의 key 집합 (Connect_ID 의 집합)
            Iterator<String> it = clients.keySet().iterator();

            while (it.hasNext()) {	// 다음 요소가 있으면
                try {

                    String key = (String) it.next();                // 반복문을 통해 각 Key 반복해서 변경됨.
                    System.out.print("key="+key+"\n");                   // 키 출력
//                    System.out.println(" value="+clients.get(key)); // value 출력

                    System.out.println("ID_arr 길이 = "+ID_arr.size()+"\n");
                    for(int a = 0; a < ID_arr.size(); a++ ){    // ID_arr 길이 만큼 반복

                        if(ID_arr.get(a).equals(key)){  // 아이디 배열의 키와 해시맵의 키가 일치하면 데이터 전송
                            // 클라이언트의 OutputStream 을 저장
                            OutputStream dos = clients.get(it.next());  // it.next() = key 를 의미,  OutputStream dos  에 해시맵의 키에 따른 value = DataOutputStream 객체를 담는다는 코드
                            DataOutputStream output = new DataOutputStream(dos);    //  OutputStream dos 를 가진 DataOutputStream output 객체를 생성

                            // 클라이언트에게 메세지 전송
                            output.writeUTF(msg);   // output 보내면서 msg 를 담아서 보내게 된다.
                            System.out.print("메세지 전송됨. \n");
                        }
                    }// 반복문 끝

                } catch (Exception e) {
                    e.printStackTrace();
                }
            }

            ID_arr.clear();
            System.out.print("ID_arr 초기화 되었으면 0 나옴 = "+ID_arr.size()+"\n");
        }
    }// MultiThread 종료





    // 방 데이터를 가질 데이터 세트
    static class Room_DATA {

        String Room_key;   // 방 키
        String Users;      // 참여자들  EX) 성훈, 은채, 한국

        // 생성자
        public Room_DATA( String Room_key, String Users ) { // 방 키, 참여자들
            this.Room_key = Room_key;   // 서버에서 방 번호
            this.Users = Users;   // 모임 이름
        }
    }


}// 코드 끝
