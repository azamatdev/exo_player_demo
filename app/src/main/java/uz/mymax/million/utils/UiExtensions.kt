package uz.mymax.million.utils

import android.app.ActivityManager
import android.content.Context
import android.util.Log
import android.util.TypedValue
import android.view.View
import android.view.animation.AccelerateDecelerateInterpolator
import androidx.fragment.app.Fragment

@Suppress("DEPRECATION")
fun Context.isServiceRunning(serviceClassName: String): Boolean {
    val activityManager = getSystemService(Context.ACTIVITY_SERVICE) as? ActivityManager

    return activityManager?.getRunningServices(Integer.MAX_VALUE)?.any { it.service.className == serviceClassName } ?: false
}


fun Fragment.log(message: String, TAG: String = "HomeTag") {
    Log.d(TAG, message)
}

/**
 * Animating view with slide animation
 */
fun View.slideDown() {
    this.animate().translationY(250f).alpha(0.0f).setDuration(50).setInterpolator(
        AccelerateDecelerateInterpolator()
    )
    this.hide()
}

fun View.slideUp() {
    this.show()
    this.animate().translationY(0f).alpha(1.0f).setDuration(150).setInterpolator(
        AccelerateDecelerateInterpolator()
    )
}


/**
 * Hide the view. (visibility = View.INVISIBLE)
 */
fun View.hide(): View {
    if (visibility != View.GONE) {
        visibility = View.GONE
    }
    return this
}

fun View.show(): View {
    if (visibility != View.VISIBLE) {
        visibility = View.VISIBLE
    }
    return this
}


