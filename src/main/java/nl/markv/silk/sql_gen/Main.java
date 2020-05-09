package nl.markv.silk.sql_gen;

import org.apache.commons.lang3.NotImplementedException;

import nl.markv.silk.example.Examples;
import nl.markv.silk.sql_gen.writer.SqlStringWriter;
import nl.markv.silk.types.SilkSchema;

public class Main {
	public static void main(String[] args) {
		for (SilkSchema schema : new Examples().jsons()) {
			SqlStringWriter writer = new SqlStringWriter();
			if (1==1) throw new NotImplementedException("todo: ");  //TODO @mark: implement
			//Generator.generate(writer, schema, Generator.Dialect.Sqlite);
			System.out.println(writer.build());
		}
	}
}
