package com.racks.mydoctor.models;

import androidx.lifecycle.LiveData;
import androidx.room.Dao;
import androidx.room.Insert;
import androidx.room.OnConflictStrategy;
import androidx.room.Query;
import androidx.room.Update;
@Dao
public interface UserDetailDao {
    @Insert(onConflict = OnConflictStrategy.REPLACE)
    void insertUserDetail(UserDetailModel userDetail);

    @Update
    void updateUserDetail(UserDetailModel userDetail);

    @Query("SELECT * FROM user_detail WHERE id = 1 LIMIT 1")
    LiveData<UserDetailModel> getUserDetail();
    @Query("DELETE FROM user_detail")
    void deleteAll();
}
