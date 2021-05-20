package com.yumin.todolist.ui.item_list

import android.app.Activity
import android.graphics.Color
import android.os.Bundle
import android.util.Log
import android.view.*
import android.view.inputmethod.InputMethodManager
import android.widget.*
import androidx.activity.OnBackPressedCallback
import androidx.appcompat.app.AlertDialog
import androidx.appcompat.app.AppCompatActivity
import androidx.constraintlayout.widget.ConstraintLayout
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.lifecycle.Observer
import androidx.navigation.fragment.findNavController
import androidx.recyclerview.widget.DefaultItemAnimator
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.google.android.material.floatingactionbutton.FloatingActionButton
import com.google.android.material.snackbar.Snackbar
import com.yumin.todolist.MainActivity
import com.yumin.todolist.R
import com.yumin.todolist.TodoListApplication
import com.yumin.todolist.ViewModelFactory
import com.yumin.todolist.data.ItemInfo
import com.yumin.todolist.data.ListInfo
import com.yumin.todolist.ui.color_view.ColorView
import com.yumin.todolist.ui.navigation_list.NavListAdapter


class ItemListFragment : Fragment(), MainActivity.RefreshListListener, ItemClickListener, MainActivity.DrawerLayoutStateListener {
    private lateinit var mUnCompleteRecyclerView: RecyclerView
    private lateinit var mUnCompleteItemAdapter: ItemListAdapter
    private lateinit var mFab: FloatingActionButton
    private var mSnackBar: Snackbar? = null
    private lateinit var mListName: TextView
    private lateinit var mListColorView: ColorView
    private lateinit var mCompleteRecyclerView: RecyclerView
    private lateinit var mCompleteItemAdapter: ItemListAdapter
    private lateinit var mCompleteLinearLayout: LinearLayout
    private lateinit var mRootView: View
    private lateinit var mListInfo: ListInfo
    private lateinit var mListInfoList: List<ListInfo>
    private lateinit var mNoItemLayout: ConstraintLayout
    private val mTodoListViewModel: ItemListViewModel by viewModels {
        ViewModelFactory((activity?.application as TodoListApplication).repository)
    }
    private var mChosenListId: Int = 0
    private var mTempEditListId: Int = -1

    companion object {
//        var LIST_ID: Int = 0
//        var TEMP_LIST_ID: Int = -1
        val TAG: String = ItemListFragment.javaClass.toString()
    }

