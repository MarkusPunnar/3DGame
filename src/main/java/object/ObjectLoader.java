package object;

import model.data.ModelData;
import model.data.VertexData;
import org.joml.Vector2f;
import org.joml.Vector3f;

import java.io.IOException;
import java.net.URISyntaxException;
import java.net.URL;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.List;

public class ObjectLoader {

    public static ModelData loadObjectModel(String fileName) throws URISyntaxException, IOException {
        URL location = ObjectLoader.class.getClassLoader().getResource("models/" + fileName + ".obj");
        if (location == null) {
            throw new IllegalArgumentException("Object model file not found");
        }
        List<String> lines = Files.readAllLines(Paths.get(location.toURI()));
        List<VertexData> vertices = new ArrayList<>();
        List<Vector3f> normals = new ArrayList<>();
        List<Integer> indices = new ArrayList<>();
        List<Vector2f> textures = new ArrayList<>();
        boolean read = false;
        for (String line : lines) {
            String[] tokens = line.split(" ");
            String type = tokens[0];
            if (!read) {
                switch (type) {
                    case "v":
                        Vector3f vertexVector = new Vector3f(Float.parseFloat(tokens[1]), Float.parseFloat(tokens[2]), Float.parseFloat(tokens[3]));
                        VertexData vertexData = new VertexData(vertices.size(), vertexVector);
                        vertices.add(vertexData);
                        break;
                    case "vn":
                        normals.add(new Vector3f(Float.parseFloat(tokens[1]), Float.parseFloat(tokens[2]), Float.parseFloat(tokens[3])));
                        break;
                    case "vt":
                        textures.add(new Vector2f(Float.parseFloat(tokens[1]), Float.parseFloat(tokens[2])));
                        break;
                    case "f":
                        read = true;
                        break;
                    default:
                }
            }
            if (line.startsWith("f")) {
                String[] vertex1 = tokens[1].split("/");
                String[] vertex2 = tokens[2].split("/");
                String[] vertex3 = tokens[3].split("/");
                processVertex(vertex1, indices, vertices);
                processVertex(vertex2, indices, vertices);
                processVertex(vertex3, indices, vertices);
            }
        }
        removeUnusedVertices(vertices);
        float[] verticesArray = new float[vertices.size() * 3];
        float[] texturesArray = new float[vertices.size() * 2];
        float[] normalsArray = new float[vertices.size() * 3];
        float furthest = convertDataToArrays(vertices, textures, normals, verticesArray, texturesArray, normalsArray);
        int[] indicesArray = convertIndicesListToArray(indices);
        return new ModelData(verticesArray, texturesArray, normalsArray, indicesArray, furthest);
    }

    private static void processVertex(String[] vertexData, List<Integer> indices, List<VertexData> vertices) {
        int vertexIndex = Integer.parseInt(vertexData[0]) - 1;
        VertexData currentVertexData = vertices.get(vertexIndex);
        int textureIndex = Integer.parseInt(vertexData[1]) - 1;
        int normalIndex = Integer.parseInt(vertexData[2]) - 1;
        if (!currentVertexData.isSet()) {
            currentVertexData.setNormalIndex(normalIndex);
            currentVertexData.setTextureIndex(textureIndex);
            indices.add(vertexIndex);
        } else {
            handleProcessedVertex(currentVertexData, textureIndex, normalIndex, indices, vertices);
        }
    }

    private static void removeUnusedVertices(List<VertexData> vertices) {
        for (VertexData vertexData : vertices) {
            if (!vertexData.isSet()) {
                vertexData.setTextureIndex(0);
                vertexData.setNormalIndex(0);
            }
        }
    }

    private static void handleProcessedVertex(VertexData previousVertexData, int newTextureIndex, int newNormalIndex, List<Integer> indices, List<VertexData> vertices) {
        if (previousVertexData.hasSameTextureAndNormal(newTextureIndex, newNormalIndex)) {
            indices.add(previousVertexData.getIndex());
        } else {
            VertexData anotherVertexData = previousVertexData.getDuplicateVertexData();
            if (anotherVertexData != null) {
                handleProcessedVertex(anotherVertexData, newTextureIndex, newNormalIndex, indices, vertices);
            } else {
                VertexData duplicateVertexData = new VertexData(vertices.size(), previousVertexData.getPosition());
                duplicateVertexData.setTextureIndex(newTextureIndex);
                duplicateVertexData.setNormalIndex(newNormalIndex);
                previousVertexData.setDuplicateVertexData(duplicateVertexData);
                vertices.add(duplicateVertexData);
                indices.add(duplicateVertexData.getIndex());
            }
        }
    }

    private static float convertDataToArrays(List<VertexData> vertices, List<Vector2f> textures, List<Vector3f> normals, float[] verticesArray, float[] texturesArray, float[] normalsArray) {
        float furthestPoint = 0;
        for (int i = 0; i < vertices.size(); i++) {
            VertexData currentVertexData = vertices.get(i);
            if (currentVertexData.getLength() > furthestPoint) {
                furthestPoint = currentVertexData.getLength();
            }
            Vector3f position = currentVertexData.getPosition();
            Vector2f textureCoord = textures.get(currentVertexData.getTextureIndex());
            Vector3f normalVector = normals.get(currentVertexData.getNormalIndex());
            verticesArray[i * 3] = position.x;
            verticesArray[i * 3 + 1] = position.y;
            verticesArray[i * 3 + 2] = position.z;
            texturesArray[i * 2] = textureCoord.x;
            texturesArray[i * 2 + 1] = 1 - textureCoord.y;
            normalsArray[i * 3] = normalVector.x;
            normalsArray[i * 3 + 1] = normalVector.y;
            normalsArray[i * 3 + 2] = normalVector.z;
        }
        return furthestPoint;
    }

    private static int[] convertIndicesListToArray(List<Integer> indices) {
        int[] indicesArray = new int[indices.size()];
        for (int i = 0; i < indicesArray.length; i++) {
            indicesArray[i] = indices.get(i);
        }
        return indicesArray;
    }
}
