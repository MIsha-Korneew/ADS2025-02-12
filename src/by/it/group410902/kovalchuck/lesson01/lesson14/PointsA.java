package by.it.group410902.kovalchuck.lesson01.lesson14;

import java.util.*;

public class PointsA {
    public static void main(String[] args) {
        Scanner scanner = new Scanner(System.in);

        double maxDistance = scanner.nextDouble();
        // ���������� �����
        int n = scanner.nextInt();

        // ������ ��� �������� ��������� �����
        int[][] points = new int[n][3];

        for (int i = 0; i < n; i++) {
            points[i][0] = scanner.nextInt();
            points[i][1] = scanner.nextInt();
            points[i][2] = scanner.nextInt();
        }

        DSU dsu = new DSU(n);

        // �������� �� ���� ����� �����
        for (int i = 0; i < n; i++) {
            for (int j = i + 1; j < n; j++) {

                double distance = calculateDistance(points[i], points[j]);

                if (distance < maxDistance) {
                    dsu.union(i, j);
                }
            }
        }

        // ������� ���� ���������
        List<Integer> clusterSizes = new ArrayList<>();
        for (int i = 0; i < n; i++) {
            // ���� i - �������� ������� ������ ���������
            if (dsu.find(i) == i) {
                // ��������� ������ ����� �������� � ������
                clusterSizes.add(dsu.size[i]);
            }
        }

        // ��������� ������� ��������� � ������� ��������
        Collections.sort(clusterSizes, Collections.reverseOrder());

        // ������� ���������
        for (int i = 0; i < clusterSizes.size(); i++) {
            System.out.print(clusterSizes.get(i));
            // ��������� ������ ����� �������, ����� ����������
            if (i < clusterSizes.size() - 1) {
                System.out.print(" ");
            }
        }
    }

    private static double calculateDistance(int[] p1, int[] p2) {
        // ��������� �������� �� ������ ����������
        double dx = p1[0] - p2[0];
        double dy = p1[1] - p2[1];
        double dz = p1[2] - p2[2];

        return Math.hypot(Math.hypot(dx, dy), dz);
    }

    static class DSU {
        int[] parent; // ������ ������������ ���������
        int[] size;   // ������ �������� ��������

        public DSU(int n) {
            parent = new int[n];
            size = new int[n];
            // �������������: ������ ������� - ������ ������ ���������
            for (int i = 0; i < n; i++) {
                parent[i] = i;
                size[i] = 1;
            }
        }

        public int find(int x) {
            // ���� x �� �������� ������ ������ ���������
            if (parent[x] != x) {
                // ���������� ������� ������ � ��������� ��������
                parent[x] = find(parent[x]);
            }
            return parent[x];
        }

        //���������� ��� ���������, ���������� x � y
        public void union(int x, int y) {
            // ������� ����� �������� ��� x � y
            int rootX = find(x);
            int rootY = find(y);

            // ���� �������� ��� � ����� ���������, ������ �� ������
            if (rootX != rootY) {
                // ���������� ������� ��������� � �������
                if (size[rootX] < size[rootY]) {

                    parent[rootX] = rootY;
                    size[rootY] += size[rootX];
                } else {

                    parent[rootY] = rootX;
                    size[rootX] += size[rootY];
                }
            }
        }
    }
}