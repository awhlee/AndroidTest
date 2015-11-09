package com.juicedfootball.content;

import android.content.ContentProvider;
import android.content.ContentValues;
import android.content.UriMatcher;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteQueryBuilder;
import android.net.Uri;
import android.text.TextUtils;
import android.util.Log;

import com.juicedfootball.common.JuicedFootballUtils;
import com.juicedfootball.content.database.JuicedFootballDbHelper;

/**
 * JuicedFootballContentProvider
 * The ContentProvider wrapping the data in our database
 */
public class JuicedFootballContentProvider extends ContentProvider {
    static final String LOG_TAG = JuicedFootballUtils.LOG_TAG_CONTENT_PROVIDER;

    public static final String PROVIDER_AUTHORITY = "com.juicedfootball.provider";
    private SQLiteDatabase mDb;
    private JuicedFootballDbHelper mDbHelper;

    // The IDs that are results from the UriMatcher
    private static final int PLAYERS_ID = 1;
    private static final int PLAYERS_PLAYER_ID = 2;
    private static final int TEAMS_ID = 3;
    private static final int TEAMS_TEAM_ID = 4;
    private static final int GAMES_ID = 5;
    private static final int GAMES_WEEK_ID = 6;
    private static final int GAMES_GAME_ID = 7;
    private static final int PEOPLE_ID = 8;
    private static final int PEOPLE_PERSON_ID = 9;

    // UriMatcher to help us parse incoming requests.
    private static final UriMatcher sUriMatcher = new UriMatcher(UriMatcher.NO_MATCH);
    static {
        // List of players
        sUriMatcher.addURI(PROVIDER_AUTHORITY, "players", PLAYERS_ID);
        // Details of a particular player
        sUriMatcher.addURI(PROVIDER_AUTHORITY, "players/#", PLAYERS_PLAYER_ID);
        // List of NFL teams
        sUriMatcher.addURI(PROVIDER_AUTHORITY, "teams", TEAMS_ID);
        // Details of a particular NFL team
        sUriMatcher.addURI(PROVIDER_AUTHORITY, "teams/#", TEAMS_TEAM_ID);
        // List of games, only used for inserting not querying
        sUriMatcher.addURI(PROVIDER_AUTHORITY, "games", GAMES_ID);
        // The games for a given week
        sUriMatcher.addURI(PROVIDER_AUTHORITY, "games/week/#", GAMES_WEEK_ID);
        // The details of a particular game
        sUriMatcher.addURI(PROVIDER_AUTHORITY, "games/#", GAMES_GAME_ID);
        // List of users
        sUriMatcher.addURI(PROVIDER_AUTHORITY, "users", PEOPLE_ID);
        // Details of the particular user
        sUriMatcher.addURI(PROVIDER_AUTHORITY, "users/#", PEOPLE_PERSON_ID);
    }

    // Begin required overrides for a ContentProvider subclass
    @Override
    public boolean onCreate () {
        Log.i(LOG_TAG, "onCreate()");

        // Create/Open/Upgrade the database using the database helper.
        mDbHelper = new JuicedFootballDbHelper(getContext());
        mDb = mDbHelper.getWritableDatabase();
        if (mDb == null) {
            Log.i(LOG_TAG, "onCreate() - returning false");
            return false;
        }
        Log.i(LOG_TAG, "onCreate() - returning true");
        return true;
    }

