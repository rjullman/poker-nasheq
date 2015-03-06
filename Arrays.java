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

}
