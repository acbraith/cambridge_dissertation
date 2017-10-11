package tests;

import demolitionUtilities.Matrix;

/**
 * Testing class for the Matrix class.
 * 
 * @author Alex Braithwaite
 *
 */
public class MatrixTester {
	public static void main(String[] args) {
		Matrix m1 = Matrix.rows(new double[][] { { 1, 2 }, { -3, 4 } });
		Matrix m2 = Matrix.columns(new double[][] { { 5, -6 }, { 7, -8 } });
		System.out.println("m1 = " + m1.toString());
		System.out.println("m2 = " + m2.toString());

		Matrix m3 = m1.multiplyRight(m2);
		System.out.println("m3 = m1 * m2");
		System.out.println("m3 = " + m3.toString());
		if (m3.equals(Matrix.rows(new double[][] { { -7, -9 }, { -39, -53 } })))
			System.out.println("PASS");
		else
			System.out.println("FAIL");

		Matrix m4 = m2.multiplyRight(m1);
		System.out.println("m4 = m2 * m1");
		System.out.println("m4 = " + m4.toString());
		if (m4.equals(Matrix.rows(new double[][] { { -16, 38 }, { 18, -44 } })))
			System.out.println("PASS");
		else
			System.out.println("FAIL");

		Matrix m5 = m1.multiplyLeft(m2);
		System.out.println("m5 = m2 * m1");
		System.out.println("m5 = " + m5.toString());
		if (m5.equals(Matrix.rows(new double[][] { { -16, 38 }, { 18, -44 } })))
			System.out.println("PASS");
		else
			System.out.println("FAIL");

		Matrix m6 = m1.transpose();
		System.out.println("m6 = m1'");
		System.out.println("m6 = " + m6.toString());
		if (m6.equals(Matrix.rows(new double[][] { { 1, -3 }, { 2, 4 } })))
			System.out.println("PASS");
		else
			System.out.println("FAIL");

		Matrix m7 = m1.add(m2);
		System.out.println("m7 = m1 + m2");
		System.out.println("m7 = " + m7.toString());
		if (m7.equals(Matrix.rows(new double[][] { { 6, 9 }, { -9, -4 } })))
			System.out.println("PASS");
		else
			System.out.println("FAIL");

	}
}
