package nctu.cs.cgv.itour.fragment;

import android.app.Dialog;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Matrix;
import android.net.Uri;
import android.os.Bundle;
import android.os.Handler;
import android.support.annotation.NonNull;
import android.support.design.widget.BottomSheetDialog;
import android.support.design.widget.FloatingActionButton;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.content.res.ResourcesCompat;
import android.support.v7.app.ActionBar;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.preference.PreferenceManager;
import android.util.Log;
import android.view.GestureDetector;
import android.view.KeyEvent;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.ScaleGestureDetector;
import android.view.View;
import android.view.ViewGroup;
import android.view.Window;
import android.view.WindowManager;
import android.view.inputmethod.EditorInfo;
import android.widget.Button;
import android.widget.EditText;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.ChildEventListener;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.Query;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.Objects;

import nctu.cs.cgv.itour.R;
import nctu.cs.cgv.itour.Utility;
import nctu.cs.cgv.itour.activity.CheckinActivity;
import nctu.cs.cgv.itour.activity.MainActivity;
import nctu.cs.cgv.itour.custom.ReportManager;
import nctu.cs.cgv.itour.custom.RotationGestureDetector;
import nctu.cs.cgv.itour.object.Checkin;
import nctu.cs.cgv.itour.object.CheckinNode;
import nctu.cs.cgv.itour.object.SpotNode;
import nctu.cs.cgv.itour.object.TogoPlannedData;

import static android.content.Context.LAYOUT_INFLATER_SERVICE;
import static nctu.cs.cgv.itour.MyApplication.MAP_DISPLAY_COMMUNITY;
import static nctu.cs.cgv.itour.MyApplication.MAX_ZOOM;
import static nctu.cs.cgv.itour.MyApplication.MIN_ZOOM;
import static nctu.cs.cgv.itour.MyApplication.VERSION_ALL_FEATURE;
import static nctu.cs.cgv.itour.MyApplication.VERSION_ONLY_GOOGLE_COMMENT;
import static nctu.cs.cgv.itour.MyApplication.VERSION_OPTION;
import static nctu.cs.cgv.itour.MyApplication.ZOOM_THRESHOLD;
import static nctu.cs.cgv.itour.MyApplication.dirPath;
import static nctu.cs.cgv.itour.MyApplication.mapTag;
import static nctu.cs.cgv.itour.MyApplication.realMesh;
import static nctu.cs.cgv.itour.MyApplication.sourceMapTag;
import static nctu.cs.cgv.itour.MyApplication.spotList;
import static nctu.cs.cgv.itour.Utility.gpsToImgPx;
import static nctu.cs.cgv.itour.Utility.spToPx;
import static nctu.cs.cgv.itour.activity.MainActivity.collectedCheckinIsVisited;
import static nctu.cs.cgv.itour.activity.MainActivity.collectedCheckinKey;
import static nctu.cs.cgv.itour.activity.MainActivity.firebaseLogManager;
import static nctu.cs.cgv.itour.object.FirebaseLogData.LOG_CHECKIN_NAVIGATE;
import static nctu.cs.cgv.itour.object.FirebaseLogData.LOG_REPORT_ANYWHERE;
import static nctu.cs.cgv.itour.object.FirebaseLogData.LOG_REPORT_CHECKIN;
import static nctu.cs.cgv.itour.object.FirebaseLogData.LOG_REPORT_TOGO;
import static nctu.cs.cgv.itour.object.FirebaseLogData.LOG_TOGO_NAVIGATE;
import static nctu.cs.cgv.itour.object.FirebaseLogData.LOG_NOTE_IS_COLLECTED_CHECKIN;
import static nctu.cs.cgv.itour.object.FirebaseLogData.LOG_NOTE_IS_COLLECTED_TOGO;
import static nctu.cs.cgv.itour.object.FirebaseLogData.LOG_NOTE_IS_NOT_COLLECTED_TOGO;
import static nctu.cs.cgv.itour.object.FirebaseLogData.LOG_NOTE_IS_OTHER_CHECKIN;
import static nctu.cs.cgv.itour.object.FirebaseLogData.LOG_NOTE_IS_SELF_CHECKIN;

public class PersonalMapFragment extends Fragment {

    private static final String TAG = "PersonalMapFragment";
    private Context context;
    // variables
    private Matrix transformMat;
    private float scale = 1;
    private float rotation = 0;
    private float gpsDistortedX = -1;
    private float gpsDistortedY = -1;
    private int mapCenterX = 0;
    private int mapCenterY = 0;
    private int checkinIconWidth;
    private int checkinIconHeight;
    private int gpsMarkerPivotX;
    private int gpsMarkerPivotY;
    private int checkinIconPivotX;
    private int checkinIconPivotY;
    private int mergedCheckinIconPivotX;
    private int mergedCheckinIconPivotY;
    private int spotIconPivotX;
    private int spotIconPivotY;
    private float lastLat, lastLng;
    //    private int edgeNodeIconPivotX;
//    private int edgeNodeIconPivotY;
    // UI references
    private RelativeLayout rootLayout;
    private ImageView touristMap;
    private ImageView fogMap;
    private LinearLayout gpsMarker;
    private FloatingActionButton reportBtn;
    private FloatingActionButton gpsBtn;
    private FloatingActionButton addBtn;
    private FloatingActionButton switchBtn;
    private Bitmap fogBitmap;
    private ActionBar actionBar;
    private View seperator;
    public String mapDisplayType = MAP_DISPLAY_COMMUNITY;
    private Query savePostIdQuery;
    private ChildEventListener savePostIdListener;
//    private Map<>

    // objects
//    private List<ImageNode> edgeNodeList;
//    private List<ImageNode> pathEdgeNodeList;


    private Map<String, CheckinNode> checkinNodeMap;
    private List<CheckinNode> checkinNodeList;
    private Map<String, CheckinNode> checkinClusterNodeMap;
    private List<CheckinNode> checkinClusterNodeList;
    private Map<String, CheckinNode> togoNodeMap;
    private List<CheckinNode> togoNodeList;


    public Map<String, SpotNode> spotNodeMap;
    private List<SpotNode> spotNodeList;
    private LayoutInflater inflater;
    private Handler translationHandler;
    // gestures
    private GestureDetector gestureDetector;
    private ScaleGestureDetector scaleGestureDetector;
    private RotationGestureDetector rotationGestureDetector;
    // settings
    private SharedPreferences preferences;
    // flags
    private boolean isGpsCurrent = false;
    private boolean isOrientationCurrent = true;
    private boolean checkinSwitch = true;
    private boolean spotSwitch = true;
    private Button reportAnywhereBtn;
//    private boolean fogSwitch = false;
//    private boolean edgeLengthSwitch = false;

    private String uid = ""; // Empty string means guest mode(getCurrentUser() == null)

