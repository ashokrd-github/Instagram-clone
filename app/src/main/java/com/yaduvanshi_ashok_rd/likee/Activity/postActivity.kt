package com.yaduvanshi_ashok_rd.likee.Activity

import android.Manifest
import android.app.ProgressDialog
import android.content.ContentValues
import android.content.Intent
import android.content.pm.ActivityInfo
import android.content.pm.PackageManager
import android.graphics.Bitmap
import android.graphics.Color
import android.net.Uri
import android.os.Bundle
import android.provider.CalendarContract.Colors
import android.provider.MediaStore
import android.text.TextUtils
import android.widget.Button
import android.widget.EditText
import android.widget.ImageButton
import android.widget.ImageView
import android.widget.ProgressBar
import android.widget.Toast
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import androidx.core.app.ActivityCompat
import androidx.core.content.ContextCompat
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import com.google.android.gms.tasks.Continuation
import com.google.android.gms.tasks.OnCompleteListener
import com.google.android.gms.tasks.Task
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.database.FirebaseDatabase
import com.google.firebase.storage.FirebaseStorage
import com.google.firebase.storage.StorageReference
import com.google.firebase.storage.StorageTask
import com.google.firebase.storage.UploadTask
import com.soundcloud.android.crop.Crop
import com.squareup.picasso.Picasso
import com.yaduvanshi_ashok_rd.likee.MainActivity
import com.yaduvanshi_ashok_rd.likee.R
import me.leolin.shortcutbadger.ShortcutBadger
import java.io.File
import java.sql.Time
import java.util.Date
import java.util.UUID

class postActivity : AppCompatActivity() {
    private  var myUrl=""
    private  var imageUri: Uri?=null
    private  var storagePostPictureRef: StorageReference?=null
    private lateinit var dont_post_picture: ImageButton
    private lateinit var post_picture: Button
    private lateinit var picture_to_be_posted: ImageView
    private lateinit var write_post: EditText
    private lateinit var post_gallery:Button
//    private lateinit var showBadge:Button

    companion object {
        const val REQUEST_CODE_PERMISSIONS = 101
        const val REQUEST_CODE_PICK_IMAGE = 102
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_post)
        enableEdgeToEdge()
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
                REQUEST_CODE_PERMISSIONS
            )
        }


        dont_post_picture = findViewById(R.id.dont_post_picture)
        post_picture = findViewById(R.id.post_picture)
        picture_to_be_posted = findViewById(R.id.picture_to_be_posted)
        write_post = findViewById(R.id.write_post)
        post_gallery = findViewById(R.id.post_gallery)
//        showBadge = findViewById(R.id.badger)

//        showBadge.setOnClickListener {
//            ShortcutBadger.applyCount(applicationContext, 1)
//        }


        storagePostPictureRef= FirebaseStorage.getInstance().reference.child("Post Picture")

        dont_post_picture.setOnClickListener {
            val intent=Intent(this@postActivity,MainActivity::class.java)
            intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TASK or Intent.FLAG_ACTIVITY_NEW_TASK)
            startActivity(intent)
            finish()
        }

        post_picture.setOnClickListener {
            uploadPost()

        }
//        picture_to_be_posted.setImageURI(intent.data)

        post_gallery.setOnClickListener {
            val intent = Intent(Intent.ACTION_PICK, MediaStore.Images.Media.EXTERNAL_CONTENT_URI)
            startActivityForResult(intent, REQUEST_CODE_PICK_IMAGE)
        }





    }

    override fun onRequestPermissionsResult(
        requestCode: Int,
        permissions: Array<String>,
        grantResults: IntArray
    ) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults)
        if (requestCode == REQUEST_CODE_PERMISSIONS) {
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
        if (requestCode == REQUEST_CODE_PICK_IMAGE && resultCode == RESULT_OK && data != null) {
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
                picture_to_be_posted.setImageBitmap(imageUri)
            }
        }
    }

    private fun uploadPost() {
        when
        {
            imageUri == null -> Toast.makeText(this, "Please select image first.", Toast.LENGTH_LONG).show()
            TextUtils.isEmpty(write_post.text.toString()) -> Toast.makeText(this, "Please write caption", Toast.LENGTH_LONG).show()

            else -> {
                val progressDialog = ProgressDialog(this)
                progressDialog.setTitle("Posting")
                progressDialog.setMessage("Please wait, we are posting..")
                progressDialog.show()

                val fileRef = storagePostPictureRef!!.child(System.currentTimeMillis().toString()+ ".jpg")

                var uploadTask: StorageTask<*>
                uploadTask = fileRef.putFile(imageUri!!)

                uploadTask.continueWithTask(Continuation <UploadTask.TaskSnapshot, Task<Uri>>{ task ->
                    if (!task.isSuccessful)
                    {
                        task.exception?.let {
                            throw it
                            progressDialog.dismiss()
                        }
                    }
                    return@Continuation fileRef.downloadUrl
                }).addOnCompleteListener (OnCompleteListener<Uri> { task ->
                    if (task.isSuccessful)
                    {
                        val downloadUrl = task.result
                        myUrl = downloadUrl.toString()



                        val ref = FirebaseDatabase.getInstance().reference.child("Posts")
                        val postid=ref.push().key

                        val postMap = HashMap<String, Any>()

                        postMap["postid"] = postid!!
                        postMap["caption"] = write_post.text.toString()
                        postMap["publisher"] = FirebaseAuth.getInstance().currentUser!!.uid
                        postMap["postimage"] = myUrl
                        postMap["timestamp"] = Date().time

                        ref.child(postid).updateChildren(postMap)


                        val commentRef=FirebaseDatabase.getInstance().reference.child("Comment").child(postid)
                        val commentMap = HashMap<String, Any>()
                        commentMap["publisher"] = FirebaseAuth.getInstance().currentUser!!.uid
                        commentMap["comment"] =  write_post.text.toString()

                        commentRef.push().setValue(commentMap)

                        Toast.makeText(this, "Uploaded successfully", Toast.LENGTH_LONG).show()

                        val intent = Intent(this@postActivity, MainActivity::class.java)
                        startActivity(intent)
                        finish()
                        progressDialog.dismiss()
                    }
                    else
                    {
                        progressDialog.dismiss()
                    }
                })
            }
        }
    }
}