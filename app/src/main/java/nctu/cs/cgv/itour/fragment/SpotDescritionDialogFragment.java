package nctu.cs.cgv.itour.fragment;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.content.res.ResourcesCompat;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.StaggeredGridLayoutManager;

import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;

import android.os.Bundle;
import android.support.v4.app.DialogFragment;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentTransaction;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.WindowManager;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.TextView;

import com.bumptech.glide.request.RequestOptions;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.Query;

import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

import nctu.cs.cgv.itour.R;
import nctu.cs.cgv.itour.Utility;
import nctu.cs.cgv.itour.activity.MainActivity;
import nctu.cs.cgv.itour.custom.CheckinCommentItemAdapter;
import nctu.cs.cgv.itour.custom.CheckinItemAdapter;
import nctu.cs.cgv.itour.custom.GoogleCommentItemAdapter;
import nctu.cs.cgv.itour.custom.ItemClickSupport;
import nctu.cs.cgv.itour.custom.TogoItemAdapter;
import nctu.cs.cgv.itour.object.Checkin;
import nctu.cs.cgv.itour.object.CheckinComment;
import nctu.cs.cgv.itour.object.GlideApp;
import nctu.cs.cgv.itour.object.GoogleComment;
import nctu.cs.cgv.itour.object.GoogleCommentManager;
import nctu.cs.cgv.itour.object.SpotNode;
import nctu.cs.cgv.itour.object.TogoPlannedData;