    public static PersonalMapFragment newInstance() {
        return new PersonalMapFragment();
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        context = getContext();
        lastLat = 0;
        lastLng = 0;
        // load preferences
        preferences = PreferenceManager.getDefaultSharedPreferences(Objects.requireNonNull(context));

        // init objects
//        edgeNodeList = new ArrayList<>();
//        pathEdgeNodeList = new ArrayList<>();
        checkinNodeMap = new HashMap<>();
        checkinNodeList = new ArrayList<>();
        checkinClusterNodeMap = new HashMap<>();
        checkinClusterNodeList = new ArrayList<>();
        togoNodeList = new ArrayList<>();
        transformMat = new Matrix();
        inflater = (LayoutInflater) context.getSystemService(LAYOUT_INFLATER_SERVICE);
        translationHandler = new Handler();

        // init size variables
        checkinIconWidth = (int) getResources().getDimension(R.dimen.checkin_icon_width);
        checkinIconHeight = (int) getResources().getDimension(R.dimen.checkin_icon_height);
        int mergedCheckinIconWidth = (int) getResources().getDimension(R.dimen.merged_checkin_icon_width);
        int mergedCheckinIconHeight = (int) getResources().getDimension(R.dimen.merged_checkin_icon_height);
        int spotIconWidth = (int) getResources().getDimension(R.dimen.spot_icon_width);
        int spotIconHeight = (int) getResources().getDimension(R.dimen.spot_icon_height);
//        int edgeNodeIconWidth = (int) getResources().getDimension(R.dimen.edge_node_width);
//        int edgeNodeIconHeight = (int) getResources().getDimension(R.dimen.edge_node_height);
        int gpsMarkerWidth = (int) getResources().getDimension(R.dimen.gps_marker_width);
        int gpsMarkerHeight = (int) getResources().getDimension(R.dimen.gps_marker_height);
        int gpsDirectionHeight = (int) getResources().getDimension(R.dimen.gps_direction_height);
        int gpsMarkerPadding = (int) getResources().getDimension(R.dimen.gps_marker_padding);

        // init pivot variables
        gpsMarkerPivotX = gpsMarkerWidth / 2 + gpsMarkerPadding;
        gpsMarkerPivotY = gpsDirectionHeight + gpsMarkerHeight / 2 + gpsMarkerPadding;
        checkinIconPivotX = checkinIconWidth / 3;
        checkinIconPivotY = checkinIconHeight;
        mergedCheckinIconPivotX = mergedCheckinIconWidth / 3;
        mergedCheckinIconPivotY = mergedCheckinIconHeight;
        spotIconPivotX = spotIconWidth / 2;
        spotIconPivotY = spToPx(context, 14) / 2;
//        edgeNodeIconPivotX = edgeNodeIconWidth / 2;
//        edgeNodeIconPivotY = edgeNodeIconHeight / 2;

        if (FirebaseAuth.getInstance().getCurrentUser() != null) {
            uid = FirebaseAuth.getInstance().getCurrentUser().getUid();

        }


    }

    @Override
    public void onDestroy() {
        super.onDestroy();
    }

    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, ViewGroup parent, Bundle savedInstanceState) {
        return inflater.inflate(R.layout.fragment_personal_map, parent, false);
    }

    @Override
    public void onViewCreated(View view, Bundle savedInstanceState) {
        mapDisplayType = MAP_DISPLAY_COMMUNITY;
        rootLayout = view.findViewById(R.id.personal_map_layout);
        gpsMarker = view.findViewById(R.id.personal_gps_marker);
        reportBtn = view.findViewById(R.id.btn_report);
        gpsBtn = view.findViewById(R.id.btn_gps);
        addBtn = view.findViewById(R.id.btn_add);
        switchBtn = view.findViewById(R.id.btn_switch_map);
        if (VERSION_OPTION == VERSION_ONLY_GOOGLE_COMMENT) {
            switchBtn.setVisibility(View.GONE);
        }
        FrameLayout frameLayout = view.findViewById(R.id.prsonal_tourist_map);

        seperator = view.findViewById(R.id.personal_seperator);

        // set subtitle
        actionBar = ((AppCompatActivity) getActivity()).getSupportActionBar();
        actionBar.setSubtitle("社群地圖");

        // set tourist map
        Bitmap touristMapBitmap = BitmapFactory.decodeFile(dirPath + "/" + sourceMapTag + "_distorted_map.png");
        int touristMapWidth = touristMapBitmap.getWidth();
        int touristMapHeight = touristMapBitmap.getHeight();
        touristMap = new ImageView(context);
        touristMap.setLayoutParams(new RelativeLayout.LayoutParams(touristMapWidth, touristMapHeight));
        touristMap.setScaleType(ImageView.ScaleType.MATRIX);
        touristMap.setImageBitmap(touristMapBitmap);
        touristMap.setPivotX(0);
        touristMap.setPivotY(0);
        frameLayout.addView(touristMap);

        // draw fog
//        fogBitmap = Bitmap.createBitmap(touristMapWidth, touristMapHeight, Bitmap.Config.ARGB_8888);
//        Canvas canvas = new Canvas(fogBitmap);
//        canvas.drawARGB(120, 0, 0, 0);
//        fogMap = new ImageView(context);
//        fogMap.setLayoutParams(new RelativeLayout.LayoutParams(touristMapWidth, touristMapHeight));
//        fogMap.setScaleType(ImageView.ScaleType.MATRIX);
//        fogMap.setImageBitmap(fogBitmap);
//        fogMap.setPivotX(0);
//        fogMap.setPivotY(0);
//        frameLayout.addView(fogMap);

        // draw edge distance indicator
//        edgeNodeList = edgeNode.getNodeList();
//        for (ImageNode imageNode : edgeNodeList) {
//            addEdgeNode(imageNode, "black");
//        }

        // draw spots
        spotNodeMap = new LinkedHashMap<>(spotList.fullNodeMap);    // for search query
        spotNodeList = new ArrayList<>(spotNodeMap.values());   // for transformation
        for (SpotNode spotNode : spotNodeList) {
            addSpotNode(spotNode);
        }

        // set gpsMarker
        gpsMarker.setPivotX(gpsMarkerPivotX);
        gpsMarker.setPivotY(gpsMarkerPivotY);

        // set buttons
        gpsBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (gpsMarker.getVisibility() == View.GONE) {
                    Toast.makeText(context, getString(R.string.toast_gps_outside), Toast.LENGTH_SHORT).show();
                    return;
                }
                if (!isGpsCurrent)
                    translateToImgPx(gpsDistortedX, gpsDistortedY, true);
                else if (!isOrientationCurrent)
                    rotateToNorth();
            }
        });
        if (uid.equals("")) {
            addBtn.setVisibility(View.GONE);
        }
        addBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (gpsMarker.getVisibility() == View.GONE) {
                    Toast.makeText(context,
                            getString(R.string.toast_gps_outside) + "\n" + getString(R.string.toast_cannot_checkin),
                            Toast.LENGTH_SHORT).show();
                    return;
                }
                startActivity(new Intent(context, CheckinActivity.class));
            }
        });
        switchBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                switchMap();
            }
        });
        reportBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                reportAnywhereClicked();
            }
        });
        setTouchListener();

        setHasOptionsMenu(true);

//        switchFog(preferences.getBoolean("fog", false));
//        switchDistanceIndicator(preferences.getBoolean("distance_indicator", false));
        switchSpotIcon(preferences.getBoolean("spot", true));


//        ((MainActivity) getActivity()).queryCommentNotification();

        if (!uid.equals("")) {
            ((MainActivity) getActivity()).querySavedPostId();
        }

        // load checkin after map view set.
//        ((MainActivity) getActivity()).queryCheckin();
        rootLayout.post(new Runnable() {
            @Override
            public void run() {
                mapCenterX = rootLayout.getWidth() / 2;
                mapCenterY = rootLayout.getHeight() / 5 * 2;
            }
        });

        reRender();
//        //Log.d("NIVRAMMM", "Viewcreate");
    }

