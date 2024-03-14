package com.example.androidessncial.database

import android.content.ContentValues
import android.content.Context
import android.database.Cursor
import android.database.sqlite.SQLiteDatabase
import android.database.sqlite.SQLiteOpenHelper
import com.example.androidessncial.server.ApiSendRetrofit
import java.util.ArrayList

class DatabaseHelper(context: Context) : SQLiteOpenHelper(
    context, DATABASE_NAME, null, DATABASE_VERSION
)
{
    companion object
    {
        private const val DATABASE_VERSION = 1
        private const val DATABASE_NAME = "LocationData.db"
        private const val TABLE_NAME = "location_data"
        private const val COLUMN_ID = "id"
        private const val COLUMN_LATITUDE = "latitude"
        private const val COLUMN_LONGITUDE = "longitude"
        private const val COLUMN_DATE = "datedmy"
        private const val COLUMN_TIME = "datetime"
    }

    override fun onCreate(db: SQLiteDatabase)
    {
        val createTable = ("CREATE TABLE " + TABLE_NAME + " (" +
                COLUMN_ID + " INTEGER PRIMARY KEY AUTOINCREMENT, " +
                COLUMN_LATITUDE + " REAL, " +
                COLUMN_LONGITUDE + " REAL, " +
                COLUMN_DATE + " TEXT, " +
                COLUMN_TIME + " TEXT)")
        db.execSQL(createTable)
    }

    override fun onUpgrade(db: SQLiteDatabase, oldVersion: Int, newVersion: Int)
    {
        db.execSQL("DROP TABLE IF EXISTS $TABLE_NAME")
        onCreate(db)
    }

    /*
    * Método para inserir dados na tabela location_data
    */
    fun insertItem(apiSendRetrofit: ApiSendRetrofit): Boolean
    {
        val db = this.writableDatabase
        val values = ContentValues()
        values.put(COLUMN_LATITUDE, apiSendRetrofit.latitude)
        values.put(COLUMN_LONGITUDE, apiSendRetrofit.longitude)
        values.put(COLUMN_DATE, apiSendRetrofit.datedmy)
        values.put(COLUMN_TIME, apiSendRetrofit.datetime)
        val result = db.insert(TABLE_NAME, null, values)
        db.close()
        return result != -1L
    }

    /*
    * Método para responder com os dados da tabela location_data
    */
    fun getAllItems(): List<ApiSendRetrofit>
    {
        val itemList = ArrayList<ApiSendRetrofit>()
        val db = this.writableDatabase
        val query = "SELECT * FROM $TABLE_NAME"
        val cursor: Cursor = db.rawQuery(query, null)

        if (cursor.moveToFirst())
        {
            //Estrutura de controle (loop do while).
            do
            {
                val latitude = cursor.getDouble(getColumnIndex(cursor, COLUMN_LATITUDE))
                val longitude = cursor.getDouble(getColumnIndex(cursor, COLUMN_LONGITUDE))
                val datedmy = cursor.getString(getColumnIndex(cursor, COLUMN_DATE))
                val datetime = cursor.getString(getColumnIndex(cursor, COLUMN_TIME))
                val apiSendRetrofit = ApiSendRetrofit(latitude, longitude, datedmy, datetime)
                itemList.add(apiSendRetrofit)
            } while (cursor.moveToNext())
        }
        cursor.close()
        /*
        * Retornando a lista toda dados contido no banco local
        */
        return itemList
    }

    /*
    * Método para excluir todos os dados da tabela location_data
    */
    fun deleteAllItems()
    {
        val db = this.writableDatabase
        db.delete(TABLE_NAME, null, null)
        db.close()
    }

    private fun getColumnIndex(cursor: Cursor, columnName: String): Int
    {
        return cursor.getColumnIndex(columnName)
    }
}