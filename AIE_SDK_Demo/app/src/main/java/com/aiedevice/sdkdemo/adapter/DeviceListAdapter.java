package com.aiedevice.sdkdemo.adapter;

import android.content.Context;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.aiedevice.sdk.account.AuthManager;
import com.aiedevice.sdk.base.net.CommonResultListener;
import com.aiedevice.sdk.device.DeviceManager;
import com.aiedevice.sdk.device.bean.BeanDeviceDetail;
import com.aiedevice.sdk.device.bean.BeanDeviceHardwareAttr;
import com.aiedevice.sdk.device.bean.BeanDeviceHardwareList;
import com.aiedevice.sdkdemo.R;
import com.aiedevice.sdkdemo.utils.Toaster;

import java.util.List;

public class DeviceListAdapter extends RecyclerView.Adapter<RecyclerView.ViewHolder> {
    private static final String TAG = DeviceListAdapter.class.getSimpleName();

    private Context mContext;
    private LayoutInflater mLayoutInflater;
    private List<BeanDeviceDetail> mItems;
    private DeviceSelectListener mDevSelectListener;
    private View mLastSelectView = null;

    public DeviceListAdapter(Context context) {
        mContext = context;
        mLayoutInflater = LayoutInflater.from(context);

    }

    public void setDeviceSelectListener(DeviceSelectListener listener) {
        mDevSelectListener = listener;
    }

    public void clearItems() {
        if (mItems == null) return;
        mItems.clear();
        notifyDataSetChanged();
    }

    public void setItems(List<BeanDeviceDetail> items) {
        mItems = items;
        notifyDataSetChanged();
    }

    public BeanDeviceDetail getItem(int position) {
        return mItems.get(position);
    }

    @Override
    public int getItemCount() {
        return mItems == null ? 0 : mItems.size();
    }

    @Override
    public RecyclerView.ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View view1 = mLayoutInflater.inflate(R.layout.master_item_layout, parent, false);
        MasterViewHolder holder = new MasterViewHolder(view1);
        return holder;
    }

    @Override
    public void onBindViewHolder(RecyclerView.ViewHolder holder, int position) {
        final BeanDeviceDetail detail = getItem(position);
        MasterViewHolder viewHolder = (MasterViewHolder) holder;
        viewHolder.id.setText(" 设备ID :" + detail.getId());
        viewHolder.name.setText(" 设备名称 :" + detail.getName());
        viewHolder.root_layout.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (mLastSelectView != null)
                    mLastSelectView.setSelected(false);
                mLastSelectView = view.findViewById(R.id.root_layout);
                mLastSelectView.setSelected(true);
                AuthManager.setDeviceInfo(detail.getId(), detail.getAppId());
                if (mDevSelectListener != null) {
                    mDevSelectListener.onDeviceSelected(detail);
                }
            }
        });
        viewHolder.btn_detail.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                DeviceManager.getDeviceDetail(mContext, new CommonResultListener<BeanDeviceDetail>() {
                    @Override
                    public void onResultSuccess(BeanDeviceDetail beanResult) {
                        Log.i(TAG, "deviceDetail:" + beanResult.toString());
                        Toaster.show(beanResult.toString());
                    }

                    @Override
                    public void onResultFailed(int errorCode, String desc) {
                        Log.e(TAG, "onError errCode:" + errorCode + ",errMsg:" + desc);
                    }
                });
            }
        });

        viewHolder.btn_hardware.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                DeviceManager.getDeviceHardwareInfo(mContext, new CommonResultListener<BeanDeviceHardwareList>() {
                    @Override
                    public void onResultSuccess(BeanDeviceHardwareList beanResult) {
                        for (BeanDeviceHardwareAttr attr : beanResult.getList()) {
                            Log.i(TAG, "BeanDeviceHardwareAttr:" + attr.toString());
                        }
                        Toaster.show(beanResult.toString());
                    }

                    @Override
                    public void onResultFailed(int errorCode, String desc) {
                        Log.e(TAG, "onError errCode:" + errorCode + ",desc:" + desc);

                    }
                });
            }
        });


        if (mLastSelectView == null) {
            mLastSelectView = viewHolder.root_layout;
            viewHolder.root_layout.setSelected(true);
        }
    }

    class MasterViewHolder extends RecyclerView.ViewHolder {

        TextView id;
        TextView name;
        RelativeLayout root_layout;
        Button btn_detail;
        Button btn_hardware;

        public MasterViewHolder(View itemView) {
            super(itemView);
            id = (TextView) itemView.findViewById(R.id.tv_device_id);
            name = (TextView) itemView.findViewById(R.id.tv_res_title);
            root_layout = (RelativeLayout) itemView.findViewById(R.id.root_layout);
            btn_detail = (Button) itemView.findViewById(R.id.btn_detail);
            btn_hardware = (Button) itemView.findViewById(R.id.btn_hardware);
        }
    }

    public interface DeviceSelectListener {
        void onDeviceSelected(BeanDeviceDetail deviceDetail);
    }
}
