package com.example.dothingandroid

import androidx.room.ColumnInfo
import androidx.room.Entity
import androidx.room.PrimaryKey


@Entity(tableName = "UserTable")
data class User(
    @PrimaryKey @ColumnInfo(name = "Name") val Name: String,
    val Token: String
)