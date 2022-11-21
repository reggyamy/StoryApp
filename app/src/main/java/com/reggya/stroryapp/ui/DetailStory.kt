package com.reggya.stroryapp.ui

import android.app.Activity
import android.app.AlertDialog
import android.content.Context
import android.graphics.Color
import android.graphics.drawable.ColorDrawable
import android.widget.ImageView
import android.widget.TextView
import com.bumptech.glide.Glide
import com.reggya.stroryapp.data.ListStoryItem
import com.reggya.stroryapp.R

object DetailStory {

    fun showDetail(context: Context, item: ListStoryItem){
        val dialog = AlertDialog.Builder(context).create()
        val view = (context as Activity).layoutInflater.inflate(R.layout.detail_story, null)
        dialog.setView(view)
        val name = view.findViewById<TextView>(R.id.name)
        val desc = view.findViewById<TextView>(R.id.desc)
        val image = view.findViewById<ImageView>(R.id.image)
        name.text = item.name
        desc.text = item.description
        Glide.with(context).load(item.photoUrl).into(image)

        dialog.window?.setBackgroundDrawable(ColorDrawable(Color.TRANSPARENT))

        dialog.show()
    }
}