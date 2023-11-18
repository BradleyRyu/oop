package com.example.myprojectapplication

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import androidx.fragment.app.Fragment
import androidx.navigation.fragment.NavHostFragment
import androidx.navigation.ui.setupActionBarWithNavController
import androidx.navigation.ui.setupWithNavController
import com.example.myprojectapplication.databinding.ActivityMainBinding

class MainActivity : AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        val binding = ActivityMainBinding.inflate(layoutInflater)
        val navController = binding.frmFrag.getFragment<NavHostFragment>().navController
        // 제 컴퓨터에서는 액션 바가 설정이 안 되네요 ㅠㅠ.. 혹시 되는 분 부탁드립니다..!
        // setupActionBarWithNavController(navController)
        binding.bottomNav.setupWithNavController(navController)
        setContentView(binding.root)
    }

}