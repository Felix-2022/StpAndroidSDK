# StpAndroidSDK

## 概述
本 SDK 主要提供了百进智能点读笔常用的五大模块接口

+ 账号管理
  1. 登录
  2. 登出
  3. 设置操作设备
  4. 当前操作设备ID
  
+ 设备管理
  1. 获取设备列表
  2. 获取设备详情
  3. 获取设备硬件信息
  4. 修改设备音量
  5. 修改设备名称
  6. 解除设备绑定
  
+ 学习报告
  1. 已读绘本列表(按日期查询、倒叙分页查询)
  2. 各项学习数据(点读、跟读、日报）(按日期查询、倒叙分页查询)
  3. 各项报告趋势(点读、跟读、学习时长、阅读绘本数)
  
+ 绘本管理
  1. 全部绘本
  2. 搜索绘本
  3. 设备已添加绘本
  4. 绘本详情
  5. 下载绘本或点读包
  6. 删除绘本(单本、批量)
  7. 获取设备存储空间状态
  8. 全部点读包
  9. 已下载点读包
  
+ 同步课堂
  1. 设置个推ID
  2. 同步课堂开关
  3. 同步课堂心跳
 
## 快速集成

### 1. 由百进分配唯一的PackageId
### 2. 接入stpsdk-版本号.aar
### 3. 初始化SDK
  ```
  /**
   * StpSDK初始化
   *
   * @param context
   * @param packageId 平台分配的packgeId
   * @param env       (测试：SDKConfig.ENV_TEST ,线上：ENV_SDKConfig.ENV_ONLINE)
   */
   StpSDK.getInstance().init(this, PACKAGE_ID, SDKConfig.ENV_ONLINE);
  ```
### 4. 登录:百进账号或第三方鉴权登录二选一
> 注意：使用第三方鉴权登录前，需要双方服务器端研发先联调完服务器间鉴权接口
```
 
百进账号登录
AccountManager.login(context, 手机号, 密码, 回调)
  
第三方登录接口
AccountManager.login(context, 客户用户唯一标识, 客户用户鉴权码, 回调) {
```

### 5. 设置要操作设备信息
```
AccountManager.setDeviceInfo(设备ID,设备APPId);
```

## 点读笔配网集成
```
### 1.接入SdkDemo中的blufilibrary.aar
### 2.参考com.aiedevice.sdkdemo.activity.blufi目录下有完整配网代码，请参考

```

## 同步课堂集成
### 1. 客户自行在个推开放平台上注册应用
### 2. 将注册好的个推AppID、AppKey、AppSecret提供给百进
### 3. 客户APP接入个推sdk
### 4. 个推sdk注册成功后得到个推clientId
### 5. 请求百进接口绑定个推clientId
```
注册方式有2种，
1. 未登录时已经获得个推clientId，在第三方登录接口时注册：AccountManager.loginEx
/**
 * 第三方登录接口
 *
 * @param userId    客户用户唯一标识
 * @param thirdCode 客户用户鉴权码
 * @param pushId    个推(第三方长连接服务商)生成的ClientId,(*非必须，接入同步课堂相关功能需要*)
 * @param listener
 */
public static void loginEx(Context context, String userId, String thirdCode, String pushId, ResultListener listener) {

2.独立设置个推clientId：ClassRoomManager.setPushId
/**
 * 设置个推推送ID
 *
 * @param pushId   个推(第三方长连接服务商)生成的ClientId
 * @param listener
 */
public static void setPushId(Context context, String pushId, ResultListener listener) 
```
### 6. 开启同步课堂开关：ClassRoomManager.syncSwitch
```
/**
 * 同步课堂开关
 *
 * @param context
 * @param isOpen   true:开 false:关
 * @param listener
 */
public static void syncSwitch(Context context, boolean isOpen, ResultListener listener) 
```
### 7. 每隔30秒循环请求心跳接口: ClassRoomManager.sendSyncHeartbeat
```
/**
* 同步课堂心跳，业务端30秒轮训一次，弱超过30S*2未发送心跳，则服务器关闭同步课堂不再发送长连接
*
* @param context
* @param listener
* @return
*/
public static boolean sendSyncHeartbeat(Context context, ResultListener listener)
```
### 8. 点读笔进入点读模式，点击有视频讲解的绘本

### 9.个推收到带有同步课堂视频连接的个推消息

 
 
## 各模块接口说明

### 账号管理AccountManager
```
/**
* 百进账号登录
*
* @param context
* @param phone    手机
* @param password   密码
* @param listener
*/
public static void login(Context context, String phone, String password, ResultListener listener) 


/**
* 第三方登录接口
*
* @param userId    客户用户唯一标识
* @param thirdCode 客户用户鉴权码
* @param listener
*/
public static void loginEx(Context context, String userId, String thirdCode, ResultListener listener)

/**
* 第三方登录接口
*
* @param userId    客户用户唯一标识
* @param thirdCode 客户用户鉴权码
* @param pushId    个推(第三方长连接服务商)生成的ClientId,(*非必须，接入同步课堂相关功能需要*)
* @param listener
*/
public static void loginEx(Context context, String userId, String thirdCode, String pushId, ResultListener listener)


/**
* 登出接口
*
* @param listener
*/
public static void logout(Context context, ResultListener listener)


/**
* 设置接口操作的设备信息
*
* @param deviceID 设备ID
* @param appID    设备所属的appID
*/
public static void setDeviceInfo(String deviceID, String appID)

/**
* 获取当前操作的设备ID
* @return
*/
public static String getDeviceId() {
return SharedPreferencesUtil.getDeviceId();
}
```
  ### 设备管理DeviceManager
```
/**
* 获取设备绑定信息
*
* @param listener
*/
public static void getDeviceBindInfo(Context context, ResultListener listener) 

/**
* 获取设备列表
*
* @param listener
*/
public static void getDeviceList(Context context, ResultListener listener) 

/**
* 获取设备详情
* @param context
* @param listener
*/
public static void getDeviceDetail(Context context, ResultListener listener) 


/**
* 获取设备硬件信息
* @param context
* @param listener
*/
public static void getDeviceHardwareInfo(Context context, ResultListener listener) 

/**
* 修改设备音量
* @param context
* @param volume 音量值
* @param listener
*/
public static void changeDeviceVolume(Context context, int volume, ResultListener listener) 


/**
* 修改设备名称
* @param context
* @param deviceName 设备名称
* @param listener
*/
public static void updateDeviceName(Context context, String deviceName, ResultListener listener)

/**
* 解除设备绑定
* @param context
* @param listener
*/
public static void deleteDevice(Context context, ResultListener listener) 
    
    
```
### 学习报告StudyManager
```
/**
* 已读绘本列表(按数量查询)
*
* @param from     从第几条数据开始，0起始
* @param size     返回多少条数据
* @param listener
*/
public static void getPicBookList(Context context, int from, int size, ResultListener listener)


/**
* 已读绘本列表(按时间查询)
*
* @param startDate 开始时间，格式"yyyy-MM-dd"
* @param endDate   截止时间，格式"yyyy-MM-dd"
* @param listener
*/
public static void getPicBookList(Context context, String startDate, String endDate, ResultListener listener)

/**
* 学习报告(按数量查询)
*
* @param type       StudyConstants.TYPE_POINT_READING:点读次数,TYPE_FOLLOW_READING:跟读次数
* @param withDetail 1：返回详情 0:没有详情
* @param from       从第几条数据开始
* @param size       返回多少条数据
* @param listener
*/
public static void getReportList(Context context, String type, int withDetail, int from, int size, ResultListener listener)


/**
* 学习报告(按时间查询)
*
* @param type       StudyConstants.TYPE_POINT_READING:点读次数,TYPE_FOLLOW_READING:跟读次数,
* @param withDetail 1：返回详情 0:没有详情
* @param startDate  开始时间，格式"yyyy-MM-dd"
* @param endDate    截止时间，格式"yyyy-MM-dd"
* @param listener
*/

public static void getReportList(Context context, String type, int withDetail, String startDate, String endDate, ResultListener listener)

/**
* 报告趋势(按时间查询)
*
* @param type      StudyConstants.TYPE_POINT_READING:点读次数,TYPE_FOLLOW_READING:跟读次数,
*                  StudyConstants.TYPE_DURATION:学习时长,TYPE_PIC_BOOK:阅读绘本
* @param startDate 开始时间，格式"yyyy-MM-dd"
* @param endDate   截止时间，格式"yyyy-MM-dd"
* @param listener
*/
public static void getReportTrend(Context context, String type, String startDate, String endDate, ResultListener listener)

    
```
### 绘本管理BookManager
```
/**
* 获取全部绘本列表
*
* @param context
* @param from     从第几条数据开始，0起始
* @param size     返回多少条数据
* @param listener
*/
public static void getAllBookList(Context context, int from, int size, ResultListener listener) 

/**
* 按关键字搜索绘本
*
* @param context
* @param keyword
* @param listener
*/
public static void searchBook(Context context, String keyword, ResultListener listener)

/**
* 获取设备已添加绘本列表
*
* @param context
* @param from     从第几条数据开始，0起始
* @param size     返回多少条数据
* @param listener
*/
public static void getDeviceBookList(Context context, int from, int size, ResultListener listener) 

/**
* 获取绘本详情
*
* @param context
* @param mid      书ID
* @param listener
*/
public static void getBookDetail(Context context, String mid, ResultListener listener) 

/**
* 添加绘本下载
*
* @param context
* @param mid      书ID
* @param listener
*/
public static void downloadBook(Context context, String mid, ResultListener listener)

/**
* 删除设备已下载绘本
*
* @param context
* @param mid      书ID
* @param listener
*/
public static void deleteBook(Context context, String mid, ResultListener listener) 

/**
* 批量删除设备已下载绘本
*
* @param context
* @param bookIdList 书ID集合
* @param listener
*/
public static void deleteBookList(Context context, List<String> bookIdList, ResultListener listener) 


/**
* 获取设备sdcard状态（总容量，剩余容量，数据单位是M）
*
* @param context
* @param listener
*/
public static void getDeviceStorage(Context context, ResultListener listener)

/**
* 获取全部点读包列表
*
* @param context
* @param from     从第几条数据开始，0起始
* @param size     返回多少条数据
* @param listener
*/
public static void getAllReadingPackage(Context context, int from, int size, ResultListener listener)

/**
* 获取已下载的点读包列表
*
* @param context
* @param from     从第几条数据开始，0起始
* @param size     返回多少条数据
* @param listener
*/
public static void getDeviceReadingPackage(Context context, int from, int size, ResultListener listener)


```
### 同步课堂ClassRoomManager
```
/**
* 设置个推推送ID
*
* @param pushId   个推(第三方长连接服务商)生成的ClientId
* @param listener
*/
public static void setPushId(Context context, String pushId, ResultListener listener) 

/**
* 同步课堂开关
*
* @param context
* @param isOpen   true:开 false:关
* @param listener
*/
public static void syncSwitch(Context context, boolean isOpen, ResultListener listener)    

/**
* 同步课堂心跳，业务端30秒轮训一次，弱超过30S*2未发送心跳，则服务器关闭同步课堂不再发送长连接
*
* @param context
* @param listener
* @return
*/
public static boolean sendSyncHeartbeat(Context context, ResultListener listener)


```

