package com.docusign.controller.eSignature.examples;

import com.docusign.DSConfiguration;
import com.docusign.common.WorkArguments;
import com.docusign.core.model.Session;
import com.docusign.core.model.User;
import com.docusign.esign.api.EnvelopesApi;
import com.docusign.esign.client.ApiException;
import com.docusign.esign.model.ConsoleViewRequest;
import com.docusign.esign.model.ViewUrl;

import javax.servlet.http.HttpServletResponse;

import com.docusign.services.eSignature.examples.EmbeddedConsoleService;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.ModelMap;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.servlet.view.RedirectView;


/**
 * Embedded the DocuSign Web UI.<br />
 * Redirect the user to the DocuSign Web UI. The starting view can be either
 * an envelope's documents or the front page of the Web UI. The user does not
 * necessarily return from the DocuSign Web UI, so using this API call is often a final
 * step for the application. You can also open the WebUI in a new tab for the user
 */
@Controller
@RequestMapping("/eg012")
public class EG012ControllerEmbeddedConsole extends AbstractEsignatureController {

    private final Session session;
    private final User user;


    @Autowired
    public EG012ControllerEmbeddedConsole(DSConfiguration config, Session session, User user) {
        super(config, "eg012", "Embedded DocuSign web tool");
        this.session = session;
        this.user = user;
    }

    @Override
    protected void onInitModel(WorkArguments args, ModelMap model) throws Exception {
        super.onInitModel(args, model);
        model.addAttribute(MODEL_ENVELOPE_OK, StringUtils.isNotBlank(session.getEnvelopeId()));
    }

    @Override
    // ***DS.snippet.0.start
    protected Object doWork(WorkArguments args, ModelMap model, HttpServletResponse response) throws ApiException {
        args.setDsReturnUrl(config.getDsReturnUrl());

        EnvelopesApi envelopesApi = createEnvelopesApi(session.getBasePath(), user.getAccessToken());

        // Step 2. Call the CreateSenderView API
        ViewUrl viewUrl = EmbeddedConsoleService.createConsoleView(
                envelopesApi,
                config.getDsReturnUrl(),
                session.getEnvelopeId(),
                args.getStartingView(),
                session.getAccountId());

        return new RedirectView(viewUrl.getUrl());
    }
    // ***DS.snippet.0.end
}
