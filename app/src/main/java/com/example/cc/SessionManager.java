package com.example.cc;

import android.content.Context;
import android.content.SharedPreferences;

public class SessionManager {

    private static final String PREF_NAME = "CCSession";
    private static final String KEY_USER_ID = "userId";
    private static final String KEY_USER_TYPE = "userType";

    public static void saveUserId(Context context, int userId) {
        SharedPreferences prefs = context.getSharedPreferences(PREF_NAME, Context.MODE_PRIVATE);
        prefs.edit().putInt(KEY_USER_ID, userId).apply();
    }

    public static int getUserId(Context context) {
        SharedPreferences prefs = context.getSharedPreferences(PREF_NAME, Context.MODE_PRIVATE);
        return prefs.getInt(KEY_USER_ID, -1);
    }

    public static void saveUserEmail(Context context, String email) {
        SharedPreferences prefs = context.getSharedPreferences("CCSession", Context.MODE_PRIVATE);
        prefs.edit().putString("email", email).apply();
    }

    public static String getUserEmail(Context context) {
        SharedPreferences prefs = context.getSharedPreferences("CCSession", Context.MODE_PRIVATE);
        return prefs.getString("email", "");
    }

    public static void saveUserType(Context context, String userType) {
        SharedPreferences prefs = context.getSharedPreferences("CCSession", Context.MODE_PRIVATE);
        prefs.edit().putString("userType", userType).apply();
    }

    public static String getUserType(Context context) {
        SharedPreferences prefs = context.getSharedPreferences("CCSession", Context.MODE_PRIVATE);
        return prefs.getString("userType", "");  // e.g., "coach", "admin", "learner"
    }
    public static void logout(Context context) {
        SharedPreferences prefs = context.getSharedPreferences(PREF_NAME, Context.MODE_PRIVATE);
        prefs.edit().clear().apply();
    }

    public static boolean isLoggedIn(Context context) {
        return getUserId(context) != -1;
    }


}
