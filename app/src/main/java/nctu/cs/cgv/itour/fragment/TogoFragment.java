package nctu.cs.cgv.itour.fragment;

import android.app.Dialog;
import android.content.Context;
import android.graphics.Canvas;
import android.os.Bundle;
import android.support.design.widget.BottomSheetDialog;
import android.support.v4.app.Fragment;
import android.support.v4.widget.SwipeRefreshLayout;
import android.support.v7.app.ActionBar;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.DividerItemDecoration;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.helper.ItemTouchHelper;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.view.WindowManager;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.AutoCompleteTextView;
import android.widget.Button;
import android.widget.EditText;

import java.io.File;
import java.util.ArrayList;
import java.util.Objects;

import nctu.cs.cgv.itour.R;
import nctu.cs.cgv.itour.activity.LocationChooseActivity;
import nctu.cs.cgv.itour.activity.MainActivity;
import nctu.cs.cgv.itour.custom.ItemClickSupport;
import nctu.cs.cgv.itour.custom.SwipeController;
import nctu.cs.cgv.itour.custom.SwipeControllerActions;
import nctu.cs.cgv.itour.custom.TogoItemAdapter;
import nctu.cs.cgv.itour.object.Node;
import nctu.cs.cgv.itour.object.SpotList;
import nctu.cs.cgv.itour.object.SystemNotification;
import nctu.cs.cgv.itour.object.TogoPlannedData;

import static nctu.cs.cgv.itour.MyApplication.dirPath;
import static nctu.cs.cgv.itour.MyApplication.mapTag;
import static nctu.cs.cgv.itour.Utility.actionLog;
import static nctu.cs.cgv.itour.Utility.dpToPx;
import static nctu.cs.cgv.itour.Utility.hideSoftKeyboard;
import static nctu.cs.cgv.itour.activity.MainActivity.isSpot;

