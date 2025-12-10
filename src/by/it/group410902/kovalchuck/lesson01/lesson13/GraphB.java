package by.it.group410902.kovalchuck.lesson01.lesson13;

import java.util.*;

public class GraphB {
    public static void main(String[] args) {
        Scanner scanner = new Scanner(System.in);
        String input = scanner.nextLine();

        // ������� ������� ������ ��� ���������� �����
        Map<String, List<String>> graph = new HashMap<>();
        String[] edges = input.split(", ");

        // ��������� ������� ����� �����
        for (String edge : edges) {
            // ��������� ����� �� ��������� � �������� �������
            String[] parts = edge.split(" -> ");
            String from = parts[0];
            String to = parts[1];

            // ��������� ������� � ����, ���� �� ��� ���
            graph.putIfAbsent(from, new ArrayList<>());
            graph.putIfAbsent(to, new ArrayList<>());
            // ��������� ������������ ����� �� ��������� � �������� �������
            graph.get(from).add(to);
        }

        // �������� �� ������� ������ � �����
        boolean hasCycle = hasCycle(graph);

        // ����� ����������: "yes" ���� ���� ����, "no" ���� ��� ������
        System.out.println(hasCycle ? "yes" : "no");
    }

    //��������� ������� ������ � ��������������� �����
    private static boolean hasCycle(Map<String, List<String>> graph) {
        // ������� ��� ������������ ��������� ������:
        // 0 - �� �������� (WHITE)
        // 1 - � �������� ��������� (GRAY)
        // 2 - ��������� ��������� (BLACK)
        Map<String, Integer> visited = new HashMap<>();

        // ������������� ���� ������ ��� ������������
        for (String node : graph.keySet()) {
            visited.put(node, 0);
        }

        // ������ DFS ��� ������ ������������ �������
        for (String node : graph.keySet()) {
            if (visited.get(node) == 0) {
                if (dfs(node, graph, visited)) {
                    return true; // ������ ����
                }
            }
        }

        return false; // ������ �� ����������
    }

    //����������� ������� ������ � ������� (DFS) ��� ����������� ������
    private static boolean dfs(String node, Map<String, List<String>> graph, Map<String, Integer> visited) {
        visited.put(node, 1); // �������� ������� ��� "� �������� ���������" (GRAY)

        // ���������� ������������ ���� ������� ������� �������
        for (String neighbor : graph.get(node)) {
            if (visited.get(neighbor) == 0) {
                // ���� ����� �� �������, ��������� DFS ��� ����
                if (dfs(neighbor, graph, visited)) {
                    return true; // ���� ��������� � ���������
                }
            } else if (visited.get(neighbor) == 1) {
                // ���� ����� ��������� "� �������� ���������" - ��� �������� �����, ���������� ����
                return true; // ������ ����!
            }
            // ���� ����� ��� ��������� ��������� (��������� 2), ������ ����������
        }

        visited.put(node, 2); // �������� ������� ��� "��������� ���������" (BLACK)
        return false; // � ���� ����� ������ �� ����������
    }
}