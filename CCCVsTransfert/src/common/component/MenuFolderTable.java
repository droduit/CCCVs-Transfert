package common.component;

import global.Global;
import global.GlobalObjects;
import global.Module;

import java.io.File;
import java.io.Serializable;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;


import sql.query.tbl_folders;
import toolbox.Sql;
import toolbox.Utilities;

import main.CccvsTransfert;
import model.Folder;
import modules.FolderModule;
import modules.ReMailLinkModule;
import modules.TransfertModule;

import com.vaadin.data.Item;
import com.vaadin.data.util.BeanItemContainer;
import com.vaadin.event.Action;
import com.vaadin.event.ItemClickEvent;
import com.vaadin.event.ItemClickEvent.ItemClickListener;
import com.vaadin.terminal.ExternalResource;
import com.vaadin.terminal.ThemeResource;
import com.vaadin.ui.Label;
import com.vaadin.ui.Table;
import com.vaadin.ui.Window;
import com.vaadin.ui.Window.Notification;

/**
 * Table pour la gestion des dossiers.
 * Cette class permet l'affichage des dossiers dans un tableau.
 * Le tableau implémente un menu, affiché sur le clic droit de la souris.
 * Ce menu propose la suppression des dossiers expirés et le téléchargement des dossiers toujours disponibles.
 * @author Dominique Roduit
 * @version 1.0
 *
 */
public class MenuFolderTable extends Table implements Serializable {
	private static final long serialVersionUID = -1478560432200636202L;
	/** Contient toutes les instances qui doivent être accessibles dans toute l'application **/
	protected GlobalObjects global = CccvsTransfert.getGlobalMethod();
	/** Action supprimer du menu contextuel **/
	protected static Action ACTION_DEL;
	/** Action téléchargement **/
	protected static Action ACTION_DOWNLOAD;
	/** Action pour le renvoi d'un lien **/
	protected static Action ACTION_REMAIL;
	/** Action pour la récupération du lien de téléchargement **/
	protected static Action ACTION_GET_LINK;
	/** Action pour le rechargement de la zone **/
	protected static Action ACTION_REFRESH;
	/** Liste des actions du menu contextuel affiché sur le clique droit pour ce tableau **/
	protected static Action[] ACTIONS;
	/** Contient la/les lignes  du tableau sélectionnées **/
	protected Object selectedItems = getValue();
	
	private BeanItemContainer<Folder> cntFolders = new BeanItemContainer<Folder>(Folder.class);
	
