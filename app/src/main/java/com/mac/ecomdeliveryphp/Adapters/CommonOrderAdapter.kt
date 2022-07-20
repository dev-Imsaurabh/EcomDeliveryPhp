package com.mac.ecomdeliveryphp.Adapters

import android.content.Context
import android.content.Intent
import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.mac.ecomdeliveryphp.Activities.CommonViewActivity
import com.mac.ecomdeliveryphp.MainActivity
import com.mac.ecomdeliveryphp.Models.CommonOrderModel
import com.mac.ecomdeliveryphp.databinding.ActiveItemLayoutBinding

class CommonOrderAdapter(val context:Context,val list:MutableList<CommonOrderModel>):
    RecyclerView.Adapter<CommonOrderAdapter.myViewHolder>() {


    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): myViewHolder {

        val binding = ActiveItemLayoutBinding.inflate(LayoutInflater.from(context))
        return myViewHolder(binding)


    }

    override fun onBindViewHolder(holder: myViewHolder, position: Int) {
        val listPos = list[position]
        holder.setData(listPos)

    }

    override fun getItemCount(): Int {
        return list.size
    }


    inner class myViewHolder(val binding: ActiveItemLayoutBinding):RecyclerView.ViewHolder(binding.root){
        fun setData(listPos: CommonOrderModel) {

            val split = listPos.orderId.split(",")

            if(split.size>1){
                binding.orderId.setText("Order ID: "+split[0])
            }else{
                binding.orderId.setText("Order ID: "+listPos.orderId)
            }
            binding.goBtn.setOnClickListener {
                val intent =Intent(context,CommonViewActivity::class.java)
                intent.putExtra("orderId",listPos.orderId)
                intent.putExtra("productId",listPos.productId)
                intent.putExtra("orderManagerId",listPos.orderManagerId)
                intent.putExtra("uid",listPos.uid)
                intent.putExtra("total",listPos.total)
                context.startActivity(intent)

            }



        }

    }
}