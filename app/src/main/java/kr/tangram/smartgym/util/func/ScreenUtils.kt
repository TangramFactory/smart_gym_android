package com.tangramfactory.smartrope.common.func

import android.content.Context
import android.content.res.Configuration
import android.graphics.Point
import android.os.Build
import android.util.DisplayMetrics
import android.util.TypedValue
import android.view.WindowManager
import kr.tangram.smartgym.util.loge


/**
 * Created by sanghyun on 2018. 3. 14..
 */

class ScreenUtils {
    companion object {

        private var mContext: Context? = null
        private var sContentPortraitHeight = -1
        private var sContentLandHeight = -1
        private var sContentPortraitWdith = -1
        private var sContentLandWidth = -1
        private var sActionBarPortraitHeight = -1
        private var sActionBarLandHeight = -1
        private var sScreenPortraitWidth = -1
        private var sScreenPortraitHeight = -1
        private var sScreenLandWidth = -1
        private var sScreenLandHeight = -1

        //private fun ScreenUtils(): ??? {}

        fun init(context: Context) {
            mContext = context.applicationContext
        }

        fun dpToPx(dp: Int): Int {
            return (dp * getContext()!!.resources.displayMetrics.density).toInt()
        }

        fun pxToDp(px: Int): Int {
            return (px / getContext()!!.resources.displayMetrics.density).toInt()
        }

        fun density():Float {
            return getContext()!!.resources.displayMetrics.density
        }

        private fun getContext(): Context? {

            return mContext!!
        }

        /**
         * @return [true] if the device has a small screen, [false] otherwise.
         */
        fun hasSmallScreen(): Boolean {
            return getScreenType() == Configuration.SCREENLAYOUT_SIZE_SMALL
        }

        /**
         * @return [true] if the device has a normal screen, [false] otherwise.
         */
        fun hasNormalScreen(): Boolean {
            return getScreenType() == Configuration.SCREENLAYOUT_SIZE_NORMAL
        }

        /**
         * @return [true] if the device has a large screen, [false] otherwise.
         */
        fun hasLargeScreen(): Boolean {
            return getScreenType() == Configuration.SCREENLAYOUT_SIZE_LARGE
        }

        /**
         * @return [true] if the device has an extra large screen, [false] otherwise.
         */
        fun hasXLargeScreen(): Boolean {
            return getScreenType() == Configuration.SCREENLAYOUT_SIZE_XLARGE
        }

        fun isTablet(context: Context): Boolean {
            return context.resources.configuration
                    .screenLayout and Configuration.SCREENLAYOUT_SIZE_MASK >= Configuration.SCREENLAYOUT_SIZE_LARGE
        }

        //public static boolean isTablet(Resources res) {
        //    if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.HONEYCOMB_MR2) {
        //        return mContext.getResources().getConfiguration().smallestScreenWidthDp
        //                >= 600;
        //    } else { // for devices without smallestScreenWidthDp reference calculate if device screen is over 600
        //        return (res.getDisplayMetrics().widthPixels / res.getDisplayMetrics().density) >= 600;
        //    }
        //}

        fun getDrawerWidth(): Int {
            val res = getContext()!!.resources

            return if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.HONEYCOMB_MR2) {
                if (res.configuration.smallestScreenWidthDp >= 600 || res.configuration.orientation === Configuration.ORIENTATION_LANDSCAPE) {
                    // device is a tablet
                    (320 * res.displayMetrics.density).toInt()
                } else {
                    (res.displayMetrics.widthPixels - 56 * res.displayMetrics.density).toInt()
                }
            } else { // for devices without smallestScreenWidthDp reference calculate
                // if device screen is over 600 dp
                if (res.displayMetrics.widthPixels / res.displayMetrics.density >= 600 || res.configuration.orientation === Configuration.ORIENTATION_LANDSCAPE) {
                    (320 * res.displayMetrics.density).toInt()
                } else {
                    (res.displayMetrics.widthPixels - 56 * res.displayMetrics.density).toInt()
                }
            }
        }

        fun getStatusBarHeight(): Int {
            val resourceId = getContext()!!.resources.getIdentifier("status_bar_height", "dimen",
                    "android")
            return if (resourceId > 0) {
                getContext()!!.resources.getDimensionPixelSize(resourceId)
            } else 0
        }

        fun getActionBarHeight(): Int {
            if (this.isLandscape()) {
                if (sActionBarLandHeight == -1) {
                    sActionBarLandHeight = resolveActionBarHeight()
                }
                return sActionBarLandHeight
            } else {
                if (sActionBarPortraitHeight == -1) {
                    sActionBarPortraitHeight = resolveActionBarHeight()
                }
                return sActionBarPortraitHeight
            }
        }

        fun getMaterialActionBarHeight(): Int {
            return mContext!!.resources.getDimensionPixelSize(
                    androidx.appcompat.R.dimen.abc_action_bar_default_height_material)
        }

        fun getNavigationBarHeight(context: Context): Int {
            val resources = context.resources
            val resourceId = resources.getIdentifier("navigation_bar_height", "dimen", "android")
            return if (resourceId > 0) {
                resources.getDimensionPixelSize(resourceId)
            } else 0
        }

