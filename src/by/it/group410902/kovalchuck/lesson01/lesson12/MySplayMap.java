package by.it.group410902.kovalchuck.lesson01.lesson12;

import java.util.*;

public class MySplayMap implements NavigableMap<Integer, String> {


    private static class Node {
        Integer key;
        String value;
        Node left, right, parent;

        Node(Integer key, String value, Node parent) {
            this.key = key;
            this.value = value;
            this.parent = parent;
        }
    }

    private Node root;
    private int size;

    // ���������� ���������� ��������� � ������
    @Override
    public int size() {
        return size;
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
        Node node = find((Integer) key);
        if (node != null) {
            // ���������� ��������� ���� � ������
            splay(node);
            return true;
        }
        return false;
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
        if (value.equals(node.value)) {
            // ��� ���������� �������� ���������� ���� � ������
            splay(node);
            return true;
        }
        return containsValue(node.left, value) || containsValue(node.right, value);
    }

    // ��������� �������� �� �����
    @Override
    public String get(Object key) {
        if (!(key instanceof Integer)) return null;
        Node node = find((Integer) key);
        if (node != null) {
            // ���������� ��������� ���� � ������
            splay(node);
            return node.value;
        }
        return null;
    }

    // ����� ���� �� ����� ��� splay ��������
    private Node find(Integer key) {
        Node current = root;
        while (current != null) {
            int cmp = key.compareTo(current.key);
            if (cmp < 0) {
                current = current.left;
            } else if (cmp > 0) {
                current = current.right;
            } else {
                return current;
            }
        }
        return null;
    }

    // ���������� ��� ���������� ���� ����-��������
    @Override
    public String put(Integer key, String value) {
        if (key == null) throw new NullPointerException();

        // ���� ������ ������, ������� ������
        if (root == null) {
            root = new Node(key, value, null);
            size = 1;
            return null;
        }

        Node current = root;
        Node parent = null;
        // ����� ����� ��� �������
        while (current != null) {
            parent = current;
            int cmp = key.compareTo(current.key);
            if (cmp < 0) {
                current = current.left;
            } else if (cmp > 0) {
                current = current.right;
            } else {
                // ���� ��� ���������� - ��������� ��������
                String oldValue = current.value;
                current.value = value;
                splay(current);
                return oldValue;
            }
        }

        // ������� ����� ����
        Node newNode = new Node(key, value, parent);
        int cmp = key.compareTo(parent.key);
        // ��������� � ������ �����
        if (cmp < 0) {
            parent.left = newNode;
        } else {
            parent.right = newNode;
        }
        size++;
        // ���������� ����� ���� � ������
        splay(newNode);
        return null;
    }

    // �������� �������� �� �����
    @Override
    public String remove(Object key) {
        if (!(key instanceof Integer)) return null;
        Node node = find((Integer) key);
        if (node == null) return null;

        String oldValue = node.value;
        // ���������� ��������� ���� � ������
        splay(node);

        // ������ 1: ��� ������ ���������
        if (node.left == null) {
            root = node.right;
            if (root != null) root.parent = null;
        }
        // ������ 2: ��� ������� ���������
        else if (node.right == null) {
            root = node.left;
            if (root != null) root.parent = null;
        }
        // ������ 3: ���� ��� ���������
        else {
            // ������� ����������� ������� � ������ ���������
            Node min = min(node.right);
            // ���� min �� �������� ���������������� ������ ��������
            if (min.parent != node) {
                // ������������� �����
                if (min.right != null) {
                    min.right.parent = min.parent;
                }
                min.parent.left = min.right;
                min.right = node.right;
                min.right.parent = min;
            }
            // ������������ ����� ���������
            min.left = node.left;
            min.left.parent = min;
            min.parent = null;
            root = min;
        }

        size--;
        return oldValue;
    }

    // ����� ���� � ����������� ������ � ���������
    private Node min(Node node) {
        while (node.left != null) {
            node = node.left;
        }
        return node;
    }

    // ����� ���� � ������������ ������ � ���������
    private Node max(Node node) {
        while (node.right != null) {
            node = node.right;
        }
        return node;
    }

