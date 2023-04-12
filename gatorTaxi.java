import java.util.ArrayList;
import java.util.Comparator;

import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.util.Scanner;


class ABC implements Comparator<NodeClass> {
    public int compare(NodeClass a, NodeClass b) {
        return a.key - b.key;
    }
}

class Data implements Comparable<Data> {

    Integer rideNumber;
    Integer rideCost;
    Integer tripDuration;

    public Data(Integer rideNumber, Integer rideCost, Integer tripDuration) {
        this.rideNumber = rideNumber;
        this.rideCost = rideCost;
        this.tripDuration = tripDuration;
    }

    public String toString() {
        String s = "";
        s += "(" + this.rideNumber + "," + this.rideCost + "," + this.tripDuration + ")";
        return s;
    }

    @Override
    public int compareTo(Data o) {
        if (this.rideCost - o.rideCost == 0) {
            return this.tripDuration - o.tripDuration;
        }
        return this.rideCost - o.rideCost;
    }
}

class NodeClass implements Comparable<NodeClass> {
    private static final boolean RED = true;
    Data data;
    Integer key;
    NodeClass parent;
    NodeClass left;
    NodeClass right;
    boolean color; // black = False red = True

    public NodeClass(Integer rideNumber, Integer ridCost, Integer tripDuration) {
        this.data = new Data(rideNumber, ridCost, tripDuration);
        this.key = rideNumber;
        this.color = RED;

    }

    public void set(NodeClass node) {
        this.key = node.key;
        this.data = node.data;
    }

    @Override
    public int compareTo(NodeClass o) {
        return this.data.compareTo(o.data);
    }
}

public class gatorTaxi {
    private MinHeap<Data> minHeap;
    private RBTree tree;

    gatorTaxi() {
        this.minHeap = new MinHeap<>();
        this.tree = new RBTree();
    }

    public NodeClass print(Integer rideNumber) {
        NodeClass node = tree.search(rideNumber);
        if (node == null || node == tree.nill) {
            node = new NodeClass(0, 0, 0);
        }
        return node;
    }

    public ArrayList<NodeClass> print(Integer ride1, Integer ride2) {
        ArrayList<NodeClass> arr = tree.range(ride1, ride2);
        if (arr.isEmpty()) {
            NodeClass node = new NodeClass(0, 0, 0);
            arr.add(node);
        }
        return arr;
    }

    public Boolean insert(Integer rideNumber, Integer rideCost, Integer tripDuration) {
        NodeClass node = new NodeClass(rideNumber, rideCost, tripDuration);
        if (tree.search(node.data.rideNumber) == tree.nill) {
            minHeap.insert(node.data);
            tree.insert(node);
            return true;
        } else {
            return false;
        }
    }

    public Data getNextRide() {
        Data data = minHeap.removeMin();
        NodeClass node;
        if (data != null) {
            node = tree.search(data.rideNumber);
            tree.delete(node.data.rideNumber);
            return data;
        } else {
            // handle if node == null => "No Active ride requests" should be printed
            return null;
        }
    }

    // Problem
    public void cancelRide(Integer rideNumber) {
        NodeClass node = tree.search(rideNumber);
        if (node != null) {
            minHeap.delete(node.data);
            tree.delete(rideNumber);
        }

    }

    public void updateTrip(Integer rideNumber, Integer newTripDuration) {
        NodeClass node = tree.search(rideNumber);
        if (node == null) {
            return;
        }
        minHeap.delete(node.data);
        if (node.data.tripDuration >= newTripDuration) {
            node.data.tripDuration = newTripDuration;
            minHeap.insert(node.data);
        } else if (newTripDuration <= node.data.tripDuration * 2) {
            node.data.tripDuration = newTripDuration;
            node.data.rideCost += 10;
            minHeap.insert(node.data);
        } else {
            tree.delete(rideNumber);
        }
    }

