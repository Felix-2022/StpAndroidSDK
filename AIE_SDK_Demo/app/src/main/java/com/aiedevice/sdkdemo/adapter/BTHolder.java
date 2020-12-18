package com.aiedevice.sdkdemo.adapter;

import android.support.v7.widget.RecyclerView;
import android.view.View;
import android.widget.CheckBox;
import android.widget.TextView;

import com.aiedevice.sdkdemo.R;

public class BTHolder extends RecyclerView.ViewHolder {
    TextView tvDeviceName;
    TextView tvDeviceAddress;
    CheckBox checkBox;

    BTHolder(View itemView) {
        super(itemView);
        tvDeviceName = (TextView) itemView.findViewById(R.id.device_name);
        tvDeviceAddress = (TextView) itemView.findViewById(R.id.device_address);
        checkBox = (CheckBox) itemView.findViewById(R.id.check);
    }
}
