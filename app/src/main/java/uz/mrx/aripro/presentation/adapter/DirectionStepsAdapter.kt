package uz.mrx.aripro.presentation.adapter

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import uz.mrx.aripro.R
import uz.mrx.aripro.data.model.DirectionStep
import uz.mrx.aripro.utils.SwipeToRevealView


class DirectionStepsAdapter(
    private val steps: List<DirectionStep>,
    private val onSwiped: (DirectionStep) -> Unit
) : RecyclerView.Adapter<DirectionStepsAdapter.StepViewHolder>() {

    inner class StepViewHolder(val view: View) : RecyclerView.ViewHolder(view) {
        val swipeView: SwipeToRevealView = view.findViewById(R.id.swipeView)
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): StepViewHolder {
        val view = LayoutInflater.from(parent.context)
            .inflate(R.layout.item_direction_step, parent, false)
        return StepViewHolder(view)
    }

    override fun onBindViewHolder(holder: StepViewHolder, position: Int) {
        val step = steps[position]
        holder.swipeView.setText(step.label)

        holder.swipeView.visibility = if (step.isCompleted) View.GONE else View.VISIBLE

        holder.swipeView.setOnSwipeListener {
            step.isCompleted = true
            notifyItemChanged(position)
            onSwiped(step)
        }
    }

    override fun getItemCount() = steps.size
}
