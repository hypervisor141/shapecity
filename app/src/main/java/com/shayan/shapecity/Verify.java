package com.shayan.shapecity;

import android.content.Context;
import android.telephony.TelephonyManager;

import com.google.i18n.phonenumbers.NumberParseException;
import com.google.i18n.phonenumbers.PhoneNumberUtil;

import java.util.Locale;

public final class Verify{


    private static final String[] countries ={
            "AF", "AL", "DZ", "AS", "AD", "AO", "AI", "AQ", "AG", "AR", "AM", "AW", "AU", "AT", "AZ", "BS",
            "BH", "BD", "BB", "BY", "BE", "BZ", "BJ", "BM", "BT", "BO", "BA", "BW", "BR", "IO", "VG", "BN",
            "BG", "BF", "BI", "KH", "CM", "CA", "CV", "KY", "CF", "TD", "CL", "CN", "CX", "CC", "CO", "KM",
            "CK", "CR", "HR", "CU", "CW", "CY", "CZ", "CD", "DK", "DJ", "DM", "DO", "TL", "EC", "EG", "SV",
            "GQ", "ER", "EE", "ET", "FK", "FO", "FJ", "FI", "FR", "PF", "GA", "GM", "GE", "DE", "GH", "GI",
            "GR", "GL", "GD", "GU", "GT", "GG", "GN", "GW", "GY", "HT", "HN", "HK", "HU", "IS", "IN", "ID",
            "IR", "IQ", "IE", "IM", "IL", "IT", "CI", "JM", "JP", "JE", "JO", "KZ", "KE", "KI", "XK", "KW",
            "KG", "LA", "LV", "LB", "LS", "LR", "LY", "LI", "LT", "LU", "MO", "MK", "MG", "MW", "MY", "MV",
            "ML", "MT", "MH", "MR", "MU", "YT", "MX", "FM", "MD", "MC", "MN", "ME", "MS", "MA", "MZ", "MM",
            "NA", "NR", "NP", "NL", "AN", "NC", "NZ", "NI", "NE", "NG", "NU", "KP", "MP", "NO", "OM", "PK",
            "PW", "PS", "PA", "PG", "PY", "PE", "PH", "PN", "PL", "PT", "PR", "QA", "CG", "RE", "RO", "RU",
            "RW", "BL", "SH", "KN", "LC", "MF", "PM", "VC", "WS", "SM", "ST", "SA", "SN", "RS", "SC", "SL",
            "SG", "SX", "SK", "SI", "SB", "SO", "ZA", "KR", "SS", "ES", "LK", "SD", "SR", "SJ", "SZ", "SE",
            "CH", "SY", "TW", "TJ", "TZ", "TH", "TG", "TK", "TO", "TT", "TN", "TR", "TM", "TC", "TV", "VI",
            "UG", "UA", "AE", "GB", "US", "UY", "UZ", "VU", "VA", "VE", "VN", "WF", "EH", "YE", "ZM", "ZW",
    };

