package com.horizon.view.home;

import com.vaadin.flow.component.UI;
import com.vaadin.flow.component.html.H1;
import com.vaadin.flow.component.html.Image;
import com.vaadin.flow.component.orderedlayout.VerticalLayout;
import com.vaadin.flow.component.page.PendingJavaScriptResult;
import com.vaadin.flow.router.PageTitle;
import com.vaadin.flow.router.Route;
import com.vaadin.flow.server.VaadinSession;
import elemental.json.Json;
import elemental.json.JsonValue;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpMethod;
import org.springframework.http.ResponseEntity;
import org.springframework.web.client.RestTemplate;

import java.util.HashMap;
import java.util.Map;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.doAnswer;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
public class HomeViewTest {

    @Mock
    private RestTemplate restTemplate;

    @Mock
    private UI ui;

    @Mock
    private com.vaadin.flow.component.page.Page page;

    @Mock
    private VaadinSession vaadinSession;

    private HomeView homeView;

    private final String backendUrl = "http://localhost:8080";

    @BeforeEach
    public void setup() {
        UI.setCurrent(ui);
        when(ui.getPage()).thenReturn(page);
        homeView = new HomeView(backendUrl, restTemplate);
    }

    @Test
    public void testHomeView_displaysWelcomeMessageAndProfilePicture() {
        // Given
        String jwtToken = "mock-jwt-token";
        Map<String, Object> user = new HashMap<>();
        user.put("name", "Test User");
        user.put("picture", "http://example.com/picture.jpg");

        PendingJavaScriptResult jsResult = mock(PendingJavaScriptResult.class);
        when(page.executeJs(eq("return localStorage.getItem('jwt_token');"))).thenReturn(jsResult);
        doAnswer(invocation -> {
            com.vaadin.flow.function.SerializableConsumer<String> callback = invocation.getArgument(1);
            callback.accept(jwtToken);
            return null;
        }).when(jsResult).then(eq(String.class), any(com.vaadin.flow.function.SerializableConsumer.class));

        when(restTemplate.exchange(eq(backendUrl + "/api/user"), eq(HttpMethod.GET), any(HttpEntity.class), eq(Map.class)))
                .thenReturn(ResponseEntity.ok(user));

        // When
        homeView.onAttach(null);

        // Then
        H1 welcomeMessage = (H1) homeView.getChildren().findFirst().get();
        assertEquals("Welcome, Test User!", welcomeMessage.getText());

        Image profilePicture = (Image) homeView.getChildren().skip(1).findFirst().get();
        assertEquals("http://example.com/picture.jpg", profilePicture.getSrc());
    }
}