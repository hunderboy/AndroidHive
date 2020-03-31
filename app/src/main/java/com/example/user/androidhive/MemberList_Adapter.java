package com.example.user.androidhive;

import android.content.Context;
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
 * Created by user on 2018-03-17.
 */

public class MemberList_Adapter extends RecyclerView.Adapter<MemberList_Adapter.ViewHolder>{
    Context context;
    private ArrayList<Applied_User_list_Data> Member_List_Dataset;   // 모임데이터를 가질 어레이리스트를 선언

    String Participants_at_MemberList_Adapter = receive0 + ","; // 어댑터에서 방 참여자들

    // 데이터 레이아웃 에 존재하는 위젯들을 바인딩 한다.
    public class ViewHolder extends RecyclerView.ViewHolder{
        public View view;   // 뷰 바인딩 메소드에서 아이템 클릭 리스너를 실행 시키기 위해서 필요한 뷰 객체

        // 표현할 이미지 뷰와 텍스트 객체 선언
        TextView User_and_id_TEXT;  // 이름(아이디)
        // 이미지뷰
        ImageView User_profile_img; // 유저 프로필 이미지
        ImageView Check_Box_View;   // 리스트 체크박스 이미지


        // 뷰홀더 메소드 생성
        public ViewHolder(View view) {
            super(view);
            this.view = view;

            // 텍스트 뷰
            User_and_id_TEXT = (TextView)view.findViewById(R.id.User_and_id_TEXT);
            // 이미지 뷰
            User_profile_img = (ImageView)view.findViewById(R.id.User_profile_img);
            Check_Box_View = (ImageView)view.findViewById(R.id.Check_Box_View);

        }// ViewHolder 끝

    }// 뷰홀더 끝



    private OnitemClick mCallback;  // onClick 콜백 객체

    // 어댑터 생성자
    // Provide a suitable constructor (depends on the kind of dataset)
    public MemberList_Adapter(ArrayList<Applied_User_list_Data> myDataset, OnitemClick listener) {    // , OnitemClick listener
        Member_List_Dataset = myDataset;
        this.mCallback = listener;
    }

