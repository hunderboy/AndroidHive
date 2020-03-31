package com.example.user.androidhive;

/**
 * Created by user on 2017-12-23.
 * 개설된 모임 리스트에 대한 데이터 정보들을 담고있다.
 */
public class Opened_Moim_Data {

    public String text;     // 모임명
    public String text2;    // 총인원

    public String img;  // 이미지 URI

    public String Number;    // 모임 번호
    public String Creator;    // 개설자


    public String Place;    // 위치데이터


//    public Opened_Moim_Data(String text, String img, String text2) {
//        this.text = text;
//        this.img = img;
//        this.text2 = text2;
//        Log.e("데이터 전달됨","");
//    }// 모임 데이터

    public Opened_Moim_Data(String text, String text2, String img, String Number, String Creator, String Place) {
        this.text = text;
        this.text2 = text2;
        this.img = img;

        this.Number = Number;
        this.Creator = Creator;

        this.Place = Place;

    }// 모임 데이터

}