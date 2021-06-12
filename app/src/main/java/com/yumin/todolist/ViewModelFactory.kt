package com.yumin.todolist

import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import com.yumin.todolist.data.FirebaseRepository
import com.yumin.todolist.data.RoomRepository
import com.yumin.todolist.ui.backup_restore.BackupRestoreViewModel
import com.yumin.todolist.ui.home.HomeViewModel
import com.yumin.todolist.ui.navigation_list.NavListViewModel
import com.yumin.todolist.ui.item_list.ItemListViewModel
import com.yumin.todolist.ui.singin.SignInViewModel

class ViewModelFactory(private val roomRepository: RoomRepository,
                       private val firebaseRepository: FirebaseRepository) : ViewModelProvider.Factory {
    override fun <T : ViewModel?> create(modelClass: Class<T>): T {
        when {
            modelClass.isAssignableFrom(ItemListViewModel::class.java) -> {
                @Suppress("UNCHECKED_CAST")
                return ItemListViewModel(roomRepository) as T
            }
            modelClass.isAssignableFrom(HomeViewModel::class.java) -> {
                @Suppress("UNCHECKED_CAST")
                return HomeViewModel(roomRepository) as T
            }
            modelClass.isAssignableFrom(NavListViewModel::class.java) -> {
                @Suppress("UNCHECKED_CAST")
                return NavListViewModel(roomRepository) as T
            }
            modelClass.isAssignableFrom(BackupRestoreViewModel::class.java) -> {
                @Suppress("UNCHECKED_CAST")
                return BackupRestoreViewModel(roomRepository, firebaseRepository) as T
            }
            modelClass.isAssignableFrom(SignInViewModel::class.java) -> {
                @Suppress("UNCHECKED_CAST")
                return SignInViewModel(roomRepository, firebaseRepository) as T
            }
        }
        throw IllegalArgumentException("Unable to construct view model")
    }
}