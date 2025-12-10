package by.it.group410902.kovalchuck.lesson01.lesson12;

import java.util.*;

public class MyRbMap implements SortedMap<Integer, String> {

    // ��������� ��� ������ �����
    private static final boolean RED = true;
    private static final boolean BLACK = false;

    // ���������� ����� ��� ������������� ���� ������-������� ������
    private static class Node {
        Integer key;
        String value;
        Node left, right;
        boolean color;
        int size;

        // ����������� ����
        Node(Integer key, String value, boolean color, int size) {
            this.key = key;
            this.value = value;
            this.color = color;
            this.size = size;
        }
    }

    private Node root;

    // ��������, �������� �� ���� �������
    private boolean isRed(Node node) {
        if (node == null) return false;
        return node.color == RED;
    }

    // ��������� ������� ���������
    private int size(Node node) {
        if (node == null) return 0;
        return node.size;
    }

    // ���������� ���������� ��������� � ������
    @Override
    public int size() {
        return size(root);
    }

    // ���������, ����� �� ������
    @Override
    public boolean isEmpty() {
        return root == null;
    }

    // ���������, ���������� �� ���� � ������
    @Override
    public boolean containsKey(Object key) {
        if (!(key instanceof Integer)) return false;
        return get(root, (Integer) key) != null;
    }

    // ���������, ���������� �� �������� � ������
    @Override
    public boolean containsValue(Object value) {
        if (!(value instanceof String)) return false;
        return containsValue(root, (String) value);
    }

    // ����������� ����� �������� � ������
    private boolean containsValue(Node node, String value) {
        if (node == null) return false;
        if (value.equals(node.value)) return true;
        return containsValue(node.left, value) || containsValue(node.right, value);
    }

    // ��������� �������� �� �����
    @Override
    public String get(Object key) {
        if (!(key instanceof Integer)) return null;
        Node node = get(root, (Integer) key);
        return node == null ? null : node.value;
    }

    // ����� ���� �� �����
    private Node get(Node node, Integer key) {
        while (node != null) {
            int cmp = key.compareTo(node.key);
            if (cmp < 0) node = node.left;
            else if (cmp > 0) node = node.right;
            else return node;
        }
        return null;
    }

    // ���������� ��� ���������� ���� ����-��������
    @Override
    public String put(Integer key, String value) {
        if (key == null) throw new NullPointerException();
        String oldValue = get(key);
        root = put(root, key, value);
        root.color = BLACK;
        return oldValue;
    }

    // ����������� ���������� ���� � ������
    private Node put(Node node, Integer key, String value) {
        if (node == null) return new Node(key, value, RED, 1);

        int cmp = key.compareTo(node.key);
        if (cmp < 0) node.left = put(node.left, key, value);
        else if (cmp > 0) node.right = put(node.right, key, value);
        else node.value = value;

        // ������������ ������ ����� �������
        if (isRed(node.right) && !isRed(node.left)) node = rotateLeft(node);
        if (isRed(node.left) && isRed(node.left.left)) node = rotateRight(node);
        if (isRed(node.left) && isRed(node.right)) flipColors(node);

        node.size = size(node.left) + size(node.right) + 1;
        return node;
    }

    // ����� ������� ��� ������������
    private Node rotateLeft(Node node) {
        Node x = node.right;
        node.right = x.left;
        x.left = node;
        x.color = node.color;
        node.color = RED;
        x.size = node.size;
        node.size = size(node.left) + size(node.right) + 1;
        return x;
    }

    // ������ ������� ��� ������������
    private Node rotateRight(Node node) {
        Node x = node.left;
        node.left = x.right;
        x.right = node;
        x.color = node.color;
        node.color = RED;
        x.size = node.size;
        node.size = size(node.left) + size(node.right) + 1;
        return x;
    }

    // ����� ������ ����� ��� ������������
    private void flipColors(Node node) {
        node.color = !node.color;
        node.left.color = !node.left.color;
        node.right.color = !node.right.color;
    }

    // �������� �������� �� �����
    @Override
    public String remove(Object key) {
        if (!(key instanceof Integer)) return null;
        if (!containsKey(key)) return null;

        String oldValue = get(key);

        // ��������� ��������� ����� ����� ��� ��������� ��������
        if (!isRed(root.left) && !isRed(root.right))
            root.color = RED;

        root = remove(root, (Integer) key);
        if (!isEmpty()) root.color = BLACK;
        return oldValue;
    }

    // ����������� �������� ���� �� ������
    private Node remove(Node node, Integer key) {
        if (key.compareTo(node.key) < 0) {
            if (!isRed(node.left) && !isRed(node.left.left))
                node = moveRedLeft(node);
            node.left = remove(node.left, key);
        } else {
            if (isRed(node.left))
                node = rotateRight(node);
            if (key.compareTo(node.key) == 0 && (node.right == null))
                return null;
            if (!isRed(node.right) && !isRed(node.right.left))
                node = moveRedRight(node);
            if (key.compareTo(node.key) == 0) {
                Node x = min(node.right);
                node.key = x.key;
                node.value = x.value;
                node.right = deleteMin(node.right);
            } else {
                node.right = remove(node.right, key);
            }
        }
        return balance(node);
    }

