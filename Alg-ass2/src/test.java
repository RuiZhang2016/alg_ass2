import java.util.ArrayList;
import java.util.HashMap;

/**
 * Created by rui on 10/23/16.
 */
public class test {

    public Point[] a;

    test(int n){
        a = new Point[n];
        a[0] = new Point(5,5);
        Point b = a[0];
        b.x = 4;
    }

    class Point{
        public int x = 0;
        public int y = 0;

        Point(int a, int b){
            x = a;
            y = b;
        }
    }

    public static void main(String[] args) {
        test a = new test(5);

        System.out.println(a.a[0].x);
    }
}
