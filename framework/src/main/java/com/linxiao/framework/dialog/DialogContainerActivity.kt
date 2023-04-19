package com.linxiao.framework.dialog

import android.os.Bundle
import androidx.fragment.app.DialogFragment
import androidx.fragment.app.Fragment
import androidx.fragment.app.FragmentManager
import com.linxiao.framework.architecture.BaseActivity

/**
 * DialogFragment 容器activity
 *
 * @author lx8421bcd
 * @since 2016-11-25
 */
class DialogContainerActivity : BaseActivity() {

    companion object {
        const val DIALOG_KEY = "DIALOG_KEY"

        @JvmField
        val alertDialogFragmentCacheMap = HashMap<String, DialogFragment>()
    }

    private val fragmentLifecycleCallbacks = object : FragmentManager.FragmentLifecycleCallbacks() {

        override fun onFragmentDetached(fm: FragmentManager, f: Fragment) {
            super.onFragmentDetached(fm, f)
            finish()
        }
    }

    public override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        supportFragmentManager.registerFragmentLifecycleCallbacks(fragmentLifecycleCallbacks, true)
        val dialogKey = intent.getStringExtra(DIALOG_KEY)
        if (dialogKey.isNullOrEmpty()) {
            finish()
            return
        }
        val dialogFragment = alertDialogFragmentCacheMap[dialogKey]
        alertDialogFragmentCacheMap.remove(dialogKey)
        if (dialogFragment == null) {
            finish()
            return
        }
        val tag = dialogFragment::class.simpleName + dialogFragment.hashCode()
        dialogFragment.show(supportFragmentManager, tag)
    }

    override fun onPause() {
        super.onPause()
        overridePendingTransition(0, 0)
    }

}