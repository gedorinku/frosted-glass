#version 150

uniform sampler2D Sampler0;

uniform vec4 ColorModulator;

in vec4 vertexColor;
in vec2 texCoord0;
in vec2 texCoord2;
in vec4 normal;

out vec4 fragColor;

void main() {
    //fragColor = vec4(1.0, 1.0, 0.0, 0.5);
//     vec2 tFrag = vec2(1.0 / 128.0);
//     vec4 destColor = texture(Sampler0, texCoord0);
//
//     destColor *= 0.36;
//     destColor += texture(Sampler0, (texCoord0 + vec2(-1.0,  1.0)) * tFrag) * 0.04;
//     destColor += texture(Sampler0, (texCoord0 + vec2( 0.0,  1.0)) * tFrag) * 0.04;
//     destColor += texture(Sampler0, (texCoord0 + vec2( 1.0,  1.0)) * tFrag) * 0.04;
//     destColor += texture(Sampler0, (texCoord0 + vec2(-1.0,  0.0)) * tFrag) * 0.04;
//     destColor += texture(Sampler0, (texCoord0 + vec2( 1.0,  0.0)) * tFrag) * 0.04;
//     destColor += texture(Sampler0, (texCoord0 + vec2(-1.0, -1.0)) * tFrag) * 0.04;
//     destColor += texture(Sampler0, (texCoord0 + vec2( 0.0, -1.0)) * tFrag) * 0.04;
//     destColor += texture(Sampler0, (texCoord0 + vec2( 1.0, -1.0)) * tFrag) * 0.04;
//     destColor += texture(Sampler0, (texCoord0 + vec2(-2.0,  2.0)) * tFrag) * 0.02;
//     destColor += texture(Sampler0, (texCoord0 + vec2(-1.0,  2.0)) * tFrag) * 0.02;
//     destColor += texture(Sampler0, (texCoord0 + vec2( 0.0,  2.0)) * tFrag) * 0.02;
//     destColor += texture(Sampler0, (texCoord0 + vec2( 1.0,  2.0)) * tFrag) * 0.02;
//     destColor += texture(Sampler0, (texCoord0 + vec2( 2.0,  2.0)) * tFrag) * 0.02;
//     destColor += texture(Sampler0, (texCoord0 + vec2(-2.0,  1.0)) * tFrag) * 0.02;
//     destColor += texture(Sampler0, (texCoord0 + vec2( 2.0,  1.0)) * tFrag) * 0.02;
//     destColor += texture(Sampler0, (texCoord0 + vec2(-2.0,  0.0)) * tFrag) * 0.02;
//     destColor += texture(Sampler0, (texCoord0 + vec2( 2.0,  0.0)) * tFrag) * 0.02;
//     destColor += texture(Sampler0, (texCoord0 + vec2(-2.0, -1.0)) * tFrag) * 0.02;
//     destColor += texture(Sampler0, (texCoord0 + vec2( 2.0, -1.0)) * tFrag) * 0.02;
//     destColor += texture(Sampler0, (texCoord0 + vec2(-2.0, -2.0)) * tFrag) * 0.02;
//     destColor += texture(Sampler0, (texCoord0 + vec2(-1.0, -2.0)) * tFrag) * 0.02;
//     destColor += texture(Sampler0, (texCoord0 + vec2( 0.0, -2.0)) * tFrag) * 0.02;
//     destColor += texture(Sampler0, (texCoord0 + vec2( 1.0, -2.0)) * tFrag) * 0.02;
//     destColor += texture(Sampler0, (texCoord0 + vec2( 2.0, -2.0)) * tFrag) * 0.02;
//     //fragColor = destColor;
//     fragColor = vec4(destColor.rgb, 1.0);
//    vec4 destColor = texture(Sampler0, texCoord0) * vertexColor * ColorModulator;
//    //fragColor = vec4(destColor.rgb, 1.0);// * ColorModulator;
//    fragColor = texture(Sampler0, texCoord0) * vertexColor;
//    vec4 color = texture(Sampler0, texCoord0) * vertexColor * ColorModulator;
//    fragColor = linear_fog(color, vertexDistance, FogStart, FogEnd, FogColor);

    vec4 color = texture(Sampler0, texCoord0) * vertexColor;
    fragColor = color * ColorModulator;
    //fragColor = vec4(texCoord0.x, texCoord0.y, 0.0, 1.0);
    //fragColor = texture(Sampler0, vec2(1.0, 1.0));
    //fragColor = ColorModulator;
    //fragColor = texture(Sampler0, vec2(0.1, 0.1));
    //fragColor = texelFetch(Sampler0, ivec2(2, 2), 0);
}
