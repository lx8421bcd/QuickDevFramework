package com.linxiao.quickdevframework.main

import android.os.Bundle
import android.util.Log
import android.view.KeyEvent
import androidx.appcompat.app.ActionBarDrawerToggle
import androidx.core.view.GravityCompat
import androidx.fragment.app.Fragment
import androidx.fragment.app.FragmentManager
import androidx.fragment.app.FragmentTransaction
import androidx.recyclerview.widget.DefaultItemAnimator
import androidx.recyclerview.widget.LinearLayoutManager
import com.linxiao.framework.architecture.SimpleViewBindingActivity
import com.linxiao.framework.common.ToastAlert
import com.linxiao.quickdevframework.R
import com.linxiao.quickdevframework.databinding.ActivityMainBinding
import com.linxiao.quickdevframework.databinding.ContentMainBinding
import com.linxiao.quickdevframework.sample.adapter.AdapterTestFragment
import com.linxiao.quickdevframework.sample.frameworkapi.ApplicationApiFragment
import com.linxiao.quickdevframework.sample.frameworkapi.BiometricApiFragment
import com.linxiao.quickdevframework.sample.frameworkapi.DialogApiFragment
import com.linxiao.quickdevframework.sample.frameworkapi.NotificationApiFragment
import com.linxiao.quickdevframework.sample.frameworkapi.PermissionApiFragment
import com.linxiao.quickdevframework.sample.frameworkapi.ToastApiFragment
import com.linxiao.quickdevframework.sample.json.JsonTestFragment
import com.linxiao.quickdevframework.sample.netapi.DownloadTestFragment
import com.linxiao.quickdevframework.sample.netapi.NetTestFragment
import com.linxiao.quickdevframework.sample.widget.WidgetsGuideFragment

class MainActivity : SimpleViewBindingActivity<ActivityMainBinding>() {

    companion object {
        private const val KEY_CURRENT_TAG = "CurrentTag"
        private const val KEY_TAGS = "FragmentTags"
        private const val KEY_CLASS_NAMES = "FragmentClassNames"
    }

    private var contentBinding: ContentMainBinding? = null