//    @Override
//    public void onCreateOptionsMenu(Menu menu, MenuInflater inflater) {
//        inflater.inflate(R.menu.menu_search, menu);
//        super.onCreateOptionsMenu(menu, inflater);
//
//        // set search view autocomplete
//        final ArrayAdapter<String> adapter = new ArrayAdapter<>(context, R.layout.item_search, new ArrayList<>(spotList.getPersonalSpotsName()));
//        final ArrayAdapterSearchView searchView = (ArrayAdapterSearchView) MenuItemCompat.getActionView(menu.findItem(R.id.action_search));
//        searchView.setAdapter(adapter);
//        searchView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
//            @Override
//            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
//                String autocompleteStr = adapter.getItem(position);
//                Node node = spotList.fullNodeMap.get(autocompleteStr);
//                translateToImgPx(node.x, node.y, false);
//                searchView.clearFocus();
//                searchView.setText(autocompleteStr);
//                // send action log to server
//                actionLog("search: ", autocompleteStr, "");
//            }
//        });
//    }

    @Override
    public void setUserVisibleHint(boolean isVisibleToUser) {
        super.setUserVisibleHint(isVisibleToUser);

        if (getUserVisibleHint()) {
//            //Log.d("NIVRAMMM", "HINT");
            if (actionBar != null) {
                reRender();
                actionBar.setSubtitle(getString(R.string.subtitle_map));
            }

            if (gpsMarker != null && gpsMarker.getVisibility() == View.GONE) {
                Toast.makeText(context, getString(R.string.toast_gps_outside), Toast.LENGTH_SHORT).show();
            }
        }
    }

    private void setTouchListener() {
        gestureDetector = new GestureDetector(
                context, new GestureDetector.SimpleOnGestureListener() {

            @Override
            public boolean onDown(MotionEvent e) {
                return true;
            }

            @Override
            public boolean onScroll(MotionEvent e1, MotionEvent e2, float distanceX, float distanceY) {
                transformMat.postTranslate(-distanceX, -distanceY);
                isGpsCurrent = false;
                gpsBtn.setImageResource(R.drawable.ic_gps_fixed_black_24dp);
                reRender();

                return true;
            }

            @Override
            public boolean onFling(MotionEvent e1, MotionEvent e2, float velocityX, float velocityY) {
                return true;
            }
        });

        scaleGestureDetector = new ScaleGestureDetector(
                context, new ScaleGestureDetector.SimpleOnScaleGestureListener() {

            @Override
            public boolean onScaleBegin(ScaleGestureDetector scaleGestureDetector) {
                return true;
            }

            @Override
            public boolean onScale(ScaleGestureDetector scaleGestureDetector) {
                float focusX = scaleGestureDetector.getFocusX();
                float focusY = scaleGestureDetector.getFocusY();
                float scaleFactor = scaleGestureDetector.getScaleFactor();
                // clamp scaleFactor
                scaleFactor = MAX_ZOOM >= scale * scaleFactor ? scaleFactor : MAX_ZOOM / scale;
                scaleFactor = MIN_ZOOM <= scale * scaleFactor ? scaleFactor : MIN_ZOOM / scale;

                transformMat.postTranslate(-focusX, -focusY);
                transformMat.postScale(scaleFactor, scaleFactor);
                transformMat.postTranslate(focusX, focusY);
                scale *= scaleFactor;

                reRender();

                return true;
            }

            @Override
            public void onScaleEnd(ScaleGestureDetector scaleGestureDetector) {
            }
        });

        rotationGestureDetector = new RotationGestureDetector(
                new RotationGestureDetector.OnRotationGestureListener() {
                    @Override
                    public void onRotation(RotationGestureDetector rotationDetector) {
                        float focusX = rotationDetector.getFocusX();
                        float focusY = rotationDetector.getFocusY();
                        float deltaAngel = -rotationDetector.getDeltaAngle();

                        transformMat.postTranslate(-focusX, -focusY);
                        transformMat.postRotate(deltaAngel);
                        transformMat.postTranslate(focusX, focusY);

                        rotation += deltaAngel;
                        if (rotation > 180)
                            rotation -= 360;
                        if (rotation <= -180)
                            rotation += 360;

                        isOrientationCurrent = false;

                        reRender();
                    }

                    @Override
                    public void onRotationEnd(RotationGestureDetector rotationDetector) {
                    }
                });

        rootLayout.setOnTouchListener(new View.OnTouchListener() {
            @Override
            public boolean onTouch(View v, MotionEvent event) {
                translationHandler.removeCallbacksAndMessages(null);
                boolean res = false;
                res |= scaleGestureDetector.onTouchEvent(event);
                res |= rotationGestureDetector.onTouchEvent(event);
                res |= gestureDetector.onTouchEvent(event);
                return res;
            }
        });
    }

    public void reRender() {
        reRenderPersonal(true, false);
    }

    public void reRenderPersonal(boolean performMerge, boolean togoIsUpdated) {

        performMerge = false;
        if (VERSION_OPTION == VERSION_ALL_FEATURE) {
            PersonalFragment personalFragment = (PersonalFragment)getParentFragment();

            for (CheckinNode checkinNode : checkinNodeList) {
                checkinNode.icon.setVisibility(View.GONE);
            }
            checkinNodeList.clear();
            personalFragment.addPostedCheckinIcon();
            personalFragment.addCollectedCheckinIcon();
        }




        boolean isMerged = performMerge && scale < ZOOM_THRESHOLD;
        if (getFragmentManager() == null) return;
        List<Fragment> fragmentList = getFragmentManager().getFragments();
        TogoFragment togoFragment = new TogoFragment();
        for (Fragment fragment: fragmentList) {
            if (fragment.getClass() == TogoFragment.class) togoFragment = (TogoFragment)fragment;
        }

        Log.d("NIVRAMM", "render size compare :" + togoFragment.togoItemAdapter.togoPlannedDataList.size() + ", " + togoNodeList.size());
        if (togoIsUpdated){
            for (CheckinNode checkinNode : togoNodeList) {
                checkinNode.icon.setVisibility(View.GONE);
            }
            togoNodeList.clear();
            List<SpotNode> togoSpotNodeList = new ArrayList<>();
            for (TogoPlannedData togoPlannedData : togoFragment.togoItemAdapter.togoPlannedDataList) {
//                //Log.d("NIVRAMM", "togoPlannedData:" + togoPlannedData.locationName);
                if(spotList.fullNodeMap.containsKey(togoPlannedData.locationName)) {
                    SpotNode spotNode = spotList.fullNodeMap.get(togoPlannedData.locationName);
                    togoSpotNodeList.add(spotNode);
                    setTogoNode(spotNode, togoPlannedData.isVisited);
                }
            }
        } else if (togoFragment.togoItemAdapter.togoPlannedDataList.size() < togoNodeList.size()) {
            for (CheckinNode checkinNode : togoNodeList) {
                checkinNode.icon.setVisibility(View.GONE);
            }
            togoNodeList.clear();
        } else if (togoFragment.togoItemAdapter.togoPlannedDataList.size() > togoNodeList.size()){
            List<SpotNode> togoSpotNodeList = new ArrayList<>();
            for (TogoPlannedData togoPlannedData : togoFragment.togoItemAdapter.togoPlannedDataList) {
//                //Log.d("NIVRAMM", "togoPlannedData:" + togoPlannedData.locationName);
                if(spotList.fullNodeMap.containsKey(togoPlannedData.locationName)) {
                    SpotNode spotNode = spotList.fullNodeMap.get(togoPlannedData.locationName);
                    togoSpotNodeList.add(spotNode);
                    setTogoNode(spotNode, togoPlannedData.isVisited);
                }
            }
        }


        Matrix gpsMarkTransform = new Matrix();
        Matrix spotIconTransform = new Matrix();
//        Matrix nodeIconTransform = new Matrix();
        Matrix checkinIconTransform = new Matrix();
        Matrix mergedCheckinIconTransform = new Matrix();
        gpsMarkTransform.postTranslate(-gpsMarkerPivotX, -gpsMarkerPivotY);
        spotIconTransform.postTranslate(-spotIconPivotX, -spotIconPivotY);
//        nodeIconTransform.postTranslate(-edgeNodeIconPivotX, -edgeNodeIconPivotY);
        checkinIconTransform.postTranslate(-checkinIconPivotX, -checkinIconPivotY);
        mergedCheckinIconTransform.postTranslate(-mergedCheckinIconPivotX, -mergedCheckinIconPivotY);
        float[] point = new float[]{0, 0};

        // transform tourist map (ImageView)
        transformMat.mapPoints(point);
        touristMap.setScaleX(scale);
        touristMap.setScaleY(scale);
        touristMap.setRotation(rotation);
        touristMap.setTranslationX(point[0]);
        touristMap.setTranslationY(point[1]);

        // transform gpsMarker
        point[0] = gpsDistortedX;
        point[1] = gpsDistortedY;
        transformMat.mapPoints(point);
        gpsMarkTransform.mapPoints(point);
        gpsMarker.setTranslationX(point[0]);
        gpsMarker.setTranslationY(point[1]);
        for (CheckinNode checkinNode : togoNodeList) {
//            //Log.d("NIVRAMM", "DRAW TOGO");
            point[0] = checkinNode.x;
            point[1] = checkinNode.y;
            transformMat.mapPoints(point);
            checkinIconTransform.mapPoints(point);
            checkinNode.icon.setTranslationX(point[0]);
            checkinNode.icon.setTranslationY(point[1]);
        }

        if (spotSwitch) {

//            } else {
//                for (SpotNode spotNode : spotNodeList) {
//                    if (spotNode.order == 2) {
//                        spotNode.icon.setVisibility(View.VISIBLE);
//                    }
//                }
//            }
            for (SpotNode spotNode : spotNodeList) {
                if (spotNode.order >= 3) {
                    spotNode.icon.setVisibility(View.GONE);
                }
            }
            for (SpotNode spotNode : spotNodeList) {
                point[0] = spotNode.x;
                point[1] = spotNode.y;
                transformMat.mapPoints(point);
                spotIconTransform.mapPoints(point);
                spotNode.icon.setTranslationX(point[0]);
                spotNode.icon.setTranslationY(point[1]);
            }
            if (true) {
                for (SpotNode spotNode : spotNodeList) {
                    if (spotNode.order == 2) {
                        spotNode.icon.setVisibility(View.GONE);
                    }
                }
            }}

        if (checkinSwitch && VERSION_OPTION == VERSION_ALL_FEATURE) {
            if (isMerged) {

                for (CheckinNode checkinNode : checkinNodeList) {
                    checkinNode.icon.setVisibility(View.GONE);
                }

                for (CheckinNode checkinClusterNode : checkinClusterNodeList) {
                    checkinClusterNode.icon.setVisibility(View.VISIBLE);
                }
            }
//            } else {
//                for (CheckinNode checkinNode : checkinNodeList) {
//                    checkinNode.icon.setVisibility(View.VISIBLE);
//                }
//
//                for (CheckinNode checkinClusterNode : checkinClusterNodeList) {
//                    checkinClusterNode.icon.setVisibility(View.GONE);
//                }
//            }

            for (CheckinNode checkinNode : checkinNodeList) {
                point[0] = checkinNode.x;
                point[1] = checkinNode.y;
                transformMat.mapPoints(point);
                checkinIconTransform.mapPoints(point);
                checkinNode.icon.setTranslationX(point[0]);
                checkinNode.icon.setTranslationY(point[1]);
            }

            for (CheckinNode checkinClusterNode : checkinClusterNodeList) {
                point[0] = checkinClusterNode.x;
                point[1] = checkinClusterNode.y;
                transformMat.mapPoints(point);
                mergedCheckinIconTransform.mapPoints(point);
                checkinClusterNode.icon.setTranslationX(point[0]);
                checkinClusterNode.icon.setTranslationY(point[1]);
            }
        }




    }

    private void setTogoNode(SpotNode spotNode, boolean isVisited) {
        Checkin checkin = new Checkin();
        checkin.lat = spotNode.lat;
        checkin.lng = spotNode.lng;
        checkin.uid = FirebaseAuth.getInstance().getCurrentUser().getUid();
        float[] imgPx = gpsToImgPx(Float.valueOf(checkin.lat), Float.valueOf(checkin.lng));
        addTogoIcon(checkin, imgPx[0], imgPx[1], spotNode.name, isVisited);
    }

    private void addSpotNode(final SpotNode spotNode) {
        View icon = inflater.inflate(R.layout.item_spot, null);
        spotNode.icon = icon;
        ((TextView) spotNode.icon.findViewById(R.id.spot_name)).setText(spotNode.name);
        rootLayout.addView(icon, rootLayout.indexOfChild(seperator));
    }

