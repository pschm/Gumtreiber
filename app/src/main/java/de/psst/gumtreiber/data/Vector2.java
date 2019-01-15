package de.psst.gumtreiber.data;

/**
 * Representation of a point in 2D-space as a vector
 */
public class Vector2 {

    public float x, y;

    public Vector2() {
    }

    public Vector2(float x, float y) {
        this.x = x;
        this.y = y;
    }

    @Override
    public String toString() {
        return "(" + x + ", " + y + ")";
    }

    public Vector2 add(float x, float y) {
        this.x += x;
        this.y += y;
        return this;
    }

    public Vector2 add(Vector2 v) {
        x += v.x;
        y += v.y;

        return this;
    }

    public static Vector2 add(Vector2 v1, Vector2 v2) {
        return new Vector2(v1.x + v2.x, v1.y + v2.y);
    }

    public Vector2 sub(float x, float y) {
        this.x -= x;
        this.y -= y;
        return this;
    }

    public Vector2 sub(Vector2 v) {
        x -= v.x;
        y -= v.y;
        return this;
    }

    public static Vector2 sub(Vector2 v1, Vector2 v2) {
        return new Vector2(v1.x - v2.x, v1.y - v2.y);
    }


    public float distance(Vector2 v) {
        float x_d = v.x - x;
        float y_d = v.y - y;
        return (float) Math.sqrt(x_d * x_d + y_d * y_d);
    }

    public float magnitude() {
        return (float) Math.sqrt(x * x + y * y);
    }

    public Vector2 normalize() {
        float length = magnitude();
        if (length != 0) {
            x /= length;
            y /= length;
        }
        return this;
    }


    public boolean equals(Vector2 v) {
        return x == v.x && y == v.y;
    }

    /**
     * @return the angle in degrees of this vector (point) relative to the x-axis. Angles are towards the positive y-axis
     * (typically counter-clockwise) and between 0 and 360.
     */
    public float angle() {
        float angle = (float) Math.toDegrees(Math.atan2(y, x));
        if (angle < 0) angle += 360;
        return angle;
    }
}