    // Splay �������� - ����������� ���� � ������
    private void splay(Node node) {
        while (node.parent != null) {
            if (node.parent.parent == null) {
                // Zig - ���� �������
                if (node.parent.left == node) {
                    rotateRight(node.parent);
                } else {
                    rotateLeft(node.parent);
                }
            } else if (node.parent.left == node && node.parent.parent.left == node.parent) {
                // Zig-Zig (������ ��������)
                rotateRight(node.parent.parent);
                rotateRight(node.parent);
            } else if (node.parent.right == node && node.parent.parent.right == node.parent) {
                // Zig-Zig (����� ��������)
                rotateLeft(node.parent.parent);
                rotateLeft(node.parent);
            } else if (node.parent.left == node && node.parent.parent.right == node.parent) {
                // Zig-Zag (������-�����)
                rotateRight(node.parent);
                rotateLeft(node.parent);
            } else {
                // Zig-Zag (�����-������)
                rotateLeft(node.parent);
                rotateRight(node.parent);
            }
        }
        root = node;
    }

    // ����� �������
    private void rotateLeft(Node node) {
        Node rightChild = node.right;
        if (rightChild == null) return;

        // ��������������� ����� ��������� ������� �������
        node.right = rightChild.left;
        if (rightChild.left != null) {
            rightChild.left.parent = node;
        }

        // ��������� �������� ������� �������
        rightChild.parent = node.parent;
        if (node.parent == null) {
            root = rightChild;
        } else if (node == node.parent.left) {
            node.parent.left = rightChild;
        } else {
            node.parent.right = rightChild;
        }

        // ������ node ����� �������� rightChild
        rightChild.left = node;
        node.parent = rightChild;
    }

    // ������ �������
    private void rotateRight(Node node) {
        Node leftChild = node.left;
        if (leftChild == null) return;

        // ��������������� ������ ��������� ������ �������
        node.left = leftChild.right;
        if (leftChild.right != null) {
            leftChild.right.parent = node;
        }

        // ��������� �������� ������ �������
        leftChild.parent = node.parent;
        if (node.parent == null) {
            root = leftChild;
        } else if (node == node.parent.right) {
            node.parent.right = leftChild;
        } else {
            node.parent.left = leftChild;
        }

        // ������ node ������ �������� leftChild
        leftChild.right = node;
        node.parent = leftChild;
    }

    // ������� ������
    @Override
    public void clear() {
        root = null;
        size = 0;
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
        Node minNode = min(root);
        splay(minNode);
        return minNode.key;
    }

    // ��������� ���������� (�����������) �����
    @Override
    public Integer lastKey() {
        if (isEmpty()) throw new NoSuchElementException();
        Node maxNode = max(root);
        splay(maxNode);
        return maxNode.key;
    }

    // ���������� ����, ������ ������� ���������
    @Override
    public Integer lowerKey(Integer key) {
        Node node = findLower(key);
        if (node != null) {
            splay(node);
            return node.key;
        }
        return null;
    }

    // ����� ����������� �����, ������ �������� ���������
    private Node findLower(Integer key) {
        Node current = root;
        Node candidate = null;
        while (current != null) {
            int cmp = key.compareTo(current.key);
            if (cmp <= 0) {
                // ���� ����� - ������� ���� ������� �������
                current = current.left;
            } else {
                // ����� ���������, ���� ������ ��� ������ ��������
                candidate = current;
                current = current.right;
            }
        }
        return candidate;
    }

    // ���������� ����, ������� ��� ������ ���������
    @Override
    public Integer floorKey(Integer key) {
        Node node = findFloor(key);
        if (node != null) {
            splay(node);
            return node.key;
        }
        return null;
    }

    // ����� ����������� �����, �������� ��� ������� ���������
    private Node findFloor(Integer key) {
        Node current = root;
        Node candidate = null;
        while (current != null) {
            int cmp = key.compareTo(current.key);
            if (cmp < 0) {
                // ���� ����� - ������� ���� ������� �������
                current = current.left;
            } else if (cmp > 0) {
                // ����� ���������, ���� ������ ��� ������ ��������
                candidate = current;
                current = current.right;
            } else {
                // ����� ������ ����������
                return current;
            }
        }
        return candidate;
    }

    // ���������� ����, ������� ��� ������ ���������
    @Override
    public Integer ceilingKey(Integer key) {
        Node node = findCeiling(key);
        if (node != null) {
            splay(node);
            return node.key;
        }
        return null;
    }

    // ����� ����������� �����, �������� ��� ������� ���������
    private Node findCeiling(Integer key) {
        Node current = root;
        Node candidate = null;
        while (current != null) {
            int cmp = key.compareTo(current.key);
            if (cmp < 0) {
                // ����� ���������, ���� ����� ��� ������ ��������
                candidate = current;
                current = current.left;
            } else if (cmp > 0) {
                // ���� ������ - ������� ���� ������� ���������
                current = current.right;
            } else {
                // ����� ������ ����������
                return current;
            }
        }
        return candidate;
    }

