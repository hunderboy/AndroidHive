package com.example.user.androidhive;

/**
 * Created by user on 2018-03-16.
 */

public class Applied_Moim_List_Spinner_Data {

    public String Moim_num;  // 모임번호
    public String Moim_name; // 모임이름
    public String Moim_image_name; // 모임 이미지 명

    // 생성자
    public Applied_Moim_List_Spinner_Data(String Moim_num, String Moim_name, String Moim_image_name ) {
        this.Moim_num = Moim_num;               // 모임번호
        this.Moim_name = Moim_name;             // 모임이름
        this.Moim_image_name = Moim_image_name; // 모임 이미지 명
    }// 모임


    // 주로 리사이클러뷰가 포함된 화면에서 출력함.
    public String get_Moim_num() { // 모임번호 불러오는 메소드
        return Moim_num;
    }
    public String get_Moim_name() { // 모임이름 불러오는 메소드
        return Moim_name;
    }
    public String get_Moim_image_name() { // 모임 이미지 명 불러오는 메소드
        return Moim_image_name;
    }




}
