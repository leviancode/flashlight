package com.leviancode.storm.listeners;

import android.widget.SeekBar;
import com.leviancode.storm.MainActivity;

public class SeekBarListener implements SeekBar.OnSeekBarChangeListener {
    private MainActivity mainActivity;
    private int freq;

    public SeekBarListener(MainActivity mainActivity) {
        this.mainActivity = mainActivity;
    }

    @Override
    public void onProgressChanged(SeekBar seekBar, int progress, boolean fromUser) {

    }

    @Override
    public void onStartTrackingTouch(SeekBar seekBar) {

    }

    @Override
    public void onStopTrackingTouch(SeekBar seekBar) {
        freq = seekBar.getProgress();
        mainActivity.getFreqTextView().setText(String.valueOf(freq));
        mainActivity.getFlashLight().setFreq(freq);
    }
}
