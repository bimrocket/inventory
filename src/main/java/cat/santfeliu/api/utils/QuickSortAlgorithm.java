package cat.santfeliu.api.utils;

public class QuickSortAlgorithm {
	private String input[];
	private int length;

	public void sort(String[] data) {

		if (data == null || data.length == 0) {
			return;
		}
		this.input = data;
		length = data.length;
		quickSort(0, length - 1);
	}

	/*
	 * This method implements in-place quicksort algorithm recursively.
	 */
	private void quickSort(int low, int high) {
		int i = low;
		int j = high;

		// pivot is middle index
		String pivot = input[low + (high - low) / 2];

		// Divide into two arrays
		while (i <= j) {
			/**
			 * As shown in above image, In each iteration, we will identify a number from
			 * left side which is greater then the pivot value, and a number from right side
			 * which is less then the pivot value. Once search is complete, we can swap both
			 * numbers.
			 */
			while (input[i].compareTo(pivot) < 0) {
				i++;
			}
			while (input[j].compareTo(pivot) > 0) {
				j--;
			}
			if (i <= j) {
				swap(i, j);
				// move index to next position on both sides
				i++;
				j--;
			}
		}

		// calls quickSort() method recursively
		if (low < j) {
			quickSort(low, j);
		}

		if (i < high) {
			quickSort(i, high);
		}
	}

	private void swap(int i, int j) {
		String temp = input[i];
		input[i] = input[j];
		input[j] = temp;
	}
}
