
/**
 *
 * AVLTree
 *
 * An implementation of a AVL Tree with
 * distinct integer keys and info
 *
 */

public class AVLTree {

	/**
	 * public boolean empty()
	 *
	 * returns true if and only if the tree is empty
	 *
	 */
	private AVLNode min; 
	private AVLNode max;               
	private AVLNode root;


	public boolean empty() {
		return root == null || root.key == -1; 
	}


	/**
	 * public String search(int k)
	 *
	 * returns the info of an item with key k if it exists in the tree
	 * otherwise, returns null
	 */
	public String search(int k)
	{
		if (this.empty())					//Case A: tree is empty
			return null;
		AVLNode result = searchNode(this.root, k);
		if(result != null)                           //Case B: tree is not empty
			return (result).value; 
		return null;
	}


	/**
	 * public int insert(int k, String i)
	 *
	 * inserts an item with key k and info i to the AVL tree.
	 * the tree must remain valid (keep its invariants).
	 * returns the number of rebalancing operations, or 0 if no rebalancing operations were necessary.
	 * returns -1 if an item with key k already exists in the tree.
	 */
	public int insert(int k, String i) {
		int rebalance_counter;
		AVLNode toInsert = new AVLNode (k, i);
		if (this.empty()) {               					 //Case A: Tree is empty
			this.root = toInsert;
			toInsert.setHeight(0);
			toInsert.insertVirtualChilds();
			rebalance_counter = 0;
			this.min = toInsert;							//upgrade minimum
			this.max = toInsert;								//upgrade max
		}
		else{
			if(isExist(k))										//Case B: k is already exist in the tree
				return -1;
			else {											        	//Case C: Insert a key to non-empty tree
				if(toInsert.key > this.max.key)
					this.max = toInsert;
				else
					if(toInsert.key<this.min.key)
						this.min = toInsert;
				rebalance_counter = Tree_Insert(this.root, toInsert); //send node to insertion

			}

		}

		return rebalance_counter;	
	}

	private int Tree_Insert(AVLNode root, AVLNode toInsert) {
		AVLNode parent = Tree_Position(root, toInsert.key);      // Get the last node before the insertion
		boolean is_leaf = parent.isLeaf();

		if (parent.getKey() > toInsert.key) 					//insert the node as right or left child
			parent.setLeft(toInsert);
		else 
			parent.setRight(toInsert);

		//update the details of the new node
		toInsert.setHeight(0);
		toInsert.setParent(parent);
		toInsert.insertVirtualChilds();

		if (!is_leaf)
			return 0;

		parent.promote();
		return rebalanceTreeInsert(parent);						//send the tree to rebalancing
	}

