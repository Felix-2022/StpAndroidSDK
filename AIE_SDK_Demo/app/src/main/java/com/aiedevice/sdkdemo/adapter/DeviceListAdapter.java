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

import com.aiedevice.sdk.account.AccountManager;
import com.aiedevice.sdk.base.bean.BeanResult;
import com.aiedevice.sdk.base.net.ResultListener;
import com.aiedevice.sdk.device.DeviceManager;
import com.aiedevice.sdk.device.bean.BeanDeviceDetail;
import com.aiedevice.sdk.device.bean.BeanDeviceHardwareAttr;
import com.aiedevice.sdk.device.bean.BeanDeviceHardwareList;
import com.aiedevice.sdk.util.GsonUtils;
import com.aiedevice.sdkdemo.R;
import com.aiedevice.sdkdemo.utils.Toaster;
import com.google.gson.Gson;

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
                AccountManager.setDeviceInfo(detail.getId(), detail.getAppId());
                if (mDevSelectListener != null) {
                    mDevSelectListener.onDeviceSelected(detail);
                }
            }
        });
        viewHolder.btn_detail.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                DeviceManager.getDeviceDetail(mContext, new ResultListener() {
                    @Override
                    public void onSuccess(BeanResult beanResult) {
                        Log.i(TAG, "device detail:" + beanResult.getData());
                        Gson gson = GsonUtils.getGson();
                        BeanDeviceDetail deviceDetail = gson.fromJson(beanResult.getData(), BeanDeviceDetail.class);
                        Log.i(TAG, "deviceDetail.name:" + deviceDetail.getName());
                        Toaster.show(beanResult.getData());
                    }

                    @Override
                    public void onError(int errCode, String errMsg) {
                        Log.e(TAG, "onError errCode:" + errCode + ",errMsg:" + errMsg);
                    }
                });
            }
        });

        viewHolder.btn_hardware.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                DeviceManager.getDeviceHardwareInfo(mContext, new ResultListener() {
                    @Override
                    public void onSuccess(BeanResult beanResult) {
                        Log.i(TAG, "device hardware:" + beanResult.getData());
                        Toaster.show(beanResult.getData());
                        Gson gson = GsonUtils.getGson();
                        BeanDeviceHardwareList bean = gson.fromJson(beanResult.getData(), BeanDeviceHardwareList.class);
                        for (BeanDeviceHardwareAttr attr : bean.getList()) {
                            Log.i(TAG, "attr key:" + attr.getKey() + ",value:" + attr.getVal());
                        }
                    }

                    @Override
                    public void onError(int errCode, String errMsg) {
                        Log.e(TAG, "onError errCode:" + errCode + ",errMsg:" + errMsg);
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