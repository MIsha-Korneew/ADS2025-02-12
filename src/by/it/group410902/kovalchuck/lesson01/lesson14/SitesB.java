package by.it.group410902.kovalchuck.lesson01.lesson14;

import java.util.*;

public class SitesB {
    public static void main(String[] args) {
        Scanner scanner = new Scanner(System.in);

        // ������� � ����� �����
        Map<String, Integer> siteToIndex = new HashMap<>();

        List<String> sites = new ArrayList<>();
        // DSU ��� ���������� ����������� ������
        DSU dsu = new DSU();

        // ������ ������� ������
        while (scanner.hasNextLine()) {
            String line = scanner.nextLine();
            // ��������� ������� ���������� �����
            if ("end".equals(line)) {
                break;
            }

            // ��������� ������
            String[] parts = line.split("\\+");

            if (parts.length != 2) {
                continue;
            }

            String site1 = parts[0];
            String site2 = parts[1];

            // ���� ���� ����������� �������, ��������� ��� � ������� � DSU
            if (!siteToIndex.containsKey(site1)) {
                siteToIndex.put(site1, sites.size());
                sites.add(site1);
                dsu.makeSet(sites.size() - 1);
            }

            // ������������ ������ ���� ����������
            if (!siteToIndex.containsKey(site2)) {
                siteToIndex.put(site2, sites.size());
                sites.add(site2);
                dsu.makeSet(sites.size() - 1);
            }

            // �������� ������� ������
            int index1 = siteToIndex.get(site1);
            int index2 = siteToIndex.get(site2);

            // ���������� ���������, ���������� ��� �����
            dsu.union(index1, index2);
        }

        // �������� ���������� �� �������� ���������
        Map<Integer, Integer> clusterSizes = new HashMap<>();
        for (int i = 0; i < sites.size(); i++) {
            // ������� �������� ������� ��� �������� �����
            int root = dsu.find(i);
            // ����������� ������� ������� ��������
            clusterSizes.put(root, clusterSizes.getOrDefault(root, 0) + 1);
        }

        // ������� ������ �������� ���������
        List<Integer> sizes = new ArrayList<>(clusterSizes.values());
        // ��������� ������� ��������� � ������� ��������
        Collections.sort(sizes, Collections.reverseOrder());

        // ������� ���������
        for (int i = 0; i < sizes.size(); i++) {
            System.out.print(sizes.get(i));
            // ��������� ������ ����� �������, ����� ����������
            if (i < sizes.size() - 1) {
                System.out.print(" ");
            }
        }
    }

    static class DSU {
        private List<Integer> parent;
        private List<Integer> rank;

        public DSU() {
            parent = new ArrayList<>();
            rank = new ArrayList<>();
        }

        //������� ����� ��������� ��� �������� x
        public void makeSet(int x) {
            // ����������� ������ �������, ���� ����������
            while (parent.size() <= x) {
                // ������ ����� ������� ���������� ������ ������ ���������
                parent.add(parent.size());
                rank.add(0);
            }
        }

        public int find(int x) {
            // ���� x �� �������� ������ ������ ���������
            if (parent.get(x) != x) {
                // ���������� ������� ������ � ��������� ��������
                parent.set(x, find(parent.get(x)));
            }
            return parent.get(x);
        }

        public void union(int x, int y) {
            // ������� ����� �������� ��� x � y
            int rootX = find(x);
            int rootY = find(y);

            // ���� �������� ��� � ����� ���������, ������ �� ������
            if (rootX != rootY) {
                // ���������� ����� ������
                if (rank.get(rootX) < rank.get(rootY)) {
                    // ������������ ��������� � ������� ������ � ��������� � ������� ������
                    parent.set(rootX, rootY);
                } else if (rank.get(rootX) > rank.get(rootY)) {
                    // ������������ ��������� � ������� ������ � ��������� � ������� ������
                    parent.set(rootY, rootX);
                } else {
                    // ���� ����� �����, ������������ ���� ��������� � �������
                    // � ����������� ���� �����
                    parent.set(rootY, rootX);
                    rank.set(rootX, rank.get(rootX) + 1);
                }
            }
        }
    }
}