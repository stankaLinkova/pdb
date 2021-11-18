package sk.upjs.gursky.pdb;

import java.io.File;
import java.io.IOException;
import java.io.RandomAccessFile;
import java.nio.ByteBuffer;
import java.nio.channels.FileChannel;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Iterator;
import java.util.LinkedList;
import java.util.List;

import sk.upjs.gursky.bplustree.BPTree;

public class UnclusteredBPTreeSalary extends BPTree<PersonSalaryKey, SalaryAndOffsetEntry> {

	private static final long serialVersionUID = 336521173682680970L;
	public static final int PAGE_SIZE = 4096;

	private File personsFile;

	private UnclusteredBPTreeSalary(File personsFile, File indexFile) {
		super(SalaryAndOffsetEntry.class, indexFile);
		this.personsFile = personsFile;
	}

	public static UnclusteredBPTreeSalary newTreeBulkLoading(File personsFile, File indexFile) throws IOException {
		UnclusteredBPTreeSalary tree = new UnclusteredBPTreeSalary(personsFile, indexFile);
		tree.setNodeSize(PAGE_SIZE);

		RandomAccessFile raf = new RandomAccessFile(personsFile, "r");
		FileChannel channel = raf.getChannel();
		ByteBuffer buffer = ByteBuffer.allocateDirect(PAGE_SIZE);

		long fileSize = personsFile.length();

		List<SalaryAndOffsetEntry> pairs = new ArrayList<>();

		for (int offset = 0; offset < fileSize; offset += PAGE_SIZE) {
			buffer.clear();
			channel.read(buffer, offset);
			buffer.rewind();
			int personsCount = buffer.getInt();
			for (int i = 0; i < personsCount; i++) {
				PersonEntry personEntry = new PersonEntry();
				personEntry.load(buffer);
				pairs.add(new SalaryAndOffsetEntry(personEntry.salary, offset + 4 + (i * personEntry.getSize())));
			}

		}
		channel.close();
		raf.close();
		Collections.sort(pairs);

		tree.openAndBatchUpdate(pairs.iterator(), pairs.size());

		return tree;
	}

	public List<PersonEntry> intervalQuerySalaryEntries(PersonSalaryKey low, PersonSalaryKey high) throws IOException {
		List<SalaryAndOffsetEntry> pairs = super.intervalQuery(low, high);
		RandomAccessFile raf = new RandomAccessFile(personsFile, "r");
		FileChannel channel = raf.getChannel();
		ByteBuffer buffer = ByteBuffer.allocateDirect(PAGE_SIZE);
		List<PersonEntry> entries = new LinkedList<PersonEntry>();

		for (SalaryAndOffsetEntry pair : pairs) {
			buffer.clear();
			long pageOffset = (pair.getOffset() / PAGE_SIZE) * PAGE_SIZE;
			int bufferOffset = (int) (pair.getOffset() - pageOffset);
			channel.read(buffer, pageOffset);
			buffer.position(bufferOffset);
			PersonEntry personEntry = new PersonEntry();
			personEntry.load(buffer);
			entries.add(personEntry);

		}

		channel.close();
		raf.close();
		return entries;
	}

}
