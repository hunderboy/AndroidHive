package com.example.user.androidhive.CalenderDecorate;

import android.content.Intent;
import android.os.Build;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.RequiresApi;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import com.example.user.androidhive.R;
import com.prolificinteractive.materialcalendarview.CalendarDay;
import com.prolificinteractive.materialcalendarview.CalendarMode;
import com.prolificinteractive.materialcalendarview.DayViewDecorator;
import com.prolificinteractive.materialcalendarview.DayViewFacade;
import com.prolificinteractive.materialcalendarview.MaterialCalendarView;
import com.prolificinteractive.materialcalendarview.OnDateSelectedListener;

import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;

/**
 * 2017-12-22 (수정)
 * 오늘 이전의 날짜가 선택되지 않게끔 예외처리 하였으나 오히려 그 다음달 이후의 날짜들도 선택되지 않는 오류가 생겼다,
 * 오늘에 해당하는 달에만 선택되지 않게끔 오류 처리를 할려고 하였으나 해결하지 못하였다.
 * 그래서 다른 방법으로 예외처리를 할 예정이다.
 */
public class CalenderActivity extends AppCompatActivity {

    MaterialCalendarView materialCalendarView;

    String year,month,day;  // 년,월,일
    String translation_day_of_the_week;  // 요일의 한국어 데이터를 담을 String (일, 월, 화, 수, 목, 금, 토)


    SimpleDateFormat mFormat_year = new SimpleDateFormat("yyyy"); // 년도 포맷
    SimpleDateFormat mFormat_month = new SimpleDateFormat("MM");  // 월 포맷
    static SimpleDateFormat mFormat_day = new SimpleDateFormat("dd");  // 일 포맷





    @RequiresApi(api = Build.VERSION_CODES.N)
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_calender);

        materialCalendarView = (MaterialCalendarView)findViewById(R.id.calendarView);

// 오늘 날짜 데이터를 받아서 최소 날짜가 오늘 데이터에 따라 변경되어야 함.
        long mNow = System.currentTimeMillis(); // 현재 년-월-일, 시-분-초 데이터를 가져온다.
        Date mDate = new Date(mNow);    // 가져온 현재 시각데이터를 Date 변수로 변환 시킨다.

        String Today_Year = mFormat_year.format(mDate); // String Today_Year 에 년도 값을 담는다.
        Log.e("Today_Year ="," "+Today_Year);
        String Today_Month = mFormat_month.format(mDate);   // String Today_Month 에 월 값을 담는다.
        Log.e("Today_Month ="," "+Today_Month);


        materialCalendarView.state().edit()
                .setFirstDayOfWeek(Calendar.SUNDAY)
                .setMinimumDate(CalendarDay.from(Integer.parseInt(Today_Year), Integer.parseInt(Today_Month)-1, 1))   // 최소 날짜 = 년도와 월 은 정수 형태로 변환해서 넣어야 한다.
                // 이렇게 설정하면 매년과 월 이 증가하면서 변할때 마다 그 이전의 날들은 없어진다. EX) 10월이 지나서 11월이 되면 10월은 없어진다.
                .setMaximumDate(CalendarDay.from(2030, 11, 31)) // 최대 날짜
                .setCalendarDisplayMode(CalendarMode.MONTHS)
                .commit();

        // addDecorators 로 달력에 효과를 줄수있습니다.
        materialCalendarView.addDecorators(new SundayDecorator(), new SaturdayDecorator(), new OneDayDecorator() );



        // 달력에 선택가능 불가능 에 대한 Decorator 클래스 연결
//        materialCalendarView.addDecorator(new PrimeDayDisableDecorator());  // 선택 불가능 클래스
        materialCalendarView.addDecorator(new EnableOneToTenDecorator());   // 선택 가능 클래스

        // Add a second decorator that explicitly enables days <= 10. This will work because
        //  decorators are applied in order, and the system allows re-enabling
