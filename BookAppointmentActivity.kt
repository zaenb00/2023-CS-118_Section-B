package com.example.appointmentbookingapplication

import android.app.DatePickerDialog
import android.app.TimePickerDialog
import android.content.Intent
import android.os.Bundle
import android.widget.*
import androidx.appcompat.app.AlertDialog
import androidx.appcompat.app.AppCompatActivity
import java.util.Calendar

class BookAppointmentActivity : AppCompatActivity() {

    private lateinit var etName: EditText
    private lateinit var etPhone: EditText
    private lateinit var etEmail: EditText
    private lateinit var spinnerType: Spinner
    private lateinit var tvSelectedDate: TextView
    private lateinit var tvSelectedTime: TextView
    private lateinit var btnPickDate: Button
    private lateinit var btnPickTime: Button
    private lateinit var radioGroupGender: RadioGroup
    private lateinit var cbTerms: CheckBox
    private lateinit var btnConfirm: Button

    private var selectedDate = ""
    private var selectedTime = ""

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_book_appointment)

        // Initialize views
        etName          = findViewById(R.id.etName)
        etPhone         = findViewById(R.id.etPhone)
        etEmail         = findViewById(R.id.etEmail)
        spinnerType     = findViewById(R.id.spinnerType)
        tvSelectedDate  = findViewById(R.id.tvSelectedDate)
        tvSelectedTime  = findViewById(R.id.tvSelectedTime)
        btnPickDate     = findViewById(R.id.btnPickDate)
        btnPickTime     = findViewById(R.id.btnPickTime)
        radioGroupGender = findViewById(R.id.radioGroupGender)
        cbTerms         = findViewById(R.id.cbTerms)
        btnConfirm      = findViewById(R.id.btnConfirm)

        // Setup Spinner
        val adapter = ArrayAdapter.createFromResource(
            this,
            R.array.appointment_types,
            android.R.layout.simple_spinner_item
        )
        adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item)
        spinnerType.adapter = adapter

        // Date Picker — only allow future dates
        btnPickDate.setOnClickListener {
            val cal = Calendar.getInstance()
            val datePicker = DatePickerDialog(
                this,
                { _, year, month, day ->
                    // Check if selected date is in the past
                    val selected = Calendar.getInstance()
                    selected.set(year, month, day, 0, 0, 0)
                    val today = Calendar.getInstance()
                    today.set(today.get(Calendar.YEAR), today.get(Calendar.MONTH), today.get(Calendar.DAY_OF_MONTH), 0, 0, 0)

                    if (selected.before(today)) {
                        showAlert("Invalid Date", "Please select a future date for your appointment.")
                    } else {
                        selectedDate = "$day/${month + 1}/$year"
                        tvSelectedDate.text = "📅 Date: $selectedDate"
                        showToast("Date selected: $selectedDate")
                    }
                },
                cal.get(Calendar.YEAR),
                cal.get(Calendar.MONTH),
                cal.get(Calendar.DAY_OF_MONTH)
            )
            // Disable past dates
            datePicker.datePicker.minDate = System.currentTimeMillis() - 1000
            datePicker.show()
        }

        // Time Picker
        btnPickTime.setOnClickListener {
            val cal = Calendar.getInstance()
            TimePickerDialog(
                this,
                { _, hour, minute ->
                    val amPm = if (hour >= 12) "PM" else "AM"
                    val displayHour = if (hour % 12 == 0) 12 else hour % 12
                    selectedTime = String.format("%02d:%02d %s", displayHour, minute, amPm)
                    tvSelectedTime.text = "🕐 Time: $selectedTime"
                    showToast("Time selected: $selectedTime")
                },
                cal.get(Calendar.HOUR_OF_DAY),
                cal.get(Calendar.MINUTE),
                false
            ).show()
        }

        // Terms and Conditions — show dialog when clicked
        cbTerms.setOnCheckedChangeListener { _, isChecked ->
            if (isChecked) {
                showTermsDialog()
            }
        }

        // Confirm Button
        btnConfirm.setOnClickListener {
            if (validateForm()) {
                showConfirmationDialog()
            }
        }
    }

    // ─── Validation ────────────────────────────────────────────────

    private fun validateForm(): Boolean {
        val name  = etName.text.toString().trim()
        val phone = etPhone.text.toString().trim()
        val email = etEmail.text.toString().trim()

        // Name: not empty, only letters and spaces, min 3 chars
        if (name.isEmpty()) {
            etName.error = "Name is required"
            etName.requestFocus()
            showToast("Please enter your full name")
            return false
        }
        if (name.length < 3) {
            etName.error = "Name must be at least 3 characters"
            etName.requestFocus()
            showToast("Name is too short")
            return false
        }
        if (!name.matches(Regex("^[a-zA-Z ]+$"))) {
            etName.error = "Name must contain only letters"
            etName.requestFocus()
            showToast("Name should not contain numbers or special characters")
            return false
        }

        // Phone: not empty, only digits, exactly 11 digits
        if (phone.isEmpty()) {
            etPhone.error = "Phone number is required"
            etPhone.requestFocus()
            showToast("Please enter your phone number")
            return false
        }
        if (!phone.matches(Regex("^[0-9]+$"))) {
            etPhone.error = "Phone number must contain only digits"
            etPhone.requestFocus()
            showToast("Phone number should not contain letters or special characters")
            return false
        }
        if (phone.length != 11) {
            etPhone.error = "Phone number must be exactly 11 digits"
            etPhone.requestFocus()
            showToast("Enter a valid 11-digit phone number")
            return false
        }

        // Email: not empty, valid format
        if (email.isEmpty()) {
            etEmail.error = "Email is required"
            etEmail.requestFocus()
            showToast("Please enter your email address")
            return false
        }
        if (!android.util.Patterns.EMAIL_ADDRESS.matcher(email).matches()) {
            etEmail.error = "Enter a valid email address"
            etEmail.requestFocus()
            showToast("Invalid email format (e.g. name@example.com)")
            return false
        }

        // Appointment type
        if (spinnerType.selectedItemPosition == 0) {
            showToast("Please select an appointment type")
            showAlert("Missing Field", "Please select an appointment type from the dropdown.")
            return false
        }

        // Date
        if (selectedDate.isEmpty()) {
            showToast("Please select an appointment date")
            showAlert("Missing Field", "Please pick a date for your appointment.")
            return false
        }

        // Time
        if (selectedTime.isEmpty()) {
            showToast("Please select an appointment time")
            showAlert("Missing Field", "Please pick a time for your appointment.")
            return false
        }

        // Gender
        if (radioGroupGender.checkedRadioButtonId == -1) {
            showToast("Please select your gender")
            showAlert("Missing Field", "Please select your gender to continue.")
            return false
        }

        // Terms and Conditions
        if (!cbTerms.isChecked) {
            showToast("Please accept the Terms and Conditions")
            showAlert("Terms & Conditions", "You must accept the Terms and Conditions before confirming your booking.")
            return false
        }

        return true
    }

    // ─── Dialogs ───────────────────────────────────────────────────

    // Show terms and conditions text in a dialog
    private fun showTermsDialog() {
        AlertDialog.Builder(this)
            .setTitle("Terms and Conditions")
            .setMessage(
                "By booking an appointment you agree to:\n\n" +
                        "1. Arrive 10 minutes before your appointment time.\n\n" +
                        "2. Cancellations must be made at least 24 hours in advance.\n\n" +
                        "3. Your personal data will be used only for appointment purposes.\n\n" +
                        "4. The clinic reserves the right to reschedule appointments if necessary.\n\n" +
                        "Do you accept these terms?"
            )
            .setPositiveButton("Accept") { dialog, _ ->
                cbTerms.isChecked = true
                showToast("Terms and Conditions accepted")
                dialog.dismiss()
            }
            .setNegativeButton("Decline") { dialog, _ ->
                cbTerms.isChecked = false
                showToast("You must accept the Terms and Conditions to proceed")
                dialog.dismiss()
            }
            .setCancelable(false)
            .show()
    }

    // Show booking summary confirmation dialog before navigating
    private fun showConfirmationDialog() {
        val selectedGender = findViewById<RadioButton>(radioGroupGender.checkedRadioButtonId)

        AlertDialog.Builder(this)
            .setTitle("Confirm Booking")
            .setMessage(
                "Please review your details:\n\n" +
                        "👤 Name:  ${etName.text.toString().trim()}\n" +
                        "📞 Phone: ${etPhone.text.toString().trim()}\n" +
                        "📧 Email: ${etEmail.text.toString().trim()}\n" +
                        "🏥 Type:  ${spinnerType.selectedItem}\n" +
                        "📅 Date:  $selectedDate\n" +
                        "🕐 Time:  $selectedTime\n" +
                        "⚧ Gender: ${selectedGender.text}\n\n" +
                        "Are you sure you want to confirm this booking?"
            )
            .setPositiveButton("Yes, Confirm") { dialog, _ ->
                showToast("Appointment booked successfully!")
                dialog.dismiss()
                navigateToConfirmation(selectedGender.text.toString())
            }
            .setNegativeButton("Go Back") { dialog, _ ->
                showToast("You can edit your details")
                dialog.dismiss()
            }
            .setCancelable(false)
            .show()
    }

    // ─── Helpers ───────────────────────────────────────────────────

    private fun showToast(message: String) {
        Toast.makeText(this, message, Toast.LENGTH_SHORT).show()
    }

    private fun showAlert(title: String, message: String) {
        AlertDialog.Builder(this)
            .setTitle(title)
            .setMessage(message)
            .setPositiveButton("OK") { dialog, _ -> dialog.dismiss() }
            .show()
    }

    private fun navigateToConfirmation(gender: String) {
        val intent = Intent(this, ConfirmationActivity::class.java)
        intent.putExtra("name",   etName.text.toString().trim())
        intent.putExtra("phone",  etPhone.text.toString().trim())
        intent.putExtra("email",  etEmail.text.toString().trim())
        intent.putExtra("type",   spinnerType.selectedItem.toString())
        intent.putExtra("date",   selectedDate)
        intent.putExtra("time",   selectedTime)
        intent.putExtra("gender", gender)
        startActivity(intent)
    }
}