//    private void addEdgeNode(ImageNode imageNode, String iconColor) {
//        imageNode.icon = new ImageView(context);
//        if (iconColor.equals("blue"))
//            ((ImageView) imageNode.icon).setImageResource(R.drawable.ftprint_trans);
//        if (iconColor.equals("black"))
//            ((ImageView) imageNode.icon).setImageResource(R.drawable.ftprint_black_trans);
//        imageNode.icon.setLayoutParams(new RelativeLayout.LayoutParams(nodeIconWidth, nodeIconHeight));
//        rootLayout.addView(imageNode.icon, rootLayout.indexOfChild(seperator));
//    }
//
//    public void showPathIdicator(SpotNode spotNode) {
//        for (ImageNode imageNode : pathEdgeNodeList) {
//            rootLayout.removeView(imageNode.icon);
//        }
//
//        EdgeNode.Vertex from = edgeNode.findVertex(gpsDistortedX, gpsDistortedY);
//        EdgeNode.Vertex to = edgeNode.findVertex(spotNode.x, spotNode.y);
//        edgeNode.shortestPath(from, to);
//        pathEdgeNodeList = edgeNode.getPathNodeList();
//        for (ImageNode imageNode : pathEdgeNodeList) {
//            addEdgeNode(imageNode, "blue");
//        }
//    }

    public void showCheckinDialog(String key) {
        FragmentManager fragmentManager = getActivity().getSupportFragmentManager();
        CheckinDialogFragment checkinDialogFragment = CheckinDialogFragment.newInstance(key, TAG);
        checkinDialogFragment.show(fragmentManager, "fragment_checkin_dialog");
    }

    public void showCheckinListDialog(CheckinNode checkinNode) {
        FragmentManager fragmentManager = getActivity().getSupportFragmentManager();
        CheckinListDialogFragment checkinListDialogFragment = CheckinListDialogFragment.newInstance(checkinNode);
        checkinListDialogFragment.show(fragmentManager, "fragment_checkin_list_dialog");
    }

    public void addCheckin(final Checkin checkin, String type) {
        float[] imgPx = gpsToImgPx(Float.valueOf(checkin.lat), Float.valueOf(checkin.lng));
//        //Log.d("NIVRAMMM", "checkin key :" + checkin.key);
        addCheckinIcon(checkin, imgPx[0], imgPx[1], type);
//        addCheckinClusterIcon(checkin, imgPx[0], imgPx[1]);
//        changeCheckin(checkin); // set heat checkin icon
//        reRender();
    }

    // change with hot checkin icon
    public void changeCheckin(final Checkin checkin) {
//        View checkinNodeView = checkinNodeMap.get(checkin.key).icon;
//        View checkinClusterNodeView = checkinClusterNodeMap.get(checkin.key).icon;
//
//        boolean allPopularFlag = checkin.popularTargetUid.containsKey("all") && checkin.popularTargetUid.get("all");
//        boolean targetPopularFlag = !rate.equals("") && checkin.popularTargetUid.containsKey(rate) && checkin.popularTargetUid.get(rate);
//        int checkinLikeNum = checkin.likeNum;
//        if (checkin.like != null) checkinLikeNum += checkin.like.size();
//        if (allPopularFlag || targetPopularFlag || checkinLikeNum >= 5) {
//            if (rate.equals(checkin.rate)) {
//                ((ImageView) checkinNodeView).setImageDrawable(
//                        ResourcesCompat.getDrawable(getResources(), R.drawable.self_hot_checkin_icon_60px, null));
//            } else {
//                ((ImageView) checkinNodeView).setImageDrawable(
//                        ResourcesCompat.getDrawable(getResources(), R.drawable.hot_checkin_icon_60px, null));
//            }
//            ImageView clusterIcon = checkinClusterNodeView.findViewById(R.id.checkin_icon);
//            clusterIcon.setImageDrawable(
//                    ResourcesCompat.getDrawable(getResources(), R.drawable.hot_checkin_icon_96px, null));
//        }

//        reRender();
    }

    // checkins that their icon overlay
    private void addCheckinIcon(final Checkin checkin, float x, float y, String type) {

        // create new node
        CheckinNode checkinNode = new CheckinNode(x, y, Float.valueOf(checkin.lat), Float.valueOf(checkin.lng));
        checkinNode.icon = new ImageView(context);
        checkinNode.icon.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                showCheckinMapIconClickedOptions(checkin);
            }
        });
        checkinNode.icon.setLayoutParams(new RelativeLayout.LayoutParams(checkinIconWidth, checkinIconHeight));

        if (uid.equals(checkin.uid)) {
//            ((ImageView) checkinNode.icon).setImageDrawable(
//                    ResourcesCompat.getDrawable(getResources(), R.drawable.self_checkin_icon_60px, null));
            ((ImageView) checkinNode.icon).setImageDrawable(
                    ResourcesCompat.getDrawable(getResources(), R.drawable.icon_posted_checkin_60, null));
        } else {
//            //Log.d("ISVISITED", collectedCheckinIsVisited.containsKey(checkin.key) + ", " + collectedCheckinIsVisited.get(checkin.key));
            if (collectedCheckinIsVisited.containsKey(checkin.key) && collectedCheckinIsVisited.get(checkin.key)) {
                ((ImageView) checkinNode.icon).setImageDrawable(
                        ResourcesCompat.getDrawable(getResources(), R.drawable.icon_visited_60, null));
            }else  {
                ((ImageView) checkinNode.icon).setImageDrawable(
                        ResourcesCompat.getDrawable(getResources(), R.drawable.icon_collected_checkin_60, null));
            }

        }
        rootLayout.addView(checkinNode.icon, rootLayout.indexOfChild(seperator));
        checkinNode.checkinList.add(checkin);
        checkinNodeList.add(checkinNode);
    }


    // checkins that their icon overlay

    private void addTogoIcon(final Checkin checkin, float x, float y, final String spotName, boolean isVisited) {
        // create new node
        CheckinNode checkinNode = new CheckinNode(x, y, Float.valueOf(checkin.lat), Float.valueOf(checkin.lng));
        checkinNode.icon = new ImageView(context);
        checkinNode.icon.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                showTogoMapIconClickedOptions(spotName, checkin.lat, checkin.lng);

            }
        });
        checkinNode.icon.setLayoutParams(new RelativeLayout.LayoutParams(checkinIconWidth, checkinIconHeight));

        if (isVisited == false) {
            ((ImageView) checkinNode.icon).setImageDrawable(
                    ResourcesCompat.getDrawable(getResources(), R.drawable.icon_togo_60, null));
        } else {
            ((ImageView) checkinNode.icon).setImageDrawable(
                    ResourcesCompat.getDrawable(getResources(), R.drawable.icon_visited_60, null));
        }
        rootLayout.addView(checkinNode.icon);
