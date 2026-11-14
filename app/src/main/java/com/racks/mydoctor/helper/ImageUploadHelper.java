package com.racks.mydoctor.helper;

import android.content.Context;
import android.database.Cursor;
import android.graphics.Bitmap;
import android.graphics.Matrix;
import android.media.ExifInterface;
import android.net.Uri;
import android.os.Build;
import android.provider.MediaStore;
import android.util.Log;

import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.UploadTask;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.IOException;

public class ImageUploadHelper {


    public interface UploadListener {
        void onSuccess(String downloadableUrl);
        void onProgressChanged(double progress);
        void onFailure(String errorMsg);
    }
    public static String getPath(Context context, Uri uri) {
        String result = null;
        String[] proj = {MediaStore.Images.Media.DATA};
        Cursor cursor = context.getContentResolver().query(uri, proj, null, null, null);
        if (cursor != null) {
            if (cursor.moveToFirst()) {
                int column_index = cursor.getColumnIndexOrThrow(proj[0]);
                result = cursor.getString(column_index);
            }
            cursor.close();
        }
        if (result == null) {
            return "Not found";
        }
        return result;
    }

    public static void compressImage(Context context, Uri selectedImageUri, Bitmap bitmap1, UploadListener listener) {
        Matrix matrix2 = new Matrix();
        if (Build.VERSION.SDK_INT >= 24) {
            try {
                ExifInterface exif2 = new ExifInterface(getPath(context, selectedImageUri));
                int orientation2 = exif2.getAttributeInt("Orientation", 1);
                if (orientation2 == 3) {
                    matrix2.postRotate(180.0f);
                } else if (orientation2 == 6) {
                    matrix2.postRotate(90.0f);
                } else if (orientation2 == 8) {
                    matrix2.postRotate(270.0f);
                }
            } catch (IOException e5) {
                Log.e("ImageUploadError", "exception "+e5.getLocalizedMessage());
            }
            Bitmap rotatedBitmap22 = Bitmap.createBitmap(bitmap1, 0, 0, bitmap1.getWidth(), bitmap1.getHeight(), matrix2, true);
            ByteArrayOutputStream baos22 = new ByteArrayOutputStream();
            rotatedBitmap22.compress(Bitmap.CompressFormat.JPEG, 20, baos22);
            uploadImage(baos22, listener);
        } else {
            Bitmap rotatedBitmap222 = Bitmap.createBitmap(bitmap1, 0, 0, bitmap1.getWidth(), bitmap1.getHeight(), matrix2, true);
            ByteArrayOutputStream baos222 = new ByteArrayOutputStream();
            rotatedBitmap222.compress(Bitmap.CompressFormat.JPEG, 20, baos222);
            uploadImage(baos222, listener);
        }
    }

    public static void uploadImage(ByteArrayOutputStream byteArrayOutputStream, UploadListener listener) {
        byte[] data = byteArrayOutputStream.toByteArray();
        ByteArrayInputStream stream = new ByteArrayInputStream(data);
        StorageReference storageReference = FirebaseStorage.getInstance().getReference();
        String fileName = FirebaseAuth.getInstance().getUid() + ".jpg";
        StorageReference fileRef = storageReference.child("uploads/profilePics/" + fileName);
        UploadTask uploadTask = fileRef.putStream(stream);
        uploadTask.addOnProgressListener(snapshot -> {
            double progress = (100.0 * snapshot.getBytesTransferred()) / snapshot.getTotalByteCount();
            listener.onProgressChanged(progress);
        });
        uploadTask.addOnSuccessListener(taskSnapshot -> {
            fileRef.getDownloadUrl().addOnSuccessListener(new OnSuccessListener<Uri>() {
                @Override
                public void onSuccess(Uri uri) {
                    String downloadableUrl = uri.toString();
                    listener.onSuccess(downloadableUrl);
                }
            });

        });
        uploadTask.addOnFailureListener(e -> listener.onFailure(e.getMessage()));
    }
}
