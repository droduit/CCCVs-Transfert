package main;


import sql.query.tbl_users;
import toolbox.Sql;
import global.Global;
import global.UserSession;

import com.vaadin.terminal.ExternalResource;
import com.vaadin.terminal.ThemeResource;
import com.vaadin.ui.AbsoluteLayout;
import com.vaadin.ui.AbstractLayout;
import com.vaadin.ui.Alignment;
import com.vaadin.ui.Button;
import com.vaadin.ui.CustomComponent;
import com.vaadin.ui.GridLayout;
import com.vaadin.ui.HorizontalLayout;
import com.vaadin.ui.Label;
import com.vaadin.ui.VerticalLayout;
import com.vaadin.ui.Embedded;
import com.vaadin.ui.Button.ClickEvent;

/**
 * <b>IMPORTANT !<b><br><br>Construction du composant principal de l'application.<br><br>
 * Ce composant personnalisé est un layout qui contient l'interface utilisateur. Il est complexe
 * et composé de plusieurs parties distinctes (Haut, Milieu, Bas). Chaque partie est dotée d'un menu.
 * Cette class ne contient que les blocs et dispositions, le contenu est chargé depuis la class principale {@link CccvsTransfert}.
 * @author Dominique Roduit
 * @version 1.0.0
 * @since 2013-03-01
 *
 */
public class MainLayout extends CustomComponent {
	
	/** Hauteur du header **/
	private final String HEADER_HEIGHT = Global.getParm("LAYOUT_MAIN_HEADER_HEIGHT");
	/** Hauteur du ruban **/
	private final String RIBBON_HEIGHT = Global.getParm("LAYOUT_MAIN_RIBBON_HEIGHT");
	/** Largeur du menu principal **/
	private final String MENU_WIDTH = Global.getParm("LAYOUT_MAIN_MENU_WIDTH");
	/** Hauteur du footer **/
	private final String FOOTER_HEIGHT = Global.getParm("LAYOUT_MAIN_FOOTER_HEIGHT");
	/** Layout principal pleine page contenant les 3 niveaux de bases (top, middle, bottom) **/
	private AbsoluteLayout mainLayout;
	
	// Zone du haut -------------------------------------------------------------------
	/** Layout général du haut **/
	private HorizontalLayout topLayout;
		/** Layout d'affichage du Logo de l'application **/
		private HorizontalLayout headerLogo;
		/** Layout du menu de droite dans le header de l'application **/
		private HorizontalLayout headerMenu;
	// ---------------------------------------------------------------------------------
	
	// Zone du milieu qui contient le menu + le corps de page -------------------------
	/** Layout général du milieu **/
	private HorizontalLayout middleLayout; 
		/** Contient les layout du menu **/
		private VerticalLayout menu;
			/** Ruban en haut du menu **/
			private HorizontalLayout menuRibbon;
			/** Zone dynamique du menu, placée en dessous du ruban **/
			private VerticalLayout menuContent;
		
		/** Corps de page localisé dans la zone du milieu et qui contient les éléments de la zone dynamique (rubban du body + zone de contenu) **/
		private VerticalLayout body;
			/** Ruban en haut du corps de page **/
			private HorizontalLayout bodyRibbon;
			/** Partie dynamique du body qui contiendra le contenu des pages **/
			private VerticalLayout bodyContent; 
			/** Zone contenant le menu dynamique du ruban du corps de page **/
			private HorizontalLayout bodyRibbonMenu; 
	// ---------------------------------------------------------------------------------
			
	// Zone du bas ---------------------------------------------------------------------
	/** Layout général du bas **/
	private VerticalLayout bottomLayout;
		/** Zone de téléchargement du footer<br>Masquée par défaut **/
		private VerticalLayout bottomContentArea;
		/** Bouton de changement de langue **/
		private Button btChangeLanguage;
	// ---------------------------------------------------------------------------------
	
