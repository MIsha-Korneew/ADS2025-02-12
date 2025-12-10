package by.it.group410902.kovalchuck.lesson01.lesson12;

import java.util.Map;

public class MyAvlMap implements Map<Integer, String> {

    private static class Node {
        Integer key;
        String value;
        Node left;
        Node right;
        int height;

        Node(Integer key, String value) {
            this.key = key;
            this.value = value;
            this.height = 1;  // ����� ���� ����� ������ 1
        }
    }

    private Node root;  // ������
    private int size;   // ���������� ��������� � �����

    //����������� ������ �����
    public MyAvlMap() {
        root = null;
        size = 0;
    }

    //���������� ������ ����.
    private int height(Node node) {
        return node == null ? 0 : node.height;
    }

    //��������� �������� ����� ������ � ������� �����������.
    private int balanceFactor(Node node) {
        return node == null ? 0 : height(node.left) - height(node.right);
    }

    //��������� ������ ���� �� ������ ����� ��� ��������.
    private void updateHeight(Node node) {
        if (node != null) {
            // ������ = ������������ ������ �������� + 1
            node.height = Math.max(height(node.left), height(node.right)) + 1;
        }
    }

    //��������� ������ ������� ������ ���� y.
    private Node rotateRight(Node y) {
        // ��������� ������ �� ����������� ����
        Node x = y.left;
        Node T2 = x.right;

        // ��������� �������
        x.right = y;
        y.left = T2;

        // ��������� ������ ���������� �����
        updateHeight(y);
        updateHeight(x);

        return x;  // x ���������� ����� ������
    }

    //��������� ����� ������� ������ ���� x.
    private Node rotateLeft(Node x) {
        // ��������� ������ �� ����������� ����
        Node y = x.right;
        Node T2 = y.left;

        // ��������� �������
        y.left = x;
        x.right = T2;

        // ��������� ������ ���������� �����
        updateHeight(x);
        updateHeight(y);

        return y;  // y ���������� ����� ������
    }

    //����������� ����, ���� ��� ����������, �������� ��������������� ��������.
    private Node balance(Node node) {
        if (node == null) return null;

        // ��������� ������ �������� ����
        updateHeight(node);
        int balance = balanceFactor(node);

        // Left Left Case - ��������� ���� ������ �������
        if (balance > 1 && balanceFactor(node.left) >= 0) {
            return rotateRight(node);
        }

        // Left Right Case - ��������� ����� ������� ������ �������, ����� ������ ������� �������� ����
        if (balance > 1 && balanceFactor(node.left) < 0) {
            node.left = rotateLeft(node.left);
            return rotateRight(node);
        }

        // Right Right Case - ��������� ���� ����� �������
        if (balance < -1 && balanceFactor(node.right) <= 0) {
            return rotateLeft(node);
        }

        // Right Left Case - ��������� ������ ������� ������� �������, ����� ����� ������� �������� ����
        if (balance < -1 && balanceFactor(node.right) > 0) {
            node.right = rotateRight(node.right);
            return rotateLeft(node);
        }

        // ������������ �� ���������
        return node;
    }

    //������� ���� � ����������� ������ � ���������.
    private Node minValueNode(Node node) {
        Node current = node;
        // ����������� ���� ��������� � ����� ����� ����
        while (current.left != null) {
            current = current.left;
        }
        return current;
    }

    //��������� ��� ��������� ���� ����-�������� � �����.
    @Override
    public String put(Integer key, String value) {
        if (key == null) {
            throw new NullPointerException("Key cannot be null");
        }

        // ������ ��� �������� ������� �������� ����� ��������
        String[] oldValue = new String[1];
        root = put(root, key, value, oldValue);

        // ���� ����� �� ����, ����������� ������
        if (oldValue[0] == null) {
            size++;
        }
        return oldValue[0];
    }

    //����������� ��������������� ����� ��� ������� ����
    private Node put(Node node, Integer key, String value, String[] oldValue) {
        // �������� ����� ������� - ������� ����� ����
        if (node == null) {
            return new Node(key, value);
        }

        // ���������� ����� ��� ����������� ����������� ������
        int cmp = key.compareTo(node.key);

        if (cmp < 0) {
            // ���� ������ - ���� � ����� ���������
            node.left = put(node.left, key, value, oldValue);
        } else if (cmp > 0) {
            // ���� ������ - ���� � ������ ���������
            node.right = put(node.right, key, value, oldValue);
        } else {
            // ���� ������ - ��������� ��������
            oldValue[0] = node.value;
            node.value = value;
            return node;  // ������������ �� ��������� ��� ����������
        }

        // ����������� ������ ����� �������
        return balance(node);
    }

    //������� ���� � ��������� � ��� �������� �� �����
    @Override
    public String remove(Object key) {
        if (key == null) {
            throw new NullPointerException("Key cannot be null");
        }
        if (!(key instanceof Integer)) {
            return null; // ���� �� Integer - �� ����� ���� � �����
        }

        Integer intKey = (Integer) key;
        // ������ ��� �������� ���������� �������� ����� ��������
        String[] removedValue = new String[1];
        root = remove(root, intKey, removedValue);

        // ���� ���� ��� ������ � ������, ��������� ������
        if (removedValue[0] != null) {
            size--;
        }
        return removedValue[0];
    }

