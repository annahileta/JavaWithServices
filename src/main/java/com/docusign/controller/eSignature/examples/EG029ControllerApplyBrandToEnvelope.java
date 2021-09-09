package com.docusign.controller.eSignature.examples;

import java.io.IOException;
import java.util.Arrays;
import java.util.List;

import javax.servlet.http.HttpServletResponse;

import com.docusign.common.WorkArguments;
import com.docusign.core.model.DoneExample;
import com.docusign.core.model.Session;
import com.docusign.core.model.User;
import com.docusign.services.eSignature.examples.ApplyBrandToEnvelopeService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.ModelMap;
import org.springframework.web.bind.annotation.RequestMapping;

import com.docusign.DSConfiguration;
import com.docusign.esign.api.AccountsApi;
import com.docusign.esign.api.EnvelopesApi;
import com.docusign.esign.client.ApiException;
import com.docusign.esign.model.BrandsResponse;
import com.docusign.esign.model.Document;
import com.docusign.esign.model.EnvelopeDefinition;
import com.docusign.esign.model.EnvelopeSummary;
import com.docusign.esign.model.Recipients;
import com.docusign.esign.model.Signer;
import com.docusign.esign.model.Tabs;


/**
 * Applying a brand to an envelope<br />
 * The envelope includes a pdf document. Anchor text <i>AutoPlace</i> is used
 * to position the signing fields in the documents.
 */
@Controller
@RequestMapping("/eg029")
public class EG029ControllerApplyBrandToEnvelope extends AbstractEsignatureController {

    private static final String MODEL_LIST_BRAND = "listBrands";

    private final Session session;
    private final User user;


    @Autowired
    public EG029ControllerApplyBrandToEnvelope(DSConfiguration config, Session session, User user) {
        super(config, "eg029", "Apply brand to envelope");
        this.session = session;
        this.user = user;
    }

    @Override
    protected void onInitModel(WorkArguments args, ModelMap model) throws Exception {
        super.onInitModel(args, model);
        AccountsApi accountsApi = createAccountsApi(session.getBasePath(), user.getAccessToken());
        BrandsResponse brands = accountsApi.listBrands(session.getAccountId());
        model.addAttribute(MODEL_LIST_BRAND, brands.getBrands());
    }

    @Override
    protected Object doWork(WorkArguments args, ModelMap model, HttpServletResponse response)
            throws ApiException, IOException {
        // Step 2: Construct your API headers
        EnvelopesApi envelopesApi = createEnvelopesApi(session.getBasePath(), user.getAccessToken());

        // Step 3: Construct your envelope JSON body
        EnvelopeDefinition envelope = ApplyBrandToEnvelopeService.makeEnvelope(args);

        // Step 5: Call the eSignature REST API
        EnvelopeSummary envelopeSummary = ApplyBrandToEnvelopeService.applyBrandToEnvelope(
                envelopesApi,
                session.getAccountId(),
                envelope
        );

        DoneExample.createDefault(title)
                .withJsonObject(envelopeSummary)
                .withMessage("The envelope has been created and sent!<br />Envelope ID "
                    + envelopeSummary.getEnvelopeId() + ".")
                .addToModel(model);
        return DONE_EXAMPLE_PAGE;
    }
}
