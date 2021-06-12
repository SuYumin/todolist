package com.yumin.todolist.data

import androidx.room.ColumnInfo
import androidx.room.Entity
import androidx.room.ForeignKey
import androidx.room.PrimaryKey

@Entity(tableName = "MyTodoItem",
        foreignKeys = [ForeignKey(entity = TodoList::class, parentColumns = arrayOf("id"),
            childColumns = arrayOf("listId"), onDelete = ForeignKey.CASCADE)]
)
data class TodoItem(
    @PrimaryKey(autoGenerate = true) var id: Int = 0,
    @ColumnInfo(name = "name") var name: String = "",
    @ColumnInfo(name = "finished") var finished: Boolean = false,
    @ColumnInfo(name = "listId",index = true) var listId: Int = 1,
    @ColumnInfo(name = "createdTime") var createdTime: Long = 0) {

    override fun equals(other: Any?): Boolean {
        if (other !is TodoItem)
            return false

        val item = other as TodoItem
        return item.name.equals(this.name) && item.createdTime == this.createdTime && item.listId == this.listId
    }
}