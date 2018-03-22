package modules;

import global.Global;
import global.GlobalObjects;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Set;


import sql.query.tbl_contacts;
import sql.query.tbl_folders;
import toolbox.Mailer;
import toolbox.Sql;
import toolbox.Utilities;


import com.vaadin.data.Item;
import com.vaadin.data.util.BeanItemContainer;
import com.vaadin.terminal.ThemeResource;
import com.vaadin.ui.AbstractSelect.MultiSelectMode;
import com.vaadin.ui.Alignment;
import com.vaadin.ui.Button;
import com.vaadin.ui.Button.ClickEvent;
import com.vaadin.ui.CustomComponent;
import com.vaadin.ui.Embedded;
import com.vaadin.ui.HorizontalLayout;
import com.vaadin.ui.Label;
import com.vaadin.ui.Table;
import com.vaadin.ui.VerticalLayout;
import com.vaadin.ui.Window.Notification;

import main.CccvsTransfert;
import model.Contact;
import model.Folder;

/**
 * Redistribution des e-mail pour l'accès à un dossier.<br>
 * Ce module peut être utile si un des contacts dit ne pas avoir reçu le mail pour X raisons, il permet de réenvoyer le lien crypté.<br>
 * Par contre la date d'expiration est conservée et ne change pas.
 * @author Dominique Roduit
 *
 */
public class ReMailLinkModule extends CustomComponent {
	/** Stockage des objets déclarés en global **/
	private GlobalObjects global = CccvsTransfert.getGlobalMethod();
	/** Clé primaire du dossier **/
	private int PKNoFolder = 0;
	/** Layout racine du module **/
	private VerticalLayout mainLayout = new VerticalLayout();
	/** Contient la liste des contacts inclus dans le dossier **/
	private BeanItemContainer<Contact> cntContacts = new BeanItemContainer<Contact>(Contact.class);
	/** Composant table qui contient les contacts **/
	private Table tblContacts;
	/** Contient les informations sur le dossier **/
	private Folder folder;
	
