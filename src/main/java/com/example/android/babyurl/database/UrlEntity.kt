package com.example.android.babyurl.database

import androidx.room.ColumnInfo
import androidx.room.Entity
import androidx.room.PrimaryKey
import com.example.android.babyurl.home.UrlHome

@Entity(tableName = "UrlTable")
data class UrlEntity constructor(
    @PrimaryKey
    val rowId : Long,
    @ColumnInfo(name = "shortUrl")
    val shortUrl : String,
    @ColumnInfo(name = "longUrl")
    val longUrl : String
)

fun List<UrlEntity>.asHome() : List<UrlHome> {
    return map {
        UrlHome(rowId = it.rowId,
                shortUrl = it.shortUrl,
                longUrl = it.longUrl)
    }
}

