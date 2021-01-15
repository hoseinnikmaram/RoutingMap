package com.nikmaram.map.utility;

import android.content.Context;
import android.graphics.BitmapFactory;
import android.util.Log;

import com.nikmaram.map.R;
import com.nikmaram.map.database_helper.AssetDatabaseHelper;
import com.nikmaram.map.model.PointModel;

import org.neshan.core.LngLat;
import org.neshan.core.LngLatVector;
import org.neshan.core.Variant;
import org.neshan.geometry.LineGeom;
import org.neshan.graphics.ARGB;
import org.neshan.layers.VectorElementLayer;
import org.neshan.styles.AnimationStyle;
import org.neshan.styles.AnimationStyleBuilder;
import org.neshan.styles.AnimationType;
import org.neshan.styles.LineStyle;
import org.neshan.styles.LineStyleCreator;
import org.neshan.styles.MarkerStyle;
import org.neshan.styles.MarkerStyleCreator;
import org.neshan.ui.MapView;
import org.neshan.utils.BitmapUtils;
import org.neshan.vectorelements.Line;
import org.neshan.vectorelements.Marker;

import java.util.List;

public class MapTools {
    public static Boolean is_marker_first=true;
    public static Marker marker_previous;


    public static void getDBPoints(Context context, VectorElementLayer markerLayer, MapView map){
        // we create an AssetDatabaseHelper object, create a new database in mobile storage
        // and copy database.sqlite file into the new created database
        // Then we open the database and return the SQLiteDatabase object
        AssetDatabaseHelper myDbHelper = new AssetDatabaseHelper(context);

        myDbHelper.open();




        for (PointModel pointModel:myDbHelper.get_points()) {

            double lng = pointModel.getLng();
            double lat = pointModel.getLat();
            Long id = pointModel.getId();
            LngLat lngLat = new LngLat(lng, lat);

            Log.i("ppp", id.toString());
            addMarker(lngLat,context,markerLayer,id);

        }



        myDbHelper.close();
    }




    public static void addMarker(LngLat loc, Context context, VectorElementLayer markerLayer, long id){
        // If you want to have only one marker on map at a time, uncomment next line to delete all markers before adding a new marker

        // Creating animation for marker. We should use an object of type AnimationStyleBuilder, set
        // all animation features on it and then call buildStyle() method that returns an object of type
        // AnimationStyle
        AnimationStyleBuilder animStBl = new AnimationStyleBuilder();
        animStBl.setFadeAnimationType(AnimationType.ANIMATION_TYPE_SMOOTHSTEP);
        animStBl.setSizeAnimationType(AnimationType.ANIMATION_TYPE_SPRING);
        animStBl.setPhaseInDuration(0.5f);
        animStBl.setPhaseOutDuration(0.5f);
        AnimationStyle animSt = animStBl.buildStyle();

        // Creating marker style. We should use an object of type MarkerStyleCreator, set all features on it
        // and then call buildStyle method on it. This method returns an object of type MarkerStyle
        MarkerStyleCreator markStCr = new MarkerStyleCreator();
        markStCr.setSize(30f);
        markStCr.setBitmap(BitmapUtils.createBitmapFromAndroidBitmap(BitmapFactory.decodeResource(context.getResources(), R.drawable.ic_marker_blue)));
        // AnimationStyle object - that was created before - is used here
        markStCr.setAnimationStyle(animSt);
        MarkerStyle markSt = markStCr.buildStyle();

        // Creating marker
        Marker marker = new Marker(loc, markSt);
        // Setting a metadata on marker, here we have an id for each marker
        marker.setMetaDataElement("id", new Variant(id));

        // Adding marker to markerLayer, or showing marker on map!
        markerLayer.add(marker);
    }
    // This method gets a LngLat as input and adds a marker on that position
    public static void addUserMarker(LngLat loc, Context context, VectorElementLayer markerLayer){
        // Creating marker style. We should use an object of type MarkerStyleCreator, set all features on it
        // and then call buildStyle method on it. This method returns an object of type MarkerStyle
        MarkerStyleCreator markStCr = new MarkerStyleCreator();
        markStCr.setSize(33f);

        markStCr.setBitmap(BitmapUtils.createBitmapFromAndroidBitmap(BitmapFactory.decodeResource(context.getResources(), R.drawable.ic_locate)));
        MarkerStyle markSt = markStCr.buildStyle();

        // Creating user marker
        Marker marker = new Marker(loc, markSt);

        if(is_marker_first){
            marker_previous=marker;
            is_marker_first=false;
            Log.i("aaa", "addUserMarker: is first");

        }
        else {
        marker_previous.delete();
            Log.i("aaa", "addUserMarker: delete");

        }
        markerLayer.add(marker);

        // Adding user marker to userMarkerLayer, or showing marker on map!
      //  markerLayer.add(marker);
    }
    public static void changeMarkerToBlue(Context context,Marker redMarker){
        // create new marker style
        MarkerStyleCreator markStCr = new MarkerStyleCreator();
        markStCr.setSize(30f);
        // Setting a new bitmap as marker
        markStCr.setBitmap(BitmapUtils.createBitmapFromAndroidBitmap(BitmapFactory.decodeResource(context.getResources(), R.drawable.ic_marker_blue)));
        MarkerStyle blueMarkSt = markStCr.buildStyle();

        // changing marker style using setStyle
        redMarker.setStyle(blueMarkSt);
    }
    // change deselected marker color to red
    public static void changeMarkerToRed(Context context,Marker blueMarker){
        // create new marker style
        MarkerStyleCreator markStCr = new MarkerStyleCreator();
        markStCr.setSize(30f);
        // Setting a new bitmap as marker
        markStCr.setBitmap(BitmapUtils.createBitmapFromAndroidBitmap(BitmapFactory.decodeResource(context.getResources(), R.drawable.ic_marker)));
        MarkerStyle redMarkSt = markStCr.buildStyle();

        // changing marker style using setStyle
        blueMarker.setStyle(redMarkSt);
    }

    // Drawing line on map
    public static LineGeom drawLineGeom(List<PolylineEncoding.LatLng> paths, VectorElementLayer lineLayer) {
        // we clear every line that is currently on map
        lineLayer.clear();
        // Adding some LngLat points to a LngLatVector
        LngLatVector lngLatVector = new LngLatVector();
        for (PolylineEncoding.LatLng path : paths) {
            lngLatVector.add(new LngLat(path.lng, path.lat));
        }

        // Creating a lineGeom from LngLatVector
        LineGeom lineGeom = new LineGeom(lngLatVector);
        // Creating a line from LineGeom. here we use getLineStyle() method to define line styles
        Line line = new Line(lineGeom, getLineStyle());
        // adding the created line to lineLayer, showing it on map
        lineLayer.add(line);

        return lineGeom;
    }



    // In this method we create a LineStyleCreator, set its features and call buildStyle() method
    // on it and return the LineStyle object (the same routine as crating a marker style)
    private static LineStyle getLineStyle(){
        LineStyleCreator lineStCr = new LineStyleCreator();
        lineStCr.setColor(new ARGB((short) 2, (short) 119, (short) 189, (short)190));
        lineStCr.setWidth(10f);
        lineStCr.setStretchFactor(0f);
        return lineStCr.buildStyle();
    }

    public static void SaveDBPoint(Context context, PointModel pointModel){

        AssetDatabaseHelper myDbHelper = new AssetDatabaseHelper(context);

        myDbHelper.open();



        myDbHelper.Save_Point(pointModel);


        myDbHelper.close();
    }




}
