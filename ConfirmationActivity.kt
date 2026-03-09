package com.example.appointmentbookingapplication

import android.content.Intent
import android.os.Bundle
import android.widget.Button
import android.widget.TextView
import androidx.appcompat.app.AppCompatActivity

class ConfirmationActivity : AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_confirmation)

        // Get views
        val tvName = findViewById<TextView>(R.id.tvName)
        val tvPhone = findViewById<TextView>(R.id.tvPhone)
        val tvEmail = findViewById<TextView>(R.id.tvEmail)
        val tvType = findViewById<TextView>(R.id.tvType)
        val tvDate = findViewById<TextView>(R.id.tvDate)
        val tvTime = findViewById<TextView>(R.id.tvTime)
        val tvGender = findViewById<TextView>(R.id.tvGender)
        val btnDone = findViewById<Button>(R.id.btnDone)

        // Display data received from BookAppointmentActivity
        tvName.text   = "👤 Full Name:          ${intent.getStringExtra("name")}"
        tvPhone.text  = "📞 Phone Number:    ${intent.getStringExtra("phone")}"
        tvEmail.text  = "📧 Email Address:     ${intent.getStringExtra("email")}"
        tvType.text   = "🏥 Appointment Type: ${intent.getStringExtra("type")}"
        tvDate.text   = "📅 Date:                    ${intent.getStringExtra("date")}"
        tvTime.text   = "🕐 Time:                    ${intent.getStringExtra("time")}"
        tvGender.text = "⚧ Gender:               ${intent.getStringExtra("gender")}"

        // Back to Home
        btnDone.setOnClickListener {
            val intent = Intent(this, MainActivity::class.java)
            intent.flags = Intent.FLAG_ACTIVITY_CLEAR_TOP or Intent.FLAG_ACTIVITY_NEW_TASK
            startActivity(intent)
            finish()
        }
    }
}