    // ����������� �������� ���� ����� ��� ������������
    private Node moveRedLeft(Node node) {
        flipColors(node);
        if (isRed(node.right.left)) {
            node.right = rotateRight(node.right);
            node = rotateLeft(node);
            flipColors(node);
        }
        return node;
    }

    // ����������� �������� ���� ������ ��� ������������
    private Node moveRedRight(Node node) {
        flipColors(node);
        if (isRed(node.left.left)) {
            node = rotateRight(node);
            flipColors(node);
        }
        return node;
    }

    // �������� ������������ ���� �� ���������
    private Node deleteMin(Node node) {
        if (node.left == null)
            return null;

        if (!isRed(node.left) && !isRed(node.left.left))
            node = moveRedLeft(node);

        node.left = deleteMin(node.left);
        return balance(node);
    }

    // ������������ ���� ����� ��������
    private Node balance(Node node) {
        if (isRed(node.right)) node = rotateLeft(node);
        if (isRed(node.left) && isRed(node.left.left)) node = rotateRight(node);
        if (isRed(node.left) && isRed(node.right)) flipColors(node);

        node.size = size(node.left) + size(node.right) + 1;
        return node;
    }

    // ����� ���� � ����������� ������ � ���������
    private Node min(Node node) {
        if (node.left == null) return node;
        return min(node.left);
    }

    // ������� ������
    @Override
    public void clear() {
        root = null;
    }

    // ��������� ������������� ������
    @Override
    public String toString() {
        if (isEmpty()) return "{}";

        StringBuilder sb = new StringBuilder();
        sb.append("{");
        inOrderToString(root, sb);
        if (sb.length() > 1) {
            sb.setLength(sb.length() - 2);
        }
        sb.append("}");
        return sb.toString();
    }

    // ����� ������ � ������� ����������� ��� toString
    private void inOrderToString(Node node, StringBuilder sb) {
        if (node == null) return;
        inOrderToString(node.left, sb);
        sb.append(node.key).append("=").append(node.value).append(", ");
        inOrderToString(node.right, sb);
    }

    // ��������� ������� (�����������) �����
    @Override
    public Integer firstKey() {
        if (isEmpty()) throw new NoSuchElementException();
        return min(root).key;
    }

    // ��������� ���������� (�����������) �����
    @Override
    public Integer lastKey() {
        if (isEmpty()) throw new NoSuchElementException();
        return max(root).key;
    }

    // ����� ���� � ������������ ������ � ���������
    private Node max(Node node) {
        if (node.right == null) return node;
        return max(node.right);
    }

    // ��������� ����� ����� �� ���������� �����
    @Override
    public SortedMap<Integer, String> headMap(Integer toKey) {
        if (toKey == null) throw new NullPointerException();
        MyRbMap result = new MyRbMap();
        headMap(root, toKey, result);
        return result;
    }

    // ����������� ���������� headMap
    private void headMap(Node node, Integer toKey, MyRbMap result) {
        if (node == null) return;
        if (node.key.compareTo(toKey) < 0) {
            result.put(node.key, node.value);
            headMap(node.right, toKey, result);
        }
        headMap(node.left, toKey, result);
    }

    // ��������� ����� ����� �� ���������� �����
    @Override
    public SortedMap<Integer, String> tailMap(Integer fromKey) {
        if (fromKey == null) throw new NullPointerException();
        MyRbMap result = new MyRbMap();
        tailMap(root, fromKey, result);
        return result;
    }

    // ����������� ���������� tailMap
    private void tailMap(Node node, Integer fromKey, MyRbMap result) {
        if (node == null) return;
        if (node.key.compareTo(fromKey) >= 0) {
            result.put(node.key, node.value);
            tailMap(node.left, fromKey, result);
        }
        tailMap(node.right, fromKey, result);
    }

    // ������, �� ������������� � ������ ����������

    // ���������� ��� �������������� ������
    @Override
    public Comparator<? super Integer> comparator() {
        return null;
    }

    // ��������� ������������ �����
    @Override
    public SortedMap<Integer, String> subMap(Integer fromKey, Integer toKey) {
        throw new UnsupportedOperationException();
    }

    // ��������� ������
    @Override
    public Set<Integer> keySet() {
        throw new UnsupportedOperationException();
    }

    // ��������� ��������
    @Override
    public Collection<String> values() {
        throw new UnsupportedOperationException();
    }

    // ��������� ��� ����-��������
    @Override
    public Set<Entry<Integer, String>> entrySet() {
        throw new UnsupportedOperationException();
    }

    // ���������� ���� ��������� �� ������ �����
    @Override
    public void putAll(Map<? extends Integer, ? extends String> m) {
        throw new UnsupportedOperationException();
    }
}