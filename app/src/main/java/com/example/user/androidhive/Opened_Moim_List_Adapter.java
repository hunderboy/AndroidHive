package com.example.user.androidhive;

import android.app.Activity;
import android.content.Intent;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;

import com.bumptech.glide.Glide;
import com.google.zxing.integration.android.IntentIntegrator;

import java.util.ArrayList;

/**
 * Created by user on 2017-12-23.
 * 개설된 모임 리스트 화면에서 리사이클러뷰에 데이터 연결
 */
public class Opened_Moim_List_Adapter extends RecyclerView.Adapter<Opened_Moim_List_Adapter.ViewHolder> {

    private ArrayList<Opened_Moim_Data> mDataset;   // 모임데이터를 가질 어레이리스트를 선언

    //qr code scanner object
    private static IntentIntegrator qrScan;


    static String QR_Moim_NUMBER;// static 변수이며, QR 코드 촬영후 받아올 모임번호와 비교할 변수임.
    // 해당 모임의 QR코드인지 확인하기 위해서 = QR 코드 아무거나 찍으면 안되니까
    // QR_Moim_NUMBER 변수는 Make_list_Activity 의 onActivityResult 에서
    // QR 코드 촬영후 데이터 받아오는 과정 중에서 해당 모임의 번호를 가지고 있는지 비교하기 위해 사용된다.

    // 데이터 레이아웃 에 존재하는 위젯들을 바인딩 한다.
    public static class ViewHolder extends RecyclerView.ViewHolder {
        // each data item is just a string in this case

        // 표현할 이미지 뷰와 텍스트 객체 선언
        public TextView mTextView;
        public TextView mTextView2;

        // 이미지뷰 객체 생성
        public ImageView mImageView;

        // 버튼 객체 생성
        public Button QR_attendance_button;     // QR 출석체크 버튼 - 스캐너 켜야됨
        public Button People_management_button; // 신청자 관리 버튼

        // 뷰홀더 메소드 생성
        public ViewHolder(View view) {
            super(view);
            // 텍스트 뷰
            mTextView = (TextView)view.findViewById(R.id.Moim_name);
            mTextView2 = (TextView)view.findViewById(R.id.Total_people);

            // 이미지 뷰
            mImageView = (ImageView)view.findViewById(R.id.default_image);

            //intializing scan object
            qrScan = new IntentIntegrator((Activity) view.getContext());    // 개설된 모임 리스트가 있는 액티비티에서 객체 생성

            // QR 출석체크 버튼 객체 생성
            QR_attendance_button = (Button)view.findViewById(R.id.QR_attendance_button);
            // 신청자 관리 버튼 객체 생성
            People_management_button = (Button)view.findViewById(R.id.People_management_button);


        }// 뷰홀더 메소드 끝
    }// 뷰홀더 클래스 끝

    // Provide a suitable constructor (depends on the kind of dataset)
    public Opened_Moim_List_Adapter(ArrayList<Opened_Moim_Data> myDataset) {
        mDataset = myDataset;
    }

    // Create new views (invoked by the layout manager)
    @Override
    public Opened_Moim_List_Adapter.ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        // create a new view
        View v = LayoutInflater.from(parent.getContext()).inflate(R.layout.opened_moim_list2, parent, false);
        // 뷰 홀더에 넣어줄 뷰를 찾는 과정. my_view라는 xml에는 cardview가 들어갈 예정이다.

        // set the view's size, margins, paddings and layout parameters
        ViewHolder vh = new ViewHolder(v);  // 이 view를 넣기위한 ViewHolder를 다음과 같이 선언한 후 넣는다.

