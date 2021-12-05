package org.happyhorse.naivesearch.ui.reset;

import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;
import androidx.lifecycle.ViewModel;
//TODO:not used
public class ResetViewModel extends ViewModel {

    private MutableLiveData<String> mText;

    public ResetViewModel() {
        mText = new MutableLiveData<>();
        mText.setValue("This is gallery fragment");
    }

    public LiveData<String> getText() {
        return mText;
    }
}