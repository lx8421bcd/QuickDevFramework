package com.linxiao.framework.dialog

import android.content.DialogInterface
import android.content.Intent
import androidx.fragment.app.DialogFragment
import com.linxiao.framework.common.ContextProvider

/**
 * Dialog相关扩展
 * <p>refactor from AlertDialogManager</p>
 *
 * @author lx8421bcd
 * @since 2016-08-07
 */

fun DialogFragment.showInNewActivity() {
    val tag: String = this::class.java.simpleName + this.hashCode()
    DialogContainerActivity.alertDialogFragmentCacheMap[tag] = this
    val intent = Intent(ContextProvider.get(), DialogContainerActivity::class.java)
    intent.putExtra(DialogContainerActivity.DIALOG_KEY, tag)
    intent.addFlags(Intent.FLAG_ACTIVITY_NO_ANIMATION)
    intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK)
    ContextProvider.get().startActivity(intent)
}

@JvmOverloads
fun showAlert(
    message: CharSequence,
    positiveListener: DialogInterface.OnClickListener? = null,
    negativeListener: DialogInterface.OnClickListener? = null,
) {
    val dialog = AlertDialogFragment()
    dialog.setMessage(message)
    dialog.setPositiveButton(listener = positiveListener)
    dialog.setNegativeButton(listener = negativeListener)
    dialog.showInNewActivity()
}