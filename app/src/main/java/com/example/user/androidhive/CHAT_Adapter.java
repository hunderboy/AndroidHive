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
import java.util.List;

import jp.wasabeef.glide.transformations.CropCircleTransformation;

/**
 * Created by user on 2018-03-24.
 */

public class CHAT_Adapter extends RecyclerView.Adapter<RecyclerView.ViewHolder> {


    private final static int TYPE_CHAT = 1;   // 뷰타입 번호 1 = 채팅 뷰
    private final static int TYPE_ALRAM = 2;  // 뷰타입 번호 2 = 알림 뷰
    private final static int TYPE_MY_CHAT = 3;  // 뷰타입 번호 2 = 알림 뷰


    // 각 뷰가 들어갈 공간인 ArrayList 선언
    private List<Object> setChatFeed = new ArrayList<>();

    /**
     * Context 는 여기에서 사용되지 않지만 복잡한 작업을 수행하거나 외부에서 메소드를 호출해야 할 수도 있습니다
     * 화면 제어 관련.
     */
    private Context context;    // 화면 제어 권자

    // 생성자
    public CHAT_Adapter(Context context){
            this.context=context;
            }

    // 어댑터에 연결할 데이터 세트를 선언함.
    /**
     * 사용 방법은 EX) callSMSAdapter.setCallSMSFeed(callSMSList); 형태로..
     * 리사이클러뷰가 존재하는 화면에서
     * 리사이클러뷰 어댑터 클래스(CHAT_Adapter) 객체로써 선언된 callSMSAdapter 의 메소드 setCallSMSFeed 를 사용하겠다는 의미이다.
     * 해당 메소드의 입력값인 List<Object> A 라는 객체를 넣으면
     * 입력된 List<Object> A 와 어댑터에서 사용하는 List<Object> B .
     * 즉 A == B 동일 하게 되고
     * 어댑터에서는 B 를 이용하여 뷰 처리를 하게 된다.
     * @param setChatFeed
     */
    public void setChatFeed(List<Object> setChatFeed){   // 메인화면에서 사용됨,
            this.setChatFeed = setChatFeed;
            }

    // We need to override this as we need to differentiate
    // which type viewHolder to be attached
    // This is being called from onBindViewHolder() method

    /**
     * 뷰타입에 따라서 처리방식 다르게 하기
     *  - 처리 방식
     * callSMSList.add(new Call_NUMber("Rob","9:40 AM"));
     * callSMSList.add(new SMS("Sandy","Hey, what's up?","9:42 AM"));
     * 이라고 메인 화면에서 add 처리 한다고 하면
     *
     * instanceof Call_NUMber = 해당 포지션의 데이터클래스 처리가 = Call_NUMber 이라면 => return TYPE_CHAT;
     * instanceof SMS = 해당 포지션의 데이터클래스 처리가 = SMS 이라면 => return TYPE_ALRAM;
     * 예외 처리 실시
     */
    @Override
    public int getItemViewType(int position) {
            if (setChatFeed.get(position) instanceof ChatHistory_Data) { // 데이터클래스 처리 ChatHistory_Data 경우
            return TYPE_CHAT; // 뷰타입 번호 1
            } else if (setChatFeed.get(position) instanceof LineText_Data) { // 데이터클래스 처리 LineText_Data 경우
            return TYPE_ALRAM; // 뷰타입 번호 2
            } else if (setChatFeed.get(position) instanceof My_Chat_Data) { // 데이터클래스 처리 My_Chat_Data 경우
            return TYPE_MY_CHAT; // 뷰타입 번호 3
            }
            return -1;
            }

    // 뷰 내용 설정 하기
    @Override
    public void onBindViewHolder(final RecyclerView.ViewHolder holder, final int position) {
            // 뷰홀더 객체인 holder 를 생성하고, getItemViewType 메소드를 이용하여
            // int값을 가져온다. = 뷰타입 번호 가져오기
            int viewType = holder.getItemViewType();

            switch (viewType){  // 뷰타입 번호에 따라서 다르게 뷰 생성
            case TYPE_CHAT: // 뷰타입 번호 1
            ChatHistory_Data chatDatas = (ChatHistory_Data)setChatFeed.get(position);   // 채팅에 관련된 데이터 클래스 객체 가져와서
            ((ChatViewHolder)holder).ConnectDatas(chatDatas);
            break;
            case TYPE_ALRAM: // 뷰타입 번호 2
            LineText_Data LineText = (LineText_Data)setChatFeed.get(position);   // 채팅에 관련된 데이터 클래스 객체 가져와서
            ((AlramViewHolder)holder).showCenterTEXT(LineText);
            break;
            case TYPE_MY_CHAT: // 뷰타입 번호 3
            My_Chat_Data mychat = (My_Chat_Data)setChatFeed.get(position);   // 채팅에 관련된 데이터 클래스 객체 가져와서
            ((MyTEXT_ViewHolder)holder).set_My_TEXT(mychat);    // 이부분에 문제 있다.
            break;
            }
            }

    @Override
    public int getItemCount(){
            return setChatFeed.size();
            }

