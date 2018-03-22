package model;

import java.io.Serializable;


/**
 * Modèle d'un fichier (Table trans_files).<br>
 * Permet le stockage des informations sur un fichier dans un objet.
 * @author Dominique Roduit
 *
 */
public class Files implements Serializable {
	private static final long serialVersionUID = 401647635446496726L;
	/** Clé primaire du fichier **/
	private int PKNoFile = 0;
	/** Clé étrangère du dossier qui contient le fichier **/
	private int FKNoFolder = 0;
	/** Nom du fichier (tel qu'enregistré sur le disque) **/
	private String file_name = "";
	/** Nom que l'utilisateur a donné au fichier **/
	private String file_rename = "";
	/** Taille du fichier (en octet) **/
	private long file_size = 0;
	/** Extension du fichier **/
	private String file_extension = "";
	/** Description du fichier **/
	private String file_description = "";
	/** Taille du fichier formattée par la class Utilities. **/
	private String file_size_formatted = "";
	
	/**
	 * Constructeur du model pour le stockage d'un model de fichier
	 * @param PKNoFile Clé primaire du fichier
	 * @param FKNoFolder Clé étrangère du dossier
	 * @param file_name Nom du fichier
	 * @param file_rename Nom du fichier spécifié par l'utilisateur
	 * @param file_size Taille du fichier
	 * @param file_extension Extension du fichier
	 * @param file_description Description du fichier
	 */
	public Files(int PKNoFile, int FKNoFolder, String file_name, String file_rename, long file_size,
			String file_extension, String file_description) {
		setPKNoFile(PKNoFile);
		setFKNoFolder(FKNoFolder);
		setFile_name(file_name);
		setFile_rename(file_rename);
		setFile_size(file_size);
		setFile_extension(file_extension);
		setFile_description(file_description);
	}
	/**
	 * Constructeur du model pour le stockage d'un model de fichier
	 * @param PKNoFile Clé primaire du fichier
	 * @param FKNoFolder Clé étrangère du dossier
	 * @param file_name Nom du fichier
	 * @param file_rename Nom du fichier spécifié par l'utilisateur
	 * @param file_size Taille du fichier
	 * @param file_extension Extension du fichier
	 * @param file_description Description du fichier
	 * @param file_size_formatted Taille du fichier formattée
	 */
	public Files(int PKNoFile, int FKNoFolder, String file_name, String file_rename, long file_size,
			String file_extension, String file_description, String file_size_formatted) {
		
		this(PKNoFile, FKNoFolder, file_name, file_rename, file_size, file_extension, file_description);
		setFile_size_formatted(file_size_formatted);
	}

	/**
	 * Obtention de la clé primaire du fichier
	 * @return Clé primaire du fichier
	 */
	public int getPKNoFile() {
		return PKNoFile;
	}
	/**
	 * Fixation de la clé primaire du fichier
	 * @param pKNoFile Clé primaire du fichier
	 */
	public void setPKNoFile(int pKNoFile) {
		PKNoFile = pKNoFile;
	}
	/**
	 * Obtention de la clé étrangère vers le dossier contenant le fichier
	 * @return Clé étrangère vers le dossier
	 */
	public int getFKNoFolder() {
		return FKNoFolder;
	}
	/**
	 * Paramétrage de la clé étrangère
	 * @param fKNoFolder Clé étrangère vers le dossier
	 */
	public void setFKNoFolder(int fKNoFolder) {
		FKNoFolder = fKNoFolder;
	}
	/**
	 * Obtention du nom du fichier tel qu'il est enregistré sur le disque
	 * @return Nom du fichier sur le disque
	 */
	public String getFile_name() {
		return file_name;
	}
	/**
	 * Réglage du nom de fichier
	 * @param file_name Nom du fichier
	 */
	public void setFile_name(String file_name) {
		this.file_name = file_name;
	}
	/**
	 * Obtention du nom de fichier spécifié par l'utilisateur
	 * @return Nom du fichier spécifié par l'utilisateur
	 */
	public String getFile_rename() {
		return file_rename;
	}
	/**
	 * Réglage du nom du fichier spécifié par l'utilisateur
	 * @param file_rename Nom du fichier spécifié par l'utilisateur
	 */
	public void setFile_rename(String file_rename) {
		this.file_rename = file_rename;
	}
	/**
	 * Obtention de la taille du fichier
	 * @return Taille du fichier (Octets)
	 */
	public long getFile_size() {
		return file_size;
	}
	/**
	 * Réglage de la taille du fichier
	 * @param file_size Taille du fichier en octets
	 */
	public void setFile_size(long file_size) {
		this.file_size = file_size;
	}
	/**
	 * Obtention de l'extension du fichier
	 * @return Extension du fichier
	 */
	public String getFile_extension() {
		return file_extension;
	}
	/**
	 * Réglage de l'extension du fichier
	 * @param file_extension Extension du fichier
	 */
	public void setFile_extension(String file_extension) {
		this.file_extension = file_extension;
	}
	/**
	 * Obtention de la description du fichier
	 * @return Description du fichier
	 */
	public String getFile_description() {
		return file_description;
	}
	/**
	 * Réglage de la description du fichier
	 * @param file_description Description du fichier
	 */
	public void setFile_description(String file_description) {
		this.file_description = file_description;
	}
	/**
	 * Obtention de la taille du fichier formattée
	 * @return Taille formattée
	 */
	public String getFile_size_formatted() {
		return file_size_formatted;
	}
	/**
	 * Réglage de la taille formattée du fichier
	 * @param file_size_formatted Taille formattée du fichier
	 */
	public void setFile_size_formatted(String file_size_formatted) {
		this.file_size_formatted = file_size_formatted;
	}
}
