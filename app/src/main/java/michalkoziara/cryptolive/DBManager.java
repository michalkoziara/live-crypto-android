package michalkoziara.cryptolive;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.SQLException;
import android.database.sqlite.SQLiteDatabase;

public class DBManager {

    private DatabaseHelper dbHelper;

    private Context context;

    private SQLiteDatabase database;

    public DBManager(Context c) {
        context = c;
    }

    public DBManager open() throws SQLException {
        dbHelper = new DatabaseHelper(context);
        database = dbHelper.getWritableDatabase();
        return this;
    }

    public void close() {
        dbHelper.close();
    }

    public void insert(String fullName, String symbol) {
        ContentValues contentValue = new ContentValues();
        contentValue.put(DatabaseHelper.FULL_NAME, fullName);
        contentValue.put(DatabaseHelper.SYMBOL, symbol);
        database.insert(DatabaseHelper.TABLE_NAME, null, contentValue);
    }

    public Cursor fetch() {
        String[] columns = new String[] { DatabaseHelper._ID, DatabaseHelper.FULL_NAME, DatabaseHelper.SYMBOL };
        Cursor cursor = database.query(DatabaseHelper.TABLE_NAME, columns, null, null, null, null, null);

        return cursor;
    }

    public int update(long _id, String fullName, String symbol) {
        ContentValues contentValues = new ContentValues();
        contentValues.put(DatabaseHelper.FULL_NAME, fullName);
        contentValues.put(DatabaseHelper.SYMBOL, symbol);
        int i = database.update(DatabaseHelper.TABLE_NAME, contentValues, DatabaseHelper._ID + " = " + _id, null);
        return i;
    }

    public void delete(long _id) {
        database.delete(DatabaseHelper.TABLE_NAME, DatabaseHelper._ID + "=" + _id, null);
    }

}