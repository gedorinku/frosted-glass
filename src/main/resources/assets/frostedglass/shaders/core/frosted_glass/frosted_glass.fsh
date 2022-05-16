#version 150

uniform sampler2D Sampler0;

uniform vec4 ColorModulator;

in vec4 vertexColor;
noperspective in vec2 texCoord0;
in vec2 texCoord2;
in vec4 texProj0;
in vec4 normal;

out vec4 fragColor;

void main() {
    vec2 cord = (texCoord0 + vec2(1.0, 1.0)) / 2.0;
    vec2 tFrag = vec2(1.0 / 500.0);
    vec4 destColor = texture(Sampler0, cord);

    destColor *= 0.36;

    destColor += texture(Sampler0, cord + vec2(-1.0, 1.0) * tFrag) * 0.04;
    destColor += texture(Sampler0, cord + vec2(0.0, 1.0) * tFrag) * 0.04;
    destColor += texture(Sampler0, cord + vec2(1.0, 1.0) * tFrag) * 0.04;
    destColor += texture(Sampler0, cord + vec2(-1.0, 0.0) * tFrag) * 0.04;
    destColor += texture(Sampler0, cord + vec2(1.0, 0.0) * tFrag) * 0.04;
    destColor += texture(Sampler0, cord + vec2(-1.0, -1.0) * tFrag) * 0.04;
    destColor += texture(Sampler0, cord + vec2(0.0, -1.0) * tFrag) * 0.04;
    destColor += texture(Sampler0, cord + vec2(1.0, -1.0) * tFrag) * 0.04;
    destColor += texture(Sampler0, cord + vec2(-2.0, 2.0) * tFrag) * 0.02;
    destColor += texture(Sampler0, cord + vec2(-1.0, 2.0) * tFrag) * 0.02;
    destColor += texture(Sampler0, cord + vec2(0.0, 2.0) * tFrag) * 0.02;
    destColor += texture(Sampler0, cord + vec2(1.0, 2.0) * tFrag) * 0.02;
    destColor += texture(Sampler0, cord + vec2(2.0, 2.0) * tFrag) * 0.02;
    destColor += texture(Sampler0, cord + vec2(-2.0, 1.0) * tFrag) * 0.02;
    destColor += texture(Sampler0, cord + vec2(2.0, 1.0) * tFrag) * 0.02;
    destColor += texture(Sampler0, cord + vec2(-2.0, 0.0) * tFrag) * 0.02;
    destColor += texture(Sampler0, cord + vec2(2.0, 0.0) * tFrag) * 0.02;
    destColor += texture(Sampler0, cord + vec2(-2.0, -1.0) * tFrag) * 0.02;
    destColor += texture(Sampler0, cord + vec2(2.0, -1.0) * tFrag) * 0.02;
    destColor += texture(Sampler0, cord + vec2(-2.0, -2.0) * tFrag) * 0.02;
    destColor += texture(Sampler0, cord + vec2(-1.0, -2.0) * tFrag) * 0.02;
    destColor += texture(Sampler0, cord + vec2(0.0, -2.0) * tFrag) * 0.02;
    destColor += texture(Sampler0, cord + vec2(1.0, -2.0) * tFrag) * 0.02;
    destColor += texture(Sampler0, cord + vec2(2.0, -2.0) * tFrag) * 0.02;

    fragColor = vec4(destColor.rgb, 1.0);
}
