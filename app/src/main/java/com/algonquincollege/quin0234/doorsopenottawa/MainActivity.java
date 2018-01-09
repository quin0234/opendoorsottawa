package com.algonquincollege.quin0234.doorsopenottawa;

import android.app.Activity;
import android.app.LoaderManager;
import android.content.AsyncTaskLoader;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.Loader;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.net.Uri;
import android.os.Bundle;
import android.support.v4.content.LocalBroadcastManager;
import android.support.v7.widget.RecyclerView;
import android.widget.Toast;

import com.algonquincollege.quin0234.doorsopenottawa.model.BuildingPOJO;
import com.algonquincollege.quin0234.doorsopenottawa.services.MyService;
import com.algonquincollege.quin0234.doorsopenottawa.utils.NetworkHelper;

import java.io.IOException;
import java.io.InputStream;
import java.net.URL;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

//Paul Quinnell
//Main Activity
//DoorsOpenOttawa

public class MainActivity extends Activity
        implements LoaderManager.LoaderCallbacks<Map<Integer, Bitmap>> {

    private static final String JSON_URL;

    private Map<Integer, Bitmap> mBitmaps;
    private BuildingAdapter        mBuildingAdapter;
    private List<BuildingPOJO>     mBuildingList;
    private boolean              networkOk;
    private RecyclerView         mRecyclerView;

    static {
        JSON_URL = "https://doors-open-ottawa.mybluemix.net/buildings";
    }

    private BroadcastReceiver mBroadcastReceiver = new BroadcastReceiver() {
        @Override
        public void onReceive(Context context, Intent intent) {
            if (intent.hasExtra(MyService.MY_SERVICE_PAYLOAD)) {
                BuildingPOJO[] BuildingsArray = (BuildingPOJO[]) intent
                        .getParcelableArrayExtra(MyService.MY_SERVICE_PAYLOAD);
                Toast.makeText(MainActivity.this,
                        "Received " + BuildingsArray.length + " Buildings from service",
                        Toast.LENGTH_SHORT).show();

                mBuildingList = Arrays.asList(BuildingsArray);

                getLoaderManager().initLoader(0, null, MainActivity.this).forceLoad();

            } else if (intent.hasExtra(MyService.MY_SERVICE_EXCEPTION)) {
                String message = intent.getStringExtra(MyService.MY_SERVICE_EXCEPTION);
                Toast.makeText(context, message, Toast.LENGTH_SHORT).show();
            }
        }
    };

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        mRecyclerView = (RecyclerView) findViewById(R.id.rvBuildings);


        LocalBroadcastManager.getInstance(getApplicationContext())
                .registerReceiver(mBroadcastReceiver,
                        new IntentFilter(MyService.MY_SERVICE_MESSAGE));

        networkOk = NetworkHelper.hasNetworkAccess(this);
        if (networkOk) {
            Intent intent = new Intent(this, MyService.class);
            intent.setData(Uri.parse(JSON_URL));
            startService(intent);
        } else {
            Toast.makeText(this, "Network not available", Toast.LENGTH_SHORT).show();
        }
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();

        LocalBroadcastManager.getInstance(getApplicationContext())
                .unregisterReceiver(mBroadcastReceiver);
    }

    private void displayBuildings() {
        if (mBuildingList != null) {
            mBuildingAdapter = new BuildingAdapter(this, mBuildingList);
            mRecyclerView.setAdapter(mBuildingAdapter);
        }
    }

    @Override
    public Loader<Map<Integer, Bitmap>> onCreateLoader(int i, Bundle bundle) {
        return new ImageDownloader(this, mBuildingList);
    }

    @Override
    public void onLoadFinished(Loader<Map<Integer, Bitmap>> loader, Map<Integer, Bitmap> integerBitmapMap) {
        mBitmaps = integerBitmapMap;
        displayBuildings();
    }

    @Override
    public void onLoaderReset(Loader<Map<Integer, Bitmap>> loader) {
        // NO-OP
    }

    private static class ImageDownloader
            extends AsyncTaskLoader<Map<Integer, Bitmap>> {

        private static final String PHOTOS_BASE_URL = JSON_URL + "/";
        private static List<BuildingPOJO> mBuildingList;

        public ImageDownloader(Context context, List<BuildingPOJO> BuildingList) {
            super(context);
            mBuildingList = BuildingList;
        }

        @Override
        public Map<Integer, Bitmap> loadInBackground() {
            //download image files here
            Map<Integer, Bitmap> map = new HashMap<>();
            for (BuildingPOJO aBuilding: mBuildingList) {
                String imageUrl = PHOTOS_BASE_URL + aBuilding.getBuildingId() + "/image";
                InputStream in = null;

                try {
                    in = (InputStream) new URL(imageUrl).getContent();
                    Bitmap bitmap = BitmapFactory.decodeStream(in);
                    map.put(aBuilding.getBuildingId(), bitmap);
                } catch (IOException e) {
                    e.printStackTrace();
                } finally {
                    try {
                        in.close();
                    } catch (IOException e) {
                        e.printStackTrace();
                    }
                }
            }

            return map;
        }   // end method loadInBackground
    }   // end class ImageDownloader
}
