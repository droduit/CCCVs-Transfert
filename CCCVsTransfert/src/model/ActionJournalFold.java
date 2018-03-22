package model;

/**
 * Mod�le pour le stockage d'actions journalis�es sur les dossiers<br><b>Table :</b> trans_journal_folders
 * @author Dominique Roduit
 *
 */
public class ActionJournalFold {
	/** Cl� primaire de l'action **/
	private int PKNoJourFolder;
	/** Type de l'action journalis�e<br><br><b>Actions</b><br><li>download</li><li>consult</li> **/
	private String action_type = "";
	/** Description de l'action ex�cut�e **/
	private String action_description = "";
	/** Fichier concern� par l'action **/
	private Files action_file;
	/** Dossier concern� par l'action **/
	private Folder action_folder;
	/** Date de l'action **/
	private String action_date;
	/** Contact qui d�clenche l'action **/
	private Contact action_contact;
	
	/**
	 * Cr�ation d'un objet pour une action journalis�e ex�cut�e sur un dossier
	 * @param Type Type d'action
	 * @param Description Description de l'action
	 */
	public ActionJournalFold(String Type, String Description) {
		setAction_type(Type);
		setAction_description(Description);
	}
	/**
	 * Cr�ation d'un objet pour une action journalis�e ex�cut�e sur un dossier
	 * @param action_type Type d'action
	 * @param action_description Description de l'action
	 * @param action_file Fichier concern� par l'action
	 * @param action_folder Dossier concern� par l'action
	 * @param action_date Date de l'action
	 * @param action_contact Contact qui d�clenche l'action
	 */
	public ActionJournalFold(int PKNoJourFolder, String action_type,
			Files action_file, Folder action_folder, String action_date,
			Contact action_contact) {
		this.PKNoJourFolder = PKNoJourFolder;
		this.action_type = action_type;
		this.action_file = action_file;
		this.action_folder = action_folder;
		this.action_date = action_date;
		this.action_contact = action_contact;
	}
	/**
	 * Obtention du type d'action
	 * @return Type d'action
	 */
	public String getAction_type() {
		return action_type;
	}
	/**
	 * Enregistrement du type d'action
	 * @param action_type Type d'action
	 */
	public void setAction_type(String action_type) {
		this.action_type = action_type;
	}
	/**
	 * Obtention de la description de l'action effectu�e
	 * @return Description de l'action effectu�e
	 */
	public String getAction_description() {
		return action_description;
	}
	/**
	 * Enregistrement de la description de l'action effectu�e
	 * @param action_description Description de l'action effectu�e
	 */
	public void setAction_description(String action_description) {
		this.action_description = action_description;
	}
	/**
	 * @return Fichier concern� par l'action
	 */
	public Files getAction_file() {
		return action_file;
	}
	/**
	 * @param action_file Fichier concern� par l'action
	 */
	public void setAction_file(Files action_file) {
		this.action_file = action_file;
	}
	/**
	 * @return Dossier concern� par l'action
	 */
	public Folder getAction_folder() {
		return action_folder;
	}
	/**
	 * @param action_folder Dossier concern� par l'action
	 */
	public void setAction_folder(Folder action_folder) {
		this.action_folder = action_folder;
	}
	/**
	 * @return Date de l'action
	 */
	public String getAction_date() {
		return action_date;
	}
	/**
	 * @param action_date Date de l'action
	 */
	public void setAction_date(String action_date) {
		this.action_date = action_date;
	}
	/**
	 * @return Contact qui d�clenche l'action
	 */
	public Contact getAction_contact() {
		return action_contact;
	}
	/**
	 * @param action_contact Contact qui d�clenche l'action
	 */
	public void setAction_contact(Contact action_contact) {
		this.action_contact = action_contact;
	}
	/**
	 * @return Cl� primaire de l'action
	 */
	public int getPKNoJourFolder() {
		return PKNoJourFolder;
	}
	/**
	 * @param pKNoJourFolder Cl� primaire de l'action
	 */
	public void setPKNoJourFolder(int pKNoJourFolder) {
		PKNoJourFolder = pKNoJourFolder;
	}
	
}