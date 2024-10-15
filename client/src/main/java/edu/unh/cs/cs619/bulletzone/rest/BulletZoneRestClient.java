package edu.unh.cs.cs619.bulletzone.rest;

import org.androidannotations.rest.spring.annotations.Delete;
import org.androidannotations.rest.spring.annotations.Get;
import org.androidannotations.rest.spring.annotations.Path;
import org.androidannotations.rest.spring.annotations.Post;
import org.androidannotations.rest.spring.annotations.Put;
import org.androidannotations.rest.spring.annotations.Rest;
import org.androidannotations.rest.spring.api.RestClientErrorHandling;
import org.springframework.http.converter.StringHttpMessageConverter;
import org.springframework.http.converter.json.MappingJackson2HttpMessageConverter;
import org.springframework.web.client.RestClientException;

import edu.unh.cs.cs619.bulletzone.util.BooleanWrapper;
import edu.unh.cs.cs619.bulletzone.util.GameEventCollectionWrapper;
import edu.unh.cs.cs619.bulletzone.util.GridWrapper;
import edu.unh.cs.cs619.bulletzone.util.LongWrapper;
import edu.unh.cs.cs619.bulletzone.util.ResultWrapper;

/** "http://stman1.cs.unh.edu:6191/games"
 * "http://10.0.0.145:6191/games"
 * http://10.0.2.2:8080/
 * Created by simon on 10/1/14.
 */

//@Rest(rootUrl = "http://10.0.0.53:8080/games",
//@Rest(rootUrl = "http://stman1.cs.unh.edu:6192/games",
@Rest(rootUrl = "http://stman1.cs.unh.edu:61912/games",
        converters = {StringHttpMessageConverter.class, MappingJackson2HttpMessageConverter.class})
        // TODO: disable intercepting and logging
        // , interceptors = { HttpLoggerInterceptor.class }

public interface BulletZoneRestClient extends RestClientErrorHandling {
    void setRootUrl(String rootUrl);

    @Post("")
    LongWrapper join() throws RestClientException;

    @Get("")
    GridWrapper grid() throws RestClientException;

    @Get("/events/{sinceTime}")
    GameEventCollectionWrapper events(@Path long sinceTime) throws RestClientException;

    @Put("/account/register/{username}/{password}")
    ResultWrapper register(@Path String username, @Path String password) throws RestClientException;

    @Put("/account/login/{username}/{password}")
    LongWrapper login(@Path String username, @Path String password) throws RestClientException;

    @Put("/{tankId}/move/{direction}")
    BooleanWrapper move(@Path long tankId, @Path byte direction) throws RestClientException;

    @Put("/{tankId}/turn/{direction}")
    BooleanWrapper turn(@Path long tankId, @Path byte direction) throws RestClientException;

    @Put("/{tankId}/fire/1")
    BooleanWrapper fire(@Path long tankId) throws RestClientException;

    @Delete("/{tankId}/leave")
    BooleanWrapper leave(@Path long tankId) throws RestClientException;
}
