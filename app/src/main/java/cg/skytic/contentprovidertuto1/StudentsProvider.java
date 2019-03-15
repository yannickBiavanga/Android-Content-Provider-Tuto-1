package cg.skytic.contentprovidertuto1;

import android.content.ContentProvider;
import android.content.ContentUris;
import android.content.ContentValues;
import android.content.Context;
import android.content.UriMatcher;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import android.database.sqlite.SQLiteQueryBuilder;
import android.net.Uri;
import android.text.TextUtils;

import java.util.HashMap;

public class StudentsProvider extends ContentProvider {

    public static final String PROVIDER_NAME ="cg.skytic.contentprovidertuto1.StudentsProvider";
    public static final String URL ="content://"+PROVIDER_NAME+"/students";
    public static final Uri CONTENT_URI =Uri.parse(URL);

    //champs de la table
    public static final String ID ="_id";
    public static final String NAME ="name";
    public static final String GRADE ="grade";

    private static HashMap<String,String> STUDENT_PROJECTION_MAP;

    public static final int STUDENTS =1;
    public static final int STUDENT_ID =2;

    public static final UriMatcher MATCHER;

    static {
        MATCHER = new UriMatcher(UriMatcher.NO_MATCH);
        MATCHER.addURI(PROVIDER_NAME,"students",STUDENTS);
        MATCHER.addURI(PROVIDER_NAME,"students/#",STUDENTS);
    }


    //Declaracton des attribut de la base de données

    private SQLiteDatabase sqLiteDatabase;
    public static String DATABASE_NAME ="college";
    public static String STUDENT_TABLE="students";
    public static final int DATABASE_VERSION =1;

    public static String CREATE_DB_TABLE ="CREATE TABLE "+ STUDENT_TABLE
            +" ( _id INTEGER PRIMARY KEY AUTOINCREMENT , "
            +" name TEXT NOT NULL, "
            +" grade TEXT NOT NULL ); ";


    /*Notre HElper*/

    private static class DatabaseHelper extends SQLiteOpenHelper{

        public DatabaseHelper( Context context) {
            super(context, DATABASE_NAME, null, DATABASE_VERSION);
        }

        @Override
        public void onCreate(SQLiteDatabase sqLiteDatabase) {
            sqLiteDatabase.execSQL(CREATE_DB_TABLE);
        }

        @Override
        public void onUpgrade(SQLiteDatabase sqLiteDatabase, int i, int i1) {
            sqLiteDatabase.execSQL("DROP TABLE IF EXISTS " +STUDENT_TABLE);

            onCreate(sqLiteDatabase);
        }
    }



    /*Methode de traitement liées aux ContentProvider*/


    @Override
    public boolean onCreate() {

        Context context = getContext();
        DatabaseHelper databaseHelper = new DatabaseHelper(context);

        sqLiteDatabase = databaseHelper.getWritableDatabase();

       return (sqLiteDatabase==null)?false:true;
    }



    @Override
    public Cursor query( Uri uri,String[] projection,
                         String selection,String[] selectionArgs, String sortOrder) {

        SQLiteQueryBuilder sqLiteQueryBuilder = new SQLiteQueryBuilder();
        sqLiteQueryBuilder.setTables(STUDENT_TABLE);

        switch (MATCHER.match(uri)){
            case STUDENTS :
                sqLiteQueryBuilder.setProjectionMap(STUDENT_PROJECTION_MAP);
                break;

            case STUDENT_ID:

                sqLiteQueryBuilder.appendWhere(ID+"="+uri.getPathSegments().get(1));
                break;
                default:
        }

        if (sortOrder==null || sortOrder==""){
            sortOrder = NAME;
        }

        Cursor cursor = sqLiteQueryBuilder.query(sqLiteDatabase,projection,selection,selectionArgs,null,null,sortOrder);
        cursor.setNotificationUri(getContext().getContentResolver(),uri);

        return cursor;
    }

    @Override
    public String getType(Uri uri) {

        switch (MATCHER.match(uri)){

            case STUDENTS :
                /**
                 * Get all student records
                 */
                return "vnd.android.cursor.dir/vnd.example.students";



            case STUDENT_ID:

                /**
                 * Get particular student records
                 */
                return "vnd.android.cursor.item/vnd.example.students";

            default:
                throw new IllegalArgumentException("Unsupported URI: " + uri);

        }

    }


    @Override
    public Uri insert( Uri uri,  ContentValues contentValues) {

        long rowId = sqLiteDatabase.insert(STUDENT_TABLE,null,contentValues);

        if (rowId>0){
            Uri uri1 = ContentUris.withAppendedId(uri,rowId);
            getContext().getContentResolver().notifyChange(uri1,null);

            return uri1;
        }else {
        return null;
        }

    }

    @Override
    public int delete( Uri uri, String selection, String[] selectionArgs) {
        int count =0;

        switch (MATCHER.match(uri)){
            case STUDENTS:
                count = sqLiteDatabase.delete(STUDENT_TABLE,selection,selectionArgs);

                break;
            case STUDENT_ID:
                String id = uri.getPathSegments().get(1);
                count = sqLiteDatabase.delete(STUDENT_TABLE,ID + "="+id+
                        (!TextUtils.isEmpty(selection) ? "  AND (" + selection + ')' : ""),selectionArgs);

                break;

                default:
                    throw new IllegalArgumentException("Unknown URI " + uri);
        }


        getContext().getContentResolver().notifyChange(uri,null);
        return count;
    }

    @Override
    public int update(Uri uri,  ContentValues contentValues, String selection, String[] selectionArgs) {

        int count =0;

        switch (MATCHER.match(uri)){

            case STUDENTS:

                count = sqLiteDatabase.update(STUDENT_TABLE,contentValues,selection,selectionArgs);

                break;

            case STUDENT_ID:
                count = sqLiteDatabase.update(STUDENT_TABLE,contentValues,ID +"="
                        +uri.getPathSegments().get(1)+(!TextUtils.isEmpty(selection) ?" AND ("+selection+')':""),
                        selectionArgs);
                break;

                default:
                    throw new IllegalArgumentException("Unknown URI " + uri );
        }

        getContext().getContentResolver().notifyChange(uri,null);

        return count;
    }
}
