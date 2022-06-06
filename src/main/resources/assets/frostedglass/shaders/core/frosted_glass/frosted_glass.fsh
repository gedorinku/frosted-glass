#version 150

uniform sampler2D Sampler0;
uniform sampler2D Sampler1;

uniform vec4 ColorModulator;

uniform ivec2 WindowSize;
uniform int BlurDirection;

const int VERTICAL_AND_HORIZONTAL = 0;
const int VERTICAL = 1;
const int HORIZONTAL = 2;

in vec4 vertexColor;
noperspective in vec2 frameBufferCoord;
in vec2 texCoord0;
in vec2 texCoord2;
in vec4 texProj0;
in vec4 normal;

out vec4 fragColor;

const float PI = 3.1415926535897932384626433832795;
float stdDev = 12.0 / ((2560 + 1440) / 2.0) * ((WindowSize.x + WindowSize.y) / 2.0);

float gaus(vec2 pos) {
    float r = length(pos);

    return pow(sqrt(2 * PI * stdDev * stdDev), -2) * exp(- r * r / (2 * stdDev * stdDev));
}

void main() {
    vec2 cord = (frameBufferCoord + vec2(1.0, 1.0)) / 2.0;
    vec2 tFrag = vec2(1.0) / WindowSize;
    vec4 destColor = vec4(0.0);

    int radius = int(3 * stdDev);
    int diamerter = 2 * radius;
    // 手に持ったときや GUI に表示するときだけガウシアンブラーを2回に分ける実装ができていないので、とりあえず雑に品質を落としている。
    int sampleSize = BlurDirection == VERTICAL_AND_HORIZONTAL ? diamerter / 4 : diamerter / 2;
    float step = float(diamerter) / max(1.0, sampleSize - 1.0);
    float sum = 0.0;

    if (BlurDirection == VERTICAL_AND_HORIZONTAL) {
        for (int i = 0; i < sampleSize; i++) {
            for (int j = 0; j < sampleSize; j++) {
                vec2 d = vec2(-radius + i * step, -radius + j * step);
                float g = gaus(d);
                sum += g;
                destColor += texture(Sampler1, cord + d * tFrag) * g;
            }
        }
    } else {
        vec2 dir = BlurDirection == VERTICAL ? vec2(0.0, 1.0) : vec2(1.0, 0.0);

        for (int i = 0; i < sampleSize; i++) {
            vec2 d = (-radius + i * step) * dir;
            float g = gaus(d);
            sum += g;
            destColor += texture(Sampler1, cord + d * tFrag) * g;
        }
    }

    destColor *= 1.0 / sum;

    vec4 glassColor = texture(Sampler0, texCoord0) * vertexColor * ColorModulator;
    fragColor = vec4(destColor.rgb * (1.0 - glassColor.a) + glassColor.rgb * glassColor.a, 1.0);
}
