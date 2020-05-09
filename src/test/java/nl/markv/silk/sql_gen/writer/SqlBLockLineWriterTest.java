package nl.markv.silk.sql_gen.writer;

import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertEquals;

class SqlBLockLineWriterTest {

	@Test
	void noStatement() {
		SqlStringWriter sql = new SqlStringWriter();
		sql.statements(() -> {});
		assertEquals("", sql.build());
	}

	@Test
	void singleStatement() {
		SqlStringWriter sql = new SqlStringWriter();
		sql.statements(() -> sql.add("drop table Users"));
		assertEquals("drop table Users;\n", sql.build());
	}

	@Test
	void multipleStatementsOneWrite() {
		SqlStringWriter sql = new SqlStringWriter();
		sql.statements(() -> sql.add("drop table Users;\ndrop table Payments"));
		assertEquals("drop table Users;\ndrop table Payments;\n", sql.build());
	}

	@Test
	void multipleStatementsMultipleWrite() {
		SqlStringWriter sql = new SqlStringWriter();
		sql.statements(() -> {
			sql.addLine("drop table Users;");
			sql.add("drop table Payments");
		});
		assertEquals("drop table Users;\ndrop table Payments;\n", sql.build());
	}

	@Test
	void line() {
	}
}