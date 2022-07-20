package com.mac.ecomdeliveryphp.Fragments

import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.recyclerview.widget.LinearLayoutManager
import com.android.volley.AuthFailureError
import com.android.volley.Request
import com.android.volley.RequestQueue
import com.android.volley.toolbox.StringRequest
import com.android.volley.toolbox.Volley
import com.mac.ecomadminphp.Utils.Constants
import com.mac.ecomadminphp.Utils.SharedPref
import com.mac.ecomdeliveryphp.Adapters.CommonOrderAdapter
import com.mac.ecomdeliveryphp.Models.CommonOrderModel
import com.mac.ecomdeliveryphp.databinding.FragmentActiveBinding
import com.mac.ecomdeliveryphp.databinding.FragmentDeliveredBinding
import org.json.JSONArray
import org.json.JSONException
import org.json.JSONObject
import java.util.HashMap

class DeliveredFragment : Fragment() {

    private lateinit var _binding:FragmentDeliveredBinding
    private val binding get() = _binding
    private lateinit var deluid:String
    private  var list= mutableListOf<CommonOrderModel>()


    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentDeliveredBinding.inflate(layoutInflater)

        checkUser()
        getActiveOrders()


        return binding.root
    }



    private fun checkUser() {
        val checkUser =
            context?.let { SharedPref.readFromSharedPref(it, Constants.userPrefName, Constants.defaultPrefValue) };
        if (!checkUser.equals("1")) {
            val splitDetails = checkUser?.split(",")
            deluid = "del"+splitDetails?.get(3) ?: "nothing"


        }
    }




    private fun getActiveOrders(
    ) {
        val getActiveOrders = Constants.baseUrl + "/getAllActiveOrders.php"
        val request: StringRequest =object: StringRequest(
            Request.Method.POST, getActiveOrders,
            { response ->
                try {

                    val jsonObject  = JSONObject(response)
                    val success:String = jsonObject.getString("success")
                    val jsonArray: JSONArray = jsonObject.getJSONArray("data")
                    if(success.equals("1")){

                        for (item in 0 until jsonArray.length()){
                            val jsonObject: JSONObject = jsonArray.getJSONObject(item)
                            val id:String =jsonObject.getString("id")
                            val orderId:String =jsonObject.getString("orderId")
                            val productId:String =jsonObject.getString("productId")
                            val orderManagerId:String =jsonObject.getString("orderManagerId")
                            val status:String= jsonObject.getInt("status").toString()
                            val total:String= jsonObject.getString("total")
                            val uid:String= jsonObject.getString("uid")

                            val data = CommonOrderModel(id,orderId,productId,orderManagerId,status,total,uid)

                            if(data.status.equals("1")){
                                list.add(0,data)
                            }



                        }

                        binding.deliveredRecycler.setHasFixedSize(true)
                        binding.deliveredRecycler.layoutManager=LinearLayoutManager(context)
                        val adapter = context?.let { CommonOrderAdapter(it,list) }
                        if (adapter != null) {
                            adapter.notifyDataSetChanged()
                        }

                        binding.deliveredRecycler.adapter=adapter

                    }

                }catch (e: JSONException){
                    e.printStackTrace();
                }


            },
            { error ->

            }){
            override fun getParams(): Map<String, String> {
                val params: MutableMap<String, String> = HashMap()
                params["deluid"] = deluid
                return params
            }

            @Throws(AuthFailureError::class)
            override fun getHeaders(): Map<String, String> {
                val params: MutableMap<String, String> = HashMap()
                params["Content-Type"] = "application/x-www-form-urlencoded"
                return params
            }
        }


        val queue: RequestQueue = Volley.newRequestQueue(context)
        queue.add(request)


    }




}