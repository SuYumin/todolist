package com.yumin.todolist.data

import android.util.Log
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.database.*
import com.yumin.todolist.LogUtils

class FirebaseRepository {
    companion object {
        private const val TAG: String = "[FirebaseRepository]"
    }
    private var databaseReference: DatabaseReference = FirebaseDatabase.getInstance().reference

    fun getAllItem(): LiveData<List<TodoItem>> {
        var allItemLiveData = MutableLiveData<List<TodoItem>>()
        getItemPath().addValueEventListener(object: ValueEventListener{
            override fun onDataChange(snapshot: DataSnapshot) {
                val list = mutableListOf<TodoItem>()
                if (snapshot.exists()) {
                    for (item in snapshot.children) {
                        val value = item.getValue(TodoItem::class.java)
                        LogUtils.logD(TAG,"getTodoItemList [onDataChange] itemInfo = $value")
                        value?.let { list.add(it) }
                    }
                }
                allItemLiveData.postValue(list)
            }

            override fun onCancelled(error: DatabaseError) {
                LogUtils.logD(TAG,"getTodoItemList [onCancelled] error = $error.message")
            }
        })
        return allItemLiveData
    }

    fun addItem(todoItem: TodoItem){
        val id = getItemPath().push().key
        id?.let {
            getItemPath().child(it).setValue(todoItem)
        }
    }

    fun deleteAll(){
        deleteAllItem()
        deleteAllList()
    }

    private fun deleteAllItem(){
        getItemPath().removeValue()
    }

    private fun deleteAllList(){
        getListPath().removeValue()
    }


    fun getAllList(): LiveData<List<TodoList>> {
        var allListLiveData = MutableLiveData<List<TodoList>>()
        getListPath().addValueEventListener(object: ValueEventListener{
            override fun onDataChange(snapshot: DataSnapshot) {
                val list = mutableListOf<TodoList>()
                if (snapshot.exists()) {
                    for (item in snapshot.children) {
                        val value = item.getValue(TodoList::class.java)
                        LogUtils.logD(TAG,"getListInfo [onDataChange] itemInfo = $value")
                        value?.let { list.add(it) }
                    }
                }
                allListLiveData.postValue(list)
            }

            override fun onCancelled(error: DatabaseError) {
                LogUtils.logD(TAG,"getListInfo [onCancelled] error = $error.message")
            }
        })
        return allListLiveData
    }

    fun addList(todoList: TodoList) {
        val id = getListPath().push().key
        id?.let {
            getListPath().child(it).setValue(todoList)
        }
    }

    private fun getItemPath(): DatabaseReference {
        return getCurrentUserId()?.let {
            databaseReference.child("todo_item").child(it)
        }
    }

    private fun getListPath(): DatabaseReference {
        return getCurrentUserId()?.let {
            databaseReference.child("list_info").child(it)
        }
    }

    private fun getCurrentUserId(): String {
        return FirebaseAuth.getInstance().currentUser.uid
    }

}