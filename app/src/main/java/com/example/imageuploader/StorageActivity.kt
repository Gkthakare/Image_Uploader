package com.example.imageuploader

import android.content.Intent
import android.graphics.BitmapFactory
import android.os.Bundle
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import com.example.imageuploader.databinding.ActivityStorageBinding
import com.google.firebase.storage.FirebaseStorage
import java.io.File

class StorageActivity : AppCompatActivity() {

    private lateinit var binding: ActivityStorageBinding
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityStorageBinding.inflate(layoutInflater)
        setContentView(binding.root)


        binding.uploadImage.setOnClickListener {
            openActivity2()
        }

        binding.getImage.setOnClickListener {
            val imageName = binding.editText.text.toString()
            val storageRef = FirebaseStorage.getInstance().getReference("image/$imageName")

            val localFile = File.createTempFile("tempImage","jpg")
            storageRef.getFile(localFile).addOnSuccessListener{
                val bitmap = BitmapFactory.decodeFile(localFile.absolutePath)
                binding.imageView.setImageBitmap(bitmap)
            }.addOnFailureListener {
                Toast.makeText(this, "Failed To Retrieve The Image", Toast.LENGTH_SHORT).show()
            }
        }

    }

    private fun openActivity2() {
        val intent = Intent(this,MainActivity::class.java)
        startActivity(intent)
    }
}
