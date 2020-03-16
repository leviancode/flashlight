package com.leviancode.storm;
import android.Manifest;
import android.content.Context;
import android.content.pm.PackageManager;
import android.hardware.camera2.CameraAccessException;
import android.hardware.camera2.CameraManager;
import android.os.Bundle;
import android.view.View;
import android.widget.ImageButton;
import android.widget.SeekBar;
import android.widget.TextView;
import android.widget.Toast;

import com.leviancode.storm.listeners.SeekBarListener;
import com.leviancode.storm.listeners.TurnOnButtonListener;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;


public class MainActivity extends AppCompatActivity {

    private ImageButton turnOnImageButton;
    private SeekBar seekBar;
    private TextView freqTextView;
    private final int CAMERA_REQUEST_CODE = 2;
    private FlashLight flashLight;


    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);


        freqTextView = findViewById(R.id.freqTextView);
        turnOnImageButton = findViewById(R.id.turnOnImageButton);
        seekBar = findViewById(R.id.seekBar);

        askPermission(Manifest.permission.CAMERA,CAMERA_REQUEST_CODE);
        flashLight = new FlashLight(this);

        turnOnImageButton.setOnClickListener(new TurnOnButtonListener(this));
        seekBar.setOnSeekBarChangeListener(new SeekBarListener(this));

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
                turnOnImageButton.setEnabled(false);
            }
        }
    }

    public ImageButton getTurnOnImageButton() {
        return turnOnImageButton;
    }

    public SeekBar getSeekBar() {
        return seekBar;
    }

    public TextView getFreqTextView() {
        return freqTextView;
    }

    public FlashLight getFlashLight() {
        return flashLight;
    }
}