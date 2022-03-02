package com.example.android.babyurl

import android.widget.TextView
import androidx.databinding.BindingAdapter
import com.example.android.babyurl.home.UrlHome


@BindingAdapter("settingShortUrl")
fun TextView.setShortUrl(item : UrlHome?){
    item?.let {
        text = item.shortUrl
    }
}

@BindingAdapter("settingLongUrl")
fun TextView.setLongUrl(item : UrlHome?){
    item?.let {
        text = item.longUrl
    }
}