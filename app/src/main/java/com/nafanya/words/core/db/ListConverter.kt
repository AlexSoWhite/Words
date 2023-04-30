package com.nafanya.words.core.db

import androidx.room.ProvidedTypeConverter
import androidx.room.TypeConverter
import com.google.gson.Gson

@ProvidedTypeConverter
class ListConverter {

    @TypeConverter
    fun toString(list: List<String>): String = Gson().toJson(list)

    @TypeConverter
    fun toList(json: String) = Gson().fromJson(json, Array<String>::class.java).toList()
}
