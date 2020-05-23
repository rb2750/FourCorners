package com.rb2750.lwjgl.graphics;

import com.rb2750.lwjgl.Main;
import com.rb2750.lwjgl.entities.Camera;
import com.rb2750.lwjgl.maths.MatrixUtil;
import com.rb2750.lwjgl.networking.client.Client;
import com.rb2750.lwjgl.networking.server.Server;
import com.rb2750.lwjgl.serialization.SerialDatabase;
import com.rb2750.lwjgl.serialization.SerialField;
import com.rb2750.lwjgl.serialization.SerialObject;
import com.rb2750.lwjgl.util.Location;
import com.rb2750.lwjgl.util.OBJLoader;
import com.rb2750.lwjgl.util.Size;
import lombok.Getter;
import lombok.Setter;
import org.dyn4j.dynamics.Body;
import org.joml.Matrix4f;
import org.joml.Vector3f;
import org.joml.Vector4f;

import java.util.ArrayList;
import java.util.List;

import static org.lwjgl.opengl.GL11.*;

public abstract class DisplayObject {
    private VertexArray mesh;
    @Getter
    protected Texture texture;
    protected Shader shader;
    protected String texturePath; // Set in child objects
    protected float[] vertices;
    protected int[] indices;
    protected float[] tcs;
    protected float[] normals;
    public float layer = 0.0f;
    @Getter
    protected Vector4f baseColour;
    @Getter
    protected Location location;
    @Getter
    public Size size;
    @Getter
    private Vector3f rotation = new Vector3f(0, 0, 0);
    public float borderSize = 0;
    public Vector4f borderColour = new Vector4f();
    public boolean invisible = false;
    @Getter
    private Vector4f lastColor;
    @Getter
    @Setter
    public boolean absoluteLocation = false;
    @Getter
    @Setter
    protected Body body;

    public DisplayObject(Location location, Size size, Shader shader, Vector4f baseColour) {
        this.location = location;
        this.size = size;
        this.shader = shader;
        this.baseColour = baseColour;
    }

    public DisplayObject(Size size, Shader shader, Vector4f baseColour) {
        this.size = size;
        this.shader = shader;
        this.baseColour = baseColour;
    }

    public DisplayObject setBaseColour(Vector4f baseColour) {
        this.baseColour = baseColour;
        lastColor = baseColour;
        return this;
    }

    /**
     * Move the entity to a specific location while ignoring any objects that are in the way
     *
     * @param location Location to move to
     */
    public void teleport(Location location) {
        if (body != null) body.translate(location.getX(), location.getY());
        this.location = location;
    }

    /**
     * Prepare for safe deletion of an object
     */
    public void cleanUp() {
        mesh.cleanUp();
        texture.cleanUp();
    }

    /**
     * Takes the set vertices and indices and calculates normals for each.
     *
     * @return Array of normals
     * @throws IllegalArgumentException If vertices and indices aren't set.
     */
    protected float[] calcNormals() {
        if (vertices == null || indices == null) {
            throw new IllegalArgumentException("Vertices and indices must be set before calculating normals.");
        }

        List<Vector3f> verticesList = new ArrayList<Vector3f>();

        for (int i = 0; i < vertices.length; i += 3) {
            verticesList.add(new Vector3f(vertices[i], vertices[i + 1], vertices[i + 2]));
        }

        List<Vector3f> normalsList = new ArrayList<Vector3f>();

        for (int i = 0; i < indices.length; i += 3) {
            int i0 = indices[i];
            int i1 = indices[i + 1];
            int i2 = indices[i + 2];

            Vector3f v1 = verticesList.get(i1).sub(verticesList.get(i0));
            Vector3f v2 = verticesList.get(i2).sub(verticesList.get(i0));

            Vector3f normal = v1.cross(v2).normalize();

            normalsList.add(normal);
        }

        float[] resultingNormals = new float[normalsList.size() * 3];

        int normalPointer = 0;

        for (Vector3f normal : normalsList) {
            resultingNormals[normalPointer++] = normal.x;
            resultingNormals[normalPointer++] = normal.y;
            resultingNormals[normalPointer++] = normal.z;
        }

        return resultingNormals;
    }

