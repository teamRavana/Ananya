package org.uom.raavana.ananya.service.endpoints;


import org.apache.cxf.jaxrs.ext.multipart.Multipart;

import javax.servlet.http.HttpServletRequest;
import javax.ws.rs.Path;
import javax.ws.rs.core.Context;
import javax.ws.rs.core.Response;


/**
 *  Service endpoint to annotate an input text using the trained models (Classifier)
 */
@Path(value = "/classifier")
public class ClassifierServiceEndpoint {


    public Response annotate(@Context HttpServletRequest httpServletRequest, @Multipart(value="texts/plain") String text){



        String responseJson = "";
        return Response.ok().entity(responseJson).encoding("UTF-8").build();
    }


    /**
     *
     * @param text
     *          : Input text to be annotated (UTF-8 encoded Sinhala text)
     *
     * @return
     *          : the json string containing the output tags
     */
    private String annotateText(String text){


        return null;
    }
}
