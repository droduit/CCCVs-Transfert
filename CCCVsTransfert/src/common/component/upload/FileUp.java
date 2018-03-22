package common.component.upload;

import java.io.File;

/**
 * Model d'un fichier pour l'intégration dans le tableau d'upload.
 * La class sert à stocker les informations sur les fichiers sélectionnés dans le 
 * but de pouvoir récupérer tous les attributs d'un fichier.
 * @author Dominique Roduit
 *
 */
public class FileUp {
	/** Identifiant du fichier dans le tableau **/
	private int id;
	/** Informations sur le fichier enregistré sur le disque **/
	private File fileDiskInfo;
	/** Nom original du fichier (tel qu'il était nommé sur le PC de l'auteur) **/
	private String originalName;
	/** Nom du fichier renommé **/
	private String rename;
	/** Description du fichier **/
	private String description;
	
	/**
	 * Création d'un model de fichier pour la table d'upload
	 * @param fileDiskInfo Informations sur le fichier enregistré sur le disque
	 * @param originalName Nom original du fichier (tel qu'il était nommé sur le PC de l'auteur)
	 */
	public FileUp(File fileDiskInfo, String originalName) {
		this.setFileDiskInfo(fileDiskInfo);
		this.setOriginalName(originalName);
	}
	/**
	 * Création d'un model de fichier pour la table d'upload
	 * @param fileDiskInfo Informations sur le fichier enregistré sur le disque
	 * @param originalName Nom original du fichier (tel qu'il était nommé sur le PC de l'auteur)
	 * @param rename Nom du fichier renommé
	 */
	public FileUp(File fileDiskInfo, String originalName, String rename) {
		this(fileDiskInfo, originalName);
		setRename(rename);
	}
	/**
	 * Obtention du nom original du fichier
	 * @return Nom du fichier original
	 */
	public String getOriginalName() {
		return originalName;
	}
	/**
	 * Enregistrement du nom original du fichier
	 * @param originalName
	 */
	public void setOriginalName(String originalName) {
		this.originalName = originalName;
	}
	/**
	 * Obtention des informations sur le fichier enregistré sur le disque
	 * @return Informations sur le fichier enregistré
	 */
	public File getFileDiskInfo() {
		return fileDiskInfo;
	}
	/**
	 * Enregistrement des informations du fichier enregistré
	 * @param fileDiskInfo informations du fichier enregistréI
	 */
	public void setFileDiskInfo(File fileDiskInfo) {
		this.fileDiskInfo = fileDiskInfo;
	}
	/**
	 * @return Description du fichier
	 */
	public String getDescription() {
		return description;
	}
	/**
	 * @param description Description du fichier
	 */
	public void setDescription(String description) {
		this.description = description;
	}
	/**
	 * @return Nom du fichier redéfinit par l'utilisateur
	 */
	public String getRename() {
		return rename;
	}
	/**
	 * @param Nom du fichier redéfinit par l'utilisateur
	 */
	public void setRename(String rename) {
		this.rename = rename;
	}
	/**
	 * @return Obtention de l'identifiant du fichier dans le tableau
	 */
	public int getId() {
		return id;
	}
	/**
	 * @param id ID du fichier dans le tableau
	 */
	public void setId(int id) {
		this.id = id;
	}
}
