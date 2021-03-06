/*******************************************************************************
 * AbstractNamedRecord.java
 * insights-event-logger
 * Created by Gooru on 2014
 * Copyright (c) 2014 Gooru. All rights reserved.
 * http://www.goorulearning.org/
 * Permission is hereby granted, free of charge, to any person obtaining
 * a copy of this software and associated documentation files (the
 * "Software"), to deal in the Software without restriction, including
 * without limitation the rights to use, copy, modify, merge, publish,
 * distribute, sublicense, and/or sell copies of the Software, and to
 * permit persons to whom the Software is furnished to do so, subject to
 * the following conditions:
 * The above copyright notice and this permission notice shall be
 * included in all copies or substantial portions of the Software.
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

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import com.fasterxml.jackson.annotation.JacksonInject;
import com.fasterxml.jackson.annotation.JsonProperty;

/**
 * Abstract class for records with name maps.
 */
public abstract class AbstractNamedRecord {
    @JsonProperty
    private HashMap<String, String> names = new HashMap<String, String>();
    @JsonProperty("geoname_id")
    private Integer geoNameId;

    @JacksonInject("locales")
    private List<String> locales = new ArrayList<String>();

    AbstractNamedRecord() {
    }

    /**
     * @return The GeoName ID for the city. This attribute is returned by all
     *         end points.
     */
    public Integer getGeoNameId() {
        return this.geoNameId;
    }

    /**
     * @return The name of the city based on the locales list passed to the
     *         {@link com.maxmind.geoip2.WebServiceClient} constructor. This
     *         attribute is returned by all end points.
     */
    public String getName() {
        for (String lang : this.locales) {
            if (this.names.containsKey(lang)) {
                return this.names.get(lang);
            }
        }
        return null;
    }

    /**
     * @return A {@link Map} from locale codes to the name in that locale. This
     *         attribute is returned by all end points.
     */
    public Map<String, String> getNames() {
        return new HashMap<String, String>(this.names);
    }

    /*
     * (non-Javadoc)
     *
     * @see java.lang.Object#toString()
     */
    @Override
    public String toString() {
        return this.getName() != null ? this.getName() : "";
    }
}
