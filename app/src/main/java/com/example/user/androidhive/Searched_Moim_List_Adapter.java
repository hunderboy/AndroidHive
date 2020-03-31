package com.example.user.androidhive;

import android.content.Intent;
import android.graphics.Color;
import android.os.AsyncTask;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.TextView;

import com.bumptech.glide.Glide;

import java.io.BufferedReader;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.ArrayList;

/**
 * Created by user on 2018-01-06.
 */

public class Searched_Moim_List_Adapter extends RecyclerView.Adapter<Searched_Moim_List_Adapter.ViewHolder>  {

//    static int pwd = 0;


    private ArrayList<Searched_Moim_Data> searched_Dataset;   // 모임데이터를 가질 어레이리스트를 선언

    // Provide a reference to the views for each data item
    // Complex data items may need more than one view per item, and
    // you provide access to all the views for a data item in a view holder
    // 데이터 레이아웃 에 존재하는 위젯들을 바인딩 한다.
    public static class ViewHolder extends RecyclerView.ViewHolder implements View.OnClickListener{
        // each data item is just a string in this case

        // 표현할 이미지 뷰와 텍스트 객체 선언
        public TextView Moim_name;
        public TextView Moim_date;
        public TextView Moim_address;
        public TextView Charged_Text;

        // 숨겨진 텍스트뷰
        public TextView NO;             // 모임 번호 저장됨
        public TextView Bookmark_num;   // 북마크 번호 저장됨
        public TextView Current_ID;     // 현재 아이디 저장됨

        // 기본 이미지 텍스트뷰
        public ImageView default_image;

        // 즐겨찾기 이미지 버튼
        public ImageButton BookMarkButton;


        // 뷰홀더 메소드 생성
        public ViewHolder(View view) {
            super(view);
            // 텍스트 뷰
            Moim_name = (TextView)view.findViewById(R.id.Moim_name);
            Moim_date = (TextView)view.findViewById(R.id.Moim_date);
            Moim_address = (TextView)view.findViewById(R.id.Moim_address);
            Charged_Text = (TextView)view.findViewById(R.id.Charged_Text);

            // 숨겨진 아이디 텍스트뷰
            NO = (TextView)view.findViewById(R.id.NO);
            Bookmark_num = (TextView)view.findViewById(R.id.Bookmark_num);
            Current_ID = (TextView)view.findViewById(R.id.Current_ID);

            // 이미지 뷰
            default_image = (ImageView)view.findViewById(R.id.default_image);

            view.setOnClickListener(this);

            // 즐겨찾기 버튼
            BookMarkButton = (ImageButton)view.findViewById(R.id.Bookmark_Button);
            BookMarkButton.setOnClickListener(new View.OnClickListener() {  // 즐겨찾기 버튼 클릭 리스너
                @Override
                public void onClick(View view) {    // 클릭이벤트설정

                    if(Bookmark_num.getText().toString().equals("0")){  // 기본으로 북마크 넘버는 0 으로 설정되어 있다.
                        // 0 이면 즐겨 찾기 선택이 되어있지 않다는 이야기이다.
                        // 그러므로 즐겨찾기 클릭이벤트 발생 시에 Bookmark_num 를 1로 변경해준다.
                        Bookmark_num.setText("1");  // 북마크 번호를 1로 설정

                        // 이제 모임 번호를 추가 해야 한다.
                        String getNO = NO.getText().toString(); // 해당 아이템의 모임번호를 가져온다
                        String getID = Current_ID.getText().toString(); // 해당 아이템의 현재 접속자 아이디를 가져온다
                        // 접속자 아이디와 번호를 합께 보낸다.
                        // 그래서 해당 아이디의 즐겨찾기 번호 칼럼에 저 번호를 저장해라 라는 명령을 내린다.
                        Save_BookMark_Num task = new Save_BookMark_Num();
                        task.execute(getID,getNO);  // 아이디와 모임 번호 전송

                        // 일단 버튼의 이미지가 선택된 별 이미지로 변경되어야 한다.
                        BookMarkButton.setImageResource(R.drawable.selected_star2);

                    }
                    else{ // 북마크 번호가 1 이면 이미 즐겨찾기에 선택되어 있다는 것이다. - 선택 해제 해야함

                        Bookmark_num.setText("0");  // 북마크 번호를 0로 설정 - 선택 해제
                        // 일단 버튼의 이미지가 비어있는 별 이미지로 변경되어야 한다.
                        BookMarkButton.setImageResource(R.drawable.star_empty2);

                        // 이제 모임 번호를 삭제 해야 한다.
                        String getNO = NO.getText().toString(); // 해당 아이템의 모임번호를 가져온다
                        String getID = Current_ID.getText().toString(); // 해당 아이템의 현재 접속자 아이디를 가져온다
                        // 접속자 아이디와 번호를 합께 보낸다.
                        // 그래서 해당 아이디의 즐겨찾기 번호 칼럼에 저 번호를 삭제해라 라는 명령을 내린다.
                        Delete_BookMark_Num task2 = new Delete_BookMark_Num();
                        task2.execute(getID,getNO);  // 아이디와 모임 번호 전송
                    }
                }
            });
        }// ViewHolder 끝


