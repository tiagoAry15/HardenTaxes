package com.example.todolist.adapter

import android.view.View

interface TaskItemListener {

    fun onClick(v: View, position:Int)
    fun onLongClick(v: View, position:Int)
}