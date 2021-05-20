package com.yumin.todolist.data

import androidx.room.ColumnInfo
import androidx.room.Entity
import androidx.room.ForeignKey
import androidx.room.PrimaryKey

@Entity(tableName = "MyTodoList",
        foreignKeys = [ForeignKey(entity = ListInfo::class, parentColumns = arrayOf("id"),
            childColumns = arrayOf("listId"), onDelete = ForeignKey.CASCADE)]
)
data class ItemInfo(
    @PrimaryKey(autoGenerate = true) val id: Int = 0,
    @ColumnInfo(name = "name") var name: String = "",
    @ColumnInfo(name = "priority") val priority: Int = 0,
    @ColumnInfo(name = "finished") var finished: Boolean = false,
    @ColumnInfo(name = "content") val content: String = "",
    @ColumnInfo(name = "listId",index = true) var listId: Int = 1) {
}