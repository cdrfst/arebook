package com.sinomaps.geobookar.opengl;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.opengl.GLES20;

import com.sinomaps.geobookar.utility.MyLogger;
import com.sinomaps.geobookar.vr.Texture;

import net.lingala.zip4j.util.InternalZipConstants;

import java.io.BufferedInputStream;
import java.io.BufferedReader;
import java.io.ByteArrayOutputStream;
import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.util.ArrayList;
import java.util.List;

public class My3DObject {
    private boolean bIsEarth = false;
    private boolean bIsEnableXRotate = true;
    private float boundingRadius = 0.0f;
    private float[] center = new float[3];
    private Texture defaultTexture = null;
    public List<My3DItem> items = new ArrayList();
    private float mScale = 1.0f;
    private List<MtlInfo> mtls = new ArrayList();

    /* renamed from: sp */
    private ShaderParam f94sp = new ShaderParam();
    private float xAngle = 0.0f;
    private float yAngle = 0.0f;

    public void setXAngle(float xAngle2) {
        this.xAngle = xAngle2;
    }

    public float getXAngle() {
        return this.xAngle;
    }

    public void setYAngle(float yAngle2) {
        this.yAngle = yAngle2;
    }

    public float getYAngle() {
        return this.yAngle;
    }

    public void setScale(float scale) {
        this.mScale = scale;
    }

    public float getScale() {
        return this.mScale;
    }

    public void setXRotateEnable(boolean isEnable) {
        this.bIsEnableXRotate = isEnable;
    }

    public boolean getXRoateIsEnable() {
        return this.bIsEnableXRotate;
    }

    public float[] getCenterPoint() {
        return this.center;
    }

    public float getBoundingRadius() {
        return this.boundingRadius;
    }

    public boolean isbIsEarth() {
        return this.bIsEarth;
    }

    public void setbIsEarth(boolean bIsEarth2) {
        this.bIsEarth = bIsEarth2;
    }

    public void draw(float[] mMVPMatrix, float[] mMVMatrix, float[] mModelMatrix, float[] mViewMatrix) {
        for (int i = 0; i < this.items.size(); i++) {
            ((My3DItem) this.items.get(i)).draw(this.f94sp, mMVPMatrix, mMVMatrix, mModelMatrix, mViewMatrix);
        }
    }

    private void loadMtls(Context context, String mtlFile) {
        MyLogger.m163v("开始读取纹理数据……");
        this.mtls.clear();
        try {
            String workFolder = mtlFile.substring(0, mtlFile.lastIndexOf(InternalZipConstants.ZIP_FILE_SEPARATOR) + 1);
            InputStream is = new FileInputStream(mtlFile);
            BufferedReader reader = new BufferedReader(new InputStreamReader(is, "gb2312"));
            MtlInfo mtl = null;
            String str = "";
            while (true) {
                String line = reader.readLine();
                if (line == null) {
                    reader.close();
                    is.close();
                    return;
                } else if (line.startsWith("newmtl")) {
                    mtl = new MtlInfo();
                    mtl.name = line.split(" ")[1];
                    MyLogger.m163v("材质名称：" + mtl.name);
                } else if (line.startsWith("Ka")) {
                    String ss = line.substring(4);
                    mtl.ambientColor[0] = Float.parseFloat(ss.split(" ")[0]);
                    mtl.ambientColor[1] = Float.parseFloat(ss.split(" ")[1]);
                    mtl.ambientColor[2] = Float.parseFloat(ss.split(" ")[2]);
                    MyLogger.m163v("材质阴影色：" + mtl.ambientColor[0] + "," + mtl.ambientColor[1] + "," + mtl.ambientColor[2]);
                } else if (line.startsWith("Kd")) {
                    String ss2 = line.substring(4);
                    mtl.diffuseColor[0] = Float.parseFloat(ss2.split(" ")[0]);
                    mtl.diffuseColor[1] = Float.parseFloat(ss2.split(" ")[1]);
                    mtl.diffuseColor[2] = Float.parseFloat(ss2.split(" ")[2]);
                    MyLogger.m163v("材质固有色：" + mtl.diffuseColor[0] + "," + mtl.diffuseColor[1] + "," + mtl.diffuseColor[2]);
                } else if (line.startsWith("Ks")) {
                    String ss3 = line.substring(4);
                    mtl.highLight[0] = Float.parseFloat(ss3.split(" ")[0]);
                    mtl.highLight[1] = Float.parseFloat(ss3.split(" ")[1]);
                    mtl.highLight[2] = Float.parseFloat(ss3.split(" ")[2]);
                    MyLogger.m163v("材质高光色：" + mtl.highLight[0] + "," + mtl.highLight[1] + "," + mtl.highLight[2]);
                } else if (line.startsWith("d  ")) {
                    mtl.alpha = Float.parseFloat(line.substring(3));
                    MyLogger.m163v("材质透明度：" + mtl.alpha);
                } else if (line.startsWith("map_Kd")) {
                    String textureFileName = line.substring(line.lastIndexOf(" ") + 1);
                    mtl.textureName = textureFileName;
                    MyLogger.m163v("材质纹理图片：" + mtl.textureName);
                    mtl.texture = initTextureFromFile(workFolder + textureFileName);
                } else if (line.equals("#") && mtl != null) {
                    this.mtls.add(mtl);
                }
            }
        } catch (Exception ex) {
            MyLogger.m163v("load mtl file error:" + ex.toString());
        }
    }

