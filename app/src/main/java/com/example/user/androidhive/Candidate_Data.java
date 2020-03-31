package com.example.user.androidhive;

/**
 * Created by user on 2018-04-03.
 * 신청자 관리 화면에서
 * 해당 모임 신청자 리스트 관리
 */

public class Candidate_Data {

    String User_ID;                 // 아이디
    String User_Callnum;            // 전화번호
    String User_Apply_State;        // 신청 상태
    String User_Attendance_State;   // 출석 상태
    String User_NAME;               // 신청자 이름

    String Manage_Moim_NUMBER;   // 모임 번호
    String Checking;            // 체크박스 체킹



    /**
     * 1. 아이디 (고유함)
     * 2. 전화번호
     * 3. 참여확정 or 대기자 - 신청 상태
     * 4. 출석일 or 미출석   - 출석 상태
     * 5. 모임 번호
     */
    // 아이디, 전화번호, 신청 상태, 출석 상태, 모임번호, 유저 이름, 체크박스 선택 번호
    public Candidate_Data(String User_ID, String User_Callnum, String User_Apply_State, String User_Attendance_State, String Manage_Moim_NUMBER, String User_NAME, String Checking ) {
        this.User_ID = User_ID;                             // 아이디
        this.User_Callnum = User_Callnum;                   // 전화번호
        this.User_Apply_State = User_Apply_State;           // 신청 상태
        this.User_Attendance_State = User_Attendance_State; // 출석 상태
        this.Manage_Moim_NUMBER = Manage_Moim_NUMBER;       // 모임 번호
        this.User_NAME = User_NAME;                         // 신청자 이름
        this.Checking = Checking;                         // 체크박스 체킹


    }


}
