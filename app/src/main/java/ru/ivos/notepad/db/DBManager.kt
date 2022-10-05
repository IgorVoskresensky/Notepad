package ru.ivos.notepad.db

import android.annotation.SuppressLint
import android.content.ContentValues
import android.content.Context
import android.database.sqlite.SQLiteDatabase
import android.provider.BaseColumns
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext

class DBManager(val conext: Context) {

    val dbHelper = DBHelper(conext)
    var db: SQLiteDatabase? = null

    fun openDb(){
        db = dbHelper.writableDatabase
    }

    suspend fun insertToDb(title:String, content:String, url: String) = withContext(Dispatchers.IO){
        val values = ContentValues().apply {
            put(DBNameClass.COLUMN_NAME_TITLE, title)
            put(DBNameClass.COLUMN_NAME_CONTENT, content)
            put(DBNameClass.COLUMN_NAME_PHOTO_URL, url)
        }
        db?.insert(DBNameClass.TABLE_NAME, null, values)
    }

    suspend fun updateItem(title:String, content:String, url: String, id: Int) = withContext(Dispatchers.IO){
        val element = BaseColumns._ID + "=$id"
        val values = ContentValues().apply {
            put(DBNameClass.COLUMN_NAME_TITLE, title)
            put(DBNameClass.COLUMN_NAME_CONTENT, content)
            put(DBNameClass.COLUMN_NAME_PHOTO_URL, url)
        }
        db?.update(DBNameClass.TABLE_NAME, values, element, null)
    }

    fun removeItemFromDb(id: String) {
        val element = BaseColumns._ID + "=$id"
        db?.delete(DBNameClass.TABLE_NAME, element, null)
    }

    @SuppressLint("Range")
    suspend fun readDBData(searchText: String): ArrayList<ListItem> = withContext(Dispatchers.IO){
        val dataList = ArrayList<ListItem>()
        val selection = "${DBNameClass.COLUMN_NAME_TITLE} like ?"
        val cursor = db?.query(DBNameClass.TABLE_NAME, null, selection, arrayOf("%$searchText%"), null, null, null)
        with(cursor){
            while (this?.moveToNext()!!){
                val dataTitle = getString(getColumnIndex(DBNameClass.COLUMN_NAME_TITLE))
                val dataContent = getString(getColumnIndex(DBNameClass.COLUMN_NAME_CONTENT))
                val dataUrl = getString(getColumnIndex(DBNameClass.COLUMN_NAME_PHOTO_URL))
                val dataId = getInt(getColumnIndex(BaseColumns._ID))
                val item = ListItem()
                item.title = dataTitle
                item.content = dataContent
                item.url = dataUrl
                item.id = dataId
                dataList.add(item)
            }
        }
        cursor?.close()
        return@withContext dataList
    }

    fun closeDb(){
        dbHelper.close()
    }
}