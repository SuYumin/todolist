package com.yumin.todolist.ui.navigation_list

import android.graphics.Color
import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.EditText
import android.widget.ImageView
import android.widget.LinearLayout
import android.widget.TextView
import androidx.appcompat.app.AppCompatActivity
import androidx.core.widget.doOnTextChanged
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.navigation.fragment.findNavController
import com.google.android.material.floatingactionbutton.FloatingActionButton
import com.yumin.todolist.MainActivity
import com.yumin.todolist.R
import com.yumin.todolist.TodoListApplication
import com.yumin.todolist.ViewModelFactory
import com.yumin.todolist.data.ListInfo
import com.yumin.todolist.ui.color_view.ColorSelectorDialog
import com.yumin.todolist.ui.color_view.ColorView
import com.yumin.todolist.ui.color_view.ColorViewInfo


class NavListFragment : Fragment() {
    private var mSelectedColor: Int = Color.parseColor("#e57373")
    private var mSelectedColorPosition: Int = 0
    private lateinit var listInfo: ListInfo

    private val addListViewModel: NavListViewModel by viewModels {
        ViewModelFactory((activity?.application as TodoListApplication).repository)
    }

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        val root = inflater.inflate(R.layout.fragment_add_list, container, false)
        val checkFab: FloatingActionButton = root.findViewById(R.id.floatingActionButton)
        val colorTextView: TextView = root.findViewById(R.id.color_textView)
        val colorLinearLayout: LinearLayout = root.findViewById(R.id.color_linearLayout)
        var listNameEditText: EditText = root.findViewById(R.id.list_name_editText)
        listNameEditText.doOnTextChanged { text, start, before, count ->
            when(text.isNullOrEmpty()) {
                true -> {
                    Log.d("[doOnTextChanged]","isNullOrEmpty true")
                    checkFab.isEnabled = false
                    colorTextView.visibility = View.GONE
                    colorLinearLayout.visibility = View.GONE
                }

                false -> {
                    Log.d("[doOnTextChanged]","isNullOrEmpty false")
                    checkFab.isEnabled = true
                    colorTextView.visibility = View.VISIBLE
                    colorLinearLayout.visibility = View.VISIBLE
                }
            }
        }

        val colorView: ColorView = root.findViewById(R.id.color_view)
        colorView.setOnClickListener {
            Log.d("[AddListFragment]","[colorView] onClick")
            val colorSelectorDialog: ColorSelectorDialog? =
                context?.let {
                    ColorSelectorDialog(
                        it,
                        getColorList(),
                        mSelectedColorPosition
                    )
                }
            colorSelectorDialog?.setClickListener {
                when(it.id) {
                    R.id.buttonOk -> {
                        mSelectedColor = colorSelectorDialog?.selectColor
                        mSelectedColorPosition = colorSelectorDialog?.position
                        colorView.colorValue = colorSelectorDialog?.selectColor
                        colorSelectorDialog.dismiss()
                    }

                    R.id.buttonCancel -> {
                        colorSelectorDialog.dismiss()
                    }
                }
            }
            colorSelectorDialog?.show()
        }

        checkFab.setOnClickListener {
            if (arguments != null) {
                listInfo.name = listNameEditText.text.toString()
                listInfo.color = mSelectedColor!!
                addListViewModel.updateListInfo(listInfo)

                var bundle = Bundle()
                bundle.putInt(MainActivity.KEY_CHOSEN_LIST_ID,listInfo.id)
                findNavController().navigate(R.id.nav_list_view,bundle)
            } else {
                addListViewModel.insertList( ListInfo(name = listNameEditText.text.toString(), color = mSelectedColor!!))
                findNavController().navigateUp()
            }
        }

        val closeImage: ImageView = root.findViewById(R.id.close_imageView)
        closeImage.setOnClickListener {
            findNavController().navigateUp()
        }

        // check bundle
        arguments?.getInt(MainActivity.KEY_CHOSEN_LIST_ID)?.apply {
            // trigger view model for get List info and refresh List info
            addListViewModel.getEditListInfo(this).observe(viewLifecycleOwner,{
                listNameEditText.setText(it.name)
                colorView.colorValue = it.color
                mSelectedColor = it.color
                listInfo = it
            })
        }

        return root
    }

    private fun getColorList(): MutableList<ColorViewInfo>? {
        val colorList = mutableListOf<ColorViewInfo>()
        val colorArray= resources.getStringArray(R.array.color_view_values)
        for (color in colorArray) {
            colorList.add(
                ColorViewInfo(
                    false,
                    Color.parseColor(color)
                )
            )
        }
        return colorList
    }

    override fun onResume() {
        super.onResume()
        (activity as AppCompatActivity).supportActionBar?.hide()
    }

    override fun onStop() {
        super.onStop()
        (activity as AppCompatActivity).supportActionBar?.show()
    }
}