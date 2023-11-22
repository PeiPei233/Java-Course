import java.util.Arrays;

public class Matrix {
    private double[][] data;

    public Matrix(int rows, int cols) {
        data = new double[rows][cols];
    }
    
    public Matrix(double[][] data) {
        this.data = new double[data.length][];
        int cols = data[0].length;
        for (int i = 0; i < data.length; i++) {
            this.data[i] = Arrays.copyOf(data[i], data[i].length);
            if (this.data[i].length != cols) {
                throw new IllegalArgumentException("Matrix rows have different lengths");
            }
        }
    }

    public Matrix(Matrix m) {
        this(m.data);
    }

    // get the number of rows
    public int rows() {
        return data.length;
    }

    // get the number of columns
    public int cols() {
        return data[0].length;
    }

    // get the (i, j)-th element
    public double get(int i, int j) {
        return data[i][j];
    }

    // set the (i, j)-th element
    public void set(int i, int j, double value) {
        data[i][j] = value;
    }

    // add two matrices
    public Matrix add(Matrix m) {
        if (rows() != m.rows() || cols() != m.cols()) {
            throw new IllegalArgumentException("Matrix sizes do not match");
        } else {
            Matrix result = new Matrix(rows(), cols());
            for (int i = 0; i < rows(); i++) {
                for (int j = 0; j < cols(); j++) {
                    result.set(i, j, get(i, j) + m.get(i, j));
                }
            }
            return result;
        }
    }

    // subtract two matrices
    public Matrix sub(Matrix m) {
        if (rows() != m.rows() || cols() != m.cols()) {
            throw new IllegalArgumentException("Matrix sizes do not match");
        } else {
            Matrix result = new Matrix(rows(), cols());
            for (int i = 0; i < rows(); i++) {
                for (int j = 0; j < cols(); j++) {
                    result.set(i, j, get(i, j) - m.get(i, j));
                }
            }
            return result;
        }
    }

    // multiply a matrix by a scalar
    public Matrix mul(double scalar) {
        Matrix result = new Matrix(rows(), cols());
        for (int i = 0; i < rows(); i++) {
            for (int j = 0; j < cols(); j++) {
                result.set(i, j, get(i, j) * scalar);
            }
        }
        return result;
    }

    // compute the dot product of two matrices
    public Matrix mul(Matrix m) {
        if (cols() != m.rows()) {
            throw new IllegalArgumentException("Matrix sizes do not match");
        } else {
            Matrix result = new Matrix(rows(), m.cols());
            for (int i = 0; i < rows(); i++) {
                for (int j = 0; j < m.cols(); j++) {
                    double sum = 0;
                    for (int k = 0; k < cols(); k++) {
                        sum += get(i, k) * m.get(k, j);
                    }
                    result.set(i, j, sum);
                }
            }
            return result;
        }
    }

    // compute the transpose of a matrix
    public Matrix transpose() {
        Matrix result = new Matrix(cols(), rows());
        for (int i = 0; i < rows(); i++) {
            for (int j = 0; j < cols(); j++) {
                result.set(j, i, get(i, j));
            }
        }
        return result;
    }

    @Override
    public String toString() {
        StringBuilder sb = new StringBuilder();
        sb.append("[");
        for (int i = 0; i < rows(); i++) {
            sb.append("[");
            for (int j = 0; j < cols(); j++) {
                sb.append(get(i, j));
                if (j < cols() - 1) {
                    sb.append(", ");
                }
            }
            sb.append("]");
            if (i < rows() - 1) {
                sb.append(",\n");
            }
        }
        sb.append("]");
        return sb.toString();
    }
}
