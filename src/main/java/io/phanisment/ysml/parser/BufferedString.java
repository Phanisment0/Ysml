package io.phanisment.ysml.parser;

/**
 * A memory-efficient wrapper for a segment of a character buffer.
 * Instead of creating a new String object for every token, this class
 * points directly to the original data, significantly reducing GC pressure.
 */
public class BufferedString {
	public final char[] buffer;
	public final int start;
	public final int length;
	public final int line;

	public BufferedString(final char[] buffer, final int start, final int length, int line) {
		this.buffer = buffer;
		this.start = start;
		this.length = length;
		this.line = line;
	}

	/**
	 * Compares the content of this buffer segment with another object.
	 * This ensures that two different BufferedString objects pointing to 
	 * identical character sequences are treated as equal.
	 */
	@Override
	public boolean equals(Object o) {
		if (this == o) return true;
		if (!(o instanceof BufferedString that)) return false;

		// Fast fail: if lengths differ, they cannot be equal
		if (this.length != that.length) return false;

		// Character-by-character comparison to ensure content equality
		for (int i = 0; i < this.length; i++) if (this.buffer[this.start + i] != that.buffer[that.start + i]) return false;
		
		return true;
	}

	/**
	 * Generates a hash code based on the character content.
	 * Uses the same algorithm as java.lang.String (s[0]*31^(n-1) + s[1]*31^(n-2) + ...)
	 * to ensure consistent behavior when used in HashMaps.
	 */
	@Override
	public int hashCode() {
		int h = 0;
		// Content-based hashing is required because HashMaps rely on this 
		// to locate the correct bucket for the key.
		for (int i = 0; i < length; i++) h = 31 * h + buffer[start + i]; 
		return h;
	}

	/**
	 * Converts the buffered segment into a standard String.
	 * Note: This creates a new String object and should only be called 
	 * when an actual String representation is needed (e.g., logging or final output).
	 */
	@Override
	public String toString() {
		return new String(buffer, start, length);
	}
}
