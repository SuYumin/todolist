package com.yumin.todolist.data

import androidx.room.*
import kotlinx.coroutines.flow.Flow

@Dao
interface TodoItemDao {
    @Query("SELECT * FROM MyTodoItem ORDER BY id ASC")
    fun getAll(): Flow<List<TodoItem>>

    @Query("SELECT * FROM MyTodoItem WHERE listId LIKE :id")
    fun getItemsByListId(id: Int): Flow<List<TodoItem>>

    @Query("SELECT * FROM MyTodoItem WHERE listId LIKE :id AND finished = 1")
    fun getCompleteItemsByListId(id: Int): Flow<List<TodoItem>>

    @Query("SELECT * FROM MyTodoItem WHERE listId LIKE :id AND finished = 0")
    fun getUnCompleteItemsByListId(id: Int): Flow<List<TodoItem>>

    @Query("SELECT * FROM MyTodoItem WHERE id IN (:ids)")
    fun loadAllByIds(ids: IntArray): Flow<List<TodoItem>>

    @Query("SELECT * FROM MyTodoItem WHERE name LIKE :name LIMIT 1")
    fun findByName(name: String): Flow<TodoItem>

    @Query("SELECT * FROM MyTodoItem WHERE id LIKE :id LIMIT 1")
    fun findById(id: Int): Flow<TodoItem>

    @Update(onConflict = OnConflictStrategy.REPLACE)
    fun updateTodoItem(vararg todoItem: TodoItem)

    @Insert
    fun insert(vararg todoItem: TodoItem)

    @Delete
    fun delete(todoItem: TodoItem)

    @Query("DELETE FROM MyTodoItem")
    fun deleteAll()
}