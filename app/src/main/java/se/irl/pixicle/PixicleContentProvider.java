package se.irl.pixicle;

import android.content.ContentProvider;
import android.content.ContentUris;
import android.content.ContentValues;
import android.content.Context;
import android.content.UriMatcher;
import android.database.Cursor;
import android.database.SQLException;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import android.database.sqlite.SQLiteQueryBuilder;
import android.net.Uri;
import android.text.TextUtils;
import android.util.Log;

public class PixicleContentProvider extends ContentProvider {

    static final String PROVIDER_NAME = "se.irl.Pixicle.PixicleContentProvider";
    static final String PIXICLE_URL = "content://" + PROVIDER_NAME + "/Pixicle";
    static final Uri PIXICLE_URI = Uri.parse(PIXICLE_URL);
    static final String CONFIGURATION_URL = "content://" + PROVIDER_NAME + "/Configuration";
    static final Uri CONFIGURATION_URI = Uri.parse(CONFIGURATION_URL);

    public static final String PIXICLE_ID_COLUMN = "_id";
    public static final String DEVICE_ID_COLUMN = "deviceId";
    public static final String NAME_COLUMN = "name";
    public static final String SOFTWARE_VERSION_COLUMN = "softwareVersion";
    public static final String NEOPIXEL_ARRANGEMENT_COLUMN = "neoPixelArrangement";
    public static final String NUMBER_OF_NEOPIXELS_COLUMN = "numberOfNeoPixels";
    public static final String PIN_NUMBER_COLUMN = "pinNumber";
    public static final String ACCESS_TOKEN_COLUMN = "accessToken";
    public static final String CREATED_TIME_COLUMN = "createdTime";
    public static final String TIMESTAMP_COLUMN = "timestamp";
    public static final String CONFIGURATION_COLUMN = "configuration";
    public static final String PLUGIN_NAME_COLUMN = "pluginName";
    public static final String CONFIGURATION_PIXICLE_ID_COLUMN = "pixicleId";

    static final int PIXICLE_TOKEN = 1;
    static final int PIXICLE_ID_TOKEN = 2;
    static final int CONFIGURATION_TOKEN = 3;
    static final int CONFIGURATION_PIXICLE_TOKEN = 4;
    static final int CONFIGURATION_PIXICLE_ID_TOKEN = 5;

    static final UriMatcher uriMatcher;
    static{
        uriMatcher = new UriMatcher(UriMatcher.NO_MATCH);
        uriMatcher.addURI(PROVIDER_NAME, "Pixicle", PIXICLE_TOKEN);
        uriMatcher.addURI(PROVIDER_NAME, "Pixicle/#", PIXICLE_ID_TOKEN);
        uriMatcher.addURI(PROVIDER_NAME, "Configuration", CONFIGURATION_TOKEN);
        uriMatcher.addURI(PROVIDER_NAME, "Configuration/#", CONFIGURATION_PIXICLE_TOKEN);
        uriMatcher.addURI(PROVIDER_NAME, "Configuration/#/*", CONFIGURATION_PIXICLE_ID_TOKEN);
    }

    public PixicleContentProvider() {
    }

    @Override
    public int delete(Uri uri, String selection, String[] selectionArgs) {
        int count;
        String pixicleId;
        String pluginName;

        switch (uriMatcher.match(uri)){
            case PIXICLE_TOKEN:
                Log.d("PixicleContentProvider", "DataProvider: Deleting all pixicles");
                count = db.delete(PIXICLE_TABLE_NAME, selection, selectionArgs);
                break;

            case PIXICLE_ID_TOKEN:
                pixicleId = uri.getPathSegments().get(1);
                Log.d("PixicleContentProvider", "DataProvider: Deleting pixicle id="+pixicleId);
                count = db.delete(PIXICLE_TABLE_NAME, "_id = " + pixicleId +
                        (!TextUtils.isEmpty(selection) ? " AND (" + selection + ')' : ""), selectionArgs);
                break;

            case CONFIGURATION_TOKEN:
                Log.d("PixicleContentProvider", "DataProvider: Deleting all configurations");
                count = db.delete(CONFIGURATION_TABLE_NAME, selection, selectionArgs);
                break;

            case CONFIGURATION_PIXICLE_TOKEN:
                pixicleId = uri.getPathSegments().get(1);
                Log.d("PixicleContentProvider", "DataProvider: Deleting configurations for pixicle id="+pixicleId);
                count = db.delete(CONFIGURATION_TABLE_NAME, "pixicleId = " + pixicleId +
                        (!TextUtils.isEmpty(selection) ? " AND (" + selection + ')' : ""), selectionArgs);
                break;

            case CONFIGURATION_PIXICLE_ID_TOKEN:
                pixicleId = uri.getPathSegments().get(1);
                pluginName = uri.getPathSegments().get(2);
                Log.d("PixicleContentProvider", "DataProvider: Deleting configuration for plugin "+pluginName+" of pixicle id="+pixicleId);
                count = db.delete(CONFIGURATION_TABLE_NAME, "pixicleId = " + pixicleId + " AND pluginName = '"+pluginName+"'" +
                        (!TextUtils.isEmpty(selection) ? " AND (" + selection + ')' : ""), selectionArgs);
                break;

            default:
                throw new IllegalArgumentException("Unknown URI " + uri);
        }

        getContext().getContentResolver().notifyChange(uri, null);

        return count;
    }

