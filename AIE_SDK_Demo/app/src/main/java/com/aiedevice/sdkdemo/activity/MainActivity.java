package com.aiedevice.sdkdemo.activity;

import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.text.TextUtils;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.aiedevice.sdk.account.AccountManager;
import com.aiedevice.sdk.account.bean.BeanLoginData;
import com.aiedevice.sdk.base.bean.BeanResult;
import com.aiedevice.sdk.base.net.ResultListener;
import com.aiedevice.sdk.book.BookManager;
import com.aiedevice.sdk.book.bean.BeanBookDetail;
import com.aiedevice.sdk.book.bean.BeanBookListResult;
import com.aiedevice.sdk.book.bean.BeanStorageStatus;
import com.aiedevice.sdk.classroom.ClassRoomManager;
import com.aiedevice.sdk.device.DeviceManager;
import com.aiedevice.sdk.device.bean.BeanDeviceDetail;
import com.aiedevice.sdk.study.StudyConstants;
import com.aiedevice.sdk.study.StudyManager;
import com.aiedevice.sdk.study.bean.BeanReportBookList;
import com.aiedevice.sdk.study.bean.BeanReportList;
import com.aiedevice.sdk.study.bean.BeanReportTrend;
import com.aiedevice.sdk.study.bean.BeanReportTrendDay;
import com.aiedevice.sdk.util.GsonUtils;
import com.aiedevice.sdkdemo.R;
import com.aiedevice.sdkdemo.activity.blufi.PreConnectActivity;
import com.aiedevice.sdkdemo.adapter.DeviceListAdapter;
import com.aiedevice.sdk.device.bean.BeanDeviceList;
import com.aiedevice.sdkdemo.bean.GlobalVars;
import com.aiedevice.sdkdemo.utils.Toaster;
import com.aiedevice.sdkdemo.utils.VolumeHelper;
import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;

import java.util.Arrays;
import java.util.List;

import butterknife.BindView;
import butterknife.OnClick;

public class MainActivity extends StpBaseActivity {
    private static final String TAG = MainActivity.class.getSimpleName();
    private DeviceListAdapter mDeviceListAdapter;
    private DeviceListAdapter.DeviceSelectListener mDevSelectListener;

    @BindView(R.id.sn)
    public TextView sn;
    @BindView(R.id.device_name)
    public EditText deviceName;
    @BindView(R.id.rv_device_list)
    public RecyclerView mRvDeviceList;
    @BindView(R.id.device_layout)
    LinearLayout deviceLayout;
    @BindView(R.id.classroom_layout)
    LinearLayout classroomLayout;
    @BindView(R.id.study_layout)
    LinearLayout studyLayout;
    @BindView(R.id.book_layout)
    LinearLayout bookLayout;
    @BindView(R.id.classroom_switcher)
    Button classroomSwitcher;

    private boolean mClassroomOpen = false;//同步课堂开关状态
    private BeanDeviceDetail mDeviceDetail;

