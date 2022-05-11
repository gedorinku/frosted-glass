#version 150

#moj_import <matrix.glsl>

uniform sampler2D Sampler0;
uniform sampler2D Sampler1;
uniform vec4 ColorModulator;

uniform float GameTime;

in vec4 texProj0;
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
    vec4 destColor = texture(Sampler0, texCoord0) * vertexColor;
    fragColor = destColor;// * ColorModulator;
}
