package com.mac.ecomdeliveryphp.Authentication

import android.app.Dialog
import android.content.Intent
import android.os.Bundle
import android.text.InputType
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import com.android.volley.AuthFailureError
import com.android.volley.RequestQueue
import com.android.volley.Response
import com.android.volley.toolbox.StringRequest
import com.android.volley.toolbox.Volley
import com.mac.ecomadminphp.Utils.Constants
import com.mac.ecomadminphp.Utils.SharedPref
import com.mac.ecomdeliveryphp.MainActivity
import com.mac.ecomdeliveryphp.databinding.ActivityLoginBinding
import org.json.JSONArray
import org.json.JSONException
import org.json.JSONObject

class Login_Activity : AppCompatActivity() {
    private lateinit var binding: ActivityLoginBinding
    private lateinit var str_email: String
    private lateinit var str_password: String
    private  val loginUrl:String = Constants.baseUrl+"/login.php"
    private  val fetchUrl:String = Constants.baseUrl+"/fetchUser.php"
    private  var  state:Int=0

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityLoginBinding.inflate(layoutInflater)
        setContentView(binding.root)

        ClickOnSignUpBtn();
        ClickOnLoginBtn();
        ClickOnFogortPasswordBtn();
        ClickOnShowPasswordBtn()
    }

    private fun ClickOnFogortPasswordBtn() {
        binding.forgetBtn.setOnClickListener {

            startActivity(Intent(this,ForgotPassword_Activity::class.java))
        }
    }

    private fun ClickOnLoginBtn() {
        binding.loginBtn.setOnClickListener {
            var dialog = com.mac.ecomadminphp.Utils.ProgressDialog.progressDialog(this,"Logging in please wait...")
            dialog.show()
            CheckValidation(dialog);
        }
    }

    private fun CheckValidation(dialog: Dialog) {
        str_email = binding.etEmail.text.toString().trim()
        str_password = binding.etPassword.text.toString().trim()
        if (str_email.isEmpty()) {
            binding.etEmail.setError("Please enter email")
            binding.etEmail.requestFocus()

        } else if (str_password.isEmpty()) {
            binding.etPassword.setError("Please enter password")
            binding.etPassword.requestFocus()

        } else {

            LoginUser(dialog);

        }
    }

    private fun LoginUser(dialog: Dialog) {

        val request: StringRequest = object : StringRequest(
            Method.POST, loginUrl,
            Response.Listener { response ->

                if(response.equals("1")){

                    dialog.dismiss()


                    fetchUserInfo(str_email,dialog);


                }else{
                    Toast.makeText(this,"Account not exist", Toast.LENGTH_SHORT).show()
                    dialog.dismiss()

                }



            },
            Response.ErrorListener { error ->
                dialog.dismiss()
                Toast.makeText(this,error.message,Toast.LENGTH_SHORT).show()


            }) {
            override fun getParams(): Map<String, String> {
                val params: MutableMap<String, String> = HashMap()
                params["email"] = str_email
                params["password"] = str_password
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

    private fun fetchUserInfo(email: String, dialog: Dialog) {
        val request: StringRequest = object : StringRequest(
            Method.POST, fetchUrl,
            Response.Listener { response ->


                try {

                   val jsonObject  = JSONObject(response)
                   val success:String = jsonObject.getString("success")
                   val jsonArray:JSONArray = jsonObject.getJSONArray("data")
                   if(success.equals("1")){

                       val jsonObject:JSONObject = jsonArray.getJSONObject(0)

                       val id:String =jsonObject.getString("id")
                       val name:String =jsonObject.getString("username")
                       val email:String =jsonObject.getString("email")
                       val password:String =jsonObject.getString("password")
                       val uid:String =jsonObject.getString("uid")
                       setUser(name,email,password,dialog,uid)

                   }

               }catch (e:JSONException){
                   e.printStackTrace();
               }


            },
            Response.ErrorListener { error ->
                dialog.dismiss()
                Toast.makeText(this,error.message,Toast.LENGTH_SHORT).show()

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

    private fun ClickOnSignUpBtn() {
        binding.signupBtn.setOnClickListener {
            startActivity(Intent(this, SignUp_Activity::class.java))
            finish()
        }
    }

    fun setUser(name: String, email: String, password: String, dialog: Dialog, uid: String){
        val userInfo:String = "$name,$email,$password,$uid"
        SharedPref.writeInSharedPref(this, Constants.userPrefName,userInfo)
        Toast.makeText(this,"Login successful", Toast.LENGTH_SHORT).show();
        dialog.dismiss()
        startActivity(Intent(this, MainActivity::class.java));
        finish()

    }


    override fun onStart() {
        super.onStart()
        checkUser()
    }

    private fun checkUser() {
        val checkUser = SharedPref.readFromSharedPref(this,Constants.userPrefName,Constants.defaultPrefValue);
        if(!checkUser.equals("0")){
            startActivity(Intent(this,MainActivity::class.java))
            finish()
        }

    }


    private fun ClickOnShowPasswordBtn() {
        binding.showBtn.setOnClickListener {
            if(state==0){
                binding.etPassword.inputType = InputType.TYPE_CLASS_TEXT or InputType.TYPE_TEXT_VARIATION_NORMAL
                binding.showBtn.setText("Hide password")
                state=1
            }else{
                binding.etPassword.inputType=InputType.TYPE_CLASS_TEXT or InputType.TYPE_TEXT_VARIATION_PASSWORD
                binding.showBtn.setText("Show password")
                state=0
            }

        }
    }
}