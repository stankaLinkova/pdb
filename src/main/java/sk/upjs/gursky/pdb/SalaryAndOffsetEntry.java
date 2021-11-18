package sk.upjs.gursky.pdb;

import java.nio.ByteBuffer;

import sk.upjs.gursky.bplustree.BPObject;

public class SalaryAndOffsetEntry implements BPObject<PersonSalaryKey, SalaryAndOffsetEntry> {

	private static final long serialVersionUID = 996943218866351534L;
	int salary;
	long offset;

	public SalaryAndOffsetEntry() {
	}

	public SalaryAndOffsetEntry(int salary, long offset) {
		this.salary = salary;
		this.offset = offset;
	}

	public long getOffset() {
		return offset;
	}

	public int getSalary() {
		return salary;
	}

	@Override
	public int compareTo(SalaryAndOffsetEntry o) {
		return Integer.compare(this.salary, o.salary);
	}

	@Override
	public void load(ByteBuffer bb) {
		salary = bb.getInt();
		offset = bb.getLong();

	}

	@Override
	public void save(ByteBuffer bb) {
		bb.putInt(salary);
		bb.putLong(offset);
	}

	@Override
	public int getSize() {
		return 12;
	}

	@Override
	public PersonSalaryKey getKey() {
		return new PersonSalaryKey(salary);
	}

}
