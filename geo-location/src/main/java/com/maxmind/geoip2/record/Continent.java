/*******************************************************************************
 * Copyright 2014 Ednovo d/b/a Gooru. All rights reserved.
 * http://www.goorulearning.org/
 *   
 *   Continent.java
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
/**
 * This class provides a model for the data returned by the GeoIP2 Omni end
 * point.
 *
 * The only difference between the City, City/ISP/Org, and Omni model classes is
 * which fields in each record may be populated.
 *
 * @see <a href="http://dev.maxmind.com/geoip/geoip2/web-services">GeoIP2 Web
 *      Services</a>
 */
package com.maxmind.geoip2.record;

import com.fasterxml.jackson.annotation.JsonProperty;

/**
 * Contains data for the continent record associated with an IP address.
 *
 * This record is returned by all the end points.
 */
final public class Continent extends AbstractNamedRecord {
    @JsonProperty("code")
    private String code;

    public Continent() {
        super();
    }

    /**
     * @return A two character continent code like "NA" (North America) or "OC"
     *         (Oceania). This attribute is returned by all end points.
     */
    public String getCode() {
        return this.code;
    }
}