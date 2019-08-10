package com.sinomaps.geobookar.utility;

import android.app.Activity;
import android.app.AlertDialog;
import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.content.ServiceConnection;
import android.os.Handler;
import android.os.IBinder;
import android.os.Message;
import android.os.Messenger;
import android.os.RemoteException;
import android.util.Log;

import com.sinomaps.geobookar.ar.ObjectScanActivity;
import com.sinomaps.geobookar.model.ArResouceParamBean;
import com.sinomaps.geobookar.model.ArResouceResponseBean;
import com.sinomaps.geobookar.model.MethodName_Ar;
import com.sinomaps.geobookar.model.ResourceParamKeys;
import com.sinomaps.geobookar.model.ResourceStatus;

import java.util.HashMap;
import java.util.concurrent.Callable;

public class ResourceMgrTool {
    //region 与资源模块通信
    private static final int MSG_GET_RESOUCE_PATH = 0x1100;//获取资源路径
    private static final int MSG_GET_RESOUCE_STATUS = 0x1101;//获取资源状态
    private static final int MSG_PLAY_RESOUCE = 0x1102;//播放资源
    private static final int MSG_DOWNLOAD_RESOUCE = 0x1103;//下载资源
    private static final int MSG_OPEN_RESOUCE_LIST_WINDOW = 0x1104;//打开资源列表窗口,并选中资源
    private static final int MSG_OPEN_RESOUCE_DOWNLOAD_WINDOW = 0x1105;//打开资源下载窗口
    private static final String TAG = "ResourceMgrTool";
    private static ResCallbackListener resCallbackListener = null;
    static Messenger mService;
    static boolean isConn;

    public static void bindServiceInvoked(Activity activity) {
        Intent intent = new Intent();
        intent.setPackage(activity.getPackageName());
        intent.setAction("com.mainbo.ztec.resouce");
        activity.bindService(intent, mConn, Context.BIND_AUTO_CREATE);
        Log.e(TAG, "bindService invoked !");
    }


    public static void destroyCommunicateResource(Activity activity) {
        try {
            activity.unbindService(mConn);
        } catch (Exception e) {
            Log.e(TAG, e.getMessage());
        }
    }

    static Messenger mMessenger = new Messenger(new Handler() {
        @Override
        public void handleMessage(Message msgFromServer) {
            ArResouceResponseBean responseBean = (ArResouceResponseBean) msgFromServer.obj;
            switch (msgFromServer.what) {
                case MSG_GET_RESOUCE_PATH:
                    break;
                case MSG_GET_RESOUCE_STATUS:
                    Log.i(TAG, "收到返回消息" + responseBean.getResourceStatus().toString());
                    if (resCallbackListener != null) {
                        try {
                            resCallbackListener.resCallback(responseBean);
                        } finally {
                            resCallbackListener = null;
                        }
                    }
                    break;
            }
            super.handleMessage(msgFromServer);
        }
    });

    static ServiceConnection mConn = new ServiceConnection() {
        @Override
        public void onServiceConnected(ComponentName name, IBinder service) {
            mService = new Messenger(service);
            isConn = true;

        }

        @Override
        public void onServiceDisconnected(ComponentName name) {
            mService = null;
            isConn = false;

        }
    };

    private static void callService(String chapterId, String resourceId, int operateMode, HashMap map, ResCallbackListener callback) {
        Message msgFromClient = Message.obtain(null, operateMode);
        ArResouceParamBean bean = new ArResouceParamBean();
//        bean.setMethodName(methodName);
        bean.setParam(map);
        msgFromClient.obj = bean;
        msgFromClient.replyTo = mMessenger;
        msgFromClient.arg1 = 888;
        msgFromClient.arg1 = 999;
        if (isConn) {
            //往服务端发送消息
            try {
                resCallbackListener = callback;
                mService.send(msgFromClient);
                Log.d(TAG, "消息已发送");
            } catch (RemoteException e) {
                resCallbackListener = null;
                e.printStackTrace();
            }
        }
    }

//    public static void getResourcePath(String chapterId, String resourceId, ResCallbackListener resCallbackListener) {
//        HashMap<String, String> map = new HashMap<>();
//        map.put(ResourceParamKeys.CHADAPTERID, chapterId);
//        map.put(ResourceParamKeys.RESOUCEID, resourceId);
//        callService(chapterId, resourceId, MSG_GET_RESOUCE_PATH, MethodName_Ar.MSG_GET_RESOUCE_PATH, map, resCallbackListener);
//    }

    public static void downLoadResource(String chapterId, String resourceId, ResCallbackListener resCallbackListener) {
        HashMap<String, String> map = new HashMap<>();
        map.put(ResourceParamKeys.CHADAPTERID, chapterId);
        map.put(ResourceParamKeys.RESOUCEID, resourceId);
        callService(chapterId, resourceId, MSG_DOWNLOAD_RESOUCE, map, resCallbackListener);
    }

    public static void getResourceStatus(String chapterId, String resourceId, ResCallbackListener resCallbackListener) {
        HashMap<String, String> map = new HashMap<>();
        map.put(ResourceParamKeys.CHADAPTERID, chapterId);
        map.put(ResourceParamKeys.RESOUCEID, resourceId);
        callService(chapterId, resourceId, MSG_GET_RESOUCE_STATUS, map, resCallbackListener);
    }

    public static void playResource(final String chapterId, final String resourceId, final ResCallbackListener resCallbackListener) {
        final HashMap<String, String> map = new HashMap<>();
        map.put(ResourceParamKeys.CHADAPTERID, chapterId);
        map.put(ResourceParamKeys.RESOUCEID, resourceId);
        new Thread(new Runnable() {
            @Override
            public void run() {
                callService(chapterId, resourceId, MSG_PLAY_RESOUCE, map, resCallbackListener);
            }
        }).start();

    }

    /**
     * 跳转到资源列表界面
     * @param chapterId
     * @param resourceId
     * @param resCallbackListener
     */
    public static void gotoResourceListWindow(String chapterId, String resourceId, ResCallbackListener resCallbackListener) {
        HashMap<String, String> map = new HashMap<>();
        map.put(ResourceParamKeys.CHADAPTERID, chapterId);
        map.put(ResourceParamKeys.RESOUCEID, resourceId);
        callService(chapterId, resourceId, MSG_OPEN_RESOUCE_LIST_WINDOW, map, resCallbackListener);
    }


    /**
     * 跳转到资源下载窗口
     * @param chapterId
     * @param resourceId
     * @param resCallbackListener
     */
    public static void gotoResourceDownloadWindow(String chapterId, String resourceId, ResCallbackListener resCallbackListener) {
        HashMap<String, String> map = new HashMap<>();
        map.put(ResourceParamKeys.CHADAPTERID, chapterId);
        map.put(ResourceParamKeys.RESOUCEID, resourceId);
        callService(chapterId, resourceId, MSG_OPEN_RESOUCE_DOWNLOAD_WINDOW, map, resCallbackListener);
    }

    //endregion
    public interface ResCallbackListener {
        void resCallback(ArResouceResponseBean responseBean);
    }
}
