package com.abhirajsharma.theuniversitydatabase.others

import android.content.Context
import android.content.SharedPreferences
import androidx.preference.PreferenceManager

class SharedPreference {

    private val accountType = CurrentTeacherInfo.accountType + CurrentUserInfo.accountType

    private fun getSharedPreferences(ctx: Context?): SharedPreferences {
        return PreferenceManager.getDefaultSharedPreferences(ctx)
    }

    fun setUser(ctx: Context?, account: String) {
        val editor: SharedPreferences.Editor = getSharedPreferences(ctx).edit()
        editor.putString(accountType, account)
        editor.apply()
    }

    fun getUser(ctx: Context?): String? {
        return getSharedPreferences(ctx).getString(accountType, "")
    }

}