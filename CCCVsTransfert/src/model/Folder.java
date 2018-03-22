package model;

import java.io.Serializable;

/**
 * Mod�le d'un dossier de transfert (Table trans_folders).<br>
 * Permet le stockage des informations sur un dossier dans un objet.
 * @author Dominique Roduit
 *
 */
public class Folder implements Serializable {
	private static final long serialVersionUID = -6316591403478282596L;
	/** Cl� primaire du dossier **/
	private int PKNoFolder;
	/** Cl� �trang�re vers l'utilisateur, auteur du dossier */
	private int FKNoUser;
	/** Nom du dossier **/
	private String name = "";
	/** Description du dossier **/
	private String description = "";
	/** Date de cr�ation du dossier **/
	private String creation_date;
	/** Dur�e de vie du dossier (en jours) **/
	private double expiration = 1;
	/** Date d'expiration du dossier **/
	private String expiration_date = "";
	/** Statut du dossier (archive ou disponible) **/
	private boolean archive = false;
	
	public Folder() {
		
	}
	/**
	 * Constructeur du model pour le stockage d'un model de dossier
	 * @param PKNoFolder Cl� primaire du dossier
	 * @param FKNoUser Cl� �trang�re vers l'utilisateur
	 * @param folder_name Nom du dossier
	 * @param folder_creation_date Date de cr�ation du dossier
	 * @param folder_expiration Date d'expiration du dossier
	 * @param folder_archive true si le dossier est une archive
	 */
	public Folder(int PKNoFolder, int FKNoUser, String folder_name, String description, String folder_creation_date,
			String folder_expiration, boolean folder_archive) {
		setPKNoFolder(PKNoFolder);
		setFKNoUser(FKNoUser);
		setName(folder_name);
		setCreation_date(folder_creation_date);
		setExpiration_date(folder_expiration);
		setArchive(folder_archive);
		setDescription(description);
	}

	/**
	 * Obtention de la cl� primaire du dossier
	 * @return Cl� primaire du dossier
	 */
	public int getPKNoFolder() {
		return PKNoFolder;
	}
	/**
	 * R�glage de la cl� primaire du dossier
	 * @param pKNoFolder Cl� primaire du dossier
	 */
	public void setPKNoFolder(int pKNoFolder) {
		PKNoFolder = pKNoFolder;
	}
	/**
	 * Obtention de la cl� �trang�re de l'utilisateur
	 * @return Cl� �trang�re de l'utilisateur
	 */
	public int getFKNoUser() {
		return FKNoUser;
	}
	/**
	 * R�glage de la cl� �trang�re de l'utilisateur
	 * @param fKNoUser Cl� �trang�re de l'utilisateur
	 */
	public void setFKNoUser(int fKNoUser) {
		FKNoUser = fKNoUser;
	}
	/**
	 * Obtention du nom du dossier
	 * @return Nom du dossier
	 */
	public String getName() {
		return name;
	}
	/**
	 * R�glage du nom du dossier
	 * @param name Nom du dossier
	 */
	public void setName(String name) {
		this.name = name;
	}
	/**
	 * @return La Description du dossier
	 */
	public String getDescription() {
		return description;
	}
	/**
	 * @param description Description du dossier
	 */
	public void setDescription(String description) {
		this.description = description;
	}
	/**
	 * Obtention de la date de cr�ation du dossier
	 * @return Date de cr�ation du dossier
	 */
	public String getCreation_date() {
		return creation_date;
	}
	/**
	 * D�finition de la date de cr�ation du dossier
	 * @param creation_date Date de cr�ation du dossier
	 */
	public void setCreation_date(String creation_date) {
		this.creation_date = creation_date;
	}
	/**
	 * Obtention de la dur�e de vie du dossier
	 * @return Dur�e de vie du dossier
	 */
	public double getExpiration() {
		return expiration;
	}
	/**
	 * D�finition de la dur�e de vie du dossier
	 * @param expiration Dur�e de vie du dossier en jours
	 */
	public void setExpiration(double expiration) {
		this.expiration = expiration;
	}
	/**
	 * Indique si le dossier est une archive ou non
	 * @return true si le dossier est une archive (expir�)
	 */
	public boolean getArchive() {
		return archive;
	}
	/**
	 * D�finit si le dossier est une archive ou non
	 * @param archive true si le dossier est une archive
	 */
	public void setArchive(boolean archive) {
		this.archive = archive;
	}
	/**
	 * Obtention de la date d'expiration du dossier
	 * @return Date d'expiration du dossier au format MySQL
	 */
	public String getExpiration_date() {
		return expiration_date;
	}
	/**
	 * D�finition de la date d'expiration du dossier
	 * @param expiration_date Date d'expiration du dossier au format MySQL
	 */
	public void setExpiration_date(String expiration_date) {
		this.expiration_date = expiration_date;
	}
}
