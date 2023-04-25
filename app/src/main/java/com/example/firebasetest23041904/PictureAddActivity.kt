package com.example.firebasetest23041904
import android.app.Activity
import android.content.Intent
import android.net.Uri
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.provider.MediaStore
import android.util.Log
import android.view.View
import android.widget.Toast
import androidx.activity.result.contract.ActivityResultContracts
import com.bumptech.glide.Glide
import com.example.firebasetest23041904.databinding.ActivityPictureAddBinding
import java.text.SimpleDateFormat
import java.util.*

class PictureAddActivity : AppCompatActivity() {
    val binding by lazy { ActivityPictureAddBinding.inflate(layoutInflater) }
    var imageUri: Uri? = null
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(binding.root)

        // 외부저장소 접근 허용했는지 응답받음
        val requestLauncher =
            registerForActivityResult(ActivityResultContracts.StartActivityForResult()) {
                if (it.resultCode == Activity.RESULT_OK) {
                    imageUri = it.data?.data
                    Glide.with(applicationContext).load(imageUri).into(binding.ivAddPicture)
                }
            }
        // 이벤트 접근
        binding.ivAddPicture.setOnClickListener {
            val intent = Intent(Intent.ACTION_PICK)
            intent.setDataAndType(MediaStore.Images.Media.EXTERNAL_CONTENT_URI, "image/*")
            requestLauncher.launch(intent)
        }
        // FireStore에 실시간 데이터베이스 저장과 storage에 사진저장
        binding.btnPictureSave.setOnClickListener {
            if (binding.ivAddPicture.drawable != null && binding.edtAddContent.text.isNotEmpty()) {
                if (!MyApplication.checkAuth()) {
                    // 실시간 데이터에 있는 테이블에 Picture에 push 하여 키값을 등록되는 것을 가져옴
                    binding.progressBar.visibility = View.VISIBLE
                    Toast.makeText(applicationContext, "인증이 안되었습니다.", Toast.LENGTH_SHORT).show()
                    val intent = Intent(applicationContext, MainActivity::class.java)
                    startActivity(intent)
                    finish()
                }
                val pictureDAO = PictureDAO()
                // 실시간 테이블 picture에 push를 해서 key값을 등록하고 가져옴
                val docID = pictureDAO.databaseReference?.push()?.key
                val email = MyApplication.email
                val content = binding.edtAddContent.text.toString().trim()
                val date = SimpleDateFormat("yy-MM-dd HH.mm.ss").format(Date())
                val pictureData = PictureData(docID, email, content, date)

                // 실시간 DB Picture 테이블에 클래스 저장
                pictureDAO.databaseReference?.child(docID!!)?.setValue(pictureData)?.addOnSuccessListener {
                    Log.e("PictureAddActivity","pictureData를 picture 테이블에 입력성공")
                    // 이미지를 스토리지에 저장
                    // Firebase storage에 이미지 업로드 진행
                    val pictureRef = pictureDAO.storage?.reference?.child("images/${docID}.png")
                    pictureRef?.putFile(imageUri!!)?.addOnSuccessListener {
                        binding.progressBar.visibility = View.INVISIBLE
                        Toast.makeText(applicationContext, "이미지 업로드 성공", Toast.LENGTH_SHORT).show()
                        Log.e("PictureAddActivity","이미지 업로드 성공")
                        finish()
                    }?.addOnFailureListener {
                        binding.progressBar.visibility = View.INVISIBLE
                        Toast.makeText(applicationContext, "이미지 업로드 실패", Toast.LENGTH_SHORT).show()
                        Log.e("PictureAddActivity","이미지 업로드 실패")
                    }
                    // 이미지를 스토리지 저장 끝
                }?.addOnFailureListener {
                    Log.e("PictureAddActivity","pictureData를 picture 테이블에 입력실패")
                }
            } else {
                binding.progressBar.visibility = View.INVISIBLE
                Toast.makeText(applicationContext, "사진과 내용을 입력해주세요.", Toast.LENGTH_SHORT).show()
            }
        }//end of btnPictureSave
    }
}