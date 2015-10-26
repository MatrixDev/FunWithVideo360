package dev.matrix.video360.utils;

import java.io.ByteArrayOutputStream;
import java.io.InputStream;

/**
 * @author rostyslav.lesovyi
 */
public class Utils {

	public static String read(InputStream is) {
		try {
			ByteArrayOutputStream bos = new ByteArrayOutputStream();

			int len;
			byte[] buffer = new byte[4096];

			while ((len = is.read(buffer)) > 0) {
				bos.write(buffer, 0, len);
			}
			return new String(bos.toByteArray());
		} catch (Exception ex) {
			return "";
		}
	}

}
