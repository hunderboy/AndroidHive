package com.example.user.androidhive;


import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import android.util.Log;

/**
 * 채팅 데이터 모두 저장
 * 채팅 저장 테이블에 저장
 */
public class SQLite_Save_Chat extends SQLiteOpenHelper {

    // DBHelper 생성자로 관리할 DB 이름과 버전 정보를 받음
    public SQLite_Save_Chat(Context context, String name, SQLiteDatabase.CursorFactory factory, int version) {
        super(context,name, factory, version); // "/mnt/sdcard/"+
    }


    /**
     *   SQLite_Save_Chat 객체를 생성 하면 아래에 해당하는 onCreate 함수가 실행된다.
     *   DB를 새로 생성할 때 호출되는 함수
     */
    @Override
    public void onCreate(SQLiteDatabase db) {
        db.execSQL("CREATE TABLE Chat_msg (num INTEGER NOT NULL PRIMARY KEY AUTOINCREMENT, room_key TEXT NOT NULL, sender TEXT NOT NULL, msg TEXT NOT NULL, send_time TEXT );");

        Log.e("SQLite 의 onCreate ","들어감");

//  CREATE TABLE `Chat_msg` (
//     `num` INTEGER NOT NULL PRIMARY KEY AUTOINCREMENT,
//     `room_key` TEXT NOT NULL,
//     `sender` TEXT NOT NULL,
//     `msg` TEXT NOT NULL,
//     `send_time` TEXT NOT NULL,
//  );


    }

    // DB 업그레이드를 위해 버전이 변경될 때 호출되는 함수
    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {    }



    /**
     * 채팅 데이터 테이블
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
        db.execSQL("INSERT INTO Chat_msg (room_key, sender, msg, send_time) VALUES('" + room_key + "', " + sender + ", '" + msg + "', '" + time + "');");
        db.close();
    }

    // sqlite 에서 결과값 출력
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


}
