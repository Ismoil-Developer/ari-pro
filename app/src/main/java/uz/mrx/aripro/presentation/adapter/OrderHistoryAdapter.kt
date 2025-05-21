package uz.mrx.aripro.presentation.adapter

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.ListAdapter
import androidx.recyclerview.widget.RecyclerView
import uz.mrx.aripro.R
import uz.mrx.aripro.data.model.LoadData
import uz.mrx.aripro.databinding.ItemOrderBinding

class OrderHistoryAdapter(private var onItemClickListener: (LoadData) -> Unit) :
    ListAdapter<LoadData, OrderHistoryAdapter.ViewHolder>(LoadDataDiffUtilCallback) {


    inner class ViewHolder(private val binding: ItemOrderBinding) :
        RecyclerView.ViewHolder(binding.root) {
        fun onBind() {
            val newsData = getItem(absoluteAdapterPosition)

            binding.textView.text = newsData.id
            binding.textView2.text = newsData.status

            binding.icOrder.setImageResource(R.drawable.ic_load)

            itemView.setOnClickListener {
                onItemClickListener.invoke(newsData)
            }

        }
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        return ViewHolder(
            ItemOrderBinding.inflate(
                LayoutInflater.from(parent.context),
                parent,
                false
            )
        )
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) = holder.onBind()


    object LoadDataDiffUtilCallback : DiffUtil.ItemCallback<LoadData>() {
        override fun areItemsTheSame(oldItem: LoadData, newItem: LoadData): Boolean {
            return oldItem.id == newItem.id
        }

        override fun areContentsTheSame(oldItem: LoadData, newItem: LoadData): Boolean {
            return oldItem == newItem
        }
    }
}