    private val fragmentTags = ArrayList<String?>()
    private val fragmentClassNames = ArrayList<String>()
    private val fragments = ArrayList<Fragment>()
    private var mFragmentManager: FragmentManager? = null
    private var currentTag: String? = null
    private var exitTime: Long = 0
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        //        setImmersiveMode(true);
//        StatusBarUtil.setStatusBarColor(this, Color.TRANSPARENT);
//        StatusBarUtil.setStatusBarLightMode(this, true);
        contentBinding = viewBinding.contentMain
        setSupportActionBar(viewBinding.toolbar)
        mFragmentManager = supportFragmentManager
        if (savedInstanceState != null) {
            restoreFragments(savedInstanceState)
        } else {
            initFragments()
        }
        val toggle = ActionBarDrawerToggle(
            this,
            contentBinding!!.drawerMain,
            viewBinding.toolbar,
            R.string.app_name,
            R.string.app_name
        )
        contentBinding!!.drawerMain.setDrawerListener(toggle)
        toggle.syncState()
        initApiSampleList()
    }

    private fun initApiSampleList() {
        val apiSampleList: MutableList<ApiSampleObject> = ArrayList()
        apiSampleList.add(ApiSampleObject("Application API", "ApplicationApiFragment"))
        apiSampleList.add(ApiSampleObject("Dialog API", "DialogApiFragment"))
        apiSampleList.add(ApiSampleObject("Json parse API", "JsonTestFragment"))
        apiSampleList.add(ApiSampleObject("Biometric API", "BiometricApiFragment"))
        apiSampleList.add(ApiSampleObject("Notification API", "NotificationApiFragment"))
        apiSampleList.add(ApiSampleObject("Toast API", "ToastApiFragment"))
        apiSampleList.add(ApiSampleObject("Permission API", "PermissionApiFragment"))
        apiSampleList.add(ApiSampleObject("Network API", "NetTestFragment"))
        apiSampleList.add(ApiSampleObject("Download Test", "DownloadTestFragment"))
        apiSampleList.add(ApiSampleObject("Adapter API", "AdapterTestFragment"))
        apiSampleList.add(ApiSampleObject("Widgets", "WidgetsGuideFragment"))
        val listAdapter = ApiSampleListAdapter()
        listAdapter.items = apiSampleList
        listAdapter.setOnItemClickListener { adapter, view, position ->
            val obj = adapter.getItem(position)
            switchFragment(obj!!.target)
            if (supportActionBar != null) {
                supportActionBar!!.title = obj.apiName
            }
            contentBinding!!.drawerMain.closeDrawer(GravityCompat.START)
        }
        contentBinding!!.rcvApiSampleList.setLayoutManager(LinearLayoutManager(this))
        contentBinding!!.rcvApiSampleList.setItemAnimator(DefaultItemAnimator())
        contentBinding!!.rcvApiSampleList.setAdapter(listAdapter)
    }

    private fun initFragments() {
        addFragment(ApplicationApiFragment(), "ApplicationApiFragment")
        addFragment(DialogApiFragment(), "DialogApiFragment")
        addFragment(JsonTestFragment(), "JsonTestFragment")
        addFragment(BiometricApiFragment(), "BiometricApiFragment")
        addFragment(NotificationApiFragment(), "NotificationApiFragment")
        addFragment(ToastApiFragment(), "ToastApiFragment")
        addFragment(PermissionApiFragment(), "PermissionApiFragment")
        addFragment(NetTestFragment(), "NetTestFragment")
        addFragment(DownloadTestFragment(), "DownloadTestFragment")
        addFragment(AdapterTestFragment(), "AdapterTestFragment")
        addFragment(WidgetsGuideFragment(), "WidgetsGuideFragment")
        currentTag = "DialogApiFragment"
        switchFragment(currentTag)
    }

    private fun restoreFragments(savedInstanceState: Bundle) {
        currentTag = savedInstanceState.getString(KEY_CURRENT_TAG, "")
        fragmentTags.clear()
        fragmentTags.addAll(savedInstanceState.getStringArrayList(KEY_TAGS)!!)
        fragmentClassNames.clear()
        fragmentClassNames.addAll(savedInstanceState.getStringArrayList(KEY_CLASS_NAMES)!!)
        Log.d(TAG, fragmentTags.toString())
        Log.d(TAG, fragmentClassNames.toString())
        Log.d(TAG, "CurrentTag = $currentTag")
        for (i in fragmentTags.indices) {
            var fragment = mFragmentManager!!.findFragmentByTag(fragmentTags[i])
            if (fragment == null) {
                try {
                    val fragmentClass = Class.forName(fragmentClassNames[i])
                    val obj = fragmentClass.newInstance()
                    if (obj is Fragment) {
                        fragment = obj
                    }
                } catch (e: Exception) {
                    e.printStackTrace()
                }
            }
            if (fragment != null) {
                fragments.add(fragment)
                if (fragment.isAdded) {
                    mFragmentManager!!.beginTransaction()
                        .hide(fragment)
                        .commitAllowingStateLoss()
                }
            }
        }
        switchFragment(currentTag)
    }

    private fun switchFragment(tag: String?) {
        /* Fragment 切换 */
        val transaction = mFragmentManager!!.beginTransaction()
        if (fragmentTags.indexOf(tag) < 0) {
            return
        }
        val showFragment = fragments[fragmentTags.indexOf(tag)]
        val currentFragment = mFragmentManager!!.findFragmentByTag(currentTag)
        if (currentFragment != null) {
            transaction.hide(currentFragment)
        }
        if (showFragment.isAdded) {
            transaction.show(showFragment)
        } else {
            transaction.add(R.id.content_frame, showFragment, tag)
            transaction.show(showFragment)
        }
        transaction.setTransitionStyle(FragmentTransaction.TRANSIT_FRAGMENT_FADE)
        transaction.commitAllowingStateLoss()
        currentTag = tag
    }

    protected fun addFragment(fragment: Fragment, tag: String) {
        fragments.add(fragment)
        fragmentTags.add(tag)
        fragmentClassNames.add(fragment.javaClass.getName())
    }

    override fun onSaveInstanceState(outState: Bundle) {
        super.onSaveInstanceState(outState)
        outState.putString(KEY_CURRENT_TAG, currentTag)
        outState.putStringArrayList(KEY_TAGS, fragmentTags)
        outState.putStringArrayList(KEY_CLASS_NAMES, fragmentClassNames)
    }

    override fun onKeyDown(keyCode: Int, event: KeyEvent): Boolean {
        //2秒内按两次退出
        if (keyCode == KeyEvent.KEYCODE_BACK && event.action == KeyEvent.ACTION_DOWN) {
            if (System.currentTimeMillis() - exitTime > 2000) {
                ToastAlert.showToast(this, getString(R.string.press_again_exit), 2000)
                exitTime = System.currentTimeMillis()
            } else {
                finish()
                System.exit(0)
            }
            return true
        }
        return super.onKeyDown(keyCode, event)
    }
}
