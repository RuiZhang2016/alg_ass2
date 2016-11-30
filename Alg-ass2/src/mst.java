import javafx.scene.layout.Priority;

import java.util.*;

/**
 * Created by rui on 10/19/16.
 */
public class mst {
    private Graph graph;
    private Edge[] KMST;
    private Edge[] PMST;
    public double K_Running_Time;
    public double P_Running_Time;
    public double K_L;
    public double P_L;


    mst(int num){
        graph = new Graph(num);

        long startTime = System.currentTimeMillis();
        KMST = graph.Kruskal_MST();
        long endTime = System.currentTimeMillis();
        K_Running_Time = endTime - startTime;
//        System.out.println("Kruskal_MST run time with " + num + " vertexes :" + K_Running_Time);

        startTime = System.currentTimeMillis();
        PMST = graph.Prim_MST();
        endTime = System.currentTimeMillis();
        P_Running_Time = endTime - startTime;
//        System.out.println("Prim_MST run time with " + num + " vertexes :" + P_Running_Time + "\n");

        L();
    }


    public void L(){
        double sum = 0;
        for(Edge v : KMST){
            sum += v.weight;
        }
//        System.out.println("K_Sum: " + sum);
        K_L = sum;
        sum = 0;
        for(Edge v : PMST){
            sum += v.weight;
        }
        P_L = sum;
//        System.out.println("P_Sum: " + sum + "\n");
    }

    class Graph{
        private double[][] matrix;
        private HashMap<Double, ArrayList<Edge>> Weight_to_Edge = new HashMap<>();
        private Point[] Points;
        private Vertex[] Vertexes;
        private double[] weights;
        private Min_Heap Q;

        Graph(int Num_of_Points){
            Initialization(Num_of_Points);
            Random r = new Random();
            double weight = 0;
            Edge e = null;
            for(int i = 0; i < Num_of_Points; i++){
                for (int j = 0; j<i; j++){
                    weight = r.nextDouble();
                    matrix[i][j] = weight;
                    matrix[j][i] = weight;
                    e = new Edge(Points[i], Points[j], weight);
                    Update_Sorted_Edges(weight,e);
                }
            }
            Object[] t= Weight_to_Edge.keySet().toArray();
            weights = new double[t.length];
            for(int i = 0; i < weights.length; i++){
                weights[i] = (double)t[i];
            }
            Quick_Sort(0,weights.length-1);
        }

        /**
         *
         * @param start
         * @param end
         */
        private void Quick_Sort(int start, int end){
            if (start < end){
                int q = Partition(start, end);
                Quick_Sort(start, q-1);
                Quick_Sort(q+1,end);
            }
        }

        /**
         *
         * @param start
         * @param end
         * @return
         */
        private int Partition(int start, int end){
            double x = weights[end];
            int i = start - 1;
            double tmp = 0;
            for(int j = start; j < end; j++){
                if (weights[j]<=x){
                    i = i + 1;
                    tmp = weights[i];
                    weights[i] = weights[j];
                    weights[j] = tmp;
                }
            }
            tmp = weights[i+1];
            weights[i+1] = weights[end];
            weights[end] = tmp;
            return i+1;
        }

        /**
         * This function initialize all the points and the matrix of the graph.
         * @param Num_of_Points
         */
        private void Initialization(int Num_of_Points){
            matrix = new double[Num_of_Points][Num_of_Points];
            Points = new Point[Num_of_Points];
            for(int i = 0 ; i< Num_of_Points; i++ ){
                Points[i] = new Point(i);
            }
            Vertexes = new Vertex[Num_of_Points];
            for(int i = 0 ; i< Num_of_Points; i++ ){
                Vertexes[i] =  new Vertex(i);
            }

            Q = new Min_Heap(Num_of_Points);

            Vertex r = Vertexes[0];
            r.key = 0;
            for(Vertex tmp: Vertexes){
                Q.Insert(tmp);
            }
        }


        private void Update_Sorted_Edges(double weight, Edge e){
            ArrayList<Edge> W_E = Weight_to_Edge.get(weight);
            if (W_E == null){
                W_E = new ArrayList<Edge>();
            }
            W_E.add(e);
            Weight_to_Edge.put(weight,W_E);
        }

        /**
         * This function finds the minimum spinning tree by Kruskal algorithm.
         * @return
         */
        public Edge[] Kruskal_MST(){
            Edge[] A = new Edge[Points.length-1];
            int index = 0;
            for(double w: weights){
                ArrayList<Edge> edges = Weight_to_Edge.get(w);
                for(Edge e: edges){
                    if (e.A.label == e.B.label) continue;
                    if (e.A.Find_Set() != e.B.Find_Set()){
                        A[index] = e;
                        index = index + 1;
                        e.A.Union(e.B);
                    }
                }
            }
            return A;
        }

