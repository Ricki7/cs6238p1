package utility;

import java.math.BigInteger;

public class Node {
	private int x;
	private BigInteger y;
	public int getX() {
		return x;
	}
	public void setX(int x) {
		this.x = x;
	}
	public BigInteger getY() {
		return y;
	}
	public void setY(BigInteger y) {
		this.y = y;
	}
	public String toString() {
        return ("("+getX()+", "+getY()+")");
   }
	
}
