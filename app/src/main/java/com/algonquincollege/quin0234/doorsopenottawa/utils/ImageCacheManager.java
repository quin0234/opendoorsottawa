package com.algonquincollege.quin0234.doorsopenottawa.utils;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;

import com.algonquincollege.quin0234.doorsopenottawa.model.BuildingPOJO;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;

/**
 * Cache images in persistent storage
 *
 * @author David Gasner (original)
 */
public class ImageCacheManager {

    public static Bitmap getBitmap(Context context, BuildingPOJO Building) {
        String fileName = context.getCacheDir() + "/" + Building.getImage().replaceAll("\\s+", "");
        fileName = fileName.replace("/images", "");
        File file = new File(fileName);
        if (file.exists()) {
            try {
                return BitmapFactory.decodeStream(new FileInputStream(file));
            } catch (FileNotFoundException e) {
                e.printStackTrace();
            }
        }
        return null;
    }

    public static void putBitmap(Context context, BuildingPOJO Building, Bitmap bitmap) {
        String fileName = context.getCacheDir() + "/" + Building.getImage().replaceAll("\\s+", "");
        fileName = fileName.replace("/images", "");
        File file = new File(fileName);
        FileOutputStream outputStream = null;
        try {
            outputStream = new FileOutputStream(file);
            bitmap.compress(Bitmap.CompressFormat.JPEG, 85, outputStream);
        } catch (IOException e) {
            e.printStackTrace();
        } finally {
            if (outputStream != null) {
                try {
                    outputStream.close();
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
        }
    }
}
