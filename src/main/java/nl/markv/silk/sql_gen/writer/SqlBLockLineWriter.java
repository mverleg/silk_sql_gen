package nl.markv.silk.sql_gen.writer;

import javax.annotation.Nonnull;

public abstract class SqlBLockLineWriter implements SqlWriter {

	protected int writeCount = 0;
	private boolean needsLeadingComma = false;

	@Override
	public void statements(@Nonnull Runnable writeAction) {
		int oldWriteCount = writeCount;
		writeAction.run();
		if (writeCount != oldWriteCount) {
			addLine(";");
		}
	}

	@Override
	public void lineBlock(@Nonnull Runnable writeAction) {
		needsLeadingComma = true;
		writeAction.run();
		newline();
		needsLeadingComma = false;
	}

	@Override
	public void line(@Nonnull Runnable writeAction) {
		if (needsLeadingComma) {
			needsLeadingComma = false;
		} else {
			add(",\n\t");
		}
		int oldWriteCount = writeCount;
		writeAction.run();
		if (writeCount != oldWriteCount) {
			needsLeadingComma = false;
		}
	}
}
