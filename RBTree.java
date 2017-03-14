/**
 * Created by Василий on 11.03.2017.
 */
import java.io.*;

public class RBTree {
    private final int RED = 0;
    private final int BLACK = 1;

    private class Node {
        int key = -1, color = BLACK;
        Node parent = nil, left = nil, right = nil;
        public Node(int key) {
            this.key = key;
        }
    }

    private final Node nil = new Node(-1);

    private Node root = nil;

    public void insert(Node node) {
        Node tmp = root;
        if (root == nil)
            root = node;
        else {
            node.color = RED;
            while (true) {
                if (node.key > tmp.key) {
                    if (tmp.right != nil)
                        tmp = tmp.right;
                    else {
                        node.parent = tmp;
                        tmp.right = node;
                        break;
                    }
                }
                else {
                    if (tmp.left != nil)
                        tmp = tmp.left;
                    else {
                        node.parent = tmp;
                        tmp.left = node;
                        break;
                    }
                }
            }
        }
        fixUp(node);
    }

    public void print(Node node) {
        if (node == nil)
            return;
        else {
            System.out.println((node.color == RED ? "Красный " : "Чёрный ") + node.key + "; Родитель: " + node.parent.key);
            print(node.left);
            print(node.right);
        }
    }

   public Node find(Node node, Node findNode) {
       if (root == nil)
           return null;
        if (findNode.key > node.key) {
            if (node.right != nil)
                return find(node.right, findNode);
        }
        else if (findNode.key < node.key) {
            if (node.left != nil)
                return find(node.left, findNode);
        }
        else if (findNode.key == node.key)
            return node;
        return null;

   }

    private void leftRotate(Node node) {
        if (node.parent == nil) {
            Node right = root.right;
            root.right = right.left;
            right.left.parent = root;
            root.parent = right;
            right.left = root;
            right.parent = nil;
            root = right;
        }
        else {
            if (node.parent.left == node)
                node.parent.left = node.right;
            else
                node.parent.right = node.right;
            node.right.parent = node.parent;
            node.parent = node.right;
            if (node.right.left != nil)
                node.right.left.parent = node;
            node.right = node.right.left;
            node.parent.left = node;
        }
    }

    private void rightRotate(Node node) {
        if (node.parent == nil) {
            Node left = root.left;
            root.left = root.left.right;
            left.right.parent = root;
            root.parent = left;
            left.right = root;
            left.parent = nil;
            root = left;
        }
        else {
            if (node.parent.left == node)
                node.parent.left = node.left;
            else
                node.parent.right = node.left;
            node.left.parent = node.parent;
            node.parent = node.left;
            if (node.left.right != nil)
                node.left.right.parent = node;
            node.left = node.left.right;
            node.parent.right = node;
        }
    }

    private void fixUp(Node node) {
        while (node.parent.color == RED) {
            Node uncle = nil;
            if (node.parent == node.parent.parent.left) {  //Все случаи можно посмотреть здесь: http://www.mkurnosov.net/teaching/uploads/DSA/dsa-fall-lecture4.pdf
                uncle = node.parent.parent.right;
                if (uncle != nil && uncle.color == RED) {  //Первый случай: перекрашиваем и ползем вверх
                    node.parent.color = BLACK;
                    uncle.color = BLACK;
                    node.parent.parent.color = RED;
                    node = node.parent.parent;
                    continue;
                }
                if (node == node.parent.right) {  //Второй случай
                    node = node.parent;
                    leftRotate(node);
                }
                node.parent.color = BLACK;  //Переход к третьему случаю
                node.parent.parent.color = RED;
                rightRotate(node.parent.parent);
            } else {  //Здесь те же случаи, но симметрично
                uncle = node.parent.parent.left;
                if (uncle != nil && uncle.color == RED) {
                    node.parent.color = BLACK;
                    uncle.color = BLACK;
                    node.parent.parent.color = RED;
                    node = node.parent.parent;
                    continue;
                }
                if (node == node.parent.left) {
                    node = node.parent;
                    rightRotate(node);
                }
                node.parent.color = BLACK;
                node.parent.parent.color = RED;
                leftRotate(node.parent.parent);
            }
        }
        root.color = BLACK; //Корень долен быть черным
    }