	/**
	 * Affiche un tableau contenant la liste des contacts
	 */
	public MenuFolderTable() {
		ACTION_DEL = new Action(Global.i("CAPTION_DELETE"), new ThemeResource(Global.PATH_THEME_RESSOURCES+"false.png"));
		ACTION_DOWNLOAD = new Action(Global.i("CAPTION_DOWNLOAD"), new ThemeResource(Global.PATH_THEME_RESSOURCES+"down.png"));
		ACTION_REMAIL = new Action(Global.i("CAPTION_SEND_BACK_MAIL")+"...", new ThemeResource(Global.PATH_THEME_RESSOURCES+"action-mail-send.png"));
		ACTION_GET_LINK = new Action(Global.i("CAPTION_GET_DOWNLOAD_LINK"), new ThemeResource(Global.PATH_THEME_RESSOURCES+"link.png"));
		ACTION_REFRESH = new Action(Global.i("CAPTION_RELOAD"), new ThemeResource(Global.PATH_THEME_RESSOURCES+"reload.png"));
		ACTIONS = new Action[] { ACTION_DEL, ACTION_REFRESH };
		
		setSelectable(true);
		setStyleName("no-resizable");
		setSizeFull();

	    setContainerDataSource(cntFolders);
	    reload();
	    
	    addGeneratedColumn("folder", new Table.ColumnGenerator() {
			private static final long serialVersionUID = 6431238475420220823L;

			public Object generateCell(Table source, Object itemId, Object columnId) {
				Folder folder = (Folder)itemId;
				
				Label lblDossier = new Label(folder.getName());
				lblDossier.setStyleName("cursor-pointer");
				lblDossier.addStyleName("v-menu-item-folder");
				if(folder.getArchive()==true) {
					lblDossier.addStyleName("archive");
				}
				
				return lblDossier;
			}
		});
		setColumnHeader("folder", Global.i("CAPTION_FOLDER"));
		
		setVisibleColumns(new String[]{"folder"});


		// Gestion du clique droit
		addActionHandler(new Action.Handler() {
			private static final long serialVersionUID = 4079502573436362050L;
			// Sur le clique droit, on retourne les actions désirées
			public Action[] getActions(Object target, Object sender) {
				if(target!=null) {
					Folder folder = (Folder)target;

					if(folder.getArchive()) {
						return ACTIONS;
					} else {
						return new Action[]{ACTION_DOWNLOAD, ACTION_GET_LINK, ACTION_REMAIL, ACTION_REFRESH};
					}
				} else {
					return new Action[]{ ACTION_REFRESH };
				}
			}
			// Lors de l'appui sur une action
			public void handleAction(Action action, Object sender, Object target) {
				Folder folder = (Folder)target;
				
				// Action "Supprimer"
				if(action==ACTION_DEL) {
					actionDelete(folder);
				}
				// Action "Télécharger"
				if(action==ACTION_DOWNLOAD) {
					String archiveName = Utilities.getFolderArchiveName(folder.getName(), folder.getCreation_date());

					System.out.println(archiveName);
					
					File internFile = new File(Global.UPLOAD_DIR+archiveName);
					if(internFile.exists()) {
						CccvsTransfert.mainWindow.open(new ExternalResource(Utilities.getURLForFile(archiveName)),"_self");
					} else {
						CccvsTransfert.mainWindow.showNotification(Global.i("CAPTION_INEXISTING_FILE"), Notification.TYPE_WARNING_MESSAGE);
					}
					
				}
				// Action *Renvoyer le lien aux destinataires*
				if(action==ACTION_REMAIL) {
					Window winRemail = new Window(Global.i("TITLE_WINDOW_REMAILLINK"));
					winRemail.setIcon(new ThemeResource(Global.PATH_THEME_RESSOURCES+"action-mail-send.png"));
					winRemail.setResizable(false);
					winRemail.setModal(true);
					winRemail.addComponent(new ReMailLinkModule(folder.getPKNoFolder()));
					winRemail.setWidth("700px");
					winRemail.setHeight("450px");
					global.setWinReMailLink(winRemail);
					CccvsTransfert.mainWindow.addWindow(winRemail);
				}
				// Action "Récupérer le lien"
				if(action==ACTION_GET_LINK) {
					String archiveName = Utilities.getFolderArchiveName(folder.getName(), folder.getCreation_date());
					
					global.openWindowGetLink(archiveName);
				}
				
				if(action==ACTION_REFRESH) {
					System.out.println("Rechargement du tableau du menu contenant les dossiers");
					reload();
				}
			}
		});
		
		// Pour sélectionner la ligne par clique droit
		addListener(new ItemClickListener() {
			private static final long serialVersionUID = -3399336511865348332L;

			public void itemClick(ItemClickEvent event) {
				if(event.getButton()==ItemClickEvent.BUTTON_RIGHT) {
					setValue(null);  // Dé-sélectionne tout
					select(event.getItemId()); // Sélectionne la ligne en cours
				}
				Item item = event.getItem();
				
				if(item!=null) {
					selectedItems = Integer.parseInt(item.getItemProperty("PKNoFolder").toString());
					CccvsTransfert.loadModule(new Module(TransfertModule.getBodyRibbonContent(), FolderModule.getBodyContent(Integer.parseInt(item.getItemProperty("PKNoFolder").toString()))));
				}
			}
		});
	}
	/**
	 * Sélectionn des données qui vont remplir notre tableau
	 * @return (Object[]) Tableau d'objet contenant les lignes du tableau
	 */
	public ArrayList<Folder> getFolders() {
    	ArrayList<Folder> folders = new ArrayList<Folder>();
    	
		ResultSet data = Sql.query(tbl_folders.getAllFolders());
		System.out.println(tbl_folders.getAllFolders());
	     
	     try {
			while(data.next()) {
				
				folders.add(
					new Folder(
						data.getInt("PKNoFolder"),
						data.getInt("FKNoUser"),
						data.getString("folder_name"),
						data.getString("folder_description"),
						data.getString("folder_creation_date"),
						data.getString("folder_expiration"),
						data.getBoolean("folder_archive")
					)
				);
			 }
		} catch (SQLException e) {
			e.printStackTrace();
		}
	    return folders;
    }

	/**
	 * (Re)Chargement des données du tableau
	 */
	 public void reload() {
		cntFolders.removeAllItems();
		cntFolders.addAll(getFolders());
		
		if(getFolders().size()<1) {
			setVisible(false);
		} else {
			setVisible(true);
		}
    }
	/**
	 * Action exécutée sur le clique de l'option "Supprimer" du menu contextuel
	 * @param target (Object) Item en cours
	 */
	public void actionDelete(Folder target) {
		Sql.exec(tbl_folders.getDeleteFolder(Integer.toString(target.getPKNoFolder())));
		Sql.Disconnect();
		reload();
		
		// S'il n'y a aucun item dans la table
		CccvsTransfert.loadModule(new Module(null, TransfertModule.getBodyContent()));
	}

	/**
	 * Charge le contenu pour l'item sélectionné
	 */
	public void loadContentForSelectedItem() {
		/*
		String[] ids = getValuesFromObject(getValue());
		if(!ids[0].equals("")) {
			CccvsTransfert.loadModule(new Module(TransfertModule.getBodyRibbonContent(), FolderModule.getBodyContent(Integer.parseInt(ids[0]))));
		}
		*/
	}
}
