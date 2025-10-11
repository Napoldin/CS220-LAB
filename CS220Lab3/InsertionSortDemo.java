import javax.swing.*;

/**
 * The InsertionSortDemo class serves as an example of using
 * Threads by extending the SwingWorker class
 */
public class InsertionSortDemo extends SwingWorker {

	private Integer[] data;
	
	/**
	 * Constructor for the InsertionSortDemo class
	 * @param data the array of Integers to be sorted
	 */
	public InsertionSortDemo(Integer[] data)
	{
		super();
		this.data = data;
	}
	
	/**
	 * Overriding the SwingWorker doInBackGround method to 
	 * call the insertionSort method
	 * @return null - method requires a return
	 */
	@Override
	public Void doInBackground() 
	{
		insertionSort();
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
     * Sorts the specified array of objects using an insertion
     * sort algorithm.
     *
     * @param data the array to be sorted
     */
	private void insertionSort()
	    {
	        for (int index = 1; index < data.length; index++)
	        {
	            int key = data[index];
	            int position = index;
				updateProgress(index);
	          
	            while (position > 0 && data[position-1].compareTo(key) > 0)
	            {
	                data[position] = data[position-1];
	                position--;
	            }
				
	            data[position] = key;
	        }
	    }
	  
  	/**
	 * private updateProgress method calculates the current progress of the sort 
	 * and updates the progress attribute of the SwingWorker class.  Keep in mind, SwingWorker
	 * is the super class of this class and provides the ability to add a change listener to 
	 * listen for changes.
	 */	
	  private void updateProgress(int numberOfPasses)
	  {
	  	int result;
	  	double progressCount = 1 - (((double)data.length - (double)numberOfPasses)/(double)data.length);
	  	result = (int) (progressCount * 100);
	  	setProgress(result);
	  }
}
