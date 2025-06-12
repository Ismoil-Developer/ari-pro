package uz.mrx.aripro.presentation.adapter

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.ListAdapter
import androidx.recyclerview.widget.RecyclerView
import uz.mrx.aripro.data.model.OrderCancelData
import uz.mrx.aripro.databinding.ItemCancelOrderBinding

class CancelAdapter(private var onItemClickListener: (OrderCancelData) -> Unit) :
    ListAdapter<OrderCancelData, CancelAdapter.ViewHolder>(OrderCancelDataDiffUtilCallback) {

    private var selectedPosition: Int = RecyclerView.NO_POSITION

    inner class ViewHolder(private val binding: ItemCancelOrderBinding) :
        RecyclerView.ViewHolder(binding.root) {

        fun onBind() {
            val item = getItem(absoluteAdapterPosition)

            binding.locationName.text = item.reason

            // Toggle selected icon
            binding.icSelected.visibility = if (item.isSelected) View.VISIBLE else View.GONE
            binding.icUnselected.visibility = if (item.isSelected) View.GONE else View.VISIBLE

            binding.root.setOnClickListener {
                // Deselect previous
                if (selectedPosition != RecyclerView.NO_POSITION) {
                    getItem(selectedPosition).isSelected = false
                    notifyItemChanged(selectedPosition)
                }

                // Select current
                item.isSelected = true
                notifyItemChanged(absoluteAdapterPosition)
                selectedPosition = absoluteAdapterPosition

                onItemClickListener.invoke(item)
            }
        }
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        return ViewHolder(
            ItemCancelOrderBinding.inflate(
                LayoutInflater.from(parent.context),
                parent,
                false
            )
        )
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) = holder.onBind()

    object OrderCancelDataDiffUtilCallback : DiffUtil.ItemCallback<OrderCancelData>() {
        override fun areItemsTheSame(oldItem: OrderCancelData, newItem: OrderCancelData): Boolean {
            return oldItem.id == newItem.id
        }

        override fun areContentsTheSame(oldItem: OrderCancelData, newItem: OrderCancelData): Boolean {
            return oldItem.id == newItem.id && oldItem.reason == newItem.reason && oldItem.isSelected == newItem.isSelected
        }

    }
}
