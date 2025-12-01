package uni.zf.xinpian.view

import android.app.Activity
import android.app.AlertDialog
import androidx.fragment.app.Fragment
import uni.zf.xinpian.R

fun Fragment.showConfirmationDialog(message: String, onConfirm: () -> Unit) {
    AlertDialog.Builder(context, R.style.CustomAlertDialogTheme)
        .setMessage(message)
        .setPositiveButton("确认") { dialog, _ ->
            onConfirm()
            dialog.dismiss()
        }
        .setNegativeButton("取消") { dialog, _ -> dialog.dismiss() }
        .create()
        .show()
}

fun Activity.showConfirmationDialog(message: String, onConfirm: () -> Unit) {
    AlertDialog.Builder(this, R.style.CustomAlertDialogTheme)
        .setMessage(message)
        .setPositiveButton("确认") { dialog, _ ->
            onConfirm()
            dialog.dismiss()
        }
        .setNegativeButton("取消") { dialog, _ -> dialog.dismiss() }
        .create()
        .show()
}