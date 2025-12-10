package by.it.group410902.kovalchuck.lesson01.lesson13;

import java.util.*;

public class GraphA {
    public static void main(String[] args) {
        Scanner scanner = new Scanner(System.in);
        String input = scanner.nextLine();

        // ���������� �����
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
            graph.get(from).add(to);
        }

        // ��������� �������������� ���������� �����
        List<String> result = topologicalSort(graph);

        // ����� ���������� ����������
        for (int i = 0; i < result.size(); i++) {
            System.out.print(result.get(i));
            if (i < result.size() - 1) {
                System.out.print(" ");
            }
        }
    }

    //���������� ��������� ����
    private static List<String> topologicalSort(Map<String, List<String>> graph) {
        List<String> result = new ArrayList<>(); // �������������� ������ ������
        Map<String, Integer> inDegree = new HashMap<>(); // ������� ��� �������� �������� ����� ������

        // ������������� �������� ����� ��� ���� ������
        for (String node : graph.keySet()) {
            inDegree.put(node, 0);
        }

        // ���������� �������� ����� ��� ���� ������
        for (String node : graph.keySet()) {
            for (String neighbor : graph.get(node)) {
                inDegree.put(neighbor, inDegree.getOrDefault(neighbor, 0) + 1);
            }
        }

        // ������������ ������� ��� ������ � ������� �������� �����
        PriorityQueue<String> queue = new PriorityQueue<>();

        // ��������� � ������� ��� ������� � ������� �������� �����
        for (String node : inDegree.keySet()) {
            if (inDegree.get(node) == 0) {
                queue.offer(node);
            }
        }

        // �������� ���� ��� �������������� ����������
        while (!queue.isEmpty()) {
            // ��������� ������� � ���������� ������������������ ���������
            String current = queue.poll();
            result.add(current); // ��������� ������� � ���������

            // ������������ ���� ������� ������� �������
            for (String neighbor : graph.get(current)) {
                // ��������� ������� ����� ������
                inDegree.put(neighbor, inDegree.get(neighbor) - 1);
                // ���� ������� ����� ����� �������, ��������� � �������
                if (inDegree.get(neighbor) == 0) {
                    queue.offer(neighbor);
                }
            }
        }

        return result;
    }
}