    @Override
    public Cursor query(Uri uri, String[] projection, String selection, String[] selectionArgs,
            String sortOrder) {
        Log.i(LOG_TAG, "query() - uri = " + uri);
        SQLiteQueryBuilder queryBuilder = new SQLiteQueryBuilder();
        String id;

        // We build a different query depending on the URI match.
        switch (sUriMatcher.match(uri)) {
            case PLAYERS_ID:
                queryBuilder.setTables(JuicedFootballDbHelper.PlayersTable.TABLE_NAME);
                break;
            case PLAYERS_PLAYER_ID:
                queryBuilder.setTables(JuicedFootballDbHelper.PlayersTable.TABLE_NAME);
                // We want path segment 1 because players(0)/#(1)
                id = uri.getPathSegments().get(1);
                queryBuilder.appendWhere(JuicedFootballDbHelper.PlayersTable.KEY_ROW_ID + "=" + id);
                break;
            case TEAMS_ID:
                queryBuilder.setTables(JuicedFootballDbHelper.TeamsTable.TABLE_NAME);
                break;
            case TEAMS_TEAM_ID:
                queryBuilder.setTables(JuicedFootballDbHelper.TeamsTable.TABLE_NAME);
                // We want path segment 1 because teams(0)/#(1)
                id = uri.getPathSegments().get(1);
                queryBuilder.appendWhere(JuicedFootballDbHelper.PlayersTable.KEY_ROW_ID + "=" + id);
                break;
            case GAMES_WEEK_ID:
                // Note the week comes as the last part.
                queryBuilder.setTables(JuicedFootballDbHelper.GamesTable.TABLE_NAME);
                // We want path segment 2 because games(0)/week(1)/#(2)
                id = uri.getPathSegments().get(2);
                queryBuilder.appendWhere(JuicedFootballDbHelper.PlayersTable.KEY_ROW_ID + "=" + id);
                break;
            case GAMES_GAME_ID:
                queryBuilder.setTables(JuicedFootballDbHelper.GamesTable.TABLE_NAME);
                // We want path segment 1 because games(0)/#(1)
                id = uri.getPathSegments().get(1);
                queryBuilder.appendWhere(JuicedFootballDbHelper.PlayersTable.KEY_ROW_ID + "=" + id);
                break;
            case PEOPLE_ID:
                queryBuilder.setTables(JuicedFootballDbHelper.UsersTable.TABLE_NAME);
                break;
            case PEOPLE_PERSON_ID:
                queryBuilder.setTables(JuicedFootballDbHelper.UsersTable.TABLE_NAME);
                // We want path segment 1 because users(0)/#(1)
                id = uri.getPathSegments().get(1);
                queryBuilder.appendWhere(JuicedFootballDbHelper.PlayersTable.KEY_ROW_ID + "=" + id);
                break;
            default:
                throw new IllegalArgumentException("Unsupported URI: " + uri);
        }
        // Now make the query with the rest of the parameters and return a cursor to the user.
        Cursor cursor = queryBuilder.query(mDb, projection, selection,
                selectionArgs, null, null, sortOrder);
        return cursor;
    }

    @Override
    public Uri insert(Uri uri, ContentValues values) {
        Log.i(LOG_TAG, "insert() - uri = " + uri);

        // Two variables that change depending on the table that we insert into.
        String tableName;

        // Only going to allow adds in:
        // "players" which is PLAYERS_ID
        // "teams" which is TEAMS_ID
        // "games" which is GAMES_ID
        // "users" which is PEOPLE_ID
        switch (sUriMatcher.match(uri)) {
            case PLAYERS_ID:
                tableName = JuicedFootballDbHelper.PlayersTable.TABLE_NAME;
                break;
            case TEAMS_ID:
                tableName = JuicedFootballDbHelper.TeamsTable.TABLE_NAME;
                break;
            case GAMES_ID:
                tableName = JuicedFootballDbHelper.GamesTable.TABLE_NAME;
                break;
            case PEOPLE_ID:
                tableName = JuicedFootballDbHelper.UsersTable.TABLE_NAME;
                break;
            default:
                throw new IllegalArgumentException("Unsupported URI: " + uri);
        }
        long id = mDb.insert(tableName, null, values);

        if (id < 0) {
            Log.e(LOG_TAG, "Could not insert at uri: " + uri);
            return null;
        }

        // Notify change for the content resolvers that are interested in this information
        // TODO: Research to see if we need to notify on games/week/# instead of games because
        // the list of games can only be pulled via games/week/#. If we want to do this, we'll have
        // to strip off the week from the game and generate a new URI to notify on the correct
        // content URI.
        getContext().getContentResolver().notifyChange(uri, null);
        // TODO: Change from using tableName to some other define off of the table helper objects
        // like contentPath or something similar so that they can change independently.
        return Uri.parse("content://" + PROVIDER_AUTHORITY + "/" + tableName + "/" + id);
    }

