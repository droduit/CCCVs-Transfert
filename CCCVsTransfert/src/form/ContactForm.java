package form;

import global.Global;
import global.GlobalObjects;
import global.Module;
import global.UserSession;

import java.io.Serializable;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.Arrays;

import main.CccvsTransfert;
import model.Contact;
import modules.ContactModule;


import sql.query.tbl_contacts;
import toolbox.Sql;
import toolbox.Utilities;

import com.vaadin.data.Item;
import com.vaadin.data.Property.ValueChangeEvent;
import com.vaadin.data.Property.ValueChangeListener;
import com.vaadin.data.util.BeanItem;
import com.vaadin.data.validator.EmailValidator;
import com.vaadin.data.validator.StringLengthValidator;
import com.vaadin.event.ShortcutAction.KeyCode;
import com.vaadin.ui.Alignment;
import com.vaadin.ui.Button;
import com.vaadin.ui.Component;
import com.vaadin.ui.CustomComponent;
import com.vaadin.ui.DefaultFieldFactory;
import com.vaadin.ui.Field;
import com.vaadin.ui.Form;
import com.vaadin.ui.HorizontalLayout;
import com.vaadin.ui.Table;
import com.vaadin.ui.TextField;
import com.vaadin.ui.VerticalLayout;
import com.vaadin.ui.Button.ClickEvent;
import com.vaadin.ui.Button.ClickListener;
import com.vaadin.ui.Component.Event;
import com.vaadin.ui.Window;
import com.vaadin.ui.Window.Notification;

import common.component.AccordionMenu;
import common.component.ContactTable;
import common.component.MenuContactTable;

/**
 * Cette class permet l'ajout et la mise � jour des contacts via un formulaire affich� dans une sous-fen�tre.
 * Un constructeur permet l'ajout, un autre permet la mise � jour.
 * @author Dominique
 *
 */
public class ContactForm extends CustomComponent {

	/** Contient toutes les instances qui doivent �tre accessibles dans toute l'application **/
	private GlobalObjects global = CccvsTransfert.getGlobalMethod();
	/** Instance de la sous-fen�tre qui va contenir ce formulaire **/
	private Window winContact = global.getWindowContact();
	/** Instance de la table pour l'affichage des contacts **/
	private ContactTable tblContact = global.getTableContact();
	/** Instance du bouton d'ajout d'un contact **/
	private Button btAddContact = global.getBtAddContact();
	/** Contient toutes les requ�tes SQL **/
	private tbl_contacts queries = new tbl_contacts();
	
	/** Layout vertical permettant d'afficher le formulaire en ligne **/
	private VerticalLayout mainLayout;
	/** Mod�le de contact **/
	private Contact contact;
	/** Bouton Ajouter **/
	private Button btApply;
	/** Formulaire permettant l'ajout et l'�dition **/
	private Form form;
	/** Bas du formulaire **/
	private HorizontalLayout footerLayout;
	/** Zone contenant les boutons du formulaire **/
	private HorizontalLayout buttons;
	/** Bouton Annuler **/
	private Button cancel;
	/** Etabli le lien entre le mod�le de Contact et les �l�ments du formulaire **/
	private BeanItem<Contact> contactItem;
	
