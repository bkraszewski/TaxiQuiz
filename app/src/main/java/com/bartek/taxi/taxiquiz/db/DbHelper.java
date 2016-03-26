package com.bartek.taxi.taxiquiz.db;

import android.content.Context;
import android.database.sqlite.SQLiteDatabase;

import com.bartek.taxi.taxiquiz.entity.ExamScore;
import com.j256.ormlite.android.apptools.OpenHelperManager;
import com.j256.ormlite.android.apptools.OrmLiteSqliteOpenHelper;
import com.j256.ormlite.support.ConnectionSource;
import com.j256.ormlite.table.TableUtils;

import java.sql.SQLException;

public class DbHelper extends OrmLiteSqliteOpenHelper {

    private static final String DATABASE_NAME = "quiz_data";
    private static final int DATABASE_VERSION = 1;
    private static DbHelper instance;

    public DbHelper(Context context) {
        super(context, DATABASE_NAME, null, DATABASE_VERSION);
    }


    public static DbHelper getInstance(Context context) {
        if (instance == null) {
            instance = OpenHelperManager.getHelper(context, DbHelper.class);
        }

        return instance;
    }

    @Override
    public void onCreate(SQLiteDatabase database, ConnectionSource connectionSource) {
        try {
            TableUtils.createTable(connectionSource, ExamScore.class);
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    @Override
    public void onUpgrade(SQLiteDatabase database, ConnectionSource connectionSource, int oldVersion, int newVersion) {

    }
}
