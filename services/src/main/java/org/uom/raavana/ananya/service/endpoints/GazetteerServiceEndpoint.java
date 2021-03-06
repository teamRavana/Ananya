package org.uom.raavana.ananya.service.endpoints;


import org.apache.commons.lang.StringUtils;
import org.apache.cxf.jaxrs.ext.multipart.Multipart;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.ws.rs.Consumes;
import javax.ws.rs.POST;
import javax.ws.rs.Path;
import javax.ws.rs.Produces;
import javax.ws.rs.core.Context;
import javax.ws.rs.core.Response;
import java.io.File;

@Path(value = "/gazetteer")
public class GazetteerServiceEndpoint {

    public static final String GAZETTEER_ROOT = "GZ_ROOT";
    public static final String DEFAULT_APP_ROOT = ".ananya/input/gazetteer";

    @POST
    @Path("/")
    @Consumes("text/plain")
    @Produces("application/json")
    public Response annotate
            (@Context HttpServletRequest httpServletRequest, @Multipart(value="texts/plain") String text){

        String gazetteerPath = System.getenv(GAZETTEER_ROOT);


        if (gazetteerPath == null || StringUtils.isEmpty(gazetteerPath)){
            System.err.println("Environment Varible " + GAZETTEER_ROOT + " not set");

            gazetteerPath = System.getProperty("user.home")+ File.separator+DEFAULT_APP_ROOT;
            System.err.println("Setting default gazetteer input directory : "+gazetteerPath);
        }

        System.out.println("Gazetteer Path : "+gazetteerPath);
        String response;

        try {
            AnnotationService service = new AnnotationService(gazetteerPath);
            response = service.tagNamedEntities(text);

            return Response.status(HttpServletResponse.SC_OK).
                    entity(response).encoding("UTF-8").build();
        }catch (Exception ex){
            System.err.println(ex);
            return Response.serverError().build();
        }

    }
}
