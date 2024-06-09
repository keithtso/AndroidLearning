package com.example.sqldatabasepractice;

import android.content.ContentProvider;
import android.content.ContentValues;
import android.content.UriMatcher;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.net.Uri;

import java.net.URI;

public class DataBaseProvider extends ContentProvider {

    public static final int BOOK_DIR = 0;

    public static final int BOOK_ITEM = 1;

    public static final int CATEGORY_DIR = 2;

    public static final int CATEGORY_ITEM = 3;

    private static final String AUTHORITY = "com.example.sqldatabasepractice.provider";

    private static UriMatcher uriMatcher;

    private MySQLHelper sqlHelper;

    static {
        uriMatcher = new UriMatcher(UriMatcher.NO_MATCH);
        uriMatcher.addURI(AUTHORITY,"book", BOOK_DIR);
        uriMatcher.addURI(AUTHORITY,"book/#", BOOK_ITEM);
        uriMatcher.addURI(AUTHORITY,"category", CATEGORY_DIR);
        uriMatcher.addURI(AUTHORITY,"category/#", CATEGORY_ITEM);
    }

    public DataBaseProvider() {
    }

    @Override
    public int delete(Uri uri, String selection, String[] selectionArgs) {
        SQLiteDatabase db = sqlHelper.getWritableDatabase();
        int deletedRows = 0;
        switch (uriMatcher.match(uri)) {
            case BOOK_DIR:
                deletedRows = db.delete("Book",selection,selectionArgs);
                break;
            case BOOK_ITEM:
                String bookID = uri.getPathSegments().get(1);
                deletedRows = db.delete("Book","id=?",new String[] {bookID});
                break;
            case CATEGORY_DIR:
                deletedRows = db.delete("Category",selection,selectionArgs);
                break;
            case  CATEGORY_ITEM:
                String categoryID = uri.getPathSegments().get(1);
                deletedRows = db.delete("Category","id=?",new String[] {categoryID});
                break;
            default:
                break;
        }
        return deletedRows;
    }

    @Override
    public String getType(Uri uri) {
        String cursorDirStr = "vnd.android.cursor.dir";
        String bookProviderStr = "vnd." + getContext().getPackageName() +".book";
        String cursorItemStr = "vnd.android.cursor.item";
        String categoryProviderStr = "vnd." + getContext().getPackageName() +".category";

        switch (uriMatcher.match(uri)) {
            case BOOK_DIR:
                return cursorDirStr + "/" + bookProviderStr;
            case BOOK_ITEM:
                return cursorItemStr + "/" + bookProviderStr;
            case CATEGORY_DIR:
                return cursorDirStr + "/" + categoryProviderStr;
            case CATEGORY_ITEM:
                return cursorItemStr + "/" + categoryProviderStr;
            default:
                break;
        }

        return null;
    }

    @Override
    public Uri insert(Uri uri, ContentValues values) {
        SQLiteDatabase db = sqlHelper.getWritableDatabase();
        Uri uriResult = null;
        switch (uriMatcher.match(uri)) {
            case BOOK_DIR:
            case BOOK_ITEM:
                long newBookID = db.insert("Book",null,values);
                uriResult = Uri.parse("content://"+AUTHORITY+"/book/" + newBookID);
                break;

            case CATEGORY_DIR:
            case CATEGORY_ITEM:
                long newCategoryID = db.insert("Category",null,values);
                uriResult = Uri.parse("content://"+AUTHORITY+"/categroy/"+newCategoryID);
                break;
            default:
                break;
        }
        return uriResult;
    }

    @Override
    public boolean onCreate() {
        sqlHelper = new MySQLHelper(getContext(),"BookStore.db", null, 2);
        return true;
    }

    @Override
    public Cursor query(Uri uri, String[] projection, String selection,
                        String[] selectionArgs, String sortOrder) {
        SQLiteDatabase db = sqlHelper.getReadableDatabase();
        Cursor cursor = null;
        switch (uriMatcher.match(uri)) {
            case BOOK_DIR:
                cursor = db.query("Book",projection,selection,selectionArgs,null,null,sortOrder);
                return cursor;

            case BOOK_ITEM:
                String bookID = uri.getPathSegments().get(1);
                cursor = db.query("Book",projection,"id=?",new String[] {bookID},null,null,sortOrder);
                return cursor;

            case CATEGORY_DIR:
                cursor = db.query("Category",projection,selection,selectionArgs,null,null,sortOrder);
                return cursor;
            case CATEGORY_ITEM:
                String categoryID = uri.getPathSegments().get(1);
                cursor = db.query("Category",projection, "id=?",new String[]{categoryID},null,null,sortOrder);
                return cursor;
            default:
                break;
        }
        return cursor;
    }

    @Override
    public int update(Uri uri, ContentValues values, String selection,
                      String[] selectionArgs) {
        SQLiteDatabase db = sqlHelper.getWritableDatabase();
        int updateRows = 0;
        switch (uriMatcher.match(uri)) {
            case BOOK_DIR:
                updateRows = db.update("Book",values,selection,selectionArgs);
                break;
            case BOOK_ITEM:
                String bookID = uri.getPathSegments().get(1);
                updateRows = db.update("Book",values,"id=?",new String[] {bookID});
                break;
            case CATEGORY_DIR:
                updateRows = db.update("Category",values,selection,selectionArgs);
                break;
            case  CATEGORY_ITEM:
                String categoryID = uri.getPathSegments().get(1);
                updateRows = db.update("Category",values,"id=?",new String[] {categoryID});
                break;
            default:
                break;
        }
        return updateRows;
    }
}