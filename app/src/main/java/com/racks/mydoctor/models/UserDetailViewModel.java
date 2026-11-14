package com.racks.mydoctor.models;

import android.app.Application;

import androidx.annotation.NonNull;
import androidx.lifecycle.AndroidViewModel;
import androidx.lifecycle.LiveData;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.racks.mydoctor.helper.LocalDatabaseHelper;

public class UserDetailViewModel extends AndroidViewModel {
    private final UserDetailDao userDetailDao;
    private final LiveData<UserDetailModel> userDetailData;
    private final DatabaseReference databaseReference;

    public UserDetailViewModel(@NonNull Application application) {
        super(application);
        LocalDatabaseHelper db = LocalDatabaseHelper.getInstance(application);
        userDetailDao = db.userDetailDao();
        userDetailData = userDetailDao.getUserDetail();
        databaseReference = FirebaseDatabase.getInstance().getReference("Users");
    }

    // Method to set user data
    public LiveData<UserDetailModel> getUserDetail() {
        return userDetailData;
    }

    public void insertOrUpdateUserDetail(UserDetailModel userDetail) {
        new Thread(() -> userDetailDao.insertUserDetail(userDetail)).start();
    }

    public void fetchUserDetailFromFirebase(String userType, FetchUserCallback callback) {
        databaseReference.child(userType + "s").child(FirebaseAuth.getInstance().getUid())
                .addValueEventListener(new ValueEventListener() {
                    @Override
                    public void onDataChange(@NonNull DataSnapshot snapshot) {
                        if (snapshot.exists()) {
                            UserDetailModel userDetail = snapshot.getValue(UserDetailModel.class);
                            if (userDetail != null) {
                                insertOrUpdateUserDetail(userDetail);
                                callback.onSuccess(userDetail);
                            } else {
                                callback.onError("User data is null");
                            }
                        } else {
                            callback.onError("User not found in database");
                        }
                    }

                    @Override
                    public void onCancelled(@NonNull DatabaseError error) {
                        callback.onError("Firebase error: " + error.getMessage());
                    }
                });
    }

    public interface FetchUserCallback {
        void onSuccess(UserDetailModel userDetail);

        void onError(String errorMessage);
    }

}
