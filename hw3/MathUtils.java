public class MathUtils {
    public static UnmodifiableVector getUnmodifiableVector(Vector v) {
        return new UnmodifiableVector(v);
    }

    public static UnmodifiableMatrix getUnmodifiableMatrix(Matrix m) {
        return new UnmodifiableMatrix(m);
    }
}
