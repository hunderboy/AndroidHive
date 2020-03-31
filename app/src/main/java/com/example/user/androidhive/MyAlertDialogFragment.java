package com.example.user.androidhive;

import android.app.AlertDialog;
import android.app.Dialog;
import android.app.DialogFragment;
import android.content.DialogInterface;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.util.Log;

/**
 * Created by user on 2017-12-09.
 */

public class MyAlertDialogFragment extends DialogFragment {

    public static MyAlertDialogFragment newInstance(String title){
        MyAlertDialogFragment frag = new MyAlertDialogFragment();
        Bundle args = new Bundle();
        args.putString("title", title);
        frag.setArguments(args);
        return frag;
    }

    @NonNull
    @Override
    public Dialog onCreateDialog(Bundle savedInstanceState) {
        //return super.onCreateDialog(savedInstanceState);

        String title = getArguments().getString("title");
        return new AlertDialog.Builder(getActivity())
//                .setIcon(R.mipmap.ic_launcher)
                .setTitle(title)
                .setPositiveButton("카메라", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        Log.i("MyLog", "카메라 버튼이 눌림");

                    }
                })
                .setPositiveButton("갤러리", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        Log.i("MyLog", "갤러리 버튼이 눌림");

                    }
                })
                .setNegativeButton("취소", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        Log.i("MyLog", "취소 버튼이 눌림");
                    }
                })
                .create();
    }
}
