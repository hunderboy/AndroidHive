package com.example.user.androidhive;

/**
 * Created by user on 2018-03-03.
 * 상대방 채팅과
 * 내가 전송하는 채팅
 */

public class ChatHistory_Data {

    public String ID;           // 상대방 아이디
    public String Chat_Massage; // 채팅 메세지
    public String your_time; // 상대방이 보낸 채팅 시간

    public ChatHistory_Data(String ID, String Chat_Massage, String your_time) {
        this.ID = ID;                       // 아이디
        this.Chat_Massage = Chat_Massage;   // 채팅 메세지
        this.your_time = your_time;         // 상대방이 보낸 채팅 시간
    }// 검색한 모임 데이터


    // 주로 리사이클러뷰가 포함된 화면에서 출력함.
    public String getID() {   // 아이템에 있는 표현되지는 않지만 존재 하는 ID (공통)
        return ID;
    }
    public String getChat_Massage() {   // 아이템 이름 불러오는 메소드
        return Chat_Massage;
    }
    public String getChat_yourtime() {   // 아이템 이름 불러오는 메소드
        return your_time;
    }
}