        return vh;
    }

    // Replace the contents of a view (invoked by the layout manager)
    @Override
    public void onBindViewHolder(ViewHolder holder, final int position) {
        // - get element from your dataset at this position
        // - replace the contents of the view with that element
        holder.mTextView.setText(mDataset.get(position).text);
        holder.mTextView2.setText(mDataset.get(position).text2);

        /**
         * 이미지 설정을 위한 코드 - 버벅거림이 있다 -> 글라이드 + 리사이징
         */
        final String img_NAME = mDataset.get(position).img;   // 포지션 별 String 형태로 된 이미지 Uri를 가져온다.

        Glide.with(holder.mImageView.getContext())
                .load("http://49.247.208.191/AndroidHive/Moim_default_IMG/"+img_NAME)
                .placeholder(R.drawable.noimage)
                .into(holder.mImageView);

        // QR 출석체크 버튼 클릭 이벤트설정
        holder.QR_attendance_button.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                QR_Moim_NUMBER = mDataset.get(position).Number.toString();    // 클릭한 아이템의 모임번호 설정,

                //scan option
                qrScan.setPrompt("Scanning...");
                //qrScan.setOrientationLocked(false);
                qrScan.initiateScan();
            }
        });

        // 신청자 관리 버튼 클릭 이벤트설정
        holder.People_management_button.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                QR_Moim_NUMBER = mDataset.get(position).Number.toString();    // 클릭한 아이템의 모임번호 설정,
                Log.e("신청자 관리 버튼 모임 번호 =",QR_Moim_NUMBER);

                Intent intent = new Intent( view.getContext(), Applicants_management_Activity.class);    // 다음넘어갈 화면

                // intent 객체에 데이터를 실어서 보내기
                // 리스트뷰 클릭시 인텐트 (Intent) 생성하고 position 값을 이용하여 인텐트로 넘길값들을 넘긴다.
                intent.putExtra("management_Moim_NUMBER", QR_Moim_NUMBER);      // 모임번호 넘기기
                Log.e("관리 화면으로 넘긴 번호 = "," "+ QR_Moim_NUMBER);

                view.getContext().startActivity(intent);    // 신청자 관리 화면 으로 이동
            }
        });

    }// onBindViewHolder 끝



    // Return the size of your dataset (invoked by the layout manager)
    @Override
    public int getItemCount() {
        return mDataset.size();
    }

}// 코드 끝







//        getItemViewType(mDataset.get(position))

//        final Bitmap[] bitmap = new Bitmap[1];
//
//        // 안드로이드에서 네트워크와 관련된 작업을 할 때,
//        // 반드시 메인 Thread가 아닌 별도의 작업 Thread를 생성하여 작업해야 한다.
//        Thread mThread = new Thread() {
//            @Override
//            public void run() {
//                try {
//                    URL url = new URL("http://49.247.208.191/AndroidHive/Moim_default_IMG/"+img_NAME);
//
//
//                    // Web에서 이미지를 가져온 뒤
//                    // ImageView에 지정할 Bitmap을 만든다
//                    HttpURLConnection conn = (HttpURLConnection) url.openConnection();
//                    conn.setDoInput(true); // 서버로 부터 응답 수신
//                    conn.connect();
//
//                    InputStream is = conn.getInputStream(); // InputStream 값 가져오기
//                    bitmap[0] = BitmapFactory.decodeStream(is); // Bitmap으로 변환
//
//                } catch (MalformedURLException e) {
//                    e.printStackTrace();
//
//                } catch (IOException e) {
//                    e.printStackTrace();
//                }
//            }
//        };
//
//        mThread.start(); // Thread 실행
//
//        try {
//            // 메인 Thread는 별도의 작업 Thread가 작업을 완료할 때까지 대기해야한다
//            // join()를 호출하여 별도의 작업 Thread가 종료될 때까지 메인 Thread가 기다리게 한다
//            mThread.join();
//
//            // 작업 Thread에서 이미지를 불러오는 작업을 완료한 뒤
//            // UI 작업을 할 수 있는 메인 Thread에서 ImageView에 이미지를 지정한다
//
//            if(img_NAME == null){
////                holder.mImageView.setImageResource(R.drawable.default_image);
//            }
//            else {
//                holder.mImageView.setImageBitmap(bitmap[0]);
//            }
////            holder.mImageView.setImageBitmap(mDataset.get(position).bitmap[0]);
////            holder.mImageView.setImageResource(mDataset.get(position).img);
//
//        } catch (InterruptedException e) {
//            e.printStackTrace();
//        }
