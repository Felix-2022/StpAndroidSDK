package com.aiedevice.sdkdemo.adapter;

import android.content.Context;
import android.support.v7.widget.RecyclerView;
import android.text.TextUtils;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.aiedevice.sdkdemo.bean.EspBleDevice;
import com.aiedevice.sdkdemo.presenter.SearchDeviceActivityPresenterImpl;
import com.aiedevice.sdkdemo.R;

import java.util.List;
import java.util.Locale;

/**
 * Created by lixiang on 17-11-3.
 */
public class BTAdapter extends RecyclerView.Adapter<BTHolder> implements View.OnClickListener {
    private final List<EspBleDevice> bleDevices;
    private Context context;
    private OnDeviceSelectedListener onDeviceSelectedListener;

    public BTAdapter(List<EspBleDevice> bleDevices, Context context) {
        this.bleDevices = bleDevices;
        this.context = context;
    }

    public void setOnDeviceSelectedListener(OnDeviceSelectedListener onDeviceSelectedListener) {
        this.onDeviceSelectedListener = onDeviceSelectedListener;
    }

    @Override
    public BTHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View itemView = LayoutInflater.from(this.context).inflate(R.layout.blufi_device_item, parent, false);
        return new BTHolder(itemView);
    }

    @Override
    public void onBindViewHolder(BTHolder holder, int position) {
        Log.i(SearchDeviceActivityPresenterImpl.TAG, "onBindViewHolder");
        EspBleDevice bleDevice = bleDevices.get(position);

        String name = bleDevice.device.getName();
        if (TextUtils.isEmpty(name)) {
            name = "Unnamed";
        }
        holder.tvDeviceName.setText(name);

        holder.tvDeviceAddress.setText(String.format(Locale.ENGLISH,
                "%s  %d", bleDevice.device.getAddress(), bleDevice.rssi));

        holder.checkBox.setChecked(bleDevice.checked);
        holder.checkBox.setVisibility(View.GONE);//不考虑多个设备
        holder.checkBox.setTag(position);
        holder.checkBox.setOnClickListener(this);
    }

    @Override
    public int getItemCount() {
        return bleDevices.size();
    }

    @Override
    public void onClick(View v) {
        if (onDeviceSelectedListener != null)
            onDeviceSelectedListener.onDeviceSelected(bleDevices.get((Integer) v.getTag()));
    }

    public interface OnDeviceSelectedListener {
        void onDeviceSelected(EspBleDevice espBleDevice);
    }
}
