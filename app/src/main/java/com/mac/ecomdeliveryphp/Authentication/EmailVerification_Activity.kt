package com.mac.ecomdeliveryphp.Authentication

import android.app.Dialog
import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.widget.Toast
import androidx.appcompat.app.AlertDialog
import com.android.volley.AuthFailureError
import com.android.volley.RequestQueue
import com.android.volley.Response
import com.android.volley.toolbox.StringRequest
import com.android.volley.toolbox.Volley
import com.mac.ecomadminphp.Utils.Constants
import com.mac.ecomadminphp.Utils.ProgressDialog
import com.mac.ecomadminphp.Utils.SharedPref
import com.mac.ecomdeliveryphp.MainActivity
import com.mac.ecomdeliveryphp.databinding.ActivityEmailVerificationBinding

class EmailVerification_Activity : AppCompatActivity() {
    private lateinit var binding: ActivityEmailVerificationBinding
    private lateinit var  email:String
    private lateinit var otp:String
    private lateinit var title:String
    private val verifyEmail: String = Constants.baseUrl + "/verifyEmail.php"

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding= ActivityEmailVerificationBinding.inflate(layoutInflater)
        setContentView(binding.root)
        title= intent.getStringExtra("title").toString().trim()

        email = intent.getStringExtra("email").toString().trim()
        otp = intent.getStringExtra("otp").toString().trim()
        binding.emailInfo.setText("We have sent you email on ${email} \nplease check your spam folder !")

        ClickOnVerifyBtn();


    }

    private fun ClickOnVerifyBtn() {
        binding.verifyBtn.setOnClickListener {
            checkValidation();

        }
    }

    private fun checkValidation() {
        if(!binding.etOtp.text.isEmpty()){
            if(binding.etOtp.text.toString().trim().equals(otp)){
                val dialog = ProgressDialog.progressDialog(this,"Verifying...")
                dialog.show()

                if(title.equals("email verification")){
                    verifyEmailFromServer(dialog)

                }else{
                    val intent = Intent(this,ChangePassword_Activity::class.java)
                    intent.putExtra("email",email)
                    startActivity(intent)
                    finish()
                }


            }else{

                val adialog = AlertDialog.Builder(this);
                adialog.setTitle("Wrong OTP")
                adialog.setMessage("You have entered incorrect OTP")
                adialog.setCancelable(true)
                adialog.setNegativeButton("Re-enter"){dialogInterface,which->
                    binding.etOtp.setText("")
                }

                adialog.show()


            }
        }else{
            Toast.makeText(this,"Please enter OTP",Toast.LENGTH_SHORT).show()


        }
    }

    private fun verifyEmailFromServer(dialog: Dialog) {
        val request: StringRequest = object : StringRequest(
            Method.POST, verifyEmail,
            Response.Listener { response ->
                if(response.equals("Email verified")){
                    dialog.dismiss()
                    SharedPref.writeInSharedPref(this,"verifyEmail","1")


                    val adialog = AlertDialog.Builder(this);
                    adialog.setTitle("Email verification successful")
                    adialog.setMessage("Congratulations ! Your email is verified")
                    adialog.setCancelable(false)

                    adialog.setPositiveButton("Go back"){dialogInterface,which->
                        startActivity(Intent(this, MainActivity::class.java))
                        finish()
                    }
                    adialog.show()


                }else{
                    dialog.dismiss()
                    Toast.makeText(this,"Email verification failed",Toast.LENGTH_SHORT).show()
                }
            },
            Response.ErrorListener { error ->
                Toast.makeText(this,"Email verification failed",Toast.LENGTH_SHORT).show()
                dialog.dismiss()


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
}