package com.sinomaps.geobookar.utility;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.os.Build.VERSION;
import android.os.Bundle;
import android.os.Environment;
import android.preference.PreferenceManager;
import android.util.TypedValue;
import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout.LayoutParams;
import android.widget.Toast;

import com.sinomaps.geobookar.R;
import com.sinomaps.geobookar.model.ImageInfo;
import com.sinomaps.geobookar.model.ModelInfo;
import com.sinomaps.geobookar.model.ObjectInfo;
import com.sinomaps.geobookar.ui.MultiObjectActivity;
import com.sinomaps.geobookar.ui.ResModelActivity;

import net.lingala.zip4j.util.InternalZipConstants;

import org.xmlpull.v1.XmlPullParser;
import org.xmlpull.v1.XmlPullParserFactory;

import java.io.File;
import java.io.FileInputStream;
import java.util.ArrayList;
import java.util.List;

//import com.sinomaps.geobookar.photo.ShowPhotoListActivity;
//import net.sqlcipher.database.SQLiteDatabase;

public class MyUtility {
    public static String getDataBathPath(Context context) {
//        return context.getExternalFilesDir(null) + context.getResources().getString(R.string.path_data_base);
        return  Environment.getExternalStorageDirectory() + context.getResources().getString(R.string.path_data_base);
    }

    public static String getCurBookID(Context context) {
        return PreferenceManager.getDefaultSharedPreferences(context).getString("CurBookID", "");
    }

    public static String getProjectBathPath(Context context) {
        return getDataBathPath(context) + getCurBookID(context) + InternalZipConstants.ZIP_FILE_SEPARATOR;
    }

    public static String getProjectConfigFilePath(Context context) {
        return getProjectBathPath(context) + "Basic/category.xml";
    }

    public static boolean checkResourceIsExist(Activity activity, String uri) {
        if (new File(uri).exists()) {
            return true;
        }
        Toast.makeText(activity.getApplicationContext(), "资源不存在，请下载离线数据包！", Toast.LENGTH_SHORT).show();
//        activity.startActivity(new Intent(activity, OfflineDataActivity.class));
        return false;
    }

    public static void enableTranscent(Activity activity, int colorResourceId) {
        if (VERSION.SDK_INT >= 19) {
            activity.getWindow().addFlags(67108864);
            View statusView = new View(activity);
            statusView.setLayoutParams(new LayoutParams(-1, getStatusBarHeight(activity)));
            statusView.setBackgroundColor(activity.getResources().getColor(colorResourceId));
            ((ViewGroup) activity.getWindow().getDecorView()).addView(statusView);
//            ViewGroup rootView = (ViewGroup) ((ViewGroup) activity.findViewById(16908290)).getChildAt(0);
//            rootView.setFitsSystemWindows(true);
//            rootView.setClipToPadding(true);
        }
    }

    public static int getStatusBarHeight(Activity context) {
        int statusBarHeight = 0;
        try {
            Class<?> c = Class.forName("com.android.internal.R$dimen");
            return context.getResources().getDimensionPixelSize(Integer.parseInt(c.getField("status_bar_height").get(c.newInstance()).toString()));
        } catch (Exception e1) {
            e1.printStackTrace();
            return statusBarHeight;
        }
    }

    public static int getActionBarHeight(Activity context) {
        TypedValue tv = new TypedValue();
        if (context.getTheme().resolveAttribute(16843499, tv, true)) {
            return TypedValue.complexToDimensionPixelSize(tv.data, context.getResources().getDisplayMetrics());
        }
        return 0;
    }

