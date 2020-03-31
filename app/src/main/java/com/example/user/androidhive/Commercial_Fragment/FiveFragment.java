package com.example.user.androidhive.Commercial_Fragment;

import android.content.Intent;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.RelativeLayout;

import com.example.user.androidhive.R;
import com.example.user.androidhive.Searched_Moim_See_Activity;

public class FiveFragment extends android.support.v4.app.Fragment
{
    public FiveFragment()
    {
    }


    @Override
    public void onCreate(Bundle savedInstanceState)
    {
        super.onCreate(savedInstanceState);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState)
    {
        RelativeLayout layout = (RelativeLayout) inflater.inflate(R.layout.fragment_five, container, false);


        layout.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
//                Toast.makeText(getContext(), "5번 프래그먼트 선택됨", Toast.LENGTH_SHORT).show(); // 불안 이야기

                Intent mIntent = new Intent(getContext(), Searched_Moim_See_Activity.class);
                mIntent.putExtra("NUMBER","48" );
                getContext().startActivity(mIntent);



            }
        });


        return layout;
    }
}
