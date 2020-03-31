package com.example.user.androidhive.MainActivity_New_Moim;

/**
 * 신규 모임 데이터 클래스
 */
public class New_Moim_Data {

     String M_no;         // 모임번호
     String Paid_or_Free; // 유료 or 무료
     String Date;         // 날짜
     String Img_Name ;    // 이미지 이름
    String simple_address ;    // 이미지 이름


    private String flowerName; // 카테고리 !!

    //  모임번호, 유무료, 날짜, 이미지파일-이름
    public New_Moim_Data(String M_no, String Paid_or_Free ,String Date, String Img_Name, String simple_address ) {

        this.M_no = M_no;
        this.Paid_or_Free = Paid_or_Free;
        this.Date = Date;
        this.Img_Name = Img_Name;
        this.simple_address = simple_address;

    }

//    public String getFlowerName() {
//        return flowerName;
//    }
//
//    public String getFlowerDescription() {
//        return flowerDescription;
//    }
//
//    public int getFlowerImage() {
//        return flowerImage;
//    }

}