    private MtlInfo getMtlInfo(String mtlName) {
        for (MtlInfo model : this.mtls) {
            if (model.name.equals(mtlName)) {
                return model;
            }
        }
        return null;
    }

    public void loadObjFile(Context context, String objPath) {
        long startTime = System.currentTimeMillis();
        MyLogger.m163v("开始读取OBJ格式模型数据……");
        String objFileName = objPath + ".obj";
        String mtlFileName = objPath + ".mtl";
        File file = new File(mtlFileName);
        if (file.exists()) {
            loadMtls(context, mtlFileName);
        }
        this.items.clear();
        float[] maxVerts = new float[3];
        float[] minVerts = new float[3];
        try {
            FileInputStream fileInputStream = new FileInputStream(objFileName);
            InputStreamReader inputStreamReader = new InputStreamReader(fileInputStream, "gbk");
            BufferedReader bufferedReader = new BufferedReader(inputStreamReader);
            ArrayList<Float> alv = new ArrayList<>();
            ArrayList<Float> alt = new ArrayList<>();
            ArrayList<Float> aln = new ArrayList<>();
            ArrayList<Float> alvResult = new ArrayList<>();
            ArrayList<Float> altResult = new ArrayList<>();
            ArrayList<Float> alnResult = new ArrayList<>();
            My3DItem item = null;
            while (true) {
                String line = bufferedReader.readLine();
                if (line == null) {
                    break;
                } else if (line.contains("# object ")) {
                    if (item != null) {
                        item.initData(alvResult, altResult, alnResult);
                        this.items.add(item);
                    }
                    alvResult.clear();
                    altResult.clear();
                    alnResult.clear();
                    item = new My3DItem();
                    item.name = line.split(" ")[2];
                    MyLogger.m163v("模型名称：" + item.name);
                } else {
                    if (item != null) {
                        if (item.name != null && line.equals("g " + item.name)) {
                            String mtlName = bufferedReader.readLine().split(" ")[1];
                            MyLogger.m163v("模型材质名称：" + mtlName);
                            item.mtlInfo = getMtlInfo(mtlName);
                        }
                    }
                    String[] tempsa = line.split("[ ]+");
                    if (tempsa[0].trim().equals("v")) {
                        float valueX = Float.parseFloat(tempsa[1]);
                        float valueY = Float.parseFloat(tempsa[2]);
                        float valueZ = Float.parseFloat(tempsa[3]);
                        alv.add(Float.valueOf(valueX));
                        alv.add(Float.valueOf(valueY));
                        alv.add(Float.valueOf(valueZ));
                        minVerts[0] = minVerts[0] > valueX ? valueX : minVerts[0];
                        minVerts[1] = minVerts[1] > valueY ? valueY : minVerts[1];
                        minVerts[2] = minVerts[2] > valueZ ? valueZ : minVerts[2];
                        if (maxVerts[0] >= valueX) {
                            valueX = maxVerts[0];
                        }
                        maxVerts[0] = valueX;
                        if (maxVerts[1] >= valueY) {
                            valueY = maxVerts[1];
                        }
                        maxVerts[1] = valueY;
                        if (maxVerts[2] >= valueZ) {
                            valueZ = maxVerts[2];
                        }
                        maxVerts[2] = valueZ;
                    } else if (tempsa[0].trim().equals("vt")) {
                        alt.add(Float.valueOf(Float.parseFloat(tempsa[1])));
                        alt.add(Float.valueOf(Float.parseFloat(tempsa[2])));
                        alt.add(Float.valueOf(Float.parseFloat(tempsa[3])));
                    } else if (tempsa[0].trim().equals("vn")) {
                        aln.add(Float.valueOf(Float.parseFloat(tempsa[1])));
                        aln.add(Float.valueOf(Float.parseFloat(tempsa[2])));
                        aln.add(Float.valueOf(Float.parseFloat(tempsa[3])));
                    } else if (tempsa[0].trim().equals("f")) {
                        String splitStr = InternalZipConstants.ZIP_FILE_SEPARATOR;
                        String[] firstVtArray = tempsa[1].split(splitStr);
                        String[] secondVtArray = tempsa[2].split(splitStr);
                        String[] thirdVtArray = tempsa[3].split(splitStr);
                        int[] index = new int[3];
                        index[0] = Integer.parseInt(firstVtArray[0]) - 1;
                        float x0 = ((Float) alv.get(index[0] * 3)).floatValue();
                        float y0 = ((Float) alv.get((index[0] * 3) + 1)).floatValue();
                        float z0 = ((Float) alv.get((index[0] * 3) + 2)).floatValue();
                        alvResult.add(Float.valueOf(x0));
                        alvResult.add(Float.valueOf(y0));
                        alvResult.add(Float.valueOf(z0));
                        index[1] = Integer.parseInt(secondVtArray[0]) - 1;
                        float x1 = ((Float) alv.get(index[1] * 3)).floatValue();
                        float y1 = ((Float) alv.get((index[1] * 3) + 1)).floatValue();
                        float z1 = ((Float) alv.get((index[1] * 3) + 2)).floatValue();
                        alvResult.add(Float.valueOf(x1));
                        alvResult.add(Float.valueOf(y1));
                        alvResult.add(Float.valueOf(z1));
                        index[2] = Integer.parseInt(thirdVtArray[0]) - 1;
                        float x2 = ((Float) alv.get(index[2] * 3)).floatValue();
                        float y2 = ((Float) alv.get((index[2] * 3) + 1)).floatValue();
                        float z2 = ((Float) alv.get((index[2] * 3) + 2)).floatValue();
                        alvResult.add(Float.valueOf(x2));
                        alvResult.add(Float.valueOf(y2));
                        alvResult.add(Float.valueOf(z2));
                        int indexNormal = Integer.parseInt(firstVtArray[2]) - 1;
                        alnResult.add(aln.get(indexNormal * 3));
                        alnResult.add(aln.get((indexNormal * 3) + 1));
                        alnResult.add(aln.get((indexNormal * 3) + 2));
                        int indexNormal2 = Integer.parseInt(secondVtArray[2]) - 1;
                        alnResult.add(aln.get(indexNormal2 * 3));
                        alnResult.add(aln.get((indexNormal2 * 3) + 1));
                        alnResult.add(aln.get((indexNormal2 * 3) + 2));
                        int indexNormal3 = Integer.parseInt(thirdVtArray[2]) - 1;
                        alnResult.add(aln.get(indexNormal3 * 3));
                        alnResult.add(aln.get((indexNormal3 * 3) + 1));
                        alnResult.add(aln.get((indexNormal3 * 3) + 2));
                        if (!firstVtArray[1].equals("")) {
                            int indexTex = Integer.parseInt(firstVtArray[1]) - 1;
                            altResult.add(alt.get(indexTex * 3));
                            altResult.add(alt.get((indexTex * 3) + 1));
                            altResult.add(alt.get((indexTex * 3) + 2));
                            int indexTex2 = Integer.parseInt(secondVtArray[1]) - 1;
                            altResult.add(alt.get(indexTex2 * 3));
                            altResult.add(alt.get((indexTex2 * 3) + 1));
                            altResult.add(alt.get((indexTex2 * 3) + 2));
                            int indexTex3 = Integer.parseInt(thirdVtArray[1]) - 1;
                            altResult.add(alt.get(indexTex3 * 3));
                            altResult.add(alt.get((indexTex3 * 3) + 1));
                            altResult.add(alt.get((indexTex3 * 3) + 2));
                        }
                    }
                }
            }
            bufferedReader.close();
            fileInputStream.close();
            item.initData(alvResult, altResult, alnResult);
            alvResult.clear();
            altResult.clear();
            alnResult.clear();
            this.items.add(item);
        } catch (Exception ex) {
            MyLogger.m163v("load object error:" + ex.toString());
        }
        for (int t = 0; t < 3; t++) {
            this.center[t] = (Math.abs(maxVerts[t]) - Math.abs(minVerts[t])) / 2.0f;
        }
        this.boundingRadius = (float) Math.sqrt((double) (((maxVerts[0] - this.center[0]) * (maxVerts[0] - this.center[0])) + ((maxVerts[1] - this.center[1]) * (maxVerts[1] - this.center[1])) + ((maxVerts[2] - this.center[2]) * (maxVerts[2] - this.center[2]))));
        MyLogger.m163v("加载模型时间：" + (System.currentTimeMillis() - startTime));
    }

