package kr.tangram.smartgym.ui.leftMenu

import android.content.Intent
import android.graphics.Bitmap
import android.graphics.drawable.Drawable
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.os.Parcelable
import android.widget.ImageView
import androidx.activity.result.ActivityResultLauncher
import androidx.activity.result.contract.ActivityResultContracts
import androidx.fragment.app.DialogFragment
import com.bumptech.glide.Glide
import com.bumptech.glide.request.target.CustomTarget
import com.bumptech.glide.request.transition.Transition
import kr.tangram.smartgym.ImageSize
import kr.tangram.smartgym.R
import kr.tangram.smartgym.base.BaseActivity
import kr.tangram.smartgym.databinding.ActivityGymCreateBinding
import kr.tangram.smartgym.ui.main.map.BottomDialogFragment
import kr.tangram.smartgym.ui.main.map.GymCreateBottomDialogFragment
import kr.tangram.smartgym.ui.main.map.gym.GymViewModel
import org.koin.androidx.viewmodel.ext.android.getViewModel

class GymCreateActivity : BaseActivity<ActivityGymCreateBinding, GymViewModel>(R.layout.activity_gym_create) {
    override val viewModel: GymViewModel by lazy { getViewModel() }
    private lateinit var changedImageView : ImageView
    private lateinit var imageViewType : String

    private val selectMenuActivityList = arrayListOf(SelectMenu.JUMP.toString(), SelectMenu.RUN.toString())
    private val selectMenuTypeList =arrayListOf(SelectMenu.SCHOOL.toString(), SelectMenu.GYM.toString(), SelectMenu.PARK.toString(), SelectMenu.BRANDED.toString())
    override fun onCreate(savedInstanceState: Bundle?) {
        binding = ActivityGymCreateBinding.inflate(layoutInflater)
        super.onCreate(savedInstanceState)
        setContentView(binding.root)
        binding.viewModel = viewModel
        binding.lifecycleOwner = this

        binding.ivTopImage.setOnClickListener {
            changedImageView = binding.ivTopImage
            imageViewType = "BackGround"
            getProfileImageFromDevice()
        }

        binding.civGymMarker.setOnClickListener {
            changedImageView = binding.civGymMarker
            imageViewType = "Marker"
            getProfileImageFromDevice()
        }

        binding.btnGymCreate.setOnClickListener {
            val gymName = binding.edtGymName.text.toString()
            val gymIntro = binding.edtGymIntroduce.text.toString()
            val gymCreateInfo = binding.edtGymCreateInfo.text.toString()

            viewModel.saveGymCreateData(gymName, gymIntro, gymCreateInfo)
        }

        binding.btnGymSelectActivity.setOnClickListener {
            openBottomDialogFragment(selectMenuActivityList){ clickedItem -> viewModel.setSelectedActivity(clickedItem) }

//            GymCreateBottomDialogFragment(selectMenuActivityList) { viewModel.setSelectedActivity(it) }.run {
//                setStyle(DialogFragment.STYLE_NORMAL, R.style.AppBottomSheetDialogTheme)
//                show(supportFragmentManager, GymCreateBottomDialogFragment::class.java.simpleName)
//            }
        }

        binding.btnGymSelectType.setOnClickListener {
            openBottomDialogFragment(selectMenuTypeList){ clickedItem -> viewModel.setSelectedType(clickedItem) }

//            GymCreateBottomDialogFragment(selectMenuTypeList) { viewModel.setSelectedType(it) }.run {
//                setStyle(DialogFragment.STYLE_NORMAL, R.style.AppBottomSheetDialogTheme)
//                show(supportFragmentManager, GymCreateBottomDialogFragment::class.java.simpleName)
//            }
        }
    }

    private fun openBottomDialogFragment(arrayList: ArrayList<String>, onSuccess:(string: String)->Unit){
        GymCreateBottomDialogFragment(arrayList) { onSuccess(it) }.run {
            setStyle(DialogFragment.STYLE_NORMAL, R.style.AppBottomSheetDialogTheme)
            show(supportFragmentManager, GymCreateBottomDialogFragment::class.java.simpleName)
        }
    }

    private fun getProfileImageFromDevice() {
        val intent = Intent(Intent.ACTION_GET_CONTENT)
        intent.type = "image/*"
        launcher.launch(intent);
    }



    private val launcher: ActivityResultLauncher<Intent> =
        registerForActivityResult(ActivityResultContracts.StartActivityForResult()) { result ->
            if (result.resultCode == RESULT_OK) {
                val intent = result.data!!

                Glide.with(this)
                    .asBitmap()
                    .override(600, 600)
                    .load(intent.data)
                    .error(R.drawable.ic_default_profile)
                    .into(targetLargeImage)

                Glide.with(this)
                    .asBitmap()
                    .override(200, 200)
                    .load(intent.data)
                    .into(targetSmallImage)
            }
        }

    private val targetLargeImage = object : CustomTarget<Bitmap>() {
        override fun onResourceReady(
            resource: Bitmap,
            transition: Transition<in Bitmap>?,
        ) { // STORAGE UPLOAD
            viewModel.saveImage(resource, changedImageView, ImageSize.Large, imageViewType)
        }

        override fun onLoadCleared(placeholder: Drawable?) {}
    }

    private val targetSmallImage = object : CustomTarget<Bitmap>() {
        override fun onResourceReady(
            resource: Bitmap,
            transition: Transition<in Bitmap>?,
        ) { // STORAGE UPLOAD
            viewModel.saveImage(resource, changedImageView, ImageSize.Small, imageViewType)
        }

        override fun onLoadCleared(placeholder: Drawable?) {}
    }




    enum class SelectMenu {
        JUMP , RUN , SCHOOL, GYM, PARK, BRANDED
    }
}