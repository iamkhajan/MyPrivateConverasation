package com.example.khajan.myconversationprivacy;

import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.Environment;
import android.os.FileObserver;
import android.util.Log;

import com.example.khajan.myconversationprivacy.service.GlobalTouchService;

import org.greenrobot.eventbus.EventBus;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.io.RandomAccessFile;
import java.nio.channels.FileChannel;

/**
 * Created by khajan on 12/12/16.
 */

public class MyFileObserver extends FileObserver {


    String sdcardPath = Environment.getExternalStorageDirectory().getAbsolutePath();
    String originalImageDirPath;


    private static final String TAG = "MyFileObserver";
    boolean isPresent;

    String absolutePath;
    Context mContext;


    public MyFileObserver(String path, Context context) {
        super(path);
        absolutePath = path;
        mContext = context;
        originalImageDirPath = absolutePath + "OriginalImageFolder/";
    }

    public MyFileObserver(String path, int mask) {
        super(path, mask);
    }

    @Override
    public void onEvent(int event, String path) {

        if (path == null)
            return;

       /* if (event == FileObserver.ACCESS) {
            Log.d(TAG, "onEvent: file accessed" + path);
        }
        if (event == FileObserver.CLOSE_NOWRITE) {
            Log.d(TAG, "onEvent: file close no write" + path);
        }
        if (event == FileObserver.CREATE) {
            Log.d(TAG, "onEvent: file created" + path);
        }
        if (event == FileObserver.CLOSE_WRITE) {
            Log.d(TAG, "onEvent: file close write" + path);
        }*/

        if ((FileObserver.ACCESS & event) != 0) {
            //handle deleted file
//            if (!isPresent) {
            if (true) {
//                Log.d(TAG, "onEvent: file accessed " + path);
//                makeCopyOfFile(path);
                Intent globalService = new Intent(mContext, GlobalTouchService.class);
                mContext.startService(globalService);
                makeCopyOfFile(path, 0);
            }
        }

        //data was written to a file
        if ((FileObserver.CLOSE_NOWRITE & event) != 0) {
            Log.d(TAG, "KhajanPandeyonEvent: file close no write " + path);
        }
        if ((FileObserver.CREATE & event) != 0) {
            Log.d(TAG, "KhajanPandeyonEvent: file created " + path);
        }
        if ((FileObserver.CLOSE_WRITE & event) != 0) {
            Log.d(TAG, "KhajanPandeyonEvent: file close write " + path);
        }

    }

    private synchronized void makeCopyOfFile(String foundPath) {

        if (MyApplication.imageList.contains(foundPath)) {
            return;
        }
        /**just for time being*/
        if (foundPath.equalsIgnoreCase("Khajan.JPG"))
            return;


       /* Intent intent = new Intent(mContext, LockScreen.class);
        mContext.startActivity(intent);*/

//        if (foundPath.equalsIgnoreCase("IMG-20161213-WA0002.jpg")) {
        String nameOfFile[] = foundPath.split("\\.");
        String newFileName = nameOfFile[0] + "copy.jpg";
        File originalImage = new File(absolutePath + foundPath);
        File pseudoImage = new File(absolutePath + "Khajan.JPG");

        if (!pseudoImage.exists()) {
            Log.d(TAG, "makeCopyOfFile: pseudo image not present " + pseudoImage.getName());
            copyPseudoImage();
            makeCopyOfFile(foundPath);
            return;
        }

        /**Adding to arrayList*/
        MyApplication.imageList.add(foundPath);


        /**first copy from pseudo image so that it will be
         *  available for next time */
        File pseudoWorkingCopy = new File(absolutePath + "KhajanWorkingCopy.JPG");
        copy(pseudoImage, pseudoWorkingCopy);

        /**we will be working on pseudoWorkingCopy*/
        Log.d(TAG, "makeCopyOfFile: pseudoWorkingCopy has been created " + pseudoWorkingCopy.getName());

        String copiedFileName = absolutePath + newFileName;
        File originalImageCopy = new File(copiedFileName);

        /**coping original image */

        copy(originalImage, originalImageCopy);
        Log.d(TAG, "makeCopyOfFile: originalImageCopy has been created " + originalImageCopy.getName());

        if (pseudoWorkingCopy.exists()) {
            originalImage.delete();
            pseudoWorkingCopy.renameTo(originalImage);
            Log.d(TAG, "makeCopyOfFile: after renaming pseudo image name is : " + pseudoWorkingCopy.getName());
        }
//        Log.d(TAG, "makeCopyOfFile: pseudoWorkingCopy finally name is : " + pseudoWorkingCopy.getName());

    }

