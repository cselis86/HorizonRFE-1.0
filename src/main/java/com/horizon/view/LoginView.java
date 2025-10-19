package com.horizon.view;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.vaadin.flow.component.UI;
import com.vaadin.flow.component.button.Button;
import com.vaadin.flow.component.html.Anchor;
import com.vaadin.flow.component.html.H1;
import com.vaadin.flow.component.login.LoginForm;
import com.vaadin.flow.component.orderedlayout.VerticalLayout;
import com.vaadin.flow.router.BeforeEnterEvent;
import com.vaadin.flow.router.BeforeEnterObserver;
import com.vaadin.flow.router.PageTitle;
import com.vaadin.flow.router.Route;
import com.vaadin.flow.router.RouteAlias;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.web.client.RestTemplate;

import java.util.HashMap;
import java.util.Map;

@Route("login")
@RouteAlias("")
@PageTitle("Login | Horizon Rent")
public class LoginView extends VerticalLayout implements BeforeEnterObserver {

    protected final LoginForm login = new LoginForm();
    private final String backendUrl;
    private final RestTemplate restTemplate;

    public LoginView(@Value("${app.backend.url}") String backendUrl, RestTemplate restTemplate) {
        this.backendUrl = backendUrl;
        this.restTemplate = restTemplate;
        addClassName("login-view");
        setSizeFull();
        setAlignItems(Alignment.CENTER);
        setJustifyContentMode(JustifyContentMode.CENTER);

        // Remove default action, we'll handle login with JWT
        login.setAction(null);
        login.addLoginListener(event -> authenticate(event.getUsername(), event.getPassword()));

        Anchor googleLoginButton = new Anchor(backendUrl + "/oauth2/authorization/google", "Login with Google");
        googleLoginButton.getElement().setAttribute("router-ignore", true);

        add(new H1("Horizon Rent"), login, googleLoginButton);
    }

    protected void authenticate(String username, String password) {
        String loginUrl = backendUrl + "/auth/login";

        HttpHeaders headers = new HttpHeaders();
        headers.setContentType(MediaType.APPLICATION_JSON);

        Map<String, String> requestBody = new HashMap<>();
        requestBody.put("username", username);
        requestBody.put("password", password);

        try {
            HttpEntity<Map<String, String>> request = new HttpEntity<>(requestBody, headers);
            Map<String, String> response = restTemplate.postForObject(loginUrl, request, Map.class);

            String jwtToken = response.get("token");
            if (jwtToken != null) {
                // Store JWT token in local storage
                UI.getCurrent().getPage().executeJs("localStorage.setItem('jwt_token', $0)", jwtToken);
                UI.getCurrent().navigate("/home");
            } else {
                login.setError(true);
            }
        } catch (Exception e) {
            login.setError(true);
            e.printStackTrace();
        }
    }

    @Override
    public void beforeEnter(BeforeEnterEvent beforeEnterEvent) {
        // inform the user about an authentication error
        if (beforeEnterEvent.getLocation()
                .getQueryParameters()
                .getParameters()
                .containsKey("error")) {
            login.setError(true);
        }
    }
}
