public class Test {
    
    public static void main(String[] args) {
        Vector v1 = new Vector(new double[] {1, 2, 3});
        Vector v2 = new Vector(3);
        v2.set(0, 4);
        v2.set(1, 5);
        v2.set(2, 6);
        Vector v3 = v1.add(v2);
        System.out.println(v3);
        Vector v4 = v1.sub(v2);
        System.out.println(v4);
        Vector v5 = v1.mul(2);
        System.out.println(v5);
        double d1 = v1.dot(v2);
        System.out.println(d1);

        Matrix m1 = new Matrix(new double[][] {{1, 2, 3}, {4, 5, 6}});
        Matrix m2 = new Matrix(2, 3);
        m2.set(0, 0, 7);
        m2.set(0, 1, 8);
        m2.set(0, 2, 9);
        m2.set(1, 0, 10);
        m2.set(1, 1, 11);
        m2.set(1, 2, 12);
        Matrix m3 = m1.add(m2);
        System.out.println(m3);
        Matrix m4 = m1.sub(m2);
        System.out.println(m4);
        Matrix m5 = m1.mul(2);
        System.out.println(m5);
        Matrix m6 = m1.mul(m2.transpose());
        System.out.println(m6);

        UnmodifiableVector uv1 = new UnmodifiableVector(new double[] {1, 2, 3});
        UnmodifiableVector uv2 = new UnmodifiableVector(v2);
        UnmodifiableVector uv3 = uv1.add(uv2);
        System.out.println(uv3);
        System.out.println(uv3.get(1));

        UnmodifiableMatrix um1 = new UnmodifiableMatrix(new double[][] {{1, 2, 3}, {4, 5, 6}});
        UnmodifiableMatrix um2 = new UnmodifiableMatrix(m2);
        UnmodifiableMatrix um3 = um1.mul(um2.transpose());
        System.out.println(um3);
        System.out.println(um3.get(1, 1));

        // The following code should not compile
        // uv3.set(1, 10);
        // um3.set(1, 1, 10);
    }

}
