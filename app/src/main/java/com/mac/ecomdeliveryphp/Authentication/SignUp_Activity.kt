package com.mac.ecomdeliveryphp.Authentication

import android.app.Dialog
import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.widget.Toast
import com.android.volley.AuthFailureError
import com.android.volley.RequestQueue
import com.android.volley.Response
import com.android.volley.toolbox.StringRequest
import com.android.volley.toolbox.Volley
import com.mac.ecomadminphp.Utils.Constants
import com.mac.ecomadminphp.Utils.SharedPref
import com.mac.ecomdeliveryphp.MainActivity
import com.mac.ecomdeliveryphp.databinding.ActivitySignUpBinding
import java.util.*
import kotlin.collections.HashMap

class SignUp_Activity : AppCompatActivity() {
    private lateinit var binding : ActivitySignUpBinding
    private lateinit var str_username:String;
    private lateinit var str_email:String
    private lateinit var str_password:String
    private lateinit var uid:String
    private  val registerUrl:String = Constants.baseUrl+"/register.php"
    private  val validateUrl:String = Constants.baseUrl+"/validate.php"


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivitySignUpBinding.inflate(layoutInflater)
        setContentView(binding.root)


        ClickOnLoginBtn();
        ClickOnSignUpBtn();
    }

    private fun ClickOnSignUpBtn() {
        binding.signupBtn.setOnClickListener{
            var dialog = com.mac.ecomadminphp.Utils.ProgressDialog.progressDialog(this,"Registering please wait...")
            dialog.show()
            checkValidation(dialog);
        }
    }

    private fun checkValidation(dialog: Dialog) {
        str_username = binding.etUsername.text.toString().trim()
        str_email = binding.etEmail.text.toString().trim()
        str_password = binding.etPassword.text.toString().trim()

        if(str_username.isEmpty()){
            binding.etUsername.setError("Please enter username")
            binding.etUsername.requestFocus()

        }else if(str_email.isEmpty()){
            binding.etEmail.setError("Please enter email")
            binding.etEmail.requestFocus()

        }else if(str_password.isEmpty()){
            binding.etPassword.setError("Please enter password")
            binding.etPassword.requestFocus()

        }else{
            ValidateUser(dialog)
        }

    }

    private fun CreateUser(dialog: Dialog) {

        val random = Random(System.currentTimeMillis())
        val finalResult = 10000000+random.nextLong()

        if(finalResult.toString().contains("-")){
            val key = finalResult.toString().replace("-","")
            uid ="uid"+key

        }else{
            uid ="uid"+finalResult.toString()

        }


        val request: StringRequest = object : StringRequest(Method.POST, registerUrl,
            Response.Listener { response ->

                setUser(str_username,str_email,str_password,uid)

                binding.etUsername.setText("")
                binding.etEmail.setText("")
                binding.etPassword.setText("")

                Toast.makeText(this,response,Toast.LENGTH_SHORT).show();
                dialog.dismiss()
                startActivity(Intent(this, MainActivity::class.java));
                finish()

            },
            Response.ErrorListener { error ->
                Toast.makeText(this,error.message,Toast.LENGTH_SHORT).show()
                dialog.dismiss()
            }) {
            override fun getParams(): Map<String, String> {
                val params: MutableMap<String, String> = HashMap()
                params["username"] = str_username
                params["email"] = str_email
                params["password"] = str_password
                params["uid"]=uid
                return params
            }

            @Throws(AuthFailureError::class)
            override fun getHeaders(): Map<String, String> {
                val params: MutableMap<String, String> = HashMap()
                params["Content-Type"] = "application/x-www-form-urlencoded"
                return params
            }
        }

        val queue:RequestQueue = Volley.newRequestQueue(this)
        queue.add(request)




    }

    private fun ClickOnLoginBtn() {
        binding.loginBtn.setOnClickListener{
            startActivity(Intent(this, Login_Activity::class.java))
            finish()
        }

    }
    private fun ValidateUser(dialog: Dialog) {

        val request: StringRequest = object : StringRequest(
            Method.POST, validateUrl,
            Response.Listener { response ->

                if(response.equals("1")){
                    Toast.makeText(this,"Account already exist", Toast.LENGTH_SHORT).show()
                    dialog.dismiss()
                }else if(response.equals("user not exist")){
                    CreateUser(dialog)
                }else{
                    CreateUser(dialog)

                }



            },
            Response.ErrorListener { error ->
                dialog.dismiss()

            }) {
            override fun getParams(): Map<String, String> {
                val params: MutableMap<String, String> = HashMap()
                params["email"] = str_email
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


    fun setUser(name: String, email: String, password: String, uid: String){
        val userInfo:String = "$name,$email,$password,$uid"
        SharedPref.writeInSharedPref(this,Constants.userPrefName,userInfo)

    }

}