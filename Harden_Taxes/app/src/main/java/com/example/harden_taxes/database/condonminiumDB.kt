package com.example.harden_taxes.database

import androidx.room.Database
import androidx.room.RoomDatabase
import com.example.harden_taxes.model.Apartment
import com.example.harden_taxes.model.User

@Database(entities = [User::class, Apartment::class ],version = 1)
abstract class condonminiumDB:RoomDatabase() {

    abstract  fun getUserDAO(): UserDAO
    abstract fun getApartmentDAO(): ApartmentDAO
}