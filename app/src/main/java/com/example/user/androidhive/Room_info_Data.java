package com.example.user.androidhive;

/**
 * Created by user on 2018-03-24.
 */

public class Room_info_Data {

     String room_num_at_server;   // 서버에서 방 번호
     String Moim_title;           // 모임 이름
     String users;                // 참여자 들 아이디
     String users_num;            // 참여자 수
     String Moim_img_name;        // 모임 이미지 파일 이름

    // 방번호, 모임명, 참여자들 , 참여자수 , 이미지 파일
    public Room_info_Data(String room_num_at_server, String Moim_title, String users, String users_num, String Moim_img_name) {
        this.room_num_at_server = room_num_at_server;   // 서버에서 방 번호
        this.Moim_title = Moim_title;   // 모임 이름
        this.users = users;         // 참여자 들 아이디
        this.users_num = users_num; // 참여자 수
        this.Moim_img_name = Moim_img_name; // 모임 이미지 파일 이름

    }// 신청된 모임 데이터

}
