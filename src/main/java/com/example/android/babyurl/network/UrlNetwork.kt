package com.example.android.babyurl.network

import com.example.android.babyurl.database.UrlEntity

data class UrlNetwork(val rowId: Long , val shortUrl : String , val longUrl: String){
       constructor() : this(-1 , "null" , "null")
        fun asDatabase() : UrlEntity{
            return UrlEntity(rowId , shortUrl , longUrl)
        }
}
