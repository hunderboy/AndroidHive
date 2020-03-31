package com.example.user.androidhive;

import android.support.v7.app.AppCompatActivity;

import java.text.DateFormat;
import java.text.SimpleDateFormat;

public class DialogsActivity extends AppCompatActivity {

    private static final DateFormat FORMATTER = SimpleDateFormat.getDateInstance();
//
//    @Override
//    protected void onCreate(Bundle savedInstanceState) {
//        super.onCreate(savedInstanceState);
//        setContentView(R.layout.activity_dialogs);
//
//        Button button_simple_dialog = (Button)findViewById(R.id.button_simple_dialog);
//
//        button_simple_dialog.setOnClickListener(new Button.OnClickListener() {
//            @Override
//            public void onClick(View view) {
//
//
//            }
//        });
//
//        ButterKnife.bind(this);
//    }
//
//
//
//
//
//
//    @OnClick(R.id.button_simple_dialog)
//    void onSimpleCalendarDialogClick() {
//        new SimpleCalendarDialogFragment().show(getSupportFragmentManager(), "test-simple-calendar");
//    }
//
//    public static class SimpleCalendarDialogFragment extends AppCompatDialogFragment implements OnDateSelectedListener {
//
//        private TextView textView;
//
//        @NonNull
//        @Override
//        public Dialog onCreateDialog(Bundle savedInstanceState) {
//            LayoutInflater inflater = getActivity().getLayoutInflater();
//
//            //inflate custom layout and get views
//            //pass null as parent view because will be in dialog layout
//            View view = inflater.inflate(R.layout.dialog_basic, null);
//
//            textView = (TextView) view.findViewById(R.id.textView);
//
//            MaterialCalendarView widget = (MaterialCalendarView) view.findViewById(R.id.calendarView);
//
//            widget.setOnDateChangedListener(this);
//
//            return new AlertDialog.Builder(getActivity())
//                    .setTitle(R.string.title_activity_dialogs)
//                    .setView(view)
//                    .setPositiveButton(android.R.string.ok, null)
//                    .create();
//        }
//
//        @Override
//        public void onDateSelected(@NonNull MaterialCalendarView widget, @NonNull CalendarDay date, boolean selected) {
//            textView.setText(FORMATTER.format(date.getDate()));
//        }
//    }



}
