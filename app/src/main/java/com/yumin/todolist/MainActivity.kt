package com.yumin.todolist


import android.content.pm.PackageInfo
import android.content.pm.PackageManager
import android.net.ConnectivityManager
import android.net.NetworkInfo
import android.os.Bundle
import android.util.Base64
import android.view.Menu
import android.widget.*
import androidx.activity.viewModels
import androidx.appcompat.app.AlertDialog
import androidx.appcompat.app.AppCompatActivity
import androidx.appcompat.widget.Toolbar
import androidx.constraintlayout.widget.ConstraintLayout
import androidx.drawerlayout.widget.DrawerLayout
import androidx.lifecycle.Observer
import androidx.navigation.NavController
import androidx.navigation.findNavController
import androidx.navigation.ui.AppBarConfiguration
import androidx.navigation.ui.navigateUp
import androidx.navigation.ui.setupActionBarWithNavController
import androidx.navigation.ui.setupWithNavController
import com.facebook.login.LoginManager
import com.google.android.material.navigation.NavigationView
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.FirebaseUser
import com.google.firebase.auth.ktx.auth
import com.google.firebase.ktx.Firebase
import com.yumin.todolist.ui.item_list.ItemListViewModel
import com.yumin.todolist.ui.navigation_list.NavListAdapter
import java.security.MessageDigest
import java.security.NoSuchAlgorithmException


class MainActivity : AppCompatActivity(){
    private lateinit var mAppBarConfiguration: AppBarConfiguration
    private lateinit var mNavController: NavController
    private val mItemListViewModel: ItemListViewModel by viewModels {
        ViewModelFactory(
            (application as TodoListApplication).roomRepository,
            (application as TodoListApplication).firebaseRepository
        )
    }
    private var mRefreshListListener: RefreshListListener? = null
    private lateinit var mDrawerLayout: DrawerLayout
    private lateinit var listAdapter: NavListAdapter
    private var mChosenListId: Int = 0
    private lateinit var mUserName: TextView
    private lateinit var mUserEmail: TextView
    private lateinit var mFirebaseAuth: FirebaseAuth
    private lateinit var mAuthStateListener: FirebaseAuth.AuthStateListener
    private var mSignOutListener:SignOutListener? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        val toolbar: Toolbar = findViewById(R.id.toolbar)
        setSupportActionBar(toolbar)

        mDrawerLayout = findViewById(R.id.drawer_layout)

        val navigationView: NavigationView = findViewById(R.id.nav_view)
        mNavController = findNavController(R.id.nav_host_fragment)
        // Passing each menu ID as a set of Ids because each
        // menu should be considered as top level destinations.
        mAppBarConfiguration = AppBarConfiguration(
            setOf(
                R.id.nav_home,
                R.id.nav_list_view,
                R.id.nav_add_list,
                R.id.nav_backup_restore
            ), mDrawerLayout
        )
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
                val bundle = Bundle()
                bundle.putInt(KEY_CHOSEN_LIST_ID, listAdapter.getItem(position)?.id!!)
                mNavController.navigate(R.id.nav_list_view, bundle)
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

        mFirebaseAuth = Firebase.auth
        mAuthStateListener = FirebaseAuth.AuthStateListener {
            // check firebase auth user
            val user = mFirebaseAuth.currentUser
            updateUserInfoUI(user)
        }

