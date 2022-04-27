/**
 *     MyExtranet, il portale per collaborare con l’ente Regione Veneto.
 *     Copyright (C) 2022  Regione Veneto
 *
 *     This program is free software: you can redistribute it and/or modify
 *     it under the terms of the GNU Affero General Public License as
 *     published by the Free Software Foundation, either version 3 of the
 *     License, or (at your option) any later version.
 *
 *     This program is distributed in the hope that it will be useful,
 *     but WITHOUT ANY WARRANTY; without even the implied warranty of
 *     MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 *     GNU Affero General Public License for more details.
 *
 *     You should have received a copy of the GNU Affero General Public License
 *     along with this program.  If not, see <https://www.gnu.org/licenses/>.
 */
package it.regioneveneto.myp3.myextranet.config;

import java.util.ArrayList;
import java.util.List;

import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.stereotype.Component;

@Component
@ConfigurationProperties(prefix = "myextranet")
public class MyExtranetProperties {

    private String example;

    public String getExample() {
        return example;
    }

    public void setExample(String example) {
        this.example = example;
    }
    
    //Inner class quartz properties
    public static class QuartzProperties {

        private List<InnerQuartzProperties> properties = new ArrayList<>();
        private List<JobProperties> job = new ArrayList<>();

        //Getter and setter
        public List<JobProperties> getJob() {
            return job;
        }

        public void setJob(List<JobProperties> job) {
            this.job = job;
        }

        public List<InnerQuartzProperties> getProperties() {
            return properties;
        }

        public void setProperties(List<InnerQuartzProperties> properties) {
            this.properties = properties;
        }

        //Inner class JobProperties
        public static class InnerQuartzProperties {
            private String name;
            private String value;


            public String getName() {
                return name;
            }

            public void setName(String name) {
                this.name = name;
            }

            public String getValue() {
                return value;
            }

            public void setValue(String value) {
                this.value = value;
            }
        }

        //Inner class JobProperties
        public static class JobProperties {

            private String name;
            private String cron;


            //Getter and setter
            public String getName() {
                return name;
            }

            public void setName(String name) {
                this.name = name;
            }

            public String getCron() {
                return cron;
            }

            public void setCron(String cron) {
                this.cron = cron;
            }
        }
    }
}
