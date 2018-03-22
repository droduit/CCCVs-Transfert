package form;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;

import sql.query.tbl_contacts;
import sql.query.tbl_recipients;
import toolbox.Sql;
import toolbox.Utilities;

import main.CccvsTransfert;
import model.Contact;
import model.Folder;

import com.vaadin.data.Item;
import com.vaadin.data.Property;
import com.vaadin.data.Property.ValueChangeEvent;
import com.vaadin.data.util.BeanItem;
import com.vaadin.data.util.BeanItemContainer;
import com.vaadin.data.validator.EmailValidator;
import com.vaadin.data.validator.StringLengthValidator;
import com.vaadin.event.Action;
import com.vaadin.event.ItemClickEvent;
import com.vaadin.event.ItemClickEvent.ItemClickListener;
import com.vaadin.event.ShortcutAction.KeyCode;
import com.vaadin.terminal.ThemeResource;
import com.vaadin.ui.AbstractSelect;
import com.vaadin.ui.Alignment;
import com.vaadin.ui.Button;
import com.vaadin.ui.ComboBox;
import com.vaadin.ui.Component;
import com.vaadin.ui.CustomComponent;
import com.vaadin.ui.DefaultFieldFactory;
import com.vaadin.ui.Field;
import com.vaadin.ui.Form;
import com.vaadin.ui.HorizontalLayout;
import com.vaadin.ui.Slider;
import com.vaadin.ui.Slider.ValueOutOfBoundsException;
import com.vaadin.ui.Table;
import com.vaadin.ui.TextField;
import com.vaadin.ui.VerticalLayout;
import com.vaadin.ui.Window;
import com.vaadin.ui.Button.ClickEvent;
import com.vaadin.ui.Button.ClickListener;
import com.vaadin.ui.Window.Notification;

import common.component.ContactTable;
import common.component.MenuContactTable;

import global.Global;
import global.GlobalObjects;
import global.UserSession;

/**
 * 2e vue de l'assistant pour la création d'un dossier.<br>
 * Cette vue affiche un tableau ainsi qu'une liste déroulante contenant la liste de tous les contacts
 * de l'utilisateur. L'utilisateur peut choisir lesquels contacts il souhaite ajouter comme destinataires.
 * Cette class transmet les informations sélectionnées à la vue suivante ({@link WizFoldUpload}).
 * @author Dominique Roduit
 *
 */
public class WizFoldDest extends CustomComponent {
	/** Contient toutes les instances qui doivent être accessibles dans toute l'application **/
	private GlobalObjects global = CccvsTransfert.getGlobalMethod();
	/** Instance de la sous-fenêtre qui va contenir ce formulaire **/
	private Window winFolder = global.getWindowFolder();
	/** Layout vertical permettant d'afficher le formulaire en ligne **/
	private VerticalLayout mainLayout;
	/** Modèle de contact **/
	private Contact contact;
	/** Bouton Ajouter **/
	private Button btApply;
	/** Formulaire permettant l'ajout et l'édition **/
	private Form form;
	/** Bas du formulaire **/
	private HorizontalLayout footerLayout;
	/** Zone contenant les boutons du formulaire **/
	private HorizontalLayout buttons;
	/** Bouton Annuler **/
	private Button cancel;
	/** Etabli le lien entre le modèle de Contact et les éléments du formulaire **/
	private BeanItemContainer<Contact> container = new BeanItemContainer<Contact>(Contact.class);
	/** Informations sur le dossier **/
	private Folder folder = null;
	/** Liste déroulante qui contient les contacts **/
	private ComboBox cbxContact;
	/** Tableau visuel contenant les destinataires sélectionnés **/
	private Table tblRecipients;
	/** Tableau contenant les destinataires sélectionnés **/
	private ArrayList<Integer> slctContacts = new ArrayList<Integer>();
	/** Action "Supprimer" du menu contextuel **/
	private Action ACTION_DEL;
	

