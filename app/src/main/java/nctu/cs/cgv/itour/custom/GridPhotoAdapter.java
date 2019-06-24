package nctu.cs.cgv.itour.custom;

import android.content.Context;
import android.support.annotation.NonNull;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.ImageView;

import com.bumptech.glide.Glide;
import com.bumptech.glide.request.RequestOptions;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;

import java.util.ArrayList;

import nctu.cs.cgv.itour.R;
import nctu.cs.cgv.itour.object.Checkin;
import nctu.cs.cgv.itour.object.GlideApp;

import static nctu.cs.cgv.itour.MyApplication.VERSION_ALL_FEATURE;
import static nctu.cs.cgv.itour.MyApplication.VERSION_ONLY_GOOGLE_COMMENT;
import static nctu.cs.cgv.itour.MyApplication.VERSION_OPTION;
import static nctu.cs.cgv.itour.MyApplication.fileDownloadURL;

/**
 * Created by lobZter on 2018/1/20.
 */

public class GridPhotoAdapter extends ArrayAdapter<Checkin> {

    private Context context;

    public GridPhotoAdapter(Context context, ArrayList<Checkin> checkins) {
        super(context, 0, checkins);
        this.context = context;
    }

    @NonNull
    @Override
    public View getView(int position, View view, @NonNull ViewGroup parent) {

        LayoutInflater inflater = LayoutInflater.from(context);
        view = inflater.inflate(R.layout.item_grid_photo, parent, false);

        final ImageView photo = view.findViewById(R.id.photo);

        Checkin checkin = getItem(position);
        final String filename = checkin.photo;
        if (VERSION_OPTION == VERSION_ALL_FEATURE) {
            if (!filename.equals("")) {
                Glide.with(context)
                        .load(fileDownloadURL + "?filename=" + filename)
                        .apply(new RequestOptions().placeholder(R.drawable.ic_broken_image_black_48dp))
                        .into(photo);
            }
        } else if (VERSION_OPTION == VERSION_ONLY_GOOGLE_COMMENT) {
//            Log.d("NIVRAM", "SET GRID GCV");

        }


        return view;
    }
}