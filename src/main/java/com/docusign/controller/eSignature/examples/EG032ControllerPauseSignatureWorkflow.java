package com.docusign.controller.eSignature.examples;

import com.docusign.DSConfiguration;
import com.docusign.common.WorkArguments;
import com.docusign.core.model.DoneExample;
import com.docusign.core.model.Session;
import com.docusign.esign.api.EnvelopesApi;
import com.docusign.esign.client.ApiException;
import com.docusign.core.model.User;
import com.docusign.esign.model.EnvelopeDefinition;
import com.docusign.esign.model.EnvelopeSummary;
import com.docusign.services.eSignature.examples.PauseSignatureWorkflowService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.ModelMap;
import org.springframework.web.bind.annotation.RequestMapping;

import javax.servlet.http.HttpServletResponse;
import java.io.IOException;

/**
 * This topic demonstrates how to create an envelope where the workflow
 * is paused before the envelope is sent to a second recipient.
 */
@Controller
@RequestMapping("/eg032")
public class EG032ControllerPauseSignatureWorkflow extends AbstractEsignatureController {

    private final Session session;
    private final User user;


    @Autowired
    public EG032ControllerPauseSignatureWorkflow(DSConfiguration config, Session session, User user) {
        super(config, "eg032", "Pause a signature workflow.");
        this.session = session;
        this.user = user;
    }

    @Override
    protected Object doWork(WorkArguments args, ModelMap model, HttpServletResponse response)
            throws ApiException, IOException
    {

        // Step 2: Construct your API headers
        EnvelopesApi envelopesApi = createEnvelopesApi(this.session.getBasePath(), this.user.getAccessToken());

        // Step 3: Construct your envelope JSON body
        EnvelopeDefinition envelope = PauseSignatureWorkflowService.createEnvelope(args);

        // Step 4: Call the eSignature REST API
        EnvelopeSummary results = PauseSignatureWorkflowService.pauseSignatureWorkflow(
                envelopesApi,
                this.session.getAccountId(),
                envelope
        );

        this.session.setEnvelopeId(results.getEnvelopeId());
        DoneExample.createDefault(this.title)
                .withJsonObject(results)
                .withMessage(
                        "The envelope where the workflow is paused before the envelope is sent to a second recipient " +
                                "has been created and sent!<br />Envelope ID "
                                + this.session.getEnvelopeId() + ".")
                .addToModel(model);

        return DONE_EXAMPLE_PAGE;
    }
}