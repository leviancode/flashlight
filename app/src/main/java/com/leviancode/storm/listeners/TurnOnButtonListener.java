package com.leviancode.storm.listeners;

import android.view.View;
import com.leviancode.storm.MainActivity;

public class TurnOnButtonListener implements View.OnClickListener {
    private MainActivity mainActivity;

    public TurnOnButtonListener(MainActivity mainActivity) {
        this.mainActivity = mainActivity;
    }

    @Override
    public void onClick(View v) {
        mainActivity.getFlashLight().turnOnOff();
    }
}