//        rootLayout.addView(checkinNode.icon, rootLayout.indexOfChild(seperator));
        checkinNode.checkinList.add(checkin);
        togoNodeList.add(checkinNode);
//        checkinNodeMap.put(checkin.key, checkinNode);
    }

    public void onLocateClick(String lat, String lng, String spotName) {
        float[] imgPx = Utility.gpsToImgPx(Float.valueOf(lat), Float.valueOf(lng));
        translateToImgPx(imgPx[0], imgPx[1], false);
        searchLocationDialog(lat, lng, spotName);
    }


    public void translateToImgPx(final float x, final float y, final boolean toCurrent) {

        Runnable translationInterpolation = new Runnable() {
            @Override
            public void run() {
                float[] point = new float[]{x, y};
                transformMat.mapPoints(point);
                float distanceToCenterX = mapCenterX - point[0];
                float distanceToCenterY = mapCenterY - point[1];
                float scaleTo22 = ZOOM_THRESHOLD - scale;

                if (Math.abs(distanceToCenterX) <= 30.0f || Math.abs(distanceToCenterY) <= 30.0f) {
                    transformMat.postTranslate(distanceToCenterX, distanceToCenterY);
                    if (scale < ZOOM_THRESHOLD) {
                        transformMat.postTranslate(-point[0], -point[1]);
                        transformMat.postScale(ZOOM_THRESHOLD / scale, ZOOM_THRESHOLD / scale);
                        transformMat.postTranslate(point[0], point[1]);
                        scale = ZOOM_THRESHOLD;
                    }
                    reRender();
                    translationHandler.removeCallbacks(this);
                    if (toCurrent) {
                        gpsBtn.setImageResource(R.drawable.ic_gps_fixed_blue_24dp);
                        isGpsCurrent = true;
                    } else {
                        gpsBtn.setImageResource(R.drawable.ic_gps_fixed_black_24dp);
                        isGpsCurrent = false;
                    }
                } else {
                    transformMat.postTranslate(distanceToCenterX / 5, distanceToCenterY / 5);
                    if (scale < ZOOM_THRESHOLD) {
                        transformMat.postTranslate(-point[0], -point[1]);
                        transformMat.postScale((scale + scaleTo22 / 5) / scale, (scale + scaleTo22 / 5) / scale);
                        transformMat.postTranslate(point[0], point[1]);
                        scale += scaleTo22 / 5;
                    }
                    reRenderPersonal(false, false);
                    translationHandler.postDelayed(this, 5);
                }
            }
        };
        if (translationHandler == null) translationHandler = new Handler();
        translationHandler.postDelayed(translationInterpolation, 5);
    }

    public void rotateToNorth() {
        final Handler rotationHandler = new Handler();
        Runnable rotationInterpolation = new Runnable() {
            @Override
            public void run() {
                if (Math.abs(rotation) <= 6.0f) {
                    transformMat.postTranslate(-mapCenterX, -mapCenterY);
                    transformMat.postRotate(-rotation);
                    transformMat.postTranslate(mapCenterX, mapCenterY);
                    rotation = 0;
                    reRender();
                    rotationHandler.removeCallbacks(this);
                    isOrientationCurrent = true;
                } else {
                    transformMat.postTranslate(-mapCenterX, -mapCenterY);
                    transformMat.postRotate(-rotation / 5);
                    transformMat.postTranslate(mapCenterX, mapCenterY);
                    rotation -= rotation / 5;
                    reRender();
                    rotationHandler.postDelayed(this, 5);
                }
            }
        };
        rotationHandler.postDelayed(rotationInterpolation, 5);
    }

    public void handleGpsUpdate(float lat, float lng) {
        Log.d("GpsUpdateGG", lat + ", " + lng);
        lastLat = lat;
        lastLng = lng;
        if (getView() == null) {
            return;
        }

        // GPS is within tourist map.
        if (lat >= realMesh.minLat && lat <= realMesh.maxLat && lng >= realMesh.minLon && lng <= realMesh.maxLon) {

            if (gpsMarker.getVisibility() != View.VISIBLE) {
                gpsMarker.setVisibility(View.VISIBLE);
            }

            float[] imgPx = gpsToImgPx(lat, lng);

            if (imgPx[0] != -1 && imgPx[1] != -1) {
                gpsDistortedX = imgPx[0];
                gpsDistortedY = imgPx[1];

                reRender();
            }

        } else { // GPS outside.

            if (gpsMarker.getVisibility() != View.GONE) {
                gpsMarker.setVisibility(View.GONE);
                Toast.makeText(context, getString(R.string.toast_gps_outside), Toast.LENGTH_LONG).show();
            }
        }
    }


    public void handleFogUpdate(double lat, double lng) {
//        if (lat >= realMesh.minLat && lat <= realMesh.maxLat && lng >= realMesh.minLon && lng <= realMesh.maxLon) {
//            float[] imgPx = gpsToImgPx(lat, lng);
//            if (imgPx[0] != -1 && imgPx[1] != -1) {
//                // update fog map
//                Paint paint = new Paint();
//                paint.setColor(Color.TRANSPARENT);
//                paint.setXfermode(new PorterDuffXfermode(PorterDuff.Mode.CLEAR));
//                paint.setMaskFilter(new BlurMaskFilter(25, BlurMaskFilter.Blur.NORMAL));
//                Canvas canvas = new Canvas(fogBitmap);
//                canvas.drawCircle(imgPx[0], imgPx[1], 25, paint);
//                fogMap.postInvalidate();
//            }
//        }
    }

    public void handleSensorChange(float r) {
        final float RADIAN = 57.296f;
        if (gpsMarker != null) {
            gpsMarker.setRotation(r * RADIAN + rotation);
        }
    }