        // 리스트 클릭시 나타나는 이벤트
        @Override
        public void onClick(View view) {
//            Toast.makeText(view.getContext(),getPosition()+" 번째 리스트 클릭", Toast.LENGTH_SHORT).show();

            // view.getContext() = 현재 화면 제어권자
            // 화면도 넘기면서 모임을 찾을 기반 데이터를 같이 넘긴다. = 모임명 + 개설자 + 모임번호
            Intent intent = new Intent(view.getContext(), Searched_Moim_See_Activity.class);    // 넘어갈 Searched_Moim_See_Activity 화면

            String getNO = NO.getText().toString();

            // intent 객체에 데이터를 실어서 보내기
            // 리스트뷰 클릭시 인텐트 (Intent) 생성하고 position 값을 이용하여 인텐트로 넘길값들을 넘긴다.
            intent.putExtra("NUMBER", getNO);        // 모임 번호

            view.getContext().startActivity(intent);    // 현재화면 제어권자에서 startActivity 시켜라 라는 말이다.
        }
    }




    // Provide a suitable constructor (depends on the kind of dataset)
    public Searched_Moim_List_Adapter(ArrayList<Searched_Moim_Data> myDataset) {
        searched_Dataset = myDataset;
    }

    // Create new views (invoked by the layout manager)
    @Override
    public Searched_Moim_List_Adapter.ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        // create a new view
        View v = LayoutInflater.from(parent.getContext()).inflate(R.layout.searched_moim_list, parent, false);
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

        // 각 아이템에는 이미지, 모임명, 모임시간, 주소, 유/무료 의 총 5개의 데이터가 표시 된다.
        holder.NO.setText(searched_Dataset.get(position).Number);
        holder.Current_ID.setText(searched_Dataset.get(position).ID);
        holder.Bookmark_num.setText(searched_Dataset.get(position).Bookmark_number);    // 북마크 번호 설정 = 즐겨찾기와 동일 하면 1이 저장됨

        if(searched_Dataset.get(position).Bookmark_number.equals("1")){    // 1 라면
            // 일단 버튼의 이미지가 선택된 이미지로 변경되어야 한다.
            holder.BookMarkButton.setImageResource(R.drawable.selected_star2);
        }

        // 위젯에 모임 내용들을 세팅한다.
        holder.Moim_name.setText(searched_Dataset.get(position).Name);
        holder.Moim_date.setText(searched_Dataset.get(position).Moim_Time);
        holder.Moim_address.setText(searched_Dataset.get(position).address);
        holder.Charged_Text.setText(searched_Dataset.get(position).Charged);

        if(searched_Dataset.get(position).Charged.equals("유료")){    // 유료 라면
            holder.Charged_Text.setTextColor(Color.RED);    // 글자를 빨강색으로 변경
        }

        /**
         * 이미지 설정을 위한 코드 - 버벅거림이 있다 -> 글라이드 + 리사이징
         */
        final String img_NAME = searched_Dataset.get(position).IMG;   // 포지션 별 String 형태로 된 이미지 Uri를 가져온다.

