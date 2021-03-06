package name.lmj0011.courierlocker.adapters

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.core.content.ContextCompat
import androidx.core.graphics.drawable.DrawableCompat
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.ListAdapter
import androidx.recyclerview.widget.RecyclerView
import name.lmj0011.courierlocker.R
import name.lmj0011.courierlocker.database.Customer
import name.lmj0011.courierlocker.databinding.ListItemCustomerBinding

class CustomerListAdapter(private val clickListener: CustomerListener): ListAdapter<Customer, CustomerListAdapter.ViewHolder>(CustomerDiffCallback()) {
    override fun getItemId(position: Int): Long {
        // return the Item's database row id
        return super.getItem(position).id
    }

    class ViewHolder private constructor(val binding: ListItemCustomerBinding) : RecyclerView.ViewHolder(binding.root){

        fun bind(clickListener: CustomerListener, c: Customer) {
            binding.customer = c
            binding.clickListener = clickListener
            binding.customerNameTextView.text = c.name
            binding.customerAddressTextView.text = c.address
            binding.noteTextView.text = c.note

            when(c.impression) {
                0 -> {
                    binding.impressionImageView.setImageResource(R.drawable.ic_happy_face)
                }
                else -> {
                    binding.impressionImageView.setImageResource(R.drawable.ic_sad_face)
                }
            }

            binding.executePendingBindings()
        }

        companion object {
            fun from(parent: ViewGroup): ViewHolder {
                val layoutInflater = LayoutInflater.from(parent.context)
                val binding = ListItemCustomerBinding.inflate(layoutInflater, parent, false)

                return ViewHolder(binding)
            }
        }
    }

    class CustomerDiffCallback : DiffUtil.ItemCallback<Customer>() {
        override fun areItemsTheSame(oldItem: Customer, newItem: Customer): Boolean {
            return oldItem.id == newItem.id
        }

        override fun areContentsTheSame(oldItem: Customer, newItem: Customer): Boolean {
            return oldItem == newItem
        }
    }

    class CustomerListener(val clickListener: (gateCodeId: Long) -> Unit) {
        fun onClick(c: Customer) = clickListener(c.id)
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        val c = getItem(position)
        holder.bind(clickListener, c)
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        return ViewHolder.from(parent)
    }

    fun filterBySearchQuery(query: String?, list: MutableList<Customer>): MutableList<Customer> {
        if (query.isNullOrBlank()) return list

        return list.filter {
            val inName = it.name.contains(query, true)
            val inAddress = it.address.contains(query, true)
            val inNote = it.note.contains(query, true)

            return@filter inName || inAddress || inNote
        }.toMutableList()
    }

}