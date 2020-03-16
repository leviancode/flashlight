package com.leviancode.storm;

import android.content.Context;
import android.hardware.camera2.CameraAccessException;
import android.hardware.camera2.CameraManager;

import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.atomic.AtomicBoolean;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.concurrent.locks.Condition;
import java.util.concurrent.locks.Lock;
import java.util.concurrent.locks.ReentrantLock;

public class FlashLight {
    private MainActivity mainActivity;
    private CameraManager cameraManager;
    private AtomicBoolean isFlashOn = new AtomicBoolean(false);
    private String cameraId;
    private AtomicInteger freq = new AtomicInteger(0);
    private Lock lock = new ReentrantLock();
    private Condition strobeCondition = lock.newCondition();

    public FlashLight(MainActivity mainActivity) {
        this.mainActivity = mainActivity;
        setup();
    }

    private void setup(){
        cameraManager = (CameraManager) mainActivity.getSystemService(Context.CAMERA_SERVICE);
        try {
            cameraId = cameraManager.getCameraIdList()[0];
        } catch (CameraAccessException e) {}
        ExecutorService executorService = Executors.newSingleThreadExecutor();
        executorService.submit(new StrobeRunner());
    }

    public void turnOnOff() {

        try {
            changeButtonImg();
            cameraManager.setTorchMode(cameraId, !isFlashOn.getAndSet(!isFlashOn.get()));
            if (isStrobeOn()){
                lock.lock();
                try{
                    strobeCondition.signal();
                }finally {
                    lock.unlock();
                }
            }
        } catch (CameraAccessException e) {
        }
    }

    private void changeButtonImg(){
        if (isFlashOn.get())
            mainActivity.getTurnOnImageButton().setImageResource(R.drawable.button_off);
        else
            mainActivity.getTurnOnImageButton().setImageResource(R.drawable.button_on);
    }

    private boolean isStrobeOn() {
        return freq.get() > 0 && isFlashOn.get();
    }

    public void setFreq(int freq) {
        this.freq.set(freq);
        if (freq>0){
            lock.lock();
            try{
                strobeCondition.signal();
            }finally {
                lock.unlock();
            }
        }
    }

    public Condition getStrobeCondition() {
        return strobeCondition;
    }

    private class StrobeRunner implements Runnable{

        @Override
        public void run() {
            boolean flag = false;
            while (!Thread.currentThread().isInterrupted()) {
                try {
                    if (isStrobeOn()) {

                        Thread.sleep(10000 / freq.get());
                        cameraManager.setTorchMode(cameraId, flag);
                        flag = !flag;

                    } else {
                        cameraManager.setTorchMode(cameraId, isFlashOn.get());
                        lock.lock();
                        try {
                            strobeCondition.await();
                        } finally {
                            lock.unlock();
                        }
                    }
                } catch (InterruptedException | CameraAccessException e) {
                }
            }
        }
    }
}