	/**
	 * Affiche un formulaire pour l'ajout de contact
	 */
	public ContactForm() {		
		doForm();
		contact.setPK(0);
	}
	/**
	 * Affiche un formulaire pour l'�dition d'un contact
	 * @param PK La cl� primaire du contact � �diter
	 */
	public ContactForm(String PK) {
		// Renommage de la fen�tre
		winContact.setCaption(Global.i("CAPTION_CONTACT_EDIT"));

		doForm();
		
		btApply.setCaption(Global.i("CAPTION_SAVE"));
		contact.setPK(Integer.parseInt(PK)); 
		try {
			ResultSet c_data = Sql.query(queries.getSelectByPKContactQuery(PK));
			c_data.first();
			contact.setMail(c_data.getString("contact_mail"));
			contact.setName(c_data.getString("contact_name"));
			form.discard();
		} catch (SQLException e) {
			e.printStackTrace();
		}
	}
	/**
	 * Construction du formulaire
	 */
	private void doForm() {
		mainLayout = new VerticalLayout();
		mainLayout.setSizeFull();
		
		contact = new Contact();
		contactItem = new BeanItem<Contact>(contact);
		
		form = new Form();
		form.setWidth("300px");
		
		form.setFormFieldFactory(new ContactFieldFactory());
		form.setItemDataSource(contactItem);
		
		form.setVisibleItemProperties(Arrays.asList(new String[]{
			"name", "mail"	
		}));
		
		mainLayout.addComponent(form);
		mainLayout.setComponentAlignment(form, Alignment.MIDDLE_CENTER);
		
		footerLayout = new HorizontalLayout();
		footerLayout.setWidth("100%");
	
		buttons = new HorizontalLayout();
		buttons.setSpacing(true);
		
		// Cr�ation du bouton "Annuler"
		cancel = new Button(Global.i("CAPTION_CANCEL"), new Button.ClickListener() {
			public void buttonClick(ClickEvent event) {
				form.discard();
				(winContact.getParent()).removeWindow(winContact);
			}
		});
		cancel.setTabIndex(4);
		buttons.addComponent(cancel);
		buttons.setComponentAlignment(cancel, Alignment.MIDDLE_CENTER);
		
		// Cr�ation du bouton "Ajouter"
		btApply = new Button(Global.i("CAPTION_ADD"), new Button.ClickListener() {
			public void buttonClick(ClickEvent event) {
				try {
					form.commit();
				} catch(Exception e) {
					System.out.println(e);
				}
			}
		});
		btApply.setTabIndex(3);
		buttons.addComponent(btApply);
		
		footerLayout.addComponent(buttons);
		footerLayout.setComponentAlignment(buttons, Alignment.TOP_CENTER);
		form.setFooter(footerLayout);
		
		btApply.setClickShortcut(KeyCode.ENTER);
		btApply.addListener(SubmitListener);
		
		form.focus();
		
		setCompositionRoot(mainLayout);
	}
	/**
	 * Retourne une instance du bouton "Ajouter" de la sous-fen�tre
	 * @return Instance du bouton "Ajouter"
	 */
	public Button getButtonApply() {
		return btApply;
	}
	/**
	 * Ecouteur ex�cut� lors de la validation du formulaire
	 */
	private ClickListener SubmitListener = new ClickListener() {
		@Override
		public void buttonClick(ClickEvent event) {
			if(form.isValid()) {
				// On ne laisse aux externe ajouter que des adresses de personnes internes � la caisse
				if(UserSession.isInternal() || (!UserSession.isInternal() && Utilities.isInternal(contact.getMail()))) {
					// Si le mod�le de contact ne contient pas de valeur
					if(contact.getPK()==0) { // On ajoute le contact
						Sql.exec(queries.getInsertContactQuery(contact.getName(), contact.getMail()));
					} else { // On le met � jour
						Sql.exec(queries.getUpdateContactQuery(Integer.toString(contact.getPK()), contact.getName(), contact.getMail()));
					}
					
					if(tbl_contacts.getNumberOfContact()==1) {
						CccvsTransfert.loadModule(new Module(ContactModule.getBodyRibbonContent(), ContactModule.getBodyContent()));
					}
					
					// Mise � jour de la table
					tblContact.reload();
					// Mise � jour de la table du menu
					MenuContactTable menuContactTable = global.getMenuContactTable();
					menuContactTable.reload();
					
					// Fermeture de la sous-fen�tre
					(winContact.getParent()).removeWindow(winContact);
					// Focus sur le bouton d'ajout de contact
					btAddContact.focus();
					
					// On redonne le focus a la ligne en cours ou � la derni�re ligne ajout�e
					if(contact.getPK()!=0) {
						tblContact.select(contact.getPK());
					} else {
						try {
							ResultSet lastPK = Sql.query(tbl_contacts.getLastContact());
							lastPK.first();
							tblContact.select(lastPK.getInt("LAST"));
						} catch (SQLException e) {
							e.printStackTrace();
						} finally {
							Sql.Disconnect();
						}
					}
				} else { 
					getWindow().showNotification(null, Global.i("CAPTION_RESTRICT_EMAIL")+" "+Global.getParm("EMAIL_INTERNE_SUFFIXE"),Notification.TYPE_WARNING_MESSAGE);
				}
			}
		}
	};
	
	/**
	 * Classe li�es au formulaire de la sous-fen�tre.
	 * Elle permet de formatter et de programmer le comportement des �l�ments du formulaire.
	 * @author Dominique
	 *
	 */
	private class ContactFieldFactory extends DefaultFieldFactory {
		/**
		 * Cr�ation des �l�ments du formulaire
		 */
		public Field createField(Item item, Object propertyId, Component uiContext) {
			Field f;
			
			f = super.createField(item, propertyId, uiContext);
			
			if(propertyId.equals("name")) {
				TextField tf = (TextField) f;
				tf.setRequired(true);
				tf.setCaption(Global.i("CAPTION_NAME"));
				tf.setRequiredError(Global.i("CAPTION_EMPTY_NAME"));
				tf.setColumns(18);
				tf.setTabIndex(1);
				tf.addValidator(new StringLengthValidator(Global.i("CAPTION_LENGTH_RESTRICT_1"), 3,25,false));
			} else if(propertyId.equals("mail")) {
				TextField tf = (TextField) f;
				tf.setRequired(true);
				tf.setCaption(Global.i("CAPTION_EMAIL"));
				tf.setRequiredError(Global.i("CAPTION_EMPTY_MAIL"));
				tf.setColumns(18);
				tf.setTabIndex(2);
				tf.addValidator(new EmailValidator(Global.i("CAPTION_ERROR_MAIL")));
			} else if(propertyId.equals("PK")) {
				TextField tf = (TextField) f;
			}
			return f;
		}
	}
}