	/**
	 * Assemblage des layouts par l'appel des méthodes de création des composants
	 */
	public MainLayout() {
		super.setSizeFull();
		
		// Absolute Layout qui va contenir tout les autres layout
		mainLayout = new AbsoluteLayout();
		mainLayout.setSizeFull();
		
		// Grid pleine page qui permet de placer les 3 premiers niveau de Layout Verticalement
		GridLayout vMain = new GridLayout(1, 3);
		vMain.setSizeFull();
		vMain.setRowExpandRatio(1, 100);
		mainLayout.addComponent(vMain);
		
		// Header
		topLayout = new HorizontalLayout();
		topLayout.setHeight(HEADER_HEIGHT);
		topLayout.setWidth("100%");
		topLayout.setStyleName("v-header");
		vMain.addComponent(topLayout, 0, 0);
		buildTopLayout();
	
		// Body
		middleLayout = new HorizontalLayout();
		middleLayout.setSizeFull();
		middleLayout.setStyleName("v-body");
		vMain.addComponent(middleLayout, 0, 1);
		buildMiddleLayout();
		
		// Footer
		bottomLayout = new VerticalLayout();
		bottomLayout.setSizeFull();
		bottomLayout.setHeight(FOOTER_HEIGHT);
		bottomLayout.setStyleName("v-footer");
		vMain.addComponent(bottomLayout, 0, 2);
		buildBottomLayout();
		
		setCompositionRoot(mainLayout);
	}
	// -------------------------------------------------------------------------------------
	// =============== Construction du Layout du haut ====================================
	// -------------------------------------------------------------------------------------
	/**
	 * Construction du layout du haut de la page (header)
	 */
	private void buildTopLayout() {
		// Grid qui va contenir le logo et le menu du header
		GridLayout grdTopLayout = new GridLayout(2,1);
		grdTopLayout.setSizeFull();
		grdTopLayout.setColumnExpandRatio(1, 100);
		topLayout.addComponent(grdTopLayout);
		
		// Logo du header à la meme taille que le menu
		headerLogo = new HorizontalLayout();
		headerLogo.setSizeUndefined();
		headerLogo.setStyleName("v-header-logo");
		headerLogo.setMargin(false,false,false,true);
		grdTopLayout.addComponent(headerLogo, 0,0);
		grdTopLayout.setComponentAlignment(headerLogo, Alignment.MIDDLE_LEFT);
		
		// Menu du header
		headerMenu = new HorizontalLayout();
		headerMenu.setSizeUndefined();
		headerMenu.setStyleName("v-header-menu");
		headerMenu.setSpacing(true);
		headerMenu.setMargin(false, true, false, false);
		grdTopLayout.addComponent(headerMenu, 1,0);
		grdTopLayout.setComponentAlignment(headerMenu, Alignment.MIDDLE_RIGHT);	
	}
	// -------------------------------------------------------------------------------------
	
	
	// -------------------------------------------------------------------------------------
	// =============== Construction du Layout du milieu ====================================
	// -------------------------------------------------------------------------------------
	/**
	 * Construction du Layout du millieu (menu et corps de page)
	 */
	private void buildMiddleLayout() {
		
		GridLayout grdMiddleLayout = new GridLayout(2,1);
		grdMiddleLayout.setSizeFull();
		grdMiddleLayout.setColumnExpandRatio(1, 100);
		middleLayout.addComponent(grdMiddleLayout);
		
		// Menu qui contiendra l'arbre
		menu = new VerticalLayout();
		menu.setWidth(MENU_WIDTH);
		menu.setHeight("100%");
		menu.setStyleName("v-body-menu");
		grdMiddleLayout.addComponent(menu,0,0);
		
		// Fabrication du menu
		loadMenuLayout();
		
		// Corps de page qui contiendra le contenu principal
		body = new VerticalLayout();
		body.setSizeFull();
		body.setStyleName("v-body");
		grdMiddleLayout.addComponent(body,1,0);
		
		// Fabrication du body
		loadBodyLayout();
	}
	/**
	 * Construction des éléments du menu latéral
	 */
	private void loadMenuLayout() {
		// Grid pour utiliser le 100% de la hauteur du menu
		GridLayout grdMenuLayout = new GridLayout(1,2);
		grdMenuLayout.setSizeFull();
		grdMenuLayout.setRowExpandRatio(1, 100);
		menu.addComponent(grdMenuLayout);
		
		// Ruban en haut du menu
		menuRibbon = new HorizontalLayout();
		menuRibbon.setHeight(RIBBON_HEIGHT);
		menuRibbon.setWidth("100%");
		menuRibbon.setStyleName("v-menu-ribbon");
		grdMenuLayout.addComponent(menuRibbon,0,0);
		
		// Ajout du contenu du ruban --------------------------------------------

        // Insertion du texte
        Label lblFileManager = new Label(Global.getParm("APP_NAME")+" "+Global.getParm("APP_VERSION").substring(0, Global.getParm("APP_VERSION").lastIndexOf(".")));
        lblFileManager.setStyleName("v-menu-ribbon-lbl");
        menuRibbon.addComponent(lblFileManager);  
	    //----------------------------------------------------------------------

		// Contenu du menu, zone dynamique qui contiendra l'arbre
		menuContent = new VerticalLayout();
		menuContent.setSizeFull();
		menuContent.setSpacing(false);
		menuContent.setStyleName("v-menu-content");
		grdMenuLayout.addComponent(menuContent,0,1);
	}
	/**
	 * Construction des éléments du corps de page
	 */
	private void loadBodyLayout() {
		// Grid pour utiliser le 100% de la largeur du body
		GridLayout grdBodyLayout = new GridLayout(1,2);
		grdBodyLayout.setSizeFull();
		grdBodyLayout.setRowExpandRatio(1, 100);
		body.addComponent(grdBodyLayout);
		body.setExpandRatio(grdBodyLayout, 100);
		
		// Ruban en haut du corps de page qui contiendra les boutons
		bodyRibbon = new HorizontalLayout();
		bodyRibbon.setHeight(RIBBON_HEIGHT);
		bodyRibbon.setWidth("100%");
		bodyRibbon.setStyleName("v-body-ribbon");
		grdBodyLayout.addComponent(bodyRibbon, 0,0);
		
		// Ajout d'un layout horizontal aligné a gauche pour le boutons d'actions
		bodyRibbonMenu = new HorizontalLayout();
		bodyRibbonMenu.setStyleName("v-body-ribbon-menu");
		bodyRibbonMenu.setSpacing(true);
		bodyRibbon.addComponent(bodyRibbonMenu);
		bodyRibbon.setComponentAlignment(bodyRibbonMenu, Alignment.MIDDLE_LEFT);
		
		// Partie dynamique du body qui contiendra vraiment le contenu des différentes pages
		bodyContent = new VerticalLayout();
		bodyContent.setSizeFull();
		bodyContent.setStyleName("v-body");
		grdBodyLayout.addComponent(bodyContent);
	}
	// -------------------------------------------------------------------------------------