	//rebalance tree after insertion of new node
	private int rebalanceTreeInsert(AVLNode node) {
		int counter = 1;
		AVLNode parent = node.parent;
		int leftEdge;
		int rightEdge; 
		while (parent != null){
			if (parent.parent!= null) {
				if (parent.isValid() && parent.parent.isValid())
					break;
			}
			leftEdge = parent.getLeftEdge();
			rightEdge = parent.getRightEdge();

			if(leftEdge == 0 && rightEdge == 2) {												//case A: leftEdge = 0, rightEdge:

				if ((parent.left).getLeftEdge() == 1 && (parent.left).getRightEdge() == 2) {	//case A.1: leftLeftEdge = 1, leftRightEdge = 2
					RRotate(parent);
					parent.demote();
					counter += 2;
					break;
				}

				else
					if ((parent.left).getLeftEdge() == 2 && (parent.left).getRightEdge() == 1) {//Case A.2: leftLeftEdge = 2, leftRightEdge = 1
						parent.demote();
						(parent.left).demote();
						(parent.left.right).promote();
						LRRotate(parent);
						counter += 5;
						break;
					}	
			}

			else if (leftEdge == 2 && rightEdge == 0){											//Case B: leftEdge = 0, rightEdge = 0				
				if ((parent.right).getLeftEdge() == 2 && (parent.right).getRightEdge() == 1) {	//Case B.1: rightLeftEdgr = 2, rightRightEdge = 1
					LRotate(parent);
					parent.demote();
					counter += 2;
					break;
				}

				else
					if ((parent.right).getLeftEdge() == 1 && (parent.right).getRightEdge() == 2) {//Case B.2: rightLeftEdge =1, rightRightEdge = 2
						parent.demote();
						(parent.right).demote();
						(parent.right.left).promote();
						RLRotate(parent);
						counter += 5;
						break;
					}	


			}	
			// join tree's rebalancing special case solution:
			if ((leftEdge == 3 && rightEdge ==1) || (leftEdge == 1 && rightEdge ==3)){            
				if(leftEdge == 3 && rightEdge ==1) {											  //Case C: leftEdge = 3, rightEdge = 1
					if((parent.right).getLeftEdge() == 1 && (parent.right).getRightEdge() == 1) { //Case C.1: rightLeftEdge = 1, rightRightEdge = 1
						node.parent.demote();
						LRotate(node.parent);
						node.promote();

					}
				}
				else {																			//Case D:leftEdge = 1. rightEdge = 3
					if((parent.left).getLeftEdge() == 1 && (parent.left).getRightEdge() == 1) {	//Case C.2:leftLeftEdge = 1, leftRightEdge = 1
						node.parent.demote();
						RRotate(node.parent);
						node.promote();
					}

				}
				parent = node.parent;	
			}

			else if ((leftEdge == 1 && rightEdge == 0) || (rightEdge == 1 && leftEdge == 0 )){ //Case E: leftEdge = 1,rightEdge = 0 || leftEdge = 0, rightEdge = 1
				parent.promote();
				counter++;
				parent = parent.parent;

			}
			else
				parent = parent.parent;

		}

		return counter;
	}
	//returns the node with key k and increase the size of the nodes we go throught in the loop(using for update size while inserting new node)
	private AVLNode Tree_Position (AVLNode node, int k) {
		AVLNode pos = null;
		while (node.isRealNode()) {
			node.size ++;
			pos = node;
			if (k<node.key)
				node = (AVLNode) node.getLeft();
			else
				node = (AVLNode) node.getRight();

		}

		return pos;

	}
	//Right Rotate
	private void RRotate (AVLNode node) {
		AVLNode prev_parent = node.parent;
		AVLNode y = node.left;
		AVLNode y_right = y.right;
		y.parent = node.parent;
		y.right = node;
		node.left = y_right;
		y_right.parent = node;
		node.parent = y;

		if (prev_parent == null)				//update the root 
			root = y;
		else if (prev_parent.key > node.key)	//update left or right child
			prev_parent.left = y;
		else
			prev_parent.right = y;				

		node.size = node.left.size + node.right.size + 1;	//update size of node
		y.size = y.left.size + y.right.size + 1;			//update size of rotated node

	}
	//Left Rotate
	private void LRotate (AVLNode node) {
		AVLNode prev_parent = node.parent;
		AVLNode x = node.right;
		AVLNode x_left = x.left;
		x.parent = node.parent;
		x.left = node;
		node.right = x_left;
		x_left.parent = node;
		node.parent = x;

		if(prev_parent == null)					//update the root
			root = x;
		else if (prev_parent.key > node.key)	//update left or right child
			prev_parent.left = x;
		else 
			prev_parent.right = x;

		node.size = node.left.size + node.right.size + 1;
		x.size = x.left.size + x.right.size + 1;
	}
	//Double rotate 1: Left rotate and Right rotate
	private void LRRotate (AVLNode node) {
		this.LRotate(node.left);
		this.RRotate(node);
	}
	//Double rotate 2: right rotate and left rotate
	private void RLRotate (AVLNode node) {
		this.RRotate(node.right);
		this.LRotate(node);
	}
	/**
	 * public int delete(int k)
	 *
	 * deletes an item with key k from the binary tree, if it is there;
	 * the tree must remain valid (keep its invariants).
	 * returns the number of rebalancing operations, or 0 if no rebalancing operations were needed.
	 * returns -1 if an item with key k was not found in the tree.
	 */
	public int delete(int k)
	{
		int counter;
		if (!isExist(k))								//Case A: k not exist
			return -1;
		if (this.root.size == 1) {						//Case B: only one node left before the delete
			this.root = null;
			this.max = null;
			this.min = null;
			return 0;
		}
		AVLNode succ;
		AVLNode toDelete = searchNode(this.root, k);
		if (!(toDelete.isUnary()) && !(toDelete.isLeaf())) {  		//Case C: delete non-leaf and non-unary node
			succ = Successor(toDelete);
			this.decreasePath(succ.key);
			toDelete.key = succ.key;
			toDelete.value = succ.value;
			counter = Tree_Delete(succ);
			this.max = this.maxNode();
			this.min = this.minNode();
			return counter;
		}
		this.decreasePath(k);										
		counter = Tree_Delete(toDelete);                          //Case D: delete leaf or unary node
		this.max = this.maxNode();
		this.min = this.minNode();
		return counter;

	}