    public void changeData(String path) {
        try {
            FileOutputStream fos = new FileOutputStream(path + ".dat");
            DataOutputStream dos = new DataOutputStream(fos);
            MyLogger.m163v("开始转换数据……");
            dos.writeUTF("start");
            dos.writeInt(this.mtls.size());
            for (int i = 0; i < this.mtls.size(); i++) {
                MtlInfo mtlInfo = (MtlInfo) this.mtls.get(i);
                dos.writeUTF(mtlInfo.name);
                dos.writeUTF(mtlInfo.textureName);
                dos.writeFloat(mtlInfo.diffuseColor[0]);
                dos.writeFloat(mtlInfo.diffuseColor[1]);
                dos.writeFloat(mtlInfo.diffuseColor[2]);
                dos.writeFloat(mtlInfo.alpha);
                MyLogger.m163v("材质名称：" + mtlInfo.name + ",贴图：" + mtlInfo.textureName + ",固有色:" + mtlInfo.diffuseColor[0] + "," + mtlInfo.diffuseColor[1] + "," + mtlInfo.diffuseColor[2] + ",透明度：" + mtlInfo.alpha);
            }
            dos.writeFloat(this.center[0]);
            dos.writeFloat(this.center[1]);
            dos.writeFloat(this.center[2]);
            dos.writeFloat(this.boundingRadius);
            dos.writeInt(this.items.size());
            MyLogger.m163v("模型个数：" + this.items.size());
            for (int i2 = 0; i2 < this.items.size(); i2++) {
                My3DItem item = (My3DItem) this.items.get(i2);
                dos.writeUTF(item.name);
                dos.writeUTF(item.mtlInfo.name);
                MyLogger.m163v("模型名称：" + item.name + "，材质名称：" + item.mtlInfo.name);
                dos.writeInt(item.numVerts);
                MyLogger.m163v("顶点个数：" + item.numVerts);
                dos.writeInt(item.listVerts.size());
                MyLogger.m163v("vert：" + item.listVerts.size());
                for (int j = 0; j < item.listVerts.size(); j++) {
                    dos.writeFloat(((Float) item.listVerts.get(j)).floatValue());
                }
                dos.writeInt(item.listTextCoords.size());
                MyLogger.m163v("textcoords：" + item.listTextCoords.size());
                for (int j2 = 0; j2 < item.listTextCoords.size(); j2++) {
                    dos.writeFloat(((Float) item.listTextCoords.get(j2)).floatValue());
                }
                dos.writeInt(item.listNorms.size());
                MyLogger.m163v("norms：" + item.listNorms.size());
                for (int j3 = 0; j3 < item.listNorms.size(); j3++) {
                    dos.writeFloat(((Float) item.listNorms.get(j3)).floatValue());
                }
            }
            dos.writeUTF("end");
            MyLogger.m163v("转换数据完成。");
            dos.close();
            fos.close();
        } catch (Exception ex) {
            MyLogger.m163v(ex.toString());
        }
    }

