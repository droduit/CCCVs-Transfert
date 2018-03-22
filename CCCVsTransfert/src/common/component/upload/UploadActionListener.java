package common.component.upload;

import java.io.File;
import java.util.ArrayList;

import org.vaadin.easyuploads.MultiUpload.FileDetail;

/**
 * Interface contenant les �v�nements re�us par les fichiers lors de l�envoi.<br>
 * Son impl�mentation permet de d�tecter le d�but et la fin d�un transfert, de d�tecter les erreurs et de suivre la progression des fichiers.
 * @author steffen.c-on, Dominique Roduit
 * @version 18.03.2013
 */
public interface UploadActionListener {
  /** Called if a upload of a file is started.
   * @param filename name of the file which is currently uploading
   * @param pendingFiles number of pending files (without the currently uploading file)
   */
  void fileUploadStarted(int idFile, String filename, int pendingFiles);
  /** Called if a upload of a file is finished successful.
   * @param filename name of the file which is currently finished
   * @param pendingFiles number of pending files
   */
  void fileUploadFinished(int idFile, String filename, File file, int pendingFiles);
  /** Called if a upload of a file is finished with an error.
   * @param filename name of the file which is aborted
   * @param pendingFiles number of pending files
   */
  void fileUploadError(String filename, int pendingFiles);
  /**
   * Appel� durant la progression de l'upload d'un fichier
   * @param idFile Identifiant du fichier
   * @param filename Nom du fichier
   * @param pendingFiles Nombre de fichiers en attentes
   * @param progress Progression (de 0 � 1)
   */
  void fileUploadProgress(int idFile, String filename, int pendingFiles, float progress);
  /**
   * Appel� lorsque tout les fichiers sont envoy�s avec succ�s
   * @param fileList Liste des fichiers envoy�s
   */
  void fileUploadComplete(ArrayList<File> fileList);
  /**
   * Appel� lorsque les fichiers sont s�lectionn�s (ou d�pos�s dans la fen�tre)
   * @param error Code d'erreur retourn� (s'il y en a une)
   */
  void onSelectedFiles(int error);
  
}