package com.mac.ecomadminphp.Utils

import android.content.Context
import android.content.SharedPreferences


class SharedPref {
    companion object {

        fun writeInSharedPref(context:Context,prefName:String,valueToSave:String){

            val sp:SharedPreferences = context.getSharedPreferences(Constants.mainUserPref,Context.MODE_PRIVATE)
            val editor = sp.edit()
            editor.putString(prefName,valueToSave);
            editor.apply()
            editor.commit()


        }

        fun readFromSharedPref(context:Context, prefName:String,defaultValue:String): String? {

            val sp:SharedPreferences = context.getSharedPreferences(Constants.mainUserPref,Context.MODE_PRIVATE)
            val value:String? = sp.getString(prefName,defaultValue);

            return value



        }

    }
}