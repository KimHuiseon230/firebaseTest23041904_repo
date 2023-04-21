package com.example.firebasetest23041904

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import android.view.View
import android.widget.Toast
import androidx.activity.result.ActivityResultLauncher
import androidx.activity.result.contract.ActivityResultContracts
import com.example.firebasetest23041904.databinding.ActivityMainBinding

import com.google.android.gms.auth.api.signin.GoogleSignIn
import com.google.android.gms.auth.api.signin.GoogleSignInOptions
import com.google.android.gms.common.api.ApiException
import com.google.firebase.auth.GoogleAuthProvider

class MainActivity : AppCompatActivity(), View.OnClickListener {
    lateinit var binding: ActivityMainBinding
    lateinit var requestLauncher: ActivityResultLauncher<Intent>
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityMainBinding.inflate(layoutInflater)
        setContentView(binding.root)

        //구글로그인 요청을 하면 결과 값을 리턴 콜백처리
        requestLauncher =
            registerForActivityResult(ActivityResultContracts.StartActivityForResult()) {
                try {
                    // 구글로그인 결과내용
                    val task = GoogleSignIn.getSignedInAccountFromIntent(it.data)
                    //구글에서 받은 위임장
                    val account = task.getResult(ApiException::class.java)
                    //위임장을 가지고 인증서
                    val credential = GoogleAuthProvider.getCredential(account.idToken, null)
                    //구글 인증서를 통해서 파이어베이스 로그인
                    MyApplication.firebaseAuth.signInWithCredential(credential)
                        .addOnCompleteListener(this) { task ->
                            if (task.isSuccessful) {
                                MyApplication.email = account.email
                                binding.authMainTextView.text = "구글 로그인 성공"
                                Log.e("MainActivity", "구글 로그인 성공")
                                Toast.makeText(this@MainActivity, "구글 로그인 성공", Toast.LENGTH_SHORT)
                                    .show()
                                changeStateButton("구글 로그인")
                                val intent = Intent(this@MainActivity,SubActivity::class.java)
                                startActivity(intent)

                            //인텐트
                            } else {
                                binding.authMainTextView.text = "구글 로그인 실패"
                                Toast.makeText(this@MainActivity, "구글 로그인 실패", Toast.LENGTH_SHORT)
                                    .show()
                            }
                        }
                } catch (e: ApiException) {
                    //구글로그인 인증 API 예외발생
                    binding.authMainTextView.text = "구글로그인 인증 API 예외발생"
                    Toast.makeText(this@MainActivity, "구글 로그인 API 실패", Toast.LENGTH_SHORT).show()
                    Log.e("MainActivity", "${e.printStackTrace()}")
                }
            } //end of registerForActivityResult

