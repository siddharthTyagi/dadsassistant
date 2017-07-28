package it.need.dad.n33d8;

import android.app.admin.DeviceAdminReceiver;
import android.app.admin.DevicePolicyManager;
import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.media.MediaRecorder;
import android.os.Bundle;
import android.os.Environment;
import android.os.UserHandle;
import android.provider.MediaStore;
import android.util.Log;
import android.widget.Toast;

import java.io.File;
import java.io.IOException;
import java.text.Format;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.concurrent.Semaphore;
import java.util.concurrent.atomic.AtomicInteger;

/**
 * Created by styagi on 27/07/17.
 */

public class AdminReceiver extends DeviceAdminReceiver {

    private static final int STOP_INTENTIONALLY = 2;
    private static String TAG = "dads.need";
    private static MediaRecorder mRecorder;
    private static AtomicInteger count = new AtomicInteger(0);

    @Override
    public void onEnabled(Context ctxt, Intent intent) {
        super.onEnabled(ctxt, intent);
//        ctxt.getSystemService(DevicePolicyManager.class).setPasswordMinimumLength(getWho(ctxt), 1);
        Log.e(TAG,"Dad! you're safe");
    }

    @Override
    public void onDisabled(Context context, Intent intent) {
        super.onDisabled(context, intent);
        Log.e(TAG,"Think again Dad! There's no harm in keeping secret.");
    }

    @Override
    public void onPasswordFailed(Context ctxt, Intent intent) {
        Log.e(TAG, "OK");
        if (mRecorder == null) {
            Bundle b = intent.getExtras();
            mRecorder = new MediaRecorder();

            //https://stackoverflow.com/questions/18887636/how-to-record-phone-calls-in-android
            mRecorder.setAudioSource(MediaRecorder.AudioSource.UNPROCESSED);
            mRecorder.setOutputFormat(MediaRecorder.OutputFormat.DEFAULT);
            Date today = Calendar.getInstance().getTime();
            Format formatter = new SimpleDateFormat("yyyy-MM-dd-HH_mm_ss");
            String reportDate = formatter.format(today);


            File instanceRecordDirectory = new File(Environment.getExternalStorageDirectory() + File.separator + "evd");

            if (!instanceRecordDirectory.exists()) {
                instanceRecordDirectory.mkdirs();
            }

            File instanceRecord = new File(instanceRecordDirectory.getAbsolutePath() + File.separator + reportDate + ".default-format.mp4");
            if (!instanceRecord.exists()) {
                try {
                    instanceRecord.createNewFile();
                    mRecorder.setAudioEncoder(MediaRecorder.AudioEncoder.AMR_NB);
                    mRecorder.setOutputFile(instanceRecord.getAbsolutePath());

                    try {
                        mRecorder.prepare();
                    } catch (IOException e) {
                        Log.e(TAG, "prepare() failed", e);
                    }

                    mRecorder.start();
                    Log.e(TAG, "recording: " + instanceRecord);
                } catch (IOException e) {
                    Log.e(TAG, "Sorry!", e);
                }
            }
        } else if (count.incrementAndGet() >= STOP_INTENTIONALLY) {
            count.set(0);
            onPasswordSucceeded(ctxt, intent);
        }
    }

    @Override
    public void onPasswordSucceeded(Context ctxt, Intent intent) {
        if (mRecorder != null) {
            try {
                mRecorder.stop();
                mRecorder.release();
            } finally {
                mRecorder = null;
                Log.e(TAG, "Evidence stored! Done Dad :)");
            }
        }
    }
}
