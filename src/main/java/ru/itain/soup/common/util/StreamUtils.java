package ru.itain.soup.common.util;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.PipedInputStream;
import java.io.PipedOutputStream;

public final class StreamUtils {

	private StreamUtils() {
	}

	public static InputStream pipe(ByteArrayOutputStream outputStream) {
		PipedInputStream in = new PipedInputStream();
		new Thread(() -> {
			try (PipedOutputStream out = new PipedOutputStream(in)) {
				outputStream.writeTo(out);
			} catch (IOException e) {
				throw new IllegalStateException("Can't pipe", e);
			}
		}).start();
		return in;
	}

}
