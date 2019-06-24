package nctu.cs.cgv.itour.fragment;

import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.design.widget.TabLayout;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentPagerAdapter;
import android.support.v4.view.MenuItemCompat;
import android.support.v7.app.ActionBar;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;

import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

import nctu.cs.cgv.itour.R;
import nctu.cs.cgv.itour.activity.MainActivity;
import nctu.cs.cgv.itour.custom.ArrayAdapterSearchView;
import nctu.cs.cgv.itour.custom.AutoCompleteAdapter;
import nctu.cs.cgv.itour.custom.MyViewPager;
import nctu.cs.cgv.itour.object.Checkin;
import nctu.cs.cgv.itour.object.Node;

import static nctu.cs.cgv.itour.MyApplication.VERSION_ALL_FEATURE;
import static nctu.cs.cgv.itour.MyApplication.VERSION_ONLY_GOOGLE_COMMENT;
import static nctu.cs.cgv.itour.MyApplication.VERSION_OPTION;
import static nctu.cs.cgv.itour.MyApplication.spotList;
import static nctu.cs.cgv.itour.Utility.actionLog;
import static nctu.cs.cgv.itour.Utility.dpToPx;
import static nctu.cs.cgv.itour.activity.MainActivity.firebaseLogManager;
import static nctu.cs.cgv.itour.object.FirebaseLogData.LOG_NOTE_IS_COLLECTED_TOGO;
import static nctu.cs.cgv.itour.object.FirebaseLogData.LOG_NOTE_IS_NOT_COLLECTED_TOGO;
import static nctu.cs.cgv.itour.object.FirebaseLogData.LOG_SEARCH_LOCATION;
import static nctu.cs.cgv.itour.object.FirebaseLogData.LOG_TOGO_LOCATE;

public class PersonalFragment extends Fragment {

    private static final String TAG = "PersonalFragment";
    private ActionBar actionBar;
    public MyViewPager viewPager;
    private List<Fragment> fragmentList;
    public PersonalMapFragment personalMapFragment;
    public TogoFragment togoFragment;
    private CollectedCheckinFragment collectedCheckinFragment;
    private PostedCheckinFragment postedCheckinFragment;
    private TabLayout tabLayout;
    public static PersonalFragment newInstance() {
        return new PersonalFragment();
    }

