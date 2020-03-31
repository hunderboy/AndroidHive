package com.example.user.androidhive;

/**
 * Created by user on 2018-01-24.
 */

public class Applied_list_Data {

    public String ID;   // 현재 접속자 아이디 - 즐겨찾기 기능을 위해 추가

    public String Number;       // 모임 번호

    public String IMG;          // 이미지 URI

    public String Name;         // 모임명
    public String Moim_Time;    // 모임날짜
    public String address;      // 장소 = 일반주소, 상세주소 아님

    public String Application_status;  // 신청자 참여 상태 = 참여확정 or 대기자

    public String Charged;  // 유료,무료
    public String Available_Seats;  // 신청가능인원 수

    public String Apply_DATE;  // 신청일



    //    String ID, String Number, String IMG, String Name, String Moim_Time, String address, String Application_status
    // 아이디, 모임번호, 기본이미지, 모임명, 모임시간, 주소, 접수상태, 유료 or 무료, 신청가능한 인원
    public Applied_list_Data(String ID, String Number, String IMG, String Name, String Moim_Time, String address, String Application_status, String Charged, String Available_Seats, String Apply_DATE) {
        this.ID = ID;
        this.Number = Number;

        this.IMG = IMG;
        this.Name = Name;
        this.Moim_Time = Moim_Time;
        this.address = address;

        this.Application_status = Application_status;   // 신청 상태

        this.Charged = Charged;                 // 유료,무료
        this.Available_Seats = Available_Seats; // 신청가능인원

        this.Apply_DATE = Apply_DATE;                 // 신청일

    }// 신청된 모임 데이터



}
