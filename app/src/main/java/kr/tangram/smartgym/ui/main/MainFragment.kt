package kr.tangram.smartgym.ui.main

import android.content.Intent
import androidx.lifecycle.ViewModelProvider
import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import android.widget.TextView
import com.google.firebase.auth.ktx.auth
import com.google.firebase.ktx.Firebase
import kr.tangram.smartgym.R
import kr.tangram.smartgym.ui.login.LoginActivity
import org.w3c.dom.Text

class MainFragment : Fragment() {

    companion object {
        @JvmStatic
        fun newInstance() = MainFragment()
    }

    private lateinit var viewModel: MainViewModel

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?,
    ): View? {
        val view = inflater.inflate(R.layout.fragment_main, container, false)
        viewModel = ViewModelProvider(this)[MainViewModel::class.java]
        val auth = Firebase.auth
        val tv = view.findViewById<TextView>(R.id.tvMain)
        tv.text = auth.currentUser?.email.toString()
        val btn = view.findViewById<Button>(R.id.button)
        btn.setOnClickListener {
            Firebase.auth.signOut()
            startActivity(Intent(requireActivity(), LoginActivity::class.java))
        }
        return view
    }



}