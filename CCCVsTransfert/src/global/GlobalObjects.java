package global;

import java.io.Serializable;

import main.CccvsTransfert;
import main.MainLayout;
import modules.ContactModule;
import modules.GetLinkModule;

import com.vaadin.terminal.ThemeResource;
import com.vaadin.ui.Button;
import com.vaadin.ui.Table;
import com.vaadin.ui.VerticalLayout;
import com.vaadin.ui.Window;
import common.component.ContactTable;
import common.component.MenuAdmin;
import common.component.MenuContactTable;
import common.component.MenuFolderTable;
import common.component.upload.CustomUpload;
import form.WizFoldUpload;

/**
 * Class stockant tous les objets utilisés souvent, qui doivent être accessible n'importe où dans l'application.<br>
 * Attention cependant de ne pas utiliser un objet avant qu'il n'ait été créé et enregistré dans le champ qui lui est approprié.
 * À la différence de la class Global, les méthodes ne sont pas statiques, la classe doit être instanciée pour que ses méthodes soient utilisables.
 * @author Dominique Roduit
 *
 */
public class GlobalObjects implements Serializable {	
	/**
	 * 
	 */
	private static final long serialVersionUID = 3403587256380538282L;
	//--------- MAIN 
	/** Fenêtre principale également atteignable avec {@code CccvsTransfert.mainWindow} **/
	private Window mainWindow = null;
	/** Layout principal de l'application **/
	private MainLayout mainLayout = null;
	//--------- CONTACT
	/** Fenêtre pour l'ajout/edition de contact **/
	private Window windowContact = null;
	/** Tableau contenant les contacts d'un utilisateur **/
	private ContactTable tableContact = null;
	/** Bouton d'ajout des contacts **/
	private Button btAddContact = null;
	/** Table des contacts affichée dans le menu **/
	private MenuContactTable menuContactTable = null;
	//--------- FOLDER
	/** Table des dossiers affichée dans le menu **/
	private MenuFolderTable menuFolderTable = null;
	/** Bouton d'ajout d'un dossier **/
	private Button btAddFolder = null;
	/** Fenêtre contenant les wizard de création d'un dossier **/
	private Window windowFolder = null;
	/** Table contenant les fichiers uploadés (affectée dans {@link WizFoldUpload} **/
	private Table uploadTable = null;
	/** Module d'upload **/
	private CustomUpload uploadModule = null;
	/** Layout qui contient le module d'upload dans la fenêtre de création d'un dossier **/
	private VerticalLayout uploadModuleLayout = null;
	/** Fenêtre pour la redistribution du lien d'accès au dossier **/
	private Window winReMailLink = null;
	/** Fenêtre pour la récupération du lien de téléchargement **/
	private Window winGetLink = null;
	//--------- ADMIN
	private MenuAdmin menuAdmin = null;
	
