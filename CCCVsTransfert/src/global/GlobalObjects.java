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
 * Class stockant tous les objets utilis�s souvent, qui doivent �tre accessible n'importe o� dans l'application.<br>
 * Attention cependant de ne pas utiliser un objet avant qu'il n'ait �t� cr�� et enregistr� dans le champ qui lui est appropri�.
 * � la diff�rence de la class Global, les m�thodes ne sont pas statiques, la classe doit �tre instanci�e pour que ses m�thodes soient utilisables.
 * @author Dominique Roduit
 *
 */
public class GlobalObjects implements Serializable {	
	/**
	 * 
	 */
	private static final long serialVersionUID = 3403587256380538282L;
	//--------- MAIN 
	/** Fen�tre principale �galement atteignable avec {@code CccvsTransfert.mainWindow} **/
	private Window mainWindow = null;
	/** Layout principal de l'application **/
	private MainLayout mainLayout = null;
	//--------- CONTACT
	/** Fen�tre pour l'ajout/edition de contact **/
	private Window windowContact = null;
	/** Tableau contenant les contacts d'un utilisateur **/
	private ContactTable tableContact = null;
	/** Bouton d'ajout des contacts **/
	private Button btAddContact = null;
	/** Table des contacts affich�e dans le menu **/
	private MenuContactTable menuContactTable = null;
	//--------- FOLDER
	/** Table des dossiers affich�e dans le menu **/
	private MenuFolderTable menuFolderTable = null;
	/** Bouton d'ajout d'un dossier **/
	private Button btAddFolder = null;
	/** Fen�tre contenant les wizard de cr�ation d'un dossier **/
	private Window windowFolder = null;
	/** Table contenant les fichiers upload�s (affect�e dans {@link WizFoldUpload} **/
	private Table uploadTable = null;
	/** Module d'upload **/
	private CustomUpload uploadModule = null;
	/** Layout qui contient le module d'upload dans la fen�tre de cr�ation d'un dossier **/
	private VerticalLayout uploadModuleLayout = null;
	/** Fen�tre pour la redistribution du lien d'acc�s au dossier **/
	private Window winReMailLink = null;
	/** Fen�tre pour la r�cup�ration du lien de t�l�chargement **/
	private Window winGetLink = null;
	//--------- ADMIN
	private MenuAdmin menuAdmin = null;
	
	/**
	 * Obtention du tableau des fichiers upload�s.
	 * @return Tableau des fichiers upload�s.
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
	 * Obtention de la fen�tre de cr�ation d'un dossier
	 * @return Fen�tre de cr�ation d'un dossier
	 */
	public Window getWindowFolder() {
		return windowFolder;
	}
	/**
	 * Stockage de la fen�tre de cr�ation d'un dossier
	 * @param windowFolder Fen�tre de cr�ation d'un dossier
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
	 * Obtention du tableau des dossiers int�gr� au menu
	 * @return Tableau des dossiers du menu
	 */
	public MenuFolderTable getMenuFolderTable() {
		return menuFolderTable;
	}
	/**
	 * Stockage en global du tableau des dossiers int�gr� au menu
	 * @param menuFolderTable Tableau des dossiers du menu
	 */
	public void setMenuFolderTable(MenuFolderTable menuFolderTable) {
		this.menuFolderTable = menuFolderTable;
	}
	/**
	 * Obtention du tableau des contacts int�gr� au menu {@link MenuContactTable}
	 * @return Tableau des contacts dumenu
	 */
	public MenuContactTable getMenuContactTable() {
		return menuContactTable;
	}
	/**
	 * Stockage en global du tableau des contacts int�gr� au menu. {@link MenuContactTable}
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
	 * Obtention de la fen�tre contenant le formulaire d'ajout/�dition de contact. {@link ContactModule}
	 * @return Fen�tre d'ajout/�dition de contact
	 */
	public Window getWindowContact() {
		return windowContact;
	}
	/**
	 * Stockage en global de la fen�tre d'ajout/�dition de contact
	 * @param windowContact Fen�tre d'ajout/�dition de contact
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
	 * Stockage en global du layout principal de l'application. D�finit dans {@link CccvsTransfert}
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
	 * @return Fen�tre de reditribution du lien d'acc�s au dossier
	 */
	public Window getWinReMailLink() {
		return winReMailLink;
	}
	/**
	 * @param winReMailLink Fen�tre de reditribution du lien d'acc�s au dossier
	 */
	public void setWinReMailLink(Window winReMailLink) {
		this.winReMailLink = winReMailLink;
	}
	/**
	 * @return Fen�tre pour la r�cup�ration du lien de t�l�chargement
	 */
	public Window getWinGetLink() {
		return winGetLink;
	}
	/**
	 * @param winGetLink Fen�tre pour la r�cup�ration du lien de t�l�chargement
	 */
	public void setWinGetLink(Window winGetLink) {
		this.winGetLink = winGetLink;
	}
	/**
	 * Ouvre la fen�tre de r�cup�ration du lien de t�l�chargement
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
