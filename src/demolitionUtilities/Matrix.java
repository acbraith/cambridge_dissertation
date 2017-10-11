package demolitionUtilities;

import java.util.Arrays;

/**
 * Immutable class for a matrix or vector.
 * 
 * @author Alex Braithwaite
 *
 */
public class Matrix {

	private int rows;
	private int columns;

	private double[][] matrix;

	/**
	 * Create a matrix populated with zeros.
	 * 
	 * @param rows
	 * @param columns
	 */
	public Matrix(int rows, int columns) {
		this.rows = rows;
		this.columns = columns;
		this.matrix = new double[rows][columns];
	}

	/**
	 * Create a matrix populated with zeros.
	 * 
	 * @param rows
	 * @param columns
	 * @return
	 */
	public static Matrix makeZero(int rows, int columns) {
		return new Matrix(rows, columns);
	}

	/**
	 * Create a matrix from an array of arrays in row major order.
	 * 
	 * @param rscs
	 * @return
	 */
	public static Matrix rows(double[][] rscs) {
		int rows = rscs.length;
		int columns = rscs[0].length;
		Matrix m = new Matrix(rows, columns);
		for (int r = 0; r < rows; r++) {
			for (int c = 0; c < columns; c++) {
				m.matrix[r][c] = rscs[r][c];
			}
		}
		return m;
	}

	/**
	 * Create a row vector from an array.
	 * 
	 * @param rs
	 * @return
	 */
	public static Matrix rows(double[] rs) {
		int rows = rs.length;
		int columns = 1;
		Matrix m = new Matrix(rows, columns);
		for (int r = 0; r < rows; r++) {
			m.matrix[r][0] = rs[r];
		}
		return m;
	}

	/**
	 * Create a matrix from an array of arrays in column major order.
	 * 
	 * @param csrs
	 * @return
	 */
	public static Matrix columns(double[][] csrs) {
		Matrix m = rows(csrs);
		return m.transpose();
	}

	/**
	 * Create a column vector from an array.
	 * 
	 * @param cs
	 * @return
	 */
	public static Matrix columns(double[] cs) {
		Matrix m = rows(cs);
		return m.transpose();
	}

	public int countRows() {
		return rows;
	}

	public int countColumns() {
		return columns;
	}

	/**
	 * Multiply two matrices.
	 * 
	 * @param A
	 * @param B
	 * @return A*B
	 */
	private Matrix multiply(Matrix A, Matrix B) {
		if (A.columns != B.rows) {
			throw new IllegalArgumentException("Matrix dimensions do not match, supplied " + A.rows + "x" + A.columns
					+ " and " + B.rows + "x" + B.columns);
		}

		Matrix C = Matrix.makeZero(A.rows, B.columns);
		for (int i = 0; i < A.rows; i++) {
			for (int j = 0; j < B.columns; j++) {
				for (int k = 0; k < A.columns; k++) {
					C.matrix[i][j] += A.matrix[i][k] * B.matrix[k][j];
				}
			}
		}
		return C;
	}

	/**
	 * Get an element from this matrix.
	 * 
	 * @param row
	 * @param column
	 * @return
	 */
	public double get(int row, int column) {
		if (row >= this.rows || row < 0 || column >= this.columns || column < 0) {
			throw new IllegalArgumentException("Values out of range, requested " + row + "," + column
					+ ", but matrix is only " + this.rows + "x" + this.columns);
		}
		return matrix[row][column];
	}

	/**
	 * 
	 * @param m
	 * @return this + m
	 */
	public Matrix add(Matrix m) {
		if (m.rows != this.rows || m.columns != this.columns) {
			throw new IllegalArgumentException("Matrix dimensions do not match, supplied " + this.rows + "x"
					+ this.columns + " and " + m.rows + "x" + m.columns);
		}
		Matrix m2 = new Matrix(this.rows, this.columns);
		for (int r = 0; r < rows; r++) {
			for (int c = 0; c < columns; c++) {
				m2.matrix[r][c] = this.matrix[r][c] + m.matrix[r][c];
			}
		}
		return m2;
	}

	/**
	 * Add a constant to all values in this matrix.
	 * 
	 * @param a
	 * @return
	 */
	public Matrix add(double a) {
		Matrix m = new Matrix(this.rows, this.columns);
		for (int r = 0; r < rows; r++) {
			for (int c = 0; c < columns; c++) {
				m.matrix[r][c] = this.matrix[r][c] + a;
			}
		}
		return m;
	}

	/**
	 * @param m
	 * @return this * m
	 */
	public Matrix multiplyRight(Matrix m) {
		return multiply(this, m);
	}

	public Matrix multiply(double s) {
		Matrix m = new Matrix(this.rows, this.columns);
		for (int r = 0; r < rows; r++) {
			for (int c = 0; c < columns; c++) {
				m.matrix[r][c] = this.matrix[r][c] * s;
			}
		}
		return m;
	}

	/**
	 * @param m
	 * @return m * this
	 */
	public Matrix multiplyLeft(Matrix m) {
		return multiply(m, this);
	}

	public Matrix multiplyElements(Matrix m) {
		if (m.rows != this.rows || m.columns != this.columns) {
			throw new IllegalArgumentException("Matrix dimensions do not match, supplied " + this.rows + "x"
					+ this.columns + " and " + m.rows + "x" + m.columns);
		}
		Matrix m2 = new Matrix(this.rows, this.columns);
		for (int r = 0; r < rows; r++) {
			for (int c = 0; c < columns; c++) {
				m2.matrix[r][c] = this.matrix[r][c] * m.matrix[r][c];
			}
		}
		return m2;
	}

	/**
	 * Transpose this matrix.
	 * @return
	 */
	public Matrix transpose() {
		Matrix m = new Matrix(this.columns, this.rows);
		for (int r = 0; r < rows; r++) {
			for (int c = 0; c < columns; c++) {
				m.matrix[c][r] = this.matrix[r][c];
			}
		}
		return m;
	}

	@Override
	public String toString() {
		String s = "[";
		for (int r = 0; r < rows; r++) {
			String a = Arrays.toString(matrix[r]);
			s += a.substring(1, a.length() - 1);
			s += " ; ";
		}
		s = s.substring(0, s.length() - 3);
		s += "]";
		return s;
	}

	@Override
	public int hashCode() {
		final int prime = 31;
		int result = 1;
		result = prime * result + columns;
		result = prime * result + Arrays.deepHashCode(matrix);
		result = prime * result + rows;
		return result;
	}

	@Override
	public boolean equals(Object obj) {
		if (this == obj)
			return true;
		if (obj == null)
			return false;
		if (getClass() != obj.getClass())
			return false;
		Matrix other = (Matrix) obj;
		if (columns != other.columns)
			return false;
		if (!Arrays.deepEquals(matrix, other.matrix))
			return false;
		if (rows != other.rows)
			return false;
		return true;
	}

}
