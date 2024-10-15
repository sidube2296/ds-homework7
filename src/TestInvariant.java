import java.util.ArrayList;
import java.util.Iterator;
import java.util.function.Consumer;
import java.util.function.Supplier;

import edu.uwm.cs351.Process;
import edu.uwm.cs351.Process.Queue;
import edu.uwm.cs351.Process.Queue.Spy;
import junit.framework.TestCase;


public class TestInvariant extends TestCase {
	protected Spy spy;
	protected int reports = 0;
	
	protected void assertReporting(boolean expected, Supplier<Boolean> test) {
		reports = 0;
		Consumer<String> savedReporter = spy.getReporter();
		try {
			spy.setReporter((String message) -> {
				++reports;
				if (message == null || message.trim().isEmpty()) {
					assertFalse("Uninformative report is not acceptable", true);
				}
				if (expected) {
					assertFalse("Reported error incorrectly: " + message, true);
				}
			});
			assertEquals(expected, test.get().booleanValue());
			if (!expected) {
				assertEquals("Expected exactly one invariant error to be reported", 1, reports);
			}
			spy.setReporter(null);
		} finally {
			spy.setReporter(savedReporter);
		}
	}
	
	protected void assertWellformed(boolean expected, Queue r) {
		assertReporting(expected, () -> spy.wellFormed(r));
	}
	
	protected void assertWellformed(boolean expected, Iterator<Process> r) {
		assertReporting(expected, () -> spy.wellFormed(r));
	}

	protected Queue list;
	protected Iterator<Process> it;
	private Spy.DebugProcess dummy, p1, p2, p3;

	@Override
	public void setUp() throws NoSuchFieldException{
		spy = new Spy();
		dummy = new Spy.DebugProcess();
		p1 = new Spy.DebugProcess("p1",1);
		p2 = new Spy.DebugProcess("p2",2);
		p3 = new Spy.DebugProcess("p3",3);
	}
	
	public void testA0() {
		list = spy.newInstance(null, 0, 0);
		assertWellformed(false, list);
	}
	
	public void testA1() {
		dummy = new Spy.DebugProcess();
		list = spy.newInstance(dummy, 0, 0);
		assertWellformed(false, list);		
	}
	
	public void testA2() {
		dummy = new Spy.DebugProcess();
		list = spy.newInstance(dummy, 0, 0);
		dummy.setNext(dummy);
		assertWellformed(false, list);
	}
	
	public void testA3() {
		dummy = new Spy.DebugProcess();
		list = spy.newInstance(dummy, 0, 0);
		dummy.setPrev(dummy);
		assertWellformed(false, list);
	}
	
	public void testA4() {
		dummy = new Spy.DebugProcess();
		list = spy.newInstance(dummy, 0, 0);
		dummy.setPrev(dummy);
		dummy.setNext(dummy);
		assertWellformed(true, list);
	}
	
	public void testA5() {
		dummy = new Spy.DebugProcess("dummy", 0);
		list = spy.newInstance(dummy, 0, 0);
		dummy.setPrev(dummy);
		dummy.setNext(dummy);
		assertWellformed(false, list);
	}
	
	public void testA6() {
		dummy = new Spy.DebugProcess("", 0);
		list = spy.newInstance(dummy, 0, 0);
		dummy.setPrev(dummy);
		dummy.setNext(dummy);
		assertWellformed(false, list);
	}
	
	public void testA7() {
		dummy = new Spy.DebugProcess();
		list = spy.newInstance(dummy, 1, 0);
		dummy.setPrev(dummy);
		dummy.setNext(dummy);
		assertWellformed(false, list);
	}
	

	public void testB0() {
		list = spy.newInstance(dummy, 0, 0);
		dummy.setNext(p1);
		dummy.setPrev(p1);
		p1.setNext(dummy);
		p1.setPrev(dummy);
		assertWellformed(false, list);
	}

	public void testB1() {
		list = spy.newInstance(dummy, 1, 0);
		dummy.setNext(p1);
		dummy.setPrev(p1);
		p1.setNext(dummy);
		p1.setPrev(dummy);
		assertWellformed(true, list);
	}

