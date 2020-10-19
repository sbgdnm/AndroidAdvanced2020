package com.sbgdnm.yummyfood.ui

import android.content.Intent
import android.graphics.drawable.ShapeDrawable
import android.graphics.drawable.shapes.RectShape
import android.os.Bundle
import android.util.Log
import android.view.Menu
import android.view.MenuItem
import androidx.appcompat.widget.SearchView
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModelProvider
import androidx.recyclerview.widget.DividerItemDecoration
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.google.firebase.auth.FirebaseAuth
import com.sbgdnm.yummyfood.MainActivity
import com.sbgdnm.yummyfood.R
import com.sbgdnm.yummyfood.adapters.OnRecipeListener
import com.sbgdnm.yummyfood.adapters.RecipeRecyclerAdapter
import com.sbgdnm.yummyfood.data.model.Recipe
import com.sbgdnm.yummyfood.util.AppContants
import com.sbgdnm.yummyfood.viewmodels.RecipeListViewModel
import kotlinx.android.synthetic.main.activity_recipe_list.*

class RecipeListActivity : BaseActivity(), OnRecipeListener {
    private val TAG = "RecipeListActivity"
    private lateinit var auth: FirebaseAuth
    private lateinit var recipeListViewModel: RecipeListViewModel
    private lateinit var recyclerView: RecyclerView
    private lateinit var recipeRecyclerAdapter: RecipeRecyclerAdapter
    private lateinit var searchView: SearchView

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_recipe_list)
        recyclerView = findViewById(R.id.recipe_list)
        searchView = findViewById(R.id.search_view)
        auth = FirebaseAuth.getInstance()

        recipeListViewModel = ViewModelProvider(this).get(RecipeListViewModel::class.java)

        initRecyclerView()
        initSearchView()



        if (!recipeListViewModel.isViewingRecipes) {
            //display search categories
            displaySearchCategories()
        }
        setSupportActionBar(findViewById(R.id.toolbar))
    }

    override fun onBackPressed() {
        if (recipeListViewModel.onBackPressed()) {
            super.onBackPressed()
        } else {
            displaySearchCategories()
        }
    }

    override fun onCreateOptionsMenu(menu: Menu?): Boolean {
        menuInflater.inflate(R.menu.recipe_search_menu, menu)
        return super.onCreateOptionsMenu(menu)
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        if (item.itemId == R.id.action_category) {
            displaySearchCategories()
        }
        else if(item.itemId == R.id.action_out){
            if (auth.currentUser !=null) {
                auth.signOut()
                startActivity(Intent(this, MainActivity::class.java))
                finish()
            }

        }
        return super.onOptionsItemSelected(item)
    }

    private fun searchRecipe(query: String, page: Int) {
        recipeListViewModel.searchRecipes(query, page).observe(this, Observer {
            for (recipe: Recipe in it) {
                Log.d(TAG, "subscribeObservers recipe title: ${recipe.title}")
            }
            recipeRecyclerAdapter.setRecipesList(it)
        })
    }

    private fun initRecyclerView() {
        recipeRecyclerAdapter = RecipeRecyclerAdapter(this)
        recyclerView.adapter = recipeRecyclerAdapter
        recyclerView.layoutManager = LinearLayoutManager(this)
        recyclerView.addItemDecoration(
            DividerItemDecoration(
                this,
                DividerItemDecoration.VERTICAL
            ).also { deco ->
                with(ShapeDrawable(RectShape())) {
                    intrinsicHeight = (resources.displayMetrics.density * 12).toInt()
                    alpha = 0
                    deco.setDrawable(this)
                }
            })

        recyclerView.addOnScrollListener(object : RecyclerView.OnScrollListener() {
            override fun onScrollStateChanged(recyclerView: RecyclerView, newState: Int) {
                if (!recyclerView.canScrollVertically(1)) {
                    // search next page
                    searchNextPage()
                }
            }
        })
    }

    private fun searchNextPage() {
        recipeListViewModel.searchNextPage().observe(this, Observer {
            for (recipe: Recipe in it) {
                Log.d(TAG, "subscribeObservers recipe title: ${recipe.title}")
            }
            recipeRecyclerAdapter.setRecipesList(it)
        })
    }

    private fun initSearchView() {
        searchView.setOnQueryTextListener(object : SearchView.OnQueryTextListener {
            override fun onQueryTextSubmit(query: String?): Boolean {
                if (query != null) {
                    recipeRecyclerAdapter.displayLoading()
                    searchRecipe(query, 1)
                    searchView.clearFocus()
                }
                return false
            }

            override fun onQueryTextChange(newText: String?): Boolean {

                return false
            }

        })
    }

    override fun onRecipeClick(position: Int) {
        val intent = Intent(this, RecipeActivity::class.java)

        intent.putExtra(AppContants.RECIPE, recipeRecyclerAdapter.selectedRecipe(position))
        startActivity(intent)
    }

    override fun onCategoryClick(category: String?) {
        if (category != null) {
            categories.text="List Products"
            searchRecipe(category, 1)
            searchView.clearFocus()
        }
    }

    private fun displaySearchCategories() {
        recipeListViewModel.isViewingRecipes = false
        recipeRecyclerAdapter.displaySearchCategories()
    }
}