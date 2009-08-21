/*
    Copyright 2006-2007 VTT Biotechnology

    This file is part of MYLLY.

    MYLLY is free software; you can redistribute it and/or modify
    it under the terms of the GNU General Public License as published by
    the Free Software Foundation; either version 2 of the License, or
    (at your option) any later version.

    MYLLY is distributed in the hope that it will be useful,
    but WITHOUT ANY WARRANTY; without even the implied warranty of
    MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
    GNU General Public License for more details.

    You should have received a copy of the GNU General Public License
    along with MYLLY; if not, write to the Free Software
    Foundation, Inc., 51 Franklin St, Fifth Floor, Boston, MA  02110-1301  USA
*/
package guineu.modules.mylly.gcgcaligner.datastruct;

/**
 * Class FibHeap implements a Fibonacci-heap data-structure for Dijkstra's
 * algorithm
 * 
 * @see <A href="http://en.wikipedia.org/wiki/Fibonacci_heap">Wikipedia
 *      Fibonacci Heap entry</A><br>
 * @see <A
 *      href="http://www.cs.yorku.ca/~aaw/Jason/FibonacciHeapAnimation.html">Fibonacci
 *      Heap Animation</A><br>
 * 
 * @version 09 Jan 2006
 * @author Jarkko Miettinen
 */
public class FibHeap
{

	/* Below are the instance-fields of FibHeap. */
	private int					numberOfNodes;

	private FibNode				min;

	public static final double	GOLDEN_RATIO	= 1.618033988749894848;

	/**
	 * Creates an empty Fibonacci heap.
	 */
	public FibHeap()
	{
		this.min = null;
		this.numberOfNodes = 0;
	}

	/**
	 * @return number of FibNodes in heap
	 */
	public int getNumberOfNodes()
	{
		return this.numberOfNodes;
	}

	/**
	 * @return node with smallest key
	 */
	public FibNode getMin()
	{
		return this.min;
	}

	/**
	 * Adds node to heap's rootlist and if the current minimum key in rootlist
	 * is larger than insertedNode's key, make insertedNode the new minimum
	 * node.
	 * 
	 * @param insertedNode
	 *            node that is inserted to this heap.
	 */
	public void insert(FibNode insertedNode)
	{
		if (this.min != null)
		{
			insertedNode.setLeft(this.min);
			insertedNode.setRight(this.min.getRight());
			this.min.getRight().setLeft(insertedNode);
			this.min.setRight(insertedNode);
			if (insertedNode.getKey() < this.min.getKey()) this.min = insertedNode;
		} else
		{
			this.min = insertedNode;
			insertedNode.setLeft(insertedNode);
			insertedNode.setRight(insertedNode);
		}
		numberOfNodes++;
	}

	/**
	 * Add heap given as a parameter to this heap by uniting their rootLists.
	 * 
	 * @param addedHeap
	 * @return this FibHeap after heapAddedToThisHeap is added to it.
	 */
	public FibHeap union(FibHeap addedHeap)
	{
		FibNode startingPoint = addedHeap.getMin();
		FibNode temp = startingPoint;
		FibNode next;
		int heapSizeCounter = 0;
		if (temp != null) do
		{
			heapSizeCounter++;
			next = temp.getRight();
			addToRootList(temp);
			temp = next;
		} while (next != startingPoint);
		if (this.min == null
				|| (addedHeap.getMin() != null && (addedHeap.getMin().getKey() < this.min
																							.getKey()))) this.min = addedHeap
																																.getMin();
		this.numberOfNodes += heapSizeCounter;
		return this;
	}

	/**
	 * Unites two FibHeaps by first creating a new FibHeap, then continously
	 * calling extractMin() to both FibHeaps and then calling
	 * <code>insert(FibNode)</code> on new FibHeap to the extracted FibNodes.
	 * One could do this slower if one tried hard, but not much slower.
	 * 
	 * @param heap1
	 *            heap to unite with heap2
	 * @param heap2
	 *            heap to unite with heap1
	 * @return FibHeap that is union of heap1 and heap2
	 */
	public static FibHeap union(FibHeap heap1, FibHeap heap2)
	{
		FibHeap newHeap = new FibHeap();
		FibNode extracted;
		while ((extracted = heap1.extractMin()) != null)
		{
			newHeap.insert(extracted);
		}
		while ((extracted = heap2.extractMin()) != null)
		{
			newHeap.insert(extracted);
		}
		return newHeap;
	}

	private void link(FibNode newChild, FibNode newParent)
	{
		removeFromRootList(newChild);
		newChild.setParent(newParent);
		if (newParent.getChild() != null)
		{
			newChild.setLeft(newParent.getChild());
			newChild.setRight(newParent.getChild().getRight());
			newParent.getChild().getRight().setLeft(newChild);
			newParent.getChild().setRight(newChild);
		} else
		{
			newParent.setChild(newChild);
			newChild.setLeft(newChild);
			newChild.setRight(newChild);
		}
		newChild.setMark(false);
		newParent.increaseDegree();
	}

