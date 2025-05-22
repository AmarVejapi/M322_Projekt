package com.amar.quizmaster.views;

import com.vaadin.flow.component.applayout.AppLayout;
import com.vaadin.flow.component.applayout.DrawerToggle;
import com.vaadin.flow.component.button.Button;
import com.vaadin.flow.component.button.ButtonVariant;
import com.vaadin.flow.component.icon.VaadinIcon;
import com.vaadin.flow.component.orderedlayout.FlexComponent;
import com.vaadin.flow.component.orderedlayout.HorizontalLayout;
import com.vaadin.flow.component.orderedlayout.Scroller;
import com.vaadin.flow.component.sidenav.SideNav;
import com.vaadin.flow.component.sidenav.SideNavItem;
import com.vaadin.flow.dom.Style;

public class MainLayout extends AppLayout {

    public MainLayout() {
        addToNavbar(getHeader());

        SideNav nav = getSideNav();
        var scroller = new Scroller(nav);
        addToDrawer(scroller);

        setPrimarySection(Section.DRAWER);
    }

    private HorizontalLayout getHeader() {
        var toggle = new DrawerToggle();

        var leftSection = new HorizontalLayout(toggle);
        leftSection.setAlignItems(FlexComponent.Alignment.CENTER);

        var logoutButton = new Button("Logout", event -> getUI().ifPresent(ui -> ui.navigate("login")));
        logoutButton.addThemeVariants(ButtonVariant.LUMO_PRIMARY, ButtonVariant.LUMO_ERROR);

        var rightSection = new HorizontalLayout(logoutButton);
        rightSection.setAlignItems(FlexComponent.Alignment.CENTER);
        rightSection.setJustifyContentMode(FlexComponent.JustifyContentMode.END);

        var header = new HorizontalLayout(leftSection, rightSection);
        header.setWidthFull();
        header.setPadding(true);
        header.setJustifyContentMode(FlexComponent.JustifyContentMode.BETWEEN);
        header.setAlignItems(FlexComponent.Alignment.CENTER);

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
}
