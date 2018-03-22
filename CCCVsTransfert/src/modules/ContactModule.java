package modules;

import main.CccvsTransfert;
import main.MainLayout;
import sql.query.tbl_contacts;
import toolbox.Utilities;

import com.vaadin.terminal.ThemeResource;
import com.vaadin.ui.AbsoluteLayout;
import com.vaadin.ui.Alignment;
import com.vaadin.ui.Button;
import com.vaadin.ui.HorizontalLayout;
import com.vaadin.ui.Window;
import com.vaadin.ui.Button.ClickEvent;
import common.component.ContactTable;

import form.ContactForm;
import global.Global;
import global.GlobalObjects;

/**
 * Page de gestion des contacts.<br>L'utilisateur peut ajouter/éditer/supprimer des contacts.
 * Les actions effectuées sur cette page sont répercutées sur le tableau du menu affichant le même résultat
 * @author Dominique Roduit
 *
 */
public class ContactModule {
	/** Contient toutes les instances qui doivent être accessibles dans toute l'application **/
	private static GlobalObjects global = CccvsTransfert.getGlobalMethod();
	/** Instance de la fenêtre principale **/
	private static Window mainWindow = CccvsTransfert.mainWindow;
	/** Layout Global de l'application **/
	private static MainLayout mainLayout = global.getMainLayout();
	/** Bouton d'ajout d'un contact **/
	private static Button btAddContact;
	/** Table contenant la liste des contacts **/
	private static ContactTable tableContact;
	/** Contient la fenêtre d'ajout/édition d'un contact **/
	private static Window winContact;
	
	/**
	 * Chargement des boutons dans le ruban du corps de page
	 */
	public static HorizontalLayout getBodyRibbonContent() {
		System.out.println(Utilities.getCurrentDate()+" -- [Chargement du module] Ruban");
		
		HorizontalLayout hRibbon = new HorizontalLayout();
		hRibbon.setSizeFull();
		
		btAddContact = new Button(Global.i("CAPTION_CONTACT_ADD"));
		btAddContact.setIcon(new ThemeResource(Global.PATH_THEME_RESSOURCES+"add-contact.png"));
	    btAddContact.focus();
		global.setBtAddContact(btAddContact);
		
		btAddContact.addListener(new Button.ClickListener() {
			@Override
			public void buttonClick(ClickEvent event) {
				System.out.println(winContact);
				
				Window winContact = new Window(Global.i("CAPTION_CONTACT_ADD"));
				winContact.setModal(true);
				winContact.setWidth("440px");
				winContact.setHeight("238px");
				winContact.setResizable(false);
				CccvsTransfert.mainWindow.addWindow(winContact);
				global.setWindowContact(winContact);
				
				ContactForm cf;
				// Si le bouton ne possède aucun style
				if(btAddContact.getStyleName().equals("")) { // On ajoute un nouveau contact
					cf = new ContactForm();
				} else { // Sinon, on met à jour le contact sélectionné
					cf =  new ContactForm(btAddContact.getStyleName().replace("[", "").replace("]", ""));
					btAddContact.removeStyleName(btAddContact.getStyleName());
				}
				winContact.addComponent(cf);
			}
		});
		hRibbon.addComponent(btAddContact);
		hRibbon.setComponentAlignment(btAddContact, Alignment.MIDDLE_LEFT);
		
		int nbContact = tbl_contacts.getNumberOfContact();
		if(nbContact<1) {
			AbsoluteLayout absFinger = new AbsoluteLayout();
			absFinger.setWidth("46px");
			absFinger.setHeight("32px");
			absFinger.setStyleName("finger");
			hRibbon.addComponent(absFinger);
			hRibbon.setComponentAlignment(absFinger, Alignment.MIDDLE_LEFT);
		}
	    
	    return hRibbon;
	}
	/**
	 * Insertion du contenu dans le corps de la page.<br>
	 * Le contenu comprend un tableau répertoriant les contact que l'utilisateur à ajouté.<br>
	 * Le tableau implémente un menu contextuel qui permet la suppression et l'édition d'un contact
	 */
	public static ContactTable getBodyContent() {
		System.out.println(Utilities.getCurrentDate()+" -- [Chargement du module] Body");
		
	    tableContact = new ContactTable();
	    global.setTableContact(tableContact);
	    return tableContact;
	}
}
