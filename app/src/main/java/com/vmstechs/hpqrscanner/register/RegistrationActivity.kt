package com.vmstechs.hpqrscanner.register

import android.content.Intent
import android.graphics.Bitmap
import android.graphics.BitmapFactory
import android.net.Uri
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import android.view.View
import android.widget.AdapterView
import android.widget.Toast
import androidx.core.view.isVisible
import androidx.lifecycle.ViewModelProvider
import com.squareup.picasso.Picasso
import com.vmstechs.hpqrscanner.R
import com.vmstechs.hpqrscanner.camera.CameraActivity
import com.vmstechs.hpqrscanner.databinding.ActivityRegistrationBinding
import com.vmstechs.hpqrscanner.stateslist.MyStateAdapter
import com.vmstechs.hpqrscanner.stateslist.StateList
import com.vmstechs.hpqrscanner.time_slot_list.TimeSlotAdapter
import com.vmstechs.hpqrscanner.time_slot_list.TimeSlotList
import java.io.File
import java.io.FileOutputStream

class RegistrationActivity : AppCompatActivity() {
    private lateinit var registrationBinding: ActivityRegistrationBinding
    private lateinit var registrationViewModel: RegistrationViewModel

    private var citiesList = StateList.stateList
    var selectedCity: String? = null

    private var timeSlotList = TimeSlotList.timeSlotList
    var selectedTimeSlot: String? = null

    companion object {
        var profileImageUri: Uri? = null
    }


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        registrationBinding = ActivityRegistrationBinding.inflate(layoutInflater)
        setContentView(registrationBinding.root)
        registrationViewModel = ViewModelProvider(this)[RegistrationViewModel::class.java]
        observeViewModel()
        setStateSpinner()
        setTimeSlotSpinner()
        // val resourceId = R.drawable.profile_placeholder  // Replace with your actual resource ID
        // profileImageUri = Uri.parse("android.resource://${this.packageName}/$resourceId")
        /*val dummyImage = "file:///storage/emulated/0/Android/data/com.vmstechs.hpqrscanner/files/Pictures/20240916_104756.jpg"
        profileImageUri = Uri.parse(dummyImage)*/

        registrationBinding.profileImage.setOnClickListener {
            startActivity(Intent(this, CameraActivity::class.java))
        }

        registrationBinding.captureImage.setOnClickListener {
            startActivity(Intent(this, CameraActivity::class.java))
        }

        registrationBinding.txtSubmit.setOnClickListener {
            Log.d("MVVM", "Profile Image: $profileImageUri")
            val profileFile = getProfileImage(profileImageUri)
            registrationViewModel.refresh(
                this@RegistrationActivity,
                registrationBinding.edtUserName.text.toString(),
                registrationBinding.edtCompanyName.text.toString(),
                registrationBinding.edtDesignation.text.toString(),
                registrationBinding.edtEmail.text.toString(),
                selectedCity!!,
                selectedTimeSlot!!,
                getProfileImage(profileImageUri)
                //profileImageUri.toString()
            )
        }

    }

    private fun getProfileImage(profileImageUri: Uri?): File {
        return if (profileImageUri == null) {
            Log.d("MVVM", "Is Null")
            // Use dummy image from resources
            val dummyBitmap = BitmapFactory.decodeResource(this.resources, R.drawable.profile_placeholder)
            val dummyFile = File(this.cacheDir, "dummy_image.png")
            saveBitmapToFile(dummyBitmap, dummyFile)
            dummyFile
        } else {
            Log.d("MVVM", "Not Null")
            File(profileImageUri.path ?: "")
        }
    }

    private fun observeViewModel() {
        registrationViewModel.registerUserResponse.observe(this) {
            profileImageUri = null
            Toast.makeText(this, it.message, Toast.LENGTH_LONG).show()
            registrationViewModel.refreshUserJoinConference(it.data.id)

        }
        registrationViewModel.joinConferenceResponse.observe(this) {
            Toast.makeText(this, it.message, Toast.LENGTH_LONG).show()
            finish()
        }
        registrationViewModel.resultError.observe(this) {
            profileImageUri = null
            Toast.makeText(this, it, Toast.LENGTH_LONG).show()
            finish()
        }
        registrationViewModel.loading.observe(this) {
            registrationBinding.loaderRegistration.isVisible = it
        }
    }

    override fun onResume() {
        super.onResume()
        if (profileImageUri != null) {
            Picasso.get().load(profileImageUri).into(registrationBinding.profileImage)
        } else {
            Picasso.get().load(R.drawable.profile_placeholder)
                .into(registrationBinding.profileImage)
        }
    }

    private fun setStateSpinner() {

        registrationBinding.spinnerCity.adapter =
            MyStateAdapter(this@RegistrationActivity, citiesList)
        registrationBinding.spinnerCity.onItemSelectedListener =
            object : AdapterView.OnItemSelectedListener {
                override fun onItemSelected(
                    parent: AdapterView<*>?,
                    view: View?,
                    position: Int,
                    id: Long
                ) {
                    selectedCity = citiesList[position].state_name
                    // age = citiesList[position].age?.toInt() ?: 0

                    /*Toast.makeText(
                        this@SignUpActivity,
                        "Selected city : ${citiesList!![position].state_name}",
                        Toast.LENGTH_LONG
                    ).show()*/
                }

                override fun onNothingSelected(parent: AdapterView<*>?) {
                    Toast.makeText(
                        this@RegistrationActivity,
                        "Please select your city",
                        Toast.LENGTH_LONG
                    ).show()
                }
            }

    }

    private fun setTimeSlotSpinner() {

        registrationBinding.spinnerTimeSlot.adapter =
            TimeSlotAdapter(this@RegistrationActivity, timeSlotList)
        registrationBinding.spinnerTimeSlot.onItemSelectedListener =
            object : AdapterView.OnItemSelectedListener {
                override fun onItemSelected(
                    parent: AdapterView<*>?,
                    view: View?,
                    position: Int,
                    id: Long
                ) {
                    selectedTimeSlot = timeSlotList[position].timeSlotString
                    // age = citiesList[position].age?.toInt() ?: 0

                    /*Toast.makeText(
                        this@SignUpActivity,
                        "Selected city : ${citiesList!![position].state_name}",
                        Toast.LENGTH_LONG
                    ).show()*/
                }

                override fun onNothingSelected(parent: AdapterView<*>?) {
                    Toast.makeText(
                        this@RegistrationActivity,
                        "Please select your city",
                        Toast.LENGTH_LONG
                    ).show()
                }
            }

    }
    private fun saveBitmapToFile(bitmap: Bitmap, file: File) {
        val out = FileOutputStream(file)
        bitmap.compress(Bitmap.CompressFormat.PNG, 100, out)
        out.flush()
        out.close()
    }

}