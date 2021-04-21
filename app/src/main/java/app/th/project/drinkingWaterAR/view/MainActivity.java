package app.th.project.drinkingWaterAR.view;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.location.Location;
import android.os.AsyncTask;
import android.os.Bundle;
import android.os.Handler;
import android.os.Looper;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.view.View.OnTouchListener;
import android.widget.Button;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.Toast;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.app.AlertDialog.Builder;
import androidx.appcompat.widget.AppCompatTextView;

import app.th.project.drinkingWaterAR.R;
import app.th.project.drinkingWaterAR.R.id;
import app.th.project.drinkingWaterAR.model.PlaceItem;
import app.th.project.drinkingWaterAR.model.PlaceList;
import app.th.project.drinkingWaterAR.utils.LocationDisplayUtil;
import app.th.project.drinkingWaterAR.utils.PermissionUtil;
import com.google.ar.core.Anchor;
import com.google.ar.core.Camera;
import com.google.ar.core.Frame;
import com.google.ar.core.Session;
import com.google.ar.core.TrackingState;
import com.google.ar.core.exceptions.CameraNotAvailableException;
import com.google.ar.core.exceptions.UnavailableException;
import com.google.ar.sceneform.ArSceneView;
import com.google.ar.sceneform.FrameTime;
import com.google.ar.sceneform.Node;
import com.google.ar.sceneform.Scene.OnUpdateListener;
import com.google.ar.sceneform.rendering.Renderable;
import com.google.ar.sceneform.rendering.ViewRenderable;
import java.lang.ref.WeakReference;
import java.util.ArrayList;
import java.util.Collection;
import java.util.LinkedHashSet;
import java.util.List;
import java.util.Set;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.ExecutionException;
import java.util.function.BiFunction;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import uk.co.appoly.arcorelocation.LocationMarker;
import uk.co.appoly.arcorelocation.LocationScene;
import uk.co.appoly.arcorelocation.LocationMarker.ScalingMode;
import uk.co.appoly.arcorelocation.rendering.LocationNode;
import uk.co.appoly.arcorelocation.rendering.LocationNodeRender;
import uk.co.appoly.arcorelocation.sensor.DeviceLocation;

public final class MainActivity extends AppCompatActivity {
    private boolean arCoreInstallRequested;
    private LocationScene locationScene;
    private Handler arHandler = new Handler(Looper.getMainLooper());
    @NotNull
    public AlertDialog loadingDialog;
    private final Runnable resumeARTask = (Runnable)(new Runnable() {
        public final void run() {
            LocationScene locationScene = MainActivity.this.locationScene;
            if (locationScene != null) {
                locationScene.resume();
            }

            try {
                ((ArSceneView) MainActivity.this.findViewById(id.arSceneView)).resume();
            } catch (CameraNotAvailableException e) {
                e.printStackTrace();
            }
        }
    });
    private Set buildingsSet;
    private boolean areAllPinsLoaded;

    public MainActivity() {
        Set set = new LinkedHashSet();
        this.buildingsSet = set;
    }

