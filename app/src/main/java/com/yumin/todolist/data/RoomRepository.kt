package com.yumin.todolist.data

import androidx.annotation.WorkerThread
import kotlinx.coroutines.flow.Flow

class RoomRepository(private val todoItemDao: TodoItemDao, private val todoListDao: TodoListDao) {
    val allItem: Flow<List<TodoItem>> = todoItemDao.getAll()
    val allList: Flow<List<TodoList>> = todoListDao.getAll()

    @Suppress("RedundantSuspendModifier")
    @WorkerThread
    suspend fun insertItem(todoItem: TodoItem) {
        todoItemDao.insert(todoItem)
    }

    @Suppress("RedundantSuspendModifier")
    @WorkerThread
    suspend fun insertList(todoList: TodoList): Long {
        return todoListDao.insert(todoList)
    }

    @Suppress("RedundantSuspendModifier")
    @WorkerThread
    suspend fun updateItem(todoItem: TodoItem){
        todoItemDao.updateTodoItem(todoItem)
    }

    fun getItemsByListId(id: Int): Flow<List<TodoItem>> {
        return todoItemDao.getItemsByListId(id)
    }

    fun getCompleteItemsByListId(id: Int): Flow<List<TodoItem>> {
        return todoItemDao.getCompleteItemsByListId(id)
    }

    fun getUnCompleteItemsByListId(id: Int): Flow<List<TodoItem>> {
        return todoItemDao.getUnCompleteItemsByListId(id)
    }

    fun getListById(id: Int): Flow<TodoList> {
        return todoListDao.findById(id)
    }

    fun deleteListById(id: Int){
        return todoListDao.deleteById(id)
    }

    fun updateListItem(todoList: TodoList){
        todoListDao.updateListItem(todoList)
    }

    fun deleteItem(todoItem: TodoItem) {
        todoItemDao.delete(todoItem)
    }

    fun deleteAll() {
        todoItemDao.deleteAll()
        todoListDao.deleteAll()
    }
}