    @Override
    public int update(Uri uri, ContentValues values, String selection, String[] selectionArgs) {
        Log.i(LOG_TAG, "update() - uri = " + uri);

        // Only going to allow updates in:
        // "players/#" which is PLAYERS_PLAYER_ID
        // "teams/#" which is TEAMS_TEAM_ID
        // "games/#" which is GAMES_GAME_ID
        // "users/#" which is PEOPLE_PERSON_ID
        String tableName;
        switch (sUriMatcher.match(uri)) {
            case PLAYERS_PLAYER_ID:
                tableName = JuicedFootballDbHelper.PlayersTable.TABLE_NAME;
                break;
            case TEAMS_TEAM_ID:
                tableName = JuicedFootballDbHelper.TeamsTable.TABLE_NAME;
                break;
            case GAMES_GAME_ID:
                tableName = JuicedFootballDbHelper.GamesTable.TABLE_NAME;
                break;
            case PEOPLE_PERSON_ID:
                tableName = JuicedFootballDbHelper.UsersTable.TABLE_NAME;
                break;
            default:
                throw new IllegalArgumentException("Unsupported URI: " + uri);
        }
        // The only type of update that we support is when we know the ID of the particular
        // type of object. count should only be 0 or 1 as we are matching our our primary key
        // for each table.
        int count = mDb.update(tableName, values,
                JuicedFootballDbHelper.PlayersTable.KEY_ROW_ID + " = " + uri.getLastPathSegment() +
                (!TextUtils.isEmpty(selection) ? " AND (" + selection + ")" : ""), selectionArgs);
        // Notify change for the content resolvers that are interested in this information
        getContext().getContentResolver().notifyChange(uri, null);
        return count;
    }

    @Override
    public int delete (Uri uri, String selection, String[] selectionArgs) {
        Log.i(LOG_TAG, "delete() - uri = " + uri);

        // Only going to allow deletes in:
        // "players/#" which is PLAYERS_PLAYER_ID
        // "teams/#" which is TEAMS_TEAM_ID
        // "games/#" which is GAMES_GAME_ID
        // "users/#" which is PEOPLE_PERSON_ID
        String tableName;
        switch (sUriMatcher.match(uri)) {
            case PLAYERS_PLAYER_ID:
                tableName = JuicedFootballDbHelper.PlayersTable.TABLE_NAME;
                break;
            case TEAMS_TEAM_ID:
                tableName = JuicedFootballDbHelper.TeamsTable.TABLE_NAME;
                break;
            case GAMES_GAME_ID:
                tableName = JuicedFootballDbHelper.GamesTable.TABLE_NAME;
                break;
            case PEOPLE_PERSON_ID:
                tableName = JuicedFootballDbHelper.UsersTable.TABLE_NAME;
                break;
            default:
                throw new IllegalArgumentException("Unsupported URI: " + uri);
        }
        // The only type of delete that we support is when we know the ID of the particular
        // type of object. count should only be 0 or 1 as we are matching our our primary key
        // for each table.
        int count = mDb.delete(tableName,
                JuicedFootballDbHelper.PlayersTable.KEY_ROW_ID + " = " + uri.getLastPathSegment() +
                (!TextUtils.isEmpty(selection) ? " AND (" + selection + ")" : ""), selectionArgs);
        // Notify change for the content resolvers that are interested in this information
        getContext().getContentResolver().notifyChange(uri, null);
        return count;
    }

    @Override
    public String getType(Uri uri) {
        switch (sUriMatcher.match(uri)) {
            case PLAYERS_ID:
                return "vnd.android.cursor.dir/com.juicedfootball.players";
            case PLAYERS_PLAYER_ID:
                return "vnd.android.cursor.item/com.juicedfootball.player";
            case GAMES_WEEK_ID:
                return "vnd.android.cursor.dir/com.juicedfootball.games";
            case GAMES_GAME_ID:
                return "vnd.android.cursor.item/com.juicedfootball.game";
            case PEOPLE_ID:
                return "vnd.android.cursor.dir/com.juicedfootball.users";
            case PEOPLE_PERSON_ID:
                return "vnd.android.cursor.item/com.juicedfootball.user";
            default:
                throw new IllegalArgumentException("Unsupported URI: " + uri);
        }
    }

}
