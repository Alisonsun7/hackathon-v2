package persistentdata.formatted;

import persistentdata.PersistentDataException;

import java.io.IOException;
import java.io.Reader;
import java.util.ArrayList;

public class CSVReader implements FormattedReader<String[]> {
	private final CSVFormat format;
	private final Reader reader;
    private final String[] lines;
	private int lineIndex;

	public CSVReader(CSVFormat format, Reader reader) {
		this.format = format;
		this.reader = reader;
		// Read the file into a long String
        String file = this.readFile(reader);
		// Parse the file into lines
		this.lines = this.splitFile(file);
	}

	public boolean hasNext() {
		// TODO: Complete this method according to the CSV specification, without using dedicated libraries
		return this.lineIndex < this.lines.length;
	}

	// These format strings are provided to give you some ideas about what error cases might be encountered,
	// but they aren't complete. If you haven't seen these before, you can fill in the %s with .formatted:
	// for example, "hello %s".formatted("Bernardo") returns "hello Bernardo"
	private static final String LINE_TOO_SHORT_MESSAGE = "Line was too short: expected %s fields but found %s";
	private static final String LINE_TOO_LONG_MESSAGE = "Line was too long: expected %s fields";
	private static final String IMPROPER_ESCAPE_MESSAGE = "EOF reached unexpectedly while escaped";
	private static final String REACHED_EOF_MESSAGE = "Already reached end of file while reading";

	public String[] getNext() {
		// TODO: Complete this method according to the CSV specification, without using dedicated libraries
		if (!this.hasNext()) {
			throw new CSVIOException(REACHED_EOF_MESSAGE);
		}
		return this.splitLine(lines[this.lineIndex++]);
	}

	private String readFile(Reader reader) {
		try {
			StringBuilder sb = new StringBuilder();
			int ch;
			while ((ch = reader.read()) != -1) {
				sb.append((char) ch);
			}
			return sb.toString();
		} catch (IOException e) {
			throw new CSVIOException(e.toString());
		}
	}

	private String[] splitFile(String file) {
		StringBuilder line = new StringBuilder();
		ArrayList<String> lines = new ArrayList<>();

		if (file == null || file.isEmpty()) {
			return new String[0];
		}

		boolean inEscape = false;
		for (int i = 0; i < file.length(); i++) {
			char c = file.charAt(i);

			if (c == this.format.ESCAPE_MARKER) {
				// Meeting an escape marker
				inEscape = !inEscape;
				line.append(c);
			} else if (c == this.format.LINE_SEPARATOR && !inEscape) {
				// Meeting a real line separator
				lines.add(line.toString());
				line.setLength(0);
			} else {
				// Normal character
				line.append(c);
			}
		}

		if (inEscape) {
			throw new CSVIOException(IMPROPER_ESCAPE_MESSAGE);
		} else {
			lines.add(line.toString());
		}

		return lines.toArray(new String[0]);
	}

	private String[] splitLine(String line) {
		StringBuilder field = new StringBuilder();
		ArrayList<String> fields = new ArrayList<>();

		boolean inEscape = false;
		for (int i = 0; i < line.length(); i++) {
			char c = line.charAt(i);

			if (c == this.format.ESCAPE_MARKER) {
				// Meeting an escape marker
				if (inEscape && (i + 1) < line.length() && line.charAt(i + 1) == this.format.ESCAPE_MARKER) {
					// <""> inside <"...">
					field.append(this.format.ESCAPE_MARKER);
					i++;
				} else {
					// Status inversion
					inEscape = !inEscape;
				}
			} else if (c == this.format.FIELD_SEPARATOR && !inEscape) {
				// Meeting a real field separator
				fields.add(field.toString());
				field.setLength(0);
			} else {
				// Normal character
				field.append(c);
			}
		}

		if (inEscape) {
			throw new CSVIOException(IMPROPER_ESCAPE_MESSAGE);
		} else {
			fields.add(field.toString());
		}

		if (fields.size() > this.format.COLUMN_COUNT) {
			throw new CSVIOException(LINE_TOO_LONG_MESSAGE.formatted(fields.size()));
		} else if (fields.size() < this.format.COLUMN_COUNT) {
			throw new CSVIOException(LINE_TOO_SHORT_MESSAGE.formatted(this.format.COLUMN_COUNT, fields.size()));
		}

		return fields.toArray(new String[0]);
	}

	public static class CSVIOException extends PersistentDataException {
		public CSVIOException(String message) {
			super(message);
		}
	}
}
