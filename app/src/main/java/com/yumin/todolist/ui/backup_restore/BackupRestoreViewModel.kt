package com.yumin.todolist.ui.backup_restore

import android.content.SharedPreferences
import android.content.SharedPreferences.Editor
import androidx.lifecycle.LiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.asLiveData
import androidx.lifecycle.viewModelScope
import com.google.firebase.auth.FirebaseAuth
import com.yumin.todolist.data.*
import kotlinx.coroutines.launch

class BackupRestoreViewModel(
    private val roomRepository: RoomRepository,
    private val firebaseRepository: FirebaseRepository
): ViewModel() {


    fun getAllItemFromFirebase(): LiveData<List<TodoItem>>{
        return firebaseRepository.getAllItem()
    }

    fun deleteFirebase(){
        firebaseRepository.deleteAll()
    }

    fun addItemToFirebase(todoItem: TodoItem){
        firebaseRepository.addItem(todoItem)
    }

    fun getAllListFromFirebase(): LiveData<List<TodoList>> {
        return firebaseRepository.getAllList()
    }

    fun addListToFirebase(todoList: TodoList){
        firebaseRepository.addList(todoList)
    }

    fun getAllItemFromRoom(): LiveData<List<TodoItem>> {
        return roomRepository.allItem.asLiveData()
    }

    fun deleteRoom(){
        roomRepository.deleteAll()
    }

    fun addTodoItemToRoom(todoItem: TodoItem){
        viewModelScope.launch {
            roomRepository.insertItem(todoItem)
        }
    }

    fun getAllListFromRoom(): LiveData<List<TodoList>> {
        return roomRepository.allList.asLiveData()
    }

    fun addListToRoom(todoList: TodoList){
        viewModelScope.launch {
            roomRepository.insertList(todoList)
        }
    }

    fun setBackupTime(sharedPreferences: SharedPreferences, value: String){
        var uid = FirebaseAuth.getInstance().currentUser.uid
        val editor: Editor = sharedPreferences.edit()
        editor.putString(uid+"_backup_time", value)
        editor.apply()
    }

    fun getBackupTime(sharedPreferences: SharedPreferences): LiveData<String> {
        var uid = FirebaseAuth.getInstance().currentUser.uid
        return SharedPreferenceLiveData<String>(uid+"_backup_time", "", sharedPreferences)
    }

    fun setRestoreTime(sharedPreferences: SharedPreferences, value: String){
        var uid = FirebaseAuth.getInstance().currentUser.uid
        val editor: Editor = sharedPreferences.edit()
        editor.putString(uid+"_restore_time", value)
        editor.apply()
    }

    fun getRestoreTime(sharedPreferences: SharedPreferences):LiveData<String> {
        var uid = FirebaseAuth.getInstance().currentUser.uid
        return SharedPreferenceLiveData<String>(uid+"_restore_time", "", sharedPreferences)
    }
}