    // Create new views (invoked by the layout manager)
    @Override
    public MemberList_Adapter.ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        // create a new view
        View v = LayoutInflater.from(parent.getContext()).inflate(R.layout.member_list_item, parent, false);
        // 뷰 홀더에 넣어줄 뷰를 찾는 과정. = recent_word_list.xml 이 아이템의 뷰 이다.
        ViewHolder vh = new ViewHolder(v);  // 이 view를 넣기위한 ViewHolder를 다음과 같이 선언한 후 넣는다.
        return vh;
    }

    // 뷰 바인딩
    @Override
    public void onBindViewHolder(final ViewHolder holder, final int position) {

        /** 처음 시작할때 최초 실행 */
        String name = Member_List_Dataset.get(position).User_name;
        final String user_id = Member_List_Dataset.get(position).User_ID;

        String name_and_id = name+"("+user_id+")";   // ex: 이름(아이디)
        // 텍스트 세팅
        holder.User_and_id_TEXT.setText(name_and_id);

        // 이미지 세팅
        Glide.with(holder.User_profile_img.getContext())
                .load(R.drawable.default_image) // 기본 이미지
                .centerCrop()
                .crossFade()
                .bitmapTransform(new CropCircleTransformation(holder.User_profile_img.getContext()))
                .override(100,100)  // 리소스의 이미지를 강제적으로 작게 줄여서 보여주려고 할대 사용
                .into(holder.User_profile_img);

        String Checked_NUM = Member_List_Dataset.get(position).Checked_NUM; // 체크 번호

        Log.e("체크번호 확인 = ",Checked_NUM);
        if(Checked_NUM.equals("0")){
            holder.Check_Box_View.setImageResource(R.drawable.plus_box);   // 체크박스 기본 설정
        }

        // 모임에 따라서 신청자 리스트가 계속 변경 될때마다, onBindViewHolder 가 실행된다.
        // 마지막 onBindViewHolder 가 실행될때 까지 사용자는 클릭이벤트가 발생하지 않음.
        Participants_at_MemberList_Adapter = receive0+",";
        // 그런데 이 부분에서 신청자가 많아서 리스트가 길어 페이지를 넘어갈 경우에도 초기 값을 유지 할것인가? 에 대해서는 아직 테스트 안함.. = 해야됨!!




        /**
         * 아이템 클릭 리스너
            - 체크박스
            - 체크박스 체크 유무에 따른 해당 아이디 추가 삭제 - 콜백 함수로 넘겨 줄까??
            -
         */
        holder.view.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                if( Member_List_Dataset.get(position).Checked_NUM.equals("1") ){
                // 체크박스가 선택 되어있다면 ---------------------------------------------------------------------------------------------------------------------------------------------------------------------------
                    Member_List_Dataset.get(position).Checked_NUM = "0";    // "선택해제" 상태로 변경.
                    // 선택 해제 상태 이므로 체크 박스 이미지를 + 버튼 형태로 변환한다.
                    holder.Check_Box_View.setImageResource(R.drawable.plus_box);    // 플러스 이미지 설정


                    int num = -1;   // -1 화면에 전송

                    /** 참여자 리스트에서 제거 과정 !! */
                    String[] user_list_arr = Participants_at_MemberList_Adapter.split(","); // 참여리스트 를 분리자(,) 로 나누어 String 배열 user_list_arr 에 따로 담는다.

                    // 반복문 통해서 일치하는 아이디 검색
                    for(int i=0 ; i < user_list_arr.length ; i++){  // user_list_arr의 길이. 만약 3 이면 0.1.2 총 3번 돈다
                        if( user_list_arr[i].equals(user_id) ){
                            // 아이디가 검색 되면 - 아이디 데이터 삭제
                            user_list_arr[i] = "아이디 삭제";
                            Log.e("user_list_arr[i] 삭제 = ",user_list_arr[i]);
                        }
                    }// 중복 아이디 삭제 반복문 끝

                    Participants_at_MemberList_Adapter = ""; // 초기화

                    for(int i=0 ; i < user_list_arr.length ; i++){  // user_list_arr의 길이. 만약 3 이면 0.1.2 총 3번 돈다
                        if( user_list_arr[i].equals("아이디 삭제") ){  // 만약 아이디가 비어있으면
                            // 패스 한다
                        }
                        else{ // 배열의 값이 비어있지 않으면 합치기 진행
                            Participants_at_MemberList_Adapter = Participants_at_MemberList_Adapter + user_list_arr[i] + ",";   // 아이디1,아이디2,아이디3, = 형태로 저장
                        }
                    }// 참여 아이디 재설정 반복문 끝

                    // Participants_at_MemberList_Adapter = EX) masury2011,sudo888,ki1234, 꼴의 형태로 데이터가 완성 되고, 이것을 콜백 함수로 액티비티에 전송한다.
                    mCallback.onClick(num,Participants_at_MemberList_Adapter); // 콜백 함수 액티비티에 숫자 전달

                    Member_List_Dataset.get(position).Checked_NUM = "0";
                    Log.e(position+" 번째 포지션의 체크번호 = ",Member_List_Dataset.get(position).Checked_NUM);

                }
                // 체크박스가 선택되어 있지 않다면 -----------------------------------------------------------------------------------------------------------------------------------------------------------------
                else{
                    Member_List_Dataset.get(position).Checked_NUM = "1";    // "선택해제" 상태로 변경.
                    // 선택 해제 상태 이므로 체크 박스 이미지를 + 버튼 형태로 변환한다.
                    holder.Check_Box_View.setImageResource(R.drawable.check_box);    // 플러스 이미지 설정

                    int num = 1; // +1 화면에 전송

                    Log.e("유저 아이디  = ",user_id);

                    Participants_at_MemberList_Adapter = Participants_at_MemberList_Adapter + user_id +","; // 방 참여자 데이터

                    mCallback.onClick(num,Participants_at_MemberList_Adapter);  // 콜백 함수 액티비티 전달

                    Member_List_Dataset.get(position).Checked_NUM = "1";
                    Log.e(position+" 번째 포지션의 체크번호 = ",Member_List_Dataset.get(position).Checked_NUM);

                }// 조건문 끝
            }// 아이템 클릭 리스너 끝
        });



    }// onBindViewHolder 끝


    // Return the size of your dataset (invoked by the layout manager)
    @Override
    public int getItemCount() {
        return Member_List_Dataset.size();
    }

}// 코드 끝
