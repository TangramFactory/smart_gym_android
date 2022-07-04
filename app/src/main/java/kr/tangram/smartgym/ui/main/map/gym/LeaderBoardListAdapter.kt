/*
 * Copyright (c) 2021 TANGRAM FACTORY, INC. All rights reserved.
 * @auth kang@tangram.kr
 * @date 4/15/21 3:33 PM.
 */

package kr.tangram.smartgym.ui.main.map.gym

import android.graphics.Color
import android.util.TypedValue
import android.view.LayoutInflater
import android.view.ViewGroup
import android.widget.TextView
import androidx.core.content.res.ResourcesCompat.getFont
import androidx.databinding.BindingAdapter
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.mikhaellopez.circularimageview.CircularImageView
import kr.tangram.smartgym.R
import kr.tangram.smartgym.data.remote.model.UserInfo
import kr.tangram.smartgym.databinding.ItemEmptyLayoutBinding
import kr.tangram.smartgym.databinding.ItemLeaderBoardUserInfoBinding


class LeaderBoardListAdapter(
) : RecyclerView.Adapter<RecyclerView.ViewHolder>() {

    interface ItemEvent {
        fun itemClick(data: UserInfo)
    }

    private val userList: MutableList<UserInfo> = mutableListOf()
    fun setList(userList: List<UserInfo>?) {
        this.userList.clear()
        if (userList != null) this.userList.addAll(userList)
        notifyDataSetChanged()
    }


    override fun getItemCount(): Int = if (userList.size == 0)  1  else  userList.size


    override fun getItemViewType(position: Int): Int = if (userList.size == 0) VIEW_TYPE_EMPTY else VIEW_TYPE_DATE



    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): RecyclerView.ViewHolder {
        return when(viewType){
            VIEW_TYPE_DATE -> LeaderBoardListViewHolder(ItemLeaderBoardUserInfoBinding.inflate(LayoutInflater.from(parent.context), parent, false))
            VIEW_TYPE_EMPTY -> EmptyViewHolder(ItemEmptyLayoutBinding.inflate(LayoutInflater.from(parent.context), parent, false))
            else -> EmptyViewHolder(ItemEmptyLayoutBinding.inflate(LayoutInflater.from(parent.context), parent, false))
        }
    }

    override fun onBindViewHolder(holder: RecyclerView.ViewHolder, position: Int) {
        val viewType = getItemViewType(position)
        if (viewType == VIEW_TYPE_DATE) {
            val data = userList[position]
            val infoDataViewHolder = holder as? LeaderBoardListViewHolder
            if (data != null && infoDataViewHolder != null)
                infoViewHolderSetting(position, data, infoDataViewHolder)

        } else if (viewType == VIEW_TYPE_EMPTY) {
            val emptyViewHolder = holder as EmptyViewHolder

        }
    }


    private fun infoViewHolderSetting(
        position: Int,
        data: UserInfo,
        viewHolder: LeaderBoardListViewHolder,
    ) {
        val rank = (position + 1)
        viewHolder.run {
            val own = position==4 // 나일때
//            viewHolder.itemView.setOnClickListener { event.itemClick(data) }
            viewHolder.binding.model = data

        }
    }








    class LeaderBoardListViewHolder(val binding: ItemLeaderBoardUserInfoBinding) : RecyclerView.ViewHolder(binding.root) {}
    class EmptyViewHolder(val binding: ItemEmptyLayoutBinding) : RecyclerView.ViewHolder(binding.root) {}

    companion object {

        private const val VIEW_TYPE_DATE = 0
        private const val VIEW_TYPE_EMPTY = 1


        private val boldBorder = 10.0f
        private val thinBorder = 4.0f


        @BindingAdapter("bind_profile_image", "bind_rank")
        @JvmStatic
        fun rankUserProfileImageSetting(
            imageView: CircularImageView,
            data: UserInfo,
            rank: Int,
        ) {

            when (rank) {
                1 -> {
                    // 1등
                    imageView.borderColor = Color.parseColor("#FFC22B")
                    imageView.borderWidth = boldBorder
                }
                2 -> {
                    // 2등
                    imageView.borderColor = Color.parseColor("#CCCCCC")
                    imageView.borderWidth = boldBorder
                }
                3 -> {
                    // 3등
                    imageView.borderColor = Color.parseColor("#CE8505")
                    imageView.borderWidth = boldBorder
                }
                0 -> {
                    // 순위가 없을 때
                    imageView.borderColor = Color.parseColor("#333333")
                    imageView.borderWidth = thinBorder

                }
                else -> {
                    // 순위가 있을 때
                    imageView.borderColor = Color.parseColor("#999999")
                    imageView.borderWidth = thinBorder
                }
            }

            val profileImage = data.userUid
            val email = data.userEmail?:"null"
//            val imageURL = data.imagePath

            Glide.with(imageView).load(profileImage).error(R.drawable.ic_default_profile).into(imageView)
        }



        @BindingAdapter("bind_text_name", "bind_own_flag")
        @JvmStatic
        fun displayNameSetting(textView: TextView, name: String, own: Boolean ) {
            textView.text = name
            val font = if (own) R.font.roboto_medium else R.font.roboto_light
            textView.typeface = getFont(textView.context, font)
        }


        @BindingAdapter("bind_text_rank")
        @JvmStatic
        fun rankTextSetting(textView: TextView, rank: Int) {
            when (rank) {
                1, 2, 3 -> {
                    textView.text = null
                    val backgroundRes = when (rank) {
                        1 -> R.drawable.ic_leader_board_rank_1st
                        2 -> R.drawable.ic_leader_board_rank_2nd
                        3 -> R.drawable.ic_leader_board_rank_3rd
                        else -> 0
                    }
                    textView.setBackgroundResource(backgroundRes)
                }
                else -> {
                    val font = if (rank >= 4) R.font.roboto_medium else R.font.roboto_light
                    textView.typeface = getFont(textView.context, font)
                    textView.setBackgroundResource(0)
                    textView.text = (rank).toString()
                }
            }
        }

        @BindingAdapter("bind_rank","bind_own", "bind_text_jump_count")
        @JvmStatic
        fun jumpCountSetting(textView: TextView, rank: Int, own: Boolean, jumpCount: Int ) {
            textView.text =jumpCount.toString()

            val textSize = if (rank <= 3) R.dimen.list_top_rank_jump_count_text_size
            else R.dimen.list_normal_user_jump_count_text_size

            textView.setTextSize(TypedValue.COMPLEX_UNIT_PX, textView.context.resources.getDimension(textSize))

            val fontRes = when {
                rank <= 3 -> R.font.roboto_medium
                rank <= 10 -> R.font.roboto_bold
                own -> R.font.roboto_medium
                else -> R.font.roboto_light
            }
            textView.typeface = getFont(textView.context,fontRes)

        }


        @BindingAdapter("bind_user_data_list")
        @JvmStatic
        fun bindUserDataList(recyclerView:RecyclerView,  userList: List<UserInfo>?){
            if(recyclerView.adapter == null){
                val lm = LinearLayoutManager(recyclerView.context)
                val adapter = LeaderBoardListAdapter()
                recyclerView.layoutManager = lm
                recyclerView.adapter = adapter
            }
            (recyclerView.adapter as LeaderBoardListAdapter).setList(userList)
            recyclerView.adapter?.notifyDataSetChanged()
        }


    }



}
