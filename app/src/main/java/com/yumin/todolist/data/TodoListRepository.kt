package com.yumin.todolist.data

import androidx.annotation.WorkerThread
import kotlinx.coroutines.flow.Flow

class TodoListRepository(private val todoItemDao: ItemInfoDao, private val listInfoDao: ListInfoDao) {
    val allItemInfo: Flow<List<ItemInfo>> = todoItemDao.getAll()
    val allListInfo: Flow<List<ListInfo>> = listInfoDao.getAll()

    @Suppress("RedundantSuspendModifier")
    @WorkerThread
    suspend fun insertTodoItem(itemInfo: ItemInfo) {
        todoItemDao.insert(itemInfo)
    }

    @Suppress("RedundantSuspendModifier")
    @WorkerThread
    suspend fun insertListItem(listInfo: ListInfo) {
        listInfoDao.insert(listInfo)
    }

    @Suppress("RedundantSuspendModifier")
    @WorkerThread
    suspend fun updateTodoItem(itemInfo: ItemInfo){
        todoItemDao.updateTodoItem(itemInfo)
    }

    fun getItemsByListId(id: Int): Flow<List<ItemInfo>> {
        return todoItemDao.getItemsByListId(id)
    }

    fun getCompleteItemsByListId(id: Int): Flow<List<ItemInfo>> {
        return todoItemDao.getCompleteItemsByListId(id)
    }

    fun getUnCompleteItemsByListId(id: Int): Flow<List<ItemInfo>> {
        return todoItemDao.getUnCompleteItemsByListId(id)
    }

    fun getListById(id: Int): Flow<ListInfo> {
        return listInfoDao.findById(id)
    }

    fun deleteListById(id: Int){
        return listInfoDao.deleteById(id)
    }

    fun updateListItem(listInfo: ListInfo){
        listInfoDao.updateListItem(listInfo)
    }

    fun deleteItem(itemInfo: ItemInfo) {
        todoItemDao.delete(itemInfo)
    }
}