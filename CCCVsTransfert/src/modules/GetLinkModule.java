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
import com.vaadin.ui.TextArea;
import com.vaadin.ui.VerticalLayout;
import com.vaadin.ui.Window.Notification;

import main.CccvsTransfert;
import model.Contact;
import model.Folder;

/**
 * Module qui permet la récupération d'un lien de fichier ou d'un lien d'une archive de dossier.<br>
 * L'utilisateur peut ainsi télécharger lui même ses propres fichiers ou partager un fichier avec n'importe
 * qui d'autre en lui donnant uniquement l'URL vers le fichier.
 * @author Dominique Roduit
 *
 */
public class GetLinkModule extends CustomComponent {
	/** Stockage des objets conservés en global **/
	private GlobalObjects global = CccvsTransfert.getGlobalMethod();
	/** Layout racine du module **/
	private VerticalLayout mainLayout = new VerticalLayout();

	/**
	 * Affichage des composants (TextArea, boutons)
	 * @param filename Nom du fichier sur le disque
	 */
	public GetLinkModule(String filename) {
		mainLayout.setSizeFull();
		mainLayout.setSpacing(true);
		
		TextArea txtLink = new TextArea();
		txtLink.setWidth("100%");
		txtLink.setHeight("50px");
		txtLink.setValue(Utilities.getURLForFile(filename));
		txtLink.selectAll();
		txtLink.focus();
		txtLink.setReadOnly(true);
		txtLink.setStyleName("get-link-textarea");
		mainLayout.addComponent(txtLink);
		
		HorizontalLayout buttons = new HorizontalLayout();
		buttons.setSpacing(true);
		mainLayout.addComponent(buttons);
		
		Button btAnnuler = new Button(Global.i("CAPTION_CLOSE"), new Button.ClickListener() {
			public void buttonClick(ClickEvent event) {
				closeWindow();
			}
		});

		buttons.addComponent(btAnnuler);
		buttons.setComponentAlignment(btAnnuler, Alignment.MIDDLE_RIGHT);
		
		mainLayout.setComponentAlignment(buttons, Alignment.MIDDLE_RIGHT);
	
		setCompositionRoot(mainLayout);
	}
	/**
	 * Fermeture de la fenêtre flottante
	 */
	private void closeWindow() {
		(global.getWinGetLink().getParent()).removeWindow(global.getWinGetLink());
	}
}
