package com.yaduvanshi_ashok_rd.likee

import android.app.ProgressDialog
import android.content.Intent
import android.content.pm.ActivityInfo
import android.graphics.Color
import android.os.Build
import android.os.Bundle
import android.provider.CalendarContract.Colors
import android.text.TextUtils
import android.widget.Button
import android.widget.EditText
import android.widget.TextView
import android.widget.Toast
import androidx.activity.enableEdgeToEdge
import androidx.annotation.RequiresApi
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.ktx.auth
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseError
import com.google.firebase.database.DatabaseReference
import com.google.firebase.database.FirebaseDatabase
import com.google.firebase.database.ValueEventListener
import com.google.firebase.ktx.Firebase
import com.yaduvanshi_ashok_rd.likee.Model.User
import java.time.LocalDateTime

class SignUpActivity : AppCompatActivity() {

    private lateinit var fullName: EditText
    private lateinit var userName:EditText
    private lateinit var userEmail:EditText
    private lateinit var password:EditText
    private lateinit var signUp:Button
    private lateinit var auth: FirebaseAuth
    private lateinit var loginText: TextView



    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_sign_up)

        enableEdgeToEdge()
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main)) { v, insets ->
            val systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars())
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom)
            insets
        }
        requestedOrientation = ActivityInfo.SCREEN_ORIENTATION_PORTRAIT

        window.statusBarColor = Color.TRANSPARENT
        fullName = findViewById(R.id.full_Name)
        userName = findViewById(R.id.userName)
        password = findViewById(R.id.password)
        userEmail =findViewById(R.id.userEmail)
        signUp = findViewById(R.id.btnSignUp)
        loginText = findViewById(R.id.loginText)
        auth = Firebase.auth



        loginText.setOnClickListener {
            startActivity(Intent(this, LoginActivity::class.java))
        }



        signUp.setOnClickListener {
            createAccount()
        }





    }
    private fun createAccount(){
            val fullName=fullName.text.toString()
            val userName=userName.text.toString()
            val email=userEmail.text.toString()
            val password=password.text.toString()


            when{
                TextUtils.isEmpty(fullName)-> Toast.makeText(this, "Name is required", Toast.LENGTH_SHORT).show()
                TextUtils.isEmpty(userName)-> Toast.makeText(this, "Username is required", Toast.LENGTH_SHORT).show()
                TextUtils.isEmpty(email)-> Toast.makeText(this, "Email is required", Toast.LENGTH_SHORT).show()
                TextUtils.isEmpty(password)-> Toast.makeText(this, "Password is required", Toast.LENGTH_SHORT).show()

                else->
                {
                    val progressDialog= ProgressDialog(this@SignUpActivity)
                    progressDialog.setTitle("SignUp")
                    progressDialog.setMessage("Please wait...")
                    progressDialog.setCanceledOnTouchOutside(false)
                    progressDialog.show()

                    val mAuth: FirebaseAuth = FirebaseAuth.getInstance()
                    mAuth.createUserWithEmailAndPassword(email,password)
                        .addOnCompleteListener { task ->
                            if(task.isSuccessful)
                            {
                                saveUserInfo(fullName,userName,email, password, progressDialog)
                            }
                            else
                            {
                                val message=task.exception!!.toString()
                                Toast.makeText(this,"Error : $message", Toast.LENGTH_LONG).show()
                                mAuth.signOut()
                                progressDialog.dismiss()
                            }
                        }
                }
            }
        }

        private fun saveUserInfo(fullName: String, userName: String, email: String, password:String, progressDialog: ProgressDialog) {
            val currentUserId=FirebaseAuth.getInstance().currentUser!!.uid
            val userRef : DatabaseReference =FirebaseDatabase.getInstance().reference.child("Users")

            val userMap=HashMap<String,Any>()
            userMap["uid"]=currentUserId
            userMap["fullname"]=fullName
            userMap["username"]=userName.toLowerCase()
            userMap["email"]=email
            userMap["password"]=password
            userMap["bio"]="Hey! I am using Likeee"
            userMap["image"]="gs://instagram-clone-app-205f9.appspot.com/Default images/profile.png"


            userRef.child(currentUserId).setValue(userMap)
                .addOnCompleteListener {task ->
                    if(task.isSuccessful)
                    {
                        Toast.makeText(this,"Account has been created",Toast.LENGTH_SHORT).show()


                        FirebaseDatabase.getInstance().reference
                            .child("Follow").child(currentUserId)
                            .child("Following").child(currentUserId)
                            .setValue(true)


                        val intent= Intent(this@SignUpActivity,MainActivity::class.java)
                        intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TASK or Intent.FLAG_ACTIVITY_NEW_TASK)
                        startActivity(intent)
                        finish()
                    }
                    else
                    {
                        val message=task.exception!!.toString()
                        Toast.makeText(this,"Error : $message", Toast.LENGTH_LONG).show()
                        FirebaseAuth.getInstance().signOut()
                        progressDialog.dismiss()
                    }
                }
    }
}