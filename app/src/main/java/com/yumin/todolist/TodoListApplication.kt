package com.yumin.todolist

import android.app.Application
import com.yumin.todolist.data.FirebaseRepository
import com.yumin.todolist.data.TodoListDatabase
import com.yumin.todolist.data.RoomRepository
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.SupervisorJob

class TodoListApplication: Application() {
    private val applicationScope = CoroutineScope(SupervisorJob())
    // Using by lazy so the database and the repository are only created when they're needed
    // rather than when the application starts
    private val database by lazy { TodoListDatabase.getDatabase(this,applicationScope) }
    val roomRepository by lazy { RoomRepository(database.getTodoItemDao(), database.getListItemDao()) }
    val firebaseRepository by lazy { FirebaseRepository() }
}