package com.rb2750.lwjgl.util;

import static java.lang.Math.*;
import org.apache.commons.lang3.tuple.Pair;
import org.apache.commons.lang3.tuple.Triple;
import org.lwjgl.opengl.GL11;
import static org.lwjgl.opengl.GL11.*;

import java.awt.geom.Rectangle2D;
import java.util.ArrayList;
import java.util.List;

public class Util {
    public static void drawSquare(double x, double y, double width, double height) {
        glTranslated(x, y, 0);

        glBegin(GL_POLYGON);

        glVertex2d(0, 0);
        glVertex2d(width, 0);
        glVertex2d(width, height);
        glVertex2d(0, height);

        glEnd();
        glLoadIdentity();
    }

    public static void drawCube(double x, double y, double z, double w, double h, double l) {
//        glLoadIdentity();                 // Reset the model-view matrix
        glTranslated(x, y, 0);  // Move right and into the screen
        glEnable(GL_DEPTH_TEST);
        glEnable(GL_DEPTH_BUFFER_BIT);
        GL11.glDepthFunc(GL11.GL_LEQUAL);
        GL11.glHint(GL11.GL_PERSPECTIVE_CORRECTION_HINT, GL11.GL_NICEST);
        glBegin(GL_QUADS);                // Begin drawing the color cube with 6 quads
        // Front face
        glColor3d(0f, 0f, 1f);     // Green
        glVertex3d(0, 0, 0);
        glVertex3d(w, 0, 0);
        glVertex3d(w, h, 0);
        glVertex3d(0, h, 0);

        // Top face
        glColor3d(1.0f, 0.5f, 0.0f);     // Orange
        glVertex3d(0, h, 0);
        glVertex3d(0, h, l);
        glVertex3d(w, h, l);
        glVertex3d(w, h, 0);
//
//        // Right face
        glColor3d(1.0f, 0.0f, 0.0f);     // Red
        glVertex3d(w, 0, 0); // 2
        glVertex3d(w, 0, l); // 1
        glVertex3d(w, h, l); // 4
        glVertex3d(w, h, 0); // 3
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
        glLoadIdentity();
    }

    public static boolean checkPointInCircle(double x, double y, double centerX, double centerY, double radius, double startAngle, double endAngle) {
        double radiusSquared = radius * radius;
        double startVectorX = cos(startAngle);
        double startVectorY = sin(startAngle);
        double endVectorX = cos(endAngle);
        double endVectorY = sin(endAngle);
        double distanceX = x - centerX;
        double distanceY = y - centerY;

        if (((distanceX * distanceX) + (distanceY * distanceY)) > radiusSquared) return false;
        if (((distanceX * -startVectorY) + (distanceY * startVectorX)) < 0.0f) return false;
        if (((distanceX * -endVectorY) + (distanceY * endVectorX)) > 0.0f) return false;

        return true;
    }

    public static boolean checkPoint(double x, double y, double centerX, double centerY, double radius, double startAngle, double endAngle) {
        x = centerX - x;
        y = centerY - y;
        double polarradius = Math.sqrt(x * x + y * y);
        double Angle = Math.atan(y / x);
        return (Angle >= startAngle && Angle <= endAngle && polarradius < radius);
    }

    public static long getTime() {
        return System.nanoTime() / 1000000;
    }

    public static List<Pair<Double, Double>> drawCircle(double x, double y, double radius) {
        return drawCircle(x, y, radius, 2);
    }

    public static List<Pair<Double, Double>> drawCircle(double x, double y, double radius, double radians) {
        glTranslated(x, y, 0);

        int triangles = 50;
        double pi = radians * 3.14159f;

        List<Pair<Double, Double>> trianglesList = new ArrayList<>();

        glBegin(GL_TRIANGLE_FAN);
        glVertex2d(0, 0); // origin
        for (int i = 0; i <= triangles; i++) {
            glVertex2d(radius * cos(i * pi / triangles),
                    radius * sin(i * pi / triangles));
            trianglesList.add(Pair.of(x + radius * cos(i * pi / triangles), y + radius * sin(i * pi / triangles)));
        }
        glEnd();
        glLoadIdentity();
        return trianglesList;
    }

    public static void glColor(double red, double green, double blue, double alpha) {
        glColor4d(red / 255, green / 255, blue / 255, alpha / 255);
    }

    public static Rectangle2D getRectangle(Location location, Size size) {
        return new Rectangle2D.Double(location.getX(), location.getY(), size.getWidth(), size.getHeight());
    }

    public static Rectangle2D getRectangle(Location location, double width, double height) {
        return new Rectangle2D.Double(location.getX(), location.getY(), width, height);
    }

    public static Rectangle2D getRectangle(double x, double y, Size size) {
        return new Rectangle2D.Double(x, y, size.getWidth(), size.getHeight());
    }

    private static double clamp(double x, double lower, double upper) {
        return Math.max(lower, Math.min(upper, x));
    }

    public static Point getNearestPointInPerimeter(Rectangle2D rect, double x, double y) {
        double l = rect.getX(), t = rect.getY(), w = rect.getWidth(), h = rect.getHeight();

        double r = l + w, b = t + h;

        x = clamp(x, l, r);
        y = clamp(y, t, b);

        double dl = Math.abs(x - l), dr = Math.abs(x - r), dt = Math.abs(y - t), db = Math.abs(y - b);
        double m = Math.min(Math.min(Math.min(dl, dr), dt), db);

        if (m == dt) return new Point(x, t);
        if (m == db) return new Point(x, b);
        if (m == dl) return new Point(l, y);
        return new Point(r, y);
    }
}
