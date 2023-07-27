package com.minhoi.recipeapp

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.widget.ImageView
import android.widget.TextView
import android.widget.Toast
import androidx.databinding.DataBindingUtil
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.lifecycleScope
import com.bumptech.glide.Glide
import com.kakao.sdk.user.UserApiClient
import com.minhoi.recipeapp.api.Ref
import com.minhoi.recipeapp.databinding.ActivityRcpInfoBinding
import com.minhoi.recipeapp.model.RecipeDto
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch

class RcpInfoActivity : AppCompatActivity() {
    private lateinit var binding : ActivityRcpInfoBinding
    private lateinit var userId : String
    private lateinit var viewModel : RcpInfoViewModel
    private lateinit var recipeData : RecipeDto
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = DataBindingUtil.setContentView(this,R.layout.activity_rcp_info)
        viewModel = ViewModelProvider(this).get(RcpInfoViewModel::class.java)

        val intent = getIntent()

        val rcpSeq = intent.getStringExtra("rcpSeq")

        lifecycleScope.launch(Dispatchers.IO) {
            val recipeData = viewModel.getRecipe(rcpSeq!!)

            binding.apply {
                menuName.text = recipeData.rcp_NM
                menuIngredient.text = split(recipeData.rcp_PARTS_DTLS!!)

                Glide.with(this@RcpInfoActivity)
                    .load(recipeData.att_FILE_NO_MAIN)
                    .into(menuImage)

            }

        }


        val name = intent.getStringExtra("name")
        val ingredient = intent.getStringExtra("ingredient")
        val manual01 = intent.getStringExtra("manual01")
        val manual02 = intent.getStringExtra("manual02")
        val manual03 = intent.getStringExtra("manual03")

        val image01 = intent.getStringExtra("image01")
        val image02 = intent.getStringExtra("image02")
        val image03 = intent.getStringExtra("image03")

        val imageSrc = intent.getStringExtra("imageSrc")

        val menuName = findViewById<TextView>(R.id.menuName)
        menuName.text = name
        val ingredientText = findViewById<TextView>(R.id.menuIngredient)

        if (ingredient != null) {
            ingredientText.text = split(ingredient)
        }

        val back = findViewById<ImageView>(R.id.menuBackBtn)
        back.setOnClickListener {
            finish()
        }



        lifecycleScope.launch {
            val userId = viewModel.getUser()
            viewModel.isBookmark(userId, rcpSeq!!)

            binding.rcpBookmarkBtn.setOnClickListener {
                if(userId == ""){
                    Toast.makeText(this@RcpInfoActivity, "로그인 후 이용 가능합니다.", Toast.LENGTH_LONG).show()
                }
                else {
                    if(viewModel.isBookmarked.value == true) {
                        viewModel.deleteBookmark(userId, rcpSeq)
                    } else {
                        viewModel.setBookmark(userId, rcpSeq)
                    }
                }
            }
        }

        // isBookmarked 값 관찰하여 북마크 되어있으면 색칠, 아니면 색칠 안된 버튼으로 업데이트
        viewModel.isBookmarked.observe(this) {
            if(it){
                binding.rcpBookmarkBtn.setImageResource(R.drawable.bookmarked)
            } else {
                binding.rcpBookmarkBtn.setImageResource(R.drawable.bookmark)
            }
        }

    }

    private fun split(str : String) : String {
        val strSplit = str.split(",")
        val sb = StringBuilder()
        for( i in strSplit.indices) {
            sb.append(strSplit[i].trim() + "\n")
        }
        return sb.toString()
    }
}