	// -------------------------------------------------------------------------------------
	// ================== Construction du Layout du bas ====================================
	// -------------------------------------------------------------------------------------
	/**
	 * Construction du layout du bas (footer)
	 */
	public void buildBottomLayout() {
		GridLayout grdBottomLayout = new GridLayout(1,2);
		grdBottomLayout.setSizeFull();
		grdBottomLayout.setRowExpandRatio(1, 100);
		bottomLayout.addComponent(grdBottomLayout);
		
		// Zone en haut du layout pour rétrécir le layout
		HorizontalLayout footerToogle = new HorizontalLayout();
		footerToogle.setWidth("100%");
		footerToogle.setHeight("25px");
		footerToogle.setStyleName("v-footer-toogle");
		grdBottomLayout.addComponent(footerToogle,0,0);
		
		// Contenu de la zone toogle --------------------------------------
		GridLayout grdToogle = new GridLayout(2,1);
		grdToogle.setSizeFull();
		footerToogle.addComponent(grdToogle);
		
		// Insertion du Label a coté de l'image de transfert
		Label lblUserMail = new Label();
		lblUserMail.setIcon(new ThemeResource(Global.IMG_APPROVED));
		lblUserMail.setCaption(UserSession.getMail());
		lblUserMail.setStyleName("v-footer-toogle-text");
		grdToogle.addComponent(lblUserMail,0,0);
		grdToogle.setComponentAlignment(lblUserMail, Alignment.MIDDLE_LEFT);
		
		bottomLayout.setHeight("25px");

		// Insertion de l'image pour rétrécir le footer
		btChangeLanguage = new Button("", new Button.ClickListener() {
			@Override
			public void buttonClick(ClickEvent event) {
				String newLanguage = (Global.IMG_FLAG_COUNTRY.indexOf("de")>-1) ? "fr" : "de";
				Global.IMG_FLAG_COUNTRY = Global.PATH_THEME_RESSOURCES+newLanguage+".png";
				event.getComponent().setIcon(new ThemeResource(Global.IMG_FLAG_COUNTRY));
				
				UserSession.setLangue(newLanguage);
				// Mise à jour de la langue de la base de données
				if(UserSession.getID()>0) {
					Sql.exec(tbl_users.getUpdLanguage(newLanguage));
				}
				// Rechargement du tableau des traductions
				Global.reloadTranslations();
				// Rechargement du layout complet depuis la class principal CccvsTransfert
			}
		});
		btChangeLanguage.setDescription(Global.i("CAPTION_CHANGE_LANGUAGE"));
		btChangeLanguage.setIcon(new ThemeResource(Global.IMG_FLAG_COUNTRY));
		btChangeLanguage.setStyleName("v-footer-toogle-button");
		grdToogle.addComponent(btChangeLanguage,1,0);
		grdToogle.setComponentAlignment(btChangeLanguage, Alignment.MIDDLE_RIGHT);

		// ----------------------------------------------------------------

		// Zone qui contiendra les téléchargements en cours
		bottomContentArea = new VerticalLayout();
		bottomContentArea.setSizeFull();
		grdBottomLayout.addComponent(bottomContentArea,0,1);
		
	}
	// -------------------------------------------------------------------------------------
	/**
	 * Affiche/Masque le footer
	 * @param enabled true : affiché, false : désactivé
	 */
	public void setFooterEnabled(boolean enabled) {
		bottomContentArea.setVisible(enabled);
		int size = (enabled) ? 190 : 25;
		bottomLayout.setHeight(size+"px");
	}
	/**
	 * Restaure la taille initiale du ruban
	 */
	public void restoreRibbonHeight() {
		bodyRibbon.setHeight(RIBBON_HEIGHT);
	}
	// -------------------------------------------------------------------------------------
	// ==================== getters des composants à contenu dynamique =====================
	// -------------------------------------------------------------------------------------
	/**
	 * Retourne la zone du header contenant le logo 
	 * @return Zone du header contenant le logo
	 */
	public HorizontalLayout getHeaderLogo() {
		return headerLogo;
	}
	/**
	 * Retourne le menu du header
	 * @return Menu du header
	 */
	public HorizontalLayout getHeaderMenu() {
		return headerMenu;
	}
	/**
	 * Retourne le corps de page dans lequel on va insérer notre contenu
	 * @return Corps de page
	 */
	public VerticalLayout getBody() {
		return bodyContent;
	}
	/**
	 * @return Zone racine du ruban du corps de page
	 */
	public HorizontalLayout getBodyRibbonWrapper() {
		return bodyRibbon;
	}
	/**
	 * Retourne le ruban du corps de page
	 * @return Ruban du corps de page
	 */
	public HorizontalLayout getBodyRibbon() {
		return bodyRibbonMenu;
	}
	/**
	 * Retourne la zone d'affichage dynamique du menu
	 * @return Menu latéral
	 */
	public VerticalLayout getMenu() {
		return menuContent;
	}
	/**
	 * Retourne le ruban du menu
	 * @return Ruban du menu
	 */
	public HorizontalLayout getMenuRibbon() {
		return menuRibbon;
	}
	/**
	 * Retourne la zone de téléchargement dans le footer
	 * @return Zone de téléchargement du footer
	 */
	public VerticalLayout getFooter() {
		return bottomContentArea;
	}
	/**
	 * Retourne le bouton de changement de langue
	 * @return Bouton de changement de langue
	 */
	public Button getBtChangeLanguage() {
		return btChangeLanguage;
	}
	// -------------------------------------------------------------------------------------
	
	// -------------------------------------------------------------------------------------
	// ==================== setters des composants à contenu dynamique =====================
	// -------------------------------------------------------------------------------------
	/**
	 * Fixe la taille du menu latéral
	 * @param width Taille du menu en px
	 */
	public void setMenuWidth(String width) {
		menu.setWidth(width);
	}
	// -------------------------------------------------------------------------------------
}
