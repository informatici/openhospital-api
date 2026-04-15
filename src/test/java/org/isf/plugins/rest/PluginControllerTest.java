/*
 * Open Hospital (www.open-hospital.org)
 * Copyright © 2006-2026 Informatici Senza Frontiere (info@informaticisenzafrontiere.org)
 *
 * Open Hospital is a free and open source software for healthcare data management.
 *
 * This program is free software: you can redistribute it and/or modify
 * it under the terms of the GNU General Public License as published by
 * the Free Software Foundation, either version 3 of the License, or
 * (at your option) any later version.
 *
 * https://www.gnu.org/licenses/gpl-3.0-standalone.html
 *
 * This program is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU General Public License for more details.
 *
 * You should have received a copy of the GNU General Public License
 * along with this program. If not, see <https://www.gnu.org/licenses/>.
 */
package org.isf.plugins.rest;

import org.isf.OpenHospitalApiApplication;
import org.isf.plugins.config.*;
import org.isf.plugins.exception.PluginAccessDeniedException;
import org.isf.plugins.proxy.IPluginRequestForwarder;
import org.isf.plugins.registry.IPluginRegistry;
import org.isf.plugins.security.AuthenticationContext;
import org.isf.plugins.security.IAuthenticationSupplier;
import org.isf.plugins.security.IPluginAuthorizationChecker;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.mock.web.MockMultipartFile;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.test.context.support.WithMockUser;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.MvcResult;
import org.springframework.web.client.RestClientResponseException;

import java.util.List;
import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@AutoConfigureMockMvc
@SpringBootTest(classes = OpenHospitalApiApplication.class)
class PluginControllerTest {


	private static final PluginBundle PLUGIN_BUNDLE = new PluginBundle("Smart Doc", "mf-manifest.json", "module", PluginLocation.MAIN, "assets/styles.css");

	/**
	 * A plugin definition with admin-only GET/POST/DELETE on /documents.
	 */
	private static final PluginDefinition SMART_DOC = new PluginDefinition("smart-doc", "http://localhost:4000/api", "/health", new PluginConfiguration(PLUGIN_BUNDLE, List.of(new PluginPermission("admin", List.of(new PluginRoute("/documents", List.of("GET", "POST", "DELETE")))))));

	@MockitoBean
	private IPluginRegistry pluginRegistry;
	@MockitoBean
	private IPluginAuthorizationChecker authorizationChecker;
	@MockitoBean
	private IPluginRequestForwarder requestForwarder;
	@MockitoBean
	private IAuthenticationSupplier authenticationSupplier;

	@Autowired
	private MockMvc mockMvc;

	@BeforeEach
	void stubAuthenticationSupplier() {
		// Delegate to SecurityContextHolder so @WithMockUser authentication is returned
		// when the controller calls authenticationSupplier.get() during request processing.
		lenient().when(authenticationSupplier.get()).thenAnswer(inv -> new AuthenticationContext(SecurityContextHolder.getContext().getAuthentication(), "admin"));
	}

	// -------------------------------------------------------------------------
	// GET /plugins — list registered plugins
	// -------------------------------------------------------------------------

	@Test
	@WithMockUser
	@DisplayName("GET /plugins returns the list of registered plugins")
	void shouldReturnListOfRegisteredPlugins() throws Exception {
		PluginDefinition billing = new PluginDefinition("billing", "http://localhost:5000/api", "/health", null);
		when(pluginRegistry.all()).thenReturn(List.of(SMART_DOC, billing));

		mockMvc.perform(get("/plugins")).andExpect(status().isOk()).andExpect(jsonPath("$.length()").value(2)).andExpect(jsonPath("$[0].id").value("smart-doc")).andExpect(jsonPath("$[1].id").value("billing"));
	}

	@Test
	@WithMockUser
	@DisplayName("GET /plugins returns empty array when no plugins are registered")
	void shouldReturnEmptyArrayWhenNoPluginsRegistered() throws Exception {
		when(pluginRegistry.all()).thenReturn(List.of());

		mockMvc.perform(get("/plugins")).andExpect(status().isOk()).andExpect(jsonPath("$.length()").value(0));
	}

	// -------------------------------------------------------------------------
	// ANY /plugins/{id}/** — proxy handler
	// -------------------------------------------------------------------------

	@Test
	@WithMockUser(username = "alice")
	@DisplayName("Should return upstream response for a proxied GET request")
	void shouldReturnUpstreamResponseForProxiedGetRequest() throws Exception {
		when(pluginRegistry.find("smart-doc")).thenReturn(Optional.of(SMART_DOC));
		doNothing().when(authorizationChecker).assertAccess(any(), any(), any());
		when(requestForwarder.forward(any(), any(), any(), any(), any())).thenReturn(ResponseEntity.ok("upstream-body".getBytes()));

		MvcResult result = mockMvc.perform(get("/plugins/smart-doc/documents/42")).andExpect(status().isOk()).andReturn();

		assertThat(result.getResponse().getContentAsString()).isEqualTo("upstream-body");
	}

