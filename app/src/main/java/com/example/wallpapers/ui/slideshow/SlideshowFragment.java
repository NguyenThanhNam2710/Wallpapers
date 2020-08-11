package com.example.wallpapers.ui.slideshow;

import android.content.Intent;
import android.graphics.Color;
import android.os.AsyncTask;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;

import com.example.wallpapers.MainActivity;
import com.example.wallpapers.R;

import cn.pedant.SweetAlert.SweetAlertDialog;

public class SlideshowFragment extends Fragment {

    SweetAlertDialog mProgressDialog;

    public View onCreateView(@NonNull LayoutInflater inflater,
                             ViewGroup container, Bundle savedInstanceState) {
        View root = inflater.inflate(R.layout.fragment_slideshow, container, false);
        mProgressDialog = new SweetAlertDialog(getActivity(), SweetAlertDialog.WARNING_TYPE);
        mProgressDialog.setTitleText("Are you sure?");
        mProgressDialog.setContentText("Do you want to exit the program!");
        mProgressDialog.setConfirmText("Yes, exit it!");
        mProgressDialog.setCancelText("No,cancel please!");
        mProgressDialog.showCancelButton(true);
        mProgressDialog.setConfirmClickListener(new SweetAlertDialog.OnSweetClickListener() {
            @Override
            public void onClick(SweetAlertDialog sDialog) {
                sDialog.cancel();
                // Tao su kien ket thuc app
                Intent startMain = new Intent(Intent.ACTION_MAIN);
                startMain.addCategory(Intent.CATEGORY_HOME);
                startActivity(startMain);
                getActivity().finish();
            }
        });
        mProgressDialog.setCancelClickListener(new SweetAlertDialog.OnSweetClickListener() {
            @Override
            public void onClick(SweetAlertDialog sweetAlertDialog) {
                Intent intent = new Intent(getActivity().getApplicationContext(), MainActivity.class);
                startActivity(intent);

            }
        })
                .show();
        return root;
    }
}