    public void loadDatFile(Context context, String objPath) {
        long startTime = System.currentTimeMillis();
        String workFolder = objPath.substring(0, objPath.lastIndexOf(InternalZipConstants.ZIP_FILE_SEPARATOR) + 1);
        try {
            FileInputStream fis = new FileInputStream(objPath + ".dat");
            BufferedInputStream bis = new BufferedInputStream(fis);
            DataInputStream dis = new DataInputStream(bis);
            String readUTF = dis.readUTF();
            MyLogger.m163v("开始读取自定义模型数据……");
            this.mtls.clear();
            int n_textures = dis.readInt();
            MyLogger.m163v("纹理个数：" + n_textures);
            for (int i = 0; i < n_textures; i++) {
                MtlInfo mtl = new MtlInfo();
                mtl.name = dis.readUTF();
                mtl.textureName = dis.readUTF();
                mtl.diffuseColor[0] = dis.readFloat();
                mtl.diffuseColor[1] = dis.readFloat();
                mtl.diffuseColor[2] = dis.readFloat();
                mtl.alpha = dis.readFloat();
                MyLogger.m163v("材质：" + mtl.name + ",贴图：" + mtl.textureName + ",固有色：" + mtl.diffuseColor[0] + "," + mtl.diffuseColor[1] + "," + mtl.diffuseColor[2] + ",alpha:" + mtl.alpha);
                if (mtl.textureName.equals("default_texture.png")) {
                    if (this.defaultTexture == null) {
                        this.defaultTexture = initTextureFromAsset(context, "default_texture.png");
                    }
                    mtl.texture = this.defaultTexture;
                } else if (!mtl.textureName.equals("")) {
                    mtl.texture = initTextureFromFile(workFolder + mtl.textureName);
                }
                this.mtls.add(mtl);
            }
            this.center[0] = dis.readFloat();
            this.center[1] = dis.readFloat();
            this.center[2] = dis.readFloat();
            this.boundingRadius = dis.readFloat();
            MyLogger.m163v("模型属性：" + this.center[0] + "," + this.center[1] + "," + this.center[2] + ",r:" + this.boundingRadius);
            int n_items = dis.readInt();
            MyLogger.m163v("模型个数：" + n_items);
            for (int i2 = 0; i2 < n_items; i2++) {
                My3DItem item = new My3DItem();
                item.name = dis.readUTF();
                String mtlName = dis.readUTF();
                MyLogger.m163v("模型名称：" + item.name + ",材质：" + mtlName);
                item.mtlInfo = getMtlInfo(mtlName);
                item.numVerts = dis.readInt();
                ArrayList<Float> alvResult = new ArrayList<>();
                ArrayList<Float> altResult = new ArrayList<>();
                ArrayList<Float> alnResult = new ArrayList<>();
                int vCount = dis.readInt();
                for (int j = 0; j < vCount; j++) {
                    alvResult.add(Float.valueOf(dis.readFloat()));
                }
                int tCount = dis.readInt();
                for (int j2 = 0; j2 < tCount; j2++) {
                    altResult.add(Float.valueOf(dis.readFloat()));
                }
                int nCount = dis.readInt();
                for (int j3 = 0; j3 < nCount; j3++) {
                    alnResult.add(Float.valueOf(dis.readFloat()));
                }
                item.initData(alvResult, altResult, alnResult);
                this.items.add(item);
            }
            String readUTF2 = dis.readUTF();
            MyLogger.m163v("读取模型数据完毕。");
            dis.close();
            bis.close();
            fis.close();
        } catch (Exception ex) {
            MyLogger.m163v(ex.toString());
        }
        MyLogger.m163v("加载模型时间：" + (System.currentTimeMillis() - startTime));
    }

