package com.sinomaps.geobookar.ui;

import android.os.Bundle;
import android.view.View;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.ListView;


import com.sinomaps.geobookar.R;
import com.sinomaps.geobookar.model.ImageInfo;
import com.sinomaps.geobookar.model.ObjectInfo;
import com.sinomaps.geobookar.utility.MyUtility;

import org.xmlpull.v1.XmlPullParser;
import org.xmlpull.v1.XmlPullParserFactory;

import java.io.FileInputStream;
import java.util.ArrayList;
import java.util.List;

/* renamed from: com.sinomaps.geobookar.ui.MultiObjectActivity */
public class MultiObjectActivity extends BaseActivity {
    /* access modifiers changed from: private */
    public List<ObjectInfo> mObjects = new ArrayList();

    /* access modifiers changed from: protected */
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView((int) R.layout.activity_multi_object);
        MyUtility.enableTranscent(this, R.color.yt_color_transcent);
        ObjectInfo obj = (ObjectInfo) getIntent().getSerializableExtra("Object");
        setTitle(obj.Name);
        boolean flag = false;
        try {
            String categoryConfigFilePath = MyUtility.getProjectConfigFilePath(this);
            if (!MyUtility.checkResourceIsExist(this, categoryConfigFilePath)) {
                finish();
                return;
            }
            XmlPullParser xmlParser = XmlPullParserFactory.newInstance().newPullParser();
            xmlParser.setInput(new FileInputStream(categoryConfigFilePath), "UTF-8");
            int eventType = xmlParser.getEventType();
            while (eventType != 1) {
                String strName = xmlParser.getName();
                if (eventType == 2) {
                    if (strName.equals("object") && xmlParser.getAttributeValue(0).equals(obj.f93ID)) {
                        flag = true;
                    }
                    if (flag && strName.equals("subObject")) {
                        ObjectInfo object = new ObjectInfo();
                        object.Name = xmlParser.getAttributeValue(null, "name");
                        object.Type = xmlParser.getAttributeValue(null, "type");
                        object.Src = xmlParser.getAttributeValue(null, "src");
                        this.mObjects.add(object);
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
                                } else if (xmlParser.getName().equals("subObject")) {
                                    break;
                                }
                                eventType = xmlParser.next();
                            }
                        }
                    }
                } else if (eventType == 3 && flag && strName.equals("object")) {
                    flag = false;
                }
                eventType = xmlParser.next();
            }
//            ListView listView = (ListView) findViewById(R.id.listMultiObject);
//            listView.setAdapter(new CommonAdapter<ObjectInfo>(this, this.mObjects, R.layout.item_list_object) {
//                public void convert(ViewHolder holder, ObjectInfo objectInfo) {
//                    holder.setVisible(R.id.itemPageNumber, 8);
//                    holder.setText(R.id.itemName, objectInfo.Name);
//                    holder.setImageResource(R.id.itemResTypeIcon, MyUtility.getResourceTypeIconId(objectInfo.Type));
//                }
//            });
//            listView.setOnItemClickListener(new OnItemClickListener() {
//                public void onItemClick(AdapterView<?> adapterView, View view, int position, long id) {
//                    MyUtility.gotoDetailPage(MultiObjectActivity.this, (ObjectInfo) MultiObjectActivity.this.mObjects.get(position));
//                }
//            });
        } catch (Exception ex) {
            ex.printStackTrace();
        }
    }
}
