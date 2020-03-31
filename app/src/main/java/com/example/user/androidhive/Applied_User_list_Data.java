package com.example.user.androidhive;

/**
 * Created by user on 2018-03-16.
 */

public class Applied_User_list_Data {

    public String User_name; // 신청자 이름
    public String User_ID;   // 모임 아이디
    public String Checked_NUM; // 체크박스 체크 상태 확인용


    public String User_IMG; // 유저 이미지 명


    // 생성자
    public Applied_User_list_Data(String User_name, String User_ID, String Checked_NUM ) { //  String User_IMG
        this.User_name = User_name;   // 신청자 이름
        this.User_ID = User_ID;       // 모임 아이디
        this.Checked_NUM = Checked_NUM;  // 체크박스 체크 상태 확인용

//        this.User_IMG = User_IMG;     // 유저 이미지 명
    }


}