import static nctu.cs.cgv.itour.MyApplication.VERSION_ALL_FEATURE;
import static nctu.cs.cgv.itour.MyApplication.VERSION_ONLY_GOOGLE_COMMENT;
import static nctu.cs.cgv.itour.MyApplication.VERSION_OPTION;
import static nctu.cs.cgv.itour.MyApplication.latitude;
import static nctu.cs.cgv.itour.MyApplication.longitude;
import static nctu.cs.cgv.itour.MyApplication.mapTag;
import static nctu.cs.cgv.itour.MyApplication.spotList;
import static nctu.cs.cgv.itour.Utility.actionLog;
import static nctu.cs.cgv.itour.activity.MainActivity.checkinMap;
import static nctu.cs.cgv.itour.activity.MainActivity.collectedCheckinKey;
import static nctu.cs.cgv.itour.activity.MainActivity.getSpotDescription;

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
        SpotNode spotNode = spotList.fullNodeMap.get(spotName);

        TextView description = view.findViewById(R.id.spot_description);
        TextView distance = view.findViewById(R.id.spot_distance);
        TextView spotNameTv = view.findViewById(R.id.spot_name_dialog);
        spotNameTv.setText(spotName);
        if (spotNode != null) {
//            actionLog("browse checkin", checkin.location, checkin.key);
//            username.setText(checkin.username);
//            location.setText(checkin.location);
            //TODO:
            description.setText(getSpotDescription(spotName));

            float dist = Utility.gpsToMeter(latitude, longitude, Float.valueOf(spotNode.lat), Float.valueOf(spotNode.lng));
            distance.setText(String.valueOf((int)dist) + getString(R.string.meter));


            setPhoto(view, spotName);
            setActionBtn(view, spotNode, spotName);
//            setComment(view, checkin);
        } else {
            description.setText("ERROR");
            distance.setText("");

            setPhoto(view, "");
        }

        refresh();
        RecyclerView moreCheckinRecyclerView = view.findViewById(R.id.more_checkin_view);
        RecyclerView googleCommentRecyclerView = view.findViewById(R.id.lv_spot_comment);
        if (VERSION_OPTION == VERSION_ALL_FEATURE) {
            googleCommentRecyclerView.setVisibility(View.GONE);
            moreCheckinRecyclerView.setAdapter(checkinItemAdapter);
            moreCheckinRecyclerView.setLayoutManager(new StaggeredGridLayoutManager(2, StaggeredGridLayoutManager.VERTICAL));

            ItemClickSupport.addTo(moreCheckinRecyclerView).setOnItemClickListener(
                    new ItemClickSupport.OnItemClickListener() {
                        @Override
                        public void onItemClicked(RecyclerView recyclerView, int position, View v) {
                            Checkin checkin = checkinItemAdapter.getItem(position);
                            CheckinDialogFragment checkinDialogFragment = CheckinDialogFragment.newInstance(checkin.key);
                            checkinDialogFragment.show(Objects.requireNonNull(getFragmentManager()), "fragment_checkin_dialog");
                        }
                    }
            );
        } else if (VERSION_OPTION == VERSION_ONLY_GOOGLE_COMMENT) {
            moreCheckinRecyclerView.setVisibility(View.GONE);
            setGoogleComment(view);
        }

    }

    private void setPhoto(final View view, final String filename) {
//        StorageReference storageReference = FirebaseStorage.getInstance().getReference().child("Image").child(filename + ".png");
        StorageReference storageReference = FirebaseStorage.getInstance().getReference().child("image").child(filename + ".png");
        final ImageView photo = view.findViewById(R.id.photo);

        if (filename.equals("")) {
            photo.setVisibility(View.GONE);
            return;
        }


        GlideApp.with(this /* context */)
                .load(storageReference)
                .apply(new RequestOptions().placeholder(R.drawable.ic_broken_image_black_48dp))
                .into(photo);
    }

    void setGoogleComment(View view) {

        RecyclerView googleCommentRecyclerView = view.findViewById(R.id.lv_spot_comment);
        googleCommentRecyclerView.setVisibility(View.VISIBLE);
        final GoogleCommentItemAdapter googleCommentItemAdapter = new GoogleCommentItemAdapter(getContext(), new ArrayList<GoogleComment>());
        googleCommentRecyclerView.setAdapter(googleCommentItemAdapter);
        googleCommentRecyclerView.setLayoutManager(new LinearLayoutManager(getContext(), LinearLayoutManager.VERTICAL, false));
        googleCommentRecyclerView.scrollToPosition(googleCommentItemAdapter.getItemCount() - 1);

//        GoogleComment googleComment = new GoogleComment("很棒", "5", "帥哥");
        GoogleCommentManager googleCommentManager = new GoogleCommentManager();
        List<GoogleComment> googleCommentList = googleCommentManager.getGoogleCommentList(spotName);
        for (GoogleComment googleComment: googleCommentList
             ) {
            googleCommentItemAdapter.add(googleComment);

        }
    }

    private void setActionBtn(final View view, final SpotNode spotNode, final String spotName) {
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

        PersonalFragment personalFragment = ((MainActivity)getActivity()).personalFragment;
        final TogoItemAdapter togoItemAdapter = personalFragment.togoFragment.togoItemAdapter;
        final ImageView saveBtn = view.findViewById(R.id.btn_spot_save);

        if (VERSION_OPTION == VERSION_ALL_FEATURE) {
            saveBtn.setVisibility(View.GONE);
        } else if (VERSION_OPTION == VERSION_ONLY_GOOGLE_COMMENT){
            saveBtn.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    DatabaseReference databaseReference = FirebaseDatabase.getInstance().getReference();
                    if (togoItemAdapter.isTogo(spotName)) {
                        saveBtn.setImageDrawable(ResourcesCompat.getDrawable(getResources(),
                                R.drawable.ic_bookmark_border_black_24dp, null));
                        togoItemAdapter.removeTogo(spotName);
                    } else {
                        saveBtn.setImageDrawable(ResourcesCompat.getDrawable(getResources(),
                                R.drawable.ic_bookmark_blue_24dp, null));
                        togoItemAdapter.addTogo(new TogoPlannedData(spotName));
                    }
                }
            });

            if (togoItemAdapter.isTogo(spotName)) {
                saveBtn.setImageDrawable(ResourcesCompat.getDrawable(getResources(),
                        R.drawable.ic_bookmark_blue_24dp, null));
            }
        }

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
