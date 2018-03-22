package common.component;

import java.io.Serializable;

import sql.query.tbl_contacts;
import global.Global;
import global.GlobalObjects;
import global.Module;
import global.UserSession;
import main.CccvsTransfert;
import main.MainLayout;
import modules.ContactModule;
import modules.TransfertModule;
import modules.admin.AdminUsersModule;

import com.vaadin.event.ItemClickEvent;
import com.vaadin.event.ItemClickEvent.ItemClickListener;
import com.vaadin.terminal.ThemeResource;
import com.vaadin.ui.Button;
import com.vaadin.ui.HorizontalLayout;
import com.vaadin.ui.Accordion;
import com.vaadin.ui.Label;
import com.vaadin.ui.Panel;
import com.vaadin.ui.TabSheet;
import com.vaadin.ui.Window;
import com.vaadin.ui.TabSheet.SelectedTabChangeEvent;
import com.vaadin.ui.TabSheet.Tab;
import com.vaadin.ui.Table;
import com.vaadin.ui.VerticalLayout;
import com.vaadin.ui.themes.Reindeer;

/**
 * Création du composant accordéon du menu qui contient les modules ContactTable
 * et MenuFolderTable.
 * 
 * @author Dominique Roduit
 * 
 */
public class AccordionMenu extends HorizontalLayout implements
		Accordion.SelectedTabChangeListener, Serializable {
	private static final long serialVersionUID = 4763358917170872926L;
	/**
	 * Contient toutes les instances qui doivent être accessibles dans toute
	 * l'application
	 **/
	private static GlobalObjects global = CccvsTransfert.getGlobalMethod();
	/** Instance de la fenêtre principale **/
	private static Window mainWindow = CccvsTransfert.mainWindow;
	/** Layout Global de l'application **/
	private static MainLayout mainLayout;
	/** Menu accordéon **/
	private static Accordion accMenu;
	/** Instance du tableau contenant les contact du menu **/
	private static MenuContactTable menuContactTable;
	/** Instance du tableau contenant les dossiers du menu **/
	private MenuFolderTable menuFolderTable;
	/** Instance du paneau d'administration **/
	private MenuAdmin menuAdmin;
	/** Texte de l'onglet du gestionnaire de fichiers **/
	private static String CAPTION_TAB_FILE_MANAGER = "";
	/** Texte de l'onglet des contacts **/
	private static String CAPTION_TAB_CONTACTS = "";
	/** Texte de l'onglet d'administration **/
	private static String CAPTION_TAB_ADMIN = "";
	/** Stocke le caption de l'onglet sélectionné **/
	private static String selectedTab = "";

	public AccordionMenu() {
		mainLayout = global.getMainLayout();
		
		menuContactTable = new MenuContactTable();
		menuFolderTable = new MenuFolderTable();
		menuAdmin = new MenuAdmin();
		
		CAPTION_TAB_FILE_MANAGER = Global.i("CAPTION_FILE_MANAGER");
		CAPTION_TAB_CONTACTS = Global.i("CAPTION_CONTACTS");
		CAPTION_TAB_ADMIN = Global.i("CAPTION_ADMIN");

		setSpacing(true);

		// Panel qui contiendra les dossiers
		PanelLight pnlFileManager = new PanelLight();
		// Contenu du panel
		final SQLTable table = new SQLTable();
		table.setStyleName("no-resizable");
		table.setSizeFull();
		table.setColumnHeaderMode(Table.COLUMN_HEADER_MODE_HIDDEN);
		table.addContainerProperty("bt_label", Label.class, null,
				Global.i("CAPTION_FOLDER"), null, null);
		table.addListener(new ItemClickListener() {
			public void itemClick(ItemClickEvent event) {
				if (event.getButton() == ItemClickEvent.BUTTON_LEFT) {
					String[] ids = table.getValuesFromObject(table.getValue());
					if (ids.length <= 1) {
						table.setValue(null);
					} // Dé-sélectionne tout
					table.select(event.getItemId()); // Sélectionne la ligne en
														// cours
					getWindow().showNotification(table.getValue().toString());
				}
			}
		});

		pnlFileManager.addComponent(menuFolderTable);
		global.setMenuFolderTable(menuFolderTable);
		
		// Panel
		PanelLight pnlContacts = new PanelLight();
		pnlContacts.addComponent(menuContactTable);
		global.setMenuContactTable(menuContactTable);
		
		// Panel admin
		PanelLight pnlAdmin = new PanelLight();
		pnlAdmin.addComponent(menuAdmin);
		global.setMenuAdmin(menuAdmin);

		// Menu Accordion
		accMenu = new Accordion();
		accMenu.setStyleName("v-accordion-icon");
		accMenu.setSizeFull();
		// Tab 1 (FileManager)
		ThemeResource iconManager = new ThemeResource(Global.PATH_THEME_RESSOURCES + "clouddrive.png");
		accMenu.addTab(pnlFileManager, CAPTION_TAB_FILE_MANAGER, iconManager);
		// Tab 2 (contacts)
		ThemeResource iconContact = new ThemeResource(Global.PATH_THEME_RESSOURCES + "contact.png");
		accMenu.addTab(pnlContacts, CAPTION_TAB_CONTACTS, iconContact);
		// Tab 3 (admin)
		if(UserSession.isAdmin()) {
			ThemeResource iconAdmin = new ThemeResource(Global.PATH_THEME_RESSOURCES + "admin.png");
			accMenu.addTab(pnlAdmin, CAPTION_TAB_ADMIN, iconAdmin);
		}

		accMenu.addListener(this);

		// S'il n'existe encore aucun contact pour cet utilisateur
		if (tbl_contacts.getNumberOfContact() < 1) {
			accMenu.setSelectedTab(pnlContacts);
			selectedTab = CAPTION_TAB_CONTACTS;
		}

		addComponent(accMenu);
	}

	@Override
	public void selectedTabChange(SelectedTabChangeEvent event) {
		TabSheet tabsheet = event.getTabSheet();
		Tab tab = tabsheet.getTab(tabsheet.getSelectedTab());
		if (tab != null) {
			selectTab(tab.getCaption());
		}
	}

	/**
	 * Sélectionne un onglet du menu
	 * 
	 * @param caption
	 *            Le texte de l'onglet à sélectionner
	 */
	public static void selectTab(String caption) {
		Module slctModule = null;
		if (caption.equals(CAPTION_TAB_FILE_MANAGER)) {
			slctModule = new Module(TransfertModule.getBodyRibbonContent(), TransfertModule.getBodyContent());
			global.getMenuFolderTable().loadContentForSelectedItem();
			selectedTab = CAPTION_TAB_FILE_MANAGER;
		} else if (caption.equals(CAPTION_TAB_CONTACTS)) {
			if (tbl_contacts.getNumberOfContact() < 1) {
				slctModule = new Module(ContactModule.getBodyRibbonContent(), TransfertModule.getBodyContent());
			} else {
				slctModule = new Module(ContactModule.getBodyRibbonContent(), ContactModule.getBodyContent());
				menuContactTable.reload();
			}
			selectedTab = CAPTION_TAB_CONTACTS;
		} else if(caption.equals(CAPTION_TAB_ADMIN)) {
			slctModule = new Module(AdminUsersModule.getBodyRibbonContent(), AdminUsersModule.getBodyContent());
			selectedTab = CAPTION_TAB_ADMIN;
		} else {
			slctModule = new Module(TransfertModule.getBodyRibbonContent(), TransfertModule.getBodyContent());
		}
		CccvsTransfert.loadModule(slctModule);
	}

	/**
	 * Retourne l'accordéon (composant du menu)
	 * 
	 * @return Menu latéral
	 */
	public static Accordion getMenu() {
		return accMenu;
	}

	/**
	 * Retourne le caption de l'onglet sélectionné dans le menu
	 * 
	 * @return Caption de l'onglet sélectionné du menu
	 */
	public static String getSelectedTab() {
		return selectedTab;
	}

}
