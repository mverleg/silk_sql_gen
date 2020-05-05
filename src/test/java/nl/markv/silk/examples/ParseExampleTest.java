package nl.markv.silk.examples;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Paths;

import org.junit.jupiter.api.Test;

import nl.markv.silk.SilkVersion;

import static nl.markv.silk.generate.Generate.generateSilkObjects;

public class ParseExampleTest {

	@Test
	public void generateExamples() throws IOException {
		generateSilkObjects(
				SilkVersion.versionPath(),
				Paths.get("..", ".."),
				Files.createTempDirectory("silk")
		);
	}
}
