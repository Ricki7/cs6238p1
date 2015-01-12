package utility;

import java.util.Arrays;

public class Statistics {
	int[] data;
	int size;

	public Statistics(int[] data) {
		this.data = data;
		size = data.length;
	}

	public int getMean() {
		int sum = 0;
		for (int a : data)
			sum += a;
		return sum / size;
	}

	public int getVariance() {
		int mean = getMean();
		int temp = 0;
		for (int a : data)
			temp += (mean - a) * (mean - a);
		return temp / size;
	}

	public int getStdDev() {
		return (int) Math.sqrt(getVariance());
	}

	public int median() {
		int[] b = new int[data.length];
		System.arraycopy(data, 0, b, 0, b.length);
		Arrays.sort(b);

		if (data.length % 2 == 0) {
			return (int) ((b[(b.length / 2) - 1] + b[b.length / 2]) / 2.0);
		} else {
			return b[b.length / 2];
		}
	}
}