package com.sinomaps.geobookar.vr;

/* renamed from: com.sinomaps.geobookar.vr.CubeShaders */
public class CubeShaders {
    public static final String CUBE_MESH_FRAGMENT_SHADER = " \n\nprecision mediump float; \n \nvarying vec2 texCoord; \nuniform sampler2D texSampler2D; \n \nvoid main() \n{ \n   gl_FragColor = texture2D(texSampler2D, texCoord); \n} \n";
    public static final String CUBE_MESH_VERTEX_SHADER = " \n\nattribute vec4 vertexPosition; \nattribute vec2 vertexTexCoord; \n\nvarying vec2 texCoord; \n\nuniform mat4 modelViewProjectionMatrix; \n\nvoid main() \n{ \n   gl_Position = modelViewProjectionMatrix * vertexPosition; \n   texCoord = vertexTexCoord; \n} \n";
}
