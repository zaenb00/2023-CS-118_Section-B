package com.example.appointmentbookingapplication

import android.content.Intent
import android.os.Bundle
import android.widget.Button
import androidx.appcompat.app.AppCompatActivity

class MainActivity : AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        val btnBookAppointment = findViewById<Button>(R.id.btnBookAppointment)

        btnBookAppointment.setOnClickListener {
            val intent = Intent(this, BookAppointmentActivity::class.java)
            startActivity(intent)
        }
    }
}