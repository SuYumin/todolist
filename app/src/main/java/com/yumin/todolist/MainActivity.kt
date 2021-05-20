package com.yumin.todolist

import android.os.Bundle
import android.util.Log
import android.view.Menu
import android.widget.*
import androidx.activity.viewModels
import androidx.appcompat.app.AppCompatActivity
import androidx.appcompat.widget.Toolbar
import androidx.core.view.GravityCompat
import androidx.drawerlayout.widget.DrawerLayout
import androidx.lifecycle.Observer
import androidx.navigation.NavController
import androidx.navigation.findNavController
import androidx.navigation.ui.AppBarConfiguration
import androidx.navigation.ui.navigateUp
import androidx.navigation.ui.setupActionBarWithNavController
import androidx.navigation.ui.setupWithNavController
import com.google.android.material.navigation.NavigationView
import com.yumin.todolist.ui.navigation_list.NavListAdapter
import com.yumin.todolist.ui.item_list.ItemListViewModel

class MainActivity : AppCompatActivity(){
    private lateinit var mAppBarConfiguration: AppBarConfiguration
    private lateinit var mNavController: NavController
    private val mItemListViewModel: ItemListViewModel by viewModels {
        ViewModelFactory((application as TodoListApplication).repository)
    }
    private var mRefreshListListener: RefreshListListener? = null
    private lateinit var mDrawerLayout: DrawerLayout
    private var mDrawerLayoutStateListener: DrawerLayoutStateListener? = null
    private lateinit var listAdapter: NavListAdapter
    private var mChosenListId: Int = 0

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        val toolbar: Toolbar = findViewById(R.id.toolbar)
        setSupportActionBar(toolbar)

        mDrawerLayout = findViewById(R.id.drawer_layout)
        mDrawerLayout.addDrawerListener(object: DrawerLayout.SimpleDrawerListener() {
            override fun onDrawerStateChanged(newState: Int) {
                when(newState) {
                    DrawerLayout.STATE_SETTLING -> {
                        if (!mDrawerLayout.isDrawerOpen(GravityCompat.START)) {
                            mDrawerLayoutStateListener?.isDrawerLayoutOpen()
                        }
                    }
                }
            }
        })

        val navigationView: NavigationView = findViewById(R.id.nav_view)
        mNavController = findNavController(R.id.nav_host_fragment)
        // Passing each menu ID as a set of Ids because each
        // menu should be considered as top level destinations.
        mAppBarConfiguration = AppBarConfiguration(setOf(R.id.nav_home,R.id.nav_list_view,R.id.nav_add_list), mDrawerLayout)
        setupActionBarWithNavController(mNavController, mAppBarConfiguration)
        navigationView.setupWithNavController(mNavController)

        val allItem: LinearLayout = navigationView.findViewById(R.id.all_item_view)
        allItem.setOnClickListener {
            if (mNavController.currentDestination?.id != R.id.nav_home) {
                mNavController.navigate(R.id.nav_home)
            }
            mDrawerLayout.close()
        }

        val navigationListView: ListView = navigationView.findViewById(R.id.nav_list_view)
        listAdapter = NavListAdapter(this, null)
        navigationListView.adapter = listAdapter

        navigationListView.onItemClickListener = AdapterView.OnItemClickListener { adapterView, view, position, id ->
            // change data set and gray out item
            if (mNavController.currentDestination?.id != R.id.nav_list_view) {
                Log.d(TAG,"[currentDestination?.getId() != R.id.nav_list]")
                val bundle = Bundle()
                bundle.putInt(KEY_CHOSEN_LIST_ID,listAdapter.getItem(position)?.id!!)
                mNavController.navigate(R.id.nav_list_view,bundle)
            }
            mChosenListId = listAdapter.getItem(position)?.id!!
            mRefreshListListener?.onRefresh(mChosenListId)
            mDrawerLayout.close()
        }

        val addList: LinearLayout = navigationView.findViewById(R.id.add_list_linear_layout)
        addList.setOnClickListener {
            mNavController.navigate(R.id.nav_add_list)
            mDrawerLayout.close()
        }

        observeViewModel()
    }

    private fun observeViewModel(){
        mItemListViewModel.listItemList?.observe(this, Observer {
            listAdapter.updateDataSet(it)
        })
    }

    interface DrawerLayoutStateListener{
        fun isDrawerLayoutOpen()
    }

    fun setUpDrawerLayoutState(listener:DrawerLayoutStateListener){
        this.mDrawerLayoutStateListener = listener
    }

    override fun onBackPressed() {
        if (mNavController.currentDestination?.id == R.id.nav_home)
            finish()
        else
            super.onBackPressed()
    }

    override fun onCreateOptionsMenu(menu: Menu): Boolean {
        // Inflate the menu; this adds items to the action bar if it is present.
//        menuInflater.inflate(R.menu.main, menu)
        return true
    }

    override fun onSupportNavigateUp(): Boolean {
        val navController = findNavController(R.id.nav_host_fragment)
        return navController.navigateUp(mAppBarConfiguration) || super.onSupportNavigateUp()
    }

    companion object {
        val TAG: String = MainActivity::getLocalClassName.toString()
        const val KEY_CHOSEN_LIST_ID = "KEY_CHOSEN_LIST_ID"
    }

    fun setOnRefreshHomeListener(refreshListListener: RefreshListListener){
        mRefreshListListener = refreshListListener
    }

    interface RefreshListListener {
        fun onRefresh(refreshListId: Int)
    }
}