        fun getScreenWidth(): Int {
            if (this.isLandscape()) {
                if (sScreenLandWidth == -1) {
                    resolveScreenSize()
                }
                return sScreenLandWidth
            } else {
                if (sScreenPortraitWidth == -1) {
                    resolveScreenSize()
                }
                return sScreenPortraitWidth
            }
        }

        /**
         * include StatusBarHeight
         * @return
         */
        fun getScreenHeight(): Int {
            if (this.isLandscape()) {
                if (sScreenLandHeight == -1) {
                    resolveScreenSize()
                }

                return sScreenLandHeight
            } else {
                if (sScreenPortraitHeight == -1) {
                    resolveScreenSize()
                }

                return sScreenPortraitHeight
            }
        }

        /**
         * exclude NavigationBarHeight and StatusBarHeight
         * @return
         */
        fun getContentHeight(): Int {
            if (this.isLandscape()) {
                if (sContentLandHeight == -1) {
                    val point = resolveContentSize()
                    sContentLandHeight = point.y
                    sContentLandWidth = point.x
                }
                return sContentLandHeight
            } else {
                //portrait
                if (sContentPortraitHeight == -1) {
                    val point = resolveContentSize()
                    sContentPortraitHeight = point.y
                    sContentPortraitWdith = point.x
                }

                return sContentPortraitHeight
            }
        }

        /**
         * exclude NavigationBarHeight
         * @return
         */
        fun getContentWidth(): Int {
            if (this.isLandscape()) {
                if (sContentLandWidth == -1) {
                    val point = resolveContentSize()
                    sContentLandHeight = point.y
                    sContentLandWidth = point.x
                }

                return sContentLandWidth
            } else {
                //portrait
                if (sContentPortraitWdith == -1) {
                    val point = resolveContentSize()
                    sContentPortraitHeight = point.y
                    sContentPortraitWdith = point.x
                }
                return sContentPortraitWdith
            }
        }

        fun isLandscape(): Boolean {
            //
            // 종종 NullPointerException 문제가 발생
            //
            return try {
                getContext()!!.resources.configuration.orientation == Configuration.ORIENTATION_LANDSCAPE
            } catch (e:NullPointerException) {
                false
            }
        }

        fun getScreenType(): Int {
            return getContext()!!.resources.configuration.screenLayout and Configuration.SCREENLAYOUT_SIZE_MASK
        }

        ///**
        // * In Lollipop include NavigationBar
        // * @param view
        // */
        //public static void getRootView(final View view){
        //    view.getViewTreeObserver().addOnPreDrawListener(new ViewTreeObserver.OnPreDrawListener() {
        //        @Override public boolean onPreDraw() {
        //            view.getRootView().getHeight();
        //            return true;
        //        }
        //    });
        //}

        private fun resolveContentSize(): Point {
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.HONEYCOMB_MR2) {
                try {
                    val configuration = getContext()!!.resources.configuration
                    return Point(dpToPx(configuration.screenWidthDp), dpToPx(configuration.screenHeightDp))
                } catch (e:Exception) {
                    loge("",e.toString())
                    return Point(0,0)
                }
            } else {
                // APIs prior to v13 gave the screen dimensions in pixels.
                // We convert them to DIPs before returning them.
                try {
                    val windowManager = getContext()!!.getSystemService(Context.WINDOW_SERVICE) as WindowManager
                    val displayMetrics = DisplayMetrics()
                    windowManager.defaultDisplay.getMetrics(displayMetrics)
                    return Point(displayMetrics.widthPixels, displayMetrics.heightPixels)
                } catch (e:Exception) {
                    loge("",e.toString())
                    return Point(0,0)
                }
            }
        }

        private fun resolveActionBarHeight(): Int {
            val typedValue = TypedValue()
//            if (Build.VERSION.SDK_INT > Build.VERSION_CODES.HONEYCOMB) {
                getContext()!!.theme.resolveAttribute(android.R.attr.actionBarSize, typedValue, true)
//            } else {
//                getContext()!!.theme.resolveAttribute(R.attr.actionBarSize, typedValue, true)
//            }
            return dpToPx(typedValue.data)
        }

        private fun resolveScreenSize() {
            val w = getContext()!!.getSystemService(Context.WINDOW_SERVICE) as WindowManager
            val d = w.defaultDisplay

//            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.HONEYCOMB_MR2) {
                val size = Point()
                d.getSize(size)
                if (this.isLandscape()) {
                    sScreenLandWidth = size.x
                    sScreenLandHeight = size.y
                } else {
                    sScreenPortraitWidth = size.x
                    sScreenPortraitHeight = size.y
                }
//            } else {
//                if (this.isLandscape()) {
//                    sScreenLandWidth = d.width
//                    sScreenLandHeight = d.height
//                } else {
//                    sScreenPortraitWidth = d.width
//                    sScreenPortraitHeight = d.height
//                }
//            }
        }

    }


}
