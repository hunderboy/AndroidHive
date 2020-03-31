package com.example.user.androidhive;

/**
 * Created by user on 2018-03-07.
 */

public class My_Chat_Data {

    public String My_send_time; // 내가 보낸 시간
    public String MyText;       // 내가 보낸 메세지

    public My_Chat_Data(String MyText, String My_send_time ) {
        this.MyText = MyText;   // 채팅 메세지
        this.My_send_time = My_send_time;   // 내가 채팅 보낸 시간
    }

    // 주로 리사이클러뷰가 포함된 화면에서 출력함.
    public String getMyText() {
        return MyText;
    }
    // 주로 리사이클러뷰가 포함된 화면에서 출력함.
    public String getMySend_Time() {
        return My_send_time;
    }

}
