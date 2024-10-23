import java.util.ConcurrentModificationException;
import java.util.Iterator;
import java.util.NoSuchElementException;
import java.util.function.Supplier;

import edu.uwm.cs351.Process;

public class TestQueue extends TestCollection<Process> {

	private Process.Queue pq;
	private Process p1, p2, p3, p4;
	
	private int ix(Supplier<Process> s) {
		try {
			Process result = s.get();
			if (result == null) return 0;
			for (int i=1; i < e.length; ++i) {
				if (e[i] == result) return i;
			}
			return -2;
		} catch (RuntimeException ex) {
			return -1;
		}
	}
	
	@Override
	protected void initCollections() {
		permitNulls = false;
		permitDuplicates = false;	
		hasRemove = false;
		c = pq = new Process.Queue();
		e = new Process[10];
		for (int i=0; i < 10; ++i) {
			e[i]= new Process("Process #" + i, i * 10);
		}
		p1 = e[1];
		p2 = e[2];
		p3 = e[3];
		p4 = e[4];
	}

	
	/// Locked tests:
	
	public void test() {
		pq = new Process.Queue();
		assertEquals(Ti(1071004616), pq.size());
		// Use -1 for errors, 0 for null, 1 for p1, 2 for p2, etc.
		// Use -2 for None of the above
		assertEquals(Ti(1488949487), ix(() -> pq.peek()));
		assertEquals(Ti(18519146), ix(() -> pq.poll()));
		assertEquals(Ti(495922773), ix(() -> pq.element()));
		assertEquals(Ti(581283569), ix(() -> pq.remove()));
		test1(pq);
	}
	
	private void test1(Process.Queue pq) {
		// pq starts out empty
		pq.offer(p1);
		pq.offer(p2);
		// Use -1 for errors, 0 for null, 1 for p1, 2 for p2, etc.
		// Use -2 for None of the above
		assertEquals(Ti(1124590455), ix (() -> pq.peek()));
		pq.offer(p3);
		assertEquals(Ti(1433762461), ix (() -> pq.poll()));
		assertEquals(Ti(1301203106), ix (() -> pq.peek()));
		assertEquals(Ti(316018735), ix (() -> pq.poll()));
		pq.clear();
		assertEquals(0, pq.size());
		test2(pq);
	}
	
	private void test2(Process.Queue pq) {
		// pq starts out empty:
		pq.offer(p1);
		pq.takeAll(pq);
		assertEquals(Ti(319158578), pq.size());
		// Use -1 for errors, 0 for null, 1 for p1, 2 for p2, etc.
		// Use -2 for None of the above
		assertEquals(Ti(128688223), ix (() -> pq.peek()));
		Process.Queue clone = pq.clone();
		assertEquals(1, clone.size());
		// Use -1, 0, 1, 2, 3 as above, -2 for none of the above
		assertEquals(Ti(660169371), ix (() -> clone.peek()));
		pq.takeAll(clone);
		assertEquals(Ti(145822061), pq.size());
		assertEquals(Ti(1835153215),clone.size());
		// Use -1, 0, 1, 2, 3, -2 as above.
		assertEquals(Ti(233209596), ix( () -> pq.poll())); // the original one!
		assertEquals(Ti(1002616638), ix( () -> pq.poll()));
		assertEquals(0, pq.size());
		test3(pq);
	}
	
	private void test3(Process.Queue pq) {
		// pq.start out empty
		pq.offer(p1);
		pq.offer(p2);
		Iterator<Process> it = pq.iterator();
		assertTrue(it.hasNext());
		// Use -1 for errors, 0 for null, 1 for p1, 2 for p2, etc.
		// Use -2 for None of the above
		assertEquals(Ti(76187204), ix(() -> it.next()));
		pq.offer(p3);
		assertEquals(Ti(1854935229), ix(() -> it.next()));
	}
	
	
	/// Regular tests
	
	/// test5x: simple queue operations:
	
	public void test50() {
		assertEquals(0, pq.size());
	}
	
	public void test51() {
		assertNull(pq.peek());
	}
	
	public void test52() {
		assertNull(pq.poll());
	}
	
	public void test53() {
		assertException(NullPointerException.class, () -> pq.offer(null));
	}
	
	public void test54() {
		pq.offer(p4);
		assertEquals(1,pq.size());
	}
	
	public void test55() {
		pq.offer(p2);
		assertSame(p2, pq.peek());
		assertEquals(1, pq.size());
	}
	
	public void test56() {
		pq.offer(p3);
		assertSame(p3, pq.poll());
		assertEquals(0, pq.size());
	}
	
	public void test57() {
		pq.offer(p1);
		assertException(NullPointerException.class, () -> pq.offer(null));
	}
	
