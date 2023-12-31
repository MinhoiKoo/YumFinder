package com.minhoi.recipeapp.ui

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.widget.ImageView
import android.widget.Toast
import androidx.databinding.DataBindingUtil
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.lifecycleScope
import androidx.recyclerview.widget.DividerItemDecoration
import androidx.recyclerview.widget.LinearLayoutManager
import com.bumptech.glide.Glide
import com.minhoi.recipeapp.R
import com.minhoi.recipeapp.adapter.recyclerview.RecipeInfoListAdapter
import com.minhoi.recipeapp.databinding.ActivityRcpInfoBinding
import com.minhoi.recipeapp.model.InfoIngredientDto
import com.minhoi.recipeapp.model.RecipeCookingWayData
import com.minhoi.recipeapp.model.RecipeDto
import com.minhoi.recipeapp.ui.viewmodel.RcpInfoViewModel
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext

class RcpInfoActivity : AppCompatActivity() {
    private val TAG = RcpInfoActivity::class.simpleName
    private lateinit var binding : ActivityRcpInfoBinding
    private lateinit var viewModel : RcpInfoViewModel
    private lateinit var recipeData : RecipeDto
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = DataBindingUtil.setContentView(this, R.layout.activity_rcp_info)
        viewModel = ViewModelProvider(this).get(RcpInfoViewModel::class.java)

        val intent = getIntent()

        val rcpSeq = intent.getStringExtra("rcpSeq")
        val ingredientAdapter = RecipeInfoListAdapter(this)
        val cookingWayAdapter = RecipeInfoListAdapter(this)
        val cookingWayList = arrayListOf<RecipeCookingWayData>()

        binding.menuBackBtn.setOnClickListener {
            finish()
        }

        binding.infoIngredientRv.apply {
            adapter = ingredientAdapter
            layoutManager = LinearLayoutManager(this@RcpInfoActivity)
            val decoration = DividerItemDecoration(this@RcpInfoActivity, 1)
            addItemDecoration(decoration)
        }

        binding.cookingWayRv.apply {
            adapter = cookingWayAdapter
            layoutManager = LinearLayoutManager(this@RcpInfoActivity)
        }

        lifecycleScope.launch(Dispatchers.IO) {
            val recipeData = viewModel.getRecipe(rcpSeq!!)

            withContext(Dispatchers.Main) {
                binding.apply {
                    menuName.text = recipeData.rcp_NM
//                    foodKcal.text = "열랑 : ${recipeData.info_ENG}"
//                    foodCar.text = recipeData.info_CAR
//                    foodFat.text = recipeData.info_FAT
//                    foodNa.text = recipeData.info_NA
//                    foodProtein.text = recipeData.info_PRO
                    Glide.with(this@RcpInfoActivity)
                        .load(recipeData.att_FILE_NO_MAIN)
                        .into(menuImage)
                    val ingredientList = recipeData.rcp_PARTS_DTLS.split(",").map {InfoIngredientDto(it.trim())}
                    ingredientAdapter.setIngredients(ingredientList)
                }

                val manualList = recipeData.manual.split("xx")
                val imageList = recipeData.image.split(",")

                for (i in manualList.indices) {
                    cookingWayList.add(RecipeCookingWayData(manualList[i].trim(','), imageList[i].trim().trim(',')))
                }
                cookingWayAdapter.setWays(cookingWayList)
            }
        }

        val back = findViewById<ImageView>(R.id.menuBackBtn)
        back.setOnClickListener {
            finish()
        }

        lifecycleScope.launch {
            val userId = viewModel.getUser()
            viewModel.isBookmark(userId, rcpSeq!!)

            binding.rcpBookmarkBtn.setOnClickListener {
                when(userId){
                    "" -> Toast.makeText(this@RcpInfoActivity, "로그인 후 이용 가능합니다.", Toast.LENGTH_LONG).show()
                    else -> {
                        if(viewModel.isBookmarked.value == true) {
                            viewModel.deleteBookmark(userId, rcpSeq)
                        } else {
                            viewModel.setBookmark(userId, rcpSeq)
                        }
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

//    private fun split(str : String) : List<String> {
//        val strSplit = str.split(",")
//        val sb = StringBuilder()
//        for( i in strSplit.indices) {
//            sb.append(strSplit[i].trim() + "\n")
//        }
//        return sb.toString()
//    }
}