    protected void onCreate(@Nullable Bundle savedInstanceState) {
//        Log.d("Entered inside onCreate", "savedInstanceState.toString()");
        super.onCreate(savedInstanceState);
        this.setContentView(R.layout.main);
        this.renderMarkers();
        try {
            this.startLoadingDialog();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    protected void onResume() {
//        Log.d("Entered inside onResume", "onResume");
        super.onResume();
        this.callPermissionUtil();
    }

    protected void onPause() {
//        Log.d("Entered inside onPause", "onPause");
        super.onPause();
        ArSceneView arSceneView = (ArSceneView)this.findViewById(id.arSceneView);
        if (arSceneView.getSession() != null) {
            LocationScene locationScene = this.locationScene;
            if (locationScene != null) {
                locationScene.pause();
            }
//            arSceneView = (ArSceneView)this.findViewById(id.arSceneView);
            if (arSceneView != null) {
                arSceneView.pause();
            }
        }

    }
    protected void onStop() {
//        Log.d("Entered inside onStop", "onStop");
        super.onStop();
    }

    private final void startLoadingDialog() throws Exception {
        Builder alertDialogBuilder = new Builder((Context)this);
        View loadingDialog = LayoutInflater.from((Context)this).inflate(R.layout.loading_dialog, (ViewGroup)null);
        if (loadingDialog == null) {
            throw new Exception("null cannot be cast to non-null type android.widget.LinearLayout");
        } else {
            LinearLayout dialogHintMainView = (LinearLayout)loadingDialog;
            alertDialogBuilder.setView((View)dialogHintMainView);
            AlertDialog alertDialog = alertDialogBuilder.create();
            this.loadingDialog = alertDialog;
            this.loadingDialog.setCanceledOnTouchOutside(false);
        }
    }

    public final void onButtonTapped(@NotNull View view) {

        if (view instanceof Button) {
            this.onPause();
            Intent intent = new Intent((Context)this, SignInActivity.class);
            this.startActivity(intent);
        }
    }

    private final void setupSession() {
        if ((ArSceneView)this.findViewById(id.arSceneView) != null) {
            ArSceneView arSceneView = (ArSceneView)this.findViewById(id.arSceneView);
            if (arSceneView.getSession() == null) {
                try {
                    Session session = LocationDisplayUtil.setupSession((Activity)this, this.arCoreInstallRequested);
                    if (session == null) {
                        this.arCoreInstallRequested = true;
                        return;
                    }

                    ((ArSceneView)this.findViewById(id.arSceneView)).setupSession(session);
                } catch (UnavailableException ex) {
                    LocationDisplayUtil.handleSessionException((Activity)this, ex);
                }
            }

            if (this.locationScene == null) {
                this.locationScene = new LocationScene((Activity)this, (ArSceneView)this.findViewById(id.arSceneView));
                LocationScene locationScene = this.locationScene;
                locationScene.setMinimalRefreshing(true);
                locationScene.setOffsetOverlapping(true);
                locationScene.setAnchorRefreshInterval(2000);
            }

            try {
                this.resumeARTask.run();
            } catch (Exception ex) {
                Toast.makeText((Context)this, (CharSequence)"Failed to start device camera", Toast.LENGTH_LONG).show();
                this.finish();
                return;
            }

            LocationAsyncTask locationAsyncTask = new LocationAsyncTask(new WeakReference(this));
            LocationScene[] locationScenes = new LocationScene[1];
            LocationScene locationScene = this.locationScene;
            locationScenes[0] = locationScene;
            locationAsyncTask.execute((Object[]) locationScenes);

        }
    }

    private final void renderMarkers() {
        this.setupBuildingsMarkers();
        this.updateBuildingsMarkers();
    }

    private final void setupBuildingsMarkers() {
        Set venuesSet = this.buildingsSet;
        List placeList = PlaceList.getBuildings();
        Log.d("Entered inside setUpAndRender", placeList.toString());
        venuesSet.addAll((Collection)placeList);
        venuesSet.forEach((venue)->
        {
            CompletableFuture completableFutureViewRenderable =
                    ViewRenderable.builder().setView((Context)this, R.layout.location_layout_renderable).build();
            CompletableFuture
                    .anyOf(completableFutureViewRenderable)
                    .handle((BiFunction)(new setupBuildingsMarkersBiFunction((PlaceItem) venue, completableFutureViewRenderable, this)));
        });
    }

    private final void updateBuildingsMarkers() {
        ArSceneView arSceneView = (ArSceneView)this.findViewById(id.arSceneView);
        arSceneView.getScene().addOnUpdateListener((OnUpdateListener)(new OnUpdateListener() {
            public final void onUpdate(FrameTime it) {
                if (MainActivity.this.areAllPinsLoaded) {
                    LocationScene locationScene = MainActivity.this.locationScene; // var10000
                    if (locationScene != null) {
                        ArrayList<LocationMarker> locationMarkersList = locationScene.mLocationMarkers;
                        if (locationMarkersList != null) {
                            locationMarkersList.forEach((marker) ->{
                                LocationNode node = marker.anchorNode;
                                marker.setHeight(LocationDisplayUtil.createHeight(node != null? node.getDistance() : 0));
                            });
                        }
                    }

                    ArSceneView arSceneView1 = (ArSceneView) MainActivity.this.findViewById(id.arSceneView);

                    Frame sceneViewArFrame = arSceneView1.getArFrame();
                    if (sceneViewArFrame != null) {
                        Frame frame = sceneViewArFrame;
                        Camera cam = frame.getCamera();
                        if (cam.getTrackingState() == TrackingState.TRACKING) {
                            locationScene = MainActivity.this.locationScene;
                            locationScene.processFrame(frame);
                        }
                    }
                }
            }
        }));
    }

    private final void bindMarkerToScene(final LocationMarker locationMarker, final View layoutRendarable) {
        locationMarker.setScalingMode(ScalingMode.FIXED_SIZE_ON_SCREEN);
        locationMarker.setScaleModifier(0.5F);
        LocationScene locationScene = this.locationScene;
        if (locationScene != null) {
            ArrayList markersList = locationScene.mLocationMarkers;
            if (markersList != null) {
                markersList.add(locationMarker);
            }
        }

        LocationNode locationNode = locationMarker.anchorNode;
        if (locationNode != null) {
            locationNode.setEnabled(true);
        }

        this.arHandler.post((Runnable)(new attachMarkerToSceneRunnable(this, locationMarker, layoutRendarable)));
        locationMarker.setRenderEvent((LocationNodeRender)(new LocationNodeRender() {
            public final void render(LocationNode locationNode) {
                AppCompatTextView textView = (AppCompatTextView)layoutRendarable.findViewById(id.distance);
                LocationDisplayUtil locationUtils = new LocationDisplayUtil();
                textView.setText((CharSequence)locationUtils.showDistance(locationNode.getDistance()));
                MainActivity.this.calculateNewScaleModifier(locationMarker, locationNode.getDistance());
            }
        }));
    }

    private final void calculateNewScaleModifier(LocationMarker locationMarker, int distance) {
        float scaleModifier = LocationDisplayUtil.distanceBasedModifier(distance);
        if (scaleModifier == -1.0F) {
            this.disconnectMarker(locationMarker);
        } else {
            locationMarker.setScaleModifier(scaleModifier);
        }

    }

    private final void disconnectMarker(LocationMarker locationMarker) {
        LocationNode locationNode = locationMarker.anchorNode;
        if (locationNode != null) {
            Anchor nodeAnchor = locationNode.getAnchor();
            if (nodeAnchor != null) {
                nodeAnchor.detach();
            }
        }

        locationNode = locationMarker.anchorNode;
        if (locationNode != null) {
            locationNode.setEnabled(false);
        }

        locationMarker.anchorNode = (LocationNode)null;
    }

    private final Node setWaterLocationNode(final PlaceItem venue, CompletableFuture completableFuture) throws ExecutionException, InterruptedException {
        Node node = new Node();
        node.setRenderable((Renderable)completableFuture.get());
        Object obj = completableFuture.get();

        View nodeLayout = ((ViewRenderable)obj).getView();

        AppCompatTextView venueName = (AppCompatTextView)nodeLayout.findViewById(id.name);
        RelativeLayout markerLayoutContainer = (RelativeLayout)nodeLayout.findViewById(id.pinContainer);

        venueName.setText((CharSequence)venue.getName());

        markerLayoutContainer.setVisibility(View.GONE);
        nodeLayout.setOnTouchListener(new OnTouchListener() {
            @Override
            public boolean onTouch(View v, MotionEvent event) {
                Toast.makeText(MainActivity.this, (CharSequence)venue.getDescription(), Toast.LENGTH_LONG).show();
                return false;
            }
        });
        return node;
    }

    private final void callPermissionUtil() {
        if (!PermissionUtil.hasRequiredPermissions((Activity)this)) {
//            Log.d("here entered to check permisssion", "check permission");
            PermissionUtil.requestRequiredPermissions((Activity)this);
        } else {
//            Log.d("here entered to setup session", "session setup");
            this.setupSession();
        }

    }

    public void onRequestPermissionsResult(int requestCode, @NotNull String[] permissions, @NotNull int[] results) {
        if (!PermissionUtil.hasRequiredPermissions((Activity)this)) {
            Toast.makeText((Context)this, R.string.camera_and_location_permission_request, Toast.LENGTH_LONG).show();
            if (!PermissionUtil.shouldShowRequestPermissionRationale((Activity)this)) {
                PermissionUtil.startPermissionSettings((Activity)this);
            }
            this.finish();
        }

    }



    public static final Node setWaterLocationNode(MainActivity activity, PlaceItem venue, CompletableFuture completableFuture) throws ExecutionException, InterruptedException {
        return activity.setWaterLocationNode(venue, completableFuture);
    }

    public static final Handler getArHandler(MainActivity activity) {
        return activity.arHandler;
    }

    public static final void bindMarkerToScene(MainActivity activity, LocationMarker locationMarker, View layoutRendarable) {
        activity.bindMarkerToScene(locationMarker, layoutRendarable);
    }

    public static final Set getVenuesSet(MainActivity activity) {
        return activity.buildingsSet;
    }

    public static final void setAreAllPinsLoaded(MainActivity activity, boolean bool) {
        activity.areAllPinsLoaded = bool;
    }


    final class setupBuildingsMarkersRunnable implements Runnable {

        final setupBuildingsMarkersBiFunction renderVenuesMarkersBiFunction;
        final LocationMarker locationMarker;

        setupBuildingsMarkersRunnable(setupBuildingsMarkersBiFunction renderVenuesMarkersBiFunction, LocationMarker locationMarker) {
            this.renderVenuesMarkersBiFunction = renderVenuesMarkersBiFunction;
            this.locationMarker = locationMarker;
        }

        public final void run() {
            MainActivity activity = this.renderVenuesMarkersBiFunction.mainActivity;
            Object getFuture = null;
            try {
                getFuture = this.renderVenuesMarkersBiFunction.completableFutureViewRenderable.get();
            } catch (ExecutionException e) {
                e.printStackTrace();
            } catch (InterruptedException e) {
                e.printStackTrace();
            }

            View getFutureView = ((ViewRenderable)getFuture).getView();

            MainActivity.bindMarkerToScene(activity, this.locationMarker, getFutureView);
            MainActivity.setAreAllPinsLoaded(this.renderVenuesMarkersBiFunction.mainActivity, true);


        }
    }

    final class attachMarkerToSceneRunnable implements Runnable {
        final MainActivity mainActivity;
        final LocationMarker locationMarker;
        final View layoutRenderable;

        attachMarkerToSceneRunnable(MainActivity activity, LocationMarker locationMarker, View layoutRenderable) {
            this.mainActivity = activity;
            this.locationMarker = locationMarker;
            this.layoutRenderable = layoutRenderable;
        }

        public final void run() {

            RelativeLayout relativeLayout = (RelativeLayout)this.layoutRenderable.findViewById(id.pinContainer);

            relativeLayout.setVisibility(View.VISIBLE);
        }
    }


    final class setupBuildingsMarkersBiFunction implements BiFunction {
        final PlaceItem placeItem;
        final CompletableFuture completableFutureViewRenderable;
        final MainActivity mainActivity;

        setupBuildingsMarkersBiFunction(PlaceItem placeItem, CompletableFuture completableFuture, MainActivity mainActivity) {
            this.placeItem = placeItem;
            this.completableFutureViewRenderable = completableFuture;
            this.mainActivity = mainActivity;
        }


        public Object apply(Object o1, Object o2) {
            return this.apply((Throwable)o2);
        }

        @Nullable
        public final Void apply(Throwable throwable) {
            if (throwable != null) {
                return null;
            } else {
                try {
                    Double placeItemLon = Double.parseDouble(this.placeItem.getLon());
                    Double placeItemLat = Double.parseDouble(this.placeItem.getLat());

                    Node renderNode = MainActivity.setWaterLocationNode(this.mainActivity, this.placeItem, this.completableFutureViewRenderable);
                    LocationMarker venueMarker = new LocationMarker(placeItemLon, placeItemLat, renderNode);
                    MainActivity.getArHandler(this.mainActivity).postDelayed((Runnable)(new setupBuildingsMarkersRunnable(this, venueMarker)), 200L);
                } catch (Exception ex) {
                }
                return null;
            }
        }
    }

    public class LocationAsyncTask extends AsyncTask {
        private final WeakReference activityWeakReference;
        List<Double[]> listCoordinates = new ArrayList<>();
        protected void onPreExecute() {
            super.onPreExecute();
            Object obj = this.activityWeakReference.get();
            ((MainActivity)obj).loadingDialog.show();
        }

        @NotNull
        protected List doInBackground(LocationScene... p0) {

            Double deviceLatitude = null;
            Double deviceLongitude = null;
//            do {
//                deviceLatitude = p0[0].deviceLocation.currentBestLocation.getLatitude();
//                deviceLongitude = p0[0].deviceLocation.currentBestLocation.getLongitude();
//            } while (deviceLatitude == null || deviceLongitude == null);


            do {
                DeviceLocation deviceLocation;
                Location currentBestLocation;
                Double coordinate;
                lat: {
                    deviceLocation = p0[0].deviceLocation;
                    if (deviceLocation != null) {
                        currentBestLocation = deviceLocation.currentBestLocation;
                        if (currentBestLocation != null) {
                            coordinate = currentBestLocation.getLatitude();
                            break lat;
                        }
                    }

                    coordinate = null;
                }

                lon: {
                    deviceLatitude = coordinate;
                    deviceLocation = p0[0].deviceLocation;
                    if (deviceLocation != null) {
                        currentBestLocation = deviceLocation.currentBestLocation;
                        if (currentBestLocation != null) {
                            coordinate = currentBestLocation.getLongitude();
                            break lon;
                        }
                    }

                    coordinate = null;
                }

                deviceLongitude = coordinate;
            } while(deviceLatitude == null || deviceLongitude == null);
            listCoordinates.add(new Double[]{deviceLatitude, deviceLongitude});
            return listCoordinates;
        }

        public Object doInBackground(Object[] var1) {
            return this.doInBackground((LocationScene[])var1);
        }

        protected void onPostExecute(@NotNull List geolocation) {
            Object obj = this.activityWeakReference.get();

            ((MainActivity)obj).loadingDialog.dismiss();
        }

        public void onPostExecute(Object var1) {
            this.onPostExecute((List)var1);
        }

        public LocationAsyncTask(@NotNull WeakReference activityWeakReference) {
            super();
            this.activityWeakReference = activityWeakReference;
        }
    }
}
