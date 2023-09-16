package com.minhoi.recipeapp.ui.ingredients

import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.databinding.DataBindingUtil
import androidx.fragment.app.activityViewModels
import androidx.recyclerview.widget.GridLayoutManager
import com.minhoi.recipeapp.R
import com.minhoi.recipeapp.adapter.recyclerview.SelectIngredientAdapter
import com.minhoi.recipeapp.databinding.FragmentIngredientFruitBinding
import com.minhoi.recipeapp.model.SelectedIngredientDto

class IngredientFruitFragment : Fragment() {
    private lateinit var binding : FragmentIngredientFruitBinding
    private lateinit var ingredientAdapter : SelectIngredientAdapter
    private val viewModel : IngredientViewModel by activityViewModels()
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        // Inflate the layout for this fragment
        binding = DataBindingUtil.inflate(inflater, R.layout.fragment_ingredient_fruit, container, false)
        ingredientAdapter = SelectIngredientAdapter(requireContext()) {
            //onClickListener
            val selectItem = SelectedIngredientDto(it.name, it.imagePath)
            viewModel.addIngredient(selectItem)
        }

        binding.fruitRv.apply {
            adapter = ingredientAdapter
            layoutManager = GridLayoutManager(requireContext(), 4)
        }

        viewModel.fruitList.observe(viewLifecycleOwner) {
            ingredientAdapter.setList(it)
        }

        return binding.root
    }

}