//        materialCalendarView.addDecorator(new EnableOneToTenDecorator());



        // 날짜 선택 클릭 리스너
        materialCalendarView.setOnDateChangedListener(new OnDateSelectedListener() {
            @Override
            public void onDateSelected(@NonNull MaterialCalendarView widget, @NonNull CalendarDay date, boolean selected) {
//                Toast.makeText(MainActivity.this, "" + year + "/" + (month + 1) + "/" + dayOfMonth, Toast.LENGTH_SHORT).show();
//                Toast.makeText(MainActivity.this, "" + date + "/" + (month + 1), Toast.LENGTH_SHORT).show();

                Log.e("date="," "+date);    // 선택된 날짜 로그 찍기

                String 년도 = String.valueOf(date.getYear()); // Year 를 담는다.

                int 달 = date.getMonth()+1;  // 달은 +1 추가 시켜줘야 한다.

                // 각 변수의 로그를 찍어 본다.
                Log.e("년도 ="," "+년도);
                Log.e("date.getYear() ="," "+date.getYear());   // int 값 = date.getYear();
                Log.e("date.getDate() ="," "+date.getDate());   // 전체 달력 데이터 출력 = 요일 포함
                Log.e("date.getDay() ="," "+date.getDay());     // int 값 = date.getDay();
                Log.e("date.getMonth() ="," "+date.getMonth()); // int 값 = date.getDay();
                Log.e("date.toString() ="," "+date.toString());

//                  String.valueOf(date.getDate()) - 스트링으로 변환 시키는 함수

                String Day_of_the_week = String.valueOf(date.getDate()).substring(0,3);
                Log.e("date.요일 ="," "+Day_of_the_week);  // 요일만 추출되는지 한번 보자.


                if(Day_of_the_week.equals("Sun")){
                    translation_day_of_the_week = "(일)";
                }
                else if(Day_of_the_week.equals("Mon")){
                    translation_day_of_the_week = "(월)";
                }
                else if(Day_of_the_week.equals("Tue")){
                    translation_day_of_the_week = "(화)";
                }
                else if(Day_of_the_week.equals("Wed")){
                    translation_day_of_the_week = "(수)";
                }
                else if(Day_of_the_week.equals("Thu")){
                    translation_day_of_the_week = "(목)";
                }
                else if(Day_of_the_week.equals("Fri")){
                    translation_day_of_the_week = "(금)";
                }
                else if(Day_of_the_week.equals("Sat")){
                    translation_day_of_the_week = "(토)";
                }
                Log.e("스트링 요일 적용 ="," "+translation_day_of_the_week);

                // 년,월,일 데이터를 담는다.
                year = String.valueOf(date.getYear());
                month = String.valueOf(달);
                day = String.valueOf(date.getDay());

                TextView textView = (TextView)findViewById(R.id.textView);
                textView.setText(String.valueOf(date.getYear())+"."+String.valueOf(달)+"."+String.valueOf(date.getDay())+" "+translation_day_of_the_week);   // 년 월 일 요일

            }
        });// 리스너 끝


        Button OK = (Button)findViewById(R.id.OK_button);
        // 설정 클릭 리스너
        OK.setOnClickListener(new Button.OnClickListener() {
            @Override
            public void onClick(View view) {

                if(year == null){
                    Toast.makeText(CalenderActivity.this, "날짜를 선택하여 주세요.", Toast.LENGTH_SHORT).show();
                }
                else {
                    // 데이터 넘겨주기
                    Intent intent = new Intent(); // 키값, 밸류값
                    intent.putExtra("YEAR", year);          // 년
                    intent.putExtra("MONTH", month);        // 월
                    intent.putExtra("DAY", day);            // 일
                    intent.putExtra("Day_Of_The_Week", translation_day_of_the_week);// 요일
                    // 년,월,일 로그 찍어보기
                    Log.e("년도 =", " " + year);
                    Log.e("월 =", " " + month);
                    Log.e("일 =", " " + day);

                    setResult(RESULT_OK, intent);
                    finish();// 현재 액티비티 종료
                }
            }
        });

        // 취소 버튼 생성
        Button Cancel = (Button)findViewById(R.id.Cancel_button);
        // 취소 클릭 리스너
        Cancel.setOnClickListener(new Button.OnClickListener() {
            @Override
            public void onClick(View view) {
                finish();// 현재 액티비티 종료
            }
        });
    }// onCreate 끝


    /**
     *  날짜가 선택될수 없게 만드는 클래스
     *  선택될 수 없게 하는 목적  = 만약 오늘이 13일이면 오늘 13일을 제외한 14일 부터 선택이 가능하게끔 설정
     *  즉 1 ~ 13일까지는 선택 불가능 , 14 ~ 31일까지는 선택가능
     */
    private static class PrimeDayDisableDecorator implements DayViewDecorator {


        @Override
        public boolean shouldDecorate(CalendarDay day ) {
            Log.e("CalendarDay day = ",""+day);

            long mNow = System.currentTimeMillis(); // 현재 년-월-일, 시-분-초 데이터를 가져온다.
            Date mDate = new Date(mNow);    // 가져온 현재 시각데이터를 Date 변수로 변환 시킨다.
            Log.e("mDate = ",""+mDate);    // EX TODAY = 13


            boolean[] DAY_ARRAY = new boolean[32];  // 0~ 31 까지 32 가지 = 날짜 배열 생성
            String Today_day = mFormat_day.format(mDate);   // String Today_day 에 Day 값을 담는다. 일포맷에 따라 Day 만 추출하여 Today_day 에 담는 것이다.

            int TODAY = Integer.parseInt(Today_day);
            Log.e("오늘 = ",""+TODAY);    // EX TODAY = 13

            // 반복문 돌리기
            for(int i =0; i <= TODAY; i++){  // 오늘이 13 이면 0~13 까지 돈다. = 총 14번 돈다는 것이다.
                // TODAY 는 매일 12시에 값이 증가한다.
                DAY_ARRAY[i] = true;// true를 함으로써 날짜를  선택불가능으로 설정 => 0번째 칸은 필요 없고, 1~13 일까지를 불가능으로 설정
            }

            int TODAY_Plus1 = Integer.parseInt(Today_day)+1;    // 오늘이 13일 이면 , TODAY_Plus1 = 14 저장
            // 반복문 돌리기
            for(int i = TODAY_Plus1; i <= 31; i++){  // 오늘이 13 이면 0~13 까지 돈다.
                // TODAY 는 매일 12시에 값이 증가한다.
                DAY_ARRAY[i] = false;   // 선택 되게끔 설정
            }
            Log.e("DAY_ARRAY[] = ","0 ="+ DAY_ARRAY[0]+", 1 ="+DAY_ARRAY[1]+", 2 ="+DAY_ARRAY[2]+", 3 ="+DAY_ARRAY[3]);
            Log.e("DAY_ARRAY[day.getDay()]",""+ DAY_ARRAY[day.getDay()]);

            return DAY_ARRAY[day.getDay()];
//            return PRIME_TABLE[day.getDay()];   // 선택될수 없는 날짜 정보를 얻어서
        }

        @Override
        public void decorate(DayViewFacade view) {
            view.setDaysDisabled(true); // 선택될수 없게 끔 하라.
        }

        static boolean[] PRIME_TABLE = {
                false,  // 0?
                false,  // 1 일
                true,   // 2 일
                true,   // 3
                false,  // 4
                true,   // 5
                false,
                true, // 7
                false,
                false,
                false,
                true, // 11
                false,// 12
                true, // 13
                false,
                false,
                false,
                true, // 17
                false,
                true, // 19
                false,
                false,
                false,
                true, // 23
                false,
                false,
                false,
                false,
                false,
                true, // 29
                false,
                true, // 31
//                false,
//                false,
//                false, //PADDING
        };
    }

    private static class EnableOneToTenDecorator implements DayViewDecorator {

        @Override
        public boolean shouldDecorate(CalendarDay day) {    // 캘린더 날짜에서
            Log.e("선택가능 day.getDay() = ",""+day.getDay());
            return day.getDay() <= 10;  // 10일 과 같거나 이하의 날짜 들은
        }

        @Override
        public void decorate(DayViewFacade view) {  // 선택할수 있게끔 변경한다.
            view.setDaysDisabled(false);
        }
    }







}