    private static final String[] codes ={
            "93", "355", "213", "1684", "376", "244", "1264", "672", "1268", "54", "374", "297", "61", "43", "994",
            "1242", "973", "880", "1246", "375", "32", "501", "229", "1331", "975", "591", "387", "267", "55", "246", "1284",
            "673", "359", "226", "257", "855", "237", "1", "238", "1345", "236", "235", "56", "86", "61", "61", "57", "269",
            "682", "506", "385", "53", "599", "357", "420", "243", "45", "253", "1767", "1809", "670", "593", "20", "503", "240",
            "291", "372", "251", "500", "298", "679", "358", "33", "689", "241", "220", "995", "49", "233", "350",
            "30", "99", "1473", "1671", "502", "441481", "224", "245", "592", "509", "504", "852", "36", "354", "91", "62",
            "98", "964", "353", "441624", "972", "39", "225", "1876", "81", "441534", "962", "7", "254", "686", "383", "965",
            "996", "856", "371", "961", "226", "231", "218", "423", "370", "352", "853", "389", "261", "265", "60", "960",
            "223", "356", "692", "222", "230", "262", "52", "691", "373", "377", "976", "382", "1664", "212", "258", "95",
            "264", "674", "977", "31", "599", "687", "64", "505", "227", "234", "683", "850", "1670", "47", "968", "92",
            "680", "970", "507", "675", "595", "51", "63", "64", "48", "351", "1787", "974", "242", "262", "40",
            "7", "250", "590", "290", "1869", "1758", "590", "508", "1784", "685", "378", "239", "966", "221", "381", "248",
            "232", "65", "1721", "421", "386", "677", "252", "27", "82", "211", "34", "94", "249", "597", "47", "268",
            "46", "41", "963", "886", "992", "255", "66", "228", "690", "676", "1868", "216", "90", "993", "1649", "688",
            "1340", "256", "380", "971", "44", "1", "598", "998", "678", "379", "58", "84", "681", "212", "967", "260", "263"
    };

//    private static final String[] codes ={
//            "93", "355", "213", "1-684", "376", "244", "1-264", "672", "1-268", "54", "374", "297", "61", "43", "994",
//            "1-242", "973", "880", "1-246", "375", "32", "501", "229", "1-331", "975", "591", "387", "267", "55", "246", "1-284",
//            "673", "359", "226", "257", "855", "237", "1", "238", "1-345", "236", "235", "56", "86", "61", "61", "57", "269",
//            "682", "506", "385", "53", "599", "357", "420", "243", "45", "253", "1-767", "1-809", "670", "593", "20", "503", "240",
//            "291", "372", "251", "500", "298", "679", "358", "33", "689", "241", "220", "995", "49", "233", "350",
//            "30", "99", "1-473", "1-671", "502", "44-1481", "224", "245", "592", "509", "504", "852", "36", "354", "91", "62",
//            "98", "964", "353", "44-1624", "972", "39", "225", "1-876", "81", "44-1534", "962", "7", "254", "686", "383", "965",
//            "996", "856", "371", "961", "226", "231", "218", "423", "370", "352", "853", "389", "261", "265", "60", "960",
//            "223", "356", "692", "222", "230", "262", "52", "691", "373", "377", "976", "382", "1-664", "212", "258", "95",
//            "264", "674", "977", "31", "599", "687", "64", "505", "227", "234", "683", "850", "1-670", "47", "968", "92",
//            "680", "970", "507", "675", "595", "51", "63", "64", "48", "351", "1-787", "974", "242", "262", "40",
//            "7", "250", "590", "290", "1-869", "1-758", "590", "508", "1-784", "685", "378", "239", "966", "221", "381", "248",
//            "232", "65", "1-721", "421", "386", "677", "252", "27", "82", "211", "34", "94", "249", "597", "47", "268",
//            "46", "41", "963", "886", "992", "255", "66", "228", "690", "676", "1-868", "216", "90", "993", "1-649", "688",
//            "1-340", "256", "380", "971", "44", "1", "598", "998", "678", "379", "58", "84", "681", "212", "967", "260", "263"
//    };



    protected static String getCountry(String code){
        for(int index = 0; index < codes.length; index++){
            if(codes[index].equalsIgnoreCase(code)){
                return countries[index];
            }
        }

        return null;
    }


    protected static String getCode(String country){
        for(int index = 0; index < countries.length; index++){
            if(countries[index].equalsIgnoreCase(country)){

                return codes[index];
            }
        }

        return null;
    }


    protected static int getIndexOfCode(String code){
        for(int index = 0; index < codes.length; index++){
            if(codes[index].equalsIgnoreCase(code)){
                return index;
            }
        }

        return -1;
    }


    protected static int getIndexOfCountry(String country){
        for(int index = 0; index < countries.length; index++){
            if(countries[index].equalsIgnoreCase(country)){
                return index;
            }
        }

        return -1;
    }


    protected static String getUserCountry(Context context){
        try{
            final TelephonyManager tm =(TelephonyManager) context.getSystemService(Context.TELEPHONY_SERVICE);
            final String simCountry = tm.getSimCountryIso();

            if(simCountry != null && simCountry.length() == 2){
                return simCountry.toUpperCase(Locale.US);
            } else if(tm.getPhoneType() != TelephonyManager.PHONE_TYPE_CDMA){
                String networkCountry = tm.getNetworkCountryIso();

                if(networkCountry != null && networkCountry.length() == 2){
                    return networkCountry.toUpperCase(Locale.US);
                }
            }
        } catch(Exception ex){
            ex.printStackTrace();
        }

        return null;
    }



    protected static String[] getCodes(){
        return codes;
    }

    protected static String[] getCountries(){
        return countries;
    }




    protected static int startsWithCode(String num){
        num = num.replace("+", "");

        for(int i = 0; i < codes.length; i++){
            if(num.startsWith(codes[i])){
                return i;
            }
        }

        return -1;
    }


    protected static String parseCountryCode(PhoneNumberUtil formatter, String num){
        try{
            return String.valueOf(formatter.parse(num, "").getCountryCode());
        }catch(NumberParseException e){
            e.printStackTrace();
        }

        return null;
    }


    protected static String parseNationalNumber(PhoneNumberUtil formatter, String num){
        try{
            return String.valueOf(formatter.parse(num, "").getNationalNumber());
        }catch(NumberParseException e){
            e.printStackTrace();
        }

        return null;
    }

}
