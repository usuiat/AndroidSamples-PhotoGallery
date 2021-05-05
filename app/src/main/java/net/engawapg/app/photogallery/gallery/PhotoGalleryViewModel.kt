package net.engawapg.app.photogallery.gallery

import android.app.Application
import android.content.ContentUris
import android.net.Uri
import android.provider.MediaStore
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.viewModelScope
import androidx.recyclerview.widget.DiffUtil
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import net.engawapg.app.util.Event

/* PhotoGalleryを構成するアイテム */
data class PhotoGalleryItem(
    val uri: Uri /* 現状はURIのみ保持する */
) {
    companion object {
        /* DiffUtilの定義 */
        val DIFF_UTIL = object: DiffUtil.ItemCallback<PhotoGalleryItem>() {
            override fun areContentsTheSame(oldItem: PhotoGalleryItem, newItem: PhotoGalleryItem)
                    : Boolean {
                return oldItem == newItem
            }

            override fun areItemsTheSame(oldItem: PhotoGalleryItem, newItem: PhotoGalleryItem)
                    : Boolean {
                return oldItem.uri == newItem.uri
            }
        }

    }
}

class PhotoGalleryViewModel(app: Application) : AndroidViewModel(app) {
    /* Permission承認されたかどうか */
    val isPermissionGranted = MutableLiveData<Boolean>().apply { value = false }
    /* Permission拒否されたかどうか */
    val isPermissionDenied = MutableLiveData<Boolean>().apply { value = false }
    /* 写真のリスト */
    val photoList = MutableLiveData<List<PhotoGalleryItem>>()
    /* 写真選択イベント */
    val onSelect = MutableLiveData<Event<Uri>>()

    /* 写真のリストを読み込む */
    fun loadPhotoList() {
        /* Scopeを使ってバックグラウンドで動作させる */
        viewModelScope.launch {
            photoList.value = queryPhoto()
        }
    }

    /* バックグラウンドでMediaStoreから写真を検索する */
    private suspend fun queryPhoto() : List<PhotoGalleryItem> {
        /* IO用のスレッドで動作させる */
        return withContext(Dispatchers.IO) {
            val list = mutableListOf<PhotoGalleryItem>()

            /* 読み込む列の指定 */
            val projection = arrayOf(
                    MediaStore.Images.Media._ID, /* ID : URI取得に必要 */
                    MediaStore.Images.Media.DATE_TAKEN  /* 撮影日時 */
            )
            val selection = null /* 行の絞り込みの指定。nullならすべての行を読み込む。*/
            val selectionArgs = null /* selectionの?を置き換える引数 */
            /* 並び順の指定 : 撮影日時の新しい順 */
            val sortOrder = "${MediaStore.Images.Media.DATE_TAKEN} DESC"

            getApplication<Application>().contentResolver.query(
                    MediaStore.Images.Media.EXTERNAL_CONTENT_URI,
                    projection, selection, selectionArgs, sortOrder
            )?.use { cursor -> /* cursorは、検索結果の各行の情報にアクセスするためのオブジェクト。*/
                /* 必要な情報が格納されている列番号を取得する。 */
                val idColumn = cursor.getColumnIndexOrThrow(MediaStore.Images.Media._ID)
                while (cursor.moveToNext()) { /* 順にカーソルを動かしながら、情報を取得していく。*/
                    val id = cursor.getLong(idColumn)
                    /* IDからURIを取得してリストに格納 */
                    val uri = ContentUris.withAppendedId(
                            MediaStore.Images.Media.EXTERNAL_CONTENT_URI, id)
                    list.add(PhotoGalleryItem(uri))
                }
            }

            list
        }
    }

    /* 指定したインデックスのPhotoGalleryItemを取得 */
    fun getPhotoItem(index: Int) = photoList.value?.getOrNull(index)

    /* 写真クリックイベント */
    fun onClick(item: PhotoGalleryItem) {
        /* URIをイベントとして渡す */
        onSelect.value = Event(item.uri)
    }
}