package com.yumin.todolist.data

import android.content.Context
import androidx.room.Database
import androidx.room.Room
import androidx.room.RoomDatabase
import androidx.sqlite.db.SupportSQLiteDatabase
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.launch


@Database(entities = arrayOf(TodoItem::class,TodoList::class), version = 1)
abstract class TodoListDatabase : RoomDatabase() {
    abstract fun getTodoItemDao(): TodoItemDao
    abstract fun getListItemDao(): TodoListDao

    /**
     * A singleton design pattern is used to ensure that the database instance created is one
     * */
    companion object {
        val TAG = TodoListDatabase::class.java.simpleName
        private const val DB_NAME = "mysql.db"
        private var databaseManager: TodoListDatabase? = null

        var callback: Callback = object : Callback() {
            override fun onCreate(db: SupportSQLiteDatabase) {
                // ADD YOUR "Math - Sport - Art - Music" here
            }

            override fun onOpen(db: SupportSQLiteDatabase) {
                // do something every time database is open
            }
        }

        fun getDatabase(context: Context,scope: CoroutineScope): TodoListDatabase {
            if (databaseManager == null) {
                databaseManager = Room.databaseBuilder(
                    context,
                    TodoListDatabase::class.java,
                    "mysql.db"
                ).allowMainThreadQueries().addCallback(TodoListDatabaseCallback(scope)).build()
            }
            return databaseManager as TodoListDatabase
        }
    }

    class TodoListDatabaseCallback (private val scope: CoroutineScope) : RoomDatabase.Callback() {
        override fun onCreate(db: SupportSQLiteDatabase) {
            super.onCreate(db)
            databaseManager?.let { database ->
                scope.launch {
                    val listItemDao = database.getListItemDao()
                    // init default list
                    listItemDao.insert(TodoList(name = "default list",createdTime = System.currentTimeMillis()))
                }
            }
        }
    }
}