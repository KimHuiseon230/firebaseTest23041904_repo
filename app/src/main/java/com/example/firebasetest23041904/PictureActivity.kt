package com.example.firebasetest23041904

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import com.example.firebasetest23041904.databinding.ActivityPictureBinding

class PictureActivity : AppCompatActivity() {
    lateinit var binding: ActivityPictureBinding
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding =ActivityPictureBinding.inflate(layoutInflater)
        setContentView(binding.root)

        binding.pictureRegisterBtn.setOnClickListener {
            val intent = Intent(this@PictureActivity, PictureAddActivity::class.java)
            startActivity(intent)
        }
    }
}