	private void consolidate()
	{
		// int maxDepth = (int)
		// Math.floor(Math.log((double)this.numberOfElements)/Math.log(GOLDEN_RATIO));
		// We make this one longer than should be possible to add null to end
		int maxDepth = this.numberOfNodes + 1;
		FibNode[] helperArray = new FibNode[maxDepth];
		for (int i = 0; i < helperArray.length; i++)
			helperArray[i] = null;
		FibNode x = this.min;
		FibNode currentPoint = x;
		int numberOfRootNodes = 0;
		if (currentPoint != null)
		{
			numberOfRootNodes++;
			currentPoint = currentPoint.getRight();
			while (currentPoint != this.min)
			{
				currentPoint = currentPoint.getRight();
				numberOfRootNodes++;
			}
		}
		while (numberOfRootNodes > 0)
		{
			int d = x.getDegree();
			FibNode next = x.getRight();
			while (helperArray[d] != null)
			{
				FibNode y = helperArray[d];
				if (x.getKey() > y.getKey())
				{
					y = x;
					x = helperArray[d];
				}
				link(y, x);
				helperArray[d] = null;
				d++;
			}
			helperArray[d] = x;
			x = next;
			numberOfRootNodes--;
		}
		this.min = null;
		for (int i = 0; i < helperArray.length; i++)
		{
			if ((x = helperArray[i]) != null)
			{
				addToRootList(x);
				if (this.min == null || (this.min.getKey() > x.getKey())){ this.min = x;}
			}
		}
	}

	private void addToRootList(FibNode node)
	{
		node.getRight().setLeft(node.getLeft());
		node.getLeft().setRight(node.getRight());
		node.setParent(null);
		if (this.min == null)
		{
			this.min = node;
			node.setLeft(node);
			node.setRight(node);
		} else
		{
			node.setRight(this.min.getRight());
			node.setLeft(this.min);
			this.min.getRight().setLeft(node);
			this.min.setRight(node);
			if (node.getKey() < this.min.getKey()) this.min = node;
		}
	}

	private void removeFromRootList(FibNode node)
	{
		node.getLeft().setRight(node.getRight());
		node.getRight().setLeft(node.getLeft());
	}

	public FibNode extractMin()
	{
		FibNode x = this.min;
		if (x != null)
		{
			FibNode currentChild = x.getChild();
			FibNode temp;
			int d = x.getDegree();
			for (int i = 0; i < d; i++)
			{
				temp = currentChild.getRight();
				addToRootList(currentChild);
				currentChild = temp;
			}
			removeFromRootList(x);
			if (x == x.getRight())
			{
				this.min = null;
			} else
			{
				this.min = x.getRight();
				consolidate();
			}
			this.numberOfNodes--;
		}
		return x;
	}

	/**
	 * Decreases the given FibNodes key to newKey if newKey isn't larger than
	 * given FibNodes current key. After that we balance the tree if balancing
	 * is needed.<br>
	 * Balancing is needed if heap-condition if violated, namely this given node
	 * now has smaller key than it's parent's key.
	 * 
	 * @param node
	 *            <code>FibNode</code> whose key we want to change.
	 * @param newKey
	 *            new <code>double</code> value of the key.
	 */
	public void decreaseKey(FibNode node, double newKey)
	{
		if (newKey < node.getKey())
		{
			node.setKey(newKey);
			FibNode parent = node.getParent();
			if (parent != null && node.getKey() < parent.getKey())
			{
				cut(node, parent);
				cascadingCut(parent);
			}
			if (node.getKey() < this.min.getKey()) this.min = node;
		}
	}

	private void cut(FibNode x, FibNode y)
	{
		x.getLeft().setRight(x.getRight());
		x.getRight().setLeft(x.getLeft());
		y.decreaseDegree();

		if (y.getDegree() == 0) y.setChild(null);
		else if (y.getChild() == x) y.setChild(x.getRight());
		addToRootList(x);
		x.setMark(false);
	}

	private void cascadingCut(FibNode y)
	{
		FibNode parent = y.getParent();
		if (parent != null)
		{
			if (!y.getMark()) y.setMark(true);
			else
			{
				cut(y, parent);
				cascadingCut(parent);
			}
		}
	}

	/**
	 * Returns string representation of this FibHeap.
	 * 
	 * @return String representation of this FibHeap. Format is following:<br>
	 *         Degree: 8 min: {id: 2 child: 3 left: 4 right: 6}
	 */
	public String toString()
	{
		return "Degree: " + this.numberOfNodes + " min: {" + this.min + '}';
	}
	
	public static void main(String args[])
	{
		test();
	}
	
	private static void test()
	{
		
	}
}