	private int Tree_Delete (AVLNode toDelete){
		AVLNode prevParent;
		if (toDelete.isLeaf()) {                             //Case 1: Node to delete is leaf
			prevParent = toDelete.parent;
			if (prevParent.right.key == toDelete.key) {
				prevParent.right = toDelete.right;           // Virtual node as right child of prevParent
				toDelete.right.parent = prevParent;
			}
			else {
				prevParent.left = toDelete.right;            //Virtual node as left child of prevParent
				toDelete.right.parent = prevParent;
			}

			if((prevParent.getLeftEdge() == 2 && prevParent.getRightEdge() == 1) || (prevParent.getLeftEdge() == 1 && prevParent.getRightEdge() == 2)) {
				return 0;
			}

			else {

				return rebalanceTreeDelete(prevParent);

			}
		}

		else {                                             	//Case 2: Node to delete is Unary
			prevParent = toDelete.parent;
			if (prevParent == null) {                                       // Case 2.1: the Unary node is a root
				if (toDelete.right != null && toDelete.right.key != -1) {
					this.root = toDelete.right;
					toDelete.right.parent = null;
					this.max = this.root;
					this.min = this.root;
					return 0;
				}
				else {                                                     //Case 2.2: The Unary node isn't the root
					this.root = toDelete.left;
					toDelete.left.parent = null;
					this.max = this.root;
					this.min = this.root;
					return 0;
				}

			}
			if (prevParent.right.key == toDelete.key) {
				if (toDelete.right.rank != -1) {
					prevParent.right = toDelete.right;
					toDelete.right.parent = prevParent;
				}
				else {
					prevParent.right = toDelete.left;
					toDelete.left.parent = prevParent;	
				}
			}

			else {
				if(toDelete.left.rank != -1) {
					prevParent.left = toDelete.left;
					toDelete.left.parent = prevParent;
				}
				else {
					prevParent.left = toDelete.right;
					toDelete.right.parent = prevParent;
				}
			}

			if ((prevParent.getLeftEdge() == 2 && prevParent.getRightEdge() == 1) || (prevParent.getLeftEdge() == 1 && prevParent.getRightEdge() == 2)) {
				return 0;
			}

			else {
				return rebalanceTreeDelete(prevParent);
			}


		}

	}
	//decrease the size of the nodes in the path from the root to node with key value of k by one
	private AVLNode decreasePath(int k) {
		AVLNode node = this.root;
		if (k == this.root.key) {
			this.root.size--;
			return node;
		}
		while (node.key != k) {
			node.size --;
			if (node.key > k) 
				node = node.left;
			else
				node = node.right;	
		}

		return node;

	}
	//find the successor of given node
	private AVLNode Successor (AVLNode node) {
		AVLTree temp = new AVLTree();
		AVLNode parent;
		if (node.right.isRealNode()) {
			temp.root = node.right;
			return temp.minNode();

		}

		parent = node.parent;
		while (parent != null && node.key == parent.right.key) {
			node = parent;
			parent = node.parent;
		}
		return parent;
	}