	public void test58() {
		pq.offer(p2);
		assertException(IllegalArgumentException.class, () -> pq.offer(p2));
	}
	
	public void test59() {
		pq.offer(new Process("Test", 0));
		assertEquals(1, pq.size());
	}

	
	/// test6x: tests of larger queues
	
	public void test60() {
		pq.offer(p1);
		pq.offer(p2);
		assertSame(p1, pq.peek());
		pq.offer(p3);
		assertSame(p1, pq.poll());
		assertSame(p2, pq.poll());
		assertSame(p3, pq.peek());
		assertEquals(1, pq.size());
	}
	
	public void test61() {
		pq.offer(p1);
		pq.offer(p2);
		pq.offer(p3);
		assertException(IllegalArgumentException.class, () -> pq.offer(p2));
		assertSame(p1,pq.poll());
		pq.offer(p1);
		assertSame(p2, pq.peek());
		assertException(IllegalArgumentException.class, () -> pq.offer(p2));
		assertSame(p2, pq.poll());
		pq.offer(p2);
		assertSame(p3, pq.peek());
	}
	
	public void test62() {
		pq.offer(p2);
		pq.takeAll(pq);
		assertEquals(1, pq.size());
	}
	
	public void test63() {
		for (int i=0; i < 10; ++i) {
			pq.offer(new Process("Process #" + i, i));
		}
		for (int i=0; i < 10; ++i) {
			Process newp = new Process("Process #" + i, i);
			pq.offer(newp); // looks similar, but not same
			Process p = pq.poll();
			assertEquals("Process #"+i, p.getName());
			assertFalse(newp == p);
		}
		for (int i=0; i < 10; ++i) {
			Process p = pq.poll();
			assertEquals("Process #"+i, p.getName());
		}
	}
	
	public void test64() {
		Process.Queue q2 = new Process.Queue();
		pq.offer(p1);
		q2.offer(p2);
		assertException(IllegalArgumentException.class, () -> pq.offer(p2));
		assertException(IllegalArgumentException.class, () -> q2.offer(p1));
	}
	
	public void test65() {
		Process.Queue q2 = new Process.Queue();
		pq.offer(p1);
		q2.offer(p2);
		assertSame(p2, q2.poll());
		pq.offer(p2);
		assertException(IllegalArgumentException.class, () -> q2.offer(p2));		
	}
	
	public void test66() {
		Process.Queue q2 = new Process.Queue();
		pq.offer(p1);
		q2.offer(p2);
		q2.offer(p3);
		pq.takeAll(q2);
		assertEquals(3, pq.size());
	}
	
	public void test67() {
		Process.Queue q2 = new Process.Queue();
		q2.offer(p2);
		q2.offer(p3);
		pq.takeAll(q2);
		assertSame(p2, pq.poll());
	}
	
	public void test68() {
		Process.Queue q2 = new Process.Queue();
		pq.offer(p1);
		q2.offer(p2);
		q2.offer(p3);
		pq.takeAll(q2);
		assertException(IllegalArgumentException.class, () -> pq.offer(p1));		
		assertException(IllegalArgumentException.class, () -> pq.offer(p2));		
		assertException(IllegalArgumentException.class, () -> pq.offer(p3));		
		assertException(IllegalArgumentException.class, () -> q2.offer(p1));		
		assertException(IllegalArgumentException.class, () -> q2.offer(p2));		
		assertException(IllegalArgumentException.class, () -> q2.offer(p3));		
	}
	
	public void test69() {
		Process.Queue q2 = new Process.Queue();
		q2.offer(p3);
		q2.offer(p2);
		q2.takeAll(pq);
		assertEquals(2, q2.size());
		assertSame(p3, q2.poll());
		pq.offer(p1);
		assertSame(p2, q2.poll());
		assertNull(q2.poll());
		assertEquals(1,pq.size());
	}
	
	
	/// test7x: tests of clone()
	
	public void test70() {
		Process.Queue clone = pq.clone();
		assertEquals(0, clone.size());
	}
	
	public void test71() {
		Process.Queue clone = pq.clone();
		pq.offer(p1);
		assertNull(clone.peek());
	}
	
	public void test72() {
		pq.offer(p1);
		Process.Queue clone = pq.clone();
		assertEquals(1, clone.size());
	}
	
	public void test73() {
		pq.offer(p2);
		Process.Queue clone = pq.clone();
		clone.offer(p3);
		assertSame(p2, pq.poll());
		assertNull(pq.poll());
		assertEquals(p2, clone.peek());
	}
	
	public void test74() {
		pq.offer(p3);
		Process.Queue clone = pq.clone();
		Process p3p = clone.poll();
		assertFalse(p3 == p3p);
		assertTrue(p3.equals(p3p));
	}
	
