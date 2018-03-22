package common.component.upload;

import java.io.File;
import java.util.ArrayList;

import org.vaadin.easyuploads.MultiUpload.FileDetail;

/**
 * Interface contenant les évènements reçus par les fichiers lors de l’envoi.<br>
 * Son implémentation permet de détecter le début et la fin d’un transfert, de détecter les erreurs et de suivre la progression des fichiers.
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
   * Appelé durant la progression de l'upload d'un fichier
   * @param idFile Identifiant du fichier
   * @param filename Nom du fichier
   * @param pendingFiles Nombre de fichiers en attentes
   * @param progress Progression (de 0 à 1)
   */
  void fileUploadProgress(int idFile, String filename, int pendingFiles, float progress);
  /**
   * Appelé lorsque tout les fichiers sont envoyés avec succès
   * @param fileList Liste des fichiers envoyés
   */
  void fileUploadComplete(ArrayList<File> fileList);
  /**
   * Appelé lorsque les fichiers sont sélectionnés (ou déposés dans la fenêtre)
   * @param error Code d'erreur retourné (s'il y en a une)
   */
  void onSelectedFiles(int error);
  
}