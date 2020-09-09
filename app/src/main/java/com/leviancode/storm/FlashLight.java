package com.leviancode.storm;

import android.content.Context;
import android.hardware.camera2.CameraAccessException;
import android.hardware.camera2.CameraManager;
import android.widget.Toast;

import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.atomic.AtomicBoolean;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.concurrent.locks.Condition;
import java.util.concurrent.locks.Lock;
import java.util.concurrent.locks.ReentrantLock;

public class FlashLight {
    private Context context;
    private CameraManager cameraManager;
    private AtomicBoolean isFlashOn = new AtomicBoolean(false);
    private String cameraId;
    private AtomicInteger freq = new AtomicInteger(0);
    private Lock lock = new ReentrantLock();
    private Condition strobeCondition = lock.newCondition();

    public FlashLight(MainActivity context) {
        this.context = context;
        setup();
    }

    private void setup(){
        cameraManager = (CameraManager) context.getSystemService(android.content.Context.CAMERA_SERVICE);
        try {
            cameraId = cameraManager.getCameraIdList()[0];
        } catch (CameraAccessException e) {
            Toast.makeText(context, "Failed to access camera", Toast.LENGTH_LONG).show();
        }
        ExecutorService executorService = Executors.newSingleThreadExecutor();
        executorService.submit(new StrobeRunner());
    }

    public void turnOnOff() {
        try {
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
            Toast.makeText(context, "Failed to access camera", Toast.LENGTH_LONG).show();
        }
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

    public boolean isFlashOn() {
        return isFlashOn.get();
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
