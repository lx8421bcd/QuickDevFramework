package com.linxiao.framework.dialog

import android.graphics.Color
import android.text.TextUtils
import android.util.TypedValue
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.annotation.ColorRes
import androidx.recyclerview.widget.RecyclerView
import com.linxiao.framework.common.DensityHelper
import com.linxiao.framework.databinding.ItemTextOptionsBinding

class TextOptionsAdapter : RecyclerView.Adapter<TextOptionsAdapter.TextOptionHolder>() {

    class TextOptionHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        val viewBinding by lazy {
            ItemTextOptionsBinding.bind(itemView)
        }
    }

    private var isAttached = false
    private val optionTextList = ArrayList<String>()
    private val optionTextColorMap = HashMap<String, Int>()
    private val optionOnClickMap = HashMap<String, View.OnClickListener>()
    private var itemClick: (itemView: View, position: Int) -> Unit = {_, _ -> }

    var itemTextSize = DensityHelper.dp2px(14f)
    var itemTextGravity = -1
    var defaultTextColor = Color.BLACK


    init {
        setOnItemClickListener { itemView, position ->
            val text = optionTextList[position]
            optionOnClickMap[text]?.onClick(itemView)
        }
    }

    fun setOnItemClickListener(listener: (itemView: View, position: Int) -> Unit) {
        itemClick = { itemView, position ->
            listener.invoke(itemView, position)
            val text = optionTextList[position]
            optionOnClickMap[text]?.onClick(itemView)
        }
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): TextOptionHolder {
        val itemView = ItemTextOptionsBinding.inflate(LayoutInflater.from(parent.context)).root
        return TextOptionHolder(itemView)
    }

    override fun getItemCount(): Int {
        return optionTextList.size
    }

    override fun onBindViewHolder(holder: TextOptionHolder, position: Int) {
        val data = optionTextList[position]
        if (itemTextGravity >= 0) {
            holder.viewBinding.root.gravity = itemTextGravity
        }
        holder.viewBinding.root.setTextSize(TypedValue.COMPLEX_UNIT_PX, itemTextSize.toFloat())
        holder.viewBinding.root.setTextColor(optionTextColorMap[data]?: defaultTextColor)
        holder.viewBinding.root.text = data
    }

    override fun onAttachedToRecyclerView(recyclerView: RecyclerView) {
        super.onAttachedToRecyclerView(recyclerView)
        isAttached = true
    }

    fun addOptionItem(
        title: String,
        click: View.OnClickListener,
        @ColorRes textColor: Int = defaultTextColor
    ) {
        if (TextUtils.isEmpty(title)) {
            return
        }
        optionTextList.remove(title)
        optionTextList.add(title)
        optionTextColorMap[title] = textColor
        optionOnClickMap[title] = click
        if (isAttached) {
            notifyItemRangeChanged(0, optionTextList.size + 1)
        }
    }
}