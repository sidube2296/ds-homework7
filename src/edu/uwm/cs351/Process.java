package edu.uwm.cs351;
import java.awt.Color;
import java.util.AbstractQueue;
import java.util.ConcurrentModificationException;
import java.util.Iterator;
import java.util.NoSuchElementException;
import java.util.function.Consumer;

import javax.swing.JProgressBar;
import javax.swing.UIManager;

/**
 * The class Process.
 */
public class Process implements Cloneable{

	private String name;
	private int totalInstructions;
	private int finishedInstructions;
	private JProgressBar bar;
	Process next, prev;

	/**
	 * Instantiates a new process.
	 *
	 * @param name the name of the process, must not be null
	 * @param totalIns the total instructions of the process
	 */
	public Process(String name, int totalIns) {
		if (name == null) throw new NullPointerException("name must not be null");
		this.name = name;
		this.totalInstructions = totalIns;
	}

	/**
	 * Create an internal process to be used as a dummy node.
	 */
	private Process() {
		name = null;
		totalInstructions = 0;
	}

	/** Gets the name.
	 * @return the name */
	public String getName(){return name;}

	/** Gets the progress bar.
	 * Creates and initializes it if necessary.
	 * @return the progress bar */
	public JProgressBar getBar(){
		if (bar == null) createProgressBar();
		return bar;
	}

	/** Gets the total amount of instructions in the process.
	 * @return the total instructions */
	public int getTotal(){return totalInstructions;}

	/**
	 * Gets the amount of finished instructions.
	 * @return the finished instructions */
	public int getFinished(){return finishedInstructions;}

	/**
	 * Checks if the process is completed.
	 * @return true, if is done */
	public boolean isDone(){
		return finishedInstructions == totalInstructions;}

	/** Perform a single instruction of the process. */
	public void performInstruction(){
		if (!isDone())
			finishedInstructions++;
	}

	private void createProgressBar(){
		bar = new JProgressBar(0, totalInstructions);
		bar.setValue(finishedInstructions);
		UIManager.put("ProgressBar.selectionForeground", Color.WHITE);
		bar.setString(bar.getValue()+"/"+bar.getMaximum());
		bar.setStringPainted(true);}

	protected void updateProgressBar(boolean active){
		getBar().setValue(finishedInstructions);
		//set color
		if (isDone() || !active) bar.setForeground(new Color(102,153,204));
		else bar.setForeground(new Color(102,204,204));
		//set label
		if (isDone()) bar.setString("Finished @ "+Driver.pulseCt);
		else bar.setString(finishedInstructions+"/"+totalInstructions);
		bar.repaint();
	}

	/**
	 *  Returns a clone of this process that is identical in every way
	 *  except that it has null links.
	 *
	 * @return the process
	 * @see java.lang.Object#clone()
	 */
	@Override
	public Process clone(){
		Process copy = null; 
		try{
			copy = (Process) super.clone();
			copy.next = copy.prev = null;
			copy.bar = null;
		}
		catch (CloneNotSupportedException e){
			throw new RuntimeException("forgot to make Cloneable?");
		}
		return copy;
	}

	/**
	 *  Checks for equality of this process with the parameter process.
	 *  It will check everything except for the links.
	 *
	 * @param other the object against which to test for equality
	 * @return true, if successful
	 * @see java.lang.Object#equals(java.lang.Object)
	 */
	@Override
	public boolean equals(Object other){
		if (!(other instanceof Process) || other == null) return false;
		Process p = (Process) other;

		return p.totalInstructions == totalInstructions &&
				p.finishedInstructions == finishedInstructions &&
				p.name.equals(name);
	}


	/**
	 * The Class Queue.
	 */
	public static class Queue extends AbstractQueue<Process> implements Cloneable{

		private int manyItems;
		private int version;
		private Process dummy;

		private static Consumer<String> reporter = (s) -> System.out.println("Invariant error: "+ s);
		
		private boolean report(String error) {
			reporter.accept(error);
			return false;
		}

		/** The Invariant of our endogenous doubly-linked circular queue. */
		private boolean wellFormed() {
			// Invariant:
			// 1. dummy is never null.
			// TODO
			if (dummy==null) return report("Dummy node is null.");
			// 2. dummy's name is null and dummy's totalInstructions is 0
			// TODO
			if (dummy.name!=null || dummy.totalInstructions!=0) return report("Dummy node's name is not null or totalInstructions is not 0.");

			Process current = dummy;
			int count = 0;
			do {
				// 3. The queue is correctly doubly-linked.  You only need to check that
				//	  each process (including the dummy process) is the previous process of its next process
				// TODO
			    if (current.next == null || current.next.prev != current) return report("Queue is not correctly doubly-linked.");
			    current = current.next;
			    // 4. There is no Process with null name and in the queue (except the dummy process)
				// TODO
			    if (current != dummy) {
			        if (current.name == null) return report("Found a process with a null name.");
			        count++;
			    }
			} while (current != dummy);
			// 5. manyItems is number of non-dummy processes in queue
			// TODO
			if (count != manyItems) {
			    return report("manyItems does not match the number of real processes.");
			}
			// If no problems found, then return true:
			return true;
		}