    public PersonalFragment() {
        togoFragment = TogoFragment.newInstance();
        collectedCheckinFragment = CollectedCheckinFragment.newInstance();
        postedCheckinFragment = PostedCheckinFragment.newInstance();
        personalMapFragment = PersonalMapFragment.newInstance();
        fragmentList = new ArrayList<>();

        fragmentList.add(personalMapFragment);
        fragmentList.add(togoFragment);
        if (VERSION_OPTION == VERSION_ALL_FEATURE) {
            fragmentList.add(collectedCheckinFragment);
            fragmentList.add(postedCheckinFragment);
        }

//        fragmentList.add(personalMapFragment);
    }

    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, ViewGroup parent, Bundle savedInstanceState) {
        if (VERSION_OPTION == VERSION_ONLY_GOOGLE_COMMENT) setHasOptionsMenu(true);
        return inflater.inflate(R.layout.fragment_list, parent, false);
    }

    @Override
    public void onViewCreated(@NonNull View view, Bundle savedInstanceState) {

        actionBar = ((AppCompatActivity) Objects.requireNonNull(getActivity())).getSupportActionBar();



        viewPager = view.findViewById(R.id.view_pager);
        viewPager.setAdapter(new FragmentPagerAdapter(getChildFragmentManager()) {

//            String tabTitles[] = new String[]{"我的地點", "收藏打卡", "個人發文"};

            String tabTitles[] = new String[]{"我的地圖", "我的地點", "收藏打卡", "個人發文"};

            @Override
            public Fragment getItem(int position) {
                return fragmentList.get(position);
            }

            @Override
            public int getCount() {
                return fragmentList.size();
            }

            @Override
            public CharSequence getPageTitle(int position) {
                // Generate title based on item position
                return tabTitles[position];
            }
        });
        viewPager.setPagingEnabled(false);
        viewPager.setOffscreenPageLimit(2);

        tabLayout = view.findViewById(R.id.tab_layout);
        tabLayout.setupWithViewPager(viewPager);
    }

    @Override
    public void setUserVisibleHint(boolean isVisibleToUser) {
        super.setUserVisibleHint(isVisibleToUser);

        if (getUserVisibleHint()) {
            if (actionBar != null) {
                actionBar.setElevation(0);
                actionBar.setSubtitle(getString(R.string.subtitle_personal));
            }
            togoFragment.refresh();
            collectedCheckinFragment.refresh();
            postedCheckinFragment.refresh();
            personalMapFragment.reRender();
//            addCollectedCheckinIcon();
//            addPostedCheckinIcon();
        } else {
            if (actionBar != null) {
//                personalMapFragment.clearMap();
                actionBar.setElevation(dpToPx(getContext(), 4));
            }
        }
    }

    public void notifyCollectedCheckinChanged() {
        collectedCheckinFragment.checkinItemAdapter.notifyDataSetChanged();
    }

    public void notifyPostedCheckinChanged() {
        postedCheckinFragment.checkinItemAdapter.notifyDataSetChanged();
    }

    public void addPostedCheckinIcon() {
//        if (postedCheckinFragment.getActivity() == null) {
//            Log.d("NIVRAMMMM", "PPPPPPPPPPPPPP");
//            return;
//        }
        postedCheckinFragment.refresh();
        List<Checkin> postedCheckins = postedCheckinFragment.checkinItemAdapter.checkins;
        for (Checkin checkin: postedCheckins) {
            Checkin newCheckin = new Checkin(checkin);
            personalMapFragment.addCheckin(newCheckin, "posted");
        }
    }

    public void addCollectedCheckinIcon() {
//        if (collectedCheckinFragment.getActivity() == null) {
//            Log.d("NIVRAMMMM", "CCCCCCCCCCCCCCC");
//            return;
//        }
        ((MainActivity) getActivity()).queryCollectedIsVisited();
        collectedCheckinFragment.refresh();
        List<Checkin> collectedCheckins = collectedCheckinFragment.checkinItemAdapter.checkins;
        for (Checkin checkin: collectedCheckins) {
            Checkin newCheckin = new Checkin(checkin);
            personalMapFragment.addCheckin(newCheckin, "collected");
        }
    }

    @Override
    public void onCreateOptionsMenu(Menu menu, MenuInflater inflater) {
        Log.d("SEARCH_MENU", "create");

        if (VERSION_OPTION == VERSION_ALL_FEATURE) {
            super.onCreateOptionsMenu(menu, inflater);
        } else {
            inflater.inflate(R.menu.menu_search, menu);
            super.onCreateOptionsMenu(menu, inflater);
            Log.d("SEARCH_MENU", "setup");
            // set search view autocomplete
            final AutoCompleteAdapter adapter = new AutoCompleteAdapter(getContext(), R.layout.item_search, new ArrayList<>(spotList.getFullSpotsName()));
            final ArrayAdapterSearchView searchView = (ArrayAdapterSearchView) MenuItemCompat.getActionView(menu.findItem(R.id.action_search));
            searchView.setAdapter(adapter);
            searchView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
                @Override
                public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                    String autocompleteStr = adapter.getItem(position);
                    Node node = spotList.fullNodeMap.get(autocompleteStr);
                    personalMapFragment.translateToImgPx(node.x, node.y, false);
                    String logNote = "";
                    if(((MainActivity)getActivity()).personalFragment.togoFragment.togoItemAdapter.isTogo(autocompleteStr)) {
                        logNote = LOG_NOTE_IS_COLLECTED_TOGO;
                    } else {
                        logNote = LOG_NOTE_IS_NOT_COLLECTED_TOGO;
                    }
                    personalMapFragment.searchLocationGoogleCommentVersionDialog(spotList.fullNodeMap.get(autocompleteStr).lat, spotList.fullNodeMap.get(autocompleteStr).lng, LOG_TOGO_LOCATE, autocompleteStr, logNote);

                    searchView.clearFocus();
                    searchView.setText(autocompleteStr);
                    firebaseLogManager.log(LOG_SEARCH_LOCATION, autocompleteStr);
                    // send action log to server
                    actionLog("search: ", autocompleteStr, "");
                }
            });
        }

    }
    public void switchTab(int position) {
        tabLayout.getTabAt(position).select();
    }
}
