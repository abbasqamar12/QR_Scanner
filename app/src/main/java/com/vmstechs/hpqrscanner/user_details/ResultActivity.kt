package com.vmstechs.hpqrscanner.user_details

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.os.Handler
import android.os.Looper
import android.util.Log
import android.widget.Toast
import androidx.lifecycle.ViewModelProvider
import com.vmstechs.hpqrscanner.databinding.ActivityResultBinding

class ResultActivity : AppCompatActivity() {
    private lateinit var resultBinding: ActivityResultBinding
    private lateinit var resultViewModel: ResultViewModel
    // private var userId: String = "1"


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        resultBinding = ActivityResultBinding.inflate(layoutInflater)
        setContentView(resultBinding.root)
        resultViewModel = ViewModelProvider(this)[ResultViewModel::class.java]
        val userId = intent.getStringExtra("RESPONSE")
        resultBinding.txtUserName.text = userId
        resultViewModel.refreshUserDetails(userId ?: "1")


        /* resultBinding.btnJoinConference.setOnClickListener {
             resultViewModel.refreshUserJoinConference(userId ?: "1")
         }*/

        resultBinding.btnBackToScan.setOnClickListener {
            finish()
            // resultViewModel.refreshAllUsers()
        }

        observeViewModel()

    }


    private fun observeViewModel() {
        resultViewModel.userDetailsResponse.observe(this) {
            setUserData(it.profile_data)

        }
        resultViewModel.joinConferenceResponse.observe(this) {
            Toast.makeText(this, it.message, Toast.LENGTH_LONG).show()
            navigateToHome()
        }
        resultViewModel.resultError.observe(this) {
            Toast.makeText(this, it, Toast.LENGTH_LONG).show()
            navigateToHome()
        }
        resultViewModel.loading.observe(this) {

        }

        resultViewModel.allUsersResponse.observe(this) {
            Log.d("MVVM", "All Users Response: $it")
        }

    }

    private fun navigateToHome() {
        Handler(Looper.getMainLooper()).postDelayed({
            finish()
        }, 5000)
    }

    private fun setUserData(profileData: ProfileData) {
        resultBinding.txtUserName.text = profileData.Full_Name
        resultBinding.txtDesignation.text = profileData.Designation
        resultBinding.txtCompanyName.text = profileData.Company_Name

        resultViewModel.refreshUserJoinConference(profileData.id.toString())

    }
}