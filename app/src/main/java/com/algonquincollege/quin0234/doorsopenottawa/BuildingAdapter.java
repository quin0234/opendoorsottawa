package com.algonquincollege.quin0234.doorsopenottawa;


import android.content.Context;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.algonquincollege.quin0234.doorsopenottawa.model.BuildingPOJO;
import com.squareup.picasso.Picasso;

import java.util.List;

import static com.algonquincollege.quin0234.doorsopenottawa.services.MyService.TAG;

/**
 * BuildingAdapter.
 *
 */
public class BuildingAdapter extends RecyclerView.Adapter<BuildingAdapter.ViewHolder> {

    private static final String PHOTOS_BASE_URL = "https://doors-open-ottawa.mybluemix.net/buildings/";

    private Context              mContext;
    private List<BuildingPOJO>     mBuildings;

    public BuildingAdapter(Context context, List<BuildingPOJO> Buildings) {
        this.mContext = context;
        this.mBuildings = Buildings;
    }

    @Override
    public BuildingAdapter.ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        LayoutInflater inflater = LayoutInflater.from(mContext);
        View BuildingView = inflater.inflate(R.layout.item_building, parent, false);
        ViewHolder viewHolder = new ViewHolder(BuildingView);
        return viewHolder;
    }

    @Override
    public void onBindViewHolder(BuildingAdapter.ViewHolder holder, int position) {
        final BuildingPOJO aBuilding = mBuildings.get(position);

        holder.tvName.setText(aBuilding.getNameEN());


        String url = PHOTOS_BASE_URL + aBuilding.getBuildingId() + "/image";
        Picasso.with(mContext)
                .load(url)
                .error(R.drawable.noimagefound)
                .fit()
                .into(holder.imageView);

        holder.mView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Toast.makeText(mContext, aBuilding.getNameEN(),
                        Toast.LENGTH_SHORT).show();

                Log.i(TAG, aBuilding.getNameEN());

            }
        });
    }

    @Override
    public int getItemCount() {
        return mBuildings.size();
    }

    public static class ViewHolder extends RecyclerView.ViewHolder {

        public TextView tvName;
        public ImageView imageView;
        public View mView;

        public ViewHolder(View BuildingView) {
            super(BuildingView);

            tvName = (TextView) BuildingView.findViewById(R.id.buildingNameText);
            imageView = (ImageView) BuildingView.findViewById(R.id.imageView);
            mView = BuildingView;
        }
    }

}
