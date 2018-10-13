package io.sabag.androidConferences.matchers

import android.view.View
import android.widget.ImageView
import androidx.core.content.ContextCompat
import androidx.test.espresso.matcher.BoundedMatcher
import org.hamcrest.Description
import android.graphics.Bitmap
import android.graphics.Canvas
import android.graphics.drawable.Drawable
import androidx.annotation.DrawableRes


class BackgroundResourceMatcher(
        @DrawableRes private val drawableResource: Int
) : BoundedMatcher<View, ImageView>(ImageView::class.java) {

    companion object {
        fun withBackgroundResource(@DrawableRes backgroundColor: Int) = BackgroundResourceMatcher(backgroundColor)
    }

    override fun matchesSafely(item: ImageView): Boolean {
        val itemBitmap = getBitmap(item.drawable)
        val resourceBitmap = getBitmap(ContextCompat.getDrawable(item.context, drawableResource)!!)
        return (itemBitmap.sameAs(resourceBitmap))
    }

    override fun describeTo(description: Description?) {
        description?.appendText("Image resource not match to given drawable resource $drawableResource")
    }

    private fun getBitmap(drawable: Drawable): Bitmap {
        val bitmap = Bitmap.createBitmap(drawable.intrinsicWidth, drawable.intrinsicHeight, Bitmap.Config.ARGB_8888)
        val canvas = Canvas(bitmap)
        drawable.setBounds(0, 0, canvas.width, canvas.height)
        drawable.draw(canvas)
        return bitmap
    }
}