package com.rb2750.lwjgl.util;

import org.lwjgl.opengl.GL11;

import java.awt.*;

import static org.lwjgl.opengl.GL11.*;

public class Util {
    public static void drawSquare(double x1, double y1, double width, double height) {
        glColor3d(1, 0, 0);

        glBegin(GL_POLYGON);

        glVertex2d(x1, y1);
        glVertex2d(x1 + width, y1);
        glVertex2d(x1 + width, y1 + height);
        glVertex2d(x1, y1 + height);

        glEnd();
    }

    public static void drawCube(float x, float y, float z, float w, float h, float l) {
//        glLoadIdentity();                 // Reset the model-view matrix
//        glTranslatef(x, y, -150.0f);  // Move right and into the screen
        glEnable(GL_DEPTH_TEST);
        glEnable(GL_DEPTH_BUFFER_BIT);
        GL11.glDepthFunc(GL11.GL_LEQUAL);
        GL11.glHint(GL11.GL_PERSPECTIVE_CORRECTION_HINT, GL11.GL_NICEST);
        glBegin(GL_QUADS);                // Begin drawing the color cube with 6 quads
        // Front face
        glColor3f(0f, 0f, 1f);     // Green
        glVertex3f(x, y, z);
        glVertex3f(x + w, y, z);
        glVertex3f(x + w, y + h, z);
        glVertex3f(x, y + h, z);

        // Top face
        glColor3f(1.0f, 0.5f, 0.0f);     // Orange
        glVertex3f(x, y + h, z);
        glVertex3f(x, y + h, z + l);
        glVertex3f(x + w, y + h, z + l);
        glVertex3f(x + w, y + h, z);
//
//        // Right face
        glColor3f(1.0f, 0.0f, 0.0f);     // Red
        glVertex3f(x + w, y, z); // 2
        glVertex3f(x + w, y, z + l); // 1
        glVertex3f(x + w, y + h, z + l); // 4
        glVertex3f(x + w, y + h, z); // 3
//
//        // Back face (z = -1.0f)
//        glColor3f(1.0f, 1.0f, 0.0f);     // Yellow
//        glVertex3f(100f, -100f, -100f);
//        glVertex3f(-100f, -100f, -100f);
//        glVertex3f(-100f, 100f, -100f);
//        glVertex3f(100f, 100f, -100f);
//
//        // Left face (x = -1.0f)
//        glColor3f(0.0f, 0.0f, 1.0f);     // Blue
//        glVertex3f(-100f, 100f, 100f);
//        glVertex3f(-100f, 100f, -100f);
//        glVertex3f(-100f, -100f, -100f);
//        glVertex3f(-100f, -100f, 100f);
//
//        // Right face (x = 1.0f)
//        glColor3f(1.0f, 0.0f, 1.0f);     // Magenta
//        glVertex3f(100f, 100f, -100f);
//        glVertex3f(100f, 100f, 100f);
//        glVertex3f(100f, -100f, 100f);
//        glVertex3f(100f, -100f, -100f);
        glEnd();  // End of drawing color-cube
    }


    private static float clamp(float x, float lower, float upper) {
        return Math.max(lower, Math.min(upper, x));
    }

    public static Point getNearestPointInPerimeter(Rectangle rect, float x, float y) {
        float l = (float) rect.getX(), t = (float) rect.getY(), w = (float) rect.getWidth(), h = (float) rect.getHeight();

        float r = l + w, b = t + h;

        x = clamp(x, l, r);
        y = clamp(y, t, b);

        float dl = Math.abs(x - l), dr = Math.abs(x - r), dt = Math.abs(y - t), db = Math.abs(y - b);
        float m = Math.min(Math.min(Math.min(dl, dr), dt), db);

        if (m == dt) return new Point(x, t);
        if (m == db) return new Point(x, b);
        if (m == dl) return new Point(l, y);
        return new Point(r, y);
    }
}
