package com.example.user.androidhive;

import android.app.Activity;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.res.ColorStateList;
import android.graphics.Color;
import android.graphics.Paint;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Build;
import android.support.annotation.RequiresApi;
import android.support.v7.app.AlertDialog;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.bumptech.glide.Glide;

import java.io.BufferedReader;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.ArrayList;

import static android.content.ContentValues.TAG;

/**
 * Created by user on 2018-01-24.
 */

public class Apply_List_Adapter  extends RecyclerView.Adapter<Apply_List_Adapter.ViewHolder>  {

    static String Attendence_Apply_Confirm = "";  // 참여신청확인 변수 => '참여확정완료' 가 들어가 있으면 리스트화면을 종료했다가 Destory 하고 다시 생성한다.
    static String Apply_Cancel_taxt = "삭제완료";  // 참여취소 변수 => '참여취소' 가 들어가 있으면 리스트화면을 종료했다가 Destory 하고 다시 생성한다.


    /**
     * 참여취소가 확인이 되면
     * 결과가 '참여취소' => 그 의미는 참여 데이터가 완전히 삭제되었음을 의미한다.
     * 그러므로 삭제가 확인되었을때
     * 해당 리스트의 아이템을 리스트에서 삭제한다. = Clear
     */
    private ArrayList<Applied_list_Data> ApplyList_Dataset;   // 모임데이터를 가질 어레이리스트를 선언


    // 데이터 레이아웃 에 존재하는 위젯들을 바인딩 한다.
    public class ViewHolder extends RecyclerView.ViewHolder implements View.OnClickListener{
        // each data item is just a string in this case

        // 표현할 이미지 뷰와 텍스트 객체 선언
        // 모임데이터
        public TextView Moim_name;      // 모임명
        public TextView Moim_date;      // 모임날짜
        public TextView Moim_address;   // 모임주소

        public TextView Charged_Text;      // 유료, 무료
        public TextView Available_Seats_Text;   // 신청가능인원
        public TextView Deadline_Text;   // 마감 텍스트 -> 명 신청가능 or 마감

        // 신청자 데이터
        public TextView AP_status;              // 참여확정 or 대기자

        // 숨겨진 텍스트뷰
        public TextView NO;             // 모임 번호 저장됨
        public TextView Current_ID;     // 현재 아이디 저장됨
        public TextView Apply_date;     // 모임 신청일

        // 기본 이미지 텍스트뷰
        public ImageView default_image; // 모임 대표 이미지

        /**
         * 버튼 객체 생성
         * 1. 결제or티켓
         * 2. 수정하기
         * 3. 참여취소
         */
        public Button Modify_button;        // 수정하기 버튼
        public Button Pay_or_ticket_button; // 결제or티켓 버튼
        public Button Cancel_button;        // 참여 취소 버튼