    // Invoked by layout manager to create new views
    @Override
    public RecyclerView.ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
            // Attach layout for single cell
            RecyclerView.ViewHolder viewHolder;
            // Identify viewType returned by getItemViewType(...)
            // and return ViewHolder Accordingly
            switch (viewType){
            case TYPE_CHAT: // 뷰타입 번호 = 1 = 상대방 채팅
            View callsView = LayoutInflater
            .from(parent.getContext())
            .inflate(R.layout.opponenet_chat, parent, false);   // opponent_chat_item
            viewHolder = new ChatViewHolder(callsView);
            break;
            case TYPE_ALRAM:  // 뷰타입 번호 = 2 = 중앙 텍스트 표시
            View smsView = LayoutInflater
            .from(parent.getContext())
            .inflate(R.layout.center_text, parent, false); // activity_chatitem
            viewHolder = new AlramViewHolder(smsView);
            break;
            case TYPE_MY_CHAT:  // 뷰타입 번호 = 3 = 내 채팅
            View my_chat_View = LayoutInflater
            .from(parent.getContext())
            .inflate(R.layout.my_chat, parent, false);
            viewHolder = new MyTEXT_ViewHolder(my_chat_View);
            break;

    default:
            viewHolder = null;
            break;
            }
            return viewHolder;
            }

    /**
     * 1 번째 뷰 홀더
     * 상대방이 쓴 채팅을 보여주기 위한 뷰홀더
     */
    public class ChatViewHolder extends RecyclerView.ViewHolder {

        // 텍스트 뷰 객체
        private TextView NameText;      // 이름 텍스트
        private TextView ChatingText;   // 채팅 메세지
        private TextView Chating_Time;   // 상대방 채팅 시간

        // 이미지 뷰 객체
        private ImageView id_image;     // 아이디에 따라 달라지는 이미지 뷰

        public ChatViewHolder(View itemView) {
            super(itemView);

            NameText = (TextView)itemView.findViewById(R.id.name_text);         // 상대방 이름 연결
            ChatingText = (TextView)itemView.findViewById(R.id.Chat_MSG);       // 상대방 채팅 메세지 연결
            Chating_Time = (TextView)itemView.findViewById(R.id.your_time_text);   // 상대방 채팅 시간

            // 기본 이미지 연결 설정
            id_image = (ImageView)itemView.findViewById(R.id.default_image);    // 상대방 프로필 이미지
        }

        // 바인딩 해주는 부분을 여기서 처리한다
        public void ConnectDatas(ChatHistory_Data chatDatas){
            // Attach values for each item
            // 데이터 불러오고
            String user_ID  = chatDatas.getID();                    // 상대방 유저 아이디
            String Massage   = chatDatas.getChat_Massage();         // 상대방 유저가 보낸 메세지
            String opponent_time = chatDatas.getChat_yourtime();    // 상대방 유저가 보낸 시간
            String profile_img_NAME = null;                                // 상대방 유저 프로필 이미지


                    // 데이터 연결 하고
            NameText.setText(user_ID); // 아이디 = 이름 연결
            ChatingText.setText(Massage); // 채팅 메세지 연결
            Chating_Time.setText(opponent_time); // 채팅 시간 연결
            Log.e("아이디 ",NameText.getText().toString());


            // 시연을 위한 하드코딩 예외 처리 원래 코드 짜야됨..
            if(user_ID.equals("sudo888")){
                profile_img_NAME = "ex_save_335218";
            }
            else if(user_ID.equals("masury2011")){
                profile_img_NAME = "ex_save_94019";
            }
            else if(user_ID.equals("wheeing")){
                profile_img_NAME = "ex_save_22454";
            }

            /**
             * 내 앱에 연결 할 시에
             * 외부 ip 에 따라서 이미지 설정할 수 있도록 변경해야 함
             */
                Glide.with(id_image.getContext())
                        .load("http://49.247.208.191/AndroidHive/Moim_default_IMG/"+profile_img_NAME+".jpg")
                        .placeholder(R.drawable.ic_account_circle)
                        .centerCrop()
                        .crossFade()
                        .bitmapTransform(new CropCircleTransformation(id_image.getContext()))   // 원형크롭 라이브러리 사용함 = compile 'jp.wasabeef:glide-transformations:2.0.1'
                        .override(100,100)  // 리소스의 이미지를 강제적으로 작게 줄여서 보여주려고 할대 사용
                        .into(id_image);

        }
    }// ChatViewHolder 끝

    /**
     * 2 번째 뷰홀더
     * -------- ??? 님이 접속하셨습니다. --------
     * 를 보여 주기 위한 뷰홀더
     */
    public class AlramViewHolder extends RecyclerView.ViewHolder {

        // 채팅 상단 텍스트
        private TextView centerTextView;

        public AlramViewHolder(View itemView) {
            super(itemView);
            // Initiate view
            centerTextView =(TextView)itemView.findViewById(R.id.text);
        }
        // 바인딩 해주는 부분을 여기서 처리한다
        public void showCenterTEXT(LineText_Data lineText){
            // Attach values for each item
            String centertext = lineText.getcenterText();
            centerTextView.setText(centertext); // 중앙 텍스트 연결
        }
    }

    /**
     * 3 번재 뷰홀더
     * 내가 쓴 채팅을 보여주기 위한 뷰 홀더
     */
    public class MyTEXT_ViewHolder extends RecyclerView.ViewHolder {

        // 내 채팅 텍스트
        private TextView My_massage;
        // 내가 채팅 보낸 시간
        private TextView my_time;


        // 내채팅 뷰홀더 생성자
        public MyTEXT_ViewHolder(View itemView) {
            super(itemView);
            // Initiate view
            My_massage =(TextView)itemView.findViewById(R.id.my_massage_textView);
            my_time =(TextView)itemView.findViewById(R.id.my_time);
        }
        // 바인딩 해주는 부분을 여기서 처리한다
        public void set_My_TEXT(My_Chat_Data my_chat_data){
            // Attach values for each item
            String my_massage = my_chat_data.getMyText();   // 택스트 가져오기
            Log.e("내가 보낸 메세지 = ",my_massage);
            My_massage.setText(my_massage); // 내 메세지 텍스트 연결
            String my_send_time = my_chat_data.getMySend_Time();   // 택스트 가져오기
            Log.e("내가 보낸 시간 = ",my_send_time);
            my_time.setText(my_send_time); // 보낸 시간 설정

        }
    }

}

