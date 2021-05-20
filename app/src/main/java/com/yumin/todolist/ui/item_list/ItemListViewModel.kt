package com.yumin.todolist.ui.item_list

import androidx.lifecycle.*
import com.yumin.todolist.data.*
import kotlinx.coroutines.launch

class ItemListViewModel(private val repository: TodoListRepository) : ViewModel() {
    val itemInfoList: LiveData<List<ItemInfo>>? = repository?.allItemInfo.asLiveData()
    val listItemList: LiveData<List<ListInfo>>? = repository?.allListInfo.asLiveData()

    private val listInfoById: MutableLiveData<Int> = MutableLiveData()
    var listInfo = Transformations.switchMap(listInfoById){
        repository.getListById(it).asLiveData()
    }

    private val todoListById: MutableLiveData<Int> = MutableLiveData()
    var unCompleteTodoItemList = Transformations.switchMap(todoListById){
        repository.getUnCompleteItemsByListId(it).asLiveData()
    }

    var completeTodoItemList = Transformations.switchMap(todoListById){
        repository.getCompleteItemsByListId(it).asLiveData()
    }

    var todoItemList = Transformations.switchMap(todoListById) {
        repository.getItemsByListId(it).asLiveData()
    }

    fun updateTodoItem(itemInfo: ItemInfo) = viewModelScope.launch {
        repository.updateTodoItem(itemInfo)
    }

    fun insertTodoItem(itemInfo: ItemInfo) = viewModelScope.launch {
        repository.insertTodoItem(itemInfo)
    }

    fun updateListInfoQueryId(id: Int){
        listInfoById.postValue(id)
    }

    fun updateTodoListQueryId(id: Int){
        todoListById.postValue(id)
    }

    fun deleteList(id: Int){
        repository.deleteListById(id)
    }

    fun deleteItem(itemInfo: ItemInfo) {
        repository.deleteItem(itemInfo)
    }
}