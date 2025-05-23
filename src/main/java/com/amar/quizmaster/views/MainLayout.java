package com.amar.quizmaster.views;

import com.amar.quizmaster.utils.DarkModeCookieUtil;
import com.vaadin.flow.component.applayout.AppLayout;
import com.vaadin.flow.component.applayout.DrawerToggle;
import com.vaadin.flow.component.button.Button;
import com.vaadin.flow.component.button.ButtonVariant;
import com.vaadin.flow.component.html.H2;
import com.vaadin.flow.component.icon.VaadinIcon;
import com.vaadin.flow.component.orderedlayout.HorizontalLayout;
import com.vaadin.flow.component.orderedlayout.Scroller;
import com.vaadin.flow.component.sidenav.SideNav;
import com.vaadin.flow.component.sidenav.SideNavItem;
import com.vaadin.flow.dom.Style;
import com.vaadin.flow.router.HasDynamicTitle;
import com.vaadin.flow.router.PageTitle;
import com.vaadin.flow.theme.lumo.LumoUtility;

public class MainLayout extends AppLayout {

    private final H2 pageTitle = new H2();

    public MainLayout() {
        addToNavbar(getHeader());

        SideNav nav = getSideNav();
        var scroller = new Scroller(nav);
        addToDrawer(scroller);

        setPrimarySection(Section.DRAWER);
    }

    private HorizontalLayout getHeader() {
        var toggle = new DrawerToggle();

        pageTitle.addClassNames(LumoUtility.FontSize.LARGE, LumoUtility.Margin.NONE, LumoUtility.Flex.GROW);

        var logoutButton = new Button("Logout", event -> getUI().ifPresent(ui -> ui.navigate("login")));
        logoutButton.addThemeVariants(ButtonVariant.LUMO_PRIMARY, ButtonVariant.LUMO_ERROR);

        var header = new HorizontalLayout(toggle, pageTitle, new DarkModeCookieUtil(), logoutButton);
        header.addClassNames(
                LumoUtility.Width.FULL,
                LumoUtility.Display.FLEX,
                LumoUtility.AlignItems.CENTER,
                LumoUtility.Padding.End.MEDIUM
        );

        return header;
    }

    private SideNav getSideNav() {
        var nav = new SideNav();

        var titleItem = new SideNavItem("Quiz Master");
        titleItem.getStyle()
                .setFontSize("1.5em")
                .setFontWeight(Style.FontWeight.BOLD)
                .setPaddingBottom("20px");

        nav.addItem(titleItem,
                new SideNavItem("Home", MainView.class, VaadinIcon.HOME.create()),
                new SideNavItem("Lern-Quiz", LernQuizStartView.class, VaadinIcon.BOOK.create()),
                new SideNavItem("Test-Quiz", TestQuizStartView.class, VaadinIcon.CLIPBOARD_TEXT.create()),
                new SideNavItem("Admin-Bereich", AdminOverviewView.class, VaadinIcon.COG.create())
        );

        return nav;
    }

    private String getPageTitle() {
        if (getContent() instanceof HasDynamicTitle titleHolder) {
            return titleHolder.getPageTitle();
        }
        PageTitle title = getContent() != null ? getContent().getClass().getAnnotation(PageTitle.class) : null;
        return title != null ? title.value() : "";
    }

    @Override
    protected void afterNavigation() {
        super.afterNavigation();
        pageTitle.setText(getPageTitle());
    }
}
