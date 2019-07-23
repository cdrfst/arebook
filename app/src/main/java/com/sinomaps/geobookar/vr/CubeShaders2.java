package com.sinomaps.geobookar.vr;

/* renamed from: com.sinomaps.geobookar.vr.CubeShaders2 */
public class CubeShaders2 {
    public static final String CUBE_MESH_FRAGMENT_SHADER = " \n\nprecision mediump float; \n \nvarying vec2 texCoord; \nvarying vec4 normal; \n \nuniform float enableTexture; \n \nuniform vec4 fragColor;  \n \nuniform sampler2D sTexture;\n \nvoid main() \n{ \n  if(enableTexture > 0.5) {gl_FragColor = texture2D(sTexture, texCoord);} else{gl_FragColor = fragColor;} \n} \n";
    public static final String CUBE_MESH_VERTEX_SHADER = " \n\nattribute vec4 vertexPosition; \nattribute vec4 vertexNormal; \nattribute vec2 vertexTexCoord; \n\nvarying vec2 texCoord; \nvarying vec4 normal; \n\nuniform mat4 modelViewProjectionMatrix; \n\nvoid main() \n{ \n   gl_Position = modelViewProjectionMatrix * vertexPosition; \n   normal = vertexNormal; \n   texCoord = vertexTexCoord; \n} \n";
}
