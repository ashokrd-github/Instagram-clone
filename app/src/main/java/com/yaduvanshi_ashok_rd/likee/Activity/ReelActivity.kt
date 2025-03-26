package com.yaduvanshi_ashok_rd.likee.Activity

import android.app.ProgressDialog
import android.content.Intent
import android.content.pm.ActivityInfo
import android.net.Uri
import android.os.Bundle
import android.provider.CalendarContract.Colors
import android.provider.MediaStore
import android.text.TextUtils
import android.widget.Button
import android.widget.EditText
import android.widget.ImageButton
import android.widget.ImageView
import android.widget.Toast
import android.widget.Toolbar
import android.widget.VideoView
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
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
import com.yaduvanshi_ashok_rd.likee.MainActivity
import com.yaduvanshi_ashok_rd.likee.R

class ReelActivity : AppCompatActivity() {
//    private lateinit var reelToolbar: Toolbar
    private lateinit var video: VideoView
    private lateinit var btnReel: Button
    private lateinit var reelCaption: EditText
    private lateinit var gallery: Button

    private  var myUrl=""
    private  var reelUri: Uri?=null
    private  var storagePostPictureRef: StorageReference?=null
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_reel)
        enableEdgeToEdge()
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main)) { v, insets ->
            val systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars())
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom)
            insets
        }
        requestedOrientation = ActivityInfo.SCREEN_ORIENTATION_PORTRAIT

        setSupportActionBar(findViewById(R.id.reelToolbar))
        supportActionBar?.setDisplayHomeAsUpEnabled(true)
        supportActionBar?.setDisplayHomeAsUpEnabled(true)
        video = findViewById(R.id.video)
        gallery = findViewById(R.id.gallery)
        btnReel = findViewById(R.id.btnReels)
        reelCaption = findViewById(R.id.reelCaption)



        storagePostPictureRef = FirebaseStorage.getInstance().reference.child("Post Reels")




        gallery.setOnClickListener {
            val galleryIntent =
                Intent(Intent.ACTION_PICK, MediaStore.Images.Media.EXTERNAL_CONTENT_URI)
            galleryIntent.type = "image/*" + "video/*"
            startActivityForResult(galleryIntent, 101)
        }
//
//    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
//        super.onActivityResult(requestCode, resultCode, data)
//        if (requestCode == CropImage.CROP_IMAGE_ACTIVITY_RESULT_ERROR_CODE) {
//            val result = CropImage.getActivityResult(data)
//            if (resultCode == Activity.RESULT_OK) {
//                imageUri= result.uri
//                picture_to_be_posted.setImageURI(imageUri)
//            } else if (resultCode == CropImage.CROP_IMAGE_ACTIVITY_RESULT_ERROR_CODE) {
//                val error = result.error
//            }
//        }

    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)
        if (requestCode == 101 && resultCode ==  RESULT_OK){

            val result = data
            reelUri = result?.data
            video.setVideoURI(reelUri)
            video.start()

//            video.resume()
//            video.stopPlayback()
        }
        btnReel.setOnClickListener {
            uploadPost()
        }
    }

    private fun uploadPost() {
        when
        {
            reelUri == null -> Toast.makeText(this, "Please select video first.", Toast.LENGTH_LONG).show()
            TextUtils.isEmpty(reelCaption.text.toString()) -> Toast.makeText(this, "Please write caption", Toast.LENGTH_LONG).show()

            else -> {
                val progressDialog = ProgressDialog(this)
                progressDialog.setTitle("Reel")
                progressDialog.setMessage("Please wait, we are posting..")
                progressDialog.show()

                val fileRef = storagePostPictureRef!!.child(System.currentTimeMillis().toString()+ ".Mp4")

                var uploadTask: StorageTask<*>
                uploadTask = fileRef.putFile(reelUri!!)

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



                        val ref = FirebaseDatabase.getInstance().reference.child("Reels")
                        val reelid=ref.push().key

                        val postMap = HashMap<String, Any>()

                        postMap["reelId"] = reelid!!
                        postMap["caption"] = reelCaption.text.toString()
                        postMap["publisher"] = FirebaseAuth.getInstance().currentUser!!.uid
                        postMap["reels"] = myUrl

                        ref.child(reelid).updateChildren(postMap)


                        val commentRef= FirebaseDatabase.getInstance().reference.child("Comment").child(reelid)
                        val commentMap = HashMap<String, Any>()
                        commentMap["publisher"] = FirebaseAuth.getInstance().currentUser!!.uid
                        commentMap["caption"] =  reelCaption.text.toString()

                        commentRef.push().setValue(commentMap)

                        Toast.makeText(this, "Uploaded successfully", Toast.LENGTH_LONG).show()

                        val intent = Intent(this@ReelActivity, MainActivity::class.java)
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