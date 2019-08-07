package com.sinomaps.geobookar.model;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;

public class ObjectInfo implements Serializable {

    /* renamed from: ID */
    public String ResID;
    public String ChapterID;
    public String ID;
    public String Name;
    public int Page;
    public String Src;
    public String Text;
    public String Type;
    public List<ImageInfo> images = new ArrayList();
    public List<ModelInfo> models = new ArrayList();

    public void AddImage(ImageInfo model) {
        this.images.add(model);
    }

    public void AddModel(ModelInfo model) {
        this.models.add(model);
    }
}
