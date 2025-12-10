package by.it.group410902.kovalchuck.lesson01.lesson13;

import java.util.*;

public class GraphC {

    // ��������� ������ ��� ��������� ��������
    private static Map<String, List<String>> graph = new HashMap<>();
    private static Map<String, List<String>> reversed = new HashMap<>();
    private static Set<String> visited = new HashSet<>();                    // ��������� ���������� ������
    private static Deque<String> order = new ArrayDeque<>();                 // ���� ��� ������� ���������� ������
    private static List<List<String>> scc = new ArrayList<>();               // ������ ��������� ������� ���������
    private static Map<String, Integer> vertexToComponent = new HashMap<>(); // ������������ ������� � ����������

    public static void main(String[] args) {

        graph.clear();
        reversed.clear();
        visited.clear();
        order.clear();
        scc.clear();
        vertexToComponent.clear();

        Scanner sc = new Scanner(System.in);
        String input = sc.nextLine().trim();
        buildGraph(input); // ���������� ����� �� ������� ������

        // ������ ������ DFS - ����������� ������� ������
        for (String v : getAllVerticesSorted()) {
            if (!visited.contains(v)) dfs1(v);
        }

        // ������ ������ DFS �� ����������������� ����� - ����� ��������� ������� ���������
        visited.clear();
        while (!order.isEmpty()) {
            String v = order.pop(); // ���� ������� � ������� �������� ������� ����������
            if (!visited.contains(v)) {
                List<String> component = new ArrayList<>();
                dfs2(v, component);
                Collections.sort(component); // ��������� ������� ������ ����������
                scc.add(component);
            }
        }

        //  ������: ������� -> ����� � ���������� ������� ���������
        for (int i = 0; i < scc.size(); i++) {
            for (String v : scc.get(i)) vertexToComponent.put(v, i);
        }

        // ������ ���� ���������� ��� �������
        int n = scc.size();
        List<Set<Integer>> compGraph = new ArrayList<>();
        for (int i = 0; i < n; i++) compGraph.add(new HashSet<>());

        // ��������� ���� ����� ������������ �������� �����
        for (String from : graph.keySet()) {
            for (String to : graph.get(from)) {
                int cf = vertexToComponent.get(from);
                int ct = vertexToComponent.get(to);
                if (cf != ct) compGraph.get(cf).add(ct); // ������ ���� ����� ������� ������������
            }
        }

        // 5. �������������� ���������� ����� �����������
        List<Integer> topoOrder = topologicalSort(compGraph);

        // ����� ��������� ������� ��������� � ������� �������������� ����������
        // �� ���������-������� � ����������-������
        for (int i : topoOrder) {
            for (String v : scc.get(i)) System.out.print(v);
            System.out.println();
        }
    }

    //���������� ��������� � ������������������ ����� �� ������� ������
    private static void buildGraph(String input) {
        String[] edges = input.split(",");
        for (String e : edges) {
            e = e.trim();
            if (e.isEmpty()) continue;
            String[] parts = e.split("->");
            if (parts.length != 2) continue;
            String from = parts[0].trim();
            String to = parts[1].trim();

            // ��������� ���� � �������� ����
            graph.computeIfAbsent(from, k -> new ArrayList<>()).add(to);
            // ��������� �������� ���� � ����������������� ����
            reversed.computeIfAbsent(to, k -> new ArrayList<>()).add(from);

            // �����������, ��� ��� ������� ������������ � ����� ������
            graph.putIfAbsent(to, new ArrayList<>());
            reversed.putIfAbsent(from, new ArrayList<>());
        }

        // ��������� ������ ��������� ��� ������������������ ������� ������
        for (List<String> lst : graph.values()) Collections.sort(lst);
        for (List<String> lst : reversed.values()) Collections.sort(lst);
    }

    //������ ������ DFS - ���������� ����� order � ������� ������� ���������� ��������� ������
    private static void dfs1(String v) {
        visited.add(v);
        for (String to : graph.get(v)) {
            if (!visited.contains(to)) dfs1(to);
        }
        order.push(v); // ��������� ������� � ���� ����� ��������� ���� � ��������
    }

    //������ ������ DFS �� ����������������� ����� - ����� ��������� ������� ���������
    private static void dfs2(String v, List<String> component) {
        visited.add(v);
        component.add(v); // ��������� ������� � ������� ����������
        for (String to : reversed.get(v)) {
            if (!visited.contains(to)) dfs2(to, component);
        }
    }

    //��������� ���� ������ ����� � ��������������� �������
    private static List<String> getAllVerticesSorted() {
        List<String> all = new ArrayList<>(graph.keySet());
        Collections.sort(all);
        return all;
    }

    //�������������� ���������� ����� ����������� � ������� DFS
    private static List<Integer> topologicalSort(List<Set<Integer>> compGraph) {
        int n = compGraph.size();
        boolean[] vis = new boolean[n];
        List<Integer> result = new ArrayList<>();
        for (int i = 0; i < n; i++) {
            if (!vis[i]) dfsTopo(i, vis, result, compGraph);
        }
        Collections.reverse(result); // ������������� ����� �������� ���������� �������
        return result;
    }

    //��������������� DFS ��� �������������� ����������
    private static void dfsTopo(int v, boolean[] vis, List<Integer> result, List<Set<Integer>> compGraph) {
        vis[v] = true;
        for (int to : compGraph.get(v)) {
            if (!vis[to]) dfsTopo(to, vis, result, compGraph);
        }
        result.add(v); // ��������� ������� ����� ��������� ���� � ��������
    }
}