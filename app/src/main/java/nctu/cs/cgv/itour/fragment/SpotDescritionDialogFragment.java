package nctu.cs.cgv.itour.fragment;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.widget.SwipeRefreshLayout;
import android.support.v7.widget.StaggeredGridLayoutManager;
import android.util.Log;

import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;

import android.os.Bundle;
import android.support.v4.app.DialogFragment;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentTransaction;
import android.support.v4.content.res.ResourcesCompat;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.WindowManager;
import android.widget.AdapterView;
import android.widget.EditText;
import android.widget.GridView;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.bumptech.glide.Glide;
import com.bumptech.glide.request.RequestOptions;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.ChildEventListener;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.Query;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;
import java.util.Objects;

import nctu.cs.cgv.itour.R;
import nctu.cs.cgv.itour.Utility;
import nctu.cs.cgv.itour.activity.MainActivity;
import nctu.cs.cgv.itour.custom.CheckinItemAdapter;
import nctu.cs.cgv.itour.custom.CommentItemAdapter;
import nctu.cs.cgv.itour.custom.GridPhotoAdapter;
import nctu.cs.cgv.itour.custom.ItemClickSupport;
import nctu.cs.cgv.itour.object.Checkin;
import nctu.cs.cgv.itour.object.Comment;
import nctu.cs.cgv.itour.object.CommentNotification;
import nctu.cs.cgv.itour.object.GlideApp;
import nctu.cs.cgv.itour.object.LikeNotification;
import nctu.cs.cgv.itour.object.SpotList;
import nctu.cs.cgv.itour.object.SpotNode;

import static nctu.cs.cgv.itour.MyApplication.fileDownloadURL;
import static nctu.cs.cgv.itour.MyApplication.latitude;
import static nctu.cs.cgv.itour.MyApplication.longitude;
import static nctu.cs.cgv.itour.MyApplication.mapTag;
import static nctu.cs.cgv.itour.MyApplication.spotList;
import static nctu.cs.cgv.itour.Utility.actionLog;
import static nctu.cs.cgv.itour.activity.MainActivity.checkinMap;
import static nctu.cs.cgv.itour.activity.MainActivity.collectedCheckinKey;
import static nctu.cs.cgv.itour.activity.MainActivity.getDescription;

public class SpotDescritionDialogFragment extends DialogFragment {

    private static final String TAG = "SpotDescritionDialogFragment";
    private Query postReference;
    private String spotName;
    private ListView swipeRefreshLayout;
    public CheckinItemAdapter checkinItemAdapter;


    public static SpotDescritionDialogFragment newInstance(String spotName) {
        SpotDescritionDialogFragment spotDescritionDialogFragment = new SpotDescritionDialogFragment();
        Bundle args = new Bundle();
        args.putString("spotName", spotName);
        spotDescritionDialogFragment.setArguments(args);
        return spotDescritionDialogFragment;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if (getArguments() != null) {
            spotName = getArguments().getString("spotName");
        }
        checkinItemAdapter = new CheckinItemAdapter(getActivity(), new ArrayList<Checkin>(), this);

    }

    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        return inflater.inflate(R.layout.fragment_spot_description_dialog, container);
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        SpotNode spotNode = spotList.personalNodeMap.get(spotName);

        TextView description = view.findViewById(R.id.spot_description);
        TextView distance = view.findViewById(R.id.spot_distance);

        if (spotNode != null) {
//            actionLog("browse checkin", checkin.location, checkin.key);
//            username.setText(checkin.username);
//            location.setText(checkin.location);
            //TODO:
            description.setText(getDescription(spotName));

            float dist = Utility.gpsToMeter(latitude, longitude, Float.valueOf(spotNode.lat), Float.valueOf(spotNode.lng));
            distance.setText(String.valueOf((int)dist) + getString(R.string.meter));


            setPhoto(view, spotName);
            setActionBtn(view, spotNode);
//            setComment(view, checkin);
        } else {
            description.setText("ERROR");
            distance.setText("");

            setPhoto(view, "");
        }

