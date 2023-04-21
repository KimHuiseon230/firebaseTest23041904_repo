package com.example.firebasetest23041904

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.view.View
import android.widget.Toast
import com.example.firebasetest23041904.databinding.ActivitySubBinding

class SubActivity : AppCompatActivity(), View.OnClickListener {
    lateinit var binding: ActivitySubBinding
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivitySubBinding.inflate(layoutInflater)
        setContentView(binding.root)
        binding.updateBtn.setOnClickListener(this)
        binding.listBtn.setOnClickListener(this)
    }

    override fun onClick(v: View?) {
        when (v?.id) {
            // firebase 데이터 저장
            R.id.update_btn -> {
                val userDao = UserDAO()
                val no = binding.noEdit.text.toString()
                val name = binding.nameEdit.text.toString()
                val age = binding.ageEdit.text.toString()
                val user = User("", no, name, age)
                userDao.fbInsert(user).addOnSuccessListener {
                    // 성공시 메세지
                    Toast.makeText(this, "파이어베이스에  user 등록 성공", Toast.LENGTH_SHORT).show()
                    binding.noEdit.text.clear()
                    binding.nameEdit.text.clear()
                    binding.ageEdit.text.clear()
                    binding.stateText.text ="파이어베이스 등록 성공"
                }.addOnFailureListener {
                    // 실패시 메세지
                    Toast.makeText(this, "파이어베이스에 user 등록 실패", Toast.LENGTH_SHORT).show()
                    binding.stateText.text ="파이어베이스 등록 실패"
                }
            }
            R.id.list_btn -> {
                val intent = Intent(this@SubActivity, ListActivity::class.java)
                startActivity(intent)
            }

        }
    }
}