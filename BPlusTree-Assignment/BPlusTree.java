package BPlusTree;
import java.io.*;
import java.util.*;

public class BPlusTree {
    private int M;
    NonLeafNode root;
    LeafNode Leaf;

    static LeafNode lastLeaf;
    public BPlusTree(){
        this.M = 0;
        root = null;
        Leaf = null;
    }
    public void setM(int M){
        this.M = M;
    }
    public class Node {
        NonLeafNode parent;
        //Parent node is NonLeafNode.
    }

    public static class key implements Comparable<key> {
        int key;
        int value;

        public key(int key, int value) {
            this.key = key;
            this.value = value;
        }
        //override the compareTo method to use sort method in java.util.Collections
        public int compareTo(key otherKey) {
            if (this.key < otherKey.key)
                return -1;
            else if (this.key > otherKey.key)
                return 1;
            return 0;
        }
    }

    public class NonLeafNode extends Node {
        int maxChildNum; // Node can have up to M nodes.
        int minChildNum; // Must have at least [M / 2] childs.
        List<Integer> keys;
        List<Node> childs;
        public NonLeafNode() {
            maxChildNum = M;
            minChildNum = (int) Math.ceil(M / 2.0);
            this.keys = new ArrayList<>();
            this.childs = new ArrayList<>();
        }

        public NonLeafNode(List<Integer> keys, List<Node> childs) {
            super();
            maxChildNum = M;
            minChildNum = (int) Math.ceil(M / 2.0);
            this.keys = keys;
            this.childs = childs;
        }

        public void addChild(Node newChild) {
            this.childs.add(newChild);
        }
        public void prependChild(Node newChild){this.childs.add(0, newChild); }

        public void addKey(int newKey) {
            this.keys.add(newKey);
        }
        public void prependKey(int newKey){ this.keys.add(0, newKey);}
        public void sorting() {
            Collections.sort(keys);
        }

        public int findIndexOfKey(int key) {
            int i;
            for (i = 0; i < this.keys.size(); i++) {
                if (key < keys.get(i))
                    break;
            }
            return i;
        }

        public int findIndexOfChild(Node N){
            int i;
            for(i = 0; i<this.childs.size(); i++){
                if(N == this.childs.get(i))
                    break;
            }
            return i;
        }

        public void insertChild(int idx, Node newNode) {
            childs.add(idx, newNode);
        }
        public void deleteKey(int idx) {
            this.keys.remove(idx);
        }
        public void deleteChild(int idx) {
            this.childs.remove(idx);
        }
        public boolean isOverFlow() {
            return this.maxChildNum < this.childs.size();
        }
        public boolean isUnderFlow(){ return this.minChildNum > this.childs.size(); }
        public boolean isAffordable(){ return this.childs.size() > minChildNum; }
    }

    public class LeafNode extends Node {
        int maxKeyNum;
        int minKeyNum;
        List<key> keys;
        LeafNode right;
        LeafNode left;
        // LeafNode should be connected to each other as linked list.

        public LeafNode(key newKey) {
            this.maxKeyNum = M - 1; //num of key is always same as num of childs - 1
            this.minKeyNum = (int) (Math.ceil(M / 2.0) - 1);
            this.keys = new ArrayList<>();
            this.addKey(newKey);
            this.right = null;
            this.left = null;
        }

        public LeafNode(List<key> keys) {
            this.maxKeyNum = M - 1;
            this.minKeyNum = (int) (Math.ceil(M / 2.0) - 1);
            this.keys = keys;
        }

        public void addKey(key Key) {
            this.keys.add(Key);
        }
        public void prependKey(key Key){this.keys.add(0, Key); }
        public void deleteKey(int index) {
            this.keys.remove(index);
        }
        public boolean isOverFlow() { return this.keys.size() > maxKeyNum; }
        public boolean isUnderFlow(){ return this.keys.size() < minKeyNum; }
        public void sorting() { Collections.sort(keys); }
        public boolean isAffordable(){
            return this.keys.size() > minKeyNum;
        }
    }

