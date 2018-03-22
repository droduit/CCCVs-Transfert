package common.component;

import com.vaadin.ui.Panel;
import com.vaadin.ui.VerticalLayout;
import com.vaadin.ui.themes.Reindeer;

/**
 * Cr�ation d'un panel d�pourvu du style CSS natif de Vaadin
 * @author Dominique Roduit
 *
 */
public class PanelLight extends Panel {
	/** Layout pour la suppression des styles appliqu�s sur le Panel **/
	private VerticalLayout layout;
	
	/**
	 * Cr�ation d'un Panel sans style CSS
	 */
	public PanelLight() {
		super();
		setStyleName(Reindeer.PANEL_LIGHT);
		setSizeFull();
		
		layout = (VerticalLayout) getContent();
		layout.setStyleName("v-menu-buttons");
		layout.setSizeFull();
		layout.setMargin(false);
		layout.setSpacing(false);
	}
	/**
	 * Sp�cifications des marges
	 * @param enabled true = Marges activ�es
	 */
	public void setMargin(boolean enabled) {
		layout.setMargin(enabled);
	}
	/**
	 * Sp�cification des marges int�rieur (padding)
	 * @param enabled true = marges int�rieur activ�es (padding)
	 */
	public void setSpacing(boolean enabled) {
		layout.setSpacing(enabled);
	}
	/**
	 * Retourne le layout int�rieur du panel
	 */
	public VerticalLayout getLayout() {
		return layout;
	}
	/**
	 * Rend le panel scrollable ou non
	 */
	public void setScrollable(boolean enabled) {
		if(enabled) {
			setSpacing(true);
			setMargin(true);
			getLayout().setSizeUndefined();
			getLayout().setWidth("100%");
			getLayout().removeStyleName("v-menu-buttons");
		} else {
			layout.setStyleName("v-menu-buttons");
			layout.setSizeFull();
			layout.setMargin(false);
			layout.setSpacing(false);
		}
	}
}
