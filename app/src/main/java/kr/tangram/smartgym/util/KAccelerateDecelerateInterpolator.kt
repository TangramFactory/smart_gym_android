package kr.tangram.smartgym.util

/**
 * Created by sanghyun on 2018. 3. 20..
 */

class KAccelerateDecelerateInterpolator : android.view.animation.Interpolator {

    override fun getInterpolation(t: Float): Float {
        var x = t * 2.0f
        if (t < 0.5f) {
            //return 0.5f * x * x * x
            return 0.5f * x * x
        }
        x = (t - 0.5f) * 2 - 1
        return 0.5f * x * x * x + 1
    }

}