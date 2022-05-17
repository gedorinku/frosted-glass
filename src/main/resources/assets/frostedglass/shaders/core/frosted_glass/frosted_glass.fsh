#version 150

uniform sampler2D Sampler0;
uniform sampler2D Sampler1;

uniform vec4 ColorModulator;

in vec4 vertexColor;
noperspective in vec2 frameBufferCoord;
in vec2 texCoord0;
in vec2 texCoord2;
in vec4 texProj0;
in vec4 normal;

out vec4 fragColor;

const float PI = 3.1415926535897932384626433832795;
const float STD_DEV = 3.0;

float gaus(vec2 pos) {
    float r = length(pos);

    return pow(sqrt(2 * PI * STD_DEV * STD_DEV), -2)  * exp(- r * r / (2 * STD_DEV * STD_DEV));
}

void main() {
    vec2 cord = (frameBufferCoord + vec2(1.0, 1.0)) / 2.0;
    vec2 tFrag = vec2(1.0 / 500.0);
    vec4 destColor = vec4(0.0);

    const int RANGE = int(3 * STD_DEV);
    for (int dx = -RANGE; dx <= RANGE; dx++) {
        for (int dy = -RANGE; dy <= RANGE; dy++) {
            destColor += texture(Sampler1, cord + vec2(dx, dy) * tFrag) * gaus(vec2(dx, dy));
        }
    }

    vec4 glassColor = texture(Sampler0, texCoord0) * vertexColor * ColorModulator;
    fragColor = vec4(destColor.rgb * (1.0 - glassColor.a) + glassColor.rgb * glassColor.a, 1.0);
}
