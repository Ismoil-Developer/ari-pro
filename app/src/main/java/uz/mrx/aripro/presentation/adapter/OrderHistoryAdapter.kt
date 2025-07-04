package uz.mrx.aripro.presentation.adapter

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.ListAdapter
import androidx.recyclerview.widget.RecyclerView
import uz.mrx.aripro.R
import uz.mrx.aripro.data.model.LoadData
import uz.mrx.aripro.data.remote.response.history.OrderHistoryResponse
import uz.mrx.aripro.databinding.ItemOrderBinding

class OrderHistoryAdapter(private var onItemClickListener: (OrderHistoryResponse) -> Unit) :
    ListAdapter<OrderHistoryResponse, OrderHistoryAdapter.ViewHolder>(OrderHistoryResponseDiffUtilCallback) {


    inner class ViewHolder(private val binding: ItemOrderBinding) :
        RecyclerView.ViewHolder(binding.root) {
        fun onBind() {
            val newsData = getItem(absoluteAdapterPosition)

            binding.textView.text = newsData.order_code

            if (newsData.status == "completed"){
                binding.icOrder.setImageResource(R.drawable.ic_delivery_completed)
                binding.textView2.text = "Buyurtma yakunlandi"
            }else{
                binding.icOrder.setImageResource(R.drawable.ic_load)
                binding.textView2.text = "Yetkazib berishda"

            }

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


    object OrderHistoryResponseDiffUtilCallback : DiffUtil.ItemCallback<OrderHistoryResponse>() {
        override fun areItemsTheSame(oldItem: OrderHistoryResponse, newItem: OrderHistoryResponse): Boolean {
            return oldItem.id == newItem.id
        }

        override fun areContentsTheSame(oldItem: OrderHistoryResponse, newItem: OrderHistoryResponse): Boolean {
            return oldItem == newItem
        }
    }
}
