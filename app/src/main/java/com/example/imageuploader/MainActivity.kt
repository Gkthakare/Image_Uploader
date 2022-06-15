package com.example.imageuploader

import android.app.Activity
import android.app.ProgressDialog
import android.content.Intent
import android.net.Uri
import android.os.Bundle
import android.widget.Toast
import androidx.activity.result.contract.ActivityResultContracts
import androidx.appcompat.app.AppCompatActivity
import com.example.imageuploader.databinding.ActivityMainBinding
import com.google.firebase.database.FirebaseDatabase
import com.google.firebase.storage.FirebaseStorage
import com.google.firebase.storage.StorageReference
import java.text.SimpleDateFormat
import java.util.*


class MainActivity : AppCompatActivity() {

    private lateinit var binding : ActivityMainBinding
    private lateinit var ImageUri : Uri
    private lateinit var storage : StorageReference
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityMainBinding.inflate(layoutInflater)
        setContentView(binding.root)

        binding.selectImage.setOnClickListener{
            selectImage()
        }
        binding.upload.setOnClickListener {
            uploadImage()
        }
    }

    private fun uploadImage() {
        val progressDialog = ProgressDialog(this)
        progressDialog.setMessage("Uploading File ....")
        progressDialog.setCancelable(false)
        progressDialog.show()

        val formatter = SimpleDateFormat("yyyy_MM_dd_HH_mm_ss", Locale.getDefault())
        val now = Date()
        val filename = formatter.format(now)
        storage= FirebaseStorage.getInstance().reference.child("image/$filename")

        storage.putFile(ImageUri).
                addOnSuccessListener {
                    binding.fireBaseImage.setImageURI(ImageUri)
                    Toast.makeText(this@MainActivity, "Successfully Uploaded", Toast.LENGTH_SHORT).show()
                }.addOnFailureListener{
            Toast.makeText(this@MainActivity, "Upload Failed", Toast.LENGTH_SHORT).show()
        }


    }

    private fun selectImage() {
        val intent = Intent()
        intent.type = "image/*"
        intent.action = Intent.ACTION_GET_CONTENT
        getResult.launch(intent)
    }
    private val getResult = registerForActivityResult(ActivityResultContracts.StartActivityForResult()) {
        if (it.resultCode == Activity.RESULT_OK){
            ImageUri = it.data?.data!!


            val imageName: StorageReference = storage.child("image" + ImageUri.lastPathSegment)
            imageName.putFile(ImageUri).addOnSuccessListener {
                imageName.downloadUrl.addOnSuccessListener { uri ->
                    val databaseReference = FirebaseDatabase.getInstance()
                        .getReferenceFromUrl("https://imageuploader-56abc-default-rtdb.firebaseio.com/")
                        .child("Images")

                    val hashMap: HashMap<String, String> = HashMap()
                    hashMap.put("imageUrl", uri.toString())
                    databaseReference.setValue(hashMap)
                }
            }
        }
        }
    }