        Glide.with(holder.default_image.getContext())
                .load("http://49.247.208.191/AndroidHive/Moim_default_IMG/"+img_NAME)
                .placeholder(R.drawable.noimage)
                .into(holder.default_image);

    }// onBindViewHolder 끝


    // Return the size of your dataset (invoked by the layout manager)
    @Override
    public int getItemCount() {
        return searched_Dataset.size();
    }



    /**
     * 선택된 아이템을 현재 접속자 아이디의 즐겨찾기에 모임 번호를 추가
     */
    private static class Save_BookMark_Num extends AsyncTask<String, Void, String> {
        String errorString = null;

        @Override
        protected void onPreExecute() {     // 시작전
            super.onPreExecute();
        }

        @Override
        protected void onPostExecute(String result) {   // 완료 후
            super.onPostExecute(result);
            Log.e("", "response - " + result);

            String error_sql = result.substring(0,3);

            if (result.equals("아이디 또는 모임번호가 비어있습니다.")){    // 값을 받아 오지 못했다면
                Log.e("", "아이디 또는 모임번호가 비어있습니다.");
            }
            else if(error_sql.equals("SQL문")){  // result에서 잘라낸 데이터가 'SQL문' 이라면 에러가 발생햇다는 의미
                Log.e("SQL문 에러", ""+result);
            }
            else {  // 데이터 제대로 받아왔다는 의미
                // 아무일도 안함.
                Log.e("변경완료", ""+result);
            }
        }

        @Override
        protected String doInBackground(String... params) {

            String ID  = params[0];             // 아이디 전달
            String Moim_NO  = params[1];  // 모임번호 전달


            Log.e("List_Adapter 아이디 =",ID);
            Log.e("Moim_NO =",Moim_NO);

            String serverURL = "http://49.247.208.191/AndroidHive/BookMark/BookMark_ADD.php";   // AndroidHive/BookMark/BookMark_ADD.php 의 파일로 이동
            String postParameters = "ID=" + ID + "&Moim_NO=" + Moim_NO;   // 아이디와 모임번호 를 전달

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
                Log.d("word_List_Adapter", "response code - " + responseStatusCode);

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
                Log.d("word_List_Adapter", "InsertData: Error ", e);
                errorString = e.toString();
                return null;
            }
        }
    }// Save_BookMark_Num 끝




    /**
     * 선택된 모임에 대한 현재 접속자 아이디의 즐겨찾기에 해당 모임의 번호를 삭제
     */
    private static class Delete_BookMark_Num extends AsyncTask<String, Void, String> {
        String errorString = null;

        @Override
        protected void onPreExecute() {     // 시작전
            super.onPreExecute();
        }

        @Override
        protected void onPostExecute(String result) {   // 완료 후
            super.onPostExecute(result);
            Log.e("", "response - " + result);

            String error_sql = result.substring(0,3);

            if (result.equals("아이디 또는 모임번호가 비어있습니다.")){    // 값을 받아 오지 못했다면
                Log.e("", "아이디 또는 모임번호가 비어있습니다.");
            }
            else if(error_sql.equals("SQL문")){  // result에서 잘라낸 데이터가 'SQL문' 이라면 에러가 발생햇다는 의미
                Log.e("SQL문 에러", ""+result);
            }
            else {  // 데이터 제대로 받아왔다는 의미
                // 아무일도 안함.
                Log.e("변경완료", ""+result);
            }
        }

        @Override
        protected String doInBackground(String... params) {

            String ID  = params[0];       // 아이디 전달
            String Moim_NO  = params[1];  // 모임번호 전달


            Log.e("List_Adapter 아이디 =",ID);
            Log.e("삭제할 Moim_NO =",Moim_NO);

            String serverURL = "http://49.247.208.191/AndroidHive/BookMark/BookMark_Delete.php";   // AndroidHive/BookMark/BookMark_Delete.php 의 파일로 이동
            String postParameters = "ID=" + ID + "&Moim_NO=" + Moim_NO;   // 아이디와 모임번호 를 전달

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
                Log.d("word_List_Adapter", "response code - " + responseStatusCode);

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
                Log.d("word_List_Adapter", "InsertData: Error ", e);
                errorString = e.toString();
                return null;
            }
        }
    }// Delete_BookMark_Num 끝



}
