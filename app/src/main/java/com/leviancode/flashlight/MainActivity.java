package com.leviancode.flashlight;
import android.Manifest;
import android.content.pm.PackageManager;
import android.os.Bundle;
import android.view.View;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.SeekBar;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;


public class MainActivity extends AppCompatActivity {
    private TextView mFreqTextView;
    private FlashLight mFlashLight;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        mFreqTextView = findViewById(R.id.freqTextView);
        SeekBar seekBar = findViewById(R.id.seekBar);
        seekBar.setOnSeekBarChangeListener(new SeekBarListener());

        mFlashLight = new FlashLight(this);
    }

    public void onFlashButtonClicked(View view) {
        if (mFlashLight.isFlashOn()){
            ((ImageView)view).setImageResource(R.drawable.switch_off);
        }else{
            ((ImageView)view).setImageResource(R.drawable.switch_on);
        }
        mFlashLight.turnOnOff();
    }

    class SeekBarListener implements SeekBar.OnSeekBarChangeListener {
        @Override
        public void onProgressChanged(SeekBar seekBar, int progress, boolean fromUser) {
            mFreqTextView.setText(String.valueOf(progress));
        }

        @Override
        public void onStartTrackingTouch(SeekBar seekBar) {
        }

        @Override
        public void onStopTrackingTouch(SeekBar seekBar) {
            mFlashLight.setFreq(seekBar.getProgress());
        }
    }
}