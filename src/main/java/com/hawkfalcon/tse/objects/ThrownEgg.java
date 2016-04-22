package com.hawkfalcon.tse.objects;

/**
 * Created for the AddstarMC Project.
 * Created by Narimm on 22/04/2016.
 */
public class ThrownEgg {
        private String type;
        private String variant;

     public ThrownEgg(String type, String variant){
            this.type = type;
            this.variant=variant;
        }

        public String getType() {
            return type;
        }

        public String getMobVariant() {
            return variant;
        }
    }