    boolean delete(Node z) {
        z = find(root, z);
        if (z == null)
            return false;
        Node x;
        Node y = z;
        int y_original_color = y.color;

        if (z.left == nil) {
            x = z.right;
            transplant(z, z.right);
        } else if (z.right == nil){
            x = z.left;
            transplant(z, z.left);
        } else {
            y = treeMinimum(z.right);
            y_original_color = y.color;
            x = y.right;
            if (y.parent == z)
                x.parent = y;
            else {
                transplant(y, y.right);
                y.right = z.right;
                y.right.parent = y;
            }
            transplant(z, y);
            y.left = z.left;
            y.left.parent = y;
            y.color = z.color;
        }
        if (y_original_color == BLACK)
            deleteFixup(x);
        return true;
    }

    void deleteFixup(Node x) {
        while (x != root && x.color == BLACK){
            if (x == x.parent.left) {
                Node w = x.parent.right;
                if (w.color == RED) {
                    w.color = BLACK;
                    x.parent.color = RED;
                    leftRotate(x.parent);
                    w = x.parent.right;
                }
                if (w.left.color == BLACK && w.right.color == BLACK) {
                    w.color = RED;
                    x = x.parent;
                    continue;
                }
                else if (w.right.color == BLACK) {
                    w.left.color = BLACK;
                    w.color = RED;
                    rightRotate(w);
                    w = x.parent.right;
                }
                if (w.right.color == RED) {
                    w.color = x.parent.color;
                    x.parent.color = BLACK;
                    w.right.color = BLACK;
                    leftRotate(x.parent);
                    x = root;
                }
            } else {
                Node w = x.parent.left;
                if (w.color == RED) {
                    w.color = BLACK;
                    x.parent.color = RED;
                    rightRotate(x.parent);
                    w = x.parent.left;
                }
                if (w.right.color == BLACK && w.left.color == BLACK) {
                    w.color = RED;
                    x = x.parent;
                    continue;
                }
                else if (w.left.color == BLACK) {
                    w.right.color = BLACK;
                    w.color = RED;
                    leftRotate(w);
                    w = x.parent.left;
                }
                if (w.left.color == RED) {
                    w.color = x.parent.color;
                    x.parent.color = BLACK;
                    w.left.color = BLACK;
                    rightRotate(x.parent);
                    x = root;
                }
            }
        }
        x.color = BLACK;
    }

    void transplant(Node target, Node with){
        if(target.parent == nil) {
            root = with;
        } else if(target == target.parent.left) {
            target.parent.left = with;
        } else
            target.parent.right = with;
        with.parent = target.parent;
    }

    Node treeMinimum(Node subTreeRoot){
        while(subTreeRoot.left != nil){
            subTreeRoot = subTreeRoot.left;
        }
        return subTreeRoot;
    }

    Node treeMaximum(Node subTreeRoot){
        while(subTreeRoot.right != nil){
            subTreeRoot = subTreeRoot.right;
        }
        return subTreeRoot;
    }

    public void menu() {
        BufferedReader reader = new BufferedReader(new InputStreamReader(System.in));
        String s = "";
        int key;
        System.out.println("Введите элементы дерева. Чтобы остановить ввод, введите \"end\".");
        try {
            while (!s.equals("end")) {
                s = reader.readLine();
                if (!s.equals("end")) {
                    key = Integer.parseInt(s);
                    Node node = new Node(key);
                    insert(node);
                }
            }
        }
        catch (Exception e) {
            System.out.println(e.getMessage());
        }
        System.out.println("Введенное Вами дерево: ");
        print(root);
        delete(new Node(22));
        print(root);
        int min1 = treeMinimum(root).key;
        Node tmp = root;
        while (tmp.left != treeMinimum(root))
            tmp=tmp.left;
        int min2 = tmp.key;
        int max1 = treeMaximum(root).key;
        System.out.println("Минимальная сумма чисел: " + (min1 + min2));
        System.out.println("Максимальная разность чисел: " + (max1 - min1));
    }

    public static void main(String[] args) {
        RBTree tree = new RBTree();
        tree.menu();
    }
}
