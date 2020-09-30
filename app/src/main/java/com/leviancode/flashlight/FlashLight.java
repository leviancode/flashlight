package com.leviancode.flashlight;

import android.content.Context;
import android.hardware.camera2.CameraAccessException;
import android.hardware.camera2.CameraManager;
import android.util.Log;
import android.widget.Toast;

import java.util.Objects;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.atomic.AtomicBoolean;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.concurrent.locks.Condition;
import java.util.concurrent.locks.Lock;
import java.util.concurrent.locks.ReentrantLock;

public class FlashLight {
    private String mCameraId;
    private CameraManager mCameraManager;
    private AtomicBoolean mIsFlashOn = new AtomicBoolean(false);
    private AtomicInteger mFreq = new AtomicInteger(0);
    private Lock mLock = new ReentrantLock();
    private Condition mStrobeCondition = mLock.newCondition();

    public FlashLight(Context context) {
        mCameraManager = (CameraManager) context.getSystemService(android.content.Context.CAMERA_SERVICE);
        try {
            mCameraId = mCameraManager.getCameraIdList()[0];
        } catch (CameraAccessException e) {
            Log.e("MainActivity", "CameraAccessException", e);
            Toast.makeText(context, "Failed to access camera", Toast.LENGTH_LONG).show();
        }
        ExecutorService executorService = Executors.newSingleThreadExecutor();
        executorService.submit(new StrobeRunner());
    }

    public void turnOnOff() {
        try {
            mCameraManager.setTorchMode(mCameraId, !mIsFlashOn.getAndSet(!mIsFlashOn.get()));
            if (isStrobeOn()){
                mLock.lock();
                try{
                    mStrobeCondition.signal();
                }finally {
                    mLock.unlock();
                }
            }
        } catch (CameraAccessException e) {
            Log.e("Flashlight", "CameraAccessException", e);
        }
    }

    private boolean isStrobeOn() {
        return mFreq.get() > 0 && mIsFlashOn.get();
    }

    public void setFreq(int freq) {
        this.mFreq.set(freq);
        if (freq>0){
            mLock.lock();
            try{
                mStrobeCondition.signal();
            }finally {
                mLock.unlock();
            }
        }
    }

    public boolean isFlashOn() {
        return mIsFlashOn.get();
    }

    private class StrobeRunner implements Runnable{

        @Override
        public void run() {
            boolean flag = false;
            while (!Thread.currentThread().isInterrupted()) {
                try {
                    if (isStrobeOn()) {
                        Thread.sleep(10000 / mFreq.get());
                        mCameraManager.setTorchMode(mCameraId, flag);
                        flag = !flag;
                    } else {
                        mCameraManager.setTorchMode(mCameraId, mIsFlashOn.get());
                        mLock.lock();
                        try {
                            mStrobeCondition.await();
                        } finally {
                            mLock.unlock();
                        }
                    }
                } catch (InterruptedException | CameraAccessException e) {
                    Log.e("Flashlight", Objects.requireNonNull(e.getMessage()));
                }
            }
        }
    }
}
