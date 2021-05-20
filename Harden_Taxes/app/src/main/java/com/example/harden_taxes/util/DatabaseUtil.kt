package com.example.harden_taxes.util

import android.content.Context
import androidx.room.Room
import com.example.harden_taxes.database.condonminiumDB

object DatabaseUtil {
    private var db: condonminiumDB? = null
    fun getDatabaseInstance(context: Context): condonminiumDB {
        if (db == null) {
            val db = Room.databaseBuilder(
                context,
                condonminiumDB::class.java,
                "todolist.db"
            ).fallbackToDestructiveMigration()
                .build()
        }
        return db!!

    }
}
