package com.rb2750.lwjgl.graphics.postprocess;

import com.rb2750.lwjgl.Main;
import lombok.Getter;

import java.nio.ByteBuffer;

import static org.lwjgl.opengl.GL11.*;
import static org.lwjgl.opengl.GL12.*;
import static org.lwjgl.opengl.GL14.*;
import static org.lwjgl.opengl.GL30.*;

public class FBO
{
    public static final int NONE = 0;
    public static final int DEPTH_TEXTURE = 1;
    public static final int DEPTH_RENDER_BUFFER = 2;

    private final int width;
    private final int height;

    private int frameBuffer;

    @Getter
    private int colourTexture;
    @Getter
    private int depthTexture;

    private int depthBuffer;
    private int colourBuffer;

    public FBO(int width, int height, int depthType)
    {
        this.width = width;
        this.height = height;
        initFrameBuffer(depthType);
    }

    public void cleanUp()
    {
        glDeleteFramebuffers(frameBuffer);
        glDeleteTextures(colourTexture);
        glDeleteTextures(depthTexture);
        glDeleteRenderbuffers(depthBuffer);
        glDeleteRenderbuffers(colourBuffer);
    }

    public void bind()
    {
        glBindFramebuffer(GL_DRAW_FRAMEBUFFER, frameBuffer);
        glViewport(0, 0, width, height);
    }

    public void unbind()
    {
        glBindFramebuffer(GL_FRAMEBUFFER, 0);
        glViewport(0, 0, Main.getGameWidth(), Main.getGameHeight());
    }

    public void bindRead()
    {
        glBindTexture(GL_TEXTURE_2D, 0);
        glBindFramebuffer(GL_READ_FRAMEBUFFER, frameBuffer);
        glReadBuffer(GL_COLOR_ATTACHMENT0);
    }

    private void initFrameBuffer(int depthType)
    {
        createFrameBuffer();
        createTextureAttachment();

        switch(depthType)
        {
            case DEPTH_RENDER_BUFFER:
                createDepthBufferAttachment();
                break;
            case DEPTH_TEXTURE:
                createDepthTextureAttachment();
                break;
            default:
                break;
        }

        unbind();
    }

    private void createFrameBuffer()
    {
        frameBuffer = glGenFramebuffers();
        glBindFramebuffer(GL_FRAMEBUFFER, frameBuffer);
        glDrawBuffer(GL_COLOR_ATTACHMENT0);
    }

    private void createTextureAttachment()
    {
        colourTexture = glGenTextures();
        glBindTexture(GL_TEXTURE_2D, colourTexture);
        glTexImage2D(GL_TEXTURE_2D, 0, GL_RGBA8, width, height, 0, GL_RGBA, GL_UNSIGNED_BYTE, (ByteBuffer)null);
        glTexParameteri(GL_TEXTURE_2D, GL_TEXTURE_MAG_FILTER, GL_LINEAR);
        glTexParameteri(GL_TEXTURE_2D, GL_TEXTURE_MIN_FILTER, GL_LINEAR);
        glTexParameteri(GL_TEXTURE_2D, GL_TEXTURE_WRAP_S, GL_CLAMP_TO_EDGE);
        glTexParameteri(GL_TEXTURE_2D, GL_TEXTURE_WRAP_T, GL_CLAMP_TO_EDGE);
        glFramebufferTexture2D(GL_FRAMEBUFFER, GL_COLOR_ATTACHMENT0, GL_TEXTURE_2D, colourTexture, 0);
    }

    private void createDepthTextureAttachment()
    {
        depthTexture = glGenTextures();
        glBindTexture(GL_TEXTURE_2D, depthTexture);
        glTexImage2D(GL_TEXTURE_2D, 0, GL_DEPTH_COMPONENT24, width, height, 0, GL_DEPTH_COMPONENT, GL_FLOAT, (ByteBuffer)null);
        glTexParameteri(GL_TEXTURE_2D, GL_TEXTURE_MAG_FILTER, GL_LINEAR);
        glTexParameteri(GL_TEXTURE_2D, GL_TEXTURE_MIN_FILTER, GL_LINEAR);
        glFramebufferTexture2D(GL_FRAMEBUFFER, GL_DEPTH_ATTACHMENT, GL_TEXTURE_2D, depthTexture, 0);
    }

    private void createDepthBufferAttachment()
    {
        depthBuffer = glGenRenderbuffers();
        glBindRenderbuffer(GL_RENDERBUFFER, depthBuffer);
        glRenderbufferStorage(GL_RENDERBUFFER, GL_DEPTH_COMPONENT24, width, height);
        glFramebufferRenderbuffer(GL_FRAMEBUFFER, GL_DEPTH_ATTACHMENT, GL_RENDERBUFFER, depthBuffer);
    }
}
