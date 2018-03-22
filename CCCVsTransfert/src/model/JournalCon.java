package model;

/**
 * Mod�le d'une action de connexion journalis�e (Table trans_journal_connections).<br>
 * Permet le stockage des actions de connexion dans un objet Java.
 * @author Dominique Roduit
 *
 */
public class JournalCon {
	/** Cl� primaire de l'action **/
	private int PKNoJourConnection;
	/** L'adresse e-mail de la personne qui d�clenche l'action **/
	private String joco_mail;
	/** La date � laquelle l'action est survenue **/
	private String joco_date;
	/** L'adresse IP publique de la personne qui a d�clench� l'action **/
	private String joco_ip;
	/** Syst�me d'exploitation de la personne qui d�clenche l'action **/
	private String joco_os;
	/** Nom de navigateur et version de la personne qui d�clenche l'action **/
	private String joco_browser;
	/** Action d�clench�e **/
	private String joco_action;
	/** Commentaire sur l'action **/
	private String joco_comment;
	
	
	public JournalCon(int pKNoJourConnection, String joco_mail,
			String joco_date, String joco_ip, String joco_os,
			String joco_browser, String joco_action, String joco_comment) {
		PKNoJourConnection = pKNoJourConnection;
		this.joco_mail = joco_mail;
		this.joco_date = joco_date;
		this.joco_ip = joco_ip;
		this.joco_os = joco_os;
		this.joco_browser = joco_browser;
		this.joco_action = joco_action;
		this.joco_comment = joco_comment;
	}
	/**
	 * @return Cl� primaire de l'action
	 */
	public int getPKNoJourConnection() {
		return PKNoJourConnection;
	}
	/**
	 * @param pKNoJourConnection Cl� primaire de l'action
	 */
	public void setPKNoJourConnection(int pKNoJourConnection) {
		PKNoJourConnection = pKNoJourConnection;
	}
	/**
	 * @return L'adresse e-mail de la personne qui d�clenche l'action
	 */
	public String getJoco_mail() {
		return joco_mail;
	}
	/**
	 * @param joco_mail L'adresse e-mail de la personne qui d�clenche l'action
	 */
	public void setJoco_mail(String joco_mail) {
		this.joco_mail = joco_mail;
	}
	/**
	 * @return La date � laquelle l'action est survenue
	 */
	public String getJoco_date() {
		return joco_date;
	}
	/**
	 * @param joco_date La date � laquelle l'action est survenue
	 */
	public void setJoco_date(String joco_date) {
		this.joco_date = joco_date;
	}
	/**
	 * @return L'adresse IP publique de la personne qui a d�clench� l'action
	 */
	public String getJoco_ip() {
		return joco_ip;
	}
	/**
	 * @param joco_ip L'adresse IP publique de la personne qui a d�clench� l'action
	 */
	public void setJoco_ip(String joco_ip) {
		this.joco_ip = joco_ip;
	}
	/**
	 * @return Syst�me d'exploitation de la personne qui d�clenche l'action
	 */
	public String getJoco_os() {
		return joco_os;
	}
	/**
	 * @param joco_os Syst�me d'exploitation de la personne qui d�clenche l'action
	 */
	public void setJoco_os(String joco_os) {
		this.joco_os = joco_os;
	}
	/**
	 * @return Nom de navigateur et version de la personne qui d�clenche l'action
	 */
	public String getJoco_browser() {
		return joco_browser;
	}
	/**
	 * @param joco_browser Nom de navigateur et version de la personne qui d�clenche l'action
	 */
	public void setJoco_browser(String joco_browser) {
		this.joco_browser = joco_browser;
	}
	/**
	 * @return Action d�clench�e
	 */
	public String getJoco_action() {
		return joco_action;
	}
	/**
	 * @param joco_action Action d�clench�e
	 */
	public void setJoco_action(String joco_action) {
		this.joco_action = joco_action;
	}
	/**
	 * @return Commentaire sur l'action
	 */
	public String getJoco_comment() {
		return joco_comment;
	}
	/**
	 * @param joco_comment Commentaire sur l'action
	 */
	public void setJoco_comment(String joco_comment) {
		this.joco_comment = joco_comment;
	}
	
	
}