    private synchronized void makeCopyOfFile(String foundPath, int a) {

        if (MyApplication.imageList.contains(foundPath)) {
            return;
        }
        /**just for time being*/
        if (foundPath.equalsIgnoreCase("Khajan.JPG"))
            return;

        /*if (!isPresent) {

            Intent intent = new Intent(mContext, LockScreen.class);
            mContext.startActivity(intent);
            isPresent = true;
        }*/

        String nameOfFile[] = foundPath.split("\\.");
//        String newFileName = nameOfFile[0] + "copy.jpg";
        String newFileName = foundPath;
        File originalImage = new File(absolutePath + foundPath);
        File pseudoImage = new File(absolutePath + "Khajan.JPG");

        if (!pseudoImage.exists()) {
            Log.d(TAG, "makeCopyOfFile: pseudo image not present " + pseudoImage.getName());
            copyPseudoImage();
            makeCopyOfFile(foundPath, 0);
            return;
        }

        /**Adding to arrayList*/
        MyApplication.imageList.add(foundPath);


        /**first copy from pseudo image so that it will be
         *  available for next time */
        File pseudoImageWorkingCopy = new File(absolutePath + "KhajanWorkingCopy.JPG");
        copyFile(pseudoImage, pseudoImageWorkingCopy);

        /**we will be working on pseudoWorkingCopy*/
        Log.d(TAG, "makeCopyOfFile: pseudoWorkingCopy has been created " + pseudoImageWorkingCopy.getName());

        File ordinalNewDir = new File(originalImageDirPath);

        if (!ordinalNewDir.exists()) {
            ordinalNewDir.mkdir();
        }

        String copiedFileName = absolutePath + "OriginalImageFolder/" + newFileName;
        File originalImageCopy = new File(copiedFileName);


        /**coping original image */

        copyFile(originalImage, originalImageCopy);
        Log.d(TAG, "makeCopyOfFile: originalImageCopy has been created " + originalImageCopy.getName());


        EventBus.getDefault().post(true);

        if (pseudoImageWorkingCopy.exists()) {
            originalImage.delete();
            pseudoImageWorkingCopy.renameTo(originalImage);
            Log.d(TAG, "makeCopyOfFile: after renaming pseudo image name is : " + pseudoImageWorkingCopy.getName());
        }
    }

    public void copy(File src, File dst) {

        try {
            InputStream in = new FileInputStream(src);
            OutputStream out = new FileOutputStream(dst);

            // Transfer bytes from in to out
            byte[] buf = new byte[1024];
            int len;
            while ((len = in.read(buf)) > 0) {
                out.write(buf, 0, len);
            }
            in.close();
            out.flush();
            out.close();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }


    public static void copyFile(File sourceFile, File destFile) {


        try {
            if (!destFile.exists()) {
                destFile.createNewFile();
            }

            FileChannel source = null;
            FileChannel destination = null;
            try {
                source = new RandomAccessFile(sourceFile, "rw").getChannel();
                destination = new RandomAccessFile(destFile, "rw").getChannel();

                long position = 0;
                long count = source.size();

                source.transferTo(position, count, destination);
            } finally {
                if (source != null) {
                    source.close();
                }
                if (destination != null) {
                    destination.close();
                }
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
    }


    /* Checks if external storage is available for read and write */
    public boolean isExternalStorageWritable() {
        String state = Environment.getExternalStorageState();
        if (Environment.MEDIA_MOUNTED.equals(state)) {
            return true;
        }
        return false;
    }


    void copyPseudoImage() {
        Bitmap bm = BitmapFactory.decodeResource(mContext.getResources(), R.drawable.khajan);


        try {
            File file = new File(absolutePath, "Khajan.jpg");
            if (!file.exists()) {
                FileOutputStream outStream = new FileOutputStream(file);
                bm.compress(Bitmap.CompressFormat.JPEG, 100, outStream);
                outStream.flush();
                outStream.close();
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
    }


}