    public void bindTextures() {
        Texture texture;
        for (My3DItem my3DItem : this.items) {
            MtlInfo mtlInfo = my3DItem.mtlInfo;
            if (mtlInfo == null) {
                texture = this.defaultTexture;
            } else {
                texture = my3DItem.mtlInfo.texture;
            }
            if (!(mtlInfo == null || texture == null)) {
                GLES20.glGenTextures(1, texture.mTextureID, 0);
                GLES20.glBindTexture(3553, texture.mTextureID[0]);
                GLES20.glTexParameterf(3553, 10241, 9728.0f);
                GLES20.glTexParameterf(3553, 10240, 9728.0f);
                GLES20.glTexParameterf(3553, 10242, 10497.0f);
                GLES20.glTexParameterf(3553, 10243, 10497.0f);
                GLES20.glTexImage2D(3553, 0, 6408, texture.mWidth, texture.mHeight, 0, 6408, 5121, texture.mData);
                GLES20.glBindTexture(3553, 0);
            }
        }
    }

    public void initShader(Context context, String vertexFileName, String fragFileName) {
        int vertexShader = GLES20.glCreateShader(35633);
        GLES20.glShaderSource(vertexShader, loadFromAssetsFile(context, vertexFileName));
        GLES20.glCompileShader(vertexShader);
        int fragShader = GLES20.glCreateShader(35632);
        GLES20.glShaderSource(fragShader, loadFromAssetsFile(context, fragFileName));
        GLES20.glCompileShader(fragShader);
        this.f94sp.mProgram = GLES20.glCreateProgram();
        GLES20.glAttachShader(this.f94sp.mProgram, vertexShader);
        GLES20.glAttachShader(this.f94sp.mProgram, fragShader);
        GLES20.glLinkProgram(this.f94sp.mProgram);
        int[] linkStatus = new int[1];
        GLES20.glGetProgramiv(this.f94sp.mProgram, 35714, linkStatus, 0);
        if (linkStatus[0] == 0) {
            GLES20.glDeleteProgram(this.f94sp.mProgram);
            MyLogger.m166w("Link of program failed.");
        }
        this.f94sp.mPositionHandle = GLES20.glGetAttribLocation(this.f94sp.mProgram, "vPosition");
        this.f94sp.mNormalHandle = GLES20.glGetAttribLocation(this.f94sp.mProgram, "vNormal");
        this.f94sp.mEnableTextureHandle = GLES20.glGetUniformLocation(this.f94sp.mProgram, "texturesEnabled");
        this.f94sp.mTexCoorHandle = GLES20.glGetAttribLocation(this.f94sp.mProgram, "vTexture");
        this.f94sp.mTextureSamplerHandle = GLES20.glGetUniformLocation(this.f94sp.mProgram, "textures");
        this.f94sp.mLightingEnabledHandle = GLES20.glGetUniformLocation(this.f94sp.mProgram, "lightingEnabled");
        this.f94sp.mMVPMatrixHandle = GLES20.glGetUniformLocation(this.f94sp.mProgram, "mMVPMatrix");
        this.f94sp.mModelViewMatrixHandle = GLES20.glGetUniformLocation(this.f94sp.mProgram, "modelViewMatrix");
        this.f94sp.mModelMatrixHandle = GLES20.glGetUniformLocation(this.f94sp.mProgram, "mModelMatrix");
        this.f94sp.mViewMatrixHandle = GLES20.glGetUniformLocation(this.f94sp.mProgram, "mViewMatrix");
        this.f94sp.mLighPosVecHandle = GLES20.glGetUniformLocation(this.f94sp.mProgram, "lightPosVec");
        this.f94sp.mVBrightnessHandle = GLES20.glGetUniformLocation(this.f94sp.mProgram, "vBrightness");
        this.f94sp.mColor = GLES20.glGetUniformLocation(this.f94sp.mProgram, "uColor");
    }

