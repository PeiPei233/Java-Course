import java.util.Arrays;

public class Vector {
    private double[] data;

    public Vector(int size) {
        data = new double[size];
    }

    public Vector(double[] data) {
        // make a copy of the data
        this.data = Arrays.copyOf(data, data.length);
    }

    public Vector(Vector v) {
        this(v.data);
    }

    // get the size of the vector
    public int size() {
        return data.length;
    }

    // get the i-th element
    public double get(int i) {
        return data[i];
    }

    // set the i-th element
    public void set(int i, double value) {
        data[i] = value;
    }

    // add two vectors
    public Vector add(Vector v) {
        if (size() != v.size()) {
            throw new IllegalArgumentException("Vector sizes do not match");
        } else {
            Vector result = new Vector(size());
            for (int i = 0; i < size(); i++) {
                result.set(i, get(i) + v.get(i));
            }
            return result;
        }
    }

    // subtract two vectors
    public Vector sub(Vector v) {
        if (size() != v.size()) {
            throw new IllegalArgumentException("Vector sizes do not match");
        } else {
            Vector result = new Vector(size());
            for (int i = 0; i < size(); i++) {
                result.set(i, get(i) - v.get(i));
            }
            return result;
        }
    }

    // multiply a vector by a scalar
    public Vector mul(double scalar) {
        Vector result = new Vector(size());
        for (int i = 0; i < size(); i++) {
            result.set(i, get(i) * scalar);
        }
        return result;
    }

    // compute the dot product of two vectors
    public double dot(Vector v) {
        if (size() != v.size()) {
            throw new IllegalArgumentException("Vector sizes do not match");
        } else {
            double result = 0;
            for (int i = 0; i < size(); i++) {
                result += get(i) * v.get(i);
            }
            return result;
        }
    }
    
    @Override
    public String toString() {
        return Arrays.toString(data);
    }
}