	public void test75() {
		pq.offer(p1);
		pq.offer(p2);
		Process.Queue clone = pq.clone();
		Process p1p = clone.poll();
		assertFalse(p1 == p1p);
		assertTrue(p1.equals(p1p));
		Process p2p = clone.poll();
		assertFalse(p2 == p2p);
		assertTrue(p2.equals(p2p));
		assertNull(clone.poll());
	}
	
	private class Queue extends Process.Queue { }
	public void test76() {
		Queue q = new Queue();
		q.offer(p1);
		Process.Queue pq = q.clone();
		assertTrue("clone() didn't use super.clone()", pq instanceof Queue);
	}

	/// test8x (x > 4): tests of clear()
	
	public void test85() {
		pq.clear();
		assertEquals(0, pq.size());
	}
	
	public void test86() {
		pq.offer(p1);
		pq.clear();
		assertEquals(0, pq.size());
	}
	
	public void test87() {
		pq.offer(p2);
		pq.clear();
		assertNull(pq.poll());
	}
	
	public void test88() {
		pq.offer(p3);
		pq.clear();
		pq.offer(p3);
	}
	
	public void test89() {
		pq.offer(p1);
		pq.offer(p2);
		pq.offer(p3);
		pq.clear();
		pq.offer(p3);
		pq.offer(p2);
		pq.offer(p1);
	}
	
	
	/// test9x: tests of fail-fast iterator errors
	// (Don't worry about failures here, until you've fixed failures
	//  in test0x - test4x which test Iterators more generally.)
	
	public void test90() {
		Iterator<Process> it = pq.iterator();
		assertNull(pq.poll());
		assertFalse(it.hasNext());
		assertException(NoSuchElementException.class, () -> it.next());
	}
	
	public void test91() {
		Iterator<Process> it = pq.iterator();
		pq.clear();
		assertFalse(it.hasNext());
		assertException(NoSuchElementException.class, () -> it.next());
	}
	
	public void test92() {
		Iterator<Process> it = pq.iterator();
		pq.offer(p2);
		assertException(ConcurrentModificationException.class, () -> it.hasNext());
		assertException(ConcurrentModificationException.class, () -> it.next());
	}
	
	public void test93() {
		pq.offer(p3);
		Iterator<Process> it = pq.iterator();
		assertSame(p3,pq.peek());
		assertTrue(it.hasNext());
		assertSame(p3, it.next());
	}
	
	public void test94() {
		pq.offer(p1);
		Iterator<Process> it = pq.iterator();
		assertSame(p1,pq.poll());
		assertException(ConcurrentModificationException.class, () -> it.hasNext());
		assertException(ConcurrentModificationException.class, () -> it.next());
	}
	
	public void test95() {
		pq.offer(p2);
		Iterator<Process> it = pq.iterator();
		pq.offer(p3);
		assertException(ConcurrentModificationException.class, () -> it.hasNext());
		assertException(ConcurrentModificationException.class, () -> it.next());
	}
	
	public void test96() {
		pq.offer(p3);
		Iterator<Process> it = pq.iterator();
		pq.clear();
		assertException(ConcurrentModificationException.class, () -> it.hasNext());
		assertException(ConcurrentModificationException.class, () -> it.next());	
	}
	
	public void test97() {
		pq.offer(p1);
		pq.offer(p2);
		Iterator<Process> it = pq.iterator();
		pq.takeAll(pq);
		assertTrue(it.hasNext());
		assertSame(p1, it.next());
		Process.Queue q2 = new Process.Queue();
		pq.takeAll(q2);
		assertTrue(it.hasNext());
		assertSame(p2, it.next());
	}
	
	public void test98() {
		pq.offer(p2);
		pq.offer(p3);
		Iterator<Process> it = pq.iterator();
		Process.Queue q2 = new Process.Queue();
		q2.offer(p1);
		pq.takeAll(q2);
		assertException(ConcurrentModificationException.class, () -> it.hasNext());
		assertException(ConcurrentModificationException.class, () -> it.next());			
	}
	
	public void test99() {
		Process.Queue q2 = new Process.Queue();
		Iterator<Process> it = pq.iterator();
		q2.takeAll(pq);
		assertFalse(it.hasNext());
		assertException(NoSuchElementException.class, () -> it.next());
		pq.offer(p3);
		pq.offer(p1);
		Iterator<Process> i2 = pq.iterator();
		q2.takeAll(pq);
		assertException(ConcurrentModificationException.class, () -> i2.hasNext());
		assertException(ConcurrentModificationException.class, () -> i2.next());					
	}
}
