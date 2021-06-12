package com.yumin.todolist.ui.backup_restore

import android.content.Context
import android.content.SharedPreferences
import android.graphics.Color
import android.graphics.drawable.ColorDrawable
import android.os.Bundle
import android.os.Handler
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.*
import androidx.appcompat.app.AlertDialog
import androidx.appcompat.app.AppCompatActivity
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.navigation.fragment.findNavController
import com.google.firebase.auth.FirebaseAuth
import com.yumin.todolist.LogUtils
import com.yumin.todolist.R
import com.yumin.todolist.TodoListApplication
import com.yumin.todolist.ViewModelFactory
import com.yumin.todolist.data.TodoItem
import com.yumin.todolist.data.TodoList
import kotlinx.coroutines.coroutineScope
import java.text.SimpleDateFormat
import java.util.*

class BackupRestoreFragment: Fragment() {
    private val mViewModel: BackupRestoreViewModel by viewModels {
        ViewModelFactory(roomRepository = (activity?.application as TodoListApplication).roomRepository,
                            firebaseRepository = (activity?.application as TodoListApplication).firebaseRepository)
    }
    private var mRoomAllItem: List<TodoItem>? = null
    private var mRoomAllTodo: List<TodoList>? = null
    private var mFirebaseAllItem: List<TodoItem>? = null
    private var mFirebaseAllList: List<TodoList>? = null
    private lateinit var mBackupTime: TextView
    private lateinit var mRestoreTime: TextView
    private var mSharedPreferences: SharedPreferences? = null

    override fun onResume() {
        super.onResume()
        (activity as AppCompatActivity).supportActionBar?.hide()
    }

    override fun onStop() {
        super.onStop()
        (activity as AppCompatActivity).supportActionBar?.show()
    }

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        val root: View = inflater.inflate(R.layout.fragment_backup_restore,container,false)

        val close: ImageView = root.findViewById(R.id.close_imageView)
        close.setOnClickListener {
            findNavController().navigateUp()
        }

        val startBackupButton: Button = root.findViewById(R.id.start_backup)
        startBackupButton.setOnClickListener{
            // show dialog to warning
            createWarningDialog(getString(R.string.backup_clear_warning)) { startBackupToFirebase() }
        }

        val startRestoreButton: Button = root.findViewById(R.id.start_restore)
        startRestoreButton.setOnClickListener {
            // show dialog to warning
            createWarningDialog(getString(R.string.restore_clear_warning)) { startRestoreToRoom() }
        }

        mBackupTime = root.findViewById(R.id.last_backup_time)
        mRestoreTime = root.findViewById(R.id.last_restore_time)

        mSharedPreferences = context?.getSharedPreferences("BackupRestore", Context.MODE_PRIVATE)

        observeViewModel()

        return root
    }

    private fun createWarningDialog(message: String, operate: ()->Unit){
        val signOutDialog = activity?.let {
            AlertDialog.Builder(it)
                .setMessage(message)
                .setPositiveButton(R.string.option_yes) { dialog, which ->
                    // add progressbar dialog
                    var progressBarDialog = progressbarDialog()
                    progressBarDialog.window?.setBackgroundDrawable(ColorDrawable(Color.TRANSPARENT))
                    progressBarDialog.show()

                    operate()

                    Handler().postDelayed({
                        progressBarDialog.dismiss()
                    }, 500)
                }
                .setNegativeButton(R.string.option_no, null)
        }
        signOutDialog?.show()
    }


    private fun observeViewModel(){
        mViewModel.getAllItemFromRoom().observe(viewLifecycleOwner) {
            it?.let {
                mRoomAllItem = it
            }
        }

        mViewModel.getAllListFromRoom().observe(viewLifecycleOwner) {
            it?.let {
                mRoomAllTodo = it
            }
        }

        mViewModel.getAllItemFromFirebase().observe(viewLifecycleOwner) {
            it?.let {
                mFirebaseAllItem = it
            }
        }

        mViewModel.getAllListFromFirebase().observe(viewLifecycleOwner) {
            it?.let{
                mFirebaseAllList = it
            }
        }

        mSharedPreferences?.let { it ->
            mViewModel.getBackupTime(it).observe(viewLifecycleOwner){
                mBackupTime.text = it
            }

            mViewModel.getRestoreTime(it).observe(viewLifecycleOwner){
                mRestoreTime.text = it
            }
        }
    }

    private fun progressbarDialog(): android.app.AlertDialog {
        val dialogBuilder: android.app.AlertDialog.Builder = android.app.AlertDialog.Builder(activity)
        val view: View = LayoutInflater.from(activity).inflate(R.layout.dialog_progressbar, null)
        dialogBuilder.setView(view)
        return dialogBuilder.create()
    }

    private fun startBackupToFirebase() {
        if (mRoomAllItem.isNullOrEmpty()) {
            Toast.makeText(activity, R.string.backup_no_data_warning , Toast.LENGTH_SHORT).show()
            return
        }

        mViewModel.deleteFirebase()

        for (item in mRoomAllItem!!) {
            LogUtils.logD(TAG, "[startBackupToFirebase] add $item")
            mViewModel.addItemToFirebase(item)
        }

        for (list in mRoomAllTodo!!) {
            LogUtils.logD(TAG, "[startBackupToFirebase] add $list")
            mViewModel.addListToFirebase(list)
        }

        val sdf = SimpleDateFormat("yyyy/M/dd hh:mm:ss")
        mSharedPreferences?.let { mViewModel.setBackupTime(it,sdf.format(Date())) }
    }

    private fun startRestoreToRoom(){
        if (mFirebaseAllItem.isNullOrEmpty()){
            Toast.makeText(activity,R.string.restore_no_data_warning,Toast.LENGTH_SHORT).show()
            return
        }

        mViewModel.deleteRoom()

        for (list in mFirebaseAllList!!) {
            LogUtils.logD(TAG,"[startRestoreToRoom] add $list")
            mViewModel.addListToRoom(list)
        }

        for (item in mFirebaseAllItem!!) {
            LogUtils.logD(TAG,"[startRestoreToRoom] add $item")
            mViewModel.addTodoItemToRoom(item)
        }

        val sdf = SimpleDateFormat("yyyy/M/dd hh:mm:ss")
        mSharedPreferences?.let { mViewModel.setRestoreTime(it,sdf.format(Date())) }
    }

    companion object {
        private const val TAG = "[BackupFragment]"
    }
}