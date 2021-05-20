package com.yumin.todolist.data

import androidx.room.*
import kotlinx.coroutines.flow.Flow

@Dao
interface ListInfoDao {
    @Query("SELECT * FROM MyList ORDER BY id ASC")
    fun getAll(): Flow<List<ListInfo>>

    @Query("SELECT * FROM MyList WHERE id IN (:ids)")
    fun loadAllByIds(ids: IntArray): Flow<List<ListInfo>>

    @Query("SELECT * FROM MyList WHERE name LIKE :name LIMIT 1")
    fun findByName(name: String): Flow<ListInfo>

    @Query("SELECT * FROM MyList WHERE id LIKE :id LIMIT 1")
    fun findById(id: Int): Flow<ListInfo>

    @Insert
    fun insert(vararg listInfo: ListInfo)

    @Delete
    fun delete(listInfo: ListInfo)

    @Query("DELETE FROM MyList WHERE id = :id")
    fun deleteById(id: Int)

    @Update
    fun updateListItem(vararg listInfo: ListInfo)
}