        binding.registerBtn.setOnClickListener(this)
        binding.deleteBtn.setOnClickListener(this)
        binding.googleLoginBtn.setOnClickListener(this)
        binding.loginBtn.setOnClickListener(this)
        binding.logoutBtn.setOnClickListener(this)
        changeStateButton("처음")
    }

    override fun onClick(v: View?) {
        when (v?.id) {
            // 이메일, 비밀번호 회원가입
            R.id.registerBtn -> {
                val email = binding.authEmailEditView.text.toString()
                val password = binding.authPasswordEditView.text.toString()
                //파이어베이스에 이메일과 패스워드 통해서 회원가입 신청
                MyApplication.firebaseAuth.createUserWithEmailAndPassword(email, password)
                    .addOnCompleteListener(this) { task ->
                        binding.authEmailEditView.text.clear()
                        binding.authPasswordEditView.text.clear()
                        if (task.isSuccessful) {
                            //사용자메일로 이메일 검증상태 전송진행
                            MyApplication.firebaseAuth.currentUser?.sendEmailVerification()
                                ?.addOnCompleteListener { sendTask ->
                                    if (sendTask.isSuccessful) {
                                        Toast.makeText(
                                            this@MainActivity,
                                            "파이어베이스 회원가입 성공 전송메일 확인",
                                            Toast.LENGTH_SHORT
                                        ).show()
                                        binding.authMainTextView.text = "파이어베이스 가입 성공"
                                        changeStateButton("회원가입")
                                    } else {
                                        Toast.makeText(
                                            this@MainActivity,
                                            "파이어베이스 전송메일 발송 실패",
                                            Toast.LENGTH_SHORT
                                        ).show()
                                        binding.authMainTextView.text = "파이어베이스 가입 발송 실패"
                                        changeStateButton("처음")
                                    }
                                }
                        } else {
                            Toast.makeText(
                                this@MainActivity,
                                "파이어베이스 메일, 패스워드 인증 실패",
                                Toast.LENGTH_SHORT
                            ).show()
                            binding.authMainTextView.text = "파이어베이스 인증 실패"
                            changeStateButton("처음")
                        }
                    }
            }
            // 회원 삭제
            R.id.deleteBtn -> {
                val user = MyApplication.firebaseAuth.currentUser
                if (user != null) {
                    user.delete().continueWith { task ->
                        if (task.isSuccessful) {
                            binding.authMainTextView.text = "파이어베이스 로그인 삭제"
                            changeStateButton("회원탈퇴")
                        } else {
                            binding.authMainTextView.text = "파이어베이스 로그인 삭제 실패"
                        }
                    }
                }
            }
            //구글로그인
            R.id.googleLoginBtn -> {
                val gso = GoogleSignInOptions.Builder(GoogleSignInOptions.DEFAULT_SIGN_IN)
                    .requestIdToken(getString(R.string.default_web_client_id))
                    .requestEmail()
                    .build()
                val signIntent = GoogleSignIn.getClient(this, gso).signInIntent
                requestLauncher.launch(signIntent)
            }
            R.id.loginBtn -> {
                val email = binding.authEmailEditView.text.toString()
                val password = binding.authPasswordEditView.text.toString()
                MyApplication.firebaseAuth.signInWithEmailAndPassword(email, password)
                    .addOnCompleteListener(this) { task ->
                        binding.authEmailEditView.text.clear()
                        binding.authPasswordEditView.text.clear()
                        if (task.isSuccessful) {
                            if (MyApplication.checkAuth()) {
                                MyApplication.email = email
                                binding.authMainTextView.text = "파이어베이스 로그인 성공"
                                changeStateButton("로그인")
                                //인텐트
                            } else {
                                binding.authMainTextView.text = "파이어베이스 로그인 실패"
                            }
                        } else {
                            binding.authMainTextView.text = "파이어베이스 로그인 실패"
                        }
                    }
            }
            R.id.logoutBtn -> {
                MyApplication.firebaseAuth.signOut()
                MyApplication.email = null
                binding.authMainTextView.text = "로그아웃"
                changeStateButton("로그아웃")
            }
        }
    }
    private fun changeStateButton(s: String) {
        binding.registerBtn.visibility =View.VISIBLE
        binding.deleteBtn.visibility =View.VISIBLE
        binding.googleLoginBtn.visibility =View.VISIBLE
        binding.loginBtn.visibility =View.VISIBLE
        binding.logoutBtn.visibility =View.VISIBLE
        when(s){
            "처음","로그아웃"->{
                binding.deleteBtn.visibility =View.INVISIBLE
                binding.logoutBtn.visibility =View.INVISIBLE }
            "회원가입" ->{
             binding.registerBtn.visibility =View.INVISIBLE
             binding.logoutBtn.visibility =View.INVISIBLE }
            "구글로그인"->{
                binding.registerBtn.visibility =View.INVISIBLE
                binding.deleteBtn.visibility =View.INVISIBLE
                binding.googleLoginBtn.visibility =View.INVISIBLE
                binding.loginBtn.visibility =View.INVISIBLE }
            "로그인"-> {
                binding.registerBtn.visibility =View.INVISIBLE
                binding.googleLoginBtn.visibility =View.INVISIBLE
                binding.logoutBtn.visibility =View.INVISIBLE }
            "회원탈퇴"->{
                binding.deleteBtn.visibility =View.INVISIBLE
                binding.loginBtn.visibility =View.INVISIBLE
                binding.logoutBtn.visibility =View.INVISIBLE
            }
        }
    }
}