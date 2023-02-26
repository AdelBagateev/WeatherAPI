package com.example.wetherapp.adapter

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView

import com.example.wetherapp.data.response.FindResponse
import com.example.wetherapp.databinding.ItemCityBinding

class CityAdapter (
    private val list: List<FindResponse>,
    private val action: (FindResponse) -> Unit,
) : RecyclerView.Adapter<CityItem>() {

    override fun onCreateViewHolder(
        parent: ViewGroup,
        viewType: Int
    ): CityItem = CityItem(
        binding = ItemCityBinding.inflate(
            LayoutInflater.from(parent.context),
            parent,
            false
        ),
        action = action
    )

    override fun onBindViewHolder(
        holder: CityItem,
        position: Int
    ) {
        holder.onBind(list[position])
    }

    override fun getItemCount(): Int = list.size
}
