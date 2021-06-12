package com.yumin.todolist.ui.singin

import androidx.lifecycle.*
import com.yumin.todolist.data.FirebaseRepository
import com.yumin.todolist.data.RoomRepository
import com.yumin.todolist.data.TodoItem
import com.yumin.todolist.data.TodoList
import kotlinx.coroutines.launch

class SignInViewModel(val roomRepository: RoomRepository, val firebaseRepository: FirebaseRepository) :
    ViewModel() {
    private lateinit var firebaseTodoItems: LiveData<List<TodoItem>>
    private lateinit var firebaseTodoLists: LiveData<List<TodoList>>
    private val roomTodoItems: LiveData<List<TodoItem>> = roomRepository.allItem.asLiveData()
    private val roomTodoList: LiveData<List<TodoList>> = roomRepository.allList.asLiveData()
    val missionResult: MediatorLiveData<Result> = MediatorLiveData()
    var syncResult: MutableLiveData<Boolean> = MutableLiveData()
    var firebaseUserExist: MutableLiveData<Boolean> = MutableLiveData()

    fun fetchData() {
        firebaseTodoItems = Transformations.switchMap(firebaseUserExist) {
            firebaseRepository.getAllItem()
        }

        firebaseTodoLists = Transformations.switchMap(firebaseUserExist) {
            firebaseRepository.getAllList()
        }

        var result = Result();
        missionResult.addSource(firebaseTodoItems) {
            result.firebaseTodoItems = it
            missionResult.postValue(result)
        }

        missionResult.addSource(firebaseTodoLists) {
            result.firebaseTodoList = it
            missionResult.postValue(result)
        }

        missionResult.addSource(roomTodoItems) {
            result.roomTodoItem = it
            missionResult.postValue(result)
        }

        missionResult.addSource(roomTodoList) {
            result.roomTodoList = it
            missionResult.postValue(result)
        }
    }

    fun syncFirebaseToRoom(){
        // todolist first
        for (list in firebaseTodoLists.value!!) {
            if (!roomTodoList.value?.contains(list)!!) {
                var oldId = list.id
                var insertList = list
                insertList.id = 0
                viewModelScope.launch {
                    val newId = roomRepository.insertList(insertList)
                    for (item in firebaseTodoItems.value!!) {
                        if (item.listId == oldId) {
                            if (!roomTodoItems.value?.contains(item)!!) {
                                var insertItem = item
                                insertItem.id = 0
                                insertItem.listId = newId.toInt()
                                roomRepository.insertItem(insertItem)
                            }
                        }
                    }
                }
            }
        }
        syncResult.postValue(true)
    }

    class Result {
        var firebaseTodoItems: List<TodoItem>? = null
        var firebaseTodoList: List<TodoList>? = null
        var roomTodoItem: List<TodoItem>? = null
        var roomTodoList: List<TodoList>? = null
        fun isComplete(): Boolean {
            return firebaseTodoItems != null && firebaseTodoList != null && roomTodoItem != null && roomTodoList != null
        }
    }
}