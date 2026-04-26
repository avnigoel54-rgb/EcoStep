package com.example.ecotrack

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
import androidx.core.content.ContextCompat
import androidx.recyclerview.widget.RecyclerView

class DeviceAdapter(
    private val list: MutableList<Device>,
    private val onLongPressDelete: (Int) -> Unit
) : RecyclerView.Adapter<DeviceAdapter.ViewHolder>() {

    class ViewHolder(view: View) : RecyclerView.ViewHolder(view) {
        val name: TextView = view.findViewById(R.id.txtName)
        val subtitle: TextView = view.findViewById(R.id.txtSubtitle)
        val power: TextView = view.findViewById(R.id.txtPower)
        val status: ImageView = view.findViewById(R.id.iconStatus)
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        val view = LayoutInflater.from(parent.context)
            .inflate(R.layout.item_device, parent, false)
        return ViewHolder(view)
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        val device = list[position]
        val context = holder.itemView.context

        holder.name.text = device.name
        holder.subtitle.text = device.subtitle
        holder.power.text = device.power

        if (device.isOn) {
            holder.status.setImageResource(android.R.drawable.presence_online)
            holder.status.setColorFilter(ContextCompat.getColor(context, R.color.status_on))
        } else {
            holder.status.setImageResource(android.R.drawable.presence_offline)
            holder.status.setColorFilter(ContextCompat.getColor(context, R.color.status_off))
        }

        holder.itemView.setOnLongClickListener {
            onLongPressDelete(holder.adapterPosition)
            true
        }
    }

    override fun getItemCount(): Int = list.size

    fun setDevices(newDevices: List<Device>) {
        list.clear()
        list.addAll(newDevices)
        notifyDataSetChanged()
    }

    fun addDevice(device: Device) {
        list.add(0, device)
        notifyItemInserted(0)
    }

    fun removeDevice(position: Int) {
        if (position in list.indices) {
            list.removeAt(position)
            notifyItemRemoved(position)
        }
    }
}
