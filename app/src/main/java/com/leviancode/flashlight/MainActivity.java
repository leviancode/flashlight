package com.leviancode.flashlight;
import android.Manifest;
import android.content.pm.PackageManager;
import android.os.Bundle;
import android.view.View;
import android.widget.ImageButton;
import android.widget.SeekBar;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;


public class MainActivity extends AppCompatActivity {
    private ImageButton flashOnImgButton;
    private SeekBar seekBar;
    private TextView freqTextView;
    private final int CAMERA_REQUEST_CODE = 2;
    private FlashLight flashLight;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        freqTextView = findViewById(R.id.freqTextView);
        flashOnImgButton = findViewById(R.id.flashButton);
        seekBar = findViewById(R.id.seekBar);
        seekBar.setOnSeekBarChangeListener(new SeekBarListener());

        askPermission(Manifest.permission.CAMERA,CAMERA_REQUEST_CODE);
    }


    protected void askPermission(String permission,int requestCode) {
        if (ContextCompat.checkSelfPermission(this, permission)!= PackageManager.PERMISSION_GRANTED) {
            // We don't have permission
            ActivityCompat.requestPermissions(this, new String[]{permission}, requestCode);
        }
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        if (requestCode == CAMERA_REQUEST_CODE) {
            if (grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                Toast.makeText(this, "Camera Permission Granted", Toast.LENGTH_LONG).show();
            } else {
                Toast.makeText(this, "Camera Permission Denied", Toast.LENGTH_LONG).show();
                flashOnImgButton.setEnabled(false);
            }
        }
    }

    public void onFlashButtonClicked(View view) {
        if(flashLight == null){
            flashLight = new FlashLight(this);
        }
        if (flashLight.isFlashOn()){
            flashOnImgButton.setImageResource(R.drawable.switch_off);
        }else{
            flashOnImgButton.setImageResource(R.drawable.switch_on);
        }
        flashLight.turnOnOff();
    }

    class SeekBarListener implements SeekBar.OnSeekBarChangeListener {
        @Override
        public void onProgressChanged(SeekBar seekBar, int progress, boolean fromUser) {
        }

        @Override
        public void onStartTrackingTouch(SeekBar seekBar) {
        }

        @Override
        public void onStopTrackingTouch(SeekBar seekBar) {
            int freq = seekBar.getProgress();
            freqTextView.setText(String.valueOf(freq));
            flashLight.setFreq(freq);
        }
    }
}