    public static void main(String[] args) {
        // write your code here
        if (args.length == 0) {
            System.out.println("Error: input file not specified.");
            return;
        }
        String inputFileName = args[0];
        gatorTaxi gatorTaxi = new gatorTaxi();
        try {
            File inputFile = new File(inputFileName);
            Scanner scanner = new Scanner(inputFile);
            FileWriter outputFile = new FileWriter("output.txt");

            while (scanner.hasNextLine()) {
                String inputLine = scanner.nextLine().trim();
                String[] in = inputLine.substring(inputLine.indexOf("(") + 1, inputLine.indexOf(")")).split(",");

                if (inputLine.startsWith("Insert")) {
                    Integer rideNumber = Integer.parseInt(in[0].trim());
                    Integer rideCost = Integer.parseInt(in[1].trim());
                    Integer tripDuration = Integer.parseInt(in[2].trim());

                    Boolean node = gatorTaxi.insert(rideNumber, rideCost, tripDuration);
                    if (node == false) {
                        String out = "Duplicate RideNumber";
                        outputFile.write(out + "\n");
                        break;
                    }
                } else if (inputLine.startsWith("Print")) {
                    if (in.length == 1) {
                        Integer rideNumber = Integer.parseInt(in[0].trim());
                        NodeClass node = gatorTaxi.print(rideNumber);

                        outputFile.write("(" + node.data.rideNumber + "," + node.data.rideCost + ","
                                + node.data.tripDuration + ")");
                        outputFile.write("\n");
                    } else {
                        Integer rideNumber1 = Integer.parseInt(in[0].trim());
                        Integer rideNumber2 = Integer.parseInt(in[1].trim());
                        ArrayList<NodeClass> arr = gatorTaxi.print(rideNumber1, rideNumber2);

                        for (int i = 0; i < arr.size(); i++) {
                            NodeClass node = arr.get(i);
                            outputFile.write("(" + node.data.rideNumber + "," + node.data.rideCost + ","
                                    + node.data.tripDuration + ")");
                            if (i + 1 != arr.size()) {
                                outputFile.write(",");
                            } else {
                                outputFile.write("\n");
                            }

                        }
                    }
                } else if (inputLine.startsWith("UpdateTrip")) {
                    Integer rideNumber = Integer.parseInt(in[0].trim());
                    Integer newTripDuration = Integer.parseInt(in[1].trim());
                    gatorTaxi.updateTrip(rideNumber, newTripDuration);
                } else if (inputLine.startsWith("GetNextRide")) {
                    Data node = gatorTaxi.getNextRide();
                    if (node == null) {
                        String out = "No active ride requests";
                        outputFile.write(out);
                    } else {
                        outputFile.write("(" + node.rideNumber + ", " + node.rideCost + ", " + node.tripDuration + ")");
                    }
                    outputFile.write("\n");
                } else if (inputLine.startsWith("CancelRide")) {
                    Integer rideNumber = Integer.parseInt(in[0].trim());
                    gatorTaxi.cancelRide(rideNumber);
                }
            }

            scanner.close();
            outputFile.close();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

}
//MinHeap implemantation
class MinHeap<T extends Comparable<T>> {
    private ArrayList<T> heap;

    public MinHeap() {
        this.heap = new ArrayList<>();
    }

    public void insert(T node) {
        // Add new Element to the end of the heap
        heap.add(node);
        int currIdx = heap.size() - 1;

        // Reordering the heap to place the newly add element to its right position
        while (currIdx > 0) {
            int parIdx = (currIdx - 1) / 2;
            T par = heap.get(parIdx);
            if (node.compareTo(par) < 0) {
                heap.set(currIdx, par);
                heap.set(parIdx, node);
                currIdx = parIdx;
            } else {
                break;
            }
        }
    }

    public T getMin() {
        if (heap.size() == 0) {
            return null;
        }
        return heap.get(0);
    }

    // Removing Smallest Element
    public T removeMin() {
        if (heap.size() == 0) {
            return null;
        }
        T minNode = this.getMin();
        T lastNode = heap.remove(heap.size() - 1);
        // If more the one element exists in the heap
        if (heap.size() != 0) {

            // setting last element of heap as first element
            heap.set(0, lastNode);

            int currIdx = 0;

            // reordering to find right position of last element in new heap
            while (true) {
                int leftIdx = 2 * currIdx + 1;
                int rightIdx = 2 * currIdx + 2;

                if (leftIdx >= heap.size()) {
                    break;
                }
                int minChildIdx = leftIdx;

                if (rightIdx < heap.size()) {
                    T left = heap.get(leftIdx);
                    T right = heap.get(rightIdx);

                    if (left.compareTo(right) > 0) {
                        minChildIdx = rightIdx;
                    }
                }

                T minChild = heap.get(minChildIdx);

                if (lastNode.compareTo(minChild) > 0) {
                    heap.set(currIdx, minChild);
                    heap.set(minChildIdx, lastNode);
                    currIdx = minChildIdx;
                } else {
                    break;
                }

            }
        }

        return minNode;
    }

    public String toString() {
        String s = "";
        for (int i = 0; i < heap.size(); i++) {
            T data = heap.get(i);
            s = s + data.toString() + ", ";
        }
        return s;
    }

    public boolean isEmpty() {
        return heap.isEmpty();
    }

    public void delete(T node) {
        if (heap.size() == 0) {
            return;
        }

        int idx = heap.indexOf(node);

        if (idx == -1) {
            return;
        }

        T lastNode = heap.remove(heap.size() - 1);

        if (idx != heap.size()) {
            heap.set(idx, lastNode);
            int currIdx = idx;

            while (true) {
                int leftIdx = 2 * currIdx + 1;
                int rightIdx = 2 * currIdx + 2;

                if (leftIdx >= heap.size()) {
                    break;
                }

                int minChildIdx = leftIdx;

                if (rightIdx < heap.size()) {
                    T left = heap.get(leftIdx);
                    T right = heap.get(rightIdx);
                    if (left.compareTo(right) > 0) {
                        minChildIdx = rightIdx;
                    }
                }

                T minChild = heap.get(minChildIdx);

                if (lastNode.compareTo(minChild) > 0) {
                    heap.set(currIdx, minChild);
                    heap.set(minChildIdx, lastNode);
                    currIdx = minChildIdx;
                } else {
                    break;
                }
            }
            int parentIdx = (currIdx - 1) / 2;

            T parent = heap.get(parentIdx);

            if (currIdx > 0 && lastNode.compareTo(parent) < 0) {
                while (currIdx > 0) {
                    parentIdx = (currIdx - 1) / 2;
                    parent = heap.get(parentIdx);

                    if (lastNode.compareTo(parent) < 0) {
                        heap.set(currIdx, parent);
                        heap.set(parentIdx, lastNode);
                        currIdx = parentIdx;
                    } else {
                        break;
                    }
                }
            }
        }
    }
}
//Red Black tree implemantation
class RBTree {
    NodeClass root;
    NodeClass nill;
    private static final boolean RED = true;
    private static final boolean BLACK = false;

    RBTree() {
        this.nill = new NodeClass(null, null, null);
        this.nill.left = null;
        this.nill.right = null;
        this.nill.color = BLACK;
        this.root = this.nill;
    }

    public void rotateLeft(NodeClass node) {
        if (node == null || node == nill) {
            return;
        }
        NodeClass rightNode = node.right;

        node.right = rightNode.left;
        if (rightNode.left != nill) {
            rightNode.left.parent = node;
        }

        rightNode.parent = node.parent;
        if (node.parent == null) {
            this.root = rightNode;
        } else if (node.parent.left == node) {
            node.parent.left = rightNode;
        } else {
            node.parent.right = rightNode;
        }
        rightNode.left = node;
        node.parent = rightNode;
    }

    public void rotateRight(NodeClass node) {
        if (node == null || node == nill) {
            return;
        }
        NodeClass leftNode = node.left;

        node.left = leftNode.right;
        if (leftNode.right != null && leftNode.right != nill) {
            leftNode.right.parent = node;
        }

        leftNode.parent = node.parent;
        if (node.parent == null) {
            this.root = leftNode;
        }

        else if (node.parent.left == node) {
            node.parent.left = leftNode;
        } else {
            node.parent.right = leftNode;
        }

        leftNode.right = node;
        node.parent = leftNode;

    }

    public boolean insert(NodeClass node) {

        node.parent = null;
        node.left = nill;
        node.right = nill;
        node.color = RED;

        NodeClass searchNode = this.root;
        NodeClass serachNodeParent = null;
        while (searchNode != nill) {
            serachNodeParent = searchNode;
            if (searchNode.key > node.key) {
                searchNode = searchNode.left;
            } else if (searchNode.key < node.key) {
                searchNode = searchNode.right;
            } else {
                return false;
            }
        }
        node.parent = serachNodeParent;
        if (serachNodeParent == null) {
            root = node;
        } else if (serachNodeParent.key > node.key) {
            serachNodeParent.left = node;
        } else {
            serachNodeParent.right = node;
        }

        if (node.parent == null) {
            return true;
        }
        if (node.parent.parent == null) {
            return true;
        }

        insertFixUp(node);

        return true;

    }

    public Boolean getColor(NodeClass node) {
        if (node == null || node == nill) {
            return BLACK;
        }
        return node.color;
    }

    public void flipColor(NodeClass node) {
        if (node == null || node == nill) {
            return;
        }
        node.color = RED;
        if (node.left != null) {
            node.left.color = BLACK;
        }
        if (node.right != null) {
            node.right.color = BLACK;
        }
    }

    public void insertFixUp(NodeClass node) {
        NodeClass nodeUncle;
        while (node.parent.color == RED) {
            if (node.parent == node.parent.parent.right) {
                nodeUncle = node.parent.parent.left;
                if (nodeUncle.color == RED) {
                    nodeUncle.color = BLACK;
                    node.parent.color = BLACK;
                    node.parent.parent.color = RED;
                    node = node.parent.parent;
                } else {
                    if (node == node.parent.left) {
                        node = node.parent;
                        rotateRight(node);
                    }
                    node.parent.color = BLACK;
                    node.parent.parent.color = RED;
                    rotateLeft(node.parent.parent);
                }
            } else {
                nodeUncle = node.parent.parent.right;

                if (nodeUncle.color == RED) {
                    nodeUncle.color = BLACK;
                    node.parent.color = BLACK;
                    node.parent.parent.color = RED;
                    node = node.parent.parent;
                } else {
                    if (node == node.parent.right) {
                        node = node.parent;
                        rotateLeft(node);
                    }
                    node.parent.color = BLACK;
                    node.parent.parent.color = RED;
                    rotateRight(node.parent.parent);
                }
            }
            if (node == root) {
                break;
            }
        }
        root.color = BLACK;

    }

    public void transplant(NodeClass replacingNode, NodeClass newReplacement) {
        if (replacingNode.parent == null) {
            this.root = newReplacement;
        } else if (replacingNode == replacingNode.parent.left) {
            replacingNode.parent.left = newReplacement;
        } else {
            replacingNode.parent.right = newReplacement;
        }
        newReplacement.parent = replacingNode.parent;

    }

    public NodeClass minimum(NodeClass node) {
        while (node.left != nill) {
            node = node.left;
        }
        return node;
    }

    public void delete(Integer key) {
        NodeClass node = search(key);

        if (node == nill || node == null) {
            return;
        }
        NodeClass nodeTobeReplace = node;
        boolean nodeTobeReplace_originalColor = node.color;
        NodeClass fixUpNode;
        // case 1
        if (node.left == nill) {
            fixUpNode = node.right;
            transplant(node, node.right);
        }
        // case 2
        else if (node.right == nill) {
            fixUpNode = node.left;
            transplant(node, node.left);
        }
        // case 3
        else {
            nodeTobeReplace = minimum(node.right);
            nodeTobeReplace_originalColor = nodeTobeReplace.color;
            fixUpNode = nodeTobeReplace.right;
            if (nodeTobeReplace.parent == node) {
                fixUpNode.parent = nodeTobeReplace;
            } else {
                transplant(nodeTobeReplace, nodeTobeReplace.right);
                nodeTobeReplace.right = node.right;
                nodeTobeReplace.right.parent = nodeTobeReplace;
            }
            transplant(node, nodeTobeReplace);
            nodeTobeReplace.left = node.left;
            nodeTobeReplace.left.parent = nodeTobeReplace;
            nodeTobeReplace.color = node.color;
        }

        if (nodeTobeReplace_originalColor == BLACK) {
            deleteFixUp(fixUpNode);
        }
    }

    // black = 0 red = 1
    public void deleteFixUp(NodeClass nodeToFix) {
        NodeClass node;
        while (nodeToFix != this.root && nodeToFix.color == BLACK) {
            if (nodeToFix == nodeToFix.parent.left) {
                node = nodeToFix.parent.right;
                if (node.color == RED) {
                    node.color = BLACK;
                    nodeToFix.parent.color = RED;
                    rotateLeft(nodeToFix.parent);
                    node = nodeToFix.parent.right;
                }

                if (node.left.color == BLACK && node.right.color == BLACK) {
                    node.color = RED;
                    nodeToFix = nodeToFix.parent;
                } else {
                    if (node.right.color == BLACK) {
                        node.left.color = BLACK;
                        node.color = RED;
                        rotateRight(node);
                        node = nodeToFix.parent.right;
                    }

                    node.color = nodeToFix.parent.color;
                    nodeToFix.parent.color = BLACK;
                    node.right.color = BLACK;
                    rotateLeft(nodeToFix.parent);
                    nodeToFix = root;
                }
            } else {
                node = nodeToFix.parent.left;
                if (node.color == RED) {
                    node.color = BLACK;
                    nodeToFix.parent.color = RED;
                    rotateRight(nodeToFix.parent);
                    node = nodeToFix.parent.left;
                }
                if (node.right.color == BLACK && node.right.color == BLACK) {
                    node.color = RED;
                    nodeToFix = nodeToFix.parent;
                } else {
                    if (node.left.color == BLACK) {
                        node.right.color = BLACK;
                        node.color = RED;
                        rotateLeft(node);
                        node = nodeToFix.parent.left;
                    }

                    node.color = nodeToFix.parent.color;
                    nodeToFix.parent.color = BLACK;
                    node.left.color = BLACK;
                    rotateRight(nodeToFix.parent);
                    nodeToFix = root;

                }
            }
        }

        nodeToFix.color = BLACK;
    }

    public NodeClass search(Integer key) {
        NodeClass temp = this.root;
        while (temp != nill) {
            if (temp.key.equals(key)) {
                return temp;
            } else if (temp.key > key) {
                temp = temp.left;
            } else {
                temp = temp.right;
            }
        }
        return temp;
    }

    private void rangeHelper(NodeClass node, Integer key1, Integer key2, ArrayList<NodeClass> arr) {
        if (node == null || node == nill) {
            return;
        }
        if (node.key > key2) {
            rangeHelper(node.left, key1, key2, arr);
        } else if (node.key >= key1 && node.key <= key2) {

            rangeHelper(node.left, key1, key2, arr);
            arr.add(node);
            rangeHelper(node.right, key1, key2, arr);

        } else {
            rangeHelper(node.right, key1, key2, arr);

        }
    }

    public ArrayList<NodeClass> range(Integer key1, Integer key2) {
        ArrayList<NodeClass> arr = new ArrayList<>();
        rangeHelper(root, key1, key2, arr);
        return arr;
    }

}