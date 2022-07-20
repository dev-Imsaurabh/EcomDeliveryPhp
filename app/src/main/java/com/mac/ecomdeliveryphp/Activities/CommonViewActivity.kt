package com.mac.ecomdeliveryphp.Activities

import android.app.Dialog
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.view.View.GONE
import android.view.View.VISIBLE
import android.widget.Toast
import com.android.volley.AuthFailureError
import com.android.volley.RequestQueue
import com.android.volley.Response
import com.android.volley.toolbox.StringRequest
import com.android.volley.toolbox.Volley
import com.mac.ecomadminphp.UserArea.Activities.Model.OrderModel
import com.mac.ecomadminphp.Utils.Constants
import com.mac.ecomadminphp.Utils.ProgressDialog
import com.mac.ecomadminphp.Utils.SharedPref
import com.mac.ecomdeliveryphp.databinding.ActivityCommonViewBinding
import com.squareup.picasso.Picasso
import org.json.JSONArray
import org.json.JSONException
import org.json.JSONObject
import java.text.SimpleDateFormat
import java.util.*

class CommonViewActivity : AppCompatActivity() {
    private lateinit var inorderId: String
    private lateinit var inproductId: String
    private lateinit var inorderManagerId: String
    private lateinit var intotal: String
    private lateinit var instatus: String
    private lateinit var inuid: String
    private  var uid: String=""
    private  var  OTP:String=""
    var manageOrderValue ="0"
    private lateinit var paymentM:String
    private lateinit var deliveredBy:String
    private val fetchUrl = Constants.baseUrl1 + "/Orders/getOrdersForUser.php"
    private val statusUrl = Constants.baseUrl + "/updateStatus.php"
    private lateinit var binding: ActivityCommonViewBinding
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityCommonViewBinding.inflate(layoutInflater)
        setContentView(binding.root)

        checkUser()

