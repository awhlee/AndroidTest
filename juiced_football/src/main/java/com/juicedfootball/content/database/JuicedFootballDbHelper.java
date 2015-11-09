package com.juicedfootball.content.database;

import android.content.ContentValues;
import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import android.text.TextUtils;
import android.util.Log;

import java.text.SimpleDateFormat;
import java.util.Date;

/**
 * This is a helper class that works that facilitates the details fo the JuicedFootballDb
 */
public class JuicedFootballDbHelper extends SQLiteOpenHelper {
    private static final String LOG_TAG = "JuicedDbHelper";

    public static final String SQLITE_TABLE = "juiced_football";
    public static final int DATABASE_VERSION = 1;
    public static final boolean FORCE_CONTENT = true;

    private static PlayersTable mPlayersTable = new PlayersTable();
    private static TeamsTable mTeamsTable = new TeamsTable();
    private static GamesTable mGamesTable = new GamesTable();
    private static UsersTable mUsersTable = new UsersTable();

    public JuicedFootballDbHelper(Context context) {
        super(context, SQLITE_TABLE, null, DATABASE_VERSION);
    }

    /**
     * Function called when the database is created
     * @param db The database that was just created
     */
    public void onCreate(SQLiteDatabase db) {
        Log.i(LOG_TAG, "onCreate");
        // Ask each table to create itself within the database
        // TODO: Consider looping over a list of tables.
        mPlayersTable.onCreate(db);
        mTeamsTable.onCreate(db);
        mGamesTable.onCreate(db);
        mUsersTable.onCreate(db);

        if (FORCE_CONTENT) {
            // Populate data into the tables
            insertTempContent(db);
        }
    }

