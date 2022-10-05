package ru.ivos.notepad.db

import android.provider.BaseColumns

object DBNameClass: BaseColumns {

    const val TABLE_NAME = "notepad"
    const val COLUMN_NAME_TITLE = "title"
    const val COLUMN_NAME_CONTENT = "content"
    const val COLUMN_NAME_PHOTO_URL = "photo_url"


    const val DATABASE_VERSION = 7
    const val DATABASE_NAME = "Notepad.db"

    const val CREATE_TABLE =
        "CREATE TABLE IF NOT EXISTS $TABLE_NAME (" +
                "${BaseColumns._ID} INTEGER PRIMARY KEY," +
                "$COLUMN_NAME_TITLE TEXT," +
                "$COLUMN_NAME_CONTENT TEXT," +
                "$COLUMN_NAME_PHOTO_URL TEXT)"

    const val SQL_DELETE_TABLE = "DROP TABLE IF EXISTS $TABLE_NAME"
}