        /**
         * This function finds the minimum spinning tree by Prim algorithm.
         * @return
         */
        public Edge[] Prim_MST(){
            Edge[] A = new Edge[Vertexes.length-1];
            int index = 0;

            while( Q.size != 0){
                Vertex u = Q.Remove_Min();
                for(Vertex v: Vertexes){
                    if (v.label != u.label){
                        if(Q.Contain(v) && matrix[u.label][v.label]< v.key){
                            v.p = u;
                            v.key = matrix[u.label][v.label];
                        }
                    }
                }
                Q.Update();
//                System.out.println("");
                if (u.p != null) {
                    A[index] = new Edge(new Point(u.p.label), new Point(u.label), u.key);
//                    System.out.println(A[index]);
                    index++;
                }
            }

            return A;
        }

        @Override
        public String toString(){
            String graph = "";
            for(double[] row: matrix){
                for(double w: row){
                    graph = graph + w + " ";
                }
                graph = graph + "\n";
            }
            return graph;
        }

        class MyComparator implements Comparator<Vertex>{
            public int compare(Vertex a, Vertex b){
                return a.key-b.key>0?1:-1;
            }
        }

    }

    /**
     * The directed forest is implemented in the class Point.
     */
    class Point{
        public int rank;
        public Point p;
        public int label;

        Point(int label){
            this.label = label;
            Make_Set();
        }

        public void Make_Set(){
            p = this;
            rank = 0;
        }

        public Point Find_Set(){
            if (this != p){
                p = p.Find_Set();
            }
            return p;
        }

        public void Link(Point y){
            if (rank > y.rank){
                y.p = this;
            }else{
                p = y;
                if (rank == y.rank)
                    y.rank = y.rank + 1;
            }
        }

        public void Union(Point y){
            Find_Set().Link(y.Find_Set());
        }

        public String toString(){
            return ""+label;
        }
    }

    class Edge{
        public Point A;
        public Point B;
        public double weight;

        Edge(Point a, Point b,double w){
            A = a;
            B = b;
            weight = w;
        }

        @Override
        public String toString(){
            return A.toString() + " - " + B.toString() + " - " + weight;
        }
    }

    class Vertex{
        public double key;
        public Vertex p;
        public int label;

        Vertex(int i){
            key = Double.MAX_VALUE;
            p = null;
            label = i;
        }

    }

    class Min_Heap{
        private Vertex[] Heap;
        private int size;
        private int maxsize;


        Min_Heap(int maxsize){
            this.size = 0;
            this.maxsize = maxsize;
            Heap = new Vertex[maxsize+1];
            Heap[0] = new Vertex(-1);
        }

        private int Parent(int index){
            return index/2;
        }

        private int Left_Child(int index){
            return 2*index;
        }

        private int Right_Child(int index){
            return 2*index + 1;
        }

        private  boolean Is_Leaf(int index){
            return ((index<=size) && (index > size/2));
        }

        private void swap(int index1, int index2)
        {
            Vertex tmp;
            tmp = Heap[index1];
            Heap[index1] = Heap[index2];
            Heap[index2] = tmp;
        }

        public void Insert(Vertex element){
            Heap[++size] = element;
            int Current_Index = size;
            while (Current_Index >= 2 && Heap[Current_Index].key < Heap[Parent(Current_Index)].key)
            {
                swap(Current_Index,Parent(Current_Index));
                Current_Index = Parent(Current_Index);
            }
        }

        public Vertex Remove_Min(){
            swap(1,size);
            size--;
            if(size!=0) Push_Down(1);
            return Heap[size+1];
        }

        private void Push_Down(int index){
            int Smallest_Child;
            while (!Is_Leaf(index)) {
                Smallest_Child = Left_Child(index);
                if (Smallest_Child < size && Heap[Smallest_Child].key > Heap[Smallest_Child + 1].key)
                    Smallest_Child = Smallest_Child + 1;
                if (Heap[index].key <= Heap[Smallest_Child].key)
                    return;
                swap(index, Smallest_Child);
                index = Smallest_Child;
            }
        }

        public void Update(){
            Vertex[] tmp = Heap;
            int old_size = size;
            size = 0;
            for(int i = 1; i <= old_size; i++ ){
                Insert(Heap[i]);
            }
        }

        public boolean Contain(Vertex v){
            for ( Vertex vv: Heap) {
                if (vv.label == v.label) return true;
            }
            return false;
        }

    }

    public static void main(String[] args) {
        double[][] L = new double[2][5];
        double[][] T = new double[2][5];
        mst trial;
        int times = 5;
        int[] size = {10,100,500,1000,2000};
        for(int i = 0; i < times; i++){
            for (int j = 0; j < size.length; i++){
                trial = new mst(size[j]);
                L[0][j] = L[0][j] + trial.K_L;
                L[1][j] = L[1][j] + trial.P_L;
                T[0][j] = T[0][j] + trial.K_Running_Time;
                T[1][j] = T[1][j] + trial.P_Running_Time;
            }
        }

        for(int i = 0; i < 2; i++){
            System.out.println("n L(n)         Time");
            for (int j = 0; j < size.length; j++){
                L[i][j] = L[i][j]/times;
                T[i][j] = T[i][j]/times;
                System.out.println(size[j] +" " + L[i][j] + " "+ T[i][j]);
            }
            System.out.println("");
        }



//        System.out.println(trial1.graph);
//        for(Edge e: trial1.KMST){
//            System.out.println(e);
//        }
//        System.out.println("");
//        for(Edge e: trial1.PMST){
//            System.out.println(e);
//        }
    }


}

