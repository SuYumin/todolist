package com.yumin.todolist.data

import androidx.room.*
import kotlinx.coroutines.flow.Flow

@Dao
interface ItemInfoDao {
    @Query("SELECT * FROM MyTodoList ORDER BY id ASC")
    fun getAll(): Flow<List<ItemInfo>>

    @Query("SELECT * FROM MyTodoList WHERE listId LIKE :id")
    fun getItemsByListId(id: Int): Flow<List<ItemInfo>>

    @Query("SELECT * FROM MyTodoList WHERE listId LIKE :id AND finished = 1")
    fun getCompleteItemsByListId(id: Int): Flow<List<ItemInfo>>

    @Query("SELECT * FROM MyTodoList WHERE listId LIKE :id AND finished = 0")
    fun getUnCompleteItemsByListId(id: Int): Flow<List<ItemInfo>>

    @Query("SELECT * FROM MyTodoList WHERE id IN (:ids)")
    fun loadAllByIds(ids: IntArray): Flow<List<ItemInfo>>

    @Query("SELECT * FROM MyTodoList WHERE name LIKE :name LIMIT 1")
    fun findByName(name: String): Flow<ItemInfo>

    @Query("SELECT * FROM MyTodoList WHERE id LIKE :id LIMIT 1")
    fun findById(id: Int): Flow<ItemInfo>

    @Update(onConflict = OnConflictStrategy.REPLACE)
    fun updateTodoItem(vararg itemInfo: ItemInfo)

    @Insert
    fun insert(vararg itemInfo: ItemInfo)

    @Delete
    fun delete(itemInfo: ItemInfo)
}