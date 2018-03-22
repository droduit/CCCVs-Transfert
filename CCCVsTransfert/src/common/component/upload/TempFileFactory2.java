package common.component.upload;


import java.io.File;
import java.io.IOException;

import org.vaadin.easyuploads.FileFactory;

/**
 * Enregistrement temporaire des fichiers s�lectionn�s pour l'upload.
 */
class TempFileFactory2 implements FileFactory {
	/**
	 * Surcharge de la m�thode createFile de la class FileFactory.
	 */
	public File createFile(String fileName, String mimeType) {
		final String tempFileName = "upload_tmpfile_"
				+ System.currentTimeMillis();
		try {
			System.out.println(File.createTempFile(tempFileName, null).getName());
			return File.createTempFile(tempFileName, null);
		} catch (IOException e) {
			throw new RuntimeException(e);
		}
	}

}