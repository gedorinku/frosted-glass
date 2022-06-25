#version 150
#extension GL_ARB_explicit_attrib_location : enable

#moj_import <light.glsl>
#moj_import <projection.glsl>

layout (location = 0) in vec3 Position;
layout (location = 1) in vec4 Color;
layout (location = 2) in vec2 UV0;
layout (location = 3) in ivec2 UV2;
layout (location = 4) in vec3 Normal;

uniform sampler2D Sampler2;

uniform mat4 ModelViewMat;
uniform mat4 ProjMat;
uniform vec3 ChunkOffset;

out vec4 vertexColor;
noperspective out vec2 frameBufferCoord;
out vec2 texCoord0;
out vec4 texProj0;
out vec4 normal;

void main() {
    gl_Position = ProjMat * ModelViewMat * vec4(Position + ChunkOffset, 1.0);

    vertexColor = Color * minecraft_sample_lightmap(Sampler2, UV2);
    frameBufferCoord = gl_Position.xy / gl_Position.w;
    texCoord0 = UV0;
    texProj0 = projection_from_position(gl_Position);
    normal = ProjMat * ModelViewMat * vec4(Normal, 0.0);
}
