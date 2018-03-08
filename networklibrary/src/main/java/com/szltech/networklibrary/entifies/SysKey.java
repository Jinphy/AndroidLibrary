package com.szltech.networklibrary.entifies;

/**
 * Created by Jinphy on 2018/3/7.
 */

public class SysKey extends BaseResultEntity{

    /**
     * retdata : {"syskey":"eu4aKwY1WRPe7hK9pJ80MeYc6VsAPBAW|mzHSCxlGyermGvRGf5wM0oJJ3coX2H7B|Vu6OIbG6VdC7tz8NhBLC2mH1k3jnajIF"}
     */

    private RetdataBean retdata;

    public RetdataBean getRetdata() {
        return retdata;
    }

    public void setRetdata(RetdataBean retdata) {
        this.retdata = retdata;
    }

    public static class RetdataBean {
        /**
         * syskey : eu4aKwY1WRPe7hK9pJ80MeYc6VsAPBAW|mzHSCxlGyermGvRGf5wM0oJJ3coX2H7B|Vu6OIbG6VdC7tz8NhBLC2mH1k3jnajIF
         */

        private String syskey;

        public String getSyskey() {
            return syskey;
        }

        public void setSyskey(String syskey) {
            this.syskey = syskey;
        }
    }
}
