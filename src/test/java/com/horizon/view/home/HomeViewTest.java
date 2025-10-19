package com.horizon.view.home;

import com.vaadin.flow.component.UI;
import com.vaadin.flow.component.html.H1;
import com.vaadin.flow.component.html.Image;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.web.client.RestTemplate;

import java.util.HashMap;
import java.util.Map;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
public class HomeViewTest {

    @Mock
    private RestTemplate restTemplate;

    private HomeView homeView;

    @BeforeEach
    public void setup() {
        UI.setCurrent(new UI());
        homeView = new HomeView("http://localhost:8080", restTemplate);
    }

    @Test
    public void testHomeView_displaysWelcomeMessageAndProfilePicture() {
        // Given
        Map<String, Object> user = new HashMap<>();
        user.put("name", "Test User");
        user.put("picture", "http://example.com/picture.jpg");

        when(restTemplate.getForObject("http://localhost:8080/api/user", Map.class)).thenReturn(user);

        // When
        homeView.onAttach(null);

        // Then
        H1 welcomeMessage = (H1) homeView.getChildren().findFirst().get();
        assertEquals("Welcome, Test User!", welcomeMessage.getText());

        Image profilePicture = (Image) homeView.getChildren().skip(1).findFirst().get();
        assertEquals("http://example.com/picture.jpg", profilePicture.getSrc());
    }
}
