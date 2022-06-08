package kr.tangram.smartgym.ui.welcome

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.fragment.app.Fragment
import androidx.viewpager2.widget.ViewPager2
import kr.tangram.smartgym.R

class WelComeFragment : Fragment(){

    lateinit var tv_title : TextView
    lateinit var tv_content : TextView

    companion object{
        fun newInstance(position: Int) : WelComeFragment{
            val f = WelComeFragment()
            val args = Bundle()
            args.putInt("position", position)
            f.arguments = args;
            return f
        }
    }

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {

        val view: View = inflater.inflate(R.layout.fragment_welcome, container, false)


        tv_title = view.findViewById<TextView>(R.id.tv_title)
        tv_content = view.findViewById<TextView>(R.id.tv_content)


        arguments?.let {
            notifyDataChanged(it.getInt("position", 0))
        }



        return view
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
    }

    override fun onActivityCreated(savedInstanceState: Bundle?)
    {
        super.onActivityCreated(savedInstanceState);
    }


    fun notifyDataChanged(position : Int)
    {
        if(position == 0){
            tv_title.setText("환영합니다")
            tv_content.setText("지도 위에 펼쳐지는 가상의 스마트 짐입니다")
        } else if(position == 1){
            tv_title.setText("운동하고 공유하고")
            tv_content.setText("커뮤니티 가입해 내 주변 사용자들과 함께 운동해요")
        } else{
            tv_title.setText("운동을 게임처럼")
            tv_content.setText("다양한 챌린지에 도전하고 재미와 보상을 한번에!")
        }

    }

}