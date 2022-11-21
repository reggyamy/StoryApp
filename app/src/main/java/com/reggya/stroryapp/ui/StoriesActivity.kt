package com.reggya.stroryapp.ui

import android.app.AlertDialog
import android.content.Intent
import android.content.Intent.ACTION_GET_CONTENT
import android.content.pm.PackageManager
import android.graphics.BitmapFactory
import android.graphics.Color
import android.graphics.drawable.ColorDrawable
import android.net.Uri
import android.os.Build
import android.os.Bundle
import android.provider.MediaStore
import android.util.Log
import android.view.View
import android.view.WindowInsets
import android.view.WindowManager
import android.widget.ImageView
import android.widget.Toast
import androidx.activity.result.contract.ActivityResultContracts
import androidx.appcompat.app.AppCompatActivity
import androidx.appcompat.widget.AppCompatButton
import androidx.appcompat.widget.PopupMenu
import androidx.core.app.ActivityCompat
import androidx.core.content.ContextCompat
import androidx.core.content.FileProvider
import androidx.lifecycle.ViewModelProvider
import androidx.recyclerview.widget.LinearLayoutManager
import com.reggya.stroryapp.R
import com.reggya.stroryapp.data.ApiResponseType.*
import com.reggya.stroryapp.data.StoryViewModel
import com.reggya.stroryapp.data.ViewModelFactory
import com.reggya.stroryapp.databinding.ActivityStoriesBinding
import com.reggya.stroryapp.utils.LoginPreference
import com.reggya.stroryapp.utils.Utils.createCustomTempFile
import com.reggya.stroryapp.utils.Utils.reduceFileImage
import com.reggya.stroryapp.utils.Utils.uriToFile
import java.io.File

class StoriesActivity : AppCompatActivity() {

    private lateinit var binding: ActivityStoriesBinding
    private lateinit var viewModel: StoryViewModel
    private lateinit var currentPhotoPath: String
    private var view: View? = null
    private var dialog: AlertDialog? = null

    private var getFile: File? = null

    companion object {
        private val REQUIRED_PERMISSIONS = arrayOf(android.Manifest.permission.CAMERA)
        private const val REQUEST_CODE_PERMISSIONS = 10
    }

