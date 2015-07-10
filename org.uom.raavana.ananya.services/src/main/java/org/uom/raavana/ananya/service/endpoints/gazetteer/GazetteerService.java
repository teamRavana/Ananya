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
    public Response annotate(@Context HttpServletRequest httpServletRequest, @Multipart(value="text/plain") String text){

       // create the request for service

        String response = "    {\n" +
                "        \"T1\": {\n" +
                "            \"type\": \"Person\",\n" +
                "           \"offsets\": [[53, 64]],\n" +
                "           \"texts\": [\"දුලාජ් චාමර\"]\n" +
                "       },\n" +
                "        \"T2\": {\n" +
                "            \"type\": \"Person\",\n" +
                "           \"offsets\": [[78 , 91]],\n" +
                "           \"texts\": [\"අවිශ්ක සුලෝචන\"]\n" +
                "       },\n" +
                "        \"T102\": {\n" +
                "               \"type\": \"Person\",\n" +
                "               \"offsets\": [[479 , 492]],\n" +
                "               \"texts\": [\"ටී.බී. ඒකනායක\"]\n" +
                "       }\n" +
                "    } ";

        // get the response

        return Response.status(HttpServletResponse.SC_OK).
                entity(response).encoding("UTF-8").build();
    }
}