	@Test
	@WithMockUser(username = "alice")
	@DisplayName("Should forward body and return 201 for a proxied POST request")
	void shouldForwardBodyAndReturn201ForProxiedPostRequest() throws Exception {
		when(pluginRegistry.find("smart-doc")).thenReturn(Optional.of(SMART_DOC));
		doNothing().when(authorizationChecker).assertAccess(any(), any(), any());
		when(requestForwarder.forward(any(), any(), any(), any(), any())).thenReturn(ResponseEntity.status(HttpStatus.CREATED).body(new byte[0]));

		mockMvc.perform(post("/plugins/smart-doc/documents").contentType(MediaType.APPLICATION_JSON).content("{\"title\":\"test\"}")).andExpect(status().isCreated());
	}

	// -------------------------------------------------------------------------
	// Verify assertAccess is called with the correct path and method
	// -------------------------------------------------------------------------

	@Test
	@WithMockUser(username = "alice")
	@DisplayName("Should call assertAccess with the sub-path and HTTP method")
	void shouldCallAssertAccessWithSubPathAndMethod() throws Exception {
		when(pluginRegistry.find("smart-doc")).thenReturn(Optional.of(SMART_DOC));
		doNothing().when(authorizationChecker).assertAccess(any(), any(), any());
		when(requestForwarder.forward(any(), any(), any(), any(), any())).thenReturn(ResponseEntity.ok(new byte[0]));

		mockMvc.perform(get("/plugins/smart-doc/documents/123"));

		verify(authorizationChecker).assertAccess(SMART_DOC, "/documents/123", "GET");
	}

	// -------------------------------------------------------------------------
	// 404 — plugin not found
	// -------------------------------------------------------------------------

	@Test
	@WithMockUser(username = "alice")
	@DisplayName("Should return 404 when the plugin is not found")
	void shouldReturn404WhenPluginNotFound() throws Exception {
		when(pluginRegistry.find("unknown")).thenReturn(Optional.empty());

		mockMvc.perform(get("/plugins/unknown/path")).andExpect(status().isNotFound()).andExpect(jsonPath("$.status").value(404)).andExpect(jsonPath("$.message").value(org.hamcrest.Matchers.containsString("unknown")));
	}

	// -------------------------------------------------------------------------
	// 403 — access denied
	// -------------------------------------------------------------------------

	@Test
	@WithMockUser(username = "alice")
	@DisplayName("Should return 403 when access is denied")
	void shouldReturn403WhenAccessIsDenied() throws Exception {
		when(pluginRegistry.find("smart-doc")).thenReturn(Optional.of(SMART_DOC));
		doThrow(new PluginAccessDeniedException("smart-doc", "alice")).when(authorizationChecker).assertAccess(any(), any(), any());

		mockMvc.perform(get("/plugins/smart-doc/documents")).andExpect(status().isForbidden()).andExpect(jsonPath("$.status").value(403)).andExpect(jsonPath("$.message").value(org.hamcrest.Matchers.containsString("alice")));
	}

	// -------------------------------------------------------------------------
	// Upstream error passthrough
	// -------------------------------------------------------------------------

	@Test
	@WithMockUser(username = "alice")
	@DisplayName("Should pass through 502 from upstream service")
	void shouldPassThrough502FromUpstream() throws Exception {
		when(pluginRegistry.find("smart-doc")).thenReturn(Optional.of(SMART_DOC));
		doNothing().when(authorizationChecker).assertAccess(any(), any(), any());

		RestClientResponseException upstream = new RestClientResponseException("Bad Gateway", 502, "Bad Gateway", new org.springframework.http.HttpHeaders(), "upstream error".getBytes(), null);
		when(requestForwarder.forward(any(), any(), any(), any(), any())).thenThrow(upstream);

		mockMvc.perform(get("/plugins/smart-doc/documents")).andExpect(status().isBadGateway());
	}

	// -------------------------------------------------------------------------
	// Multipart forwarding
	// -------------------------------------------------------------------------

	@Test
	@WithMockUser(username = "alice")
	@DisplayName("Should forward multipart body raw and return 201")
	void shouldForwardMultipartBodyAndReturn201() throws Exception {
		when(pluginRegistry.find("smart-doc")).thenReturn(Optional.of(SMART_DOC));
		doNothing().when(authorizationChecker).assertAccess(any(), any(), any());
		when(requestForwarder.forward(any(), any(), any(), any(), any())).thenReturn(ResponseEntity.status(HttpStatus.CREATED).body(new byte[0]));

		MockMultipartFile file = new MockMultipartFile("document", "id-card.png", MediaType.IMAGE_PNG_VALUE, "image-content".getBytes());

		mockMvc.perform(multipart("/plugins/smart-doc/documents").file(file)).andExpect(status().isCreated());

		verify(requestForwarder).forward(any(), any(), any(), any(), any());
	}

	// -------------------------------------------------------------------------
	// PluginErrorResponse record
	// -------------------------------------------------------------------------

	@Test
	@DisplayName("Should expose status and message via record accessors")
	void shouldExposeStatusAndMessageViaRecordAccessors() {
		PluginController.PluginErrorResponse response = new PluginController.PluginErrorResponse(404, "not found");

		assertThat(response.status()).isEqualTo(404);
		assertThat(response.message()).isEqualTo("not found");
	}
}
