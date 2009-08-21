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
 * Innerclass FibNode implements a node in Fibonacci-Heap.
 * 
 * @version 09 Jan 2006
 * @author Jarkko Miettinen
 */
public class FibNode
{
	private FibNode	left;

	private FibNode	child;

	private FibNode	parent;

	private FibNode	right;

	private Object	objectKey;

	private boolean	mark;

	private double	key;

	private int		id;

	private int		degree;

	
	public FibNode(double key)
	{
		this(key, null);
	}

	/**
	 * Creates a new FibNode with given id and given key if the key is >= 0.
	 * @param key
	 *            the key associated with this FibNode
	 * @param objectKey
	 *            some object possibly associated with this FibNode
	 */
	public FibNode(double key, Object objectKey)
	{
		this.degree = 0;
		this.parent = null;
		this.child = null;
		this.left = this;
		this.right = this;
		if (key >= 0.0)
		{
			this.key = key;
		} else
		{
			key = 0.0;
		}
		this.objectKey = objectKey;
	}

	/**
	 * Used to retrieve the Object assosiated with this FibNode
	 * 
	 * @return
	 */
	public Object getObjectKey()
	{
		return this.objectKey;
	}

	public void setObjectKey(Object o)
	{
		this.objectKey = o;
	}

	/**
	 * Sets this FibNodes parent and child references to null and degree to
	 * zero. Left and right FibNode are set to reference this.
	 */
	public void reset()
	{
		this.degree = 0;
		this.parent = this.child = null;
		this.left = this.right = this;
	}

	/**
	 * Returns parents.
	 * 
	 * @return parent <code>FibNode</code> of this node.
	 */
	public FibNode getParent()
	{
		return this.parent;
	}

	/**
	 * Sets new parent.
	 * 
	 * @param newParent
	 *            <code>FibNode</code> set to be this <code>FibNode's</code>
	 *            new parent.
	 */
	public void setParent(FibNode newParent)
	{
		this.parent = newParent;
	}

	/**
	 * Returns <code>FibNode</code> that is this <code>FibNode's</code>
	 * child. Returned <code>FibNode</code> is some <code>FibNode</code> on
	 * the doubly-linked list of childs.
	 * 
	 * @return child of this FibNode
	 */
	public FibNode getChild()
	{
		return this.child;
	}

	/**
	 * Sets <code>FibNode</code> pointing to this <code>FibNode's</code>
	 * child to newChild but doesn't modify the doubly-linked child-list in any
	 * way. This modification is done in FibHeap.
	 * 
	 * @param newChild
	 */
	public void setChild(FibNode newChild)
	{
		this.child = newChild;
	}

	/**
	 * Returns FibNode left from this node.
	 * 
	 * @return <code>FibNode</code> on the left side of this
	 *         <code>FibNode</code> on the doubly-linked childList / rootList.
	 */
	public FibNode getLeft()
	{
		return this.left;
	}

	/**
	 * Sets this <code>FibNode's</code> left <code>FibNode</code> to newLeft
	 * but doesn't alter the doubly-linked list in any way. This alteration is
	 * done in FibHeap.
	 * 
	 * @param newLeft
	 */
	public void setLeft(FibNode newLeft)
	{
		this.left = newLeft;
	}

	/**
	 * Returns FibNode right from this node.
	 * 
	 * @return <code>FibNode</code> on the right side of this
	 *         <code>FibNode</code> on the doubly-linked childList / rootList.
	 */
	public FibNode getRight()
	{
		return this.right;
	}

	/**
	 * Sets this <code>FibNode's</code> right <code>FibNode</code> to
	 * newRight but doesn't alter the doubly-linked list in any way. This
	 * alteration is done in FibHeap.
	 * 
	 * @param newLeft
	 */
	public void setRight(FibNode newRight)
	{
		this.right = newRight;
	}

	/**
	 * Returns boolean whether or not this <code>FibNode</code> has lost a
	 * child since the last time this was made the child of another node. Used
	 * in FibHeap's cascadingCut() method.
	 * 
	 * @return has this FibNode lost a child since last time it was made the
	 *         child of another node.
	 */
	public boolean getMark()
	{
		return this.mark;
	}

	/**
	 * Used to set whether or not this <code>FibNode</code> has lost a child
	 * since the last time this was made the child of another node. Used in
	 * FibHeap's link, cut and cascadingCut -methods.
	 * 
	 * @param mark
	 *            sets mark to given value.
	 */
	public void setMark(boolean mark)
	{
		this.mark = mark;
	}

	/**
	 * Returns number of children on this <code>FibNode's</code> childList
	 * this only exist as a doubly-linked list of references among the children.
	 * 
	 * @return number of children.
	 */
	public int getDegree()
	{
		return this.degree;
	}

	/**
	 * Increases this <code>FibNode's</code> number of children by one.
	 */
	public void increaseDegree()
	{
		this.degree++;
	}

	/**
	 * Decreases this <code>FibNode's</code> number of children by one.
	 */
	public void decreaseDegree()
	{
		if (--this.degree < 0) this.degree = 0;
	}

	/**
	 * Returns a <code>double</code> key associated with this
	 * <code>FibNode</code>. More modular approach would be to associate a
	 * Object as the key but performance would suffer in Dijkstra because of
	 * castings of Object to Double and retrieving it's double value I think.
	 * 
	 * @return this FibNode's key.
	 */
	public double getKey()
	{
		return this.key;
	}

	/**
	 * Sets new <code>double</code> key.
	 * 
	 * @param newKey
	 */
	public void setKey(double newKey)
	{
		this.key = newKey;
	}

	public String toString()
	{
		StringBuilder sb = new StringBuilder();
		return sb.toString();
	}

	// TODO maybe override hashcode and equals so that same id and same key ==
	// same node

	public boolean equals(Object o)
	{
		if (o instanceof FibNode)
		{
			return false;
		}
		return false;
	}

	public int hashCode()
	{
		return (new Double(key)).hashCode();
	}
}