    public LeafNode findLeafNode(int key) {
        int i;// Index of the Node that contains the key.
        Node N = this.root != null ? this.root : this.Leaf;
        while (!(N instanceof LeafNode)) { // Until N is LeafNode.
            NonLeafNode temp = (NonLeafNode) N;
            for (i = 0; i < temp.keys.size(); i++) {
                if (key < temp.keys.get(i))
                    break;
            }
            N = temp.childs.get(i);
        }
        return (LeafNode) N;
    }

    public void Insert(int key, int value) {
        // 삽입
        if (this.Leaf == null) {
            //There is no leaf node. There isn't any key.
            this.Leaf = new LeafNode(new key(key, value));
            return;
        }

        LeafNode nodeToInsert;
        if (this.root == null) {
            //root is null means that there is only one Leaf node.
            nodeToInsert = Leaf;
        } else
            nodeToInsert = findLeafNode(key);

        nodeToInsert.addKey(new key(key, value));
        nodeToInsert.sorting();
        //sort after append new key
        if (nodeToInsert.isOverFlow()) {
            //need to split
            SplitLeafNode(nodeToInsert);
        }
    }

    public int getMidIdx() {
        // index used when split the node
        // new right node contains key located in mid.
        return (int) (this.M / 2.0);
    }

    public List<key> divideLeafNodeKey(LeafNode N, int Mid) {
        List<key> ret = new ArrayList<>();
        int times = N.keys.size() - Mid;
        for (int i = 0; i < times; i++) {
            ret.add(N.keys.get(Mid));
            N.deleteKey(Mid);
        }
        return ret;
    }

    public List<Integer> divideNonLeafNodeKey(NonLeafNode N, int Mid) {
        List<Integer> ret = new ArrayList<>();
        int times = N.keys.size() - Mid - 1;
        for (int i = 0; i < times; i++) {
            ret.add(N.keys.get(Mid + 1));
            N.deleteKey(Mid + 1);
        }
        N.deleteKey(Mid);
        return ret;
    }

    public List<Node> divideNonLeafNodeChild(NonLeafNode N, int Mid) {
        List<Node> ret = new ArrayList<>();
        int times = N.childs.size() - Mid - 1;
        for (int i = 0; i < times; i++) {
            ret.add(N.childs.get(Mid + 1));
            N.deleteChild(Mid + 1);
        }
        return ret;
    }

    public void SplitLeafNode(LeafNode LN) {
        int Mid = this.getMidIdx();
        key parentKey = LN.keys.get(Mid); // new Parent Key.
        List<key> newKeys = divideLeafNodeKey(LN, Mid); //key List that contain keys mid to end.
        NonLeafNode Parent = LN.parent;
        LeafNode rightSibling = new LeafNode(newKeys);

        if (Parent == null) {
            //should make a new parent node.
            NonLeafNode newParent = new NonLeafNode();
            newParent.addKey(parentKey.key);
            newParent.addChild(LN);
            newParent.addChild(rightSibling);
            LN.parent = newParent;
            rightSibling.parent = newParent;
            rightSibling.right = LN.right;
            LN.right = rightSibling;
            rightSibling.left = LN;
            this.root = newParent;
            return;
        } else{
            Parent.insertChild(Parent.findIndexOfKey(parentKey.key) + 1, rightSibling);
            Parent.addKey(parentKey.key);
            Parent.sorting();
            rightSibling.right = LN.right;
            rightSibling.parent = Parent;
            LN.right = rightSibling;
            rightSibling.left = LN;
            if(rightSibling.right != null)
                rightSibling.right.left = rightSibling;
        }

        while (Parent.isOverFlow()) {
            Parent = SplitNonLeafNode(Parent);
        }
    }

