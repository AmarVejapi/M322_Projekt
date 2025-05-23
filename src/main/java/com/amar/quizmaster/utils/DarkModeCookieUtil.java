package com.amar.quizmaster.utils;

import com.vaadin.flow.component.UI;
import com.vaadin.flow.component.button.Button;
import com.vaadin.flow.component.icon.VaadinIcon;
import com.vaadin.flow.server.VaadinRequest;
import com.vaadin.flow.server.VaadinService;
import com.vaadin.flow.theme.lumo.Lumo;
import jakarta.servlet.http.Cookie;

public class DarkModeCookieUtil extends Button {

    private static final String DARK_MODE_COOKIE = "DarkMode";

    public DarkModeCookieUtil() {
        boolean darkMode = getCookieValue().equals("true");
        updateTheme(darkMode);

        addClickListener(event -> updateTheme(!UI.getCurrent().getElement().getThemeList().contains(Lumo.DARK)));
    }

    private void updateTheme(boolean enableDarkMode) {
        UI.getCurrent().getElement().getThemeList().set(Lumo.DARK, enableDarkMode);
        setIcon(VaadinIcon.MOON.create());
        setCookie(enableDarkMode ? "true" : "false");
    }

    private static void setCookie(String value) {
        Cookie cookie = new Cookie(DarkModeCookieUtil.DARK_MODE_COOKIE, value);
        cookie.setMaxAge(Integer.MAX_VALUE);
        VaadinService.getCurrentResponse().addCookie(cookie);
    }

    private static String getCookieValue() {
        if (VaadinRequest.getCurrent() == null) return "";
        for (Cookie cookie : VaadinRequest.getCurrent().getCookies()) {
            if (DarkModeCookieUtil.DARK_MODE_COOKIE.equals(cookie.getName())) {
                return cookie.getValue();
            }
        }
        return "";
    }
}