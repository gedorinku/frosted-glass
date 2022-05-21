#version 150
#extension GL_ARB_explicit_attrib_location : enable

#moj_import <projection.glsl>

layout (location = 0) in vec3 Position;
layout (location = 1) in vec4 Color;
layout (location = 2) in vec2 UV0;
layout (location = 3) in vec2 UV2;
layout (location = 4) in vec3 Normal;

uniform mat4 ModelViewMat;
uniform mat4 ProjMat;
uniform vec3 ChunkOffset;

out vec4 vertexColor;
noperspective out vec2 frameBufferCoord;
out vec2 texCoord0;
out vec2 texCoord2;
out vec4 texProj0;
out vec4 normal;

void main() {
    gl_Position = ProjMat * ModelViewMat * vec4(Position + ChunkOffset, 1.0);

    vertexColor = Color;
    frameBufferCoord = gl_Position.xy / gl_Position.w;
    texCoord0 = UV0;
    texCoord2 = UV2;
    texProj0 = projection_from_position(gl_Position);
    normal = ProjMat * ModelViewMat * vec4(Normal, 0.0);
}