    public NonLeafNode SplitNonLeafNode(NonLeafNode N) {
        int Mid = this.getMidIdx();
        int parentKey = N.keys.get(Mid);
        NonLeafNode Parent = N.parent;
        NonLeafNode newNode = new NonLeafNode(this.divideNonLeafNodeKey(N, Mid), this.divideNonLeafNodeChild(N, Mid));
        for(int i = 0; i<newNode.childs.size(); i++)
            newNode.childs.get(i).parent = newNode;

        //      new Parent
        //    /           \
        //  NN            newNode
        //  / \             /  \
        // Childs          Childs
        //Set right child's parent newNode.

        if (Parent == null){
            //If this node is root
            //should make a new root node
            NonLeafNode newParent = new NonLeafNode();
            newParent.addKey(parentKey);
            newParent.addChild(N);
            newParent.addChild(newNode);
            N.parent = newParent;
            newNode.parent = newParent;
            this.root = newParent;
            return newParent;
        } else {
            //Node has a parent node.
            Parent.insertChild(N.parent.findIndexOfKey(parentKey) + 1, newNode);
            Parent.addKey(parentKey);
            Parent.sorting();
            newNode.parent = Parent;
            return Parent;
        }
    }

    public void single_search_key(int key) {
        if(this.root == null && this.Leaf == null) return;
        //if there is no key in tree, just return.
        Node N = this.root != null ? this.root : this.Leaf;

        //Do not use method findLeafNode() to record paths.
        List<NonLeafNode> path = new ArrayList<>();
        //this List will contain nodes passed for finding key.

        while(!(N instanceof LeafNode)){
            NonLeafNode Internal = (NonLeafNode) N;
            path.add(Internal);
            int i;
            for(i = 0; i<Internal.keys.size(); i++)
                if(Internal.keys.get(i) > key)
                    break;
            N = Internal.childs.get(i);
        }

        LeafNode L = (LeafNode)N;
        for (key keyOfLeaf : L.keys) {
            if (keyOfLeaf.key == key) {
                //Found the key.
                for(NonLeafNode passedNode : path) {
                    for(int i = 0; i<passedNode.keys.size(); i++){
                        System.out.print(passedNode.keys.get(i));
                        if(i != passedNode.keys.size() - 1)
                            System.out.print(",");
                    }
                    System.out.println();
                }
                System.out.println(keyOfLeaf.value);
                return;
            }
        }
        System.out.println("NOT FOUND!");
    }

