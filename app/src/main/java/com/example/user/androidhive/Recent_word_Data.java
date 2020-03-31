package com.example.user.androidhive;


/**
 * Created by user on 2018-01-09.
    최근 검색어 데이터클래스
 */
public class Recent_word_Data {

    public String ID;
    public String word;


    public Recent_word_Data(String ID, String word) {
        this.ID = ID;
        this.word = word;
    }// 검색한 모임 데이터


    // 주로 리사이클러뷰가 포함된 화면에서 출력함.
    public String getWord() {   // 아이템 이름 불러오는 메소드
        return word;
    }

    public String getID() {   // 아이템에 있는 표현되지는 않지만 존재 하는 ID (공통)
        return ID;
    }

}