    public static List<ObjectInfo> getKnowledgeObjects(Context context, String sectionId) {
        List<ObjectInfo> list = new ArrayList<>();
        boolean sectionFlag = false;
        try {
            String categoryConfigFilePath = getProjectConfigFilePath(context);
            if (!new File(categoryConfigFilePath).exists()) {
                return null;
            }
            XmlPullParser xmlParser = XmlPullParserFactory.newInstance().newPullParser();
            FileInputStream fileInputStream = new FileInputStream(categoryConfigFilePath);
            xmlParser.setInput(fileInputStream, "UTF-8");
            int eventType = xmlParser.getEventType();
            while (eventType != 1) {
                if (eventType == 2) {
                    String strName = xmlParser.getName();
                    if (strName.equals("section") && xmlParser.getAttributeValue(0).equals(sectionId)) {
                        sectionFlag = true;
                    } else if (strName.equals("section") && !xmlParser.getAttributeValue(0).equals(sectionId)) {
                        sectionFlag = false;
                    }
                    if (sectionFlag && strName.equals("object")) {
                        ObjectInfo object = new ObjectInfo();
                        object.ID = xmlParser.getAttributeValue(0);
                        object.Name = xmlParser.getAttributeValue(1);
                        object.Page = Integer.parseInt(xmlParser.getAttributeValue(2));
                        object.Type = xmlParser.getAttributeValue(3);
                        object.Src = xmlParser.getAttributeValue(null, "src");
                        list.add(object);
                        if (object.Type.equals("model")) {
                            ModelInfo model = new ModelInfo();
                            model.Name = object.Name;
                            model.Src = object.Src + model.Name;
                            String strXAngle = xmlParser.getAttributeValue(null, "xAngle");
                            String strYAngle = xmlParser.getAttributeValue(null, "yAngle");
                            String strIsEarth = xmlParser.getAttributeValue(null, "isEarth");
                            if (strXAngle != null) {
                                model.XAngle = Float.parseFloat(strXAngle);
                            }
                            if (strYAngle != null) {
                                model.YAngle = Float.parseFloat(strYAngle);
                            }
                            if (strIsEarth != null) {
                                model.IsEarth = strIsEarth.equals("1");
                            }
                            object.AddModel(model);
                        }
                        if (object.Type.equals("images")) {
                            while (eventType != 1) {
                                if (eventType == 2) {
                                    String strName2 = xmlParser.getName();
                                    if (strName2.equals("text")) {
                                        object.Text = xmlParser.nextText();
                                    } else if (strName2.equals("image")) {
                                        ImageInfo image = new ImageInfo();
                                        image.Name = xmlParser.getAttributeValue(0);
                                        image.Type = xmlParser.getAttributeValue(1);
                                        image.Src = xmlParser.getAttributeValue(2);
                                        image.Text = xmlParser.nextText();
                                        object.AddImage(image);
                                    }
                                } else if (eventType != 3) {
                                    continue;
                                } else if (xmlParser.getName().equals("object")) {
                                    break;
                                }
                                eventType = xmlParser.next();
                            }
                        }
                        if (object.Type.equals("models")) {
                            while (eventType != 1) {
                                if (eventType != 2) {
                                    if (eventType == 3 && xmlParser.getName().equals("object")) {
                                        break;
                                    }
                                } else if (xmlParser.getName().equals("model")) {
                                    ModelInfo model2 = new ModelInfo();
                                    model2.Name = xmlParser.getAttributeValue(0);
                                    model2.Src = xmlParser.getAttributeValue(1);
                                    String strXAngle2 = xmlParser.getAttributeValue(null, "xAngle");
                                    String strYAngle2 = xmlParser.getAttributeValue(null, "yAngle");
                                    String strIsEarth2 = xmlParser.getAttributeValue(null, "isEarth");
                                    if (strXAngle2 != null) {
                                        model2.XAngle = Float.parseFloat(strXAngle2);
                                    }
                                    if (strYAngle2 != null) {
                                        model2.YAngle = Float.parseFloat(strYAngle2);
                                    }
                                    if (strIsEarth2 != null) {
                                        model2.IsEarth = strIsEarth2.equals("1");
                                    }
                                    object.AddModel(model2);
                                }
                                eventType = xmlParser.next();
                            }
                        }
                    }
                }
                eventType = xmlParser.next();
            }
            return list;
        } catch (Exception ex) {
            ex.printStackTrace();
            return list;
        }
    }