	/**
	 * Affiche un formulaire pour l'ajout de destinataires à un dossier
	 * @param folder Informations sur le dossier, définies dans le wizard 1
	 */
	public WizFoldDest(Folder folder) {	
		this.folder = folder;
		
		ACTION_DEL = new Action(Global.i("CAPTION_REVOKE"),new ThemeResource(Global.PATH_THEME_RESSOURCES+"false.png"));
		
		winFolder.setClosable(false);
		winFolder.setWidth("540px");
		winFolder.setHeight("300px");
		winFolder.setCaption(Global.i("CAPTION_TRANSFERT")+" - "+Global.i("TITLE_WINDOW_ADD_RECIPIENTS"));
		winFolder.setIcon(new ThemeResource(Global.PATH_THEME_RESSOURCES+"user--arrow.png"));
		
		
		doForm();
	}
	/**
	 * Construction du formulaire
	 */
	private void doForm() {
		mainLayout = new VerticalLayout();
		mainLayout.setSizeFull();
		mainLayout.setSpacing(true);
		
		contact = new Contact();
		
		// Ajout d'une combobox qui permet de sélectionner les contacts a ajouter
		ArrayList<Contact> contactList = getContactList();
		container.addAll(contactList);
		
		cbxContact = new ComboBox(null);
		cbxContact.setContainerDataSource(container);
		cbxContact.setNullSelectionAllowed(false);
		cbxContact.setImmediate(true);
		cbxContact.setItemCaptionPropertyId("name");
		cbxContact.setItemCaptionMode(AbstractSelect.ITEM_CAPTION_MODE_PROPERTY);
		cbxContact.setWidth("100%");
		cbxContact.setInputPrompt(Global.i("CAPTION_SELECT_CONTACT"));
		cbxContact.focus();
		
		tblRecipients = new Table();
		tblRecipients.setSizeFull();
		tblRecipients.setHeight("160px");
		tblRecipients.setSelectable(true);
		tblRecipients.setColumnCollapsingAllowed(true);
		tblRecipients.addContainerProperty("name", String.class, null, Global.i("CAPTION_NAME"), null, null);
		tblRecipients.addContainerProperty("email", String.class, null, Global.i("CAPTION_ADRESSE_EMAIL"), null, null);
		tblRecipients.addContainerProperty("internal", String.class, null, Global.i("CAPTION_INTERNAL"), null, Table.ALIGN_CENTER);
		tblRecipients.setColumnCollapsed("internal", true);
		tblRecipients.addActionHandler(new Action.Handler() {
			public void handleAction(Action action, Object sender, Object target) {
				if(action==ACTION_DEL) {
					tblRecipients.removeItem(tblRecipients.getValue());
				}
			}
			public Action[] getActions(Object target, Object sender) {
				if(target!=null) {
					return new Action[] { ACTION_DEL };
				} else {
					return null;
				}
			}
		});
		tblRecipients.addListener(new ItemClickListener() {
			public void itemClick(ItemClickEvent event) {
				if(event.getButton()==ItemClickEvent.BUTTON_RIGHT) {
					tblRecipients.setValue(null);   // Dé-sélectionne tout
					tblRecipients.select(event.getItemId()); // Sélectionne la ligne en cours
				}
			}
		});

		cbxContact.addListener(new Property.ValueChangeListener() {
			public void valueChange(ValueChangeEvent event) {
				if(cbxContact.getValue()!=null) {
					Contact slctContact = ((Contact) cbxContact.getValue());
					
					String interne_caption = (slctContact.isInternal()) ? Global.i("CAPTION_INTERNAL") : Global.i("CAPTION_EXTERNAL");
					Object[] item = new Object[]{ slctContact.getName(), slctContact.getMail(), interne_caption};
					tblRecipients.addItem(item, slctContact.getPK());
					cbxContact.setValue(null);
				}
			}
		});
		
		
		
		mainLayout.addComponent(cbxContact);
		mainLayout.addComponent(tblRecipients);
		
		
	
		buttons = new HorizontalLayout();
		buttons.setSpacing(true);
		mainLayout.addComponent(buttons);
		mainLayout.setComponentAlignment(buttons, Alignment.BOTTOM_RIGHT);
		
		cancel = new Button(Global.i("CAPTION_CANCEL"), new Button.ClickListener() {
			public void buttonClick(ClickEvent event) {
				(winFolder.getParent()).removeWindow(winFolder);
			}
		});
		cancel.setTabIndex(4);
		buttons.addComponent(cancel);
		buttons.setComponentAlignment(cancel, Alignment.BOTTOM_RIGHT);
		
		// Création du bouton "Ajouter"
		btApply = new Button(Global.i("CAPTION_NEXT")+" »");
		btApply.setTabIndex(3);
		buttons.addComponent(btApply);
		
		btApply.setClickShortcut(KeyCode.ENTER);
		btApply.addListener(SubmitListener);
		
		setCompositionRoot(mainLayout);
	}
	/**
	 * Retourne la liste des contacts de l'utilisateur connecté
	 * @return Liste des contacts
	 */
	private ArrayList<Contact> getContactList() {
		ArrayList<Contact> contacts = new ArrayList<Contact>();
		ResultSet data = Sql.query(tbl_contacts.getSelectAllContactQuery());
		try {
			while(data.next()) {
				contacts.add(new Contact(
					data.getInt("PKNoContact"),
					data.getString("contact_name"),
					data.getString("contact_mail"),
					data.getBoolean("contact_internal")
				));
			}
		} catch (SQLException e) {
			e.printStackTrace();
		}
		return contacts;
	}
	/**
	 * Action lors de la soumission du wizard
	 */
	private ClickListener SubmitListener = new ClickListener() {
		@Override
		public void buttonClick(ClickEvent event) {
			// On récupère les contact sélectionnés
			for(Object id : tblRecipients.getItemIds()) {
				slctContacts.add(Integer.parseInt(id.toString()));
			}
			
			// Si on a au moins un contact dans notre tableau
			if(slctContacts.size()>0) {
				// Passage au wizard suivant
				winFolder.removeAllComponents();
				winFolder.addComponent(new WizFoldUpload(folder, slctContacts));
			} else {
				getWindow().showNotification(null, Global.i("CAPTION_ADD_RECIPIENTS"),Notification.TYPE_WARNING_MESSAGE);
			}
		}
	};
	
}
