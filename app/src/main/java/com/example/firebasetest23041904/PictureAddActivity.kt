package com.example.firebasetest23041904

import android.app.Activity
import android.content.Intent
import android.net.Uri
import android.os.Build.VERSION_CODES.P
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.provider.MediaStore
import android.provider.MediaStore.Audio.Media
import android.widget.Toast
import androidx.activity.result.contract.ActivityResultContracts
import com.bumptech.glide.Glide
import com.example.firebasetest23041904.databinding.ActivityPictureAddBinding
import java.text.SimpleDateFormat
import java.util.*

class PictureAddActivity : AppCompatActivity() {
    lateinit var binding: ActivityPictureAddBinding
    var imgUri: Uri? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityPictureAddBinding.inflate(layoutInflater)
        setContentView(binding.root)
        // 외부 저장소 접근 허용 여부 응답
        val requestLauncher = registerForActivityResult(
            ActivityResultContracts.StartActivityForResult()
        ) {
            if (it.resultCode == Activity.RESULT_OK) {
                imgUri = it.data?.data
                Glide.with(applicationContext).load(imgUri).into(binding.ivAddPicture)
            }
        }

        // 이벤트 접근
        binding.ivAddPicture.setOnClickListener {
            val intent = Intent(Intent.ACTION_PICK)
            intent.setDataAndType(MediaStore.Images.Media.EXTERNAL_CONTENT_URI, "image/*")
            requestLauncher.launch(intent)
        }

        // fireStore 실시간 데이터 저장과 Storage 사진 저장
        binding.btnPictureSave.setOnClickListener {
            if (binding.ivAddPicture.drawable != null && binding.edtAddContent.text.isEmpty()) {
                val pictureDAO = PictureDAO()
                // 실시간 데이터에 있는 테이블에 Picture에 push 하여 키값을 등록되는 것을 가져옴
                if (!MyApplication.checkAuth()) {
                    Toast.makeText(applicationContext, "인증되지 않았습니다.", Toast.LENGTH_SHORT).show()
                    val intent = Intent(applicationContext, MainActivity::class.java)
                    startActivity(intent)
                    finish()
                }
                val docID = pictureDAO.databaseReference?.push()?.key
                val email = MyApplication.email
                val content = binding.edtAddContent.text.toString().trim()
                val date = SimpleDateFormat("yy-MM-dd HH.mm.ss").format(Date())
                val pictureData = PictureData(docID,email,content,date)
                // firebase storage 이미지 업로드 진행
                pictureDAO.storage?.reference?.child("images/${docID}.png")
            }
        } //end of btnPictureSave
    }
}