    public static ObjectInfo getObjectFromXML(Context context, String id) {
        try {
            String categoryConfigFilePath = getProjectConfigFilePath(context);
            if (!new File(categoryConfigFilePath).exists()) {
                Toast.makeText(context, "配置文件未找到", Toast.LENGTH_SHORT).show();
                return null;
            }
            XmlPullParser xmlParser = XmlPullParserFactory.newInstance().newPullParser();
            xmlParser.setInput(new FileInputStream(categoryConfigFilePath), "UTF-8");
            int eventType = xmlParser.getEventType();
            String chapterId = null;
            while (eventType != XmlPullParser.END_DOCUMENT) {
                if (eventType != XmlPullParser.START_TAG || !xmlParser.getName().equals("object") || !xmlParser.getAttributeValue(null, "id").toLowerCase().equals(id.toLowerCase())) {
                    if (xmlParser.getName() != null && xmlParser.getName().equals("chapter")) {
                        chapterId = xmlParser.getAttributeValue(null, "id");
                    }
                    eventType = xmlParser.next();
                } else {
                    ObjectInfo object = new ObjectInfo();
                    object.ID = xmlParser.getAttributeValue(null, "id");
                    object.Name = xmlParser.getAttributeValue(null, "name");
                    object.ResID = xmlParser.getAttributeValue(null, "resid");
                    object.Type = xmlParser.getAttributeValue(null, "type");
                    object.ChapterID = chapterId;
                    if (object.Type.equals("model")) {
                        ModelInfo model = new ModelInfo();
                        model.Name = object.Name;
                        model.Src = object.Src + model.Name;
                        String strXAngle = xmlParser.getAttributeValue(null, "xAngle");
                        String strYAngle = xmlParser.getAttributeValue(null, "yAngle");
                        String strIsEarth = xmlParser.getAttributeValue(null, "isEarth");
                        if (strXAngle != null) {
                            model.XAngle = Float.parseFloat(strXAngle);
                        }
                        if (strYAngle != null) {
                            model.YAngle = Float.parseFloat(strYAngle);
                        }
                        if (strIsEarth != null) {
                            model.IsEarth = strIsEarth.equals("1");
                        }
                        object.AddModel(model);
                    }
//                    if (object.Type.equals("images")) {
//                        while (eventType != 1) {
//                            if (eventType != 2) {
//                                if (eventType == 3 && xmlParser.getName().equals("object")) {
//                                    break;
//                                }
//                            } else {
//                                String strName = xmlParser.getName();
//                                if (strName.equals("text")) {
//                                    object.Text = xmlParser.nextText();
//                                } else if (strName.equals("image")) {
//                                    ImageInfo image = new ImageInfo();
//                                    image.Name = xmlParser.getAttributeValue(null, "name");
//                                    image.Type = xmlParser.getAttributeValue(null, "type");
//                                    image.Src = xmlParser.getAttributeValue(null, "src");
//                                    image.Text = xmlParser.nextText();
//                                    object.AddImage(image);
//                                }
//                            }
//                            eventType = xmlParser.next();
//                        }
//                    }
                    if (!object.Type.equals("models")) {
                        return object;
                    }
                    while (eventType != XmlPullParser.END_DOCUMENT) {
                        if (eventType == XmlPullParser.START_TAG) {
                            if (xmlParser.getName().equals("model")) {
                                ModelInfo model2 = new ModelInfo();
                                model2.Name = xmlParser.getAttributeValue(null, "name");
                                model2.Src = xmlParser.getAttributeValue(null, "src");
                                String strXAngle2 = xmlParser.getAttributeValue(null, "xAngle");
                                String strYAngle2 = xmlParser.getAttributeValue(null, "yAngle");
                                String strIsEarth2 = xmlParser.getAttributeValue(null, "isEarth");
                                if (strXAngle2 != null) {
                                    model2.XAngle = Float.parseFloat(strXAngle2);
                                }
                                if (strYAngle2 != null) {
                                    model2.YAngle = Float.parseFloat(strYAngle2);
                                }
                                if (strIsEarth2 != null) {
                                    model2.IsEarth = strIsEarth2.equals("1");
                                }
                                object.AddModel(model2);
                            }
                        } else if (eventType == 3 && xmlParser.getName().equals("object")) {
                            return object;
                        }
                        eventType = xmlParser.next();
                    }
                    return object;
                }
            }
        } catch (Exception e) {
        }
        return null;
    }

