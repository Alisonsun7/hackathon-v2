package sorteddata.avltree;

import java.util.Comparator;

class AVLNodeFilled<T> extends AVLNode<T> {
	final AVLNode<T> left, right;
	final T value;
	private final int height, balance, size;
	public AVLNodeFilled(Comparator<T> comparator, T value, AVLNode<T> left, AVLNode<T> right) {
		super(comparator);
		this.value = value;
		this.left = left;
		this.right = right;
		this.size = left.size() + right.size() + 1;
		this.height = Math.max(left.height(), right.height())+1;

		// TODO: Overwrite the following line to correctly compute the tree's balance factor
		this.balance = this.left.height() - this.right.height();
	}

	public int height() {
		return height;
	}
	public int balanceFactor() {
		return balance;
	}
	public int size() {
		return size;
	}

	public String toString() {
		if (left instanceof AVLNodeEmpty<T> && right instanceof AVLNodeEmpty<T>)
			return value.toString();
		else
			return "%s -> (%s, %s)".formatted(value.toString(), left.toString(), right.toString());
	}

	public AVLNodeFilled<T> insert(T element) {
		// TODO: Complete this method
		int compResult = comparator.compare(element, value);

		AVLNodeFilled<T> newNode;
		if (compResult < 0) {
			newNode = new AVLNodeFilled<>(comparator, value, left.insert(element), right);
		} else if (compResult > 0) {
			newNode = new AVLNodeFilled<>(comparator, value, left, right.insert(element));
		} else {
			return this;
		}

		int balance = newNode.balanceFactor();

		// LL
		if (balance > 1 && comparator.compare(element, ((AVLNodeFilled<T>) newNode.left).value) < 0) {
			return newNode.rightRotate();
		}

		// RR
		if (balance < -1 && comparator.compare(element, ((AVLNodeFilled<T>) newNode.right).value) > 0) {
			return newNode.leftRotate();
		}

		// LR
		if (balance > 1 && comparator.compare(element, ((AVLNodeFilled<T>) newNode.left).value) > 0) {
			AVLNodeFilled<T> leftRotated = ((AVLNodeFilled<T>) newNode.left).leftRotate();
			newNode = new AVLNodeFilled<>(comparator, newNode.value, leftRotated, newNode.right);
			return newNode.rightRotate();
		}

		// RL
		if (balance < -1 && comparator.compare(element, ((AVLNodeFilled<T>) newNode.right).value) < 0) {
			AVLNodeFilled<T> rightRotated = ((AVLNodeFilled<T>) newNode.right).rightRotate();
			newNode = new AVLNodeFilled<>(comparator, newNode.value, newNode.left, rightRotated);
			return newNode.leftRotate();
		}
		return newNode;

	}

	/**
	 * Executes a left rotation on the current node, as defined
	 * by the AVL Tree algorithm.
	 * @return the new node taking this node's place after rotation
	 */
	private AVLNodeFilled<T> leftRotate() {
		// TODO: Complete this method
		AVLNodeFilled<T> X = this;
		AVLNodeFilled<T> Y = (AVLNodeFilled<T>) X.right;
		AVLNode<T> A = X.left;
		AVLNode<T> B = Y.left;
		AVLNode<T> C = Y.right;
		return new AVLNodeFilled<>(
				this.comparator, Y.value,
				new AVLNodeFilled<>(this.comparator, X.value, A, B),
				C
		);
	}

	/**
	 *       Y                            X
	 *     /   \                       /    \
	 *    X     C        ->           A      Y
	 *  /   \                               /  \
	 * A     B                             B    C
	 *
	 * Executes a right rotation on the current node, as defined
	 * by the AVL Tree algorithm.
	 * @return the new node taking this node's place after rotation
	 */
	private AVLNodeFilled<T> rightRotate() {
		// TODO: Complete this method
		AVLNodeFilled<T> Y = this;
		AVLNodeFilled<T> X = (AVLNodeFilled<T>) Y.left;
		AVLNode<T> A = X.left;
		AVLNode<T> B = X.right;
		AVLNode<T> C = Y.right;
		return new AVLNodeFilled<>(
				this.comparator, X.value, A,
				new AVLNodeFilled<>(this.comparator, Y.value, B, C)
		);
	}

	public T getAtIndex(int i) {
		if (i < left.size()) return left.getAtIndex(i);
		else if (i == left.size()) return value;
		return right.getAtIndex(i - left.size() - 1);
	}

	public boolean contains(T element) {
		if (comparator.compare(value, element) < 0) {
			return right.contains(element);
		} else if (comparator.compare(element, value) < 0) {
			return left.contains(element);
		}
		return true;
	}

	public T get(T element) {
		if (comparator.compare(value, element) < 0) {
			return right.get(element);
		} else if (comparator.compare(element, value) < 0) {
			return left.get(element);
		}
		return value;
	}
}
