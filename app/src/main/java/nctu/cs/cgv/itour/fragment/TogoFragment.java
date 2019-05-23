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
import android.util.Log;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.EditText;

import java.util.ArrayList;

import nctu.cs.cgv.itour.R;
import nctu.cs.cgv.itour.custom.SwipeController;
import nctu.cs.cgv.itour.custom.SwipeControllerActions;
import nctu.cs.cgv.itour.custom.TogoItemAdapter;
import nctu.cs.cgv.itour.object.Checkin;
import nctu.cs.cgv.itour.object.TogoData;

import static nctu.cs.cgv.itour.Utility.dpToPx;

public class TogoFragment extends Fragment{
    private static final String TAG = "TogoFragment";
    public TogoItemAdapter togoItemAdapter;
    private SwipeRefreshLayout swipeRefreshLayout;
    private ActionBar actionBar;
    private final int ORDER_TIME = 0;
    private final int ORDER_POPULAR = 1;
    private int orderFlag = ORDER_TIME;

    public static TogoFragment newInstance() {
        return new TogoFragment();
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        actionBar = ((AppCompatActivity) getActivity()).getSupportActionBar();
        togoItemAdapter = new TogoItemAdapter(setTogoList());
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
                TogoData togoData = togoItemAdapter.togoDataList.get(position);
                togoItemAdapter.togoDataList.remove(position);
                togoItemAdapter.removeTogo(togoData);
                togoItemAdapter.notifyItemRemoved(position);
                togoItemAdapter.notifyItemRangeChanged(position, togoItemAdapter.getItemCount());
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

    }


    public void refresh() {

    }

    public void addItem() {
        final Dialog dialog= new BottomSheetDialog(getActivity());
        dialog.setContentView(R.layout.add_togo_dialog);
        dialog.getWindow().clearFlags(WindowManager.LayoutParams.FLAG_DIM_BEHIND);
        Button confirmBtn = dialog.findViewById(R.id.btn_confirm_adding_togo);
        confirmBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                EditText editText = dialog.findViewById(R.id.place_togo);
                final String togoName = editText.getText().toString();
                togoItemAdapter.addTogo(new TogoData(togoName));
                dialog.dismiss();
            }
        });

        Button cancelBtn = dialog.findViewById(R.id.btn_cancel_adding_togo);
        cancelBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                dialog.dismiss();
            }
        });


        dialog.show();
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

    public ArrayList<TogoData> setTogoList() {
        ArrayList<TogoData> togoData = new ArrayList<>();
        togoData.add(new TogoData("三協成博物館"));
        togoData.add(new TogoData("紅樓中餐廳"));
        togoData.add(new TogoData("重建街戀愛巷"));
        togoData.add(new TogoData("海風餐廳"));
        togoData.add(new TogoData("真理大學禮拜堂"));
        return togoData;
    }

}