    /**
     * 测试账号，仅供初步了解sdk使用
     * 正式接入时通常使用第三方鉴权方式，需要鉴权id和鉴权token，不需要手机号和密码
     */
    private static final String DEMO_PHONE = "13552966915";
    private static final String DEMO_PWD = "111111";
    private static final int mFrom = 0;
    private static final int mSize = 10;
    private static final String mStartDay = "2020-12-17";
    private static final String mEndDay = "2020-12-18";
    private static final String MID = "YWlyZXM6MjU3NzU4OQ";//图书id

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        initView();
    }

    @Override
    protected void onResume() {
        super.onResume();
        login();
    }

    @Override
    protected int getLayoutResId() {
        return R.layout.act_main;
    }

    private void initView() {
        setTitle("主界面");
        mDevSelectListener = new DeviceListAdapter.DeviceSelectListener() {
            @Override
            public void onDeviceSelected(BeanDeviceDetail deviceDetail) {
                if (deviceDetail != null) {
                    Log.i(TAG, "onDeviceSelected deviceDetail:" + deviceDetail);
                    mDeviceDetail = deviceDetail;
                    deviceName.setText(mDeviceDetail.getName());
                    sn.setText(mDeviceDetail.getId());
                    AccountManager.setDeviceInfo(deviceDetail.getId(), deviceDetail.getAppId());
                }
            }
        };

        mRvDeviceList.setLayoutManager(new LinearLayoutManager(mContext));
        mDeviceListAdapter = new DeviceListAdapter(mContext);
        mDeviceListAdapter.setDeviceSelectListener(mDevSelectListener);
        mRvDeviceList.setAdapter(mDeviceListAdapter);
    }

    private void login() {

        /**
         * AIE账号登录
         */
        AccountManager.login(mContext, DEMO_PHONE, DEMO_PWD, new ResultListener() {
            @Override
            public void onSuccess(BeanResult result) {
                try {
                    Gson gson = GsonUtils.getGson();
                    BeanLoginData loginData = gson.fromJson(result.getData(), BeanLoginData.class);
                    if (loginData.getDevices() != null && loginData.getDevices().size() > 0) {
                        /**
                         * 存在绑定点读笔设备，设置sdk操作的设备id和appId，此步非常重要，不设置所有接口无法正常使用
                         * 存在绑定点读笔设备，设置sdk操作的设备id和appId，此步非常重要，不设置所有接口无法正常使用
                         * 存在绑定点读笔设备，设置sdk操作的设备id和appId，此步非常重要，不设置所有接口无法正常使用
                         */
                        mDeviceDetail = loginData.getDevices().get(0);
                        Log.i(TAG, "onSuccess set mDeviceDetail");
                        AccountManager.setDeviceInfo(mDeviceDetail.getId(), mDeviceDetail.getAppId());

                        deviceName.setText(mDeviceDetail.getName());
                        sn.setText(mDeviceDetail.getId());
                        getDeviceList();
                    } else {
                        /**
                         * 没有绑定点读笔设备，跳转到绑定界面
                         */
                        PreConnectActivity.launch(mContext);
                    }
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }

            @Override
            public void onError(int code, String message) {
                Log.i(TAG, "code = " + code + "；message = " + message);
                Toaster.show(getString(R.string.login_activity_login_failed));
            }
        });

//        /**
//         * 第三方账号登录
//         */
//        AccountManager.loginEx(mContext, "客户用户唯一标识", "客户用户鉴权码", new ResultListener() {
//            @Override
//            public void onSuccess(BeanResult result) {
//                try {
//                    Gson gson = GsonUtils.getGson();
//                    BeanLoginData loginData = gson.fromJson(result.getData(), BeanLoginData.class);
//                    if (loginData.getDevices() != null && loginData.getDevices().size() > 0) {
//                        /**
//                         * 存在绑定点读笔设备，设置sdk操作的设备id和appId，此步非常重要，不设置所有接口无法正常使用
//                         * 存在绑定点读笔设备，设置sdk操作的设备id和appId，此步非常重要，不设置所有接口无法正常使用
//                         * 存在绑定点读笔设备，设置sdk操作的设备id和appId，此步非常重要，不设置所有接口无法正常使用
//                         */
//                        mDeviceDetail = loginData.getDevices().get(0);
//                        Log.i(TAG, "onSuccess set mDeviceDetail");
//                        AccountManager.setDeviceInfo(mDeviceDetail.getId(), mDeviceDetail.getAppId());
//
//                        deviceName.setText(mDeviceDetail.getName());
//                        sn.setText(mDeviceDetail.getId());
//                        getDeviceList();
//                    } else {
//                        /**
//                         * 没有绑定点读笔设备，跳转到绑定界面
//                         */
//                        PreConnectActivity.launch(mContext);
//                    }
//                } catch (Exception e) {
//                    e.printStackTrace();
//                }
//            }
//
//            @Override
//            public void onError(int code, String message) {
//                Log.i(TAG, "code = " + code + "；message = " + message);
//                Toaster.show(getString(R.string.login_activity_login_failed));
//            }
//        });

    }

    private void getDeviceList() {
        DeviceManager.getDeviceList(mContext, new ResultListener() {
            @Override
            public void onSuccess(BeanResult beanResult) {
                Gson gson = GsonUtils.getGson();
                BeanDeviceList listData = gson.fromJson(beanResult.getData(), BeanDeviceList.class);
                mDeviceListAdapter.setItems(listData.getDeviceList());
                Toaster.show("获取设备列表成功");
            }

            @Override
            public void onError(int errCode, String errMsg) {
                Log.i(TAG, "code = " + errCode + "；message = " + errMsg);
                Toaster.show("获取设备列表失败 错误:" + errMsg);
            }
        });

    }

    @OnClick({R.id.btn_device, R.id.btn_study, R.id.update_device_name, R.id.volume_add, R.id.volume_reduce, R.id.btn_book, R.id.btn_logout, R.id.btn_classroom, R.id.bind_pen, R.id.classroom_switcher, R.id.classroom_heart_beat, R.id.bind_push_id, R.id.read_book_page, R.id.read_book_range, R.id.report_range, R.id.report_page, R.id.report_trend, R.id.unbind, R.id.all_books, R.id.search_book, R.id.device_books, R.id.book_detail, R.id.download_book, R.id.delete_book, R.id.delete_books, R.id.storage, R.id.all_reading_package, R.id.device_reading_package})
    public void onViewClick(View view) {
        switch (view.getId()) {
            case R.id.bind_pen:
                PreConnectActivity.launch(mContext);
                break;
            case R.id.btn_device:
                if (deviceLayout.getVisibility() == View.VISIBLE) {
                    deviceLayout.setVisibility(View.GONE);
                } else {
                    deviceLayout.setVisibility(View.VISIBLE);
                }
                break;

            case R.id.update_device_name:
                DeviceManager.updateDeviceName(mContext, deviceName.getText().toString(), new ResultListener() {
                    @Override
                    public void onSuccess(BeanResult beanResult) {
                        Log.i(TAG, "onSuccess");
                        Toaster.show(R.string.request_success);
                    }

                    @Override
                    public void onError(int errCode, String errMsg) {
                        Log.i(TAG, "onError errCode:" + errCode + ",errMsg:" + errMsg);
                        Toaster.show(String.format(getString(R.string.request_fail), errCode, errMsg));
                    }
                });
                break;
            case R.id.volume_add:
                int volumeAfterAdd = VolumeHelper.volumeAdd(mDeviceDetail.getVolume());
                DeviceManager.changeDeviceVolume(mContext, volumeAfterAdd, new ResultListener() {
                    @Override
                    public void onSuccess(BeanResult beanResult) {
                        Log.i(TAG, "onSuccess");
                        mDeviceDetail.setVolume(volumeAfterAdd);
                        Toaster.show(R.string.request_success);
                    }

                    @Override
                    public void onError(int errCode, String errMsg) {
                        Log.i(TAG, "onError errCode:" + errCode + ",errMsg:" + errMsg);
                        Toaster.show(String.format(getString(R.string.request_fail), errCode, errMsg));
                    }
                });
                break;
            case R.id.volume_reduce:
                int volumeAfterReduce = VolumeHelper.volumeReduce(mDeviceDetail.getVolume());
                DeviceManager.changeDeviceVolume(mContext, volumeAfterReduce, new ResultListener() {
                    @Override
                    public void onSuccess(BeanResult beanResult) {
                        Log.i(TAG, "onSuccess");
                        mDeviceDetail.setVolume(volumeAfterReduce);
                        Toaster.show(R.string.request_success);
                    }

                    @Override
                    public void onError(int errCode, String errMsg) {
                        Log.i(TAG, "onError errCode:" + errCode + ",errMsg:" + errMsg);
                        Toaster.show(String.format(getString(R.string.request_fail), errCode, errMsg));
                    }
                });
                break;
            case R.id.unbind:
                AlertDialog dialog = new AlertDialog.Builder(mContext)
                        .setMessage(R.string.unbind_dialog).setNegativeButton(R.string.cancel, new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialogInterface, int i) {

                            }
                        }).setPositiveButton(R.string.sure, new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialogInterface, int i) {
                                DeviceManager.deleteDevice(mContext, new ResultListener() {
                                    @Override
                                    public void onSuccess(BeanResult beanResult) {
                                        Log.i(TAG, "onSuccess");
                                        Toaster.show(R.string.request_success);
                                        getDeviceList();
                                    }

                                    @Override
                                    public void onError(int errCode, String errMsg) {
                                        Log.i(TAG, "onError errCode:" + errCode + ",errMsg:" + errMsg);
                                        Toaster.show(String.format(getString(R.string.request_fail), errCode, errMsg));
                                    }
                                });
                            }
                        }).create();
                dialog.show();
                break;
            case R.id.btn_study:
                if (studyLayout.getVisibility() == View.VISIBLE) {
                    studyLayout.setVisibility(View.GONE);
                } else {
                    studyLayout.setVisibility(View.VISIBLE);
                }
                break;
            case R.id.read_book_page:
                StudyManager.getPicBookList(mContext, mFrom, mSize, new ResultListener() {
                    @Override
                    public void onSuccess(BeanResult beanResult) {
                        Log.i(TAG, "onSuccess data:" + beanResult.getData());
                        Gson gson = GsonUtils.getGson();
                        List<BeanReportBookList> list = gson.fromJson(beanResult.getData(), new TypeToken<List<BeanReportBookList>>() {
                        }.getType());
                        for (BeanReportBookList bookListResult : list) {
                            Log.i(TAG, "date:" + bookListResult.getName());
                        }
                        Toaster.show(R.string.request_success);

                    }

                    @Override
                    public void onError(int errCode, String errMsg) {
                        Log.i(TAG, "onError errCode:" + errCode + ",errMsg:" + errMsg);
                        Toaster.show(String.format(getString(R.string.request_fail), errCode, errMsg));

                    }
                });
                break;
            case R.id.read_book_range:
                StudyManager.getPicBookList(mContext, mStartDay, mEndDay, new ResultListener() {
                    @Override
                    public void onSuccess(BeanResult beanResult) {
                        Log.i(TAG, "onSuccess data:" + beanResult.getData());
                        Gson gson = GsonUtils.getGson();
                        List<BeanReportBookList> list = gson.fromJson(beanResult.getData(), new TypeToken<List<BeanReportBookList>>() {
                        }.getType());
                        for (BeanReportBookList bookListResult : list) {
                            Log.i(TAG, "date:" + bookListResult.getName());
                        }
                        Toaster.show(R.string.request_success);

                    }

                    @Override
                    public void onError(int errCode, String errMsg) {
                        Log.i(TAG, "onError errCode:" + errCode + ",errMsg:" + errMsg);
                        Toaster.show(String.format(getString(R.string.request_fail), errCode, errMsg));

                    }
                });
                break;
            case R.id.report_page:
                StudyManager.getReportList(mContext, StudyConstants.TYPE_FOLLOW_READING, 1, mFrom, mSize, new ResultListener() {
                    @Override
                    public void onSuccess(BeanResult beanResult) {
                        Log.i(TAG, "onSuccess data:" + beanResult.getData());
                        Gson gson = GsonUtils.getGson();
                        List<BeanReportList> list = gson.fromJson(beanResult.getData(), new TypeToken<List<BeanReportList>>() {
                        }.getType());
                        for (BeanReportList bookListResult : list) {
                            Log.i(TAG, "date:" + bookListResult.getName());
                        }
                        Toaster.show(R.string.request_success);

                    }

                    @Override
                    public void onError(int errCode, String errMsg) {
                        Log.i(TAG, "onError errCode:" + errCode + ",errMsg:" + errMsg);
                        Toaster.show(String.format(getString(R.string.request_fail), errCode, errMsg));

                    }
                });
                break;
            case R.id.report_range:
                StudyManager.getReportList(mContext, StudyConstants.TYPE_FOLLOW_READING, 1, mStartDay, mEndDay, new ResultListener() {
                    @Override
                    public void onSuccess(BeanResult beanResult) {
                        Log.i(TAG, "onSuccess data:" + beanResult.getData());
                        Gson gson = GsonUtils.getGson();
                        List<BeanReportList> list = gson.fromJson(beanResult.getData(), new TypeToken<List<BeanReportList>>() {
                        }.getType());
                        for (BeanReportList bookListResult : list) {
                            Log.i(TAG, "date:" + bookListResult.getName());
                        }
                        Toaster.show(R.string.request_success);
                    }

                    @Override
                    public void onError(int errCode, String errMsg) {
                        Log.i(TAG, "onError errCode:" + errCode + ",errMsg:" + errMsg);
                        Toaster.show(String.format(getString(R.string.request_fail), errCode, errMsg));
                    }
                });
                break;
            case R.id.report_trend:
                StudyManager.getReportTrend(mContext, StudyConstants.TYPE_DURATION, mStartDay, mEndDay, new ResultListener() {
                    @Override
                    public void onSuccess(BeanResult beanResult) {
                        Gson gson = GsonUtils.getGson();
                        BeanReportTrend trend = gson.fromJson(beanResult.getData(), BeanReportTrend.class);
                        Log.i(TAG, "totalValue:" + trend.getTotal());
                        for (BeanReportTrendDay day : trend.getList()) {
                            Log.i(TAG, "day:" + day.getDay() + ",value:" + day.getValue());
                        }
                        Toaster.show(R.string.request_success);
                    }

                    @Override
                    public void onError(int errCode, String errMsg) {
                        Log.i(TAG, "onError errCode:" + errCode + ",errMsg:" + errMsg);
                        Toaster.show(String.format(getString(R.string.request_fail), errCode, errMsg));
                    }
                });
                break;

            case R.id.btn_book:
                if (bookLayout.getVisibility() == View.VISIBLE) {
                    bookLayout.setVisibility(View.GONE);
                } else {
                    bookLayout.setVisibility(View.VISIBLE);
                }
                break;
            case R.id.all_books:
                BookManager.getAllBookList(mContext, mFrom, mSize, new ResultListener() {
                    @Override
                    public void onSuccess(BeanResult beanResult) {
                        Log.i(TAG, "onSuccess data:" + beanResult.getData());
                        Gson gson = GsonUtils.getGson();
                        BeanBookListResult result = gson.fromJson(beanResult.getData(), BeanBookListResult.class);
                        if (result != null) {
                            Log.i(TAG, "total:" + result.getTotal());
                            for (BeanBookDetail detail : result.getList()) {
                                Log.i(TAG, detail.toString());
                            }
                        }
                        Toaster.show(R.string.request_success);
                    }

                    @Override
                    public void onError(int errCode, String errMsg) {
                        Log.i(TAG, "onError errCode:" + errCode + ",errMsg:" + errMsg);
                        Toaster.show(String.format(getString(R.string.request_fail), errCode, errMsg));
                    }
                });
                break;
            case R.id.search_book:
                BookManager.searchBook(mContext, "英语", new ResultListener() {
                    @Override
                    public void onSuccess(BeanResult beanResult) {
                        Log.i(TAG, "onSuccess data:" + beanResult.getData());
                        Toaster.show(R.string.request_success);
                        Gson gson = GsonUtils.getGson();
                        BeanBookListResult result = gson.fromJson(beanResult.getData(), BeanBookListResult.class);
                        if (result != null) {
                            Log.i(TAG, "total:" + result.getTotal());
                            for (BeanBookDetail detail : result.getList()) {
                                Log.i(TAG, detail.toString());
                            }
                        }
                    }

                    @Override
                    public void onError(int errCode, String errMsg) {
                        Log.i(TAG, "onError errCode:" + errCode + ",errMsg:" + errMsg);
                        Toaster.show(String.format(getString(R.string.request_fail), errCode, errMsg));
                    }
                });
                break;
            case R.id.device_books:
                BookManager.getDeviceBookList(mContext, mFrom, mSize, new ResultListener() {
                    @Override
                    public void onSuccess(BeanResult beanResult) {
                        Log.i(TAG, "onSuccess data:" + beanResult.getData());
                        Toaster.show(R.string.request_success);
                    }

                    @Override
                    public void onError(int errCode, String errMsg) {
                        Log.i(TAG, "onError errCode:" + errCode + ",errMsg:" + errMsg);
                        Toaster.show(String.format(getString(R.string.request_fail), errCode, errMsg));
                    }
                });
                break;
            case R.id.book_detail:
                BookManager.getBookDetail(mContext, MID, new ResultListener() {
                    @Override
                    public void onSuccess(BeanResult beanResult) {
                        Log.i(TAG, "onSuccess data:" + beanResult.getData());
                        Toaster.show(R.string.request_success);
                        Gson gson = GsonUtils.getGson();
                        BeanDeviceDetail deviceDetail = gson.fromJson(beanResult.getData(), BeanDeviceDetail.class);
                        Log.i(TAG, "deviceDetail.name:" + deviceDetail.getName());
                    }

                    @Override
                    public void onError(int errCode, String errMsg) {
                        Log.i(TAG, "onError errCode:" + errCode + ",errMsg:" + errMsg);
                        Toaster.show(String.format(getString(R.string.request_fail), errCode, errMsg));
                    }
                });
                break;
            case R.id.download_book:
                BookManager.downloadBook(mContext, MID, new ResultListener() {
                    @Override
                    public void onSuccess(BeanResult beanResult) {
                        Log.i(TAG, "onSuccess data:" + beanResult.getData());
                        Toaster.show(R.string.request_success);
                    }

                    @Override
                    public void onError(int errCode, String errMsg) {
                        Log.i(TAG, "onError errCode:" + errCode + ",errMsg:" + errMsg);
                        Toaster.show(String.format(getString(R.string.request_fail), errCode, errMsg));
                    }
                });
                break;
            case R.id.delete_book:
                BookManager.deleteBook(mContext, MID, new ResultListener() {
                    @Override
                    public void onSuccess(BeanResult beanResult) {
                        Log.i(TAG, "onSuccess data:" + beanResult.getData());
                        Toaster.show(R.string.request_success);
                    }

                    @Override
                    public void onError(int errCode, String errMsg) {
                        Log.i(TAG, "onError errCode:" + errCode + ",errMsg:" + errMsg);
                        Toaster.show(String.format(getString(R.string.request_fail), errCode, errMsg));
                    }
                });
                break;
            case R.id.delete_books:
                BookManager.deleteBookList(mContext, Arrays.asList(MID), new ResultListener() {
                    @Override
                    public void onSuccess(BeanResult beanResult) {
                        Log.i(TAG, "onSuccess data:" + beanResult.getData());
                        Toaster.show(R.string.request_success);
                    }

                    @Override
                    public void onError(int errCode, String errMsg) {
                        Log.i(TAG, "onError errCode:" + errCode + ",errMsg:" + errMsg);
                        Toaster.show(String.format(getString(R.string.request_fail), errCode, errMsg));
                    }
                });
                break;
            case R.id.storage:
                BookManager.getDeviceStorage(mContext, new ResultListener() {
                    @Override
                    public void onSuccess(BeanResult beanResult) {
                        Log.i(TAG, "onSuccess data:" + beanResult.getData());
                        Gson gson = GsonUtils.getGson();
                        BeanStorageStatus storageStatus = gson.fromJson(beanResult.getData(), BeanStorageStatus.class);
                        Log.i(TAG, "storage status:" + storageStatus.toString());
                        Toaster.show(storageStatus.toString());
                    }

                    @Override
                    public void onError(int errCode, String errMsg) {
                        Log.i(TAG, "onError errCode:" + errCode + ",errMsg:" + errMsg);
                        Toaster.show(String.format(getString(R.string.request_fail), errCode, errMsg));
                    }
                });
                break;
            case R.id.all_reading_package:
                BookManager.getAllReadingPackage(mContext, mFrom, mSize, new ResultListener() {
                    @Override
                    public void onSuccess(BeanResult beanResult) {
                        Log.i(TAG, "onSuccess data:" + beanResult.getData());
                        Toaster.show(R.string.request_success);
                        Gson gson = GsonUtils.getGson();
                        BeanBookListResult result = gson.fromJson(beanResult.getData(), BeanBookListResult.class);
                        if (result != null) {
                            Log.i(TAG, "total:" + result.getTotal());
                            for (BeanBookDetail detail : result.getList()) {
                                Log.i(TAG, detail.toString());
                            }
                        }
                    }

                    @Override
                    public void onError(int errCode, String errMsg) {
                        Log.i(TAG, "onError errCode:" + errCode + ",errMsg:" + errMsg);
                        Toaster.show(String.format(getString(R.string.request_fail), errCode, errMsg));
                    }
                });
                break;
            case R.id.device_reading_package:
                BookManager.getDeviceReadingPackage(mContext, mFrom, mSize, new ResultListener() {
                    @Override
                    public void onSuccess(BeanResult beanResult) {
                        Log.i(TAG, "onSuccess data:" + beanResult.getData());
                        Toaster.show(R.string.request_success);
                        Gson gson = GsonUtils.getGson();
                        BeanBookListResult result = gson.fromJson(beanResult.getData(), BeanBookListResult.class);
                        if (result != null) {
                            Log.i(TAG, "total:" + result.getTotal());
                            for (BeanBookDetail detail : result.getList()) {
                                Log.i(TAG, detail.toString());
                            }
                        }
                    }

                    @Override
                    public void onError(int errCode, String errMsg) {
                        Log.i(TAG, "onError errCode:" + errCode + ",errMsg:" + errMsg);
                        Toaster.show(String.format(getString(R.string.request_fail), errCode, errMsg));
                    }
                });
                break;
            case R.id.btn_classroom:
                if (classroomLayout.getVisibility() == View.VISIBLE) {
                    classroomLayout.setVisibility(View.GONE);
                } else {
                    classroomLayout.setVisibility(View.VISIBLE);
                }
                break;
            case R.id.bind_push_id:
                String pushId = GlobalVars.getPushId();
                Log.i(TAG, "pushId:" + pushId);
                if (TextUtils.isEmpty(pushId)) {
                    Log.e(TAG, "请先注册个推ID");
                    return;
                }
                ClassRoomManager.setPushId(mContext, pushId, new ResultListener() {
                    @Override
                    public void onSuccess(BeanResult beanResult) {
                        Log.i(TAG, "onSuccess");
                        Toaster.show(R.string.request_success);
                    }

                    @Override
                    public void onError(int errCode, String errMsg) {
                        Log.i(TAG, "onError errCode:" + errCode + ",errMsg:" + errMsg);
                        Toaster.show(String.format(getString(R.string.request_fail), errCode, errMsg));
                    }
                });
                break;
            case R.id.classroom_switcher:
                mClassroomOpen = !mClassroomOpen;
                Log.i(TAG, "set classroom status:" + mClassroomOpen);
                if (mClassroomOpen) {
                    classroomSwitcher.setText(R.string.classroom_open);
                } else {
                    classroomSwitcher.setText(R.string.classroom_close);
                }
                ClassRoomManager.syncSwitch(mContext, mClassroomOpen, new ResultListener() {
                    @Override
                    public void onSuccess(BeanResult beanResult) {
                        Log.i(TAG, "onSuccess");
                        Toaster.show(R.string.request_success);
                    }

                    @Override
                    public void onError(int errCode, String errMsg) {
                        Log.i(TAG, "onError errCode:" + errCode + ",errMsg:" + errMsg);
                        Toaster.show(String.format(getString(R.string.request_fail), errCode, errMsg));
                    }
                });
                break;
            case R.id.classroom_heart_beat:
                /**
                 * 需要每隔30秒，调用一次，超过1分钟不发心跳，服务器将不再推送长连接
                 * 需要每隔30秒，调用一次，超过1分钟不发心跳，服务器将不再推送长连接
                 * 需要每隔30秒，调用一次，超过1分钟不发心跳，服务器将不再推送长连接
                 */
                ClassRoomManager.sendSyncHeartbeat(mContext, new ResultListener() {
                    @Override
                    public void onSuccess(BeanResult beanResult) {
                        Log.i(TAG, "onSuccess");
                        Toaster.show(R.string.request_success);
                    }

                    @Override
                    public void onError(int errCode, String errMsg) {
                        Log.i(TAG, "onError errCode:" + errCode + ",errMsg:" + errMsg);
                        Toaster.show(String.format(getString(R.string.request_fail), errCode, errMsg));
                    }
                });
                break;
            case R.id.btn_logout:
                AlertDialog logoutDialog = new AlertDialog.Builder(mContext)
                        .setMessage(R.string.logout_dialog).setNegativeButton(R.string.cancel, new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialogInterface, int i) {

                            }
                        }).setPositiveButton(R.string.sure, new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialogInterface, int i) {
                                logout();
                            }
                        }).create();
                logoutDialog.show();
                break;
        }

    }


    private void logout() {
        AccountManager.logout(mContext, new ResultListener() {
            @Override
            public void onSuccess(BeanResult result) {
                Log.i(TAG, "[logout-succ] result=" + result.getResult());
                GlobalVars.setLoginStatus(false);
                Toaster.show("注销成功");
                finish();
            }

            @Override
            public void onError(int errCode, String errMsg) {
                Log.i(TAG, "[logout-err] code=" + errCode + " message=" + errMsg);
            }
        });
    }

    public static void launch(Context context) {
        Intent intent = new Intent(context, MainActivity.class);
        context.startActivity(intent);
    }
}
