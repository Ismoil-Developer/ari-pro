package uz.mrx.aripro.presentation.adapter

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.ListAdapter
import androidx.recyclerview.widget.RecyclerView
import uz.mrx.aripro.data.remote.response.order.AssignedResponse
import uz.mrx.aripro.databinding.ItemOrderBinding

class AssignedAdapter(private var onItemClickListener: (AssignedResponse) -> Unit) :
    ListAdapter<AssignedResponse, AssignedAdapter.ViewHolder>(AssignedResponseDiffUtilCallback) {

    inner class ViewHolder(private val binding: ItemOrderBinding) :
        RecyclerView.ViewHolder(binding.root) {

        fun onBind() {

            val shop = getItem(absoluteAdapterPosition)

            binding.textView.text = shop.order_code
            binding.textView2.text = shop.shop_title

            itemView.setOnClickListener {
                onItemClickListener.invoke(shop)
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

    object AssignedResponseDiffUtilCallback : DiffUtil.ItemCallback<AssignedResponse>() {
        override fun areItemsTheSame(oldItem: AssignedResponse, newItem: AssignedResponse): Boolean {
            return oldItem.id == newItem.id
        }

        override fun areContentsTheSame(oldItem: AssignedResponse, newItem: AssignedResponse): Boolean {
            return oldItem == newItem
        }
    }

}