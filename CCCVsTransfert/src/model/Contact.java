package model;

import java.io.Serializable;

/**
 * Modèle de contact.<br>
 * Cette class correspond aux champs de la base de données pour la table tbl_contacts.
 * @author Dominique Roduit
 *
 */
public class Contact implements Serializable {
	private static final long serialVersionUID = 5247113982637915044L;
	/** Clé primaire du contact **/
	private int PK = 0;
	/** Clé étrangère vers l'utilisateur qui a créé le contact **/
	private int FKNoUser = 0;
	/** Nom du contact **/
	private String name = "";
	/** Adresse e-mail du contact **/
	private String mail = "";
	/** Indique si le contact est un interne ou non **/
	private boolean internal = false;
	/** Date de création du contact **/
	private String creation_date = "";
	
	public Contact() {
		
	}
	/**
	 * Constructeur du model pour le stockage d'informations sur un contact
	 * @param PK Clé primaire du contact
	 * @param name Nom du contact
	 * @param mail Adresse e-mail du contact
	 * @param internal true si le contact est un interne
	 */
	public Contact(int PK, String name, String mail, boolean internal) {
		setPK(PK);
		setName(name);
		setMail(mail);
		setInternal(internal);
	}
	/**
	 * Contructeur du model pour le stockage d'informations sur un contact
	 * @param PK Clé primaire du contact
	 * @param FKNoUser Clé étrangère vers l'utilisateur qui détient ce contact dans sa liste
	 * @param name Nom du contact
	 * @param mail Adresse e-mail du contact
	 * @param creation_date Date de créatin du contact
	 * @param internal true si le contact est un interne
	 */
	public Contact(int PKNoContact, int FKNoUser, String name, String mail, String creation_date, boolean internal) {
		this(PKNoContact, name, mail, internal);
		setFKNoUser(FKNoUser);
		setCreation_date(creation_date);
	}
	/**
	 * Obtention de la clé primaire du contact
	 * @return Cél primaire du contact
	 */
	public int getPK() {
		return PK;
	}
	/**
	 * Définition de la clé primaire du contact
	 * @param pK Clé primaire du contact
	 */
	public void setPK(int pK) {
		PK = pK;
	}
	/**
	 * Obtention du nom du contact
	 * @return Nom du contact
	 */
	public String getName() {
		return name;
	}
	/**
	 * Définition du nom du contact
	 * @param name Nom du contact
	 */
	public void setName(String name) {
		this.name = name;
	}
	/**
	 * Obtention de l'adresse e-mail du contact 
	 * @return Adresse e-mail du contact
	 */
	public String getMail() {
		return mail;
	}
	/**
	 * Définition de l'adresse e-mail du contact
	 * @param email Adresse e-mail du contact
	 */
	public void setMail(String email) {
		this.mail = email;
	}
	/**
	 * Vidage des champs pour l'annulation des valeurs entrées dans le formulaire d'ajout/édition
	 */
	public void reset() {
		setName("");
		setMail("");
	}
	/**
	 * Obtention de la clé étrangère de l'utilisateur détenant le contact
	 * @return Clé étrangère de l'utilisateur
	 */
	public int getFKNoUser() {
		return FKNoUser;
	}
	/**
	 * Définition de la clé étrangère de l'utilisateur détenant le contact
	 * @param fKNoUser Clé étrangère de l'utilisateur
	 */
	public void setFKNoUser(int fKNoUser) {
		FKNoUser = fKNoUser;
	}
	/**
	 * Indique si le contact est un interne ou non
	 * @return true si le contact est un interne
	 */
	public boolean isInternal() {
		return internal;
	}
	/**
	 * Définit si le contact est un interne ou non
	 * @param internal true si le contact est un interne
	 */
	public void setInternal(boolean internal) {
		this.internal = internal;
	}
	/**
	 * @return Date de création du contact
	 */
	public String getCreation_date() {
		return creation_date;
	}
	/**
	 * @param creation_date Date de création du contact
	 */
	public void setCreation_date(String creation_date) {
		this.creation_date = creation_date;
	}
}