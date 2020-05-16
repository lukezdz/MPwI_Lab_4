package pl.zdziarski.lukasz.utils;

public class Coord {
	private int x;
	private int y;

	public Coord(int x, int y) {
		setX(x);
		setY(y);
	}

	public int getX() {
		return x;
	}

	public int getY()
	{
		return y;
	}

	public void setX(int x)
	{
		this.x = x;
	}

	public void setY(int y)
	{
		this.y = y;
	}

	@Override
	public String toString()
	{
		return "Coord{" +
				"x=" + x +
				", y=" + y +
				'}';
	}

	@Override
	public boolean equals(Object obj)
	{
		return obj instanceof Coord && ((Coord)obj).getX() == getX() && ((Coord)obj).getY() == getY();
	}
}