		/** Instantiates a new queue object.
		 *  It will only contain a dummy process with null name and 0 instructions,
		 *  linked circularly to itself. This dummy process should never be
		 *  passed outside of this class.
		 *  @postcondition queue is empty except for dummy process
		 */
		public Queue(){
			// TODO
		    dummy = new Process();   
		    dummy.next = dummy.prev = dummy; 
		    manyItems = 0; 

			assert wellFormed() : "invariant failed in constructor";
		}

		// do not change this - used by JUnit tests
		private Queue(boolean ignored) { } 

		/** Adds a new process to the end of the queue.
		 * 
		 * @param p process to add to the end of this queue
		 * @throws NullPointerException if the process to add is null
		 * @throws IllegalArgumentException if the process is already in another queue
		 * 
		 * @return true always
		 * @see java.util.Queue#offer(java.lang.Object)
		 */
		@Override
		public boolean offer(Process p) {
			assert wellFormed() : "invariant failed at start of offer";
			// TODO
		    if (p == null) throw new NullPointerException("Process cannot be null.");
		    if (p.next != null || p.prev != null) throw new IllegalArgumentException("Process is already in another queue.");

		    p.prev = dummy.prev;
		    p.next = dummy;
		    dummy.prev.next = dummy.prev= p;
		    manyItems++;
		    version++;

			assert wellFormed() : "invariant failed at end of offer";
			return true;
		}

		/** Add all processes from parameter queue into the back of this queue.
		 *  The parameter queue should be empty after this method,
		 * except if the parameter is the same as this, in which case, nothing happens.
		 * @param pq the queue from which to take all processes, must not be null
		 */
		public void takeAll(Queue pq) {
			assert wellFormed() : "invariant failed at start of takeAll";
			// TODO
		    if (this == pq || pq.dummy.next == pq.dummy)	
		        return;
		    
		    this.dummy.prev.next = pq.dummy.next;
		    pq.dummy.next.prev = this.dummy.prev;

		    this.dummy.prev = pq.dummy.prev;
		    pq.dummy.prev.next = this.dummy;

		    this.manyItems += pq.manyItems;
		    
		    pq.manyItems = 0;
		    pq.dummy.next = pq.dummy.prev = pq.dummy;
		    version++;
		    pq.version++;

			assert wellFormed() : "invariant failed at end of takeAll";
			assert pq.wellFormed() : "parameter queue invariant failed at end of takeAll";
		}

		/** Returns the next process to be polled from this queue.
		 * 
		 * @return the next process to be polled by this queue, or null if empty
		 * @see java.util.Queue#peek()
		 */
		@Override
		public Process peek(){
			assert wellFormed() : "invariant failed at start of peek";
			// TODO
		    if (dummy == dummy.next) {
		        return null;
		    }

		    return dummy.next;
		}

		/** Removes and returns the process at the start of this queue, null if empty.
		 *  This method should never return the dummy process!
		 * 
		 * @returns the process at the start of this queue, or null if empty 
		 * @see java.util.Queue#poll()
		 */
		@Override
		public Process poll() {
			assert wellFormed() : "invariant failed at start of poll";
			Process result = null;
			// TODO
		    if (dummy == dummy.next) {
		        return result;
		    }

		    result = dummy.next;
		    dummy.next = result.next;
		    result.next.prev = dummy;
		    
		    result.next = result.prev = null;
		    manyItems--;
		    version++;

			assert wellFormed() : "invariant failed at end of poll";
			return result;
		}
		
		/**
		 * Clears all the elements from the queue.
		 * This method will break all the links between processes and reset the queue to its initial empty state.
		 */
		public void clear() {
		    assert wellFormed() : "Invariant failed at start of clear()";
		    
		    if (manyItems > 0) {
		    	for(Process current = dummy.next; current != dummy;) {
			        Process temp = current.next;
			        current.next = current.prev = null;
			        current = temp;
		    	}
		    
			    dummy.next = dummy.prev = dummy;
			    manyItems = 0;
			    version++;
		    }
		    
		    assert wellFormed() : "Invariant failed at end of clear()";
		}
		

		/** Returns the number of non-dummy processes in this queue.
		 * 
		 * @return the number of non-dummy processes
		 * @see java.util.AbstractCollection#size()
		 */
		@Override
		public int size() {
			assert wellFormed() : "invariant of result failed at start of size()";
			// TODO
		    return manyItems;
		}

