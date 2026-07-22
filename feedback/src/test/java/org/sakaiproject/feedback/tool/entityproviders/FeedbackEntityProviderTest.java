/*
 * Copyright (c) 2003-2026 The Apereo Foundation
 *
 * Licensed under the Educational Community License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *             http://opensource.org/licenses/ecl2
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package org.sakaiproject.feedback.tool.entityproviders;

import static org.junit.Assert.assertEquals;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

import java.util.HashMap;
import java.util.Locale;
import java.util.Map;

import javax.servlet.http.HttpServletRequest;

import org.junit.After;
import org.junit.Before;
import org.junit.Test;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;

import org.sakaiproject.entitybroker.DeveloperHelperService;
import org.sakaiproject.entitybroker.EntityView;
import org.sakaiproject.entitybroker.entityprovider.extension.RequestGetter;
import org.sakaiproject.feedback.util.SakaiProxy;

public class FeedbackEntityProviderTest {

    @Mock
    private SakaiProxy sakaiProxy;

    @Mock
    private RequestGetter requestGetter;

    @Mock
    private DeveloperHelperService developerHelperService;

    @Mock
    private HttpServletRequest request;

    private FeedbackEntityProvider provider;
    private AutoCloseable mocks;

    @Before
    public void setUp() {

        mocks = MockitoAnnotations.openMocks(this);

        provider = new FeedbackEntityProvider();
        provider.setSakaiProxy(sakaiProxy);
        provider.setRequestGetter(requestGetter);
        provider.setDeveloperHelperService(developerHelperService);

        when(sakaiProxy.getLocale()).thenReturn(Locale.US);
        when(requestGetter.getRequest()).thenReturn(request);
        when(request.getHeader("User-Agent")).thenReturn("test-agent");
        when(request.getRemoteAddr()).thenReturn("127.0.0.1");
        when(developerHelperService.getCurrentUserId()).thenReturn("user1");
    }

    @After
    public void tearDown() throws Exception {

        if (mocks != null) {
            mocks.close();
        }
    }

    private EntityView mockView() {

        EntityView view = mock(EntityView.class);
        when(view.getPathSegments()).thenReturn(new String[] { "feedback", "siteId", "reportcontent" });
        when(view.getPathSegment(1)).thenReturn("siteId");
        return view;
    }

    @Test
    public void handleContentReportReturnsBadTitleWhenTitleIsMissing() {

        Map<String, Object> params = new HashMap<>();
        params.put("description", "some description");
        // "title" intentionally omitted

        String result = provider.handleContentReport(mockView(), params);

        assertEquals("BAD_TITLE", result);
    }

    @Test
    public void handleContentReportReturnsBadTitleWhenTitleIsEmpty() {

        Map<String, Object> params = new HashMap<>();
        params.put("title", "");
        params.put("description", "some description");

        String result = provider.handleContentReport(mockView(), params);

        assertEquals("BAD_TITLE", result);
    }

    @Test
    public void handleContentReportReturnsBadDescriptionWhenDescriptionIsMissing() {

        Map<String, Object> params = new HashMap<>();
        params.put("title", "a valid title");
        // "description" intentionally omitted

        String result = provider.handleContentReport(mockView(), params);

        assertEquals("BAD_DESCRIPTION", result);
    }
}
