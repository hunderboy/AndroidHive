package com.example.user.androidhive;

import android.app.AlertDialog;
import android.app.Dialog;
import android.app.DialogFragment;
import android.app.TimePickerDialog;
import android.os.Build;
import android.os.Bundle;
import android.support.annotation.RequiresApi;
import android.text.format.DateFormat;
import android.util.Log;
import android.widget.Button;
import android.widget.TimePicker;

import java.util.Calendar;

import static com.example.user.androidhive.Moim_Make_Activity.Clock_num;

/**
 * Created by user on 2017-12-09.
 */

public class TimePickerFragment extends DialogFragment implements TimePickerDialog.OnTimeSetListener {

    // 오전,오후, 시, 분 = 전역변수 화
    static String aMpM;
    static int currentHour;    // 현재 시간
    static int using_minute;
    // static 으로 하면 액티비티가 죽어도 데이터는 살아 있다. 메모리에 남는다는 예기
    static int button1;

    int hour;
    int minute;

    int current_time;

    @RequiresApi(api = Build.VERSION_CODES.N)
    @Override
    public Dialog onCreateDialog(Bundle savedInstanceState) {

        //현재 시간을 타임 피커의 초기값으로 사용
        final Calendar c = Calendar.getInstance();  // 캘린더 변수 현재 시간 설정
//       current_time = c.get(Calendar.AM_PM); // 시간 - 현재 시간 설정
        hour = c.get(Calendar.HOUR_OF_DAY); // 시간 - 현재 시간 설정
        minute = c.get(Calendar.MINUTE);    // 분 - 현재 분 설정


        if(button1 == 1){
//            aaa = c.get(Calendar.AM_PM); // 시간 - 현재 시간 설정
            hour = currentHour;     // 시간 - 선택된 시간 설정
            minute = using_minute;  // 분 - 선택된 분 설정
            Log.e("button1 == 1 조건문으로 들어옴",""+hour+minute);
        }
//        else if(Clock_num == 2){
//            int aaa = c.get(Calendar.AM_PM); // 시간 - 현재 시간 설정
//            int hour = c.get(Calendar.HOUR_OF_DAY); // 시간 - 현재 시간 설정
//            int minute = c.get(Calendar.MINUTE);    // 분 - 현재 분 설정
//        }


        TimePickerDialog tpd = new TimePickerDialog(getActivity(),
                AlertDialog.THEME_DEVICE_DEFAULT_DARK, this,
                hour, minute, DateFormat.is24HourFormat(getActivity()));

        //타임 피커의 타이틀 설정
//        TextView tvTitle = new TextView(getActivity());
//        tvTitle.setText("TimepickerDialog 타이틀");
//        tvTitle.setBackgroundColor(Color.parseColor("#ffEEE8AA"));
//        tvTitle.setPadding(5, 3, 5, 3);
//        tvTitle.setGravity(Gravity.CENTER_HORIZONTAL);
//        tpd.setCustomTitle(tvTitle);
        return tpd;
    }



    @Override
    public void onTimeSet(TimePicker view, int hourOfDay, int minute) { // 시간이 선택된 후 완료버튼 누르면 처리되는 메소드

        // 모임 시간 설정을 위해 선언
        // 추가 화면에서
        Button set_clock1 = (Button) getActivity().findViewById(R.id.set_clock1);   // 시간 설정 1
        Button set_clock2 = (Button) getActivity().findViewById(R.id.set_clock2);   // 시간 설정 2
        Button set_apply_clock1 = (Button) getActivity().findViewById(R.id.set_apply_clock1);   // 시간 설정 2
        Button set_apply_clock2 = (Button) getActivity().findViewById(R.id.set_apply_clock2);   // 시간 설정 2

        // 수정 화면에서
//        Button Modify_set_clock1 = (Button) getActivity().findViewById(R.id.Modify_set_clock1);   // 시간 설정 1
//        Button Modify_set_clock2 = (Button) getActivity().findViewById(R.id.Modify_set_clock2);   // 시간 설정 2
//        Button Modify_set_apply_clock1 = (Button) getActivity().findViewById(R.id.Modify_set_apply_clock1);   // 시간 설정 2
//        Button Modify_set_apply_clock2 = (Button) getActivity().findViewById(R.id.Modify_set_apply_clock2);   // 시간 설정 2

        aMpM = "오전"; // 오전,오휴를 구분짓기 위해서

        if(hourOfDay > 11){ // 11시가 넘으면
            aMpM = "오후";
        }
        if(hourOfDay > 11){ // 11시가 넘으면
            currentHour = hourOfDay-12;
        }
        else{
            currentHour = hourOfDay;
        }

        using_minute = minute;  // 사용되는 minute

//        tv.setText("설정된 시간은.. \n\n");
//        tv.setText(String.valueOf(hourOfDay) + "= String.valueOf(hourOfDay).. \n\n");   // 오후가 되면 숫자가 +12가 된다는 거다
//
//        tv.setText( String.valueOf(currentHour) + "시 " + String.valueOf(minute) + "분\n"); // 시간 설정
        // 만약 내가 오전 8시 40 분을 선택 = 8 시 40 분
        // 만약 내가 오후 8시 40 분은 선택 = 20시 40 분

        // Clock_num 번호에 따라
        if(Clock_num == 1) {
            // 추가 화면의
            set_clock1.setText(aMpM + " " + String.valueOf(currentHour) + "시 " + String.valueOf(using_minute) + "분");    // 첫번째 시각 버튼에 시간 설정
            Log.e("set_clock1 = ",set_clock1.getText().toString());
//            button1 = 1;
            // 수정 화면의
//            Modify_set_clock1.setText(aMpM + " " + String.valueOf(currentHour) + "시 " + String.valueOf(using_minute) + "분");    // 첫번째 시각 버튼에 시간 설정

        }
        else if(Clock_num == 2){
            // 추가 화면의
            set_clock2.setText(aMpM + " "  + String.valueOf(currentHour) + "시 " + String.valueOf(using_minute) + "분");   // 두번째 시각 버튼에 시간 설정
            Log.e("set_clock2 = ",set_clock2.getText().toString());
            // 수정 화면의
//            Modify_set_clock2.setText(aMpM + " "  + String.valueOf(currentHour) + "시 " + String.valueOf(using_minute) + "분");   // 두번째 시각 버튼에 시간 설정
        }
        else if(Clock_num == 3){
            // 추가 화면의
            set_apply_clock1.setText(aMpM + " "  + String.valueOf(currentHour) + "시 " + String.valueOf(using_minute) + "분");   // 두번째 시각 버튼에 시간 설정
            Log.e("set_apply_clock1 = ",set_apply_clock1.getText().toString());
            // 수정 화면의
//            Modify_set_apply_clock1.setText(aMpM + " "  + String.valueOf(currentHour) + "시 " + String.valueOf(using_minute) + "분");   // 두번째 시각 버튼에 시간 설정
        }
        else if(Clock_num == 4){
            // 추가 화면의
            set_apply_clock2.setText(aMpM + " "  + String.valueOf(currentHour) + "시 " + String.valueOf(using_minute) + "분");   // 두번째 시각 버튼에 시간 설정
            Log.e("set_apply_clock2 = ",set_apply_clock2.getText().toString());
            // 수정 화면의
//            Modify_set_apply_clock2.setText(aMpM + " "  + String.valueOf(currentHour) + "시 " + String.valueOf(using_minute) + "분");   // 두번째 시각 버튼에 시간 설정
        }
    }// onTimeSet 끝

}

