package com.example.user.androidhive.MainActivity_Populer_Moim;

import android.content.Context;
import android.content.Intent;
import android.graphics.Color;
import android.support.annotation.NonNull;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.bumptech.glide.Glide;
import com.example.user.androidhive.R;
import com.example.user.androidhive.Searched_Moim_See_Activity;

import java.util.ArrayList;

public class Populer_moim_GridLayout_Adapter extends RecyclerView.Adapter<Populer_moim_GridLayout_Adapter.ViewHolder>{

    private Context mContext;
    private ArrayList<Populer_Moim_Data> Populer_moim_DataList; // 데이터 클래스 포함 하는 어레이 리스트


    // Provide a suitable constructor (depends on the kind of dataset)
    public Populer_moim_GridLayout_Adapter(Context mContext,ArrayList<Populer_Moim_Data> myDataset) {
        this.mContext = mContext;
        this.Populer_moim_DataList = myDataset;
    }

    public static class ViewHolder extends RecyclerView.ViewHolder {
        public View view;

        // 이미지 뷰
        ImageView M_default_Image; // 모임 기본이미지

        // 텍스트 뷰
        TextView M_no;         // 모임 번호
        TextView Paid_or_Free; // 유료 무료
        TextView Date;         // 모임 진행 날짜 = 두개 합쳐서 사용
        TextView simple_address;// 간단 모임 주소

        TextView M_Category;   // 카테고리..!!


        public ViewHolder(View itemView) {
            super(itemView);
            this.view = itemView;
            // 이미지 뷰
            M_default_Image = (ImageView)itemView.findViewById(R.id.M_default_Image);

            // 텍스트뷰
            M_no = (TextView)itemView.findViewById(R.id.M_no);
            Paid_or_Free = (TextView)itemView.findViewById(R.id.Paid_or_Free);
            Date = (TextView)itemView.findViewById(R.id.Date);
            simple_address = (TextView)itemView.findViewById(R.id.simple_address); // 간단 모임 주소

            M_Category = (TextView)itemView.findViewById(R.id.M_Category);


        }
    }

    @Override
    public Populer_moim_GridLayout_Adapter.ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        // create a new view
        View v = LayoutInflater.from(parent.getContext()).inflate(R.layout.new_moim_grid_item , parent, false);
        // 뷰 홀더에 넣어줄 뷰를 찾는 과정. my_view라는 xml에는 cardview가 들어갈 예정이다.
        Populer_moim_GridLayout_Adapter.ViewHolder vh = new Populer_moim_GridLayout_Adapter.ViewHolder(v);  // 이 view를 넣기위한 ViewHolder를 다음과 같이 선언한 후 넣는다.
        return vh;
    }


    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, final int position) {

        // 안보임
        holder.M_no.setText(Populer_moim_DataList.get(position).M_no);                  // 모임 번호
        // 보임
        holder.Date.setText(Populer_moim_DataList.get(position).Date);                  // 날짜
        holder.simple_address.setText(Populer_moim_DataList.get(position).simple_address); // 간단 모임주소


        // 유료, 무료 설정
        if(Populer_moim_DataList.get(position).Paid_or_Free.equals("유료")){ // 유료 경우
            holder.Paid_or_Free.setText(Populer_moim_DataList.get(position).Paid_or_Free);  // 유료 or 무료
            holder.Paid_or_Free.setTextColor(Color.RED); // 빨강색
        }
        else { // 무료
            holder.Paid_or_Free.setText(Populer_moim_DataList.get(position).Paid_or_Free);  // 유료 or 무료
            holder.Paid_or_Free.setTextColor(Color.GREEN); // 초록색
        }


        // 이미지 파일 이름 가져오기
        String img_NAME = Populer_moim_DataList.get(position).Img_Name;

        // 이미지 설정
        Glide.with(holder.M_default_Image.getContext())
                .load("http://49.247.208.191/AndroidHive/Moim_default_IMG/"+img_NAME)
                .placeholder(R.drawable.noimage)
                .into(holder.M_default_Image);


        holder.view.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
//                Toast.makeText(view.getContext(),position+" 번 아이템 선택 됨.",Toast.LENGTH_SHORT).show();

                Intent mIntent = new Intent(mContext, Searched_Moim_See_Activity.class);
                mIntent.putExtra("NUMBER",Populer_moim_DataList.get(position).M_no );
                mContext.startActivity(mIntent);


            }// 아이템 클릭 리스너 끝
        });

    }// onBindViewHolder 끝


    @Override
    public int getItemCount() {
        return Populer_moim_DataList.size();
    }




}
