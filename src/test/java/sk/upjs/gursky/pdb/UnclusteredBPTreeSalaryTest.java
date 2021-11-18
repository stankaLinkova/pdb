package sk.upjs.gursky.pdb;

import static org.junit.Assert.assertTrue;
import static org.junit.Assert.fail;

import java.io.File;
import java.util.List;

import org.junit.After;
import org.junit.Before;
import org.junit.Test;

public class UnclusteredBPTreeSalaryTest {

	private static final File INDEX_FILE = new File("personSalary.unkl");
	private UnclusteredBPTreeSalary bptree;

	@Before
	public void setUp() throws Exception {
		bptree = UnclusteredBPTreeSalary.newTreeBulkLoading(Generator.GENERATED_FILE, INDEX_FILE);
	}

	@After
	public void tearDown() throws Exception {
		bptree.close();
		INDEX_FILE.delete();
	}

	@Test
	public void test() throws Exception {
		long time = System.nanoTime();
		List<PersonEntry> result = bptree.intervalQuerySalaryEntries(new PersonSalaryKey(1000),
				new PersonSalaryKey(1200));
		time = System.nanoTime() - time;

		System.out.println("time: " + time / 1000000.0 + " ms");
		for (int i = 0; i < 50; i++) {
			System.out.println(result.get(i));
		}

		assertTrue(result.size() > 0);
	}
}
