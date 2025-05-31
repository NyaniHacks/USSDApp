package com.munene.ussdapp  // Use your actual package name

import android.content.Intent
import android.net.Uri
import android.os.Bundle
import android.widget.ArrayAdapter
import android.widget.Button
import android.widget.Spinner
import androidx.appcompat.app.AppCompatActivity
import android.Manifest
import android.content.pm.PackageManager
import androidx.core.app.ActivityCompat
import androidx.core.content.ContextCompat
import android.widget.Toast


class MainActivity : AppCompatActivity() {

    private val ussdCodes = listOf(
        "*144#" to "Safaricom Balance",
        "*544#" to "Safaricom Data",
        "*126#" to "Airtel Balance",
        "*555#" to "Telkom Balance"
        // Add more here
    )

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        val spinner: Spinner = findViewById(R.id.ussdSpinner)
        val dialButton: Button = findViewById(R.id.dialButton)

        val adapter = ArrayAdapter(
            this,
            android.R.layout.simple_spinner_dropdown_item,
            ussdCodes.map { it.second }
        )
        spinner.adapter = adapter

        dialButton.setOnClickListener {
            val selectedCode = ussdCodes[spinner.selectedItemPosition].first
            dialUssdCode(selectedCode)
        }
    }

    override fun onRequestPermissionsResult(
        requestCode: Int,
        permissions: Array<String>,
        grantResults: IntArray
    ) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults)
        if (requestCode == 1 && grantResults.isNotEmpty() && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
            // Retry the USSD code
            val spinner: Spinner = findViewById(R.id.ussdSpinner)
            val selectedCode = ussdCodes[spinner.selectedItemPosition].first
            dialUssdCode(selectedCode)
        } else {
            // Show a message
            Toast.makeText(this, "Permission denied to make phone calls", Toast.LENGTH_SHORT).show()
        }
    }

    private fun dialUssdCode(code: String) {
        val encodedHash = Uri.encode("#")
        val ussd = code.replace("#", encodedHash)
        val intent = Intent(Intent.ACTION_CALL, Uri.parse("tel:$ussd"))

        if (ContextCompat.checkSelfPermission(this, Manifest.permission.CALL_PHONE)
            != PackageManager.PERMISSION_GRANTED) {
            ActivityCompat.requestPermissions(this, arrayOf(Manifest.permission.CALL_PHONE), 1)
        } else {
            startActivity(intent)
        }
    }
}