	//rebalance AVLTree after deletion
	private int rebalanceTreeDelete(AVLNode node) {
		int counter = 0;
		int leftEdge;
		int rightEdge;
		AVLNode parent = node;
		while (node != null) {
			leftEdge = node.getLeftEdge();
			rightEdge = node.getRightEdge();
			parent = node.parent;
			if (leftEdge == 2 && rightEdge == 2) {                                        //Case A: leftEdge = 2 and rightEdge = 2
				node.demote();
				counter++;
			}

			if (leftEdge == 3 && rightEdge == 1){                                        //Case B: leftEdge = 3 and rightEdge = 1
				if( node.right.getLeftEdge() == 1 && node.right.getRightEdge() == 1) {   //Case B.1: RightLeftEdge = 1 and RightRightEdge = 1
					node.demote();
					node.right.promote();
					LRotate(node);
					return counter + 3;
				}

				if (node.right.getLeftEdge() == 2 && node.right.getRightEdge() == 1) {   //Case B.2:  RightLeftEdge = 2 and RightRightEdge = 1

					node.demote();
					node.demote();
					LRotate(node);
					counter += 3;

				}
				else {
					if (node.right.getLeftEdge() == 1 && node.right.getRightEdge() == 2) { //Case B.3: RightLeftEdge = 1 and RightRightEdge = 2

						node.demote();
						node.demote();
						node.right.demote();
						node.right.left.promote();
						RRotate(node.right);
						LRotate(node);
						counter += 6;

					}
				}
			}

			if (leftEdge == 1 && rightEdge == 3) {                                     //Case C: leftEdge = 1 and rightEdge = 3 
				if(node.left.getLeftEdge() == 1 && node.left.getRightEdge() == 1) {    //Case C.1: LeftLeftEdge = 1 and LeftRightEdge = 1
					node.demote();
					node.left.promote();
					RRotate(node);
					return counter + 3;
				}

				if(node.left.getLeftEdge() == 1 && node.left.getRightEdge() == 2) {   //Case C.2: LeftLeftEdge = 1 and LeftRightEdge = 2

					node.demote();
					node.demote();
					RRotate(node);
					counter += 3;

				}

				else {
					if (node.left.getRightEdge() == 1 && node.left.getLeftEdge() == 2) { //Case C.3 LeftRightEdge = 1 and LeftLeftEdge = 2

						node.demote();
						node.demote();
						node.left.demote();
						node.left.right.promote();
						LRotate(node.left);
						RRotate(node);
						counter += 6;

					}
				}

			}
			node = parent;

		}

		return counter;
	}

	/**
	 * public String min()
	 *
	 * Returns the info of the item with the smallest key in the tree,
	 * or null if the tree is empty
	 */
	public String min()
	{
		if (this.root != null)
			return this.min.value;
		return null;
	}

	/**
	 * public String max()
	 *
	 * Returns the info of the item with the largest key in the tree,
	 * or null if the tree is empty
	 */
	public String max()
	{
		if (this.root != null)
			return this.max.value;
		return null;
	}


	/**
	 * public int[] keysToArray()
	 *
	 * Returns a sorted array which contains all keys in the tree,
	 * or an empty array if the tree is empty.
	 */
	public int[] keysToArray()
	{
		int[] emptyArr = {};
		if (root == null || root.rank == -1)
			return emptyArr;
		int[] arr = new int[root.size]; 
		keyToArray_rec(root, arr, 0);
		return arr;
	}

	private int keyToArray_rec(AVLNode node, int []arr, int index) {
		if (!node.isRealNode())
			return index;
		index = keyToArray_rec(node.left, arr, index);
		arr[index++] = node.key;
		index = keyToArray_rec(node.right, arr, index);
		return index;
	}

	/**
	 * public String[] infoToArray()
	 *
	 * Returns an array which contains all info in the tree,
	 * sorted by their respective keys,
	 * or an empty array if the tree is empty.
	 */
	public String[] infoToArray()
	{
		String[] emptyArr = {};
		if (root == null)
			return emptyArr;
		String[] arr = new String[root.size]; 
		infoToArray_rec(root, arr, 0);
		return arr;
	}

	private int infoToArray_rec(AVLNode node, String []arr, int index) {
		if (!node.isRealNode())
			return index;
		index = infoToArray_rec(node.left, arr, index);
		arr[index++] = node.value;
		index = infoToArray_rec(node.right, arr, index);
		return index;
	}

	/**
	 * public int size()
	 *
	 * Returns the number of nodes in the tree.
	 *
	 * precondition: none
	 * postcondition: none
	 */
	public int size()
	{
		if (this.root == null)
			return 0;
		return this.root.size; 
	}

