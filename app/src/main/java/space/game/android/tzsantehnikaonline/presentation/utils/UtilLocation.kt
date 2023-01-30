package space.game.android.tzsantehnikaonline.presentation.utils

import android.content.Context
import android.graphics.Bitmap
import android.graphics.Canvas
import android.location.Location
import android.location.LocationManager
import androidx.core.content.ContextCompat

object UtilLocation {
    fun getLastKnownLocation(context: Context): Location? {
        val manager = context.getSystemService(Context.LOCATION_SERVICE) as LocationManager
        var utilLocation: Location?
        val providers = manager.getProviders(false)
        for (provider in providers) {
            utilLocation = manager.getLastKnownLocation(provider!!)
            if (utilLocation != null) return utilLocation
        }
        return null
    }
}

fun Context.getBitmapFromVectorDrawable(drawableId: Int): Bitmap? {
    val drawable = ContextCompat.getDrawable(this, drawableId) ?: return null
    val bitmap = Bitmap.createBitmap(
        drawable.intrinsicWidth,
        drawable.intrinsicHeight,
        Bitmap.Config.ARGB_8888
    ) ?: return null
    val canvas = Canvas(bitmap)
    drawable.setBounds(0, 0, canvas.width, canvas.height)
    drawable.draw(canvas)
    return bitmap
}