	/**
	 * Obtention du tableau des fichiers uploadés.
	 * @return Tableau des fichiers uploadés.
	 */
	public Table getUploadTable() {
		return uploadTable;
	}
	/**
	 * Stockage de la table d'upload en global
	 * @param uploadTable Table d'upload
	 */
	public void setUploadTable(Table uploadTable) {
		this.uploadTable = uploadTable;
	}
	/**
	 * Obtention de la fenêtre de création d'un dossier
	 * @return Fenêtre de création d'un dossier
	 */
	public Window getWindowFolder() {
		return windowFolder;
	}
	/**
	 * Stockage de la fenêtre de création d'un dossier
	 * @param windowFolder Fenêtre de création d'un dossier
	 */
	public void setWindowFolder(Window windowFolder) {
		this.windowFolder = windowFolder;
	}
	/**
	 * Obtention du bouton "Nouveau dossier" dossier
	 * @return Bouton "Nouveau dossier"
	 */
	public Button getBtAddFolder() {
		return btAddFolder;
	}
	/**
	 * Stockage en global du bouton "Nouveau dossier".
	 * @param btAddFolder Bouton "Nouveau dossier"
	 */
	public void setBtAddFolder(Button btAddFolder) {
		this.btAddFolder = btAddFolder;
	}
	/**
	 * Obtention du tableau des dossiers intégré au menu
	 * @return Tableau des dossiers du menu
	 */
	public MenuFolderTable getMenuFolderTable() {
		return menuFolderTable;
	}
	/**
	 * Stockage en global du tableau des dossiers intégré au menu
	 * @param menuFolderTable Tableau des dossiers du menu
	 */
	public void setMenuFolderTable(MenuFolderTable menuFolderTable) {
		this.menuFolderTable = menuFolderTable;
	}
	/**
	 * Obtention du tableau des contacts intégré au menu {@link MenuContactTable}
	 * @return Tableau des contacts dumenu
	 */
	public MenuContactTable getMenuContactTable() {
		return menuContactTable;
	}
	/**
	 * Stockage en global du tableau des contacts intégré au menu. {@link MenuContactTable}
	 * @param menuContactTable Tableau des contacts du menu
	 */
	public void setMenuContactTable(MenuContactTable menuContactTable) {
		this.menuContactTable = menuContactTable;
	}
	/**
	 * Obtention du bouton "Ajouter un contact" {@link ContactModule}
	 * @return Bouton "Ajouter un contact"
	 */
	public Button getBtAddContact() {
		return btAddContact;
	}
	/**
	 * Stockage en global du bouton "Ajouter un contact". {@link ContactModule}
	 * @param btAddContact Bouton "Ajouter un contact"
	 */
	public void setBtAddContact(Button btAddContact) {
		this.btAddContact = btAddContact;
	}
	/**
	 * Obtention du tableau des contacts. {@link ContactTable}
	 * @return Tableau des contacts
	 */
	public ContactTable getTableContact() {
		return tableContact;
	}
	/**
	 * Stockage en global du tableau des contacts. {@link ContactTable}
	 * @param tableContact Tableau des contacts
	 */
	public void setTableContact(ContactTable tableContact) {
		this.tableContact = tableContact;
	}
	/**
	 * Obtention de la fenêtre contenant le formulaire d'ajout/édition de contact. {@link ContactModule}
	 * @return Fenêtre d'ajout/édition de contact
	 */
	public Window getWindowContact() {
		return windowContact;
	}
	/**
	 * Stockage en global de la fenêtre d'ajout/édition de contact
	 * @param windowContact Fenêtre d'ajout/édition de contact
	 */
	public void setWindowContact(Window windowContact) {
		this.windowContact = windowContact;
	}
	/**
	 * Obtention du layout principal de l'application. {@link MainLayout}
	 * @return Layout principal de l'application
	 */
	public MainLayout getMainLayout() {
		return mainLayout;
	}
	/**
	 * Stockage en global du layout principal de l'application. Définit dans {@link CccvsTransfert}
	 * @param mainLayout Layout principal de l'application
	 */
	public void setMainLayout(MainLayout mainLayout) {
		this.mainLayout = mainLayout;
	}
	/**
	 * @return the uploadModule
	 */
	public CustomUpload getUploadModule() {
		return uploadModule;
	}
	/**
	 * @param uploadModule the uploadModule to set
	 */
	public void setUploadModule(CustomUpload uploadModule) {
		this.uploadModule = uploadModule;
	}
	/**
	 * @return the uploadModuleLayout
	 */
	public VerticalLayout getUploadModuleLayout() {
		return uploadModuleLayout;
	}
	/**
	 * @param uploadModuleLayout the uploadModuleLayout to set
	 */
	public void setUploadModuleLayout(VerticalLayout uploadModuleLayout) {
		this.uploadModuleLayout = uploadModuleLayout;
	}
	/**
	 * @return Fenêtre de reditribution du lien d'accès au dossier
	 */
	public Window getWinReMailLink() {
		return winReMailLink;
	}
	/**
	 * @param winReMailLink Fenêtre de reditribution du lien d'accès au dossier
	 */
	public void setWinReMailLink(Window winReMailLink) {
		this.winReMailLink = winReMailLink;
	}
	/**
	 * @return Fenêtre pour la récupération du lien de téléchargement
	 */
	public Window getWinGetLink() {
		return winGetLink;
	}
	/**
	 * @param winGetLink Fenêtre pour la récupération du lien de téléchargement
	 */
	public void setWinGetLink(Window winGetLink) {
		this.winGetLink = winGetLink;
	}
	/**
	 * Ouvre la fenêtre de récupération du lien de téléchargement
	 * @param filename Nom du fichier pour lequel on veux obtenir le lien
	 */
	public void openWindowGetLink(String filename) {
		Window winGetLink = new Window(Global.i("TITLE_WINDOW_GET_LINK"));
		winGetLink.setIcon(new ThemeResource(Global.PATH_THEME_RESSOURCES+"link.png"));
		winGetLink.setResizable(false);
		winGetLink.setModal(true);
		winGetLink.addComponent(new GetLinkModule(filename));
		winGetLink.setWidth("700px");
		winGetLink.setHeight("176px");
		CccvsTransfert.getGlobalMethod().setWinGetLink(winGetLink);
		CccvsTransfert.mainWindow.addWindow(winGetLink);
	}
	/**
	 * @return Le menu d'administration
	 */
	public MenuAdmin getMenuAdmin() {
		return menuAdmin;
	}
	/**
	 * @param menuAdmin Menu d'administration
	 */
	public void setMenuAdmin(MenuAdmin menuAdmin) {
		this.menuAdmin = menuAdmin;
	}
}
