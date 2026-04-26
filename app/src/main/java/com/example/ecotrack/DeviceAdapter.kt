package com.example.ecotrack

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.example.myapplication.R

class DeviceAdapter(private val list: List<Device>) :
    RecyclerView.Adapter<DeviceAdapter.ViewHolder>() {

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

        holder.name.text = device.name
        holder.subtitle.text = device.subtitle
        holder.power.text = device.power

        if (device.isOn) {
            holder.status.setImageResource(android.R.drawable.presence_online)
        } else {
            holder.status.setImageResource(android.R.drawable.presence_offline)
        }
    }

    override fun getItemCount(): Int {
        return list.size
    }
}