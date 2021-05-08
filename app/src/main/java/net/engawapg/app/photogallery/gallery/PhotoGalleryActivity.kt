package net.engawapg.app.photogallery.gallery

import android.content.Context
import android.content.Intent
import android.net.Uri
import android.os.Bundle
import androidx.activity.result.contract.ActivityResultContract
import androidx.appcompat.app.AppCompatActivity
import net.engawapg.app.photogallery.R
import net.engawapg.app.util.EventObserver
import org.koin.android.viewmodel.ext.android.viewModel

class PhotoGalleryActivity : AppCompatActivity() {

    private val viewModel: PhotoGalleryViewModel by viewModel()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_photo_gallery)
        if (savedInstanceState == null) {
            supportFragmentManager.beginTransaction()
                .replace(R.id.container, PhotoGalleryFragment.newInstance())
                .commitNow()
        }

        /* ViewModelでURIが選択されたイベントを受信し、Activityの結果としてセットする */
        viewModel.onSelect.observe(this, EventObserver {
            setResult(RESULT_OK, Intent().putExtra(INTENT_URI, it.toString()))
            finish()
        })
    }

    /* ActivityResultContractのカスタマイズ。このActivityのResultとしてURIを返す */
    class ResultContract: ActivityResultContract<Unit, Uri?>() {
        override fun createIntent(context: Context, input: Unit?) =
            Intent(context, PhotoGalleryActivity::class.java)

        override fun parseResult(resultCode: Int, intent: Intent?): Uri? {
            return if (resultCode == RESULT_OK) {
                intent?.getStringExtra(INTENT_URI)?.let {
                    Uri.parse(it)
                }
            } else {
                null
            }
        }
    }

    companion object {
        const val INTENT_URI = "Uri"
    }
}