public final class UnmodifiableVector {
    private final Vector vector;    // final to make it immutable

    public UnmodifiableVector(int size) {
        vector = new Vector(size);
    }

    public UnmodifiableVector(double[] data) {
        // make a copy of the data
        vector = new Vector(data);
    }
    
    public UnmodifiableVector(Vector vector) {
        // make a copy of the vector to make it immutable
        this.vector = new Vector(vector);
    }

    public UnmodifiableVector(UnmodifiableVector v) {
        // no need to make a copy of the vector since it is already immutable
        this.vector = v.vector;
    }

    // get the size of the vector
    public int size() {
        return vector.size();
    }

    // get the i-th element
    public double get(int i) {
        return vector.get(i);
    }

    // No set method

    // add two vectors
    public UnmodifiableVector add(UnmodifiableVector v) {
        return new UnmodifiableVector(vector.add(v.vector));
    }

    // subtract two vectors
    public UnmodifiableVector sub(UnmodifiableVector v) {
        return new UnmodifiableVector(vector.sub(v.vector));
    }

    // multiply a vector by a scalar
    public UnmodifiableVector mul(double scalar) {
        return new UnmodifiableVector(vector.mul(scalar));
    }

    // dot product of two vectors
    public double dot(UnmodifiableVector v) {
        return vector.dot(v.vector);
    }

    @Override
    public String toString() {
        return vector.toString();
    }
}
