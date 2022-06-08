package kr.tangram.smartgym.ui.welcome

import android.os.Bundle
import android.util.Log
import android.view.View
import android.widget.TextView
import androidx.appcompat.app.AppCompatActivity
import androidx.databinding.DataBindingUtil
import androidx.fragment.app.Fragment
import androidx.fragment.app.FragmentActivity
import androidx.viewpager2.adapter.FragmentStateAdapter
import androidx.viewpager2.widget.ViewPager2
import butterknife.ButterKnife
import butterknife.OnClick
import com.tbuonomo.viewpagerdotsindicator.DotsIndicator
import kr.tangram.smartgym.R

class WelComeActivity : AppCompatActivity() {

    lateinit var view_paper : ViewPager2
    lateinit var tv_next : TextView

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        setContentView(R.layout.activity_welcome)

        ButterKnife.bind(this)

        view_paper = findViewById<ViewPager2>(R.id.view_paper)
        tv_next = findViewById<TextView>(R.id.tv_next)
        var indicator = findViewById<DotsIndicator>(R.id.indicator)

        view_paper.apply {
            adapter = ViewAdapter(context as FragmentActivity)
        }

        view_paper.registerOnPageChangeCallback(object : ViewPager2.OnPageChangeCallback() {
            override fun onPageSelected(position: Int) {
                super.onPageSelected(position)
                notifyNextChanged(position)
            }
        })

        indicator.setViewPager2(view_paper)

    }

    class ViewAdapter(fa: FragmentActivity): FragmentStateAdapter(fa) {

        override fun getItemCount(): Int = 3

        override fun createFragment(position: Int): Fragment {
            return when (position) {
                0 -> { WelComeFragment.newInstance(position)}
                1 -> { WelComeFragment.newInstance(position)}
                else -> { WelComeFragment.newInstance(position)}
            }
        }
    }

    fun notifyNextChanged(position: Int)
    {
        if(2 <= position) tv_next.setText("시작")
        else tv_next.setText("다음")
    }

    @OnClick(R.id.tv_next)
    fun onClickNext()
    {
        if(view_paper.currentItem < 2){
            view_paper.currentItem++
            notifyNextChanged(view_paper.currentItem)
        }
    }



}