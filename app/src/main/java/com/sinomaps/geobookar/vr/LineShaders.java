package com.sinomaps.geobookar.vr;

/* renamed from: com.sinomaps.geobookar.vr.LineShaders */
public class LineShaders {
    public static final String LINE_FRAGMENT_SHADER = " \n \nprecision mediump float; \nuniform float opacity; \nuniform vec3 color; \n \nvoid main() \n{ \n   gl_FragColor = vec4(color.r, color.g, color.b, opacity); \n} \n";
    public static final String LINE_VERTEX_SHADER = " \nattribute vec4 vertexPosition; \nuniform mat4 modelViewProjectionMatrix; \n \nvoid main() \n{ \n   gl_Position = modelViewProjectionMatrix * vertexPosition; \n} \n";
}
