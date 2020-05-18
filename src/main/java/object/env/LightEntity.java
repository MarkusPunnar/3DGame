package object.env;

import engine.model.TexturedModel;
import object.Entity;
import org.joml.Vector3f;

public class LightEntity extends Entity {

    private final Light light;

    private LightEntity(Builder builder) {
        super(builder);
        Vector3f lightPosition = new Vector3f(builder.position);
        lightPosition.x += builder.offsetX;
        lightPosition.y += builder.offsetY;
        lightPosition.z += builder.offsetZ;
        this.light = new Light(lightPosition, builder.colour, builder.attenuation);
    }

    public static class Builder extends Entity.Builder {

        private final Vector3f position;

        private Vector3f colour = new Vector3f(1);
        private Vector3f attenuation = new Vector3f(1, 0, 0);
        private float offsetX = 0;
        private float offsetY = 0;
        private float offsetZ = 0;

        public Builder(TexturedModel texturedModel, Vector3f position) {
            super(texturedModel, position);
            this.position = position;
        }

        public Builder colour(Vector3f colour) {
            this.colour = colour;
            return self();
        }

        public Builder attenuation(Vector3f attenuation) {
            this.attenuation = attenuation;
            return self();
        }

        public Builder offsetX(float offsetX) {
            this.offsetX = offsetX;
            return this;
        }

        public Builder offsetY(float offsetY) {
            this.offsetY = offsetY;
            return this;
        }

        public Builder offsetZ(float offsetZ) {
            this.offsetZ = offsetZ;
            return this;
        }

        public LightEntity build() {
            return new LightEntity(this);
        }

        @Override
        protected Builder self() {
            return this;
        }
    }

    public Light getLight() {
        return light;
    }
}
