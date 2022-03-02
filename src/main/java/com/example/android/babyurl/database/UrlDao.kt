package com.example.android.babyurl.database

import androidx.lifecycle.LiveData
import androidx.room.*
import java.sql.RowId


@Dao
interface UrlDao {

    @Insert
    suspend fun insert(urlEntity: UrlEntity)


    @Query("DELETE FROM UrlTable WHERE rowId = :rowId")
    suspend fun delete(rowId : Long)

    @Query("DELETE FROM UrlTable")
    suspend fun deleteAll()

    // not a suspend function because , current version or room database doesn't support coroutine with live data
    @Query("SELECT * FROM UrlTable ORDER BY rowId DESC")
    fun getAllUrls() : LiveData<List<UrlEntity>>

}