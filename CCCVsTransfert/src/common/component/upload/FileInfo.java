package common.component.upload;

/**
 * Class intermédiaire dont le but est de stocker des informations précises sur un fichier.<br>
 * On peut ensuite utiliser directement ce modèle pour récupérer tous les attributs du fichier dont on a besoin.<br>
 * Utilisé exclusivement pour l'upload de fichier dans la class {@link MultiFileUpload}.
 * @author Dominique Roduit
 *
 */
public class FileInfo {
	/** Identifiant du fichier **/
	private int id = 0;
	/** Nom du fichier **/
	private String name = "";
	/** Taille du fichier **/
	private long size = 0;
	/** Type MIME du fichier **/
	private String type = "";

	/**
	 * Création d'un model de fichier pour le stockage des informations d'un fichier dans un objet
	 * @param id Identifiant du fichier
	 * @param fileName Nom du fichier
	 * @param contentLength Taille du fichier
	 * @param mimeType Type MIME du fichier
	 */
	public FileInfo(int id, String fileName, long contentLength, String mimeType) {
		this.id = id;
		name = fileName;
		size = contentLength;
		type = mimeType;
	}
	/**
	 * @return Nom du fichier
	 */
	public String getName() {
		return name;
	}
	/**
	 * @return Taille du fichier
	 */
	public long getSize() {
		return size;
	}
	/**
	 * @return Type MIME du fichier
	 */
	public String getType() {
		return type;
	}
	/**
	 * @return Identifiant du fichier
	 */
	public int getId() {
		return id;
	}
	/**
	 * @param id Identifiant du fichier
	 */
	public void setId(int id) {
		this.id = id;
	}	
}
