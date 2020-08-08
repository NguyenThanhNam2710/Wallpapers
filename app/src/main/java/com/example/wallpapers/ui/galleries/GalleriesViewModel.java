package com.example.wallpapers.ui.galleries;

import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;
import androidx.lifecycle.ViewModel;

public class GalleriesViewModel extends ViewModel {

    private MutableLiveData<String> mText;

    public GalleriesViewModel() {
        mText = new MutableLiveData<>();
        mText.setValue("This is gallery fragment");
    }

    public LiveData<String> getText() {
        return mText;
    }
}