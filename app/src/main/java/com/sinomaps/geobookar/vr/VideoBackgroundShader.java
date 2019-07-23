package com.sinomaps.geobookar.vr;

/* renamed from: com.sinomaps.geobookar.vr.VideoBackgroundShader */
public class VideoBackgroundShader {
    public static final String VB_FRAGMENT_SHADER = "precision mediump float;\nvarying vec2 texCoord;\nuniform sampler2D texSampler2D;\nvoid main ()\n{\n    gl_FragColor = texture2D(texSampler2D, texCoord);\n}\n";
    public static final String VB_VERTEX_SHADER = "attribute vec4 vertexPosition;\nattribute vec2 vertexTexCoord;\nuniform mat4 projectionMatrix;\nvarying vec2 texCoord;\nvoid main()\n{\n    gl_Position = projectionMatrix * vertexPosition;\n    texCoord = vertexTexCoord;\n}\n";
}
