package de.tudarmstadt.travelreminder.gmaps;

import android.arch.persistence.db.SupportSQLiteDatabase;
import android.arch.persistence.room.Database;
import android.arch.persistence.room.Room;
import android.arch.persistence.room.RoomDatabase;
import android.content.Context;
import android.database.Cursor;
import android.database.MatrixCursor;
import android.database.SQLException;
import android.util.Log;

import java.util.ArrayList;

/**
 * Database class.
 * The implementation will be generated by googles persistence library Room.
 */
@Database(exportSchema = false, entities = {GMapsRoutePlanModel.class}, version = 5)
abstract public class GMapsDatabase extends RoomDatabase {
    /**
     * The name of the SQLite database.
     */
    private static final String DATABASE_NAME = "gmaps_travelreminder";

    /**
     * An instance of the database object.
     */
    private static GMapsDatabase instance;

    /**
     * Creates an database instance by a given context.
     *
     * @param context App context with to generate this database.
     * @return Returns an instance of the database.
     */
    public static GMapsDatabase getDatabase(Context context) {
        if (instance == null)
            instance = Room
                    .databaseBuilder(context.getApplicationContext(), GMapsDatabase.class, DATABASE_NAME)
                    .build();
        return instance;
    }

    /**
     * Returns the implemented route plan model data access object.
     *
     * @return An implemented instance of the route plan model data access object.
     */
    public abstract GMapsRoutePlanDao RoutePlanModelDao();

    // @TODO Remove before release.
    public ArrayList<Cursor> getData(String Query) {
        //get writable database
        SupportSQLiteDatabase sqlDB = this.getOpenHelper().getWritableDatabase();
        String[] columns = new String[]{"message"};
        //an array list of cursor to save two cursors one has results from the query
        //other cursor stores error message if any errors are triggered
        ArrayList<Cursor> alc = new ArrayList<Cursor>(2);
        MatrixCursor Cursor2 = new MatrixCursor(columns);
        alc.add(null);
        alc.add(null);

        try {
            String maxQuery = Query;
            //execute the query results will be save in Cursor c
            Cursor c = sqlDB.query(maxQuery, null);

            //add value to cursor2
            Cursor2.addRow(new Object[]{"Success"});

            alc.set(1, Cursor2);
            if (null != c && c.getCount() > 0) {

                alc.set(0, c);
                c.moveToFirst();

                return alc;
            }
            return alc;
        } catch (SQLException sqlEx) {
            Log.d("printing exception", sqlEx.getMessage());
            //if any exceptions are triggered save the error message to cursor an return the arraylist
            Cursor2.addRow(new Object[]{"" + sqlEx.getMessage()});
            alc.set(1, Cursor2);
            return alc;
        } catch (Exception ex) {
            Log.d("printing exception", ex.getMessage());

            //if any exceptions are triggered save the error message to cursor an return the arraylist
            Cursor2.addRow(new Object[]{"" + ex.getMessage()});
            alc.set(1, Cursor2);
            return alc;
        }
    }
}