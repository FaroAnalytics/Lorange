package com.faroanalytics.lorange;


import android.content.ActivityNotFoundException;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Matrix;
import android.media.ExifInterface;
import android.net.Uri;
import android.os.AsyncTask;
import android.widget.ImageView;
import android.widget.Toast;

import java.io.IOException;
import java.io.InputStream;
import java.net.MalformedURLException;
import java.net.URL;
import java.net.URLConnection;

public class DownloadImage extends AsyncTask<String, Void, Bitmap> {
    private ImageView productImage;

    public DownloadImage(ImageView productImage) {this.productImage = productImage;}

    @Override
    protected Bitmap doInBackground(String... params) {
        String url = params[0];
        try {
            URLConnection connection = new URL(url).openConnection();
            connection.setConnectTimeout(1000 * 10);
            connection.setReadTimeout(1000 * 10);
            return BitmapFactory.decodeStream((InputStream) connection.getContent(), null, null);
        } catch (MalformedURLException e) {
            e.printStackTrace();
            return null;
        }catch (IOException e) {
            e.printStackTrace();
            return null;
        }
    }

    @Override
    protected void onPostExecute(Bitmap bitmap) {
        if (bitmap != null) {
            this.productImage.setImageBitmap(resizeBitmap(bitmap));
        }
    }

    public Bitmap resizeBitmap(Bitmap bitmap) {
        Bitmap squared;
        if (bitmap.getWidth() > bitmap.getHeight()) {
            squared = Bitmap.createBitmap(bitmap,
                    (bitmap.getWidth()-bitmap.getHeight())/2,
                    1,
                    bitmap.getWidth()-(bitmap.getWidth()-bitmap.getHeight())/2,
                    bitmap.getHeight()-1);
        } else if (bitmap.getWidth() < bitmap.getHeight()) {
            squared = Bitmap.createBitmap(bitmap,
                    1,
                    (bitmap.getHeight()-bitmap.getWidth())/2,
                    bitmap.getWidth()-1,
                    bitmap.getHeight()-(bitmap.getHeight()-bitmap.getWidth())/2);
        } else {squared = bitmap;}
        Bitmap resized = Bitmap.createScaledBitmap(squared, 140, 140, true);
        return resized;
    }
}


