package model;

import java.io.Serializable;

/**
 * Mod�le de contact.<br>
 * Cette class correspond aux champs de la base de donn�es pour la table tbl_contacts.
 * @author Dominique Roduit
 *
 */
public class Contact implements Serializable {
	/** Cl� primaire du contact **/
	private int PK = 0;
	/** Cl� �trang�re vers l'utilisateur qui a cr�� le contact **/
	private int FKNoUser = 0;
	/** Nom du contact **/
	private String name = "";
	/** Adresse e-mail du contact **/
	private String mail = "";
	/** Indique si le contact est un interne ou non **/
	private boolean internal = false;
	
	public Contact() {
		
	}
	/**
	 * Constructeur du model pour le stockage d'un model de contact
	 * @param PK Cl� primaire du contact
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
	 * Obtention de la cl� primaire du contact
	 * @return C�l primaire du contact
	 */
	public int getPK() {
		return PK;
	}
	/**
	 * D�finition de la cl� primaire du contact
	 * @param pK Cl� primaire du contact
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
	 * D�finition du nom du contact
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
	 * D�finition de l'adresse e-mail du contact
	 * @param email Adresse e-mail du contact
	 */
	public void setMail(String email) {
		this.mail = email;
	}
	/**
	 * Vidage des champs pour l'annulation des valeurs entr�es dans le formulaire d'ajout/�dition
	 */
	public void reset() {
		setName("");
		setMail("");
	}
	/**
	 * Obtention de la cl� �trang�re de l'utilisateur d�tenant le contact
	 * @return Cl� �trang�re de l'utilisateur
	 */
	public int getFKNoUser() {
		return FKNoUser;
	}
	/**
	 * D�finition de la cl� �trang�re de l'utilisateur d�tenant le contact
	 * @param fKNoUser Cl� �trang�re de l'utilisateur
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
	 * D�finit si le contact est un interne ou non
	 * @param internal true si le contact est un interne
	 */
	public void setInternal(boolean internal) {
		this.internal = internal;
	}
}