    /**
     * Render the object using the camera
     *
     * @param camera    The camera to render the object to
     * @param clipPlane The clipping plane, meaning if the object is to far from the camera it won't be rendered
     */
    public void render(Camera camera, Vector4f clipPlane) {
        if (mesh == null || shader == null || camera == null || texture == null)
            return;

//        System.out.println(rotation);
        shader.enable();
//        shader.setUniformMat4f("ml_matrix", MatrixUtil.transformation(Conversions.odeVec3ToVec3f(body.getPosition()), Conversions.odeQuatToQuatF(body.getQuaternion()), new Vector3f(size.getWidth(), size.getHeight(), size.getWidth())));
        Matrix4f ml_matrix;
        Vector3f location = new Vector3f(this.location.getX(), this.location.getY(), layer);

        if (absoluteLocation) location.add(camera.getPosition());

//        if (body != null)
//            ml_matrix = MatrixUtil.transformation(new Vector3f((float) body.getTransform().getTranslationX(), (float) body.getTransform().getTranslationY(), layer), 0, 0, (float) body.getTransform().getRotation(), new Vector3f(size.getWidth(), size.getHeight(), size.getDepth()));
//        else
            ml_matrix = MatrixUtil.transformation(new Vector3f(location.x, location.y, layer), rotation.x, rotation.y, rotation.z, new Vector3f(size.getWidth(), size.getHeight(), size.getDepth()));
        shader.setUniformMat4f("ml_matrix", ml_matrix);
        shader.setUniformMat4f("vw_matrix", MatrixUtil.view(camera));

        if (shader != Shader.BASIC_TEX && shader != Shader.BASIC_COLOUR) {
            Vector3f colour = new Vector3f(baseColour.x, baseColour.y, baseColour.z);
            shader.setUniform3f("baseColour", colour);
            shader.setUniform1f("shineDamper", texture.getShineDamper());
            shader.setUniform1f("reflectivity", texture.getReflectivity());
            shader.setUniform3f("eyePos", camera.getPosition());
        }

        if (shader == Shader.BASIC_COLOUR) {
            glEnable(GL_BLEND);
            glBlendFunc(GL_SRC_ALPHA, GL_ONE_MINUS_SRC_ALPHA);
            shader.setBoolean("absoluteLocation", false/*absoluteLocation*/);
            shader.setUniform4f("colour", new Vector4f(baseColour).div(255, 255, 255, 255));
            shader.setUniform1f("borderSize", borderSize);
            shader.setUniform4f("borderColour", new Vector4f(borderColour).div(255, 255, 255, 255));

        }

        shader.setUniform4f("clipPlane", clipPlane);
        texture.bind();

        mesh.render();

        glDisable(GL_BLEND);
        shader.disable();
    }

    protected void createMesh() {
        Main.instance.runOnUIThread(() -> {
            mesh = new VertexArray(vertices, indices, tcs, normals);
            texture = new Texture(texturePath);
            texture.setReflectivity(0.5f);
        });
    }

    /**
     * Change the entities rotation to whatever is passed in as a parameter
     *
     * @param rotation The new rotation of the entity
     */
    public void setRotation(Vector3f rotation) {
        this.rotation = rotation;
        this.rotation.x %= 360;
        this.rotation.y %= 360;
        this.rotation.z %= 360;
    }

    /**
     * Rotate the entity relative to its current rotation
     *
     * @param rotation How much to rotate the entity
     */
    public void rotate(Vector3f rotation) {
        this.setRotation(new Vector3f(this.rotation).add(rotation));
    }

    /**
     * Create new mesh from an obj file
     *
     * @param filePath Path to the obj file
     */
    protected void createMesh(String filePath) {
        Main.instance.runOnUIThread(() -> {
            mesh = OBJLoader.loadOBJ(filePath);
            texture = new Texture(texturePath);
            texture.setReflectivity(0.5f);
        });
    }

    /**
     * Serializes {@link DisplayObject} with all its details.
     * @param name Name that should be given to the {@link SerialObject}.
     * @return {@link SerialObject} prepared to be put into a {@link SerialDatabase}.
     * @see SerialDatabase
     * @see SerialObject
     */
    public SerialObject serialize(String name)
    {
        SerialObject result = new SerialObject(name);

        result.addField(SerialField.createFloat("X", location.getX()));
        result.addField(SerialField.createFloat("Y", location.getY()));

        result.addField(SerialField.createFloat("W", size.width));
        result.addField(SerialField.createFloat("H", size.height));
        result.addField(SerialField.createFloat("D", size.depth));

        result.addField(SerialField.createFloat("RX", rotation.x));
        result.addField(SerialField.createFloat("RY", rotation.y));
        result.addField(SerialField.createFloat("RZ", rotation.z));

        return result;
    }

//    /**
//     * Serializes {@link DisplayObject} with limited details. Useful for networking where not all details are necessary.
//     * @param name Name that should be given to the {@link SerialObject}.
//     * @return {@link SerialObject} prepared to be put into a {@link SerialDatabase}.
//     * @see SerialDatabase
//     * @see SerialObject
//     * @see Client
//     * @see Server
//     */
//    public SerialObject serializeForNetwork(String name)
//    {
//        SerialObject result = new SerialObject(name);
//
//        // TODO
//
//        return result;
//    }

    protected void deserialize(SerialObject object)
    {
        // TODO
    }
}
