package com.example.myapplication;

import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import org.osmdroid.DefaultResourceProxyImpl;
import org.osmdroid.ResourceProxy;
import org.osmdroid.api.IMapController;
import org.osmdroid.tileprovider.MapTileProviderBasic;
import org.osmdroid.tileprovider.tilesource.TileSourceFactory;
import org.osmdroid.tileprovider.tilesource.XYTileSource;
import org.osmdroid.util.GeoPoint;
import org.osmdroid.views.MapView;
import org.osmdroid.views.overlay.ItemizedIconOverlay;
import org.osmdroid.views.overlay.MyLocationOverlay;
import org.osmdroid.views.overlay.OverlayItem;
import org.osmdroid.views.overlay.ScaleBarOverlay;
import org.osmdroid.views.overlay.SimpleLocationOverlay;
import org.osmdroid.views.overlay.TilesOverlay;

import android.database.sqlite.SQLiteDatabase;
import android.location.Location;
import android.location.LocationListener;
import android.location.LocationManager;
import android.os.Build;
import android.os.Bundle;
import android.provider.Settings;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.app.ActivityCompat;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.Button;

public class mapinit extends AppCompatActivity {

    private MapView map;
    private SimpleLocationOverlay mMyLocationOverlay;
    private ScaleBarOverlay mScaleBarOverlay;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_mapinit);
        map = (MapView) findViewById(R.id.map);

        map.setTileSource(new XYTileSource("OSMPublicTransport",ResourceProxy.string.public_transport, 0, 16, 256, ".png", new String[] {
                "http://otile1.mqcdn.com/tiles/1.0.0/map/",
                "http://otile2.mqcdn.com/tiles/1.0.0/map/",
                "http://otile3.mqcdn.com/tiles/1.0.0/map/",
                "http://otile4.mqcdn.com/tiles/1.0.0/map/"}));
        map.setBuiltInZoomControls(true);
        map.setMultiTouchControls(true);
        map.setUseDataConnection(false); //optional, but a good way to prevent loading from the network and test your zip loading.
        IMapController mapController = map.getController();
        mapController.setZoom(14);
        LocationManager locationManger=(LocationManager) getSystemService(LOCATION_SERVICE);
        Location lastLocation=locationManger.getLastKnownLocation(LocationManager.GPS_PROVIDER);
        GeoPoint startPoint;
        if(lastLocation==null)
        {
            startPoint = new GeoPoint(22.5567,88.3022);
        }
        else
        {
            startPoint = new GeoPoint(lastLocation.getLatitude(),lastLocation.getLongitude());
           //!sr
        }

        mapController.setCenter(startPoint);


    }
}