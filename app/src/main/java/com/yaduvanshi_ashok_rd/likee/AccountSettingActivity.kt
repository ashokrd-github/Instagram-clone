package com.yaduvanshi_ashok_rd.likee

import android.Manifest
import android.app.AlertDialog
import android.app.ProgressDialog
import android.content.Intent
import android.content.pm.ActivityInfo
import android.content.pm.PackageManager
import android.graphics.BitmapFactory
import android.graphics.Color
import android.graphics.drawable.BitmapDrawable
import android.net.Uri
import android.os.Bundle
import android.provider.CalendarContract.Colors
import android.provider.ContactsContract.Profile
import android.provider.MediaStore
import android.text.TextUtils
import android.widget.Button
import android.widget.EditText
import android.widget.ImageButton
import android.widget.ImageView
import android.widget.TextView
import android.widget.Toast
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import androidx.core.app.ActivityCompat
import androidx.core.content.ContextCompat
import androidx.core.graphics.createBitmap
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import com.google.android.gms.tasks.Continuation
import com.google.android.gms.tasks.OnCompleteListener
import com.google.android.gms.tasks.Task
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.FirebaseUser
import com.google.firebase.auth.ktx.auth
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseError
import com.google.firebase.database.DatabaseReference
import com.google.firebase.database.FirebaseDatabase
import com.google.firebase.database.ValueEventListener
import com.google.firebase.ktx.Firebase
import com.google.firebase.storage.FirebaseStorage
import com.google.firebase.storage.StorageReference
import com.google.firebase.storage.StorageTask
import com.google.firebase.storage.UploadTask
import com.soundcloud.android.crop.Crop
import com.squareup.picasso.Picasso
import com.yaduvanshi_ashok_rd.likee.Activity.GalleryActivity
import com.yaduvanshi_ashok_rd.likee.Activity.ProfileDetail
import com.yaduvanshi_ashok_rd.likee.Activity.ReelActivity
import com.yaduvanshi_ashok_rd.likee.Activity.postActivity
import com.yaduvanshi_ashok_rd.likee.Activity.postActivity.Companion
import com.yaduvanshi_ashok_rd.likee.Adapter.userAdapter
import com.yaduvanshi_ashok_rd.likee.Model.User
import java.io.File
import java.util.UUID

class AccountSettingActivity : AppCompatActivity() {
    private lateinit var userName: EditText
    private lateinit var fullName: EditText
    private lateinit var bio: EditText
    private lateinit var auth: FirebaseAuth
    private lateinit var btnLogout: Button
    private lateinit var close: ImageButton
    private lateinit var changeImage: TextView
    private lateinit var checked: ImageButton
    private lateinit var deletedAccount: Button
    private lateinit var userProfile: ImageView
    private lateinit var firebaseUser: FirebaseUser
    private var checker = ""
    private var myUrl = ""
    private var imageUri: Uri? = null
    private var storageProfileRef: StorageReference? = null

    companion object {
        private const val REQUEST_CODE_PERMISSIONS = 101
        private const val REQUEST_CODE_PICK_IMAGE = 102
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContentView(R.layout.activity_account_setting)
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main)) { v, insets ->
            val systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars())
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom)
            insets
        }
        requestedOrientation = ActivityInfo.SCREEN_ORIENTATION_PORTRAIT

        val permissions = arrayOf(
            Manifest.permission.READ_EXTERNAL_STORAGE,
            Manifest.permission.WRITE_EXTERNAL_STORAGE
        )

        if (ContextCompat.checkSelfPermission(
                this,
                Manifest.permission.READ_EXTERNAL_STORAGE
            ) != PackageManager.PERMISSION_GRANTED ||
            ContextCompat.checkSelfPermission(
                this,
                Manifest.permission.WRITE_EXTERNAL_STORAGE
            ) != PackageManager.PERMISSION_GRANTED
        ) {
            ActivityCompat.requestPermissions(
                this,
                permissions,
                postActivity.REQUEST_CODE_PERMISSIONS
            )
        }

