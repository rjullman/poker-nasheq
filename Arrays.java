public class Arrays {

    private Arrays() {
        // uninstantiable utility class
    }
    
    public static int maxarg(double[] a) {
        int m = 0;
        for (int i = 1; i < a.length; i++) {
            if (a[i] > a[m]) {
                m = i;
            }
        }
        return m;
    }

    public static double sum(double[] a) {
        double s = 0;
        for (double d : a) {
            s += d;
        }
        return s;
    }

    public static int sum(boolean[] a) {
        int s = 0;
        for (boolean b : a) {
            if (b) { s++; }
        }
        return s;
    }

}