        refresh();
        RecyclerView recyclerView = view.findViewById(R.id.more_checkin_view);
        recyclerView.setAdapter(checkinItemAdapter);
        recyclerView.setLayoutManager(new StaggeredGridLayoutManager(2, StaggeredGridLayoutManager.VERTICAL));

        ItemClickSupport.addTo(recyclerView).setOnItemClickListener(
                new ItemClickSupport.OnItemClickListener() {
                    @Override
                    public void onItemClicked(RecyclerView recyclerView, int position, View v) {
                        Checkin checkin = checkinItemAdapter.getItem(position);
                        CheckinDialogFragment checkinDialogFragment = CheckinDialogFragment.newInstance(checkin.key);
                        checkinDialogFragment.show(Objects.requireNonNull(getFragmentManager()), "fragment_checkin_dialog");
                    }
                }
        );

    }

    private void setPhoto(final View view, final String filename) {
//        StorageReference storageReference = FirebaseStorage.getInstance().getReference().child("Image").child(filename + ".png");
        StorageReference storageReference = FirebaseStorage.getInstance().getReference().child("image").child(filename + ".png");
        final ImageView photo = view.findViewById(R.id.photo);

        if (filename.equals("")) {
            photo.setVisibility(View.GONE);
            return;
        }

//        Glide.with(Objects.requireNonNull(getContext()))
//                .load(storageReference)
//                .apply(new RequestOptions().placeholder(R.drawable.ic_broken_image_black_48dp))
//                .into(photo);
//        Glide.with(getContext())
//                .load(storageReference)
//                .into(photo);

        GlideApp.with(this /* context */)
                .load(storageReference)
                .apply(new RequestOptions().placeholder(R.drawable.ic_broken_image_black_48dp))
                .into(photo);
    }

    private void setActionBtn(final View view, final SpotNode spotNode) {
        final LinearLayout locateBtn = view.findViewById(R.id.btn_spot_locate);
        locateBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                PersonalMapFragment personalMapFragment = new PersonalMapFragment();
                for (Fragment fragment: getFragmentManager().getFragments()) {
                    if (fragment.getClass() == PersonalMapFragment.class) {
                        personalMapFragment = (PersonalMapFragment) fragment;
                    }
                }
                PersonalFragment personalFragment =  (PersonalFragment)getParentFragment();
                personalFragment.switchTab(0);
                personalMapFragment.onLocateClick(spotNode.lat, spotNode.lng);
                Fragment fragment = Objects.requireNonNull(getFragmentManager()).findFragmentByTag("SpotDescritionDialogFragment");
                Objects.requireNonNull(getFragmentManager()).beginTransaction().remove(fragment).commitAllowingStateLoss();
            }
        });

    }
        public void refresh() {
        if (FirebaseAuth.getInstance().getCurrentUser() != null) {
            if (checkinItemAdapter == null) checkinItemAdapter = new CheckinItemAdapter(getActivity(), new ArrayList<Checkin>(), this);
            checkinItemAdapter.clear();
            for (final Checkin checkin : checkinMap.values()) {
                if (checkin.location.equals(spotName))
                    checkinItemAdapter.insert(checkin, 0);
            }
        }
    }

    @Override
    public void onStart() {
        super.onStart();
        // set dialog layout
        Objects.requireNonNull(getDialog().getWindow())
                .setLayout(WindowManager.LayoutParams.MATCH_PARENT,     // width
                        WindowManager.LayoutParams.WRAP_CONTENT);    // height
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
    }

    @Override
    public void show(FragmentManager manager, String tag) {
//        mDismissed = false;
//        mShownByMe = true;
        FragmentTransaction ft = manager.beginTransaction();
        ft.add(this, tag);
        ft.commitAllowingStateLoss();
    }

}
