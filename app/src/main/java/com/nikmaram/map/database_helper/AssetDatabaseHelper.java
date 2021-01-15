package com.nikmaram.map.database_helper;

import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

import com.nikmaram.map.model.PointModel;

import java.io.File;
import java.io.FileOutputStream;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.List;


public class AssetDatabaseHelper extends SQLiteOpenHelper {

    private Context main_context;

    private static String DB_PATH;
    private static String DB_NAME = "database.db";
    private static int DB_VERSION = 1;
    private static String DB_TBL_Points = "points";
    private static String DB_TBL_SETTINGS = "settings";

    private SQLiteDatabase db;

    public AssetDatabaseHelper( Context con )
    {
        super(con, DB_NAME, null, DB_VERSION);

        main_context = con;

        DB_PATH = con.getCacheDir().getPath() + "/" + DB_NAME;
    }

    @Override
    public void onCreate(SQLiteDatabase db)
    {
        /* do nothing */
    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion)
    {
        /* do nothing */
    }

    private boolean dbExists()
    {
        File f = new File( DB_PATH );
        if( f.exists() )
            return true;
        else
            return false;
    }

    private boolean copyDB()
    {
        try
        {
            FileOutputStream out = new FileOutputStream( DB_PATH );

            InputStream in = main_context.getAssets().open( DB_NAME );

            byte[] buffer = new byte[1024];
            int ch;

            while( ( ch = in.read( buffer) ) > 0 )
            {
                out.write( buffer , 0 , ch);
            }

            out.flush();
            out.close();
            in.close();

            return true;
        }
        catch( Exception e )
        {
            /* do nothing */
            return false;
        }
    }

    public void open()
    {
        if( dbExists() )
        {
            try
            {
                File temp = new File( DB_PATH );

                db = SQLiteDatabase.openDatabase(
                        temp.getAbsolutePath() , null , SQLiteDatabase.OPEN_READWRITE
                );
            }
            catch(Exception e)
            {
                /* do nothing */
            }
        }
        else
        {
            if( copyDB() )
                open();
        }
    }

    @Override
    public synchronized void close()
    {
        db.close();
    }



    public List<PointModel> get_points()
    {
        Cursor result = db.rawQuery(
                "SELECT * FROM " + DB_TBL_Points  , null
        );

        List<PointModel> all_data = new ArrayList<>();

        while( result.moveToNext() )
        {
            PointModel temp = new PointModel(result.getLong(0),result.getDouble( 1 ),result.getDouble( 2 ),result.getString( 3 ));

            all_data.add( temp );
        }

        return all_data;
    }

    public void Save_Point(PointModel pointModel)
    {


            String sql="INSERT INTO " + DB_TBL_Points+"(id,lng, lat, tag) VALUES("+pointModel.getId()+", "+pointModel.getLng()+", "+pointModel.getLat()+", '"+pointModel.getTag()+"')";
            db.execSQL(sql);


    }





}