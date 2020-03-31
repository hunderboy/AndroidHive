package com.example.user.androidhive;

import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import android.util.Log;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

/**
 * Created by user on 2018-03-23.
 * 채팅방에 대한 정보들을 저장할 SQLite
 * SQLite_Create_Room 클래스의 객체를 생성 할때 채팅방데이터를 저장할 DB가 만들어지고,
 * 동시에 onCreate 에서 테이블도 만들어진다.
 */

public class SQLite_Create_Room extends SQLiteOpenHelper {

    // DBHelper 생성자로 관리할 DB 이름과 버전 정보를 받음
    public SQLite_Create_Room(Context context, String name, SQLiteDatabase.CursorFactory factory, int version) {
        super(context,name, factory, version); // "/mnt/sdcard/"+
    }

    /**
     *   SQLite_Create_Room 객체를 생성 하면 아래에 해당하는 onCreate 함수가 실행된다.
     */
    @Override
    public void onCreate(SQLiteDatabase db) {
        // 새로운 테이블 생성
        /* 이름은 MONEYBOOK이고, 자동으로 값이 증가하는 _id 정수형 기본키 컬럼과
        item 문자열 컬럼, price 정수형 컬럼, create_at 문자열 컬럼으로 구성된 테이블을 생성. */
        /**
         * 테이블 명 : RoomList_info , 칼럼 6개
         * 1. RoomList_no = INTEGER, auto_increment, primary key
         * 2. Room_KEY = 방 키 TEXT
         * 3. Room_Moim_Name = 모임명 TEXT
         * 4. Users = 방 참여자 TEXT
         * 5. Number_of_people = 참여 인원수 INTEGER
         * 6. Moim_img_Name = 모임 이미지 이름 TEXT
         */
        db.execSQL("CREATE TABLE RoomList_info ( RoomList_no INTEGER PRIMARY KEY AUTOINCREMENT, Room_KEY TEXT, Room_Moim_Name TEXT, Users TEXT, Number_of_people INTEGER, Moim_img_Name TEXT  );");  // 채팅방 정보 테이블 생성
        db.execSQL("CREATE TABLE Chat_msg (num INTEGER  PRIMARY KEY AUTOINCREMENT, room_key TEXT, sender TEXT, msg TEXT, send_time TEXT );");
//        db.execSQL("CREATE TABLE ChatData (c_no INTEGER PRIMARY KEY AUTOINCREMENT, room_no_at_server TEXT, chat_by_id TEXT, chat_data TEXT, chat_time TEXT);"); // 채팅데이터 정보 테이블
    }

    // DB 업그레이드를 위해 버전이 변경될 때 호출되는 함수
    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {}


    /**
     * 채팅방 정보 테이블
     */
    public void RoomList_info_insert(String Room_KEY, String Room_Moim_Name, String Users, int Number_of_people, String Moim_img_Name ) { // 채팅방 정보 데이터 삽입 5개
        // 읽고 쓰기가 가능하게 DB 열기
        SQLiteDatabase db = getWritableDatabase();
        // DB에 입력한 값으로 행 추가
        db.execSQL( "INSERT INTO RoomList_info VALUES(null, '" + Room_KEY + "', '"+ Room_Moim_Name +"', '"+ Users +"', '"+ Number_of_people +"', '" + Moim_img_Name + "' );" ); // 삽입
        db.close();
    }

    public void RoomList_info_update(String item, int price) {
        SQLiteDatabase db = getWritableDatabase();
        // 입력한 항목과 일치하는 행의 가격 정보 수정
        db.execSQL("UPDATE MONEYBOOK SET price=" + price + " WHERE item='" + item + "';");
        db.close();
    }

    public void RoomList_info_delete(String Room_KEY) {
        SQLiteDatabase db = getWritableDatabase();
        // 입력한 항목과 일치하는 행 삭제
        db.execSQL("DELETE FROM RoomList_info WHERE Room_KEY='" + Room_KEY + "';");
        db.close();
    }

    public String RoomList_info_getResult() {
        // 읽기가 가능하게 DB 열기
        SQLiteDatabase db = getReadableDatabase();
        String result = "";

        // DB에 있는 데이터를 쉽게 처리하기 위해 Cursor를 사용하여 테이블에 있는 모든 데이터 출력
        Cursor cursor = db.rawQuery("SELECT * FROM RoomList_info", null);
        while (cursor.moveToNext()) {
            result +=
                    "--------------------------------------------------------------------------------" + "\n"
                    + "번호  : " + cursor.getString(0) + "\n"
                    + "방 키 : " + cursor.getString(1) + "\n"
                    + "모임명 : " + cursor.getString(2) + "\n"
                    + "참여자들 : " + cursor.getString(3) + "\n"
                    + "참여인원 수 : " + cursor.getInt(4) + "\n"
                    + "모임이미지 이름 : " + cursor.getString(5) + "\n";
        }
        return result;
    }