        val userInfoLayout: LinearLayout = findViewById(R.id.user_login_layout)
        userInfoLayout.setOnClickListener {
            if (!isNetworkConnected()) {
                Toast.makeText(baseContext,R.string.setup_network_warning,Toast.LENGTH_SHORT).show()
                return@setOnClickListener
            }

            if (mFirebaseAuth.currentUser == null) {
                mNavController.navigate(R.id.nav_sign_in)
            } else {
                // sign out
                val signOutDialog = AlertDialog.Builder(this)
                    .setTitle(R.string.sign_out_dialog)
                    .setPositiveButton(R.string.option_yes) { dialog, which ->
                        for (user in mFirebaseAuth.currentUser.providerData) {
                            if (user.providerId == "facebook.com") {
                                LogUtils.logD(TAG, "User is signed in with Facebook")
                                LoginManager.getInstance().logOut()
                            }
                        }
                        Firebase.auth.signOut()
                        // update UI
                        updateUserInfoUI(null)
                        mSignOutListener?.let {
                            it.onDeleteAllItems()
                        }
                    }
                    .setNegativeButton(R.string.option_no, null)
                signOutDialog.show()
            }
            mDrawerLayout.close()
        }

        mUserName = findViewById(R.id.user_name)
        mUserEmail = findViewById(R.id.user_email)

        val backupRestore: ConstraintLayout = findViewById(R.id.backup_restore)
        backupRestore.setOnClickListener {
            if (mFirebaseAuth.currentUser == null) {
                Toast.makeText(this, R.string.sign_in_warning, Toast.LENGTH_SHORT).show()
                return@setOnClickListener
            }
            mNavController.navigate(R.id.nav_backup_restore)
            mDrawerLayout.close()
        }

        observeViewModel()

    }

    private fun getHashKey() {
        // for test
        val info: PackageInfo
        try {
            info =
                packageManager.getPackageInfo("com.yumin.todolist", PackageManager.GET_SIGNATURES)
            for (signature in info.signatures) {
                var md: MessageDigest = MessageDigest.getInstance("SHA")
                md.update(signature.toByteArray())
                val something = String(Base64.encode(md.digest(), 0))
                //String something = new String(Base64.encodeBytes(md.digest()));
                LogUtils.logE("hash key", something)
            }
        } catch (e1: PackageManager.NameNotFoundException) {
            LogUtils.logE("name not found", e1.toString())
        } catch (e: NoSuchAlgorithmException) {
            LogUtils.logE("no such an algorithm", e.toString())
        } catch (e: Exception) {
            LogUtils.logE("exception", e.toString())
        }
    }

    private fun observeViewModel(){
        mItemListViewModel.listItemTodoList?.observe(this, Observer {
            listAdapter.updateDataSet(it)
        })
    }

    override fun onStart() {
        super.onStart()
        // register a listener for update user UI
        mFirebaseAuth?.let {
            mFirebaseAuth.addAuthStateListener(mAuthStateListener)
        }
    }

    override fun onStop() {
        super.onStop()
        // remove firebase auth state listener
        mFirebaseAuth.removeAuthStateListener(mAuthStateListener)
    }

    private fun updateUserInfoUI(firebaseUser: FirebaseUser?){
        mUserName.text = firebaseUser?.displayName ?: getText(R.string.nav_header_title)
        mUserEmail.text = firebaseUser?.email ?: ""
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
        val TAG: String = MainActivity::class.java.simpleName
        const val KEY_CHOSEN_LIST_ID = "KEY_CHOSEN_LIST_ID"
    }

    fun setOnRefreshListener(refreshListListener: RefreshListListener){
        mRefreshListListener = refreshListListener
    }

    fun refreshList(id: Int){
        mRefreshListListener?.let { it.onRefresh(id) }
    }

    interface RefreshListListener {
        fun onRefresh(refreshListId: Int)
    }

    fun setSignOutListener(signOutListener: SignOutListener){
        mSignOutListener = signOutListener
    }

    interface SignOutListener {
        fun onDeleteAllItems()
    }

    private fun isNetworkConnected(): Boolean {
        val connectivity = getSystemService(CONNECTIVITY_SERVICE) as ConnectivityManager
        if (null != connectivity) {
            val info = connectivity.activeNetworkInfo
            if (null != info && info.isConnected) {
                if (info.state == NetworkInfo.State.CONNECTED) {
                    return true
                }
            }
        }
        return false
    }

}