    // ���������� ����, ������ ������� ���������
    @Override
    public Integer higherKey(Integer key) {
        Node node = findHigher(key);
        if (node != null) {
            splay(node);
            return node.key;
        }
        return null;
    }

    // ����� ����������� �����, ������ �������� ���������
    private Node findHigher(Integer key) {
        Node current = root;
        Node candidate = null;
        while (current != null) {
            int cmp = key.compareTo(current.key);
            if (cmp < 0) {
                // ����� ���������, ���� ����� ��� ������ ��������
                candidate = current;
                current = current.left;
            } else {
                // ���� ������ - ������� ���� ������� ���������
                current = current.right;
            }
        }
        return candidate;
    }

    // ��������� ����� ����� �� ���������� ����� (��������)
    @Override
    public SortedMap<Integer, String> headMap(Integer toKey) {
        if (toKey == null) throw new NullPointerException();
        MySplayMap result = new MySplayMap();
        headMap(root, toKey, result);
        return result;
    }

    // ����������� ���������� headMap
    private void headMap(Node node, Integer toKey, MySplayMap result) {
        if (node == null) return;
        if (node.key.compareTo(toKey) < 0) {
            // ���� �������� - ���������
            result.put(node.key, node.value);
            // ������� ������ ���������
            headMap(node.right, toKey, result);
        }
        // ������ ������� ����� ���������
        headMap(node.left, toKey, result);
    }

    // ��������� ����� ����� �� ���������� ����� (�������)
    @Override
    public SortedMap<Integer, String> tailMap(Integer fromKey) {
        if (fromKey == null) throw new NullPointerException();
        MySplayMap result = new MySplayMap();
        tailMap(root, fromKey, result);
        return result;
    }

    // ����������� ���������� tailMap
    private void tailMap(Node node, Integer fromKey, MySplayMap result) {
        if (node == null) return;
        if (node.key.compareTo(fromKey) >= 0) {
            // ���� �������� - ���������
            result.put(node.key, node.value);
            // ������� ����� ���������
            tailMap(node.left, fromKey, result);
        }
        // ������ ������� ������ ���������
        tailMap(node.right, fromKey, result);
    }

    // ������, �� ������������� � ������ ����������

    @Override
    public Entry<Integer, String> lowerEntry(Integer key) {
        throw new UnsupportedOperationException();
    }

    @Override
    public Entry<Integer, String> floorEntry(Integer key) {
        throw new UnsupportedOperationException();
    }

    @Override
    public Entry<Integer, String> ceilingEntry(Integer key) {
        throw new UnsupportedOperationException();
    }

    @Override
    public Entry<Integer, String> higherEntry(Integer key) {
        throw new UnsupportedOperationException();
    }

    @Override
    public Entry<Integer, String> firstEntry() {
        throw new UnsupportedOperationException();
    }

    @Override
    public Entry<Integer, String> lastEntry() {
        throw new UnsupportedOperationException();
    }

    @Override
    public Entry<Integer, String> pollFirstEntry() {
        throw new UnsupportedOperationException();
    }

    @Override
    public Entry<Integer, String> pollLastEntry() {
        throw new UnsupportedOperationException();
    }

    @Override
    public NavigableMap<Integer, String> descendingMap() {
        throw new UnsupportedOperationException();
    }

    @Override
    public NavigableSet<Integer> navigableKeySet() {
        throw new UnsupportedOperationException();
    }

    @Override
    public NavigableSet<Integer> descendingKeySet() {
        throw new UnsupportedOperationException();
    }

    @Override
    public NavigableMap<Integer, String> subMap(Integer fromKey, boolean fromInclusive, Integer toKey, boolean toInclusive) {
        throw new UnsupportedOperationException();
    }

    @Override
    public NavigableMap<Integer, String> headMap(Integer toKey, boolean inclusive) {
        throw new UnsupportedOperationException();
    }

    @Override
    public NavigableMap<Integer, String> tailMap(Integer fromKey, boolean inclusive) {
        throw new UnsupportedOperationException();
    }

    @Override
    public Comparator<? super Integer> comparator() {
        return null;
    }

    @Override
    public SortedMap<Integer, String> subMap(Integer fromKey, Integer toKey) {
        throw new UnsupportedOperationException();
    }

    @Override
    public Set<Integer> keySet() {
        throw new UnsupportedOperationException();
    }

    @Override
    public Collection<String> values() {
        throw new UnsupportedOperationException();
    }

    @Override
    public Set<Entry<Integer, String>> entrySet() {
        throw new UnsupportedOperationException();
    }

    @Override
    public void putAll(Map<? extends Integer, ? extends String> m) {
        throw new UnsupportedOperationException();
    }
}