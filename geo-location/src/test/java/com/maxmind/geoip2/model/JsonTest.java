/*******************************************************************************
 * Copyright 2014 Ednovo d/b/a Gooru. All rights reserved.
 * http://www.goorulearning.org/
 *   
 *   JsonTest.java
 *   event-api-stable-1.2
 *   
 * Permission is hereby granted, free of charge, to any person obtaining
 * a copy of this software and associated documentation files (the
 * "Software"), to deal in the Software without restriction, including
 * without limitation the rights to use, copy, modify, merge, publish,
 * distribute, sublicense, and/or sell copies of the Software, and to
 * permit persons to whom the Software is furnished to do so, subject to
 * the following conditions:
 *  
 * The above copyright notice and this permission notice shall be
 * included in all copies or substantial portions of the Software.
 *  
 * THE SOFTWARE IS PROVIDED "AS IS", WITHOUT WARRANTY OF ANY KIND,
 * EXPRESS OR IMPLIED, INCLUDING BUT NOT LIMITED TO THE WARRANTIES OF
 * MERCHANTABILITY, FITNESS FOR A PARTICULAR PURPOSE AND
 * NONINFRINGEMENT. IN NO EVENT SHALL THE AUTHORS OR COPYRIGHT HOLDERS BE
 * LIABLE FOR ANY CLAIM, DAMAGES OR OTHER LIABILITY, WHETHER IN AN ACTION
 * OF CONTRACT, TORT OR OTHERWISE, ARISING FROM, OUT OF OR IN CONNECTION
 * WITH THE SOFTWARE OR THE USE OR OTHER DEALINGS IN THE SOFTWARE.
 ******************************************************************************/
package com.maxmind.geoip2.model;

import static org.junit.Assert.assertEquals;

import java.io.IOException;
import java.util.ArrayList;

import org.junit.Test;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.DeserializationFeature;
import com.fasterxml.jackson.databind.InjectableValues;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;

public class JsonTest {

    private String omniBody = "{" + "\"city\":{" + "\"confidence\":76,"
            + "\"geoname_id\":9876," + "\"names\":{" + "\"en\":\"Minneapolis\""
            + "}" + "}," + "\"continent\":{" + "\"code\":\"NA\","
            + "\"geoname_id\":42," + "\"names\":{" + "\"en\":\"North America\""
            + "}" + "}," + "\"country\":{" + "\"confidence\":99,"
            + "\"iso_code\":\"US\"," + "\"geoname_id\":1," + "\"names\":{"
            + "\"en\":\"United States of America\"" + "}" + "},"
            + "\"location\":{" + "\"accuracy_radius\":1500,"
            + "\"latitude\":44.98," + "\"longitude\":93.2636,"
            + "\"metro_code\":765," + "\"time_zone\":\"America/Chicago\""
            + "}," + "\"postal\":{\"confidence\": 33, \"code\":\"55401\"},"
            + "\"registered_country\":{" + "\"geoname_id\":2,"
            + "\"iso_code\":\"CA\"," + "\"names\":{" + "\"en\":\"Canada\""
            + "}" + "}," + "\"represented_country\":{" + "\"geoname_id\":3,"
            + "\"iso_code\":\"GB\"," + "\"names\":{"
            + "\"en\":\"United Kingdom\"" + "}," + "\"type\":\"C<military>\""
            + "}," + "\"subdivisions\":[{" + "\"confidence\":88,"
            + "\"geoname_id\":574635," + "\"iso_code\":\"MN\"," + "\"names\":{"
            + "\"en\":\"Minnesota\"" + "}" + "}," + "{\"iso_code\":\"TT\"}],"
            + "\"traits\":{" + "\"autonomous_system_number\":1234,"
            + "\"autonomous_system_organization\":\"AS Organization\","
            + "\"domain\":\"example.com\"," + "\"ip_address\":\"1.2.3.4\","
            + "\"is_anonymous_proxy\":true,"
            + "\"is_satellite_provider\":true," + "\"isp\":\"Comcast\","
            + "\"organization\":\"Blorg\"," + "\"user_type\":\"college\""
            + "}," + "\"maxmind\":{\"queries_remaining\":11}" + "}";

    @Test
    public void testSerialization() throws JsonProcessingException, IOException {
        ObjectMapper mapper = new ObjectMapper();
        mapper.configure(DeserializationFeature.FAIL_ON_UNKNOWN_PROPERTIES,
                false);
        InjectableValues inject = new InjectableValues.Std().addValue(
                "locales", new ArrayList<String>());
        OmniResponse response = mapper.reader(OmniResponse.class).with(inject).readValue(
                this.omniBody);
        JsonNode expectedNode = mapper.readValue(this.omniBody, JsonNode.class);
        JsonNode actualNode = mapper.readValue(response.toJson(), JsonNode.class);

        assertEquals(expectedNode, actualNode);
    }

}
