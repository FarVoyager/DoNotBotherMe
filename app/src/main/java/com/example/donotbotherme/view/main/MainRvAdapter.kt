package com.example.donotbotherme.view.main

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.example.donotbotherme.databinding.RvMainItemBinding
import com.example.donotbotherme.model.DisturbCondition
import java.lang.StringBuilder

class MainRvAdapter(
    private val onListItemClickListener: OnListItemClickListener,
    private var data: List<DisturbCondition>
) : RecyclerView.Adapter<MainRvAdapter.RecyclerItemViewHolder>() {

    fun setData(data: List<DisturbCondition>) {
        this.data = data
        notifyDataSetChanged()
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): RecyclerItemViewHolder {
        val binding =
            RvMainItemBinding.inflate(LayoutInflater.from(parent.context), parent, false)
        return RecyclerItemViewHolder(binding)
    }

    override fun onBindViewHolder(holder: RecyclerItemViewHolder, position: Int) {
        holder.bind(data[position])
    }

    override fun getItemCount(): Int {
        return data.size
    }

    inner class RecyclerItemViewHolder(private val binding: RvMainItemBinding): RecyclerView.ViewHolder(binding.root) {
        fun bind(data: DisturbCondition) {
            if (layoutPosition != RecyclerView.NO_POSITION) {
                binding.mainRvItemName.text = data.contactName
                binding.mainRvItemStartTime.text = data.timeStart
                binding.mainRvItemEndTime.text = data.timeEnd
                val days = buildDaysString(data)
                binding.mainRvItemDays.text = days
                itemView.setOnClickListener { openInNewWindow(data) }
            }
        }
    }

    private fun openInNewWindow(listItemData: DisturbCondition) {
        onListItemClickListener.onItemClick(listItemData)
    }

    interface OnListItemClickListener {
        fun onItemClick(data: DisturbCondition)
    }

    private fun buildDaysString(data: DisturbCondition): String {
        val stringBuilder = StringBuilder()
        if (data.isMondayBlocked) stringBuilder.append("Пн, ") else stringBuilder.append("")
        if (data.isTuesdayBlocked) stringBuilder.append("Вт, ") else stringBuilder.append("")
        if (data.isWednesdayBlocked) stringBuilder.append("Ср, ") else stringBuilder.append("")
        if (data.isThursdayBlocked) stringBuilder.append("Чт, ") else stringBuilder.append("")
        if (data.isFridayBlocked) stringBuilder.append("Пт, ") else stringBuilder.append("")
        if (data.isSaturdayBlocked) stringBuilder.append("Сб, ") else stringBuilder.append("")
        if (data.isSundayBlocked) stringBuilder.append("Вс, ") else stringBuilder.append("")
        stringBuilder.delete(stringBuilder.lastIndex - 1, stringBuilder.lastIndex)

        return stringBuilder.toString()
    }

}