//    public void switchFog(boolean flag) {
//        fogSwitch = flag;
//        if (flag) {
//            fogMap.setVisibility(View.VISIBLE);
//        } else {
//            fogMap.setVisibility(View.GONE);
//        }
//        reRender();
//    }

//    public void switchDistanceIndicator(boolean flag) {
//        edgeLengthSwitch = flag;
//        if (flag) {
//            for (ImageNode imageNode : edgeNodeList) {
//                imageNode.icon.setVisibility(View.VISIBLE);
//            }
//        } else {
//            for (ImageNode imageNode : edgeNodeList) {
//                imageNode.icon.setVisibility(View.GONE);
//            }
//        }
//        reRender();
//    }

    public void switchCheckinIcon(boolean flag) {
        checkinSwitch = flag;
        if (flag) {
            for (CheckinNode checkinNode : checkinNodeList) {
                checkinNode.icon.setVisibility(View.VISIBLE);
            }
            for (CheckinNode checkinClusterNode : checkinClusterNodeList) {
                checkinClusterNode.icon.setVisibility(View.VISIBLE);
            }
            for (CheckinNode checkinNode : togoNodeList) {
                checkinNode.icon.setVisibility(View.VISIBLE);
            }
        } else {
            for (CheckinNode checkinNode : checkinNodeList) {
                checkinNode.icon.setVisibility(View.GONE);
            }
            for (CheckinNode checkinClusterNode : checkinClusterNodeList) {
                checkinClusterNode.icon.setVisibility(View.GONE);
            }
            for (CheckinNode checkinNode : togoNodeList) {
                checkinNode.icon.setVisibility(View.GONE);
            }
        }
        reRender();
    }

    public void switchSpotIcon(boolean flag) {
        spotSwitch = flag;
        if (flag) {
            for (SpotNode spotNode : spotNodeList) {
                spotNode.icon.setVisibility(View.VISIBLE);
            }
        } else {
            for (SpotNode spotNode : spotNodeList) {
                spotNode.icon.setVisibility(View.GONE);
            }
        }
        reRender();
    }

    public String getUid() {
        return uid;
    }

    public void searchLocationDialog(final String lat, final String lng, final String spotName) {
        final BottomSheetDialog bottomSheetDialog= new BottomSheetDialog(getActivity());
        bottomSheetDialog.setContentView(R.layout.search_location_dialog);
        bottomSheetDialog.getWindow().clearFlags(WindowManager.LayoutParams.FLAG_DIM_BEHIND);
        String latLng = lat + "," + lng;
        //Log.d("NIVRAM", "LATLNG: " + latLng);
//        Button searchLocationBtn = bottomSheetDialog.findViewById(R.id.search_location);
//        searchLocationBtn.setOnClickListener(new View.OnClickListener() {
//            @Override
//            public void onClick(View v) {
//                String latLng = lat + "," + lng;
//                //Log.d("NIVRAM", "LATLNG: " + latLng);
//                searchLocation(latLng);
//            }
//        });

        Button navigateLocationBtn = bottomSheetDialog.findViewById(R.id.navigate_location);
        navigateLocationBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String latLng = lat + "," + lng;
                //Log.d("NIVRAM", "LATLNG: " + latLng);
                String logNote = "";
                List<Fragment> fragmentList = getFragmentManager().getFragments();
                TogoFragment togoFragment = new TogoFragment();
                for (Fragment fragment: fragmentList) {
                    if (fragment.getClass() == TogoFragment.class) togoFragment = (TogoFragment)fragment;
                }
                if (togoFragment.togoItemAdapter.isTogo(spotName)) logNote = LOG_NOTE_IS_COLLECTED_TOGO;
                else logNote = LOG_NOTE_IS_NOT_COLLECTED_TOGO;
                navigateLocation(latLng, LOG_TOGO_NAVIGATE, spotName, logNote);
            }
        });

        Button cancelSearchLocation = bottomSheetDialog.findViewById(R.id.cancel_search_location);
        cancelSearchLocation.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                bottomSheetDialog.dismiss();
            }
        });
        bottomSheetDialog.show();
    }

    public void searchLocation(String locationName) {
        locationName = locationName.replace(' ', '+');
        //Log.d("NIVRAM", "location : " + locationName);
        Uri gmmIntentUri = Uri.parse("geo:0,0?q=" + locationName + "&mode=w");
        Intent mapIntent = new Intent(Intent.ACTION_VIEW, gmmIntentUri);
        mapIntent.setPackage("com.google.android.apps.maps");
        startActivity(mapIntent);
    }

    public void navigateLocation(String locationName, String logTag, String logMsg, String logNote) {
        firebaseLogManager.log(logTag, logMsg, logNote);
        locationName = locationName.replace(' ', '+');
        Uri gmmIntentUri = Uri.parse("google.navigation:q=" + locationName);
        Intent mapIntent = new Intent(Intent.ACTION_VIEW, gmmIntentUri);
        mapIntent.setPackage("com.google.android.apps.maps");
        startActivity(mapIntent);
    }

    public void showCheckinMapIconClickedOptions(final Checkin checkin) {
        final BottomSheetDialog bottomSheetDialog= new BottomSheetDialog(getActivity());
        bottomSheetDialog.setContentView(R.layout.personal_map_icon_clicked_options_dialog);
        bottomSheetDialog.getWindow().clearFlags(WindowManager.LayoutParams.FLAG_DIM_BEHIND);
        TextView userName = bottomSheetDialog.findViewById(R.id.user_name);
        String latLng = checkin.lat + "," + checkin.lng ;
        //Log.d("NIVRAM", "LATLNG: " + latLng);
        TextView spotNameTextView = bottomSheetDialog.findViewById(R.id.spot_name_tv);
        if (uid.equals(checkin.uid)) {
            userName.setText("我自己的打卡");
            if (checkin.location.equals("")) {
                spotNameTextView.setText("未知地點");
            } else {
                spotNameTextView.setText(checkin.location);
            }
        } else {
            userName.setText(checkin.username + "的打卡");
            if (checkin.location.equals("")) {
                spotNameTextView.setText("未知地點");
            } else {

                spotNameTextView.setText(checkin.location);
            }
        }
        Button showDescriptionBrn = bottomSheetDialog.findViewById(R.id.show_description);
        showDescriptionBrn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                showCheckinDialog(checkin.key);
//                bottomSheetDialog.dismiss();
            }
        });

        Button navigateLocationBtn = bottomSheetDialog.findViewById(R.id.navigate_location);
        navigateLocationBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String latLng = checkin.lat + "," + checkin.lng ;
                //Log.d("NIVRAM", "LATLNG: " + latLng);
                String logNote = "";
                if (collectedCheckinKey.containsKey(checkin.key) && collectedCheckinKey.get(checkin.key)) logNote = LOG_NOTE_IS_COLLECTED_CHECKIN;
                else if (checkin.uid.equals(FirebaseAuth.getInstance().getCurrentUser().getUid())) logNote = LOG_NOTE_IS_SELF_CHECKIN;
                else logNote = LOG_NOTE_IS_OTHER_CHECKIN;

                navigateLocation(latLng, LOG_CHECKIN_NAVIGATE, checkin.key, logNote);
            }
        });

        final Button reportVisitedBtn = bottomSheetDialog.findViewById(R.id.report_visited);
        reportVisitedBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                reportCollectedCheckinOptionClicked(checkin, bottomSheetDialog);
            }
        });

        final Button removeTogoBtn = bottomSheetDialog.findViewById(R.id.remove_togo);
        removeTogoBtn.setVisibility(View.GONE);

        if (uid.equals(checkin.uid)) {
            reportVisitedBtn.setVisibility(View.GONE);
        }
        bottomSheetDialog.show();

    }

    public void showTogoMapIconClickedOptions(final String spotName, final String lat, final String lng) {
//        //TODO: Show description dialog

        final BottomSheetDialog bottomSheetDialog= new BottomSheetDialog(getActivity());
        bottomSheetDialog.setContentView(R.layout.personal_map_icon_clicked_options_dialog);
        bottomSheetDialog.getWindow().clearFlags(WindowManager.LayoutParams.FLAG_DIM_BEHIND);
        TextView userName = bottomSheetDialog.findViewById(R.id.user_name);
        userName.setVisibility(View.GONE);
        String latLng = lat + "," + lng;
        //Log.d("NIVRAM", "LATLNG: " + latLng);
        TextView spotNameTextView = bottomSheetDialog.findViewById(R.id.spot_name_tv);
        spotNameTextView.setText(spotName);
        Button showDescriptionBrn = bottomSheetDialog.findViewById(R.id.show_description);
        showDescriptionBrn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                SpotDescritionDialogFragment spotDescritionDialogFragment = SpotDescritionDialogFragment.newInstance(spotName, TAG);
                spotDescritionDialogFragment.show(getFragmentManager(), "SpotDescritionDialogFragment");
//                bottomSheetDialog.dismiss();
            }
        });

        Button navigateLocationBtn = bottomSheetDialog.findViewById(R.id.navigate_location);
        navigateLocationBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String latLng = lat + "," + lng;
                //Log.d("NIVRAM", "LATLNG: " + latLng);
                String logNote = "";
                List<Fragment> fragmentList = getFragmentManager().getFragments();
                TogoFragment togoFragment = new TogoFragment();
                for (Fragment fragment: fragmentList) {
                    if (fragment.getClass() == TogoFragment.class) togoFragment = (TogoFragment)fragment;
                }
                if (togoFragment.togoItemAdapter.isTogo(spotName)) logNote = LOG_NOTE_IS_COLLECTED_TOGO;
                else logNote = LOG_NOTE_IS_NOT_COLLECTED_TOGO;
                navigateLocation(latLng, LOG_TOGO_NAVIGATE, spotName, logNote);
            }
        });

        final Button reportVisitedBtn = bottomSheetDialog.findViewById(R.id.report_visited);
        if (((MainActivity)getActivity()).personalFragment.togoFragment.togoItemAdapter.isTogo(spotName) == false) reportVisitedBtn.setVisibility(View.GONE);
        reportVisitedBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                reportTogoVisitedOptionClicked(spotName, bottomSheetDialog);
            }
        });

        final Button removeTogoBtn = bottomSheetDialog.findViewById(R.id.remove_togo);
        if (((MainActivity)getActivity()).personalFragment.togoFragment.togoItemAdapter.isTogo(spotName) == false) removeTogoBtn.setVisibility(View.GONE);
        removeTogoBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                removeTogoOptionClicked(spotName);
                bottomSheetDialog.dismiss();
            }
        });
        bottomSheetDialog.show();
    }

    void removeTogoOptionClicked(final String spotName) {
//        //Log.d("NIVRAMM", "SHOW DIALOG");
        //TODO: show remove confirmation dialog
        final Dialog dialog = new Dialog(getActivity());
        dialog.setContentView(R.layout.confirmation_dialog);
        TextView confirmation = dialog.findViewById(R.id.confirmation_message);
        confirmation.setText("確定要刪除 " + spotName + " 嗎?");
        Button confiramtionBtn = dialog.findViewById(R.id.btn_report_confirm);
        confiramtionBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                removeTogo(spotName);
                dialog.dismiss();
            }
        });
        Button cancelBtn = dialog.findViewById(R.id.btn_report_cancel);
        cancelBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                dialog.dismiss();
            }
        });
        dialog.show();
    }

    void removeTogo(String spotName) {
        List<Fragment> fragmentList = getFragmentManager().getFragments();
        TogoFragment togoFragment = new TogoFragment();
        for (Fragment fragment: fragmentList) {
            if (fragment.getClass() == TogoFragment.class) togoFragment = (TogoFragment)fragment;
        }
        for (TogoPlannedData togoPlannedData: togoFragment.togoItemAdapter.togoPlannedDataList) {
            //Log.d("NIVRAM", togoPlannedData.locationName + "XDDDD");
        }
        for (int i = 0; i < togoFragment.togoItemAdapter.togoPlannedDataList.size(); i++) {
            if (togoFragment.togoItemAdapter.togoPlannedDataList.get(i).locationName.equals(spotName)) {
                togoFragment.togoItemAdapter.removeTogo(togoFragment.togoItemAdapter.togoPlannedDataList.get(i), i, TAG);
            }
        }
        reRender();
    }

    void reportCollectedCheckinOptionClicked(final Checkin checkin, final BottomSheetDialog parentDialog) {
//        //Log.d("NIVRAMM", "SHOW DIALOG");
        //TODO: show remove confirmation dialog
        final Dialog dialog = new Dialog(getActivity());
        dialog.setContentView(R.layout.confirmation_dialog);
        TextView confirmation = dialog.findViewById(R.id.confirmation_message);
        if (checkin.location.equals("")) {
            confirmation.setText("是否造訪了來自" + System.getProperty("line.separator")+ checkin.username + "的未知地點呢?");
        } else {
            confirmation.setText("是否造訪了 " + checkin.location + " 呢?");
        }
        Button confiramtionBtn = dialog.findViewById(R.id.btn_report_confirm);
        confiramtionBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                reportCollectedCheckinVisited(checkin);
                parentDialog.dismiss();
                dialog.dismiss();
            }
        });
        Button cancelBtn = dialog.findViewById(R.id.btn_report_cancel);
        cancelBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                dialog.dismiss();
            }
        });
        dialog.show();
    }

    void reportTogoVisitedOptionClicked(final String spotName, final Dialog parentDialog) {
//        //Log.d("NIVRAMM", "SHOW DIALOG");
        //TODO: show remove confirmation dialog
        final Dialog dialog = new Dialog(getActivity());
        dialog.setContentView(R.layout.confirmation_dialog);
        TextView confirmation = dialog.findViewById(R.id.confirmation_message);
        confirmation.setText("是否造訪了 " + spotName + " 呢?");
        Button confiramtionBtn = dialog.findViewById(R.id.btn_report_confirm);
        confiramtionBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                reportTogoVisited(spotName);
                parentDialog.dismiss();
                dialog.dismiss();
            }
        });
        Button cancelBtn = dialog.findViewById(R.id.btn_report_cancel);
        cancelBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                dialog.dismiss();
            }
        });
        dialog.show();
    }

    void reportCollectedCheckinVisited(Checkin checkin) {
        reportCompleteToast();
        firebaseLogManager.log(LOG_REPORT_CHECKIN, checkin.key);
        DatabaseReference databaseReference = FirebaseDatabase.getInstance().getReference();
        databaseReference.child("users").child(uid).child("visited").child("collected_post").child(mapTag).child(checkin.key).setValue(true);
        collectedCheckinIsVisited.put(checkin.key, true);
        reRender();
        ReportManager reportManager = new ReportManager();
        reportManager.reportTogo();
    }

    void reportTogoVisited(String spotName) {
        setTogoVisited(spotName);
        reportCompleteToast();
        firebaseLogManager.log(LOG_REPORT_TOGO, spotName);
    }

    void setTogoVisited(String spotName) {
        //TODO: set
        List<Fragment> fragmentList = getFragmentManager().getFragments();
        TogoFragment togoFragment = new TogoFragment();
        for (Fragment fragment: fragmentList) {
            if (fragment.getClass() == TogoFragment.class) togoFragment = (TogoFragment)fragment;
        }
        togoFragment.togoItemAdapter.setIsVisited(spotName, true);
        reRender();
    }

    void reportAnywhereClicked() {
        final Dialog dialog = new Dialog(getActivity());
        dialog.setContentView(R.layout.report_anywhere_dialog);
        TextView textView = dialog.findViewById(R.id.report_message);
        textView.setText("請輸入造訪的景點");
        final EditText visitedPlace = dialog.findViewById(R.id.visited_place);
        visitedPlace.setOnEditorActionListener(new TextView.OnEditorActionListener() {
            @Override
            public boolean onEditorAction(TextView v, int actionId, KeyEvent event) {
                boolean handled = false;
                if (actionId == EditorInfo.IME_ACTION_SEND) {
                    reportAnywhere(visitedPlace.getText().toString(), dialog);
//                    dialog.dismiss();
                    handled = true;
                }
                return handled;
            }
        });

        Button confirmationBtn = dialog.findViewById(R.id.btn_report_confirm);
        confirmationBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                reportAnywhere(visitedPlace.getText().toString(), dialog);
//                dialog.dismiss();
            }
        });
        Button cancelBtn = dialog.findViewById(R.id.btn_report_cancel);
        cancelBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                dialog.dismiss();
            }
        });
        dialog.show();
        Window window = dialog.getWindow();
        window.setLayout(WindowManager.LayoutParams.MATCH_PARENT, WindowManager.LayoutParams.WRAP_CONTENT);

    }

    void reportAnywhere(final String locationName, final Dialog parentDialog) {
        List<Fragment> fragmentList = getFragmentManager().getFragments();
        TogoFragment togoFragment = new TogoFragment();
        for (Fragment fragment: fragmentList) {
            if (fragment.getClass() == TogoFragment.class) togoFragment = (TogoFragment)fragment;
        }
        for (TogoPlannedData togoPlannedData:togoFragment.togoItemAdapter.togoPlannedDataList) {
            if (togoPlannedData.locationName.equals(locationName)) {
                reportTogoVisitedOptionClicked(locationName, parentDialog);
                return;
            }
        }

        final Dialog dialog = new Dialog(getActivity());
        dialog.setContentView(R.layout.confirmation_dialog);
        TextView confirmation = dialog.findViewById(R.id.confirmation_message);
        confirmation.setText("是否造訪了 " + locationName + " 呢?");
        Button confiramtionBtn = dialog.findViewById(R.id.btn_report_confirm);
        confiramtionBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                reportMotivation(locationName);
                parentDialog.dismiss();
                dialog.dismiss();
            }
        });
        Button cancelBtn = dialog.findViewById(R.id.btn_report_cancel);
        cancelBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                dialog.dismiss();
            }
        });
        dialog.show();
    }

    void reportMotivation(final String locationName) {
        if (lastLat == 0 && lastLng == 0) {
            Toast.makeText(getContext(), "無法取得你的位置", Toast.LENGTH_SHORT);
            return;
        }

        final Dialog dialog = new Dialog(getActivity());
        dialog.setContentView(R.layout.report_anywhere_dialog);
        TextView textView = dialog.findViewById(R.id.report_message);
        textView.setText("促使你造訪這個景點的原因是:");
        final EditText motivation = dialog.findViewById(R.id.visited_place);
        motivation.setHint("造訪原因");
        motivation.setOnEditorActionListener(new TextView.OnEditorActionListener() {
            @Override
            public boolean onEditorAction(TextView v, int actionId, KeyEvent event) {
                boolean handled = false;
                if (actionId == EditorInfo.IME_ACTION_SEND) {
                    reportAnywhere(locationName, motivation.getText().toString());
                    handled = true;
                }
                dialog.dismiss();
                return handled;
            }
        });

        Button confirmationBtn = dialog.findViewById(R.id.btn_report_confirm);
        confirmationBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                reportAnywhere(locationName, motivation.getText().toString());
                dialog.dismiss();
            }
        });
        Button cancelBtn = dialog.findViewById(R.id.btn_report_cancel);
        cancelBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                dialog.dismiss();
            }
        });
        dialog.show();
        Window window = dialog.getWindow();
        window.setLayout(WindowManager.LayoutParams.MATCH_PARENT, WindowManager.LayoutParams.WRAP_CONTENT);

    }

    void reportAnywhere(String location, String motivation) {
        reportCompleteToast();
        firebaseLogManager.log(LOG_REPORT_ANYWHERE, location, motivation);
    }

    void reportCompleteToast() {
        Toast.makeText(getContext(), "謝謝回報!", Toast.LENGTH_SHORT).show();
    }

    void switchMap() {
//        ((MainActivity)getActivity()).viewPager.setCurrentItem(0);
        ((MainActivity)getActivity()).bottomBar.selectTabAtPosition(0, true);

//        ((MainActivity)getActivity()).personalFragment.setUserVisibleHint(false);
//        ((MainActivity)getActivity()).mapFragment.setUserVisibleHint(true);
    }

    public void searchLocationGoogleCommentVersionDialog(final String lat, final String lng, final String logTag, final String spotName, final String logNote) {
        final BottomSheetDialog bottomSheetDialog= new BottomSheetDialog(getActivity());
        bottomSheetDialog.setContentView(R.layout.personal_map_icon_clicked_options_dialog);
        bottomSheetDialog.getWindow().clearFlags(WindowManager.LayoutParams.FLAG_DIM_BEHIND);
        TextView userName = bottomSheetDialog.findViewById(R.id.user_name);
        userName.setVisibility(View.GONE);
        String latLng = lat + "," + lng;
        //Log.d("NIVRAM", "LATLNG: " + latLng);
        TextView spotNameTextView = bottomSheetDialog.findViewById(R.id.spot_name_tv);
        spotNameTextView.setText(spotName);
        Button showDescriptionBrn = bottomSheetDialog.findViewById(R.id.show_description);
        showDescriptionBrn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                SpotDescritionDialogFragment spotDescritionDialogFragment = SpotDescritionDialogFragment.newInstance(spotName, TAG);
                spotDescritionDialogFragment.show(getFragmentManager(), "SpotDescritionDialogFragment");
//                bottomSheetDialog.dismiss();
            }
        });

        Button navigateLocationBtn = bottomSheetDialog.findViewById(R.id.navigate_location);
        navigateLocationBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String latLng = lat + "," + lng;
                //Log.d("NIVRAM", "LATLNG: " + latLng);
                String logNote = "";
                List<Fragment> fragmentList = getFragmentManager().getFragments();
                TogoFragment togoFragment = new TogoFragment();
                for (Fragment fragment: fragmentList) {
                    if (fragment.getClass() == TogoFragment.class) togoFragment = (TogoFragment)fragment;
                }
                if (togoFragment.togoItemAdapter.isTogo(spotName)) logNote = LOG_NOTE_IS_COLLECTED_TOGO;
                else logNote = LOG_NOTE_IS_NOT_COLLECTED_TOGO;
                navigateLocation(latLng, LOG_TOGO_NAVIGATE, spotName, logNote);
            }
        });

        final Button reportVisitedBtn = bottomSheetDialog.findViewById(R.id.report_visited);
        if (((MainActivity)getActivity()).personalFragment.togoFragment.togoItemAdapter.isTogo(spotName) == false) reportVisitedBtn.setVisibility(View.GONE);
        reportVisitedBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                reportTogoVisitedOptionClicked(spotName, bottomSheetDialog);
            }
        });

        final Button removeTogoBtn = bottomSheetDialog.findViewById(R.id.remove_togo);
        if (((MainActivity)getActivity()).personalFragment.togoFragment.togoItemAdapter.isTogo(spotName) == false) removeTogoBtn.setVisibility(View.GONE);
        removeTogoBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                removeTogoOptionClicked(spotName);
                bottomSheetDialog.dismiss();
            }
        });
        bottomSheetDialog.show();
    }
}
