package org.happyhorse.naivesear4ch.ui.theme;

import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;
import androidx.lifecycle.ViewModel;
//TODO:not used
public class ThemeViewModel extends ViewModel {

    private MutableLiveData<String> mText;

    public ThemeViewModel() {
        mText = new MutableLiveData<>();
        mText.setValue("This is slideshow fragment");
    }

    public LiveData<String> getText() {
        return mText;
    }
}