    public void range_search_key(int st, int ed){
        LeafNode N = findLeafNode(st);
        if(N == null){
            System.out.println("NOT FOUND!");
            return;
        }

        while(N != null) {
            int i;
            for (i = 0; i < N.keys.size(); i++) {
                int Key = N.keys.get(i).key;
                if (st < Key && Key < ed) {
                    System.out.println(Key + "," + N.keys.get(i).value);
                } else if (Key >= ed)
                    return;
            }
            N = N.right; //go to right node.
        }
    }
    public void Delete(int key){
        if(this.Leaf == null) return;
        //if this.Leaf is the only leaf in tree, should delete key in this.Leaf or find the node to delete key.
        LeafNode NodeToDelete = this.Leaf.parent == null ? this.Leaf : findLeafNode(key);

        for(int i = 0; i<NodeToDelete.keys.size(); i++){
            if(NodeToDelete.keys.get(i).key == key){
                NodeToDelete.deleteKey(i);
                if(i == 0 && NodeToDelete.parent != null && NodeToDelete != this.Leaf){
                    if(NodeToDelete.keys.isEmpty() && NodeToDelete.parent.findIndexOfChild(NodeToDelete) != 0)
                        continue; //in this case key will be deleted in parent. so I don't need to update the key.

                    updateKey(NodeToDelete.parent, key);
                }
                break;
            }
        }
        if(NodeToDelete.parent != null & NodeToDelete.isUnderFlow())
            balanceLeafNode(NodeToDelete, key);
    }
    public void balanceLeafNode(LeafNode N, int deletedkKey){
        NonLeafNode Parent = N.parent;
        //find index of Node in Parent Node.
        int indexOfNode = N.parent.findIndexOfChild(N);
        //if Node has left sibling, assign N.left or null.
        //sibling means Nodes that have same parents.
        LeafNode left = indexOfNode > 0 ? N.left : null;
        //if Node has right sibling, assign N.right or null.
        LeafNode right = indexOfNode < N.parent.childs.size() - 1 ? N.right : null;

        if(right == null){
            //this node is rightmost sibling.
            //so cannot borrow key from right sibling.
            if(left.isAffordable()){
                //left sibling can lend key to N.
                //Should borrow the least key from left sibling.
                key rightMostKey = left.keys.get(left.keys.size() - 1);
                left.deleteKey(left.keys.size() - 1);
                N.prependKey(rightMostKey);
                //update parent's key as rightMostKey's key.
                updateKey(Parent, N.keys.size() > 1 ? N.keys.get(1).key : deletedkKey);
                //consider situation that N has only one key borrowed from left sibling.
            }
            else{
                //If this node cannot borrow key from left sibling, should merge with left sibling.
                for(int i = 0; i< N.keys.size(); i++)
                    left.addKey(N.keys.get(i));
                left.right = N.right;
                if(N.right != null) N.right.left = left;
                Parent.deleteKey(Parent.keys.size() - 1);
                Parent.deleteChild(Parent.childs.size() - 1);
                if(Parent != this.root && Parent.isUnderFlow()){
                    //it's ok if root node is under flowed.
                    //Balance NonLeafNode
                    balanceNonLeafNode(Parent);
                }
                else if(Parent == this.root && Parent.keys.size() == 0){
                    this.root = null;
                    left.parent = null;
                    this.Leaf = left;
                    N = null;
                }
            }
        }
        else if(left == null){
            //this node is leftmost sibling.
            //cannot borrow key from left sibling.
            if(right.isAffordable()){
                //right sibling can lend key to N.
                //Should borrow the biggest key from right sibling.
                key leftMostKey = right.keys.get(0);
                right.deleteKey(0);
                N.addKey(leftMostKey);
                //update parent's key because right sibling's the least key is changed.
                updateKey(Parent, leftMostKey.key);
            }
            else{
                //this node cannot borrow key from right sibling. so should merge with right sibling.
                for(int i = N.keys.size() - 1; i >= 0; i--)
                    right.prependKey(N.keys.get(i));
                right.left = N.left;
                if(N.left != null) N.left.right = right;
                Parent.deleteChild(0);
                Parent.deleteKey(0);
                if(N == this.Leaf) this.Leaf = right;

                if(Parent != this.root && Parent.isUnderFlow())
                    balanceNonLeafNode(Parent); //balance NonLeafNode(parent).

                else if(Parent == this.root && Parent.keys.size() == 0){
                    //root should be deleted.
                    this.root = null;
                    right.parent = null;
                    N = null;
                    this.Leaf = right;
                    //right is the only leafNode now in tree.
                }
            }
        }
        else{
            //Node has both left and right sibling.
            if(left.isAffordable()){
                //left sibling can lend a key to N.
                key rightMostKey = left.keys.get(left.keys.size() - 1);
                left.deleteKey(left.keys.size() - 1);
                N.prependKey(rightMostKey);
                //update parent's key
                updateKey(Parent, N.keys.size() > 1 ? N.keys.get(1).key : deletedkKey);
            }
            else if(right.isAffordable()){
                //right sibling can lend a key to N.
                key leftMostKey = right.keys.get(0);
                right.deleteKey(0);
                N.addKey(leftMostKey);
                //Update parent's key
                updateKey(Parent, leftMostKey.key);
            }
            else{
                //cannot borrow key from any sibling.
                //should merge with sibling.
                //I will merge with left sibling.
                left.right = N.right;
                N.right.left = left;
                for(int i = 0; i<N.keys.size(); i++)
                    left.addKey(N.keys.get(i));
                left.sorting();

                Parent.deleteKey(Parent.findIndexOfChild(left));
                Parent.deleteChild(Parent.findIndexOfChild(N));
                N = null;
                if(Parent != this.root && Parent.isUnderFlow()){
                    //balance NonLeafNode;
                    balanceNonLeafNode(Parent);
                }
            }
        }
    }
    public void balanceNonLeafNode(NonLeafNode N) {
        NonLeafNode Parent = N.parent;
        int N_index = Parent.findIndexOfChild(N);
        //if this Node have left sibling assign left sibling or null.
        NonLeafNode left = N_index > 0 ? (NonLeafNode) Parent.childs.get(N_index - 1) : null;
        //if this Node have right sibling assign right sibling or null.
        NonLeafNode right = N_index < Parent.childs.size() - 1 ? (NonLeafNode) Parent.childs.get(N_index + 1) : null;

        if (left != null && left.isAffordable()) {
            //can borrow key from left sibling.
            int keyToDown = Parent.keys.get(N_index - 1);
            N.prependKey(keyToDown);
            Parent.deleteKey(N_index - 1);
            N.sorting();
            Parent.addKey(left.keys.get(left.keys.size() - 1));
            Parent.sorting();
            left.childs.get(left.childs.size() - 1).parent = N;
            //move child from left to N.
            N.prependChild(left.childs.get(left.childs.size() - 1));
            left.deleteKey(left.keys.size() - 1);
            left.deleteChild(left.childs.size() - 1);
        } else if (right != null && right.isAffordable()) {
            //can borrow key from right sibling.
            int keyToDown = Parent.keys.get(N_index);
            N.addKey(keyToDown);
            Parent.deleteKey(N_index);
            N.sorting();
            Parent.addKey(right.keys.get(0));
            Parent.sorting();
            right.childs.get(0).parent = N;
            //move child from right to N.
            N.addChild(right.childs.get(0));
            right.deleteKey(0);
            right.deleteChild(0);
        } else {
            //cannot borrow from any sibling.
            //Then should merge with sibling.
            boolean isRootChanged = false;
            if (left != null && !left.isAffordable()) {
                //merge with left sibling.
                int keyToDown = Parent.keys.get(N_index - 1);
                left.addKey(keyToDown);
                for (int i = 0; i < N.keys.size(); i++)
                    left.addKey(N.keys.get(i));
                left.sorting();
                Parent.deleteKey(N_index-1);
                Parent.deleteChild(N_index);
                for (int i = 0; i < N.childs.size(); i++) {
                    N.childs.get(i).parent = left;
                    left.addChild(N.childs.get(i));
                }
                N = null; //free N Node.
                if (Parent == this.root && Parent.keys.size() == 0) {
                    //root is deleted.
                    left.parent = null;
                    this.root = left;
                    isRootChanged = true;
                }
            } else {
                //merge with right sibling.
                int keyToDown = Parent.keys.get(N_index); //This key will be a new key in right node.
                right.addKey(keyToDown);
                for (int i = 0; i < N.keys.size(); i++)
                    right.addKey(N.keys.get(i));
                right.sorting();
                Parent.deleteKey(N_index);
                Parent.deleteChild(N_index);
                for (int i = N.childs.size() - 1; i >= 0; i--) {
                    N.childs.get(i).parent = right;
                    right.prependChild(N.childs.get(i));
                }
                N = null; //free N Node.
                if (Parent == this.root && Parent.keys.size() == 0) {
                    //root is deleted.
                    right.parent = null;
                    this.root = right;
                    isRootChanged = true;
                }
            }
            if (!isRootChanged && Parent != this.root && Parent.isUnderFlow())
                balanceNonLeafNode(Parent);
        }
    }
    public void updateKey(NonLeafNode Parent, int deletedKey){
        for(int i = 0; i<Parent.keys.size(); i++){
            if(deletedKey == Parent.keys.get(i)){
                int toChange = findLeftMostKey(Parent.childs.get(i+1));
                Parent.keys.set(i,toChange);
                return;
            }
        }
        if(Parent.parent != null)
            updateKey(Parent.parent, deletedKey);
    }
    public int findLeftMostKey(Node N){
        while(!(N instanceof LeafNode)){
            NonLeafNode temp = (NonLeafNode) N;
            N = temp.childs.get(0);
        }
        LeafNode leftMostLeaf = (LeafNode)N;
        //If M is less than 4, there is a case that leftMostLeaf has no key after deleting the key.
        if(leftMostLeaf.keys.size() == 0) return leftMostLeaf.right.keys.get(0).key;
        return leftMostLeaf.keys.get(0).key;
    }
    public void saveTree(FileWriter Fw, Node now){
        if(now instanceof NonLeafNode){
            NonLeafNode temp = (NonLeafNode) now;
            try{
                if(temp == this.root) {
                    Fw.write("N" + "\n"); //that means NonLeafNode.
                    Fw.write(temp.keys.size() + "\n");
                    for (int i = 0; i < temp.keys.size() - 1; i++) {
                        Fw.write(temp.keys.get(i) + ",");
                    }
                    Fw.write(temp.keys.get(temp.keys.size() - 1) + "\n");
                }
                Fw.write(temp.childs.size() + "\n");
                boolean chk = false;
                if(temp.childs.get(0) instanceof LeafNode) {
                    Fw.write("L" + "\n");
                    for(int i = 0; i<temp.childs.size(); i++) {
                        LeafNode child = (LeafNode)temp.childs.get(i);
                        Fw.write(child.keys.size() + "\n");
                        for(int j = 0; j<child.keys.size(); j++)
                            Fw.write(child.keys.get(j).key + "," + child.keys.get(j).value + "\n");
                    }
                }
                else{
                    Fw.write("N" + "\n");
                    chk = true;
                    for(int i = 0; i<temp.childs.size(); i++) {
                        NonLeafNode child = (NonLeafNode) temp.childs.get(i);
                        Fw.write(child.keys.size() + "\n");
                        for (int j = 0; j < child.keys.size() - 1; j++) {
                            Fw.write(child.keys.get(j) + ",");
                        }
                        Fw.write(child.keys.get(child.keys.size() - 1) + "\n");
                    }
                }
                if(chk) {
                    //if childs are NonLeafNode
                    for (int i = 0; i < temp.childs.size(); i++) {
                        saveTree(Fw, temp.childs.get(i));
                        //continue to write about the node.
                    }
                }
            }catch(IOException e){
                e.printStackTrace();
            }
        }
        else{
            try {
                Fw.write("L" + "\n");
                LeafNode temp = (LeafNode) now;
                Fw.write(temp.keys.size() + "\n");
                for(int i = 0; i<temp.keys.size(); i++)
                    Fw.write(temp.keys.get(i).key + "," + temp.keys.get(i).value + "\n");
            }catch(IOException e){
                e.printStackTrace();
            }
        }
    }
    public void makeTree(BufferedReader Br, NonLeafNode now, boolean isFirst) {
        try {
            if(isFirst) {
                //if root node have to me made.
                String kind = Br.readLine();
                if (kind.equals("N")) {
                    NonLeafNode N = new NonLeafNode();
                    //N will be a root.
                    int numOfKeys = Integer.parseInt(Br.readLine());
                    String line = Br.readLine();
                    String[] keys = line.split(",");
                    for (int i = 0; i < numOfKeys; i++)
                        N.addKey(Integer.parseInt(keys[i]));
                    this.root = N;
                    int numOfChilds = Integer.parseInt(Br.readLine());
                    kind = Br.readLine();
                    if (kind.equals("N")) {
                        for (int i = 0; i < numOfChilds; i++){
                            NonLeafNode newChild = new NonLeafNode();
                            numOfKeys = Integer.parseInt(Br.readLine());
                            line = Br.readLine();
                            String[] nums = line.split(",");
                            for (int j = 0; j < numOfKeys; j++)
                                newChild.addKey(Integer.parseInt(nums[j]));
                            newChild.parent = N;
                            N.addChild(newChild);
                        }
                        for(int i = 0; i<numOfChilds; i++)
                            makeTree(Br, (NonLeafNode) N.childs.get(i), false);
                    }
                    else{
                        for (int i = 0; i < numOfChilds; i++) {
                            numOfKeys = Integer.parseInt(Br.readLine());
                            List<key> newKeys = new ArrayList<>();
                            for(int j = 0; j<numOfKeys; j++){
                                line = Br.readLine();
                                String[] nums = line.split(",");
                                key Key = new key(Integer.parseInt(nums[0]), Integer.parseInt(nums[1]));
                                newKeys.add(Key);
                            }
                            LeafNode newChild = new LeafNode(newKeys);
                            newChild.parent = N;
                            N.addChild(newChild);

                            if(this.Leaf == null) this.Leaf = newChild;
                            else{
                                lastLeaf.right = newChild;
                                newChild.left = lastLeaf;
                            }
                            lastLeaf = newChild;
                        }
                    }
                }
            }
            else{
                //There is a root already.
                int numOfChild = Integer.parseInt(Br.readLine());
                boolean chk = false;
                String kind = Br.readLine();
                if (kind.equals("N")){
                    for(int i = 0; i<numOfChild; i++){
                        int numOfKeys = Integer.parseInt(Br.readLine());
                        NonLeafNode newChild = new NonLeafNode();
                        String line = Br.readLine();
                        String[] nums = line.split(",");
                        for (int j = 0; j < numOfKeys; j++)
                            newChild.addKey(Integer.parseInt(nums[j]));
                        newChild.parent = now;
                        now.addChild(newChild);
                    }
                    for(int i = 0; i<numOfChild; i++)
                        makeTree(Br, (NonLeafNode) now.childs.get(i), false);
                }
                else{
                    for(int i = 0; i<numOfChild; i++){
                        int numOfKeys = Integer.parseInt(Br.readLine());
                        List<key> newKeys = new ArrayList<>();
                        String line;
                        for(int j = 0; j<numOfKeys; j++){
                            line = Br.readLine();
                            String[] nums = line.split(",");
                            key Key = new key(Integer.parseInt(nums[0]), Integer.parseInt(nums[1]));
                            newKeys.add(Key);
                        }
                        LeafNode newChild = new LeafNode(newKeys);
                        newChild.parent = now;
                        now.addChild(newChild);

                        if(lastLeaf != null){
                            lastLeaf.right = newChild;
                            newChild.left = lastLeaf;
                        }
                        else this.Leaf = newChild;
                        lastLeaf = newChild;
                    }
                }
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public void makeTree(BufferedReader Br) {
        try {
            //if Tree has only one node now.
            String kind;
            if((kind = Br.readLine()) == null)
                return;
            if(kind.equals("L")){
                int numOfKeys = Integer.parseInt(Br.readLine());
                List<key> newKeys = new ArrayList<>();
                String line;
                for(int j = 0; j<numOfKeys; j++){
                    line = Br.readLine();
                    String[] nums = line.split(",");
                    key Key = new key(Integer.parseInt(nums[0]), Integer.parseInt(nums[1]));
                    newKeys.add(Key);
                }
                LeafNode N = new LeafNode(newKeys);
                this.Leaf = N;
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public static void print_tree(Node N){
        if(N == null) return;
        if(N.parent == null && !(N instanceof LeafNode)){
            //if root node.
            NonLeafNode temp = (NonLeafNode) N;
            for(int i = 0; i<temp.keys.size(); i++)
                System.out.print(temp.keys.get(i) + " ");
            System.out.println();
        }
        else if(N.parent == null && N instanceof LeafNode){
            LeafNode temp = (LeafNode) N;
            for(int i = 0; i<temp.keys.size(); i++)
                System.out.print(temp.keys.get(i).key + " ");
            System.out.println();
        }

        if(N instanceof NonLeafNode){
            NonLeafNode temp = (NonLeafNode) N;
            if(temp.childs.get(0) instanceof NonLeafNode) {
                for (int i = 0; i < temp.childs.size(); i++) {
                    NonLeafNode temp_child = (NonLeafNode) temp.childs.get(i);
                    for (int j = 0; j < temp_child.keys.size(); j++)
                        System.out.print(temp_child.keys.get(j) + " ");
                    System.out.print("  ");
                }
                System.out.println();
            }
            else{
                for (int i = 0; i < temp.childs.size(); i++) {
                    LeafNode temp_child = (LeafNode) temp.childs.get(i);
                    System.out.print("Leafs ");
                    for (int j = 0; j < temp_child.keys.size(); j++)
                        System.out.print(temp_child.keys.get(j).key + " ");
                    System.out.print("  ");
                }
                System.out.println();
            }
            for(int i = 0; i<temp.childs.size(); i++)
                print_tree(temp.childs.get(i));
        }
    }

    public static void main(String[] args) {
        String cmd = args[0];
        String index_file = args[1];
        BPlusTree BT = new BPlusTree();
        if(cmd != "-c") {
            try {
                BufferedReader IndexBr = new BufferedReader(new FileReader(index_file));
                String degree;
                //if index.dat file is not empty, try to retrieve Tree made already.
                if ((degree = IndexBr.readLine()) != null) {
                    BT.setM(Integer.parseInt(degree));
                    String haveRoot = IndexBr.readLine();
                    //Read data from index.dat and retrieve Tree.
                    if (haveRoot != null) {
                        if (haveRoot.equals("Root")) BT.makeTree(IndexBr, BT.root, true);
                        else BT.makeTree(IndexBr);
                    }
                }
                IndexBr.close();
            } catch (IOException e) {
                e.printStackTrace();
            }
        }

        switch (cmd) {
            case "-c": //create new index.dat file. degree is same as args[2].
                int degree = Integer.parseInt(args[2]);
                try {
                    FileWriter Fw = new FileWriter(index_file, false);
                    Fw.write(degree + "\n"); //write degree of the tree on top.
                    Fw.flush();
                    Fw.close();
                } catch (IOException e) {
                    e.printStackTrace();
                }
                break;
            case "-i": //insert keys in data_file
                try {
                    String data_file = args[2];
                    BufferedReader DataBr = new BufferedReader(new FileReader(data_file));
                    //Read data from input.csv
                    String line;
                    while ((line = DataBr.readLine()) != null) {
                        String[] num = line.split(","); //split the string (key, value)
                        int key = Integer.parseInt(num[0]);
                        int value = Integer.parseInt(num[1]);
                        BT.Insert(key, value);
                    }
                    FileWriter Fw = new FileWriter(index_file, false); //make new index_file
                    Fw.write(BT.M + "\n");
                    if (BT.root != null) {
                        Fw.write("Root\n");
                        BT.saveTree(Fw, BT.root);
                    } else{
                        Fw.write("Leaf\n");
                        BT.saveTree(Fw, BT.Leaf);
                    };
                    DataBr.close();
                    Fw.flush();
                    Fw.close();
                } catch (IOException e) {
                    e.printStackTrace();
                }
                break;
            case "-d": //delete keys in data_file
                try {
                    String data_file = args[2];
                    BufferedReader Br = new BufferedReader(new FileReader(data_file));
                    String line;
                    while ((line = Br.readLine()) != null) {
                        int keyToDelete = Integer.parseInt(line);
                        BT.Delete(keyToDelete);
                    }
                    FileWriter Fw = new FileWriter(index_file, false);
                    Fw.write(BT.M + "\n");
                    if (BT.root != null) {
                        Fw.write("Root\n");
                        BT.saveTree(Fw, BT.root);
                    } else{
                        Fw.write("Leaf\n");
                        BT.saveTree(Fw, BT.Leaf);
                    }
                    Br.close();
                    Fw.flush();
                    Fw.close();
                } catch (IOException e) {
                    e.printStackTrace();
                }
                break;
            case "-s": //search key
                int keyToSearch = Integer.parseInt(args[2]);
                BT.single_search_key(keyToSearch);
                break;
            case "-r":
                int from = Integer.parseInt(args[2]);
                int to = Integer.parseInt(args[3]);
                BT.range_search_key(from, to);
                break;
            case "-p":
                print_tree(BT.root != null ? BT.root : BT.Leaf);
        }
    }
}
