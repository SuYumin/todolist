package com.yumin.data

import androidx.room.Dao
import androidx.room.Delete
import androidx.room.Insert
import androidx.room.Query

@Dao
interface TodoDatDao {
    @Query("SELECT * FROM TodoData ORDER BY id ASC")
    fun getAll(): List<TodoData>

    @Query("SELECT * FROM TodoData WHERE id IN (:ids)")
    fun loadAllByIds(ids: IntArray): List<TodoData>

    @Query("SELECT * FROM TodoData WHERE name LIKE :name LIMIT 1")
    fun findByName(name: String): TodoData

    @Query("SELECT * FROM TodoData WHERE id LIKE :id LIMIT 1")
    fun findById(id: Int): TodoData

    @Insert
    fun insertAll(vararg todoData: TodoData)

    @Delete
    fun delete(todoData: TodoData)
}