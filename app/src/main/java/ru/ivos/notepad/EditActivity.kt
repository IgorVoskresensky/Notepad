package ru.ivos.notepad

import android.app.Activity
import android.content.Intent
import android.net.Uri
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.view.View
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import ru.ivos.notepad.databinding.ActivityEditBinding
import ru.ivos.notepad.db.DBManager

class EditActivity : AppCompatActivity() {

    val photoRequestCode = 1
    var tmpPhotoUrl = "empty"
    val dbManager = DBManager(this)
    var id = 0
    var isEditState = false

    lateinit var binding: ActivityEditBinding

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityEditBinding.inflate(layoutInflater)
        setContentView(binding.root)
        getIntents()
    }

    override fun onResume() {
        super.onResume()
        dbManager.openDb()
    }

    override fun onDestroy() {
        super.onDestroy()
        dbManager.closeDb()
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)
        if (resultCode == Activity.RESULT_OK && requestCode == photoRequestCode) {
            binding.btnEditPhoto.setImageURI(data?.data)
            tmpPhotoUrl = data?.data.toString()
            contentResolver.takePersistableUriPermission(
                data?.data!!,
                Intent.FLAG_GRANT_READ_URI_PERMISSION
            )
        }
    }

    fun onClickAddPhoto(view: View) {
        binding.addPhotoLayout.visibility = View.VISIBLE
        binding.btnAddPhoto.visibility = View.GONE
    }

    fun onClickDeletePhoto(view: View) {
        binding.addPhotoLayout.visibility = View.GONE
        binding.btnAddPhoto.visibility = View.VISIBLE
        tmpPhotoUrl = "empty"
    }

    fun onClickEditPhoto(view: View) {

        val intent = Intent(Intent.ACTION_OPEN_DOCUMENT)
        intent.type = "image/*"

        startActivityForResult(intent, photoRequestCode)
    }

    fun onClickSave(view: View) {

        val title = binding.edTitle.text.toString()
        val content = binding.edContent.text.toString()

        if (!title.isEmpty() && !content.isEmpty()) {
            CoroutineScope(Dispatchers.Main).launch {
                if (isEditState) {
                    dbManager.updateItem(title, content, tmpPhotoUrl, id)
                } else {
                    dbManager.insertToDb(title, content, tmpPhotoUrl)
                }
                finish()
            }
        }
    }

    fun getIntents() {
        val i = intent

        if (i != null) {
            if (intent.getStringExtra(IntentConsts.INTENT_TITLE_KEY) != null) {
                binding.edTitle.setText(intent.getStringExtra(IntentConsts.INTENT_TITLE_KEY))
                binding.edContent.setText(intent.getStringExtra(IntentConsts.INTENT_CONTENT_KEY))
                tmpPhotoUrl = i.getStringExtra(IntentConsts.INTENT_URL_KEY)!!
                isEditState = true
                id = intent.getIntExtra(IntentConsts.INTENT_ID_KEY, 0)
                if (intent.getStringExtra(IntentConsts.INTENT_URL_KEY) != "empty") {
                    binding.addPhotoLayout.visibility = View.VISIBLE
                    binding.btnAddPhoto.visibility = View.GONE
                    binding.photo.setImageURI(Uri.parse(intent.getStringExtra(IntentConsts.INTENT_URL_KEY)))
                }
            }
        }
    }

}