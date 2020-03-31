package com.example.user.androidhive;

import android.content.Intent;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.bumptech.glide.Glide;

import java.util.ArrayList;

import jp.wasabeef.glide.transformations.CropCircleTransformation;

import static com.example.user.androidhive.MainActivity.receive0;

/**
 * Created by user on 2018-03-23.
 */

public class Room_List_Adapter extends RecyclerView.Adapter<Room_List_Adapter.ViewHolder>  {

    private ArrayList<Room_info_Data> mDataset;   // 모임데이터를 가질 어레이리스트를 선언


    // 데이터 레이아웃 에 존재하는 위젯들을 바인딩 한다.
    public static class ViewHolder extends RecyclerView.ViewHolder implements View.OnClickListener{
        // each data item is just a string in this case

        // 표현할 이미지 뷰와 텍스트 객체 선언
         TextView room_num_at_server; // 서버에서 방 키    - 숨김
         TextView Moim_title;         // 모임 이름            - 드러냄
         TextView users;              // 참여자 들 아이디    - 드러냄
         TextView users_num;          // 참여자 수            - 드러냄
         TextView Chat_msg;           // 채팅 메세지  - 채팅을 했을 경우에만 드러남

        // 이미지뷰 객체 생성
         ImageView Moim_ImageView;// 모임 기본이미지뷰
         ImageView People_Image; // 사람들 나타내는 이미지

        // 뷰홀더 메소드 생성
        public ViewHolder(View view) {
            super(view);

            // 텍스트 뷰
            room_num_at_server = (TextView)view.findViewById(R.id.Room_Num_at_server); // 서버에서 방 키    - 숨김
            Moim_title = (TextView)view.findViewById(R.id.Moim_name_text);  // 모임 이름            - 드러냄
            users = (TextView)view.findViewById(R.id.Users_text);           // 참여자 들 아이디    - 드러냄
            users_num = (TextView)view.findViewById(R.id.people_num);       // 참여자 수            - 드러냄
            Chat_msg = (TextView)view.findViewById(R.id.Chat_msg);   // 채팅 메세지  - 채팅을 했을 경우에만 드러남

            // 이미지 뷰
            Moim_ImageView = (ImageView)view.findViewById(R.id.moim_image); // 모임 기본이미지뷰
            People_Image = (ImageView)view.findViewById(R.id.people_img);   // 사람들 나타내는 이미지 - 여러 사람일경우에만 나타남 1:1 채팅일 때는 나타나지 않음

            view.setOnClickListener(this);  // 아이템 클릭 리스너 등록

        }// 뷰홀더 메소드 끝

        // 리스트 아이템 클릭 시 나타나는 이벤트
        @Override
        public void onClick(View view) {

            // 채팅 화면으로 이동하면서 서버 방번호도 같이 넘김
            Intent intent = new Intent(view.getContext(), ServiceChat_Activity.class);    // 넘어갈 ServiceChat_Activity 화면 = 모임확인화면

            String Room_Key = room_num_at_server.getText().toString();

            // intent 객체에 데이터를 실어서 보내기
            // 리스트뷰 클릭시 인텐트 (Intent) 생성하고 position 값을 이용하여 인텐트로 넘길값들을 넘긴다.
            intent.putExtra("Room_Key", Room_Key); // 서버 방 키

            view.getContext().startActivity(intent);    // 현재화면 제어권자에서 startActivity 시켜라 라는 말이다.
        }

    }// 뷰홀더 클래스 끝



    // 데이터 세트 연결
    public Room_List_Adapter(ArrayList<Room_info_Data> myDataset) {
        mDataset = myDataset;
    }

