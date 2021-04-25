package net.engawapg.app.photogallery

import android.os.Bundle
import androidx.activity.result.launch
import androidx.appcompat.app.AppCompatActivity
import kotlinx.android.synthetic.main.activity_main.*
import net.engawapg.app.photogallery.gallery.PhotoGalleryActivity

class MainActivity : AppCompatActivity() {

    /* PhotoGalleryActivityの結果をURIで受け取る */
    private val launcher = registerForActivityResult( PhotoGalleryActivity.ResultContract()) {
        imageView.setImageURI(it)
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        button.setOnClickListener {
            launcher.launch()
        }
    }
}