    private String loadFromAssetsFile(Context context, String fileName) {
        String result = null;
        try {
            InputStream is = context.getAssets().open(fileName);
            ByteArrayOutputStream baos = new ByteArrayOutputStream();
            while (true) {
                int ch = is.read();
                if (ch != -1) {
                    baos.write(ch);
                } else {
                    byte[] buff = baos.toByteArray();
                    baos.close();
                    is.close();
                    String result2 = new String(buff, "UTF-8");
                    try {
                        return result2.replaceAll("\\r\n", "\n");
                    } catch (Exception e) {
                        e = e;
                        result = result2;
                    }
                }
            }
        } catch (Exception e2) {
            e2.printStackTrace();
            return result;
        }
    }

    private Texture initTextureFromFile(String fileName) throws IOException {
        return initTexture(new FileInputStream(fileName));
    }

    private Texture initTextureFromAsset(Context context, String fileName) throws IOException {
        return initTexture(context.getAssets().open(fileName));
    }

    private Texture initTexture(InputStream is) throws IOException {
        Bitmap bitmap = BitmapFactory.decodeStream(is);
        is.close();
        if (bitmap == null) {
            return null;
        }
        int[] data = new int[(bitmap.getWidth() * bitmap.getHeight())];
        bitmap.getPixels(data, 0, bitmap.getWidth(), 0, 0, bitmap.getWidth(), bitmap.getHeight());
        Texture loadTextureFromIntBuffer = Texture.loadTextureFromIntBuffer(data, bitmap.getWidth(), bitmap.getHeight());
        bitmap.recycle();
        return loadTextureFromIntBuffer;
    }
}
