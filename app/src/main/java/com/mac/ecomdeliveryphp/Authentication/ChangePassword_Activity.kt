package com.mac.ecomdeliveryphp.Authentication

import android.app.Dialog
import android.content.Intent
import android.os.Bundle
import android.text.InputType
import android.widget.Toast
import androidx.appcompat.app.AlertDialog
import androidx.appcompat.app.AppCompatActivity
import com.android.volley.AuthFailureError
import com.android.volley.RequestQueue
import com.android.volley.Response
import com.android.volley.toolbox.StringRequest
import com.android.volley.toolbox.Volley
import com.mac.ecomadminphp.Utils.Constants
import com.mac.ecomadminphp.Utils.ProgressDialog
import com.mac.ecomdeliveryphp.databinding.ActivityChangePasswordBinding


class ChangePassword_Activity : AppCompatActivity() {
    private lateinit var binding: ActivityChangePasswordBinding
    private var  state:Int=0
    private lateinit var email:String
    private val changePasswordUrl: String = Constants.baseUrl + "/changePassword.php"

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding= ActivityChangePasswordBinding.inflate(layoutInflater)
        setContentView(binding.root)

        SetIntents()
        ClickOnShowPasswordBtn()
        ClickOnChangeBtn()

    }

    private fun SetIntents() {
        email = intent.getStringExtra("email").toString().trim()
    }

    private fun ClickOnChangeBtn() {
        binding.changePasswordBtn.setOnClickListener {
            if(!binding.etNewPassword.text.isEmpty()){
                val dialog =ProgressDialog.progressDialog(this,"Changing password...")
                dialog.show()
                val newPassword = binding.etNewPassword.text.toString().trim()
                ChangePassword(dialog,newPassword)
            }else{
                binding.etNewPassword.setError("Please enter your new password")
                binding.etNewPassword.requestFocus()
            }
        }
    }

    private fun ChangePassword(dialog: Dialog, newPassword: String) {

        val request: StringRequest = object : StringRequest(
            Method.POST, changePasswordUrl,
            Response.Listener { response ->
                if(response.equals("Password changed")){
                    dialog.dismiss()


                    val adialog = AlertDialog.Builder(this);
                    adialog.setTitle("Congratulations")
                    adialog.setMessage("Congratulations ! your password is changed")
                    adialog.setCancelable(false)

                    adialog.setPositiveButton("Go back to login"){dialogInterface,which->
                        finish()
                    }
                    adialog.show()


                }else{
                    dialog.dismiss()
                    Toast.makeText(this,"failed",Toast.LENGTH_SHORT).show()
                }
            },
            Response.ErrorListener { error ->
                Toast.makeText(this,"failed",Toast.LENGTH_SHORT).show()
                dialog.dismiss()


            }) {
            override fun getParams(): Map<String, String> {
                val params: MutableMap<String, String> = HashMap()
                params["email"] = email
                params["password"] = newPassword
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

    private fun ClickOnShowPasswordBtn() {
        binding.showBtn.setOnClickListener {
            if(state==0){
                binding.etNewPassword.inputType = InputType.TYPE_CLASS_TEXT or InputType.TYPE_TEXT_VARIATION_NORMAL
                binding.showBtn.setText("Hide password")
                state=1
            }else{
                binding.etNewPassword.inputType=InputType.TYPE_CLASS_TEXT or InputType.TYPE_TEXT_VARIATION_PASSWORD
                binding.showBtn.setText("Show password")
                state=0
            }

        }
    }
}