    /**
     * 채팅 데이터 테이블
     */
//     db.execSQL("CREATE TABLE ChatData (c_no INTEGER PRIMARY KEY AUTOINCREMENT, room_no_at_server TEXT, chat_by_id TEXT, chat_data TEXT, chat_time TEXT);"); // 채팅데이터 정보 테이블
//    public void ChatData_insert(String create_at, String item, int price) {
//        // 읽고 쓰기가 가능하게 DB 열기
//        SQLiteDatabase db = getWritableDatabase();
//        // DB에 입력한 값으로 행 추가
//        db.execSQL("INSERT INTO ChatData VALUES(null, '" + item + "', " + price + ", '" + create_at + "');");
//        db.close();
//    }

    public void ChatData_update(String item, int price) {
        SQLiteDatabase db = getWritableDatabase();
        // 입력한 항목과 일치하는 행의 가격 정보 수정
        db.execSQL("UPDATE MONEYBOOK SET price=" + price + " WHERE item='" + item + "';");
        db.close();
    }

    public void ChatData_delete(String item) {
        SQLiteDatabase db = getWritableDatabase();
        // 입력한 항목과 일치하는 행 삭제
        db.execSQL("DELETE FROM MONEYBOOK WHERE item='" + item + "';");
        db.close();
    }

    public String ChatData_getResult() {
        // 읽기가 가능하게 DB 열기
        SQLiteDatabase db = getReadableDatabase();
        String result = "";

//
//        // DB에 있는 데이터를 쉽게 처리하기 위해 Cursor를 사용하여 테이블에 있는 모든 데이터 출력
//        Cursor cursor = db.rawQuery("SELECT * FROM ChatData WHERE ", null);
//        while (cursor.moveToNext()) {
//            result += cursor.getString(0) + " : " + cursor.getString(1) + " | " + cursor.getInt(2) + "원 " + cursor.getString(3) + "\n";
//        }


        return result;
    }


    /**
     *  채팅 데이터 삽입
     */
    public void ChatData_insert(String room_key, String sender, String msg, String time) {
        // 읽고 쓰기가 가능하게 DB 열기
        SQLiteDatabase db = getWritableDatabase();
        Log.e("ChatData_insert","들어감 SQLite");

        // 채팅데이터를 저장하는테 필요한 데이터
        // 1. room_key
        // 2. sender
        // 3. msg
        // 4. send_time = 보낸 시간 2019-07-13,오전/오후,시(00~11),분(00~59)

        // DB에 입력한 값으로 행 추가
        db.execSQL("INSERT INTO Chat_msg (num,room_key, sender, msg, send_time) VALUES(null, '" + room_key + "', '" + sender + "', '" + msg + "', '" + time + "');");
        db.close();
    }



    public String Chat_msg_getResult(String room_key) throws JSONException {
        // 읽기가 가능하게 DB 열기
        SQLiteDatabase db = getReadableDatabase();
        String result = "";

        // JSONArray 생성
        JSONArray chatlist_JsonArray = new JSONArray();

        // DB에 있는 데이터를 쉽게 처리하기 위해 Cursor를 사용하여 테이블에 있는 모든 데이터 출력
        Cursor cursor = db.rawQuery("SELECT * FROM Chat_msg WHERE Room_KEY='" + room_key + "' ;", null);
        while (cursor.moveToNext()) {
//            result +=
//                    "--------------------------------------------------------------------------------" + "\n"
//                            + "번호  : " + cursor.getInt(0) + "\n"
//                            + "방 키 : " + cursor.getString(1) + "\n"
//                            + "전송자 : " + cursor.getString(2) + "\n"
//                            + "메세지 : " + cursor.getString(3) + "\n"
//                            + "보낸시간 : " + cursor.getString(4) + "\n";

            int num = cursor.getInt(0);
            String num_string = String.valueOf(num);

//            Log.e("번호 : ", String.valueOf(num));
//            Log.e("방 키 : ",cursor.getString(1));
//            Log.e("전송자 : ",cursor.getString(2));
//            Log.e("메세지 : ",cursor.getString(3));
//            Log.e("보낸시간 : ",cursor.getString(4));


            // JsonObject 생성
            JSONObject chat_JsonObject = new JSONObject();


            chat_JsonObject.put("번호", num_string);
            chat_JsonObject.put("방_키", cursor.getString(1));
            chat_JsonObject.put("전송자", cursor.getString(2));
            chat_JsonObject.put("메세지", cursor.getString(3));
            chat_JsonObject.put("보낸시간", cursor.getString(4));

            // JsonArray 에 JSONObject 를 집어 넣는다.
            chatlist_JsonArray.put(chat_JsonObject);

        }
        // JsonArray String 화
        String chatlist_JsonArray_String = chatlist_JsonArray.toString();
        return chatlist_JsonArray_String; // 결과로 내보냄
    }








}