	public void testB2() {
		list = spy.newInstance(p1, 1, 0);
		dummy.setNext(p1);
		dummy.setPrev(p1);
		p1.setNext(dummy);
		p1.setPrev(dummy);
		assertWellformed(false, list);
	}
	
	public void testB3() {
		list = spy.newInstance(dummy, 1, 0);
		dummy.setNext(p1);
		dummy.setPrev(dummy);
		p1.setNext(dummy);
		p1.setPrev(dummy);
		assertWellformed(false, list);
	}
	
	public void testB4() {
		list = spy.newInstance(dummy, 1, 0);
		dummy.setNext(p1);
		dummy.setPrev(null);
		p1.setNext(dummy);
		p1.setPrev(dummy);
		assertWellformed(false, list);
	}
	
	public void testB5() {
		list = spy.newInstance(dummy, 1, 0);
		dummy.setNext(p1);
		dummy.setPrev(p1);
		p1.setNext(p1);
		p1.setPrev(dummy);
		assertWellformed(false, list);
	}

	public void testC0() {
		list = spy.newInstance(dummy, 0, 0);
		dummy.setPrev(p2);
		dummy.setNext(p1);
		p1.setPrev(dummy);
		p1.setNext(p2);
		p2.setPrev(p1);
		p2.setNext(dummy);
		assertWellformed(false, list);
	}
	
	public void testC1() {
		list = spy.newInstance(dummy, 1, 0);
		dummy.setPrev(p2);
		dummy.setNext(p1);
		p1.setPrev(dummy);
		p1.setNext(p2);
		p2.setPrev(p1);
		p2.setNext(dummy);
		assertWellformed(false, list);		
	}
	
	public void testC2() {
		list = spy.newInstance(dummy, 2, 0);
		dummy.setPrev(p2);
		dummy.setNext(p1);
		p1.setPrev(dummy);
		p1.setNext(p2);
		p2.setPrev(p1);
		p2.setNext(dummy);
		assertWellformed(true, list);		
	}
	
	public void testC3() {
		list = spy.newInstance(dummy, 2, 0);
		dummy.setPrev(p1);
		dummy.setNext(p1);
		p1.setPrev(dummy);
		p1.setNext(p2);
		p2.setPrev(p1);
		p2.setNext(dummy);
		assertWellformed(false, list);		
	}
	
	public void testC4() {
		list = spy.newInstance(dummy, 2, 0);
		dummy.setPrev(p2);
		dummy.setNext(p1);
		p1.setPrev(dummy);
		p1.setNext(p2);
		p2.setPrev(dummy);
		p2.setNext(dummy);
		assertWellformed(false, list);		
	}
	
	public void testC5() {
		list = spy.newInstance(dummy, 2, 0);
		dummy.setPrev(p2);
		dummy.setNext(p1);
		p1.setPrev(dummy);
		p1.setNext(p2);
		p2.setPrev(p1);
		p2.setNext(p1);
		assertWellformed(false, list);		
	}
	
	public void testC6() {
		list = spy.newInstance(dummy, 2, 0);
		dummy.setPrev(p2);
		dummy.setNext(p1);
		p1.setPrev(p2);
		p1.setNext(p2);
		p2.setPrev(p1);
		p2.setNext(dummy);
		assertWellformed(false, list);		
	}
	
	public void testC7() {
		list = spy.newInstance(dummy, 2, 0);
		dummy.setPrev(p1);
		dummy.setNext(p2);
		p1.setPrev(dummy);
		p1.setNext(p2);
		p2.setPrev(p1);
		p2.setNext(dummy);
		assertWellformed(false, list);		
	}

	public void testD0() {
		list = spy.newInstance(dummy, 3, 0);
		dummy.setNext(p3);
		p3.setNext(p2);
		p2.setNext(p1);
		p1.setNext(dummy);
		dummy.setPrev(p1);
		p1.setPrev(p2);
		p2.setPrev(p3);
		p3.setPrev(dummy);
		assertWellformed(true, list);
	}
	
