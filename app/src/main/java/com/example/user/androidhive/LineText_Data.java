package com.example.user.androidhive;

/**
 * Created by user on 2018-03-06.
 * ??? 님이 접속 하셨습니다.
 */

public class LineText_Data {

    public String CenterText; // 라인의 중앙에 표시될 텍스트

    public LineText_Data(String CenterText ) {
        this.CenterText = CenterText;   // 채팅 메세지
    }

    // 주로 리사이클러뷰가 포함된 화면에서 출력함.
    public String getcenterText() {
        return CenterText;
    }

}
