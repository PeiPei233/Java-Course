final public class UnmodifiableMatrix {
    private final Matrix matrix;    // final to make it immutable

    public UnmodifiableMatrix(int rows, int cols) {
        matrix = new Matrix(rows, cols);
    }

    public UnmodifiableMatrix(double[][] data) {
        // make a copy of the data
        matrix = new Matrix(data);
    }

    public UnmodifiableMatrix(Matrix matrix) {
        // make a copy of the matrix to make it immutable
        this.matrix = new Matrix(matrix);
    }

    public UnmodifiableMatrix(UnmodifiableMatrix m) {
        // no need to make a copy of the matrix since it is already immutable
        this.matrix = m.matrix;
    }

    // get the number of rows
    public int rows() {
        return matrix.rows();
    }

    // get the number of columns
    public int cols() {
        return matrix.cols();
    }

    // get the (i, j)-th element
    public double get(int i, int j) {
        return matrix.get(i, j);
    }

    // No set method

    // add two matrices
    public UnmodifiableMatrix add(UnmodifiableMatrix m) {
        return new UnmodifiableMatrix(matrix.add(m.matrix));
    }

    // subtract two matrices
    public UnmodifiableMatrix sub(UnmodifiableMatrix m) {
        return new UnmodifiableMatrix(matrix.sub(m.matrix));
    }

    // multiply a matrix by a scalar
    public UnmodifiableMatrix mul(double scalar) {
        return new UnmodifiableMatrix(matrix.mul(scalar));
    }

    // multiply two matrices
    public UnmodifiableMatrix mul(UnmodifiableMatrix m) {
        return new UnmodifiableMatrix(matrix.mul(m.matrix));
    }

    // get the transpose of the matrix
    public UnmodifiableMatrix transpose() {
        return new UnmodifiableMatrix(matrix.transpose());
    }

    @Override
    public String toString() {
        return matrix.toString();
    }
}
