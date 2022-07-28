package tung.lab.firebaseloginemail.db

import android.content.ContentValues
import android.content.Context
import android.database.sqlite.SQLiteDatabase
import android.database.sqlite.SQLiteOpenHelper

class DBHelper(context: Context, factory: SQLiteDatabase.CursorFactory?) :
    SQLiteOpenHelper(context, DATABASE_NAME, factory, DATABASE_VERSION) {
    // below is the method for creating a database by a sqlite query
    override fun onCreate(db: SQLiteDatabase) {
        // below is a sqlite query, where column names
        // along with their data types is given
        val query = ("CREATE TABLE " + TABLE_USER + " ("
                + ID_COL + " INTEGER PRIMARY KEY, " +
                NAME_COL + " TEXT," +
                GENDER_COL + " INTEGER," +
                HEIGHT_COL + " INTEGER," +
                WEIGHT_COL + " INTEGER," +
                BIRTHDAY_COL + " TEXT" + ")")

        // we are calling sqlite
        // method for executing our query
        db.execSQL(query)
    }

    override fun onUpgrade(db: SQLiteDatabase, p1: Int, p2: Int) {
        // this method is to check if table already exists
        db.execSQL("DROP TABLE IF EXISTS " + TABLE_USER)
        onCreate(db)
    }

    // This method is for adding data in our database
    fun addUser(
        name: String?,
        gender: Int?,
        height: Int?,
        weight: Int?,
        birthday: String?
    ) {

        // below we are creating
        // a content values variable
        val values = ContentValues()

        // we are inserting our values
        // in the form of key-value pair
        values.put(NAME_COL, name)
        values.put(GENDER_COL, gender)
        values.put(HEIGHT_COL, height)
        values.put(WEIGHT_COL, weight)
        values.put(BIRTHDAY_COL, birthday)


        // here we are creating a
        // writable variable of
        // our database as we want to
        // insert value in our database
        val db = this.writableDatabase

        // all values are inserted into database
        db.insert(TABLE_USER, null, values)
        // at last we are
        // closing our database
        db.close()
    }

    // below method is to get
    // all data from our database
    open fun getUserDetails(): HashMap<String?, String?>? {
        val user = HashMap<String?, String?>()
        val selectQuery = "SELECT  * FROM $TABLE_USER"
        val db = this.readableDatabase
        val cursor = db.rawQuery(selectQuery, null)
        // Move to first row
        cursor.moveToFirst()
        if (cursor.count > 0) {
            user["id"] = cursor.getString(0)
            user["name"] = cursor.getString(1)
            user["gender"] = cursor.getString(2)
            user["height"] = cursor.getString(3)
            user["weight"] = cursor.getString(4)
            user["birthday"] = cursor.getString(5)
        }
        cursor.close()
        db.close()
        // return user
        return user
    }

    fun updateName(id: String, name : String?){
        val db = this.writableDatabase
        val values = ContentValues()
        values.put(NAME_COL, name)
        db.update(TABLE_USER, values, "id=?", arrayOf(id))
    }

    fun updateGender(id: String, gender : Int?){
        val db = this.writableDatabase
        val values = ContentValues()
        values.put(GENDER_COL, gender)
        db.update(TABLE_USER, values, "id=?", arrayOf(id))
    }

    fun updateHeight(id: String, height : Int?){
        val db = this.writableDatabase
        val values = ContentValues()
        values.put(HEIGHT_COL, height)
        db.update(TABLE_USER, values, "id=?", arrayOf(id))
    }

    fun updateWeight(id: String, weight : Int?){
        val db = this.writableDatabase
        val values = ContentValues()
        values.put(WEIGHT_COL, weight)
        db.update(TABLE_USER, values, "id=?", arrayOf(id))
    }

    fun updateBirthday(id: String, birthday : String?){
        val db = this.writableDatabase
        val values = ContentValues()
        values.put(BIRTHDAY_COL, birthday)
        db.update(TABLE_USER, values, "id=?", arrayOf(id))
    }


    fun deleteUser() {
        val db = this.writableDatabase
        db.execSQL("delete from $TABLE_USER")
    }

    companion object {
        // here we have defined variables for our database

        // below is variable for database name
        private val DATABASE_NAME = "SKIPPING_ROPE"

        // below is the variable for database version
        private val DATABASE_VERSION = 1

        // below is the variable for table name
        val TABLE_USER = "user_profile_table"

        // below is the variable for id column
        val ID_COL = "id"

        // below is the variable for name column
        val NAME_COL = "name"

        //gender column
        val GENDER_COL = "gender"

        //height column
        val HEIGHT_COL = "height"

        //weight column
        val WEIGHT_COL = "weight"

        // below is the variable for birthday column
        val BIRTHDAY_COL = "birthday"

        //image column
        val IMAGE_COL = "image"
    }
}