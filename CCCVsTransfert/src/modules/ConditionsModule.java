package modules;

import main.CccvsTransfert;
import main.MainLayout;
import toolbox.Utilities;

import com.vaadin.terminal.ThemeResource;
import com.vaadin.ui.Alignment;
import com.vaadin.ui.Button;
import com.vaadin.ui.Embedded;
import com.vaadin.ui.HorizontalLayout;
import com.vaadin.ui.Label;
import com.vaadin.ui.VerticalLayout;
import com.vaadin.ui.Window;
import com.vaadin.ui.Button.ClickEvent;
import com.vaadin.ui.themes.Reindeer;
import com.vaadin.ui.themes.Runo;

import common.component.AccordionMenu;
import common.component.ContactTable;
import common.component.PanelLight;

import form.WizFoldNew;
import form.WizFoldUpload;
import global.Global;
import global.GlobalObjects;

/**
 * Page d'affichage des conditions d'utilisation de l'application.<br>
 * La page inclut un bouton de retour au dernier module actif.<br>
 * Les conditions sont contenues dans un bloc et illustr�es par une image libre au format PNG.
 * @author Dominique Roduit
 *
 */
public class ConditionsModule {
	/** Contient toutes les instances qui doivent �tre accessibles dans toute l'application **/
	private static GlobalObjects global = CccvsTransfert.getGlobalMethod();
	/** Instance de la fen�tre principale **/
	private static Window mainWindow = CccvsTransfert.mainWindow;
	/** Layout Global de l'application **/
	private static MainLayout mainLayout = global.getMainLayout();
	/** Bouton d'ajout d'un contact **/
	private static Button btAddFolder;
	/** Table contenant la liste des contacts **/
	private static ContactTable tblFolder;

	/**
	 * Chargement du bouton de retour et du titre dans le ruban du corps de la page
	 */
	public static HorizontalLayout getBodyRibbonContent() {
		System.out.println(Utilities.getCurrentDate()+" -- [Chargement du module] Ruban");
	
		HorizontalLayout vlRibbon = new HorizontalLayout();
		vlRibbon.setSizeFull();
		vlRibbon.setSpacing(true);
		
		Button btBack = new Button(Global.i("CAPTION_BACK"), new Button.ClickListener() {
			public void buttonClick(ClickEvent event) {
				String slctTab = AccordionMenu.getSelectedTab();
				AccordionMenu.selectTab(slctTab);
			}
		});
		btBack.setStyleName(Runo.BUTTON_DEFAULT);
		vlRibbon.addComponent(btBack);
		vlRibbon.setComponentAlignment(btBack, Alignment.MIDDLE_LEFT);
		
		Label lblTitle = new Label(Global.i("CAPTION_USAGE_CONDITIONS"));
		lblTitle.setStyleName(Runo.LABEL_H1);
		vlRibbon.addComponent(lblTitle);
		vlRibbon.setComponentAlignment(lblTitle, Alignment.MIDDLE_LEFT);
		
		Button btUserManual = new Button(Global.i("CAPTION_USER_MANUAL"), new Button.ClickListener() {
			@Override
			public void buttonClick(ClickEvent event) {
				Window winUserManual = new Window(Global.i("CAPTION_USER_MANUAL"));
				winUserManual.setWidth("880px");
				winUserManual.setHeight("580px");
				winUserManual.center();
				winUserManual.setStyleName(Reindeer.WINDOW_BLACK);
				winUserManual.setResizable(false);
				winUserManual.setModal(true);
				CccvsTransfert.mainWindow.addWindow(winUserManual);
				
				Embedded pdf = new Embedded(null, new ThemeResource(Global.PATH_THEME_RESSOURCES+"user-manual.pdf"));
				pdf.setMimeType("application/pdf");
				pdf.setType(Embedded.TYPE_BROWSER);
				pdf.setHeight("520px");
				pdf.setWidth("845px");
				winUserManual.addComponent(pdf);
				
			}
		});
		btUserManual.setIcon(new ThemeResource(Global.PATH_THEME_RESSOURCES+"usermanual.png"));
		btUserManual.setStyleName("ribbon-right-alignment");
		vlRibbon.addComponent(btUserManual);
		vlRibbon.setComponentAlignment(btUserManual, Alignment.MIDDLE_RIGHT);

	    return vlRibbon;
	} 
	/**
	 * Insertion du contenu dans le corps de la page.<br>Le contenu comprend le texte, extrait de la base de donn�es ainsi qu'une image illustrative.
	 */
	public static VerticalLayout getBodyContent() {
		System.out.println(Utilities.getCurrentDate()+" -- [Chargement du module] Body");
	    
		VerticalLayout body = new VerticalLayout();
		body.setSizeFull();
	
		PanelLight pnlContent = new PanelLight();
		pnlContent.setScrollable(true);
		body.addComponent(pnlContent);
		
		
		String imgContent = "<div align=\"center\" style=\"margin-top:8px\"><img src=\""+Global.PATH_THEME_RESSOURCES_HTML+"book.png\" /></div>";
		
		Label lblContent = new Label(imgContent+"<div class=\"content-area-about\">"+Utilities.parmConverter(Global.i("CONTENT_USAGE_CONDITIONS"))+"</div>", Label.CONTENT_XHTML);
		pnlContent.addComponent(lblContent);
		
		return body;
	}
}