    public static void gotoDetailPage(Context context, ObjectInfo object) {
        //region 需要调用播放器的资源类型

        if (object.Type.equalsIgnoreCase("objs") == false && (object.Type.equals("models") || object.Type.equals("model")) == false) {
            Toast.makeText(context, "调用播放器", Toast.LENGTH_SHORT).show();
            return;
        }
        //endregion

        Intent intent = new Intent();
        Bundle bundle = new Bundle();
        bundle.putSerializable("Object", object);
        intent.putExtras(bundle);
        intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
        if (object.Type.equals("objs")) {
            intent.setClass(context, MultiObjectActivity.class);
        }
//        else if (object.Type.equals("mp4")) {
//            intent.setClass(context, ResVideoPlayerActivity.class);
//        } else if (object.Type.equals("mp3")) {
//            intent.setClass(context, ResMP3PlayerActivity.class);
//        } else if (object.Type.equals("swf")) {
//            intent.setClass(context, ResSwfPlayerActivity.class);
//        } else if (object.Type.equals("html")) {
//            intent.setClass(context, ResWebViewActivity.class);
//        } else if (object.Type.equals("images")) {
//            String uris = "";
//            for (int i = 0; i < object.images.size(); i++) {
//                uris = uris + "file:///" + getProjectBathPath(context) + ((ImageInfo) object.images.get(i)).Src + ",";
//            }
//            if (!uris.equals("")) {
//                uris = uris.substring(0, uris.length() - 1);
//            }
//            intent.putExtra("image_urls", uris);
//            intent.putExtra("position", 0);
//            intent.setClass(context, ResImagesActivity.class);
//        }
        else if (object.Type.equals("models") || object.Type.equals("model")) {
            intent.setClass(context, ResModelActivity.class);
        }
//        else if (object.Type.equals("ppt")) {
//            intent = new Intent("android.intent.action.VIEW");
//            intent.addCategory("android.intent.category.DEFAULT");
//            intent.addFlags(SQLiteDatabase.CREATE_IF_NECESSARY);
//            intent.setDataAndType(Uri.parse("file:///" + getProjectBathPath(context) + object.Src + object.Name + "." + object.Type), "application/vnd.ms-powerpoint");
//        } else if (object.Type.equals("doc")) {
//            intent = new Intent("android.intent.action.VIEW");
//            intent.addCategory("android.intent.category.DEFAULT");
//            intent.addFlags(SQLiteDatabase.CREATE_IF_NECESSARY);
//            intent.setDataAndType(Uri.parse("file:///" + getProjectBathPath(context) + object.Src + object.Name + "." + object.Type), "application/vnd.ms-word");
//        } else if (object.Type.equals("xls")) {
//            intent = new Intent("android.intent.action.VIEW");
//            intent.addCategory("android.intent.category.DEFAULT");
//            intent.addFlags(SQLiteDatabase.CREATE_IF_NECESSARY);
//            intent.setDataAndType(Uri.parse("file:///" + getProjectBathPath(context) + object.Src + object.Name + "." + object.Type), "application/vnd.ms-excel");
//        } else if (object.Type.equals("pdf")) {
//            intent = new Intent("android.intent.action.VIEW");
//            intent.addCategory("android.intent.category.DEFAULT");
//            intent.addFlags(SQLiteDatabase.CREATE_IF_NECESSARY);
//            intent.setDataAndType(Uri.parse("file:///" + getProjectBathPath(context) + object.Src + object.Name + "." + object.Type), "application/pdf");
//        }

        context.startActivity(intent);
    }

//    public static int getResourceTypeIconId(String type) {
//        if (type.equals("images")) {
//            return R.drawable.ic_res_type_images;
//        }
//        if (type.equals("mp3")) {
//            return R.drawable.ic_res_type_mp3;
//        }
//        if (type.equals("mp4")) {
//            return R.drawable.ic_res_type_mp4;
//        }
//        if (type.equals("swf")) {
//            return R.drawable.ic_res_type_swf;
//        }
//        if (type.equals("html")) {
//            return R.drawable.ic_res_type_zct;
//        }
//        if (type.equals("models") || type.equals("model")) {
//            return R.drawable.ic_res_type_models;
//        }
//        if (type.equals("objs")) {
//            return R.drawable.ic_res_type_objs;
//        }
//        return R.drawable.ic_res_type_other;
//    }

//    public static String getPhotoURLs(Context context) {
//        String saveDir = context.getExternalFilesDir(null) + context.getResources().getString(R.string.path_photo_save);
//        File dir = new File(saveDir);
//        String fullPaths = "";
//        if (!dir.exists() || !dir.isDirectory()) {
//            return fullPaths;
//        }
//        String[] files = dir.list();
//        for (int i = files.length - 1; i >= 0; i--) {
//            fullPaths = fullPaths + "file://" + saveDir + InternalZipConstants.ZIP_FILE_SEPARATOR + files[i] + ",";
//        }
//        if (!fullPaths.equals("")) {
//            return fullPaths.substring(0, fullPaths.length() - 1);
//        }
//        return fullPaths;
//    }
//
//    public static String getPhotoReceiveURLs(Context context) {
//        String saveDir = context.getExternalFilesDir(null) + context.getResources().getString(R.string.path_photo_receive);
//        File dir = new File(saveDir);
//        String fullPaths = "";
//        if (!dir.exists() || !dir.isDirectory()) {
//            return fullPaths;
//        }
//        String[] files = dir.list();
//        for (int i = files.length - 1; i >= 0; i--) {
//            fullPaths = fullPaths + "file://" + saveDir + files[i] + ",";
//        }
//        if (!fullPaths.equals("")) {
//            return fullPaths.substring(0, fullPaths.length() - 1);
//        }
//        return fullPaths;
//    }

//    public static void gotoViewPhotosActivity(Context context) {
//        Intent intent = new Intent();
//        intent.putExtra("image_urls", getPhotoURLs(context));
//        intent.putExtra("position", 0);
//        intent.setClass(context, ShowPhotoListActivity.class);
//        context.startActivity(intent);
//    }

//    public static void gotoViewPhotosReceiveActivity(Context context) {
//        Intent intent = new Intent();
//        intent.putExtra("image_urls", getPhotoReceiveURLs(context));
//        intent.putExtra("position", 0);
//        intent.putExtra("isViewReceivePhotos", true);
//        intent.setClass(context, ShowPhotoListActivity.class);
//        context.startActivity(intent);
//    }

//    public static void gotoHelpActivity(Context context) {
//        Intent intent = new Intent(context, MyWebViewActivity.class);
//        intent.putExtra("Title", "使用说明");
//        intent.putExtra("URL", "file:///android_asset/help/help.html");
//        context.startActivity(intent);
//    }
}