        getIntents()
        getAllOrdersFromUserCart()
        ClickOnReceiveOrderBtn()
        ClickODeliverBtn();


    }

    private fun ClickODeliverBtn() {
        binding.deliverBtn.setOnClickListener {
            if(binding.etOtp.text.toString().trim().isEmpty()||binding.etOtp.text.toString().trim().length<6){
                Toast.makeText(this, "Please check and enter correct otp", Toast.LENGTH_SHORT).show()
            }else {
                if (binding.etOtp.text.toString().trim().equals(OTP)) {
                    Toast.makeText(this, "verified", Toast.LENGTH_SHORT).show()
                    val dialog =ProgressDialog.progressDialog(this,"Delivering...")
                    dialog.show()
                    val status = "delivered"
                    GetUserOrder(status,dialog)
                }else{
                    Toast.makeText(this, "Incorrect OTP", Toast.LENGTH_SHORT).show()
                }
            }
        }

    }


    private fun getIntents() {
        inorderId = intent.getStringExtra("orderId").toString()
        inproductId = intent.getStringExtra("productId").toString()
        inorderManagerId = intent.getStringExtra("orderManagerId").toString()
        inuid = intent.getStringExtra("uid").toString()
        intotal=intent.getStringExtra("total").toString()

    }

    private fun getAllOrdersFromUserCart() {

        val request: StringRequest = object : StringRequest(
            Method.POST, fetchUrl,
            Response.Listener { response ->

                try {

                    val jsonObject = JSONObject(response)
                    val success: String = jsonObject.getString("success")
                    val jsonArray: JSONArray = jsonObject.getJSONArray("data")
                    if (success.equals("1")) {


                        for (item in 0 until jsonArray.length()) {

                            val jsonObject: JSONObject = jsonArray.getJSONObject(item)

                            val id: String = jsonObject.getString("id")
                            val orderId: String = jsonObject.getString("orderId")
                            val productId: String = jsonObject.getString("productId")
                            val productQuantity: String = jsonObject.getString("productQuantity")
                            val productAddress: String = jsonObject.getString("productAddress")
                            val productTotalPay: String = jsonObject.getString("productTotalPay")
                            val productPaymentMode: String = jsonObject.getString("productPaymentMode")
                            val productPaymentStatus: String = jsonObject.getString("productPaymentStatus")
                            val productTrackingStatus: String = jsonObject.getString("productTrackingStatus")
                            val productUid: String = jsonObject.getString("productUid")
                            val productDeliveryDate: String = jsonObject.getString("productDeliveryDate")
                            val productName: String = jsonObject.getString("productName")
                            val productImage: String = jsonObject.getString("productImage")
                            val productOrderDate: String = jsonObject.getString("productOrderDate")
                            val productDescription: String = jsonObject.getString("productDescription")
                            val productRefundStatus: String = jsonObject.getString("productRefundStatus")

                            if(orderId.equals(inorderId)&&productId.equals(inproductId)){
                                binding.productName.setText(productName)
                                Picasso.get().load(Constants.baseUrl1+"/Products/ProductImages/"+productImage).into(binding.orderImage)
                                binding.producAddress.setText(productAddress)
                                binding.productDescription.setText(productDescription)
                                binding.productDeliveryDate.setText("Expected delivery date: "+getExpectedDate(productDeliveryDate))
                                var orderdate = productOrderDate.toLong()*1000
                                binding.orderDate.setText("Order date: "+getOrderDate(orderdate.toString()))
                                if(productPaymentMode.equals("cod")){
                                    binding.amountTxt.setText("Cash ₹"+intotal)

                                }else{
                                    binding.amountTxt.visibility= GONE
                                    binding.info.setText("Already paid via online -------->")

                                }
                                binding.amountTxt.setText("Cash ₹"+intotal)
                                if(productTrackingStatus.equals("assigned")){
                                    binding.cv.visibility= GONE
                                    binding.receiveOrder.visibility= VISIBLE

                                }else if(productTrackingStatus.equals("shipped")){
                                    binding.receiveOrder.visibility= GONE
                                    binding.cv.visibility= VISIBLE
                                    val split =productDescription.split(" ")
                                    OTP=split[0].trim()
                                    deliveredBy = productRefundStatus
                                    paymentM=productPaymentMode
                                }else if(productTrackingStatus.equals("canceled")){
                                    binding.cv.visibility= GONE
                                    binding.receiveOrder.visibility= GONE
                                }

                                binding.orderId.setText("Order ID: "+orderId.split(",")[0])

                                if(productDescription.contains("OTP")){
                                    binding.productDescription.setText("Ask for OTP from user to deliver product")
                                }




                            }



                        }




                    }

                } catch (e: JSONException) {
                    e.printStackTrace();
                }


            },
            Response.ErrorListener { error ->
                Toast.makeText(this, error.message, Toast.LENGTH_SHORT).show()


            }) {
            override fun getParams(): Map<String, String> {
                val params: MutableMap<String, String> = HashMap()
                params["orderId"] = inorderManagerId
                return params
            }

            @Throws(AuthFailureError::class)
            override fun getHeaders(): Map<String, String> {
                val params: MutableMap<String, String> = HashMap()
                params["Content-Type"] = "application/x-www-form-urlencoded"
                return params
            }
        }

        val queue: RequestQueue = Volley.newRequestQueue(this)
        queue.add(request)

    }


    fun getOrderDate(orderDateTimeStamp: String): String? {
        val format: SimpleDateFormat
        val timeStamp = orderDateTimeStamp.toLong()
        val netDate = Date(timeStamp)
        format = SimpleDateFormat("dd-MM-yy h:mm a", Locale.getDefault())
        return format.format(netDate)
    }

    fun getExpectedDate(orderDateTimeStamp: String): String? {
        val format: SimpleDateFormat
        val timeStamp = orderDateTimeStamp.toLong()
        val netDate = Date(timeStamp)
        format = SimpleDateFormat("EEE, d MMM", Locale.getDefault())
        return format.format(netDate)
    }


    private fun ClickOnReceiveOrderBtn() {
        binding.receiveOrder.setOnClickListener {
            binding.receiveOrder.isEnabled=false

            val status= "shipped";

            updateSatus(status, "0")


        }


    }

    private fun updateSatus(status: String, manageOrderValue: String) {

        val dialog=ProgressDialog.progressDialog(this,"Updating...")
        dialog.show()

        val request: StringRequest = object : StringRequest(
            Method.POST, statusUrl,
            Response.Listener { response ->
                if(response.equals("shipped")){
                    dialog.dismiss()
                    Toast.makeText(this,response,Toast.LENGTH_SHORT).show()
                    binding.receiveOrder.visibility= GONE
                    binding.cv.visibility= VISIBLE

                }else if(response.equals("delivered")){
                    dialog.dismiss()
                    Toast.makeText(this,response,Toast.LENGTH_SHORT).show()
                    finish()

                }else{
                    dialog.dismiss()
                    Toast.makeText(this,response,Toast.LENGTH_SHORT).show()
                }

            },
            Response.ErrorListener { error ->
                dialog.dismiss()
                try {
                    Toast.makeText(this, error.message, Toast.LENGTH_SHORT).show()
                } catch (e: Exception) {
                }


            }) {
            override fun getParams(): Map<String, String> {
                val params: MutableMap<String, String> = HashMap()
                params["orderId"] = inorderId
                params["productId"] = inproductId
                params["status"] = status
                params["orderManagerId"] = inorderManagerId
                params["deluid"] = "del"+uid
                params["otp"]=System.currentTimeMillis().toString().substring(7,13) +" OTP for delivery"
                if(status.equals("delivered")){
                    if(paymentM.equals("cod")){
                        params["deliveredBy"] = "On "+getOrderDate((System.currentTimeMillis()).toString())+" "+"Delivered by and Rs "+intotal+" "+" collected by "+deliveredBy

                    }else{
                        params["deliveredBy"] = "On "+getOrderDate((System.currentTimeMillis()).toString())+" "+"Delivered by "+deliveredBy

                    }
                    params["paymentMode"] =paymentM
                    params["manageOrderValue"] =manageOrderValue
                }
                return params
            }

            @Throws(AuthFailureError::class)
            override fun getHeaders(): Map<String, String> {
                val params: MutableMap<String, String> = HashMap()
                params["Content-Type"] = "application/x-www-form-urlencoded"
                return params
            }
        }

        val queue: RequestQueue = Volley.newRequestQueue(this)
        queue.add(request)


    }

    private fun GetUserOrder(status: String, dialog: Dialog) {
        val distinctList = mutableListOf<OrderModel>()

        val fetchUrl = Constants.baseUrl1 + "/Orders/getOrdersForUser.php"
        val request: StringRequest = object : StringRequest(
            Method.POST, fetchUrl,
            Response.Listener { response ->

                try {

                    val jsonObject = JSONObject(response)
                    val success: String = jsonObject.getString("success")
                    val jsonArray: JSONArray = jsonObject.getJSONArray("data")
                    if (success.equals("1")) {
                        dialog.dismiss()


                        for (item in 0 until jsonArray.length()) {

                            val jsonObject: JSONObject = jsonArray.getJSONObject(item)

                            val id: String = jsonObject.getString("id")
                            val orderId: String = jsonObject.getString("orderId")
                            val productId: String = jsonObject.getString("productId")
                            val productQuantity: String = jsonObject.getString("productQuantity")
                            val productAddress: String = jsonObject.getString("productAddress")
                            val productTotalPay: String = jsonObject.getString("productTotalPay")
                            val productPaymentMode: String = jsonObject.getString("productPaymentMode")
                            val productPaymentStatus: String = jsonObject.getString("productPaymentStatus")
                            val productTrackingStatus: String = jsonObject.getString("productTrackingStatus")
                            val productUid: String = jsonObject.getString("productUid")
                            val productDeliveryDate: String = jsonObject.getString("productDeliveryDate")
                            val productName: String = jsonObject.getString("productName")
                            val productImage: String = jsonObject.getString("productImage")
                            val productOrderDate: String = jsonObject.getString("productOrderDate")
                            val productDescription: String = jsonObject.getString("productDescription")
                            val productRefundStatus: String = jsonObject.getString("productRefundStatus")


                            val orderModel = OrderModel(id,orderId,productId,productQuantity,productAddress,productTotalPay,productPaymentMode,productPaymentStatus,productTrackingStatus,productUid,productDeliveryDate,productName,productImage,productOrderDate,productDescription,productRefundStatus)
                            if(paymentM.equals("cod")){
                                if(orderModel.orderId.equals(inorderId)&&orderModel.productPaymentStatus.equals("0")){

                                    distinctList.add(orderModel)
                                }


                            }else{

                                if(orderModel.orderId.equals(inorderId)&&!orderModel.productDescription.contains("On")){

                                    distinctList.add(orderModel)
                                }



                            }


                        }


                        if(distinctList.size>1){

                        }else{
                            manageOrderValue="1"
                        }


                        updateSatus(status,manageOrderValue)






                    }else{
                        dialog.dismiss()
                    }

                } catch (e: JSONException) {
                    e.printStackTrace();
                }


            },
            Response.ErrorListener { error ->
                dialog.dismiss()
                Toast.makeText(this, error.message, Toast.LENGTH_SHORT).show()


            }) {
            override fun getParams(): Map<String, String> {
                val params: MutableMap<String, String> = HashMap()
                params["orderId"] = inorderManagerId
                return params
            }

            @Throws(AuthFailureError::class)
            override fun getHeaders(): Map<String, String> {
                val params: MutableMap<String, String> = HashMap()
                params["Content-Type"] = "application/x-www-form-urlencoded"
                return params
            }
        }

        val queue: RequestQueue = Volley.newRequestQueue(this)
        queue.add(request)

    }



    private fun checkUser() {
        val checkUser =
            SharedPref.readFromSharedPref(this, Constants.userPrefName, Constants.defaultPrefValue);
        if (!checkUser.equals("0")) {
            val splitDetails = checkUser?.split(",")
            uid = splitDetails?.get(3) ?: "nothing"


        }
    }


}