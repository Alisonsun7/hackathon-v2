package persistentdata.formatted;

import persistentdata.PersistentDataException;

import java.io.BufferedWriter;
import java.io.IOException;
import java.io.Writer;

public class CSVWriter implements FormattedWriter<String[]> {
	private final CSVFormat format;
	private final Writer writer;
	private boolean firstLine;

	public CSVWriter(CSVFormat format, Writer writer) {
		this.format = format;
		this.writer = writer;
		this.firstLine = true;
	}

	@Override
	public void putHeader() {
		// TODO: Complete this method according to the CSV specification, without using dedicated libraries
	}

	@Override
	public void putNext(String[] data) {
		// TODO: Complete this method according to the CSV specification, without using dedicated libraries
		try {
			if (!firstLine) {
				this.writer.write(format.LINE_SEPARATOR);
			} else {
				this.firstLine = false;
			}
			for (int i = 0; i < data.length; i++) {
				if (i > 0) {
					this.writer.write(format.FIELD_SEPARATOR);
				}
				String field = data[i];
				// Check field validity
				if (field == null) {
					this.writer.write("");
					continue;
				}
				// Check escape
				boolean needEscape = field.indexOf(format.FIELD_SEPARATOR) >= 0
						|| field.indexOf(format.LINE_SEPARATOR) >= 0
						|| field.indexOf(format.ESCAPE_MARKER) >= 0;
				if (needEscape) {
					String escaped = field.replace("%c".formatted(format.ESCAPE_MARKER),
							"%c%c".formatted(format.ESCAPE_MARKER, format.ESCAPE_MARKER));
					this.writer.write(format.ESCAPE_MARKER);
					this.writer.write(escaped);
					this.writer.write(format.ESCAPE_MARKER);
				} else {
					this.writer.write(field);
				}
			}
		} catch (IOException e) {
			throw new CSVReader.CSVIOException(e.toString());
		}
	}

	@Override
	public void putFooter() {
		// TODO: Complete this method according to the CSV specification, without using dedicated libraries
		try {
			this.writer.close();
		} catch (IOException e) {
			throw new CSVReader.CSVIOException(e.toString());
		}
	}

}
