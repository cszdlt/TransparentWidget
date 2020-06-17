package com.cszdlt.launchwidget.config;

import android.content.ComponentName;
import android.content.Context;
import android.content.pm.PackageInfo;
import android.content.pm.PackageManager;
import android.util.AttributeSet;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.cszdlt.launchwidget.R;

import java.util.List;

public class AppListItemAdapter extends RecyclerView.Adapter<AppListItemAdapter.ViewHolder> {

    private Context context;
    private List<ConfigActivity.ItemData> dataList;
    private AdapterOnClick observer;

    public AppListItemAdapter(Context context, List<ConfigActivity.ItemData> dataList,
                              AdapterOnClick observer) {
        this.context = context;
        this.dataList = dataList;
        this.observer = observer;

        notifyItemRangeInserted(0, dataList.size());
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        ItemView itemView = new ItemView(context);
        return new ViewHolder(itemView, observer);
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position) {
        final ConfigActivity.ItemData data = dataList.get(position);
        holder.getView().packageTv.setText(data.packageName);
        holder.getView().appTv.setText(data.appName);
        holder.getView().imageView.setImageDrawable(data.icon);
        holder.setComponentName(data.packageName, data.className);
    }

    @Override
    public int getItemCount() {
        return dataList.size();
    }

    public interface AdapterOnClick {
        void onClick(String packageName, String className);
    }

    static class ItemView extends FrameLayout {

        public ImageView imageView;
        public TextView appTv;
        public TextView packageTv;

        public ItemView(Context context) {
            super(context);
            initView();
        }

        public ItemView(Context context, AttributeSet attrs) {
            super(context, attrs);
            initView();
        }

        private void initView() {
            LayoutInflater.from(getContext()).inflate(R.layout.app_list_item, this);
            imageView = findViewById(R.id.icon);
            appTv = findViewById(R.id.appName);
            packageTv = findViewById(R.id.packageName);
        }

    }

    static class ViewHolder extends RecyclerView.ViewHolder implements View.OnClickListener {

        private String packageName = null;
        private String className = null;
        private AdapterOnClick observer;

        ViewHolder(View itemView, AdapterOnClick observer) {
            super(itemView);
            this.observer = observer;
            itemView.setOnClickListener(this);
        }

        public ItemView getView() {
            return (ItemView) itemView;
        }

        public void setComponentName(String packageName, String className) {
            this.packageName = packageName;
            this.className = className;
        }

        @Override
        public void onClick(View v) {
            if (observer != null) {
                observer.onClick(packageName, className);
            }
        }
    }

};