	/**
	 * public int getRoot()
	 *
	 * Returns the root AVL node, or null if the tree is empty
	 *
	 * precondition: none
	 * postcondition: none
	 */
	public IAVLNode getRoot()
	{
		if (this.empty())
			return null;
		return this.root;
	}
	/**
	 * public string split(int x)
	 *
	 * splits the tree into 2 trees according to the key x. 
	 * Returns an array [t1, t2] with two AVL trees. keys(t1) < x < keys(t2).
	 * precondition: search(x) != null
	 * postcondition: none
	 */   
	public AVLTree[] split(int x)
	{
		AVLNode node = searchNode(this.root, x);
		AVLTree[] splittedArray = new AVLTree[2];
		AVLTree smallerTree = new AVLTree();
		AVLTree biggerTree = new AVLTree();
		smallerTree.root = node.left;
		biggerTree.root = node.right;
		smallerTree.root.parent = null;
		biggerTree.root.parent = null;
		node.right = null;
		node.left = null;
		if(smallerTree.root.rank == -1)                                       // Reset the final trees
			smallerTree = new AVLTree();
		if(biggerTree.root.rank == -1)
			biggerTree = new AVLTree();

		AVLNode X;
		if(node.key == this.root.key) {                                      //Case 1: x == root
			if(smallerTree.root != null) {
				smallerTree.min = smallerTree.minNode();
				smallerTree.max = smallerTree.maxNode();
			}
			if (biggerTree.root != null) {
				biggerTree.min = biggerTree.minNode();		
				biggerTree.max = biggerTree.maxNode();
			}
			splittedArray[0] = smallerTree;
			splittedArray[1] = biggerTree;
			return splittedArray;
		}
		while(node.parent != null) {                                          //Case 2: x != root
			if (node.key == node.parent.right.key) {                          //Case 2.1: Node is left child
				AVLTree temp = new AVLTree();
				temp.root = node.parent.left;
				temp.root.parent = null;
				temp.max = new AVLNode(-1,"tempMax");                        //temp node, for "null" prevention in Join function.
				temp.min = new AVLNode(-1,"tempMin");                        //temp node, for "null" prevention in Join function.
				node.parent.left = null;
				X = new AVLNode (node.parent.key, node.parent.value);
				smallerTree.join(X, temp);
			}
			else{                                                             //Case 2.2: Node is right child
				AVLTree temp = new AVLTree();
				temp.root = node.parent.right;
				temp.root.parent = null;
				temp.max = new AVLNode(-1,"tempMax");                      //temp node, for "null" prevention in Join function.
				temp.min = new AVLNode(-1,"tempMin");                      //temp node, for "null" prevention in Join function.
				node.parent.right = null;
				X = new AVLNode (node.parent.key, node.parent.value);
				biggerTree.join(X, temp);	  

			}
			node = node.parent;
		}


		if(smallerTree.root != null && smallerTree.root.rank != -1) {						//upgrade min and max to smallerTree
			smallerTree.min = smallerTree.minNode();
			smallerTree.max = smallerTree.maxNode();
		}

		if(biggerTree.root != null && biggerTree.root.rank != -1) {
			biggerTree.min = biggerTree.minNode();	                                    	//upgrade min and max to biggerTree
			biggerTree.max = biggerTree.maxNode();
		}	


		splittedArray[0] = smallerTree;					//create AVLTree array
		splittedArray[1] = biggerTree;
		return splittedArray;

	}



	//find and returns a AVLNode with key value of k in a tree with root - node. returns null, otherwise.
	private static AVLNode searchNode(AVLNode node, int k) {
		if (!node.isRealNode())
			return null;
		if (node.key == k)
			return node;
		if (k>node.key)
			return searchNode(node.right,k);
		else
			return searchNode(node.left, k);

	}


