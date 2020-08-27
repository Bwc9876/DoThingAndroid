package com.example.dothingandroid

import androidx.room.ColumnInfo
import androidx.room.Entity
import androidx.room.PrimaryKey


@Entity(tableName = "GroupTable")
data class Group(@PrimaryKey @ColumnInfo(name = "Name") val Name: String, val Position: Int, val Items: String)