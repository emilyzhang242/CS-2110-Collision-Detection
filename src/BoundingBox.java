import java.util.Iterator;

/** An instance is a 2D bounding box. */
public class BoundingBox {
	/** The corner of the bounding box with the smaller x,y coordinates. */
	public Vector2D lower; // (minX, minY)

	/** The corner of the bounding box with the larger x,y coordinates.	 */
	public Vector2D upper; // (maxX, maxY)

	/** Constructor: an instance is a copy of bounding box b.*/
	public BoundingBox(BoundingBox b) {
		lower= new Vector2D(b.lower);
		upper= new Vector2D(b.upper);
	}

	/** Constructor: An instance with lower as smaller coordinates and
	 * upper as larger coordinates. */
	public BoundingBox(Vector2D lower, Vector2D upper) {
		if (upper.x < lower.x)
			throw new IllegalArgumentException("invalid bbox");
		if (upper.y < lower.y)
			throw new IllegalArgumentException("invalid bbox");

		this.lower= lower;
		this.upper= upper;
	}

	/** Return the width of this bounding box (along x-dimension).  */
	public double getWidth() {
		return upper.x - lower.x;
	}

	/** Return the height of this bounding box (along y-dimension). */
	public double getHeight() {
		return upper.y - lower.y;
	}

	/** Return the larger of the width and height of this bounding box.  */
	public double getLength() {

		if (getWidth() > getHeight()) {
			return getWidth();
		} else {
			return getHeight();
		}
	}

	/** Return the center of this bounding box.  */
	public Vector2D getCenter() {

		double centerY = lower.y + getHeight()/2;
		double centerX = lower.x + getWidth()/2;
		Vector2D vector = new Vector2D(centerX, centerY);
		return vector;
	}

	/** Return the result of displacing this bounding box by d.  */
	public BoundingBox displaced(Vector2D d) {

		double lowerX = lower.x + d.x;
		double lowerY = lower.y + d.y;
		Vector2D lowerV = new Vector2D(lowerX, lowerY);
		double upperX = upper.x + d.x;
		double upperY = upper.y + d.y;
		Vector2D upperV = new Vector2D(upperX, upperY);
		BoundingBox box = new BoundingBox(lowerV, upperV);
		return box;
	}

	/** Return true iff this bounding box contains p.  */
	public boolean contains(Vector2D p) {
		boolean inX= lower.x <= p.x && p.x <= upper.x;
		boolean inY= lower.y <= p.y && p.y <= upper.y;
		return inX && inY;
	}

	/** Return the area of this bounding box.  */
	public double getArea() {

		return getWidth()*getHeight();
	}

	/** Return true iff this bounding box overlaps with box.  */
	public boolean overlaps(BoundingBox box) {

		// check x values
		if (this.lower.x >= box.upper.x) return false;
		else if (this.lower.x < box.lower.x && this.upper.x <= box.lower.x) {
			return false;
		}
		//check y values
		if (this.lower.y >= box.upper.y) return false;
		else if (this.lower.y < box.lower.y && this.upper.y <= box.lower.y) {
			return false;
		}
		return true;
	}


	/** Return the bounding box of blocks given by iter. */
	public static BoundingBox findBBox(Iterator<Block> iter) {
		// Do not modify the following "if" statement.
		if (!iter.hasNext()) 
			throw new IllegalArgumentException("empty iterator");

		Block block = iter.next();
		
		double upperX = block.getBBox().upper.x;
		double lowerX = block.getBBox().lower.x;
		double upperY = block.getBBox().upper.y;
		double lowerY = block.getBBox().lower.y;

		while (iter.hasNext()) {
			BoundingBox tempBox = iter.next().getBBox();

			if (tempBox.upper.y > upperY) 
				upperY = tempBox.upper.y;
			if (tempBox.lower.y < lowerY) 
				lowerY = tempBox.lower.y;
			if (tempBox.upper.x > upperX) 
				upperX = tempBox.upper.x;
			if (tempBox.lower.x < lowerX) 
				lowerX = tempBox.lower.x;
		}

		Vector2D upper = new Vector2D(upperX,upperY);
		Vector2D lower = new Vector2D(lowerX,lowerY);
		BoundingBox box = new BoundingBox(lower, upper);
		return box;
	}

	/** Return a representation of this bounding box. */
	public String toString() {
		return lower + " -- " + upper;
	}
}
