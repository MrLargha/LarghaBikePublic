package ru.mrlargha.larghabike.presentation.ui.screens.bikesetup

import android.Manifest
import android.content.Context
import android.content.DialogInterface
import android.content.Intent
import android.content.pm.PackageManager
import android.net.Uri
import android.os.Bundle
import android.widget.Toast
import androidx.activity.result.contract.ActivityResultContracts
import androidx.activity.viewModels
import androidx.appcompat.app.AppCompatActivity
import androidx.core.content.FileProvider
import androidx.core.net.toFile
import androidx.core.net.toUri
import com.google.android.material.dialog.MaterialAlertDialogBuilder
import dagger.hilt.android.AndroidEntryPoint
import ru.mrlargha.larghabike.databinding.ActivityBikeSetupBinding
import ru.mrlargha.larghabike.presentation.ui.viewmodels.BikeSetupViewModel

@AndroidEntryPoint
class BikeSetupActivity : AppCompatActivity() {

    private lateinit var binding: ActivityBikeSetupBinding
    private val viewModel: BikeSetupViewModel by viewModels()
    private var imageUri: Uri? = null

    companion object {
        fun newIntent(context: Context): Intent {
            return Intent(context, BikeSetupActivity::class.java)
        }
    }

    private val takeImageLauncher =
        registerForActivityResult(ActivityResultContracts.TakePicture()) { success ->
            if (success) {
                binding.bikeImage.setImageURI(imageUri)
            }
        }

    private val chooseImageLauncher =
        registerForActivityResult(ActivityResultContracts.GetContent()) { uri ->
            if (uri == null) {
                return@registerForActivityResult
            }
            val stream = contentResolver.openInputStream(uri) ?: return@registerForActivityResult
            imageUri?.let {
                viewModel.copyStreamToUri(stream, it)
                binding.bikeImage.setImageURI(uri)
            }
        }

    private val requestPermissionsLauncher =
        registerForActivityResult(ActivityResultContracts.RequestPermission()) { granted ->
            if (granted) {
                showGetBikeImageDialog()
            } else {
                if (shouldShowRequestPermissionRationale(Manifest.permission.WRITE_EXTERNAL_STORAGE)) {
                    displayPermissionRationale()
                } else {
                    // Nothing to do with that, sad (
                    showNoPermissionsMessage()
                }
            }
        }

    private fun displayPermissionRationale() {
        MaterialAlertDialogBuilder(this).setTitle("App permissions")
            .setMessage("You should give permission to the app in order for it to access the photo of your bike")
            .setPositiveButton("OK") { _: DialogInterface, _: Int ->
                getImage()
            }
            .setNegativeButton("Cancel") { i: DialogInterface, _: Int ->
                i.dismiss()
            }
    }

    private fun getImage() {
        if (checkSelfPermission(Manifest.permission.WRITE_EXTERNAL_STORAGE) != PackageManager.PERMISSION_GRANTED) {
            requestPermissionsLauncher.launch(Manifest.permission.WRITE_EXTERNAL_STORAGE)
        } else {
            showGetBikeImageDialog()
        }
    }

    private fun showNoPermissionsMessage() {
        MaterialAlertDialogBuilder(this).setTitle("No permission")
            .setMessage("Without permission app can't set custom image for your bike. Default placeholder will be used instead")
            .create().show()
    }

    private fun showGetBikeImageDialog() {
        val builder = MaterialAlertDialogBuilder(this)
        builder.setTitle("Choose photo")
            .setItems(arrayOf("Take from camera", "Select from gallery"))
            { _: DialogInterface, i: Int ->
                imageUri = viewModel.getImageFile().toUri()
                when (i) {
                    0 -> {
                        imageUri?.toFile()?.let {
                            val uri = FileProvider.getUriForFile(
                                this,
                                "ru.mrlargha.larghabike.fileprovider",
                                it
                            )
                            takeImageLauncher.launch(uri)
                        }
                    }
                    1 -> chooseImageLauncher.launch("image/*")
                }
            }.create().show()
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityBikeSetupBinding.inflate(layoutInflater)
        setContentView(binding.root)

        binding.apply {
            bikeImage.setOnClickListener {
                getImage()
            }

            addButton.setOnClickListener {
                val newBikeName = bikeName.text.toString()
                try {
                    val wheelDiameter = wheelDiameter.text.toString().toFloat()
                    viewModel.addBike(newBikeName, wheelDiameter, imageUri)
                } catch (e: NumberFormatException) {
                    showError("Incorrect wheel diameter format")
                }

            }

            cancelButton.setOnClickListener {
                finish()
            }
        }

        viewModel.error.observe(this) {
            if (it != "")
                showError(it)
        }

        viewModel.isBikeAdded.observe(this) {
            if (it) {
                Toast.makeText(
                    this@BikeSetupActivity,
                    "Bike added successfully",
                    Toast.LENGTH_SHORT
                ).show()
                finish()
            }
        }
    }

    private fun showError(error: String) {
        Toast.makeText(this, error, Toast.LENGTH_SHORT).show()
    }
}