    // ����������� ��������������� ����� ��� �������� ����.
    private Node remove(Node node, Integer key, String[] removedValue) {
        if (node == null) {
            return null;  // ���� �� ������
        }

        // ���������� ����� ��� ����������� ����������� ������
        int cmp = key.compareTo(node.key);

        if (cmp < 0) {
            // ���� ������ - ���� � ����� ���������
            node.left = remove(node.left, key, removedValue);
        } else if (cmp > 0) {
            // ���� ������ - ���� � ������ ���������
            node.right = remove(node.right, key, removedValue);
        } else {
            // ���� ������ - ������� ����
            removedValue[0] = node.value;

            // ���� � ����� �������� ��� ��� ��������
            if (node.left == null || node.right == null) {
                node = (node.left != null) ? node.left : node.right;
            } else {
                // ���� � ����� ��������� - ������� ����������� ���� � ������ ���������
                Node temp = minValueNode(node.right);
                // �������� ���� � �������� �� ���������
                node.key = temp.key;
                node.value = temp.value;
                // ������� ����������� ���� �� ������� ���������
                node.right = remove(node.right, temp.key, new String[1]);
            }
        }

        // ���� ������ ����� ������ ����� ��������
        if (node == null) {
            return null;
        }

        // ����������� ������ ����� ��������
        return balance(node);
    }

    //���������� ��������, ��������� � ��������� ������.
    @Override
    public String get(Object key) {
        if (key == null) {
            throw new NullPointerException("Key cannot be null");
        }
        if (!(key instanceof Integer)) {
            return null; // ���� �� Integer - �� ����� ���� � �����
        }

        Integer intKey = (Integer) key;
        // ����������� ����� �� ������
        Node current = root;
        while (current != null) {
            int cmp = intKey.compareTo(current.key);
            if (cmp < 0) {
                current = current.left;      // ���� �����
            } else if (cmp > 0) {
                current = current.right;     // ���� ������
            } else {
                return current.value;        // ���� ������
            }
        }
        return null;  // ���� �� ������
    }

    //���������, ���������� �� ��������� ���� � �����.
    @Override
    public boolean containsKey(Object key) {
        if (key == null) {
            throw new NullPointerException("Key cannot be null");
        }
        if (!(key instanceof Integer)) {
            return false; // ���� �� Integer - �� ����� ���� � �����
        }

        Integer intKey = (Integer) key;
        // ����������� ����� �� ������
        Node current = root;
        while (current != null) {
            int cmp = intKey.compareTo(current.key);
            if (cmp < 0) {
                current = current.left;      // ���� �����
            } else if (cmp > 0) {
                current = current.right;     // ���� ������
            } else {
                return true;                 // ���� ������
            }
        }
        return false;  // ���� �� ������
    }

    //���������� ���������� ��������� � �����.
    @Override
    public int size() {
        return size;
    }

    //������� ��� �������� �� �����.
    @Override
    public void clear() {
        root = null;
        size = 0;
    }

    //���������, ����� �� �����.
    @Override
    public boolean isEmpty() {
        return size == 0;
    }

    //���������� ��������� ������������� �����
    @Override
    public String toString() {
        if (isEmpty()) {
            return "{}";  // ������ �����
        }

        StringBuilder sb = new StringBuilder();
        sb.append("{");
        inOrderToString(root, sb);  // ����� � ������� �����������
        // ������� ������ ������� � ������ ����� ���������� ��������
        if (sb.length() > 1) {
            // ������� ��������� ������� � ������
            sb.setLength(sb.length() - 2);
        }
        sb.append("}");
        return sb.toString();
    }

    //��������������� ����� ��� ������������� ������ ������ (�����-������-������).
    private void inOrderToString(Node node, StringBuilder sb) {
        if (node != null) {
            // ���������� ������� ����� ���������
            inOrderToString(node.left, sb);

            // ��������� ������� ������� � ������� key=value
            sb.append(node.key).append("=").append(node.value).append(", ");

            // ���������� ������� ������ ���������
            inOrderToString(node.right, sb);
        }
    }

    // ���������, ���������� �� ��������� �������� � �����.
    @Override
    public boolean containsValue(Object value) {
        throw new UnsupportedOperationException("containsValue not implemented");
    }

    //�������� ��� �������� �� ��������� ����� � �������.
    @Override
    public void putAll(Map<? extends Integer, ? extends String> m) {
        throw new UnsupportedOperationException("putAll not implemented");
    }

    //���������� ��������� ������ �����.
    @Override
    public java.util.Set<Integer> keySet() {
        throw new UnsupportedOperationException("keySet not implemented");
    }

    //���������� ��������� �������� �����.
    @Override
    public java.util.Collection<String> values() {
        throw new UnsupportedOperationException("values not implemented");
    }

    //���������� ��������� ��� ����-�������� �����.
    @Override
    public java.util.Set<Map.Entry<Integer, String>> entrySet() {
        throw new UnsupportedOperationException("entrySet not implemented");
    }
}