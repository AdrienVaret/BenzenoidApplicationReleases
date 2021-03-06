package utils;

public class Couple<X, Y> {

	private X x;
	private Y y;

	public Couple(X x, Y y) {
		super();
		this.x = x;
		this.y = y;
	}

	public X getX() {
		return x;
	}

	public Y getY() {
		return y;
	}

	public void setX(X x) {
		this.x = x;
	}

	public void setY(Y y) {
		this.y = y;
	}

	@Override
	public boolean equals(Object obj) {
		@SuppressWarnings("rawtypes")
		Couple o = (Couple) obj;
		return x.equals(o.getX()) && y.equals(o.getY());
	}

	@Override
	public String toString() {
		return "(" + x.toString() + ", " + y.toString() + ")";
	}
}
