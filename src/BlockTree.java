import java.util.ArrayList;

/**
 * An instance is a non-empty collection of points organized in a hierarchical
 * binary tree structure.
 */
public class BlockTree {
	private BoundingBox box; // bounding box of the blocks contained in this
	// tree.

	private int numBlocks; // Number of blocks in this tree.

	private BlockTree left; // left subtree --null iff this is a leaf

	private BlockTree right; // right subtree --null iff this is a leaf

	private Block block; // The block of a leaf node --null if not a leaf

	// REMARK:
	// Leaf node: left, right == null && block != null
	// Intermediate node: left, right != null && block == null

	/**
	 * Constructor: a binary tree containing blocks. Precondition: blocks is
	 * nonempty, i.e. it contain at least one block.
	 */
	public BlockTree(ArrayList<Block> blocks) {
		// Leave the following two "if" statements as they are.
		if (blocks == null)
			throw new IllegalArgumentException("blocks null");
		if (blocks.size() == 0)
			throw new IllegalArgumentException("no blocks");

		box = BoundingBox.findBBox(blocks.iterator());
		numBlocks = blocks.size();

		if (blocks.size() == 1) {
			//is leaf
			block = blocks.get(0);
			left = null;
			right = null;
		}else {
			// is intermediate block
			numBlocks = blocks.size();
			block = null;

			ArrayList<Block> highVal = new ArrayList<Block>();
			ArrayList<Block> lowVal = new ArrayList<Block>();

			if (box.getHeight() > box.getWidth()) {

				for (Block block : blocks) {
					Double blockY = block.getPosition().y;
					if (blockY < box.getCenter().y) {
						lowVal.add(block);
					} else {
						highVal.add(block);
					}
				}
			}
			// if box.getHeight () <= box.getWidth()
			else {

				for (Block block : blocks) {
					Double blockX = block.getPosition().x;

					if (blockX < box.getCenter().x) {
						lowVal.add(block);
					} else {
						highVal.add(block);
					}
				}
			}
			//recursion portion
			left = new BlockTree(lowVal);
			right = new BlockTree(highVal);
		} 
	}

	/** Return the bounding box of the collection of blocks. */
	public BoundingBox getBox() {
		return box;
	}

	/** Return true iff this is a leaf node. */
	public boolean isLeaf() {
		return block != null;
	}

	/** Return true iff this is an intermediate node. */
	public boolean isIntermediate() {
		return !isLeaf();
	}

	/** Return the number of blocks contained in this tree. */
	public int getNumBlocks() {
		return numBlocks;
	}

	/** Return true iff this collection of blocks contains point p. */
	public boolean contains(Vector2D p) {
		// Caution. By "contains" we do NOT mean that the bounding box
		// of this block tree contains p. That is not enough. We mean
		// that one of the blocks in this BlockTree contains p.

		// if point outside bounding box
		if ((p.x > box.upper.x) || (p.x < box.lower.x) || (p.y > box.upper.y)
				|| (p.y < box.lower.y)) {
			return false;
		}
		
		//if intermediate block, go down trees
		if (block == null) {
			return right.contains(p) || left.contains(p);
		//is leaf, so compare block
		}else{
			return block.contains(p);
		}
	}

	/**
	 * Return true iff (this tree displaced by thisD) and (tree t displaced by
	 * d) overlap.
	 */
	public boolean overlaps(Vector2D thisD, BlockTree t, Vector2D d) {

		BoundingBox thisBox = this.box.displaced(thisD);
		BoundingBox treeBox = t.box.displaced(d);
		
		// if bounding boxes overlap, base case
		if (!(thisBox.overlaps(treeBox))) {
			return false;
		}
		// if both are leaves, base case
		if (this.isLeaf() && t.isLeaf()) {
			return true;
		}
		
		if (box.getLength() < t.getBox().getLength()) {
			return this.overlaps(thisD, t.left, d) || 
					this.overlaps(thisD, t.right, d);
		//box length is greater
		}else{
			return t.overlaps(d, this.left, thisD) ||
					t.overlaps(d, this.right, thisD);
		}
	}

	/** Return a representation of this instance. */
	public String toString() {
		return toString(new Vector2D(0, 0));
	}

	/** Return a representation of this tree displaced by d. */
	public String toString(Vector2D d) {
		return toStringAux(d, "");
	}

	/** Useful for creating appropriate indentation for function toString. */
	private static final String indentation = "   ";

	/**
	 * Return a representation of this instance displaced by d, with indentation
	 * indent.
	 * 
	 * @param d
	 *            Displacement vector.
	 * @param indent
	 *            Indentation.
	 * @return String representation of this tree (displaced by d).
	 */
	private String toStringAux(Vector2D d, String indent) {
		String str = indent + "Box: ";
		str += "(" + (box.lower.x + d.x) + "," + (box.lower.y + d.y) + ")";
		str += " -- ";
		str += "(" + (box.upper.x + d.x) + "," + (box.upper.y + d.y) + ")";
		str += "\n";

		if (isLeaf()) {
			String vStr = "(" + (block.position.x + d.x) + ","
					+ (block.position.y + d.y) + ")" + block.halfwidth;
			str += indent + "Leaf: " + vStr + "\n";
		} else {
			String newIndent = indent + indentation;
			str += left.toStringAux(d, newIndent);
			str += right.toStringAux(d, newIndent);
		}

		return str;
	}
}