    override fun onRequestPermissionsResult(
        requestCode: Int,
        permissions: Array<String>,
        grantResults: IntArray
    ) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults)
        if (requestCode == REQUEST_CODE_PERMISSIONS) {
            if (!allPermissionsGranted()) {
                Toast.makeText(
                    this,
                    "Tidak mendapatkan permission.",
                    Toast.LENGTH_SHORT
                ).show()
                finish()
            }
        }
    }

    private fun allPermissionsGranted() = REQUIRED_PERMISSIONS.all {
        ContextCompat.checkSelfPermission(baseContext, it) == PackageManager.PERMISSION_GRANTED
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityStoriesBinding.inflate(layoutInflater)
        setContentView(binding.root)

        setupView()

        viewModel = ViewModelProvider(this, ViewModelFactory.getInstance())[StoryViewModel::class.java]

        view = layoutInflater.inflate(R.layout.alert_dialog, null)
        dialog = AlertDialog.Builder(this).create()

        val name = LoginPreference(this).getName()
        binding.welcoming.text = String.format(getString(R.string.hai), name)

        if (!allPermissionsGranted()) {
            ActivityCompat.requestPermissions(
                this,
                REQUIRED_PERMISSIONS,
                REQUEST_CODE_PERMISSIONS
            )
        }

        getAllStory()

        binding.addStory.setOnClickListener {
            dialog?.setView(view)
            val btCamera = view?.findViewById<AppCompatButton>(R.id.bt_camera)
            val btGallery = view?.findViewById<AppCompatButton>(R.id.bt_gallery)
            val btPost = view?.findViewById<AppCompatButton>(R.id.bt_post)
            val image = view?.findViewById<ImageView>(R.id.image)
            val desc = view?.findViewById<CustomEditText>(R.id.desc)

            image?.setImageResource(R.drawable.ic_round_photo_24)
            desc?.text?.clear()
            dialog?.window?.setBackgroundDrawable(ColorDrawable(Color.TRANSPARENT))
            btCamera?.setOnClickListener{startTakePhoto()}
            btGallery?.setOnClickListener { startGallery() }
            btPost?.setOnClickListener { uploadImage()}
            dialog?.show()
        }

        binding.btMenu.setOnClickListener{
            val popupMenu = PopupMenu(this, binding.btMenu)
            popupMenu.menuInflater.inflate(R.menu.menu, popupMenu.menu)
            popupMenu.setOnMenuItemClickListener {item ->
                if (item.itemId == R.id.item_logout){
                    val preference = LoginPreference(this)
                    preference.removeAll()
                    finish()
                    Log.e("share", preference.getToken().toString())
                    val intent = Intent(this, RegisterActivity::class.java)
                    startActivity(intent)
                    return@setOnMenuItemClickListener true
                }
                return@setOnMenuItemClickListener false
            }
            popupMenu.show()
        }

    }

    private fun setupView() {
        @Suppress("DEPRECATION")
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.R) {
            window.insetsController?.hide(WindowInsets.Type.statusBars())
        } else {
            window.setFlags(
                WindowManager.LayoutParams.FLAG_FULLSCREEN,
                WindowManager.LayoutParams.FLAG_FULLSCREEN
            )
        }
        supportActionBar?.hide()
    }

    private fun getAllStory() {
        viewModel.getStories(LoginPreference(this).getToken().toString()).observe(this){
            when(it.type){
                ERROR -> Toast.makeText(this, it.data?.message, Toast.LENGTH_SHORT).show()
                SUCCESS -> {
                    val storiesAdapter = StoriesAdapter()
                    storiesAdapter.setNewData(it.data?.listStory)
                    binding.rvStories.apply {
                        adapter = storiesAdapter
                        layoutManager = LinearLayoutManager(this@StoriesActivity)
                        setHasFixedSize(true)
                    }
                    storiesAdapter.itemOnClick = { story ->
                        story?.let { it1 -> DetailStory.showDetail(this, it1) }
                    }
                }
                EMPTY -> Toast.makeText(this, it.data?.message, Toast.LENGTH_SHORT).show()
            }
        }
    }

    private fun startGallery() {
        val intent = Intent()
        intent.action = ACTION_GET_CONTENT
        intent.type = "image/*"
        val chooser = Intent.createChooser(intent, "Choose a Picture")
        launcherIntentGallery.launch(chooser)
    }

    private fun startTakePhoto() {
        val intent = Intent(MediaStore.ACTION_IMAGE_CAPTURE)
        intent.resolveActivity(packageManager)

        createCustomTempFile(application).also {
            val photoURI: Uri = FileProvider.getUriForFile(
                this,
                "com.reggya.stroryapp",
                it
            )
            currentPhotoPath = it.absolutePath
            intent.putExtra(MediaStore.EXTRA_OUTPUT, photoURI)
            launcherIntentCamera.launch(intent)
        }
    }

    private val launcherIntentCamera = registerForActivityResult(
        ActivityResultContracts.StartActivityForResult()
    ) {
        if (it.resultCode == RESULT_OK) {
            val myFile = File(currentPhotoPath)
            getFile = myFile

            val result = BitmapFactory.decodeFile(getFile?.path)

            val image = view?.findViewById<ImageView>(R.id.image)
            image?.setImageBitmap(result)
        }
    }

    private val launcherIntentGallery = registerForActivityResult(
        ActivityResultContracts.StartActivityForResult()
    ) { result ->
        if (result.resultCode == RESULT_OK) {
            val selectedImg: Uri = result.data?.data as Uri

            val myFile = uriToFile(selectedImg, this)

            getFile = myFile

            val image = view?.findViewById<ImageView>(R.id.image)
            image?.setImageURI(selectedImg)
        }
    }

    private fun uploadImage() {
        if (getFile != null) {
            val file = reduceFileImage(getFile as File)
            val desc = view?.findViewById<CustomEditText>(R.id.desc)
            val description = desc?.text.toString()


            val progressBar = ProgressDialog(this)
            progressBar.showProgressBar()
            viewModel.addStory(LoginPreference(this).getToken().toString(), file, description).observe(this) {
                when (it.type) {
                    SUCCESS -> {
                        progressBar.dismissProgressBar()
                        Toast.makeText(this, it.data?.message, Toast.LENGTH_SHORT).show()
                        dialog?.dismiss()
                        getAllStory()
                    }
                    ERROR -> {
                        progressBar.dismissProgressBar()
                        Toast.makeText(this, it.message, Toast.LENGTH_SHORT).show()
                    }
                    else -> {
                        progressBar.dismissProgressBar()
                        Toast.makeText(this, it.data?.message, Toast.LENGTH_SHORT).show()
                    }
                }
            }
        } else {
            Toast.makeText(this, "Silakan masukkan berkas gambar terlebih dahulu.", Toast.LENGTH_SHORT).show()
        }
    }
}