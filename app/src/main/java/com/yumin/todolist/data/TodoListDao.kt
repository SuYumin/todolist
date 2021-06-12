package com.yumin.todolist.data

import androidx.room.*
import kotlinx.coroutines.flow.Flow

@Dao
interface TodoListDao {
    @Query("SELECT * FROM MyTodoList ORDER BY id ASC")
    fun getAll(): Flow<List<TodoList>>

    @Query("SELECT * FROM MyTodoList WHERE id IN (:ids)")
    fun loadAllByIds(ids: IntArray): Flow<List<TodoList>>

    @Query("SELECT * FROM MyTodoList WHERE name LIKE :name LIMIT 1")
    fun findByName(name: String): Flow<TodoList>

    @Query("SELECT * FROM MyTodoList WHERE id LIKE :id LIMIT 1")
    fun findById(id: Int): Flow<TodoList>

    @Insert
    fun insert(todoList: TodoList): Long

    @Delete
    fun delete(todoList: TodoList)

    @Query("DELETE FROM MyTodoList")
    fun deleteAll()

    @Query("DELETE FROM MyTodoList WHERE id = :id")
    fun deleteById(id: Int)

    @Update
    fun updateListItem(vararg todoList: TodoList)
}