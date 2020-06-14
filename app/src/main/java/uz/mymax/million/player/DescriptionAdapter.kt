//package uz.mymax.million.player
//
//import android.app.PendingIntent
//import android.content.Context
//import android.content.Intent
//import android.graphics.Bitmap
//import androidx.core.app.TaskStackBuilder
//import com.google.android.exoplayer2.Player
//import com.google.android.exoplayer2.ui.PlayerNotificationManager
//import uz.mymax.million.MainActivity
//import uz.mymax.million.data.Data
//
//
//class DescriptionAdapter(val context : Context) : PlayerNotificationManager.MediaDescriptionAdapter{
//    override fun createCurrentContentIntent(player: Player?): PendingIntent? {
//        val window: Int = player!!.currentWindowIndex
//        // Create an Intent for the activity you want to start
//        val resultIntent = Intent(context, MainActivity::class.java)
//        // Create the TaskStackBuilder
//        return TaskStackBuilder.create(context).run {
//            // Add the intent, which inflates the back stack
//            addNextIntentWithParentStack(resultIntent)
//            // Get the PendingIntent containing the entire back stack
//            getPendingIntent(0, PendingIntent.FLAG_UPDATE_CURRENT)
//        }
//    }
//
//    override fun getCurrentContentText(player: Player?): String? {
//        val window = player?.currentWindowIndex
//        return Data.getDuration(window!!)
//    }
//
//    override fun getCurrentContentTitle(player: Player?): String {
//        val window = player?.currentWindowIndex
//        return Data.getTitle(window!!)
//    }
//
//    override fun getCurrentLargeIcon(
//        player: Player?,
//        callback: PlayerNotificationManager.BitmapCallback?
//    ): Bitmap? {
//        val window: Int = player!!.currentWindowIndex
//        //        if (largeIcon == null && getLargeIconUri(window) != null) { // load bitmap async
////            loadBitmap(getLargeIconUri(window), callback)
////            return getPlaceholderBitmap()
////        }
//        return Data.getLargeIcon(window, context)
//    }
//}