	/**
	 * public join(IAVLNode x, AVLTree t)
	 *
	 * joins t and x with the tree. 	
	 * Returns the complexity of the operation (rank difference between the tree and t)
	 * precondition: keys(x,t) < keys() or keys(x,t) > keys()
	 * postcondition: none
	 */   
	public int join(IAVLNode x, AVLTree t)
	{	
		AVLNode X = (AVLNode) x;
		AVLTree largerTree;
		AVLTree smallerTree;
		int smallerTreeRank;
		int largerTreeRank;


		if(this.root == null && t.root == null) {                   // Case 1: 2 empty trees.
			this.root = X;
			X.insertVirtualChilds();
			X.size = 1;
			X.parent = null;
			this.max = X;
			this.min = X;
			return 1;
		}

		AVLNode newMax;
		AVLNode newMin;

		if(this.root == null && t.root != null) {                   //Case 2: One of the trees is empty
			if(t.root.key > X.key) {
				newMin = X;
				newMax = t.max;
			}
			else {
				newMax = X;
				newMin = t.min;
			}
			t.insert(X.key, X.value);
			this.root = t.root;
			this.max = newMax;
			this.min = newMin;
			return t.root.rank +2;
		}

		if(this.root != null && t.root == null) {
			if(this.root.key > X.key) {
				newMin = X;
				newMax = this.max;
			}
			else {
				newMax = X;
				newMin = this.min;
			}
			this.insert(X.key, X.value);
			this.max = newMax;
			this.min = newMin;
			return this.root.rank +2;
		}


		if (this.root.key > X.key) {                                 //Check which tree is larger
			largerTree = this;
			smallerTree = t;
		}
		else {
			largerTree = t;
			smallerTree = this;
		}


		smallerTreeRank = smallerTree.root.rank;
		largerTreeRank = largerTree.root.rank;
		newMin = smallerTree.min;
		newMax = largerTree.max;


		if(largerTreeRank == smallerTreeRank) {	                   //Case 3: Trees with same height
			X.rank = largerTreeRank + 1;
			X.size = largerTree.root.size + smallerTree.root.size + 1 ;
			X.left = smallerTree.root;
			X.right = largerTree.root;
			largerTree.root.parent = X;
			smallerTree.root.parent = X;
			X.parent = null;
			this.root = X;
			this.max = newMax;
			this.min = newMin;
			return 1;
		}


		if (largerTreeRank > smallerTreeRank) {						//Case 4: Larger tree is higher (Left Join)
			largerTree.leftJoin(X, smallerTree);
			this.root = largerTree.root;
			this.max = newMax;
			this.min = newMin;
			return (largerTreeRank - smallerTreeRank + 1);
		}

		else {
			largerTree.rightJoin(X, smallerTree);	
			this.root = smallerTree.root;							//Case 5: Smaller tree is higher (Right Join)
			this.min = newMin;
			this.max = newMax;
			return  (smallerTreeRank - largerTreeRank + 1);
		}

	} 


	//join, Case 4 rebalancing (Tree with larger keys is higher)
	private void leftJoin (AVLNode X, AVLTree smallerTree) {
		AVLNode node = this.root;							//find the place to insert X
		while (node.rank > smallerTree.root.rank) {
			node.size += smallerTree.root.size + 1;
			node = node.left;
		}
		AVLNode prevParent = node.parent;						
		X.size = smallerTree.root.size + node.size +1;
		int k = smallerTree.root.rank;
		X.right = node;                                    //insertion of X
		X.left = smallerTree.root;
		X.parent = prevParent;
		prevParent.left = X;
		node.parent = X;
		smallerTree.root.parent = X;
		X.rank = k+1;
		if (prevParent.rank == k + 2)							//Case A: the tree is rebalanced after one promotion
			return;
		X.parent.promote();
		this.rebalanceTreeInsert(X);							//Case B: the tree is not rebalanced after a promotion

	}
	//join, Case 5 rebalancing  (Tree with smaller keys is higher)
	private void rightJoin (AVLNode X, AVLTree smallerTree) { 
		AVLNode node = smallerTree.root;
		while (node.rank > this.root.rank) {				//find the place to insert X
			node.size += this.root.size + 1;				
			node = node.right;
		}

		AVLNode prevParent = node.parent;					
		X.size = node.size + this.root.size +1;
		int k = this.root.rank;
		prevParent.right = X;
		X.parent = prevParent;                           //insertion of X
		X.left = node;
		node.parent = X;
		X.right = this.root;
		this.root.parent = X;
		X.rank = k+1;
		if (X.parent.rank == k + 2)							//Case A: the tree is rebalanced after one promotion
			return;
		X.parent.promote();	
		smallerTree.rebalanceTreeInsert(X);					//Case B: the tree is not rebalanced after one promotion


	}


	//returns true if the key is exist in the tree, false otherwise.
	private boolean isExist(int k) {
		if (this.root == null)
			return false;
		if (this.root.key == -1)
			return false;
		AVLNode root = this.root;
		return isExist_rec(root, k);

	}

	private static boolean isExist_rec (AVLNode node, int k) {
		int currKey = node.getKey();
		if (currKey == k) {
			return true;
		}
		AVLNode left = (AVLNode) node.getLeft();
		AVLNode right = (AVLNode) node.getRight();

		if(currKey > k && left.isRealNode()) 
			return isExist_rec(left, k);

		if (currKey < k && right.isRealNode())
			return isExist_rec(right, k);

		return false;
	}

