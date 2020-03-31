package com.example.user.androidhive.CalenderDecorate;

import com.prolificinteractive.materialcalendarview.CalendarDay;
import com.prolificinteractive.materialcalendarview.DayViewDecorator;
import com.prolificinteractive.materialcalendarview.DayViewFacade;
import com.prolificinteractive.materialcalendarview.spans.DotSpan;

/**
 * Created by user on 2017-12-11.
 */
// 날짜에 빨간점을 붙이는 특수한 효과를 줄려고 했는데 적용못함.
    // https://github.com/prolificinteractive/material-calendarview/blob/master/sample/src/main/java/com/prolificinteractive/materialcalendarview/sample/BasicActivity.java  에서 확인 해봐라.
public class EventDecorator implements DayViewDecorator {

    private int color;
    private CalendarDay dates;

    public EventDecorator(int color, CalendarDay dates) {
        this.color = color;
        this.dates = dates;
    }

    @Override
    public boolean shouldDecorate(CalendarDay day) {
        return dates!= null && day.equals(dates);
    }

    @Override
    public void decorate(DayViewFacade view) {
        view.addSpan(new DotSpan(5, color));
    }
}