    override fun onActivityCreated(savedInstanceState: Bundle?) {
        super.onActivityCreated(savedInstanceState)
        (activity as MainActivity).setOnRefreshHomeListener(this)
        (activity as MainActivity).setUpDrawerLayoutState(this)
    }

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        val root = inflater.inflate(R.layout.fragment_todo_list, container, false)
        // check has bundle or not
        arguments?.apply {
            mChosenListId = this.getInt(MainActivity.KEY_CHOSEN_LIST_ID)
        }
        initView(root)
        observeViewModel()
        setHasOptionsMenu(true)
        mRootView = root
        return root
    }

    override fun onCreateOptionsMenu(menu: Menu, inflater: MenuInflater) {
        inflater.inflate(R.menu.main,menu)
        super.onCreateOptionsMenu(menu, inflater)
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        when(item.itemId) {
            R.id.action_edit_list -> {
                var bundle = Bundle()
                bundle.putInt(MainActivity.KEY_CHOSEN_LIST_ID, mChosenListId)
                findNavController().navigate(R.id.nav_add_list,bundle)
                return true
            }
            R.id.action_delete_list -> {
                mTodoListViewModel.deleteList(mChosenListId)
                findNavController().navigateUp()
                return true
            }
        }
        return false
    }

    private fun initView(root: View){
        mFab = root.findViewById(R.id.fab)
        mFab.setOnClickListener { view ->
            mFab.hide()
            createEditItemSnackBar(view,null)
        }

        // handle press back key
        (activity as AppCompatActivity).onBackPressedDispatcher.addCallback(viewLifecycleOwner,
            object: OnBackPressedCallback(true) {
            override fun handleOnBackPressed() {
                if (mSnackBar?.isShown == true) {
                    mSnackBar?.dismiss()
                    mFab.show()
                } else {
                    if (mFab.isOrWillBeShown)
                        mFab.hide()
                    findNavController().navigateUp()
                }
            }})

        mUnCompleteRecyclerView = root.findViewById(R.id.uncomplete_recyclerView)
        mUnCompleteRecyclerView.layoutManager = LinearLayoutManager(context, LinearLayoutManager.VERTICAL, false)
        val itemAnimator = DefaultItemAnimator()
        itemAnimator.addDuration = 500
        itemAnimator.removeDuration = 500
        mUnCompleteRecyclerView.itemAnimator = itemAnimator
        mUnCompleteItemAdapter = ItemListAdapter(this)
        mUnCompleteRecyclerView.adapter = mUnCompleteItemAdapter
        mListName = root.findViewById(R.id.list_name)
        mListColorView = root.findViewById(R.id.list_color)

        mCompleteRecyclerView = root.findViewById(R.id.complete_recyclerView)
        mCompleteRecyclerView.layoutManager = LinearLayoutManager(context, LinearLayoutManager.VERTICAL, false)
        val completeItemAnimator = DefaultItemAnimator()
        completeItemAnimator.addDuration = 500
        completeItemAnimator.removeDuration = 500
        mCompleteRecyclerView.itemAnimator = completeItemAnimator
        mCompleteItemAdapter = ItemListAdapter(this)
        mCompleteItemAdapter.mEnableTextGrayOut = true
        mCompleteRecyclerView.adapter = mCompleteItemAdapter

        val completeImageView: ImageView = root.findViewById(R.id.imageView2)
        mCompleteLinearLayout = root.findViewById(R.id.complete_linearLayout)
        mCompleteLinearLayout.setOnClickListener {
            when(mCompleteRecyclerView.visibility) {
                View.VISIBLE -> mCompleteRecyclerView.apply {
                    this.visibility = View.GONE
                    completeImageView.setImageResource(R.drawable.ic_baseline_keyboard_arrow_up_24)
                }

                View.GONE -> mCompleteRecyclerView.apply {
                    this.visibility = View.VISIBLE
                    completeImageView.setImageResource(R.drawable.ic_baseline_keyboard_arrow_down_24)
                }
            }
        }
        mNoItemLayout = root.findViewById(R.id.no_item_view)
    }

    private fun observeViewModel(){
        mTodoListViewModel.updateTodoListQueryId(mChosenListId)
        mTodoListViewModel.unCompleteTodoItemList.observe(viewLifecycleOwner, Observer {
            Log.d(TAG,"it.isNullOrEmpty() = ${it.isNullOrEmpty()}")

            if (!it.isNullOrEmpty())
                mUnCompleteRecyclerView.visibility = View.VISIBLE
            else
                mUnCompleteRecyclerView.visibility = View.GONE

            it?.apply {
                val newList = mutableListOf<ItemInfo>()
                Log.d(TAG,"unCompleteTodoItemList size = ${it.size} , toString = ${it.toString()}")
                newList.addAll(it)
                newList.reverse()
                mUnCompleteItemAdapter.submitList(newList)
                mUnCompleteRecyclerView.smoothScrollToPosition(0) // tmp fix recyclerview animation not showing on top
            }
        })

        mTodoListViewModel.completeTodoItemList.observe(viewLifecycleOwner,{
            Log.d(TAG,"completeTodoItemList  "+it.isNullOrEmpty())

            if (!it.isNullOrEmpty())
                mCompleteLinearLayout.visibility = View.VISIBLE
            else
                mCompleteLinearLayout.visibility = View.GONE

            it?.apply {
                val newList = mutableListOf<ItemInfo>()
                newList.addAll(it)
                newList.reverse()
                mCompleteItemAdapter.submitList(newList)
                mCompleteRecyclerView.smoothScrollToPosition(0)
            }
        })


        mTodoListViewModel.todoItemList.observe(viewLifecycleOwner,{
            if (it.isNullOrEmpty())
                mNoItemLayout.visibility = View.VISIBLE
            else
                mNoItemLayout.visibility = View.GONE
        })

        mTodoListViewModel.updateListInfoQueryId(mChosenListId)
        mTodoListViewModel.listInfo.observe(viewLifecycleOwner,{
            it?.apply{
                mListName.text = it?.name
                mListColorView.colorValue = it?.color
                mListInfo = this
            }
        })

        mTodoListViewModel.listItemList?.observe(viewLifecycleOwner,{
            mListInfoList = it
        })
    }

    private fun updateListNameByListId(listId: Int) {
        mTodoListViewModel.updateListInfoQueryId(listId)
    }

    private fun updateDataSetByListId(listId: Int){
        mTodoListViewModel.updateTodoListQueryId(listId)
    }

    override fun onRefresh(refreshListId: Int) {
        mChosenListId = refreshListId
        mUnCompleteItemAdapter.submitList(null)
        mCompleteItemAdapter.submitList(null)
        updateDataSetByListId(refreshListId)
        updateListNameByListId(refreshListId)
        if (mSnackBar?.isShownOrQueued == true)
            mSnackBar?.dismiss()
    }

    private fun createEditItemSnackBar(view: View, itemInfo: ItemInfo?){
        mSnackBar = Snackbar.make(view, "", Snackbar.LENGTH_INDEFINITE)

        val snackBarLayout: Snackbar.SnackbarLayout = mSnackBar?.view as Snackbar.SnackbarLayout
        val textView: TextView = snackBarLayout.findViewById(R.id.snackbar_text)
        textView.visibility = View.INVISIBLE

        // inflate custom view
        val customSnackBarLayout: View = this.layoutInflater.inflate(R.layout.custom_snackbar_layout, null)
        val nameEditText: EditText = customSnackBarLayout.findViewById(R.id.title_edit_text)
        itemInfo?.apply{
            nameEditText.setText(itemInfo.name)
        }

        val checkImageView: ImageView = customSnackBarLayout.findViewById(R.id.check_image_view)
        checkImageView.setOnClickListener {
            if (nameEditText.text.isNullOrEmpty()) {
                Toast.makeText(activity, "請輸入項目", Toast.LENGTH_SHORT).show()
            } else {
                Log.d(TAG, "[checkImageView] TEMP_LIST_ID = ${mTempEditListId}, titleEditText.text.toString() = " + nameEditText.text.toString())
                if (itemInfo != null) {
                    var newItemInfo:ItemInfo = itemInfo.copy()
                    newItemInfo.name = nameEditText.text.toString()

                    if (mTempEditListId != -1)
                        newItemInfo.listId = mTempEditListId

                    mTodoListViewModel.updateTodoItem(newItemInfo)
                } else {
                    mTodoListViewModel.insertTodoItem(
                        ItemInfo(name = nameEditText.text.toString(), listId = mListInfo.id)
                    )
                }
                activity?.let { it1 -> hideSoftKeyboard(it1) }
                mSnackBar?.dismiss()
                mFab.show()
            }
        }

        itemInfo?.apply{
            val listInfoLayout: ConstraintLayout = customSnackBarLayout.findViewById(R.id.list_info_view)
            val listName: TextView = customSnackBarLayout.findViewById(R.id.list_name)
            val listColorView: ColorView = customSnackBarLayout.findViewById(R.id.list_color)
            mListInfo?.apply {
                listName.text = this.name
                listColorView.colorValue = this.color
            }
            listInfoLayout.visibility = View.VISIBLE
            listInfoLayout.setOnClickListener {
                // show view to edit move to other list
                activity?.apply{
                    val inflater = this.layoutInflater
                    val view: View = inflater?.inflate(R.layout.dialog_list_view,null)
                    val alertDialog = createListDialog(view)
                    val listView = view.findViewById<ListView>(R.id.list_view)
                    val navListAdapter = NavListAdapter(activity as Activity,mListInfoList)
                    listView.adapter = navListAdapter
                    listView.onItemClickListener = AdapterView.OnItemClickListener { adapterView, view, position, id ->
                        mListInfoList[position].apply {
                            mTempEditListId = this.id
                            // update list info temporary
                            listName.text = this.name
                            listColorView.colorValue = this.color
                        }
                        alertDialog?.dismiss()
                    }
                    alertDialog?.show()
                }
            }

            val deleteItems: ImageView = customSnackBarLayout.findViewById(R.id.delete_item)
            deleteItems.visibility = View.VISIBLE
            deleteItems.setOnClickListener{
                mTodoListViewModel.deleteItem(itemInfo)
            }
        }

        snackBarLayout.setPadding(0, 0, 0, 50)
        snackBarLayout.addView(customSnackBarLayout, 0)
        snackBarLayout.setBackgroundColor(Color.WHITE)
        mSnackBar?.show()
    }

    private fun createListDialog(customView: View): AlertDialog? {
        return activity?.let {
            val builder: AlertDialog.Builder = AlertDialog.Builder(it)
            builder.setTitle("Move item to...")
            builder.setView(customView)
            builder.create()
        }
    }

    private fun hideSoftKeyboard(activity: Activity) {
        val inputMethodManager: InputMethodManager = activity.getSystemService(
            Activity.INPUT_METHOD_SERVICE) as InputMethodManager;
        if(inputMethodManager.isAcceptingText){
            inputMethodManager.hideSoftInputFromWindow(activity.currentFocus?.windowToken, 0)
        }
    }

    override fun onCheckBoxClick(isChecked: Boolean, itemInfo: ItemInfo) {
        Log.d(TAG, "[onCheckBoxClick] todoItem = $itemInfo, isChecked = $isChecked")
        itemInfo.finished = isChecked
        mTodoListViewModel.updateTodoItem(itemInfo)
    }

    override fun onItemLayoutClick(itemInfo: ItemInfo) {
        Log.d(TAG, "[onItemLayoutClick] todoItem = $itemInfo")
        // create snack bar to edit item ???? or create a fragment
        createEditItemSnackBar(mRootView,itemInfo)
    }

    override fun onStop() {
        super.onStop()
        if (mSnackBar?.isShownOrQueued == true)
            mSnackBar?.dismiss()
        activity?.let { hideSoftKeyboard(it) }
    }

    override fun isDrawerLayoutOpen() {
        Log.d(TAG,"[isDrawerLayoutOpen] ")
        if (mSnackBar?.isShownOrQueued == true) {
            mSnackBar?.dismiss()
            mFab.show()
        }
        activity?.let { hideSoftKeyboard(it) }
    }
}