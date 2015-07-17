package org.uom.raavana.ananya.service.endpoints.gazetteer;


import org.apache.cxf.jaxrs.ext.multipart.Multipart;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.ws.rs.Consumes;
import javax.ws.rs.POST;
import javax.ws.rs.Path;
import javax.ws.rs.Produces;
import javax.ws.rs.core.Context;
import javax.ws.rs.core.Response;

@Path(value = "/gazetteer")
public class GazetteerService {

    @POST
    @Path("/")
    @Consumes("text/plain")
    @Produces("application/json")
    public Response annotate
            (@Context HttpServletRequest httpServletRequest, @Multipart(value="texts/plain") String text){

        String gazetteerPath = "input/gazetteer";

        String response;

        try {
            AnnotationService service = new AnnotationService(gazetteerPath);
            response = service.tagNamedEntities(text);

            return Response.status(HttpServletResponse.SC_OK).
                    entity(response).encoding("UTF-8").build();
        }catch (Exception ex){

            return Response.serverError().build();

        }

    }
}
