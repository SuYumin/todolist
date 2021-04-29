package com.yumin.data

import androidx.room.ColumnInfo
import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity
data class TodoData(
    @PrimaryKey(autoGenerate = true) val id: Int = 0,
    @ColumnInfo(name = "name") val name: String = "",
    @ColumnInfo(name = "priority") val priority: Int = 0,
    @ColumnInfo(name = "finished") val finished: Boolean = false) {
    @ColumnInfo(name = "content") val content: String = ""
}