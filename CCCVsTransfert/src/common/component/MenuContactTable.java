package common.component;

import global.Global;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.Collection;
import java.util.HashSet;
import java.util.Iterator;
import java.util.List;
import java.util.Set;

import sql.query.tbl_contacts;
import toolbox.Sql;
import toolbox.Utilities;

import model.Contact;
import modules.ContactModule;

import com.vaadin.data.Container;
import com.vaadin.data.Item;
import com.vaadin.data.util.BeanItemContainer;
import com.vaadin.event.Action;
import com.vaadin.event.ItemClickEvent;
import com.vaadin.event.ItemClickEvent.ItemClickListener;
import com.vaadin.event.ShortcutAction.KeyCode;
import com.vaadin.event.ShortcutListener;
import com.vaadin.terminal.ThemeResource;
import com.vaadin.ui.Button;
import com.vaadin.ui.Component;
import com.vaadin.ui.DefaultFieldFactory;
import com.vaadin.ui.Field;
import com.vaadin.ui.Label;
import com.vaadin.ui.Table;
import com.vaadin.ui.TableFieldFactory;

/**
 * Table pour la gestion des contacts, affichée dans le menu latéral gauche.
 * Cette class permet l'affichage des contacts dans un tableau.
 * Le tableau implémente un menu, affiché sur le clic droit de la souris.
 * Ce menu propose la suppression des contacts.
 * En sélection multiple dans le tableau, plusieurs contacts peuvent être supprimés en même temps.
 * @author Dominique Roduit
 * @version 1.0
 *
 */
public class MenuContactTable extends ContactTable {
	/** Action supprimer du menu contextuel **/
	protected static Action ACTION_DEL;
	/** Liste des actions du menu contextuel affiché sur le clique droit pour ce tableau **/
	protected static Action[] ACTIONS;
	/** Stockage de la table des contacts du corps de page **/
	protected ContactTable contactTable = global.getTableContact();
	
	/**
	 * Affiche un tableau contenant la liste des contacts
	 */
	public MenuContactTable() {
		super();
		
		ACTION_DEL = new Action(Global.i("CAPTION_DELETE"),new ThemeResource(Global.PATH_THEME_RESSOURCES+"false.png"));
		ACTIONS = new Action[] { ACTION_DEL };
		
		setStyleName("no-resizable");
		
		removeContainerProperty("contact_name");
		addContainerProperty("contact_name", Label.class, null, Global.i("CAPTION_NAME"), null, null);
		setVisibleColumns(new Object[]{"contact_name"});
		
		removeAllActionHandlers();
		removeAllItems();
		
		// Gestion du clique droit
		addActionHandler(new Action.Handler() {
			// Sur le clique droit, on retourne les actions désirées
			public Action[] getActions(Object target, Object sender) {
				if(target!=null) {
					return ACTIONS;
				} else {
					return null;
				}
			}
			// Lors de l'appui sur une action
			public void handleAction(Action action, Object sender, Object target) {
				if(action==ACTION_DEL) {
					actionDelete(target);
				}
			}
		});
		
		// Sur le clique d'un contact
		addListener(new ItemClickListener() {
			public void itemClick(ItemClickEvent event) {
				if(event.getButton()==ItemClickEvent.BUTTON_LEFT) {
					//getWindow().showNotification(event.getItemId().toString());
				}
			}
		});
	}
	/**
	 * Sélectionn des données qui vont remplir notre tableau
	 * @return (Object[]) Tableau d'objet contenant les lignes du tableau
	 */
	public Object[] getTableData() {
    	super.PKField.clear();
		ResultSet data = Sql.query(tbl_contacts.getSelectAllContactQuery());
		
		System.out.println(Sql.rowCount()+" --- "+tbl_contacts.getSelectAllContactQuery());
	     
	     Object[] files = null;
	     try {
	    	data.last();
		    files = new Object[data.getRow()+1];
		    data.beforeFirst();
		    int j=0;
		    
			while(data.next()) {
				super.PKField.add(data.getInt("PKNoContact"));
				
				Label lblContact = new Label(data.getString("contact_name"));
				lblContact.setStyleName("cursor-pointer");
				lblContact.addStyleName("v-menu-item-contact");
				if(!data.getBoolean("contact_internal")) { lblContact.addStyleName("contact-extern"); }
				
				files[j] = new Object[] {
					lblContact
				};
				j++;
			 }
		} catch (SQLException e) {
			e.printStackTrace();
		} finally {
			Sql.Disconnect();
		}
	    return files;
    }
	/**
	 * Action exécutée sur le clique de l'option "Supprimer" du menu contextuel
	 * @param target (Object) Item en cours
	 */
	@Override
	public void actionDelete(Object target) {
		super.actionDelete(target);
		contactTable = global.getTableContact();
		contactTable.reload();
	}
}