    @Override
    public String getType(Uri uri) {
        switch (uriMatcher.match(uri)){
            /**
             * Get all pixicle records
             */
            case PIXICLE_TOKEN:
                return "vnd.android.cursor.dir/vnd.se.irl.Pixicle.PixicleContentProvider.Pixicle";

            /**
             * Get a specific pixicle record
             */
            case PIXICLE_ID_TOKEN:
                return "vnd.android.cursor.dir/vnd.se.irl.Pixicle.PixicleContentProvider.Pixicle";

            /**
             * Get all configurations
             */
            case CONFIGURATION_TOKEN:
                return "vnd.android.cursor.dir/vnd.se.irl.Pixicle.PixicleContentProvider.Configuration";

            /**
             * Get all configurations for a specific pixicle
             */
            case CONFIGURATION_PIXICLE_TOKEN:
                return "vnd.android.cursor.dir/vnd.se.irl.Pixicle.PixicleContentProvider.Configuration";

            /**
             * Get specific configuration for specific pixicle
             */
            case CONFIGURATION_PIXICLE_ID_TOKEN:
                return "vnd.android.cursor.dir/vnd.se.irl.Pixicle.PixicleContentProvider.Configuration";

            default:
                throw new IllegalArgumentException("Unsupported URI: " + uri);
        }
    }

    @Override
    public Uri insert(Uri uri, ContentValues values) {
        long currentRowID;

        switch(uriMatcher.match(uri)) {
            case PIXICLE_TOKEN:
                currentRowID = db.insert(PIXICLE_TABLE_NAME, "", values);

                if (currentRowID > 0)
                {
                    Log.d("PixicleContentProvider", "DataProvider: Inserted pixicle row id="+currentRowID);
                    Uri _uri = ContentUris.withAppendedId(PIXICLE_URI, currentRowID);
                    getContext().getContentResolver().notifyChange(_uri, null);
                    return _uri;
                }
                break;
            case CONFIGURATION_TOKEN:
                currentRowID = db.insert(CONFIGURATION_TABLE_NAME, "", values);

                if (currentRowID > 0)
                {
                    Log.d("PixicleContentProvider", "DataProvider: Inserted configuration row id="+currentRowID);
                    Uri _uri = ContentUris.withAppendedId(CONFIGURATION_URI, currentRowID);
                    getContext().getContentResolver().notifyChange(_uri, null);
                    return _uri;
                }
                break;
        }

        throw new SQLException("Failed to add a record into " + uri);
    }

    @Override
    public boolean onCreate() {
        Context context = getContext();
        DatabaseHelper dbHelper = new DatabaseHelper(context);
        db = dbHelper.getWritableDatabase();
        return db != null;
    }

    @Override
    public Cursor query(Uri uri, String[] projection, String selection, String[] selectionArgs, String sortOrder) {
        SQLiteQueryBuilder qb = new SQLiteQueryBuilder();

        if(uri.equals(PIXICLE_URI)) {
        }
        switch (uriMatcher.match(uri)) {
            case PIXICLE_TOKEN:
                qb.setTables(PIXICLE_TABLE_NAME);
                if (sortOrder == null || sortOrder == ""){
                    sortOrder = DEVICE_ID_COLUMN;
                }
                break;
            case PIXICLE_ID_TOKEN:
                qb.setTables(PIXICLE_TABLE_NAME);
                qb.appendWhere("_id = " + uri.getPathSegments().get(1));
                break;

            case CONFIGURATION_TOKEN:
                qb.setTables(CONFIGURATION_TABLE_NAME);
                if (sortOrder == null || sortOrder == ""){
                    sortOrder = PIXICLE_ID_COLUMN;
                }
                break;

            case CONFIGURATION_PIXICLE_TOKEN:
                qb.setTables(CONFIGURATION_TABLE_NAME);
                qb.appendWhere(" pixicleId = "+uri.getPathSegments().get(1));
                if (sortOrder == null || sortOrder == ""){
                    sortOrder = PLUGIN_NAME_COLUMN;
                }
                break;

            case CONFIGURATION_PIXICLE_ID_TOKEN:
                qb.setTables(CONFIGURATION_TABLE_NAME);
                qb.appendWhere(" pixicleId = " + uri.getPathSegments().get(1));
                qb.appendWhere(" pluginName = '"+uri.getLastPathSegment()+"'");
                break;

            default:
                throw new IllegalArgumentException("Unknown URI " + uri);
        }


        Cursor c = qb.query(db,	projection,	selection, selectionArgs, null, null, sortOrder);

        c.setNotificationUri(getContext().getContentResolver(), uri);
        return c;
    }

