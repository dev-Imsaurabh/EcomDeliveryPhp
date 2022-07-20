package com.mac.ecomdeliveryphp

import android.app.Dialog
import android.content.Context
import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.view.View
import android.widget.Toast
import com.android.volley.AuthFailureError
import com.android.volley.RequestQueue
import com.android.volley.Response
import com.android.volley.toolbox.StringRequest
import com.android.volley.toolbox.Volley
import com.google.android.material.tabs.TabLayout
import com.google.android.material.tabs.TabLayoutMediator
import com.mac.ecomadminphp.Utils.Constants
import com.mac.ecomadminphp.Utils.ProgressDialog
import com.mac.ecomadminphp.Utils.SharedPref
import com.mac.ecomdeliveryphp.Adapters.DelPagerAdapter
import com.mac.ecomdeliveryphp.Authentication.EmailVerification_Activity
import com.mac.ecomdeliveryphp.Authentication.Login_Activity
import com.mac.ecomdeliveryphp.databinding.ActivityMainBinding
import org.json.JSONArray
import org.json.JSONException
import org.json.JSONObject
import java.util.HashMap

class MainActivity : AppCompatActivity() {
    private lateinit var binding:ActivityMainBinding
    private lateinit var uid:String
    private val fetchUrl: String = Constants.baseUrl + "/fetchUser.php"
    private val sendMail: String = Constants.baseUrl + "/sendMail.php"


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        binding= ActivityMainBinding.inflate(layoutInflater)
        setContentView(binding.root)
        SetupViewPager()
        checkUser()
        ClickOnLogoutBtn()
        checkVerification()
        ClickOnVerifybtn()




    }



    private fun checkUser() {
        val checkUser =
            SharedPref.readFromSharedPref(this, Constants.userPrefName, Constants.defaultPrefValue);
        if (!checkUser.equals("0")) {
            val splitDetails = checkUser?.split(",")
            binding.txtUsername.setText(splitDetails?.get(0) ?: "username")
            binding.txtEmail.setText(splitDetails?.get(1) ?: "email")
            uid = splitDetails?.get(3) ?: "nothing"


        }
    }

    private fun ClickOnLogoutBtn() {

        binding.logoutBtn.setOnClickListener { LogoutUser() }

    }

    private fun LogoutUser() {
        val prefInfo = this.getSharedPreferences(Constants.mainUserPref, Context.MODE_PRIVATE)
        val editor = prefInfo.edit().clear()
        editor.commit()
        Toast.makeText(this, "Logout", Toast.LENGTH_SHORT).show()
        startActivity(Intent(this, Login_Activity::class.java))
        finish()


    }


    private fun checkVerification() {
        fetchUserInfo(binding.txtEmail.text.toString().trim())

    }

    private fun fetchUserInfo(email: String) {
        val request: StringRequest = object : StringRequest(
            Method.POST, fetchUrl,
            Response.Listener { response ->

                try {

                    val jsonObject  = JSONObject(response)
                    val success:String = jsonObject.getString("success")
                    val jsonArray: JSONArray = jsonObject.getJSONArray("data")
                    if(success.equals("1")){

                        val jsonObject: JSONObject = jsonArray.getJSONObject(0)

                        val id:String =jsonObject.getString("id")
                        val name:String =jsonObject.getString("username")
                        val email:String =jsonObject.getString("email")
                        val password:String =jsonObject.getString("password")
                        val emailv:String= jsonObject.getInt("emailv").toString()

                        if(emailv.equals("0")){
                            binding.emailVerificationLayout.visibility= View.VISIBLE;

                        }else{

                        }

                    }

                }catch (e: JSONException){
                    e.printStackTrace();
                }


            },
            Response.ErrorListener { error ->

            }) {
            override fun getParams(): Map<String, String> {
                val params: MutableMap<String, String> = HashMap()
                params["email"] = email
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


    private fun ClickOnVerifybtn() {

        binding.everifyBtn.setOnClickListener {
            val dialog = ProgressDialog.progressDialog(this,"Sending verification email...")
            dialog.show()

            val milis: String = System.currentTimeMillis().toString()
            val otp: String = milis.substring(7, 13)
            val email: String = binding.txtEmail.text.toString().trim()
            val title = "email verification"

            sendOTPforEmailVerification(email, otp, title,dialog)
        }



    }



    private fun sendOTPforEmailVerification(
        email: String,
        otp: String,
        title: String,
        dialog: Dialog
    ) {

        val request: StringRequest = object : StringRequest(
            Method.POST, sendMail,
            Response.Listener { response ->

                if(response.equals("Mail sent")){
                    dialog.dismiss()
                    Toast.makeText(this,response,Toast.LENGTH_SHORT).show()
                    val intent:Intent = Intent(this,EmailVerification_Activity::class.java)
                    intent.putExtra("email",email)
                    intent.putExtra("otp",otp);
                    intent.putExtra("title",title)
                    startActivity(intent)
                    finish()
                }else{
                    dialog.dismiss()
                    Toast.makeText(this,"Something went wrong",Toast.LENGTH_SHORT).show()
                }



            },
            Response.ErrorListener { error ->
                dialog.dismiss()
                Toast.makeText(this,"Something went wrong",Toast.LENGTH_SHORT).show()



            }) {
            override fun getParams(): Map<String, String> {
                val params: MutableMap<String, String> = HashMap()
                params["email"] = email
                params["otp"] = otp
                params["title"] = title
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


    private fun SetupViewPager() {

        val tabLayoutMediator= TabLayoutMediator(binding.tabLayout,binding.viewPager){tab,position->
            when(position){
                0->tab.text="Active"
                1->tab.text="Delivered"

            }
        }
        binding.viewPager.adapter = DelPagerAdapter.PagerAdapter(this)
        binding.tabLayout.addOnTabSelectedListener(object : TabLayout.OnTabSelectedListener {
            override fun onTabSelected(tab: TabLayout.Tab?) {
                if (tab != null) {
                    when(tab.position){

                    }
                }
            }

            override fun onTabUnselected(tab: TabLayout.Tab?) {
            }

            override fun onTabReselected(tab: TabLayout.Tab?) {
            }
        })

        tabLayoutMediator.attach()


    }
}

