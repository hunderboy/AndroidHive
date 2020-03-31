package com.example.user.androidhive;

/**
 * Created by user on 2018-01-06.
 * 검색화면에서 찾은 모임에 대한 데이터 클래스
 */

public class Searched_Moim_Data {

    public String ID;   // 현재 접속자 아이디 - 즐겨찾기 기능을 위해 추가

    public String Number;       // 모임 번호

    public String IMG;          // 이미지 URI

    public String Name;         // 모임명
    public String Moim_Time;    // 모임날짜
    public String address;      // 장소 = 일반주소, 상세주소 아님
    public String Charged;      // 유료,무료

    public String Bookmark_number;      // 유료,무료


    public Searched_Moim_Data(String ID, String Number, String IMG, String Name, String Moim_Time, String address, String Charged, String Bookmark_number) {
        this.ID = ID;

        this.Number = Number;
        this.IMG = IMG;

        this.Name = Name;
        this.Moim_Time = Moim_Time;
        this.address = address;
        this.Charged = Charged;

        this.Bookmark_number = Bookmark_number;

    }// 검색한 모임 데이터



}
