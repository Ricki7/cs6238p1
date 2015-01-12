package logic;

import java.math.BigInteger;
import java.util.ArrayList;

import utility.Node;

public class InterpolationPolynomial {
	 public BigInteger getPwhd(ArrayList<Node> node_list) {
	        double[] values = {1.0, 2.0, 3.0};
	        double[] diffs = {1.0, 7.0, 6.0};

	        // Initialize result array
	        double[] result = new double[values.length];
	        for (int i = 0; i < values.length; ++i) {
	            result[i] = 0.0;
	        }

	        for (int i = 0; i < values.length; ++i) {
	            // 'poly' has a degree 'i'. We use 'i - 1' because only terms
	            // from 0 to 'i - 1' are used
	            double[] poly = getPoly(values, i - 1);

	            // Now add to result, do not forget to multiply by the divided
	            // difference !
	            for (int j = 0; j < poly.length; ++j) {
	                result[j] += poly[j] * diffs[i];
	            }
	        }

	        for (int i = 0; i < result.length; ++i) {
	            System.out.println("Coef for x^" + i + " is: " + result[i]);
	        }
	        return null;
	    }

	    public static double[] getPoly(double[] values, int i) {
	        // Start poly: 1.0, neutral value for multiplication
	        double[] coefs = {1.0};

	        // Accumulate values of products
	        for (int j = 0; j <= i; ++j) {
	            // 'coefsLocal' represent polynom of 1st degree (x - values[j])
	            double[] coefsLocal = {-values[j], 1.0};
	            coefs = getPolyProduct(coefs, coefsLocal);
	        }

	        return coefs;
	    }

	    public static double[] getPolyProduct(double[] coefs1, double[] coefs2) {
	        // Get lengths and degree
	        int s1 = coefs1.length - 1;
	        int s2 = coefs2.length - 1;
	        int degree = s1 + s2;

	        // Initialize polynom resulting from product, with null values
	        double[] coefsProduct = new double[degree + 1];
	        for (int k = 0; k <= degree; ++k) {
	            coefsProduct[k] = 0.0;
	        }

	        // Compute products
	        for (int i = 0; i <= s1; ++i)   {
	            for (int j = 0; j <= s2; ++j)   {
	                coefsProduct[i + j] += coefs1[i] * coefs2[j];
	            }
	        }
	        return coefsProduct;
	    }
}