        // 뷰홀더 메소드 생성
        public ViewHolder(View view) {
            super(view);

            String TAG = "Apply_List_Adapter";
            Log.e(TAG,"Apply_Cancel_taxt = "+Apply_Cancel_taxt);

            // 모임 데이터 텍스트 뷰
            Moim_name = (TextView)view.findViewById(R.id.Moim_name);                            // 모임명
            Moim_date = (TextView)view.findViewById(R.id.Moim_date);                            // 모임날짜
            Moim_address = (TextView)view.findViewById(R.id.Moim_address);                      // 모임주소

            Charged_Text = (TextView)view.findViewById(R.id.Charged_Text);                      // 유료, 무료
            Available_Seats_Text = (TextView)view.findViewById(R.id.Available_people_text);     // 신청가능인원
            Deadline_Text = (TextView)view.findViewById(R.id.Deadline_Text);                    // 마감 텍스트 -> 명 신청가능 or 마감

            // 신청자 데이터 텍스트 뷰
            AP_status = (TextView)view.findViewById(R.id.AP_status);    // 참여확정 or 대기자

            // 숨겨진 텍스트뷰
            NO = (TextView)view.findViewById(R.id.NO);                  // 모임번호
            Current_ID = (TextView)view.findViewById(R.id.Current_ID);  // 현재 아이디
            Apply_date = (TextView)view.findViewById(R.id.Apply_date);  // 신청일

            // 이미지 뷰
            default_image = (ImageView)view.findViewById(R.id.default_image);

            // 클릭 관련 + 버튼
            view.setOnClickListener(this);    // 아이템 클릭리스너


            // 참여 취소 버튼 객체 생성
            Cancel_button = (Button)view.findViewById(R.id.Cancel_button);// 참여 취소 버튼
            /**
             * onBindViewHolder 에서
             * 참여 취소 버튼 클릭 리스너 생성함.
             * 현재 참여 취소 버튼만 여기서 하는중
             * 다른 부분들은 모두 ViewHolder 에서 처리하였음
             * onBindViewHolder 에서 연습한번 해보자.
             */
            Cancel_button.setOnClickListener(new View.OnClickListener() {  // 참여 취소 버튼 클릭 리스너
                @Override
                public void onClick(final View view) {    // 참여 취소 클릭 이벤트설정

                    Log.e("삭제할 포지션 = ",""+getPosition());

                    AlertDialog.Builder builder2 = new AlertDialog.Builder(view.getContext());
                    builder2.setTitle("참여취소를 하시겠습니까?");
                    builder2.setMessage("참여취소를 하시면 해당모임의 신청내용이 삭제 됩니다. 그래도 하시겠습니까?");

                    builder2.setPositiveButton("참여취소",
                            new DialogInterface.OnClickListener() {
                                public void onClick(DialogInterface dialog, int which) {

                                    // 해당모임의 모임번호와 신청자 아이디 => 를 통해 동일하면 삭제한다.
                                    // 만약 PHP 에서 삭제가 성공되지 않으면 오류가 있다는 통보를 다시 한다.

                                    String getNO = NO.getText().toString();
                                    String getID = Current_ID.getText().toString();


                                    Log.e("삭제할 모임번호 = ",""+getNO);
                                    Log.e("삭제할 아이디 = ",""+getID);

                                    // 참여취소 을 위한 Apply_Cancel_Tesk
                                    Apply_Cancel_Tesk task = new Apply_Cancel_Tesk();
                                    task.execute(getNO,getID);  // 모임번호 = getNO 와 아이디 = ID 전송

                                    Log.e("참여취소 버튼에서 삭제텍스트 = ",""+Apply_Cancel_taxt);

                                    /**
                                     * 삭제완료가 확인이 되면
                                     * 결과가 '삭제완료' => 참여 데이터가 완전히 삭제되었음을 의미한다.
                                     * 그러므로 삭제가 확인되었을때
                                     * 해당 리스트의 아이템을 리스트에서 삭제한다. = remove
                                     */
                                    if(Apply_Cancel_taxt.equals("삭제완료")){   // 만약 Apply_Cancel_taxt 텍스트가 삭제완료 라면
                                        /**
                                         * 삭제메시지를 띄운다.
                                         * 그리고 실제로 리스트의 해당 아이템을 리스트에서 삭제
                                         * 해당 포지션 데이터 가져와야 한다.
                                         */
                                        Toast.makeText(view.getContext(),getPosition()+"번째 삭제되었습니다.",Toast.LENGTH_LONG).show();

                                        Log.e("삭제할 포지션 = ",""+getPosition());

                                        // 해당 포지션의 아이템을 삭제한다.
                                        ApplyList_Dataset.remove(getPosition());
                                        notifyItemRemoved(getPosition());

                                        // 삭제 텍스트 다시 비운다.
                                        Apply_Cancel_taxt= "???";
                                        Log.e("삭제완료변수 상태 = ",""+Apply_Cancel_taxt);
                                    }// 텍스트 삭제완료 처리 끝
                                }
                            });
                    builder2.setNeutralButton("취소",
                            new DialogInterface.OnClickListener() {
                                public void onClick(DialogInterface dialog, int which) {
                                    Toast.makeText(view.getContext(),"취소되었습니다.",Toast.LENGTH_LONG).show();
                                }
                            });
                    builder2.show();
                } // 참여취소 클릭 이벤트 설정 끝
            });// 참여취소 클릭 이벤트 설정 끝





            // 신청내용 수정하기 버튼 객체 생성
            Modify_button = (Button)view.findViewById(R.id.Modify_button);
            Modify_button.setOnClickListener(new View.OnClickListener() {  // 즐겨찾기 버튼 클릭 리스너
                @Override
                public void onClick(View view) {    // 수정하기 클릭 이벤트설정
                    // 화면도 넘기면서 모임을 찾을 기반 데이터를 같이 넘긴다. = 모임명 + 개설자 + 모임번호
                    Intent intent = new Intent(view.getContext(), Apply_Modify_Activity.class);    // 넘어갈 Apply_Modify_Activity 화면 = 신청내용 수정화면

                    String getNO = NO.getText().toString();
                    String ID = Current_ID.getText().toString();

                    // intent 객체에 데이터를 실어서 보내기
                    // 리스트뷰 클릭시 인텐트 (Intent) 생성하고 position 값을 이용하여 인텐트로 넘길값들을 넘긴다.
                    intent.putExtra("NUMBER", getNO); // 모임 번호
                    intent.putExtra("ID", ID);        // 아이디

                    view.getContext().startActivity(intent);    // 현재화면 제어권자에서 startActivity 시켜라 라는 말이다.
                }
            });


            // 결제 또는 티켓 버튼 생성 = 담겨있는 글자에 따라서 버튼이 하는 행동이 다름.
            Pay_or_ticket_button = (Button)view.findViewById(R.id.Pay_or_ticket_button);
            Pay_or_ticket_button.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(final View view) {    // 클릭 이벤트설정

                    if(Pay_or_ticket_button.getText().toString().equals("결제하기")){   // 버튼 안의 글자가 '결제하기' 라면 => 결제 화면으로 이동

                        // 버튼 이벤트 내 Intent를 생성하여 Intent.ACTION_VIEW 액션을 취해준 뒤, url을 넣어줌
                        Intent intent = new Intent (Intent.ACTION_VIEW, Uri.parse("https://developers.kakao.com/payment/index#%EC%B9%B4%EC%B9%B4%EC%98%A4%ED%8E%98%EC%9D%B4"));
                        view.getContext().startActivity(intent);


                        String getNO = NO.getText().toString();
                        String getID = Current_ID.getText().toString();

                        Log.e("결제할 모임번호 = ",""+getNO);
                        Log.e("결제할 아이디 = ",""+getID);

                        // 결제기능을 위한 을 위한 Pay_Tesk
                        Pay_Tesk task = new Pay_Tesk();
                        task.execute(getNO,getID);  // 모임번호 = getNO 와 아이디 = getID 전송

                    }// 결제하기 기능 끝

                    else if(Pay_or_ticket_button.getText().toString().equals("티켓")){    // 버튼 안의 글자가 '티켓' 이라면 => 티켓화면으로 이동
                        // 인텐트로 화면이동한다.
                        Intent intent = new Intent(view.getContext(), TicketActivity.class);    // 넘어갈 TicketActivity 화면

                        String getNO = NO.getText().toString();         // 모임번호
                        String ID = Current_ID.getText().toString();    // 아이디

                        String M_NAME = Moim_name.getText().toString();         // 모임명
                        String M_APPLY_DATE = Apply_date.getText().toString();  // 신청일
                        String M_OPEN_DATE = Moim_date.getText().toString();    // 모임기간
                        String M_PLACE = Moim_address.getText().toString();     // 모임장소


                        // intent 객체에 데이터를 실어서 보내기
                        intent.putExtra("M_NAME", M_NAME);              // 모임명
                        intent.putExtra("M_APPLY_DATE", M_APPLY_DATE);  // 신청일

                        intent.putExtra("M_OPEN_DATE", M_OPEN_DATE);    // 모임기간
                        intent.putExtra("M_PLACE", M_PLACE);            // 모임장소

                        // 모임 번호와 아이디로 QR코드 구성
                        intent.putExtra("M_NUMBER", getNO);     // 모임 번호
                        intent.putExtra("USER_ID", ID);         // 아이디

                        view.getContext().startActivity(intent);    // 현재화면 제어권자에서 startActivity 시켜라 라는 말이다.

                    }// 티켓 기능 끝

                    else if(Pay_or_ticket_button.getText().toString().equals("참여신청")){    // 버튼 안의 글자가 '참여신청' 이라면 => 참여 신청 다이얼로그 띄움.
                        // 참여 신청의 경우 다이얼 로그를 띄워서 확인버튼을 누르면 참여신청 가능하게 끔 설정 하자
                        AlertDialog.Builder builder = new AlertDialog.Builder(view.getContext());
                        builder.setTitle("참여 신청");
                        builder.setMessage("참여신청을 하시겠습니까?");
                        builder.setPositiveButton("신청하기",
                                new DialogInterface.OnClickListener() {
                                    public void onClick(DialogInterface dialog, int which) {

                                        String getNO = NO.getText().toString();         // 모임번호
                                        String ID = Current_ID.getText().toString();    // 아이디

                                        // 참여신청을 위한 Apply_Tesk
                                        Apply_Tesk task = new Apply_Tesk();
                                        task.execute(getNO,ID);  // 모임번호와 아이디 전송

                                        if(Attendence_Apply_Confirm.equals("참여확정완료")){
                                            Toast.makeText(view.getContext(),"신청되었습니다.",Toast.LENGTH_LONG).show();  // current_view 를 이용하였는데 과연 될까??
                                            Attendence_Apply_Confirm = "";
                                            Log.e("참여확정변수 상태 = ",""+Attendence_Apply_Confirm);
                                        }

                                        Intent intent = new Intent(view.getContext(), Apply_List_Activity.class);    // 넘어갈 Apply_List_Activity 화면
                                        view.getContext().startActivity(intent);    // 현재화면 제어권자에서 startActivity 시켜라 라는 말이다.

                                        // 현재 액티비티 종료.
                                        ((Activity) view.getContext()).finish();

                                    }
                                });
                        builder.setNeutralButton("취소",
                                new DialogInterface.OnClickListener() {
                                    public void onClick(DialogInterface dialog, int which) {
                                        Toast.makeText(view.getContext(),"취소되었습니다.",Toast.LENGTH_LONG).show();
                                    }
                                });

                        builder.show();
                    }// 참여신청 기능 끝
                }// 클릭이벤트 설정 끝
            });// 결제, 티켓, 참여신청 버튼 클릭리스너 끝


        }// ViewHolder 메소드 끝


        // 리스트 아이템 클릭 시 나타나는 이벤트
        @Override
        public void onClick(View view) {
            // view.getContext() = 현재 화면 제어권자
            // 화면도 넘기면서 모임을 찾을 기반 데이터를 같이 넘긴다. = 모임명 + 개설자 + 모임번호
            Intent intent = new Intent(view.getContext(), Searched_Moim_See_Activity.class);    // 넘어갈 Searched_Moim_See_Activity 화면 = 모임확인화면

            String getNO = NO.getText().toString();

            // intent 객체에 데이터를 실어서 보내기
            // 리스트뷰 클릭시 인텐트 (Intent) 생성하고 position 값을 이용하여 인텐트로 넘길값들을 넘긴다.
            intent.putExtra("NUMBER", getNO);        // 모임 번호

            view.getContext().startActivity(intent);    // 현재화면 제어권자에서 startActivity
        }

    }// ViewHolder 클래스 끝


    // Provide a suitable constructor (depends on the kind of dataset)
    public Apply_List_Adapter(ArrayList<Applied_list_Data> myDataset) {
        ApplyList_Dataset = myDataset;
    }

    // Create new views (invoked by the layout manager)
    @Override
    public Apply_List_Adapter.ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        // create a new view
        View v = LayoutInflater.from(parent.getContext()).inflate(R.layout.applied_moim_list, parent, false);
        // 뷰 홀더에 넣어줄 뷰를 찾는 과정. my_view라는 xml에는 cardview가 들어갈 예정이다.

        // set the view's size, margins, paddings and layout parameters
        ViewHolder vh = new ViewHolder(v);  // 이 view를 넣기위한 ViewHolder를 다음과 같이 선언한 후 넣는다.

        return vh;
    }



    // Replace the contents of a view (invoked by the layout manager)
    @RequiresApi(api = Build.VERSION_CODES.LOLLIPOP)
    @Override
    public void onBindViewHolder(ViewHolder holder, final int position) {
        // - get element from your dataset at this position
        // - replace the contents of the view with that element

        // 각 아이템에는 이미지, 모임명, 모임시간, 주소, 유/무료 의 총 5개의 데이터가 표시 된다.
        holder.NO.setText(ApplyList_Dataset.get(position).Number);
        holder.Current_ID.setText(ApplyList_Dataset.get(position).ID);
        holder.Apply_date.setText(ApplyList_Dataset.get(position).Apply_DATE);


        // 위젯에 모임 내용들을 세팅한다.
        holder.Moim_name.setText(ApplyList_Dataset.get(position).Name);                 // 모임명
        holder.Moim_date.setText(ApplyList_Dataset.get(position).Moim_Time);            // 모임날짜
        holder.Moim_address.setText(ApplyList_Dataset.get(position).address);           // 모임주소

        holder.Charged_Text.setText(ApplyList_Dataset.get(position).Charged);           // 유료, 무료

        if(ApplyList_Dataset.get(position).Charged.equals("무료")){   // 무료이면
            holder.Charged_Text.setTextColor(Color.GREEN);  // 무료 = 글자색을 초록색으로
        }

        holder.Available_Seats_Text.setText(ApplyList_Dataset.get(position).Available_Seats);   // 신청가능인원 설정
        holder.AP_status.setText(ApplyList_Dataset.get(position).Application_status);   // 신청자 참여 상태 = 참여확정 or 대기자


        if(ApplyList_Dataset.get(position).Application_status.equals("참여확정")){    // 신청자 참여 상태가 = 참여확정 이라면
//            // 참여확정에서는 티켓 버튼이 생성되어야 한다.
            /**
             * 기본적으로는 버튼이 보이는것이 정상이고
             *  - 무료 모임에서
             * 신청인원수가 남아 있는 상태에서 신청했을 경우
             * 참여상태 = 참여확정 => 티켓을 가지고 있는 상태
             * 만약 신청인원수가 남아있지 않는 상태에서 신청했을 경우
             * 참여상태 = 대기자 => 티켓을 가지고 있지 않음 => 버튼이 보일 필요 없다.
             *
             * 그런데
             * 기존 참여확정자가 모임 참여를 취소했을 경우 => 참여신청이 가능한 상태라면
             * 어떻게 할것 인가??
             *
             *
             *
             */

            // 참여 확정 상태라면 신청가능인원이 보일 필요갸 없다.
            holder.Available_Seats_Text.setVisibility(View.INVISIBLE);  // 보이지 않게 설정
            holder.Deadline_Text.setVisibility(View.INVISIBLE);  // 보이지 않게 설정


            // 그리고 왼쪽버튼이 티켓 글자로 변경되어야 한다.
            holder.Pay_or_ticket_button.setText("티켓");
            ColorStateList button_tintColor_Blue = new ColorStateList(new int[][]{new int[0]}, new int[]{0xff00ddff});  // 파란색
            holder.Pay_or_ticket_button.setBackgroundTintList(button_tintColor_Blue); // 버튼 틴트컬러 변경

            Log.e("Apply_List_Adapter 에서"," 참여확정 조건문 들어옴");
        }// 참여확정 상태처리 끝

        else if(ApplyList_Dataset.get(position).Application_status.equals("대기자")){  // Application_status =  신청자 참여 상태가 = 대기자 라면
            // 대기자 일경우 AP_status[신청자참여상태] 텍스트가 주황색으로
            String strColor_orange = "#ffff8800";   // 16진수 색상값 적용하기
            holder.AP_status.setTextColor(Color.parseColor(strColor_orange));   // 오렌지 색으로 텍스트 적용

            Log.e("Apply_List_Adapter 에서"," 참여확정 조건문 들어옴");


            // 신청가능한 자리가 0 이면 - 참여신청을 할수 없게 해야함.
            if(ApplyList_Dataset.get(position).Available_Seats.equals("0")){
                holder.Pay_or_ticket_button.setVisibility(View.INVISIBLE);  // 참여신청 버튼을 보이지 않게함.
                holder.Available_Seats_Text.setVisibility(View.INVISIBLE);  // 신청가능한 자리 텍스트 보이지 않게 설정

                holder.Deadline_Text.setTextColor(Color.RED);      // 텍스트 색상 빨강색
                holder.Deadline_Text.setPaintFlags(holder.Deadline_Text.getPaintFlags() | Paint.FAKE_BOLD_TEXT_FLAG);   // 텍스트 굵게 설정
                holder.Deadline_Text.setText("신청좌석수 마감");   // 신청 가능자리 마감이므로 = '신청좌석수 마감' 으로 텍스트 설정
            }

            // 신청가능인원이 최소 1명 이상인 경우
            else {
                if(ApplyList_Dataset.get(position).Charged.equals("무료")){   // 무료인 경우
                    holder.Pay_or_ticket_button.setText("참여신청");    // 버튼의 텍스트를 참여신청 으로 변경
                    ColorStateList button_tintColor_Green = new ColorStateList(new int[][]{new int[0]}, new int[]{0xff99cc00});     // 밝은 초록색
                    holder.Pay_or_ticket_button.setBackgroundTintList(button_tintColor_Green); // 버튼 틴트컬러 초록색 변경
                }
                else {  // 유료인 경우
                    holder.Pay_or_ticket_button.setText("결제하기");    // 버튼의 텍스트를 결제하기 으로 변경
                    ColorStateList button_tintColor_Yellow = new ColorStateList(new int[][]{new int[0]}, new int[]{0xffffdd33});    // 노랑색
                    holder.Pay_or_ticket_button.setBackgroundTintList(button_tintColor_Yellow); // 버튼 틴트컬러 노랑색 변경
                }
            }
        }// 대기자 상태 처리




        /**
         * 이미지 설정을 위한 코드 - 버벅거림이 있다 -> 글라이드 + 리사이징
         */
        final String img_NAME = ApplyList_Dataset.get(position).IMG;   // 포지션 별 String 형태로 된 이미지 Uri를 가져온다.

        Glide.with(holder.default_image.getContext())
                .load("http://49.247.208.191/AndroidHive/Moim_default_IMG/"+img_NAME)
                .placeholder(R.drawable.noimage)
                .into(holder.default_image);

    }// onBindViewHolder 끝



    // Return the size of your dataset (invoked by the layout manager)
    @Override
    public int getItemCount() {
        return ApplyList_Dataset.size();
    }





    /**
     * 참여신청 버튼을 클릭하면 작동하는 Apply_Tesk
     * 대기자 => 참여확정자
     * 로 변경하기 위해서 사용
     */
    private static class Apply_Tesk extends AsyncTask<String, Void, String> {
        String errorString = null;
        @Override
        protected void onPreExecute() {     // 시작전
            super.onPreExecute();
        }
        @Override
        protected void onPostExecute(String result) {   // 완료 후
            super.onPostExecute(result);
            Log.d(TAG, "response - " + result);


            if (result == null){    // 값을 받아 오지 못했다면
                Log.e(TAG, "response - " + result);
            }
            else if(result.equals("참여확정완료")){
                Log.e(TAG, "참여확정완료 됨. " + result);
                Attendence_Apply_Confirm = "참여확정완료";
            }
            else if(result.equals("값을 받아오지 못했습니다.")){
                Log.e(TAG, "값을 받아오지 못했습니다 " + result);
            }
            else {  // SQL 처리중 에러발생
                Log.e(TAG, "response - " + result);
            }
        }

        @Override
        protected String doInBackground(String... params) {
            String M_NO = params[0];   // 받아온 QR 코드 모임번호
            String M_ID = params[1];   // 받아온 QR 코드 아이디

            Log.e("참여신청 : 받아온 모임번호 =",M_NO);
            Log.e("참여신청 : 받아온 아이디 =",M_ID);

            String serverURL = "http://49.247.208.191/AndroidHive/Apply/Participant/Application_status_change.php";   // AndroidHive/Apply/Participant/Application_status_change.php 의 파일로 이동
            String postParameters = "M_NO=" + M_NO + "&M_ID=" + M_ID;   // 모임번호, 아이디 전달

            try {
                URL url = new URL(serverURL);
                HttpURLConnection httpURLConnection = (HttpURLConnection) url.openConnection();

                httpURLConnection.setReadTimeout(5000);
                httpURLConnection.setConnectTimeout(5000);
                httpURLConnection.setRequestMethod("POST");
                httpURLConnection.setDoInput(true);
                httpURLConnection.connect();

                OutputStream outputStream = httpURLConnection.getOutputStream();
                outputStream.write(postParameters.getBytes("UTF-8"));
                outputStream.flush();
                outputStream.close();
                int responseStatusCode = httpURLConnection.getResponseCode();
                Log.d(TAG, "response code - " + responseStatusCode);
                InputStream inputStream;
                if(responseStatusCode == HttpURLConnection.HTTP_OK) {
                    inputStream = httpURLConnection.getInputStream();
                } else{
                    inputStream = httpURLConnection.getErrorStream();
                }
                InputStreamReader inputStreamReader = new InputStreamReader(inputStream, "UTF-8");
                BufferedReader bufferedReader = new BufferedReader(inputStreamReader);

                StringBuilder sb = new StringBuilder();
                String line;
                while((line = bufferedReader.readLine()) != null){
                    sb.append(line);
                }
                bufferedReader.close();
                return sb.toString().trim();
            } catch (Exception e) {
                Log.d(TAG, "InsertData: Error ", e);
                errorString = e.toString();
                return null;
            }
        }
    }// Apply_Tesk 끝




    /**
     * 참여취소 버튼을 클릭하면 작동하는 Apply_Cancel_Tesk
     * 작동과 동시에 DB의 Apply_info 테이블에 아이디와 모임번호를 전달하여
     * 일치하는 데이터 행을 삭제한다,
     *
     */
    private static class Apply_Cancel_Tesk extends AsyncTask<String, Void, String> {
        String errorString = null;
        @Override
        protected void onPreExecute() {     // 시작전
            super.onPreExecute();
        }
        @Override
        protected void onPostExecute(String result) {   // 완료 후
            super.onPostExecute(result);
            Log.d(TAG, "response - " + result);


            if (result == null){    // 값을 받아 오지 못했다면
                Log.e(TAG, "response - " + result);
            }
            else if(result.equals("삭제완료")){         // 삭제 되면 삭제완료 데이터변수에 삭제완료를 삽입한다.
                Log.e(TAG, "삭제완료 됨. " + result);
                Apply_Cancel_taxt = "삭제완료";     // 삭제 완료 시 Apply_Cancel_taxt 텍스트에 = "삭제완료" 설정

            }
            else if(result.equals("값을 받아오지 못했습니다.")){
                Log.e(TAG, "값을 받아오지 못했습니다 " + result);
            }
            else {  // SQL 처리중 에러발생
                Log.e(TAG, "response - " + result);
            }
        }

        @Override
        protected String doInBackground(String... params) {
            String M_NO = params[0];   // 모임 번호
            String M_ID = params[1];   // 신청 아이디

            Log.e("참여취소 : 받아온 모임번호 =",M_NO);
            Log.e("참여취소 : 받아온 아이디 =",M_ID);

            String serverURL = "http://49.247.208.191/AndroidHive/Apply/Participant/Apply_Cancel.php";   // AndroidHive/Apply/Participant/Apply_Cancel.php 의 파일로 이동
            String postParameters = "M_NO=" + M_NO + "&M_ID=" + M_ID;   // 모임번호, 아이디 전달

            try {
                URL url = new URL(serverURL);
                HttpURLConnection httpURLConnection = (HttpURLConnection) url.openConnection();

                httpURLConnection.setReadTimeout(5000);
                httpURLConnection.setConnectTimeout(5000);
                httpURLConnection.setRequestMethod("POST");
                httpURLConnection.setDoInput(true);
                httpURLConnection.connect();

                OutputStream outputStream = httpURLConnection.getOutputStream();
                outputStream.write(postParameters.getBytes("UTF-8"));
                outputStream.flush();
                outputStream.close();
                int responseStatusCode = httpURLConnection.getResponseCode();
                Log.d(TAG, "response code - " + responseStatusCode);
                InputStream inputStream;
                if(responseStatusCode == HttpURLConnection.HTTP_OK) {
                    inputStream = httpURLConnection.getInputStream();
                } else{
                    inputStream = httpURLConnection.getErrorStream();
                }
                InputStreamReader inputStreamReader = new InputStreamReader(inputStream, "UTF-8");
                BufferedReader bufferedReader = new BufferedReader(inputStreamReader);

                StringBuilder sb = new StringBuilder();
                String line;
                while((line = bufferedReader.readLine()) != null){
                    sb.append(line);
                }
                bufferedReader.close();
                return sb.toString().trim();
            } catch (Exception e) {
                Log.d(TAG, "InsertData: Error ", e);
                errorString = e.toString();
                return null;
            }
        }
    }// Apply_Cancel_Tesk 끝


    /**
     * 결제하기 버튼을 클릭하면 작동하는 Pay_Tesk
     * 작동과 동시에 DB의 Apply_info 테이블에 아이디와 모임번호를 전달하여
     * 대기자 -> 참여확정으로 변경한다.
     *
     * 일단은 현재 결제 기능은 데모버전으로만 구동가능 하므로
     * 결제가 된다는 가정하고 결제확정 데이터 삽입을 진행한다.
     */
    private static class Pay_Tesk extends AsyncTask<String, Void, String> {
        String errorString = null;
        @Override
        protected void onPreExecute() {     // 시작전
            super.onPreExecute();
        }
        @Override
        protected void onPostExecute(String result) {   // 완료 후
            super.onPostExecute(result);
            Log.d(TAG, "response - " + result);


            if (result == null){    // 값을 받아 오지 못했다면
                Log.e(TAG, "response - " + result);
            }
            else if(result.equals("삭제완료")){         // 삭제 되면 삭제완료 데이터변수에 삭제완료를 삽입한다.
                Log.e(TAG, "삭제완료 됨. " + result);
                Apply_Cancel_taxt = "삭제완료";     // 삭제 완료 시 Apply_Cancel_taxt 텍스트에 = "삭제완료" 설정

            }
            else if(result.equals("값을 받아오지 못했습니다.")){
                Log.e(TAG, "값을 받아오지 못했습니다 " + result);
            }
            else {  // SQL 처리중 에러발생
                Log.e(TAG, "response - " + result);
            }
        }

        @Override
        protected String doInBackground(String... params) {
            String M_NO = params[0];   // 모임 번호
            String M_ID = params[1];   // 신청 아이디

            Log.e("참여취소 : 받아온 모임번호 =",M_NO);
            Log.e("참여취소 : 받아온 아이디 =",M_ID);

            String serverURL = "http://49.247.208.191/AndroidHive/Apply/Participant/Application_status_change.php";   // AndroidHive/Apply/Participant/Application_status_change.php 의 파일로 이동
            String postParameters = "M_NO=" + M_NO + "&M_ID=" + M_ID;   // 모임번호, 아이디 전달

            try {
                URL url = new URL(serverURL);
                HttpURLConnection httpURLConnection = (HttpURLConnection) url.openConnection();

                httpURLConnection.setReadTimeout(5000);
                httpURLConnection.setConnectTimeout(5000);
                httpURLConnection.setRequestMethod("POST");
                httpURLConnection.setDoInput(true);
                httpURLConnection.connect();

                OutputStream outputStream = httpURLConnection.getOutputStream();
                outputStream.write(postParameters.getBytes("UTF-8"));
                outputStream.flush();
                outputStream.close();
                int responseStatusCode = httpURLConnection.getResponseCode();
                Log.d(TAG, "response code - " + responseStatusCode);
                InputStream inputStream;
                if(responseStatusCode == HttpURLConnection.HTTP_OK) {
                    inputStream = httpURLConnection.getInputStream();
                } else{
                    inputStream = httpURLConnection.getErrorStream();
                }
                InputStreamReader inputStreamReader = new InputStreamReader(inputStream, "UTF-8");
                BufferedReader bufferedReader = new BufferedReader(inputStreamReader);

                StringBuilder sb = new StringBuilder();
                String line;
                while((line = bufferedReader.readLine()) != null){
                    sb.append(line);
                }
                bufferedReader.close();
                return sb.toString().trim();
            } catch (Exception e) {
                Log.d(TAG, "InsertData: Error ", e);
                errorString = e.toString();
                return null;
            }
        }
    }// Pay_Tesk 끝



}// 코드 끝
