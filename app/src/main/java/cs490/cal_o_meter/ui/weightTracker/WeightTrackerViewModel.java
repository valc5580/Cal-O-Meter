package cs490.cal_o_meter.ui.weightTracker;

import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;
import androidx.lifecycle.ViewModel;

public class WeightTrackerViewModel extends ViewModel {

    private MutableLiveData<String> mText;

    public WeightTrackerViewModel() {
        mText = new MutableLiveData<>();
        mText.setValue("This is dashboard fragment");
    }

    public LiveData<String> getText() {
        return mText;
    }
}