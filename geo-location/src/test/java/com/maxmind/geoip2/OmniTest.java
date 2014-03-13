/*******************************************************************************
 * Copyright 2014 Ednovo d/b/a Gooru. All rights reserved.
 * http://www.goorulearning.org/
 *   
 *   OmniTest.java
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
package com.maxmind.geoip2;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.fail;

import java.io.IOException;
import java.net.InetAddress;
import java.util.List;

import org.junit.Before;
import org.junit.Test;

import com.google.api.client.http.HttpTransport;
import com.maxmind.geoip2.exception.GeoIp2Exception;
import com.maxmind.geoip2.model.OmniResponse;
import com.maxmind.geoip2.record.Location;
import com.maxmind.geoip2.record.MaxMind;
import com.maxmind.geoip2.record.Postal;
import com.maxmind.geoip2.record.Subdivision;
import com.maxmind.geoip2.record.Traits;

public class OmniTest {
    private OmniResponse omni;

    @Before
    public void createClient() throws IOException, GeoIp2Exception {
        HttpTransport transport = new TestTransport();

        WebServiceClient client = new WebServiceClient.Builder(42, "012345689")
                .testTransport(transport).build();

        this.omni = client.omni(InetAddress.getByName("1.1.1.1"));
    }

    @Test
    public void testSubdivisionsList() {
        List<Subdivision> subdivisionsList = this.omni.getSubdivisions();
        assertNotNull("city.getSubdivisionsList returns null", subdivisionsList);
        if (subdivisionsList.size() == 0) {
            fail("subdivisionsList is empty");
        }
        Subdivision subdivision = subdivisionsList.get(0);
        assertEquals("subdivision.getConfidence() does not return 88",
                new Integer(88), subdivision.getConfidence());
        assertEquals("subdivision.getGeoNameId() does not return 574635",
                574635, subdivision.getGeoNameId().intValue());
        assertEquals("subdivision.getCode() does not return MN", "MN",
                subdivision.getIsoCode());
    }

    @Test
    public void mostSpecificSubdivision() {
        assertEquals("Most specific subdivision returns last subdivision",
                "TT", this.omni.getMostSpecificSubdivision().getIsoCode());
    }

    @SuppressWarnings("boxing")
    @Test
    public void testTraits() {
        Traits traits = this.omni.getTraits();

        assertNotNull("city.getTraits() returns null", traits);
        assertEquals("traits.getAutonomousSystemNumber() does not return 1234",
                new Integer(1234), traits.getAutonomousSystemNumber());
        assertEquals(
                "traits.getAutonomousSystemOrganization() does not return AS Organization",
                "AS Organization", traits.getAutonomousSystemOrganization());
        assertEquals(
                "traits.getAutonomousSystemOrganization() does not return example.com",
                "example.com", traits.getDomain());
        assertEquals("traits.getIpAddress() does not return 1.2.3.4",
                "1.2.3.4", traits.getIpAddress());
        assertEquals("traits.isAnonymousProxy() returns true", true,
                traits.isAnonymousProxy());
        assertEquals("traits.isSatelliteProvider() returns true", true,
                traits.isSatelliteProvider());
        assertEquals("traits.getIsp() does not return Comcast", "Comcast",
                traits.getIsp());
        assertEquals("traits.getOrganization() does not return Blorg", "Blorg",
                traits.getOrganization());
        assertEquals("traits.getUserType() does not return userType",
                "college", traits.getUserType());
    }

    @Test
    public void testLocation() {

        Location location = this.omni.getLocation();

        assertNotNull("city.getLocation() returns null", location);

        assertEquals("location.getAccuracyRadius() does not return 1500",
                new Integer(1500), location.getAccuracyRadius());

        double latitude = location.getLatitude().doubleValue();
        assertEquals("location.getLatitude() does not return 44.98", 44.98,
                latitude, 0.1);
        double longitude = location.getLongitude().doubleValue();
        assertEquals("location.getLongitude() does not return 93.2636",
                93.2636, longitude, 0.1);
        assertEquals("location.getMetroCode() does not return 765",
                new Integer(765), location.getMetroCode());
        assertEquals("location.getTimeZone() does not return America/Chicago",
                "America/Chicago", location.getTimeZone());
    }

    @Test
    public void testMaxMind() {
        MaxMind maxmind = this.omni.getMaxMind();
        assertEquals("Correct number of queries remaining", 11, maxmind
                .getQueriesRemaining().intValue());
    }

    @Test
    public void testPostal() {

        Postal postal = this.omni.getPostal();
        assertEquals("postal.getCode() does not return 55401", "55401",
                postal.getCode());
        assertEquals("postal.getConfidence() does not return 33", new Integer(
                33), postal.getConfidence());

    }

    @Test
    public void testRepresentedCountry() {
        assertNotNull("city.getRepresentedCountry() returns null",
                this.omni.getRepresentedCountry());

        assertEquals(
                "city.getRepresentedCountry().getType() does not return C<military>",
                "C<military>", this.omni.getRepresentedCountry().getType());
    }
}
