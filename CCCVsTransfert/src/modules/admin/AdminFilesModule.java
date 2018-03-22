package modules.admin;

import java.io.File;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import main.CccvsTransfert;
import main.MainLayout;
import model.Files;
import model.User;
import sql.query.tbl_users;
import toolbox.Sql;
import toolbox.Utilities;

import com.vaadin.data.Item;
import com.vaadin.data.Property;
import com.vaadin.data.util.BeanItemContainer;
import com.vaadin.event.Action;
import com.vaadin.event.ItemClickEvent;
import com.vaadin.event.ItemClickEvent.ItemClickListener;
import com.vaadin.terminal.ExternalResource;
import com.vaadin.terminal.ThemeResource;
import com.vaadin.ui.Alignment;
import com.vaadin.ui.Button;
import com.vaadin.ui.Embedded;
import com.vaadin.ui.HorizontalLayout;
import com.vaadin.ui.Label;
import com.vaadin.ui.Table;
import com.vaadin.ui.VerticalLayout;
import com.vaadin.ui.Window;
import com.vaadin.ui.Button.ClickEvent;
import com.vaadin.ui.Window.Notification;
import com.vaadin.ui.themes.Runo;

import common.component.AccordionMenu;
import common.component.ContactTable;
import common.component.PanelLight;
import common.component.upload.FileInfo;

import form.WizFoldNew;
import form.WizFoldUpload;
import global.Global;
import global.GlobalObjects;
import global.Module;

/**
 * Module disponible uniquement par les administrateurs
 * Il permet la visualisation et la gestion des utilisateurs de l'interface.
 * @author Dominique Roduit
 *
 */
public class AdminFilesModule {
	/** Contient toutes les instances qui doivent être accessibles dans toute l'application **/
	private static GlobalObjects global = CccvsTransfert.getGlobalMethod();
	/** Instance de la fenêtre principale **/
	private static Window mainWindow = CccvsTransfert.mainWindow;
	/** Layout Global de l'application **/
	private static MainLayout mainLayout = global.getMainLayout();
	/** Table contenant la liste des contacts **/
	private static Table tblUsers;
	/** Container contenant la liste des fichiers du dossier **/
	private static BeanItemContainer<FileInfo> container = new BeanItemContainer<FileInfo>(FileInfo.class);
	/** Action Supprimer **/
	private static Action ACTION_DELETE; 
	/** Action "Télécharger" du menu contextuel **/
	private static Action ACTION_DOWNLOAD;
	/** Action pour la récupération du lien de téléchargement **/
	protected static Action ACTION_GET_LINK;
	