//        window.statusBarColor = Colors.TYPE_EVENT
        auth = Firebase.auth
        changeImage = findViewById(R.id.changeImage)
        btnLogout = findViewById(R.id.btnLogout)
        checked = findViewById(R.id.checked)
        userName = findViewById(R.id.userName)
        userProfile = findViewById(R.id.userProfile)
        fullName = findViewById(R.id.fullName)
        bio = findViewById(R.id.bio)
        close = findViewById(R.id.close)
        deletedAccount = findViewById(R.id.deleteAccount)
        firebaseUser = FirebaseAuth.getInstance().currentUser!!





        storageProfileRef = FirebaseStorage.getInstance().reference.child("Profile Pictures")
        getUserInfo()

        btnLogout.setOnClickListener {
            FirebaseAuth.getInstance().signOut()
            val intent = Intent(this@AccountSettingActivity, LoginActivity::class.java)
            intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TASK or Intent.FLAG_ACTIVITY_NEW_TASK)
            startActivity(intent)
            finish()
        }

        userProfile.setOnClickListener {
            val intent = Intent(this, ProfileDetail::class.java)
            intent.putExtra("name", userName.text.toString())
            intent.putExtra("username", fullName.text.toString())
//            intent.putExtra("image", userProfile.id)

            startActivity(intent)
        }


        deletedAccount.setOnClickListener {
            val user = FirebaseAuth.getInstance().currentUser

            val alertDialog = AlertDialog.Builder(this).create()
            alertDialog.window?.setBackgroundDrawableResource(R.color.colorBlack)
            alertDialog.setMessage("Are you want to delete Account ?")
            alertDialog.setButton(AlertDialog.BUTTON_NEUTRAL, "NO")
            { dialog, which ->

                dialog.dismiss()
            }
            alertDialog.setButton(AlertDialog.BUTTON_POSITIVE, "YES")
            { dialog, which ->
                user!!.delete().addOnCompleteListener { task ->
                    if (task.isSuccessful) {
                        val intent = Intent(this, SignUpActivity::class.java)
                        FirebaseDatabase.getInstance().reference.child("Users").child(user.uid)
                            .removeValue()
                        Toast.makeText(this, "Account was deleted successfully", Toast.LENGTH_SHORT)
                            .show()
                        startActivity(intent)
                        dialog.dismiss()
                    } else {
                        Toast.makeText(this, "something went wrong", Toast.LENGTH_SHORT).show()
                    }


                }

                dialog.dismiss()
            }
            alertDialog.show()
        }



        close.setOnClickListener {
            val intent = Intent(this@AccountSettingActivity, MainActivity::class.java)
            intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TASK or Intent.FLAG_ACTIVITY_NEW_TASK)
            startActivity(intent)
            finish()
        }


        changeImage.setOnClickListener {
            checker = "clicked"

//            val intent = Intent(Intent.ACTION_PICK, MediaStore.Images.Media.EXTERNAL_CONTENT_URI)
//            startActivityForResult(intent, 101)

//                CropImage.ActivityResult()
//                .setAspectRatio(1,1)
//                .start(this@AccountSettingActivity)
            val intent = Intent(Intent.ACTION_PICK, MediaStore.Images.Media.EXTERNAL_CONTENT_URI)
            startActivityForResult(intent, REQUEST_CODE_PICK_IMAGE)


        }
        checked.setOnClickListener {
            if (checker == "clicked") {
                uploadProfileImageandInfo()
            } else {
                updateUserInfoOnly()
            }

        }


    }

    private fun uploadProfileImageandInfo() {

        when {
            imageUri == null -> Toast.makeText(this, "Please select image", Toast.LENGTH_SHORT)
                .show()

            TextUtils.isEmpty(fullName.text.toString()) -> {
                Toast.makeText(this, "Full Name is required", Toast.LENGTH_SHORT).show()
            }

            TextUtils.isEmpty(userName.text.toString()) -> {
                Toast.makeText(this, "Username is required", Toast.LENGTH_SHORT).show()
            }

            else -> {
                val progressDialog = ProgressDialog(this)
                progressDialog.setTitle("Profile Settings")
                progressDialog.setMessage("Please wait! Updating...")
                progressDialog.show()

                val fileRef = storageProfileRef!!.child(firebaseUser.uid + ".png")

                val uploadTask: StorageTask<*>
                uploadTask = fileRef.putFile(imageUri!!)

                uploadTask.continueWithTask(Continuation<UploadTask.TaskSnapshot, Task<Uri>> { task ->
                    if (!task.isSuccessful) {
                        task.exception?.let {
                            throw it
                            Toast.makeText(this, "exception:--" + it, Toast.LENGTH_SHORT).show()
                            progressDialog.dismiss()
                        }
                    }
                    return@Continuation fileRef.downloadUrl
                }).addOnCompleteListener(OnCompleteListener<Uri> { task ->
                    if (task.isSuccessful) {
                        val downloadUrl = task.result
                        myUrl = downloadUrl.toString()

                        val ref = FirebaseDatabase.getInstance().reference.child("Users")
                        val userMap = HashMap<String, Any>()
                        userMap["fullname"] = fullName.text.toString()
                        userMap["username"] = userName.text.toString().toLowerCase()
                        userMap["bio"] = bio.text.toString()
                        userMap["image"] = myUrl

                        ref.child(firebaseUser.uid).updateChildren(userMap)
                        Toast.makeText(this, "Account is updated", Toast.LENGTH_SHORT).show()

                        val intent = Intent(this@AccountSettingActivity, MainActivity::class.java)
                        intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TASK or Intent.FLAG_ACTIVITY_NEW_TASK)
                        startActivity(intent)
                        finish()
                        progressDialog.dismiss()

                    } else {
                        progressDialog.dismiss()
                    }
                })
            }
        }
    }


    override fun onRequestPermissionsResult(
        requestCode: Int,
        permissions: Array<String>,
        grantResults: IntArray,
    ) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults)
        if (requestCode == postActivity.REQUEST_CODE_PERMISSIONS) {
            if (grantResults.isNotEmpty() && grantResults[0] == PackageManager.PERMISSION_GRANTED &&
                grantResults[1] == PackageManager.PERMISSION_GRANTED
            ) {
                Toast.makeText(this, "Permissions granted", Toast.LENGTH_SHORT).show()
            } else {
                Toast.makeText(this, "Permissions not granted", Toast.LENGTH_SHORT).show()
            }
        }


    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)
        if (requestCode == postActivity.REQUEST_CODE_PICK_IMAGE && resultCode == RESULT_OK && data != null) {
            val uri = data.data
            if (uri != null) {
                Crop.of(uri, Uri.fromFile(File(cacheDir, "cropped")))
                    .asSquare()
                    .start(this)
            }
        } else if (requestCode == Crop.REQUEST_CROP && resultCode == RESULT_OK) {
            val result = Crop.getOutput(data)
            if (data != null) {
                imageUri = result
                val imageUri = MediaStore.Images.Media.getBitmap(this.contentResolver, imageUri)
                userProfile.setImageBitmap(imageUri)
            }
        }
    }


    private fun updateUserInfoOnly() {

        when {
            TextUtils.isEmpty(fullName.text.toString()) -> {
                Toast.makeText(this, "Full Name is required", Toast.LENGTH_SHORT).show()
            }

            TextUtils.isEmpty(userName.text.toString()) -> {
                Toast.makeText(this, "username is required", Toast.LENGTH_SHORT).show()
            }

            else -> {
                val userRef: DatabaseReference =
                    FirebaseDatabase.getInstance().reference.child("Users")
                //using hashmap to store values
                val userMap = HashMap<String, Any>()
                userMap["fullname"] = fullName.text.toString()
                userMap["username"] = userName.text.toString().toLowerCase()
                userMap["bio"] = bio.text.toString()

                userRef.child(firebaseUser.uid).updateChildren(userMap)

                Toast.makeText(this, "Account is updated", Toast.LENGTH_SHORT).show()

                val intent = Intent(this@AccountSettingActivity, MainActivity::class.java)
                intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TASK or Intent.FLAG_ACTIVITY_NEW_TASK)
                startActivity(intent)
                finish()
            }
        }
    }

    private fun getUserInfo() {
        val usersRef =
            FirebaseDatabase.getInstance().reference.child("Users").child(firebaseUser.uid)
        usersRef.addValueEventListener(object : ValueEventListener {
            override fun onCancelled(error: DatabaseError) {
            }

            override fun onDataChange(snapshot: DataSnapshot) {

                if (snapshot.exists()) {
                    val currentUser = snapshot.getValue(User::class.java)

                    Picasso.get().load(currentUser?.getImage()).placeholder(R.drawable.profile)
                        .into(userProfile)



                    fullName.setText(currentUser?.getFullname())
                    userName.setText(currentUser?.getUsername())
                    bio.setText(currentUser?.getBio())

                }
            }
        })
    }

}
