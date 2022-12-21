/**
 * Remembers an arbitrary number of values as if written down properly on a long notepad - slower than a scrap paper because it remembers the values in the given order!
 *
 * @author John Doe
 * @version 1.3, 11/28/21
 */
public final class IntegerSequenceMemory {
	private java.util.LinkedList<Integer> notepad = new java.util.LinkedList<>();

	/**
	 * Appends the given number at the end of the notepad.
	 *
	 * @param value the value to be appended to the end of the notepad
	 */
	public void remember(int value) {
		notepad.add(value);
	}

	/**
	 * Returns a deep copy of this notepad.
	 *
	 * @return a deep copy of this notepad
	 */
	@SuppressWarnings("unchecked")
	public IntegerSequenceMemory deepCopy() {
		IntegerSequenceMemory copy = new IntegerSequenceMemory();
		copy.notepad = (java.util.LinkedList<Integer>) notepad.clone();
		return copy;
	}

	/**
	 * Returns all the numbers appended to this notepad in the order they were originally appended.
	 *
	 * @return all the numbers appended to this notepad in the order they were originally appended
	 */
	public Integer[] recallAll() {
		return notepad.toArray(new Integer[0]);
	}
}
