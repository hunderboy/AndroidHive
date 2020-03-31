package com.example.user.androidhive;

import android.content.Intent;
import android.os.AsyncTask;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import java.io.BufferedReader;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.ArrayList;

/**
 * Created by user on 2018-01-09.
 */

public class Recent_word_List_Adapter extends RecyclerView.Adapter<Recent_word_List_Adapter.ViewHolder>  {



    private ArrayList<Recent_word_Data> Recent_Word_Dataset;   // 모임데이터를 가질 어레이리스트를 선언

    // Provide a reference to the views for each data item
    // Complex data items may need more than one view per item, and
    // you provide access to all the views for a data item in a view holder
    // 데이터 레이아웃 에 존재하는 위젯들을 바인딩 한다.
    public class ViewHolder extends RecyclerView.ViewHolder implements View.OnClickListener{
        // each data item is just a string in this case

        // 표현할 이미지 뷰와 텍스트 객체 선언
        public TextView searched_Word;  // 검색어 위젯
        public TextView Current_ID;     // 아이디 위젯

        // 삭제 버튼 위젯
        public Button Delete_button;

        // 뷰홀더 메소드 생성
        public ViewHolder(View view) {
            super(view);

            // 텍스트 뷰
            searched_Word = (TextView)view.findViewById(R.id.searched_Word);
            Current_ID = (TextView)view.findViewById(R.id.Current_ID);

            // 검색어 삭제 버튼
            Delete_button = (Button)view.findViewById(R.id.Delete_Word_button);
            Delete_button.setOnClickListener(new View.OnClickListener() {  // 삭제 버튼 클릭 리스너
                @Override
                public void onClick(View view) {    // 클릭이벤트설정

                    String deleting_word = searched_Word.getText().toString();
                    Log.e("삭제한 단어 = ",""+deleting_word);
                    Log.e("포지션 = ",""+getPosition());

                    Recent_Word_Dataset.remove(getPosition());  // ViewHolder 클래스를 static을 제거 하니 된다. 그런데 사용은 되나 문제가 있다고 한다. ViewHolder static 검색 ㄱㄱ
                    notifyItemRemoved(getPosition());

                    Toast.makeText(view.getContext(),getPosition()+"번 째 삭제 버튼 클릭",Toast.LENGTH_SHORT).show();

                    // Current_ID 텍스트뷰 에서 아이디 가져온다.
                    String ID = Current_ID.getText().toString();    // 이 ID는 현재접속자 아이디 이다.  = 모든 아이템에 공동으로 저장
                    Log.e("버튼 클릭후 아이디 = ",ID);

                    // 아이템을 삭제한다.
                    Word_Delete_Task task = new Word_Delete_Task();
                    task.execute(ID,deleting_word);  // 아이디 와 삭제어 전달
                }
            });


            view.setOnClickListener(this);  // 리스트 아이템 클릭 이벤트 설정
        }// ViewHolder 끝


        // 최근 검색어 리스트 클릭시 나타나는 이벤트
        @Override
        public void onClick(View view) {
            /**
             * 최근 검색어 리스트를 클릭하게 되면
             * 1. 화면의 검색창에 검색어를 설정하고
             * 2. 검색어를 검색 확인 화면에 넘기고
             * 3. 검색어를 다시 저장한다.
             */

            String 전달할_검색어 = searched_Word.getText().toString();
            String 전달할_아이디 = Current_ID.getText().toString();

//            view.getContext().Search_EditText.setText
//            View v = LayoutInflater.from(parent.getContext()).inflate(R.layout.recent_word_list, parent, false);

            // 검색화면의 검색창에 전달할 검색어 설정 = 기능 동작 안함
            View header = LayoutInflater.from(view.getContext()).inflate(R.layout.activity_search, null, false);
            EditText Search_Window = (EditText) header.findViewById(R.id.Search_EditText);
            Search_Window.setText(전달할_검색어);

            // view.getContext() = 현재 화면 제어권자
            // 화면도 넘기면서 모임을 찾을 기반 데이터를 같이 넘긴다. = 모임명 + 개설자 + 모임번호
            Intent intent = new Intent(view.getContext(), Search_Confirm_Activity.class);    // 넘어갈 Searched_Moim_See_Activity 화면

            // intent 객체에 데이터를 실어서 보내기
            // id 를 넘기는 이유는 id 를 기반으로 최근 검색어를 찾기 위함이다.
            intent.putExtra("Word",전달할_검색어  );      // 검색어 넘기기
            intent.putExtra("ID",전달할_아이디  );      // ID 넘기기

            view.getContext().startActivity(intent);    // 현재화면 제어권자에서 startActivity 시켜라 라는 말이다.
        }


    }

