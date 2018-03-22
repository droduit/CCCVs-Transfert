package common.component.upload;

import java.io.File;

/**
 * Model d'un fichier pour l'int�gration dans le tableau d'upload.
 * La class sert � stocker les informations sur les fichiers s�lectionn�s dans le 
 * but de pouvoir r�cup�rer tous les attributs d'un fichier.
 * @author Dominique Roduit
 *
 */
public class FileUp {
	/** Identifiant du fichier dans le tableau **/
	private int id;
	/** Informations sur le fichier enregistr� sur le disque **/
	private File fileDiskInfo;
	/** Nom original du fichier (tel qu'il �tait nomm� sur le PC de l'auteur) **/
	private String originalName;
	/** Nom du fichier renomm� **/
	private String rename;
	/** Description du fichier **/
	private String description;
	
	/**
	 * Cr�ation d'un model de fichier pour la table d'upload
	 * @param fileDiskInfo Informations sur le fichier enregistr� sur le disque
	 * @param originalName Nom original du fichier (tel qu'il �tait nomm� sur le PC de l'auteur)
	 */
	public FileUp(File fileDiskInfo, String originalName) {
		this.setFileDiskInfo(fileDiskInfo);
		this.setOriginalName(originalName);
	}
	/**
	 * Cr�ation d'un model de fichier pour la table d'upload
	 * @param fileDiskInfo Informations sur le fichier enregistr� sur le disque
	 * @param originalName Nom original du fichier (tel qu'il �tait nomm� sur le PC de l'auteur)
	 * @param rename Nom du fichier renomm�
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
	 * Obtention des informations sur le fichier enregistr� sur le disque
	 * @return Informations sur le fichier enregistr�
	 */
	public File getFileDiskInfo() {
		return fileDiskInfo;
	}
	/**
	 * Enregistrement des informations du fichier enregistr�
	 * @param fileDiskInfo informations du fichier enregistr�I
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
	 * @return Nom du fichier red�finit par l'utilisateur
	 */
	public String getRename() {
		return rename;
	}
	/**
	 * @param Nom du fichier red�finit par l'utilisateur
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