public class TogoFragment extends Fragment{
    private static final String TAG = "TogoFragment";
    public TogoItemAdapter togoItemAdapter;
    private SwipeRefreshLayout swipeRefreshLayout;
    private ActionBar actionBar;
    private final int ORDER_TIME = 0;
    private final int ORDER_POPULAR = 1;
    private int orderFlag = ORDER_TIME;
    private SpotList spotList;
    public static TogoFragment newInstance() {
        return new TogoFragment();
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        actionBar = ((AppCompatActivity) getActivity()).getSupportActionBar();
        togoItemAdapter = new TogoItemAdapter(setTogoList(), this);
        ((MainActivity) getActivity()).queryTogoIsVisited();

    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {

        return inflater.inflate(R.layout.fragment_togo, container, false);
    }

    @Override
    public void onViewCreated(View view, Bundle savedInstanceState) {
        setHasOptionsMenu(true);
//
//        swipeRefreshLayout = view.findViewById(R.id.swipe_refresh_layout);
//        swipeRefreshLayout.setOnRefreshListener(new SwipeRefreshLayout.OnRefreshListener() {
//            @Override
//            public void onRefresh() {
//                refresh();
//                swipeRefreshLayout.setRefreshing(false);
//            }
//        });
//        swipeRefreshLayout.setColorSchemeResources(R.color.gps_marker_color);
//
        Context context = getContext();
        RecyclerView.LayoutManager layoutManager = new LinearLayoutManager(context);
        RecyclerView recyclerView = view.findViewById(R.id.togo_list) ;
        DividerItemDecoration itemDecor = new DividerItemDecoration(context, ((LinearLayoutManager)layoutManager).getOrientation());
        recyclerView.setAdapter(togoItemAdapter);
        final SwipeController swipeController = new SwipeController(new SwipeControllerActions() {
            @Override
            public void onRightClicked(int position) {
                TogoPlannedData togoPlannedData = togoItemAdapter.togoPlannedDataList.get(position);
                togoItemAdapter.removeTogo(togoPlannedData, position);

            }
        });

        togoItemAdapter.queryTogoList();
        ItemTouchHelper itemTouchhelper = new ItemTouchHelper(swipeController);
        itemTouchhelper.attachToRecyclerView(recyclerView);
        recyclerView.setLayoutManager(layoutManager);
        recyclerView.addItemDecoration(itemDecor);
        recyclerView.addItemDecoration(new RecyclerView.ItemDecoration() {
            @Override
            public void onDraw(Canvas c, RecyclerView parent, RecyclerView.State state) {
                swipeController.onDraw(c);
            }
        });
        ItemClickSupport.addTo(recyclerView).setOnItemClickListener(
                new ItemClickSupport.OnItemClickListener() {
                    @Override
                    public void onItemClicked(RecyclerView recyclerView, int position, View v) {
                        String spotName = togoItemAdapter.togoPlannedDataList.get(position).locationName;
                        SpotDescritionDialogFragment spotDescritionDialogFragment = SpotDescritionDialogFragment.newInstance(spotName);
                        spotDescritionDialogFragment.show(getFragmentManager(), "SpotDescritionDialogFragment");
                    }
                }
        );

    }


    public void refresh() {

    }

    public void addItem() {
        final Dialog dialog= new Dialog(getActivity());
        dialog.setContentView(R.layout.add_togo_dialog);
//        dialog.getWindow().clearFlags(WindowManager.LayoutParams.FLAG_DIM_BEHIND);
        final AutoCompleteTextView autoCompleteTextView = dialog.findViewById(R.id.place_togo);

        if (spotList == null) {
            spotList = new SpotList(new File(dirPath + "/" + mapTag + "_spot_list.txt"));
        }
        ArrayList<String> array = new ArrayList<>();
        array.addAll(spotList.getFullSpotsName());
        final ArrayAdapter<String> adapter = new ArrayAdapter<>(getActivity(), R.layout.item_search, array);

        autoCompleteTextView.setThreshold(0);
        autoCompleteTextView.setAdapter(adapter);
        autoCompleteTextView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                hideSoftKeyboard(getActivity());
                autoCompleteTextView.setText(adapter.getItem(position));
//                String autocompleteStr = adapter.getItem(position);
            }
        });

        autoCompleteTextView.setOnTouchListener(new View.OnTouchListener(){
            @Override
            public boolean onTouch(View v, MotionEvent event){
                autoCompleteTextView.showDropDown();
                return false;
            }
        });

        Button confirmBtn = dialog.findViewById(R.id.btn_confirm_adding_togo);
        confirmBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                final String togoName = autoCompleteTextView.getText().toString();
                if (isSpot(togoName)) {
                    togoItemAdapter.addTogo(new TogoPlannedData(togoName));
                    dialog.dismiss();
                } else {
                    //TODO : TOAST MESSAGE
                }

            }
        });
        Button cancelBtn = dialog.findViewById(R.id.btn_cancel_adding_togo);
        cancelBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                dialog.dismiss();
            }
        });

        WindowManager.LayoutParams lp = new WindowManager.LayoutParams();
        lp.copyFrom(dialog.getWindow().getAttributes());
        lp.width = WindowManager.LayoutParams.MATCH_PARENT;
        lp.gravity = Gravity.TOP;
        dialog.setCanceledOnTouchOutside(false);
        dialog.show();
        dialog.getWindow().setBackgroundDrawableResource(android.R.color.transparent);
        dialog.getWindow().setAttributes(lp);
    }
    @Override
    public void setUserVisibleHint(boolean isVisibleToUser) {
        super.setUserVisibleHint(isVisibleToUser);
        if (getUserVisibleHint()) {
            if (actionBar != null) {
                actionBar.setElevation(0);
                actionBar.setSubtitle(getString(R.string.subtitle_togo));
            }
            refresh();
        } else {
            if (actionBar != null) {
                actionBar.setElevation(dpToPx(getContext(), 4));
            }
        }
    }

    @Override
    public void onCreateOptionsMenu(Menu menu, MenuInflater inflater) {
        inflater.inflate(R.menu.togo_menu, menu);
        super.onCreateOptionsMenu(menu, inflater);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case R.id.add_item:
                addItem();
                return true;
            default:
                return super.onOptionsItemSelected(item);
        }
    }

    public ArrayList<TogoPlannedData> setTogoList() {
        ArrayList<TogoPlannedData> togoPlannedData = new ArrayList<>();
        togoPlannedData.add(new TogoPlannedData("三協成博物館"));
        togoPlannedData.add(new TogoPlannedData("紅樓中餐廳"));
        togoPlannedData.add(new TogoPlannedData("重建街戀愛巷"));
        togoPlannedData.add(new TogoPlannedData("海風餐廳"));
        togoPlannedData.add(new TogoPlannedData("真理大學禮拜堂"));
        return togoPlannedData;
    }

}