	/**
	 * Chargement du bouton de retour et du titre dans le ruban du corps de la page
	 */
	public static HorizontalLayout getBodyRibbonContent() {
		ACTION_DELETE = new Action(Global.i("CAPTION_DELETE"),new ThemeResource(Global.PATH_THEME_RESSOURCES+"false.png"));
		ACTION_DOWNLOAD = new Action(Global.i("CAPTION_DOWNLOAD"), new ThemeResource(Global.PATH_THEME_RESSOURCES+"down.png"));
		ACTION_GET_LINK = new Action(Global.i("CAPTION_GET_DOWNLOAD_LINK"), new ThemeResource(Global.PATH_THEME_RESSOURCES+"link.png"));
		
		System.out.println(Utilities.getCurrentDate()+" -- [Chargement du module] Ruban");
	
		HorizontalLayout vlRibbon = new HorizontalLayout();
		vlRibbon.setSizeFull();
		vlRibbon.setSpacing(true);
		
		Label lblTitle = new Label("Fichiers stockés sur le disque (BETA)");
		lblTitle.setStyleName(Runo.LABEL_H1);
		vlRibbon.addComponent(lblTitle);
		vlRibbon.setComponentAlignment(lblTitle, Alignment.MIDDLE_LEFT);

	    return vlRibbon;
	} 
	/**
	 * Insertion du contenu dans le corps de la page.<br>Le contenu comprend le texte, extrait de la base de données ainsi qu'une image illustrative.
	 */
	public static Table getBodyContent() {
		System.out.println(Utilities.getCurrentDate()+" -- [Chargement du module] Body");
		
		tblUsers = new Table();
		tblUsers.setSizeFull();
		tblUsers.setSelectable(true);
		
		container.removeAllItems();
		container.addAll(getUserList());
		
		tblUsers.setContainerDataSource(container);
		tblUsers.addGeneratedColumn("icon", new Table.ColumnGenerator() {
			public Object generateCell(Table source, Object itemId, Object columnId) {
				FileInfo file = (FileInfo)itemId;
				return Utilities.getImgFromExtension(Utilities.getExtension(file.getName()));
			}
		});
		tblUsers.setColumnWidth("icon", 28);
		tblUsers.setColumnAlignment("icon", Table.ALIGN_CENTER);
		tblUsers.setColumnCollapsible("icon", false);
		

		tblUsers.addGeneratedColumn("filesize", new Table.ColumnGenerator() {
			public Object generateCell(Table source, Object itemId, Object columnId) {
				FileInfo file = (FileInfo)itemId;
				return Utilities.formatSize(file.getSize());
			}
		});
		
		tblUsers.setVisibleColumns(new String[]{"icon", "name", "filesize"});
		tblUsers.setColumnHeaders(new String[]{"",Global.i("CAPTION_FILENAME"), Global.i("CAPTION_SIZE")});
		tblUsers.addActionHandler(new Action.Handler() {
			public void handleAction(Action action, Object sender, Object target) {
				if(action==ACTION_DOWNLOAD) {
					FileInfo file = (FileInfo)target;
					File fileToDownload = new File(Global.UPLOAD_DIR+file.getName());
					
					if(fileToDownload.exists()) {
						CccvsTransfert.mainWindow.open(new ExternalResource(Utilities.getURLForFile(file.getName())),"_self");
					} else {
						CccvsTransfert.mainWindow.showNotification(Global.i("CAPTION_INEXISTING_FILE"), Notification.TYPE_WARNING_MESSAGE);
					}
				}
				
				if(action==ACTION_GET_LINK) {
					FileInfo file = (FileInfo)target;
					global.openWindowGetLink(file.getName());
				}
				
				if(action==ACTION_DELETE) {
					tblUsers.removeItem(target);
					FileInfo file = (FileInfo)target;
					File fileToDel = new File(Global.UPLOAD_DIR+file.getName());
					fileToDel.delete();
				}
			}
			public Action[] getActions(Object target, Object sender) {
				if(target!=null) {
					return new Action[] { ACTION_DOWNLOAD, ACTION_GET_LINK, ACTION_DELETE };
				} else {
					return null;
				}
			}
		});
		tblUsers.addListener(new ItemClickListener() {
			public void itemClick(ItemClickEvent event) {
				if(event.getButton()==ItemClickEvent.BUTTON_RIGHT) {
					tblUsers.setValue(null);   // Dé-sélectionne tout
					tblUsers.select(event.getItemId()); // Sélectionne la ligne en cours
				}
			}
		});
		
		return tblUsers;
	}
	/**
	 * Récupération de la liste des utilisateurs
	 * @return Liste des utilisateurs
	 */
	private static ArrayList<FileInfo> getUserList() {
		ArrayList<FileInfo> userList = new ArrayList<FileInfo>();
		File repertoire = new File(Global.UPLOAD_DIR);
		
		int i;
		String[] listefichiers = repertoire.list();
		for(i=0; i<listefichiers.length; i++) {
			File file = new File(Global.UPLOAD_DIR+listefichiers[i]);
			userList.add(
				new FileInfo(i, listefichiers[i], file.length(), Long.toString(file.lastModified()))	
			);
		}
		
		return userList;
	}
	
	
}