	//returns the node with the smallest key
	private AVLNode minNode() { 

		return minNode_rec(this.root);			//send root to recursive function

	}
	private static AVLNode minNode_rec(AVLNode root) {
		if(!root.left.isRealNode())
			return root;
		return minNode_rec(root.left);

	}

	//returns the node with the largest key
	private AVLNode maxNode(){ 

		return maxNode_rec(this.root);			//send  root to recursive function

	}

	private static AVLNode maxNode_rec(AVLNode root) {
		if(!root.right.isRealNode())
			return root;
		return maxNode_rec(root.right);

	}


	/**
	 * public interface IAVLNode
	 * ! Do not delete or modify this - otherwise all tests will fail !
	 */
	public interface IAVLNode{	
		public int getKey(); //returns node's key (for virtuval node return -1)
		public String getValue(); //returns node's value [info] (for virtuval node return null)
		public void setLeft(IAVLNode node); //sets left child
		public IAVLNode getLeft(); //returns left child (if there is no left child return null)
		public void setRight(IAVLNode node); //sets right child
		public IAVLNode getRight(); //returns right child (if there is no right child return null)
		public void setParent(IAVLNode node); //sets parent
		public IAVLNode getParent(); //returns the parent (if there is no parent return null)
		public boolean isRealNode(); // Returns True if this is a non-virtual AVL node
		public void setHeight(int height); // sets the height of the node
		public int getHeight(); // Returns the height of the node (-1 for virtual nodes)
	}

	/**
	 * public class AVLNode
	 *
	 * If you wish to implement classes other than AVLTree
	 * (for example AVLNode), do it in this file, not in 
	 * another file.
	 * This class can and must be modified.
	 * (It must implement IAVLNode)
	 */
	public class AVLNode implements IAVLNode{

		private int key;
		private String value;
		private AVLNode left;
		private AVLNode right;
		private AVLNode parent;
		private int rank;        
		private int size;

		public AVLNode () {
		}

		public AVLNode (int key, String i) {
			this.value = i;
			this.key = key;
			this.size = 1;
		}

		public int getKey()
		{
			return this.key; 
		}

		public String getValue()
		{
			return this.value; 
		}
		public void setLeft(IAVLNode node)
		{
			this.left = (AVLNode) node; 
		}
		public IAVLNode getLeft()
		{
			return this.left; 
		}
		public void setRight(IAVLNode node)
		{
			this.right = (AVLNode) node; 
		}
		public IAVLNode getRight()
		{
			return this.right; 
		}
		public void setParent(IAVLNode node)
		{
			this.parent = (AVLNode) node;
		}
		public IAVLNode getParent()
		{
			return this.parent; 
		}
		// Returns True if this is a non-virtual AVL node
		public boolean isRealNode()
		{
			return this.rank != -1;
		}
		public void setHeight(int height)
		{
			this.rank = height;
		}
		public int getHeight()
		{
			return this.rank; 
		}

		//change a node to "virtual node"
		private void setVirtual() {	
			this.rank = -1;
			this.key = -1;
			this.size = 0;
		}
		//insert 2 virtual nodes as kids to node
		private void insertVirtualChilds() {
			AVLNode vr1 = new AVLNode();
			AVLNode vr2 = new AVLNode();
			vr1.setParent(this);
			vr2.setParent(this);
			vr1.setVirtual();
			vr2.setVirtual();
			this.setRight(vr1);
			this.setLeft(vr2);

		}
		//return delta rank between parent and his left child
		private int getLeftEdge() {
			return this.rank - this.left.rank;
		}
		//return delta rank between parent and his right child
		private int getRightEdge() {
			return this.rank - this.right.rank;
		}
		//return true if node is leaf, false otherwise.
		private boolean isLeaf() {
			return ((!this.right.isRealNode())&&(!this.left.isRealNode()));
		}
		//return true if node is unary, false otherwise.
		private boolean isUnary () {
			return (this.left.isRealNode() && !(this.right.isRealNode()) || !(this.left.isRealNode()) && this.right.isRealNode());  
		}
		//promote the rank (and height) of given node
		private void promote() {
			this.setHeight(this.rank + 1);
		}

		//demote the rank (and height) of given node
		private void demote() {
			this.setHeight(this.rank - 1);
		}
		private boolean isValid() {
			int left = this.getLeftEdge();
			int right = this.getRightEdge();
			return (left == 1 && right == 1 || left == 1 && right == 2 || right == 2 && left == 1);
		}
	}

}