	/**
	 * Affichage des composants du module (Tableau, boutons, textes, ...)
	 * @param pKNoFolder Clé primaire du dossier concerné
	 */
	public ReMailLinkModule(int pKNoFolder) {
		this.PKNoFolder = pKNoFolder;
		
		ResultSet fold = Sql.query(tbl_folders.getSelectFolderInfos(PKNoFolder));
		try {
			if(fold.first()) {
				folder = new Folder(
						PKNoFolder,
						fold.getInt("FKNoUser"),
						fold.getString("folder_name"),
						fold.getString("folder_description"),
						fold.getString("folder_creation_date"),
						fold.getString("folder_expiration"),
						fold.getBoolean("folder_archive")
				);
			}
		} catch (SQLException e) {
			e.printStackTrace();
		}
		
		mainLayout.setSizeFull();
		mainLayout.setSpacing(true);
		
		Label lblInstructions = new Label(Global.i("CAPTION_SEND_BACK_INSTRUCTIONS"));
		mainLayout.addComponent(lblInstructions);
		
		ArrayList<Contact> contactList = getContactList();
		cntContacts.addAll(contactList);
		
		tblContacts = new Table();
		tblContacts.setSizeFull();
		tblContacts.setHeight("300px");
		tblContacts.setSelectable(true);
		tblContacts.setMultiSelect(true);
		tblContacts.setMultiSelectMode(MultiSelectMode.SIMPLE);
		tblContacts.setContainerDataSource(cntContacts);
		tblContacts.setVisibleColumns(new String[]{"name","mail"});
		tblContacts.setColumnHeaders(new String[]{Global.i("CAPTION_NAME"),Global.i("CAPTION_ADRESSE_EMAIL")});
		tblContacts.addGeneratedColumn("contact_internal",  new Table.ColumnGenerator() {
			public Object generateCell(Table source, Object itemId, Object columnId) {
				Contact contact = (Contact)itemId;
				String imgSrc = (contact.isInternal()) ? "home.png" : "icon-contact.png";
				Embedded imgContact = new Embedded(null, new ThemeResource(Global.PATH_THEME_RESSOURCES+imgSrc));
				return imgContact;
			}
		});
		tblContacts.setColumnHeader("contact_internal", "");
		tblContacts.setColumnWidth("contact_internal", 24);
		tblContacts.setColumnAlignment("contact_internal", Table.ALIGN_CENTER);
		tblContacts.setVisibleColumns(new String[]{"contact_internal", "name","mail"});
		mainLayout.addComponent(tblContacts);
		
		
		HorizontalLayout buttons = new HorizontalLayout();
		buttons.setSpacing(true);
		mainLayout.addComponent(buttons);
		
		Button btAnnuler = new Button(Global.i("CAPTION_CANCEL"), new Button.ClickListener() {
			public void buttonClick(ClickEvent event) {
				closeWindow();
			}
		});

		Button btSend = new Button(Global.i("CAPTION_SEND"), new Button.ClickListener() {
			public void buttonClick(ClickEvent event) {
				String contacts = "<ul style=\"font-size:9pt\">";
				Set<?> value = (Set<?>) tblContacts.getValue();
                if (null == value || value.size() == 0) {
                	getWindow().showNotification(null, Global.i("CAPTION_MUST_SELECT_CONTACT"), Notification.TYPE_WARNING_MESSAGE);
                } else {
                	// On parcours les contacts sélectionnés
                	for(Object val : value) {
                		Contact c = (Contact)val;
                		System.out.println(c.getPK()+" - "+c.getName()+" - "+c.getMail());
                		System.out.println(PKNoFolder+" - "+folder.getExpiration_date()+" - "+folder.getDescription());
                		
                		// Envoi du mail aux contacts sélectionnés
                		int expiration = tbl_folders.getRemainingDaysFolderIsAvailable(PKNoFolder);
                		Mailer.sendDownloadLink(c.getMail(), PKNoFolder, c.getPK(), folder.getExpiration_date(), expiration, folder.getDescription(), folder.getName());
                		
                		contacts += "<li>"+c.getName()+"</li>"; 
					}
                	contacts += "</ul>";
                	
                	// Affichage de la notification
            		CccvsTransfert.mainWindow.showNotification(Global.i("CAPTION_LINK_REDISTRIBUTED"), Global.i("CAPTION_MAIL_SEND_BACK_TO").replace("%contacts%", contacts), Notification.TYPE_TRAY_NOTIFICATION, true);
            		
            		// Fermeture de la fenêtre
            		closeWindow();
                }
			}
		});
		
		buttons.addComponent(btAnnuler);
		buttons.setComponentAlignment(btAnnuler, Alignment.MIDDLE_RIGHT);
		
		buttons.addComponent(btSend);
		buttons.setComponentAlignment(btSend, Alignment.MIDDLE_RIGHT);
		
		mainLayout.setComponentAlignment(buttons, Alignment.MIDDLE_RIGHT);
	
		setCompositionRoot(mainLayout);
	}
	/**
	 * Fermeture de la fenêtre flottante
	 */
	private void closeWindow() {
		(global.getWinReMailLink().getParent()).removeWindow(global.getWinReMailLink());
	}
	/**
	 * Récupération de la liste des contacts invités à consulter le dossier
	 * @return Liste des contacts inclus dans le dossier
	 */
	private ArrayList<Contact> getContactList() {
		ResultSet contact = Sql.query(tbl_contacts.getSelectContactsFromFolder(PKNoFolder));
		ArrayList<Contact> contactList = new ArrayList<Contact>();
		
		try {
			while(contact.next()) {
				contactList.add(
					new Contact(
						contact.getInt("PKNoContact"),
						contact.getInt("FKNoUser"),
						contact.getString("contact_name"),
						contact.getString("contact_mail"),
						contact.getString("contact_creation_date"),
						contact.getBoolean("contact_internal")
					)
				);
			}
		} catch (SQLException e) {
			e.printStackTrace();
		}
		return contactList;
	}
	
}