    // Provide a suitable constructor (depends on the kind of dataset)
    public Recent_word_List_Adapter(ArrayList<Recent_word_Data> myDataset) {
        Recent_Word_Dataset = myDataset;
    }

    // Create new views (invoked by the layout manager)
    @Override
    public Recent_word_List_Adapter.ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        // create a new view
        View v = LayoutInflater.from(parent.getContext()).inflate(R.layout.recent_word_list, parent, false);
        // 뷰 홀더에 넣어줄 뷰를 찾는 과정. = recent_word_list.xml 이 아이템의 뷰 이다.

        // set the view's size, margins, paddings and layout parameters
        ViewHolder vh = new ViewHolder(v);  // 이 view를 넣기위한 ViewHolder를 다음과 같이 선언한 후 넣는다.

        return vh;
    }



    // Replace the contents of a view (invoked by the layout manager)
    @Override
    public void onBindViewHolder(ViewHolder holder, final int position) {
        // - get element from your dataset at this position
        // - replace the contents of the view with that element
        holder.searched_Word.setText(Recent_Word_Dataset.get(position).word);
        holder.Current_ID.setText(Recent_Word_Dataset.get(position).ID);


//        holder.Delete_button.setOnClickListener(new View.OnClickListener() {
//            @Override
//            public void onClick(View view) {
//
//                String word = Recent_Word_Dataset.get(position).word;
//                Log.e("단어 = ",""+word);
//                Log.e("포지션 = ",""+position);
//                Recent_Word_Dataset.remove(position);
//                notifyItemRemoved(position);
//
//                Toast.makeText(view.getContext(),"Removed : " + word,Toast.LENGTH_SHORT).show();
//            }
//        });



    }// onBindViewHolder 끝



    // Return the size of your dataset (invoked by the layout manager)
    @Override
    public int getItemCount() {
        return Recent_Word_Dataset.size();
    }




    /**
     * 선택된 단어를 삭제
     */
    private class Word_Delete_Task extends AsyncTask<String, Void, String> {

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

            if (result.equals("아이디 또는 삭제어가 비어있습니다.")){    // 값을 받아 오지 못했다면
                Log.e("", "아이디 또는 삭제어가 비어있습니다");
            }
            else if(error_sql.equals("SQL문")){  // result에서 잘라낸 데이터가 'SQL문' 이라면 에러가 발생햇다는 의미
                Log.e("SQL문 에러", ""+result);
            }
            else {  // 데이터 제대로 받아왔다는 의미
                // 아무일도 안함.
                Log.e("제대로 삭제됨", ""+result);
            }
        }


        @Override
        protected String doInBackground(String... params) {

            String ID  = params[0];             // 아이디 전달
            String deleting_word  = params[1];     // 검색어를 전체 전달


            Log.e("List_Adapter 아이디 =",ID);
            Log.e("deleting_word =",deleting_word);

            String serverURL = "http://49.247.208.191/AndroidHive/Recent_Word/Word_Delete.php";   // AndroidHive/Recent_Word/Word_Delete.php 의 파일로 이동
            String postParameters = "ID=" + ID + "&deleting_word=" + deleting_word;   // 아이디와 삭제어 를 전달


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
    }// Word_Delete_Task 끝









}