    @Override
    public int update(Uri uri, ContentValues values, String selection, String[] selectionArgs) {
        int count;

        switch (uriMatcher.match(uri)){
            case PIXICLE_TOKEN:
                count = db.update(PIXICLE_TABLE_NAME, values, selection, selectionArgs);
                break;

            case PIXICLE_ID_TOKEN:
                count = db.update(PIXICLE_TABLE_NAME, values, "_id = " + uri.getPathSegments().get(1) +
                        (!TextUtils.isEmpty(selection) ? " AND (" +selection + ')' : ""), selectionArgs);
                break;

            case CONFIGURATION_TOKEN:
                count = db.update(CONFIGURATION_TABLE_NAME, values, selection, selectionArgs);
                break;

            case CONFIGURATION_PIXICLE_TOKEN:
                count = db.update(CONFIGURATION_TABLE_NAME, values, "pixicleId = " + uri.getPathSegments().get(1) +
                        (!TextUtils.isEmpty(selection) ? " AND (" +selection + ')' : ""), selectionArgs);
                break;

            case CONFIGURATION_PIXICLE_ID_TOKEN:
                count = db.update(CONFIGURATION_TABLE_NAME, values, "pixicleId = " + uri.getPathSegments().get(1) + " AND pluginName = '"+uri.getLastPathSegment()+"'" +
                        (!TextUtils.isEmpty(selection) ? " AND (" +selection + ')' : ""), selectionArgs);
                break;

            default:
                throw new IllegalArgumentException("Unknown URI " + uri );
        }

        getContext().getContentResolver().notifyChange(uri, null);

        return count;
    }

    /**
     * Database specific constant declarations
     */
    private SQLiteDatabase db;
    static final String DATABASE_NAME = "Pixicle";
    static final String PIXICLE_TABLE_NAME = "Pixicle";
    static final String CONFIGURATION_TABLE_NAME = "Configuration";
    static final int DATABASE_VERSION = 6;
    static final String CREATE_PIXICLE_DB_TABLE =
            " CREATE TABLE " + PIXICLE_TABLE_NAME +
                    " (_id INTEGER PRIMARY KEY AUTOINCREMENT, " +
                    " deviceId TEXT NOT NULL, " +
                    " name TEXT NOT NULL, " +
                    " softwareVersion INTEGER NOT NULL, " +
                    " numberOfNeoPixels INTEGER NOT NULL, " +
                    " pinNumber INTEGER NULL," +
                    " neoPixelArrangement INTEGER NOT NULL, " + // Array, Helical, Random, Raster
                    " accessToken TEXT NULL, " +
                    " createdTime DATETIME NOT NULL, " +
                    " timestamp DATETIME NOT NULL);";
    static final String CREATE_CONFIGURATION_DB_TABLE =
            " CREATE TABLE " + CONFIGURATION_TABLE_NAME +
                    " (_id INTEGER PRIMARY KEY AUTOINCREMENT, " +
                    " pixicleId INTEGER NOT NULL, " +
                    " deviceId TEXT NOT NULL, " +
                    " pluginName TEXT NOT NULL, " +
                    " configuration TEXT NOT NULL, " +
                    " createdTime DATETIME NOT NULL, " +
                    " timestamp DATETIME NOT NULL);";

    private static class DatabaseHelper extends SQLiteOpenHelper {
        DatabaseHelper(Context context){
            super(context, DATABASE_NAME, null, DATABASE_VERSION);
        }

        @Override
        public void onCreate(SQLiteDatabase db) {
            db.execSQL(CREATE_PIXICLE_DB_TABLE);
            db.execSQL(CREATE_CONFIGURATION_DB_TABLE);
        }

        @Override
        public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
            db.execSQL("DROP TABLE IF EXISTS " +  PIXICLE_TABLE_NAME);
            db.execSQL("DROP TABLE IF EXISTS " +  CONFIGURATION_TABLE_NAME);
            onCreate(db);
        }
    }
}