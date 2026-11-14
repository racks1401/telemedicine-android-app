package com.racks.mydoctor.helper;

import android.content.Context;
import android.content.SharedPreferences;

public class SharedPreferenceManager {
    private static final String PREF_NAME = "UserTypePrefs";
    private static final String USERTYPE = "userType";
    private static final String FIRST_LOGIN = "first_login";

    private final SharedPreferences sharedPreferences;
    private final SharedPreferences.Editor editor;

    public SharedPreferenceManager(Context context) {
        sharedPreferences = context.getSharedPreferences(PREF_NAME, Context.MODE_PRIVATE);
        editor = sharedPreferences.edit();
    }

    public void setUserType(String userType) {
        editor.putString(USERTYPE, userType);
        editor.apply();
    }
    public String getUserType() {
        return sharedPreferences.getString(USERTYPE, null);
    }

    public boolean isFirstLogin(){
        return sharedPreferences.getBoolean(FIRST_LOGIN, true);
    }
    public void setFirstLogin(boolean loggedIn){
        editor.putBoolean(FIRST_LOGIN, loggedIn);
        editor.apply();
    }
    public void clearUserTypeData() {
        editor.clear();
        editor.apply();
    }
}
