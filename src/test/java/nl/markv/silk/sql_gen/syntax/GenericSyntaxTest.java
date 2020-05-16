package nl.markv.silk.sql_gen.syntax;

import java.util.Arrays;

import org.junit.jupiter.api.Test;

import static nl.markv.silk.sql_gen.syntax.GenericSyntax.nameFromCols;
import static nl.markv.silk.sql_gen.syntax.GenericSyntax.nameFromHash;
import static org.junit.jupiter.api.Assertions.*;

class GenericSyntaxTest {

	@Test
	void testNameFromCols() {
		String res = nameFromCols("p", "Table", Arrays.asList("ColA", "col_b"));
		assertEquals("p_Table_ColA_col_b", res);
	}

	@Test
	void testNameFromHash() {
		String res = nameFromHash("p_Table_", "ColA_col_b");
		assertEquals("p_Table_laWxIWq9uvZTfWUc", res);
	}

	@Test
	void testQuotesOn() {
		SqliteSyntax syn = new SqliteSyntax("abc", "1.2.3", new Syntax.SyntaxOptions(true, false));
		assertEquals("\"My_name\"", syn.quoted("My_name"));
	}

	@Test
	void testQuotesOff() {
		SqliteSyntax syn = new SqliteSyntax("abc", "1.2.3", new Syntax.SyntaxOptions(false, false));
		assertEquals("My_name", syn.quoted("My_name"));
	}
}