	public void testD1() {
		list = spy.newInstance(dummy, 3, 0);
		dummy.setNext(p3);
		p3.setNext(p2);
		p2.setNext(p1);
		p1.setNext(dummy);
		dummy.setPrev(p2);
		p1.setPrev(p2);
		p2.setPrev(p3);
		p3.setPrev(dummy);
		assertWellformed(false, list);
	}
	
	public void testD2() {
		list = spy.newInstance(dummy, 3, 0);
		dummy.setNext(p3);
		p3.setNext(p2);
		p2.setNext(p1);
		p1.setNext(dummy);
		dummy.setPrev(p3);
		p1.setPrev(p2);
		p2.setPrev(p3);
		p3.setPrev(dummy);
		assertWellformed(false, list);
	}
	
	public void testD3() {
		list = spy.newInstance(p1, 3, 0);
		dummy.setNext(p3);
		p3.setNext(p2);
		p2.setNext(p1);
		p1.setNext(dummy);
		dummy.setPrev(p1);
		p1.setPrev(p2);
		p2.setPrev(p3);
		p3.setPrev(dummy);
		assertWellformed(false, list);
	}

	public void testD4() {
		list = spy.newInstance(dummy, 3, 0);
		dummy.setNext(p2);
		p3.setNext(p2);
		p2.setNext(p1);
		p1.setNext(dummy);
		dummy.setPrev(p1);
		p1.setPrev(p2);
		p2.setPrev(dummy);
		p3.setPrev(dummy);
		assertWellformed(false, list);
	}

	public void testD5() {
		list = spy.newInstance(dummy, 2, 0);
		dummy.setNext(p2);
		p3.setNext(p2);
		p2.setNext(p1);
		p1.setNext(dummy);
		dummy.setPrev(p1);
		p1.setPrev(p2);
		p2.setPrev(dummy);
		p3.setPrev(dummy);
		assertWellformed(true, list);
	}
	
	public void testD6() {
		list = spy.newInstance(dummy, 3, 0);
		dummy.setNext(p2);
		p3.setNext(p2);
		p2.setNext(p1);
		p1.setNext(dummy);
		dummy.setPrev(p1);
		p1.setPrev(p2);
		p2.setPrev(dummy);
		p3.setPrev(dummy);
		Spy.DebugProcess x = new Spy.DebugProcess("Goku", 9001);
		x.setNext(dummy);
		x.setPrev(p1);
		p1.setNext(x);
		dummy.setPrev(x);
		assertWellformed(true, list);
	}

	protected ArrayList<Spy.DebugProcess> processes;
	
	public void testE0() {
		Spy.DebugProcess p, cur=dummy;
		int test = 100;
		processes = new ArrayList<Spy.DebugProcess>(test);
		for(int i=0; i<test; i++){
			p = new Spy.DebugProcess("p"+i,i+1);
			p.setPrev(cur);
			cur.setNext(p);
			cur = p;
			processes.add(p);
		}
		dummy.setPrev(cur);
		cur.setNext(dummy);
		list = spy.newInstance(dummy, test, 0);
		assertWellformed(true, list);
	}
	
	public void testE1() {
		testE0();
		p1 = processes.get(11);
		p2 = processes.get(13);
		p1.setNext(p2);
		p2.setPrev(p1);
	}
	
	public void testE2() {
		testE0();
		p1 = processes.get(49);
		p2 = processes.get(23);
		p1.setNext(p2);
		p2.setPrev(p1);
	}
	
	public void testE3() {
		testE0();
		p1 = processes.get(52);
		p2 = processes.get(38);
		p1.setNext(p2);
		p2.setPrev(p1);
	}
	
	public void testE4() {
		testE0();
		p1 = processes.get(93);
		p2 = processes.get(73);
		p1.setNext(p2);
		p2.setPrev(p1);
	}
	
	public void testE5() {
		testE0();
		p1 = processes.get(6);
		p2 = processes.get(58);
		p1.setNext(p2);
		p2.setPrev(p1);
	}
	
	public void testE6() {
		testE0();
		p1 = processes.get(9);
		p2 = processes.get(9);
		p1.setNext(p2);
		p2.setPrev(p1);
	}
	
