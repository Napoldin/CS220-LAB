import javax.swing.*;

/**
 * The QuickSortDemo class serves as an example of using
 * Threads by extending the SwingWorker class
 */
public class QuickSortDemo extends SwingWorker {

	private Integer[] data;
	private int numberOfPasses;
	
	/**
	 * Constructor for the QuickSortDemo class
	 * @param data the array of Integers to be sorted
	 */
	public QuickSortDemo(Integer[] data)
	{
		super();
		this.data = data;
	}
	
	/**
	 * Overriding the SwingWorker doInBackGround method to 
	 * call the quickSort method
	 * @return null - method requires a return
	 */
	@Override
	public Void doInBackground() 
	{
		quickSort(data, 0, data.length - 1);
		return null;		
	}
	
	/**
	 * Overriding the SwingWorker done method to 
	 * wrap up this thread
	 *
	 * Note the ability to retrieve exceptions that occurred while the thread was running
	 * using the get operation
	 */
	@Override
	public void done() {
		try 
		{
  		    setProgress(100);
		    get();
		}
		catch (Exception e)
		{
			System.out.println("An exception occurred while this thread was running in the background");
			System.out.println("The details of that exception are as follows:");
			e.printStackTrace();
		}
	}
	
	/**
	 * Recursively sorts a range of objects in the specified array using the
	 * quick sort algorithm. 
	 * 
	 * @param data the array to be sorted
	 * @param min  the minimum index in the range to be sorted
	 * @param max  the maximum index in the range to be sorted
	 */
	private void quickSort(Integer[] data, int min, int max)
	{
		if(min < max)
		{
			int indexofpartition = partition(data, min, max);
			quickSort(data, min, indexofpartition - 1);
			quickSort(data, indexofpartition + 1, max);
		}
		numberOfPasses++;
		updateProgress();
	}

	/**
	 * Used by the quick sort algorithm to find the partition.
	 * 
	 * @param data the array to be sorted
	 * @param min  the minimum index in the range to be sorted
	 * @param max  the maximum index in the range to be sorted
	 */	
	private int partition(Integer[] data, int min, int max)
	{
		Integer partitionelement;
		int left, right;
		int middle = (min + max) / 2;
		
		partitionelement = data[middle];
		
		swap(data, middle, min);
		
		left = min;
		right = max;
		
		try{
		
		while(left < right)
		{
			
			while (left < right && data[left].compareTo(partitionelement) <= 0)
				left++;

			while (data[right].compareTo(partitionelement) > 0)
				right--;

			if (left < right)
				swap(data, left, right);
			
		}}
		catch (Exception e)
		{ e.printStackTrace();}
		swap(data, min, right);
		return right;
	}
	
	/**
	 * Swaps to elements in an array. Used by various sorting algorithms.
	 * 
	 * @param data   the array in which the elements are swapped
	 * @param index1 the index of the first element to be swapped
	 * @param index2 the index of the second element to be swapped
	 */	
	private void swap(Integer[] data, int index1, int index2)
	{
		Integer temp = data[index1];
		data[index1] = data[index2];
		data[index2] = temp;
	}
	  
  	/**
	 * private updateProgress method calculates the current progress of the sort 
	 * and updates the progress attribute of the SwingWorker class.  Keep in mind, SwingWorker
	 * is the super class of this class and provides the ability to add a change listener to 
	 * listen for changes.
	 *
	 * Because of the possible unbalanced nature of a quicksort, it is possible for the calculation to yield
	 * a result larger than 100.  Since the progress attribute will only accept values between 0 and 100, if that 
	 * happens, the result is set to 100.
	 */	
	  private void updateProgress()
	  {
	  	int result;
	  	double progressCount = 1 - (((double)data.length - (double)numberOfPasses)/(double)data.length);
	  	result = (int) (progressCount * 100);
		if (result > 100)
			result = 100;
		setProgress(result);
	  }
}
