package com.example.user.androidhive;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.TextView;

import com.bumptech.glide.Glide;

import java.util.List;

import jp.wasabeef.glide.transformations.CropCircleTransformation;

/**
 * Created by user on 2017-12-16.
 */

public class AdapterSpinner1 extends BaseAdapter {

    Context context;         // 화면 제어권자
    List<Applied_Moim_List_Spinner_Data> Date_Set;       // 데이터 = 어레이 리스트 -> 데이터 클래스로 변경 가능한가?
    LayoutInflater inflater; // 레이아웃 inflater Date_Set

    // 생성자
    public AdapterSpinner1(Context context, List<Applied_Moim_List_Spinner_Data> Date_Set){ // 입력 데이터 (화면제어권자, 데이터 어레이리스트)
        this.context = context;
        this.Date_Set = Date_Set;
        inflater = (LayoutInflater)context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
    }

    @Override
    public int getCount() { // 아이템 수 = int 형 리턴값
        if(Date_Set!=null) return Date_Set.size();  // Date_Set = null 아니라면 데이터 size 리턴
        else return 0;  // 그렇지 않으면 int 0 리턴
    }

    /**
     * getView
     * 리스트 하나를 선택했을때 보여 주는 View
     */
    @Override
    public View getView(int position, View convertView, ViewGroup parent) {

        ImageView Moim_image; // 모임에 따라 달라지는 이미지 뷰

        if(convertView==null) {
            convertView = inflater.inflate(R.layout.spinner_spinner1_normal, parent, false);    // 스피너 레이아웃 설정 = spinner_spinner1_normal
        }

        if(Date_Set!=null){ //  Date_Set = null 아니라면
            // 모임명 설정
            String text = Date_Set.get(position).Moim_name;   // 데이터의 해당 포지션의 모임명 을 불러온다.
            ((TextView)convertView.findViewById(R.id.spinnerText)).setText(text);
            // 이미지 설정
            String m_IMG_Text = Date_Set.get(position).Moim_image_name;   // 데이터의 해당 포지션의 모임명 을 불러온다.

            Moim_image = (ImageView)convertView.findViewById(R.id.Moim_IMG);   // 모임 이미지 아이디 설정

            Glide.with(Moim_image.getContext())
                    .load("http://49.247.208.191/AndroidHive/Moim_default_IMG/"+m_IMG_Text)   // 모임이미지 String 으로 연결
                    .centerCrop()
                  .crossFade()
                  .bitmapTransform(new CropCircleTransformation(Moim_image.getContext()))   // 원형크롭 라이브러리 사용함 = compile 'jp.wasabeef:glide-transformations:2.0.1'
                  .override(80,80)  // 리소스의 이미지를 강제적으로 작게 줄여서 보여주려고 할대 사용
                  .into(Moim_image);
        }


        return convertView;
    }

    /**
     * getDropDownView
     * 리스트 전체를 출력할 때 보여 주는 View
     */
    @Override
    public View getDropDownView(int position, View convertView, ViewGroup parent) {

        ImageView Moim_image; // 모임에 따라 달라지는 이미지 뷰

        if(convertView==null){
            convertView = inflater.inflate(R.layout.spinner_spinner1_dropdown, parent, false);  // 드롭다운 스피너 레이아웃 설정
        }

        // 모임명 설정
        String text = Date_Set.get(position).Moim_name;   // 데이터의 해당 포지션의 모임명 을 불러온다.
        ((TextView)convertView.findViewById(R.id.spinnerText)).setText(text);
        // 이미지 설정
        String m_IMG_Text = Date_Set.get(position).Moim_image_name;   // 데이터의 해당 포지션의 모임명 을 불러온다.

        Moim_image = (ImageView)convertView.findViewById(R.id.Moim_IMG);   // 모임 이미지 아이디 설정

        Glide.with(Moim_image.getContext())
                .load("http://49.247.208.191/AndroidHive/Moim_default_IMG/"+m_IMG_Text)   // 모임이미지 String 으로 연결
                .centerCrop()
                .crossFade()
                .bitmapTransform(new CropCircleTransformation(Moim_image.getContext()))   // 원형크롭 라이브러리 사용함 = compile 'jp.wasabeef:glide-transformations:2.0.1'
                .override(100,100)  // 리소스의 이미지를 강제적으로 작게 줄여서 보여주려고 할대 사용
                .into(Moim_image);

        //추후에 수정 다이얼로그 띄우기
//        AlertDialog.Builder builder = new AlertDialog.Builder(context);
//        builder.setTitle("모임을 변경 하시겠습니까?");
//        builder.setMessage("초기화 합니다.");
//
//        builder.setPositiveButton("취소",
//                new DialogInterface.OnClickListener() {
//                    public void onClick(DialogInterface dialog, int which) {
//                        // 취소 버튼의 경우 아무일도 일어나지 않는다
//
//                    }
//                });
//        builder.setNeutralButton("확인",
//                new DialogInterface.OnClickListener() {
//                    public void onClick(DialogInterface dialog, int which) {
//                        //확인버튼
//                    }
//                });
//
//
//        builder.show();
        return convertView;
    }

    @Override
    public Object getItem(int position) {
        return Date_Set.get(position);
    }
    @Override
    public long getItemId(int position) {
        return position;
    }

    /**
     * 액티비티에서 데이터를 확인하기 위해서 만들어 놓은 메소드
     */
    // 액티비티에서 해당 모임의 번호 확인
    public String get_Item_num(int position) {
        return Date_Set.get(position).Moim_num;
    }
    // 액티비티에서 해당 모임의 모임명 확인
    public String get_Selected_moim_Name(int position) {
        return Date_Set.get(position).Moim_name;
    }
    public String get_Selected_moim_IMG_Name(int position) {
        return Date_Set.get(position).Moim_image_name;
    }



}