	public void testE7() {
		testE0();
		p1 = processes.get(92);
		p2 = processes.get(84);
		p1.setNext(p2);
		p2.setPrev(p1);
	}
	
	public void testE8() {
		testE0();
		p1 = processes.get(3);
		p2 = processes.get(99);
		p1.setNext(p2);
		p2.setPrev(p1);
	}
	
	public void testE9() {
		testE0();
		p1 = processes.get(88);
		p2 = processes.get(39);
		p1.setNext(p2);
		p2.setPrev(p1);
	}

	public void testF0() {
		dummy.setPrev(p1);
		dummy.setNext(p1);
		p1.setPrev(dummy);
		p1.setNext(dummy);
		list = spy.newInstance(dummy, 1, 1);
		it = spy.newIterator(list, null, 0);
		assertWellformed(true, it);
	}
	
	public void testF1() {
		dummy.setPrev(p1);
		dummy.setNext(p1);
		p1.setPrev(dummy);
		p1.setNext(dummy);
		list = spy.newInstance(dummy, 1, 0);
		it = spy.newIterator(list, null, 0);
		assertWellformed(false, it);
	}
	
	public void testF2() {
		dummy.setPrev(p1);
		dummy.setNext(p1);
		p1.setPrev(dummy);
		p1.setNext(dummy);
		list = spy.newInstance(dummy, 1, 1);
		it = spy.newIterator(list, dummy, 1);
		assertWellformed(true, it);
	}
	
	public void testF3() {
		dummy.setPrev(p1);
		dummy.setNext(p1);
		p1.setPrev(dummy);
		p1.setNext(dummy);
		list = spy.newInstance(p1, 1, 1);
		it = spy.newIterator(list, dummy, 1);
		assertWellformed(false, it);
	}
	
	public void testF4() {
		dummy.setPrev(p1);
		dummy.setNext(p1);
		p1.setPrev(dummy);
		p1.setNext(dummy);
		list = spy.newInstance(p1, 1, 2);
		it = spy.newIterator(list, dummy, 1);
		assertWellformed(false, it);
	}

	public void testF5() {
		dummy.setPrev(p1);
		dummy.setNext(p1);
		p1.setPrev(dummy);
		p1.setNext(dummy);
		list = spy.newInstance(dummy, 1, 1);
		it = spy.newIterator(list, p1, 1);
		assertWellformed(true, it);
	}
	
	public void testF6() {
		dummy.setPrev(p1);
		dummy.setNext(p1);
		p1.setPrev(dummy);
		p1.setNext(dummy);
		list = spy.newInstance(dummy, 1, 2);
		it = spy.newIterator(list, p1, 1);
		assertWellformed(true, it);
	}
	
	public void testF7() {
		Spy.DebugProcess i1 = new Spy.DebugProcess("p1", 1); // impostor node
		dummy.setPrev(p1);
		dummy.setNext(p1);
		p1.setPrev(dummy);
		p1.setNext(dummy);
		i1.setPrev(dummy);
		i1.setNext(dummy);
		list = spy.newInstance(dummy, 1, 2);
		it = spy.newIterator(list, i1, 2);
		assertWellformed(false, it);
	}
	
	public void testF8() {
		Spy.DebugProcess i1 = new Spy.DebugProcess("p1", 1); // impostor node
		dummy.setPrev(p1);
		dummy.setNext(p1);
		p1.setPrev(dummy);
		p1.setNext(dummy);
		i1.setPrev(dummy);
		i1.setNext(dummy);
		list = spy.newInstance(dummy, 1, 2);
		it = spy.newIterator(list, i1, 1);
		assertWellformed(true, it);
	}

	public void testF9() {
		Spy.DebugProcess id = new Spy.DebugProcess(); // impostor node
		dummy.setPrev(p1);
		dummy.setNext(p1);
		p1.setPrev(dummy);
		p1.setNext(dummy);
		id.setPrev(p1);
		id.setNext(p1);
		list = spy.newInstance(dummy, 1, 2);
		it = spy.newIterator(list, id, 2);
		assertWellformed(false, it);
	}
}

