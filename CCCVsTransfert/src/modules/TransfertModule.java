package modules;

import main.CccvsTransfert;
import main.MainLayout;
import sql.query.tbl_contacts;
import sql.query.tbl_folders;
import toolbox.Utilities;

import com.vaadin.terminal.ThemeResource;
import com.vaadin.ui.Alignment;
import com.vaadin.ui.Button;
import com.vaadin.ui.Embedded;
import com.vaadin.ui.Label;
import com.vaadin.ui.VerticalLayout;
import com.vaadin.ui.Window;
import com.vaadin.ui.Button.ClickEvent;
import com.vaadin.ui.Window.Notification;
import com.vaadin.ui.themes.Runo;

import common.component.AccordionMenu;
import common.component.ContactTable;
import common.component.PanelLight;

import form.WizFoldNew;
import form.WizFoldUpload;
import global.Global;
import global.GlobalObjects;
import global.Module;

/**
 * Page de gestion des transferts.<br>
 * Elle affiche un bouton dans le ruban du menu pour permettre la création d'un nouveau dossier.<br>
 * La fenêtre d'assistance à la navigation est implémentée dans cette class.<br>
 * Lorsqu'un dossier est affiché, c'est la class {@link FolderModule} qui est implémentée.
 * @author Dominique Roduit
 *
 */
public class TransfertModule {
	/** Contient toutes les instances qui doivent être accessibles dans toute l'application **/
	private static GlobalObjects global = CccvsTransfert.getGlobalMethod();
	/** Instance de la fenêtre principale **/
	private static Window mainWindow = CccvsTransfert.mainWindow;
	/** Layout Global de l'application **/
	private static MainLayout mainLayout = global.getMainLayout();
	/** Bouton d'ajout d'un contact **/
	private static Button btAddFolder;
	/** Table contenant la liste des contacts **/
	private static ContactTable tblFolder;
	/** Contient une instance de la fenêtre pour la création d'un dossier **/
	private static Window winFolder;
	

	/**
	 * Chargement des boutons dans le ruban du wrapper principal.<br><b>Bouton</b> "Nouveau dossier"
	 */
	public static Button getBodyRibbonContent() {
		System.out.println(Utilities.getCurrentDate()+" -- [Chargement du module] Ruban");
	
		btAddFolder = new Button(Global.i("CAPTION_NEW_FOLDER"));
		btAddFolder.setIcon(new ThemeResource(Global.IMG_FOLDER_ADD));
	    btAddFolder.focus();
	    
	    global.setBtAddFolder(btAddFolder);
	    
	    btAddFolder.addListener(new Button.ClickListener() {
			@Override
			public void buttonClick(ClickEvent event) {
				if(tbl_contacts.getNumberOfContact()<1) {
					CccvsTransfert.mainWindow.showNotification(Global.i("CAPTION_ERROR"), Global.i("CAPTION_ERROR_NO_CONTACT"), Notification.TYPE_WARNING_MESSAGE);
					CccvsTransfert.loadModule(new Module(ContactModule.getBodyRibbonContent(), ContactModule.getBodyContent()));
					AccordionMenu.getMenu().setSelectedTab(1);
				} else {
					winFolder = new Window(Global.i("CAPTION_TRANSFERT")+" - "+Global.i("TITLE_WINDOW_CREATE_FOLDER"));
					winFolder.setIcon(new ThemeResource(Global.PATH_THEME_RESSOURCES+"folder-horizontal.png"));
					winFolder.setModal(true);
					winFolder.setWidth("440px");
					winFolder.setHeight("255px");
					winFolder.setResizable(false);
					winFolder.setStyleName(Runo.WINDOW_DIALOG);
					CccvsTransfert.mainWindow.addWindow(winFolder);
					global.setWindowFolder(winFolder);
					
					// Chargement du premier module du wizard
					WizFoldNew wzrTrans;
					wzrTrans = new WizFoldNew();
					winFolder.addComponent(wzrTrans);
				}
			}
		});

	    return btAddFolder;
	} 
	/**
	 * Insertion du contenu de la page dans le corps de l'application.<br><br>
	 * <b>Contenu :</b><br><ul><li>Assistance à la première utilisation</li><li>Page de démarrage</li></ul>
	 */
	public static PanelLight getBodyContent() {
		System.out.println(Utilities.getCurrentDate()+" -- [Chargement du module] Body");
	    
		PanelLight pnlBody = new PanelLight();
		pnlBody.setScrollable(true);
	//	pnlBody.getLayout().setSizeFull();
		
		VerticalLayout body = new VerticalLayout();
		body.setSizeFull();
		pnlBody.addComponent(body);
	
		Label lblHomeName = new Label(Global.getParm("FACTORY_NAME"));
		lblHomeName.addStyleName("schema-illustration");
		body.addComponent(lblHomeName);
		body.setComponentAlignment(lblHomeName, Alignment.TOP_CENTER);
		
		Embedded imgIllustr = new Embedded(null, new ThemeResource(Global.PATH_THEME_RESSOURCES+"home_illustration.png"));
		imgIllustr.setStandby(Global.getParm("APP_NAME"));
		imgIllustr.setType(Embedded.TYPE_IMAGE);
		body.addComponent(imgIllustr);
		body.setComponentAlignment(imgIllustr, Alignment.TOP_CENTER);
		
		String content = "<br>";
		String imgTrue = " <img src=\""+Global.PATH_THEME_RESSOURCES_HTML+"true.png\" />";
		String imgFalse = " <img src=\""+Global.PATH_THEME_RESSOURCES_HTML+"false.png\" />";
		
		// S'il n'existe pas encore de dossier
		int nbFolder = tbl_folders.getNumberOfFolders();
		int nbContact = tbl_contacts.getNumberOfContact();
		
		if(nbFolder<1 || nbContact<1) {
			content += Global.i("CONTENT_HOME_TITLE");
			content += Global.i("CONTENT_HOME_OBJ1");
			content += (nbContact<1) ? imgFalse : imgTrue;
			content += Global.i("CONTENT_HOME_OBJ2");
			content += (nbFolder<1) ? imgFalse : imgTrue;
		} else {
			content += Global.i("CONTENT_HOME_TITLE");
			content += Global.i("CONTENT_HOME_OBJ3");
			content += Global.i("CONTENT_HOME_OBJ2");
		}
		
		Label lblHome = new Label(content, Label.CONTENT_XHTML);
		lblHome.addStyleName("align-center");
		body.addComponent(lblHome);
		body.setComponentAlignment(lblHome, Alignment.TOP_CENTER);
		
		return pnlBody;
	}
}