        @Override
        public Room_List_Adapter.ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
            // create a new view
            View v = LayoutInflater.from(parent.getContext()).inflate(R.layout.room_list_item, parent, false);  // room_list_item 뷰 연결
            ViewHolder vh = new ViewHolder(v);  // 이 view를 넣기위한 ViewHolder를 다음과 같이 선언한 후 넣는다.
            return vh;
        }// onCreateViewHolder 끝




    // 위젯과 데이터 연결
    @Override
    public void onBindViewHolder(ViewHolder holder, final int position) {

        String users_text = "";  // users 에 설정할 텍스트

        holder.room_num_at_server.setText(mDataset.get(position).room_num_at_server); // 서버에서의 방 번호 - 숨김
        holder.Moim_title.setText(mDataset.get(position).Moim_title); // 모임명

        // 유저 아이디들
        String user_ids = mDataset.get(position).users; // ex) masury2011,ki1234,sudo99, 꼴의 형태

        // 아이디 별로 배열에 담는다
        String[] user_ids_arr = user_ids.split(",");    // 참여 아이디 들을 (,) 로 나눈다.


        // 방 참여자 수
        int user_numbers = Integer.parseInt(mDataset.get(position).users_num);  // 정수형으로 참여자수 변경
        // 만약 챰여자 수가 3 이상이라면
        if(user_numbers >= 3){
            // 사람이미지 표시하고, 옆에 참여사람수 숫자 나타내고
            // 참여자 아이디 3개 연속해서 표시하고,
            for(int i=0; i < 3; i++){   // 3번 돌림
                users_text = users_text + user_ids_arr[i] + ",";    // 맨앞의 3개의 아이디만 연결함
            }
            users_text = users_text + "...";
            holder.users.setText(users_text);   // 위젯에 아이디들 데이터 설정

            users_text = "";// 초기화
            Log.e("초기화?? users_text = ",users_text);

            holder.users_num.setText(mDataset.get(position).users_num);    // 참여자 수 설정
            holder.users_num.setVisibility(View.VISIBLE);       // 보이게 설정
            holder.People_Image.setVisibility(View.VISIBLE);    // 보이게 설정

            /**
             * 참여자수 3 이상일 경우만 사람수, 아이디들
             * 참여자들 이미지 보여줌..
             */
        }
        else {  // 참여자 수가 2 이하 = 채팅방에 둘만 존재
            for(int i=0; i < 2; i++){   // 2번 돌림
                if(receive0.equals(user_ids_arr[i])){   // 현재 접속자 아이디와 같다면
                    Log.e("아이디 동일 ","");
                }
                else {
                    users_text = user_ids_arr[i];   // 아이디 텍스트 위젯에 설정할 아이디 저장
                    Log.e("아이디 다름, 표시할 아이디= ",users_text);
                    holder.users_num.setVisibility(View.INVISIBLE);       // 사람 수 안보이게 설정
                    holder.People_Image.setVisibility(View.INVISIBLE);    // People_Image 안보이게 설정
                }
            }// 반복문 종료

            holder.users.setText(users_text);   // 위젯에 아이디들 데이터 설정
        }// 조건문 끝


        /**
         * 채팅 메세지 설정
         * 해당 방번호에 따라서 방번호에 가장 마지막 채팅을 표현한다.
         */
        // 채팅 메세지
//        holder.Chat_msg.setText(mDataset.get(position).text2);    // 채팅 메세지  - 채팅을 했을 경우에만 드러남




        /** 이미지 설정을 위한 코드 - 버벅거림이 있다 -> 글라이드 + 리사이징 */
        String img_NAME = mDataset.get(position).Moim_img_name;   // 포지션 별 String 형태로 된 이미지 Uri를 가져온다.
        // 모임 기본이미지 설정
        Glide.with(holder.Moim_ImageView.getContext())
                .load("http://49.247.208.191/AndroidHive/Moim_default_IMG/"+img_NAME)
                .placeholder(R.drawable.noimage)
                .centerCrop()
                .crossFade()
                .bitmapTransform(new CropCircleTransformation(holder.Moim_ImageView.getContext()))   // 원형크롭 라이브러리 사용함 = compile 'jp.wasabeef:glide-transformations:2.0.1'
                .override(100,100)  // 리소스의 이미지를 강제적으로 작게 줄여서 보여주려고 할대 사용
                .into(holder.Moim_ImageView);


    }// onBindViewHolder 끝



    @Override
    public int getItemCount() {
        return mDataset.size();
    }

}// 코드 끝
