package com.mac.ecomdeliveryphp.Authentication

import android.app.Dialog
import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.widget.Toast
import com.android.volley.AuthFailureError
import com.android.volley.Response
import com.android.volley.toolbox.StringRequest
import com.android.volley.toolbox.Volley
import com.mac.ecomadminphp.Utils.Constants
import com.mac.ecomadminphp.Utils.ProgressDialog
import com.mac.ecomdeliveryphp.databinding.ActivityForgotPasswordBinding

class ForgotPassword_Activity : AppCompatActivity() {
    private lateinit var binding: ActivityForgotPasswordBinding
    private val sendMail: String = Constants.baseUrl + "/sendMail.php"

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityForgotPasswordBinding.inflate(layoutInflater)
        setContentView(binding.root)

        ClickOnSendOTPBtn()

    }

    private fun ClickOnSendOTPBtn() {

        binding.sendOtpBtn.setOnClickListener {

            if (!binding.etForgotEmail.text.isEmpty()) {
                val dialog = ProgressDialog.progressDialog(this, "Sending verification email...")
                dialog.show()

                val milis: String = System.currentTimeMillis().toString()
                val otp: String = milis.substring(7, 13)
                val email: String = binding.etForgotEmail.text.toString().trim()
                val title = "reset password"
                sendOTPforEmailVerification(email, otp, title,dialog)


            } else {
                binding.etForgotEmail.setError("Please enter email")
                binding.etForgotEmail.requestFocus()
            }


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

                if (response.equals("Mail sent")) {
                    dialog.dismiss()
                    Toast.makeText(this, response, Toast.LENGTH_SHORT).show()
                    val intent: Intent = Intent(this, EmailVerification_Activity::class.java)
                    intent.putExtra("email", email)
                    intent.putExtra("otp", otp);
                    intent.putExtra("title", title)
                    startActivity(intent)
                    finish()
                } else {
                    dialog.dismiss()
                    Toast.makeText(this, "Something went wrong", Toast.LENGTH_SHORT).show()
                }


            },
            Response.ErrorListener { error ->
                dialog.dismiss()
                Toast.makeText(this, "Something went wrong", Toast.LENGTH_SHORT).show()


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

        val queue = Volley.newRequestQueue(this)
        queue.add(request)

    }
}