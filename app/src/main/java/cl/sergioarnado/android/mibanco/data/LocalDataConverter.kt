package cl.sergioarnado.android.mibanco.data

import androidx.room.TypeConverter
import java.time.LocalDate

class LocalDataConverter {

    @TypeConverter
    fun fromTimestamp(value: Long?): LocalDate? {
        return value?.let { LocalDate.ofEpochDay(it) }
    }

    @TypeConverter
    fun dateToTimestamp(date: LocalDate?): Long? {
        return date?.toEpochDay()
    }
}