		/** Returns a new copy of this queue. The copy should be unaffected
		 *  by subsequent changes made to this queue, and vice versa. The
		 *  processes added to the copy should be clones.
		 *  
		 * @return a clone of this queue
		 * @see java.lang.Object#clone()
		 */
		@Override
		public Queue clone(){
			assert wellFormed() : "invariant failed at start of clone()";

			Queue copy = new Queue();

			try{ copy = (Queue) super.clone();}
			catch(CloneNotSupportedException e){
				// should not happen
			}

			// TODO
			// Creating new dummy node for the entire cloned queue
		    copy.dummy = new Process();
		    copy.dummy.next = copy.dummy.prev = copy.dummy;
		    copy.manyItems = copy.version = 0;

		    // Cloning process each time and adding it to the new queue
		    for(Process current=this.dummy.next; current!=this.dummy ; current=current.next) {
		    	Process clonedProcess = current.clone();
		    	copy.offer(clonedProcess);
		    }

			assert wellFormed() : "invariant failed at end of clone()";
			assert copy.wellFormed() : "invariant of result failed at end of clone()";
			return copy;
		}

		/** Returns a new (remove-less) iterator over this queue.
		 * @see java.util.AbstractCollection#iterator() */
		@Override
		public Iterator<Process> iterator(){
			assert wellFormed() : "invariant failed at start of iterator()";
			return new MyIterator();}

		
		private class MyIterator implements Iterator<Process>{
			private Process cursor;
			private int myVersion;
			// do not add or remove fields: only myVersion and cursor

			private boolean wellFormed() {
				// Invariant for iterator:
				// 1. Outer invariant holds
				// TODO
				if (!Queue.this.wellFormed()) 
			        return false;			    
				// Only check 2 and 3 if versions match...
				if (myVersion != Queue.this.version)
			        return true;
				// 2. cursor is never null
				// TODO
				if (cursor == null) {
			        return report("Iterator's cursor is null.");
			    }
				// 3. cursor is in the list
				// TODO
				 Process current = Queue.this.dummy;
				 do {
					    if (current == cursor)
					        break;
					    current = current.next;
					} while (current != Queue.this.dummy);

					if (current == Queue.this.dummy && cursor != Queue.this.dummy) return report("Iterator's cursor is not in the queue.");
			    
			    return true;
			}

			/** Instantiates a new iterator */
			public MyIterator(){
				// TODO
				cursor = Queue.this.dummy.next;
			    myVersion = Queue.this.version;
			}

			// do not change this - used for JUnit tests
			private MyIterator(boolean ignore){}


			/** Returns whether there are more processes to be returned.
			 * @throws ConcurrentModificationException if versions don't match
			 * @return true if there exists a next element
			 */
			public boolean hasNext() {
				assert wellFormed() : "invariant failed at start of hasNext()";
				// TODO
				if (myVersion != Queue.this.version) throw new ConcurrentModificationException("Iterator's version does not match.");   
			    return cursor != Queue.this.dummy;
			}

			/** Returns the next process in this queue. This method should
			 *  *not* call poll, or change the state of the queue in any way.
			 *  
			 *  @throws ConcurrentModificationException if versions don't match
			 *  @return the next process in the queue
			 */
			public Process next() {
				assert wellFormed() : "invariant failed at start of next()";
				// TODO
				Process result = cursor;
				if (!hasNext())throw new NoSuchElementException("No more elements in the queue.");
			    cursor = cursor.next;
				assert wellFormed() : "invariant failed at end of next()";
			    return result;
			}
		}

		/** Do not change this class */
		public static class Spy {
			public static class DebugProcess extends Process {
				public DebugProcess() {}
				public DebugProcess(String n, int i) { super(n,i); }
				public Process setNext(Process p) {
					this.next = p;
					return p;
				}
				public Process setPrev(Process p) {
					this.prev = p;
					return p;
				}
			}
			
			/**
			 * Return the sink for invariant error messages
			 * @return current reporter
			 */
			public Consumer<String> getReporter() {
				return reporter;
			}

			/**
			 * Change the sink for invariant error messages.
			 * @param r where to send invariant error messages.
			 */
			public void setReporter(Consumer<String> r) {
				reporter = r;
			}

			/**
			 * Create a debugging instance of the main class
			 * with a particular data structure.
			 * @param d the dummy
			 * @param m many items
			 * @param v the version
			 * @return a new instance with the given data structure
			 */
			public Queue newInstance(Process d, int m, int v) {
				Queue result = new Queue(false);
				result.dummy = d;
				result.manyItems = m;
				result.version = v;
				return result;
			}
			
			/**
			 * Create a debugging instance of an iterator with the given data structure.
			 * @param q the queue to create the iterator for
			 * @param c the cursor for the iterator
			 * @param v the version of the queue that this iterator works for
			 * @return the debugging iterator
			 */
			public Iterator<Process> newIterator(Queue q, Process c, int v) {
				MyIterator result = q.new MyIterator(false);
				result.cursor = c;
				result.myVersion = v;
				return result;
			}
			
			/**
			 * Return whether debugging instance meets the 
			 * requirements on the invariant.
			 * @param q instance of to use, must not be null
			 * @return whether it passes the check
			 */
			public boolean wellFormed(Queue q) {
				return q.wellFormed();
			}
			
			/**
			 * Return whether debugging instance meets the 
			 * requirements on the invariant.
			 * @param i instance of to use, must not be null
			 * @return whether it passes the check
			 */
			public boolean wellFormed(Iterator<Process> i) {
				return ((MyIterator)i).wellFormed();
			}
		}
	}
}