    /**
     * Function called when a database is upgraded
     * @param db The database
     * @param oldVersion The old version
     * @param newVersion The version we are upgrading to
     */
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
        Log.i(LOG_TAG, "onUpgrade");
        // Ask each table to upgrade itself.
        // Note this may not work if the table is new or dropped as part of the schema change.
        // TODO: Consider looping over a list of tables.
        mPlayersTable.onUpgrade(db, oldVersion, newVersion);
        mTeamsTable.onUpgrade(db, oldVersion, newVersion);
        mGamesTable.onUpgrade(db, oldVersion, newVersion);
        mUsersTable.onUpgrade(db, oldVersion, newVersion);
    }

    /**
     * The base class for all table helper subclass objects
     */
    protected static abstract class DbTable {
        public static String LOG_TAG = "JuicedDbTable";

        public static final String KEY_ROW_ID = "_id";

        /**
         * Helper to create a table
         * @param db The database
         */
        public void onCreate(SQLiteDatabase db) {
            String createCommand = getCreateCommand();
            Log.i(LOG_TAG, "createCommand = " + createCommand);
            db.execSQL(createCommand);
            return;
        }

        /**
         * Helper to upgrade a table
         * @param db The database
         * @param oldVersion The old version
         * @param newVersion The version we are upgrading to
         */
        public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
            String upgradeCommand = getUpgradeCommand(oldVersion, newVersion);
            Log.i(LOG_TAG, "upgradeCommand = " + upgradeCommand);
            if (TextUtils.equals(upgradeCommand, "")) {
                String tableName = getTableName();
                Log.i(LOG_TAG, "Recreating table " + tableName);
                db.execSQL("DROP TABLE IF EXISTS " + tableName);
                onCreate(db);
            } else {
                db.execSQL(upgradeCommand);
            }
            return;
        }

        // Subclasses can put their custom table specific logic in these functions.
        public abstract String getCreateCommand();
        public abstract String getUpgradeCommand(int oldVersion, int newVersion);
        public abstract String getTableName();
    }

    public static final class PlayersTable extends DbTable {
        public static final String LOG_TAG = "PlayersTable";

        public static final String TABLE_NAME = "players";

        public static final String KEY_TEAM_ID = "team_id";
        public static final String KEY_FIRST_NAME = "first_name";
        public static final String KEY_LAST_NAME = "last_name";
        public static final String KEY_POSITION = "position";

        private static final String DATABASE_CREATE =
                "CREATE TABLE IF NOT EXISTS " + TABLE_NAME + " (" +
                        KEY_ROW_ID + " INTEGER PRIMARY KEY AUTOINCREMENT," +
                        KEY_TEAM_ID + " INTEGER," +
                        KEY_FIRST_NAME + " TEXT NOT NULL," +
                        KEY_LAST_NAME + " TEXT NOT NULL," +
                        KEY_POSITION + " INTEGER );";

        /**
         * The name of the table that this subclass manages
         * @return The name of the table.
         */
        public String getTableName() {
            return TABLE_NAME;
        }

        /**
         * Returns the create command for this table.
         * @return The create command for this table.
         */
        @Override
        public String getCreateCommand() {
            Log.i(LOG_TAG, DATABASE_CREATE);
           return DATABASE_CREATE;
        }

        /**
         * The specific upgrade command for this table
         * @param oldVersion The version to upgrade from
         * @param newVersion The version to upgrade to
         * @return The command to upgrade this database if applicable.  null if we want to wipe.
         */
        @Override
        public String getUpgradeCommand(int oldVersion, int newVersion) {
            Log.i(LOG_TAG, "Upgrading table from version " + oldVersion + " to "
                    + newVersion + ", which will destroy all old data");
            return null;
        }
    }

    public static final class TeamsTable extends DbTable {
        public static final String LOG_TAG = "TeamsTable";

        public static final String TABLE_NAME = "teams";

        public static final String KEY_LOCATION = "location";
        public static final String KEY_NICKNAME = "nickname";

        private static final String DATABASE_CREATE =
                "CREATE TABLE IF NOT EXISTS " + TABLE_NAME + " (" +
                        KEY_ROW_ID + " INTEGER PRIMARY KEY AUTOINCREMENT," +
                        KEY_LOCATION + " TEXT NOT NULL," +
                        KEY_NICKNAME + " TEXT NOT NULL );";

        /**
         * The name of the table that this subclass manages
         * @return The name of the table.
         */
        public String getTableName() {
            return TABLE_NAME;
        }

        /**
         * Returns the create command for this table.
         * @return The create command for this table.
         */
        @Override
        public String getCreateCommand() {
            Log.i(LOG_TAG, DATABASE_CREATE);
            return DATABASE_CREATE;
        }

        /**
         * The specific upgrade command for this table
         * @param oldVersion The version to upgrade from
         * @param newVersion The version to upgrade to
         * @return The command to upgrade this database if applicable.  null if we want to wipe.
         */
        @Override
        public String getUpgradeCommand(int oldVersion, int newVersion) {
            Log.i(LOG_TAG, "Upgrading table from version " + oldVersion + " to "
                    + newVersion + ", which will destroy all old data");
            return null;
        }
    }

    public static final class GamesTable extends DbTable {
        public static final String LOG_TAG = "GamesTable";

        public static final String TABLE_NAME = "games";

        public static final String KEY_HOME_TEAM_ID = "home_team_id";
        public static final String KEY_VISITOR_TEAM_ID = "visitor_team_id";
        public static final String KEY_WEEK = "week";
        public static final String KEY_DATE = "game_date";

        private static final String DATABASE_CREATE =
                "CREATE TABLE IF NOT EXISTS " + TABLE_NAME + " (" +
                        KEY_ROW_ID + " INTEGER PRIMARY KEY AUTOINCREMENT," +
                        KEY_HOME_TEAM_ID + " INTEGER," +
                        KEY_VISITOR_TEAM_ID + " INTEGER," +
                        KEY_WEEK + " INTEGER," +
                        KEY_DATE + " TEXT NOT NULL );";

        /**
         * The name of the table that this subclass manages
         * @return The name of the table.
         */
        public String getTableName() {
            return TABLE_NAME;
        }

        /**
         * Returns the create command for this table.
         * @return The create command for this table.
         */
        @Override
        public String getCreateCommand() {
            Log.i(LOG_TAG, DATABASE_CREATE);
            return DATABASE_CREATE;
        }

        /**
         * The specific upgrade command for this table
         * @param oldVersion The version to upgrade from
         * @param newVersion The version to upgrade to
         * @return The command to upgrade this database if applicable.  null if we want to wipe.
         */
        @Override
        public String getUpgradeCommand(int oldVersion, int newVersion) {
            Log.i(LOG_TAG, "Upgrading table from version " + oldVersion + " to "
                    + newVersion + ", which will destroy all old data");
            return null;
        }
    }

    public static final class UsersTable extends DbTable {
        public static final String LOG_TAG = "UsersTable";

        public static final String TABLE_NAME = "users";

        public static final String KEY_FIRST_NAME = "first_name";
        public static final String KEY_LAST_NAME = "last_name";
        public static final String KEY_AUTH_TOKEN = "auth_token";

        private static final String DATABASE_CREATE =
                "CREATE TABLE IF NOT EXISTS " + TABLE_NAME + " (" +
                        KEY_ROW_ID + " INTEGER PRIMARY KEY AUTOINCREMENT," +
                        KEY_FIRST_NAME + " TEXT NOT NULL," +
                        KEY_LAST_NAME + " TEXT NOT NULL," +
                        KEY_AUTH_TOKEN + " TEXT NOT NULL );";

        /**
         * The name of the table that this subclass manages
         * @return The name of the table.
         */
        public String getTableName() {
            return TABLE_NAME;
        }

        /**
         * Returns the create command for this table.
         * @return The create command for this table.
         */
        @Override
        public String getCreateCommand() {
            Log.i(LOG_TAG, DATABASE_CREATE);
            return DATABASE_CREATE;
        }

        /**
         * The specific upgrade command for this table
         * @param oldVersion The version to upgrade from
         * @param newVersion The version to upgrade to
         * @return The command to upgrade this database if applicable.  null if we want to wipe.
         */
        @Override
        public String getUpgradeCommand(int oldVersion, int newVersion) {
            Log.i(LOG_TAG, "Upgrading table from version " + oldVersion + " to "
                    + newVersion + ", which will destroy all old data");
            return null;
        }
    }

    private void insertTempContent(SQLiteDatabase db) {
        ContentValues player = new ContentValues();
        player.put(PlayersTable.KEY_TEAM_ID, 1);
        player.put(PlayersTable.KEY_FIRST_NAME, "Peyton");
        player.put(PlayersTable.KEY_LAST_NAME, "Manning");
        player.put(PlayersTable.KEY_POSITION, 1);
        db.insert("players", null, player);

        player.put(PlayersTable.KEY_TEAM_ID, 2);
        player.put(PlayersTable.KEY_FIRST_NAME, "Andrew");
        player.put(PlayersTable.KEY_LAST_NAME, "Luck");
        player.put(PlayersTable.KEY_POSITION, 1);
        db.insert("players", null, player);

        ContentValues team = new ContentValues();
        team.put(TeamsTable.KEY_LOCATION, "DEN");
        team.put(TeamsTable.KEY_NICKNAME, "Broncos");
        db.insert("teams", null, team);

        team.put(TeamsTable.KEY_LOCATION, "IND");
        team.put(TeamsTable.KEY_NICKNAME, "Colts");
        db.insert("teams", null, team);

        ContentValues game = new ContentValues();
        game.put(GamesTable.KEY_HOME_TEAM_ID, 1);
        game.put(GamesTable.KEY_VISITOR_TEAM_ID, 2);
        game.put(GamesTable.KEY_WEEK, 5);
        SimpleDateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
        Date date = new Date();
        game.put(GamesTable.KEY_DATE, dateFormat.format(date));
        db.insert("games", null, game);

        ContentValues user = new ContentValues();
        user.put(UsersTable.KEY_FIRST_NAME, "Anthony");
        user.put(UsersTable.KEY_LAST_NAME, "Lee");
        user.put(UsersTable.KEY_AUTH_TOKEN, "123abc");
        db.insert("users", null, user);
    }}
