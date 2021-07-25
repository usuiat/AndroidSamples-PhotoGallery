package net.engawapg.app.photogallery

import android.os.Bundle
import androidx.activity.result.launch
import androidx.appcompat.app.AppCompatActivity
import androidx.databinding.DataBindingUtil
import net.engawapg.app.photogallery.databinding.ActivityMainBinding
import net.engawapg.app.photogallery.gallery.PhotoGalleryActivity

class MainActivity : AppCompatActivity() {

    private lateinit var binding: ActivityMainBinding

    /* PhotoGalleryActivityの結果をURIで受け取る */
    private val launcher = registerForActivityResult( PhotoGalleryActivity.ResultContract()) {
        binding.imageView.setImageURI(it)
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = DataBindingUtil.setContentView(this, R.layout.activity_main)

        binding.button.setOnClickListener {
            launcher.launch()
        }
    }
}