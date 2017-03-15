package com.example.khajan.myconversationprivacy;

import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.os.Environment;
import android.os.FileObserver;
import android.provider.Settings;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.View;

import com.example.khajan.myconversationprivacy.service.GlobalTouchService;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.RandomAccessFile;
import java.nio.channels.FileChannel;
import java.nio.channels.FileLock;
import java.nio.channels.OverlappingFileLockException;

public class MainActivity extends AppCompatActivity {

    private static final String TAG = "MainActivity";


    String sdcardPath = Environment.getExternalStorageDirectory().getAbsolutePath();
    String whatsAppImagePath = sdcardPath + "/WhatsApp/Media/WhatsApp Images/";

    String dirPah = Environment.getExternalStorageDirectory().getAbsolutePath()
            + "/WhatsApp/Media/WhatsApp Images/";

    File imageDirectory = new File(dirPah);

    String filePath = Environment.getExternalStorageDirectory().getAbsolutePath()
            + "/WhatsApp/Media/WhatsApp Images/IMG-20161212-WA0002.jpg";

    File srcFile = new File(filePath);

    String tempFilePath = Environment.getExternalStorageDirectory().getAbsolutePath()
            + "/WhatsApp/Media/WhatsApp Images/IMG-20161212-WA00040.jpg";

    String tempFilePath2 = Environment.getExternalStorageDirectory().getAbsolutePath()
            + "/WhatsApp/Media/WhatsApp Images/IMG-20161212-WA00050.jpg";
    File destFile = new File(tempFilePath);
    File destFile2 = new File(tempFilePath2);


    String originalImageDirPath = dirPah + "OriginalImageFolder/";

    boolean value;

    Intent globalService;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);


        moveTaskToBack(true);


        globalService = new Intent(this, GlobalTouchService.class);

        findViewById(R.id.button).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (!value) {
                    value = true;
//                    getOriginalImageBack();


                    try {
                        startService(globalService);
                    } catch (Exception e) {
                        e.printStackTrace();
                    }

//                    Intent intent = new Intent(MainActivity.this, LockScreen.class);
//                    startActivity(intent);

                } else {

                    try {
                        stopService(globalService);
                    } catch (Exception e) {
                        e.printStackTrace();
                    }

                    value = false;
                }
            }
        });


        new Thread(new Runnable() {
            @Override
            public void run() {

                String sdcardPath = Environment.getExternalStorageDirectory().getAbsolutePath();
                String whatsAppImagePath = sdcardPath + "/WhatsApp/Media/WhatsApp Images/";

                FileObserver observer = createFileObserver(sdcardPath + "/WhatsApp/Media/WhatsApp Images/");

                MyFileObserver myFileObserver = new MyFileObserver(sdcardPath + "/WhatsApp/Media/WhatsApp Images/", MainActivity.this);
//                MyFileObserver myFileObserver = new MyFileObserver(sdcardPath + "/KhajanTest/");
                myFileObserver.startWatching();

//                observer.startWatching();

//                copy(srcFile, destFile);

                while (true) {
//                    fileAccessStatus();
//                    copy(srcFile, destFile);
                    try {
                        Thread.sleep(500);
                    } catch (InterruptedException e) {
                        e.printStackTrace();
                    }
                }

            }
        }).start();
    }


    private FileObserver createFileObserver(String path) {
        FileObserver observer = new FileObserver(path) {
            @Override
            public void onEvent(int event, final String file) {
                event &= FileObserver.ALL_EVENTS;
                Log.d(TAG, "onEvent: " + file + "event is " + event);
                if (event == FileObserver.ACCESS) {
                    Log.d(TAG, "onEvent: file accessed " + file);
                    getDirectory(file);
                }
                if (event == FileObserver.CREATE) {
                    Log.d(TAG, "onEvent: file created" + file);
                } //...
                if (event == FileObserver.DELETE) {
                    Log.d(TAG, "onEvent: file deleted" + file);
                } //...
                if (event == FileObserver.DELETE_SELF) {
                    Log.d(TAG, "onEvent: file deleted self" + file);
                } //...

            }
        };
        return observer;
    }


    private void fileAccessStatus() {

        try {
            Log.d(TAG, "fileAccessStatus: " + filePath);
            String fileName = filePath;
            File file = new File(fileName);

            String tempFilePath = Environment.getExternalStorageDirectory().getAbsolutePath()
                    + "/WhatsApp/Media/WhatsApp Images/IMG-20161212-WA00032.jpg";
            File destFile = new File(tempFilePath);
            if (file.exists()) {
                file.renameTo(destFile);
            }
            FileChannel channel = new RandomAccessFile(file, "rw").getChannel();
// Get an exclusive lock on the whole file
            Log.d(TAG, "fileAccessStatus: if open " + channel.isOpen());
            FileLock lock = channel.lock();
            try {
                lock = channel.tryLock();
                // Ok. You get the lock
                Log.d(TAG, "fileAccessStatus: got the lock");
            } catch (OverlappingFileLockException e) {
                // File is open by someone else
                Log.d(TAG, "fileAccessStatus: lock exception" + e.getMessage());
            } finally {
                Log.d(TAG, "fileAccessStatus: releasing lock");
                lock.release();
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
    }


    private void copy(File source, File destination) {

        try {
            long length = source.length();
            FileChannel input = new FileInputStream(source).getChannel();
            try {
                FileChannel output = new FileOutputStream(destination).getChannel();
                try {
                    for (long position = 0; position < length; ) {
                        position += input.transferTo(position, length - position, output);
                    }
                } finally {
                    output.close();
                }
            } finally {
                input.close();
            }
        } catch (IOException e) {
            e.printStackTrace();
            Log.d(TAG, "copy: exception " + e.getMessage());
        }
    }

    private synchronized void getDirectory(String encodedFile) {

        for (String file : imageDirectory.list()) {
            if (encodedFile.equalsIgnoreCase(file)) {
                Log.d(TAG, "getDirectory: " + file);
                String filePath = dirPah + encodedFile;

                String name[] = encodedFile.split("\\.");
                Log.d(TAG, "getDirectory dir name: " + name[0]);

                File destFile = new File(tempFilePath);
                File originalFile = new File(whatsAppImagePath);
//                copy(originalFile, destFile);
                if (originalFile.exists()) {
                    File from = new File(originalFile, encodedFile);
                    copy(from, new File(whatsAppImagePath, name[0] + 1));
                    File to = new File(originalFile, "Khajan.jpeg");
                    Log.d(TAG, "getDirectory: " + from.renameTo(to));
                }
            }
        }

    }


    public void usageAccessSettingsPage() {
        Intent intent = new Intent();
        intent.setAction(Settings.ACTION_USAGE_ACCESS_SETTINGS);
        intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
        Uri uri = Uri.fromParts("package", this.getPackageName(), null);
        intent.setData(uri);
        startActivity(intent);
    }


    private void getOriginalImageBack() {
        File file = new File(originalImageDirPath);


        for (String imageName : file.list()) {
            for (String renamedImage : MyApplication.imageList) {
                if (renamedImage.contains(imageName)) {
                    Log.d(TAG, "getOriginalImageBack: and name is " + renamedImage + "renamed name is : " + imageName);
                    break;
                }
            }
        }
    }
}
