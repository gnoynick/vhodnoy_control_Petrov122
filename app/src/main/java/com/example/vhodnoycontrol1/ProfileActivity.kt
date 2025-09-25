package com.example.vhodnoycontrol1

import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import com.example.vhodnoycontrol1.databinding.ActivityProfileBinding

class ProfileActivity : AppCompatActivity() {
    private lateinit var binding: ActivityProfileBinding

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityProfileBinding.inflate(layoutInflater)
        setContentView(binding.root)

        val name = intent.getStringExtra("NAME")
        val email = intent.getStringExtra("EMAIL")

        binding.nameTextView.text = "Имя: $name"
        binding.emailTextView.text = "Email: $email"

        binding.backButton.setOnClickListener {
            finish()
        }
    }
}