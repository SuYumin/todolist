package com.yumin.todolist.data

import android.graphics.Color
import androidx.room.ColumnInfo
import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "MyTodoList")
data class TodoList(
    @PrimaryKey(autoGenerate = true) var id: Int = 0,
    @ColumnInfo(name = "name") var name: String = "",
    @ColumnInfo(name = "color") var color: Int = Color.parseColor("#e57373"),
    @ColumnInfo(name = "createdTime") var createdTime: Long = 0) {

    override fun equals(other: Any?): Boolean {
        if (other !is TodoList)
            return false

        val list = other as TodoList
        return list.name == this.name && list.color == this.color && list.createdTime == this.createdTime
    }
}